package net.lugocorp.kingdom.serial.v1d0d0;
import net.lugocorp.kingdom.context.Context;
import net.lugocorp.kingdom.game.model.Ability;
import net.lugocorp.kingdom.serial.Serial;

/**
 * Serial<Ability> implementation for v1.0.0
 */
public class AbilitySerial implements Serial<Ability> {
    public final String name;
    public final int cooldown;

    public AbilitySerial(String name, int cooldown) {
        this.cooldown = cooldown;
        this.name = name;
    }

    /** {@inheritdoc} */
    public Ability deserialize() {
        final Ability a = Context.getContext().generator.ability(null, this.name);
        a.goOnCooldown(this.cooldown - 1);
        return a;
    }
}
