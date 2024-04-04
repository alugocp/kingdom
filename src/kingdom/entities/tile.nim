import std/tables
import kingdom/wrapper/types
import kingdom/builtin/values
import kingdom/entities/types
import kingdom/math/hexagons
import kingdom/math/types

# Constructor for the Tile type
proc newTile*(): Tile =
    new result
    result.id = 1
    result.pos = initCoord(0, 0)
    result.sprite = NULL_SPRITE
    result.handlers = initTable[string, seq[SignalHandler[Tile]]]()
    for a in 0..5:
        result.borders[a] = OPEN_BORDER
    return result

# Sets a border value on this Tile
proc setBorder*(this: Tile, side: HexSides, border: string): void =
    this.borders[hexSideToInt(side)] = border

# Returns a border value on the given Tile
proc getBorder*(this: Tile, side: HexSides): string =
    return this.borders[hexSideToInt(side)]