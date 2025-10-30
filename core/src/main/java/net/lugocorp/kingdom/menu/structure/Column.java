package net.lugocorp.kingdom.menu.structure;
import net.lugocorp.kingdom.menu.MenuNode;

/**
 * This class represents a RowNode's Column
 */
class Column {
    private final ColumnType type;
    private final MenuNode node;
    private final int value;

    Column(ColumnType type, int value, MenuNode node) {
        this.value = value;
        this.type = type;
        this.node = node;
    }

    /**
     * Returns this Column's type
     */
    ColumnType getType() {
        return this.type;
    }

    /**
     * Returns this Column's node
     */
    MenuNode getNode() {
        return this.node;
    }

    /**
     * Returns this Column's value
     */
    int getValue() {
        return this.value;
    }
}
