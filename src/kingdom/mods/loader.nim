import std/dynlib
import std/strformat
import kingdom/mods/types
import kingdom/operators

# Type representing a mod's entry function
type ModEntryPoint = proc(game: ModCoreInterface): void {.gcsafe, stdcall.}

# Platform interface function that will inflate a mod archive file
proc inflateMod(modname: cstring): void {.importc: "inflate_mod".}

# Loads a mod and runs its init function
proc loadMod*(game: ModCoreInterface, modname: string): void =
    # We cannot import anything that uses std/streams until https://github.com/nim-lang/Nim/pull/23163 is merged
    inflateMod(modname)

    # Load the mod object file
    let filepath = fmt"out/mods/{modname}/out/{modname}-linux"
    let lib = loadLib(filepath)
    if lib == nil:
        ERROR(fmt"Error loading mod '{modname}', check that it exists and that the headers.nim file is up-to-date")

    # Grab the mod entry point function
    let initKingdomMod = cast[ModEntryPoint](lib.symAddr("initKingdomMod"))
    if initKingdomMod == nil:
        ERROR(fmt"Function 'initKingdomMod' is missing from mod library '{modname}'")

    # Run the init function and return
    game.initKingdomMod()
