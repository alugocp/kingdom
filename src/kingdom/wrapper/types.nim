import raylib
import std/tables
import kingdom/builtin/values

#
# SPRITE MANAGER TYPES
#

# Numeric ID representing a registered spritesheet
type SheetHandle* = uint8

# Numeric value representing a sprite
type SpriteHandle* = uint64

# Null sprite value
const NULL_SPRITE* = 0'u64

# Handles loading and drawing sprite assets
type SpriteManager* = ref object
    spritesheets*: seq[string]
    loaded*: Table[string, Texture2D]

#
# DRAW WRAPPER TYPES
#

# Used by the wrapper to customize how text gets drawn
type FontSettings* = object
    color*: uint32
    size*: int

# Default font in the game
const REGULAR_FONT* = FontSettings(
    color: values.BLACK,
    size: 20
)

# Font for links
const LINK_FONT* = FontSettings(
    color: values.BLUE,
    size: 20
)

# Font for links when they're hovered over
const HOVER_FONT* = FontSettings(
    color: LIGHT_BLUE,
    size: 20
)

# Font for headers
const HEADER_FONT* = FontSettings(
    color: values.BLACK,
    size: 35
)