import kingdom/math/types
import kingdom/math/hexagons
import kingdom/wrapper/window

type View* = ref object
    dx*: float
    dy*: float

# Constructor for View type
proc newView*(): View =
    new result
    result.dx = 0.0
    result.dy = 0.0

# Scroll the View
proc scroll*(this: View, dx: float, dy: float, w: Natural, h: Natural) =
    this.dx += dx
    this.dy += dy
    let window = getWindowBounds()
    if this.dx > 0:
        this.dx = 0
    else:
        let right = initCoord(w - 1, 1).getHexagonCenterPoint().x + HALF_W
        if right + this.dx < window.x:
            this.dx = window.x - right
    if this.dy > 0:
        this.dy = 0
    else:
        let bot = initCoord(0, h - 1).getHexagonCenterPoint().y + SIDE
        if bot + this.dy < window.y:
            this.dy = window.y - bot

# Adds the total scroll offset to the given Position
proc withOffset*(this: View, p: Position): Position =
    initPosition(p.x - this.dx, p.y - this.dy)