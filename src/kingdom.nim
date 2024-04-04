import kingdom/mods/loader
import kingdom/math/types
import kingdom/wrapper/init
import kingdom/entities/types
import kingdom/generation/types
import kingdom/generation/manager
import kingdom/world
import kingdom/game

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    let world = newWorld(20, 10)
    var game = newGame(world)
    discard game.loadMod("vanilla")
    world.build(proc (x: int, y: int): Tile = game.tileGeneration.generate(if x == 1 and y == 0: "Water" else: "Grass"))

    # Testing code
    discard game.addNewUnit("Plasmoid Adventurer", initCoord(0, 0))
    discard game.addNewItem("Ring of Strength", initCoord(1, 1))

    # Start the game loop
    game.gameLoop()
