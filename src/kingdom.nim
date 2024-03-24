import sequtils
import raylib
import kingdom/entities/signals
import kingdom/types/signals
import kingdom/mods/loader
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/controls
import kingdom/generation
import kingdom/game

# Record mouse state for later consumption
proc handleMouseLogic(m: ref MouseState): void =
    let x = getMouseX()
    let y = getMouseY()
    if not m.down and isMouseButtonDown(MouseButton.Left):
        m.mouseDown(float(x), float(y))
    elif m.down and isMouseButtonUp(MouseButton.Left):
        m.mouseUp(float(x), float(y))
    else:
        m.mouseMove(float(x), float(y))

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom",dynlib.} =
    var game = newGame()
    discard loadMod("/home/alexander/Desktop/kingdom/vanilla/out/vanilla-linux", game)

    # Test for signal handlers
    let u = game.unitGeneration.generate("test")
    let args = GetHealthSignalArgs(channel: "GetHealth", health: 30)
    u.handleSignal(@[], args)

    # Initialize the Raylib game window
    initWindow(800, 450, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)

    # Raylib game loop
    let m = newMouseState()
    var dx = 0.0
    var dy = 0.0
    while not windowShouldClose():
        handleMouseLogic(m)
        if m.down:
            dx += m.pos[0] - m.posprev[0]
            dy += m.pos[1] - m.posprev[1]
        beginDrawing()
        clearBackground(RAYWHITE)
        for x in 0..9:
            for y in 0..4:
                let coords = getHexagonCenterPoint(Coord(x: x, y: y))
                let v = Vector2(x: coords[0] + dx, y: coords[1] + dy)
                drawPoly(v, 6, hexagons.SIDE, 90, GREEN)
                drawPolyLines(v, 6, hexagons.SIDE, 90, BLACK)
        drawText("Hello, world!", 0, 0, 20, BLACK)
        endDrawing()
