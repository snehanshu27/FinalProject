package com.tata.selenium.test.supplierCostManagementCases;

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
import org.testng.annotations.Optional;
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

public class TC_05_SupplierCostMgmtAddCostAndCalendar implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_05_SupplierCostMgmtAddCostAndCalendar.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	
	String properties = "./data/SupplierCostManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(@Optional("Data_07")String uniqueDataId, @Optional("TC_05")String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_05_SupplierCostMgmtAddCostAndCalendar -with TestdataId: " + uniqueDataId);
		String sheetName = "Supplier_Cost_Management_Screen";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
			System.out.println(dataMap);
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

		// Select the parameters
		cu.selectDropDownByVisibleText("CostManagement_ServiceLst", dataMap.get("CostManagement_ServiceLst"));
		cu.selectDropDownByVisibleText("CostManagement_Supplier_NameLst",
				dataMap.get("CostManagement_Supplier_NameLst"));
		cu.validatePopulatedDropDownValue("CostManagement_Supplier_Acc_NameLst",
				dataMap.get("CostManagement_Supplier_Acc_NameLst"));
		cu.checkNonEditableBox("CostManagement_CurrencyTxt", dataMap.get("CostManagement_CurrencyTxt"));

		cu.clickElement("CostManagement_DisplayBtn");
		String uiCost,uiEffectiveDate;
		// passing cost and the calendar values
		if ("MT SMS".equalsIgnoreCase(dataMap.get("CostManagement_ServiceLst"))) {
			uiCost=cu.getAttribute("CostManagement_EditCostTxt","value", "$dynamicVal$",dataMap.get("CostManagement_Destination_FilterLst"));
			uiEffectiveDate=cu.getAttribute("CostManagement_EditEffectiveDateTxt","value", "$dynamicVal$",dataMap.get("CostManagement_Destination_FilterLst"));
			//Validating red color in the record if cost and date is blank
			if("--".equals(uiCost) && "--".equals(uiEffectiveDate)){
				if(cu.existElement("rowColorPath", "$destinationVal$", dataMap.get("CostManagement_Destination_FilterLst"))){
					test.log(LogStatus.PASS, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in red color</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in some othercolor than red color.</span>");
				}
			}
			cu.sendKeys("CostManagement_EditCostTxt", dataMap.get("CostManagement_EditCostTxt"), true, "$dynamicVal$",
					dataMap.get("CostManagement_Destination_FilterLst"));
			
			cu.clickElement("CostManagement_EditEffectiveDateTxt", "$dynamicVal$",
					dataMap.get("CostManagement_Destination_FilterLst"));
			
			cu.selectCalendarDate("CostManagement_EditEffectiveDateTxt", "$dynamicVal$",
					dataMap.get("CostManagement_Destination_FilterLst"),
					dataMap.get("CostManagement_EditEffectiveDateTxt"));
		} else {
			uiCost=cu.getAttribute("CostManagement_EditCostTxt","value", "$dynamicVal$",dataMap.get("CostManagement_Country_FilterLst"));
			uiEffectiveDate=cu.getAttribute("CostManagement_EditEffectiveDateTxt","value", "$dynamicVal$",dataMap.get("CostManagement_Country_FilterLst"));
			//Validating red color in the record if cost and date is blank
			if("--".equals(uiCost) && "--".equals(uiEffectiveDate)){
				if(cu.existElement("rowColorPath", "$destinationVal$", dataMap.get("CostManagement_Country_FilterLst"))){
					test.log(LogStatus.PASS, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in red color</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in some othercolor than red color.</span>");
				}
			}
			cu.sendKeys("CostManagement_EditCostTxt", dataMap.get("CostManagement_EditCostTxt"), true, "$dynamicVal$",
					dataMap.get("CostManagement_Country_FilterLst"));
			
			Thread.sleep(3000);
			cu.clickElement("CostManagement_EditEffectiveDateTxt", "$dynamicVal$",
					dataMap.get("CostManagement_Country_FilterLst"));
			
			Thread.sleep(3000);
			cu.selectCalendarDate("CostManagement_EditEffectiveDateTxt", "$dynamicVal$",
					dataMap.get("CostManagement_Country_FilterLst"),
					dataMap.get("CostManagement_EditEffectiveDateTxt"));
			
			System.out.println("date"+dataMap.get("CostManagement_EditEffectiveDateTxt"));
			
			/*
				cu.clickElement("CostManagement_EditEffectiveDateTxt", "$destination$", dataMap.get("CostManagement_Country_FilterLst"));
				cu.selectCalendarDate("CostManagement_EditEffectiveDateTxt", dataMap.get("CostManagement_EditEffectiveDateTxt"));
			*/
			
		}

		cu.clickElement("CostManagement_SubmitBtn");
		cu.checkMessage("application_PopUpTitle", "Clicking Submit button after passing cost and the calendar value",
				"The new cost(s) have been successfully entered and will become active on the effective date. You will receive a confirmation email for reference.");

		// verifying the PDF file generated after submitting
		String parentWindow = cu.getCurrWindowName();
		cu.newWindowHandles(cu.getCurrWindowName());
		String newWindowTitle = cu.getTitle();

		if (cu.existsElement("pdfEmbed")){
			cu.getScreenShot("Validating pdf displayed");
			test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after new coverage addition</span>");
		}else {
			cu.getScreenShot("PDF file loading validation");
			test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after new coverage addition -contains no pdf in title: acutal title : "
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
	public void beforeMethod(@Optional("TC_05")String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("SupplierCostManagement");
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
	public void afterMethod(@Optional("TC_05")String testCaseId) {
		Log.info("App Logout :: afterClass() method invoked...");
		try {
			Log.endTestCase(testCaseId);
			driver.quit();
			// Ending the Extent test
			extent.endTest(test);
			// Writing the report to HTML format
			extent.flush();
		} catch (Exception e) {
			LOGGER.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
