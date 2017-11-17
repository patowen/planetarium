package net.patowen.planetarium.rendering;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.imageio.ImageIO;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

/**
 * Loads and stores all textures, including a placeholder, that are used in
 * the application
 * @author Patrick Owen
 */
public class TextureBank {
	//Magenta and black checkerboard texture used when a texture is missing or
	//fails to load.
	private Texture placeholder;
	
	private Texture blank, noise;
	private Texture starfield, earth, moon;
	
	/**
	 * Loads all textures into the OpenGL context. This contributes the most
	 * to the loading time of the application
	 * @param gl
	 */
	public void initTextures(GL3 gl) {
		createPlaceholderTexture(gl);
		createBlankTexture(gl);
		createNoiseTexture(gl);
		starfield = createCubicTextureFromFile(gl, "starfield2.png");
		earth = createCubicTextureFromFile(gl, "earth2.png");
		moon = createTextureFromFile(gl, "moon.jpg");
	}
	
	public void setTexture(GL3 gl, ETexture texture) {
		switch (texture) {
			case BLANK: blank.bind(gl); break;
			case STARFIELD: starfield.bind(gl); break;
			case EARTH: earth.bind(gl); break;
			case MOON: moon.bind(gl); break;
		}
	}
	
	/**
	 * Loads and returns the texture corresponding to the given filename
	 * @param gl
	 * @param fname the filename of the texture, without its path
	 * @return the newly-created texture
	 */
	private Texture createTextureFromFile(GL3 gl, String fname) {
		try {
			ClassLoader cl = TextureBank.class.getClassLoader();
			InputStream stream = cl.getResourceAsStream("net/patowen/planetarium/textures/"+fname);
			if (stream == null) throw new IOException("Could not find data for texture: " + fname);
			
			final GLProfile glp = gl.getGLProfile();
			
			BufferedImage image = ImageIO.read(stream); // TODO: Perhaps can optimize
			TextureData textureData = AWTTextureIO.newTextureData(glp, image, GL3.GL_SRGB8, GL3.GL_RGB, true);
			Texture tex = TextureIO.newTexture(gl, textureData); // TODO: Perhaps can optimize
			
			gl.glGenerateMipmap(tex.getTarget());
			return tex;
		} catch (IOException e) {
			return placeholder;
		}
	}
	
	private Texture createCubicTextureFromFile(GL3 gl, String fname) {
		try {
			ClassLoader cl = TextureBank.class.getClassLoader();
			InputStream stream = cl.getResourceAsStream("net/patowen/planetarium/textures/"+fname);
			if (stream == null) throw new IOException("Could not find data for texture: " + fname);
			
			final GLProfile glp = gl.getGLProfile();
			
			Texture tex = TextureIO.newTexture(GL3.GL_TEXTURE_CUBE_MAP);
			BufferedImage image = ImageIO.read(stream);
			int size = image.getWidth();
			
			for (int i=0; i<6; i++) {
				BufferedImage subimage = image.getSubimage(0, size * i, size, size);
				BufferedImage copiedSubimage = new BufferedImage(
						image.getColorModel(),
						image.getRaster().createCompatibleWritableRaster(subimage.getWidth(), subimage.getHeight()),
						image.isAlphaPremultiplied(),
						null);
				subimage.copyData(copiedSubimage.getRaster());
				
				TextureData textureData = AWTTextureIO.newTextureData(glp, copiedSubimage, GL3.GL_SRGB8, GL3.GL_RGB, true);
				tex.updateImage(gl, textureData, GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i);
			}
			
			gl.glGenerateMipmap(tex.getTarget());
			return tex;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Creates a clear white 1x1 texture for rendering objects without textures
	 * @param gl
	 */
	private void createBlankTexture(GL3 gl) {
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		int[] white = new int[] {255, 255, 255};
		for (int i=0; i<image.getWidth(); i++) {
			for (int j=0; j<image.getHeight(); j++) {
				raster.setPixel(i, j, white);
			}
		}
		
		blank = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);
		blank.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
	}
	
	private void createNoiseTexture(GL3 gl) {
		BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
		WritableRaster wr = image.getRaster();
		Random r = new Random();
		
		for (int i=0; i<wr.getWidth(); i++) {
			for (int j=0; j<wr.getHeight(); j++) {
				wr.setPixel(i, j, new int[] {r.nextInt(128) + r.nextInt(128), r.nextInt(128) + r.nextInt(128), r.nextInt(128) + r.nextInt(128)});
			}
		}
		
		TextureData textureData = AWTTextureIO.newTextureData(gl.getGLProfile(), image, GL3.GL_RGB8, GL3.GL_RGB, false);
		noise = new Texture(gl, textureData);
		noise.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
		noise.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_NEAREST);
		noise.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, GL3.GL_REPEAT);
		noise.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, GL3.GL_REPEAT);
		
		gl.glActiveTexture(GL3.GL_TEXTURE1);
		noise.bind(gl);
		gl.glActiveTexture(GL3.GL_TEXTURE0);
	}
	
	/**
	 * Creates an 8x8 magenta and black checkerboard pattern to use as a texture if
	 * the correct texture is missing
	 * @param gl
	 */
	private void createPlaceholderTexture(GL3 gl) {
		BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();
		float[] black = new float[] {0, 0, 0};
		float[] magenta = new float[] {255, 0, 255};
		for (int i=0; i<image.getWidth(); i++) {
			for (int j=0; j<image.getHeight(); j++) {
				raster.setPixel(i, j, (i+j)%2==0 ? magenta : black);
			}
		}
		
		placeholder = AWTTextureIO.newTexture(gl.getGLProfile(), image, false);
		placeholder.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_NEAREST);
	}
}
