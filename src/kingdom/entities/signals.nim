import sequtils
import std/tables
import kingdom/types/entities
import kingdom/types/signals

# Tells a Unit or Tile to handle some incoming signal
proc handleSignal*(x: Entity, ctx: SignalContext, args: BaseSignalArgs): void =

    # Don't do anything if this entity has no relevant handler(s)
    if not x.handlers.hasKey(args.channel):
        return

    # Avoid infinite signal loops
    let step = (x.id, args.channel)
    if step in ctx:
        return

    # Kick off the relevant handlers
    for handler in x.handlers[args.channel]:
        handler(concat(ctx, @[step]), args, x)

# Adds a signal handler to some entity
proc addSignalHandler*[T: Entity](x: T, channel: string, handler: SignalHandler[T]): void =
    if not x.handlers.hasKey(channel):
        discard x.handlers.hasKeyOrPut(channel, newSeq[SignalHandler[T]]())
    x.handlers[channel].add(handler)