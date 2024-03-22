import std/strformat

type Counter* = object
    value: int

proc newCounter*(): ref Counter =
    new(result)
    result.value = 0

proc hello*(times: ref Counter): void =
    times.value += 1
    echo &"Hello! {times.value}"