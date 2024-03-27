import std/tables
import kingdom/entities/types
import kingdom/types/signals
import kingdom/math/types

# Constructor for the Unit type
proc newUnit*(): Unit =
    new result
    result.id = 1
    result.name = "unnamed"
    result.pos = newCoord(0, 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.abilities = @[]
    result.items = @[]
    return result