import dynlib
import test

type ModEntryPoint = proc(times: ref Counter): void {.gcsafe, stdcall.}

proc loadMod*(filepath: string, times: ref Counter): bool =
    let lib = loadLib(filepath)
    if lib == nil:
        echo "Error loading library"
        return false

    let initKingdomMod = cast[ModEntryPoint](lib.symAddr("initKingdomMod"))

    if initKingdomMod == nil:
        echo "Could not load init function from mod library"
        unloadLib(lib)
        return false

    initKingdomMod(times)
    unloadLib(lib)
    return true