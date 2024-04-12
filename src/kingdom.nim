import kingdom/wrapper/init
import kingdom/views/start

# Main entry point function which is exported to the platform interface (C code)
proc initKingdom(): void {.exportc: "init_kingdom", dynlib.} =
    let start = newStartView()
    start.gameLoop()
