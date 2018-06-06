 precision mediump float;

 varying mediump vec2 vTextureCoord;

 uniform sampler2D sTexture;
 uniform sampler2D sTexture2;

 void main()
 {
     vec3 texel = texture2D(sTexture, vTextureCoord).rgb;

     vec2 lookup;
     lookup.y = .5;

     lookup.x = texel.r;
     texel.r = texture2D(sTexture2, lookup).r;

     lookup.x = texel.g;
     texel.g = texture2D(sTexture2, lookup).g;

     lookup.x = texel.b;
     texel.b = texture2D(sTexture2, lookup).b;

     gl_FragColor = vec4(texel, 1.0);
 }
