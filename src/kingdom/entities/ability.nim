import std/tables
import std/options
import kingdom/entities/types
import kingdom/entities/signals
import kingdom/builtin/channels
import kingdom/builtin/signals
import kingdom/types
import kingdom/controls/menu

# Constructor for the Ability type
proc newAbility*(): Ability {.exportc, dynlib.} =
    new result
    result.name = "unnamed ability"
    result.desc = none(string)
    result.handlers = initTable[string, seq[SignalHandler[Ability]]]()
    return result

# Return a MenuNode describing this Ability
proc getMenuNode*(this: Ability, host: Unit): MenuNode =
    let node = newListNode()
    if this.hasSignalHandler(ABILITY_CLICKED_CHANNEL):
        node.add(newButtonNode(this.name, proc (): void =
            this.handleSignal(@[], newAbilityClickedSignalArgs(host))
        ))
    else:
        node.add(newTextNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    return node