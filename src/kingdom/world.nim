import kingdom/types/entities
import kingdom/entities/tile
import kingdom/math/types

# World type to contain Tile objects
type World* = ref object of RootObj
    tiles*: seq[seq[Tile]]
    w*: Natural
    h*: Natural

# Constructor for the World type
proc newWorld*(w: Natural, h: Natural): World =
    result = World()
    result.w = w
    result.h = h
    var id = 0
    for x in 0..(w - 1):
        result.tiles.add(@[])
        for y in 0..(h - 1):
            result.tiles[x].add(newTile(id, Coord(x: x, y: y)))
            id += 1
    return result