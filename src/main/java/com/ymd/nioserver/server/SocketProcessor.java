package com.ymd.nioserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;


public class SocketProcessor implements Runnable {
	
	private Queue<Socket> inboundSocketQueue = null;
	private Queue<Message> outboundMessageQueue = new LinkedList<Message>();
	private IMessageReaderFactory messageReaderFactory = null;
	
	//Reader selector
	private Selector readSelector = null;
	
	private static AtomicLong socketSeed = new AtomicLong(1);
	private Map<Long, Socket> socketMap = new HashMap<Long, Socket>();
	
	private ByteBuffer readByteBuffer = ByteBuffer.allocate(1024);
	
	public SocketProcessor(Queue<Socket> inboundSocketQueue, IMessageReaderFactory messageReaderFactory) throws IOException {
		this.inboundSocketQueue = inboundSocketQueue;
		this.messageReaderFactory = messageReaderFactory;
		
		//
		this.readSelector = Selector.open();
	}
	
	
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			try {
				takeNewSockets();
				readFromSockets();
				writeToSockets();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeToSockets() throws IOException {

        // Take all new messages from outboundMessageQueue
        takeNewOutboundMessages();

        // Cancel all sockets which have no more data to write.
        cancelEmptySockets();

        // Register all sockets that *have* data and which are not yet registered.
        registerNonEmptySockets();

        // Select from the Selector.
        int writeReady = this.writeSelector.selectNow();

        if(writeReady > 0){
            Set<SelectionKey>      selectionKeys = this.writeSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator   = selectionKeys.iterator();

            while(keyIterator.hasNext()){
                SelectionKey key = keyIterator.next();

                Socket socket = (Socket) key.attachment();

                socket.messageWriter.write(socket, this.writeByteBuffer);

                if(socket.messageWriter.isEmpty()){
                    this.nonEmptyToEmptySockets.add(socket);
                }

                keyIterator.remove();
            }

            selectionKeys.clear();

        }
    }

    private void takeNewOutboundMessages() {
        Message outMessage = this.outboundMessageQueue.poll();
        while(outMessage != null){
            Socket socket = this.socketMap.get(outMessage.socketId);

            if(socket != null){
                MessageWriter messageWriter = socket.messageWriter;
                if(messageWriter.isEmpty()){
                    messageWriter.enqueue(outMessage);
                    nonEmptyToEmptySockets.remove(socket);
                    emptyToNonEmptySockets.add(socket);    //not necessary if removed from nonEmptyToEmptySockets in prev. statement.
                } else{
                   messageWriter.enqueue(outMessage);
                }
            }

            outMessage = this.outboundMessageQueue.poll();
        }
    }
	
	
	
	
	
	
	
	
	
	
	private void takeNewSockets() throws IOException {
		Socket newSocket = this.inboundSocketQueue.poll();
		
		while(newSocket != null) {
			newSocket.setSocketId(SocketProcessor.socketSeed.incrementAndGet());
			newSocket.getSocketChannel().configureBlocking(false);
			
			newSocket.setMessageReader(this.messageReaderFactory.createMessageReader());
			//newSocket.messageReader.init(this.readMessageBuffer);
            //newSocket.messageWriter = new MessageWriter();
			
			this.socketMap.put(newSocket.getSocketId(), newSocket);
			
			newSocket.getSocketChannel().register(this.readSelector, SelectionKey.OP_READ, newSocket);
			
			newSocket = this.inboundSocketQueue.poll();
		}
		
	}
	
	
	private void readFromSockets() throws IOException {
        int readReady = this.readSelector.selectNow();

        if(readReady > 0){
            Set<SelectionKey> selectedKeys = this.readSelector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();

                readFromSocket(key);

                keyIterator.remove();
            }
            selectedKeys.clear();
        }
    }
	
	 private void readFromSocket(SelectionKey key) throws IOException {
	        Socket socket = (Socket) key.attachment();
	        socket.getMessageReader().read(socket, this.readByteBuffer);

	        List<Message> fullMessages = socket.getMessageReader().getMessages();
	        if(fullMessages.size() > 0){
	            for(Message message : fullMessages){
	                message.socketId = socket.getSocketId();
	                //this.messageProcessor.process(message, this.writeProxy);  //the message processor will eventually push outgoing messages into an IMessageWriter for this socket.
	            }
	            fullMessages.clear();
	        }

	        if(socket.endOfStreamReached){
	            System.out.println("Socket closed: " + socket.getSocketId());
	            this.socketMap.remove(socket.getSocketId());
	            key.attach(null);
	            key.cancel();
	            key.channel().close();
	        }
	    }
	


}
