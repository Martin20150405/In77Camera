attribute vec4 aPosition;
attribute vec4 aTextureCoord;
varying vec2 vTextureCoord;

uniform highp float texelWidthOffset;
uniform highp float texelHeightOffset;
varying vec2 blurCoordinates[5];

void main()
{
    gl_Position = aPosition;
    vec2 singleStepOffset = vec2(texelWidthOffset, texelHeightOffset);
    vTextureCoord  = aTextureCoord.xy;
    blurCoordinates[0] = aTextureCoord.xy;
    blurCoordinates[1] = aTextureCoord.xy + singleStepOffset * 1.407333;
    blurCoordinates[2] = aTextureCoord.xy - singleStepOffset * 1.407333;
    blurCoordinates[3] = aTextureCoord.xy + singleStepOffset * 3.294215;
    blurCoordinates[4] = aTextureCoord.xy - singleStepOffset * 3.294215;
}


