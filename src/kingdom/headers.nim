import std/sugar
# import kingdom/generation/types
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

# src/kingdom/entities/unit.nim
proc newUnit*(): Unit {.importc.}

# proc generate*[T: Entity](this: GenerationManager[T], key: string): T {.importc.}
proc newAbility*(): Ability {.importc.}
# proc addSignalHandler*[T: Entity](this: T, channel: string, handler: SignalHandler[T]): void {.importc.}
proc getBounds*(this: World): Coord {.importc.}
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] {.importc.}
proc canUnitTravelAcrossTiles*(this: World, unit: Unit, current: Coord, adj: Coord): bool {.importc.}
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void): void {.importc.}
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void): void {.importc.}
proc moveUnit*(this: World, u: Unit, c: Coord): void {.importc.}
proc newItem*(): Item {.importc.}
proc incStat*(this: Stats, label: string, d: int): void {.importc.}
proc newTile*(): Tile {.importc.}
proc setAllBorders*(this: Tile, border: string): void {.importc.}