import kingdom/math/types

# Aggregate of mouse state data
type MouseState* = ref object
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

# Change the state when the user releases a button on the mouse
proc mouseUp*(m: MouseState, pos: Position): void =
    m.pos = pos
    m.down = false