
proc loadMod(filepath: cstring): int {.importc.}

echo("Hello, world!")
discard loadMod("/home/alexander/Desktop/kingdom/out/vanilla")