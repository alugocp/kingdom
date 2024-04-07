import std/sets
import kingdom/controls/types

# Constructor for the KeyboardState type
proc newKeyboardState*(): KeyboardState =
    new result
    result.released = HashSet[int]()
    result.down = HashSet[int]()

# Update which keys are currently pressed
proc keysPressed*(this: KeyboardState, keycodes: HashSet[int]): void =
    this.released.clear()
    for k in this.down:
        if not keycodes.contains(k):
            this.released.incl(k)
    this.down = keycodes

# Return true if the given key was released in this frame
proc wasKeyReleased*(this: KeyboardState, keycode: int): bool =
    this.released.contains(keycode) and not this.down.contains(keycode)

# Get all the keys released in this frame
proc getKeysReleased*(this: KeyboardState): HashSet[int] =
    this.released