import std/sugar
import std/tables
import std/options
import kingdom/entities/types
import kingdom/entities/signals
import kingdom/builtin/signals

# Constructor for the Quest type
proc newQuest*(goal: int, progressLabel: (x: int, n: int) -> string, desc: string, reward: string): Quest {.exportc, dynlib.} =
    new result
    result.handlers = initTable[string, seq[SignalHandler[Tile]]]()
    result.progressLabel = progressLabel
    result.reward = reward
    result.progress = 0
    result.goal = goal
    result.desc = desc

# Returns some string to explain the progress of this Quest
proc getProgressLabel*(this: Quest): string = this.progressLabel(this.progress, this.goal)

# Handle logic whenever the player makes progress on this Quest
proc tickQuest*(this: Tile): void {.exportc, dynlib.} =
    if this.quest.isSome():
        let quest = this.quest.get()
        quest.progress += 1
        if quest.progress == quest.goal:
            this.handleSignal(@[], newQuestCompleteSignalArgs())
