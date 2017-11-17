package net.patowen.planetarium.rendering;
import java.nio.Buffer;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLUniformData;

import net.patowen.planetarium.math.Vector3;

public class UniformVector31 implements ShaderUniformData {
	public Vector3 vector;
	
	public UniformVector31(Vector3 initialVector) {
		vector = initialVector;
	}
	
	public Buffer createBuffer() {
		return Buffers.newDirectFloatBuffer(3);
	}
	
	public void addToBuffer(Buffer buf) {
		((FloatBuffer)buf).put(new float[] {
				(float)vector.x, (float)vector.y, (float)vector.z });
	}
	
	public GLUniformData getGLUniformData(String name, Buffer buffer) {
		return new GLUniformData(name, 3, (FloatBuffer)buffer);
	}
}
