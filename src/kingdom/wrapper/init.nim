import raylib
import std/sets
import kingdom/controls/types
import kingdom/controls/mouse
import kingdom/controls/keyboard
import kingdom/math/types
import kingdom/types

# Record mouse state for later consumption
proc handleLogic(this: MouseState): void =
    let x = getMouseX()
    let y = getMouseY()
    this.wasDown = this.down
    this.wasScrolling = this.scrolling
    if not this.down and isMouseButtonDown(MouseButton.Left):
        this.mouseDown(initPosition(x, y))
    elif this.down and isMouseButtonUp(MouseButton.Left):
        this.mouseUp(initPosition(x, y))
    else:
        this.mouseMove(initPosition(x, y))

# Record keyboard state for later consumption
proc handleLogic(this: KeyboardState): void =
    var pressed = HashSet[int]()
    var keycode = int(getKeyPressed())
    while keycode > 0:
        pressed.incl(keycode)
        keycode = int(getKeyPressed())
    for k in this.down:
        {.warning[HoleEnumConv]: off.}
        if isKeyDown(KeyboardKey(k)):
            pressed.incl(k)
    this.keysPressed(pressed)

# Handles game loop logic
proc gameLoop*(initial: Screen): void =
    # Initialize the Raylib game window
    initWindow(1200, 800, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)
    var screen = initial

    # Raylib game loop
    while not windowShouldClose():
        screen.mouse.handleLogic()
        screen.keyboard.handleLogic()
        screen.consumeKeyboardUpdates()
        screen.consumeMouseUpdates()
        beginDrawing()
        screen.draw()
        endDrawing()
        screen = screen.getNextScreen()
