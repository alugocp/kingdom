import std/sugar
import std/sequtils
import kingdom/draw

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

# Text paragraph
type TextNode* = ref object of MenuNode
    text*: string

proc newTextNode*(text: string): TextNode =
    new result
    result.element = MenuElement.TEXT
    result.text = text

method getHeight*(node: TextNode): float = getTextSize(node.text)[1]
method draw*(node: TextNode, x: float, y: float): void = drawText(node.text, x, y)

# Text header
type HeaderNode* = ref object of MenuNode
    text*: string

proc newHeaderNode*(text: string): HeaderNode =
    new result
    result.element = MenuElement.HEADER
    result.text = text

method getHeight*(node: HeaderNode): float = getTextSize(node.text)[1]
method draw*(node: HeaderNode, x: float, y: float): void = drawText(node.text, x, y)

# Clickable button
type ButtonNode* = ref object of MenuNode
    text*: string
    click*: () -> void

proc newButtonNode*(text: string, click: () -> void): ButtonNode =
    new result
    result.element = MenuElement.BUTTON
    result.click = click
    result.text = text

method getHeight*(node: ButtonNode): float = getTextSize(node.text)[1]
method draw*(node: ButtonNode, x: float, y: float): void = drawText(node.text, x, y)

# List of MenuNodes
type ListNode* = ref object of MenuNode
    nodes*: seq[MenuNode]

proc newListNode*(nodes: seq[MenuNode]): ListNode =
    new result
    result.nodes = nodes

method getHeight*(node: ListNode): float = node.nodes.foldl(a + b.getHeight(), 0.0)
method draw*(node: ListNode, x: float, y: float): void =
    var dy = 0.0
    for n in node.nodes:
        n.draw(x, y + dy)
        dy += n.getHeight()
