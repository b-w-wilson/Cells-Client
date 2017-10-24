package me.cells.UI;

import org.lwjgl.opengl.GL11;

import me.cells.render.ImageResource.Resource;
import me.cells.util.Config;

/**
 * Some helper methods for gui drawing
 * @author bruce
 *
 */
public class GuiHelper {

	/**
	 * Draws a square to the screen. If a texture is bound before hand it will draw
	 * the texture scaled to the size of the square
	 * 
	 * @param startX
	 *            Start X pos of the square (centre screen is 0,0)
	 * @param startY
	 *            Start Y pos of the square (centre screen is 0,0)
	 * @param sizeX
	 *            X size of the square
	 * @param sizeY
	 *            Y Size of the square
	 */
	public void drawTexturedSquare(int startX, int startY, int sizeX, int sizeY) {
		GL11.glPushMatrix();
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, 1);
			GL11.glVertex2d(startX, startY);

			GL11.glTexCoord2f(0, 0);
			GL11.glVertex2d(startX, startY + sizeY);

			GL11.glTexCoord2f(1, 0);
			GL11.glVertex2d(startX + sizeX, startY + sizeY);

			GL11.glTexCoord2f(1, 1);
			GL11.glVertex2d(startX + sizeX, startY);
		}
		GL11.glEnd();
		GL11.glPopMatrix();
	}

	int frameNum = 0;
	int prog = 0;
	/**
	 * Draws a square with an animation. 
	 * NOTE: Draw animated square does not need the image bound before hand, but drawTexturedSquare DOES.
	 * @param resource The resource located in {@link}ImageResource to get the frame count from and texture. 
	 * @param xPos
	 *            Start X pos of the square (centre screen is 0,0)
	 * @param yPos
	 *            Start Y pos of the square (centre screen is 0,0)
	 * @param xSize
	 *            X size of the square
	 * @param ySize
	 *            Y Size of the square
	 */
	public void drawAnimatedTexturedSquare(Resource resource, int xPos, int yPos, int xSize, int ySize) {
		float f = 1F;
		if (resource.frameCount > 0) {
			prog++;
			if (prog > 5) {
				prog = 0;
				if (frameNum >= resource.frameCount) {
					frameNum = 0;
				} else {
					frameNum++;
				}
			}
			f = ((float) 1 / (resource.frameCount));
		}
		//Bind the spesified resource to the renderer. Draw the object to the screen using GLQUADS. If the image is static, no special rendering is done, if the object is animated, 
		//scale the texture so that only the correct frame in the animation is displayed.
		GL11.glPushMatrix();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, resource.resourceLocation);
		GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glTexCoord2f(0, f * frameNum);
			GL11.glVertex2d(xPos, yPos);

			GL11.glTexCoord2f(0, f * (frameNum - 1));
			GL11.glVertex2d(xPos, yPos + ySize);

			GL11.glTexCoord2f(1, f * (frameNum - 1));
			GL11.glVertex2d(xPos + xSize, yPos + ySize);

			GL11.glTexCoord2f(1, f * frameNum);
			GL11.glVertex2d(xPos + xSize, yPos);
		}
		GL11.glEnd();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glPopMatrix();
	}

	/**
	 * Sets up a basic screen with the size of the users screen.
	 * @param scale Scale to set the screen too.
	 */
	public void standardPreRender(float scale) {
		GL11.glScalef((1 / (float) Config.WIDTH) * scale, (1 / (float) Config.HEIGHT) * scale, 0F);
	}

}
