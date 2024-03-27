import std/tables
import std/options
import kingdom/entities/types
import kingdom/entities/signals
import kingdom/builtin/channels
import kingdom/builtin/signals
import kingdom/menu

# Constructor for the Ability type
proc newAbility*(): Ability =
    new result
    result.name = "unnamed ability"
    result.desc = none(string)
    result.handlers = initTable[string, seq[SignalHandler[Ability]]]()
    return result

# Return a MenuNode describing this Ability
proc getMenuNode*(this: Ability, host: Unit): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    if this.hasSignalHandler(ABILITY_CLICKED_CHANNEL):
        node.add(newButtonNode("Activate", proc (): void =
            this.handleSignal(@[], newAbilityClickedSignalArgs(host))
        ))
    return node