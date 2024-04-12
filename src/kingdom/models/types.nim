import kingdom/entities/types

# World type to contain Tile objects
type World* = ref object
    units*: seq[seq[seq[Unit]]]
    items*: seq[seq[seq[Item]]]
    tiles*: seq[seq[Tile]]
    w*: Natural
    h*: Natural