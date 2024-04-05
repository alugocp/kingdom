import raylib
import kingdom/math/types

# Returns the width and height of the game window
proc getWindowBounds*(): Position =
    initPosition(float(getScreenWidth()), float(getScreenHeight()))