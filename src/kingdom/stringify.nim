import std/strformat
import kingdom/math/types

# Stringify the Coord type
proc `$`*(c: Coord): string = fmt"{c.x}, {c.y}"