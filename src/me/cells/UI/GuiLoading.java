package me.cells.UI;

import static org.lwjgl.opengl.GL11.glClearColor;

import org.lwjgl.opengl.GL11;

import me.cells.main.CellsClient;
import me.cells.network.Data;
import me.cells.render.ImageResource;
import me.cells.util.Config;
import me.cells.world.CellTile;

/**
 * Loading screen GUI, so if there is no connection its all handled.
 * 
 * @author bruce
 *
 */
public class GuiLoading extends GuiHelper implements GUI {

	/** Stores the data recieved from the server. Due to thread limitations(and safety to avoid race conditions), this is final and local.
	 * Though, remember the reference is only final, the datainside the object can still be modified.
	 * 
	 */
	final Data DATA = new Data();

	@Override
	public String getName() {
		return "loading";
	}

	@Override
	public void init() {
		CellsClient.NETWORK_HANDLER.sendMessage("world", DATA);
	}

	@Override
	public void preRender() {
		GL11.glRotatef(180, 1F, 0, 0);
		GL11.glTranslatef(-1F, 1F, 0);
		GL11.glRotatef(getRotation(), 1F, 0, 0);
		GL11.glScalef((1 / (float) Config.WIDTH) * getScale(), (1 / (float) Config.HEIGHT) * getScale(), 0F);
	}

	@Override
	public void renderTick() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		glClearColor((float) (255F / 255F), (float) (237F / 255F), (float) (212F / 255F), 1.0f);
		this.drawAnimatedTexturedSquare(ImageResource.loading, Config.WIDTH / 2 - 100, Config.HEIGHT / 2 - 100, 200,
				200);
		if (DATA.receivedData != null) {
			String[] tiles = new String(DATA.receivedData).split(";");
			for (String t : tiles) {
				String[] tile = t.split(":");
				CellsClient.WORLD.tiles.add(new CellTile(Integer.valueOf(tile[0]), Integer.valueOf(tile[1]),
						Float.valueOf(tile[2]), Integer.valueOf(tile[3])));
			}
			CellsClient.switchGUI(new GuiIngame());
		}
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void closeGame() {

	}

	@Override
	public void handleKeyboard(int key, int scancode, int action, int mods) {

	}

	@Override
	public void onClick(double x, double y) {

	}

	@Override
	public void onMouseMove(double xPos, double yPos) {

	}

	@Override
	public float getRotation() {
		return 180;
	}

	@Override
	public void handleScrollEvent(double xoffset, double yoffset) {
	}

	@Override
	public float getScale() {
		return 2F;
	}

}
