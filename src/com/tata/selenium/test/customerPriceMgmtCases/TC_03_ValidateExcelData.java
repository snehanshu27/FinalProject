package com.tata.selenium.test.customerPriceMgmtCases;

import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;
import java.util.HashMap;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.ITestResult;
public class TC_03_ValidateExcelData implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_03_ValidateExcelData.class.getName());
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
		test = extent.startTest("Execution triggered for - TC_03_ValidateExcelData -with TestdataId: " + uniqueDataId);
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

		// click display button
		cu.clickElement("DisplayBtn");

		// Export CSV and validate the data with UI
		exportCSVFieldsUpdated(cu, dataMap);

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public void exportCSVFieldsUpdated(CommonUtils cu, Map<String, String> dataMap) throws Exception {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("ExportBtn");
		cu.waitForPageLoad("Customer Price Management");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();
		// validate file name
		String expectedFileName = "\\" + dataMap.get("CustomerNameLst") + "-" + dataMap.get("CustomerAccNameLst")
				+ ".csv";

		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'CustomerName-CustomerAccountName.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'CustomerName-CustomerAccountName.csv' - ' - '"
							+ expectedFileName + "'</span>");

		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'CustomerName-CustomerAccountName.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'CustomerName-CustomerAccountName.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);

		Map<String, String> csvDatamap = csvu.getData("Destination", dataMap.get("dynamicDestiination"));

		if ((csvDatamap.get("Country").equals(dataMap.get("dynamicCountry")))
				|| csvDatamap.get("Coverage").equals(dataMap.get("CoverageChk"))) {
			test.log(LogStatus.PASS, "EXPECTECD: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are same in both csv and UI'</span>");
		} else {

			String actualDiff = "Country_csv: " + csvDatamap.get("Country") + " Country_UI: "
					+ dataMap.get("dynamicCountry") + "\n   " + "Coverage_csv: " + csvDatamap.get("Coverage")
					+ " Coverage_UI: " + dataMap.get("CoverageChk") + "\n   " + "Effective_Date_csv: "
					+ csvDatamap.get("Effective Date (DD-MM-YYYY)") + " Effective_Date_UI: "
					+ dataMap.get("Effective_DateTxt");

			test.log(LogStatus.FAIL, "EXPECTECD: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are NOT same in both csv and UI - Actual diifernce between UI and CSV is : "
							+ actualDiff + " </span>");
		}

		if (("Y").equalsIgnoreCase(csvDatamap.get("Coverage").trim())) {
			test.log(LogStatus.PASS, "The coverage is added");
		} else {
			test.log(LogStatus.PASS, "The coverage is Removed");
		}

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
