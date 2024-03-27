import std/sugar
import std/tables
import kingdom/entities/types

# Function type for generating an entity
type Generator*[T: Entity] = () -> T

# Function type for modifying the code of a generator
type Modifier*[T: Entity] = (x: T) -> void

# Object containing a base Generator and several optional Modifiers
type FullGenerator*[T: Entity] = ref object
    base*: Generator[T]
    post*: seq[Modifier[T]]

# Generic manager type for Entity generation
type GenerationManager*[T: Entity] = ref object
    generators*: Table[string, FullGenerator[T]]

# Managers for each specific Entity
type UnitGenerationManager* = GenerationManager[Unit]
type TileGenerationManager* = GenerationManager[Tile]
type ItemGenerationManager* = GenerationManager[Item]
type AbilityGenerationManager* = GenerationManager[Ability]