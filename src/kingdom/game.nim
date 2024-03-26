import std/tables
import std/options
import std/strformat
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/types/generation
import kingdom/types/entities
import kingdom/controls/mouse
import kingdom/controls/view
import kingdom/generation
import kingdom/world
import kingdom/menu

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
    menu*: Option[Menu]
    nextUnitId: int
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    mouse*: MouseState
    world*: World
    view*: View

# Constructor for a Game type
proc newGame*(world: World): Game =
    return Game(
        nextUnitId: 0,
        unitGeneration: GenerationManager[Unit](
            generators: initTable[string, FullGenerator[Unit]]()
        ),
        tileGeneration: GenerationManager[Tile](
            generators: initTable[string, FullGenerator[Tile]]()
        ),
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

# Draws all elements of this Game object
proc draw*(this: Game): void =
    this.world.draw(this.view.dx, this.view.dy)
    if this.menu.isSome:
        this.menu.get().draw()

# Check for updated mouse state and see what we have to process
proc consumeMouseUpdates*(this: Game): void =
    if this.mouse.down and this.mouse.scrolling:
        this.view.scroll(
            this.mouse.pos.x - this.mouse.posprev.x,
            this.mouse.pos.y - this.mouse.posprev.y
        )

    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        if this.menu.isSome:
            let clicked = this.menu.get().checkClick(this.mouse)
            if not clicked:
                this.closeMenu()
        else:
            let hex = getHexagonCoords(this.view.withOffset(this.mouse.pos))
            if hex.isSome and this.world.contains(hex.get()):
                let coord = hex.get()
                let list = newListNode()
                list.add(newTextNode(fmt"Tile {coord}"))
                let units = this.world.getUnits(coord).len
                if units > 0:
                    list.add(newTextNode(fmt"{units} unit(s)"))
                this.openMenu(newMenu(0, 0, 200, list))
