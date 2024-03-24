import kingdom/types/game
import kingdom/types/entities
import kingdom/generation
import kingdom/entities/unit

proc willThisWork*(): Unit =
    echo("Generating...")
    return newUnit()

proc initKingdomMod(game: Game): void {.exportc,dynlib.} =
    echo("Write your mod here!")
    game.unitGeneration.addGenerator("test", newUnit)
    #game.unitGeneration.addGenerator("test", proc (): Unit =
    #    echo("Generating...")
    #    return newUnit()
    #)
