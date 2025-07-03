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
uniform sampler2D u_normalsTexture;
uniform vec4 u_diffuseUVTransform;
uniform vec4 u_diffuseColor;
uniform vec4 u_borderColor;
uniform vec2 u_resolution;
uniform float u_timer;
uniform float u_nighttime;
uniform float u_opacity;
uniform int u_visibility;
uniform int u_tileBorder;
uniform int u_selection;
uniform bool u_wave;
varying MED vec2 v_diffuseUV;
varying vec3 v_lightDiffuse;
varying vec3 v_ambientLight;
varying vec3 v_normal;
const int HALF_VISIBILITY = 1;
const int NO_VISIBILITY = 0;
const float OUTLINE_WIDTH = 3.0;

vec4 normalsTexSample(float x, float y) {
    return texture2D(u_normalsTexture, vec2(x / 1280.0, y / 960.0));
}

bool outline() {
    vec4 bg = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 center = normalsTexSample(gl_FragCoord.x, gl_FragCoord.y);
    if (center == bg) {
        return false;
    }
    vec4 top = normalsTexSample(gl_FragCoord.x, gl_FragCoord.y + OUTLINE_WIDTH);
    vec4 bot = normalsTexSample(gl_FragCoord.x, gl_FragCoord.y - OUTLINE_WIDTH);
    vec4 right = normalsTexSample(gl_FragCoord.x + OUTLINE_WIDTH, gl_FragCoord.y);
    vec4 left = normalsTexSample(gl_FragCoord.x - OUTLINE_WIDTH, gl_FragCoord.y);
    return top == bg || bot == bg || right == bg || left == bg;
}

// Changes the output color based on Tile borders
int checkBorderColor(int border, sampler2D tex, int thresh, float x, float y) {
    if (border >= thresh) {
        vec4 color = texture2D(tex, vec2(x, y));
        if (color.x > 0.0) {
            gl_FragColor = u_borderColor;
        }
        return thresh;
    }
    return 0;
}

void main() {
    // Return black color for fog of war
    if (u_visibility == NO_VISIBILITY || outline()) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    vec3 normal = v_normal;
    bool isTopFace = normal == vec3(0.0, 1.0, 0.0);

    // Make the texture black under low enough light
    float intensity = dot(v_lightDiffuse + v_ambientLight, normalize(normal));
    if (intensity == 0.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    // Grab texture color at coordinate
    vec2 texCoords = v_diffuseUV;
    if (u_wave && isTopFace) {
        // Wave Tiles should oscillate slightly
        texCoords.x += u_diffuseUVTransform.z * 0.0075 * ((2.0 * abs(mod(u_timer, 6000.0) - 3000.0) / 3000.0) - 1.0);
    }
    gl_FragColor = texture2D(u_diffuseTexture, texCoords) * u_diffuseColor;
    gl_FragColor.a *= u_opacity;

    // Glyph texture logic
    if (isTopFace && u_includeGlyphTexture) {
        float diff = 0.0;
        float beat = 0.07 * abs(mod(u_timer, 3000.0) - 1500.0) / 1500.0;
        vec4 glyph = texture2D(u_glyphTexture, v_diffuseUV);
        if (glyph.x > 0.0) {
            diff = 0.1;
        }
        gl_FragColor.x -= (gl_FragColor.x * glyph.x * beat) + diff;
        gl_FragColor.y -= (gl_FragColor.y * glyph.y * beat) + diff;
        gl_FragColor.z -= (gl_FragColor.z * glyph.z * beat) + diff;
    }

    // Tile selection logic
    if (u_selection > 0) {
        float coeff = 0.2 * float(u_selection);
        gl_FragColor.x += coeff;
        gl_FragColor.y += coeff;
        gl_FragColor.z += coeff;
    }

    // Player border logic
    if (isTopFace && u_tileBorder > 0) {
        float bx = v_diffuseUV.x * 64.0 / 19.0;
        float by = v_diffuseUV.y * 64.0 / 18.0;
        int border = u_tileBorder;
        border -= checkBorderColor(border, u_borderTexture2, 32, 1.0 - bx, by); // Bot right
        border -= checkBorderColor(border, u_borderTexture2, 16, 1.0 - bx, 1.0 - by); // Bot left
        border -= checkBorderColor(border, u_borderTexture2, 8, bx, by); // Top right
        border -= checkBorderColor(border, u_borderTexture2, 4, bx, 1.0 - by); // Top left
        border -= checkBorderColor(border, u_borderTexture1, 2, bx, by); // Right
        border -= checkBorderColor(border, u_borderTexture1, 1, bx, 1.0 - by); // Left
    }

    // Make the color darker if it's night time or half visibility
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
