import std/sugar
import std/sequtils
import kingdom/math/types
import kingdom/wrapper/draw
import kingdom/controls/mouse
import kingdom/builtin/values

# Enum representing the types of UI elements
type MenuElement* = enum
    TEXT
    HEADER
    BUTTON
    LIST

# Parent class for all menu nodes
type MenuNode* = ref object of RootObj
    element*: MenuElement

method getHeight*(node: MenuNode): float {.base.} = 0
method draw*(node: MenuNode, x: float, y: float): void {.base.} = discard
method checkClick*(this: MenuNode, m: MouseState, r: Rect): void {.base.} = discard

# Top-level menu type
type Menu* = ref object
    root*: MenuNode
    width: float
    x*: float
    y*: float

# Constructor for Menu type
proc newMenu*(x: float, y: float, width: float, root: MenuNode): Menu =
    new result
    result.root = root
    result.width = width
    result.x = x
    result.y = y

# Draw every MenuNode in this Menu
proc draw*(this: Menu): void =
    drawRect(this.x, this.y, this.width, this.root.getHeight(), WHITE)
    this.root.draw(this.x, this.y)

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

# Text paragraph
type TextNode* = ref object of MenuNode
    text*: string

proc newTextNode*(text: string): TextNode =
    new result
    result.element = MenuElement.TEXT
    result.text = text

method getHeight*(node: TextNode): float = getTextSize(node.text).y
method draw*(node: TextNode, x: float, y: float): void = drawText(node.text, x, y, BLACK)
method checkClick*(this: TextNode, m: MouseState, r: Rect): void = discard

# Text header
type HeaderNode* = ref object of MenuNode
    text*: string

proc newHeaderNode*(text: string): HeaderNode =
    new result
    result.element = MenuElement.HEADER
    result.text = text

method getHeight*(node: HeaderNode): float = getTextSize(node.text).y
method draw*(node: HeaderNode, x: float, y: float): void = drawText(node.text, x, y, BLACK)
method checkClick*(this: HeaderNode, m: MouseState, r: Rect): void = discard

# Clickable button
type ButtonNode* = ref object of MenuNode
    text*: string
    click*: () -> void

proc newButtonNode*(text: string, click: () -> void): ButtonNode =
    new result
    result.element = MenuElement.BUTTON
    result.click = click
    result.text = text

method getHeight*(node: ButtonNode): float = getTextSize(node.text).y
method draw*(node: ButtonNode, x: float, y: float): void = drawText(node.text, x, y, BLACK)
method checkClick*(this: ButtonNode, m: MouseState, r: Rect): void =
    if m.pos.within(r):
        this.click()

# List of MenuNodes
type ListNode* = ref object of MenuNode
    nodes*: seq[MenuNode]

proc newListNode*(nodes: seq[MenuNode] = @[]): ListNode =
    new result
    result.nodes = nodes

proc add*(this: ListNode, node: MenuNode): void =
    this.nodes.add(node)

method getHeight*(node: ListNode): float = node.nodes.foldl(a + b.getHeight(), 0.0)
method draw*(node: ListNode, x: float, y: float): void =
    var dy = 0.0
    for n in node.nodes:
        n.draw(x, y + dy)
        if n.element != MenuElement.LIST:
            dy += n.getHeight()
method checkClick*(this: ListNode, m: MouseState, r: Rect): void =
    if m.pos.within(r):
        var r1 = r
        for n in this.nodes:
            r1.h = n.getHeight()
            n.checkClick(m, r1)
            if n.element != MenuElement.LIST:
                r1.y += r1.h
