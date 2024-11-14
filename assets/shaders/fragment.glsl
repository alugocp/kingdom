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
varying MED vec2 v_diffuseUV;
varying vec3 v_lightDiffuse;
varying vec3 v_ambientLight;
varying float v_opacity;
varying vec3 v_normal;

void main() {
    vec3 normal = v_normal;
    float intensity = dot(v_lightDiffuse + v_ambientLight, normalize(normal));
    gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    if (intensity <= 0.2) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, gl_FragColor.a);
    }
    gl_FragColor.a *= v_opacity;
}
