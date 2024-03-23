import std/tables
import kingdom/math/types
import kingdom/entities/signals

# Unit type for in-game characters
type Unit* = object
    pos*: Coord
    handlers*: SignalHandlersTable
    id*: int

# Constructor for the Unit type
proc newUnit*(): ref Unit =
    new result
    result.pos = Coord(x: 0, y: 0)
    result.handlers = initTable[string, seq[SignalHandler]]()
    result.handlers["GetHealth"] = @[]
    result.handlers["GetHealth"].add(
        proc (ctx: SignalContext, x: BaseSignalArgs): void =
            if x of GetHealthSignalArgs:
                echo "Is GetHealthSignalArgs"
            else:
                echo "Is not!"
    )