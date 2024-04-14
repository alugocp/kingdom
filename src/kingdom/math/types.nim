
# Game coordinate type
type Coord* = object
    x*: int
    y*: int

proc initCoord*(x: int, y: int): Coord = Coord(x: x, y: y)

# Like a Coordinate but with floats
type Position* = object
    x*: float
    y*: float

proc initPosition*(x: SomeNumber, y: SomeNumber): Position = Position(x: float(x), y: float(y))

# Float bounds
type Rect* = object
    x*: float
    y*: float
    w*: float
    h*: float

proc initRect*(x: SomeNumber, y: SomeNumber, w: SomeNumber, h: SomeNumber): Rect =
    Rect(x: float(x), y: float(y), w: float(w), h: float(h))

# Returns true if this Position falls within the given Rect
proc within*(p: Position, r: Rect): bool = p.x >= r.x and p.x <= r.x + r.w and p.y >= r.y and p.y <= r.y + r.h