package net.patowen.planetarium.model;

import net.patowen.planetarium.math.Vector3;
import net.patowen.planetarium.rendering.EShaderProgram;
import net.patowen.planetarium.rendering.ETexture;
import net.patowen.planetarium.rendering.Model;
import net.patowen.planetarium.rendering.SceneNodeType;
import net.patowen.planetarium.rendering.Vertex;

public class StarfieldCubic extends SceneNodeType {
	private int numSteps = 64;
	private double radius = 1000;
	
	public StarfieldCubic() {
		Model model = new Model();
		model.setTextureDimension(3);
		
		int[][] posX = new int[numSteps + 1][numSteps + 1];
		int[][] posY = new int[numSteps + 1][numSteps + 1];
		int[][] posZ = new int[numSteps + 1][numSteps + 1];
		
		int[][] negX = new int[numSteps + 1][numSteps + 1];
		int[][] negY = new int[numSteps + 1][numSteps + 1];
		int[][] negZ = new int[numSteps + 1][numSteps + 1];
		
		for (int i=0; i<=numSteps; i++) {
			for (int j=0; j<=numSteps; j++) {
				double u = (double)i / numSteps * 2 - 1;
				double v = (double)j / numSteps * 2 - 1;
				
				posX[i][j] = addVertex(model, new Vector3(1, u, v));
				negX[i][j] = addVertex(model, new Vector3(-1, u, v));
				
				if (i != 0 && i != numSteps) {
					posY[i][j] = addVertex(model, new Vector3(u, 1, v));
					negY[i][j] = addVertex(model, new Vector3(u, -1, v));
					
					if (j != 0 && j != numSteps) {
						posZ[i][j] = addVertex(model, new Vector3(u, v, 1));
						negZ[i][j] = addVertex(model, new Vector3(u, v, -1));
					}
				}
			}
		}
		
		// Take note of vertices shared between faces.
		for (int i=0; i<=numSteps; i++) {
			negY[0][i] = negX[0][i];
			negY[numSteps][i] = posX[0][i];
			
			posY[0][i] = negX[numSteps][i];
			posY[numSteps][i] = posX[numSteps][i];
			
			negZ[0][i] = negX[i][0];
			negZ[numSteps][i] = posX[i][0];
			
			posZ[0][i] = negX[i][numSteps];
			posZ[numSteps][i] = posX[i][numSteps];
		}
		
		for (int i=0; i<=numSteps; i++) {
			negZ[i][0] = negY[i][0];
			negZ[i][numSteps] = posY[i][0];
			
			posZ[i][0] = negY[i][numSteps];
			posZ[i][numSteps] = posY[i][numSteps];
		}
		
		for (int i=0; i<numSteps; i++) {
			for (int j=0; j<numSteps; j++) {
				model.addQuad(posX[i][j], posX[i][j+1], posX[i+1][j+1], posX[i+1][j]);
				model.addQuad(negX[i][j], negX[i+1][j], negX[i+1][j+1], negX[i][j+1]);
				
				model.addQuad(posY[i][j], posY[i+1][j], posY[i+1][j+1], posY[i][j+1]);
				model.addQuad(negY[i][j], negY[i][j+1], negY[i+1][j+1], negY[i+1][j]);
				
				model.addQuad(posZ[i][j], posZ[i][j+1], posZ[i+1][j+1], posZ[i+1][j]);
				model.addQuad(negZ[i][j], negZ[i+1][j], negZ[i+1][j+1], negZ[i][j+1]);
			}
		}
		
		setShaderProgram(EShaderProgram.CUBIC);
		setModel(model);
		setTexture(ETexture.STARFIELD);
		
		setColor(new float[] {0, 0, 0});
		setEmission(new float[] {1, 1, 1});
	}
	
	private int addVertex(Model model, Vector3 sphereCoords) {
		sphereCoords.normalize();
		Vector3 pos = new Vector3(sphereCoords.x * radius, sphereCoords.y * radius, sphereCoords.z * radius);
		Vector3 normal = new Vector3(-sphereCoords.x, -sphereCoords.y, -sphereCoords.z);
		Vector3 texCoord = new Vector3(sphereCoords);
		normal.normalize();
		return model.addVertex(new Vertex(pos, normal, texCoord));
	}
}
