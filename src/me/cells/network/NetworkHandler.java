package me.cells.network;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class NetworkHandler {
	ThreadNioClient client;

	public void openNetwork() {
		client = new ThreadNioClient();
		Thread t = new Thread(client);
		t.setDaemon(true);
		t.start();
	}
	CountDownLatch latch = new CountDownLatch(1);
	
	public ResponceHandler sendMessage(String msg) {
		ResponceHandler handler = new ResponceHandler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					client.send(msg.getBytes(), handler);
				} catch (IOException e) {
					e.printStackTrace();
				}
				handler.waitForResponse();
				latch.countDown();
			}
		}, "Connection Waiter").start();
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return handler;
	}
}
