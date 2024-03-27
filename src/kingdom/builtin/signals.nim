import kingdom/types/signals
import kingdom/entities/types
import kingdom/math/hexagons

# BUILT-IN UNIT SIGNAL ARGS TYPES

# Payload when checking if the unit can cross a certain border
type CanCrossBorderSignalArgs* = ref object of BaseSignalArgs
    canCross*: bool
    side*: HexSides
    border*: string
    tile*: Tile

proc newCanCrossBorderSignalArgs*(tile: Tile, side: HexSides, border: string): CanCrossBorderSignalArgs =
    new result
    result.channel = "CanCrossBorder"
    result.canCross = (border == OPEN_BORDER)
    result.border = border
    result.side = side
    result.tile = tile
    return result

# BUILT-IN TILE SIGNAL ARGS TYPES

# BUILT-IN ABILITY SIGNAL ARGS TYPES

# Payload when an ability is clicked (for action-based abilities)
type AbilityClickedSignalArgs* = ref object of BaseSignalArgs
    host*: Unit

proc newAbilityClickedSignalArgs*(host: Unit): AbilityClickedSignalArgs =
    new result
    result.channel = "AbilityClicked"
    result.host = host
    return result

# Payload when an ability must calculate its potential targets
type GetAbilityTargetsSignalArgs*[T: Entity] = ref object of BaseSignalArgs
    host*: Unit
    targets*: seq[T]

proc newGetAbilityTargetsSignalArgs*[T: Entity](host: Unit): GetAbilityTargetsSignalArgs[T] =
    new result
    result.channel = "GetAbilityTargets"
    result.host = host
    result.targets = @[]
    return result

# BUILT-IN ITEM SIGNAL ARGS TYPES
