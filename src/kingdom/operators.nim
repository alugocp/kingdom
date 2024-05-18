import std/strformat
import kingdom/math/types
import kingdom/builtin/types

# Stringify the Coord type
proc `$`*(this: Coord): string = fmt"({this.x}, {this.y})"

# Combination logic between two MovementTypes
proc `*`*(a: MovementType, b: MovementType): MovementType =
    if a == MovementType.OVERRIDE or b == MovementType.OVERRIDE:
        return MovementType.OVERRIDE
    if a == MovementType.CROSS and b == MovementType.CROSS:
        return MovementType.CROSS
    return MovementType.BLOCKED