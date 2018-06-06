precision mediump float;
varying vec2 vTextureCoord;
uniform sampler2D sTexture;
void main() {
    vec4 centralColor = texture2D(sTexture, vTextureCoord);
    gl_FragColor = vec4((1.0 - centralColor.rgb), centralColor.w);
}