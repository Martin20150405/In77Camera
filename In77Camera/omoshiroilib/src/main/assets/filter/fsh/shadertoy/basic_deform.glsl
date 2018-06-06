precision highp float;

uniform vec3                iResolution;
uniform float               iGlobalTime;
uniform sampler2D           sTexture;
varying vec2                vTextureCoord;

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{	
	float stongth = 0.3;
	vec2 uv = fragCoord.xy;
	float waveu = sin((uv.y + iGlobalTime) * 20.0) * 0.5 * 0.05 * stongth;
	fragColor = texture2D(sTexture, uv + vec2(waveu, 0));
}

void main() {
	mainImage(gl_FragColor, vTextureCoord);
}