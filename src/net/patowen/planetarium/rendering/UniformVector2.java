package net.patowen.planetarium.rendering;
import java.nio.Buffer;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLUniformData;

import net.patowen.planetarium.math.Vector2;

public class UniformVector2 implements ShaderUniformData {
	public Vector2 vector;
	
	public UniformVector2(Vector2 initialVector) {
		vector = initialVector;
	}
	
	public Buffer createBuffer() {
		return Buffers.newDirectFloatBuffer(2);
	}
	
	public void addToBuffer(Buffer buf) {
		((FloatBuffer)buf).put(new float[] {
				(float)vector.x, (float)vector.y });
	}
	
	public GLUniformData getGLUniformData(String name, Buffer buffer) {
		return new GLUniformData(name, 2, (FloatBuffer)buffer);
	}
}
