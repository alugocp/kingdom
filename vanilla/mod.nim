import test

proc initKingdomMod(times: ref Counter): void {.exportc,dynlib.} =
    echo("Write your mod here!")
    hello(times)