/**
 * References:
 * https://github.com/libgdx/libgdx/blob/d14a3eca/gdx/src/com/badlogic/gdx/graphics/g2d/SpriteBatch.java#L153-L185
 */
#ifdef GL_ES
#define MED mediump
precision mediump float;
#else
#define MED
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_transform;
uniform int u_mode;

void main() {
    vec4 color = u_transform * texture2D(u_texture, v_texCoords);

    // Gray mode
    if (u_mode == 1) {
        float value = (color.x + color.y + color.z) / 3.0;
        color.x = value;
        color.y = value;
        color.z = value;
    }

    // Bright mode
    if (u_mode == 2) {
        color.x += 0.5;
        color.y += 0.5;
        color.z += 0.2;
    }

    // Set the output color value
    gl_FragColor = color;
}
