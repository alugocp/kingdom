/**
 * References:
 * https://raw.githubusercontent.com/libgdx/libgdx/refs/heads/master/gdx/res/com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl
 */
attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;
uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;
uniform vec4 u_diffuseUVTransform;
uniform mat3 u_normalMatrix;
varying vec2 v_diffuseUV;
varying vec3 v_normal;

void main() {
    v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
    v_normal = normalize(u_normalMatrix * a_normal);
}
