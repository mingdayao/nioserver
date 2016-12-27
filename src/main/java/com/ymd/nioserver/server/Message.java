package com.ubs.eq.posttrade.feeenginewrapper.server;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * MessageFormat like
 * 00000000{"opt":"CalculateFee","Params":{"prar1":"michael"}}
 */
public class Message {

	public byte[] messageBody = null;
	
	public byte[] messageHead = null;
	
	//default value
	public int capacity = 0;
	
	public static int HEAD_CAPACITY = 8;
	
	private int headLength = 0;
	private int bodyLength = 0;
	
	private long socketId = 0;
	
	//create read message
	public Message() {
		messageHead = new byte[Message.HEAD_CAPACITY];
		capacity = 0;
		headLength = 0;
		bodyLength = 0;
	}
	
	//create write message
	public Message(String data) {
		byte[] msg = data.getBytes();
		headLength = 0;
		bodyLength = 0;
		capacity = msg.length;
		messageBody = msg;
		messageHead = this.getMessageHeader(msg.length).getBytes();
	}
	
	private String getMessageHeader(int headLength) {
		return String.format("%08d", headLength);
	}
	
	public int getHeadLength() {
		return headLength;
	}
	
	public void addHeadLength(int length) {
		this.headLength += length;
	}

	public int getBodyLength() {
		return bodyLength;
	}
	
	public void addBodyLength(int length) {
		this.bodyLength += length;
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
    
    public boolean readCompleted() {
    	return headLength == HEAD_CAPACITY && bodyLength == capacity ? true : false; 
    }
    
    public int headRemainings() {
    	return this.headLength<HEAD_CAPACITY ? HEAD_CAPACITY-this.headLength : 0;
    }
    
    public int bodyRemainings() {
    	return this.bodyLength<this.capacity? this.capacity-this.bodyLength : 0;
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
    	/*
    	Message message = new Message();
    	System.out.println(message.getBodyCapacity("00000010"));
    	*/
    	
    	System.out.println(new Message().getMessageHeader(12));
    }

	public long getSocketId() {
		return socketId;
	}

	public void setSocketId(long socketId) {
		this.socketId = socketId;
	}

}
