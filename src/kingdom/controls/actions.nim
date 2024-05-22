import std/sugar
import kingdom/entities/types
import kingdom/controls/types
import kingdom/builtin/types

# Item menu actions
type ItemMenuActions* = object
    equip*: (InventoryType) -> void
    autoEquip*: (InventoryType) -> void
    unequip*: () -> void
    consume*: () -> void

proc newItemMenuActions*(
    equip: (InventoryType) -> void,
    autoEquip: (InventoryType) -> void,
    unequip: () -> void,
    consume: () -> void
): ItemMenuActions =
    result.autoEquip = autoEquip
    result.consume = consume
    result.unequip = unequip
    result.equip = equip

# Unit menu actions
type UnitMenuActions* = object
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void
    equip*: (InventoryType, Item) -> void
    autoEquip*: (InventoryType, Item) -> void
    unequip*: (InventoryType, Item) -> void
    getHunger*: (Unit) -> int

proc newUnitMenuActions*(
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void,
    equip: (InventoryType, Item) -> void,
    autoEquip: (InventoryType, Item) -> void,
    unequip: (InventoryType, Item) -> void,
    getHunger: (Unit) -> int
): UnitMenuActions =
    result.getHunger = getHunger
    result.leaveParty = leaveParty
    result.joinParty = joinParty
    result.autoEquip = autoEquip
    result.unequip = unequip
    result.equip = equip

# World menu actions
type WorldMenuActions* = object
    open*: (MenuNode) -> void
    equip*: (InventoryType, Item) -> void
    unequip*: (InventoryType, Unit, Item) -> void
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void
    initMoveParty*: (Party) -> void
    getHunger*: (Unit) -> int
    canUnitAct*: (Unit) -> bool

proc newWorldMenuActions*(
    open: (MenuNode) -> void,
    equip: (InventoryType, Item) -> void,
    unequip: (InventoryType, Unit, Item) -> void,
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void,
    initMoveParty: (Party) -> void,
    getHunger: (Unit) -> int,
    canUnitAct: (Unit) -> bool
): WorldMenuActions =
    result.open = open
    result.equip = equip
    result.unequip = unequip
    result.getHunger = getHunger
    result.leaveParty = leaveParty
    result.joinParty = joinParty
    result.initMoveParty = initMoveParty
    result.canUnitAct = canUnitAct
