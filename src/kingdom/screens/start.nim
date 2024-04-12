import kingdom/generation/manager
import kingdom/controls/keyboard
import kingdom/controls/mouse
import kingdom/wrapper/sprites
import kingdom/wrapper/draw
import kingdom/entities/types
import kingdom/builtin/values
import kingdom/mods/loader
import kingdom/math/types
import kingdom/world
import kingdom/types
import kingdom/controls/menu
import kingdom/screens/game

# Constructor for the Start type
proc newStart*(): Start =
    new result
    result.keyboard = newKeyboardState()
    result.mouse = newMouseState()
    result.dead = false
    let hook = result
    let root = newListNode()
    root.add(newTextNode("Hello, and welcome to my game!"))
    root.add(newButtonNode("Play", proc (): void = hook.dead = true ))
    result.menu = newMenu(0, 0, 500, root)

# Returns which Screen should be shown in the next frame
method getNextScreen*(this: Start): Screen =
    if not this.dead:
        return this
    let world = newWorld(20, 10)
    let game = newGame(world)
    discard game.loadMod("vanilla")
    game.sprites.loadAllSheets()

    # Testing code
    world.build(proc (x: int, y: int): Tile = game.tileGeneration.generate(if x == 1 and y == 0: "Water" else: "Grass"))
    discard game.addNewUnit("Plasmoid Adventurer", initCoord(0, 0))
    discard game.addNewItem("Ring of Strength", initCoord(1, 1))
    return game

# Draws the Menu on this Start object
method draw*(this: Start): void =
    setBackground(WHITE)
    this.menu.draw(this.mouse)

# Check for updated keyboard state and see what we have to process
method consumeKeyboardUpdates*(this: Start): void = discard

# Check for updated mouse state and see what we have to process
method consumeMouseUpdates*(this: Start): void =
    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        discard this.menu.checkClick(this.mouse)
