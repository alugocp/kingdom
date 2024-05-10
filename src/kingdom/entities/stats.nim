import kingdom/entities/types
import kingdom/entities/signals
import kingdom/builtin/signals

# Constructor for the Stats type
proc newStats*(): Stats =
    new result
    result.n = 0

# Returns true if this object contains the given stat's label
proc hasStat*(this: Unit, label: string): bool {.exportc, dynlib.} =
    let stats = this.stats
    (stats.n > 0 and stats.label1 == label) or
    (stats.n > 1 and stats.label2 == label) or
    (stats.n > 2 and stats.label3 == label)

# Returns the numeric value associated with this stat label
proc getStat*(this: Unit, label: string): int {.exportc, dynlib.} =
    var base = 0
    let stats = this.stats
    if stats.n > 0 and stats.label1 == label:
        base = stats.stat1
    elif stats.n > 1 and stats.label2 == label:
        base = stats.stat2
    elif stats.n > 2 and stats.label3 == label:
        base = stats.stat3
    else:
        return 0
    let payload = newGetStatSignalArgs(this, label, base)
    this.handleSignal(@[], payload)
    payload.stat

# Assigns a numeric value to the given stat label
proc setStat*(this: Unit, label: string, stat: int): void {.exportc, dynlib.} =
    let stats = this.stats
    if stats.n > 0 and stats.label1 == label: stats.stat1 = stat
    elif stats.n > 1 and stats.label2 == label: stats.stat2 = stat
    elif stats.n > 2 and stats.label3 == label: stats.stat3 = stat
    elif stats.n == 0:
        stats.label1 = label
        stats.stat1 = stat
        stats.n += 1
    elif stats.n == 1:
        stats.label2 = label
        stats.stat2 = stat
        stats.n += 1
    elif stats.n == 2:
        stats.label3 = label
        stats.stat3 = stat
        stats.n += 1

# Increments the numeric value associated with a stat label by some delta
proc incStat*(this: Unit, label: string, d: int): void {.exportc, dynlib.} =
    let val = this.getStat(label) + d
    this.setStat(label, max(val, 0))

# Returns all the stat label/value pairs associated with this object
proc getStats*(this: Unit): seq[tuple[label: string, value: int]] =
    let stats = this.stats
    var res: seq[tuple[label: string, value: int]] = @[]
    if stats.n > 0: res.add((label: stats.label1, value: this.getStat(stats.label1)))
    if stats.n > 1: res.add((label: stats.label2, value: this.getStat(stats.label2)))
    if stats.n > 2: res.add((label: stats.label3, value: this.getStat(stats.label3)))
    return res
