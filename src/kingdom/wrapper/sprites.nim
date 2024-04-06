import raylib
import std/tables
import std/strformat
import kingdom/math/types
import kingdom/wrapper/types
import kingdom/controls/view

# Handles loading and drawing sprite assets
type SpriteManager* = ref object
    spritesheets: seq[string]
    loaded: Table[string, Texture2D]

# Constructor for the SpriteManager type
proc newSpriteManager*(): SpriteManager =
    new result
    result.loaded = initTable[string, Texture2D]()
    result.spritesheets = @[]

# Registers a filename to be loaded at will by the SpriteManager
proc registerSheet*(this: SpriteManager, modname: string, filename: string): SheetHandle =
    let i = SheetHandle(this.spritesheets.len)
    if i >= 255:
        raise newException(Exception, "Cannot register more than 255 spritesheets")
    this.spritesheets.add(fmt"out/mods/{modname}/assets/{filename}.png")
    return i

# Returns true if the given spritesheet is currently loaded in memory
proc isSheetLoaded*(this: SpriteManager, id: SheetHandle): bool =
    return this.loaded.hasKey(this.spritesheets[id])

# Loads a spritesheet by its ID into memory
proc loadSheet(this: SpriteManager, id: SheetHandle): void =
    let filepath = this.spritesheets[id]
    let image = loadImage(filepath)
    this.loaded[filepath] = loadTextureFromImage(image)

# Loads all registered spritesheets
proc loadAllSheets*(this: SpriteManager): void =
    for i in 0..(this.spritesheets.len - 1):
        this.loadSheet(SheetHandle(i))

# Creates a SpriteHandle from an input sheet ID and bounding rectangle
proc getSpriteHandle*(this: SpriteManager, id: SheetHandle, x: uint8, y: uint8, w: uint8 = 24, h: uint8 = 24): SpriteHandle =
    return (uint64(id) shl 32) + (uint64(x) shl 24) + (uint64(y) shl 16) + (uint64(w) shl 8) + (h)

# Draws a sprite from a loaded spritesheet
proc drawSprite*(this: SpriteManager, sprite: SpriteHandle, view: View, x: float, y: float): void =
    # Don't draw a null sprite or a sprite from an unloaded spritesheet
    let id = SheetHandle((sprite and 0xFF00000000'u64) shr 32)
    if sprite == NULL_SPRITE or not this.isSheetLoaded(id):
        return

    # Unpack SpriteHandle values
    let x1 = uint8((sprite and 0x00FF000000'u64) shr 24)
    let y1 = uint8((sprite and 0x0000FF0000'u64) shr 16)
    let w = uint8((sprite and  0x000000FF00'u64) shr 8)
    let h = uint8(sprite and   0x00000000FF'u64)
    let src = Rectangle(x: float(x1), y: float(y1), width: float(w), height: float(h))
    let dst = Rectangle(x: x, y: y, width: float(w) * 2, height: float(h) * 2)
    drawTexture(this.loaded[this.spritesheets[id]], src, dst, Vector2(x: 0, y: 0), 0, RayWhite)
proc drawSprite*(this: SpriteManager, sprite: SpriteHandle, view: View, pos: Position): void =
    this.drawSprite(sprite, view, pos.x, pos.y)
