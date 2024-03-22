import test

proc kingdom_mod_init(times: ref Counter): void {.exportc,dynlib.} =
    echo "Mod loaded!"
    hello(times)