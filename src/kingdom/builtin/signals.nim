import kingdom/entities/types
import kingdom/entities/stats
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/builtin/channels
import kingdom/builtin/values

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
    return result

# Payload when calculating a Unit's visibility
type GetVisibilitySignalArgs* = ref object of BaseSignalArgs
    visibility*: Natural

proc newGetVisibilitySignalArgs*(): GetVisibilitySignalArgs =
    new result
    result.channel = GET_VISIBILITY_CHANNEL
    result.visibility = 1
    return result

# Payload when calculating a Unit's movement
type GetMovementSignalArgs* = ref object of BaseSignalArgs
    movement*: Natural

proc newGetMovementSignalArgs*(): GetMovementSignalArgs =
    new result
    result.channel = GET_MOVEMENT_CHANNEL
    result.movement = 1
    return result

# Payload when a Unit gains XP
type GainXpSignalArgs* = ref object of BaseSignalArgs
    xp*: int

proc newGainXpSignalArgs*(xp: int): GainXpSignalArgs =
    new result
    result.channel = GAIN_XP_CHANNEL
    result.xp = xp
    return result

# Payload when a Unit levels up
type LevelupSignalArgs* = ref object of BaseSignalArgs

proc newLevelupSignalArgs*(xp: int): LevelupSignalArgs =
    new result
    result.channel = LEVELUP_CHANNEL
    return result

# BUILT-IN TILE SIGNAL ARGS TYPES

# BUILT-IN ABILITY SIGNAL ARGS TYPES

# Payload when an Ability is clicked (for action-based abilities)
type AbilityClickedSignalArgs* = ref object of BaseSignalArgs
    host*: Unit

proc newAbilityClickedSignalArgs*(host: Unit): AbilityClickedSignalArgs =
    new result
    result.channel = ABILITY_CLICKED_CHANNEL
    result.host = host
    return result

# Payload when an Ability must calculate its potential targets (template for each possible target type)
type GetAbilityTargetsSignalArgs*[T] = ref object of BaseSignalArgs
    host*: Unit
    targets*: seq[T]

proc newGetAbilityCoordTargetsSignalArgs*(host: Unit): GetAbilityTargetsSignalArgs[Coord] =
    new result
    result.channel = GET_ABILITY_COORD_TARGETS_CHANNEL
    result.host = host
    result.targets = @[]
    return result

# Payload when calculating a Unit's stats
type GetStatsSignalArgs* = ref object of BaseSignalArgs
    unit*: Unit
    stats*: Stats

proc newGetStatsSignalArgs*(unit: Unit): GetStatsSignalArgs =
    new result
    result.unit = unit
    result.stats = newStats()
    return result

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
    return result
