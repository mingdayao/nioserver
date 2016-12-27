package com.ubs.eq.posttrade.feeenginewrapper.processor;

public class ProcessException extends Exception {

	private static final long serialVersionUID = -1343994084079763644L;

	public ProcessException() {
		super();
	}

	public ProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessException(String message) {
		super(message);
	}
}
