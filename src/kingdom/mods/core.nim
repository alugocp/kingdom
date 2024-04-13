import kingdom/mods/types
import kingdom/views/types
import kingdom/models/types

# Constructor for the ModCoreInterface type
proc newModCoreInterface*(view: View, rules: GameRuleData): ModCoreInterface =
    new result
    result.view = view
    result.rules = rules

# Gets the current View in this ModCoreInterface as a GameView
proc getGameView*(this: ModCoreInterface): GameView {.exportc, dynlib.} =
    if this.view.viewType != ViewType.GAME:
        raise newException(Exception, "Current View is not a GameView")
    return GameView(this.view)
