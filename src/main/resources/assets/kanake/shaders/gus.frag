#version 120

varying vec2 tex_coord;
uniform sampler2D texture;
uniform float width;
uniform float height;

void main()
{
    float sum_a = 0.0;
    vec3 rgb = vec3(0.0);
    for (int i = -5; i <= 5; i+=1)
    {
        for (int j = -5; j <= 5; j+=1)
        {
            vec4 color =  texture2D(texture, tex_coord + vec2(float(i)/width, float(j)/height));
            if(color.x>0.0){
                rgb = color.xyz;
            }
            sum_a += color.w;
        }
    }
   float a = sum_a / 121.0;
    gl_FragColor = vec4(rgb, a);
    //gl_FragColor = texture2D(texture, tex_coord);
}
