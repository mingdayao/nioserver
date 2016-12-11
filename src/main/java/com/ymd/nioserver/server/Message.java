package com.ymd.nioserver.server;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 *MessageFormat like
 * 
 * 0000 0000 {"opt":"CalculateFee","Params":{"prar1":"michael"}}
 * 
 *
 */
public class Message {
	
	public byte[] messageBody = null;
	
	public byte[] messageHead = null;
	
	//default value
	public int capacity = 0;
	
	public static int HEAD_CAPACITY = 8;
	
	private int headLength = 0;
	private int bodyLength = 0;
	
	public long socketId = 0;
	
	public Message() {
		messageHead = new byte[Message.HEAD_CAPACITY];
		capacity = 0;
		headLength = 0;
		bodyLength = 0;
	}
	
	/**
     * Writes data from the ByteBuffer into this message - 
     * meaning into the buffer backing this message.
     *
     * @param byteBuffer The ByteBuffer containing the message data to write.
     * @return
     */
    public void writeToMessage(ByteBuffer byteBuffer){
        int remaining = byteBuffer.remaining();
        
        //Set Header first
        if(this.capacity==0 && remaining>0) {  //get first 8 bytes into
        	if(headLength < HEAD_CAPACITY) {
	        	int bytesToCopy = Math.min(remaining, HEAD_CAPACITY - this.headLength);
	        	byteBuffer.get(messageHead, headLength, bytesToCopy);
	        	headLength += bytesToCopy;
        	}
        	if(headLength == HEAD_CAPACITY) {
        		//get the value of head
        		capacity = this.getBodyCapacity(new String(this.messageHead));
        		messageBody = new byte[capacity];
        		bodyLength = 0;
        	}
        }
        
        remaining = byteBuffer.remaining();
        
        if(bodyLength<capacity && remaining>0) {
        	int bytesToCopy = Math.min(remaining, capacity - this.bodyLength);
        	byteBuffer.get(messageBody, this.bodyLength, bytesToCopy);
        	bodyLength += bytesToCopy;
        }
    }
    
    public boolean filledCompleted() {
    	return headLength == HEAD_CAPACITY && bodyLength == capacity && capacity != 0 ? true : false; 
    		 
    }
    
    private Pattern headPatter = Pattern.compile("^0*(\\d*)$");
    
    //TODO: need to refine here
    private int getBodyCapacity(String head) {
    	if(StringUtils.isNotBlank(head)) {
    		Matcher matcher = headPatter.matcher(head);
    		
    		if(matcher.matches()) {
    			return Integer.valueOf(matcher.group(1));
    		}
    	}
    	return 0;
    }
	
    public static void main(String[] args) {
    	Message message = new Message();
    	System.out.println(message.getBodyCapacity("00000010"));
    }
}
