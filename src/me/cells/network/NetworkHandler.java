package me.cells.network;

import java.io.IOException;

public class NetworkHandler {
	ThreadNioClient client;

	public void openNetwork() {
		client = new ThreadNioClient();
		Thread t = new Thread(client);
		t.setDaemon(true);
		t.start();

	}
	
	public void sendMessage(String msg) {
		ResponceHandler handler = new ResponceHandler();
		try {
			client.send(msg.getBytes(), handler);
		} catch (IOException e) {
			e.printStackTrace();
		}
		handler.waitForResponse();
	}
}
