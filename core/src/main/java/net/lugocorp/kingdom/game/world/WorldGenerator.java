package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.content.Labels;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.model.Unit;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {
    private static final int BIOME_UNIT_SIZE = 5;
    private final WorldGenOptions worldGenOpts;
    private final Random rand;
    private final World world;

    public WorldGenerator(World world, WorldGenOptions worldGenOpts) {
        this.rand = new Random(worldGenOpts.seed);
        this.worldGenOpts = worldGenOpts;
        this.world = world;
    }

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(GameView view, Consumer<Integer> progress) {
        final Game g = view.game;
        this.world.init(this.worldGenOpts);

        // Set up coasts (if any)
        final boolean coastTop = this.rand.nextBoolean();
        final boolean coastBot = this.rand.nextBoolean();
        final boolean coastLeft = (this.worldGenOpts.size.getArea() >= WorldSize.MEDIUM.getArea()
                || !(coastTop && coastBot)) && this.rand.nextBoolean();
        final boolean coastRight = (this.worldGenOpts.size.getArea() >= WorldSize.MEDIUM.getArea()
                || !((coastTop ? 1 : 0) + (coastBot ? 1 : 0) + (coastLeft ? 1 : 0) == 2)) && this.rand.nextBoolean();

        // Set default biome
        Biome mainBiome = Biome.GRASS;
        // TODO uncomment this when there are more units designed for these Biomes
        /*
         * float biomeSelection = this.rand.nextFloat(); if (biomeSelection < 0.05) {
         * mainBiome = Biome.SAND; } else if (biomeSelection < 0.1) { mainBiome =
         * Biome.ROCK; } else if (biomeSelection < 0.15) { mainBiome = Biome.SNOW; }
         */

        // Set Biome seeds
        final Biome[] otherBiomes = this.getDifferentBiomes(mainBiome);
        final int biomeSeedsW = this.worldGenOpts.size.w / WorldGenerator.BIOME_UNIT_SIZE;
        final int biomeSeedsH = this.worldGenOpts.size.h / WorldGenerator.BIOME_UNIT_SIZE;
        final Biome[][] biomeSeeds = new Biome[biomeSeedsW][biomeSeedsH];
        final Point[][] biomeSeedCenters = this.getSpacedPoints(WorldGenerator.BIOME_UNIT_SIZE,
                WorldGenerator.BIOME_UNIT_SIZE, 1, 1, (Point p) -> true);
        for (int b = 0; b < biomeSeedsH; b++) {
            final boolean isHorizontalCoast = (coastTop && b == 0) || (coastBot && b == biomeSeedsH - 1);
            for (int a = 0; a < biomeSeedsW; a++) {
                // Coastal Biome seeds
                if (isHorizontalCoast || (coastLeft && a == 0) || (coastRight && a == biomeSeedsW - 1)) {
                    biomeSeeds[a][b] = Biome.WATER;
                    continue;
                }

                // Non-coastal Biome seeds
                final float biomeDecision = this.rand.nextFloat();
                if (biomeDecision < 0.05) {
                    biomeSeeds[a][b] = this.randomValue(otherBiomes);
                } else if (biomeDecision < 0.4) {
                    final Biome prev = (a == 0) ? mainBiome : biomeSeeds[a - 1][b];
                    biomeSeeds[a][b] = prev;
                } else {
                    biomeSeeds[a][b] = mainBiome;
                }
            }
        }
        progress.accept(10);

        // Fill out Tiles in the World
        final Set<Point> buildingPoints = new HashSet<>();
        for (int a = 0; a < this.worldGenOpts.size.w; a++) {
            for (int b = 0; b < this.worldGenOpts.size.h; b++) {
                final Point focalSeed = new Point(a / WorldGenerator.BIOME_UNIT_SIZE,
                        b / WorldGenerator.BIOME_UNIT_SIZE);
                float closestSeedDistance = 1000f;
                Biome closestSeed = mainBiome;

                // Find the closest Biome seed
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        Point seed = new Point(focalSeed.x + dx, focalSeed.y + dy);
                        if (seed.x >= 0 && seed.y >= 0 && seed.x < biomeSeedsW && seed.y < biomeSeedsH) {
                            final float d = this.distance(biomeSeedCenters[seed.x][seed.y].x,
                                    biomeSeedCenters[seed.x][seed.y].y, a, b);
                            if (d < closestSeedDistance) {
                                closestSeed = biomeSeeds[seed.x][seed.y];
                                closestSeedDistance = d;
                            }
                        }
                    }
                }

                // Generate a Tile based on the closest Biome seed's terrain and
                // add it to the starting point candidates if it's not water
                g.generator.tile(closestSeed.terrain, a, b).spawn(view);
                if (closestSeed.terrain != Biome.WATER.terrain) {
                    buildingPoints.add(new Point(a, b));
                }
            }
        }
        progress.accept(60);

        // Place Towers
        for (Point p : this
                .collapseGridToSet(this.getSpacedPoints(6, 6, 1, 1, (Point p) -> buildingPoints.contains(p)))) {
            final Tower t = g.generator.tower(p.x, p.y);
            t.items.ifPresent((Inventory i) -> {
                for (int a = 0; a < i.getMax(); a++) {
                    i.add(g.generator.item(Labels.item_apple));
                }
            });
            buildingPoints.remove(p);
            g.towers.add(t);
        }

        // Initialize and spawn the Towers
        this.calculateTowerDomains(g);
        for (Tower tower : g.towers) {
            tower.spawn(view);
        }

        // Place Mines
        for (Point p : this
                .collapseGridToSet(this.getSpacedPoints(8, 8, 2, 2, (Point p) -> buildingPoints.contains(p)))) {
            g.generator.building(Labels.building_mine, p.x, p.y).spawn(view);
            buildingPoints.remove(p);
        }

        // Place Marketplaces
        for (Point p : this
                .collapseGridToSet(this.getSpacedPoints(8, 8, 2, 2, (Point p) -> buildingPoints.contains(p)))) {
            g.generator.building(Labels.building_marketplace, p.x, p.y).spawn(view);
            buildingPoints.remove(p);
        }

        // Place other content (Buildings and Patrons) around the World
        final Set<String> patrons = g.events.patron.getStratifiers();
        final int maxBuildings = buildingPoints.size();
        while (buildingPoints.size() > 0) {
            // Choose a Point to spawn the content
            final Point p = this.randomValue(buildingPoints);
            buildingPoints.remove(p);

            // Decide what content to spawn
            final int percent = this.rand.nextInt(1000);
            if (percent <= 50) {
                // Spawn a Patron (5% chance) if any are available
                if (patrons.size() > 0) {
                    final String patron = this.randomValue(patrons);
                    g.generator.patron(patron, p.x, p.y).spawn(view);
                    patrons.remove(patron);
                }
            } else if (percent <= 53) {
                // Spawn a Cache (0.3% chance)
                g.generator.building(Labels.building_cache, p.x, p.y).spawn(view);
            } else if (percent <= 55) {
                // Spawn a Healing Fountain (0.2% chance)
                g.generator.building(Labels.building_healing_fountain, p.x, p.y).spawn(view);
            } else if (percent <= 105) {
                // Spawn a natural Building (5% chance)
                final String terrain = g.world.getTile(p.x, p.y).get().name;
                Optional<String> building = Optional.empty();
                int radiusRange = 1;

                // Forests/Meadows on Grass Tiles
                if (terrain.equals(Biome.GRASS.terrain)) {
                    building = Optional.of(this.rand.nextBoolean() ? Labels.building_forest : Labels.building_meadow);
                    radiusRange = 3;
                }

                // Oasis/Shrubland on Sand Tiles
                if (terrain.equals(Biome.SAND.terrain)) {
                    if (this.rand.nextBoolean()) {
                        building = Optional.of(Labels.building_shrubland);
                        radiusRange = 2;
                    } else {
                        building = Optional.of(Labels.building_oasis);
                    }
                }

                // Taiga on Snow Tiles
                if (terrain.equals(Biome.SNOW.terrain)) {
                    building = Optional.of(Labels.building_taiga);
                }

                // Mountains on Rock Tiles
                if (terrain.equals(Biome.ROCK.terrain)) {
                    building = Optional.of(Labels.building_mountain);
                }

                // Actually spawn the Buildings if one was selected
                if (building.isPresent()) {
                    final Set<Point> area = Hexagons.getNeighbors(p, this.rand.nextInt(radiusRange) + 1);
                    for (Point p1 : area) {
                        // If the Tile exists, has the intended terrain, and there is no building yet
                        if (buildingPoints.contains(p1) && g.world.getTile(p1).get().name.equals(terrain)) {
                            g.generator.building(building.get(), p1.x, p1.y).spawn(view);
                            buildingPoints.remove(p1);
                        }
                    }

                    // Spawn the central Building (or water if the Building is an Oasis)
                    if (building.get().equals(Labels.building_oasis)) {
                        final Tower center = g.world.getTile(p).get().getDomainCenter();
                        final Tile t = g.generator.tile(Labels.tile_water, p.x, p.y);
                        t.setDomainCenter(center);
                        t.spawn(view);
                    } else {
                        g.generator.building(building.get(), p.x, p.y).spawn(view);
                    }
                }
            }

            // Update progress as we generate content
            progress.accept(60 + (int) Math.floor(40 * (maxBuildings - buildingPoints.size()) / (float) maxBuildings));
        }

        // Spawn all Players and their initial Units
        final List<Player> players = g.getAllPlayers();
        for (Tower tower : this.randomSubset(g.towers, players.size())) {
            final Player player = players.remove(0);
            final Point p = tower.getPoint();
            final Unit u = g.getInitialUnit(view, player, p.x, p.y);
            u.spawn(view);
            g.setLeader(view, tower, player);
            tower.vision.set(view, player, tower, p);
            g.towers.add(tower);
        }

        progress.accept(100);
    }

    /**
     * Returns all Biomes that aren't the given Biome
     */
    private Biome[] getDifferentBiomes(Biome b) {
        int a = 0;
        final Biome[] others = new Biome[Biome.values().length - 1];
        for (Biome o : Biome.values()) {
            if (o != b) {
                others[a++] = o;
            }
        }
        return others;
    }

    /**
     * Calculates the domains for each Tower
     */
    private void calculateTowerDomains(Game g) {
        final Point domainPoint = new Point(0, 0);
        final Map<Tower, Set<Point>> domains = new HashMap<>();
        for (Tower t : g.towers) {
            domains.put(t, new HashSet<Point>());
        }
        for (int a = 0; a < this.worldGenOpts.size.w; a++) {
            for (int b = 0; b < this.worldGenOpts.size.h; b++) {
                Tower best = null;
                int shortest = 0;
                domainPoint.set(a, b);
                for (Tower t : g.towers) {
                    int dist = domainPoint.distance(t.getPoint());
                    if (best == null || dist < shortest) {
                        shortest = dist;
                        best = t;
                    }
                }
                domains.get(best).add(domainPoint.copy());
            }
        }
        for (Tower tower : g.towers) {
            tower.domain.init(g.world, tower, domains.get(tower));
        }
    }

    /**
     * Returns a grid of relatively evenly spaced Points
     */
    private Point[][] getSpacedPoints(int cellW, int cellH, int marginW, int marginH,
            Function<Point, Boolean> isValid) {
        final int resultW = this.worldGenOpts.size.w / cellW;
        final int resultH = this.worldGenOpts.size.h / cellH;
        final Point[][] points = new Point[resultW][resultH];
        for (int a = 0; a < resultW; a++) {
            for (int b = 0; b < resultH; b++) {
                final int minX = Math.min((a * cellW) + marginW, this.worldGenOpts.size.w);
                final int minY = Math.min((b * cellH) + marginH, this.worldGenOpts.size.h);
                final int maxX = Math.min((a * cellW) + cellW - marginW, this.worldGenOpts.size.w);
                final int maxY = Math.min((b * cellH) + cellH - marginH, this.worldGenOpts.size.h);
                final Set<Point> possibilities = new HashSet<>();
                for (int x = minX; x < maxX; x++) {
                    for (int y = minY; y < maxY; y++) {
                        final Point p = new Point(x, y);
                        if (isValid.apply(p)) {
                            possibilities.add(p);
                        }
                    }
                }
                points[a][b] = possibilities.size() > 0 ? this.randomValue(possibilities) : null;
            }
        }
        return points;
    }

    /**
     * Takes a 2D grid of Points and puts the non-null values into a set
     */
    private Set<Point> collapseGridToSet(Point[][] grid) {
        final Set<Point> points = new HashSet<>();
        for (int a = 0; a < grid.length; a++) {
            for (int b = 0; b < grid[a].length; b++) {
                if (grid[a][b] != null) {
                    points.add(grid[a][b]);
                }
            }
        }
        return points;
    }

    /**
     * Implementation of the distance function
     */
    private float distance(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * Return a random element from the given array
     */
    private <T> T randomValue(T[] array) {
        return array[this.rand.nextInt(array.length)];
    }

    /**
     * Return a random element from the given List
     */
    private <T> T randomValue(List<T> array) {
        return array.get(this.rand.nextInt(array.size()));
    }

    /**
     * Return a random element from the given Set
     */
    private <T> T randomValue(Set<T> s) {
        final int index = this.rand.nextInt(s.size());
        final Iterator<T> iterator = s.iterator();
        for (int a = 0; a < index; a++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * Returns a random subset of the given set
     */
    private <T> Set<T> randomSubset(Set<T> s, int size) {
        if (s.size() < size) {
            throw new RuntimeException("The set is too small to return a random subset of the requested size");
        }
        final Set<T> result = new HashSet<>();
        for (int a = 0; a < size; a++) {
            final T t = this.randomValue(s);
            result.add(t);
            s.remove(t);
        }
        return result;
    }
}
