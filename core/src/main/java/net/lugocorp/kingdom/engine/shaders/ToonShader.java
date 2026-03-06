package net.lugocorp.kingdom.engine.shaders;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.engine.userdata.TileUserData;
import net.lugocorp.kingdom.math.Coords;
import net.lugocorp.kingdom.math.Point;
import net.lugocorp.kingdom.ui.selection.TileSelector;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Optional;

/**
 * This class interfaces with GLSL shader code to give the game its aesthetic
 * References:
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g3d/shaders/DefaultShader.java
 */
public class ToonShader implements Shader {
    public final FrameBuffer frameBuffer = new FrameBuffer(Format.RGBA8888, Coords.SIZE.x * 2, Coords.SIZE.y * 2, true);
    private Optional<TileSelector> tileSelector = Optional.empty();
    private Optional<TextureLoader> textures = Optional.empty();
    private ShaderProgram program;
    private RenderContext context;
    private boolean nighttime = false;

    // Shader uniforms
    private int u_normalsTexture;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_opacity;
    private int u_nighttime;
    private int u_vision;
    private int u_lightOutline;
    private int u_blackout;

    /** {@inheritdoc} */
    @Override
    public void init() {
        String fragment = Gdx.files.internal("shaders/toon/fragment.glsl").readString();
        String vertex = Gdx.files.internal("shaders/toon/vertex.glsl").readString();
        this.program = new ShaderProgram(vertex, fragment);
        if (!this.program.isCompiled()) {
            throw new GdxRuntimeException(this.program.getLog());
        }
        this.u_normalsTexture = this.program.getUniformLocation("u_normalsTexture");
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
        this.u_nighttime = this.program.getUniformLocation("u_nighttime");
        this.u_vision = this.program.getUniformLocation("u_vision");
        this.u_lightOutline = this.program.getUniformLocation("u_lightOutline");
        this.u_blackout = this.program.getUniformLocation("u_blackout");
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.program.dispose();
    }

    /**
     * Sets whether or not we should render models at night
     */
    public void setNighttime(boolean nighttime) {
        this.nighttime = nighttime;
    }

    /**
     * Sets this Shader's TextureLoader instance
     */
    public void setTextureLoader(TextureLoader textures) {
        this.textures = Optional.of(textures);
    }

    /**
     * Sets this Shader's TileSelector instance
     */
    public void setTileSelector(TileSelector tileSelector) {
        this.tileSelector = Optional.of(tileSelector);
    }

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.context = context;
        this.program.bind();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
        this.program.setUniformf(this.u_nighttime, this.nighttime ? 1f : 0f);
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        // Set normals texture (to render outlines)
        Texture normals = this.frameBuffer.getColorBufferTexture();
        this.program.setUniformi(this.u_normalsTexture, this.context.textureBinder.bind(normals));

        // Set object uniforms
        this.program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);

        // Set material uniforms
        TextureAttribute diffuse = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
        this.program.setUniformi(this.u_diffuseTexture, this.context.textureBinder.bind(diffuse.textureDescription));
        ColorAttribute color = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        this.program.setUniformf(this.u_diffuseColor, color.color);
        BlendingAttribute blend = (BlendingAttribute) renderable.material.get(BlendingAttribute.Type);
        if (blend != null) {
            this.context.setBlending(true, blend.sourceFunction, blend.destFunction);
            this.program.setUniformf(this.u_opacity, blend.opacity);
        }

        // Set special shader data
        if (renderable.userData != null && renderable.userData instanceof TileUserData) {
            TileUserData data = (TileUserData) renderable.userData;
            this.program.setUniformi(this.u_vision, data.collapseVision());
            this.program.setUniformi(this.u_blackout, 0);
        } else {
            this.program.setUniformi(this.u_vision, 2);
            this.program.setUniformi(this.u_blackout, 0);

            // Make the outline change color if this Unit / Building is being hovered over
            if (renderable.userData != null && renderable.userData instanceof CoordUserData) {
                final CoordUserData data = (CoordUserData) renderable.userData;
                this.program.setUniformi(this.u_vision, data.isLowVisibility() ? 1 : 2);
                this.program.setUniformi(this.u_lightOutline,
                        this.tileSelector.flatMap((TileSelector ts) -> ts.getHovered())
                                .map((Point p) -> p.equals(data.point)).orElse(false) ? 1 : 0);
            }
        }

        // Set context and render
        int cull = GL20.GL_BACK;
        int depth = GL20.GL_LEQUAL;
        float depthNear = 0f;
        float depthFar = 1f;
        boolean depthMask = true;
        for (Attribute attr : renderable.material) {
            final long t = attr.type;
            if ((t & IntAttribute.CullFace) == IntAttribute.CullFace) {
                cull = ((IntAttribute) attr).value;
            } else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
                DepthTestAttribute test = (DepthTestAttribute) attr;
                depth = test.depthFunc;
                depthNear = test.depthRangeNear;
                depthFar = test.depthRangeFar;
                depthMask = test.depthMask;
            }
        }
        this.context.setCullFace(cull);
        this.context.setDepthTest(depth, depthNear, depthFar);
        this.context.setDepthMask(depthMask);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
        Gdx.gl.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
        renderable.meshPart.render(this.program);
    }

    /** {@inheritdoc} */
    @Override
    public void end() {
    }

    /** {@inheritdoc} */
    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    /** {@inheritdoc} */
    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }
}
