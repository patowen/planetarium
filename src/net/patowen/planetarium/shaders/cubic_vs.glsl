#version 150

uniform mat4 transform;
uniform mat4 perspective;

in vec3 vertex_in;
in vec3 normal_in;
in vec3 tex_coord_in;

out vec3 vertex;
out vec3 normal;
out vec3 tex_coord;

void main() {
	vertex = vertex_in;
	tex_coord = tex_coord_in;
	normal = normal_in;
	gl_Position = perspective*transform*vec4(vertex_in, 1.0);
}
