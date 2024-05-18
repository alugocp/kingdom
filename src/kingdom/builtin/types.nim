
# Enum for types of damage in the game
type DamageType* = enum
    PHYSICAL
    MAGICAL

# Enum for the two types of inventory
type InventoryType* = enum
    EQUIP
    HAUL

# Enum describing if a Unit can (CROSS) or cannot (BLOCKED) cross between
# two tiles, or if they allow their entire party to cross (OVERRIDE)
type MovementType* = enum
    OVERRIDE,
    BLOCKED,
    CROSS

# Global data to be used during any given frame
type GameState* = object
    turn*: int

proc newGameState*(turn: int): GameState =
    result.turn = turn