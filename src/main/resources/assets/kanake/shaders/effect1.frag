#version 120

uniform sampler2D tex0;
uniform float time;

varying vec2 texCoords;

float max(float f1, float f2){
    return f1 > f2 ? f1 : f2;
}


float _randNorm(float seed) {
    // 将种子应用到随机数生成函数中，影响随机数序列
    float u1 = fract(sin(seed + gl_FragCoord.x * 12.9898 + gl_FragCoord.y * 78.233) * 43758.5453);
    float u2 = fract(sin(seed + gl_FragCoord.y * 12.9898 + gl_FragCoord.x * 78.233) * 43758.5453);
    // 使用 Box-Muller 转换
    float z0 = sqrt(-2.0 * log(u1)) * cos(2.0 * 3.14159265358979323846 * u2);
    return z0;
}

float randNorm(float mean, float stddev, float seed) {
    return mean + stddev * _randNorm(seed);
}

vec4 blend(vec4 c1, vec4 c2){
    if(c1.w == 0.0 && c2.w == 0.0){
        return vec4(0.0);
    }
    vec2 vAlpha = normalize(vec2(c1.w, c2.w));
    vec3 color = c1.xyz * vAlpha.x + c2.xyz * vAlpha.y;
    //vec3 color = c1.xyz * c1.w + c2.xyz * c2.w;
    return vec4(color, max(c1.w, c2.w));
}

void main() {
    vec4 color = vec4(0.0);
    float step = 0.005;
    float thisPox = 0.0;
    float sigma = 0.05;
    for(int i = 0; i < 20; i++){
        float r = randNorm(0.0, 0.05, float(i) + time);
        vec4 color1 = texture2D(tex0, texCoords + vec2(r, 0.0));
        float a1 = exp(-r * r / (2.0 * sigma * sigma)) * color1.w;
        color1 = vec4(color1.xyz, a1);
        color = blend(color, color1);
    }
    color = blend(color, texture2D(tex0, texCoords));
    gl_FragColor = color;
}
