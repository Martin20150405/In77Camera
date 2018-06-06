precision mediump float;
uniform sampler2D sTexture;
varying vec2 vTextureCoord;
vec4 calVignette(vec2 coord,vec4 color,float texture_width,float texture_height) {
	float shade = 0.6;
	float slope = 20.0;
	float range = 1.30 - sqrt(0.8) * 0.7;
	vec2 scale;
	if(texture_width > texture_height) {
		scale.x = 1.0;
		scale.y = texture_height / texture_width;
	} else {
		scale.x = texture_width / texture_height;
		scale.y = 1.0;
	}
	float inv_max_dist = 2.0 / length(scale);
	float dist = length((coord - vec2(0.5, 0.5)) * scale);
	float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);
	return vec4(color.rgb * lumen,color.a);
}
vec4 calNewVignette(vec2 coord,vec4 color,float texture_width,float texture_height,float value) {
	float shade = 0.85;
	float slope = 20.0;
	float range = 1.30 - sqrt(value) * 0.7;
	vec2 scale;
	if(texture_width > texture_height) {
		scale.x = 1.0;
		scale.y = texture_height / texture_width;
	} else {
		scale.x = texture_width / texture_height;
		scale.y = 1.0;
	}
	float inv_max_dist = 2.0 / length(scale);
	float dist = length((coord - vec2(0.5, 0.5)) * scale);
	float lumen = shade / (1.0 + exp((dist * inv_max_dist - range) * slope)) + (1.0 - shade);
	return vec4(color.rgb * lumen,color.a);
}
vec4 calVignette2(vec4 color, vec2 coord, float strength) {
	float distance = (coord.x - 0.5) * (coord.x - 0.5) + (coord.y - 0.5) * (coord.y - 0.5);
	float scale = distance / 0.5 * strength;
	color.r =  color.r - scale;
	color.g = color.g - scale;
	color.b = color.b - scale;
	return color;
}
vec4 calNewSaturation(vec4 color,float saturation) {
	float gray = dot(color.rgb, vec3(0.299,0.587,0.114));
	return vec4(gray + (saturation / 100.0 + 1.0) * (color.r - gray), gray + (saturation / 100.0 + 1.0) * (color.g - gray), gray + (saturation / 100.0 + 1.0) * (color.b - gray), color.a);
}
void main() {
	vec4 color = texture2D(sTexture, vTextureCoord);
	color = calVignette2(color, vTextureCoord,1.0);
	color = calNewSaturation(color, 57.0);
	gl_FragColor = color;
}

