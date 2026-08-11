package net.lugocorp.kingdom.ai.action;

/**
 * Encompasses a PlanNode and its score
 */
public class Plan {
    public final PlanNode root;
    public final Priority score;

    public Plan(PlanNode root, Priority score) {
        this.score = score;
        this.root = root;
    }
}
