import std/tables
import std/options
import kingdom/types/signals
import kingdom/entities/types
import kingdom/menu

# Constructor for the Ability type
proc newAbility*(): Ability =
    new result
    result.name = "unnamed ability"
    result.desc = none(string)
    result.handlers = initTable[string, seq[SignalHandler[Ability]]]()
    return result

# Return a MenuNode describing this Ability
proc getMenuNode*(this: Ability): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    return node