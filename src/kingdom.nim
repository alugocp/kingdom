import kingdom/wrapper/init
import kingdom/start

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    let start = newStart()
    start.gameLoop()
