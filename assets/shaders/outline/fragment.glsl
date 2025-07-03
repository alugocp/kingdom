varying vec3 v_normal;

void main() {
    gl_FragColor = vec4(
        (v_normal.x + 1.0) / 2.0,
        (v_normal.y + 1.0) / 2.0,
        (v_normal.z + 1.0) / 2.0,
        1.0
    );
}