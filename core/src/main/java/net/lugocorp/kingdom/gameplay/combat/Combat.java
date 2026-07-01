package net.lugocorp.kingdom.gameplay.combat;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.builtin.animation.AttackAnimation;
import net.lugocorp.kingdom.builtin.animation.DamagedAnimation;
import net.lugocorp.kingdom.color.ColorScheme;
import net.lugocorp.kingdom.game.layers.Entity;
import net.lugocorp.kingdom.game.model.Item;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.EntityType;
import net.lugocorp.kingdom.ui.overlay.EntityRisingOverlay;
import net.lugocorp.kingdom.ui.overlay.HealthChangeOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.SideEffect;
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
        // Logic for Towers
        if (this.bearer.isEntityType(EntityType.TOWER)) {
            return this.onTowerDeath(view, attacker.getLeader());
        }

        // Logic for non-Towers
        final SideEffect effects = new SideEffect();
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
        effects.add(() -> this.bearer.deactivate(view));
        return effects;
    }

    /**
     * Call this whenever a Tower dies. Restores the Tower's health and places it
     * under the attacking Player's control (if it was destroyed by another player)
     */
    private SideEffect onTowerDeath(GameView view, Optional<Player> destroyer) {
        final SideEffect effects = new SideEffect();
        final Tower t = (Tower) this.bearer;
        effects.add(() -> {
            if (!t.getLeader().equals(destroyer) && destroyer.map((Player d) -> d.isHumanPlayer()).orElse(false)) {
                view.overlays.entity(t).addRising(new EntityRisingOverlay(view, t, ColorScheme.RED.hex, "Captured"));
                view.hud.logger.log("You claimed the tower");
                view.av.loaders.sounds.play("sfx/captured");
            }
            this.health.set(this.health.getMax());
            view.game.setLeader(view, t, destroyer);
        });
        return effects;
    }

    /**
     * Call this when a Tower is damaged because its Player cannot afford it
     */
    public SideEffect damageTowerWithoutGold(GameView view, Damage dmg) {
        final SideEffect effects = new SideEffect();
        final Events.TakeDamageEvent damageEvent = new Events.TakeDamageEvent(this.bearer, dmg);
        effects.add(this.bearer.handleEvent(view, damageEvent));
        final int result = this.health.get() - damageEvent.dmg.total();
        effects.add(() -> this.health.set(result));
        if (result > 0) {
            effects.add(() -> view.overlays.entity(this.bearer).addRising(new EntityRisingOverlay(view, this.bearer,
                    ColorScheme.RED.hex, String.format("-%d", dmg.total()))));
        } else {
            if (this.bearer.leadership.belongsToHuman()) {
                effects.add(() -> {
                    view.overlays.entity(this.bearer)
                            .addRising(new EntityRisingOverlay(view, this.bearer, ColorScheme.RED.hex, "Lost"));
                    view.hud.logger.log("You lost a tower due to insufficient gold");
                });
            }
            effects.add(this.onTowerDeath(view, Optional.empty()));
        }
        return effects;
    }

    /**
     * Runs the logic required for this bearer to take Damage
     */
    public SideEffect takeDamage(GameView view, Damage dmg, Entity attacker) {
        final SideEffect effects = new SideEffect();
        if (!this.health.isVulnerable()) {
            return effects;
        }
        Events.TakeDamageEvent damageEvent = new Events.TakeDamageEvent(this.bearer, dmg);
        effects.add(this.bearer.handleEvent(view, damageEvent));
        effects.add(() -> this.health.set(this.health.get() - damageEvent.dmg.total()));
        final boolean willDie = this.health.get() <= damageEvent.dmg.total();
        if (willDie) {
            effects.add(this.onDeath(view, attacker));
        }
        if (this.bearer.getEntityType() == EntityType.UNIT) {
            effects.add(() -> view.animations.add(new DamagedAnimation((Unit) this.bearer, attacker.getPoint())));
        }
        effects.add(() -> view.overlays.entity(this.bearer).add(new HealthChangeOverlay(view, this.bearer,
                this.health.getMax(), this.health.get(), this.health.get() - damageEvent.dmg.total())));
        if (!willDie) {
            effects.add(() -> view.overlays.entity(this.bearer).addRising(new EntityRisingOverlay(view, this.bearer,
                    ColorScheme.RED.hex, String.format("-%d", dmg.total()))));
        }
        return effects;
    }

    /**
     * This bearer attacks another
     */
    public SideEffect attack(GameView view, Entity target, Damage dmg) {
        final SideEffect effects = new SideEffect();

        // Determine if this attack was a critical hit or not
        Events.CheckCriticalHitEvent crit = new Events.CheckCriticalHitEvent(this.bearer);
        effects.add(this.bearer.handleEvent(view, crit));
        if (Math.floor(Math.random() * 100) < crit.chance) {
            dmg.setMultiplier(crit.multiplier);
        }

        // Trigger conjugate Events for an Entity attacking and another Entity being
        // attacked, then calculate the outcomes of the battle (damage taken, death
        // triggers, etc)
        effects.add(this.bearer.handleEvent(view, new Events.AttackEvent(this.bearer, target, dmg)));
        effects.add(target.handleEvent(view, new Events.AttackedEvent(target, this.bearer, dmg)));
        effects.add(target.combat.takeDamage(view, dmg, this.bearer));
        effects.add(() -> {
            final boolean humanAttacker = this.bearer.getLeader().map((Player l) -> l.isHumanPlayer()).orElse(false);
            if (humanAttacker) {
                view.av.loaders.sounds.play("sfx/attack-enemy");
                if (target.isEntityType(EntityType.UNIT) && target.combat.health.isDead()) {
                    view.av.loaders.sounds.play("sfx/unit-death");
                }
            }
            if (this.bearer.getEntityType() == EntityType.UNIT) {
                final Unit u = (Unit) this.bearer;
                view.animations.add(new AttackAnimation(u, target.getPoint()));
                if (target.isEntityType(EntityType.UNIT) && target.combat.health.isDead() && !u.haul.isFull()) {
                    final Item item = view.game.mechanics.loot.drop(view.game);
                    view.overlays.entity(this.bearer)
                            .addRising(new EntityRisingOverlay(view, this.bearer, ColorScheme.WHITE.hex, item.name));
                    u.haul.add(item);
                }
            }
        });
        return effects;
    }

    /**
     * This bearer heals another
     */
    public SideEffect heal(GameView view, Entity target, int amount) {
        final SideEffect effects = new SideEffect();
        Events.HealEntityEvent heal = new Events.HealEntityEvent(this.bearer, target, amount);
        return effects.add(this.bearer.handleEvent(view, heal)).add(() -> {
            final boolean needsHealing = target.combat.health.get() < target.combat.health.getMax();
            target.combat.health.set(target.combat.health.get() + heal.amount);
            if (needsHealing) {
                view.overlays.entity(target).add(new HealthChangeOverlay(view, target, target.combat.health.getMax(),
                        target.combat.health.get(), target.combat.health.get() + heal.amount));
            }
            view.overlays.entity(target).addRising(
                    new EntityRisingOverlay(view, target, ColorScheme.GREEN.hex, String.format("+%d", heal.amount)));
        });
    }

    /**
     * This bearer heals itself
     */
    public SideEffect heal(GameView view, int amount) {
        return this.heal(view, this.bearer, amount);
    }
}
