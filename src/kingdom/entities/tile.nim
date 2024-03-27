import std/tables
import kingdom/builtin/values
import kingdom/entities/types
import kingdom/math/hexagons
import kingdom/math/types

# Constructor for the Tile type
proc newTile*(id: int, pos: Coord): Tile =
    new result
    result.id = id
    result.pos = pos
    result.handlers = initTable[string, seq[SignalHandler[Tile]]]()
    for a in 0..5:
        result.borders[a] = OPEN_BORDER
    return result

# Sets a border value on this Tile
proc setTileBorder*(tile: Tile, side: HexSides, border: string): void =
    tile.borders[hexSideToInt(side)] = border

# Returns a border value on the given Tile
proc getTileBorder*(tile: Tile, side: HexSides): string =
    return tile.borders[hexSideToInt(side)]