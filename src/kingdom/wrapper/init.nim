import raylib
import kingdom/controls/mouse
import kingdom/wrapper/sprites
import kingdom/math/types
import kingdom/game

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

# Handles game loop logic
proc gameLoop*(game: Game): void =
    # Initialize the Raylib game window
    initWindow(1200, 800, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)
    game.sprites.loadAllSheets()

    # Raylib game loop
    while not windowShouldClose():
        game.mouse.handleLogic()
        game.consumeMouseUpdates()
        beginDrawing()
        game.draw()
        endDrawing()