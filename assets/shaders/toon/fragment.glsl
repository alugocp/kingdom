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
uniform sampler2D u_normalsTexture;
uniform vec4 u_diffuseColor;
uniform float u_nighttime;
uniform float u_opacity;
uniform int u_vision;
uniform bool u_lightOutline;
uniform bool u_outlineShader;
varying MED vec2 v_diffuseUV;
const int HALF_VISIBILITY = 1;
const int NO_VISIBILITY = 0;
const float OUTLINE_WIDTH = 3.0;

vec4 normalsTexSample(float x, float y) {
    return texture2D(u_normalsTexture, vec2(x / 1600.0, y / 960.0));
}

bool outline() {
    vec4 bg = vec4(1.0, 1.0, 1.0, 1.0);
    vec4 center = normalsTexSample(gl_FragCoord.x, gl_FragCoord.y);
    if (center == bg) {
        return false;
    }
    vec4 topRight = normalsTexSample(gl_FragCoord.x + OUTLINE_WIDTH, gl_FragCoord.y + OUTLINE_WIDTH);
    vec4 botLeft = normalsTexSample(gl_FragCoord.x - OUTLINE_WIDTH, gl_FragCoord.y - OUTLINE_WIDTH);
    return center.z <= topRight.z - 0.01 || center.z <= botLeft.z - 0.01;
}

// Main shader function
void main() {
    // Apply the outline color
    if (u_outlineShader && outline()) {
        if (u_lightOutline) {
            gl_FragColor = vec4(1.0, 1.0, 0.5, 0.5);
        } else {
            gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        }
        return;
    }

    // Grab texture color at coordinate
    vec2 texCoords = v_diffuseUV;
    gl_FragColor = texture2D(u_diffuseTexture, texCoords) * u_diffuseColor;
    gl_FragColor.a *= u_opacity;

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
