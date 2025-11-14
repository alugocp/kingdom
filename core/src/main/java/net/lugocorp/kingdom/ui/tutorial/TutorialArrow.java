package net.lugocorp.kingdom.ui.tutorial;

/**
 * This enum tracks where the arrow points from a TutorialPopup
 */
enum TutorialArrow {
    NONE, UP, DOWN;

    /**
     * Returns the offset for a TutorialPopup give its height
     */
    int offset(int h) {
        if (this == TutorialArrow.NONE) {
            return -(h / 2);
        }
        if (this == TutorialArrow.DOWN) {
            return -h;
        }
        return 0;
    }
}
