import std/math
import kingdom/math/types

# Constants for optimal calculation
const r3 = sqrt(3.0)
const SIDE* = 25
const WIDTH = SIDE * r3
const DY = SIDE * 1.5
const HALF_W = WIDTH / 2

# Calculate the center point of a hexagon at the given coordinate
proc getHexagonCenterPoint*(c: Coord): (float, float) =
    return (
        (float(c.x) * WIDTH) + (float(c.y mod 2) * HALF_W) + HALF_W,
        (float(c.y) * DY) + SIDE
    )

# Returns a sequence of coords for hexagons adjacent to the one at the given coord
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] =
    var adjs = newSeq[Coord]()
    let dx = if c.x mod 2 == 1: 0 else: -1
    if c.y > 0:
        if c.x + dx > 0:
            adjs.add(Coord(x: c.x + dx, y: c.y - 1)) # Above left
        if c.x + dx + 1 < bounds.x:
            adjs.add(Coord(x: c.x + dx + 1, y: c.y - 1)) # Above right
    if c.y + 1 < bounds.y:
        if c.x + dx > 0:
            adjs.add(Coord(x: c.x + dx, y: c.y + 1)) # Bottom left
        if c.x + dx + 1 < bounds.x:
            adjs.add(Coord(x: c.x + dx + 1, y: c.y + 1)) # Bottom right
    if c.x > 0:
        adjs.add(Coord(x: c.x - 1, y: c.y)) # Left
    if c.x + 1 < bounds.x:
        adjs.add(Coord(x: c.x + 1, y: c.y)) # Right
    return adjs

# Converts a HexSide to an integer result
proc hexSideToInt*(side: HexSides): int =
    case side:
        of TOP_RIGHT: return 0
        of TOP_LEFT: return 1
        of RIGHT: return 2
        of LEFT: return 3
        of BOT_RIGHT: return 4
        of BOT_LEFT: return 5