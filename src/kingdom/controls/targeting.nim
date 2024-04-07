import std/sugar
import std/options
import kingdom/entities/types
import kingdom/math/types
import kingdom/controls/types

# Clear the data inside this Targeter
proc cancel*(this: Targeter): void =
    this.coords = none(seq[Coord])
    this.units = none(seq[Unit])
    this.coordHandler = none((Coord) -> void)
    this.unitHandler = none((Unit) -> void)

# Instantiate an inactive Targeter
proc newTargeter*(): Targeter =
    new result
    result.onTarget = proc () = discard
    result.cancel()
    return result

# Returns true if this Targeter is targeting Coords
proc isCoords*(this: Targeter): bool = this.coords.isSome

# Returns true if this Targeter is targeting Units
proc isUnits*(this: Targeter): bool = this.units.isSome

# Points this Targeter towards some Coords
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void): void = # {.exportc, dynlib.} =
    this.coordHandler = some(coordHandler)
    this.coords = some(coords)
    this.onTarget()

# Points this Targeter towards some Units
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void): void {.exportc, dynlib.} =
    this.unitHandler = some(unitHandler)
    this.units = some(units)
    this.onTarget()
