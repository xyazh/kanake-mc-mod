#version 120

uniform sampler2D tex0;
uniform sampler2D tex1;
varying vec2 TexCoords;

const float epsilon = 0.0001;  // 设置比较的容忍度

bool areColorsEqual(vec4 color1, vec4 color2) {
    return (abs(color1.r - color2.r) < epsilon) &&
    (abs(color1.g - color2.g) < epsilon) &&
    (abs(color1.b - color2.b) < epsilon) &&
    (abs(color1.a - color2.a) < epsilon);
}


void main(){
    vec4 color1 = texture2D(tex0, TexCoords);
    //vec4 color2 = texture2D(tex1, TexCoords);
    vec4 color2 = vec4(1.0,1.0,1.0,1.0);
    if (areColorsEqual(color1, color2)){
        gl_FragColor = vec4(1.0,0.0,1.0,1.0);
        return;
    }
    gl_FragColor = vec4(0.0,0.0,0.0,0.0);
}
