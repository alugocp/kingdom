import sequtils
import std/tables
import kingdom/entities/unit
import kingdom/entities/tile
import kingdom/entities/signals

# Retrieve a Unit's ID
proc getId(x: ref Unit): int = x.id

# Calculate a Tile's ID
proc getId(x: ref Tile): int = (x.pos.y * 100) + x.pos.x

# Tells a Unit or Tile to handle some incoming signal
proc handleSignal*(x: ref Unit | ref Tile, ctx: SignalContext, args: BaseSignalArgs): void =

    # Don't do anything if this entity has no relevant handler(s)
    if not x.handlers.hasKey(args.channel):
        return

    # Avoid infinite signal loops
    let step = (x.getId(), args.channel)
    if step in ctx:
        return

    # Kick off the relevant handlers
    for handler in x.handlers[args.channel]:
        handler(concat(ctx, @[step]), args)