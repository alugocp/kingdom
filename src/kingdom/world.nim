import std/tables
import std/sequtils
import kingdom/types/entities
import kingdom/entities/tile
import kingdom/math/hexagons
import kingdom/math/types

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

    var openSet: seq[Coord] = @[unit.pos]
    var cameFrom = initTable[Coord, Coord]()
    var gScore = initTable[Coord, int]()
    var fScore = initTable[Coord, int]()
    # gScore[unit.pos] = 0
    # fScore[unit.pos] = h(unit.pos)

    proc fScoreCalc(c: Coord): int =
        if fScore.hasKey(c):
            return fScore[c]
        return high(int)

    proc gScoreCalc(c: Coord): int =
        if gScore.hasKey(c):
            return gScore[c]
        return high(int)

    var current: Coord
    while openSet.len > 0:
        current = foldl(openSet, if fScoreCalc(a) < fScoreCalc(b): a else: b, openSet[0])

        # We made it!
        if current == dst:
            echo("We made it!")
            return @[]

        # Filter openSet and check adjacent tiles
        openSet = openSet.filterIt(it != current)
        let neighbors = current.getAdjacentHexagonCoords(Coord(x: world.w, y: world.h))
        # TODO filter neighbors by the unit's canCrossBorder signal
        for adj in neighbors:
            let g = gScoreCalc(current) + 1
            if g < gScoreCalc(adj):
                # cameFrom[adj] := current
                # gScore[adj] := g
                # fScore[adj] := g + h(neighbor)
                if not (adj in openSet):
                    openSet.add(adj)

    # Failed state, there is no path :(
    echo("No way to get there :(")
    return @[]