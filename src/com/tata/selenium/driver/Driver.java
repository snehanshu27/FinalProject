package com.tata.selenium.driver;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.util.SystemOutLogger;
import org.testng.IExecutionListener;
import org.testng.annotations.BeforeClass;

import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.constants.MyConstants;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.XMLParserUtil;

/**
 * @date 
 * @author 
 * @description This class drives the execution with the help of TestNG parameters
 */

public class Driver implements IExecutionListener, ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());
	@BeforeClass
	public void beforeMethodDB(){
		System.out.println("Driver before class.");
	}
	@Override
	public void onExecutionStart() {
		
		DOMConfigurator.configure("log4j.xml");
		XMLParserUtil xmlUtil = new XMLParserUtil();
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		CommonUtils.setTimeStamp("-"+ c.get(Calendar.DATE)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR)+"_"+c.get(Calendar.HOUR_OF_DAY)+"-"+c.get(Calendar.MINUTE)+"-"+c.get(Calendar.SECOND));		
		String browser = xmlUtil.getAttributeValue(BROWSER,"browser");

		//Setting the browser to run
		if ("firefox".equalsIgnoreCase(browser)){
			MyConstants.setBrowser("FIREFOX");
		}
		else if ("internetexplorer".equalsIgnoreCase(browser)) {
			MyConstants.setBrowser("IE");
		}
		else if ("chrome".equalsIgnoreCase(browser)) {
			MyConstants.setBrowser("CHROME");
		}	
		
		//Emptying the log file before every run
		try {
			   BufferedWriter out = new BufferedWriter
		         (new FileWriter(LOG_FILEPATH));
		         out.write("");
		         out.close();
		} catch (Exception e) {
			LOGGER.info("error" +e);
			CommonUtils.printConsole("Error while deleting the log file before execution of test cases: " + e);
		}		
	}

	@Override
	public void onExecutionFinish() {
		CommonUtils.printConsole("Inside onExecutionFinish method...");
	}

}
