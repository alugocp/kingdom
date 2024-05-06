
# Enum for types of damage in the game
type DamageType* = enum
    PHYSICAL
    MAGICAL

# Enum for the two types of inventory
type InventoryType* = enum
    EQUIP
    HAUL

# Global data to be used during any given frame
type GameState* = object
    turn*: int

proc newGameState*(turn: int): GameState =
    result.turn = turn