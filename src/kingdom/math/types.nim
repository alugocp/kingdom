
# Game coordinate type
type Coord* = object
    x*: Natural
    y*: Natural

# Like a Coordinate but with floats
type Position* = object
    x*: float
    y*: float

# Float bounds
type Rect* = object
    x*: float
    y*: float
    w*: float
    h*: float

# Returns true if this Position falls within the given Rect
proc within*(p: Position, r: Rect): bool = p.x >= r.x and p.x <= r.x + r.w and p.y >= r.y and p.y <= r.y + r.h