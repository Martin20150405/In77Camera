/*
precision highp float;
varying mediump vec2 vTextureCoord;
 uniform sampler2D sTexture;
 uniform sampler2D sTexture2; // lookup texture
 uniform mediump float strength;

 void main()
 {
     mediump vec4 originColor = texture2D(sTexture, vTextureCoord);
     mediump vec4 textureColor = texture2D(sTexture, vTextureCoord);

     mediump float blueColor = textureColor.b * 63.0;

     mediump vec2 quad1;
     quad1.y = floor(floor(blueColor) / 8.0);
     quad1.x = floor(blueColor) - (quad1.y * 8.0);

     mediump vec2 quad2;
     quad2.y = floor(ceil(blueColor) / 8.0);
     quad2.x = ceil(blueColor) - (quad2.y * 8.0);

     mediump vec2 texPos1;
     texPos1.x = (quad1.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos1.y = (quad1.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

     mediump vec2 texPos2;
     texPos2.x = (quad2.x * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.r);
     texPos2.y = (quad2.y * 0.125) + 0.5/512.0 + ((0.125 - 1.0/512.0) * textureColor.g);

     mediump vec4 newColor1 = texture2D(sTexture2, texPos1);
     mediump vec4 newColor2 = texture2D(sTexture2, texPos2);

     mediump vec4 newColor = mix(newColor1, newColor2, fract(blueColor));

     newColor.rgb = mix(originColor.rgb, newColor.rgb, strength);

     gl_FragColor = vec4(newColor.rgb, textureColor.w);
 }*/
