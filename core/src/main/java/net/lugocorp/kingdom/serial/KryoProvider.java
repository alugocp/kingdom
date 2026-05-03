package net.lugocorp.kingdom.serial;
import com.esotericsoftware.kryo.Kryo;

/**
 * This class returns a Kryo instance
 */
public class KryoProvider {

    /**
     * Returns a new instance of Kryo
     */
    public static Kryo getKryo() {
        Kryo kryo = new Kryo();
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);

        // Register built-in Java classes
        kryo.register(java.time.OffsetTime.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(java.util.HashMap.class);
        kryo.register(java.util.HashSet.class);
        kryo.register(java.util.Optional.class);

        // Register LibGDX classes
        kryo.register(com.badlogic.gdx.graphics.Color.class);
        kryo.register(com.badlogic.gdx.graphics.g2d.Gdx2DPixmap.class);

        // net.lugocorp.kingdom.content.vanilla
        kryo.register(net.lugocorp.kingdom.content.vanilla.VanillaMod.class);

        // net.lugocorp.kingdom.ai
        kryo.register(net.lugocorp.kingdom.ai.Actor.class);
        kryo.register(net.lugocorp.kingdom.ai.action.ActionResult.class);
        kryo.register(net.lugocorp.kingdom.ai.action.Goal.class);
        kryo.register(net.lugocorp.kingdom.ai.action.GoalUtils.class);
        kryo.register(net.lugocorp.kingdom.ai.action.Plan.class);
        kryo.register(net.lugocorp.kingdom.ai.action.PlanNode.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.AttackEnemy.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.ClaimGlyphs.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.ClaimPassiveBuildings.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.ExploreMap.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.HarvestFood.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.IncreaseUnitPoints.class);
        kryo.register(net.lugocorp.kingdom.ai.goals.MineGold.class);
        kryo.register(net.lugocorp.kingdom.ai.memory.MemoryCell.class);
        kryo.register(net.lugocorp.kingdom.ai.memory.MemoryMap.class);
        kryo.register(net.lugocorp.kingdom.ai.plans.CastSpellNode.class);
        kryo.register(net.lugocorp.kingdom.ai.plans.LazyNode.class);
        kryo.register(net.lugocorp.kingdom.ai.plans.MoveNode.class);
        kryo.register(net.lugocorp.kingdom.ai.prediction.CapturedEvents.class);
        kryo.register(net.lugocorp.kingdom.ai.prediction.EventLog.class);
        kryo.register(net.lugocorp.kingdom.ai.prediction.SelectedTargets.class);
        kryo.register(net.lugocorp.kingdom.ai.stats.DiffStat.class);
        kryo.register(net.lugocorp.kingdom.ai.stats.Stat.class);
        kryo.register(net.lugocorp.kingdom.ai.stats.Statistics.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.ArtifactWishlist.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.Desires.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.GlyphWishlist.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.PatronWishlist.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.UnitWishlist.class);
        // kryo.register(net.lugocorp.kingdom.ai.wishlist.Wishlist.class);
        kryo.register(net.lugocorp.kingdom.ai.wishlist.Wishlists.class);

        // net.lugocorp.kingdom.builtin
        kryo.register(net.lugocorp.kingdom.builtin.Events.class);
        kryo.register(net.lugocorp.kingdom.builtin.animation.AttackAnimation.class);
        kryo.register(net.lugocorp.kingdom.builtin.animation.CameraMoveAnimation.class);
        kryo.register(net.lugocorp.kingdom.builtin.animation.DamagedAnimation.class);
        kryo.register(net.lugocorp.kingdom.builtin.animation.MoveAnimation.class);
        kryo.register(net.lugocorp.kingdom.builtin.logic.AbilityLogic.class);
        kryo.register(net.lugocorp.kingdom.builtin.logic.ItemLogic.class);
        kryo.register(net.lugocorp.kingdom.builtin.logic.UnitLogic.class);

        // net.lugocorp.kingdom.color
        kryo.register(net.lugocorp.kingdom.color.ColorPoint.class);
        kryo.register(net.lugocorp.kingdom.color.ColorPool.class);
        kryo.register(net.lugocorp.kingdom.color.Colors.class);
        kryo.register(net.lugocorp.kingdom.color.ColorScheme.class);

        // net.lugocorp.kingdom.engine
        kryo.register(net.lugocorp.kingdom.engine.AudioVideo.class);
        kryo.register(net.lugocorp.kingdom.engine.InverseRenderableSorter.class);
        kryo.register(net.lugocorp.kingdom.engine.animation.Animation.class);
        kryo.register(net.lugocorp.kingdom.engine.animation.AnimationChain.class);
        kryo.register(net.lugocorp.kingdom.engine.animation.AnimationQueue.class);
        // kryo.register(net.lugocorp.kingdom.engine.animation.FrameType.class);
        kryo.register(net.lugocorp.kingdom.engine.animation.Tween.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.AssetsPool.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.ModelLoader.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.MusicLoader.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.SoundLoader.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.SpriteLoader.class);
        kryo.register(net.lugocorp.kingdom.engine.assets.TextureLoader.class);
        kryo.register(net.lugocorp.kingdom.engine.controllers.GameViewController.class);
        kryo.register(net.lugocorp.kingdom.engine.controllers.KeyState.class);
        kryo.register(net.lugocorp.kingdom.engine.controllers.MenuController.class);
        kryo.register(net.lugocorp.kingdom.engine.controllers.Shortcut.class);
        // kryo.register(net.lugocorp.kingdom.engine.controllers.TouchState.class);
        kryo.register(net.lugocorp.kingdom.engine.fonts.FontParam.class);
        kryo.register(net.lugocorp.kingdom.engine.fonts.FontService.class);
        kryo.register(net.lugocorp.kingdom.engine.projection.CameraLogic.class);
        kryo.register(net.lugocorp.kingdom.engine.projection.ViewportLogic.class);
        kryo.register(net.lugocorp.kingdom.engine.render.Drawable.class);
        kryo.register(net.lugocorp.kingdom.engine.render.DynamicModellable.class);
        kryo.register(net.lugocorp.kingdom.engine.render.Modellable.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.ElementShader.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.OutlineShader.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.PreviewShader.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.ShaderZoo.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.TileShader.class);
        kryo.register(net.lugocorp.kingdom.engine.shaders.ToonShader.class);
        kryo.register(net.lugocorp.kingdom.engine.userdata.CoordUserData.class);
        kryo.register(net.lugocorp.kingdom.engine.userdata.TileUserData.class);

        // net.lugocorp.kingdom.game
        kryo.register(net.lugocorp.kingdom.game.Game.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.Glyph.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.GlyphCategory.class);
        kryo.register(net.lugocorp.kingdom.game.glyph.UnitGlyphs.class);
        kryo.register(net.lugocorp.kingdom.game.layers.DummyUnit.class);
        kryo.register(net.lugocorp.kingdom.game.layers.Entity.class);
        kryo.register(net.lugocorp.kingdom.game.layers.Spawnable.class);
        kryo.register(net.lugocorp.kingdom.game.model.Ability.class);
        kryo.register(net.lugocorp.kingdom.game.model.Artifact.class);
        kryo.register(net.lugocorp.kingdom.game.model.Building.class);
        kryo.register(net.lugocorp.kingdom.game.model.Fate.class);
        kryo.register(net.lugocorp.kingdom.game.model.Generator.class);
        kryo.register(net.lugocorp.kingdom.game.model.Item.class);
        kryo.register(net.lugocorp.kingdom.game.model.Patron.class);
        kryo.register(net.lugocorp.kingdom.game.model.Tile.class);
        kryo.register(net.lugocorp.kingdom.game.model.Tower.class);
        kryo.register(net.lugocorp.kingdom.game.model.Unit.class);
        kryo.register(net.lugocorp.kingdom.game.player.CompPlayer.class);
        kryo.register(net.lugocorp.kingdom.game.player.HumanPlayer.class);
        kryo.register(net.lugocorp.kingdom.game.player.Player.class);
        kryo.register(net.lugocorp.kingdom.game.properties.BuildingType.class);
        kryo.register(net.lugocorp.kingdom.game.properties.EntityType.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Inventory.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Rarity.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Species.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Tags.class);
        kryo.register(net.lugocorp.kingdom.game.properties.Vision.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Abilities.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Adjacency.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Hunger.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Leadership.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Movement.class);
        kryo.register(net.lugocorp.kingdom.game.unit.Sleep.class);
        // kryo.register(net.lugocorp.kingdom.game.world.Biome.class);
        kryo.register(net.lugocorp.kingdom.game.world.World.class);
        kryo.register(net.lugocorp.kingdom.game.world.WorldGenerator.class);
        kryo.register(net.lugocorp.kingdom.game.world.WorldGenOptions.class);
        kryo.register(net.lugocorp.kingdom.game.world.WorldSize.class);

        // net.lugocorp.kingdom.gameplay
        // kryo.register(net.lugocorp.kingdom.gameplay.actions.Action.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.ActionManager.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.ActionType.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.ActivateAction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.MoveAction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.actions.SkipAction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.combat.Combat.class);
        kryo.register(net.lugocorp.kingdom.gameplay.combat.Damage.class);
        kryo.register(net.lugocorp.kingdom.gameplay.combat.HitPoints.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.AllEventHandlers.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.Event.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.EventHandler.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.EventHandlerBundle.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.EventReceiver.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.SignalBooster.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.SingleEventHandler.class);
        kryo.register(net.lugocorp.kingdom.gameplay.events.Stratified.class);
        kryo.register(net.lugocorp.kingdom.gameplay.future.FutureEventManager.class);
        kryo.register(net.lugocorp.kingdom.gameplay.future.FutureTick.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.ArtifactAuction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Auction.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.DayNight.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.DayNightState.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Fates.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.GlyphPools.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.LootTable.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Mechanics.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.NewUnit.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Patronage.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.Turn.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.TurnState.class);
        kryo.register(net.lugocorp.kingdom.gameplay.mechanics.TurnStructure.class);

        // net.lugocorp.kingdom.math
        kryo.register(net.lugocorp.kingdom.math.Coords.class);
        kryo.register(net.lugocorp.kingdom.math.Hexagons.class);
        kryo.register(net.lugocorp.kingdom.math.HexSide.class);
        kryo.register(net.lugocorp.kingdom.math.Path.class);
        kryo.register(net.lugocorp.kingdom.math.Point.class);
        kryo.register(net.lugocorp.kingdom.math.Rect.class);

        // net.lugocorp.kingdom.menu
        kryo.register(net.lugocorp.kingdom.menu.game.ArtifactNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.DayNightNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.FateNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.FateViewNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.GlyphBadgeNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.GlyphIconsNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.InventoryNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.ModelNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.ResourceBarsNode.class);
        kryo.register(net.lugocorp.kingdom.menu.game.UnitOptionsNode.class);
        kryo.register(net.lugocorp.kingdom.menu.icon.ActionNode.class);
        kryo.register(net.lugocorp.kingdom.menu.icon.HeaderDescNode.class);
        kryo.register(net.lugocorp.kingdom.menu.icon.HelperNode.class);
        kryo.register(net.lugocorp.kingdom.menu.icon.IconNode.class);
        kryo.register(net.lugocorp.kingdom.menu.input.OptionsNode.class);
        kryo.register(net.lugocorp.kingdom.menu.input.TextEntryNode.class);
        kryo.register(net.lugocorp.kingdom.menu.input.VolumeNode.class);
        // kryo.register(net.lugocorp.kingdom.menu.structure.Column.class);
        // kryo.register(net.lugocorp.kingdom.menu.structure.ColumnType.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.GridNode.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.ListNode.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.MenuMenuNode.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.RowNode.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.SpacerNode.class);
        kryo.register(net.lugocorp.kingdom.menu.structure.TabsNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.BadgeNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.ButtonNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.HeaderNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.HoverTextNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.NakedButtonNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.NameNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.PlayerBadgeNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.SubheaderNode.class);
        kryo.register(net.lugocorp.kingdom.menu.text.TextNode.class);
        kryo.register(net.lugocorp.kingdom.menu.Menu.class);
        kryo.register(net.lugocorp.kingdom.menu.MenuNode.class);
        kryo.register(net.lugocorp.kingdom.menu.MenuPopup.class);
        kryo.register(net.lugocorp.kingdom.menu.MenuSubject.class);

        // net.lugocorp.kingdom.mods
        kryo.register(net.lugocorp.kingdom.mods.GameMod.class);
        kryo.register(net.lugocorp.kingdom.mods.ModLoader.class);
        kryo.register(net.lugocorp.kingdom.mods.ModProfile.class);

        // net.lugocorp.kingdom.pathfinding
        // kryo.register(net.lugocorp.kingdom.pathfinding.PathData.class);
        kryo.register(net.lugocorp.kingdom.pathfinding.Pathfinder.class);

        // net.lugocorp.kingdom.serial
        kryo.register(net.lugocorp.kingdom.serial.KryoProvider.class);
        kryo.register(net.lugocorp.kingdom.serial.SaveLoad.class);

        // net.lugocorp.kingdom.settings
        kryo.register(net.lugocorp.kingdom.settings.Settings.class);
        kryo.register(net.lugocorp.kingdom.settings.SettingsIO.class);
        kryo.register(net.lugocorp.kingdom.settings.SettingsJson.class);

        // net.lugocorp.kingdom.ui
        kryo.register(net.lugocorp.kingdom.ui.hud.BotHud.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.DebugHud.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.Hud.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.Logger.class);
        // kryo.register(net.lugocorp.kingdom.ui.hud.LogMessage.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.Minimap.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.Popups.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.TileMenu.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.TopHud.class);
        kryo.register(net.lugocorp.kingdom.ui.hud.TurnButton.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.ActionOverlay.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.EntityRisingOverlay.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.HealthChangeOverlay.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.Overlay.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.OverlayLayer.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.ResourceOverlay.class);
        kryo.register(net.lugocorp.kingdom.ui.overlay.RisingOverlay.class);
        // kryo.register(net.lugocorp.kingdom.ui.selection.TileMenuSelectMode.class);
        // kryo.register(net.lugocorp.kingdom.ui.selection.TileMoveSelectMode.class);
        // kryo.register(net.lugocorp.kingdom.ui.selection.TileSelectMode.class);
        kryo.register(net.lugocorp.kingdom.ui.selection.TileSelector.class);
        // kryo.register(net.lugocorp.kingdom.ui.selection.TileSetSelectMode.class);
        kryo.register(net.lugocorp.kingdom.ui.tutorial.Tutorial.class);
        // kryo.register(net.lugocorp.kingdom.ui.tutorial.TutorialArrow.class);
        // kryo.register(net.lugocorp.kingdom.ui.tutorial.TutorialPopup.class);
        // kryo.register(net.lugocorp.kingdom.ui.views.ActiveModsView.class);
        // kryo.register(net.lugocorp.kingdom.ui.views.CreditsView.class);
        // kryo.register(net.lugocorp.kingdom.ui.views.GameCreationView.class);
        kryo.register(net.lugocorp.kingdom.ui.views.GameView.class);
        kryo.register(net.lugocorp.kingdom.ui.views.GenerateWorldView.class);
        // kryo.register(net.lugocorp.kingdom.ui.views.LoadGameView.class);
        kryo.register(net.lugocorp.kingdom.ui.views.LoadingGameView.class);
        kryo.register(net.lugocorp.kingdom.ui.views.SettingsView.class);
        // kryo.register(net.lugocorp.kingdom.ui.views.StartMenuView.class);
        kryo.register(net.lugocorp.kingdom.ui.views.ThreadedTaskView.class);
        kryo.register(net.lugocorp.kingdom.ui.View.class);

        // net.lugocorp.kingdom.utils
        kryo.register(net.lugocorp.kingdom.utils.BatchCounter.class);
        kryo.register(net.lugocorp.kingdom.utils.Lambda.class);
        kryo.register(net.lugocorp.kingdom.utils.Log.class);
        kryo.register(net.lugocorp.kingdom.utils.Semver.class);
        kryo.register(net.lugocorp.kingdom.utils.SideEffect.class);
        kryo.register(net.lugocorp.kingdom.utils.Tuple.class);
        return kryo;
    }
}
