import std/tables
import std/strformat
import kingdom/generation/types
import kingdom/entities/types

# Constructor for a FullGenerator type
proc newFullGenerator[T: Entity](base: Generator[T]): FullGenerator[T] =
    new result
    result.base = base
    result.post = newSeq[Modifier[T]]()
    return result

# Adds a new Generator to the given manager
proc addGenerator*[T: Entity](this: GenerationManager[T], key: string, generator: Generator[T]): void = # {.exportc,dynlib.} =
    if this.generators.hasKey(key):
        raise newException(Exception, fmt"Generator '{key}' already exists")
    let full = newFullGenerator[T](generator)
    this.generators[key] = full

# Adds a Modifier to the given Generator
proc addModifier*[T: Entity](this: GenerationManager[T], key: string, modifier: Modifier[T]): void =
    if not this.generators.hasKey(key):
        raise newException(Exception, fmt"Generator '{key}' does not exist")
    this.generator[key].post.add(modifier)

# Request the given manager to generate some Entity
proc generate*[T: Entity](this: GenerationManager[T], key: string): T = # {.exportc, dynlib.} =
    if not this.generators.hasKey(key):
        raise newException(Exception, fmt"Generator '{key}' does not exist")
    let full = this.generators[key]
    var x = full.base()
    for p in full.post:
        p(x)
    return x