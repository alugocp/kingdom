import std/options
import kingdom/math/types
import kingdom/types/signals

# Actions or passive properties associated with a Unit
type Ability* = ref object
    id*: int
    name*: string
    desc*: Option[string]
    handlers*: SignalHandlersTable[Ability]

# Items that can be equipped by a Unit
type Item* = ref object
    id*: int
    name*: string
    desc*: string
    handlers*: SignalHandlersTable[Item]

# Unit type for in-game characters
type Unit* = ref object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Unit]
    name*: string
    desc*: Option[string]
    items*: seq[Item]
    abilities*: seq[Ability]

# Tile type for the in-game map
type Tile* = ref object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Tile]
    borders*: array[0..5, string]

# Combined entity type
type Entity* = Unit | Tile | Item | Ability