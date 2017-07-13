package com.tata.selenium.test.productProvisioningCases;

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

public class TC_04_ProductProvisioningChangePriority   implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_ProductProvisioningChangePriority.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/ProductProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_04_ProductProvisioningChangePriority -with TestdataId: " + uniqueDataId);
		String sheetName = "Product_Provisioning_Screen";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
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
		
		cu.SelectDropDownByVisibleText("Product_Provisioning_ProductNameLst",dataMap.get("Product_Provisioning_ProductNameLst"));
		
		cu.clickElement("Product_Provisioning_DisplayBtn");
		cu.getScreenShot("Screenshot of the current screen");
		cu.SelectDropDownByVisibleText("Product_Provisioning_PriorityLst", dataMap.get("Product_Provisioning_PriorityLst"));
		cu.getScreenShot("Screenshot of the changing priority from P1 to P2");
		cu.checkReadonlyProperty("Product_Provisioning_PValueTxt", dataMap.get("Product_Provisioning_PValueTxt"));
		
		cu.clickElement("Product_Provisioning_SubmitBtn");
		cu.checkMessage("application_PopUpMessage", "click submit button", "The product have been successfully updated and will become active immediately. You will receive a confirmation email for reference");
		
		//verifying the PDF file generated after submitting
		String parentWindow = cu.getCurrWindowName();
		cu.newWindowHandles(cu.getCurrWindowName());
		String newWindowTitle = cu.getTitle();

		if (cu.existsElement("pdfEmbed"))
			test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded after changing priority ",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after changing priority</span>");
		else {
			cu.getScreenShot("PDF file loading validation");
			test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after changing priority",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after changing priority -contains no pdf in title: acutal title : "
							+ newWindowTitle + "</span>");
		}
		cu.DriverClose();
		
		// switch parent window
		cu.switchToWindow(parentWindow);

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
		extent = ExtReport.instance("Product_Provisioning");
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
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
