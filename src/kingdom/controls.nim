
# Aggregate of mouse state data
type MouseState* = object
    posdown: (float, float)
    posprev*: (float, float)
    pos*: (float, float)
    down*: bool

# Constructor for MouseState
proc newMouseState*(): ref MouseState =
    new result
    result.posdown = (0.0, 0.0)
    result.posprev = (0.0, 0.0)
    result.pos = (0.0, 0.0)
    result.down = false

# Change the state when the user presses down on the mouse
proc mouseDown*(m: ref MouseState, x: float, y: float): void =
    m.posdown = (x, y)
    m.posprev = (x, y)
    m.pos = (x, y)
    m.down = true

# Change the state when the user moves the mouse
proc mouseMove*(m: ref MouseState, x: float, y: float): void =
    m.posprev = m.pos
    m.pos = (x, y)

# Change the state when the user releases a button on the mouse
proc mouseUp*(m: ref MouseState, x: float, y: float): void =
    m.pos = (x, y)
    m.down = false