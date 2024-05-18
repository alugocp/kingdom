import std/sets
import std/sugar
import std/tables
import std/options
import kingdom/math/types
import kingdom/wrapper/types
import kingdom/builtin/values

# STAT TYPES

# Type encompassing up to 3 integer values associated with a Unit
type Stats* = ref object
    label1*: string
    label2*: string
    label3*: string
    stat1*: int
    stat2*: int
    stat3*: int
    n*: int

# SIGNAL TYPES

# Used to help differentiate IDs in SignalContext
type EntityTypes* = enum
    UNIT_TYPE
    TILE_TYPE
    ITEM_TYPE
    ABILITY_TYPE

# Base signal arguments payload
type BaseSignalArgs* = ref object of RootObj
    channel*: string

# Type to help prevent infinite loops in signal processing
type SignalContextElement* = (EntityTypes, int, string)
type SignalContext* = seq[SignalContextElement]

# Signal handler type
type SignalHandler*[T] = (T, SignalContext, BaseSignalArgs) -> void

# Signal handlers collection type
type SignalHandlersTable*[T] = Table[string, seq[SignalHandler[T]]]

# ENTITY TYPES

# Actions or passive properties associated with a Unit
type Ability* = ref object
    id*: int
    name*: string
    desc*: Option[string]
    handlers*: SignalHandlersTable[Ability]
    tags*: HashSet[string]

type Status* = ref object
    effect*: Ability
    frameAdded*: uint
    lifespan*: uint

# Items that can be equipped by a Unit
type Item* = ref object
    id*: int
    name*: string
    desc*: string
    tags*: HashSet[string]
    handlers*: SignalHandlersTable[Item]
    pos*: Option[Coord]

# Unit type for in-game characters
type Unit* = ref object
    id*: int
    pos*: Coord
    party*: int
    player*: int
    sprite*: SpriteHandle
    handlers*: SignalHandlersTable[Unit]
    name*: string
    desc*: Option[string]
    maxItems*: int
    items*: seq[Item]
    maxHaul*: int
    haul*: seq[Item]
    abilities*: seq[Ability]
    statuses*: seq[Status]
    stats*: Stats
    tags*: HashSet[string]
    classification*: seq[string]
    lastTurnFed*: int
    damageTaken*: int
    baseHealth*: int
    level*: int
    gold*: int
    xp*: int

type
    # Type representing a Tile's Quest
    Quest* = ref object
        handlers*: SignalHandlersTable[Tile]
        progressLabel*: (x: int, n: int) -> string
        progress*: int
        goal*: int
        reward*: string
        desc*: string

    # Tile type for the in-game map
    Tile* = ref object
        id*: int
        pos*: Coord
        name*: string
        desc*: Option[string]
        tags*: HashSet[string]
        sprite*: SpriteHandle
        handlers*: SignalHandlersTable[Tile]
        borders*: array[0..5, string]
        quest*: Option[Quest]

# Combined entity type
type Entity* = Unit | Tile | Item | Ability

# Party type
type Party* = ref object
    members*: array[0..(PARTY_LIMIT - 1), Option[Unit]]
    id*: int
    n*: int
