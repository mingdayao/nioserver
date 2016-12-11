package com.ymd.nioserver.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public interface IMessageReader {

	public void read(Socket socket, ByteBuffer byteBuffer) throws IOException;

	public List<Message> getMessages();

}
