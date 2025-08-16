package net.lugocorp.kingdom.ai;

/**
 * Tells an Actor what to do after processing a LowNode
 */
public enum ActionResult {
    NONE, // Do nothing
    POP, // Pop the current LowNode off the queue
    POP_ALL, // Pop all remaining LowNodes off the queue
    RIDE; // Pop the current LowNode and process the next one
}
