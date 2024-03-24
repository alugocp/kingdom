
# Game coordinate type
type Coord* = object
    x*: Natural
    y*: Natural

# Hexagon sides
type HexSides* = enum
    TOP_RIGHT
    TOP_LEFT
    RIGHT
    LEFT
    BOT_RIGHT
    BOT_LEFT