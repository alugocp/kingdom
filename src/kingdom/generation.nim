import std/tables
import std/strformat
import kingdom/types/generation
import kingdom/types/entities
import kingdom/entities/unit

proc newFullGenerator[T: Entity](base: Generator[T]): FullGenerator[T] =
    result = FullGenerator[T]()
    result.base = base
    #result.post = newSeq[Modifier[T]]()
    return result

proc realNewFullGenerator[T: Entity](base: Generator[T]): ref FullGenerator[T] =
    new result
    result.base = base
    #result.post = newSeq[Modifier[T]]()
    return result

# Adds a new Generator to the given manager
proc addGenerator*[T: Entity](manager: GenerationManager[T], key: string, generator: Generator[T]): void =
    echo("if")
    if manager.generators.hasKey(key):
        echo("error")
        raise newException(Exception, fmt"Generator '{key}' already exists")
    echo("add")
    discard generator()
    echo("here now")
    let thing = newFullGenerator[T](generator)
    echo("thing")
    #manager.generators = initTable[string, FullGenerator[T]]()
    #manager.generators[key] = thing
    let other = newFullGenerator[T](newUnit)
    echo("other")
    #let r = realNewFullGenerator[T](newUnit)
    echo("r")
    var tab = initTable[string, FullGenerator[T]]()
    echo("middle")
    discard manager.generators.hasKeyOrPut(key, thing)
    #tab["test"] = other
    echo("Here now")

# Adds a Modifier to the given Generator
proc addModifier*[T: Entity](manager: GenerationManager[T], key: string, modifier: Modifier[T]): void =
    if not manager.generators.hasKey(key):
        raise newException(Exception, fmt"Generator '{key}' does not exist")
    #manager.generator[key].post.add(modifier)

# Request the given manager to generate some Entity
proc generate*[T: Entity](manager: GenerationManager[T], key: string): T =
    if not manager.generators.hasKey(key):
        echo("Nope!")
        raise newException(Exception, fmt"Generator '{key}' does not exist")
    echo("Here we go...")
    let full = manager.generators[key]
    var x = full.base()
    #for p in full.post:
    #    p(x)
    return x