 precision mediump float;
 
 varying mediump vec2 vTextureCoord;
 
 uniform sampler2D sTexture;
 uniform sampler2D sTexture2;  //edgeBurn
 uniform sampler2D sTexture3;  //hefeMap
 uniform sampler2D sTexture4;  //hefeGradientMap
 uniform sampler2D sTexture5;  //hefeSoftLight
 uniform sampler2D sTexture6;  //hefeMetal
 
 uniform float strength;

 void main()
{
    vec4 originColor = texture2D(sTexture, vTextureCoord);
    vec3 texel = texture2D(sTexture, vTextureCoord).rgb;
    vec3 edge = texture2D(sTexture2, vTextureCoord).rgb;
    texel = texel * edge;
    
    texel = vec3(
                 texture2D(sTexture3, vec2(texel.r, .16666)).r,
                 texture2D(sTexture3, vec2(texel.g, .5)).g,
                 texture2D(sTexture3, vec2(texel.b, .83333)).b);
    
    vec3 luma = vec3(.30, .59, .11);
    vec3 gradSample = texture2D(sTexture4, vec2(dot(luma, texel), .5)).rgb;
    vec3 final = vec3(
                      texture2D(sTexture5, vec2(gradSample.r, texel.r)).r,
                      texture2D(sTexture5, vec2(gradSample.g, texel.g)).g,
                      texture2D(sTexture5, vec2(gradSample.b, texel.b)).b
                      );
    
    vec3 metal = texture2D(sTexture6, vTextureCoord).rgb;
    vec3 metaled = vec3(
                        texture2D(sTexture5, vec2(metal.r, texel.r)).r,
                        texture2D(sTexture5, vec2(metal.g, texel.g)).g,
                        texture2D(sTexture5, vec2(metal.b, texel.b)).b
                        );
    
    metaled.rgb = mix(originColor.rgb, metaled.rgb, strength);

    gl_FragColor = vec4(metaled, 1.0);
}