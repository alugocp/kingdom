import std/sets
import std/tables
import std/options
import kingdom/entities/types
import kingdom/entities/signals
import kingdom/builtin/channels
import kingdom/builtin/signals
import kingdom/builtin/values
import kingdom/controls/types
import kingdom/controls/menu

# Constructor for the Ability type
proc newAbility*(): Ability {.exportc, dynlib.} =
    new result
    result.name = "unnamed ability"
    result.desc = none(string)
    result.tags = initHashSet[string]()
    result.handlers = initTable[string, seq[SignalHandler[Ability]]]()

# Adds a tag to this Ability
proc addTag*(this: Ability, tag: string): void {.exportc: "addTag_ability", dynlib.} =
    this.tags.incl(tag)

# Returns true if this Ability has the given tag
proc hasTag*(this: Ability, tag: string): bool {.exportc: "hasTag_ability", dynlib.} =
    this.tags.contains(tag)

# Removes a tag from this Ability
proc dropTag*(this: Ability, tag: string): void {.exportc: "dropTag_ability", dynlib.} =
    this.tags.excl(tag)

# Return a MenuNode describing this Ability
proc getMenuNode*(this: Ability, host: Unit, hostCanAct: bool): MenuNode =
    let node = newListNode()
    if hostCanAct and host.player == HUMAN_PLAYER and this.hasSignalHandler(ABILITY_CLICKED_CHANNEL):
        node.add(newButtonNode(this.name, proc (): void =
            this.handleSignal(@[], newAbilityClickedSignalArgs(host))
        ))
    else:
        node.add(newTextNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    return node