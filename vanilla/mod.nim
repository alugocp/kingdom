import kingdom/game
import kingdom/entities/types
import kingdom/generation
import kingdom/entities/unit

proc initKingdomMod(game: Game): void {.exportc, dynlib.} =
    echo("Write your mod here!")
    game.unitGeneration.addGenerator("test", proc (): Unit =
        echo("Generating...")
        return newUnit()
    )
