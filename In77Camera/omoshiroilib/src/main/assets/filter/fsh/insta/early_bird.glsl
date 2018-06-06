 precision mediump float;

 varying mediump vec2 vTextureCoord;

 uniform sampler2D sTexture;
 uniform sampler2D sTexture2; //earlyBirdCurves
 uniform sampler2D sTexture3; //earlyBirdOverlay
 uniform sampler2D sTexture4; //vig
 uniform sampler2D sTexture5; //earlyBirdBlowout
 uniform sampler2D sTexture6; //earlyBirdMap

 const mat3 saturate = mat3(
                            1.210300,
                            -0.089700,
                            -0.091000,
                            -0.176100,
                            1.123900,
                            -0.177400,
                            -0.034200,
                            -0.034200,
                            1.265800);
 const vec3 rgbPrime = vec3(0.25098, 0.14640522, 0.0);
 const vec3 desaturate = vec3(.3, .59, .11);

 void main()
 {

     vec3 texel = texture2D(sTexture, vTextureCoord).rgb;


     vec2 lookup;
     lookup.y = 0.5;

     lookup.x = texel.r;
     texel.r = texture2D(sTexture2, lookup).r;

     lookup.x = texel.g;
     texel.g = texture2D(sTexture2, lookup).g;

     lookup.x = texel.b;
     texel.b = texture2D(sTexture2, lookup).b;

     float desaturatedColor;
     vec3 result;
     desaturatedColor = dot(desaturate, texel);


     lookup.x = desaturatedColor;
     result.r = texture2D(sTexture3, lookup).r;
     lookup.x = desaturatedColor;
     result.g = texture2D(sTexture3, lookup).g;
     lookup.x = desaturatedColor;
     result.b = texture2D(sTexture3, lookup).b;

     texel = saturate * mix(texel, result, .5);

     vec2 tc = (2.0 * vTextureCoord) - 1.0;
     float d = dot(tc, tc);

     vec3 sampled;
     lookup.y = .5;

     /*
      lookup.x = texel.r;
      sampled.r = texture2D(inputImageTexture4, lookup).r;

      lookup.x = texel.g;
      sampled.g = texture2D(inputImageTexture4, lookup).g;

      lookup.x = texel.b;
      sampled.b = texture2D(inputImageTexture4, lookup).b;

      float value = smoothstep(0.0, 1.25, pow(d, 1.35)/1.65);
      texel = mix(texel, sampled, value);
      */

     //---

     lookup = vec2(d, texel.r);
     texel.r = texture2D(sTexture4, lookup).r;
     lookup.y = texel.g;
     texel.g = texture2D(sTexture4, lookup).g;
     lookup.y = texel.b;
     texel.b	= texture2D(sTexture4, lookup).b;
     float value = smoothstep(0.0, 1.25, pow(d, 1.35)/1.65);

     //---

     lookup.x = texel.r;
     sampled.r = texture2D(sTexture5, lookup).r;
     lookup.x = texel.g;
     sampled.g = texture2D(sTexture5, lookup).g;
     lookup.x = texel.b;
     sampled.b = texture2D(sTexture5, lookup).b;
     texel = mix(sampled, texel, value);


     lookup.x = texel.r;
     texel.r = texture2D(sTexture6, lookup).r;
     lookup.x = texel.g;
     texel.g = texture2D(sTexture6, lookup).g;
     lookup.x = texel.b;
     texel.b = texture2D(sTexture6, lookup).b;

     gl_FragColor = vec4(texel, 1.0);
 }
