package net.patowen.planetarium.rendering;

import java.nio.FloatBuffer;

import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.math.VectorN;

/**
 * The {@code Vertex} class represents a vertex with a texture and normal.
 * @author Patrick Owen
 */
public class Vertex {
	private Vector3 pos;
	private Vector3 norm;
	private VectorN texCoord;
	
	public Vertex(Vector3 pos, Vector3 norm, VectorN texCoord) {
		this.pos = pos;
		this.norm = norm;
		this.texCoord = texCoord;
	}
	
	/**
	 * Places the vertex's x, y, and z coordinates into the given buffers.
	 * @param vertexBuffer the FloatBuffer for the position vector
	 * @param normalBuffer the FloatBuffer for the normal vector
	 */
	public void use(FloatBuffer vertexBuffer, FloatBuffer normalBuffer, FloatBuffer texCoordBuffer) {
		vertexBuffer.put((float)pos.x);
		vertexBuffer.put((float)pos.y);
		vertexBuffer.put((float)pos.z);
		
		normalBuffer.put((float)norm.x);
		normalBuffer.put((float)norm.y);
		normalBuffer.put((float)norm.z);
		
		texCoord.addToFloatBuffer(texCoordBuffer);
	}
	
	public void use(FloatBuffer combinedBuffer) {
		combinedBuffer.put((float)pos.x);
		combinedBuffer.put((float)pos.y);
		combinedBuffer.put((float)pos.z);
		
		combinedBuffer.put((float)norm.x);
		combinedBuffer.put((float)norm.y);
		combinedBuffer.put((float)norm.z);
		
		texCoord.addToFloatBuffer(combinedBuffer);
	}
}
