import std/sugar
import std/tables
import std/options
import kingdom/entities/types
import kingdom/math/types
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/builtin/values

# Constructor for the Item type
proc newItem*(): Item {.exportc, dynlib.} =
    new result
    result.id = 1
    result.pos = none(Coord)
    result.name = "unnamed item"
    result.desc = "just some item"
    result.handlers = initTable[string, seq[SignalHandler[Item]]]()
    return result

# Return a MenuNode describing this Item when equipped to a Unit
proc getUnitMenuNode*(this: Item, player: int, unequip: () -> void): MenuNode =
    let node = newListNode()
    node.add(newTextNode(this.name))
    node.add(newTextNode(this.desc))
    if player == HUMAN_PLAYER:
        node.add(newButtonNode("Unequip", unequip))
    return node

# Return a MenuNode describing this Item when it has no host Unit
proc getFreeMenuNode*(this: Item, equip: () -> void): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    node.add(newTextNode(this.desc))
    node.add(newButtonNode("Equip", equip))
    return node