#version 120
// 接收从顶点着色器传递的纹理坐标
varying vec2 TexCoords;

// 场景纹理
uniform sampler2D screenTexture;
uniform vec2 wh;

vec2 posToPix(vec2 pos){
    return pos * wh;
}

vec2 pixToPos(vec2 pixPos){
    return pixPos / wh;
}

void main(){
    vec2 centerPixPos = posToPix(vec2(0.5, 0.5));
    vec2 texPixCoord = posToPix(TexCoords);
    //当前像素到中心点向量，方向，距离
    vec2 direction = texPixCoord- centerPixPos;
    vec2 forth = normalize(direction);
    float distance = length(direction);
    float radius = 0.2 * wh.x;
    float radius2 = 0.06 * wh.x;
    vec4 color = vec4(0.0, 0.0, 0.0, 0.0);
    if (distance < radius2){
        color = vec4(0.0, 0.0, 0.0, 1.0);
    }else if (distance < radius){
        //假设中心在原点，光线被偏转力度向量
        float x = distance / radius;
        vec2 changeForth = normalize(vec2(acos(x), asin(x)));
        //假设光线为平行光
        vec2 lightForth = vec2(0.0, 1.0);
        //使光线偏折
        vec2 lightForth2 = normalize(lightForth + changeForth);
        //假设光线被偏折射后距离背景100px
        float l = 200.0;
        //最后投射的像素的改变量
        float dx = l / lightForth2.y * lightForth2.x;
        texPixCoord = texPixCoord - forth * dx;
        vec2 distortedCoords = pixToPos(texPixCoord);
        color = texture2D(screenTexture, distortedCoords);
    }
    gl_FragColor = color;
}