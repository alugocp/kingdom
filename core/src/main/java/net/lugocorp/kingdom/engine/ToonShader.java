package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ToonShader implements Shader {
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_normalMatrix;
    private int u_diffuseUVTransform;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_opacity;

    @Override
    public void init() {
        String fragment = Gdx.files.internal("shaders/fragment.glsl").readString();
        String vertex = Gdx.files.internal("shaders/vertex.glsl").readString();
        this.program = new ShaderProgram(vertex, fragment);
        if (!this.program.isCompiled()) {
            throw new GdxRuntimeException(this.program.getLog());
        }
        this.u_projViewTrans = this.program.getUniformLocation("u_projViewTrans");
        this.u_worldTrans = this.program.getUniformLocation("u_worldTrans");
        this.u_normalMatrix = this.program.getUniformLocation("u_normalMatrix");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
        this.u_opacity = this.program.getUniformLocation("u_opacity");
    }

    @Override
    public void dispose() {
        this.program.dispose();
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        this.camera = camera;
        this.context = context;
        this.program.begin();
        this.program.setUniformMatrix(this.u_projViewTrans, camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
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
        renderable.meshPart.render(this.program);
    }

    @Override
    public void end() {
        this.program.end();
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return true;
    }
}
