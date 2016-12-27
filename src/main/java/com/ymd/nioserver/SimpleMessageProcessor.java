package com.ubs.eq.posttrade.feeenginewrapper.processor;

import com.ubs.eq.posttrade.feeenginewrapper.server.Message;

public class SimpleMessageProcessor implements IMessageProcessor {

	@Override
	public String process(Message message) throws ProcessException {
		
		String s = new String(message.messageBody);
        System.out.println("data1 = " +s);   //This simulate the return processor
		
		return "simple";
	}

}
