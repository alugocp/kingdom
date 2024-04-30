import std/options
import kingdom/builtin/types
import kingdom/controls/types
import kingdom/models/types
import kingdom/math/types

# Enum used to differentiate subtypes of View
type ViewType* = enum
    START
    GAME

# Parent type for different game screens
type View* = ref object of RootObj
    viewType*: ViewType
    rules*: GameRuleData
    keyboard*: KeyboardState
    mouse*: MouseState

# Empty base implementations for methods in the View class
method getNextView*(this: View): View {.base.} = this
method draw*(this: View): void {.base.} = discard
method consumeKeyboardUpdates*(this: View): void {.base.} = discard
method consumeMouseUpdates*(this: View): void {.base.} = discard

# Game type used to aggregate relevant data and used in mod init functions
type GameView* = ref object of View
    state*: GameState
    menu*: Option[Menu]
    nextAbilityId*: int
    nextPartyId*: int
    nextUnitId*: int
    nextItemId*: int
    targeter*: Targeter
    hoveredHex*: Option[Coord]
    world*: World
    view*: Viewport

# Type representing the game's start menu
type StartView* = ref object of View
    menu*: Menu
    dead*: bool
