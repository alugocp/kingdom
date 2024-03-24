import std/sugar
import std/tables

# Base signal arguments payload
type BaseSignalArgs* = ref object of RootObj
    channel*: string

# Stack type to help prevent infinite loops in signal processing
type SignalContext* = seq[(int, string)]

# Signal handler type
type SignalHandler*[T] = (SignalContext, BaseSignalArgs, T) -> void

# Signal handlers collection type
type SignalHandlersTable*[T] = Table[string, seq[SignalHandler[T]]]
