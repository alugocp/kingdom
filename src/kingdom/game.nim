import std/tables
import kingdom/types/generation
import kingdom/types/entities
import kingdom/types/game

proc newGame*(): Game =
    return Game(
        unitGeneration: UnitGenerationManager(generators: initTable[string, FullGenerator[Unit]]()),
        tileGeneration: TileGenerationManager(generators: initTable[string, FullGenerator[Tile]]())
    )