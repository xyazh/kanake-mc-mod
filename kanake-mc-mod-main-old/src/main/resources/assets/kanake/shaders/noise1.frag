#version 120

uniform sampler2D tex0;
uniform sampler2D tex1;
uniform vec2 rand;
varying vec2 tex_coord;

void main(){
    vec4 color = texture2D(tex1, tex_coord+rand);
    float a = 1.0 - clamp(length(color.xyz), 0.0, 1.0);
    vec4 color2 = texture2D(tex0, tex_coord);
    gl_FragColor = vec4(color2.xyz, min(color2.a, a));
}
