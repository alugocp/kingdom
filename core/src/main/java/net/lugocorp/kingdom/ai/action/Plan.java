package net.lugocorp.kingdom.ai.action;

/**
 * Encompasses a PlanNode and its score
 */
public class Plan {
    public final PlanNode root;
    public final float score;

    public Plan(PlanNode root, float score) {
        this.score = score;
        this.root = root;
    }
}
