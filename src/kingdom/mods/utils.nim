import std/sugar
import std/options
import std/strformat
import kingdom/generation/manager
import kingdom/controls/targeting
import kingdom/views/types
import kingdom/views/game
import kingdom/models/types
import kingdom/wrapper/sprites
import kingdom/wrapper/types
import kingdom/entities/signals
import kingdom/entities/ability
import kingdom/entities/types
import kingdom/entities/stats
import kingdom/entities/quest
import kingdom/entities/unit
import kingdom/entities/item
import kingdom/builtin/types
import kingdom/builtin/signals
import kingdom/builtin/channels
import kingdom/builtin/values
import kingdom/models/world
import kingdom/models/types
import kingdom/mods/types
import kingdom/mods/core
import kingdom/operators

# Collects the code for a basic attack ability
proc attack*(game: ModCoreInterface, args: BaseSignalArgs, dtype: DamageType, dmg: int): void {.exportc, dynlib.} =
    let a = cast[AbilityClickedSignalArgs](args)
    let view = game.getGameView()
    let enemies = view.world.getEnemies(a.host)
    view.targeter.target(enemies, (u: Unit) => a.host.dealDamage(u, dtype, dmg))

# Instantiates a basic attack Ability
proc basicAttack*(game: ModCoreInterface, name: string, dtype: DamageType, dmg: int): Ability {.exportc, dynlib.} =
    let ability = newAbility()
    ability.name = name
    ability.desc = some(fmt"Deals {dmg} {dtype} damage")
    ability.addSignalHandler(ABILITY_CLICKED_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
        game.attack(args, dtype, dmg)
    )
    return ability

# Shorthand to give some Unit armor against a certain DamageType
proc addArmor*(unit: Unit, dtype: DamageType, dmg: int): void {.exportc, dynlib.} =
    let armor = newAbility()
    if dtype == DamageType.PHYSICAL:
        armor.name = "Physical Armor"
    else:
        armor.name = "Magical Armor"
    armor.desc = some(fmt"{dmg} armor against {dtype} attacks")
    armor.addSignalHandler(TAKE_DAMAGE_CHANNEL, proc (this: Ability, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[TakeDamageSignalArgs](args)
        if a.dtype == dtype:
            a.dmg -= dmg
            if a.dmg < 0:
                a.dmg = 0
    )
    unit.abilities.add(armor)

# Shorthand to give some Unit an Ability
proc ability*(this: Unit, game: ModCoreInterface, ability: string): void {.exportc, dynlib.} =
    this.abilities.add(game.rules.abilityGeneration.generate(ability))

# Shorthand to grab a Tile tilesheet sprite
proc getUnitSprite*(game: ModCoreInterface, sheet: SheetHandle, ix: uint16, iy: uint16): SpriteHandle {.exportc, dynlib.} =
    game.rules.sprites.getSpriteHandle(sheet, ix * 48, iy * 48, 48, 48)

# Shorthand to grab a Unit tilesheet sprite
proc getTileSprite*(game: ModCoreInterface, sheet: SheetHandle, ix: uint16, iy: uint16): SpriteHandle {.exportc, dynlib.} =
    game.rules.sprites.getSpriteHandle(sheet, ix * 96, iy * 110, 96, 110)

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

# Sets up an Item as gold
proc createGoldItem*(game: ModCoreInterface, name: string, quantity: int): Item {.exportc, dynlib.} =
    let item = newItem()
    item.name = name
    item.desc = fmt"Worth {quantity} gold"
    item.addSignalHandler(ITEM_CONSUMED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[ItemConsumedSignalArgs](args)
        a.host.gold += quantity
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
proc dropLoot*(unit: Unit, game: ModCoreInterface, items: seq[string]): void {.exportc, dynlib.} =
    unit.addSignalHandler(UNIT_DIES_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let view = game.getGameView()
        for item in items:
            discard view.addNewItem(item, unit.pos)
    )

# Sets up an Item to have a basic +/- effect on some Stat
proc modifyUserStat*(item: Item, game: ModCoreInterface, label: string, value: int): void {.exportc, dynlib.} =
    item.addSignalHandler(CAN_BE_EQUIPPED_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[CanBeEquippedSignalArgs](args)
        a.equippable = a.host.hasStat(label)
    )
    item.addSignalHandler(GET_STAT_CHANNEL, proc (this: Item, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[GetStatSignalArgs](args)
        if a.label == label:
            a.stat += value
    )

# Sets this Unit's movement value
proc setSpeed*(unit: Unit, speed: int): void {.exportc, dynlib.} =
    if speed == DEFAULT_MOVEMENT:
        return
    unit.addSignalHandler(GET_MOVEMENT_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[GetMovementSignalArgs](args)
        a.movement = speed
    )

# Sets this Unit's visibility value
proc setVision*(unit: Unit, vision: int): void {.exportc, dynlib.} =
    if vision == DEFAULT_VISIBILITY:
        return
    unit.addSignalHandler(GET_VISIBILITY_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[GetVisibilitySignalArgs](args)
        a.visibility = vision
    )

# Sets this Unit's max hunger value
proc setMaxHunger*(unit: Unit, hunger: int): void {.exportc, dynlib.} =
    if hunger == DEFAULT_MAX_HUNGER:
        return
    unit.addSignalHandler(GET_MAX_HUNGER_CHANNEL, proc (this: Unit, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[GetMaxHungerSignalArgs](args)
        a.hunger = hunger
    )

# Sets up a Tile to have a string of encounters
proc encounters*(tile: Tile, game: ModCoreInterface, enemies: seq[string]): void {.exportc, dynlib.} =
    var index = 0
    tile.addSignalHandler(UNIT_KILLED_CHANNEL, proc(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[UnitKilledSignalArgs](args)
        if a.killed.name == enemies[index] and index < enemies.len - 1:
            index += 1
            discard game.getGameView().addNewUnit(enemies[index], this.pos, AMBIENT_PLAYER)
    )
    tile.addSignalHandler(INIT_CHANNEL, proc(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
        discard game.getGameView().addNewUnit(enemies[0], this.pos, AMBIENT_PLAYER)
    )

# Sets up a Tile with some enemies and a Quest to slay them all
# TODO change the GameView to ModCoreInterface when this function actually gets used in mod code
proc ambientPartyQuest*(tile: Tile, game: GameView, enemies: seq[string], reward: string, giveReward: (this: Tile, game: GameView) -> void): void {.exportc, dynlib.} =
    let quest = newQuest(
        enemies.len,
        (a: int, n: int) => fmt"{a}/{n} enemies killed",
        fmt"Kill all the enemies on this tile",
        reward
    )
    quest.addSignalHandler(UNIT_KILLED_CHANNEL, proc(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
        let a = cast[UnitKilledSignalArgs](args)
        if a.killed.player == AMBIENT_PLAYER:
            this.tickQuest()
    )
    quest.addSignalHandler(INIT_CHANNEL, proc(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void =
        for enemy in enemies:
            discard game.addNewUnit(enemy, this.pos, AMBIENT_PLAYER)
    )
    quest.addSignalHandler(
        QUEST_COMPLETE_CHANNEL,
        (this: Tile, ctx: SignalContext, args: BaseSignalArgs) => this.giveReward(game)
    )
    tile.quest = some(quest)
