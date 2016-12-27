package com.ubs.eq.posttrade.feeenginewrapper.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ubs.eq.posttrade.feeenginewrapper.server.IMessageReader;
import com.ubs.eq.posttrade.feeenginewrapper.server.Message;
import com.ubs.eq.posttrade.feeenginewrapper.server.Socket;

public class TcpMessageReader implements IMessageReader {
	
	private List<Message> completeMessages = new ArrayList<Message>();
    private Message nextMessage = new Message();
	
	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException {
		@SuppressWarnings("unused")
		int bytesRead = socket.read(byteBuffer);
        byteBuffer.flip();

        while(byteBuffer.hasRemaining()) {
        	
        	this.nextMessage.writeToMessage(byteBuffer);
        	
        	if(this.nextMessage.filledCompleted()) {
        		completeMessages.add(this.nextMessage);
        		this.nextMessage = new Message();
        	}
        }
        
        byteBuffer.clear();
	}

	public List<Message> getMessages() {
		return this.completeMessages;
	}
}
