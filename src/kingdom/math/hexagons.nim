import std/math
import std/options
import std/strformat
import kingdom/math/types

# Constants for optimal calculation
const r3 = sqrt(3.0)
const SIDE* = 25
const WIDTH = SIDE * r3
const DY = SIDE * 1.5
const HALF_W = WIDTH / 2
const r3n1 = 1 / r3
const HALF_S = SIDE / 2

# Hexagon sides
type HexSides* = enum
    TOP_RIGHT
    TOP_LEFT
    RIGHT
    LEFT
    BOT_RIGHT
    BOT_LEFT

# Calculate the center point of a hexagon at the given coordinate
proc getHexagonCenterPoint*(c: Coord): Position =
    return Position(
        x: (float(c.x) * WIDTH) + (float(c.y mod 2) * HALF_W) + HALF_W,
        y: (float(c.y) * DY) + SIDE
    )

# Returns a sequence of coords for hexagons adjacent to the one at the given coord
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] =
    var adjs = newSeq[Coord]()
    let dx = if c.y mod 2 == 1: 0 else: -1
    if c.y > 0:
        if c.x + dx >= 0:
            adjs.add(Coord(x: c.x + dx, y: c.y - 1)) # Above left
        if c.x + dx + 1 < bounds.x:
            adjs.add(Coord(x: c.x + dx + 1, y: c.y - 1)) # Above right
    if c.y + 1 < bounds.y:
        if c.x + dx >= 0:
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

# Gets the opposite side form the one given
proc getOppositeSide*(side: HexSides): HexSides =
    case side:
        of TOP_RIGHT: return BOT_LEFT
        of TOP_LEFT: return BOT_RIGHT
        of RIGHT: return LEFT
        of LEFT: return RIGHT
        of BOT_RIGHT: return TOP_LEFT
        of BOT_LEFT: return TOP_RIGHT

# Returns which side on hexagon h1 coincides with h2
proc getSharedSide*(h1: Coord, h2: Coord): HexSides =
    let dx = if h1.y mod 2 == 1: 0 else: -1
    if h2.x == h1.x + dx and h2.y == h1.y - 1: return TOP_LEFT
    if h2.x == h1.x + dx + 1 and h2.y == h1.y - 1: return TOP_RIGHT
    if h2.x == h1.x + dx and h2.y == h1.y + 1: return BOT_LEFT
    if h2.x == h1.x + dx + 1 and h2.y == h1.y + 1: return BOT_RIGHT
    if h2.x == h1.x - 1 and h2.y == h1.y: return LEFT
    if h2.x == h1.x + 1 and h2.y == h1.y: return RIGHT
    raise newException(Exception, fmt"The hexagons at {h1} and {h2} don't share any sides")

# Get the coordinates of the hexagon that this position falls into
proc getHexagonCoords*(p: Position): Option[Coord] =
    type Vector = object
        x: int
        y: int
    proc newVector(x: int, y: int): Vector = Vector(x: x, y: y)
    let g = newVector(int(floor(p.x / HALF_W)), int(floor(p.y / HALF_S)))
    if g.y mod 3 == 0:
        let z = newVector(g.x, int(g.y / 3))
        let l = newPosition(p.x - (float(g.x) * HALF_W), p.y - (float(g.y) * HALF_S))
        let b = newVector(int(floor((z.x - (z.y mod 2)) / 2)), z.y)
        var d = newVector(0, 0)
        if (z.x mod 2) == 0 and (z.y mod 2) == 0 and l.y < HALF_S - (r3n1 * l.x):
            d = newVector(-1, -1)
        elif (z.x mod 2) == 1 and (z.y mod 2) == 0 and l.y < r3n1 * l.x:
            d = newVector(0, -1)
        elif (z.x mod 2) == 0 and (z.y mod 2) == 1 and l.y < r3n1 * l.x:
            d = newVector(1, -1)
        elif (z.x mod 2) == 1 and (z.y mod 2) == 1 and l.y < HALF_S - (r3n1 * l.x):
            d = newVector(0, -1)
        if b.x + d.x >= 0 and b.y + d.y >= 0:
            return some(newCoord(b.x + d.x, b.y + d.y))
    else:
        let hy = int(floor(g.y / 3))
        let hx = int(floor((g.x - (hy mod 2)) / 2))
        if hx >= 0 and hy >= 0:
            return some(newCoord(hx, hy))
    return none(Coord)
