package com.tata.selenium.test.ReceivablesCases;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.pages.LoginPage;
import com.tata.selenium.pages.MessagingInstanceHomePage;
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_01_ReceivablesUIValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_ReceivablesUIValidation.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/Receivables.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_01_ReceivablesUIValidation -with TestdataId: " + uniqueDataId);
		//Storing the sheet name
		String sheetName = "Receivables_Screen";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
		//Passing the sheet name 	
			excel.setExcelFile(DATA_FILEPATH, sheetName);
		//Storing the entire row with respect to the uniqueDataId and SheetName passed in data map.
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
		//Writing into log files in case there is a error while reading data from excel sheet
			LOGGER.error("Exception while reading data from EXCEL file for test case : " + testCaseId
					+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
			CommonUtils.printConsole("Exception while reading data from EXCEL file for test case : " + testCaseId
					+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
			Reporter.log("Exception while reading data from EXCEL file for test case : " + testCaseId
					+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
			test.log(LogStatus.FAIL, "Exception while reading data from EXCEL file for test case : " + testCaseId
					+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName,
					"Exception while reading data from EXCEL file for test case : " + testCaseId
							+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e,
					uniqueDataId, "Result_Errors");
			Assert.fail("Error occured while trying to login to the application  -  " +e);
		}
      //Writing logs into log file step by step  
		test.log(LogStatus.INFO, "Launch Application",
				"Usage: <span style='font-weight:bold;'>Going to Launch App</span>");
     //Below Class has the command to launch URL based on the browser value passed in XML file 
		CommonUtils cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		cu.printLogs("Executing Test Case -" + testCaseId + " -with TestdataId : " + uniqueDataId);
		driver = cu.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage = new LoginPage(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		//this methods waits for the complete page to load
		cu.waitForPageLoad("MessagingInstanceHomePage");

		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver, test, sheetName, uniqueDataId,
				testCaseId, properties);
		msgInsHomePage.verifyLogin(test, testCaseId, sheetName);
        //Object of the class is defined to access the function with the parameters required
		NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
				properties);
		//gets the navigation from excel sheet and gives the same to function
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		// Validating all editable drop down
		cu.checkEditableDropDown("ServiceLst", dataMap.get("ServiceLst"));
		cu.checkEditableDropDown("CustomerLst", dataMap.get("CustomerLst"));
		cu.checkEditableDropDown("CustomerAccLst", dataMap.get("CustomerAccLst"));
		cu.checkEditableDropDown("CountryLst", dataMap.get("CountryLst"));
		cu.checkEditableDropDown("DestinationLst", dataMap.get("DestinationLst"));

		//validating non editable text boxes
		cu.checkReadonlyProperty("CurrencyTxt");
		
		// Validating all buttons
		cu.checkElementPresence("DisplayBtn");
		cu.checkElementPresence("CancelBtn");
		cu.checkElementPresence("ExportBtn");
		
		if (dataMap.get("FromDate") != null && dataMap.get("FromDate").trim().length() >0 ){
			cu.checkEditableDate("FromDate",dataMap.get("FromDate"));
		}else{
			String todayAsString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
			cu.checkEditableDate("FromDate",todayAsString);
		}
		
		if (dataMap.get("ToDate") != null && dataMap.get("ToDate").trim().length() >0 ){
			cu.checkEditableDate("ToDate",dataMap.get("ToDate"));
		}else{
			String todayAsString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
			cu.checkEditableDate("ToDate",todayAsString);
		}
		
		// Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Receivables Screen");
		
		
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Receivables");
	}

	@AfterMethod
	  public void afterMethodFailed(ITestResult result) {		  
		  
		  if(ITestResult.FAILURE ==result.getStatus()
				  && !ExceptionUtils.getRootCauseMessage(result.getThrowable()).startsWith("AssertionError:")){		
			  
			  test.log(LogStatus.FAIL, "Error Ocuured in while executing the test case.<br/> Exception trace:<br/><br/> "
					  			+StringEscapeUtils.escapeHtml3(ExceptionUtils.getStackTrace(result.getThrowable())).replace("\n", "<br/>"));
		  }		 
	  }  
	  
	@AfterMethod(dependsOnMethods="afterMethodFailed")
	@Parameters("testCaseId")
	public void afterMethod(String testCaseId) {
		Log.info("App Logout :: afterClass() method invoked...");
		try {
			Log.endTestCase(testCaseId);
			driver.quit();
			// Ending the Extent test
			extent.endTest(test);
			// Writing the report to HTML format
			extent.flush();
		} catch (Exception e) {
			LOGGER.info(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
