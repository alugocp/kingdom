package net.lugocorp.kingdom.game.mechanics;
import net.lugocorp.kingdom.game.Game;
import net.lugocorp.kingdom.game.model.Glyph;
import net.lugocorp.kingdom.game.model.Unit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class tracks all the Units that can be recruited by their Glyph(s)
 */
public class GlyphPools {
    private final Map<Glyph, List<String>> pools = new HashMap<>();

    /**
     * Initializes this object, ingests all registered Unit names in the Game and
     * their associated Glyphs
     */
    public void init(Game g) {
        for (Glyph glyph : Glyph.values()) {
            this.pools.put(glyph, new ArrayList<String>());
        }
        Unit u = new Unit("", 0, 0);
        for (String name : g.events.unit.getStratifiers()) {
            u.name = name;
            g.generator.unitOptimal(u);
            if (u.playable) {
                this.add(u);
            }
        }
    }

    /**
     * Returns the remaining Unit names in the pool of the given Glyph
     */
    public int remaining(Glyph g) {
        return this.pools.get(g).size();
    }

    /**
     * Retrieves a random Unit name by the given Glyph
     */
    public String[] random(Glyph g, int n) {
        int n1 = 0;
        int[] indices = new int[n];
        String[] names = new String[n];
        int max = this.pools.get(g).size();
        while (n1 < n) {
            int i = (int) Math.floor(Math.random() * max);
            int a;
            for (a = 0; a < n1; a++) {
                if (i == indices[a]) {
                    i++;
                    if (i == max) {
                        i = 0;
                        a = 0;
                    }
                }
                if (i < indices[a]) {
                    break;
                }
            }
            for (int b = n1; b > a; b--) {
                indices[b] = indices[b - 1];
            }
            names[n1++] = this.pools.get(g).get(i);
            indices[a] = i;
        }
        return names;
    }

    /**
     * Adds a Unit to the pools by their Glyph(s)
     */
    public void add(Unit u) {
        this.pools.get(u.glyph1).add(u.name);
        if (u.glyph2.isPresent()) {
            this.pools.get(u.glyph2.get()).add(u.name);
        }
    }

    /**
     * Removes a Unit from the pools by their Glyph(s)
     */
    public void remove(Unit u) {
        this.pools.get(u.glyph1).remove(u.name);
        if (u.glyph2.isPresent()) {
            this.pools.get(u.glyph2.get()).remove(u.name);
        }
    }
}
