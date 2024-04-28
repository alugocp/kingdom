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
import kingdom/controls/actions
import kingdom/builtin/values

# Constructor for the Unit type
proc newUnit*(): Unit {.exportc, dynlib.} =
    new result
    result.id = 1
    result.name = "unnamed"
    result.party = NO_PARTY
    result.desc = none(string)
    result.sprite = NULL_SPRITE
    result.player = HUMAN_PLAYER
    result.pos = initCoord(0, 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.abilities = @[]
    result.statuses = @[]
    result.items = @[]
    return result

# Returns true if this Unit is in the same Party as the given Unit
proc isFellowPartyMember*(this: Unit, u: Unit): bool = this.party == u.party

# Return a MenuNode describing this Unit and associated actions
proc getMenuNode*(this: Unit, party: Party, actions: UnitMenuActions): MenuNode =
    let node = newListNode()
    node.add(newHeaderNode(this.name))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    if this.player == HUMAN_PLAYER:
        node.add(newTextNode("This unit is under your control"))
        node.add(newButtonNode("Join Party", () => actions.joinParty(this, party)))
        if party.n > 1:
            node.add(newButtonNode("Leave Party", () => actions.leaveParty(this, party)))
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
            node.add(item.getUnitMenuNode(this.player, () => actions.unequip(item1)))
    return node