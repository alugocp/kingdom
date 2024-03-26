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
    m.wasScrolling = m.scrolling
    if not m.down and isMouseButtonDown(MouseButton.Left):
        m.mouseDown(newPosition(x, y))
    elif m.down and isMouseButtonUp(MouseButton.Left):
        m.mouseUp(newPosition(x, y))
    else:
        m.mouseMove(newPosition(x, y))

# Handles game loop logic
proc gameLoop*(game: Game): void =
    # Initialize the Raylib game window
    initWindow(1200, 800, "Kingdom")
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