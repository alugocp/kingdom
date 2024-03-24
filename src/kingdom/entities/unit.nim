import strformat
import std/tables
import kingdom/entities/signals
import kingdom/types/entities
import kingdom/types/signals
import kingdom/types/math

# Constructor for the Unit type
proc newUnit*(): Unit =
    result = Unit()
    result.id = 1
    result.pos = Coord(x: 0, y: 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.addSignalHandler("GetHealth", proc (ctx: SignalContext, x: BaseSignalArgs, u: Unit): void =
        if x.channel == "GetHealth":
            echo "Is GetHealthSignalArgs"
            let y: GetHealthsignalArgs = cast[GetHealthsignalArgs](x)
            echo(fmt"{y.health}")
            u.handleSignal(ctx, x)
        else:
            echo "Is not!"
    )
    return result