#version 150

#define NUM_LIGHTS 1

uniform sampler2D texture_sampler;
uniform sampler2D noise_texture;

in vec3 vertex;
in vec3 normal;
in vec2 tex_coord;

uniform mat4 transform;

//Lighting
uniform vec3 light_ambient;
uniform vec3 light_position[NUM_LIGHTS];
uniform vec3 light_diffuse[NUM_LIGHTS];

//Material
uniform vec3 material_ambient;
uniform vec3 material_diffuse;
uniform vec3 material_emission;

//Noise
uniform vec2 noise_displacement;

out vec4 fragColor;

vec3 linearToSrgb(vec3 linear) {
	return vec3(
		linear.r <= 0.0031308 ? 12.92 * linear.r : 1.055 * pow(linear.r, 1.0/2.4) - 0.055,
		linear.g <= 0.0031308 ? 12.92 * linear.g : 1.055 * pow(linear.g, 1.0/2.4) - 0.055,
		linear.b <= 0.0031308 ? 12.92 * linear.b : 1.055 * pow(linear.b, 1.0/2.4) - 0.055);
}

vec3 srgbToLinear(vec3 srgb) {
	return vec3(
		srgb.r <= 0.04045 ? srgb.r / 12.92 : pow((srgb.r + 0.055) / 1.055, 2.4),
		srgb.g <= 0.04045 ? srgb.g / 12.92 : pow((srgb.g + 0.055) / 1.055, 2.4),
		srgb.b <= 0.04045 ? srgb.b / 12.92 : pow((srgb.b + 0.055) / 1.055, 2.4));
}

void main() {
	vec3 color_multiplier = light_ambient * material_ambient + material_emission;
	vec3 norm = normalize(normal);
	
	for (int i=0; i<NUM_LIGHTS; i++) {
		vec3 light_direction = light_position[i] - vertex;
		light_direction = normalize(light_direction);
		
		float directness = max(0, dot(light_direction, norm));
		color_multiplier += directness * light_diffuse[i] * material_diffuse;
	}
	
	vec3 linearColor = color_multiplier * texture(texture_sampler, tex_coord).rgb;
	vec3 srgbColor = linearToSrgb(linearColor);
	vec3 perturbedSrgbColor = srgbColor + (texture(noise_texture, gl_FragCoord.xy / 256.0 + noise_displacement).rgb - 0.5) / 128.0;

	fragColor = vec4(srgbToLinear(perturbedSrgbColor), 0);
}
