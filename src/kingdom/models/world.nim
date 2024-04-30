import std/math
import std/sets
import std/sugar
import std/tables
import std/options
import std/sequtils
import std/strformat
import kingdom/entities/types
import kingdom/entities/tile
import kingdom/entities/item
import kingdom/entities/unit
import kingdom/entities/party
import kingdom/entities/signals
import kingdom/controls/viewport
import kingdom/controls/actions
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/wrapper/draw
import kingdom/wrapper/types
import kingdom/wrapper/sprites
import kingdom/wrapper/window
import kingdom/builtin/signals
import kingdom/builtin/values
import kingdom/builtin/types
import kingdom/models/types
import kingdom/operators

# Constructor for the World type
proc newWorld*(w: Natural, h: Natural): World =
    new result
    result.nextPlayerId = 1
    result.w = w
    result.h = h

# Initializes a TileData object
proc initTileData(t: Tile): TileData =
    result.tile = t
    result.items = @[]
    result.parties = @[]

# Fills out the Tiles in this World
proc build*(this: World, generate: (x: int, y: int) -> Tile): void =
    var id = 0
    for x in 0..(this.w - 1):
        this.tiles.add(@[])
        for y in 0..(this.h - 1):
            let t = generate(x, y)
            t.id = id
            t.pos = initCoord(x, y)
            this.tiles[x].add(initTileData(t))
            id += 1

# Returns the bounds of this World as a Coord
proc getBounds*(this: World): Coord {.exportc, dynlib.} =
    initCoord(this.w, this.h)

# Retrieves a Tile in this World
proc getTile*(this: World, c: Coord): Tile = this.tiles[c.x][c.y].tile

# Retrieves a set of Units on a Tile in this World
proc getUnits*(this: World, c: Coord): seq[Unit] =
    var units = newSeq[Unit]()
    let parties = this.tiles[c.x][c.y].parties
    for party in parties:
        units = concat(units, party.getMembers())
    return units

# Retrieves a set of Items on a Tile in this World
proc getItems*(this: World, c: Coord): seq[Item] = this.tiles[c.x][c.y].items

# Retrieves a set of Parties on a Tile in this World
proc getParties*(this: World, c: Coord): seq[Party] = this.tiles[c.x][c.y].parties

# Returns the Party associated with the given Unit
proc getParty*(this: World, u: Unit): Party {.exportc, dynlib.} =
    let parties = this.getParties(u.pos)
    let filtered = parties.filterIt(it.id == u.party)
    if filtered.len != 1:
        raise newException(Exception, fmt"Invalid unit/party match length ({filtered.len}), unit's party ID is {u.party}")
    return filtered[0]

# Creates a new player ID and returns it
proc createNewPlayer*(this: World): int =
    result = this.nextPlayerId
    this.nextPlayerId += 1

# Moves a Party from one Tile in this World to another
proc moveParty*(this: World, p: Party, c: Coord): void {.exportc, dynlib.} =
    let pos = p.getMembers()[0].pos
    this.tiles[pos.x][pos.y].parties = this.tiles[pos.x][pos.y].parties.filterIt(it != p)
    this.tiles[c.x][c.y].parties.add(p)
    for u in p.getMembers():
        u.pos = c

# Deletes a Party from the World
proc deleteParty*(this: World, p: Party, pos: Coord): void =
    if p.n > 0:
        raise newException(Exception, "Cannot delete party with active members")
    this.tiles[pos.x][pos.y].parties = this.tiles[pos.x][pos.y].parties.filterIt(it != p)

# Moves an Item from one Tile in this World to another (or to add/remove it from the World)
proc moveItem*(this: World, i: Item, c: Option[Coord]): void =
    if i.pos.isSome:
        this.tiles[i.pos.get().x][i.pos.get().y].items = this.tiles[i.pos.get().x][i.pos.get().y].items.filterIt(it != i)
    if c.isSome:
        this.tiles[c.get().x][c.get().y].items.add(i)
    i.pos = c

# Equips an Item to a Unit (works for either InventoryType)
proc giveItemToUnit*(this: World, itype: InventoryType, i: Item, u: Unit): void =
    this.moveItem(i, none(Coord))
    if itype == InventoryType.EQUIP:
        u.items.add(i)
    else:
        u.haul.add(i)

# Returns true if the World contains a Tile at the given Coord
proc contains*(this: World, c: Coord): bool = c.x >= 0 and c.y >= 0 and c.x < this.w and c.y < this.h

# Returns true if there is nothing of interest on this Tile
proc isTileEmpty*(this: World, c: Coord): bool =
    not (this.getTile(c).desc.isSome() or this.getUnits(c).len > 0 or this.getItems(c).len > 0)

# Return a MenuNode describing this
proc getMenuNode*(this: World, c: Coord, actions: WorldMenuActions): MenuNode =
    let node = newListNode()
    let tile = this.getTile(c)
    let parties = this.getParties(c)
    let items = this.getItems(c)
    if tile.desc.isSome():
        node.add(newTextNode(tile.desc.get()))
        node.add(newSeparatorNode())

    # Menu elements for Parties
    if parties.len > 0:
        node.add(newHeaderNode("Parties:"))
    for p in parties:
        for u in p.getMembers():
            capture u:
                let party = this.getParty(u)
                let unitActions = newUnitMenuActions(
                    actions.leaveParty,
                    actions.joinParty,
                    actions.equip,
                    (itype: InventoryType, i: Item) => this.giveItemToUnit(itype, i, u),
                    (itype: InventoryType, i: Item) => actions.unequip(itype, u, i),
                    actions.getHunger
                )
                let root = u.getMenuNode(party, unitActions)
                node.add(newButtonNode(u.getMenuLabel(), () => actions.open(root)))
        if p.getPlayerId() == HUMAN_PLAYER:
            node.add(newSpaceNode())
            capture p:
                node.add(newButtonNode("Move", () => actions.initMoveParty(p)))
        node.add(newSeparatorNode())

    # Menu elements for Items
    if items.len > 0:
        node.add(newHeaderNode(fmt"Items:"))
    for i in items:
        capture i:
            node.add(i.getMenuNode(
                none(tuple[player: int, itype: InventoryType]),
                newItemMenuActions(
                    (itype: InventoryType) => actions.equip(itype, i),
                    proc (itype: InventoryType): void = discard,
                    proc (): void = discard
                )
            ))
            node.add(newSeparatorNode())
    return node

# Return a set of tiles on the screen that you have visibility on
proc getVisibleTiles(this: World, topLeft: Coord, botRight: Coord): HashSet[Coord] =
    var tiles = initHashSet[Coord]()
    for x in topLeft.x..botRight.x:
        for y in topLeft.y..botRight.y:
            let c = initCoord(x, y)
            if this.contains(c):
                let parties = this.getParties(c)
                var max = 0
                for party in parties:
                    let units = party.getMembers()
                    for unit in units:
                        if unit.player == HUMAN_PLAYER:
                            var payload = newGetVisibilitySignalArgs()
                            unit.handleSignal(@[], payload)
                            if payload.visibility > max:
                                max = payload.visibility
                if max > 0:
                    tiles = tiles + getRadialHexagonCoords(c, botRight, max)
    return tiles

# Draw this World object
proc draw*(this: World, sm: SpriteManager, hovered: Option[Coord], targeted: Option[seq[Coord]], view: Viewport, edgeTileSprite: SpriteHandle): void =
    # Find hexagon coords for screen bounds and calculate tile visibility
    let tl = getHexagonCoords(view.screenToGame(initPosition(0, 0)))
    let br = getHexagonCoords(view.screenToGame(getWindowBounds()))
    let topLeft = initCoord(tl.x - 1, tl.y - 1)
    let botRight = initCoord(br.x + 1, br.y + 1)
    let visible = this.getVisibleTiles(topLeft, botRight)

    # Draw every hexagon currently on the screen
    for x in topLeft.x..botRight.x:
        for y in topLeft.y..botRight.y:
            let center = getHexagonCenterPoint(x, y)
            if x < 0 or x >= this.w or y < 0 or y >= this.h:
                sm.drawSprite(edgeTileSprite, view, view.gameToScreen(initPosition(center.x - HALF_W, center.y - SIDE)))
                outlineHexagon(view.gameToScreen(center), view)
            else:
                let center = getHexagonCenterPoint(initCoord(x, y))
                let tile = this.getTile(initCoord(x, y))

                # Draw the Tile
                sm.drawSprite(tile.sprite, view, view.gameToScreen(initPosition(center.x - HALF_W, center.y - SIDE)))
                if hovered.isSome and hovered.get() == tile.pos:
                    drawHexagon(view.gameToScreen(center), HIGHLIGHT, view)
                elif targeted.isSome and targeted.get().contains(tile.pos):
                    drawHexagon(view.gameToScreen(center), HIGHLIGHT, view)
                outlineHexagon(view.gameToScreen(center), view)

                # Draw Units but only on visible Tiles
                if tile.pos in visible:
                    let parties = this.getParties(tile.pos)
                    for a in 0..(parties.len - 1):
                        if a > 7: break
                        let leader = parties[a].getMembers()[0]
                        var position = initPosition(center.x - 12, center.y - 12)
                        if parties.len == 2:
                            position.x = center.x + (48f * float(a)) - 36f
                        elif parties.len == 3:
                            position.x = center.x + (32f * float(a)) - 48f
                        elif parties.len == 4:
                            position.x = center.x + (48f * float(a mod 2)) - 36f
                            position.y = if a < 2: (center.y - 24f) else: center.y
                        elif parties.len != 1:
                            position.x = center.x + (24f * float(a mod 4)) - 48f
                            position.y = if a < 4: (center.y - 24f) else: center.y
                        sm.drawSprite(leader.sprite, view, view.gameToScreen(position))
                else:
                    drawHexagon(view.gameToScreen(center), DARKER, view)

# Checks if the Unit can cross the border from one Tile to another
proc canUnitTravelAcrossTiles*(this: World, unit: Unit, current: Coord, adj: Coord): bool {.exportc, dynlib.} =
    let side = getSharedSide(current, adj)
    let opp = getOppositeSide(side)
    let tile1 = this.getTile(current)
    let tile2 = this.getTile(adj)
    var test1 = newCanCrossBorderSignalArgs(tile1, side, tile1.getBorder(side))
    var test2 = newCanCrossBorderSignalArgs(tile2, opp, tile2.getBorder(opp))
    if not test1.canCross:
        unit.handleSignal(@[], test1)
    if not test2.canCross:
        unit.handleSignal(@[], test2)
    return test1.canCross and test2.canCross

# Return a path from the Party's current position to the destination,
# making sure to respect which borders the Party can cross.
# This function implements A* algorithm, based on pseudocode from
# Wikipedia (https://en.wikipedia.org/wiki/A*_search_algorithm)
proc pathfind*(this: World, party: Party, dst: Coord, allowedTiles: HashSet[Coord]): seq[Coord]=
    const infinity = high(int)

    # Heuristic function for the A* algorithm
    proc dist(c: Coord): int = int(ceil((abs(dst.x - c.x) + abs(dst.y - c.y)) / 2))

    # Initialize relevant tables and sets
    let start = party.getCoord()
    var openSet: seq[Coord] = @[start]
    var cameFrom = initTable[Coord, Coord]()
    var gScore = initTable[Coord, int]()
    var fScore = initTable[Coord, int]()
    fScore[start] = dist(start)
    gScore[start] = 0

    # Main algorithm body
    var current: Coord
    while openSet.len > 0:
        current = foldl(openSet, if fScore.getOrDefault(a, infinity) < fScore.getOrDefault(b, infinity): a else: b, openSet[0])

        # We made it!
        if current == dst:
            var path: seq[Coord] = @[current]
            while cameFrom.hasKey(current):
                current = cameFrom[current]
                path.insert(current, 0)
            return path

        # Filter openSet and check adjacent tiles
        openSet = openSet.filterIt(it != current)
        let neighbors = current.getAdjacentHexagonCoords(initCoord(this.w, this.h))
        let filtered = neighbors.filterIt(it in allowedTiles)
        for adj in filtered:

            # Skip any neighbors with borders the unit cannot cross
            let canPartyTravelAcrossTiles = foldl(party.getMembers(), a and this.canUnitTravelAcrossTiles(b, current, adj), true)
            if not canPartyTravelAcrossTiles:
                continue

            let g = if gScore.hasKey(current): gScore[current] + 1 else : infinity
            if g < gScore.getOrDefault(adj, infinity):
                cameFrom[adj] = current
                gScore[adj] = g
                fScore[adj] = g + dist(adj)
                if not (adj in openSet):
                    openSet.add(adj)

    # Failed state, there is no path :(
    return @[]