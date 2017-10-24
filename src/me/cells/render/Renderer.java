package me.cells.render;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glLoadIdentity;

import org.lwjgl.opengl.GL11;

import me.cells.UI.GUI;
import me.cells.main.CellsClient;
import me.cells.util.Config;

/**
 * The rendering class, basically so there isnt so much in the run tick inside the client
 * @author bruce
 *
 */
public class Renderer {

	/**
	 * render method, which can throw an exception if something goes wrong. Is
	 * called by the runTick method in the main class.
	 **/
	public void render() throws RuntimeException {
		//Clears the colour and depth bits of the screen
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//Resets the screen
		glLoadIdentity();
		GUI ui = CellsClient.getCurrentGui();
		if (ui != null) {
			ui.preRender();
			ui.renderTick();
		}
		//Put the currently constructed frame into shot, getting the next one ready to be drawn.
		glfwSwapBuffers(CellsClient.getMain().window);
		glfwPollEvents();
	}
}
