package me.cells.main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class NetworkHandler {

	static Socket client;
	static DataInputStream is = null;
	static BufferedReader d = null;

	public static void openNetwork() {
		try {
			client = new Socket("localhost", 9738);
			System.out.println("Listening on port " + client.getLocalPort());

			is = new DataInputStream(client.getInputStream());
			d = new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			System.out.println(e);
		}

	}

	public static void processInput() {
		String responseLine;
		try {
			while ((responseLine = d.readLine()) != null) {
				System.out.println("Server: " + responseLine);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void closeNetwork(){
		try {
			d.close();
			is.close();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
