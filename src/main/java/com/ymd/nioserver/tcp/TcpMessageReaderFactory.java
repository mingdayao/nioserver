package com.ymd.nioserver.tcp;

import com.ymd.nioserver.server.IMessageReader;
import com.ymd.nioserver.server.IMessageReaderFactory;

public class TcpMessageReaderFactory implements IMessageReaderFactory {
	
	public TcpMessageReaderFactory() {}
	
	public IMessageReader createMessageReader() {
		return new TcpMessageReader();
	}

}
