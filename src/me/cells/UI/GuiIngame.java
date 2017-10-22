package me.cells.UI;

import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import me.cells.main.CellsClient;
import me.cells.world.CellTile;

public class GuiIngame extends GuiHelper implements GUI {

	public float zoomLevel = 0;
	public final float MAX_ZOOM_LEVEL = 5F;
	public final float MIN_ZOOM_LEVEL = 1F;
	public int motionX = 0;
	public int motionY = 0;
	public int posX = 0;
	public int posY = 0;
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public void init() {

	}

	@Override
	public void renderTick() {
	    GL11.glEnable(GL11.GL_BLEND);
	    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glClearColor((float)(255F / 255F), (float)(237F / 255F), (float)(212F / 255F), 1.0f);
		GL11.glTranslated(posX, posY, zoomLevel);
		for(CellTile t : CellsClient.WORLD.tiles) {
			int red = (t.tileColour >> 16) & 0xFF;
			int green = (t.tileColour >> 8) & 0xFF;
			int blue = t.tileColour & 0xFF;
			GL11.glColor3d((float)(red / 255F), (float)(green / 255F), (float)(blue / 255F));
			drawTexturedSquare((201*t.tileX), (201*t.tileY), 200, 200);
			GL11.glColor3d(0, 0, 0);
			drawTexturedSquare((201*t.tileX), (201*t.tileY), 200, 5);

			GL11.glColor3d(1F, 1F, 1F);
		}
        GL11.glDisable(GL11.GL_BLEND);

	}

	@Override
	public void closeGame() {
	}

	@Override
	public void handleKeyboard(int key, int scancode, int action, int mods) {

	}

	@Override
	public void onClick(double x, double y) {
		System.out.println(x + "   " + y);

	}

	@Override
	public void onMouseMove(double xPos, double yPos) {
		//System.out.println(xPos + "   " + yPos);
	}

}
