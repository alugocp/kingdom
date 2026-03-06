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
uniform sampler2D u_pathTexture1;
uniform sampler2D u_pathTexture2;
uniform sampler2D u_pathDotTexture;
uniform sampler2D u_pathLabelsTexture;
uniform sampler2D u_borderTexture1;
uniform sampler2D u_borderTexture2;
uniform sampler2D u_borderTexture3;
uniform sampler2D u_borderTextureExt3;
uniform sampler2D u_borderTexture4;
uniform sampler2D u_borderTextureExt4;
uniform sampler2D u_borderTextureExt42;
uniform vec4 u_diffuseUVTransform;
uniform vec4 u_diffuseColor;
uniform vec4 u_borderColor;
uniform float u_timer;
uniform float u_nighttime;
uniform float u_opacity;
uniform int u_vision;
uniform int u_movePath;
uniform int u_pathLabel;
uniform int u_domainBorder;
uniform int u_domainBorderExtension;
uniform int u_tileBorder;
uniform int u_blackout;
uniform int u_hovered;
uniform int u_option;
uniform bool u_wave;
varying MED vec2 v_diffuseUV;
varying vec3 v_normal;
const int HALF_VISIBILITY = 1;
const int NO_VISIBILITY = 0;
const float OUTLINE_WIDTH = 3.0;

// (64.0 / 18.0) and (64.0 / 19.0) are special ratios based on the top face texture for tiles
const float TEX_RATIO_X = 64.0 / 18.0;
const float TEX_RATIO_Y = 64.0 / 19.0;

// Changes the output color based on Tile borders (for a single side)
int checkBorderColor(int border, vec4 color, sampler2D tex, int thresh, float x, float y) {
    if (border >= thresh) {
        vec4 value = texture2D(tex, vec2(x, y));
        if (value.a > 0.0) {
            gl_FragColor = color;
        }
        return thresh;
    }
    return 0;
}

// Handles all border-related logic
void applyBorder(int border, vec4 color, sampler2D texture1, sampler2D texture2) {
    if (border == 0) {
        return;
    }
    float bx = v_diffuseUV.x * TEX_RATIO_X;
    float by = v_diffuseUV.y * TEX_RATIO_Y;
    border -= checkBorderColor(border, color, texture2, 32, 1.0 - bx, by); // Bot right
    border -= checkBorderColor(border, color, texture2, 16, bx, by); // Bot left
    border -= checkBorderColor(border, color, texture2, 8, 1.0 - bx, 1.0 - by); // Top right
    border -= checkBorderColor(border, color, texture2, 4, bx, 1.0 - by); // Top left
    border -= checkBorderColor(border, color, texture1, 2, 1.0 - bx, by); // Right
    border -= checkBorderColor(border, color, texture1, 1, bx, by); // Left
}

// Extends smaller borders so that they connect aross tiles
void applyBorderExtensions(int extension, vec4 color) {
    if (extension == 0) {
        return;
    }
    float bx = v_diffuseUV.x * TEX_RATIO_X;
    float by = v_diffuseUV.y * TEX_RATIO_Y;
    extension -= checkBorderColor(extension, color, u_borderTextureExt4, 2048, bx, 1.0 - by); // Top left CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt4, 1024, bx, by); // Bot left CW
    extension -= checkBorderColor(extension, color, u_borderTextureExt42, 512, 1.0 - bx, 1.0 - by); // Top right CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt42, 256, 1.0 - bx, by); // Bot right CW
    extension -= checkBorderColor(extension, color, u_borderTextureExt3, 128, 1.0 - bx, 1.0 - by); // Right CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt3, 64, 1.0 - bx, by); // Right CW
    extension -= checkBorderColor(extension, color, u_borderTextureExt4, 32, 1.0 - bx, by); // Bot right CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt4, 16, 1.0 - bx, 1.0 - by); // Top right CW
    extension -= checkBorderColor(extension, color, u_borderTextureExt42, 8, bx, by); // Bot left CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt42, 4, bx, 1.0 - by); // Top left CW
    extension -= checkBorderColor(extension, color, u_borderTextureExt3, 2, bx, by); // Left CCW
    extension -= checkBorderColor(extension, color, u_borderTextureExt3, 1, bx, 1.0 - by); // Left CW
}

vec2 getPathLabelOffset(int n) {
    float x = 2.0;
    float y = 2.0;
    if (n >= 0 && n <= 9) {
        x = mod(float(n), 4.0);
        y = float(n / 4);
    }
    return vec2(x * 18.0, y * 18.0);
}

// Handles all path-related logic
void applyPath(vec4 color) {
    const float TEX_W = 72.0;
    const float TEX_H = 76.0;
    const float GLYPH = 18.0;
    applyBorder(u_movePath, color, u_pathTexture1, u_pathTexture2);
    if (u_pathLabel > 0) {
        float bx = v_diffuseUV.x * TEX_RATIO_X;
        float by = v_diffuseUV.y * TEX_RATIO_Y;

        // Render the dot texture
        vec4 value = texture2D(u_pathDotTexture, vec2(bx, by));
        if (value.a > 0.0) {
            gl_FragColor = color;
        }

        // Find the width of the path label over the dot
        int w = 1;
        if (u_pathLabel > 9) {
            w = 2;
        }
        if (u_pathLabel > 99) {
            w = 3;
        }

        // Check if the diffuse UV is within the borders for rendering a path label glyph
        float top = TEX_H / 2.0;
        float x = v_diffuseUV.x * TEX_W * TEX_W / 18.0;
        float y = v_diffuseUV.y * TEX_H * TEX_H / 19.0;
        for (int a = 0; a < w; a++) {
            float left = (TEX_W / 2.0) - (float(w) * GLYPH / 2.0) + (float(a) * GLYPH) + (GLYPH / 2.0);
            if (x >= left && x < left + GLYPH && y >= top && y < top + GLYPH) {
                // Determine which glyph from the label we will render here
                int glyph = 10;
                if (w == 3) {
                    if (a == 0) {
                        glyph = 10;
                    } else {
                        glyph = 9;
                    }
                } else if (w == 2) {
                    if (a == 0) {
                        glyph = u_pathLabel / 10;
                    } else {
                        glyph = int(mod(float(u_pathLabel), 10.0));
                    }
                } else {
                    glyph = u_pathLabel;
                }

                // Render the selected glyph
                vec2 offset = getPathLabelOffset(glyph);
                gl_FragColor = texture2D(u_pathLabelsTexture, vec2((x - left + offset.x) / TEX_W, (y - top + offset.y) / TEX_H));
            }
        }
    }
}

// Main shader function
void main() {
    bool isTopFace = v_normal == vec3(0.0, 1.0, 0.0);

    // Grab texture color at coordinate
    vec2 texCoords = v_diffuseUV;
    if (u_vision > NO_VISIBILITY && u_wave && isTopFace) {
        // Wave Tiles should oscillate slightly
        texCoords.y += u_diffuseUVTransform.z * 0.0075 * ((2.0 * abs(mod(u_timer, 6000.0) - 3000.0) / 3000.0) - 1.0);
    }
    if (u_blackout == 0) {
        gl_FragColor = texture2D(u_diffuseTexture, texCoords) * u_diffuseColor;
    } else {
        gl_FragColor = vec4(0.25, 0.25, 0.25, 1.0);
    }
    gl_FragColor.a *= u_opacity;

    // Glyph texture logic
    if (u_vision > NO_VISIBILITY && isTopFace && u_includeGlyphTexture) {
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
    if (u_hovered > 0) {
        gl_FragColor.x += 0.5;
        gl_FragColor.y += 0.5;
        gl_FragColor.z += 0.2;
    } else if (u_option > 0) {
        float coeff = (abs(mod(u_timer, 2000.0) - 1000.0) / 1000.0 * 0.35) + 0.4;
        gl_FragColor.x += coeff;
        gl_FragColor.y += coeff;
        gl_FragColor.z += coeff * 0.5;
    }

    // Border and path rendering logic
    if (isTopFace && (u_tileBorder > 0 || u_domainBorder > 0 || u_movePath > 0 || u_pathLabel > 0)) {
        vec4 black = vec4(0.0, 0.0, 0.0, 1.0);
        applyPath(black);

        // These should not render on unseen tiles
        if (u_vision > NO_VISIBILITY) {
            vec4 white = vec4(1.0, 1.0, 1.0, 1.0);
            applyBorder(u_tileBorder, u_borderColor, u_borderTexture1, u_borderTexture2);
            applyBorder(u_domainBorder, white, u_borderTexture3, u_borderTexture4);
            applyBorderExtensions(u_domainBorderExtension, white);
        }
    }

    // Make the color bluer if it's nighttime
    if (u_nighttime > 0.0) {
        mat4 bluer;
        bluer[0] = vec4(0.7, 0.0, 0.0, 0.0);
        bluer[1] = vec4(0.0, 0.7, 0.0, 0.0);
        bluer[2] = vec4(0.0, 0.0, 1.0, 0.0);
        bluer[3] = vec4(0.0, 0.0, 0.0, 1.0);
        gl_FragColor = bluer * gl_FragColor;
    }

    // Make the color darker if it's half vision
    if (u_vision <= HALF_VISIBILITY) {
        mat4 darker;
        darker[0] = vec4(0.6, 0.0, 0.0, 0.0);
        darker[1] = vec4(0.0, 0.6, 0.0, 0.0);
        darker[2] = vec4(0.0, 0.0, 0.6, 0.0);
        darker[3] = vec4(0.0, 0.0, 0.0, 1.0);
        gl_FragColor = darker * gl_FragColor;
    }
}
