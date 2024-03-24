import dynlib
import kingdom/types/game

type ModEntryPoint = proc(game: Game): void {.gcsafe, stdcall.}

proc loadMod*(filepath: string, game: Game): bool =
    # TODO unzip the archive at the filepath and find a .so within to actually load
    let lib = loadLib(filepath)
    if lib == nil:
        echo "Error loading library"
        return false

    # Grab the mod entry point function
    let initKingdomMod = cast[ModEntryPoint](lib.symAddr("initKingdomMod"))
    if initKingdomMod == nil:
        echo "Could not load init function from mod library"
        unloadLib(lib)
        return false

    # Run the init function and return
    initKingdomMod(game)
    #unloadLib(lib)
    return true