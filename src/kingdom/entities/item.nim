import std/sets
import std/sugar
import std/tables
import std/options
import kingdom/math/types
import kingdom/entities/types
import kingdom/entities/signals
import kingdom/controls/actions
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/builtin/channels
import kingdom/builtin/signals
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

# Adds a tag to this Item
proc addTag*(this: Item, tag: string): void {.exportc: "addTag_item", dynlib.} =
    this.tags.incl(tag)

# Return a MenuNode describing this Item when equipped to a Unit
proc getMenuNode*(this: Item, equipData: Option[tuple[host: Unit, itype: InventoryType]], actions: ItemMenuActions): MenuNode =
    let node = newListNode()
    node.add(newTextNode(this.name, GREEN))
    node.add(newTextNode(this.desc))

    # Controls when this Item is in possession of a Unit
    if equipData.isSome():
        let edata = equipData.get()
        let player = edata.host.player
        let itype = edata.itype
        if player != HUMAN_PLAYER:
            return node
        if itype == InventoryType.EQUIP:
            if player == HUMAN_PLAYER and this.hasSignalHandler(ITEM_ACTIVATED_CHANNEL):
                node.add(newButtonNode("Activate", proc (): void =
                    this.handleSignal(@[], newItemActivatedSignalArgs(edata.host))
                ))
            node.add(newButtonNode("Move to haul inventory", proc (): void =
                actions.unequip()
                actions.autoEquip(InventoryType.HAUL)
            ))
        else:
            if this.hasSignalHandler(ITEM_CONSUMED_CHANNEL):
                node.add(newButtonNode("Use Item", actions.consume))
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
