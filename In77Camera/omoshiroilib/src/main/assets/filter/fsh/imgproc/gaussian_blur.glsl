precision lowp float;
precision lowp int;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;

varying vec2 blurCoordinates[5];

void main()
{
    vec4 original = texture2D(sTexture, vTextureCoord);
    lowp vec4 sum = vec4(0.0);
    sum += texture2D(sTexture, blurCoordinates[0]) * 0.204164;
    sum += texture2D(sTexture, blurCoordinates[1]) * 0.304005;
    sum += texture2D(sTexture, blurCoordinates[2]) * 0.304005;
    sum += texture2D(sTexture, blurCoordinates[3]) * 0.093913;
    sum += texture2D(sTexture, blurCoordinates[4]) * 0.093913;
    gl_FragColor = vec4(sum.xyz,  1.0);
    gl_FragColor=vec4(mix(gl_FragColor.rgb, vec3(0.0), 0.02), gl_FragColor.a);
}