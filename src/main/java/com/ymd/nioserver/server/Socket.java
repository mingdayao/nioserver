package com.ymd.nioserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Socket {
	
	private SocketChannel socketChannel;
	private IMessageReader messageReader = null;
	public boolean endOfStreamReached = false;
	
	private long SocketId;
	
	public long getSocketId() {
		return SocketId;
	}

	public void setSocketId(long socketId) {
		SocketId = socketId;
	}
	
	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public Socket(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public IMessageReader getMessageReader() {
		return messageReader;
	}

	public void setMessageReader(IMessageReader messageReader) {
		this.messageReader = messageReader;
	}
	
	//
    public int read(ByteBuffer byteBuffer) throws IOException {
        int bytesRead = this.socketChannel.read(byteBuffer);
        int totalBytesRead = bytesRead;

        while(bytesRead > 0){
            bytesRead = this.socketChannel.read(byteBuffer);
            totalBytesRead += bytesRead;
        }
        if(bytesRead == -1){
            this.endOfStreamReached = true;
        }

        return totalBytesRead;
    }
	
}
