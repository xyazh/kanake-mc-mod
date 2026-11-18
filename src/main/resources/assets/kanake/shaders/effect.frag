#version 120

uniform sampler2D tex0;
uniform sampler2D tex1;
varying vec2 texCoords;
varying vec4 color;

const float epsilon = 0.0001;

bool areColorsEqual(vec4 color1, vec4 color2) {
    // The particle-only effect buffer is cleared to transparent black while
    // the screen buffer has the scene alpha. Compare RGB only; otherwise
    // opaque emissive particles with alpha 1 in the effect pass can still be
    // rejected by the bloom extraction because the screen alpha differs.
    return (abs(color1.r - color2.r) < epsilon) &&
    (abs(color1.g - color2.g) < epsilon) &&
    (abs(color1.b - color2.b) < epsilon);
}

vec4 getColor(vec2 tc) {
    vec4 color1 = texture2D(tex0, tc);
    vec4 color2 = texture2D(tex1, tc);
    if (areColorsEqual(color1, color2)) {
        // Keep the extracted light opaque. The screen buffer alpha can be
        // unrelated to the particle's own alpha, and the later additive
        // window pass must not multiply the bloom away with alpha 0.
        return vec4(color1.rgb, 1.0);
    }
    return vec4(0.0, 0.0, 0.0, 1.0);
}


void main() {
    vec4 color1 = vec4(0.0, 0.0, 0.0, 1.0);
    for (int i = -4; i < 10; i++) {
        for (int j = -4; j < 10; j++) {
            color1 += getColor(texCoords + vec2(i/385.0, j/216.0))*0.012;
        }
    }
    gl_FragColor = color * getColor(texCoords);
}
