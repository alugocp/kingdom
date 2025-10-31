package net.lugocorp.kingdom.menu.text;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.color.Colors;
import net.lugocorp.kingdom.engine.AudioVideo;
import net.lugocorp.kingdom.game.player.Player;

/**
 * This BadgeNode displays a Player's name with their colors
 */
public class PlayerBadgeNode extends BadgeNode {
    public PlayerBadgeNode(AudioVideo av, Player player) {
        super(av, Colors.asInt(player.color), ColorScheme.WHITE.hex, player.name);
    }
}
