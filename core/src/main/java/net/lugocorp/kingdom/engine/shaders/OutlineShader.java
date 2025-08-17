package net.lugocorp.kingdom.engine.shaders;
import net.lugocorp.kingdom.engine.render.userdata.CoordUserData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * This class implements the 3D model outline References:
 * https://github.com/VictorGordan/opengl-tutorials/blob/main/YoutubeOpenGL%2015%20-%20Stencil%20Buffer/Main.cpp
 */
public class OutlineShader implements Shader {
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_coordColor;

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
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_coordColor = this.program.getUniformLocation("u_coordColor");
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
        this.program.bind();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
    }

    /** {@inheritdoc} */
    @Override
    public void render(Renderable renderable) {
        this.program.setUniformMatrix(this.u_worldTrans, renderable.worldTransform);
        if (renderable.userData != null && renderable.userData instanceof CoordUserData) {
            CoordUserData data = (CoordUserData) renderable.userData;
            this.program.setUniformf(this.u_coordColor, ((data.point.x * 15) % 100) / 255f,
                    ((data.point.y * 15) % 100) / 255f, 1f, 1f);
        } else {
            this.program.setUniformf(this.u_coordColor, 1f, 1f, 1f, 1f);
        }

        // Culling and depth stuff
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
