import raylib
import kingdom/math/hexagons

const FONT_SIZE = 20
const SPACING = 1

# Initializes various settings for the draw code
proc initDrawLib*(): void =
    setTextLineSpacing(SPACING)

# Draws a hexagon like the one used for Tiles
proc drawHexagon*(x: float, y: float): void =
    let v = Vector2(x: x, y: y)
    drawPoly(v, 6, hexagons.SIDE, 90, GREEN)
    drawPolyLines(v, 6, hexagons.SIDE, 90, BLACK)

# Draw some text
proc drawText*(text: string, x: float, y: float): void =
    raylib.drawText(cstring(text), int32(x), int32(y), FONT_SIZE, BLACK)

# Calculate the size of this text
proc getTextSize*(text: string): (float, float) =
    let font = getFontDefault()
    let v = measureText(font, cstring(text), FONT_SIZE, SPACING)
    return (float(v.x), float(v.y))