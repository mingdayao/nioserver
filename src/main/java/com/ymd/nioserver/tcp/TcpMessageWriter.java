package com.ubs.eq.posttrade.feeenginewrapper.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ubs.eq.posttrade.feeenginewrapper.server.IMessageWriter;
import com.ubs.eq.posttrade.feeenginewrapper.server.Message;
import com.ubs.eq.posttrade.feeenginewrapper.server.Socket;

public class TcpMessageWriter implements IMessageWriter {

	private List<Message> pengdingProcessMessageList = new ArrayList<Message>();
	
    private Message messageInProgress = null;

	@Override
	public void write(Socket socket, ByteBuffer byteBuffer) throws IOException {
		//
		int remainings = byteBuffer.remaining();
		int headRemainings = messageInProgress.headRemainings();
		int writeLength = 0;
		
		if(remainings> 0 && headRemainings>0) {
			writeLength = Math.min(remainings, headRemainings);
			byteBuffer.put(messageInProgress.messageHead, messageInProgress.getHeadLength(), writeLength);
			messageInProgress.addHeadLength(writeLength);
		}
		
		remainings = byteBuffer.remaining();
		int bodyRemainings = messageInProgress.bodyRemainings();
		
		if(remainings>0 && bodyRemainings>0) {
			writeLength = Math.min(remainings, bodyRemainings);
			byteBuffer.put(messageInProgress.messageBody, messageInProgress.getBodyLength(), writeLength);
			messageInProgress.addBodyLength(writeLength);
		}
		
        byteBuffer.flip();

        @SuppressWarnings("unused")
		int bytesWritten = socket.write(byteBuffer);
        byteBuffer.clear();

        if(this.messageInProgress.readCompleted()){
            if(this.pengdingProcessMessageList.size() > 0){
                this.messageInProgress = this.pengdingProcessMessageList.remove(0);
            } else {
            	//todo unregister from selector
                this.messageInProgress = null;  
            }
        }
	}

	@Override
	public void put(Message message) {
		if(messageInProgress == null) {
			messageInProgress = message;
		} else {
			pengdingProcessMessageList.add(message);
		}
	}

	@Override
	public boolean isEmpty() {
		return pengdingProcessMessageList.isEmpty() && messageInProgress == null;
	}

}
