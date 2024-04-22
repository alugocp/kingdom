import std/sugar
import std/options
import std/strformat
import kingdom/entities/types
import kingdom/entities/unit
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/builtin/values

# Returns true if this party is not yet full
proc isPartyFull*(this: Party): bool =
    this.n == PARTY_LIMIT

# Returns all the Units in this Party
proc getMembers*(this: Party): seq[Unit] =
    var members = newSeq[Unit]()
    for a in 0..(this.n - 1):
        members.add(this.members[a].get())
    return members

# Adds some unit to the Party
proc addToParty*(this: Party, u: Unit): void =
    if this.isPartyFull():
        raise newException(Exception, "Party is already full")
    this.members[this.n] = some(u)
    u.party = some(this.id)
    this.n += 1

# Removes some unit from the Party
proc removeFromParty*(this: Party, u: Unit): void =
    for a in 0..(this.n - 1):
        if this.members[a].get() == u:
            u.party = none[int]()
            this.n -= 1
            if a < this.n:
                for b in (a + 1)..this.n:
                    this.members[b - 1] = this.members[b]
            this.members[this.n] = none(Unit)
            return
    raise newException(Exception, fmt"Could not remove missing unit {u.name} from the party")

# Returns a MenuNode for viewing this Party's data
proc getMenuNode*(this: Party, open: (MenuNode) -> void, unequip: (Item) -> void): MenuNode =
    let node = newListNode()
    for a in 0..(this.n - 1):
        let u = this.members[a].get()
        node.add(newButtonNode(u.name, () => open(u.getMenuNode(unequip))))
    return node
