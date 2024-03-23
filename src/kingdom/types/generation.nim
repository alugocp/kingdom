import sugar
import std/tables
import kingdom/types/entities

# Function type for generating an entity
type Generator*[T: Entity] = () -> T

# Function type for modifying the code of a generator
type Modifier*[T: Entity] = (x: T) -> void

# Object containing a base Generator and several optional Modifiers
type FullGenerator*[T: Entity] = object
    base: Generator[T]
    post: seq[Modifier[T]]

# Generic manager type for Entity generation
type GenerationManager*[T: Entity] = object
    generators*: Table[string, FullGenerator[T]]

# Manager for Unit generation
type UnitGenerationManager* = GenerationManager[Unit]

# Manager for Tile generation
type TileGenerationManager* = GenerationManager[Tile]