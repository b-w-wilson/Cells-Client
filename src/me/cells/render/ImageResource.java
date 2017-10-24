package me.cells.render;

import me.cells.main.CellsClient;

/**
 * Where images are stored for later use Right now, all textures are loaded at
 * the start of the game, but it later can be configured if we have a lot to
 * only load when needed.
 * 
 * @author bruce
 *
 */
public class ImageResource {

	//Static list of textures.
	public static Resource loading = new ImageResource.Resource("./assets/blocks.png", 45);

	/**
	 * Inner class of the Resource. Stores all nessesary data about the image
	 * loaded.
	 * 
	 * @author bruce
	 *
	 */
	public static class Resource {
		public int resourceLocation;
		public int frameCount;

		public Resource(String resourceLocation, int frameCount) {
			this.frameCount = frameCount;
			this.resourceLocation = registerTexture(resourceLocation);
		}

		private int registerTexture(String loc) {
			return CellsClient.LOADER.loadTexture(loc);
		}
	}

}
