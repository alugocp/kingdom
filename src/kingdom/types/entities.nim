import kingdom/math/types
import kingdom/types/signals

# Unit type for in-game characters
type Unit* = object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Unit]

# Tile type for the in-game map
type Tile* = object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Tile]

# Combined entity type
type Entity* = Unit | Tile