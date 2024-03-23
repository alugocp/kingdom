import kingdom/math/types
import kingdom/entities/signals

# Tile type for the in-game map
type Tile* = object
    pos*: Coord
    handlers*: SignalHandlersTable