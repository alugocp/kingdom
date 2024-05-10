import std/options
import kingdom/generation/manager
import kingdom/controls/keyboard
import kingdom/controls/mouse
import kingdom/controls/types
import kingdom/wrapper/draw
import kingdom/entities/types
import kingdom/builtin/values
import kingdom/math/types
import kingdom/models/world
import kingdom/models/types
import kingdom/controls/menu
import kingdom/views/types
import kingdom/views/game
import kingdom/quest

# Constructor for the StartView type
proc newStartView*(rules: GameRuleData): StartView =
    new result
    result.viewType = ViewType.START
    result.keyboard = newKeyboardState()
    result.mouse = newMouseState()
    result.rules = rules
    result.dead = false
    let hook = result
    let root = newListNode()
    root.add(newTextNode("Hello, and welcome to my game!"))
    root.add(newButtonNode("Play", proc (): void = hook.dead = true ))
    result.menu = newMenu(0, 0, 500, root)

# Returns which View should be shown in the next frame
method getNextView*(this: StartView): View =
    if not this.dead:
        return this
    let world = newWorld(20, 10)
    let game = newGameView(this.rules, world)

    # Testing code
    world.build(proc (x: int, y: int): Tile =
        var label = "Grass"
        if y <= 3:
            if x > 8: label = "Forest"
        else:
            if x <= 6: label = "Water"
            elif y > 5: label = "Desert"
        if (y == 3 and x < 6) or (x == 6 and y > 3):
            label = "Coast"
        if ((x == 2 or x == 1) and abs(y - 7) <= 1) or (y == 7 and x == 0):
            label = "Coast"
        if x == 1 and y == 7:
            label = "Island Fortress"
        if x <= 18 and y <= 8 and x > 13 and y > 6:
            label = "Cactus"
        if x == 6 and y == 1:
            label = "Warlock Tower"
        game.rules.tileGeneration.generate(label)
    )
    discard game.addNewUnit("Gloop the Adventurer", initCoord(0, 0), HUMAN_PLAYER)
    discard game.addNewUnit("Barnaclehead", initCoord(1, 1), HUMAN_PLAYER)
    discard game.addNewUnit("Druid", initCoord(0, 3), HUMAN_PLAYER)
    discard game.addNewUnit("Hoka and Tatanka", initCoord(1, 3), HUMAN_PLAYER)
    discard game.addNewUnit("Henrietta", initCoord(2, 3), HUMAN_PLAYER)
    discard game.addNewUnit("Fernando of the Unfaltering Gaze", initCoord(2, 1), world.createNewPlayer())
    world.getTile(initCoord(0, 1)).quest = some(newQuest("Just a test quest...heehee, that rhymes", "Idk pry some rubies or something"))
    return game

# Draws the Menu on this StartView object
method draw*(this: StartView): void =
    setBackground(MENU_BG)
    this.menu.draw(this.mouse)

# Check for updated keyboard state and see what we have to process
method consumeKeyboardUpdates*(this: StartView): void = discard

# Check for updated mouse state and see what we have to process
method consumeMouseUpdates*(this: StartView): void =
    if not this.mouse.down and this.mouse.wasDown and not this.mouse.wasScrolling:
        discard this.menu.checkClick(this.mouse)
