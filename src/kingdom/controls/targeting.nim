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

proc newTargeterInternal(): Targeter =
    new result
    result.coords = none(seq[Coord])
    result.units = none(seq[Unit])
    result.coordHandler = none((Coord) -> void)
    result.unitHandler = none((Unit) -> void)
    return result

# Instantiate a Targeter focused on Coords
proc newTargeter*(coords: seq[Coord], coordHandler: (Coord) -> void): Targeter =
    result = newTargeterInternal()
    result.coords = some(coords)
    result.coordHandler = some(coordHandler)
    return result