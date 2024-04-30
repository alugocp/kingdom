import std/sets
import std/sugar
import std/tables
import std/options
import kingdom/math/types
import kingdom/entities/types
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/builtin/values
import kingdom/builtin/types

# Constructor for the Item type
proc newItem*(): Item {.exportc, dynlib.} =
    new result
    result.id = 1
    result.pos = none(Coord)
    result.name = "unnamed item"
    result.desc = "just some item"
    result.tags = initHashSet[string]()
    result.handlers = initTable[string, seq[SignalHandler[Item]]]()

# Return a MenuNode describing this Item when equipped to a Unit
proc getMenuNode*(this: Item, equipData: Option[tuple[player: int, itype: InventoryType]], unequip: () -> void): MenuNode =
    let node = newListNode()
    node.add(newTextNode(this.name, GREEN))
    node.add(newTextNode(this.desc))

    # Controls when this Item is in possession of a Unit
    if equipData.isSome():
        let player = equipData.get().player
        # let itype = equipData.get().itype
        if player != HUMAN_PLAYER:
            return node
        node.add(newButtonNode("Drop", unequip))

    # Controls when this Item is loose in the World
    else:
        discard
    return node
