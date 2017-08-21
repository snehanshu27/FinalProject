package com.tata.selenium.constants;


/**
 * @date 
 * @author  
 * @description Interface class used to store APPLICATION CONSTANTS
 */
public interface ApplicationConstants {
	static final String REPORT_PATH= "./reports";
	static final boolean CHECK_XML_RESULT = true;
	static final String LOG_FILENAME= "./log4j.properties";
	static final String OBJECT_REPO_FILEPATH = "./data/SupplierProvisioning.properties";
	static final String DATA_REPO_FILEPATH = "./data";
	static final String DATA_FILEPATH = "./data/Test_Data.xlsx";
	//static final String DB_DATA_FILEPATH = "./data/Test_Data_DB.xlsx";
	static final String DB_DATA_FILEPATH = "D:/MMX/DatabaseTestingMaven2/data/result/DB_Result_MMX3.xlsx";
	static final String DATA_FILEPATH_NEW = "./data/Test_Data_new.xlsx";
	static final String BROWSER = "browser.xml";
	static final String SCREENSHOT_PATH = "./reports/ScreenShots/";
	static final String REPORT_SCREENSHOT_PATH = "./ScreenShots/";
	static final String LOG_FILEPATH = "./TataMessagingExchangeApp.log";
	static final String DRIVER_PATH="./lib";
	public static final String dev="";
	public static String FILE_DOWNLOAD_PATH="C:\\selenium\\files\\";
	public static int implicitWait = 3;
//	public static final String APP_URL ="https://10.133.43.10:8443/MessagingInstance";
	public static final String APP_URL = System.getProperty("appURL");
	
	public static final boolean RUN_IN_REMOTE=true;
	public static final String REMOTE_HOST_IP= System.getProperty("remoteHostIP");
	public static final String GRID_HUB_PORT= System.getProperty("remoteGridHubPort");
	public static final String DESKTOP_WIN_CONTROL_PORT= System.getProperty("remoteWinControlPort");

//	public static final String REMOTE_HOST_IP= "192.168.40.43";//"camttvdapp16";
//	public static final String GRID_HUB_PORT= "4444";
//	public static final String DESKTOP_WIN_CONTROL_PORT= "9090";
}
