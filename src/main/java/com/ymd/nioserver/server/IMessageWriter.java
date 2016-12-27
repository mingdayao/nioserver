package com.ubs.eq.posttrade.feeenginewrapper.server;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface IMessageWriter {
	
	public void write(Socket socket, ByteBuffer byteBuffer) throws IOException;

	public void put(Message message);
	
	public boolean isEmpty();
	
}
