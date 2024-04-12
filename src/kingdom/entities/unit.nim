import std/sugar
import std/tables
import std/options
import kingdom/wrapper/types
import kingdom/entities/types
import kingdom/entities/ability
import kingdom/entities/item
import kingdom/math/types
import kingdom/controls/types
import kingdom/controls/menu

# Constructor for the Unit type
proc newUnit*(): Unit {.exportc, dynlib.} =
    new result
    result.id = 1
    result.name = "unnamed"
    result.desc = none(string)
    result.sprite = NULL_SPRITE
    result.pos = initCoord(0, 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.abilities = @[]
    result.statuses = @[]
    result.items = @[]
    return result

# Return a MenuNode describing this Unit and associated actions
proc getMenuNode*(this: Unit, unequip: (Item) -> void): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    if this.abilities.len > 0:
        node.add(newSpaceNode())
        node.add(newTextNode("Abilities:"))
        for ability in this.abilities:
            node.add(ability.getMenuNode(this))
    if this.statuses.len > 0:
        node.add(newSpaceNode())
        node.add(newTextNode("Statuses:"))
        for status in this.statuses:
            node.add(status.getMenuNode(this))
    if this.items.len > 0:
        node.add(newSpaceNode())
        node.add(newTextNode("Items:"))
        for item in this.items:
            let item1 = item
            node.add(item.getUnitMenuNode(() => unequip(item1)))
    return node