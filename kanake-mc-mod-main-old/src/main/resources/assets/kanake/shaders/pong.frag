#version 120

uniform sampler2D tex0;
uniform vec2 wh;
varying vec2 texCoords;
varying vec4 color;

void main() {
    vec2 pixTex = texCoords * wh;
    vec2 lu = (pixTex + vec2(3.0,3.0))/wh;
    vec2 ru = (pixTex + vec2(3.0,-3.0))/wh;
    vec2 ld = (pixTex + vec2(-3.0,3.0))/wh;
    vec2 rd = (pixTex + vec2(-3.0,-3.0))/wh;
    vec4 c1 = texture2D(tex0,lu);
    vec4 c2 = texture2D(tex0,ru);
    vec4 c3 = texture2D(tex0,ld);
    vec4 c4 = texture2D(tex0,rd);
    vec4 cc = texture2D(tex0,texCoords);
    vec4 color1 = (cc + c1 + c2 + c3 + c4) * 0.2;
    gl_FragColor = color * color1;
}
