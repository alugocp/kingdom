
# Type representing a Tile's Quest
type Quest* = ref object
    reward*: string
    desc*: string

# Constructor for the Quest type
proc newQuest*(desc: string, reward: string): Quest =
    new result
    result.reward = reward
    result.desc = desc

# Returns some numerical value representing what still must be done on this Quest
method remainingProgress*(this: Quest): int {.base.} = 1

# Returns some string to explain the progress of this Quest
method progressLabel*(this: Quest): string {.base.} = "This quest can never be completed"

# Returns true if this Quest has been completed
proc isComplete*(this: Quest): bool = this.remainingProgress() == 0
