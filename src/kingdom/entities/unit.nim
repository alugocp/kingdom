import std/sets
import std/sugar
import std/tables
import std/options
import std/strutils
import std/strformat
import kingdom/wrapper/types
import kingdom/entities/types
import kingdom/entities/ability
import kingdom/entities/signals
import kingdom/entities/stats
import kingdom/entities/item
import kingdom/math/types
import kingdom/controls/types
import kingdom/controls/menu
import kingdom/controls/actions
import kingdom/builtin/signals
import kingdom/builtin/values
import kingdom/builtin/types

# Constructor for the Unit type
proc newUnit*(): Unit {.exportc, dynlib.} =
    new result
    result.id = 1
    result.name = "unnamed"
    result.baseHealth = 50
    result.party = NO_PARTY
    result.desc = none(string)
    result.sprite = NULL_SPRITE
    result.player = HUMAN_PLAYER
    result.pos = initCoord(0, 0)
    result.handlers = initTable[string, seq[SignalHandler[Unit]]]()
    result.abilities = @[]
    result.statuses = @[]
    result.maxItems = 4
    result.items = @[]
    result.maxHaul = 4
    result.haul = @[]
    result.stats = newStats()
    result.tags = initHashSet[string]()
    result.classification = @[UNKNOWN_CLASS]
    result.level = 1
    result.xp = 0

# Returns a label for this Unit to be used in Menus
proc getMenuLabel*(this: Unit): string =
    if this.player == HUMAN_PLAYER:
        return fmt"+ {this.name}"
    return fmt"- {this.name}"

# Returns true if this Unit is in the same Party as the given Unit
proc isFellowPartyMember*(this: Unit, u: Unit): bool = this.party == u.party

# Handle logic when this Unit gains XP points
proc gainXp*(this: Unit, xp: int): void =
    let payload = newGainXpSignalArgs(xp)
    this.handleSignal(@[], payload)
    this.xp += payload.xp
    while this.xp > MAX_XP:
        this.xp -= MAX_XP
        this.level += 1
        let payload = newLevelUpSignalArgs(xp)
        this.handleSignal(@[], payload)

# Returns this Unit's maximum health
proc getMaxHealth*(this: Unit): int =
    let payload = newGetMaxHealthSignalArgs(this.baseHealth)
    this.handleSignal(@[], payload)
    return payload.health

# Return a MenuNode describing this Unit and associated actions
proc getMenuNode*(this: Unit, party: Party, actions: UnitMenuActions): MenuNode =
    let maxHealth = this.getMaxHealth()
    let node = newListNode()
    let stats = this.stats.getStats()
    node.add(newHeaderNode(this.name))
    node.add(newTextNode(this.classification.join("/")))
    node.add(newTextNode(fmt"Lv. {this.level} ({this.xp}/{MAX_XP} xp)"))
    node.add(newTextNode(fmt"Hunger: {actions.getHunger(this)}%"))
    node.add(newTextNode(fmt"Health: {maxHealth - this.damageTaken}/{maxHealth}"))
    for stat in stats:
        node.add(newTextNode(fmt"{stat.value} {stat.label}"))
    if this.desc.isSome:
        node.add(newTextNode(this.desc.get()))
    if this.player == HUMAN_PLAYER:
        node.add(newSpaceNode())
        node.add(newTextNode("This unit is under your control"))
        node.add(newButtonNode("Join Party", () => actions.joinParty(this, party)))
        if party.n > 1:
            node.add(newButtonNode("Leave Party", () => actions.leaveParty(this, party)))
    node.add(newSpaceNode())

    # Menu elements for Abilities
    if this.abilities.len > 0:
        node.add(newHeaderNode("Abilities:"))
        for ability in this.abilities:
            node.add(ability.getMenuNode(this))
            node.add(newSeparatorNode())

    # Menu elements for Statuses
    if this.statuses.len > 0:
        node.add(newHeaderNode("Statuses:"))
        for status in this.statuses:
            node.add(status.getMenuNode(this))
            node.add(newSeparatorNode())

    # Menu elements for Items (equipped inventory)
    if this.maxItems > 0:
        node.add(newHeaderNode("Equipped Items:"))
        let label = if this.items.len == 1: "item" else: "items"
        node.add(newTextNode(fmt"{this.items.len} {label} equipped ({this.maxItems} max)"))
    for item in this.items:
        capture item:
            node.add(item.getMenuNode(
                some((player: this.player, itype: InventoryType.EQUIP)),
                newItemMenuActions(
                    (itype: InventoryType) => actions.equip(itype, item),
                    (itype: InventoryType) => actions.autoEquip(itype, item),
                    () => actions.unequip(InventoryType.EQUIP, item)
                )
            ))
            node.add(newSeparatorNode())

    if this.items.len == 0:
        node.add(newSpaceNode())

    # Menu elements for Items (haul inventory)
    if this.maxHaul > 0:
        node.add(newHeaderNode("Hauled Items:"))
        let label = if this.haul.len == 1: "item" else: "items"
        node.add(newTextNode(fmt"{this.haul.len} {label} hauled ({this.maxHaul} max)"))
    for item in this.haul:
        capture item:
            node.add(item.getMenuNode(
                some((player: this.player, itype: InventoryType.HAUL)),
                newItemMenuActions(
                    (itype: InventoryType) => actions.equip(itype, item),
                    (itype: InventoryType) => actions.autoEquip(itype, item),
                    () => actions.unequip(InventoryType.HAUL, item)
                )
            ))
            node.add(newSeparatorNode())
    return node