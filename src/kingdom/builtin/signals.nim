import kingdom/entities/types
import kingdom/entities/stats
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/builtin/channels
import kingdom/builtin/values
import kingdom/builtin/types

# BUILT-IN UNIT SIGNAL ARGS TYPES

# Payload when checking if the Unit can cross a certain border
type CanCrossBorderSignalArgs* = ref object of BaseSignalArgs
    canCross*: bool
    side*: HexSides
    border*: string
    tile*: Tile

proc newCanCrossBorderSignalArgs*(tile: Tile, side: HexSides, border: string): CanCrossBorderSignalArgs =
    new result
    result.channel = CAN_CROSS_BORDER_CHANNEL
    result.canCross = (border == OPEN_BORDER)
    result.border = border
    result.side = side
    result.tile = tile

# Payload when calculating a Unit's visibility
type GetVisibilitySignalArgs* = ref object of BaseSignalArgs
    visibility*: Natural

proc newGetVisibilitySignalArgs*(): GetVisibilitySignalArgs =
    new result
    result.channel = GET_VISIBILITY_CHANNEL
    result.visibility = 1

# Payload when calculating a Unit's movement
type GetMovementSignalArgs* = ref object of BaseSignalArgs
    movement*: Natural

proc newGetMovementSignalArgs*(): GetMovementSignalArgs =
    new result
    result.channel = GET_MOVEMENT_CHANNEL
    result.movement = 1

# Payload when a Unit gains XP
type GainXpSignalArgs* = ref object of BaseSignalArgs
    xp*: int

proc newGainXpSignalArgs*(xp: int): GainXpSignalArgs =
    new result
    result.channel = GAIN_XP_CHANNEL
    result.xp = xp

# Payload when a Unit levels up
type LevelUpSignalArgs* = ref object of BaseSignalArgs

proc newLevelUpSignalArgs*(xp: int): LevelUpSignalArgs =
    new result
    result.channel = LEVEL_UP_CHANNEL

# Payload to calculate a Unit's max health
type GetMaxHealthSignalArgs* = ref object of BaseSignalArgs
    health*: int

proc newGetMaxHealthSignalArgs*(health: int): GetMaxHealthSignalArgs =
    new result
    result.channel = GET_MAX_HEALTH_CHANNEL
    result.health = health

# Payload when a Unit takes damage
type TakeDamageSignalArgs* = ref object of BaseSignalArgs
    dtype*: DamageType
    dmg*: int

proc newTakeDamageSignalArgs*(dtype: DamageType, dmg: int): TakeDamageSignalArgs =
    new result
    result.channel = TAKE_DAMAGE_CHANNEL
    result.dtype = dtype
    result.dmg = dmg

# Payload when a Unit deals damage
type DealDamageSignalArgs* = ref object of BaseSignalArgs
    dtype*: DamageType
    attacker*: Unit
    target*: Unit
    dmg*: int

proc newDealDamageSignalArgs*(dtype: DamageType, dmg: int, attacker: Unit, target: Unit): DealDamageSignalArgs =
    new result
    result.channel = DEAL_DAMAGE_CHANNEL
    result.attacker = attacker
    result.target = target
    result.dtype = dtype
    result.dmg = dmg

# Payload to calculate a Unit's max hunger
type GetMaxHungerSignalArgs* = ref object of BaseSignalArgs
    hunger*: int

proc newGetMaxHungerSignalArgs*(hunger: int): GetMaxHungerSignalArgs =
    new result
    result.channel = GET_MAX_HUNGER_CHANNEL
    result.hunger = hunger

# Payload when a Unit dies
type UnitDiesSignalArgs* = ref object of BaseSignalArgs

proc newUnitDiesSignalArgs*(): UnitDiesSignalArgs =
    new result
    result.channel = UNIT_DIES_CHANNEL

# BUILT-IN TILE SIGNAL ARGS TYPES

# BUILT-IN ABILITY SIGNAL ARGS TYPES

# Payload when an Ability is clicked (for action-based abilities)
type AbilityClickedSignalArgs* = ref object of BaseSignalArgs
    host*: Unit

proc newAbilityClickedSignalArgs*(host: Unit): AbilityClickedSignalArgs =
    new result
    result.channel = ABILITY_CLICKED_CHANNEL
    result.host = host

# Payload when an Ability must calculate its potential targets (template for each possible target type)
type GetAbilityTargetsSignalArgs*[T] = ref object of BaseSignalArgs
    host*: Unit
    targets*: seq[T]

proc newGetAbilityCoordTargetsSignalArgs*(host: Unit): GetAbilityTargetsSignalArgs[Coord] =
    new result
    result.channel = GET_ABILITY_COORD_TARGETS_CHANNEL
    result.host = host
    result.targets = @[]

# Payload when calculating a Unit's stats
type GetStatsSignalArgs* = ref object of BaseSignalArgs
    unit*: Unit
    stats*: Stats

proc newGetStatsSignalArgs*(unit: Unit): GetStatsSignalArgs =
    new result
    result.unit = unit
    result.stats = newStats()

# BUILT-IN ITEM SIGNAL ARGS TYPES

# Payload when determining if this Item can be equipped by a given Unit
type CanBeEquippedSignalArgs* = ref object of BaseSignalArgs
    equippable*: bool
    unit*: Unit
    item*: Item

proc newCanBeEquippedSignalArgs*(unit: Unit, item: Item): CanBeEquippedSignalArgs =
    new result
    result.channel = CAN_BE_EQUIPPED_CHANNEL
    result.equippable = true
    result.unit = unit
    result.item = item

# Payload when an Item is consumed
type ItemConsumedSignalArgs* = ref object of BaseSignalArgs
    host*: Unit

proc newItemConsumedSignalArgs*(host: Unit): ItemConsumedSignalArgs =
    new result
    result.channel = ITEM_CONSUMED_CHANNEL
    result.host = host
