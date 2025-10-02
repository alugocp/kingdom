package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Building;
import net.lugocorp.kingdom.game.model.Tile;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.ui.views.GameView;
import net.lugocorp.kingdom.utils.code.Lambda;
import net.lugocorp.kingdom.utils.math.Hexagons;
import net.lugocorp.kingdom.utils.math.Point;
import net.lugocorp.kingdom.utils.math.Rect;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {
    private static final int BIOME_UNIT_SIZE = 10;
    private static final int NUM_PLAYERS = 2;

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(GameView view, WorldGenOptions worldGenOpts, Consumer<Integer> progress) {
        Random r = new Random(worldGenOpts.seed);
        Game g = view.game;

        // Set up coasts (if any)
        final boolean coastTop = r.nextBoolean();
        final boolean coastBot = r.nextBoolean();
        final boolean coastLeft = r.nextBoolean();
        final boolean coastRight = r.nextBoolean();

        // Set default biome
        Biome mainBiome = Biome.GRASS;
        // TODO uncomment this when there are more units designed for these Biomes
        /*
         * float biomeSelection = r.nextFloat(); if (biomeSelection < 0.05) { mainBiome
         * = Biome.SAND; } else if (biomeSelection < 0.1) { mainBiome = Biome.ROCK; }
         * else if (biomeSelection < 0.15) { mainBiome = Biome.SNOW; }
         */

        // Set Biome seeds
        final Biome[] otherBiomes = this.getDifferentBiomes(mainBiome);
        final int biomeSeedsW = worldGenOpts.size.w / WorldGenerator.BIOME_UNIT_SIZE;
        final int biomeSeedsH = worldGenOpts.size.h / WorldGenerator.BIOME_UNIT_SIZE;
        final Point[][] biomeSeedOffsets = new Point[biomeSeedsW][biomeSeedsH];
        final Biome[][] biomeSeeds = new Biome[biomeSeedsW][biomeSeedsH];
        for (int b = 0; b < biomeSeedsH; b++) {
            boolean isHorizontalCoast = (coastTop && b == 0) || (coastBot && b == biomeSeedsH - 1);
            for (int a = 0; a < biomeSeedsW; a++) {
                biomeSeedOffsets[a][b] = new Point(r.nextInt(WorldGenerator.BIOME_UNIT_SIZE),
                        r.nextInt(WorldGenerator.BIOME_UNIT_SIZE));

                // Coastal Biome seeds
                if (isHorizontalCoast || (coastLeft && a == 0) || (coastRight && a == biomeSeedsW - 1)) {
                    biomeSeeds[a][b] = Biome.WATER;
                    continue;
                }

                // Non-coastal Biome seeds
                final float biomeDecision = r.nextFloat();
                if (biomeDecision < 0.15) {
                    biomeSeeds[a][b] = this.randomValue(r, otherBiomes);
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
        Rect biomeSeedGrid = new Rect(0, 0, biomeSeedsW, biomeSeedsH);
        List<List<Point>> startingPoints = new ArrayList<List<Point>>();
        for (int a = 0; a < 4; a++) {
            startingPoints.add(new ArrayList<Point>());
        }
        for (int a = 0; a < worldGenOpts.size.w; a++) {
            for (int b = 0; b < worldGenOpts.size.h; b++) {
                Point focalSeed = new Point(a / WorldGenerator.BIOME_UNIT_SIZE, b / WorldGenerator.BIOME_UNIT_SIZE);
                float closestSeedDistance = 1000f;
                Biome closestSeed = mainBiome;

                // Find the closest Biome seed
                for (int dx = -1; dx < 2; dx++) {
                    for (int dy = -1; dy < 2; dy++) {
                        Point seed = new Point(focalSeed.x + dx, focalSeed.y + dy);
                        if (seed.x >= 0 && seed.y >= 0 && seed.x < biomeSeedsW && seed.y < biomeSeedsH) {
                            Point offset = biomeSeedOffsets[seed.x][seed.y];
                            float d = this.distance((seed.x * WorldGenerator.BIOME_UNIT_SIZE) + offset.x,
                                    (seed.y * WorldGenerator.BIOME_UNIT_SIZE) + offset.y, a, b);
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
                    startingPoints.get(this.getStartingPointRegion(worldGenOpts, a, b)).add(new Point(a, b));
                }
            }
        }
        progress.accept(60);

        // Randomly place feature patches around the World
        String[] oneOffFeatures = {"Vault", "Mine"};
        for (int a = 0; a < 50; a++) {
            Point p = new Point(r.nextInt(worldGenOpts.size.w), r.nextInt(worldGenOpts.size.h));
            String terrain = g.world.getTile(p.x, p.y).get().name;
            Optional<String> building = Optional.empty();
            int radiusRange = 0;

            // Determine the appropriate feature
            if (r.nextFloat() < 0.8) {
                if (terrain.equals(Biome.GRASS.terrain)) {
                    building = Optional.of(r.nextBoolean() ? "Forest" : "Meadow");
                    radiusRange = 5;
                }
                if (terrain.equals(Biome.SAND.terrain)) {
                    if (r.nextBoolean()) {
                        building = Optional.of("Shrubland");
                        radiusRange = 3;
                    } else {
                        building = Optional.of("Oasis");
                        radiusRange = 1;
                    }
                }
                if (terrain.equals(Biome.SNOW.terrain)) {
                    building = Optional.of("Taiga");
                    radiusRange = 3;
                }
                if (terrain.equals(Biome.ROCK.terrain)) {
                    building = Optional.of("Mountain");
                    radiusRange = 1;
                }
            } else {
                building = terrain.equals(Biome.WATER.terrain)
                        ? Optional.empty()
                        : Optional.of(this.randomValue(r, oneOffFeatures));
            }

            // Place the feature in a given radius
            if (building.isPresent()) {

                // If we're setting Buildings in a ring (and the center of our Oasis doesn't
                // already have a building)
                if (radiusRange > 0
                        && !(building.get().equals("Oasis") && g.world.getTile(p).get().building.isPresent())) {
                    Set<Point> area = Hexagons.getNeighbors(p, r.nextInt(radiusRange) + 1);
                    for (Point p1 : area) {
                        Optional<Tile> t = g.world.getTile(p1);
                        // If the Tile is of the intended terrain and there is no building
                        if (t.isPresent() && t.get().name.equals(terrain) && !t.get().building.isPresent()) {
                            startingPoints.get(this.getStartingPointRegion(worldGenOpts, p1.x, p1.y)).remove(p1);
                            g.generator.building(building.get(), p1.x, p1.y).spawn(view);
                        }
                    }
                }
                if (!g.world.getTile(p).get().building.isPresent()) {
                    // Make the center Tile Water if we're generating an Oasis
                    if (building.get().equals("Oasis")) {
                        g.generator.tile("Water", p.x, p.y).spawn(view);
                    } else {
                        startingPoints.get(this.getStartingPointRegion(worldGenOpts, p.x, p.y)).remove(p);
                        g.generator.building(building.get(), p.x, p.y).spawn(view);
                    }
                }
            }
        }
        progress.accept(90);

        // Set Players in the World
        int startingPointIndex = 0;
        for (int a = 0; a < WorldGenerator.NUM_PLAYERS; a++) {
            // Skip over quadrants that have no starting point candidates
            while (startingPointIndex < startingPoints.size() && startingPoints.get(startingPointIndex).size() == 0) {
                startingPointIndex++;
            }
            if (startingPointIndex == startingPoints.size()) {
                // throw new Exception("No space for all the players to spawn");
                System.err.println("No space for all the players to spawn");
                System.exit(1);
            }

            // Pick a starting point from the available candidates and spawn a Vault
            final Point p = this.randomValue(r, startingPoints.get(startingPointIndex));
            final Player player = a == 0 ? g.human : g.addComputerPlayer(view, a);
            // TODO figure out how we can avoid using hard-coded labels here (what if the
            // mods define other values?)
            final Building b = g.generator.building("Vault", p.x, p.y);
            for (int c = 0; c < 5; c++) {
                b.items.ifPresent((Inventory i) -> i.add(g.generator.item("Apple")));
            }
            b.spawn(view);
            g.getInitialUnit(view, player, p.x, p.y).spawn(view);
            startingPoints.get(startingPointIndex++).remove(p);
        }
        progress.accept(95);

        // Set Glyphs in the World
        for (List<Point> quadrant : startingPoints) {
            for (Point p : quadrant) {
                if (r.nextBoolean()) {
                    g.world.getTile(p).get().setGlyph(Optional.of(Lambda.random(GlyphCategory.class)));
                }
            }
        }
        progress.accept(100);
    }

    /**
     * Returns an integer corresponding to which area of the map a Player can spawn
     * in
     */
    private int getStartingPointRegion(WorldGenOptions w, int x, int y) {
        return (x > w.size.w / 2 ? 1 : 0) + (y > w.size.h / 2 ? 1 : 0);
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
    private <T> T randomValue(Random r, T[] array) {
        return array[r.nextInt(array.length)];
    }

    /**
     * Return a random element from the given List
     */
    private <T> T randomValue(Random r, List<T> array) {
        return array.get(r.nextInt(array.size()));
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
     * Enum for different terrain types
     */
    private static enum Biome {
        GRASS("Grass"), WATER("Water"), SAND("Sand"), ROCK("Rock"), SNOW("Snow");
        private final String terrain;

        private Biome(String terrain) {
            this.terrain = terrain;
        }
    }
}
