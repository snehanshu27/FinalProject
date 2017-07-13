package com.tata.selenium.utils;

import org.apache.log4j.Logger;

/**
 * @date
 * @author Devbrath Singh
 * @description Log Class containing methods to write logs in txt file
 */

public class Log {
	
	
	// Initialize Log4j logs
	private static Logger logStatic = Logger.getLogger(Log.class.getName());
	
	private Log() {
	  }

	/*This is to print log for the beginning of the test case, as we usually
	 run so many test cases as a test suite*/
	
	public static void startTestCase(String sTestCaseName) {

		logStatic.info("$$$$$$$$$$$$$$$$$$$$$                 " + sTestCaseName + " -------S---T---A---R---T-"
				+ "        $$$$$$$$$$$$$$$$$$$$$$$$$");

	}
	
	// This is to print log for the ending of the test case
	public static void endTestCase(String sTestCaseName) {

		logStatic.info("XXXXXXXXXXXXXXXXXXXXXXX             " + sTestCaseName + " -------E---N---D-"
				+ "             XXXXXXXXXXXXXXXXXXXXXX");
		logStatic.info("X");
		logStatic.info("X");
		logStatic.info("X");
		logStatic.info("X");
	}

	// Need to create these methods, so that they can be called
	public static void info(String message) {
		logStatic.info(message);
	}

	public static void warn(String message) {
		logStatic.warn(message);
	}

	public static void error(String message) {
		logStatic.error(message);
	}

	public static void fatal(String message) {
		logStatic.fatal(message);
	}

	public static void debug(String message) {
		logStatic.debug(message);
	}

}
