import std/strformat
import kingdom/math/types

# Stringify the Coord type
proc `$`*(this: Coord): string = fmt"({this.x}, {this.y})"