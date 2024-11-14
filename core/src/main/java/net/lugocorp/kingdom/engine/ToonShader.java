package net.lugocorp.kingdom.engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ToonShader implements Shader {
    private ShaderProgram program;
    private RenderContext context;
    private Camera camera;
    private int u_diffuseTexture;
    private int u_diffuseColor;
    private int u_projViewTrans;
    private int u_worldTrans;
    private int u_diffuseUVTransform;

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
        this.u_diffuseTexture = this.program.getUniformLocation("u_diffuseTexture");
        this.u_diffuseUVTransform = this.program.getUniformLocation("u_diffuseUVTransform");
        this.u_diffuseColor = this.program.getUniformLocation("u_diffuseColor");
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
        this.program.setUniformMatrix("u_projViewTrans", camera.combined);
    }

    @Override
    public void render(Renderable renderable) {
        this.program.setUniformMatrix("u_worldTrans", renderable.worldTransform);
        renderable.meshPart.render(this.program);
        TextureAttribute diffuse = (TextureAttribute) renderable.material.get(TextureAttribute.Diffuse);
        this.program.setUniformi("u_diffuseTexture", this.context.textureBinder.bind(diffuse.textureDescription));
        this.program.setUniformf("u_diffuseUVTransform", diffuse.offsetU, diffuse.offsetV, diffuse.scaleU,
                diffuse.scaleV);
        ColorAttribute color = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
        this.program.setUniformf("u_diffuseColor", color.color);
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
