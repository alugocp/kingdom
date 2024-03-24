import std/tables
import kingdom/types/entities
import kingdom/types/signals
import kingdom/math/types

# Constructor for the Unit type
proc newUnit*(): Unit =
    result = Unit()
    result.id = 1
    result.pos = Coord(x: 0, y: 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    return result