import raylib
import kingdom/controls/mouse
import kingdom/wrapper/draw
import kingdom/math/types
import kingdom/colors
import kingdom/game

# Record mouse state for later consumption
proc handleMouseLogic(m: MouseState): void =
    let x = getMouseX()
    let y = getMouseY()
    m.wasDown = m.down
    if not m.down and isMouseButtonDown(MouseButton.Left):
        m.mouseDown(Position(x: float(x), y: float(y)))
    elif m.down and isMouseButtonUp(MouseButton.Left):
        m.mouseUp(Position(x: float(x), y: float(y)))
    else:
        m.mouseMove(Position(x: float(x), y: float(y)))

# Handles game loop logic
proc gameLoop*(game: Game): void =
    # Initialize the Raylib game window
    initWindow(800, 450, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)

    # Raylib game loop
    while not windowShouldClose():
        handleMouseLogic(game.mouse)
        game.consumeMouseUpdates()
        beginDrawing()
        setBackground(colors.BLUE)
        game.draw()
        endDrawing()