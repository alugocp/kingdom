import raylib
import std/sets
import kingdom/wrapper/sprites
import kingdom/controls/types
import kingdom/controls/mouse
import kingdom/controls/keyboard
import kingdom/models/rules
import kingdom/views/start
import kingdom/views/types
import kingdom/math/types
import kingdom/mods/loader
import kingdom/mods/core

# Record mouse state for later consumption
proc handleLogic(this: MouseState): void =
    let x = getMouseX()
    let y = getMouseY()
    this.wasDown = this.down
    this.wasScrolling = this.scrolling
    if not this.down and isMouseButtonDown(MouseButton.Left):
        this.mouseDown(initPosition(x, y))
    elif this.down and isMouseButtonUp(MouseButton.Left):
        this.mouseUp(initPosition(x, y))
    else:
        this.mouseMove(initPosition(x, y))

# Record keyboard state for later consumption
proc handleLogic(this: KeyboardState): void =
    var pressed = HashSet[int]()
    var keycode = int(getKeyPressed())
    while keycode > 0:
        pressed.incl(keycode)
        keycode = int(getKeyPressed())
    for k in this.down:
        {.warning[HoleEnumConv]: off.}
        if isKeyDown(KeyboardKey(k)):
            pressed.incl(k)
    this.keysPressed(pressed)

# Handles game loop logic
proc gameLoop*(): void =
    # Initialize the Raylib game window
    initWindow(1200, 800, "Kingdom")
    defer: closeWindow()
    setTargetFPS(30)

    # Initialize the game logic
    let rules = newGameRuleData()
    let initial = newStartView(rules)
    let state = newModCoreInterface(initial, rules)
    discard state.loadMod("vanilla")
    rules.sprites.loadAllSheets()

    # Raylib game loop
    while not windowShouldClose():
        state.view.mouse.handleLogic()
        state.view.keyboard.handleLogic()
        state.view.consumeKeyboardUpdates()
        state.view.consumeMouseUpdates()
        beginDrawing()
        state.view.draw()
        endDrawing()
        state.view = state.view.getNextView()
