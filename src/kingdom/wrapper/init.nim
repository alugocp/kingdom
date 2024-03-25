import raylib
import kingdom/controls
import kingdom/world
import kingdom/game

# Record mouse state for later consumption
proc handleMouseLogic(m: MouseState): void =
    let x = getMouseX()
    let y = getMouseY()
    if not m.down and isMouseButtonDown(MouseButton.Left):
        m.mouseDown(float(x), float(y))
    elif m.down and isMouseButtonUp(MouseButton.Left):
        m.mouseUp(float(x), float(y))
    else:
        m.mouseMove(float(x), float(y))

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
        clearBackground(RAYWHITE)
        game.world.draw(game.view.dx, game.view.dy)
        endDrawing()