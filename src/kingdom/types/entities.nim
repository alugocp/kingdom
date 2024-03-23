import kingdom/types/math
import kingdom/types/signals

# Unit type for in-game characters
type Unit* = ref object of RootObj
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Unit]

# Tile type for the in-game map
type Tile* = ref object of RootObj
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Tile]

# Combined entity type
type Entity* = Unit | Tile