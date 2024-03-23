import modloader
import test

proc initKingdom(): void {.exportc: "init_kingdom",dynlib.} =
    echo("Hello, world!")
    let times = newCounter()
    hello(times)
    hello(times)
    discard loadMod("/home/alexander/Desktop/kingdom/out/vanilla", times)