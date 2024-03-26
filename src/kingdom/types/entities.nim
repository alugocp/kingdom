import kingdom/math/types
import kingdom/types/signals

# Unit type for in-game characters
type Unit* = ref object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Unit]
    name*: string

# Tile type for the in-game map
type Tile* = ref object
    id*: int
    pos*: Coord
    handlers*: SignalHandlersTable[Tile]
    borders*: array[0..5, string]

# Combined entity type
type Entity* = Unit | Tile

# Default Tile border value
const OPEN_BORDER* = "none"