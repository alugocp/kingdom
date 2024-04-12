import std/options
import kingdom/generation/types
import kingdom/controls/types
import kingdom/entities/types
import kingdom/wrapper/types
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

# World type to contain Tile objects
type World* = ref object
    units*: seq[seq[seq[Unit]]]
    items*: seq[seq[seq[Item]]]
    tiles*: seq[seq[Tile]]
    w*: Natural
    h*: Natural

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
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
    keyboard*: KeyboardState
    mouse*: MouseState
    world*: World
    view*: View

# Type representing the game's start menu
type Start* = ref object
    keyboard*: KeyboardState
    mouse*: MouseState
    menu*: Menu
    dead*: bool

# Aggregate type for all game screens
type Screen* = Game | Start
