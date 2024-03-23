import std/tables
import kingdom/entities/signals
import kingdom/types/entities
import kingdom/types/signals
import kingdom/math/types

# Constructor for the Unit type
proc newUnit*(): ref Unit =
    new result
    result.id = 1
    result.pos = Coord(x: 0, y: 0)
    result.handlers = initTable[string, seq[SignalHandler]]()
    result.addSignalHandler("GetHealth", proc (ctx: SignalContext, x: BaseSignalArgs): void =
        if x of GetHealthSignalArgs:
            echo "Is GetHealthSignalArgs"
        else:
            echo "Is not!"
    )