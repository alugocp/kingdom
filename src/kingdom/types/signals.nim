import std/sugar
import std/tables

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
type SignalHandler*[T] = (SignalContext, BaseSignalArgs, T) -> void

# Signal handlers collection type
type SignalHandlersTable*[T] = Table[string, seq[SignalHandler[T]]]
