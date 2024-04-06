import std/sugar
import std/options
import kingdom/entities/types
import kingdom/math/types

# Helper class to handle targeting logic
type Targeter* = ref object
    coords*: Option[seq[Coord]]
    units*: Option[seq[Unit]]
    coordHandler*: Option[(Coord) -> void]
    unitHandler*: Option[(Unit) -> void]
    onTarget*: () -> void

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
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void) =
    this.coordHandler = some(coordHandler)
    this.coords = some(coords)
    this.onTarget()

# Points this Targeter towards some Units
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void) =
    this.unitHandler = some(unitHandler)
    this.units = some(units)
    this.onTarget()
