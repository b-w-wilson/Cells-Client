package me.cells.network;

/**
 * Class for temporary holding of returned data, and waiting of connections. 
 * @author bruce
 *
 */
public class ResponceHandler {
	byte[] rsp = null;
	volatile boolean cancelled = false;

	public synchronized boolean handleResponse(byte[] rsp) {
		this.rsp = rsp;
		this.notify();
		return true;
	}

	public synchronized byte[] waitForResponse() {
		while (this.rsp == null && !cancelled) {
			try {
				this.wait();
			} catch (InterruptedException e) {
			}
		}
		if(rsp != null) {
			System.out.println(new String(this.rsp));
		}
		return this.rsp;
	}
	
	public synchronized void cancel() {
		this.cancelled = true;
		this.notify();
	}
}