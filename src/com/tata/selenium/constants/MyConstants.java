package com.tata.selenium.constants;

/**
 * @date 
 * @author 
 * @description MyConstants class used to store APPLICATION CONSTANTS for Remote Machine execution
 */
public class MyConstants {
	
	private static boolean executeTestCase = true;
	private static String HUB_PATH = "";
	private static String env= "";
	private static  String browser= "";
	
	private MyConstants(){
		
	}
	
	public static boolean isExecuteTestCase() {
		return executeTestCase;
	}
	public static void setExecuteTestCase(boolean executeTestCase) {
		MyConstants.executeTestCase = executeTestCase;
	}
	public static String getHUB_PATH() {
		return HUB_PATH;
	}
	public static void setHUB_PATH(String hUB_PATH) {
		HUB_PATH = hUB_PATH;
	}
	public static String getEnv() {
		return env;
	}
	public static void setEnv(String env) {
		MyConstants.env = env;
	}
	public static String getBrowser() {
		return browser;
	}
	public static void setBrowser(String browser) {
		MyConstants.browser = browser;
	}
	
}
