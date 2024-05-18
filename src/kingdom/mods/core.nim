import kingdom/mods/types
import kingdom/views/types
import kingdom/models/types
import kingdom/operators

# Constructor for the ModCoreInterface type
proc newModCoreInterface*(view: View, rules: GameRuleData): ModCoreInterface =
    new result
    result.view = view
    result.rules = rules

# Gets the current View in this ModCoreInterface as a GameView
proc getGameView*(this: ModCoreInterface): GameView {.exportc, dynlib.} =
    if this.view.viewType != ViewType.GAME:
        ERROR("Current View is not a GameView")
    return GameView(this.view)
