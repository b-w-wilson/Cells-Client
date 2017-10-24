package me.cells.network;

import java.io.IOException;

/**
 * The clients way to communicate with the server
 * 
 * @author bruce
 *
 */
public class NetworkHandler {
	ThreadNioClient client;

	/**
	 * Opens the network handling thread, this itself wont send any data until a
	 * connection manager thread from {@link #sendMessage(String, Data) sendMessage}
	 * is created.
	 */
	public void openNetwork() {
		client = new ThreadNioClient();
		Thread t = new Thread(client, "NIO Client");
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Sends a message to the server, taking the message as a string and a byte
	 * buffer for the returned data to be placed in.
	 * 
	 * @param msg
	 *            The data to be sent as a string
	 * @param data
	 *            The data object where inside the receivedData byte buffer the
	 *            information to be placed
	 */
	public void sendMessage(String msg, final Data data) {
		ResponceHandler handler = new ResponceHandler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (handler.rsp == null) {
					try {
						client.send(msg.getBytes(), handler);
					} catch (IOException e) {
						System.out.println("Connection error, stopping");
						continue;
					}
					handler.waitForResponse();
					if (handler.rsp == null) {
						System.out.println("Connection refused, retrying");
						handler.cancelled = false;
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				data.receivedData = handler.rsp;
				System.out.println("Connection complete!");
			}
		}, "Connection Manager").start();

	}
}
