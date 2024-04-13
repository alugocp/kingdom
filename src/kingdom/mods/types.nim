import kingdom/views/types
import kingdom/models/types

# Global game data type passed into mod init functions
type ModCoreInterface* = ref object
    rules*: GameRuleData
    view*: View