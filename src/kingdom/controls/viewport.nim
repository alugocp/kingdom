import kingdom/math/types
import kingdom/math/hexagons
import kingdom/wrapper/window
import kingdom/controls/types

const UPPER_ZOOM = 3f
const LOWER_ZOOM = 0.5f

# Constructor for Viewport type
proc newViewport*(): Viewport =
    new result
    result.scale = 2.0
    result.dx = 0.0
    result.dy = 0.0

# Converts a game position to a screen position
proc gameToScreen*(this: Viewport, pos: Position): Position =
    initPosition(
        (pos.x * this.scale) + this.dx,
        (pos.y * this.scale) + this.dy
    )

# Converts a screen position to a game position
proc screenToGame*(this: Viewport, pos: Position): Position =
    initPosition(
        (pos.x - this.dx) / this.scale,
        (pos.y - this.dy) / this.scale
    )

# Scrolls the Viewport
proc scroll*(this: Viewport, dx: float, dy: float, w: Natural, h: Natural) =
    this.dx += dx
    this.dy += dy
    let window = getWindowBounds()
    if this.dx > 0: this.dx = 0
    if this.dy > 0: this.dy = 0
    let bounds = this.gameToScreen(initPosition(
        initCoord(w - 1, 1).getHexagonCenterPoint().x + HALF_W,
        initCoord(0, h - 1).getHexagonCenterPoint().y + SIDE
    ))
    if bounds.x < window.x:
        this.dx += window.x - bounds.x
    if bounds.y < window.y:
        this.dy += window.y - bounds.y

# Returns true if the given game Position is on the screen
proc isOnScreen*(this: Viewport, g: Position): bool =
    let window = getWindowBounds()
    let p = this.gameToScreen(g)
    p.x >= 0 and p.x <= window.x and p.y >= 0 and p.y <= window.y

# Returns true if the given game Rect is on the screen
proc isOnScreen*(this: Viewport, g: Rect): bool =
    let window = getWindowBounds()
    let p = this.gameToScreen(initPosition(g.x, g.y))
    let r = initRect(p.x, p.y, g.w * this.scale, g.h * this.scale)
    r.x + r.w >= 0 and r.x <= window.x and r.y + r.h >= 0 and r.y <= window.y

# Returns true if the given hexagon's game position is on the screen
proc isHexOnScreen*(this: Viewport, h: Position): bool =
    this.isOnScreen(initRect(h.x - HALF_W, h.y - SIDE, HALF_W * 2, SIDE * 2))

proc zoom*(this: Viewport, dz: float, w: Natural, h: Natural): void =
    # Calculate center of screen as a game position
    let window = getWindowBounds()
    let g = this.screenToGame(initPosition(window.x / 2, window.y / 2))

    # Set new scale
    this.scale += dz
    if this.scale > UPPER_ZOOM: this.scale = UPPER_ZOOM
    if this.scale < LOWER_ZOOM: this.scale = LOWER_ZOOM

    # Shift scroll to keep that point in the center
    let s = this.gameToScreen(g)
    this.scroll(
        (window.x / 2) - s.x,
        (window.y / 2) - s.y,
        w,
        h
    )
