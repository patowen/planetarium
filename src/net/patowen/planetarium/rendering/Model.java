package net.patowen.planetarium.rendering;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;

public class Model {
	private ArrayList<Vertex> vertices;
	private ArrayList<Integer> elements;
	
	private int vertexArrayObject;
	
	private IntBuffer vertexArrayObjects;
	private IntBuffer bufferObjects;
	
	private int primitiveType;
	private int textureDimension;
	
	public Model() {
		vertices = new ArrayList<>();
		elements = new ArrayList<>();
		primitiveType = GL3.GL_TRIANGLES;
		textureDimension = 2;
	}
	
	public void setPrimitiveType(int primitiveType) {
		this.primitiveType = primitiveType;
	}
	
	public void setTextureDimension(int textureDimension) {
		this.textureDimension = textureDimension;
	}
	
	public void init(GL3 gl) {
		int floatsPerVertex = 3 + 3 + textureDimension;
		int bytesPerVertex = 4 * floatsPerVertex;
		FloatBuffer vertexBuffer = Buffers.newDirectFloatBuffer(floatsPerVertex * vertices.size());
		IntBuffer elementBuffer = Buffers.newDirectIntBuffer(elements.size());
		
		for (Vertex vertex : vertices) {
			vertex.use(vertexBuffer);
		}
		vertexBuffer.rewind();
		
		for (int element : elements) {
			elementBuffer.put(element);
		}
		elementBuffer.rewind();
		
		vertexArrayObjects = Buffers.newDirectIntBuffer(1);
		gl.glGenVertexArrays(1, vertexArrayObjects);
		vertexArrayObject = vertexArrayObjects.get(0);
		
		bufferObjects = Buffers.newDirectIntBuffer(2);
		gl.glGenBuffers(2, bufferObjects);
		int vertexBufferPos = bufferObjects.get(0);
		int elementBufferPos = bufferObjects.get(1);
		
		gl.glBindVertexArray(vertexArrayObject);
		gl.glEnableVertexAttribArray(0);
		gl.glEnableVertexAttribArray(1);
		gl.glEnableVertexAttribArray(2);
		
		gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vertexBufferPos);
		gl.glBufferData(GL3.GL_ARRAY_BUFFER, vertexBuffer.capacity() * 4, vertexBuffer, GL3.GL_STATIC_DRAW);
		gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, bytesPerVertex, 0);
		gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, bytesPerVertex, 4 * 3);
		gl.glVertexAttribPointer(2, textureDimension, GL3.GL_FLOAT, false, bytesPerVertex, 4 * (3 + 3));
		
		gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, elementBufferPos);
		gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity()*4, elementBuffer, GL3.GL_STATIC_DRAW);
		
		gl.glBindVertexArray(0);
	}
	
	public int addVertex(Vertex vertex) {
		int nextVertex = vertices.size();
		vertices.add(vertex);
		return nextVertex;
	}
	
	public void addLine(int v1, int v2) {
		elements.add(v1);
		elements.add(v2);
	}
	
	public void addTriangle(int v1, int v2, int v3) {
		elements.add(v1);
		elements.add(v2);
		elements.add(v3);
	}
	
	public void addQuad(int v1, int v2, int v3, int v4) {
		addTriangle(v1, v2, v3);
		addTriangle(v1, v3, v4);
	}
	
	public void render(GL3 gl) {
		gl.glBindVertexArray(vertexArrayObject);
		gl.glDrawElements(primitiveType, elements.size(), GL3.GL_UNSIGNED_INT, 0);
		gl.glBindVertexArray(0);
	}
}
