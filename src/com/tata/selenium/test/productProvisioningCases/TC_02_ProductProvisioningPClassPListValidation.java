package com.tata.selenium.test.productProvisioningCases;

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
import com.tata.selenium.utils.PropertyUtility;

public class TC_02_ProductProvisioningPClassPListValidation  implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_ProductProvisioningPClassPListValidation.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/ProductProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws InterruptedException{
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_02_ProductProvisioningPClassPListValidation -with TestdataId: " + uniqueDataId);
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
		
		
		String parentWindow = cu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		cu.clickElement("Product_Provisioning_PClassBtn");
		
		cu.newWindowHandles(parentWindow);
		
		// Taking screenshot and Logging out
		cu.getScreenShot("ScreenShot of Pclass");
		Thread.sleep(2000);
		//cu.checkNonEditableBox("Pclass_PriorityColumnTxt",dataMap.get("Pclass_PriorityColumnTxt"));
		Thread.sleep(2000);
		cu.clickElement("Pclass_CancelBtn");
		Thread.sleep(2000);
		PropertyUtility putility = null;
		Thread.sleep(200);
		driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
		//cu.checkMessage("application_PopUpMessage","Pclass popup", "Warning: This action will discard any changes made to this screen. Do you want to Continue?");
		Thread.sleep(2000);
		//cu.getScreenShot("Cancel Button clicked");
		//cu.clickElement("application_PopUpNoBtn");
		//cu.clickElement("Pclass_CancelBtn");
		//cu.clickElement("application_PopUpOkBtn");
		
		cu.switchToWindow(parentWindow);
		
		cu.SwitchFrames("//iframe[@name='bottom']");
		cu.SwitchFrames("target");
		//cu.getScreenShot("");
		
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		cu.clickElement("Product_Provisioning_PListBtn");
		
		// Taking screenshot and Logging out
		//cu.getScreenShot("Validation Of Priority List Screen pdf");
		
		//verifying the PDF file generated after submitting
				cu.newWindowHandles(cu.getCurrWindowName());
				String newWindowTitle = cu.getTitle();
				
				if (cu.existsElement("pdfEmbed"))
					test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded ",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded </span>");
				else {
					cu.getScreenShot("PDF file loading validation");
					test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded  "
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
