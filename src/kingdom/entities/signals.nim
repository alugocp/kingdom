import std/sequtils
import std/tables
import kingdom/entities/types
import kingdom/types/signals

# Internal helper function for shared Unit/Tile/Item logic
proc internalHandleSignal(x: Tile | Unit, ctx: SignalContext, args: BaseSignalArgs): SignalContext =

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

# Tells a Tile to handle some incoming signal
proc handleSignal*(x: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
    discard internalHandleSignal(x, ctx, args)

# Tells a Unit to handle some incoming signal
proc handleSignal*(x: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
    let unitCtx = internalHandleSignal(x, ctx, args)
    for ability in x.abilities:
        if ability.handlers.hasKey(args.channel):
            for handler in ability.handlers[args.channel]:
                handler(unitCtx, args, ability)
    for item in x.items:
        if item.handlers.hasKey(args.channel):
            for handler in item.handlers[args.channel]:
                handler(unitCtx, args, item)

# Adds a SignalHandler to some Entity
proc addSignalHandler*[T: Entity](x: T, channel: string, handler: SignalHandler[T]): void =
    if not x.handlers.hasKey(channel):
        x.handlers[channel] = @[]
    x.handlers[channel].add(handler)