import test

proc loadMod(filepath: cstring, times: ref Counter): int {.importc.}

echo("Hello, world!")
var times = newCounter()
hello(times)
hello(times)
discard loadMod("/home/alexander/Desktop/kingdom/out/vanilla", times)