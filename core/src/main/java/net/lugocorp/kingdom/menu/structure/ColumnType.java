package net.lugocorp.kingdom.menu.structure;

/**
 * This enum determines how we calculate a RowNode Column's width
 */
enum ColumnType {
    // Exact pixel value for width
    EXACT,

    // Some ratio of the remaining width
    RATIO,

    // Equal distribution of the remaining width
    EQUAL;
}
