/**
 * References:
 * https://raw.githubusercontent.com/libgdx/libgdx/refs/heads/master/gdx/res/com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl
 * http://www.lighthouse3d.com/tutorials/glsl-12-tutorial/toon-shader-version-ii/
 */
#ifdef GL_ES
#define MED mediump
precision mediump float;
#else
#define MED
#endif
uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;
uniform vec2 u_resolution;
varying MED vec2 v_diffuseUV;
varying vec3 v_lightDiffuse;
varying vec3 v_ambientLight;
varying float v_opacity;
varying vec3 v_normal;

// Return true if the current texture location is close to an edge (different alpha value)
// This function checks for edges within distance d
bool outline(float d) {
    float dx = d / u_resolution.x;
    float dy = d / u_resolution.y;
    float a = texture2D(u_diffuseTexture, v_diffuseUV).a;
    return texture2D(u_diffuseTexture, v_diffuseUV + vec2(-dx, dy)).a != a ||
        texture2D(u_diffuseTexture, v_diffuseUV + vec2(-dx, -dy)).a != a ||
        texture2D(u_diffuseTexture, v_diffuseUV + vec2(dx, dy)).a != a ||
        texture2D(u_diffuseTexture, v_diffuseUV + vec2(dx, -dy)).a != a;
}

void main() {
    vec3 normal = v_normal;
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    if (outline(2.0)) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, diffuse.a);
    } else {
        float intensity = dot(v_lightDiffuse + v_ambientLight, normalize(normal));
        if (intensity == 0.0) {
            gl_FragColor = vec4(0.0, 0.0, 0.0, diffuse.a);
        } else {
            gl_FragColor = diffuse;
        }
    }
    gl_FragColor.a *= v_opacity;
}
