import std/options
import kingdom/generation/types
import kingdom/controls/types
import kingdom/wrapper/types
import kingdom/models/types
import kingdom/math/types

# Parent type for different game screens
type View* = ref object of RootObj
    keyboard*: KeyboardState
    mouse*: MouseState

# Empty base implementations for methods in the View class
method getNextView*(this: View): View {.base.} = this
method draw*(this: View): void {.base.} = discard
method consumeKeyboardUpdates*(this: View): void {.base.} = discard
method consumeMouseUpdates*(this: View): void {.base.} = discard

# Game type used to aggregate relevant data and used in mod init functions
type GameView* = ref object of View
    menu*: Option[Menu]
    nextAbilityId*: int
    nextUnitId*: int
    nextItemId*: int
    targeter*: Targeter
    sprites*: SpriteManager
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    itemGeneration*: ItemGenerationManager
    abilityGeneration*: AbilityGenerationManager
    edgeTileSprite*: SpriteHandle
    hoveredHex*: Option[Coord]
    world*: World
    view*: Viewport

# Type representing the game's start menu
type StartView* = ref object of View
    menu*: Menu
    dead*: bool
