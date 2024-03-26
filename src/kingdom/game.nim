import std/tables
import std/options
import kingdom/math/hexagons
import kingdom/types/generation
import kingdom/types/entities
import kingdom/controls/mouse
import kingdom/controls/view
import kingdom/world
import kingdom/menu

# Game type used to aggregate relevant data and used in mod init functions
type Game* = ref object
    menu*: Option[Menu]
    unitGeneration*: UnitGenerationManager
    tileGeneration*: TileGenerationManager
    mouse*: MouseState
    world*: World
    view*: View

# Constructor for a Game type
proc newGame*(world: World): Game =
    return Game(
        unitGeneration: GenerationManager[Unit](
            generators: initTable[string, FullGenerator[Unit]]()
        ),
        tileGeneration: GenerationManager[Tile](
            generators: initTable[string, FullGenerator[Tile]]()
        ),
        menu: some(newMenu(0, 0, 200,
            newListNode(cast[seq[MenuNode]](@[
                newTextNode("Hello, world!"),
                newbuttonNode("Click Me", proc () = echo "Thank you!")
            ]))
        )),
        mouse: newMouseState(),
        view: newView(),
        world: world
    )

# Draws all elements of this Game object
proc draw*(this: Game): void =
    this.world.draw(this.view.dx, this.view.dy)
    if this.menu.isSome:
        this.menu.get().draw()

# Check for updated mouse state and see what we have to process
proc consumeMouseUpdates*(this: Game): void =
    if this.mouse.down:
        this.view.scroll(
            this.mouse.pos.x - this.mouse.posprev.x,
            this.mouse.pos.y - this.mouse.posprev.y
        )

    if not this.mouse.down and this.mouse.wasDown:
        if not (this.menu.isSome and this.menu.get().checkClick(this.mouse)):
            let hex = getHexagonCoords(this.view.withOffset(this.mouse.pos))
            if hex.isSome and this.world.contains(hex.get()):
                echo hex.get()
