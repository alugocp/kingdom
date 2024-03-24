import std/tables
import kingdom/types/generation
import kingdom/types/entities
import kingdom/world

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    world*: World

# Constructor for a Game type
proc newGame*(world: World): Game =
    return Game(
        unitGeneration: GenerationManager[Unit](
            generators: initTable[string, FullGenerator[Unit]]()
        ),
        tileGeneration: GenerationManager[Tile](
            generators: initTable[string, FullGenerator[Tile]]()
        ),
        world: world
    )