import std/sugar
import kingdom/entities/types
import kingdom/controls/types
import kingdom/builtin/types

# Unit menu actions
type UnitMenuActions* = object
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void
    equip*: (InventoryType, Item) -> void
    unequip*: (InventoryType, Item) -> void

proc newUnitMenuActions*(
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void,
    equip: (InventoryType, Item) -> void,
    unequip: (InventoryType, Item) -> void
): UnitMenuActions =
    result.leaveParty = leaveParty
    result.joinParty = joinParty
    result.unequip = unequip
    result.equip = equip

# Party menu actions
type PartyMenuActions* = object
    open*: (MenuNode) -> void
    equip*: (InventoryType, Item) -> void
    unequip*: (InventoryType, Item) -> void
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void

proc newPartyMenuActions*(
    open: (MenuNode) -> void,
    equip: (InventoryType, Item) -> void,
    unequip: (InventoryType, Item) -> void,
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void
): PartyMenuActions =
    result.open = open
    result.equip = equip
    result.unequip = unequip
    result.leaveParty = leaveParty
    result.joinParty = joinParty

# World menu actions
type WorldMenuActions* = object
    open*: (MenuNode) -> void
    equip*: (InventoryType, Item) -> void
    unequip*: (InventoryType, Unit, Item) -> void
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void
    initMoveParty*: (Party) -> void

proc newWorldMenuActions*(
    open: (MenuNode) -> void,
    equip: (InventoryType, Item) -> void,
    unequip: (InventoryType, Unit, Item) -> void,
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void,
    initMoveParty: (Party) -> void
): WorldMenuActions =
    result.open = open
    result.equip = equip
    result.unequip = unequip
    result.leaveParty = leaveParty
    result.joinParty = joinParty
    result.initMoveParty = initMoveParty
