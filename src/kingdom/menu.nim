import std/sugar
import std/sequtils
import kingdom/math/types
import kingdom/wrapper/draw
import kingdom/wrapper/types
import kingdom/controls/types
import kingdom/builtin/values
import kingdom/types

method getHeight*(this: MenuNode): float {.base.} = 0
method draw*(this: MenuNode, m: MouseState, r: Rect): void {.base.} = discard
method checkClick*(this: MenuNode, m: MouseState, r: Rect): void {.base.} = discard
method pack*(this: MenuNode, width: float): void {.base.} = discard

# Constructor for Menu type
proc newMenu*(x: float, y: float, width: float, root: MenuNode): Menu =
    new result
    result.root = root
    result.width = width
    result.x = x
    result.y = y

# Draw every MenuNode in this Menu
proc draw*(this: Menu, m: MouseState): void =
    drawRect(this.x, this.y, this.width, this.root.getHeight(), WHITE)
    let r = Rect(
        x: this.x,
        y: this.y,
        w: this.width,
        h: this.root.getHeight()
    )
    this.root.draw(m, r)

# Propogates a MouseState through this Menu to see if any node was clicked on
proc checkClick*(this: Menu, mouse: MouseState): bool =
    let r = Rect(
        x: this.x,
        y: this.y,
        w: this.width,
        h: this.root.getHeight()
    )
    this.root.checkClick(mouse, r)
    return mouse.pos.within(r)

# Recursively text wraps each child node to this Menu's width
proc pack*(this: Menu): void = this.root.pack(this.width)

# Text paragraph
type TextNode* = ref object of MenuNode
    text*: string

proc newTextNode*(text: string): TextNode =
    new result
    result.element = MenuElement.TEXT
    result.text = text

method getHeight*(this: TextNode): float = getTextSize(this.text).y
method draw*(this: TextNode, m: MouseState, r: Rect): void = drawText(this.text, r.x, r.y)
method checkClick*(this: TextNode, m: MouseState, r: Rect): void = discard
method pack*(this: TextNode, width: float): void =
    this.text = this.text.wrapText(width)

# Text header
type HeaderNode* = ref object of MenuNode
    text*: string

proc newHeaderNode*(text: string): HeaderNode =
    new result
    result.element = MenuElement.HEADER
    result.text = text

method getHeight*(this: HeaderNode): float = getTextSize(this.text, HEADER_FONT).y
method draw*(this: HeaderNode, m: MouseState, r: Rect): void = drawText(this.text, r.x, r.y, HEADER_FONT)
method checkClick*(this: HeaderNode, m: MouseState, r: Rect): void = discard
method pack*(this: HeaderNode, width: float): void =
    this.text = this.text.wrapText(width, HEADER_FONT)

# Clickable button
type ButtonNode* = ref object of MenuNode
    text*: string
    click*: () -> void

proc newButtonNode*(text: string, click: () -> void): ButtonNode =
    new result
    result.element = MenuElement.BUTTON
    result.click = click
    result.text = text

method getHeight*(this: ButtonNode): float = getTextSize(this.text).y
method draw*(this: ButtonNode, m: MouseState, r: Rect): void =
    let font = if m.pos.within(r): HOVER_FONT else: LINK_FONT
    drawText(this.text, r.x, r.y, font)
method checkClick*(this: ButtonNode, m: MouseState, r: Rect): void =
    if m.pos.within(r):
        this.click()
method pack*(this: ButtonNode, width: float): void =
    this.text = this.text.wrapText(width, LINK_FONT)

# Empty space
type SpaceNode* = ref object of MenuNode

proc newSpaceNode*(): SpaceNode =
    new result
    result.element = MenuElement.SPACE

method getHeight*(this: SpaceNode): float = 10
method draw*(this: SpaceNode, m: MouseState, r: Rect): void = discard
method checkClick*(this: SpaceNode, m: MouseState, r: Rect): void = discard
method pack*(this: SpaceNode, width: float): void = discard

# List of MenuNodes
type ListNode* = ref object of MenuNode
    nodes*: seq[MenuNode]

proc newListNode*(nodes: seq[MenuNode] = @[]): ListNode =
    new result
    result.nodes = nodes

proc add*(this: ListNode, node: MenuNode): void =
    this.nodes.add(node)

method getHeight*(this: ListNode): float = this.nodes.foldl(a + b.getHeight(), 0.0)
method draw*(this: ListNode, m: MouseState, r: Rect): void =
    var r1 = r
    for n in this.nodes:
        r1.h = n.getHeight()
        n.draw(m, r1)
        if n.element != MenuElement.LIST:
            r1.y += r1.h
method checkClick*(this: ListNode, m: MouseState, r: Rect): void =
    if m.pos.within(r):
        var r1 = r
        let nodes = this.nodes # Copied in case we change the Menu
        for n in nodes:
            r1.h = n.getHeight()
            n.checkClick(m, r1)
            if n.element != MenuElement.LIST:
                r1.y += r1.h
method pack*(this: ListNode, width: float): void =
    for n in this.nodes: n.pack(width)
