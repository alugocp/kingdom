import kingdom/types/entities
import kingdom/types/generation

# This file contains function declarations to be used in mod development

# kingdom/generation.nim
proc addGenerator*[T: Entity](manager: GenerationManager[T], key: string, generator: Generator[T]): void {.importc.}
proc addModifier*[T: Entity](manager: GenerationManager[T], key: string, modifier: Modifier[T]): void {.importc.}
proc generate*[T: Entity](manager: GenerationManager[T], key: string): T {.importc.}

# kingdom/entities/unit.nim
proc newUnit*(): Unit {.importc.}