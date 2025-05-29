package net.lugocorp.kingdom.engine.shaders;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import net.lugocorp.kingdom.engine.render.userdata.TileUserData;
import net.lugocorp.kingdom.utils.math.Coords;
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
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;
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
    private static final int TIMER_MAX = 12000;
    private Optional<TextureLoader> textures = Optional.empty();
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private boolean nighttime = false;

    // Shader uniforms
    private int u_directionalLight;
    private int u_ambientLight;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_normalMatrix;
    private int u_includeGlyphTexture;
    private int u_borderTexture1;
    private int u_borderTexture2;
    private int u_glyphTexture;
    private int u_diffuseUVTransform;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_opacity;
    private int u_resolution;
    private int u_nighttime;
    private int u_selection;
    private int u_visibility;
    private int u_tileBorder;
    private int u_timer;
    private int u_wave;

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
        this.u_borderTexture1 = this.program.getUniformLocation("u_borderTexture1");
        this.u_borderTexture2 = this.program.getUniformLocation("u_borderTexture2");
        this.u_glyphTexture = this.program.getUniformLocation("u_glyphTexture");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
        this.u_resolution = this.program.getUniformLocation("u_resolution");
        this.u_nighttime = this.program.getUniformLocation("u_nighttime");
        this.u_selection = this.program.getUniformLocation("u_selection");
        this.u_visibility = this.program.getUniformLocation("u_visibility");
        this.u_tileBorder = this.program.getUniformLocation("u_tileBorder");
        this.u_timer = this.program.getUniformLocation("u_timer");
        this.u_wave = this.program.getUniformLocation("u_wave");
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

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
        this.program.setUniformf(this.u_nighttime, this.nighttime ? 1f : 0f);
        this.program.setUniformf(this.u_timer, (float) (System.currentTimeMillis() % ToonShader.TIMER_MAX));
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

        // Set special shader data
        if (renderable.userData == null) {
            this.program.setUniformi(this.u_wave, 0);
            this.program.setUniformi(this.u_selection, 0);
            this.program.setUniformi(this.u_tileBorder, 0);
            this.program.setUniformi(this.u_visibility, 2);
            this.program.setUniformi(this.u_includeGlyphTexture, 0);
        } else if (renderable.userData instanceof TileUserData) {
            TileUserData data = (TileUserData) renderable.userData;
            this.program.setUniformi(this.u_visibility, data.collapseVisibility());
            this.program.setUniformi(this.u_selection, Math.min(data.selection, 3));
            this.program.setUniformi(this.u_wave, data.wave ? 1 : 0);
            this.program.setUniformi(this.u_includeGlyphTexture, 0);
            this.program.setUniformi(this.u_tileBorder, 0);

            // Glyph texture
            if (this.textures.isPresent() && data.glyph.isPresent()) {
                Optional<TextureDescriptor> tdesc = this.textures.get().getTextureDescriptor(data.glyph.get().icon);
                if (tdesc.isPresent()) {
                    this.program.setUniformi(this.u_includeGlyphTexture, 1);
                    this.program.setUniformi(this.u_glyphTexture, this.context.textureBinder.bind(tdesc.get()));
                }
            }

            // Border textures
            if (this.textures.isPresent() && data.borders > 0) {
                Optional<TextureDescriptor> tdesc1 = this.textures.get().getTextureDescriptor("ui/border1");
                Optional<TextureDescriptor> tdesc2 = this.textures.get().getTextureDescriptor("ui/border2");
                if (tdesc1.isPresent() && tdesc2.isPresent()) {
                    this.program.setUniformi(this.u_borderTexture1, this.context.textureBinder.bind(tdesc1.get()));
                    this.program.setUniformi(this.u_borderTexture2, this.context.textureBinder.bind(tdesc2.get()));
                    this.program.setUniformi(this.u_tileBorder, data.borders);
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
