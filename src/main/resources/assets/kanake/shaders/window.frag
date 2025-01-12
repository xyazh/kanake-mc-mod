#version 120

uniform sampler2D tex0;
uniform sampler2D tex1;
varying vec2 texCoords;
varying vec4 color;

void main() {
    vec4 color1 = texture2D(tex0,texCoords);
    vec4 color2 = vec4(texture2D(tex1,texCoords).xyz,0);
    gl_FragColor = color * (color1 + color2);
}
