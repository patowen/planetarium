package net.patowen.planetarium.rendering;

import com.jogamp.opengl.GL3;

import net.patowen.planetarium.math.Transform;

/**
 * Represents a mesh with a texture that can be drawn at any location
 * @author Patrick Owen
 */
public abstract class SceneNodeType {
	private ETexture texture;
	private EShaderProgram shader;
	
	private float[] color;
	private float[] emission;
	
	private Model model;
	
	public SceneNodeType() {
		color = new float[] {1, 1, 1};
		emission = new float[] {0, 0, 0};
		model = null;
		shader = EShaderProgram.STANDARD;
		texture = ETexture.BLANK;
	}
	
	public void setModel(Model model) {
		this.model = model;
	}
	
	/**
	 * Sets the texture with which to render the scene node
	 * @param texture a reference to the texture with which to render the scene node
	 */
	public void setTexture(ETexture texture) {
		this.texture = texture;
	}
	
	public void setShaderProgram(EShaderProgram shaderProgram) {
		this.shader = shaderProgram;
	}
	
	/**
	 * Sets the color with which to render the scene node
	 * @param color a 3-element array representing the color with which to render the scene node
	 */
	public void setColor(float[] color) {
		System.arraycopy(color, 0, this.color, 0, 3);
	}
	
	public void setEmission(float[] color) {
		System.arraycopy(color, 0, emission, 0, 3);
	}
	
	/**
	 * Requests buffers from OpenGL to allow the vertex, normal, texCoord, and element buffers to
	 * be used when rendering. This must be called before {@code render}
	 * @param gl
	 */
	public void renderInit(GL3 gl) {
		model.init(gl);
	}
	
	/**
	 * Renders the scene node transformed by the given transformation.
	 * @param gl
	 * @param t where the scene node should be relocated before rendering
	 */
	public void render(GL3 gl, RenderContext sh, Transform t) {
		ShaderUniformHandler suh = sh.getShaderUniformHandler();
		
		sh.setShader(gl, shader);
		
		suh.pushTransformation();
		suh.addTransformation(t);
		suh.setMaterialAmbient(color);
		suh.setMaterialDiffuse(color);
		suh.setMaterialEmission(emission);
		sh.setTexture(gl, texture);
		sh.update(gl);
		
		model.render(gl);
		
		suh.popTransformation();
	}
}
