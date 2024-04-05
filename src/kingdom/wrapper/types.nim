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

#
# DRAW WRAPPER TYPES
#

# Used by the wrapper to customize how text gets drawn
type FontSettings* = object
    color*: uint32
    size*: int

# Default font in the game
const REGULAR_FONT* = FontSettings(
    color: BLACK,
    size: 20
)

# Font for "links"
const LINK_FONT* = FontSettings(
    color: BLUE,
    size: 20
)

# Font for headers
const HEADER_FONT* = FontSettings(
    color: BLACK,
    size: 35
)