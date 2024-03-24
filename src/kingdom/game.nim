import std/tables
import kingdom/types/generation
import kingdom/types/entities
import kingdom/types/game

proc newUnitGenerationManager(): UnitGenerationManager =
    return GenerationManager[Unit](
        generators: initTable[string, FullGenerator[Unit]]()
    )

proc newTileGenerationManager(): TileGenerationManager =
    return GenerationManager[Tile](
        generators: initTable[string, FullGenerator[Tile]]()
    )

proc newGame*(): Game =
    return Game(
        unitGeneration: newUnitGenerationManager(),
        tileGeneration: newTileGenerationManager()
    )