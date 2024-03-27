import std/tables
import std/options
import kingdom/types/signals
import kingdom/entities/types

# Constructor for the Ability type
proc newAbility*(): Ability =
    new result
    result.name = "unnamed ability"
    result.desc = some("just some ability")
    result.handlers = initTable[string, seq[SignalHandler[Ability]]]()
    return result