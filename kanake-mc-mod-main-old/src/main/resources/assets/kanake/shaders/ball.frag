#version 120

varying vec3 n;
varying vec4 color;
uniform vec3 posCenter;

void main() {
    float alpha = abs(dot(normalize(n),  normalize(posCenter)));
    gl_FragColor = vec4(color.rgb, alpha);
}
