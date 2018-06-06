precision mediump float;

uniform vec3                iResolution;
uniform sampler2D           sTexture;
varying vec2                vTextureCoord;

#define S (iResolution.x / 6e1) // The cell size.

void mainImage(out vec4 c, vec2 p)
{
    c = texture2D(sTexture, floor((p + .5) / S) * S / iResolution.xy);
}

void main() {
	mainImage(gl_FragColor, vTextureCoord*iResolution.xy);
}