#version 330 core
layout(location = 0) in vec3 kanake_pos;
void main()
{
    gl_Position = vec4(kanake_pos, 1.0);
}