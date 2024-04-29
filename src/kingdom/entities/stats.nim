import kingdom/entities/types

# Constructor for the Stats type
proc newStats*(): Stats =
    new result
    result.n = 0

# Returns true if this object contains the given stat's label
proc hasStat*(this: Stats, label: string): bool =
    (this.n > 0 and this.label1 == label) or
    (this.n > 1 and this.label2 == label) or
    (this.n > 2 and this.label3 == label)

# Returns the numeric value associated with this stat label
proc getStat*(this: Stats, label: string): int =
    if this.n > 0 and this.label1 == label: this.stat1
    elif this.n > 1 and this.label2 == label: this.stat2
    elif this.n > 2 and this.label3 == label: this.stat3
    else: 0

# Assigns a numeric value to the given stat label
proc setStat*(this: Stats, label: string, stat: int): void =
    if this.n > 0 and this.label1 == label: this.stat1 = stat
    elif this.n > 1 and this.label2 == label: this.stat2 = stat
    elif this.n > 2 and this.label3 == label: this.stat3 = stat
    elif this.n == 0:
        this.label1 = label
        this.stat1 = stat
        this.n += 1
    elif this.n == 1:
        this.label2 = label
        this.stat2 = stat
        this.n += 1
    elif this.n == 2:
        this.label3 = label
        this.stat3 = stat
        this.n += 1

# Increments the numeric value associated with a stat label by some delta
proc incStat*(this: Stats, label: string, d: int): void {.exportc, dynlib.} =
    let val = this.getStat(label) + d
    this.setStat(label, if val > 0: val else: 0)
