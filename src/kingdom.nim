import kingdom/mods/loader
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/wrapper/init
import kingdom/world
import kingdom/game
import kingdom/entities/tile

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    let world = newWorld(20, 10)
    world.getTile(initCoord(0, 0)).setBorder(HexSides.RIGHT, "denied!")
    var game = newGame(world)
    discard game.loadMod("vanilla")

    # Testing code
    discard game.addNewUnit("Plasmoid Adventurer", initCoord(0, 0))
    discard game.addNewItem("Ring of Strength", initCoord(1, 1))

    # Start the game loop
    game.gameLoop()
