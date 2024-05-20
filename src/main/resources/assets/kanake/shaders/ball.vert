#version 120

varying vec3 n;
varying vec4 color;

void main() {
    color = gl_Color;
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    n = gl_Normal;
}
