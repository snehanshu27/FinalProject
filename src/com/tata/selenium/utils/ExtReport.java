package com.tata.selenium.utils;


import com.relevantcodes.extentreports.ExtentReports;
import com.tata.selenium.constants.ApplicationConstants;

public class ExtReport implements ApplicationConstants {

	private ExtReport() {
	}

	public static ExtentReports instance(String moduleName) {
		ExtentReports extent;
		String path;
		path = REPORT_PATH + "//" + moduleName + CommonUtils.getTimeStamp() + ".html";
				
		CommonUtils.printConsole("ReportPath: "+path);
		extent = new ExtentReports(path, false);
		extent.config().documentTitle("TataMessagingExhangeApp").reportName("TataMessagingExhangeApp  Automation");

		return extent;
	}

}
