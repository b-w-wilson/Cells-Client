package me.cells.main;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import org.lwjgl.glfw.GLFW;

import me.cells.UI.GUI;
import me.cells.util.Config;

public class EventHandler {

	/**
	 * Handles all key events, taking 5 parameters, all sent from the lambda
	 * expression in the startGame method in the Main class.
	 **/
	public void handleKeyEvent(long window, int key, int scancode, int action, int mods) {
		switch (key) {
		//If the ESC key is pressed, close the game.
		case GLFW_KEY_ESCAPE:
			if (action == GLFW_RELEASE) {
				glfwSetWindowShouldClose(window, true);
			}
			break;
		}
		//Get the currentGUI and call its handleKeyboard method, this allows for specific GUI's to wait for keys aswell.
		CellsClient.getCurrentGui().handleKeyboard(key, scancode, action, mods);
	}

	/**
	 * Handles all mouse click events, taking 4 parameters, all sent from the lambda
	 * expression in the startGame method in the Main class.
	 **/
	public void handleMouseEvent(long window, int button, int action, int mods) {
		//If the left mouse button is pressed, when it is released do this.
		if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_RELEASE) {
			//Get the current position of the mouse 
			double x = CellsClient.mouseX;
			double y = (Config.HEIGHT - CellsClient.mouseY);
			//Get the current GUI, and call the GUI's individual onClick method, allowing for access to mouse clicks from any GUI
			GUI gui = CellsClient.getCurrentGui();
			gui.onClick(x, y);
		}
	}

	/**
	 * Handles the mouses current position event. Assigns the two variables, mouseX
	 * and mouseY in the Main class to the current position of the mouse.
	 **/
	public void handleMousePositionEvent(long window, double xPos, double yPos) {
		GUI gui = CellsClient.getCurrentGui();
		gui.onMouseMove(xPos, yPos);
		CellsClient.mouseX = xPos;
		CellsClient.mouseY = yPos;
	}

	/**
	 * Handles all the scroll events. When the wheel is moved up, yoffset is positive, and visa versa.
	 * @param window
	 * @param xoffset
	 * @param yoffset
	 */
	public void handleScrollEvent(long window, double xoffset, double yoffset) {
		CellsClient.getCurrentGui().handleScrollEvent(xoffset, yoffset);
	}

}
