package me.cells.UI;

import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import me.cells.main.CellsClient;
import me.cells.util.Config;
import me.cells.util.EnumDirection;
import me.cells.world.CellTile;

/**
 * The GUI for when actually inside the game
 * @author bruce
 *
 */
public class GuiIngame extends GuiHelper implements GUI {

	public final float MAX_ZOOM_LEVEL = 5F;
	public final float MIN_ZOOM_LEVEL = 1F;
	public float zoomLevel = MIN_ZOOM_LEVEL;
	public float zoomLevelMot = 0;
	public float motionX = 0;
	public float motionY = 0;
	public float distanceX = 0;
	public float distanceY = 0;
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
	public void preRender() {
		standardPreRender(getScale());
	}

	@Override
	public void renderTick() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glClearColor((float) (255F / 255F), (float) (237F / 255F), (float) (212F / 255F), 1.0f);

		handleScreenMovement();
		GL11.glTranslated(posX, posY, (float) (1 / zoomLevel));

		for (CellTile t : CellsClient.WORLD.tiles) {
			int red = (t.tileColour >> 16) & 0xFF;
			int green = (t.tileColour >> 8) & 0xFF;
			int blue = t.tileColour & 0xFF;
			GL11.glColor3d((float) (red / 255F), (float) (green / 255F), (float) (blue / 255F));
			drawTexturedSquare((205 * t.tileX), (205 * t.tileY), 200, 200);
			GL11.glColor3d(0, 0, 0);

			GL11.glTranslated(0, 0, 0.1F);
			drawTexturedSquare((205 * t.tileX), (205 * t.tileY), 200, 50);

			GL11.glColor3d(1F, 1F, 1F);
		}
		GL11.glDisable(GL11.GL_BLEND);

	}

	private void handleScreenMovement() {
		if (distanceX > Config.WIDTH / 4 || distanceX < -Config.WIDTH / 4) {
			if (motionX <= 20F && motionX >= -20F)
				if (motionX > 0) {
					motionX -= (distanceX + Config.WIDTH / 4) / 200;
				} else {
					motionX -= (distanceX - Config.WIDTH / 4) / 200;
				}
		}
		if (distanceY > Config.HEIGHT / 4 || distanceY < -Config.HEIGHT / 4) {
			if (motionY <= 20F && motionY >= -20F)
				if (motionY > 0) {
					motionY += (distanceY - Config.HEIGHT / 4) / 200;
				} else {
					motionY += (distanceY + Config.HEIGHT / 4) / 200;
				}
		}
		posX += motionX;
		posY += motionY;
		if (zoomLevel >= MIN_ZOOM_LEVEL && zoomLevel + zoomLevelMot < MAX_ZOOM_LEVEL) {
			zoomLevel += zoomLevelMot;
		} else if (zoomLevel < MIN_ZOOM_LEVEL) {
			zoomLevel = MIN_ZOOM_LEVEL;
		}

		if (zoomLevelMot > 0.01F) {
			zoomLevelMot -= 0.01F;
		} else if (zoomLevelMot < -0.01F) {
			zoomLevelMot += 0.01F;
		} else {
			zoomLevelMot = 0;
		}

		if (motionX > 1F) {
			motionX -= 1F;
		} else if (motionX < -1F) {
			motionX += 1F;
		} else {
			motionX = 0;
		}

		if (motionY > 0.75F) {
			motionY -= 0.5F;
		} else if (motionY < -0.75F) {
			motionY += 0.5F;
		} else {
			motionY = 0;
		}

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
		distanceX = (int) xPos - Config.WIDTH / 2;
		distanceY = (int) yPos - Config.HEIGHT / 2;
	}

	@Override
	public float getRotation() {
		return 180;
	}

	@Override
	public void handleScrollEvent(double xoffset, double yoffset) {
		if (zoomLevel + (float) ((yoffset < 2 ? yoffset : 2) / 20F) < MAX_ZOOM_LEVEL && yoffset > 0) {
			zoomLevelMot += (float) ((yoffset < 2 ? yoffset : 2) / 20F);//TODO FIX
		} else if (zoomLevel + (float) ((yoffset > -2 ? yoffset : -2) / 20F) > MIN_ZOOM_LEVEL && yoffset < 0) {
			zoomLevelMot += (float) ((yoffset > -2 ? yoffset : -2) / 20F);
		} else {
			zoomLevelMot = 0;
		}
	}

	@Override
	public float getScale() {
		return zoomLevel;
	}

}
