import std/math
import std/sets
import std/sugar
import std/tables
import std/options
import std/sequtils
import std/strformat
import kingdom/builtin/signals
import kingdom/entities/types
import kingdom/entities/tile
import kingdom/entities/unit
import kingdom/entities/item
import kingdom/entities/signals
import kingdom/controls/viewport
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/operators
import kingdom/wrapper/draw
import kingdom/wrapper/types
import kingdom/wrapper/sprites
import kingdom/wrapper/window
import kingdom/builtin/values
import kingdom/models/types

# Constructor for the World type
proc newWorld*(w: Natural, h: Natural): World =
    new result
    result.w = w
    result.h = h

# Fills out the Tiles in this World
proc build*(this: World, generate: (x: int, y: int) -> Tile): void =
    var id = 0
    for x in 0..(this.w - 1):
        this.tiles.add(@[])
        this.units.add(@[])
        this.items.add(@[])
        for y in 0..(this.h - 1):
            let t = generate(x, y)
            t.id = id
            t.pos = initCoord(x, y)
            this.tiles[x].add(t)
            this.units[x].add(@[])
            this.items[x].add(@[])
            id += 1

# Returns the bounds of this World as a Coord
proc getBounds*(this: World): Coord {.exportc, dynlib.} =
    initCoord(this.w, this.h)

# Retrieves a Tile in this World
proc getTile*(this: World, c: Coord): Tile = this.tiles[c.x][c.y]

# Retrieves a set of Units on a Tile in this World
proc getUnits*(this: World, c: Coord): seq[Unit] = this.units[c.x][c.y]

# Retrieves a set of Items on a Tile in this World
proc getItems*(this: World, c: Coord): seq[Item] = this.items[c.x][c.y]

# Moves a Unit from one Tile in this World to another
proc moveUnit*(this: World, u: Unit, c: Coord): void {.exportc, dynlib.} =
    this.units[u.pos.x][u.pos.y] = this.units[u.pos.x][u.pos.y].filterIt(it != u)
    this.units[c.x][c.y].add(u)
    u.pos = c

# Moves an Item from one Tile in this World to another (or to add/remove it from the World)
proc moveItem*(this: World, i: Item, c: Option[Coord]): void =
    if i.pos.isSome:
        this.items[i.pos.get().x][i.pos.get().y] = this.items[i.pos.get().x][i.pos.get().y].filterIt(it != i)
    if c.isSome:
        this.items[c.get().x][c.get().y].add(i)
    i.pos = c

# Returns true if the World contains a Tile at the given Coord
proc contains*(this: World, c: Coord): bool = c.x >= 0 and c.y >= 0 and c.x < this.w and c.y < this.h

# Return a MenuNode describing this
proc getMenuNode*(this: World, c: Coord, open: (MenuNode) -> void, equip: (Item) -> void, unequip: (Unit, Item) -> void): MenuNode =
    let node = newListNode()
    node.add(newTextNode(fmt"Tile {c}"))
    let units = this.getUnits(c)
    if units.len > 0:
        node.add(newSpaceNode())
        node.add(newTextNode(fmt"{units.len} unit(s):"))
    for u in units:
        let u1 = u
        let root = u.getMenuNode((i: Item) => unequip(u1, i))
        node.add(newButtonNode(u.name, () => open(root)))
    let items = this.getItems(c)
    if items.len > 0:
        node.add(newSpaceNode())
        node.add(newTextNode(fmt"{items.len} item(s):"))
    for i in items:
        let i1 = i
        let root = i.getFreeMenuNode(() => equip(i1))
        node.add(newButtonNode(i.name, () => open(root)))
    return node

# Return a set of tiles on the screen that you have visibility on
proc getVisibleTiles(this: World, topLeft: Coord, botRight: Coord): HashSet[Coord] =
    var tiles = initHashSet[Coord]()
    for x in topLeft.x..botRight.x:
        for y in topLeft.y..botRight.y:
            let c = initCoord(x, y)
            if this.contains(c):
                let units = this.getUnits(c)
                if units.len > 0:
                    tiles = tiles + getRadialHexagonCoords(c, botRight, 2)
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
                    drawHexagon(view.gameToScreen(center), YELLOW, view)
                elif targeted.isSome and targeted.get().contains(tile.pos):
                    drawHexagon(view.gameToScreen(center), DARKER, view)
                outlineHexagon(view.gameToScreen(center), view)

                # Draw Units but only on visible Tiles
                if tile.pos in visible:
                    let units = this.getUnits(tile.pos)
                    if units.len > 0:
                        sm.drawSprite(units[0].sprite, view, view.gameToScreen(initPosition(center.x - 12, center.y - 12)))
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

# Return a path from the unit's current position to the destination,
# making sure to respect which borders that unit can cross.
# This function implements A* algorithm, based on pseudocode from
# Wikipedia (https://en.wikipedia.org/wiki/A*_search_algorithm)
proc pathfind*(this: World, unit: Unit, dst: Coord): seq[Coord]=
    const infinity = high(int)

    # Heuristic function for the A* algorithm
    proc dist(c: Coord): int = int(ceil((abs(dst.x - c.x) + abs(dst.y - c.y)) / 2))

    # Initialize relevant tables and sets
    var openSet: seq[Coord] = @[unit.pos]
    var cameFrom = initTable[Coord, Coord]()
    var gScore = initTable[Coord, int]()
    var fScore = initTable[Coord, int]()
    fScore[unit.pos] = dist(unit.pos)
    gScore[unit.pos] = 0

    # Main algorithm body
    var current: Coord
    while openSet.len > 0:
        current = foldl(openSet, if fScore.getOrDefault(a, infinity) < fScore.getOrDefault(b, infinity): a else: b, openSet[0])

        # We made it!
        if current == dst:
            echo("We made it!")
            var path: seq[Coord] = @[current]
            while cameFrom.hasKey(current):
                current = cameFrom[current]
                path.insert(current, 0)
            for c in path:
                echo(c)
            return @[]

        # Filter openSet and check adjacent tiles
        openSet = openSet.filterIt(it != current)
        let neighbors = current.getAdjacentHexagonCoords(initCoord(this.w, this.h))
        for adj in neighbors:

            # Skip any neighbors with borders the unit cannot cross
            if not this.canUnitTravelAcrossTiles(unit, current, adj):
                continue

            let g = if gScore.hasKey(current): gScore[current] + 1 else : infinity
            if g < gScore.getOrDefault(adj, infinity):
                cameFrom[adj] = current
                gScore[adj] = g
                fScore[adj] = g + dist(adj)
                if not (adj in openSet):
                    openSet.add(adj)

    # Failed state, there is no path :(
    echo("No way to get there :(")
    return @[]