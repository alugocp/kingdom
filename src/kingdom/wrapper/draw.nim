import raylib
import std/bitops
import kingdom/math/types
import kingdom/math/hexagons

const FONT_SIZE = 20
const SPACING = 1

# Converts a hexadecimal value to a Raylib color
proc toRaylibColor(x: uint32): Color =
    Color(
        r: uint8(x.bitsliced(24..31)),
        g: uint8(x.bitsliced(16..23)),
        b: uint8(x.bitsliced(8..15)),
        a: uint8(x.bitsliced(0..7))
    )

# Initializes various settings for the draw code
proc initDrawLib*(): void =
    setTextLineSpacing(SPACING)

# Lays down a solid color in the background
proc setBackground*(color: uint32): void =
    let c = color.toRaylibColor()
    clearBackground(c)

# Draws a hexagon like the one used for Tiles
proc drawHexagon*(x: float, y: float, color: uint32): void =
    let v = Vector2(x: x, y: y)
    let c = color.toRaylibColor()
    drawPoly(v, 6, hexagons.SIDE, 90, c)

# Outlines a hexagon like the one used for Tiles
proc outlineHexagon*(x: float, y: float): void =
    let v = Vector2(x: x, y: y)
    drawPolyLines(v, 6, hexagons.SIDE, 90, Black)

# Draw some text
proc drawText*(text: string, x: float, y: float, color: uint32): void =
    let c = color.toRaylibColor()
    raylib.drawText(cstring(text), int32(x), int32(y), FONT_SIZE, c)

# Calculate the size of this text
proc getTextSize*(text: string): Position =
    let font = getFontDefault()
    let v = measureText(font, cstring(text), FONT_SIZE, SPACING)
    return initPosition(v.x, v.y)

# Draw a rectangle
proc drawRect*(x: float, y: float, w: float, h: float, color: uint32): void =
    let r = Rectangle(x: x, y: y, width: w, height: h)
    let c = color.toRaylibColor()
    drawRectangle(r, c)