package net.patowen.planetarium.rendering;

import java.nio.Buffer;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLUniformData;

public class ShaderLink<E extends ShaderUniformData> {
	public E data;
	
	private final String name; // TODO: Try caching locations
	private final Buffer buffer;
	
	public ShaderLink(String name, E data) {
		this.name = name;
		this.data = data;
		
		buffer = data.createBuffer();
	}
	
	public void update(GL3 gl, int shaderProgram) {
		data.addToBuffer(buffer);
		buffer.rewind();
		GLUniformData uniformData = data.getGLUniformData(name, buffer);
		uniformData.setLocation(gl.glGetUniformLocation(shaderProgram, name));
		gl.glUniform(uniformData);
	}
}
