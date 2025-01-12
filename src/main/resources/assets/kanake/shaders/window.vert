#version 120


varying vec2 texCoords;
varying vec4 color;

void main()
{
    gl_Position = gl_Vertex;
    color = gl_Color;
    texCoords = gl_MultiTexCoord0.xy;
}