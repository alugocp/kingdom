package net.lugocorp.kingdom.engine.shaders;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * This ShaderProgram handles Menu elements
 */
public class ElementShader extends ShaderProgram {
    private static final Matrix4 identity = new Matrix4();
    public static final int DEFAULT_MODE = 0;
    public static final int GRAY_MODE = 1;
    public static final int BRIGHT_MODE = 2;

    public ElementShader() {
        super(Gdx.files.internal("shaders/element/vertex.glsl").readString(),
                Gdx.files.internal("shaders/element/fragment.glsl").readString());
        if (!this.isCompiled()) {
            throw new GdxRuntimeException(this.getLog());
        }
        this.setUniformi("u_mode", ElementShader.DEFAULT_MODE);
    }

    /**
     * Sets the current render mode for this ShaderProgram
     */
    public ElementShader setMode(int mode) {
        this.setUniformi("u_mode", mode);
        return this;
    }

    /**
     * Disables the recolor function
     */
    public ElementShader originalColor() {
        this.setUniformMatrix("u_transform", ElementShader.identity);
        return this;
    }

    /**
     * Sets the transformation matrix for arbitrary recoloring
     */
    public ElementShader recolor(Matrix4 transform) {
        this.setUniformMatrix("u_transform", transform);
        return this;
    }
}
