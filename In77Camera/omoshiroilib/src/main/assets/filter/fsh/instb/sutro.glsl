precision mediump float;
 
 varying mediump vec2 vTextureCoord;
 
 uniform sampler2D sTexture;
 uniform sampler2D sTexture2; //sutroMap;
 uniform sampler2D sTexture3; //sutroMetal;
 uniform sampler2D sTexture4; //softLight
 uniform sampler2D sTexture5; //sutroEdgeburn
 uniform sampler2D sTexture6; //sutroCurves
 
 uniform float strength;
 
 void main()
 {
     vec4 originColor = texture2D(sTexture, vTextureCoord);
     vec3 texel = texture2D(sTexture, vTextureCoord).rgb;
     
     vec2 tc = (2.0 * vTextureCoord) - 1.0;
     float d = dot(tc, tc);
     vec2 lookup = vec2(d, texel.r);
     texel.r = texture2D(sTexture2, lookup).r;
     lookup.y = texel.g;
     texel.g = texture2D(sTexture2, lookup).g;
     lookup.y = texel.b;
     texel.b	= texture2D(sTexture2, lookup).b;
     
     vec3 rgbPrime = vec3(0.1019, 0.0, 0.0);
     float m = dot(vec3(.3, .59, .11), texel.rgb) - 0.03058;
     texel = mix(texel, rgbPrime + m, 0.32);
     
     vec3 metal = texture2D(sTexture3, vTextureCoord).rgb;
     texel.r = texture2D(sTexture4, vec2(metal.r, texel.r)).r;
     texel.g = texture2D(sTexture4, vec2(metal.g, texel.g)).g;
     texel.b = texture2D(sTexture4, vec2(metal.b, texel.b)).b;
     
     texel = texel * texture2D(sTexture5, vTextureCoord).rgb;
     
     texel.r = texture2D(sTexture6, vec2(texel.r, .16666)).r;
     texel.g = texture2D(sTexture6, vec2(texel.g, .5)).g;
     texel.b = texture2D(sTexture6, vec2(texel.b, .83333)).b;
     
     texel.rgb = mix(originColor.rgb, texel.rgb, strength);

     gl_FragColor = vec4(texel, 1.0);
 }