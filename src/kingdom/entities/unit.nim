import std/tables
import std/options
import kingdom/entities/types
import kingdom/entities/ability
import kingdom/entities/item
import kingdom/math/types
import kingdom/menu

# Constructor for the Unit type
proc newUnit*(): Unit =
    new result
    result.id = 1
    result.name = "unnamed"
    result.desc = none(string)
    result.pos = initCoord(0, 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.abilities = @[]
    result.statuses = @[]
    result.items = @[]
    return result

# Return a MenuNode describing this Unit and associated actions
proc getMenuNode*(this: Unit): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    if this.abilities.len > 0:
        node.add(newHeaderNode("Abilities:"))
        for ability in this.abilities:
            node.add(ability.getMenuNode(this))
    if this.statuses.len > 0:
        node.add(newHeaderNode("Statuses:"))
        for status in this.statuses:
            node.add(status.getMenuNode(this))
    if this.items.len > 0:
        node.add(newHeaderNode("Items:"))
        for item in this.items:
            node.add(item.getMenuNode())
    return node