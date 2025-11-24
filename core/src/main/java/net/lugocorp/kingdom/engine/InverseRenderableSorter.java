package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;

/**
 * This RenderableSorter reverses the DefaultRenderableSorter results. We use
 * this for the unit preview menu because everything is inverted for some reason
 * (i.e. face normals and sorting)
 */
public class InverseRenderableSorter extends DefaultRenderableSorter {

    /**
     * Causes the results of DefaultRenderableSorter to come back reversed
     */
    @Override
    public int compare(final Renderable o1, final Renderable o2) {
        return -super.compare(o1, o2);
    }
}
