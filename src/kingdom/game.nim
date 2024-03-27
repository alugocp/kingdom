import std/tables
import std/options
import std/strformat
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/generation/manager
import kingdom/generation/types
import kingdom/entities/types
import kingdom/controls/mouse
import kingdom/controls/view
import kingdom/world
import kingdom/menu

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
    menu*: Option[Menu]
    nextAbilityId: int
    nextUnitId: int
    nextItemId: int
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    itemGeneration*: ItemGenerationManager
    abilityGeneration*: AbilityGenerationManager
    hoveredHex: Option[Coord]
    mouse*: MouseState
    world*: World
    view*: View

# Constructor for a Game type
proc newGame*(world: World): Game =
    return Game(
        nextAbilityId: 0,
        nextUnitId: 0,
        nextItemId: 0,
        unitGeneration: GenerationManager[Unit](
            generators: initTable[string, FullGenerator[Unit]]()
        ),
        tileGeneration: GenerationManager[Tile](
            generators: initTable[string, FullGenerator[Tile]]()
        ),
        itemGeneration: GenerationManager[Item](
            generators: initTable[string, FullGenerator[Item]]()
        ),
        abilityGeneration: GenerationManager[Ability](
            generators: initTable[string, FullGenerator[Ability]]()
        ),
        hoveredHex: none(Coord),
        menu: none(Menu),
        mouse: newMouseState(),
        view: newView(),
        world: world
    )

# Initializes a new Unit instance and puts it in the World
proc addNewUnit*(this: Game, key: string, pos: Coord): Unit =
    let u = this.unitGeneration.generate(key)
    u.pos = pos
    u.id = this.nextUnitId
    this.world.moveUnit(u, pos)
    this.nextUnitId += 1

# Close the currently open Menu in this Game
proc closeMenu*(this: Game): void =
    this.menu = none(Menu)

# Open a Menu in this Game
proc openMenu*(this: Game, menu: Menu): void =
    this.menu = some(menu)

# Open a Menu in this Game (with default options)
proc openMenu*(this: Game, root: MenuNode): void =
    this.menu = some(newMenu(0, 0, 200, root))

# Draws all elements of this Game object
proc draw*(this: Game): void =
    this.world.draw(this.hoveredHex, this.view.dx, this.view.dy)
    if this.menu.isSome:
        this.menu.get().draw()

# Check for updated mouse state and see what we have to process
proc consumeMouseUpdates*(this: Game): void =
    if this.mouse.down and this.mouse.scrolling:
        this.view.scroll(
            this.mouse.pos.x - this.mouse.posprev.x,
            this.mouse.pos.y - this.mouse.posprev.y
        )

    # check if the user is hovering over a hexagonal Tile
    let hex = getHexagonCoords(this.view.withOffset(this.mouse.pos))
    this.hoveredHex = hex

    # Process a click event
    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        if this.menu.isSome:
            let clicked = this.menu.get().checkClick(this.mouse)
            if not clicked:
                this.closeMenu()
        if hex.isSome and this.world.contains(hex.get()):
            let node = this.world.getMenuNode(hex.get(), proc (n: MenuNode) = this.openMenu(n))
            this.openMenu(node)
