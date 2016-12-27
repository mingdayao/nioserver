package com.ubs.eq.posttrade.feeenginewrapper.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class SocketAccepter implements Runnable {
	
	/**
	 * TODO: if need to use blocking Queue
	 */
	private Queue<Socket> socketQueue;
	
	private int tcpPort;
	
	/**
	 * TODO; add to constructor
	 */
	private long sleepTime = 10;  
	
	public SocketAccepter(int tcpPort, Queue<Socket> socketQueue) {
		this.socketQueue = socketQueue;
		this.tcpPort = tcpPort;
	}
	
	public void run() {
		try {
			ServerSocketChannel ssc = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress(this.tcpPort));
			ssc.configureBlocking(false);
			Selector acceptSelector = Selector.open();
			
			//register
			ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
			
			//TODO;
			//above move to constructor method
			
			
			//select
			while(!Thread.currentThread().isInterrupted()) {
				int acceptReadyNum = acceptSelector.select();
				
				if(acceptReadyNum == 0) {
					TimeUnit.MILLISECONDS.sleep(this.sleepTime);
					continue;
				}
				
				if(acceptReadyNum > 0) {
					Set<SelectionKey> selectedKeys = acceptSelector.selectedKeys();
		            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

		            while(keyIterator.hasNext()) {
		                SelectionKey key = keyIterator.next();
		                
		                if(key.isAcceptable()) {
		                	ServerSocketChannel channel = (ServerSocketChannel) key.channel();
		                	SocketChannel sc = channel.accept();
		                	this.socketQueue.add(new Socket(sc));
		                }

		                keyIterator.remove();
		            }
		            
		            selectedKeys.clear();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
