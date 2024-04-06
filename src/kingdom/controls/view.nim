import kingdom/math/types
import kingdom/math/hexagons
import kingdom/wrapper/window

type View* = ref object
    scale*: float
    dx*: float
    dy*: float

# Constructor for View type
proc newView*(): View =
    new result
    result.scale = 2.0
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

# Converts a game position to a screen position
proc gameToScreen*(this: View, pos: Position): Position =
    initPosition(
        (pos.x * this.scale) + this.dx,
        (pos.y * this.scale) + this.dy
    )

# Converts a screen position to a game position
proc screenToGame*(this: View, pos: Position): Position =
    initPosition(
        (pos.x - this.dx) / this.scale,
        (pos.y - this.dy) / this.scale
    )

# Returns true if the given game Position is on the screen
proc isOnScreen*(this: View, g: Position): bool =
    let window = getWindowBounds()
    let p = this.gameToScreen(g)
    p.x >= 0 and p.x <= window.x and p.y >= 0 and p.y <= window.y

# Returns true if the given game Rect is on the screen
proc isOnScreen*(this: View, g: Rect): bool =
    let window = getWindowBounds()
    let p = this.gameToScreen(initPosition(g.x, g.y))
    let r = initRect(p.x, p.y, g.w * this.scale, g.h * this.scale)
    r.x + r.w >= 0 and r.x <= window.x and r.y + r.h >= 0 and r.y <= window.y

# Returns true if the given hexagon's game position is on the screen
proc isHexOnScreen*(this: View, h: Position): bool =
    this.isOnScreen(initRect(h.x - HALF_W, h.y - SIDE, HALF_W * 2, SIDE * 2))
