/**
 * References:
 * https://raw.githubusercontent.com/libgdx/libgdx/refs/heads/master/gdx/res/com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl
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
varying float v_opacity;
varying vec3 v_normal;

void main() {
    vec3 normal = v_normal;
    gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    gl_FragColor.a *= v_opacity;
}
