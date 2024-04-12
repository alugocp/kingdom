import std/options
import kingdom/generation/types
import kingdom/controls/types
import kingdom/wrapper/types
import kingdom/models/types
import kingdom/math/types

# Enum representing the types of UI elements
type MenuElement* = enum
    TEXT
    HEADER
    BUTTON
    SPACE
    LIST

# Parent class for all menu nodes
type MenuNode* = ref object of RootObj
    element*: MenuElement

# Top-level menu type
type Menu* = ref object
    root*: MenuNode
    width*: float
    x*: float
    y*: float

# Parent type for different game screens
type Screen* = ref object of RootObj
    keyboard*: KeyboardState
    mouse*: MouseState

# Empty base implementations for methods in the Screen class
method getNextScreen*(this: Screen): Screen {.base.} = this
method draw*(this: Screen): void {.base.} = discard
method consumeKeyboardUpdates*(this: Screen): void {.base.} = discard
method consumeMouseUpdates*(this: Screen): void {.base.} = discard

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object of Screen
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
type Start* = ref object of Screen
    menu*: Menu
    dead*: bool
