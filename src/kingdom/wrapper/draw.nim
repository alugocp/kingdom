import raylib
import std/bitops
import std/strutils
import std/strformat
import kingdom/math/types
import kingdom/math/hexagons
import kingdom/wrapper/types
import kingdom/controls/view

# Contant value representing text margins
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

#
# GAME WORLD DRAWING FUNCTIONS
#

# Draws a hexagon like the one used for Tiles
proc drawHexagon*(x: float, y: float, color: uint32, view: View): void =
    let v = Vector2(x: x, y: y)
    let c = color.toRaylibColor()
    drawPoly(v, 6, hexagons.SIDE * view.scale, 90, c)
proc drawHexagon*(pos: Position, color: uint32, view: View): void =
    drawHexagon(pos.x, pos.y, color, view)

# Outlines a hexagon like the one used for Tiles
proc outlineHexagon*(x: float, y: float, view: View): void =
    let v = Vector2(x: x, y: y)
    drawPolyLines(v, 6, hexagons.SIDE * view.scale, 90, raylib.Black)
proc outlineHexagon*(pos: Position, view: View): void =
    outlineHexagon(pos.x, pos.y, view)

#
# MENU DRAWING FUNCTIONS
#

# Calculate the size of this text
proc getTextSize*(text: string, settings: FontSettings = REGULAR_FONT): Position =
    let font = getFontDefault()
    var w = 0'f32
    var h = 0'f32
    for line in text.splitLines():
        let v = measureText(font, cstring(line), float32(settings.size), SPACING)
        w = if v.x > w: v.x else: w
        h += v.y
    return initPosition(w, h)

# Draw some text
proc drawText*(text: string, x: float, y: float, settings: FontSettings = REGULAR_FONT): void =
    let c = settings.color.toRaylibColor()
    let lines = text.splitLines()
    if lines.len == 0:
        return
    var y1 = y
    let dy = lines[0].getTextSize(settings).y
    for line in lines:
        raylib.drawText(cstring(line), int32(x), int32(y1), int32(settings.size), c)
        y1 += dy

# Draw a rectangle
proc drawRect*(x: float, y: float, w: float, h: float, color: uint32): void =
    let r = Rectangle(x: x, y: y, width: w, height: h)
    let c = color.toRaylibColor()
    drawRectangle(r, c)

# Inserts newline characters where necessary to wrap text within the given bounds
proc wrapText*(text: string, width: float, settings: FontSettings = REGULAR_FONT): string =
    let words = text.split()
    let mid = "\n"
    var rebuilt = ""
    var line = ""
    for w in words:
        let lookahead = if line.len > 0: fmt"{line} {w}" else: w
        if getTextSize(lookahead, settings).x < width:
            line = lookahead
        else:
            rebuilt = if rebuilt.len > 0: fmt"{rebuilt}{mid}{line}" else: line
            line = w
    rebuilt = if rebuilt.len > 0: fmt"{rebuilt}{mid}{line}" else: line
    return rebuilt