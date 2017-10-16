package me.cells.render;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;

import org.lwjgl.opengl.GL11;

import me.cells.UI.GUI;
import me.cells.main.CellsClient;

public class CellsRenderer {
	//render method, which can throw an exception if something goes wrong. Is called by the runTick method in the main class.
	public void render() throws Exception{
		//Clears the colour and depth bits of the screen
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    glMatrixMode(GL_MODELVIEW);   
	    //Resets the screen
	    glLoadIdentity();
		GUI ui = CellsClient.getMain().getCurrentGui();
		//If the current gui isnt null
		if(ui != null){
			//Set the background colour to white 
		    glClearColor(1F, 1F, 1F, 1);	
		    //Translate and position the screen so that the buttom left hand corner is (0,0), and scale the screen so that it is the same size of the width and height.
		    GL11.glRotatef(180, 1F, 0, 0);
		    GL11.glTranslatef(-1F, 1F, 0);
		    GL11.glRotatef(180, 1F, 0, 0);
		    GL11.glScalef((1/(float)CellsClient.getMain().WIDTH)*2, (1/(float)CellsClient.getMain().HEIGHT)*2, 0);
		    //Call the renderTick method of the current gui
			ui.renderTick();
		}
		//Put the currently constucted frame into shot, getting the next one ready to be drawn.
		glfwSwapBuffers(CellsClient.getMain().window);
		glfwPollEvents();
	}
}
