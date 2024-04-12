import std/dynlib
import std/strformat
import kingdom/screens/types

# Type representing a mod's entry function
type ModEntryPoint = proc(game: Game): void {.gcsafe, stdcall.}

# Platform interface function that will inflate a mod archive file
proc inflateMod(modname: cstring): void {.importc: "inflate_mod".}

# Loads a mod and runs its init function
proc loadMod*(game: Game, modname: string): bool =
    # We cannot import anything that uses std/streams until https://github.com/nim-lang/Nim/pull/23163 is merged
    inflateMod(modname)

    # Load the mod object file
    let filepath = fmt"out/mods/{modname}/out/{modname}-linux"
    let lib = loadLib(filepath)
    if lib == nil:
        echo fmt"Error loading mod '{modname}'"
        return false

    # Grab the mod entry point function
    let initKingdomMod = cast[ModEntryPoint](lib.symAddr("initKingdomMod"))
    if initKingdomMod == nil:
        echo fmt"Could not load init function from mod library '{modname}'"
        unloadLib(lib)
        return false

    # Run the init function and return
    game.initKingdomMod()
    return true