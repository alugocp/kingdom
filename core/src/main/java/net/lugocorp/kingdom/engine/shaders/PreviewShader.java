package net.lugocorp.kingdom.engine.shaders;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class PreviewShader implements Shader {
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private Matrix4 matrix;
    private int u_projViewTrans;
    private int u_diffuseUVTransform;
    private int u_diffuseTexture;
    private int u_opacity;

    /** {@inheritdoc} */
    @Override
    public void init() {
        String fragment = Gdx.files.internal("shaders/preview/fragment.glsl").readString();
        String vertex = Gdx.files.internal("shaders/preview/vertex.glsl").readString();
        this.program = new ShaderProgram(vertex, fragment);
        if (!this.program.isCompiled()) {
            throw new GdxRuntimeException(this.program.getLog());
        }
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.program.dispose();
    }

    /**
     * Sets whether or not we should render models at night
     */
    public void setProjViewMatrix(Matrix4 matrix) {
        this.matrix = matrix;
    }

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projViewTrans, this.matrix);
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        TextureAttribute diffuse = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
        this.program.setUniformi(this.u_diffuseTexture, this.context.textureBinder.bind(diffuse.textureDescription));
        this.program.setUniformf(this.u_diffuseUVTransform, diffuse.offsetU, diffuse.offsetV, diffuse.scaleU,
                diffuse.scaleV);
        BlendingAttribute blend = (BlendingAttribute) renderable.material.get(BlendingAttribute.Type);
        if (blend != null) {
            this.context.setBlending(true, blend.sourceFunction, blend.destFunction);
            this.program.setUniformf(this.u_opacity, blend.opacity);
        }
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
