package com.tata.selenium.utils;

public class ColumnNameNotFoundException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;
	
	private final String columnName;
	
	public String getColumnName(){
		return columnName;
	}

	public ColumnNameNotFoundException(String message, String columnName){
		super(message);
		this.columnName = columnName;
	}
	
	public ColumnNameNotFoundException(String message, String columnName, Throwable cause){
		super(message, cause);
		this.columnName = columnName;
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	
	@Override
	public String getMessage() {
		return super.getMessage() + "with column name:: " +columnName;
	}

}
