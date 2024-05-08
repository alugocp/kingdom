import std/sets
import std/tables
import std/options
import kingdom/wrapper/types
import kingdom/builtin/values
import kingdom/entities/types
import kingdom/math/hexagons
import kingdom/math/types
import kingdom/quest

# Constructor for the Tile type
proc newTile*(name: string): Tile {.exportc, dynlib.} =
    new result
    result.id = 1
    result.name = name
    result.pos = initCoord(0, 0)
    result.desc = none(string)
    result.sprite = NULL_SPRITE
    result.tags = initHashSet[string]()
    result.handlers = initTable[string, seq[SignalHandler[Tile]]]()
    result.quest = none(Quest)
    for a in 0..5:
        result.borders[a] = OPEN_BORDER

# Sets all border values on this Tile
proc setAllBorders*(this: Tile, border: string): void {.exportc, dynlib.} =
    for a in 0..5:
        this.borders[a] = border

# Sets a border value on this Tile
proc setBorder*(this: Tile, side: HexSides, border: string): void =
    this.borders[hexSideToInt(side)] = border

# Returns a border value on the given Tile
proc getBorder*(this: Tile, side: HexSides): string =
    return this.borders[hexSideToInt(side)]