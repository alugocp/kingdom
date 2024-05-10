import std/sequtils
import std/tables
import kingdom/entities/types

# Internal helper function for shared Unit/Tile/Item logic
proc internalHandleSignal(this: Entity, ctx: SignalContext, step: SignalContextElement, args: BaseSignalArgs): SignalContext =

    # Don't do anything if this entity has no relevant handler(s)
    if not this.handlers.hasKey(args.channel):
        return

    # Avoid infinite signal loops
    if step in ctx:
        return

    # Kick off the relevant handlers
    for handler in this.handlers[args.channel]:
        handler(this, concat(ctx, @[step]), args)

# Tells an Ability to handle some incoming signal
proc handleSignal*(this: Ability, ctx: SignalContext, args: BaseSignalArgs): void {.exportc: "handleSignal_ability", dynlib.} =
    discard internalHandleSignal(this, ctx, (EntityTypes.ABILITY_TYPE, this.id, args.channel), args)

# Tells a Item to handle some incoming signal
proc handleSignal*(this: Item, ctx: SignalContext, args: BaseSignalArgs): void {.exportc: "handleSignal_item", dynlib.} =
    discard internalHandleSignal(this, ctx, (EntityTypes.ITEM_TYPE, this.id, args.channel), args)

# Tells a Tile to handle some incoming signal
proc handleSignal*(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void {.exportc: "handleSignal_tile", dynlib.} =
    discard internalHandleSignal(this, ctx, (EntityTypes.TILE_TYPE, this.id, args.channel), args)

# Tells a Unit to handle some incoming signal
proc handleSignal*(this: Unit, ctx: SignalContext, args: BaseSignalArgs): void {.exportc: "handleSignal_unit", dynlib.} =
    let unitCtx = internalHandleSignal(this, ctx, (EntityTypes.UNIT_TYPE, this.id, args.channel), args)
    for ability in this.abilities:
        ability.handleSignal(unitCtx, args)
    for status in this.statuses:
        status.effect.handleSignal(unitCtx, args)
    for item in this.items:
        item.handleSignal(unitCtx, args)

# Returns true if this Entity has a handler for the given channel
proc hasSignalHandler*(this: Entity, channel: string): bool =
    this.handlers.hasKey(channel)

# Adds a SignalHandler to some Entity
proc addSignalHandler*[T: Entity](this: T, channel: string, handler: SignalHandler[T]): void =
    if not this.hasSignalHandler(channel):
        this.handlers[channel] = @[]
    this.handlers[channel].add(handler)

proc addSignalHandler1*(this: Ability, channel: string, handler: SignalHandler[Ability]): void {.exportc: "addSignalHandler_ability", dynlib.} =
    addSignalHandler(this, channel, handler)
proc addSignalHandler1*(this: Item, channel: string, handler: SignalHandler[Item]): void {.exportc: "addSignalHandler_item", dynlib.} =
    addSignalHandler(this, channel, handler)
proc addSignalHandler1*(this: Unit, channel: string, handler: SignalHandler[Unit]): void {.exportc: "addSignalHandler_unit", dynlib.} =
    addSignalHandler(this, channel, handler)
proc addSignalHandler1*(this: Tile, channel: string, handler: SignalHandler[Tile]): void {.exportc: "addSignalHandler_tile", dynlib.} =
    addSignalHandler(this, channel, handler)