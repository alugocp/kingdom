import dynlib
import test

type KingdomModInit = proc(times: ref Counter): void {.gcsafe, stdcall.}

proc loadMod(filepath: string, times: ref Counter): bool =
    let lib = loadLib(filepath)
    if lib == nil:
        echo "Error loading library"
        return false

    let kingdomModInit = cast[KingdomModInit](lib.symAddr("kingdom_mod_init"))

    if kingdomModInit == nil:
        echo "Could not load init function from mod library"
        unloadLib(lib)
        return false

    kingdomModInit(times)
    unloadLib(lib)
    return true

proc init_kingdom(): void {.exportc,dynlib.} =
    echo("Hello, world!")
    let times = newCounter()
    hello(times)
    hello(times)
    discard loadMod("/home/alexander/Desktop/kingdom/out/vanilla", times)