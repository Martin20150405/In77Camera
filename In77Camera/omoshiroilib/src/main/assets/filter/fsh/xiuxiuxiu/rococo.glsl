//lvjing2

precision lowp float;

varying highp vec2 vTextureCoord;

uniform sampler2D sTexture;
uniform sampler2D sTexture2;


vec3 AdjustColorRG(vec3 inputColor, vec4 adjustAmount)
{
vec3 outputColor = inputColor;


float max_c = max(inputColor.r, inputColor.g);
float min_c = min(inputColor.r, inputColor.g);
float mid_c = inputColor.b;
if(inputColor.b < min_c)
{
mid_c = min_c;
min_c = inputColor.b;
}
if(inputColor.b > max_c)
{
mid_c = max_c;
max_c = inputColor.b;
}

float interval_up = max_c - mid_c;
float interval_down = mid_c - min_c;


float redUpmax = inputColor.r;
float redDownmax = -1.0 * (1.0-redUpmax);
float greenUpmax = inputColor.g;
float greenDownmax = -1.0 * (1.0-greenUpmax);
float blueUpmax = inputColor.b;
float blueDownmax = -1.0 * (1.0-blueUpmax);
float blackUpmax = dot(inputColor, vec3(0.3,0.6,0.1));
float blackDownmax = -1.0 * (1.0-blackUpmax);

vec4 changeColor = adjustAmount;
if(changeColor.r > redUpmax)
changeColor.r = redUpmax;
if(changeColor.r < redDownmax)
changeColor.r = redDownmax;

if(changeColor.g > greenUpmax)
changeColor.g = greenUpmax;
if(changeColor.g < greenDownmax)
changeColor.g = greenDownmax;

if(changeColor.b > blueUpmax)
changeColor.b = blueUpmax;
if(changeColor.b < blueDownmax)
changeColor.b = blueDownmax;

if(changeColor.a > blackUpmax)
changeColor.a = blackUpmax;
if(changeColor.a < blackDownmax)
changeColor.a = blackDownmax;


if((max_c == inputColor.g&&mid_c == inputColor.b)||(max_c == inputColor.b&&mid_c == inputColor.g))
{
outputColor.r = outputColor.r - changeColor.r * interval_down;
outputColor.g = outputColor.g - changeColor.g * interval_down;
outputColor.b = outputColor.b - changeColor.b * interval_down;

outputColor.r = outputColor.r - changeColor.a * interval_down;
outputColor.g = outputColor.g - changeColor.a * interval_down;
outputColor.b = outputColor.b - changeColor.a * interval_down;
}
else
{
outputColor = inputColor;
}




outputColor.r = clamp(outputColor.r, 0.0, 1.0);
outputColor.g = clamp(outputColor.g, 0.0, 1.0);
outputColor.b = clamp(outputColor.b, 0.0, 1.0);

return outputColor;

}


lowp vec3 RGBToHSL(lowp vec3 color)

{
lowp vec3 hsl; // init to 0 to avoid warnings ? (and reverse if + remove first part)

lowp float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB
lowp float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB
lowp float delta = fmax - fmin;             //Delta RGB value

hsl.z = (fmax + fmin) / 2.0; // Luminance

if (delta == 0.0)		//This is a gray, no chroma...
{
hsl.x = 0.0;	// Hue
hsl.y = 0.0;	// Saturation
}
else                                    //Chromatic data...
{
if (hsl.z < 0.5)
hsl.y = delta / (fmax + fmin); // Saturation
else
hsl.y = delta / (2.0 - fmax - fmin); // Saturation

lowp float deltaR = (((fmax - color.r) / 6.0) + (delta / 2.0)) / delta;
lowp float deltaG = (((fmax - color.g) / 6.0) + (delta / 2.0)) / delta;
lowp float deltaB = (((fmax - color.b) / 6.0) + (delta / 2.0)) / delta;

if (color.r == fmax )
hsl.x = deltaB - deltaG; // Hue
else if (color.g == fmax)
hsl.x = (1.0 / 3.0) + deltaR - deltaB; // Hue
else if (color.b == fmax)
hsl.x = (2.0 / 3.0) + deltaG - deltaR; // Hue

if (hsl.x < 0.0)
hsl.x += 1.0; // Hue
else if (hsl.x > 1.0)
hsl.x -= 1.0; // Hue
}

return hsl;
}


lowp float HueToRGB(lowp float f1, lowp float f2, lowp float hue)
{
if (hue < 0.0)
hue += 1.0;
else if (hue > 1.0)
hue -= 1.0;
lowp float res;
if ((6.0 * hue) < 1.0)
res = f1 + (f2 - f1) * 6.0 * hue;
else if ((2.0 * hue) < 1.0)
res = f2;
else if ((3.0 * hue) < 2.0)
res = f1 + (f2 - f1) * ((2.0 / 3.0) - hue) * 6.0;
else
res = f1;
return res;
}


lowp vec3 HSLToRGB(lowp vec3 hsl)
{
lowp vec3 rgb;

if (hsl.y == 0.0)
rgb = vec3(hsl.z); // Luminance
else
{
lowp float f2;

if (hsl.z < 0.5)
f2 = hsl.z * (1.0 + hsl.y);
else
f2 = (hsl.z + hsl.y) - (hsl.y * hsl.z);

lowp float f1 = 2.0 * hsl.z - f2;

rgb.r = HueToRGB(f1, f2, hsl.x + (1.0/3.0));
rgb.g = HueToRGB(f1, f2, hsl.x);
rgb.b= HueToRGB(f1, f2, hsl.x - (1.0/3.0));
}

return rgb;
}


lowp float RGBToL(lowp vec3 color)
{
lowp float fmin = min(min(color.r, color.g), color.b);    //Min. value of RGB
lowp float fmax = max(max(color.r, color.g), color.b);    //Max. value of RGB

return (fmax + fmin) / 2.0; // Luminance
}




void main()
{

vec3 sampleColor;

vec2 blurCoordinates0;
vec2 blurCoordinates1;
vec2 blurCoordinates2;
vec2 blurCoordinates3;
vec2 blurCoordinates4;
vec2 blurCoordinates5;
vec2 blurCoordinates6;
vec2 blurCoordinates7;
vec2 blurCoordinates8;
vec2 blurCoordinates9;
vec2 blurCoordinates10;
vec2 blurCoordinates11;

float mul = 0.1;

float mul_x = mul / 720.0;
float mul_y = mul / 1280.0;

blurCoordinates0 = vTextureCoord + vec2(0.0 * mul_x,-10.0 * mul_y);
blurCoordinates1 = vTextureCoord + vec2(5.0 * mul_x,-8.0 * mul_y);
blurCoordinates2 = vTextureCoord + vec2(8.0 * mul_x,-5.0 * mul_y);
blurCoordinates3 = vTextureCoord + vec2(10.0 * mul_x,0.0 * mul_y);
blurCoordinates4 = vTextureCoord + vec2(8.0 * mul_x,5.0 * mul_y);
blurCoordinates5 = vTextureCoord + vec2(5.0 * mul_x,8.0 * mul_y);
blurCoordinates6 = vTextureCoord + vec2(0.0 * mul_x,10.0 * mul_y);
blurCoordinates7 = vTextureCoord + vec2(-5.0 * mul_x,8.0 * mul_y);
blurCoordinates8 = vTextureCoord + vec2(-8.0 * mul_x,5.0 * mul_y);
blurCoordinates9 = vTextureCoord + vec2(-10.0 * mul_x,0.0 * mul_y);
blurCoordinates10 = vTextureCoord + vec2(-8.0 * mul_x,-5.0 * mul_y);
blurCoordinates11 = vTextureCoord + vec2(-5.0 * mul_x,-8.0 * mul_y);

sampleColor = texture2D(sTexture, vTextureCoord).rgb * 20.0;

sampleColor -= texture2D(sTexture, blurCoordinates0).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates1).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates2).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates3).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates4).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates5).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates6).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates7).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates8).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates9).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates10).rgb;
sampleColor -= texture2D(sTexture, blurCoordinates11).rgb;

sampleColor /= 8.0;

float hue = texture2D(sTexture, vTextureCoord).r;
hue = pow(hue, 1.2);

vec3 texel = texture2D(sTexture, vTextureCoord).rgb;
gl_FragColor = vec4(mix(texel,sampleColor,1.0-hue), 1.0);


//===================================


mat3 saturateMatrix = mat3(
1.1102,
-0.0598,
-0.061,
-0.0774,
1.0826,
-0.1186,
-0.0228,
-0.0228,
1.1772);

mat3 brightMatrix = mat3(
1.1,
0.0,
0.0,
0.0,
1.1,
0.0,
0.0,
0.0,
1.1);

//gl_FragColor = texture2D(inputImageTexture,textureCoordinate);


float dist = distance(vTextureCoord, vec2(0.5,0.5));
dist = smoothstep(0.0,1.0,dist);
dist = pow(dist, 1.5);
vec4 darkenColor = gl_FragColor;
darkenColor.r = pow(darkenColor.r, 3.0);
darkenColor.g = pow(darkenColor.g, 3.0);
darkenColor.b = pow(darkenColor.b, 3.0);

gl_FragColor = mix(gl_FragColor, darkenColor, dist);


//L1

float curveSpoint = 0.1;


gl_FragColor.r = (gl_FragColor.r - curveSpoint)*(gl_FragColor.r + curveSpoint - 2.0)/((1.0-curveSpoint)*(curveSpoint-1.0));
gl_FragColor.g = (gl_FragColor.g - curveSpoint)*(gl_FragColor.g + curveSpoint - 2.0)/((1.0-curveSpoint)*(curveSpoint-1.0));
gl_FragColor.b = (gl_FragColor.b - curveSpoint)*(gl_FragColor.b + curveSpoint - 2.0)/((1.0-curveSpoint)*(curveSpoint-1.0));


gl_FragColor.rgb = AdjustColorRG(gl_FragColor.rgb, vec4(-0.1,-0.1,-0.4,0.0));

gl_FragColor.rgb = clamp(gl_FragColor.rgb, 0.0, 1.0);

gl_FragColor.r = pow(gl_FragColor.r, 1.3);
gl_FragColor.g = pow(gl_FragColor.g, 1.3);
gl_FragColor.b = pow(gl_FragColor.b, 1.3);


vec3 shadowsShift = vec3(0.2, 0.1, 0.0);
vec3 midtonesShift = vec3(0.0, 0.0, 0.0);
vec3 highlightsShift = vec3(0.0, 0.0, 0.0);
int preserveLuminosity = 1;

// Alternative way:
//lowp vec3 lightness = RGBToL(gl_FragColor.rgb);
lowp vec3 lightness = gl_FragColor.rgb;

const lowp float a = 0.25;
const lowp float b = 0.333;
const lowp float scale = 0.7;

lowp vec3 shadows = shadowsShift * (clamp((lightness - b) / -a + 0.5, 0.0, 1.0) * scale);
lowp vec3 midtones = midtonesShift * (clamp((lightness - b) / a + 0.5, 0.0, 1.0) * clamp((lightness + b - 1.0) / -a + 0.5, 0.0, 1.0) * scale);
lowp vec3 highlights = highlightsShift * (clamp((lightness + b - 1.0) / a + 0.5, 0.0, 1.0) * scale);

mediump vec3 newColor = gl_FragColor.rgb + shadows + midtones + highlights;
newColor = clamp(newColor, 0.0, 1.0);

if (preserveLuminosity != 0) {
lowp vec3 newHSL = RGBToHSL(newColor);
lowp float oldLum = RGBToL(gl_FragColor.rgb);
gl_FragColor.rgb = HSLToRGB(vec3(newHSL.x, newHSL.y, oldLum));
} else {
gl_FragColor = vec4(newColor.rgb, gl_FragColor.w);
}


float contrast = 0.85;
gl_FragColor = vec4(((gl_FragColor.rgb - vec3(0.5)) * contrast + vec3(0.5)), gl_FragColor.w);


}