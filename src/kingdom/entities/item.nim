import std/tables
import kingdom/types/signals
import kingdom/entities/types

# Constructor for the Item type
proc newItem*(): Item =
    new result
    result.name = "unnamed item"
    result.desc = "just some item"
    result.handlers = initTable[string, seq[SignalHandler[Item]]]()
    return result