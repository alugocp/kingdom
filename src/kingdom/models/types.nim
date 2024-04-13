import kingdom/generation/types
import kingdom/entities/types
import kingdom/wrapper/types

# World type to contain Tile objects
type World* = ref object
    units*: seq[seq[seq[Unit]]]
    items*: seq[seq[seq[Item]]]
    tiles*: seq[seq[Tile]]
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