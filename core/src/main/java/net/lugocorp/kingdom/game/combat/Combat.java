package net.lugocorp.kingdom.game.combat;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.animation.AttackAnimation;
import net.lugocorp.kingdom.builtin.animation.DamagedAnimation;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.SideEffect;
import java.util.List;
import java.util.Optional;

/**
 * This class handles all combat logic for any Unit or Building
 */
public class Combat {
    private final Entity bearer;
    public final HitPoints health = new HitPoints();

    public Combat(Entity bearer) {
        this.bearer = bearer;
    }

    /**
     * This should only be used in conjunction with Kryo rehydration
     */
    public Combat() {
        this.bearer = null;
    }

    /**
     * This method gets called when a combatant is killed in battle
     */
    private SideEffect onDeath(GameView view, Entity attacker) {
        List<SideEffect> effects = SideEffect.list();
        if (this.bearer.isEntityType(EntityType.BUILDING)) {
            // Restore the Building's health and place under the attacking Player's control
            // if it was destroyed by another player
            Building b = (Building) this.bearer;
            Optional<Player> destroyer = attacker.getLeader();
            if (b.isActive() && !b.getLeader().equals(destroyer)) {
                effects.add(() -> {
                    view.logger.log(String.format("You claimed the %s", b.name));
                    // TODO play a noise here
                    this.health.set(this.health.getMax());
                    view.game.world.getTile(b.getPoint())
                            .ifPresent((Tile t) -> view.game.setLeader(view, t, destroyer));
                });
                return SideEffect.all(effects);
            }
        } else {
            effects.add(this.bearer.handleEvent(view, new Events.EntityDiedEvent(this.bearer, attacker)));
            effects.add(attacker.handleEvent(view, new Events.KilledEntityEvent(attacker, this.bearer)));

            // Track CompPlayer stats whenever Units are slain
            if (this.bearer.isEntityType(EntityType.UNIT)) {
                effects.add(() -> {
                    if (this.bearer.getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
                        CompPlayer comp = (CompPlayer) this.bearer.getLeader().get();
                        comp.stats.unitsLost.add(1);
                    }
                    if (attacker.getLeader().map((Player p) -> !p.isHumanPlayer()).orElse(false)) {
                        CompPlayer comp = (CompPlayer) attacker.getLeader().get();
                        comp.stats.enemiesKilled.add(1);
                    }
                });
            }
        }
        effects.add(() -> this.bearer.deactivate(view));
        return SideEffect.all(effects);
    }

    /**
     * Runs the logic required for this bearer to take Damage
     */
    public SideEffect takeDamage(GameView view, Damage dmg, Entity attacker) {
        if (!this.health.isVulnerable()) {
            return SideEffect.none;
        }
        List<SideEffect> effects = SideEffect.list();
        Events.TakeDamageEvent damageEvent = new Events.TakeDamageEvent(this.bearer, dmg);
        effects.add(this.bearer.handleEvent(view, damageEvent));
        effects.add(() -> this.health.set(this.health.get() - damageEvent.dmg.total()));
        if (this.health.get() <= damageEvent.dmg.total()) {
            effects.add(this.onDeath(view, attacker));
        }
        if (this.bearer.getEntityType() == EntityType.UNIT) {
            effects.add(() -> view.animations.add(new DamagedAnimation((Unit) this.bearer, attacker.getPoint())));
        }
        effects.add(() -> view.overlays.add(String.format("-%d", dmg.total()), 0xff0000, this.bearer.getPoint()));
        return SideEffect.all(effects);
    }

    /**
     * This bearer attacks another
     */
    public SideEffect attack(GameView view, Entity target, Damage dmg) {
        // Determine if this attack was a critical hit or not
        Events.CheckCriticalHitEvent crit = new Events.CheckCriticalHitEvent(this.bearer);
        this.bearer.handleEvent(view, crit);
        if (Math.floor(Math.random() * 100) < crit.chance) {
            dmg.setMultiplier(crit.multiplier);
        }

        // Trigger conjugate Events for an Entity attacking and another Entity being
        // attacked,
        // then calculate the outcomes of the battle (damage taken, death triggers, etc)
        return SideEffect.all(this.bearer.handleEvent(view, new Events.AttackEvent(this.bearer, target, dmg)),
                target.handleEvent(view, new Events.AttackedEvent(target, this.bearer, dmg)),
                target.combat.takeDamage(view, dmg, this.bearer), () -> {
                    if (this.bearer.getLeader().map((Player l) -> l.isHumanPlayer()).orElse(false)) {
                        view.av.loaders.sounds.play("sfx/attack-enemy");
                    }
                    if (this.bearer.getEntityType() == EntityType.UNIT) {
                        view.animations.add(new AttackAnimation((Unit) this.bearer, target.getPoint()));
                    }
                });
    }

    /**
     * This bearer heals another
     */
    public SideEffect heal(GameView view, Entity target, int amount) {
        Events.HealEntityEvent heal = new Events.HealEntityEvent(this.bearer, target, amount);
        this.bearer.handleEvent(view, heal);
        return () -> {
            target.combat.health.set(target.combat.health.get() + heal.amount);
            view.overlays.add(String.format("+%d", heal.amount), 0x00ff00, target.getPoint());
        };
    }

    /**
     * This bearer heals itself
     */
    public SideEffect heal(GameView view, int amount) {
        return this.heal(view, this.bearer, amount);
    }
}
