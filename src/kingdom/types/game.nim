import kingdom/types/generation

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object of RootObj
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager