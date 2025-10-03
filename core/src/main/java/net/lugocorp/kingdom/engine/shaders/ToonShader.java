package net.lugocorp.kingdom.engine.shaders;
import net.lugocorp.kingdom.engine.assets.TextureLoader;
import net.lugocorp.kingdom.engine.userdata.CoordUserData;
import net.lugocorp.kingdom.engine.userdata.TileUserData;
import net.lugocorp.kingdom.ui.selection.TileSelector;
import net.lugocorp.kingdom.utils.math.Coords;
import net.lugocorp.kingdom.utils.math.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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
    private static final int TIMER_MAX = 12000;
    private final FrameBuffer frameBuffer;
    private Optional<TileSelector> tileSelector = Optional.empty();
    private Optional<TextureLoader> textures = Optional.empty();
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private boolean nighttime = false;

    public ToonShader(FrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    // Shader uniforms
    private int u_normalsTexture;
    private int u_directionalLight;
    private int u_ambientLight;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_normalMatrix;
    private int u_includeGlyphTexture;
    private int u_borderTexture1;
    private int u_borderTexture2;
    private int u_borderTexture3;
    private int u_borderTexture4;
    private int u_glyphTexture;
    private int u_diffuseUVTransform;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_opacity;
    private int u_resolution;
    private int u_nighttime;
    private int u_selection;
    private int u_vision;
    private int u_lightOutline;
    private int u_domainBorder;
    private int u_distanceBorder;
    private int u_borderColor;
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
        this.u_normalsTexture = this.program.getUniformLocation("u_normalsTexture");
        this.u_directionalLight = this.program.getUniformLocation("u_directionalLight");
        this.u_ambientLight = this.program.getUniformLocation("u_ambientLight");
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_normalMatrix = this.program.getUniformLocation("u_normalMatrix");
        this.u_includeGlyphTexture = this.program.getUniformLocation("u_includeGlyphTexture");
        this.u_borderTexture1 = this.program.getUniformLocation("u_borderTexture1");
        this.u_borderTexture2 = this.program.getUniformLocation("u_borderTexture2");
        this.u_borderTexture3 = this.program.getUniformLocation("u_borderTexture3");
        this.u_borderTexture4 = this.program.getUniformLocation("u_borderTexture4");
        this.u_glyphTexture = this.program.getUniformLocation("u_glyphTexture");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
        this.u_resolution = this.program.getUniformLocation("u_resolution");
        this.u_nighttime = this.program.getUniformLocation("u_nighttime");
        this.u_selection = this.program.getUniformLocation("u_selection");
        this.u_vision = this.program.getUniformLocation("u_vision");
        this.u_lightOutline = this.program.getUniformLocation("u_lightOutline");
        this.u_domainBorder = this.program.getUniformLocation("u_domainBorder");
        this.u_distanceBorder = this.program.getUniformLocation("u_distanceBorder");
        this.u_borderColor = this.program.getUniformLocation("u_borderColor");
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

    /**
     * Sets this Shader's TileSelector instance
     */
    public void setTileSelector(TileSelector tileSelector) {
        this.tileSelector = Optional.of(tileSelector);
    }

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.bind();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
        this.program.setUniformf(this.u_nighttime, this.nighttime ? 1f : 0f);
        this.program.setUniformf(this.u_timer, (float) (System.currentTimeMillis() % ToonShader.TIMER_MAX));
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        // Set normals texture (to render outlines)
        Texture normals = this.frameBuffer.getColorBufferTexture();
        this.program.setUniformi(this.u_normalsTexture, this.context.textureBinder.bind(normals));

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
        if (renderable.userData != null && renderable.userData instanceof TileUserData) {
            TileUserData data = (TileUserData) renderable.userData;
            this.program.setUniformi(this.u_vision, data.collapseVision());
            this.program.setUniformi(this.u_selection, Math.min(data.selection, 3));
            this.program.setUniformf(this.u_borderColor, data.borderColor);
            this.program.setUniformi(this.u_wave, data.wave ? 1 : 0);
            this.program.setUniformi(this.u_includeGlyphTexture, 0);
            this.program.setUniformi(this.u_distanceBorder, 0);
            this.program.setUniformi(this.u_domainBorder, 0);
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
            if (this.textures.isPresent()) {
                // Player borders
                if (data.borders > 0) {
                    Optional<TextureDescriptor> tdesc1 = this.textures.get().getTextureDescriptor("game/border1");
                    Optional<TextureDescriptor> tdesc2 = this.textures.get().getTextureDescriptor("game/border2");
                    if (tdesc1.isPresent() && tdesc2.isPresent()) {
                        this.program.setUniformi(this.u_borderTexture1, this.context.textureBinder.bind(tdesc1.get()));
                        this.program.setUniformi(this.u_borderTexture2, this.context.textureBinder.bind(tdesc2.get()));
                        this.program.setUniformi(this.u_tileBorder, data.borders);
                    }
                }

                // Domain borders
                if (data.domainBorders > 0) {
                    Optional<TextureDescriptor> tdesc3 = this.textures.get().getTextureDescriptor("game/border3");
                    Optional<TextureDescriptor> tdesc4 = this.textures.get().getTextureDescriptor("game/border4");
                    if (tdesc3.isPresent() && tdesc4.isPresent()) {
                        this.program.setUniformi(this.u_borderTexture3, this.context.textureBinder.bind(tdesc3.get()));
                        this.program.setUniformi(this.u_borderTexture4, this.context.textureBinder.bind(tdesc4.get()));
                        this.program.setUniformi(this.u_domainBorder, data.domainBorders);
                    }
                }

                // Distance borders
                if (data.distanceBorders > 0) {
                    Optional<TextureDescriptor> tdesc3 = this.textures.get().getTextureDescriptor("game/border3");
                    Optional<TextureDescriptor> tdesc4 = this.textures.get().getTextureDescriptor("game/border4");
                    if (tdesc3.isPresent() && tdesc4.isPresent()) {
                        this.program.setUniformi(this.u_borderTexture3, this.context.textureBinder.bind(tdesc3.get()));
                        this.program.setUniformi(this.u_borderTexture4, this.context.textureBinder.bind(tdesc4.get()));
                        this.program.setUniformi(this.u_distanceBorder, data.distanceBorders);
                    }
                }
            }
        } else {
            this.program.setUniformi(this.u_wave, 0);
            this.program.setUniformi(this.u_selection, 0);
            this.program.setUniformi(this.u_tileBorder, 0);
            this.program.setUniformi(this.u_domainBorder, 0);
            this.program.setUniformi(this.u_distanceBorder, 0);
            this.program.setUniformi(this.u_vision, 2);
            this.program.setUniformi(this.u_includeGlyphTexture, 0);

            // Make the outline change color if this Unit / Building is being hovered over
            if (renderable.userData != null && renderable.userData instanceof CoordUserData) {
                final CoordUserData data = (CoordUserData) renderable.userData;
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
