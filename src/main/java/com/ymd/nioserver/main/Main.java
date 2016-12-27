package com.ymd.nioserver.main;

/**
 * 
 * This will help to take as wrapper to call fee engine
 *
 */
public class Main {
	
	public static void main(String[] args) {
		Server server = new Server(9999, new TcpMessageReaderFactory(), new SimpleMessageProcessor());
		server.startUp();
	}
	
}
