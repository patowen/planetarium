package net.patowen.planetarium;

/**
 * This class is the entry point of the application.
 * @author Patrick Owen
 */
public class Runner {
	/**
	 * Entry point of the application
	 * @param args
	 */
	public static void main(String[] args) {
		Controller c = new Controller();
		c.init();
		if (!c.createWindow()) {
			System.exit(1);
		};
		
		new Renderer(c);
		
		c.getWindow().setVisible(true);
	}
}
