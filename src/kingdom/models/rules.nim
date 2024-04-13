import std/tables
import kingdom/generation/types
import kingdom/wrapper/sprites
import kingdom/wrapper/types
import kingdom/entities/types
import kingdom/models/types

# Contructor for the GameRuleData type
proc newGameRuleData*(): GameRuleData =
    new result
    result.sprites = newSpriteManager()
    result.edgeTileSprite = NULL_SPRITE
    result.unitGeneration = GenerationManager[Unit](
        generators: initTable[string, FullGenerator[Unit]]()
    )
    result.tileGeneration = GenerationManager[Tile](
        generators: initTable[string, FullGenerator[Tile]]()
    )
    result.itemGeneration = GenerationManager[Item](
        generators: initTable[string, FullGenerator[Item]]()
    )
    result.abilityGeneration = GenerationManager[Ability](
        generators: initTable[string, FullGenerator[Ability]]()
    )