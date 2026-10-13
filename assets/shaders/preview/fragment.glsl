#ifdef GL_ES
#define MED mediump
precision mediump float;
#else
#define MED
#endif
uniform sampler2D u_diffuseTexture;
uniform vec4 u_diffuseColor;
uniform float u_opacity;
varying MED vec2 v_diffuseUV;
varying vec3 v_normal;
varying float v_opacity;

void main() {
    gl_FragColor = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    gl_FragColor.a *= u_opacity;

    // Make the color darker for shading
    float shadow = dot(v_normal, normalize(vec3(0.0, 1.0, 0.0)));
    if (shadow < 0.5) {
        float coeff = shadow < 0.25 ? 0.75 : 0.9;
        gl_FragColor.x *= coeff;
        gl_FragColor.y *= coeff;
        gl_FragColor.z *= coeff;
    }
}
