package net.patowen.planetarium.rendering;


import java.nio.Buffer;
import java.nio.FloatBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLUniformData;

import net.patowen.planetarium.math.Vector3;

public class UniformVector31List implements ShaderUniformData {
	public Vector3[] vectors;
	
	public UniformVector31List(int size) {
		vectors = new Vector3[size];
		
		for (int i=0; i<size; i++) {
			vectors[i] = new Vector3();
		}
	}
	
	public Buffer createBuffer() {
		return Buffers.newDirectFloatBuffer(3*vectors.length);
	}
	
	public void addToBuffer(Buffer buf) {
		for (Vector3 vector : vectors) {
			((FloatBuffer)buf).put(new float[] {
					(float)vector.x, (float)vector.y, (float)vector.z });
		}
	}
	
	public GLUniformData getGLUniformData(String name, Buffer buffer) {
		return new GLUniformData(name, 3, (FloatBuffer)buffer); // TODO: Don't recreate every frame
	}
}
