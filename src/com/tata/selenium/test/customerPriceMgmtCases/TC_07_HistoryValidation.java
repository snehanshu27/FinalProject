package com.tata.selenium.test.customerPriceMgmtCases;

import java.util.HashMap;
import java.util.List;
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
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;
public class TC_07_HistoryValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_07_HistoryValidation.class.getName());
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
		test = extent
				.startTest("Execution triggered for - TC_07_HistoryValidation -with TestdataId: " + uniqueDataId);
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

		//First validation: Price Card Tab has 5 latest values or not.
		
		 List<String> retStrOPs=cu.getAllOptionsFromDropDown("PriceHistoryLst");
		 int costcardValueCount=retStrOPs.size();
		 
		 if((costcardValueCount-1)==5){
			cu.printLogs((costcardValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.PASS, "EXPECTECD: Cost Card Drop down should have 5 latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Cost Card Drop down has '"+(costcardValueCount-1)+"' values</span>");
		 }else{
			cu.printLogs((costcardValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.FAIL, "EXPECTECD: Cost Card Drop down should have 5 latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Cost Card Drop down has '"+(costcardValueCount-1)+"' values</span>");
		 }
		 
		 cu.checkFiveLatestValFromDropDownForPriceMgmt("PriceHistoryLst", retStrOPs);
		
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
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
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}


}
