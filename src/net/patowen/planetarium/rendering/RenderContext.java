package net.patowen.planetarium.rendering;

import java.io.InputStream;
import java.util.Scanner;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;

/**
 * Helper class to allow shaders to be loaded from files in the shaders package
 * @author Patrick Owen
 */
public class RenderContext {
	private ShaderUniformHandler shaderUniformHandler;
	private TextureBank textureBank;
	
	private int standardShader, cubicShader;
	private int currentProgram;
	
	public void initShaders(GL3 gl) {
		initStandardShader(gl);
		initCubicShader(gl);
		
		shaderUniformHandler = new ShaderUniformHandler(gl);
		
		textureBank = new TextureBank();
		textureBank.initTextures(gl);
		
		currentProgram = standardShader;
	}
	
	public ShaderUniformHandler getShaderUniformHandler() {
		return shaderUniformHandler;
	}
	
	public void setShader(GL3 gl, EShaderProgram shader) {
		switch (shader) {
		case STANDARD:
			currentProgram = standardShader;
			break;
		case CUBIC:
			currentProgram = cubicShader;
			break;
		}
	}
	
	public void setTexture(GL3 gl, ETexture texture) {
		textureBank.setTexture(gl, texture);
	}
	
	private void initStandardShader(GL3 gl) {
		ShaderProgram prog = new ShaderProgram();
		prog.init(gl);
		
		ShaderCode vsCode = getShaderCode(GL3.GL_VERTEX_SHADER, "standard_vs");
		vsCode.compile(gl);
		prog.add(vsCode);
		
		ShaderCode fsCode = getShaderCode(GL3.GL_FRAGMENT_SHADER, "standard_fs");
		fsCode.compile(gl);
		prog.add(fsCode);
		
		gl.glBindAttribLocation(prog.program(), 0, "vertex_in");
		gl.glBindAttribLocation(prog.program(), 1, "normal_in");
		gl.glBindAttribLocation(prog.program(), 2, "tex_coord_in");
		
		prog.link(gl, System.err);
		prog.validateProgram(gl, System.err);
		
		standardShader = prog.program();
	}
	
	private void initCubicShader(GL3 gl) {
		ShaderProgram prog = new ShaderProgram();
		prog.init(gl);
		
		ShaderCode vsCode = getShaderCode(GL3.GL_VERTEX_SHADER, "cubic_vs");
		vsCode.compile(gl);
		prog.add(vsCode);
		
		ShaderCode fsCode = getShaderCode(GL3.GL_FRAGMENT_SHADER, "cubic_fs");
		fsCode.compile(gl);
		prog.add(fsCode);
		
		gl.glBindAttribLocation(prog.program(), 0, "vertex_in");
		gl.glBindAttribLocation(prog.program(), 1, "normal_in");
		gl.glBindAttribLocation(prog.program(), 2, "tex_coord_in");
		
		prog.link(gl, System.err);
		prog.validateProgram(gl, System.err);
		
		cubicShader = prog.program();
	}
	
	/**
	 * Returns a {@code ShaderCode} object for the specified file
	 * @param type GL_VERTEX_SHADER, GL_GEOMETRY_SHADER, GL_FRAGMENT_SHADER, etc.
	 * @param fname the name of the shader file, without its directory structure
	 * @return the fully prepared, but not compiled, {@code ShaderCode} object
	 */
	private static ShaderCode getShaderCode(int type, String fname) {
		ClassLoader cl = RenderContext.class.getClassLoader();
		InputStream stream = cl.getResourceAsStream("net/patowen/planetarium/shaders/"+fname+".glsl");
		
		StringBuilder str = new StringBuilder();
		
		Scanner scan = new Scanner(stream);
		
		while (scan.hasNextLine()) {
			str.append(scan.nextLine() + "\n");
		}
		
		scan.close();
		
		ShaderCode code = new ShaderCode(type, 1, new CharSequence[][]{{str.toString()}});
		return code;
	}
	
	/**
	 * Sets all the uniforms of the shader to the currently stored values. This
	 * should be called before drawing anything if anything was changed.
	 * @param gl
	 */
	public void update(GL3 gl) {
		gl.glUseProgram(currentProgram);
		shaderUniformHandler.update(gl, currentProgram);
	}
}
