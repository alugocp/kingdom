import kingdom/types/game
#import kingdom/types/api
#import kingdom/types/entities

proc initKingdomMod(game: Game): void {.exportc,dynlib.} =
    echo("Write your mod here!")
    #game.unitGeneration.addGenerator("test", proc (): Unit =
    #    return newUnit()
    #)
