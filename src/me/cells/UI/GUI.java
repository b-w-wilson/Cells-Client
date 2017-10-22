package me.cells.UI;


public interface GUI {
	//GUI interface to be implemented by all GUI's. This is to allow for the game to know which GUI is being ran, and call functions that all the GUI's will have,
	//since it is implemented through the GUI.
	
	//getName method, returning the String of the name of the GUI
	public String getName();

	//init method, throwing an exception whenever there is an error
	public void init();
	
	//renderTick method, called to draw onto the screen
	public void renderTick();
	
	//closeGame method, incase any GUI requires any special closing options.
	public void closeGame();
	
	//handleKeyboard method, taking key, scancode, action and mods parameters, and allows any GUI to do GUI specific keyboard handling/
	public void handleKeyboard(int key, int scancode, int action, int mods);

	//onClick method, taking x and y parameters, and allows GUI's to do specific mouse click handling.
	public void onClick(double x, double y);

	/**
	 * Returns the new position of the mouse whenever it is moved
	 * @param xPos
	 * @param yPos
	 */
	public void onMouseMove(double xPos, double yPos);
}
