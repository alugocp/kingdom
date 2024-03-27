import std/options
import kingdom/game
import kingdom/entities/types
import kingdom/generation
import kingdom/entities/unit

proc initKingdomMod(game: Game): void {.exportc, dynlib.} =
    game.unitGeneration.addGenerator("test", proc (): Unit =
        let unit = newUnit()
        unit.name = "Vanilla mod unit"
        unit.desc = some("Imported via modding!")
        return unit
    )
