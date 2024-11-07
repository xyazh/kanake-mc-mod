#version 120
// 接收从顶点着色器传递的纹理坐标
varying vec2 TexCoords;

// 场景纹理
uniform sampler2D screenTexture;
uniform vec2 wh;

void main()
{
    vec2 lensCenter = vec2(0.5, 0.5);
    float range = 0.2;

    // 计算当前像素到凹透镜中心的方向和距离
    vec2 direction = TexCoords - lensCenter;
    direction = vec2(direction.x / wh.y * wh.x,direction.y);
    vec2 forth = normalize(direction);
    float distance = length(direction);
    vec2 lensEffect = vec2(0.0, 0.0);
    if (distance < 0.7){
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    }else if (distance < 0.13333333){
        //简单的用cos模拟光线扭曲，更真实的TODO
        lensEffect = direction - forth * cos(3 / 2 * 3.1415926535 * distance / range) / 5;
        vec2 distortedCoords = TexCoords + lensEffect;
        // 从纹理中采样颜色
        vec4 color = texture2D(screenTexture, distortedCoords);
        // 将采样得到的颜色赋值给 gl_FragColor
        gl_FragColor = color;
    }else{
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
