import kingdom/types/signals
import kingdom/types/entities
import kingdom/math/types

# BUILT-IN SIGNAL ARGS TYPES

# Payload when checking if the unit can cross a certain border
type CanCrossBorderSignalArgs* = ref object of BaseSignalArgs
    canCross*: bool
    side*: HexSides
    border*: string
    tile*: Tile

proc newCanCrossBorderSignalArgs*(tile: Tile, side: HexSides, border: string): CanCrossBorderSignalArgs =
    result = CanCrossBorderSignalArgs()
    result.channel = "CanCrossBorder"
    result.canCross = (border == OPEN_BORDER)
    result.border = border
    result.side = side
    result.tile = tile
    return result