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
uniform float u_opacity;
uniform vec3 u_directionalLight;
uniform vec3 u_ambientLight;
varying vec3 v_ambientLight;
varying vec3 v_lightDiffuse;
varying vec2 v_diffuseUV;
varying float v_opacity;
varying vec3 v_normal;

void main() {
    v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
    v_normal = normalize(u_normalMatrix * a_normal);
    v_ambientLight = u_ambientLight;
    v_opacity = u_opacity;

    // Directional light
    v_lightDiffuse = vec3(1.0, 1.0, 1.0)
        * clamp(dot(v_normal, -u_directionalLight), 0.0, 1.0);
}
