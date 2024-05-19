import kingdom/builtin/values

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

# Collected information about each player in the game
type PlayerData* = object
    id*: int
    numUnits*: int

proc newPlayerData*(id: int): PlayerData =
    result.numUnits = 0
    result.id = id

# Global data to be used during any given frame
type GameState* = object
    players*: seq[PlayerData]
    turn*: int

proc newGameState*(turn: int): GameState =
    result.players = @[
        newPlayerData(HUMAN_PLAYER),
        newPlayerData(AMBIENT_PLAYER)
    ]
    result.turn = turn
