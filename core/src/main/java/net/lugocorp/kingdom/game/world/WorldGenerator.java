package net.lugocorp.kingdom.game.world;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.glyph.GlyphCategory;
import net.lugocorp.kingdom.game.model.Tower;
import net.lugocorp.kingdom.game.player.Player;
import net.lugocorp.kingdom.game.properties.Inventory;
import net.lugocorp.kingdom.math.Hexagons;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.views.GameView;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;

/**
 * This class handles world generation logic
 */
public class WorldGenerator {
    private static final int BIOME_UNIT_SIZE = 5;

    /**
     * The main function that initiates world generation
     */
    public void generateWorld(GameView view, WorldGenOptions worldGenOpts, Consumer<Integer> progress) {
        final Random r = new Random(worldGenOpts.seed);
        final Game g = view.game;

        // Set up coasts (if any)
        final boolean coastTop = r.nextBoolean();
        final boolean coastBot = r.nextBoolean();
        final boolean coastLeft = (worldGenOpts.size.getArea() >= WorldSize.MEDIUM.getArea() || !(coastTop && coastBot))
                && r.nextBoolean();
        final boolean coastRight = (worldGenOpts.size.getArea() >= WorldSize.MEDIUM.getArea()
                || !((coastTop ? 1 : 0) + (coastBot ? 1 : 0) + (coastLeft ? 1 : 0) == 2)) && r.nextBoolean();

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
            final boolean isHorizontalCoast = (coastTop && b == 0) || (coastBot && b == biomeSeedsH - 1);
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
                if (biomeDecision < 0.05) {
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
        final Set<Point> buildingPoints = new HashSet<>();
        for (int a = 0; a < worldGenOpts.size.w; a++) {
            for (int b = 0; b < worldGenOpts.size.h; b++) {
                final Point focalSeed = new Point(a / WorldGenerator.BIOME_UNIT_SIZE,
                        b / WorldGenerator.BIOME_UNIT_SIZE);
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
                    buildingPoints.add(new Point(a, b));
                }
            }
        }
        progress.accept(60);

        // Place content (Players, Buildings, Patrons, and Glyphs) around the World
        int playersSpawned = 0;
        final int maxBuildings = buildingPoints.size();
        final Set<String> patrons = g.events.patron.getStratifiers();
        while (buildingPoints.size() > 0) {
            // Choose a Point to spawn the content
            final Point p = this.randomValue(r, buildingPoints);
            buildingPoints.remove(p);

            // Place Players with priority
            if (playersSpawned < g.getAllPlayers().size()) {
                final Player player = playersSpawned == 0 ? g.human : g.comps.get(playersSpawned - 1);
                final Tower t = g.generator.tower(p.x, p.y);
                for (int a = 0; a < 5; a++) {
                    t.items.ifPresent((Inventory i) -> i.add(g.generator.item("Apple")));
                }
                t.spawn(view);
                g.getInitialUnit(view, player, p.x, p.y).spawn(view);
                playersSpawned++;
            } else {
                // Spawn non-Player content
                final int percent = r.nextInt(1000);
                if (percent <= 50 && patrons.size() > 0) {
                    // Spawn a Patron (5% chance)
                    final String patron = this.randomValue(r, patrons);
                    g.generator.patron(patron, p.x, p.y).spawn(view);
                    patrons.remove(patron);
                } else if (percent <= 450) {
                    // Spawn a Glyph (40% chance)
                    g.world.getTile(p).get().setGlyph(Optional.of(this.randomValue(r, GlyphCategory.values())));
                } else if (percent <= 453) {
                    // Spawn a Vault (0.3% chance)
                    g.generator.building("Vault", p.x, p.y).spawn(view);
                } else if (percent <= 456) {
                    // Spawn a Mine (0.3% chance)
                    g.generator.building("Mine", p.x, p.y).spawn(view);
                } else if (percent <= 458) {
                    // Spawn a Healing Fountain (0.2% chance)
                    g.generator.building("Healing Fountain", p.x, p.y).spawn(view);
                } else if (percent <= 508) {
                    // Spawn a Building (5% chance)
                    final String terrain = g.world.getTile(p.x, p.y).get().name;
                    Optional<String> building = Optional.empty();
                    int radiusRange = 1;

                    // Forests/Meadows on Grass Tiles
                    if (terrain.equals(Biome.GRASS.terrain)) {
                        building = Optional.of(r.nextBoolean() ? "Forest" : "Meadow");
                        radiusRange = 3;
                    }

                    // Oasis/Shrubland on Sand Tiles
                    if (terrain.equals(Biome.SAND.terrain)) {
                        if (r.nextBoolean()) {
                            building = Optional.of("Shrubland");
                            radiusRange = 2;
                        } else {
                            building = Optional.of("Oasis");
                        }
                    }

                    // Taiga on Snow Tiles
                    if (terrain.equals(Biome.SNOW.terrain)) {
                        building = Optional.of("Taiga");
                    }

                    // Mountains on Rock Tiles
                    if (terrain.equals(Biome.ROCK.terrain)) {
                        building = Optional.of("Mountain");
                    }

                    // Actually spawn the Buildings if one was selected
                    if (building.isPresent()) {
                        final Set<Point> area = Hexagons.getNeighbors(p, r.nextInt(radiusRange) + 1);
                        for (Point p1 : area) {
                            // If the Tile exists, has the intended terrain, and there is no building yet
                            if (buildingPoints.contains(p1) && g.world.getTile(p1).get().name.equals(terrain)) {
                                g.generator.building(building.get(), p1.x, p1.y).spawn(view);
                                buildingPoints.remove(p1);
                            }
                        }

                        // Spawn the central Building (or water if the Building is an Oasis)
                        if (building.get().equals("Oasis")) {
                            g.generator.tile("Water", p.x, p.y).spawn(view);
                        } else {
                            g.generator.building(building.get(), p.x, p.y).spawn(view);
                        }
                    }
                }
            }

            // Update progress as we generate content
            progress.accept(60 + (int) Math.floor(40 * (maxBuildings - buildingPoints.size()) / (float) maxBuildings));
        }
        progress.accept(100);
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
     * Return a random element from the given Set
     */
    private <T> T randomValue(Random r, Set<T> s) {
        final int index = r.nextInt(s.size());
        final Iterator<T> iterator = s.iterator();
        for (int a = 0; a < index; a++) {
            iterator.next();
        }
        return iterator.next();
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
}
