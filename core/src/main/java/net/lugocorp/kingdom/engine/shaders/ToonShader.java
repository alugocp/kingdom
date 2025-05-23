package net.lugocorp.kingdom.engine.shaders;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import net.lugocorp.kingdom.engine.render.RenderableUserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.util.Optional;
import com.badlogic.gdx.graphics.Texture;

/**
 * This class interfaces with GLSL shader code to give the game its aesthetic
 * References:
 * https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/graphics/g3d/shaders/DefaultShader.java
 */
public class ToonShader implements Shader {
    private Optional<TextureLoader> textures = Optional.empty();
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private boolean nighttime = false;
    private int dt = 0;

    // Shader uniforms
    private int u_directionalLight;
    private int u_ambientLight;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_normalMatrix;
    private int u_includeGlyphTexture;
    private int u_glyphTexture;
    private int u_diffuseUVTransform;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_opacity;
    private int u_resolution;
    private int u_nighttime;
    private int u_deltatime;

    /** {@inheritdoc} */
    @Override
    public void init() {
        String fragment = Gdx.files.internal("shaders/toon/fragment.glsl").readString();
        String vertex = Gdx.files.internal("shaders/toon/vertex.glsl").readString();
        this.program = new ShaderProgram(vertex, fragment);
        if (!this.program.isCompiled()) {
            throw new GdxRuntimeException(this.program.getLog());
        }
        this.u_directionalLight = this.program.getUniformLocation("u_directionalLight");
        this.u_ambientLight = this.program.getUniformLocation("u_ambientLight");
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_normalMatrix = this.program.getUniformLocation("u_normalMatrix");
        this.u_includeGlyphTexture = this.program.getUniformLocation("u_includeGlyphTexture");
        this.u_glyphTexture = this.program.getUniformLocation("u_glyphTexture");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
        this.u_resolution = this.program.getUniformLocation("u_resolution");
        this.u_nighttime = this.program.getUniformLocation("u_nighttime");
        this.u_deltatime = this.program.getUniformLocation("u_deltatime");
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
     * Sets how many milliseconds have passed since the last render
     */
    public void setDeltaTime(int dt) {
        this.dt = dt;
    }

    /**
     * Sets this Shader's TextureLoader instance
     */
    public void setTextureLoader(TextureLoader textures) {
        this.textures = Optional.of(textures);
    }

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
        this.program.setUniformf(this.u_nighttime, this.nighttime ? 1f : 0f);
        this.program.setUniformf(this.u_deltatime, (float) this.dt);
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        // Set lighting uniforms (we only set one in GameView)
        DirectionalLightsAttribute lights = (DirectionalLightsAttribute) renderable.environment
                .get(DirectionalLightsAttribute.Type);
        this.program.setUniformf(this.u_directionalLight, lights.lights.first().direction);
        ColorAttribute ambient = (ColorAttribute) renderable.environment.get(ColorAttribute.AmbientLight);
        this.program.setUniformf(this.u_ambientLight, ambient.color);
        this.program.setUniformf(this.u_resolution, Coords.SIZE.x, Coords.SIZE.y);

        // Set object uniforms
        Matrix3 normal = new Matrix3();
        this.program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);
        this.program.setUniformMatrix(this.u_normalMatrix, normal.set(renderable.worldTransform).inv().transpose());

        // Set material uniforms
        TextureAttribute diffuse = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
        this.program.setUniformi(this.u_diffuseTexture, this.context.textureBinder.bind(diffuse.textureDescription));
        this.program.setUniformf(this.u_diffuseUVTransform, diffuse.offsetU, diffuse.offsetV, diffuse.scaleU,
                diffuse.scaleV);
        ColorAttribute color = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        this.program.setUniformf(this.u_diffuseColor, color.color);
        BlendingAttribute blend = (BlendingAttribute) renderable.material.get(BlendingAttribute.Type);
        if (blend != null) {
            this.context.setBlending(true, blend.sourceFunction, blend.destFunction);
            this.program.setUniformf(this.u_opacity, blend.opacity);
        }

        // Set overlay uniforms
        this.program.setUniformi(this.u_includeGlyphTexture, 0);
        if (this.textures.isPresent() && renderable.userData != null) {
            RenderableUserData data = (RenderableUserData) renderable.userData;

            // Glyph texture
            if (data.glyph.isPresent()) {
                Optional<TextureDescriptor> tdesc = this.textures.get().getTextureDescriptor("ui/glyph");
                if (tdesc.isPresent()) {
                    this.program.setUniformi(this.u_includeGlyphTexture, 1);
                    this.program.setUniformi(this.u_glyphTexture, this.context.textureBinder.bind(tdesc.get()));
                }
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
        renderable.meshPart.render(this.program);
    }

    /** {@inheritdoc} */
    @Override
    public void end() {
        this.program.end();
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
