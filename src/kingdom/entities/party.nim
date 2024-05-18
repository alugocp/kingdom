import std/options
import std/strformat
import kingdom/math/types
import kingdom/entities/signals
import kingdom/entities/types
import kingdom/builtin/signals
import kingdom/builtin/values
import kingdom/operators

# Needed for the constructor
proc addToParty*(this: Party, u: Unit): void

# Constructor for the Party type
proc newParty*(id: int, u: Unit): Party =
    new result
    result.n = 0
    result.id = id
    result.addToParty(u)

# Returns true if this party is not yet full
proc isPartyFull*(this: Party): bool =
    this.n == PARTY_LIMIT

# Returns all the Units in this Party
proc getMembers*(this: Party): seq[Unit] =
    if this.n == 0:
        ERROR("Illegal member access on empty party")
    var members = newSeq[Unit]()
    for a in 0..(this.n - 1):
        members.add(this.members[a].get())
    return members

# Returns the position of this Party
proc getCoord*(this: Party): Coord = this.getMembers()[0].pos

# Returns the minimum movement value of any Unit in this Party (max amount the entire Party can move)
proc getMaxMovement*(this: Party): int =
    var max: Option[Natural] = none(Natural)
    for u in this.getMembers():
        let payload = newGetMovementSignalArgs(u)
        u.handleSignal(@[], payload)
        if max.isNone() or max.get() > payload.movement:
            max = some(payload.movement)
    return if max.isSome(): max.get() else: 1

# Adds some unit to the Party
proc addToParty*(this: Party, u: Unit): void =
    if this.isPartyFull():
        Error("Party is already full")
    this.members[this.n] = some(u)
    u.party = this.id
    this.n += 1

# Removes some unit from the Party and returns true if this party is empty
proc removeFromParty*(this: Party, u: Unit): bool =
    for a in 0..(this.n - 1):
        if this.members[a].get() == u:
            u.party = NO_PARTY
            this.n -= 1
            if a < this.n:
                for b in (a + 1)..this.n:
                    this.members[b - 1] = this.members[b]
            this.members[this.n] = none(Unit)
            return this.n == 0
    Error(fmt"Could not remove missing unit {u.name} from the party")

# Moves a Unit from this Party to another and returns true if this party is empty
proc giveToAnotherParty*(this: Party, other: Party, u: Unit): bool =
    if this == other:
        return
    let empty = this.removeFromParty(u)
    other.addToParty(u)
    return empty

# Returns the player ID for the owner of this party
proc getPlayerId*(this: Party): int = this.members[0].get().player
