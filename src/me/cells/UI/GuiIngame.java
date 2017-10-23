package me.cells.UI;

import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import me.cells.main.CellsClient;
import me.cells.util.Config;
import me.cells.util.EnumDirection;
import me.cells.world.CellTile;

public class GuiIngame extends GuiHelper implements GUI {

	public float zoomLevel = 0;
	public float zoomLevelMot = 0;
	public final float MAX_ZOOM_LEVEL = 5F;
	public final float MIN_ZOOM_LEVEL = 1F;
	public float motionX = 0;
	public float motionY = 0;
	public int posX = 0;
	public int posY = 0;
	public EnumDirection screenMovementDir;

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
		glClearColor((float) (255F / 255F), (float) (237F / 255F), (float) (212F / 255F), 1.0f);

		handleScreenMovement();
		System.out.println(zoomLevel);
		GL11.glTranslated(posX, posY, (float)(1 / zoomLevel));

		for (CellTile t : CellsClient.WORLD.tiles) {
			int red = (t.tileColour >> 16) & 0xFF;
			int green = (t.tileColour >> 8) & 0xFF;
			int blue = t.tileColour & 0xFF;
			GL11.glColor3d((float) (red / 255F), (float) (green / 255F), (float) (blue / 255F));
			drawTexturedSquare((201 * t.tileX), (201 * t.tileY), 200, 200);
			GL11.glColor3d(0, 0, 0);
			drawTexturedSquare((201 * t.tileX), (201 * t.tileY), 200, 5);

			GL11.glColor3d(1F, 1F, 1F);
		}
		GL11.glDisable(GL11.GL_BLEND);

	}

	private void handleScreenMovement() {
		if (motionX > 0.75F) {
			motionX -= 0.5F;
		} else if (motionX < -0.75F) {
			motionX += 0.5F;
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

		if (screenMovementDir != null) {
			switch (screenMovementDir) {
			case BACK:
				break;
			case DOWN:
				motionY += 1F;
				break;
			case FORWARD:
				break;
			case LEFT:
				motionX += 1F;
				break;
			case RIGHT:
				motionX -= 1F;
				break;
			case UP:
				motionY -= 1F;
				break;
			default:
				break;
			}
		}
		if (posX + motionX <= 0)
			posX += motionX;
		if (posY + motionY <= 0)
			posY += motionY;
		zoomLevel += zoomLevelMot;
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
		int mod = 200;
		if (yPos < mod) {
			screenMovementDir = EnumDirection.UP;
		} else if (yPos > Config.HEIGHT - mod) {
			screenMovementDir = EnumDirection.DOWN;
		} else if (xPos < mod) {
			screenMovementDir = EnumDirection.LEFT;
		} else if (xPos > Config.WIDTH - mod) {
			screenMovementDir = EnumDirection.RIGHT;
		} else {
			screenMovementDir = null;
		}
	}

	@Override
	public float getRotation() {
		return 180;
	}

	@Override
	public void handleScrollEvent(double xoffset, double yoffset) {
		if (zoomLevel + zoomLevelMot < MAX_ZOOM_LEVEL) {
			zoomLevelMot += yoffset/10;//TODO FIX
		} else if (zoomLevel + zoomLevelMot > MIN_ZOOM_LEVEL) {
			zoomLevelMot += yoffset/10;
		}
	}

	@Override
	public float getScale() {
		return zoomLevel;
	}
	

}
