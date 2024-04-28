import std/sugar
import kingdom/entities/types
import kingdom/controls/types

# Unit menu actions
type UnitMenuActions* = object
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void
    unequip*: (Item) -> void

proc newUnitMenuActions*(
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void,
    unequip: (Item) -> void
): UnitMenuActions =
    result.leaveParty = leaveParty
    result.joinParty = joinParty
    result.unequip = unequip

# Party menu actions
type PartyMenuActions* = object
    open*: (MenuNode) -> void
    unequip*: (Item) -> void
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void

proc newPartyMenuActions*(
    open: (MenuNode) -> void,
    unequip: (Item) -> void,
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void
): PartyMenuActions =
    result.open = open
    result.unequip = unequip
    result.leaveParty = leaveParty
    result.joinParty = joinParty

# World menu actions
type WorldMenuActions* = object
    open*: (MenuNode) -> void
    equip*: (Item) -> void
    unequip*: (Unit, Item) -> void
    leaveParty*: (Unit, Party) -> void
    joinParty*: (Unit, Party) -> void

proc newWorldMenuActions*(
    open: (MenuNode) -> void,
    equip: (Item) -> void,
    unequip: (Unit, Item) -> void,
    leaveParty: (Unit, Party) -> void,
    joinParty: (Unit, Party) -> void
): WorldMenuActions =
    result.open = open
    result.equip = equip
    result.unequip = unequip
    result.leaveParty = leaveParty
    result.joinParty = joinParty
