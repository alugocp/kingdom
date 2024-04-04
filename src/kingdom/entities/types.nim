import std/sugar
import std/tables
import std/options
import kingdom/math/types
import kingdom/entities/stats
import kingdom/wrapper/types

# SIGNAL TYPES

# Used to help differentiate IDs in SignalContext
type EntityTypes* = enum
    UNIT_TYPE
    TILE_TYPE
    ITEM_TYPE
    ABILITY_TYPE

# Base signal arguments payload
type BaseSignalArgs* = ref object of RootObj
    channel*: string

# Type to help prevent infinite loops in signal processing
type SignalContextElement* = (EntityTypes, int, string)
type SignalContext* = seq[SignalContextElement]

# Signal handler type
type SignalHandler*[T] = (T, SignalContext, BaseSignalArgs) -> void

# Signal handlers collection type
type SignalHandlersTable*[T] = Table[string, seq[SignalHandler[T]]]

# ENTITY TYPES

# Actions or passive properties associated with a Unit
type Ability* = ref object
    id*: int
    name*: string
    desc*: Option[string]
    handlers*: SignalHandlersTable[Ability]

# Items that can be equipped by a Unit
type Item* = ref object
    id*: int
    name*: string
    desc*: string
    handlers*: SignalHandlersTable[Item]
    pos*: Option[Coord]

# Unit type for in-game characters
type Unit* = ref object
    id*: int
    pos*: Coord
    sprite*: SpriteHandle
    handlers*: SignalHandlersTable[Unit]
    name*: string
    desc*: Option[string]
    items*: seq[Item]
    abilities*: seq[Ability]
    statuses*: seq[Ability]
    stats*: Stats

# Tile type for the in-game map
type Tile* = ref object
    id*: int
    pos*: Coord
    sprite*: SpriteHandle
    handlers*: SignalHandlersTable[Tile]
    borders*: array[0..5, string]

# Combined entity type
type Entity* = Unit | Tile | Item | Ability