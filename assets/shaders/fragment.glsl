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

// Calculates the intensity of the input color
float intensity(vec4 c){
    return sqrt((c.x * c.x) + (c.y * c.y) + (c.z * c.z));
}

// Implements a Sobel filter
float sobel(vec2 center, float dx, float dy) {
    float tl = intensity(texture2D(u_diffuseTexture, center + vec2(-dx, dy)));
    float l = intensity(texture2D(u_diffuseTexture, center + vec2(-dx, 0)));
    float bl = intensity(texture2D(u_diffuseTexture, center + vec2(-dx, -dy)));
    float t = intensity(texture2D(u_diffuseTexture, center + vec2(0, dy)));
    float b = intensity(texture2D(u_diffuseTexture, center + vec2(0, -dy)));
    float tr = intensity(texture2D(u_diffuseTexture, center + vec2(dx, dy)));
    float r = intensity(texture2D(u_diffuseTexture, center + vec2(dx, 0)));
    float br = intensity(texture2D(u_diffuseTexture, center + vec2(dx, -dy)));

    // Multiply by the Sobel matrices
    float x = tl + (2.0 * l) + bl - tr - (2.0 * r) - br;
    float y = -tl - (2.0 * t) - tr + bl + (2.0 * b) + br;
    return sqrt((x * x) + (y * y));
}

void main() {
    vec3 normal = v_normal;
    float c = sobel(v_diffuseUV, 1.0 / u_resolution.x, 1.0 / u_resolution.y);
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;
    if (c > 0.5) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, diffuse.a);
    } else {
        float intensity = dot(v_lightDiffuse + v_ambientLight, normalize(normal));
        if (intensity <= 0.2) {
            gl_FragColor = vec4(0.0, 0.0, 0.0, diffuse.a);
        } else {
            gl_FragColor = diffuse;
        }
    }
    gl_FragColor.a *= v_opacity;
}
