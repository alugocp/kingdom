import std/tables
import kingdom/types/generation
import kingdom/types/entities
import kingdom/controls/mouse
import kingdom/controls/view
import kingdom/world

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    mouse*: MouseState
    world*: World
    view*: View

# Constructor for a Game type
proc newGame*(world: World): Game =
    return Game(
        unitGeneration: GenerationManager[Unit](
            generators: initTable[string, FullGenerator[Unit]]()
        ),
        tileGeneration: GenerationManager[Tile](
            generators: initTable[string, FullGenerator[Tile]]()
        ),
        mouse: newMouseState(),
        view: newView(),
        world: world
    )

proc consumeMouseUpdates*(this: Game): void =
    if this.mouse.down:
        this.view.scroll(
            this.mouse.pos[0] - this.mouse.posprev[0],
            this.mouse.pos[1] - this.mouse.posprev[1]
        )