#version 120


varying vec2 texCoords;

void main()
{
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
    texCoords = gl_MultiTexCoord0.xy;
}