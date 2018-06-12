package com.tata.selenium.test.inventoryNumberManagement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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

/**
 * @date
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_03_AddingNumbersToSupplier implements ApplicationConstants {

	private static Logger LOGGER = Logger.getLogger(TC_03_AddingNumbersToSupplier.class.getName());
	String properties = "./data/NumberInventory.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_03_AddingNumbersToSupplier - " + uniqueDataId);
		String sheetName = "Number_Inventory_Screen";
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

		NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
				properties);
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		// Validating Submit by clicking it without adding any number
		cu.clickElement("Number_Inventory_SubmitBtn");
		cu.checkMessage("application_PopUpTitle", "Validating Submit Btn by clicking it",
				"Error: Please add a number to submit to the inventory.");

		// Selecting appropriate option to display
		if (("Y").equalsIgnoreCase(dataMap.get("Add_Inventory"))) {
			cu.clickElement("Add_Inventory");
		}

		cu.selectDropDownByVisibleText("Number_Inventory_SupplierNameLst",
				dataMap.get("Number_Inventory_SupplierNameLst"));
		cu.selectDropDownByVisibleText("Number_Inventory_TONLst", dataMap.get("Number_Inventory_TONLst"));
		cu.selectDropDownByVisibleText("Number_Inventory_CountryLst", dataMap.get("Number_Inventory_CountryLst"));
		cu.waitForPageLoad("Number Inventory");

		cu.selectDate("Number_Inventory_StartDateTxt", dataMap.get("Number_Inventory_StartDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");
		cu.selectDate("Number_Inventory_EndDateTxt", dataMap.get("Number_Inventory_EndDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");

		if (dataMap.get("Number_Inventory_NumbersTxt").contains(";")) {
			String[] data = dataMap.get("Number_Inventory_NumbersTxt").split(";");
			for (String val : data) {
				cu.enterData("Number_Inventory_NumbersTxt", val);
				cu.returnElement("Number_Inventory_NumbersTxt").sendKeys(Keys.RETURN);
			}
		} else {
			cu.enterData("Number_Inventory_NumbersTxt", dataMap.get("Number_Inventory_NumbersTxt"));
		}

		cu.clickElement("Number_Inventory_AddBtn");
		cu.waitForPageLoad("");

		cu.checkPopUp("application_PopUpTitle", "Validatin any pop up after adding number to county");

		try {
			if (driver
					.findElement(By.xpath("//*[@id='myTable']/tbody/tr/td[contains(text(),'"
							+ dataMap.get("Number_Inventory_CountryLst") + "')]")) != null
					&& driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr/td[contains(text(),'"
							+ dataMap.get("Number_Inventory_SupplierNameLst") + "')]")) != null)
			{
				test.log(LogStatus.PASS, "Number added should be displayed in the screen",
						"Number '" + dataMap.get("Number_Inventory_NumbersTxt") + "' added for country "
								+ dataMap.get("Number_Inventory_CountryLst") + " in the result section");
			}
		} catch (Exception e) {
			LOGGER.info("exception : " + e);

		}

		// Clicking Submit Btn
		cu.clickElement("Number_Inventory_SubmitBtn");
		cu.waitForPageLoad("");
		try {
			if (driver.findElement(By.xpath("//div[@id='popup_message']")).isDisplayed()
					&& driver.findElement(By.xpath("//div[@id='popup_message']")).getText().trim()
							.contains("Error: SC / LN is already present :")) {
					;
			}
		} catch (Exception e) {
			LOGGER.error("error  " + e);
			cu.printLogs(e.toString());
		}

		// Validating pop up for adding same records
		String popUpMsg = cu.returnElement("application_PopUpMessage").getText().trim();
		if (!popUpMsg.contains("Error: SC / LN is already present :")) {
			cu.checkMessage("application_PopUpTitle", "Clicking Submit button after adding new number to a County",
					"SC / LN successfully added to the inventory. You will receive a confirmation email for reference.");

			// verifying the PDF file generated after submitting
			String parentWindow = cu.getCurrWindowName();
			cu.newWindowHandles(cu.getCurrWindowName());
			String newWindowTitle = cu.getTitle();

			if (newWindowTitle != null) {
				if (cu.existsElement("pdfEmbed"))
					test.log(LogStatus.PASS, "EXPECTECD: PDF file should load after adding and submitting new number",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after adding and submitting new number in Inventory</span>");
				else {
					cu.getScreenShot("PDF file loading validation");
					test.log(LogStatus.FAIL,
							"EXPECTECD: PDF file should loaded after adding and submitting new number in Inventory",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after adding and submitting new number in Inventory -contains no pdf in title: acutal title : "
									+ newWindowTitle + "</span>");
				}
				cu.getScreenShot("Validation Of pdf file generated after adding inventory in InventoryScreen");
				cu.DriverClose();
			}
			// switch parent window
			cu.switchToWindow(parentWindow);
		} else {
			cu.checkMessage("application_PopUpTitle", "Clicking Submit button after adding new number to a County",
					"Error: SC / LN is already present : " + dataMap.get("Number_Inventory_NumbersTxt"));
		}

		// Taking screenshot and Logging out
		cu.getScreenShot("Validation Of " + dataMap.get("Number_Inventory_TONLst") + " in InventoryScreen");
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
		extent = ExtReport.instance("NumberInventory");
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
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
