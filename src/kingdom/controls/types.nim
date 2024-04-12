import std/sets
import std/sugar
import std/options
import kingdom/entities/types
import kingdom/math/types

# Type to handle game view scrolling and zoom in/out
type Viewport* = ref object
    scale*: float
    dx*: float
    dy*: float

# Helper class to handle targeting logic
type Targeter* = ref object
    coords*: Option[seq[Coord]]
    units*: Option[seq[Unit]]
    coordHandler*: Option[(Coord) -> void]
    unitHandler*: Option[(Unit) -> void]
    onTarget*: () -> void

# Aggregate of mouse state data
type MouseState* = ref object
    wasScrolling*: bool
    scrolling*: bool
    posdown*: Position
    posprev*: Position
    pos*: Position
    wasDown*: bool
    down*: bool

# Object to track user keyboard input
type KeyboardState* = ref object
    released*: HashSet[int]
    down*: HashSet[int]

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