import std/math
import std/tables
import std/sequtils
import kingdom/types/entities
import kingdom/entities/tile
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/stringify

# World type to contain Tile objects
type World* = ref object
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
        for y in 0..(h - 1):
            result.tiles[x].add(newTile(id, Coord(x: x, y: y)))
            id += 1
    return result

# Retrieves a Tile in this World
proc getTile*(world: World, x: Natural, y: Natural): Tile =
    return world.tiles[x][y]

# Return a path from the unit's current position to the destination,
# making sure to respect which borders that unit can cross.
# This function implements A* algorithm, based on pseudocode from
# Wikipedia (https://en.wikipedia.org/wiki/A*_search_algorithm)
proc pathfind*(world: World, unit: Unit, dst: Coord): seq[Coord]=
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
        let neighbors = current.getAdjacentHexagonCoords(Coord(x: world.w, y: world.h))
        # TODO filter neighbors by the unit's canCrossBorder signal
        for adj in neighbors:
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