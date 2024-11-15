uniform sampler2D tex0;
uniform int horizontal;
uniform vec2 wh;
uniform int count;
varying vec2 texCoords;
const float sigma = 6;
const float weights[5] = float[] (0.227, 0.194, 0.121, 0.054, 0.016);

vec4 blend(vec4 c1, vec4 c2){
    if(c1.w == 0.0 && c2.w == 0.0){
        return vec4(0.0);
    }
    vec2 vAlpha = normalize(vec2(c1.w, c2.w));
    vec3 color = c1.xyz * vAlpha.x + c2.xyz * vAlpha.y;
    return vec4(color, max(c1.w, c2.w));

    //return vec4(c1.xyz,max(c1.w, c2.w));
}


void main()
{
    vec2 offset = 1.0 / wh;
    vec4 color = texture2D(tex0, texCoords);
    if(horizontal == 1)
    {
        for(int i = 1; i < 5; i++){
            float r = offset.x * i;
            vec4 color1 = texture2D(tex0, texCoords + vec2(r, 0.0));
            vec4 color2 = texture2D(tex0, texCoords - vec2(r, 0.0));
            //float s = offset.x * sigma;
            //float weight = exp(-r * r / (2.0 * s * s));
            float weight = weights[i];
            float a1 = color1.w * weight;
            float a2 = color2.w * weight;
            color1 = vec4(color1.xyz, a1);
            color2 = vec4(color2.xyz, a2);
            color = blend(color, blend(color1, color2));
        }
    }
    else
    {
        for(int i = 1; i < 5; i++){
            float r = offset.y * i;
            vec4 color1 = texture2D(tex0, texCoords + vec2(0.0, r));
            vec4 color2 = texture2D(tex0, texCoords - vec2(0.0, r));
            //float s = offset.y * sigma;
            //float weight = exp(-r * r / (2.0 * s * s));
            float weight = weights[i];
            float a1 = color1.w * weight;
            float a2 = color2.w * weight;
            color1 = vec4(color1.xyz, a1);
            color2 = vec4(color2.xyz, a2);
            color = blend(color, blend(color1, color2));
        }
    }
    if(count == 5){
        color = vec4(color.xyz, color.w);
    }
    gl_FragColor = color;
}