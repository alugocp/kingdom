import std/sequtils
import std/tables
import kingdom/entities/types
import kingdom/types/signals

# Internal helper function for shared Unit/Tile/Item logic
proc internalHandleSignal(x: Entity, ctx: SignalContext, step: SignalContextElement, args: BaseSignalArgs): SignalContext =

    # Don't do anything if this entity has no relevant handler(s)
    if not x.handlers.hasKey(args.channel):
        return

    # Avoid infinite signal loops
    if step in ctx:
        return

    # Kick off the relevant handlers
    for handler in x.handlers[args.channel]:
        handler(x, concat(ctx, @[step]), args)

# Tells an Ability to handle some incoming signal
proc handleSignal*(x: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
    discard internalHandleSignal(x, ctx, (EntityTypes.ABILITY_TYPE, x.id, args.channel), args)

# Tells a Item to handle some incoming signal
proc handleSignal*(x: Item, ctx: SignalContext, args: BaseSignalArgs): void =
    discard internalHandleSignal(x, ctx, (EntityTypes.ITEM_TYPE, x.id, args.channel), args)

# Tells a Tile to handle some incoming signal
proc handleSignal*(x: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
    discard internalHandleSignal(x, ctx, (EntityTypes.TILE_TYPE, x.id, args.channel), args)

# Tells a Unit to handle some incoming signal
proc handleSignal*(x: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
    let unitCtx = internalHandleSignal(x, ctx, (EntityTypes.UNIT_TYPE, x.id, args.channel), args)
    for ability in x.abilities:
        ability.handleSignal(unitCtx, args)
    for item in x.items:
        item.handleSignal(unitCtx, args)

# Returns true if this Entity has a handler for the given channel
proc hasSignalHandler*(x: Entity, channel: string): bool =
    x.handlers.hasKey(channel)

# Adds a SignalHandler to some Entity
proc addSignalHandler*[T: Entity](x: T, channel: string, handler: SignalHandler[T]): void =
    if not x.hasSignalHandler(channel):
        x.handlers[channel] = @[]
    x.handlers[channel].add(handler)
