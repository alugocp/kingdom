import sequtils
import std/tables
import kingdom/types/entities
import kingdom/types/signals

# Tells a Unit or Tile to handle some incoming signal
proc handleSignal*(x: ref Entity, ctx: SignalContext, args: BaseSignalArgs): void =

    # Don't do anything if this entity has no relevant handler(s)
    if not x.handlers.hasKey(args.channel):
        return

    # Avoid infinite signal loops
    let step = (x.id, args.channel)
    if step in ctx:
        return

    # Kick off the relevant handlers
    for handler in x.handlers[args.channel]:
        handler(concat(ctx, @[step]), args)

# Adds a signal handler to some entity
proc addSignalHandler*(x: ref Entity, channel: string, handler: SignalHandler): void =
    if not x.handlers.hasKey(channel):
        x.handlers[channel] = @[]
    x.handlers[channel].add(handler)