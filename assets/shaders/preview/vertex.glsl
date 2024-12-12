attribute vec3 a_position;
attribute vec2 a_texCoord0;
uniform mat4 u_projViewTrans;
uniform vec4 u_diffuseUVTransform;
uniform float u_opacity;
varying vec2 v_diffuseUV;
varying float v_opacity;

void main() {
    v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    gl_Position = u_projViewTrans * vec4(a_position, 1.0);
    v_opacity = u_opacity;
}
