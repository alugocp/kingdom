import kingdom/math/types

type View* = ref object
    dx*: float
    dy*: float

# Constructor for View type
proc newView*(): View =
    new result
    result.dx = 0.0
    result.dy = 0.0

# Scroll the View
proc scroll*(this: View, dx: float, dy: float) =
    this.dx += dx
    this.dy += dy

# Adds the total scroll offset to the given Position
proc withOffset*(this: View, p: Position): Position =
    newPosition(p.x - this.dx, p.y - this.dy)