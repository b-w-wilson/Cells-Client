package me.cells.render;

import me.cells.main.CellsClient;

public class ImageResource {
	
	public static Resource fonts = new ImageResource.Resource("./assets/fonts.png", 0);
	
	public static class Resource {
		public int resourceLocation;
		public int frameCount;
		public Resource(String resourceLocation, int frameCount) {
			this.frameCount = frameCount;
			this.resourceLocation = registerTexture(resourceLocation);
		}
		private int registerTexture(String loc) {
			return CellsClient.getMain().loader.loadTexture(loc);
		}
	}
	
}
