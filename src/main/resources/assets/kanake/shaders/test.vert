#version 120

// 输入：OpenGL 内置的顶点坐标
// 内置变量：gl_Vertex 是当前的顶点位置
// gl_MultiTexCoord0 是当前顶点的第一个纹理坐标
varying vec2 TexCoords; // 用于传递到片段着色器的纹理坐标

void main()
{
    // 使用固定功能管线的模型视图和投影矩阵
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

    // 将纹理坐标传递给片段着色器
    TexCoords = gl_MultiTexCoord0.xy;
}
