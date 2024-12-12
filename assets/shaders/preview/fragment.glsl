#ifdef GL_ES
#define MED mediump
precision mediump float;
#else
#define MED
#endif
uniform sampler2D u_diffuseTexture;
varying MED vec2 v_diffuseUV;
varying float v_opacity;

void main() {
    gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV);
    gl_FragColor.a *= v_opacity;
}
