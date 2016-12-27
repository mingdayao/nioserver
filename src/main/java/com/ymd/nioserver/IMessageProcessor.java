package com.ubs.eq.posttrade.feeenginewrapper.processor;

import com.ubs.eq.posttrade.feeenginewrapper.server.Message;

/**
 * 
 * using one chain of responsibility to handle all the activities
 *
 */
public interface IMessageProcessor {
	
	public String process(Message message) throws ProcessException;
	
	
}
