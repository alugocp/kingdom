package net.lugocorp.kingdom.ai;

/**
 * Tells an Actor what to do after processing a PlanNode
 */
public enum ActionResult {
    NONE, // Do nothing
    POP, // Pop the current PlanNode off the queue
    POP_ALL, // Pop all remaining PlanNodes off the queue
    RIDE; // Pop the current PlanNode and process the next one
}
