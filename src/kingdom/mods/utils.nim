import std/sugar
import kingdom/generation/manager
import kingdom/controls/targeting
import kingdom/views/types
import kingdom/views/game
import kingdom/models/types
import kingdom/wrapper/sprites
import kingdom/wrapper/types
import kingdom/entities/signals
import kingdom/entities/types
import kingdom/entities/unit
import kingdom/entities/item
import kingdom/builtin/types
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/models/world
import kingdom/models/types
import kingdom/mods/types
import kingdom/mods/core

# Collects the code for a basic attack ability
proc attack*(game: ModCoreInterface, args: BaseSignalArgs, dtype: DamageType, dmg: int): void {.exportc, dynlib.} =
    let a = cast[AbilityClickedSignalArgs](args)
    let view = game.getGameView()
    let enemies = view.world.getEnemies(a.host)
    view.targeter.target(enemies, (u: Unit) => a.host.dealDamage(u, dtype, dmg))

# Shorthand to give some Unit armor against a certain DamageType
proc addArmor*(u: Unit, dtype: DamageType, dmg: int): void {.exportc, dynlib.} =
    u.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[TakeDamageSignalArgs](args)
        if a.dtype == dtype:
            a.dmg -= dmg
            if a.dmg < 0:
                a.dmg = 0
    )

# Shorthand to give some Unit an Ability
proc giveAbility*(game: ModCoreInterface, unit: Unit, ability: string): void {.exportc, dynlib.} =
    unit.abilities.add(game.rules.abilityGeneration.generate(ability))

# Shorthand to grab a tilesheet sprite
proc getUnitSprite*(game: ModCoreInterface, sheet: SheetHandle, ix: uint16, iy: uint16): SpriteHandle {.exportc, dynlib.} =
    game.rules.sprites.getSpriteHandle(sheet, ix * 24, iy * 24, 24, 24)

# Sets up an Item as food
proc createFoodItem*(game: ModCoreInterface, name: string): Item {.exportc, dynlib.} =
    let item = newItem()
    item.name = name
    item.desc = "Consume to reset a unit's hunger"
    item.addSignalHandler(ITEM_CONSUMED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[ItemConsumedSignalArgs](args)
        a.host.feed(game.getGameView().state)
    )
    return item

# Collects code for a basic harvest ability
proc harvest*(game: ModCoreInterface, args: BaseSignalArgs, tileType: string, item: string): void {.exportc, dynlib.} =
    let a = cast[AbilityClickedSignalArgs](args)
    let view = game.getGameView()
    let tile = view.world.getTile(a.host.pos)
    if tile.name == tileType:
        discard view.addNewItem(item, a.host.pos)

# Dictates which Item(s) a Unit will drop when it dies
proc dropLoot*(game: ModCoreInterface, unit: Unit, items: seq[string]): void {.exportc, dynlib.} =
    unit.addSignalHandler(UNIT_DIES_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let view = game.getGameView()
        for item in items:
            discard view.addNewItem(item, unit.pos)
    )
