import std/math
import std/sugar
import std/tables
import std/options
import std/sequtils
import std/strformat
import kingdom/builtin/signals
import kingdom/entities/types
import kingdom/entities/tile
import kingdom/entities/unit
import kingdom/entities/signals
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/stringify
import kingdom/wrapper/draw
import kingdom/builtin/values
import kingdom/menu

# World type to contain Tile objects
type World* = ref object
    units*: seq[seq[seq[Unit]]]
    tiles*: seq[seq[Tile]]
    w*: Natural
    h*: Natural

# Constructor for the World type
proc newWorld*(w: Natural, h: Natural): World =
    new result
    result.w = w
    result.h = h
    var id = 0
    for x in 0..(w - 1):
        result.tiles.add(@[])
        result.units.add(@[])
        for y in 0..(h - 1):
            result.tiles[x].add(newTile(id, initCoord(x, y)))
            result.units[x].add(@[])
            id += 1
    return result

# Returns the bounds of this World as a Coord
proc getBounds*(this: World): Coord =
    initCoord(this.w, this.h)

# Retrieves a Tile in this World
proc getTile*(this: World, c: Coord): Tile = this.tiles[c.x][c.y]

# Retrieves a set of Units on a Tile in this World
proc getUnits*(this: World, c: Coord): seq[Unit] = this.units[c.x][c.y]

# Moves a unit from one Tile in this World to another
proc moveUnit*(this: World, u: Unit, c: Coord): void =
    this.units[u.pos.x][u.pos.y] = this.units[u.pos.x][u.pos.y].filterIt(it != u)
    this.units[c.x][c.y].add(u)
    u.pos = c

# Returns true if the World contains a Tile at the given Coord
proc contains*(this: World, c: Coord): bool = c.x < this.w and c.y < this.h

# Return a MenuNode describing this
proc getMenuNode*(this: World, c: Coord, open: (MenuNode) -> void): MenuNode =
    let node = newListNode()
    node.add(newTextNode(fmt"Tile {c}"))
    let units = this.getUnits(c)
    if units.len > 0:
        node.add(newTextNode(fmt"{units.len} unit(s):"))
    for u in units:
        let root = u.getMenuNode()
        node.add(newButtonNode(u.name, proc (): void = open(root)))
    return node

# Draw this World object
proc draw*(this: World, hovered: Option[Coord], targeted: Option[seq[Coord]], dx: float, dy: float): void =
    for column in this.tiles:
        for tile in column:
            let coords = getHexagonCenterPoint(initCoord(tile.pos.x, tile.pos.y))
            var color = GREEN
            if targeted.isSome and targeted.get().contains(tile.pos):
                color = YELLOW
            if hovered.isSome and hovered.get() == tile.pos:
                color = DK_GREEN
            drawHexagon(coords.x + dx, coords.y + dy, color)

# Return a path from the unit's current position to the destination,
# making sure to respect which borders that unit can cross.
# This function implements A* algorithm, based on pseudocode from
# Wikipedia (https://en.wikipedia.org/wiki/A*_search_algorithm)
proc pathfind*(this: World, unit: Unit, dst: Coord): seq[Coord]=
    const infinity = high(int)

    # Checks if the Unit can cross the border from one Tile to another
    proc canTravelFromCurrent(current: Coord, adj: Coord): bool =
        let side = getSharedSide(current, adj)
        let opp = getOppositeSide(side)
        let tile1 = this.getTile(current)
        let tile2 = this.getTile(adj)
        var test1 = newCanCrossBorderSignalArgs(tile1, side, tile1.getTileBorder(side))
        var test2 = newCanCrossBorderSignalArgs(tile2, opp, tile2.getTileBorder(opp))
        if not test1.canCross:
            unit.handleSignal(@[], test1)
        if not test2.canCross:
            unit.handleSignal(@[], test2)
        return test1.canCross and test2.canCross

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
            if not canTravelFromCurrent(current, adj):
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