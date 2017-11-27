package com.tata.selenium.test.customerPriceMgmtCases;

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
public class TC_02_AddCoverage implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_AddCoverage.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/CustomerPriceManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_02_AddCoverage -with TestdataId: " + uniqueDataId);
		String sheetName = "Customer_PriceManagement_Screen";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
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

		test.log(LogStatus.INFO, "Launch Application",
				"Usage: <span style='font-weight:bold;'>Going to Launch App</span>");

		CommonUtils cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		cu.printLogs("Executing Test Case -" + testCaseId + " -with TestdataId : " + uniqueDataId);
		driver = cu.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage = new LoginPage(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		cu.waitForPageLoad("MessagingInstanceHomePage");

		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver, test, sheetName, uniqueDataId,
				testCaseId, properties);
		msgInsHomePage.verifyLogin(test, testCaseId, sheetName);

		NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
				properties);
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		// Validating all editable drop down
		cu.SelectDropDownByVisibleText("ServiceNameLst", dataMap.get("ServiceNameLst"));
		cu.SelectDropDownByVisibleText("CustomerNameLst", dataMap.get("CustomerNameLst"));
		cu.checkEditableDropDown("CustomerAccNameLst", dataMap.get("CustomerAccNameLst"));
		cu.checkEditableDropDown("CustomerProductLst", dataMap.get("CustomerProductLst"));

		// Validating Readonly properties
		cu.checkReadonlyProperty("CurrencyTxt", dataMap.get("CurrencyTxt"));
		cu.checkReadonlyProperty("RatePeriod", dataMap.get("RatePeriod"));

		// click submit and validate the pop-up message
		cu.clickElement("SubmitBtn");
		cu.checkMessage("application_PopUpMessage", "Click submit",
				"Error: No price card available. Please select a price card");

		// click display button
		cu.clickElement("DisplayBtn");

		// Select the coverage checkbox for selected destination
		if ("MT SMS".equalsIgnoreCase(dataMap.get("ServiceNameLst"))) {
			cu.selectCheckBox("dynamicCoverageChk", "$destination$", dataMap.get("dynamicDestination"));
		} else {
			cu.selectCheckBox("dynamicCoverageChk", "$destination$", dataMap.get("dynamicCountry"));
		}
		
		cu.clickElement("SubmitBtn");
		cu.checkMessage("application_PopUpMessage", "Click submit",
				"Error: Please enter a valid offer price and effective date");

		// Click Cancel
		cu.clickElement("CancelBtn");
		cu.checkMessage("application_PopUpMessage", "Click Cancel",
				"Warning: This action will reload the initial price card. Do you want to Continue?");
		cu.getScreenShot("Page refreshed after cancel");

		// Coverage checkbox and select the effective date
		if ("MT SMS".equalsIgnoreCase(dataMap.get("ServiceNameLst"))) {
			cu.selectCheckBox("dynamicCoverageChk", "$destination$", dataMap.get("dynamicDestination"));
		} else {
			cu.selectCheckBox("dynamicCoverageChk", "$destination$", dataMap.get("dynamicCountry"));
		}
		//Click date
		if ("MT SMS".equalsIgnoreCase(dataMap.get("ServiceNameLst"))) {
			cu.clickElement("Effective_DateTxt", "$destination$", dataMap.get("dynamicDestination"));
		} else {
			cu.clickElement("Effective_DateTxt", "$destination$", dataMap.get("dynamicCountry"));
		}
		
		cu.selectCalendarDate("Effective_DateTxt", dataMap.get("Effective_DateTxt"));
		cu.clickElement("SubmitBtn");
		cu.checkMessage("application_PopUpMessage", "Click submit",
				"The new price(s) have been successfully entered and will become active on the effective date. You will receive a confirmation email for reference.");

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
		extent = ExtReport.instance("CustomerPriceManagement");
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
