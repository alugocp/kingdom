import std/tables
import std/strformat
import kingdom/types/generation
import kingdom/types/entities

# Adds a new Generator to the given manager
proc addGenerator*[T: Entity](manager: GenerationManager[T], key: string, generator: Generator[T]): void =
    if manager.generators.hasKey(key):
        raise newException(fmt"Generator \"{key}\" already exists")
    manager.generators[key] = FullGenerator(base: generator, post: @[])

# Adds a Modifier to the given Generator
proc addModifier*[T: Entity](manager: GenerationManager[T], key: string, modifier: Modifier[T]): void =
    if not manager.generators.hasKey(key):
        raise newException(fmt"Generator \"{key}\" does not exist")
    manager.generator[key].post.add(modifier)

# Request the given manager to generate some Entity
proc generate*[T: Entity](manager: GenerationManager[T], key: string): T =
    if not manager.generators.hasKey(key):
        raise newException(fmt"Generator \"{key}\" does not exist")
    let full = manager.generators[key]
    var x = full.base()
    for p in full.post:
        p(x)
    return x