import kingdom/generation/types
import kingdom/entities/types
import kingdom/wrapper/types

# Tracks all parallel data on a single Tile
type TileData* = object
    parties*: seq[Party]
    items*: seq[Item]
    tile*: Tile

# World type to contain Tile objects
type World* = ref object
    tiles*: seq[seq[TileData]]
    w*: Natural
    h*: Natural

# Contains the ruleset for this instance of the game client
type GameRuleData* = ref object
    sprites*: SpriteManager
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    itemGeneration*: ItemGenerationManager
    abilityGeneration*: AbilityGenerationManager
    edgeTileSprite*: SpriteHandle