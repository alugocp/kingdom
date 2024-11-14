#ifdef GL_ES 
precision mediump float;
#endif
varying vec2 v_texCoord0;
uniform sampler2D sample;

void main() {
    gl_FragColor = texture2D(sample, v_texCoord0);
}
