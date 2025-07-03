package net.lugocorp.kingdom.engine.shaders;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.GL20;

/**
 * This class implements the 3D model outline References:
 * https://github.com/VictorGordan/opengl-tutorials/blob/main/YoutubeOpenGL%2015%20-%20Stencil%20Buffer/Main.cpp
 */
public class OutlineShader implements Shader {
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private int u_projViewTrans;
    private int u_normalMatrix;
    private int u_worldTrans;

    /** {@inheritdoc} */
    @Override
    public void init() {
        String fragment = Gdx.files.internal("shaders/outline/fragment.glsl").readString();
        String vertex = Gdx.files.internal("shaders/outline/vertex.glsl").readString();
        this.program = new ShaderProgram(vertex, fragment);
        if (!this.program.isCompiled()) {
            throw new GdxRuntimeException(this.program.getLog());
        }
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_normalMatrix = this.program.getUniformLocation("u_normalMatrix");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
    }

    /** {@inheritdoc} */
    @Override
    public void dispose() {
        this.program.dispose();
    }

    /** {@inheritdoc} */
    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        Matrix3 normal = new Matrix3();
        this.program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);
        this.program.setUniformMatrix(this.u_normalMatrix, normal.set(renderable.worldTransform).inv().transpose());

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
