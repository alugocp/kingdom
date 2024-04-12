import kingdom/generation/manager
import kingdom/controls/keyboard
import kingdom/controls/mouse
import kingdom/wrapper/sprites
import kingdom/entities/types
import kingdom/mods/loader
import kingdom/math/types
import kingdom/world
import kingdom/types
import kingdom/menu
import kingdom/game

# Constructor for the Start type
proc newStart*(): Start =
    new result
    result.keyboard = newKeyboardState()
    result.mouse = newMouseState()
    result.dead = false

# Returns which Screen should be shown in the next frame
proc getNextScreen*(this: Start): Screen =
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
proc draw*(this: Start): void =
    this.menu.draw(this.mouse)

# Check for updated keyboard state and see what we have to process
proc consumeKeyboardUpdates*(this: Start): void = discard

# Check for updated mouse state and see what we have to process
proc consumeMouseUpdates*(this: Start): void =
    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        discard this.menu.checkClick(this.mouse)
