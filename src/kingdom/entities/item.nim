import std/sets
import std/sugar
import std/tables
import std/options
import kingdom/math/types
import kingdom/entities/types
import kingdom/controls/actions
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
proc getMenuNode*(this: Item, equipData: Option[tuple[player: int, itype: InventoryType]], actions: ItemMenuActions): MenuNode =
    let node = newListNode()
    node.add(newTextNode(this.name, GREEN))
    node.add(newTextNode(this.desc))

    # Controls when this Item is in possession of a Unit
    if equipData.isSome():
        let player = equipData.get().player
        let itype = equipData.get().itype
        if player != HUMAN_PLAYER:
            return node
        if itype == InventoryType.EQUIP:
            node.add(newButtonNode("Move to haul inventory", proc (): void =
                actions.unequip()
                actions.autoEquip(InventoryType.HAUL)
            ))
        else:
            node.add(newButtonNode("Move to equip inventory", proc (): void =
                actions.unequip()
                actions.autoEquip(InventoryType.EQUIP)
            ))
        node.add(newButtonNode("Drop", actions.unequip))

    # Controls when this Item is loose in the World
    else:
        node.add(newButtonNode("Equip", () => actions.equip(InventoryType.EQUIP)))
        node.add(newButtonNode("Haul", () => actions.equip(InventoryType.HAUL)))
    return node
