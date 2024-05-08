import kingdom/wrapper/init
{.warning[UnusedImport]: off.}
import kingdom/mods/utils

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    gameLoop()
