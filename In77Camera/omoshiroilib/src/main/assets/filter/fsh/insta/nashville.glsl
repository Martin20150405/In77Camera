precision mediump float;

 varying mediump vec2 vTextureCoord;

 uniform sampler2D sTexture;
 uniform sampler2D sTexture2;

 void main()
 {
     vec3 texel = texture2D(sTexture, vTextureCoord).rgb;
     texel = vec3(
                  texture2D(sTexture2, vec2(texel.r, .16666)).r,
                  texture2D(sTexture2, vec2(texel.g, .5)).g,
                  texture2D(sTexture2, vec2(texel.b, .83333)).b);
     gl_FragColor = vec4(texel, 1.0);
 }
