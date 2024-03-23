import raylib
import kingdom/mods/loader
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/test

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
        for x in 0..9:
            for y in 0..4:
                let coords = getHexagonCenterPoint(Coord(x: x, y: y))
                let v = Vector2(x: coords[0], y: coords[1])
                drawPoly(v, 6, hexagons.SIDE, 90, GREEN)
                drawPolyLines(v, 6, hexagons.SIDE, 90, BLACK)
        drawText("Hello, world!", 0, 0, 20, BLACK)
        endDrawing()
