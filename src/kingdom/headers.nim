import std/sugar
import kingdom/entities/types
import kingdom/controls/types
import kingdom/wrapper/types
import kingdom/math/types
import kingdom/types

# src/kingdom/wrapper/sprites.nim
proc registerSheet*(this: SpriteManager, modname: string, filename: string): SheetHandle {.importc.}
proc getSpriteHandle*(this: SpriteManager, id: SheetHandle, x: uint8, y: uint8, w: uint8 = 24, h: uint8 = 24): SpriteHandle {.importc.}

# src/kingdom/generation/manager.nim
# proc addGenerator*[T: Entity](this: GenerationManager[T], key: string, generator: Generator[T]): void {.importc.}
# proc generate*[T: Entity](this: GenerationManager[T], key: string): T {.importc.}

# src/kingdom/entities/unit.nim
proc newUnit*(): Unit {.importc.}

# src/kingdom/entities/item.nim
proc newItem*(): Item {.importc.}

# src/kingdom/entities/tile.nim
proc newTile*(): Tile {.importc.}
proc setAllBorders*(this: Tile, border: string): void {.importc.}

# src/kingdom/entities/ability.nim
proc newAbility*(): Ability {.importc.}

# src/kingdom/entities/stats.nim
proc incStat*(this: Stats, label: string, d: int): void {.importc.}

# src/kingdom/entities/signals.nim
# proc addSignalHandler*[T: Entity](this: T, channel: string, handler: SignalHandler[T]): void {.importc.}

# src/kingdom/world.nim
proc getBounds*(this: World): Coord {.importc.}
proc canUnitTravelAcrossTiles*(this: World, unit: Unit, current: Coord, adj: Coord): bool {.importc.}
proc moveUnit*(this: World, u: Unit, c: Coord): void {.importc.}

# src/kingdom/math/hexagons.nim
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] {.importc.}

# src/kingdom/controls/targeting.nim
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void): void {.importc.}
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void): void {.importc.}
