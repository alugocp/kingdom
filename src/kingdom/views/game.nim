import std/sets
import std/sugar
import std/options
import std/sequtils
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/generation/manager
import kingdom/entities/types
import kingdom/controls/targeting
import kingdom/controls/keyboard
import kingdom/controls/viewport
import kingdom/controls/mouse
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/models/world
import kingdom/models/types
import kingdom/views/types

proc closeMenu*(this: GameView): void

# Constructor for a Game type
proc newGameView*(rules: GameRuleData, world: World): GameView =
    let g = GameView(
        viewType: ViewType.GAME,
        nextAbilityId: 0,
        nextUnitId: 0,
        nextItemId: 0,
        targeter: newTargeter(),
        rules: rules,
        keyboard: newKeyboardState(),
        mouse: newMouseState(),
        hoveredHex: none(Coord),
        menu: none(Menu),
        view: newViewport(),
        world: world
    )
    g.targeter.onTarget = () => g.closeMenu()
    return g

# Initializes a new Unit instance and puts it in the World
proc addNewUnit*(this: GameView, key: string, pos: Coord, player: int): Unit =
    let u = this.rules.unitGeneration.generate(key)
    u.pos = pos
    u.player = player
    u.id = this.nextUnitId
    this.world.moveUnit(u, pos)
    this.nextUnitId += 1

# Initializes a new Item instance and puts it in the World
proc addNewItem*(this: GameView, key: string, pos: Coord): Unit =
    let i = this.rules.itemGeneration.generate(key)
    i.pos = none(Coord)
    i.id = this.nextItemId
    this.world.moveItem(i, some(pos))
    this.nextItemId += 1

# Close the currently open Menu in this Game
proc closeMenu*(this: GameView): void =
    this.menu = none(Menu)

# Open a Menu in this Game
proc openMenu*(this: GameView, menu: Menu): void =
    this.menu = some(menu)

# Open a Menu in this Game (with default options)
proc openMenu*(this: GameView, root: MenuNode): void =
    let m = newMenu(0, 0, 200, root)
    this.menu = some(m)
    m.pack()

# Opens a menu used specifically for targeting
proc openTargetMenu*(this: GameView): void =
    if this.targeter.isUnits():
        let units = this.targeter.units.get()
        let handler = this.targeter.unitHandler.get()
        let node = newListNode()
        for u in units:
            let u1 = u
            node.add(newButtonNode(u.name, proc (): void =
                handler(u1)
                this.targeter.cancel()
                this.closeMenu()
            ))
        this.openMenu(node)

# Returns which View should be shown in the next frame
method getNextView*(this: GameView): View = this

# Draws all elements of this Game object
method draw*(this: GameView): void =
    this.world.draw(this.rules.sprites, this.hoveredHex, this.targeter.coords, this.view, this.rules.edgeTileSprite)
    if this.menu.isSome:
        this.menu.get().draw(this.mouse)

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
        if this.menu.isSome:
            let clicked = this.menu.get().checkClick(this.mouse)
            if clicked: return
            else: this.closeMenu()
        if this.world.contains(hex):
            if this.targeter.isCoords():
                if this.targeter.coords.get().contains(hex):
                    this.targeter.coordHandler.get()(hex)
                    this.targeter.cancel()
                    return
                this.targeter.cancel()
            let node = this.world.getMenuNode(
                hex,
                (n: MenuNode) => this.openMenu(n),
                proc (i: Item): void =
                    let units = this.world.getUnits(i.pos.get())
                    this.targeter.target(units, proc (u: Unit): void =
                        this.world.moveItem(i, none(Coord))
                        u.items.add(i)
                    )
                    this.openTargetMenu(),
                proc (u: Unit, i: Item): void =
                    u.items = u.items.filterIt(it != i)
                    this.world.moveItem(i, some(u.pos))
                    this.closeMenu()
            )
            this.openMenu(node)
