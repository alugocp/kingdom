import kingdom/math/types

# Aggregate of mouse state data
type MouseState* = ref object
    wasScrolling*: bool
    scrolling*: bool
    posdown: Position
    posprev*: Position
    pos*: Position
    wasDown*: bool
    down*: bool

# Constructor for MouseState
proc newMouseState*(): MouseState =
    new result
    result.posdown = Position(x: 0.0, y: 0.0)
    result.posprev = Position(x: 0.0, y: 0.0)
    result.pos = Position(x: 0.0, y: 0.0)
    result.wasScrolling = false
    result.scrolling = false
    result.wasDown = false
    result.down = false

# Change the state when the user presses down on the mouse
proc mouseDown*(m: MouseState, pos: Position): void =
    m.posdown = pos
    m.posprev = pos
    m.pos = pos
    m.down = true

# Change the state when the user moves the mouse
proc mouseMove*(m: MouseState, pos: Position): void =
    m.posprev = m.pos
    m.pos = pos
    if not m.scrolling and m.down and abs(m.pos.x - m.posdown.x) + abs(m.pos.y - m.posdown.y) >= 25:
        m.scrolling = true

# Change the state when the user releases a button on the mouse
proc mouseUp*(m: MouseState, pos: Position): void =
    m.scrolling = false
    m.down = false
    m.pos = pos
