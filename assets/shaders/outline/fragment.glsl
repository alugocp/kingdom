#ifdef GL_ES
#define MED mediump
precision mediump float;
#else
#define MED
#endif
uniform vec4 u_coordColor;

void main() {
    gl_FragColor = vec4(u_coordColor.x, u_coordColor.y, gl_FragCoord.z, 1.0);
}