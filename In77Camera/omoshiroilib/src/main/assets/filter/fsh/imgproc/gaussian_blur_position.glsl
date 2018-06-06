uniform sampler2D sTexture;

const lowp int GAUSSIAN_SAMPLES = 9;

varying highp vec2 vTextureCoord;
varying highp vec2 blurCoordinates[GAUSSIAN_SAMPLES];

uniform highp float aspectRatio;
uniform lowp vec2 blurCenter;
uniform highp float blurRadius;

void main() {
    highp vec2 textureCoordinateToUse = vec2(vTextureCoord.x, (vTextureCoord.y * aspectRatio + 0.5 - 0.5 * aspectRatio));
    highp float dist = distance(blurCenter, textureCoordinateToUse);
    if(abs(dist-blurRadius)<0.0005){
        gl_FragColor = vec4(0.0,1.0,1.0,0.2);
    }else
    if (dist > blurRadius){
        lowp vec4 sum = vec4(0.0);

        sum += texture2D(sTexture, blurCoordinates[0]) * 0.05;
        sum += texture2D(sTexture, blurCoordinates[1]) * 0.09;
        sum += texture2D(sTexture, blurCoordinates[2]) * 0.12;
        sum += texture2D(sTexture, blurCoordinates[3]) * 0.15;
        sum += texture2D(sTexture, blurCoordinates[4]) * 0.18;
        sum += texture2D(sTexture, blurCoordinates[5]) * 0.15;
        sum += texture2D(sTexture, blurCoordinates[6]) * 0.12;
        sum += texture2D(sTexture, blurCoordinates[7]) * 0.09;
        sum += texture2D(sTexture, blurCoordinates[8]) * 0.05;

        gl_FragColor = sum;
    }else if (dist < blurRadius){
        gl_FragColor = texture2D(sTexture, vTextureCoord);
    }
}