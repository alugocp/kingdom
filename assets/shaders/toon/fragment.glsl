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
uniform bool u_includeGlyphTexture;
uniform sampler2D u_glyphTexture;
uniform sampler2D u_diffuseTexture;
uniform sampler2D u_borderTexture1;
uniform sampler2D u_borderTexture2;
uniform vec4 u_diffuseUVTransform;
uniform vec4 u_diffuseColor;
uniform vec2 u_resolution;
uniform float u_timerMax;
uniform float u_timer;
uniform float u_nighttime;
uniform float u_opacity;
uniform int u_visibility;
uniform int u_tileBorder;
uniform bool u_wave;
varying MED vec2 v_diffuseUV;
varying vec3 v_lightDiffuse;
varying vec3 v_ambientLight;
varying vec3 v_normal;
const int HALF_VISIBILITY = 1;
const int NO_VISIBILITY = 0;

// Return true if the current texture location is close to an edge (different alpha value)
// This function checks for edges within distance d
bool outline(float d) {
    float dx = d / u_resolution.x;
    float dy = d / u_resolution.y;
    float a = texture2D(u_diffuseTexture, v_diffuseUV).a;
    return texture2D(u_diffuseTexture, v_diffuseUV + vec2(-dx, -dy)).a != a ||
        texture2D(u_diffuseTexture, v_diffuseUV + vec2(dx, dy)).a != a;
}

void main() {
    vec3 normal = v_normal;
    float halfTimer = u_timerMax / 2.0;
    bool isTopFace = normal == vec3(0.0, 1.0, 0.0);

    // Face black color for outlines or fog of war
    if (u_visibility == NO_VISIBILITY || outline(2.0)) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    // Calculate light intensity
    float intensity = dot(v_lightDiffuse + v_ambientLight, normalize(normal));

    // Set texture color and opacity
    if (intensity == 0.0) {
        // Make the texture black under low light
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        // Grab texture color at coordinate
        vec2 texCoords = v_diffuseUV;
        if (u_wave && isTopFace) {
            // Wave Tiles should oscillate slightly
            texCoords.x += u_diffuseUVTransform.z * 0.025 * (2.0 * (abs(u_timer - halfTimer) / halfTimer) - 1.0);
        }
        gl_FragColor = texture2D(u_diffuseTexture, texCoords) * u_diffuseColor;

        // Make the color darker if it's night time
        if (u_nighttime > 0.0 || u_visibility == HALF_VISIBILITY) {
            float coeff = 0.8;
            if (u_nighttime > 0.0 && u_visibility == HALF_VISIBILITY) {
                coeff = 0.4;
            }
            mat4 darker;
            darker[0] = vec4(coeff, 0.0, 0.0, 0.0);
            darker[1] = vec4(0.0, coeff, 0.0, 0.0);
            darker[2] = vec4(0.0, 0.0, coeff, 0.0);
            darker[3] = vec4(0.0, 0.0, 0.0, 1.0);
            gl_FragColor = darker * gl_FragColor;
        }
    }
    gl_FragColor.a *= u_opacity;

    // Glyph texture logic
    if (isTopFace && u_includeGlyphTexture) {
        float diff = 0.0;
        float beat = 0.07 * abs(u_timer - halfTimer) / halfTimer;
        vec4 glyph = texture2D(u_glyphTexture, v_diffuseUV);
        if (glyph.x > 0.0) {
            diff = 0.1;
        }
        gl_FragColor.x -= (gl_FragColor.x * glyph.x * beat) + diff;
        gl_FragColor.y -= (gl_FragColor.y * glyph.y * beat) + diff;
        gl_FragColor.z -= (gl_FragColor.z * glyph.z * beat) + diff;
    }

    // Player border logic
    if (isTopFace && u_tileBorder > 0) {
        float bx = v_diffuseUV.x * 64.0 / 19.0;
        float by = v_diffuseUV.y * 64.0 / 18.0;
        int border = u_tileBorder;

        // bot right
        if (border >= 32) {
            gl_FragColor += texture2D(u_borderTexture2, vec2(1.0 - bx, by));
            border -= 32;
        }

        // bot left
        if (border >= 16) {
            gl_FragColor += texture2D(u_borderTexture2, vec2(1.0 - bx, 1.0 - by));
            border -= 16;
        }

        // top right
        if (border >= 8) {
            gl_FragColor += texture2D(u_borderTexture2, vec2(bx, by));
            border -= 8;
        }

        // top left
        if (border >= 4) {
            gl_FragColor += texture2D(u_borderTexture2, vec2(bx, 1.0 - by));
            border -= 4;
        }

        // right
        if (border >= 2) {
            gl_FragColor += texture2D(u_borderTexture1, vec2(bx, by));
            border -= 2;
        }

        // left
        if (border >= 1) {
            gl_FragColor += texture2D(u_borderTexture1, vec2(bx, 1.0 - by));
            // border -= 1;
        }
    }
}
