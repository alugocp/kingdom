import std/sugar
import kingdom/generation/types
import kingdom/entities/types
import kingdom/controls/types
import kingdom/wrapper/types
import kingdom/builtin/types
import kingdom/models/types
import kingdom/views/types
import kingdom/math/types
import kingdom/mods/types

# src/kingdom/mods/core.nim
proc getGameView*(this: ModCoreInterface): GameView {.importc.}

# src/kingdom/mods/utils.nim
proc getUnitSprite*(game: ModCoreInterface, sheet: SheetHandle, ix: uint16, iy: uint16): SpriteHandle {.importc.}
proc harvest*(game: ModCoreInterface, args: BaseSignalArgs, tileType: string, item: string): void {.importc.}
proc attack*(game: ModCoreInterface, args: BaseSignalArgs, dtype: DamageType, dmg: int): void {.importc.}
proc modifyUserStat*(game: ModCoreInterface, item: Item, label: string, value: int): void {.importc.}
proc giveAbility*(game: ModCoreInterface, unit: Unit, ability: string): void {.importc.}
proc dropLoot*(game: ModCoreInterface, unit: Unit, items: seq[string]): void {.importc.}
proc createGoldItem*(game: ModCoreInterface, name: string, quantity: int): Item {.importc.}
proc createFoodItem*(game: ModCoreInterface, name: string): Item {.importc.}
proc addArmor*(u: Unit, dtype: DamageType, dmg: int): void {.importc.}

# src/kingdom/views/game.nim
proc addNewItem*(this: GameView, key: string, pos: Coord): Item {.importc.}

# src/kingdom/wrapper/sprites.nim
proc registerSheet*(this: SpriteManager, modname: string, filename: string): SheetHandle {.importc.}
proc getSpriteHandle*(this: SpriteManager, id: SheetHandle, x: uint16, y: uint16, w: uint8 = 24, h: uint8 = 24): SpriteHandle {.importc.}

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
proc addStatus*(this: Unit, lifespan: uint, ability: Ability): void {.importc.}
proc dealDamage*(this: Unit, u: Unit, dtype: DamageType, dmg: int): void {.importc.}
proc feed*(this: Unit, state: GameState): void {.importc.}
proc heal*(this: Unit, healer: Unit, health: int): void {.importc.}

# src/kingdom/entities/item.nim
proc newItem*(): Item {.importc.}

# src/kingdom/entities/tile.nim
proc newTile*(name: string): Tile {.importc.}
proc setAllBorders*(this: Tile, border: string): void {.importc.}

# src/kingdom/entities/ability.nim
proc newAbility*(): Ability {.importc.}

# src/kingdom/entities/stats.nim
proc setStat*(this: Unit, label: string, stat: int): void {.importc.}
proc incStat*(this: Unit, label: string, d: int): void {.importc.}
proc hasStat*(this: Unit, label: string): bool {.importc.}
proc getStat*(this: Unit, label: string): int {.importc.}

# src/kingdom/entities/signals.nim
proc addSignalHandler*(this: Ability, channel: string, handler: SignalHandler[Ability]): void {.importc: "addSignalHandler_ability"}
proc addSignalHandler*(this: Item, channel: string, handler: SignalHandler[Item]): void {.importc: "addSignalHandler_item"}
proc addSignalHandler*(this: Unit, channel: string, handler: SignalHandler[Unit]): void {.importc: "addSignalHandler_unit"}
proc addSignalHandler*(this: Tile, channel: string, handler: SignalHandler[Tile]): void {.importc: "addSignalHandler_tile"}
proc handleSignal*(this: Ability, ctx: SignalContext, args: BaseSignalArgs): void {.importc: "handleSignal_ability".}
proc handleSignal*(this: Item, ctx: SignalContext, args: BaseSignalArgs): void {.importc: "handleSignal_item".}
proc handleSignal*(this: Tile, ctx: SignalContext, args: BaseSignalArgs): void {.importc: "handleSignal_tile".}
proc handleSignal*(this: Unit, ctx: SignalContext, args: BaseSignalArgs): void {.importc: "handleSignal_unit".}

# src/kingdom/models/world.nim
proc getBounds*(this: World): Coord {.importc.}
proc canUnitTravelAcrossTiles*(this: World, unit: Unit, current: Coord, adj: Coord): bool {.importc.}
proc moveParty*(this: World, p: Party, c: Coord): void {.importc.}
proc getParty*(this: World, u: Unit): Party {.importc.}
proc getUnits*(this: World, c: Coord): seq[Unit] {.importc.}
proc getAllies*(this: World, u: Unit): seq[Unit] {.importc.}
proc getEnemies*(this: World, u: Unit): seq[Unit] {.importc.}
proc getTile*(this: World, c: Coord): Tile {.importc.}

# src/kingdom/math/hexagons.nim
proc getAdjacentHexagonCoords*(c: Coord, bounds: Coord): seq[Coord] {.importc.}

# src/kingdom/controls/targeting.nim
proc target*(this: Targeter, coords: seq[Coord], coordHandler: (c: Coord) -> void): void {.importc: "target_coords".}
proc target*(this: Targeter, units: seq[Unit], unitHandler: (c: Unit) -> void): void {.importc: "target_units".}
