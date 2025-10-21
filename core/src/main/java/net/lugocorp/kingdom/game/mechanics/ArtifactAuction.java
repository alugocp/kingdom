package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.builtin.Events;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Artifact;
import net.lugocorp.kingdom.game.player.CompPlayer;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.menu.Menu;
import net.lugocorp.kingdom.menu.game.ArtifactNode;
import net.lugocorp.kingdom.menu.structure.ListNode;
import net.lugocorp.kingdom.menu.structure.RowNode;
import net.lugocorp.kingdom.menu.structure.SpacerNode;
import net.lugocorp.kingdom.menu.text.ButtonNode;
import net.lugocorp.kingdom.menu.text.HeaderNode;
import net.lugocorp.kingdom.menu.text.HelperNode;
import net.lugocorp.kingdom.menu.text.NakedButtonNode;
import net.lugocorp.kingdom.menu.text.TextNode;
import net.lugocorp.kingdom.ui.ColorScheme;
import net.lugocorp.kingdom.ui.overlay.ResourceOverlay;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * This class manages the logic for artifact auctions
 */
public class ArtifactAuction {
    public static final int MAX_AUCTION_POINTS = 250;
    @FieldSerializer.Optional("random")
    private final Random random = new Random();
    private final List<Artifact> artifacts = new ArrayList<>();
    private Optional<Auction> auction = Optional.empty();
    private int points = 0;

    /**
     * Generates all registered Artifacts at the start of the Game
     */
    public void init(Game g) {
        Set<String> stratifiers = g.events.artifact.getStratifiers();
        final List<Artifact> loading = new ArrayList<>();
        int maxChips = 0;
        for (String name : stratifiers) {
            final Artifact a = g.generator.artifact(name);
            if (a.chips > maxChips) {
                maxChips = a.chips;
            }
            loading.add(a);
        }
        final int maxValue = maxChips;
        this.artifacts.addAll(Lambda.sort((Artifact a) -> maxValue - a.chips, loading));
    }

    /**
     * Returns all available Artifacts
     */
    public List<Artifact> getArtifacts() {
        return this.artifacts;
    }

    /**
     * Calculates how much gold a Player must pay to participate in an auction
     */
    public int getBuyInCost(int gold) {
        return (int) Math.floor(gold * 0.2);
    }

    /**
     * Retrieves the currently open Auction (if any)
     */
    public Optional<Auction> getAuction() {
        return this.auction;
    }

    /**
     * Closes the currently open Auction
     */
    public void closeAuction() {
        this.auction = Optional.empty();
    }

    /**
     * Opens a new Auction
     */
    public void openNewAuction(Game g, Runnable runnable) {
        this.points -= ArtifactAuction.MAX_AUCTION_POINTS;
        if (this.numberUnclaimedArtifacts() > 0) {
            this.auction = Optional.of(new Auction(g));
            runnable.run();
        }
    }

    /**
     * Returns true if we should open a new Auction
     */
    public boolean readyForNewAuction() {
        return this.points >= ArtifactAuction.MAX_AUCTION_POINTS && !this.getAuction().isPresent();
    }

    /**
     * Returns the current amount of Auction points
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Increases the number of Auction points
     */
    public void addPoints(GameView view, Point p, int amount) {
        view.overlays.add(new ResourceOverlay(view, p, 0.24f, ColorScheme.GOLD.hex, amount).then(() -> {
            this.points += amount;
            view.hud.top.update(view.game);
        }));
    }

    /**
     * Returns the number of unclaimed Artifacts left in the pool
     */
    private int numberUnclaimedArtifacts() {
        int i = 0;
        for (Artifact a : this.artifacts) {
            if (!a.isClaimed()) {
                i++;
            }
        }
        return i;
    }

    /**
     * Instantiates the Menu that allows a human Player to participate in an auction
     */
    public Menu getAuctionBuyInMenu(GameView view) {
        final int price = this.getBuyInCost(view.game.human.gold);
        final ListNode node = new ListNode()
                .add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)))
                .add(new HeaderNode(view.av, "Artifact Auction"))
                .add(new TextNode(view.av, String.format("Pay %d gold to participate in the auction?", price)))
                .add(new RowNode().add(new ButtonNode(view.av, "Yes", () -> {
                    String error = "You have no vaults with items to bargain with";
                    Set<Point> vaults = view.game.getVaultBuildings(view.game.human);
                    if (vaults.size() == 0) {
                        this.auction.get().doNotAddBidder();
                        view.hud.logger.error(error);
                        view.hud.popups.complete();
                        return;
                    }
                    view.hud.popups.setDisplay(false);
                    view.hud.logger.log("Please select a vault with items to bid");
                    view.selector.select(vaults, error, (Point p) -> {
                        this.auction.get().addBidder(view.game.human, p);
                        view.game.human.gold -= price;
                        view.hud.popups.complete();
                    });
                })).add(new ButtonNode(view.av, "No", () -> {
                    this.auction.get().doNotAddBidder();
                    view.hud.popups.complete();
                })));
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2),
                false, node);
    }

    /**
     * Instantiates the Menu that will appear after an Auction has concluded
     */
    public Menu getFollowUpMenu(GameView view, boolean firstIteration) {
        // Determine who won the Auction if this is the first iteration of the window
        if (firstIteration) {
            final Optional<Player> winner = this.auction.get().getWinner(view.game.world, this.random);
            final boolean humanPlayerWon = winner.map((Player p) -> p.isHumanPlayer()).orElse(false);
            for (Player p : view.game.getAllPlayers()) {
                if (winner.map((Player p1) -> p.equals(p1)).orElse(false)) {
                    p.getFate().handleEvent(view, new Events.WonAuctionEvent(p)).execute();
                } else {
                    p.getFate().handleEvent(view, new Events.LostAuctionEvent(p)).execute();
                }
            }
            if (humanPlayerWon) {
                view.game.human.auctionChips++;
            } else {
                winner.ifPresent((Player p) -> ((CompPlayer) p).wishlist.artifacts.doAfterAuction());
                return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(),
                        Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2), false,
                        new ListNode().add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)))
                                .add(new TextNode(view.av,
                                        winner.isPresent()
                                                ? "You did not win the auction"
                                                : "Nobody bid in this auction"))
                                .add(new ButtonNode(view.av, "Okay", () -> view.hud.popups.complete())));
            }
        }

        // Populate the Menu with ArtifactNodes if the human Player did win the Auction
        // (or if they have leftover auction chips)
        int a = 0;
        final int columns = 3;
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        final ListNode node = new ListNode()
                .add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.setDisplay(false)))
                .add(new TextNode(view.av,
                        "You outbid the other players and won the auction! You may click on an Artifact to buy with your auction chips or wait until the next time you win an auction."))
                .add(new ButtonNode(view.av, "Buy an artifact next time", () -> view.hud.popups.complete()));
        while (a < artifacts.size()) {
            final RowNode row = new RowNode().setColumns(columns);
            for (int b = 0; b < columns && a < this.artifacts.size();) {
                final Artifact artifact = this.artifacts.get(a);
                row.add(new ArtifactNode(view.av, artifact, Optional.of(() -> {
                    if (view.game.human.auctionChips >= artifact.chips) {
                        view.game.human.auctionChips -= artifact.chips;
                        artifact.claim(view, view.game.human);
                        view.hud.popups.complete();
                        if (view.game.human.auctionChips > 0) {
                            view.hud.popups.add(this.getFollowUpMenu(view, false));
                        }
                    } else {
                        view.hud.logger.error("You need more auction chips");
                    }
                })));
                a++;
                b++;
            }
            node.add(row);
        }
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), width, false, node);
    }

    /**
     * Returns a Menu to show the human Player which Artifacts they've purchased
     */
    public Menu getArtifactsMenu(GameView view, Optional<Player> owner) {
        int a = 0;
        final List<Artifact> artifacts = owner.map((Player p) -> p.artifacts).orElse(this.artifacts);
        final int columns = 3;
        final int width = Coords.SIZE.x - (Mechanics.MENU_MARGIN * 2);
        final ListNode node = new ListNode().add(new RowNode()
                .add(new NakedButtonNode(view.av, "x", () -> view.hud.popups.complete()))
                .add(new HeaderNode(view.av, owner.isPresent() ? "Your Artifacts" : "All Artifacts"))
                .add(new HelperNode(view.av,
                        "Artifacts are powerful abilities that you can unlock in auctions. An auction occurs every time the auction points bar fills up. If you win an auction then you can either claim an artifact right away or save up auction chips to buy more powerful artifacts later."))
                .add(new ButtonNode(view.av, owner.isPresent() ? "Show all artifacts" : "Show your artifacts",
                        () -> view.hud.popups.replaceUnrequired(this.getArtifactsMenu(view,
                                owner.isPresent() ? Optional.empty() : Optional.of(view.game.human))))))
                .add(new SpacerNode());
        if (artifacts.size() == 0) {
            node.add(new TextNode(view.av, "There are no artifacts to display"));
        } else {
            while (a < artifacts.size()) {
                final RowNode row = new RowNode().setColumns(columns);
                for (int b = 0; b < columns && a < artifacts.size();) {
                    final Artifact artifact = artifacts.get(a);
                    row.add(new ArtifactNode(view.av, artifact, Optional.empty()));
                    a++;
                    b++;
                }
                node.add(row);
                node.add(new SpacerNode(false));
            }
        }
        return new Menu(Mechanics.MENU_MARGIN, view.hud.top.getHeight(), width, false, node);
    }
}
