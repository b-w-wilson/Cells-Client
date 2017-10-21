package me.cells.util;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class Config {

	static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	public static final int WIDTH = gd.getDefaultConfiguration().getBounds().width;
	public static final int HEIGHT = gd.getDefaultConfiguration().getBounds().height;
	public static final String HOST_ADDRESS = "localhost";
	public static final int PORT = 9738;
	public static boolean running;

}
