package com.tata.selenium.test.securityRoles;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

/**
 * @date
 * @author Meenakshi Chellia
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_02_ValidateInputParameters implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_ValidateInputParameters.class.getName());
	String properties = "./data/SecurityRoles.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test;
    @Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_02_ValidateInputParameters - " + uniqueDataId + " for "
				+ dataMap.get("Description"));
		String sheetName = "ValidatingFields_Roles";

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
			Assert.fail("Error occured while trying to login to the application  -  " + e);
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
		cu.default_content();

		// Validating user for eXchange Tab Access
		if (("YES").equals(dataMap.get("Dashboard"))) {
			if (("YES").equals(dataMap.get("Country Status"))) {
				cu.default_content();
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("dashboard_Tab", "$destinationVal$", "Country Status");
				cu.waitForPageLoad("Country Status");
				test.log(LogStatus.PASS, "EXPECTECD:  Country Status page should get opened",
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Country Status page opened successfully</span>");
				cu.default_content();
				cu.SwitchFrames("bottom");
				cu.SwitchFrames("target");
				validateRecords(cu, "cs_service", dataMap.get("CS_Service_Num"), dataMap.get("CS_Service_Val"));
				validateRecords(cu, "cs_custName", dataMap.get("CS_Customer_Num"), dataMap.get("CS_Customer_Val"));
				validateRecords(cu, "cs_custAccName", dataMap.get("CS_CutomerAccount_Num"),
						dataMap.get("CS_CutomerAccount_Val"));
				validateRecords(cu, "cs_destinationCountry", dataMap.get("CS_DestinationCountry_Num"),
						dataMap.get("CS_DestinationCountry_Val"));
				validateRecords(cu, "cs_PoductName", dataMap.get("CS_Product_Num"), dataMap.get("CS_Product_Val"));
				cu.getScreenShot("Validation of Input Parameters field for Country Status screen");
			}

			if (("YES").equals(dataMap.get("Performance Trend"))) {
				cu.default_content();
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("dashboard_Tab", "$destinationVal$", "Performance Trend");
				cu.waitForPageLoad("Performance Trend");
				test.log(LogStatus.INFO, "EXPECTECD:  Performance Trend page should get opened",
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Performance Trend page opened successfully</span>");
				cu.default_content();
				cu.SwitchFrames("bottom");
				cu.SwitchFrames("target");
				validateRecords(cu, "pt_ServiceLst", dataMap.get("PT_ServiceLst_Num"),
						dataMap.get("PT_ServiceLst_Val"));
				validateRecords(cu, "pt_ProductLst", dataMap.get("PT_ProductLst_Num"),
						dataMap.get("PT_ProductLst_Val"));
				validateRecords(cu, "pt_CountryLst", dataMap.get("PT_CountryLst_Num"),
						dataMap.get("PT_CountryLst_Val"));
				validateRecords(cu, "pt_CustomerLst", dataMap.get("PT_CustomerLst_Num"),
						dataMap.get("PT_CustomerLst_Val"));
				validateRecords(cu, "pt_CustomerAccountNameLst", dataMap.get("PT_CustomerAccountNameLst_Num"),
						dataMap.get("PT_CustomerAccountNameLst_Val"));
				validateRecords(cu, "pt_DestinationLst", dataMap.get("PT_DestinationLst_Num"),
						dataMap.get("PT_DestinationLst_Val"));
				cu.getScreenShot("Validation of Input Parameters field for Performance Trend screen");
			}
		}

		if (("YES").equals(dataMap.get("Customer")) && ("YES").equals(dataMap.get("Coverage View"))) {
			cu.default_content();
			cu.SwitchFrames("//iframe[@name='bottom']");
			cu.SwitchFrames("//*[contains(@name,'index')]");
			cu.clickElement("mainTabLink", "$destinationVal$", "Customer");
			cu.clickElement("Customer_Tab", "$destinationVal$", "Coverage View");
			cu.waitForPageLoad("Coverage View");
			test.log(LogStatus.PASS, "EXPECTECD:  Coverage View page should get opened",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  Coverage View page opened successfully</span>");
			cu.default_content();
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
			validateRecords(cu, "cusCoverage_ServiceNameLst", dataMap.get("Customer_Coverage_ServiceNameLst_Num"),
					dataMap.get("Customer_Coverage_ServiceNameLst_Val"));
			validateRecords(cu, "cusCoverage_CustomerNameLst", dataMap.get("Customer_Coverage_CustomerNameLst_Num"),
					dataMap.get("Customer_Coverage_CustomerNameLst_Val"));
			validateRecords(cu, "cusCoverage_CustomerAccNameLst",
					dataMap.get("Customer_Coverage_CustomerAccNameLst_Num"),
					dataMap.get("Customer_Coverage_CustomerAccNameLst_Val"));
			validateRecords(cu, "cusCoverage_CustomerProductLst",
					dataMap.get("Customer_Coverage_CustomerProductLst_Num"),
					dataMap.get("Customer_Coverage_CustomerProductLst_Val"));
			cu.checkNonEditableBox("cusCoverage_CurrencyTxt", dataMap.get("Customer_Coverage_CurrencyTxt"));
			cu.getScreenShot("Validation of Input Parameters field for Customer Coverage View screen");

		}

		if (("YES").equals(dataMap.get("Financial")) && ("YES").equals(dataMap.get("Receivable"))) {

			cu.default_content();
			cu.SwitchFrames("//iframe[@name='bottom']");
			cu.SwitchFrames("//*[contains(@name,'index')]");
			cu.clickElement("mainTabLink", "$destinationVal$", "Financial");
			cu.clickElement("Financial_Tab", "$destinationVal$", "Receivable");
			cu.waitForPageLoad("Receivable");
			test.log(LogStatus.PASS, "EXPECTECD:  Receivable page should get opened",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  Receivable page opened successfully</span>");
			cu.default_content();
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
			validateRecords(cu, "rec_ServiceLst", dataMap.get("Recivable_ServiceLst_Num"),
					dataMap.get("Recivable_ServiceLst_Val"));
			validateRecords(cu, "rec_CustomerLst", dataMap.get("Recivable_CustomerLst_Num"),
					dataMap.get("Recivable_CustomerLst_Val"));
			validateRecords(cu, "rec_CustomerAccLst", dataMap.get("Recivable_CustomerAccLst_Num"),
					dataMap.get("Recivable_CustomerAccLst_Val"));
			validateRecords(cu, "rec_CountryLst", dataMap.get("Recivable_CountryLst_Num"),
					dataMap.get("Recivable_CountryLst_Val"));
			validateRecords(cu, "rec_DestinationLst", dataMap.get("Recivable_DestinationLst_Num"),
					dataMap.get("Recivable_DestinationLst_Val"));
			cu.checkNonEditableBox("rec_CurrencyTxt", dataMap.get("Recivable_CurrencyTxt"));
			cu.getScreenShot("Validation of Input Parameters field for Receivable screen");
		}

		if (("YES").equals(dataMap.get("Reporting")) && ("YES").equals(dataMap.get("Delivery Statistics"))) {
			cu.default_content();
			cu.SwitchFrames("//iframe[@name='bottom']");
			cu.SwitchFrames("//*[contains(@name,'index')]");
			cu.clickElement("mainTabLink", "$destinationVal$", "Reporting");
			cu.clickElement("Reporting_Tab", "$destinationVal$", "Delivery Statistics");
			cu.waitForPageLoad("Delivery Statistics");
			test.log(LogStatus.PASS, "EXPECTECD:  Delivery Statistics page should get opened",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  Delivery Statistics page opened successfully</span>");
			cu.default_content();
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
			validateRecords(cu, "DeliveryStat_ServiceLst", dataMap.get("DS_Service_Num"),
					dataMap.get("DS_Service_Val"));
			validateRecords(cu, "DeliveryStat_Customer_NameLst", dataMap.get("DS_Customer_Num"),
					dataMap.get("DS_Customer_Val"));
			validateRecords(cu, "DeliveryStat_CountryLst", dataMap.get("DS_Country_Num"),
					dataMap.get("DS_Country_Val"));
			validateRecords(cu, "DeliveryStat_DestinationLst", dataMap.get("DS_Destination_Num"),
					dataMap.get("DS_Destination_Val"));
			cu.getScreenShot("Validation of Input Parameters field for Delivery Statistics screen");
		}

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
		extent = ExtReport.instance("Validating_InputParameters_RoleBased");
	}

	@AfterMethod
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
			LOGGER.info(" App Logout failed () :: Exception: " + e);
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

	public void validateRecords(CommonUtils cu, String elemntPath, String fieldCount, String fieldValue)
			throws Exception {
		int val = 0;
		if (fieldCount.trim().length() > 0)
			val = Integer.parseInt(fieldCount);
		cu.checkNumberOfFieldsInDropDown(elemntPath, val);
		cu.validateFieldsInDropDown(elemntPath, fieldValue);
	}
}
