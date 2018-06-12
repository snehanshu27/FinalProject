package com.tata.selenium.test.MNP;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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




//import com.opencsv.CSVWriter;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.pages.LoginPage;
import com.tata.selenium.pages.MessagingInstanceHomePage;
import com.tata.selenium.pages.NavigationMenuPage;
//import com.tata.selenium.test.supplierCoverageCases.TC_002_SupplierCoverageMTSMS;
//import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_05_MNPDisabling implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_02_MNPUpdation.class.getName());
	String properties = "./data/MNP.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_05_MNPDisabling -with TestdataId: " + uniqueDataId);
		LOGGER.info("Execution triggered for - TC_05_MNPDisabling -with TestdataId: " + uniqueDataId);
		String sheetName = "MNP_Screen";

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
		
		WebDriver driver = null;
		 String winHandleBefore = driver.getWindowHandle();
		String parentWindow = cu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		cu.clickElement("Product_MNPBtn");
		
		cu.newWindowHandles(parentWindow);
		String winHandleAfter = driver.getWindowHandle();

		// Selecting required values from drop down based on input
		cu.selectDropDownByVisibleText("Product_Provisioning_ProductNameLst", dataMap.get("Product_Provisioning_ProductNameLst"));
		
		
		//cu.waitForPageLoad("");
		// click on display button
		cu.clickElement("Product_Provisioning_DisplayBtn");
		
		cu.selectDropDownByVisibleText("Product_Provisioning_MNPFlag", dataMap.get("Product_Provisioning_MNPFlag"));
		cu.waitForPageLoad("");
		
		String disAtt = cu.getAttribute("allCoverageCheckBoxes", "disabled");
		
		if("true".equalsIgnoreCase(disAtt.trim()))	
			test.log(LogStatus.PASS, "EXPECTECD: MNP check box should be disabled for all the countries", "Usage: <span style='font-weight:bold;'>ACTUAL:: MNP check box is disabled for all the countries </span>");
		else
		{
			cu.getScreenShot("MNP check box is not disabled");
			test.log(LogStatus.FAIL, "EXPECTECD: MNP check box for all countries should be non editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: MNP check box is editable  record name. </span>");
			
		}
		
		cu.clickElement("Product_Provisioning_SubmitBtn");
		
		//checkInputAndUICoverageIsNotSame(cu, dataMap);
		
		//modifyCoverage(cu, dataMap);
		
		cu.getScreenShot("Validation Of MNP Screen");
		
		driver.close();
		
		driver.switchTo().window(winHandleBefore);
		
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
		extent = ExtReport.instance("Product_MNP");
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
			LOGGER.info(" App Logout failed () :: Exception: " + e);
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

	private void modifyCoverage(CommonUtils cu, Map<String, String> dataMap) {
		
		if ("Y".equalsIgnoreCase(dataMap.get("Coverage"))) {
			cu.selectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			
		} else
			cu.unSelectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));

		// click submit
		cu.clickElement("Product_Provisioning_SubmitBtn");
		
	}

		
	/*private void checkInputAndUICoverageIsSame(CommonUtils cu, Map<String, String> dataMap) {
		
		Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"));
		if (!(uiData.get("Coverage").equalsIgnoreCase(dataMap.get("Coverage"))))
		{
			cu.getScreenShot("input and UI coverage is not same");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Input sheet and UI coverage data should be same, After changed were made",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is not same, data is  UI_Coverage: "
							+ uiData.get("Coverage") + " ?? Data_Coverage: " + dataMap.get("Coverage") +"</span>"); 
			Assert.fail();
		}
		
	}*/

	

	private void checkInputAndUICoverageIsNotSame(CommonUtils cu, Map<String, String> dataMap) {
		
		System.out.println("Inside checkInputAndUICoverageIsSame");
		Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"));
		if (!(uiData.get("Coverage").equalsIgnoreCase(dataMap.get("Coverage"))))
				 {
			cu.getScreenShot("input and UI coverage is not same");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Input sheet and UI coverage data should be same, After changed were made",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is not same, data is  UI_Coverage: "
							+ uiData.get("Coverage") + " ?? Data_Coverage: " + dataMap.get("Coverage") +  "</span>");
			Assert.fail();
		}
		
	}

	private Map<String, String> getCurrentCoverageUIStatus(CommonUtils cu, String destinationName) {
		
		Map<String, String> ret = new HashMap<>();
		try {
			//ret.put("Country", cu.getText("dynamicCountryTd", "$destinationVal$", destinationName));

			if (cu.isCheckBoxSelected("dynamicCoverageCheckbox", "$destinationVal$", destinationName))
				ret.put("Coverage", "Y");
			else
				ret.put("Coverage", "N");

		return ret;
		} catch (Exception e) {
			LOGGER.info("error: " + e);
			cu.getScreenShot("Get webtable coverage data for destination: " + destinationName);
			test.log(LogStatus.FAIL,
					"EXPECTECD: Webtable coverage data for destination: " + destinationName
							+ " Should be obtained sucessfully",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Failed to get webtable coverage data for destination: "
							+ destinationName + "</span>");
			Assert.fail();
			return ret;
		}
		
		}

}
