import std/sugar
import kingdom/generation/types
import kingdom/entities/types
import kingdom/controls/types
import kingdom/wrapper/types
import kingdom/models/types
import kingdom/views/types
import kingdom/math/types
import kingdom/mods/types

# src/kingdom/mods/core.nim
proc getGameView*(this: ModCoreInterface): GameView {.importc.}

# src/kingdom/wrapper/sprites.nim
proc registerSheet*(this: SpriteManager, modname: string, filename: string): SheetHandle {.importc.}
proc getSpriteHandle*(this: SpriteManager, id: SheetHandle, x: uint8, y: uint8, w: uint8 = 24, h: uint8 = 24): SpriteHandle {.importc.}

# src/kingdom/generation/manager.nim
proc addGenerator*(this: GenerationManager[Ability], key: string, generator: Generator[Ability]): void {.importc: "addGenerator_ability"}
proc addGenerator*(this: GenerationManager[Item], key: string, generator: Generator[Item]): void {.importc: "addGenerator_item"}
proc addGenerator*(this: GenerationManager[Unit], key: string, generator: Generator[Unit]): void {.importc: "addGenerator_unit"}
proc addGenerator*(this: GenerationManager[Tile], key: string, generator: Generator[Tile]): void {.importc: "addGenerator_tile"}
proc generate*(this: GenerationManager[Ability], key: string): Ability {.importc: "generate_ability".}
proc generate*(this: GenerationManager[Item], key: string): Item {.importc: "generate_item".}
proc generate*(this: GenerationManager[Unit], key: string): Unit {.importc: "generate_unit".}
proc generate*(this: GenerationManager[Tile], key: string): Tile {.importc: "generate_tile".}

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
proc addSignalHandler*(this: Ability, channel: string, handler: SignalHandler[Ability]): void {.importc: "addSignalHandler_ability"}
proc addSignalHandler*(this: Item, channel: string, handler: SignalHandler[Item]): void {.importc: "addSignalHandler_item"}
proc addSignalHandler*(this: Unit, channel: string, handler: SignalHandler[Unit]): void {.importc: "addSignalHandler_unit"}
proc addSignalHandler*(this: Tile, channel: string, handler: SignalHandler[Tile]): void {.importc: "addSignalHandler_tile"}

# src/kingdom/models/world.nim
proc getBounds*(this: World): Coord {.importc.}
proc canUnitTravelAcrossTiles*(this: World, unit: Unit, current: Coord, adj: Coord): bool {.importc.}
proc moveUnit*(this: World, u: Unit, c: Coord): void {.importc.}

# src/kingdom/math/hexagons.nim
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] {.importc.}

# src/kingdom/controls/targeting.nim
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void): void {.importc: "target_coords".}
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void): void {.importc: "target_units".}
