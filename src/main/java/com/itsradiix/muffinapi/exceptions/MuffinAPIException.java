package com.itsradiix.muffinapi.exceptions;

public class MuffinAPIException extends Exception {
	public MuffinAPIException(String errorMessage, Throwable err){
		super(errorMessage, err);
	}

	public MuffinAPIException(String errorMessage) {
		super(errorMessage);
	}
}
