import std/tables
import std/strformat
import kingdom/generation/types
import kingdom/entities/types
import kingdom/operators

# Constructor for a FullGenerator type
proc newFullGenerator[T: Entity](base: Generator[T]): FullGenerator[T] =
    new result
    result.base = base
    result.post = newSeq[Modifier[T]]()

# Adds a new Generator to the given manager
proc addGenerator*[T: Entity](this: GenerationManager[T], key: string, generator: Generator[T]): void =
    if this.generators.hasKey(key):
        ERROR(fmt"Generator '{key}' already exists")
    let full = newFullGenerator[T](generator)
    this.generators[key] = full

proc addGenerator1*(this: GenerationManager[Ability], key: string, generator: Generator[Ability]): void {.exportc: "addGenerator_ability", dynlib.} =
    addGenerator(this, key, generator)
proc addGenerator1*(this: GenerationManager[Item], key: string, generator: Generator[Item]): void {.exportc: "addGenerator_item", dynlib.} =
    addGenerator(this, key, generator)
proc addGenerator1*(this: GenerationManager[Unit], key: string, generator: Generator[Unit]): void {.exportc: "addGenerator_unit", dynlib.} =
    addGenerator(this, key, generator)
proc addGenerator1*(this: GenerationManager[Tile], key: string, generator: Generator[Tile]): void {.exportc: "addGenerator_tile", dynlib.} =
    addGenerator(this, key, generator)

# Adds a Modifier to the given Generator
proc addModifier*[T: Entity](this: GenerationManager[T], key: string, modifier: Modifier[T]): void =
    if not this.generators.hasKey(key):
        ERROR(fmt"Generator '{key}' does not exist")
    this.generator[key].post.add(modifier)

# Request the given manager to generate some Entity
proc generate*[T: Entity](this: GenerationManager[T], key: string): T =
    if not this.generators.hasKey(key):
        ERROR(fmt"Generator '{key}' does not exist")
    let full = this.generators[key]
    var x = full.base()
    if x.isNil():
        ERROR(fmt"Generator '{key}' returned nil, please add an explicit return statement")
    for p in full.post:
        p(x)
    return x

proc generate1*(this: GenerationManager[Ability], key: string): Ability {.exportc: "generate_ability", dynlib.} = generate(this, key)
proc generate1*(this: GenerationManager[Item], key: string): Item {.exportc: "generate_item", dynlib.} = generate(this, key)
proc generate1*(this: GenerationManager[Unit], key: string): Unit {.exportc: "generate_unit", dynlib.} = generate(this, key)
proc generate1*(this: GenerationManager[Tile], key: string): Tile {.exportc: "generate_tile", dynlib.} = generate(this, key)