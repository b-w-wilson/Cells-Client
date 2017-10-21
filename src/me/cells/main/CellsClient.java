package me.cells.main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_NICEST;
import static org.lwjgl.opengl.GL11.GL_PERSPECTIVE_CORRECTION_HINT;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glClearDepth;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glHint;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.IntBuffer;
import java.util.Random;
import java.util.Scanner;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import me.cells.UI.GUI;
import me.cells.UI.GuiLoading;
import me.cells.network.NetworkHandler;
import me.cells.render.CellsRenderer;
import me.cells.render.CellsTextureLoader;
import me.cells.util.Config;
import me.cells.world.CellsWorld;

// Implements runnable thread
public class CellsClient implements Runnable {

	private final static CellsClient MAIN = new CellsClient();
	private static Thread thread;
	private static GUI currentGui = null;
	public long window;
	public static double mouseX = 0;
	public static double mouseY = 0;
	

	public final CellsRenderer renderer = new CellsRenderer();
	public final EventHandler eventHandler = new EventHandler();
	public static final CellsTextureLoader LOADER = new CellsTextureLoader();
	public static final Random R = new Random();
	public static final Scanner S = new Scanner(System.in);
	public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
	public static final CellsWorld WORLD = new CellsWorld();

	//Initial method. This is the method called by the JVM to start the program
	public static void main(String[] args) {
		thread = new Thread(MAIN, "Main Thread");
		thread.setPriority(10);
		thread.start();
		//Open networking
		NETWORK_HANDLER.openNetwork();
	}

	//Main run method, called when the thread is started.
	@Override
	public void run() {
		//Assign true to running, and call the startGame method, the game has now been started
		Config.running = true;
		try {
			startGame();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		try {
			//Runs the tick every 10 milliseconds while running is true.
			while (Config.running) {
				runTick();
				Thread.sleep(10);
			}
		} catch (Throwable throwable) {
			throwable.printStackTrace(System.err);
		}
	}

	//Ends the game, and destroys any GUI's, windows or callbacks.
	public void closeGame() throws Exception {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		Config.running = false;
		if (getCurrentGui() != null) {
			getCurrentGui().closeGame();
		} else {
			throw new RuntimeException("somethings broken as usual!");
		}
	}

	//Starts the game 
	private void startGame() {
		//Creates the window using GLFW, setting up error callbacks, and the actual window itself.
		//glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err));
		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);
		//GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
		window = glfwCreateWindow(Config.WIDTH, Config.HEIGHT, "Window", glfwGetPrimaryMonitor(), NULL);
	
		
		if (window == NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		//Sets up the key, mouse button and mouse position callbacks to be inside the event handler using lambda expressions.
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			eventHandler.handleKeyEvent(window, key, scancode, action, mods);
		});

		glfwSetMouseButtonCallback(window, ((window, button, action, mods) -> {
			eventHandler.handleMouseEvent(window, button, action, mods);
		}));

		glfwSetCursorPosCallback(window, ((window, xpos, ypos) -> {
			eventHandler.handleMousePositionEvent(window, xpos, ypos);
		}));
		
		glfwSetScrollCallback(window, ((window, xoffset, yoffset) -> {
			eventHandler.handleScrollEvent(window, xoffset, yoffset);

		}));

		//Sets up the rendering of the window with VSync capabilities, and the correct scaling.
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		//glfwSetWindowPos(window, (vidmode.width() - Config.WIDTH) / 2, (vidmode.height() - Config.HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);//VSYNC
		glfwShowWindow(window);
		GL.createCapabilities();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, Config.WIDTH, Config.HEIGHT);
		//Sets the colour of the background to black.
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClearDepth(1.0f);
		//glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glShadeModel(GL_SMOOTH);
		glEnable(GL_TEXTURE_2D);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		//Assigns the current GUI to be a new instance of the "GuiTitleScreen" class.
		switchGUI(new GuiLoading());
		glLoadIdentity();
	}

	//Switch the GUI by replacing the current GUI with the new one from the parameter. Then clear all objects, before calling the init method of the new GUI
	public static void switchGUI(GUI ui) {
		currentGui = ui;
		ui.init();
	}

	//Run tick method, runs every 10 milliseconds and renders everything in the game.
	public void runTick() throws Throwable {
		if (glfwWindowShouldClose(window)) {
			closeGame();
		}
		//NETWORK_HANDLER.sendMessage(S.nextLine());
		renderer.render();
	}

	//Getter method for the currently displayed GUI, and returns that GUI
	public static GUI getCurrentGui() {
		return currentGui;
	}

	//This class getter for external classes needing to refer to this class
	public static CellsClient getMain() {
		return MAIN;
	}

}
