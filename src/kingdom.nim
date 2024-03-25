import std/tables
import raylib
import kingdom/mods/loader
import kingdom/math/types
import kingdom/controls
import kingdom/generation
import kingdom/world
import kingdom/game
import kingdom/entities/tile
import kingdom/menu

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
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    let world = newWorld(20, 10)
    world.getTile(Coord(x: 0, y: 0)).setTileBorder(HexSides.RIGHT, "denied!")
    var game = newGame(world)
    discard loadMod("/home/alexander/Desktop/kingdom/vanilla/out/vanilla-linux", game)

    # Test for signal handlers
    let u = game.unitGeneration.generate("test")
    discard world.pathfind(u, Coord(x: 2, y: 2))

    let menu = newListNode(cast[seq[MenuNode]](@[
        newTextNode("Hello!"),
        newTextNode("We got menus now")
    ]))

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
        world.draw(dx, dy)
        menu.draw(float(0), float(0))
        endDrawing()
