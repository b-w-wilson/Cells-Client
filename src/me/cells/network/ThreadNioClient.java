package me.cells.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.cells.util.Config;

/**
 * The NIO connection client. This does all the communicating, and is run on its
 * own thread. E.g. dont touch pls, as its very delicate
 * 
 * @author bruce
 *
 */
public class ThreadNioClient implements Runnable {

	// The selector we'll be monitoring
	private Selector selector;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);

	// A list of PendingChange instances
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();

	// Maps a SocketChannel to a list of bytebuffer instances(e.g. the data waiting to be sent to the server)
	private Map<SocketChannel, List<ByteBuffer>> pendingData = new HashMap<SocketChannel, List<ByteBuffer>>();

	// Maps a SocketChannel to a ResponceHandler, e.g. the pieces of code waiting for data to be received from the server
	private Map<SocketChannel, ResponceHandler> rspHandlers = Collections
			.synchronizedMap(new HashMap<SocketChannel, ResponceHandler>());

	public void run() {
		try {
			this.selector = SelectorProvider.provider().openSelector();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				// Process any pending changes to sockets, e.g. registering it with the selector or changing its operation(interest)
				synchronized (this.pendingChanges) {
					Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = (ChangeRequest) changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
							break;
						case ChangeRequest.REGISTER:
							change.socket.register(this.selector, change.ops);
							break;
						}
					}
					this.pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid()) {
						continue;
					}

					// Check what event is available and deal with it
					if (key.isConnectable()) {//Has the server acknowledged the client and added it to the selector?
						this.completeConnection(key);
					} else if (key.isReadable()) {//Has data ready to be read?
						this.read(key);
					} else if (key.isWritable()) {//Is ready to have data written?
						this.write(key);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//Queue data to be sent to the server, and registering a responce handler
	public void send(byte[] data, ResponceHandler handler) throws IOException {
		SocketChannel socket = this.initiateConnection();
		// Register the response handler
		synchronized (rspHandlers) {
			this.rspHandlers.put(socket, handler);
		}

		// And queue the data we want written
		synchronized (this.pendingData) {
			List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(socket);
			if (queue == null) {
				queue = new ArrayList<ByteBuffer>();
				this.pendingData.put(socket, queue);
			}
			queue.add(ByteBuffer.wrap(data));
		}

		// Finally, wake up our selecting thread so it can make the required changes
		this.selector.wakeup();
	}

	//Setup a connection to the server
	private SocketChannel initiateConnection() throws IOException {
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);

		// Connect to server
		socketChannel.connect(new InetSocketAddress(Config.HOST_ADDRESS, Config.PORT));

		// Queue a channel registration since the caller is not the selecting thread. As part of the registration we'll register an interest in connection events. These are raised when a channel is ready to complete connection establishment.
		synchronized (this.pendingChanges) {
			this.pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}

		return socketChannel;
	}

	//Once the channel is ready to be connected, this method is called and sets it to write, the connection is now ready for data transfer. Begin by writing the waiting data
	private void completeConnection(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			// Cancel the channel's registration with our selector
			synchronized (rspHandlers) {
				ResponceHandler handler = (ResponceHandler) this.rspHandlers.get(socketChannel);
				handler.cancel();
			}
			key.cancel();
			return;
		}

		// Register an interest in writing on this channel
		key.interestOps(SelectionKey.OP_WRITE);
	}

	//Read incoming data sent from the server
	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(this.readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		// Handle the response
		byte[] rspData = new byte[numRead];
		System.arraycopy(this.readBuffer.array(), 0, rspData, 0, numRead);

		// Look up the handler for this channel
		ResponceHandler handler = (ResponceHandler) this.rspHandlers.get(socketChannel);

		// And pass the response to it
		if (handler.handleResponse(rspData)) {
			// The handler has seen enough, close the connection
			socketChannel.close();
			socketChannel.keyFor(this.selector).cancel();
			rspHandlers.remove(socketChannel);

		}
	}

	//Actually write(send over network) data to the server
	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (this.pendingData) {
			List<ByteBuffer> queue = (List<ByteBuffer>) this.pendingData.get(socketChannel);

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) queue.get(0);
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested in writing on this socket. Switch back to waiting for data.
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}
}