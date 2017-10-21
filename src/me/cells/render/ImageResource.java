package me.cells.render;

import me.cells.main.CellsClient;

public class ImageResource {
	
	public static Resource loading = new ImageResource.Resource("./assets/blocks.png", 45);

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
