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
import kingdom/mods/utils

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
    result.menu = newMenu(0, 0, 500, false, root)

# Returns which View should be shown in the next frame
method getNextView*(this: StartView): View =
    if not this.dead:
        return this
    let world = newWorld(14, 10)
    let game = newGameView(this.rules, world)

    # Testing code
    world.build(proc (x: int, y: int): Tile =
        var label = "Grass"
        if y <= 3:
            if x > 6: label = "Forest"
        else:
            if x <= 6: label = "Water"
            elif y > 5: label = "Desert"
        if (y == 3 and x < 6) or (x == 6 and y > 3):
            label = "Coast"
        if ((x == 2 or x == 1) and abs(y - 7) <= 1) or (y == 7 and x == 0):
            label = "Coast"
        if x == 1 and y == 7:
            label = "Island Fortress"
        if x <= 12 and y <= 8 and x > 7 and y > 6:
            label = "Cactus"
        if x == 6 and y == 1:
            label = "Warlock Tower"
        return game.rules.tileGeneration.generate(label)
    )
    discard game.addNewUnit("Sir Eoinn", initCoord(0, 0), HUMAN_PLAYER)
    discard game.addNewUnit("Ixtololotli", initCoord(1, 0), HUMAN_PLAYER)
    discard game.addNewUnit("Lady Maria", initCoord(0, 1), HUMAN_PLAYER)
    ambientPartyQuest(
        world.getTile(initCoord(1, 3)),
        game,
        @["Slime Cube"],
        "Some special loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Lucky Fishing Rod", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(3, 3)),
        game,
        @["Iron Beetle", "Shade", "Banshee"],
        "A new ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Jack the Scoundrel", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(6, 6)),
        game,
        @["Iron Beetle", "Pike Gremlin", "Kobold Sycophant"],
        "A new ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Dorrie", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(6, 8)),
        game,
        @["Pike Gremlin"],
        "Some special loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Bag of Mirth", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(8, 7)),
        game,
        @["Iron Beetle", "Pike Gremlin", "Iron Beetle"],
        "A new ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Mizton of the Wastes", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(11, 6)),
        game,
        @["Pike Gremlin", "Pike Gremlin", "Pike Gremlin", "Kobold Sycophant", "Acolyte of C'thos", "Banshee"],
        "Two new allies",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Tunde the Sorceror", this.pos, HUMAN_PLAYER)
            discard game.addNewUnit("Azdwagit Half-Djinn", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(7, 6)),
        game,
        @["Kobold Sycophant", "Kobold Sycophant", "Shade", "Shade", "Pike Gremlin"],
        "Some new loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Stone Ring", this.pos)
            discard game.addNewItem("Crystal Rose", this.pos)
            discard game.addNewItem("Nopal Knife", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(9, 9)),
        game,
        @["Iron Beetle", "Pike Gremlin", "Shade", "Shade", "Pike Gremlin", "Kobold Sycophant"],
        "An ally and loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Dagger of Akhthemes", this.pos)
            discard game.addNewUnit("Iss'lis the Searing Death", this.pos, HUMAN_PLAYER)
    )

    ambientPartyQuest(
        world.getTile(initCoord(8, 0)),
        game,
        @["Shade"],
        "An ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Elder Usquanigodi", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(8, 2)),
        game,
        @["Slime Cube"],
        "An ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Druidic Hermit", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(10, 1)),
        game,
        @["Shade"],
        "An ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Ranger Dawisgala", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(11, 3)),
        game,
        @["Iron Beetle"],
        "An ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Huginn Blackfeather", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(7, 2)),
        game,
        @["Iron Beetle"],
        "Loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Scholarly Amulet", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(10, 0)),
        game,
        @["Slime Cube"],
        "Loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Wizard Robes", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(11, 2)),
        game,
        @["Shade", "Shade"],
        "Loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Telescope", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(3, 1)),
        game,
        @["Pike Gremlin"],
        "Loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Bag of Gold", this.pos)
            discard game.addNewItem("Gold Coin", this.pos)
            discard game.addNewItem("Gold Coin", this.pos)
            discard game.addNewItem("Novice's Charm", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(4, 2)),
        game,
        @["Pike Gremlin", "Slime Cube"],
        "Loot",
        proc (this: Tile, game: GameView): void =
            discard game.addNewItem("Iron Sword", this.pos)
            discard game.addNewItem("Chain Mail", this.pos)
            discard game.addNewItem("Bag of Gold", this.pos)
    )
    ambientPartyQuest(
        world.getTile(initCoord(5, 0)),
        game,
        @["Pike Gremlin", "Pike Gremlin"],
        "Ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Hardin Redbeard", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(7, 4)),
        game,
        @["Pike Gremlin", "Pike Gremlin", "Iron Beetle", "Slime Cube"],
        "Ally",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Bato", this.pos, HUMAN_PLAYER)
    )
    ambientPartyQuest(
        world.getTile(initCoord(10, 5)),
        game,
        @["Pike Gremlin", "Pike Gremlin", "Iron Beetle", "Iron Beetle", "Slime Cube", "Slime Cube", "Kobold Sycophant", "Kobold Sycophant", "Banshee", "Shade", "Acolyte of C'thos"],
        "A legendary cache",
        proc (this: Tile, game: GameView): void =
            discard game.addNewUnit("Guroch the Impenetrable", this.pos, HUMAN_PLAYER)
            discard game.addNewItem("Tome of Geomancy", this.pos)
            discard game.addNewItem("Enchanted Boots", this.pos)
            discard game.addNewItem("Fey-Wrought Plate", this.pos)
    )
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
