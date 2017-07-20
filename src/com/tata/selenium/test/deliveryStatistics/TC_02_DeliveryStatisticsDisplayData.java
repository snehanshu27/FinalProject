package com.tata.selenium.test.deliveryStatistics;

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
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_02_DeliveryStatisticsDisplayData implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_DeliveryStatisticsDisplayData.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - "+TC_02_DeliveryStatisticsDisplayData.class.getName()+" -with TestdataId: " + uniqueDataId);
		String sheetName = "Delivery_Statistics_Screen";

		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
			CommonUtils.printConsole("Exception while reading data from EXCEL file for test case : " + testCaseId
					+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
			LOGGER.info("Exception while reading data from EXCEL file for test case : " + testCaseId
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

		NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
				properties);
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

//		cu.checkMessage("application_PopUpMessage", "After loading the page",
//				"No data for the selected input parameters");

		// Validating all editable drop down
		cu.SelectDropDownByVisibleText("DeliveryStat_ServiceLst", dataMap.get("DeliveryStat_ServiceLst"));
		cu.checkEditableDropDown("DeliveryStat_DimensionLst", dataMap.get("Dimension"));
		cu.SelectDropDownByVisibleText("DeliveryStat_Customer_NameLst", dataMap.get("DeliveryStat_Customer_NameLst"));
		cu.SelectDropDownByVisibleText("DeliveryStat_Supplier_NameLst", dataMap.get("DeliveryStat_Supplier_NameLst"));
		cu.SelectDropDownByVisibleText("DeliveryStat_CountryLst", dataMap.get("DeliveryStat_CountryLst"));
		cu.SelectDropDownByVisibleText("DeliveryStat_DestinationLst", dataMap.get("DeliveryStat_DestinationLst"));
		cu.SelectDropDownByVisibleText("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));

		// Select From DATE
		cu.moveAndClick("DeliveryStat_FromDateTxt");
		Thread.sleep(2000);
		cu.moveAndClick("selectMonth");
		Thread.sleep(2000);
		cu.calMonth(dataMap.get("FromMonth"));
		Thread.sleep(2000);
		cu.calDate(dataMap.get("FromDay"));
		Thread.sleep(2000);
		cu.clickElement("clickOutside");

		// Select TO DATE
		cu.moveAndClick("DeliveryStat_ToDateTxt");
		Thread.sleep(2000);
		cu.moveAndClick("selectMonth_ToDate");
		Thread.sleep(2000);
		cu.calMonth(dataMap.get("ToMonth"));
		Thread.sleep(2000);
		cu.calDate(dataMap.get("ToDay"));
		Thread.sleep(2000);

		cu.clickElement("DeliveryStat_DisplayBtn");

		cu.getScreenShot("Screenshot of the results displayed");
		if(cu.elementDisplayed("tableFirstRow"))
			test.log(LogStatus.PASS, "EXPECTED: Data should be displayed after clicking on Display button with selected parameters",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Data has been displayed after clicking on Display button with selected parameters</span>");
		else
		{
			test.log(LogStatus.PASS, "EXPECTED: Data should be displayed after clicking on Display button with selected parameters",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Data is not displayed after clicking on Display button with selected parameters</span>");
			Assert.fail("Data is not displayed after clicking on Display button with selected parameters");
		}

		cu.clickElement("DeliveryStat_CancelBtn");
		cu.getScreenShot("After clicking Cancel Button");
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
		extent = ExtReport.instance("DeliveryStatistics");
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
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
