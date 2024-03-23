import raylib
import mods/loader
import test

proc initKingdom(): void {.exportc: "init_kingdom",dynlib.} =
    echo("Hello, world!")
    let times = newCounter()
    hello(times)
    hello(times)
    discard loader.loadMod("/home/alexander/Desktop/kingdom/vanilla/out/vanilla-linux", times)

    # Initialize the Raylib game window
    initWindow(800, 450, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)

    while not windowShouldClose():
        beginDrawing()
        clearBackground(RAYWHITE)
        drawText("Hello, world!", 0, 0, 20, BLACK)
        endDrawing()
