#version 120


varying vec2 TexCoords;

void main()
{
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    TexCoords = gl_MultiTexCoord0.xy;
}