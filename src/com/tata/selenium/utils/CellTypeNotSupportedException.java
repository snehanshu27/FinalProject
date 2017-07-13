package com.tata.selenium.utils;

/**
 * @class CellTypeNotSupportedException class is to handle runtime exception for sonar code compliance
 * @author prabhatk
 * 
 */
public class CellTypeNotSupportedException extends RuntimeException{

	
	private static final long serialVersionUID = 1L;
	
	public CellTypeNotSupportedException(){
		super();
	}
	
	
	public CellTypeNotSupportedException(String message){
		super(message);
	}
	
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	
	@Override
	public String getMessage() {
		return super.getMessage();
	}
	
}
