#version 120

uniform sampler2D tex0;
uniform sampler2D tex1;

varying vec2 texCoords;

const float epsilon = 0.0001;

bool areColorsEqual(vec4 color1, vec4 color2) {
    return (abs(color1.r - color2.r) < epsilon) &&
    (abs(color1.g - color2.g) < epsilon) &&
    (abs(color1.b - color2.b) < epsilon) &&
    (abs(color1.a - color2.a) < epsilon);
}

vec4 getColor(vec2 tc) {
    vec4 color1 = texture2D(tex0, tc);
    vec4 color2 = texture2D(tex1, tc);
    if (areColorsEqual(color1, color2)) {
        return color1;
    }
    return vec4(0.0, 0.0, 0.0, 0.0);
}


void main() {
    gl_FragColor = getColor(texCoords);
}
