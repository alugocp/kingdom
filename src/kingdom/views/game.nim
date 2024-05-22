import std/sets
import std/math
import std/sugar
import std/tables
import std/options
import std/sequtils
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/generation/manager
import kingdom/entities/signals
import kingdom/entities/party
import kingdom/entities/types
import kingdom/entities/unit
import kingdom/builtin/signals
import kingdom/builtin/values
import kingdom/builtin/types
import kingdom/wrapper/window
import kingdom/controls/targeting
import kingdom/controls/keyboard
import kingdom/controls/viewport
import kingdom/controls/actions
import kingdom/controls/mouse
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/models/world
import kingdom/models/types
import kingdom/views/types

# Width for each Menu in the GameView
const MENU_WIDTH = 300

# Forward declaration for the constructor
proc closeMenu*(this: GameView): void

# Constructor for a Game type
proc newGameView*(rules: GameRuleData, world: World): GameView =
    let g = GameView(
        viewType: ViewType.GAME,
        nextAbilityId: 0,
        nextPartyId: 0,
        nextUnitId: 0,
        nextItemId: 0,
        targeter: newTargeter(),
        rules: rules,
        nextPlayerId: AMBIENT_PLAYER + 1,
        keyboard: newKeyboardState(),
        mouse: newMouseState(),
        hoveredHex: none(Coord),
        menu: none(Menu),
        view: newViewport(),
        world: world,
        state: newGameState(0),
        unitActions: UnitActions(
            moved: initTable[Unit, int](),
            acted: initHashSet[Unit]()
        )
    )
    let g1 {.cursor.} = g
    g.targeter.onTarget = () => g1.closeMenu()
    return g

# Inits all Tiles in the World
method initView*(this: GameView): void =
    let payload = newInitSignalArgs()
    for x in 0..(this.world.w - 1):
        for y in 0..(this.world.h - 1):
            this.world.getTile(initCoord(x, y)).handleSignal(@[], payload)

# Creates a new Party and adds a Unit to it
proc addUnitToNewParty(this: GameView, u: Unit): void =
    let p = newParty(this.nextPartyId, u)
    this.world.moveParty(p, u.pos)
    this.nextPartyId += 1

# Removes a Unit from a Party and deletes the Party if it's empty
proc removeUnitFromParty(this: GameView, unit: Unit, party: Party): void =
    let empty = party.removeFromParty(unit)
    if empty:
        this.world.deleteParty(party, unit.pos)

# Initializes a new Unit instance and puts it in the World
proc addNewUnit*(this: GameView, key: string, pos: Coord, player: int, party: Option[Party] = none(Party)): Unit {.exportc, dynlib.} =
    let u = this.rules.unitGeneration.generate(key)
    this.state.players[player].numUnits += 1
    u.pos = pos
    u.player = player
    u.id = this.nextUnitId
    u.feed(this.state)
    this.nextUnitId += 1
    if party.isSome():
        party.get().addToParty(u)
    else:
        this.addUnitToNewParty(u)

# Initializes a new Item instance and puts it in the World
proc addNewItem*(this: GameView, key: string, pos: Coord): Item {.exportc, dynlib.} =
    let i = this.rules.itemGeneration.generate(key)
    i.pos = none(Coord)
    i.id = this.nextItemId
    this.world.moveItem(i, some(pos))
    this.nextItemId += 1
    return i

# Creates a new player ID and returns it
proc createNewPlayer*(this: GameView): int {.exportc, dynlib.} =
    this.state.players.add(newPlayerData(this.nextPlayerId))
    result = this.nextPlayerId
    this.nextPlayerId += 1

# This Unit deals some damage to another Unit
proc dealDamage*(this: Unit, game: GameView, u: Unit, dtype: DamageType, dmg: int): void {.exportc, dynlib.} =
    let p1 = newDealDamageSignalArgs(dtype, dmg, this, u)
    this.handleSignal(@[], p1)
    let p2 = newTakeDamageSignalArgs(p1.dtype, p1.dmg, this, u)
    u.handleSignal(@[], p2)
    let p3 = newPartyMemberTakeDamageSignalArgs(p2.dtype, p2.dmg, this, u)
    u.handleSignal(@[], p3)
    u.damageTaken += max(p3.dmg, 0)
    if u.getHealth() == 0:
        u.handleSignal(@[], newUnitDiesSignalArgs())
        game.world.getTile(u.pos).handleSignal(@[], newUnitKilledSignalArgs(u))
        game.removeUnitFromParty(u, game.world.getParty(u))
        game.state.players[u.player].numUnits -= 1

# Close the currently open Menu in this Game
proc closeMenu*(this: GameView): void =
    this.menu = none(Menu)

# Open a Menu in this Game
proc openMenu(this: GameView, root: MenuNode, right: bool): void =
    if this.state.turnPlayer != HUMAN_PLAYER:
        return
    let m = newMenu(if right: getWindowBounds().x - MENU_WIDTH else: 0, 0, MENU_WIDTH, true, root)
    this.menu = some(m)
    m.pack()

# Opens a menu used specifically for targeting
proc openTargetMenu*(this: GameView): void =
    if this.targeter.isUnits():
        let units = this.targeter.units.get()
        let handler = this.targeter.unitHandler.get()
        let node = newListNode()
        node.add(newHeaderNode("Unit targets:"))
        node.add(newSpaceNode())
        if units.len == 0:
            node.add(newTextNode("No options available"))
        for u in units:
            capture u:
                node.add(newButtonNode(u.getMenuLabel(), proc (): void =
                    this.targeter.cancel()
                    this.closeMenu()
                    handler(u)
                ))
        this.openMenu(node, true)

# Logic to handle changing turn state from one player to another
proc nextPlayerTurn*(this: GameView): void =
    this.unitActions.moved.clear()
    this.unitActions.acted.clear()
    if this.state.turnPlayer == this.nextPlayerId - 1:
        this.state.turnPlayer = HUMAN_PLAYER
    else:
        if this.state.turnPlayer == HUMAN_PLAYER:
            this.closeMenu()
        this.state.turnPlayer += 1

# Marks the given unit as having acted during this turn
proc unitHasActed*(this: GameView, u: Unit): void {.exportc, dynlib.} =
    this.unitActions.acted.incl(u)

# Logic that gets run every frame
method frame*(this: GameView): void =
    if this.state.match != MatchState.ONGOING:
        return

    # Victory/defeat criteria check
    let playerUnits = this.state.players[HUMAN_PLAYER].numUnits
    if playerUnits == 0:
        this.state.match = MatchState.DEFEAT
        let root = newListNode()
        root.add(newHeaderNode("Defeat"))
        root.add(newButtonNode("Continue", () => this.closeMenu()))
        this.openMenu(root, true)
        return
    if this.state.players.foldl(a + b.numUnits, 0) - playerUnits == 0:
        this.state.match = MatchState.VICTORY
        let root = newListNode()
        root.add(newHeaderNode("Victory"))
        root.add(newButtonNode("Continue", () => this.closeMenu()))
        this.openMenu(root, true)
        return

# Returns how many turns it has been since the given Unit was fed
proc getUnitHunger*(this: GameView, u: Unit): int =
    let turnsSinceFeeding = this.state.turn - u.lastTurnFed
    let payload = newGetMaxHungerSignalArgs()
    u.handleSignal(@[], payload)
    return min(100, int(floor(100 * float(turnsSinceFeeding) / float(payload.hunger))))

# Draws all elements of this Game object
method draw*(this: GameView): void =
    this.world.draw(this.rules.sprites, this.hoveredHex, this.targeter.coords, this.view, this.rules.edgeTileSprite)
    if this.menu.isSome():
        this.menu.get().draw(this.mouse, true)

# Check for updated keyboard state and see what we have to process
method consumeKeyboardUpdates*(this: GameView): void =
    let released = this.keyboard.getKeysReleased()
    if released.contains(61): # +
        this.view.zoom(0.1, this.world.w, this.world.h)
    if released.contains(45): # -
        this.view.zoom(-0.1, this.world.w, this.world.h)

# Check for updated mouse state and see what we have to process
method consumeMouseUpdates*(this: GameView): void =
    if this.mouse.down and this.mouse.scrolling:
        if this.menu.isSome() and this.mouse.posdown.within(this.menu.get().getMenuMouseRect()):
            this.menu.get().handleScroll(this.mouse)
            return
        this.view.scroll(
            this.mouse.pos.x - this.mouse.posprev.x,
            this.mouse.pos.y - this.mouse.posprev.y,
            this.world.w,
            this.world.h
        )

    # check if the user is hovering over a hexagonal Tile
    let hex = getHexagonCoords(this.view.screenToGame(this.mouse.pos))
    if this.world.contains(hex):
        this.hoveredHex = some(hex)
    else:
        this.hoveredHex = none(Coord)

    # Process a click event
    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        if this.menu.isSome():
            let clicked = this.menu.get().checkClick(this.mouse)
            if clicked:
                return
            elif this.state.match == MatchState.ONGOING:
                this.closeMenu()
        if this.state.match != MatchState.ONGOING:
            return
        if this.world.contains(hex):
            if this.targeter.isCoords():
                if this.targeter.coords.get().contains(hex):
                    this.targeter.coordHandler.get()(hex)
                    this.targeter.cancel()
                    return
                this.targeter.cancel()
            if this.world.isTileEmpty(hex):
                return
            let right = this.mouse.pos.x < getWindowBounds().x - MENU_WIDTH
            let actions = newWorldMenuActions(
                # open
                (n: MenuNode) => this.openMenu(n, right),

                # equip
                proc (itype: InventoryType, i: Item): void =
                    let units = this.world.getUnits(i.pos.get())
                    var filtered: seq[Unit] = @[]
                    for u in units:
                        let fullInventory = if itype == InventoryType.EQUIP: u.items.len == u.maxItems else: u.haul.len == u.maxHaul
                        if u.player != HUMAN_PLAYER or fullInventory:
                            continue
                        if itype == InventoryType.EQUIP:
                            let payload = newCanBeEquippedSignalArgs(u, i)
                            i.handleSignal(@[], payload)
                            if payload.equippable:
                                capture u:
                                    filtered.add(u)
                        else:
                            capture u:
                                filtered.add(u)
                    this.targeter.target(filtered, (u: Unit) => this.world.giveItemToUnit(itype, i, u))
                    this.openTargetMenu(),

                # unequip
                proc (itype: InventoryType, u: Unit, i: Item): void =
                    if itype == InventoryType.EQUIP:
                        u.items = u.items.filterIt(it != i)
                    else:
                        u.haul = u.haul.filterIt(it != i)
                    this.world.moveItem(i, some(u.pos))
                    this.closeMenu(),

                # leaveParty
                proc (unit: Unit, party: Party): void =
                    this.removeUnitFromParty(unit, party)
                    this.addUnitToNewParty(unit)
                    this.closeMenu(),

                # joinParty
                proc (unit: Unit, party: Party): void =
                    let root = newListNode()
                    root.add(newHeaderNode("Parties:"))
                    root.add(newSpaceNode())
                    for p in this.world.getParties(unit.pos):
                        if p == party or p.getPlayerId() != party.getPlayerId():
                            continue
                        for u in p.getMembers():
                            root.add(newTextNode(u.getMenuLabel()))
                        root.add(newSpaceNode())
                        capture p:
                            root.add(newButtonNode("Join this party", proc (): void =
                                this.removeUnitFromParty(unit, party)
                                p.addToParty(unit)
                                this.closeMenu()
                            ))
                        root.add(newSeparatorNode())
                    this.openMenu(root, right),

                # initMoveParty
                proc (party: Party): void =
                    let max = party.getMaxMovement(this.unitActions.moved)
                    let adjs = party.getCoord().getRadialHexagonCoords(this.world.getBounds(), max)
                    var distances = initTable[Coord, int]()
                    var targets: seq[Coord] = @[]
                    for dst in adjs:
                        if dst == party.getCoord():
                            continue
                        let path = this.world.pathfind(party, dst, adjs)
                        if path.len > 0 and this.world.canTileReceiveParty(party, dst):
                            distances[dst] = path.len - 1
                            targets.add(dst)
                    this.targeter.target(targets, proc (c: Coord): void =
                        for u in party.getMembers():
                            this.unitActions.moved[u] = this.unitActions.moved.getOrDefault(u, 0) + distances[c]
                        this.world.moveParty(party, c)
                    ),

                # getHunger
                (u: Unit) => this.getUnitHunger(u),

                # canUnitAct
                (u: Unit) => not this.unitActions.acted.contains(u)
            )
            let node = this.world.getMenuNode(hex, actions)
            this.openMenu(node, right)
