import std/sets
import std/sugar
import std/options
import kingdom/entities/types
import kingdom/math/types

type View* = ref object
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