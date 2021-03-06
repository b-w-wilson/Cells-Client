package me.cells.main;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetErrorCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
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

import java.util.Random;
import java.util.Scanner;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import me.cells.UI.GUI;
import me.cells.UI.GuiLoading;
import me.cells.network.NetworkHandler;
import me.cells.render.CellsTextureLoader;
import me.cells.render.Renderer;
import me.cells.util.Config;
import me.cells.world.CellsWorld;

/**
 * Main class, sets up everything.
 * 
 * @author bruce
 *
 */
public class CellsClient implements Runnable {

	private final static CellsClient MAIN = new CellsClient();
	private static Thread thread;
	private static GUI currentGui = null;
	public long window;
	public static double mouseX = 0;
	public static double mouseY = 0;

	public final Renderer renderer = new Renderer();
	public final EventHandler eventHandler = new EventHandler();
	public static final CellsTextureLoader LOADER = new CellsTextureLoader();
	public static final Random R = new Random();
	public static final Scanner S = new Scanner(System.in);
	public static final NetworkHandler NETWORK_HANDLER = new NetworkHandler();
	public static final CellsWorld WORLD = new CellsWorld();

	//Starting method
	public static void main(String[] args) {
		thread = new Thread(MAIN, "Main Thread");
		thread.setPriority(10);
		thread.start();
		NETWORK_HANDLER.openNetwork();
	}

	/**
	 * Thread run method. Configures a while loop to continuously run while sleeping
	 * for 10 ms. Also runs the {@link #startGame} method
	 */
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

	/**
	 * Does what it says, starts the game. Uses GLFW to create a window, and sets up
	 * some basic openGL parameters. Creates the callbacks for keyboard, mouse etc.
	 * and loads GuiLoading
	 */
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
		window = glfwCreateWindow(Config.WIDTH, Config.HEIGHT, "Window", NULL /* glfwGetPrimaryMonitor() */, NULL);

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
		//GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		//glfwSetWindowPos(window, (vidmode.width() - Config.WIDTH) / 2, (vidmode.height() - Config.HEIGHT) / 2);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(1);//VSYNC
		glfwShowWindow(window);
		GL.createCapabilities();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glViewport(0, 0, Config.WIDTH, Config.HEIGHT);
		//perspectiveGL(120, Config.WIDTH / Config.HEIGHT, 1000, 1);
		//Sets the colour of the background to black.
		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClearDepth(1.0f);
		glEnable(GL11.GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		//glShadeModel(GL_SMOOTH);
		glEnable(GL_TEXTURE_2D);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		//Assigns the current GUI to be a new instance of the "GuiTitleScreen" class.
		switchGUI(new GuiLoading());
		glLoadIdentity();
	}

	/**
	 * Fancy maths to setup an openGL matrix that does what we need it to.
	 * @param fovY
	 * @param aspect
	 * @param zNear
	 * @param zFar
	 */
	void perspectiveGL(double fovY, double aspect, double zNear, double zFar) {
		double fW, fH;
		fH = Math.tan(fovY / 360 * Math.PI) * zNear;
		fW = fH * aspect;

		GL11.glFrustum(-fW, fW, -fH, fH, zNear, zFar);
	}

	/**Ends the game, and destroys any GUI's, windows or callbacks.
	 * 
	 * @throws RuntimeException If something fails
	 */
	public void closeGame() throws RuntimeException {
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

	/**Switch the GUI by replacing the current GUI with the new one from the parameter. Then clear all objects, before calling the init method of the new GUI
	 * 
	 * @param ui new GUI to be put in the currents place
	 */
	public static synchronized void switchGUI(GUI ui) {
		currentGui = ui;
		ui.init();
	}

	/**
	 * Run tick method, seperate to thread Run method, very different, runs every 10 milliseconds and does everything.
	 * @throws RuntimeException
	 */
	public void runTick() throws RuntimeException {
		if (glfwWindowShouldClose(window)) {
			closeGame();
		}
		renderer.render();
	}

	/**
	 * Getter method for the currently displayed GUI, and returns that GUI
	 * @return the current GUI
	 */
	public static GUI getCurrentGui() {
		return currentGui;
	}

	/**
	 * This class getter for external classes needing to refer to this class
	 * @return The running instance of the main class. Used for thread safety
	 */
	public static CellsClient getMain() {
		return MAIN;
	}

}
