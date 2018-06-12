package com.tata.selenium.test.supplierCoverageCases.three.x;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.opencsv.CSVWriter;
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



public class TC_002_SupplierCoverageMTSMS implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_002_SupplierCoverageMTSMS.class.getName());
	String properties = "./data/SupplierCoverageObjects3x.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test;
	CommonUtils cu ;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - "+TC_002_SupplierCoverageMTSMS.class.getName()+" -with TestdataId: " + uniqueDataId);
		LOGGER.info("Execution triggered for - "+TC_002_SupplierCoverageMTSMS.class.getName()+" -with TestdataId: " + uniqueDataId);
		String sheetName = "Supplier_Coverage_Screen3.x";

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

		cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
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

		
		cu.waitForPageLoadWithSleep("SupplierCoveragePage", 20);
		cu.waitForElementVisiblity("SupplierCoverage_CancelBtn", 180);
		cu.waitForElementInvisiblity("SupplierCoveragePageLoad", 60);
		
		
		// Selecting required values from drop down based on input
		
		//Select service
		selectService(dataMap.get("Service"));
		
		//Select SupplierName
		selectSupplier(dataMap.get("Supplier_Name"));
		

		//Select Supplier Account Name
		selectSupplierAccount(dataMap.get("Supplier_Account_Name"));

		// validate Historys
		if ("ON".equals(dataMap.get("ValidateHistory")))
			validateHistory(dataMap, cu, dataMap.get("Supplier_Account_Name"));

		// select first history option
		selectFirstHistory();

		// click on display button
		clickOnDisplayButton();

		collapseMainFilter();
		
		
		List<Map<String, String>> rawDataFromStore = getUITableDataFromDataStores();
		
		
		
		scrollTableBodyToRowNo(rawDataFromStore.size(), getRowNumberOfDestination(rawDataFromStore, dataMap.get("Destination")));
		
		
		//Check input and UI coverage is not same
//		checkInputAndUICoverageIsNotSame(cu, dataMap);

		// Check error pop up, then check either SC/LN/DR/AN seslected
		if ("ON".equals(dataMap.get("ValidateErrorPopupWithoutAddingCoverage")))
		{
			validateErrorPopupWithoutAddingCoverage(cu, navMenuPage, dataMap);
			scrollTableBodyToRowNo(rawDataFromStore.size(), getRowNumberOfDestination(rawDataFromStore, dataMap.get("Destination")));
		}

		// add\modify coverage as per input data
		if (!"ON".equals(dataMap.get("InputThroughUploadButton")))
			modifyCoverage(cu, dataMap);
		else
			modifyCoverageUsingUploadOption(cu, dataMap);

		cu.sleep(4000);

		// Alert message validations
		validateSuccessPopupMessageAndPDFPageLoaded(cu);
		
		// validate Supplier Cost Management page has been loaded
		if (cu.existsElement("supplierCstMgtLabel"))
			test.log(LogStatus.PASS, "EXPECTECD: Supplier Cost Management screen should be loaded",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Supplier Cost Management screen has been loaded</span>");
		else {
			cu.getScreenShot("Supplier Cost Management screen should be loaded validation");
			test.log(LogStatus.FAIL, "EXPECTECD: Supplier Cost Management screen should be loaded",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Supplier Cost Management screen has not loaded</span>");
			Assert.fail();
		}

		cu.default_content();
		cu.SwitchFrames("//iframe[@scrolling='no']");
		cu.clickElement("exchange");
		cu.waitForPageLoad("");

		navMenuPage.navigateToMenuPageAndMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		// Selecting required values from drop down based on input
		expandMainFilter();
		//Select service
		selectService(dataMap.get("Service"));		
		//Select SupplierName
		selectSupplier(dataMap.get("Supplier_Name"));		
		//Select Supplier Account Name
		selectSupplierAccount(dataMap.get("Supplier_Account_Name"));
		//Select First History
		selectFirstHistory();
		clickOnDisplayButton();

		// get time string as date-month-year and validate date on which order
		// is created should be appended to the order number (history)
		cu.default_content();
		cu.SwitchFrames("//iframe[@scrolling='no']");
		String[] uiTime = cu.getText("uiTimestamp").split(" ");
		String expectedUIDateFormat = uiTime[1].trim() + "-" + uiTime[2].trim() + "-" + uiTime[3].trim();

		cu.default_content();
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		List<String> coverageHistories = getHistoryList();
		String selectedHistoryVal = coverageHistories.size()>0?coverageHistories.get(0):"";
		if (selectedHistoryVal.toLowerCase().contains(expectedUIDateFormat.toLowerCase()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Date on which order is created should be appended to the order number (coverage history)",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Date on which order is created has been appended to the order number (coverage history)</span>");
		else {
			cu.getScreenShot(
					"Date on which order is created should be appended to the order number (coverage history) validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Date on which order is created should be appended to the order number (coverage history)",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage history is loaded with date : "
							+ expectedUIDateFormat + " acutal coverage history option which was populated is : "
							+ selectedHistoryVal + "</span>");
		}
		collapseMainFilter();
				
		
		
		// validate coverage fields
		validateCoverageFieldsUpdatedinUI();

		// export file and validate
//		if ("ON".equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
//			exportCSVAndCoverageFieldsUpdated();

		// Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Coverage Screen");
		test = cu.getExTest();
//		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("SupplierCoverage");
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

	private List<String> getHistoryList()
	{
		cu.clickElement("SupplierCoverage_CoverageHistoryListToggleDiv");
		cu.sleep(300);
		cu.clickElement("SupplierCoverage_CoverageHistoryListToggleDiv");
		return cu.ElementsToList("SupplierCoverage_CoverageHistoryListAllOptions");
	}
	public void validateHistory(Map<String, String> dataMap, CommonUtils cu, String selectedAccName) {
		 System.out.println("Inside validateHistory ");
	     test.log(LogStatus.INFO, "Inside validateHistory ");
		
		List<String> coverageHistories = getHistoryList();
       
		if(coverageHistories.size()>0)
		{
			if (coverageHistories.size() < 6) {
				// validate only 5 history is displayed and latest one is
				// editable
				test.log(LogStatus.PASS, "EXPECTECD: Only 5 history records should be displayed",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records has been displayed, Total records found: "
								+ (coverageHistories.size()) + "</span>");

				for (int i = 0; i < coverageHistories.size(); i++) {
					cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_CoverageHistoryListToggleDiv"
													, "SupplierCoverage_CoverageHistoryListDynamicOption"
															, "$optionvalue$", coverageHistories.get(i));
					
					if(cu.elementDisplayed("SupplierCoverage_PopupWindow", 2))
						cu.clickElement("SupplierCoverage_PopupWindow_YesButton");
					
					cu.clickElement("SupplierCoverage_DisplayBtn");
					cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
					
					String disAtt = cu.getAttribute("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Coverage_Checkbox", "disabled", "$index$",
							1+"");

					if (i != 0) {
						if ("true".equalsIgnoreCase(disAtt.trim()))
							test.log(LogStatus.PASS, "EXPECTECD: Other than lastest history should be non editable",
									"Usage: <span style='font-weight:bold;'>ACTUAL:: Old record is not editable  record name: "
											+ coverageHistories.get(i) + "  optionNo: " + (i + 1) + "</span>");
						else {
							cu.getScreenShot("old record non editable for index " + (i + 1));
							test.log(LogStatus.FAIL, "EXPECTECD: Other than lastest history should be non editable",
									"Usage: <span style='font-weight:bold;'>ACTUAL:: Old record is Editable  record name: "
											+ coverageHistories.get(i) + "  optionNo: " + (i + 1) + "</span>");
						}
					}
					if (i == 0) {
						if (disAtt == null)
							test.log(LogStatus.PASS, "EXPECTECD: Lastest history should be editable",
									"Usage: <span style='font-weight:bold;'>ACTUAL:: Lastest history is Editable  record name: "
											+ coverageHistories.get(i) + "  optionNo: " + (i + 1) + "</span>");
						else {
							cu.getScreenShot("old record non editable for index " + (i + 1));
							test.log(LogStatus.FAIL, "EXPECTECD: Lastest history should be editable",
									"Usage: <span style='font-weight:bold;'>ACTUAL:: Lastest history is Not Editable  record name: "
											+ coverageHistories.get(i) + "  optionNo: " + (i + 1) + "</span>");
						}
					}
				}
			} else {
				cu.getScreenShot("Only 5 history records should be displayed");
				test.log(LogStatus.FAIL, "EXPECTECD: Only 5 history records should be displayed",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records is not displayed for old record total records deisplayed : "
								+ (coverageHistories.size() - 1) + "</span>");
				Assert.fail();
			}
		}
		else
		{
			test.log(LogStatus.INFO, "It seems the supplier account is fresh - No history record is found");
			System.out.println("It seems the supplier account is fresh - No history record is found");
		}
	
	}

	public void validateErrorPopupWithoutAddingCoverage(CommonUtils cu, NavigationMenuPage navMenuPage,
			Map<String, String> dataMap) {
		  System.out.println("Inside validateErrorPopupWithoutAddingCoverage");
		  test.log(LogStatus.INFO, "Inside validateErrorPopupWithoutAddingCoverage");
		  
		cu.selectCheckBoxWithoutAutoScroll("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_ToggleDiv"
														 ,"$DestinationName$", dataMap.get("Destination")
																,"SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_DynamicOption"
																,"$DestinationName$~$optionvalue$", dataMap.get("Destination")+"~None");	
		
		
		
		
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
	
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		
		
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRHandset_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRHandset_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRSMSC_TD", "$DestinationName$", dataMap.get("Destination"));
		cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRSMSC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		
		cu.clearData("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Remarks_Textbox", "$DestinationName$", dataMap.get("Destination"));
		
		expandMainFilter();
		clickOnSubmitButton();
		
		cu.checkMessage("SupplierCoverage_PopupWindow", "SupplierCoverage_PopupWindow_Message", "SupplierCoverage_PopupWindow_OkButton"
				, "Check popup without adding/checking SC/LN/DR/AN"
					, "Error: Please provide at least one Route feature.");

		
		
		
		selectService(dataMap.get("Service"));
		selectSupplier(dataMap.get("Supplier_Name"));
		selectSupplierAccount(dataMap.get("Supplier_Account_Name"));
		selectFirstHistory();
		clickOnDisplayButton();
		
		collapseMainFilter();
		
	}

	public void modifyCoverage(CommonUtils cu, Map<String, String> dataMap) {
		System.out.println("Inside modifyCoverage");
		test.log(LogStatus.INFO, "Inside modifyCoverage");
		if ("Y".equalsIgnoreCase(dataMap.get("Coverage"))) {
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_TD", "$DestinationName$", dataMap.get("Destination"));
			cu.selectCheckBoxWithoutAutoScroll("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_Checkbox", "$DestinationName$", dataMap.get("Destination"));

			
			if (!"".equalsIgnoreCase(dataMap.get("RouteType")))
			{
				cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_TD", "$DestinationName$", dataMap.get("Destination"));
				cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_ToggleDiv"
														 ,"$DestinationName$", dataMap.get("Destination")
																,"SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_DynamicOption"
																,"$DestinationName$~$optionvalue$", dataMap.get("Destination")+"~"+dataMap.get("RouteType"));	
			}
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("AN"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("SC"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("LC"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("DR"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRHandset_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("DRHandset"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRHandset_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRSMSC_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("DR_SMSC"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DRSMSC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_ConcatenationSupport_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("ConcatenationSupport"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_ConcatenationSupport_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_MNPSupport_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("MNPSupport"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_MNPSupport_Checkbox", "$DestinationName$", dataMap.get("Destination"));
						
			if (!"".equalsIgnoreCase(dataMap.get("DataCodingSupport")))
			{
				cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DataCodingSupport_List_TD", "$DestinationName$", dataMap.get("Destination"));
				cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DataCodingSupport_List_ToggleDiv"
						 ,"$DestinationName$", dataMap.get("Destination")
								,"SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DataCodingSupport_List_DynamicOption"
								,"$DestinationName$~$optionvalue$", dataMap.get("Destination")+"~"+dataMap.get("DataCodingSupport"));			
			}
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Default_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("PreRegAN"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_PreRegAN_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Default_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("Default"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Default_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_FormatTemplateRegistration_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("FormatTemplateRegistration"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_FormatTemplateRegistration_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TATforPreReg_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("TATforPreReg"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TATforPreReg_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_ContentWhitelist_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("ContentWhitelist"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_ContentWhitelist_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_FormatTemplateContent_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("FormatTemplateContent"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_FormatTemplateContent_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TATforContent_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("TATforContent"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TATforContent_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_NoObjectionCertificate_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("NoObjectionCertificate"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_NoObjectionCertificate_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TradeLicenseRequired_TD", "$DestinationName$", dataMap.get("Destination"));
			checkAndUncheckCheckboxBasedOnCondition(dataMap.get("TradeLicenseRequired"), "SupplierCoverage_Table_DynamicDestination_Row_Coloumn_TradeLicenseRequired_Checkbox", "$DestinationName$", dataMap.get("Destination"));
			cu.setData("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Remarks_Textbox", dataMap.get("Remarks"), "$DestinationName$", dataMap.get("Destination"));
		
		} else
		{
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_TD", "$DestinationName$", dataMap.get("Destination"));
			cu.unSelectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		}
		
		
		expandMainFilter();
		// click submit
		clickOnSubmitButton();
	}

	
	public void checkAndUncheckCheckboxBasedOnCondition(String condtionYN, String filedname, String replaceKeys, String replaceValues)
	{
		if ("Y".equalsIgnoreCase(condtionYN))
			cu.selectCheckBoxWithoutAutoScroll(filedname, replaceKeys, replaceValues);
		else if ("N".equalsIgnoreCase(condtionYN))
			cu.unSelectCheckBoxWithoutAutoScroll(filedname, replaceKeys, replaceValues);
	}
	
	public void validateFirstAccNameAutoPopulated(CommonUtils cu) {
		System.out.println("Inside validateFirstAccNameAutoPopulated");
		test.log(LogStatus.INFO, "Inside validateFirstAccNameAutoPopulated");
		cu.waitForPageLoad("SupplierCoverage");
		
		//get first option from UI list
		List<String> allAccNameList = cu.ElementsToList("SupplierCoverage_SupplierAccountNameListAllOptions");
		String firstAccNameExp = ((allAccNameList!=null && allAccNameList.size()>0)?allAccNameList.get(0):null);
		firstAccNameExp = (firstAccNameExp!=null?firstAccNameExp.trim():"");
		
		//get current Supplier AccountName
		String selectedAccName =cu.getAttribute("SupplierCoverage_SupplierAccountNameListTextBox", "value");
		selectedAccName = (selectedAccName!=null?selectedAccName.trim():cu.getAttribute("SupplierCoverage_SupplierAccountNameListTextBox", "placeholder"));
		
		if (firstAccNameExp.equals(selectedAccName))
			test.log(LogStatus.PASS,
					"EXPECTECD: supplierAccNameLst should be auto populated by the first supplier account: "
							+ firstAccNameExp,
					"Usage: <span style='font-weight:bold;'>ACTUAL:: supplierAccNameLst has been populated by the first supplier account: "
							+ selectedAccName + "</span>");
		else {
			cu.getScreenShot(
					"supplierAccNameLst should be auto populated by the first supplier account: " + firstAccNameExp);
			test.log(LogStatus.FAIL,
					"EXPECTECD: supplierAccNameLst should be auto populated by the first supplier account: "
							+ firstAccNameExp,
					"Usage: <span style='font-weight:bold;'>ACTUAL:: supplierAccNameLst has been populated by "
							+ selectedAccName + " instead of the first supplier account: " + firstAccNameExp
							+ "</span>");
		}
	}

	public void exportCSVAndCoverageFieldsUpdated() {
		System.out.println("Inside exportCSVAndCoverageFieldsUpdated");
		test.log(LogStatus.INFO, "Inside exportCSVAndCoverageFieldsUpdated");
		cu.deleteAllFilesInDownloadFolder();		
		cu.clickElement("SupplierCoverage_ExportToggleButton");
	  	cu.sleep(200);
	  	cu.clickElement("SupplierCoverage_ExportToCSVButton");
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("SupplierCoveragePageLoad", 300);
		cu.sleep(2000);
		
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + dataMap.get("Supplier_Name") + "-" + dataMap.get("Supplier_Account_Name")
				+ ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);
		Map<String, String> csvDatamap = csvu.getData("Destination", dataMap.get("Destination"));

		if (csvDatamap.get("RouteType").equals(dataMap.get("RouteType")) && csvDatamap.get("Dynamic AN").equals(dataMap.get("AN")) 
				&& csvDatamap.get("Dynamic SC").equals(dataMap.get("SC")) && csvDatamap.get("Dynamic LN").equals(dataMap.get("LC")) 
					&& csvDatamap.get("DR#").equals(dataMap.get("DR"))) {
			test.log(LogStatus.PASS, "EXPECTECD: Coverage should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is same in both csv and UI'</span>");
		} else {
			String actualDiff = "RouteType_csv: " + csvDatamap.get("RouteType") + " RouteType_UI: " + dataMap.get("RouteType") + "\n   "
					+"AN_csv: " + csvDatamap.get("Dynamic AN") + " AN_UI: " + dataMap.get("AN") + "\n   "
					+ "SC_csv: " + csvDatamap.get("Dynamic SC") + " SC_UI: " + dataMap.get("SC") + "\n   " + "LN_csv: "
					+ csvDatamap.get("Dynamic LN") + " LN_UI: " + dataMap.get("LC") + "\n   " + "DR_csv: "
					+ csvDatamap.get("DR#") + " DR_UI: " + dataMap.get("DR");

			test.log(LogStatus.FAIL, "EXPECTECD: Coverage should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is NOT same in both csv and UI - Actual diifernce between UI and CSV is : "
							+ actualDiff + " '</span>");
		}

	}

	public void validateCoverageFieldsUpdatedinUI() {
		System.out.println("Inside validateCoverageFieldsUpdatedinUI");
		test.log(LogStatus.INFO, "Inside validateCoverageFieldsUpdatedinUI");
		
//		cu.checkNonEditableDropDown("dynamicRouteTypeDropdown", "$destinationVal$", dataMap.get("Destination"));
				
		cu.selectCheckBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_Coverage_Checkbox", "$DestinationName$", dataMap.get("Destination"));

		cu.checkEditableBox("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_RouteType_List_Textbox", dataMap.get("RouteType"), "$DestinationName$", dataMap.get("Destination"));
		
		if ("Y".equalsIgnoreCase(dataMap.get("AN")))
			cu.checkCheckBoxSelected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		else {
			if ("N".equalsIgnoreCase(dataMap.get("AN")))
				cu.checkCheckBoxUnselected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_AN_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		}

		if ("Y".equalsIgnoreCase(dataMap.get("SC")))
			cu.checkCheckBoxSelected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		else {
			if ("N".equalsIgnoreCase(dataMap.get("SC")))
				cu.checkCheckBoxUnselected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_SC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		}

		if ("Y".equalsIgnoreCase(dataMap.get("LC")))
			cu.checkCheckBoxSelected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		else {
			if ("N".equalsIgnoreCase(dataMap.get("LC")))
				cu.checkCheckBoxUnselected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_LC_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		}

		if ("Y".equalsIgnoreCase(dataMap.get("DR")))
			cu.checkCheckBoxSelected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		else {
			if ("N".equalsIgnoreCase(dataMap.get("DR")))
				cu.checkCheckBoxUnselected("SupplierCoverage_Table_DynamicDestination_Row_Coloumn_DR_Checkbox", "$DestinationName$", dataMap.get("Destination"));
		}
	}

	public void validateSuccessPopupMessageAndPDFPageLoaded(CommonUtils cu) {
		
		
		System.out.println("Inside validateSuccessAlertMessageAndPDFPageLoaded");
		test.log(LogStatus.INFO, "Inside validateSuccessAlertMessageAndPDFPageLoaded");
		
		// Check success message and accept popup
		cu.checkMessage("SupplierCoverage_PopupWindow", "SupplierCoverage_PopupWindow_Message", "SupplierCoverage_PopupWindow_OkButton"
							, "Check popup success message after clicking Submit button"
								, "The new coverage(s) have been successfully entered and will become active immediately. You will receive a confirmation email for reference");
	
		// switch to new window and check pdf loaded
		String parentWindow = cu.getCurrWindowName();
		
		cu.newWindowHandles(cu.getCurrWindowName());
		String newWindowTitle = cu.getTitle();
		if (cu.existsElement("pdfEmbed"))
			test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after new coverage addition</span>");
		else {
			cu.getScreenShot("PDF file loading validation");
			test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after new coverage addition -contains no pdf in title: acutal title : "
							+ newWindowTitle + "</span>");
		}
		if(cu.getAllWindowNames().size()>1)
			cu.DriverClose();

		// switch parent window
		cu.switchToWindow(parentWindow);
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
				
	}

	public Map<String, String> getCurrentCoverageUIStatus(CommonUtils cu, String destinationName, boolean includeEnabledStatus) {
		Map<String, String> ret = null;
		System.out.println("Inside getCurrentCoverageUIStatus");
		try {
			
			ret = combineHeaderAndDataRowsToMap(getTableHeaders(), getCurrentColoumnDataBasedOnDestination(destinationName, includeEnabledStatus));
		
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
		
		return ret;
	}

	public void checkInputAndUICoverageIsNotSame(CommonUtils cu, Map<String, String> dataMap) {
		System.out.println("Inside checkInputAndUICoverageIsNotSame");
		Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"), false);
		
		if (uiData.get("Coverage #").equalsIgnoreCase(dataMap.get("Coverage"))
				&& uiData.get("RouteType").equalsIgnoreCase(dataMap.get("RouteType"))
				&& uiData.get("Dynamic AN").equalsIgnoreCase(dataMap.get("AN"))
				&& uiData.get("Dynamic SC").equalsIgnoreCase(dataMap.get("SC"))
				&& uiData.get("Dynamic LN").equalsIgnoreCase(dataMap.get("LC"))) {
			cu.getScreenShot("input and UI coverage is same");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Input sheet and UI coverage data should not be same, Because then only we can edit the coverage sucessfully",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is same, data is <br/>"
							+ " Coverage_UI: "+ uiData.get("Coverage #") + "          Coverage_Input: "+dataMap.get("Coverage")+ "<br/>"
							+ " RouteType_UI: "+uiData.get("RouteType")+ "          RouteType_Input: "+dataMap.get("RouteType")+ "<br/>"
							+"  AN_UI: " + uiData.get("Dynamic AN") +"         AN_Input: " + dataMap.get("AN")+ "<br/>"
							+ " SC_UI: " + uiData.get("Dynamic SC") + "         SC_Input: " + dataMap.get("SC")+ "<br/>"
							+ " LN_UI: " + uiData.get("Dynamic LN") + "          LN_Input: " + dataMap.get("LC")+ "<br/>"
							+ " DR_UI: " + uiData.get("DR #") + "          DR_Input: " + dataMap.get("DR")+ "<br/>"
							+ "</span>");
			Assert.fail();
		}
	}

	public void checkInputAndUICoverageIsSame(CommonUtils cu, Map<String, String> dataMap) {
		System.out.println("Inside checkInputAndUICoverageIsSame");
		Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"), false);
		if (!(uiData.get("Coverage #").equalsIgnoreCase(dataMap.get("Coverage"))
				&& uiData.get("Dynamic AN").equalsIgnoreCase(dataMap.get("AN"))
				&& uiData.get("Dynamic SC").equalsIgnoreCase(dataMap.get("SC"))
				&& uiData.get("Dynamic LN").equalsIgnoreCase(dataMap.get("LC"))
				&& uiData.get("DR #").equalsIgnoreCase(dataMap.get("DR")))) {
			cu.getScreenShot("input and UI coverage is not same");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Input sheet and UI coverage data should be same, After changed were made",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is not same, data is  UI_Coverage: "
							+ uiData.get("Coverage #") + " ?? Data_Coverage: " + dataMap.get("Coverage") + "\n  UI_AN: "
							+ uiData.get("Dynamic AN") + " ?? Data_AN: " + dataMap.get("AN") + "\n  UI_SC: " + uiData.get("SC")
							+ " ?? Data_SC: " + dataMap.get("SC") + "\n  UI_LN: " + uiData.get("LC") + " ?? Data_LN: "
							+ dataMap.get("Dynamic LN") + "\n  UI_DR: " + uiData.get("DR") + " ?? Data_DR: " + dataMap.get("DR")
							+ "</span>");
			Assert.fail();
		}
	}

	public void modifyCoverageUsingUploadOption(CommonUtils cu, Map<String, String> dataMap) {
		System.out.println("Inside modifyCoverageUsingUploadOption");
		test.log(LogStatus.INFO, "Inside modifyCoverageUsingUploadOption");
		// create csv file from input data
		Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"), false);
		String tempPath = System.getProperty("user.dir") + "\\temp\\" + CommonUtils.getCurrentTimeStamp();
		new File(tempPath).mkdirs();
		String csvFilePath = tempPath + "\\" + dataMap.get("Supplier_Name") + "-" + dataMap.get("Supplier_Account_Name")
				+ "-Coverage Update.csv";
		CSVWriter wr;
		try {
			wr = new CSVWriter(new FileWriter(csvFilePath));

			List<String[]> allLines = new ArrayList<>();
			allLines.add(new String[] {"Country", "Company", "Destination", "MCC", "MNC"
										, "RouteType", "Coverage", "Dynamic AN", "Dynamic SC", "Dynamic LN"
										,"DR#", "DR Handset", "DR SMSC", "Concatenation Support"
										, "MNP Support", "Data Coding Support", "Pre-Reg AN", "Default"										
										, "Format/ template for registration", "TAT for Pre-Reg", "Content whitelist"
										, "Format/ template for content", "TAT for Content", "No Objection Certificate required"
										, "Trade License Required", "Remarks"});
			allLines.add(new String[] {uiData.get("Country"), uiData.get("Company"), uiData.get("Destination"), uiData.get("MCC"), uiData.get("MNC")
										, dataMap.get("Coverage"), dataMap.get("RouteType"), dataMap.get("AN"), dataMap.get("SC"), dataMap.get("LC")
										, dataMap.get("DR"), dataMap.get("DRHandset"), dataMap.get("DR_SMSC"), dataMap.get("ConcatenationSupport")
										, dataMap.get("MNPSupport"), dataMap.get("DataCodingSupport"), dataMap.get("PreRegAN"), dataMap.get("Default")
										, dataMap.get("FormatTemplateRegistration"), dataMap.get("TATforPreReg"), dataMap.get("ContentWhitelist")
										, dataMap.get("FormatTemplateContent"), dataMap.get("TATforContent"), dataMap.get("NoObjectionCertificate")
										, dataMap.get("TradeLicenseRequired"), dataMap.get("Remarks")});
			wr.writeAll(allLines, false);
			wr.close();
		} catch (IOException e) {
			LOGGER.error(e);
		}

		cu.printLogs("CSV file has been generated as the input: " + csvFilePath);
		test.log(LogStatus.PASS, "EXPECTECD: CSV file should be generated as the input",
				"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: CSV file has been generated (fileupload) as the input: "
						+ csvFilePath + "</span>");
		
		
		// upload the files	
		cu.clickElement("SupplierCoverage_UploadBtn");
		cu.waitForElementVisiblity("SupplierCoverage_UploadWindow_SubmitBtn", 10);
		cu.sendKeys("SupplierCoverage_UploadWindow_UploadInptFileElement", csvFilePath, false);
		cu.sleep(500);
		cu.clickElement("SupplierCoverage_UploadWindow_SubmitBtn");
		
		// Check warnig message and accept popup
		cu.checkMessage("SupplierCoverage_PopupWindow", "SupplierCoverage_PopupWindow_Message", "SupplierCoverage_PopupWindow_YesButton"
							, "Check popup waring message for uploading the CSV file"
								, "Warning: This action will upload the Selected CSV. Do you want to Continue ?");

		cu.waitForElementInvisiblity("SupplierCoverage_PopupWindow", 60);
		
		
		// checkInputAndUICoverageIsSame
		checkInputAndUICoverageIsSame(cu, dataMap);

		// click submit
		cu.clickElement("submitBtn");
	}

	
	 List<List<String>> getUITableDataAlongWithHeadersWithoutMapping(List<List<String>>  retRowLst, boolean includeEnabledStatus)
	 {
		 driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
		int tableRowSize = cu.ElementsSizeCount("SupplierCoverage_AllTableDataRows");
		List<String> allHeaderNames = null;
		if(retRowLst.size()==0)
		{
			allHeaderNames = cu.ElementsToListWithTrim("SupplierCoverage_AllTableHeaders");			
			retRowLst.add(allHeaderNames);
		}
		else
			allHeaderNames = retRowLst.get(0);
		
		List<List<String>> visibleRowsData = getVisibleRowsData(includeEnabledStatus);
		for(List<String> iRowVisble  : visibleRowsData)
		{
			if(!retRowLst.contains(iRowVisble))
				 retRowLst.add(iRowVisble);	
		}
				
		cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicIndex_Row_AllColoumn", "$index$", tableRowSize+"");
		cu.sleep(300);
		List<String> lastRowColoumTxts = getCurrentColoumnDataBasedOnIndex(tableRowSize, includeEnabledStatus);
		
		if(!lastRowColoumTxts.equals(retRowLst.get(retRowLst.size()-1)))			
			getUITableDataAlongWithHeadersWithoutMapping(retRowLst, includeEnabledStatus);
				
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		return retRowLst;
	 }
	
	 List<String> getCurrentColoumnDataBasedOnIndex(int index, boolean includeEnabledStatus)
	 {
		 String replaceKeys = "$index$";
		 String replaceValues = index+"";
		 List<String> currentRowData = new LinkedList<>();
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Country_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Company_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Destination_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MCC_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MNC_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_RouteType_List_Textbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Coverage_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_AN_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_SC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_LC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DR_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DRHandset_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DRSMSC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_ConcatenationSupport_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MNPSupport_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DataCodingSupport_List_Textbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_PreRegAN_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Default_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_FormatTemplateRegistration_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TATforPreReg_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_ContentWhitelist_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_FormatTemplateContent_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TATforContent_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));	
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_NoObjectionCertificate_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TradeLicenseRequired_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getTxtBoxValue("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Remarks_Textbox", replaceKeys, replaceValues));
		 		 
		 return currentRowData;
	 }
	 
	 List<String> getCurrentColoumnDataBasedOnDestination(String destination, boolean includeEnabledStatus)
	 {
		 String replaceKeys = "$DestinationName$";
		 String replaceValues = destination;
		 List<String> currentRowData = new LinkedList<>();
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Country_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Company_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Destination_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MCC_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MNC_Text", replaceKeys, replaceValues));
		 currentRowData.add(cu.getTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_RouteType_List_Textbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Coverage_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_AN_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_SC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_LC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DR_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DRHandset_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DRSMSC_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_ConcatenationSupport_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_MNPSupport_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_DataCodingSupport_List_Textbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_PreRegAN_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Default_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_FormatTemplateRegistration_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TATforPreReg_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_ContentWhitelist_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_FormatTemplateContent_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TATforContent_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));	
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_NoObjectionCertificate_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_TradeLicenseRequired_Checkbox", replaceKeys, replaceValues, includeEnabledStatus));
		 currentRowData.add(cu.getTxtBoxValue("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Remarks_Textbox", replaceKeys, replaceValues));
		 		 
		 return currentRowData;
	 }
	 
	 
	 
	 List<Map<String, String>> getUITableDataFromDataStores()
	 {
		 
		 String jsStr1 = "var scriptElt = document.createElement('script');"
					+ "scriptElt.type = 'text/javascript';"
					+ "scriptElt.innerHTML='function getTableStoredata(locator){var arr = [];"
												+ "var storeItemsVar = Ext.ComponentQuery.query(locator)[0].store.data.items;"
												+ "for(var i=0; i<storeItemsVar.length; i++){arr.push(storeItemsVar[i].data);}"
												+ "return arr;}';"
			+ "document.getElementsByTagName('head')[0].appendChild(scriptElt);";
				 	 
		 	cu.executeJavaScrpit(jsStr1);			
			List<Map<String, String>> ret = (List<Map<String, String>>) cu.executeJavaScrpit("return getTableStoredata(\"#"+cu.getAttribute("SupplierCoverage_TableBody", "id")+"\")");
						
			System.out.println(ret);
			
			return ret;
	 }
	 
	 void scrollTableBodyToRowNo(int totalRows, int rowNum)
	 {
		 cu.executeJavaScrpit("SupplierCoverage_TableBody", "arguments[0].scrollTop=0;arguments[0].scrollTop = ((arguments[0].scrollHeight/"+totalRows+")*"+(rowNum-1)+");");			
	 }
	 
	 int getRowNumberOfDestination(List<Map<String, String>> tableData, String destinationName)	 
	 {
		 int ret =-1;
		 int i=0;
		 for(Map<String, String> row : tableData)
		 {
			 i++;
			 if(row.get("destinationName")!=null && row.get("destinationName").equals(destinationName))
			 {
				 ret =i;
				 break;
			 }
		 }
		 
		 return ret;
	 }
	 
	 
		List<Map<String, String>> convertListTableDataToListOfMapData(List<List<String>> uiTableDataAlongWithHeadersWithoutMapping)
		{
			List<Map<String, String>> retLst = new LinkedList<>();
			
			for(int i=1;i<uiTableDataAlongWithHeadersWithoutMapping.size();i++)
			{
				Map<String, String> rowMap = combineHeaderAndDataRowsToMap(uiTableDataAlongWithHeadersWithoutMapping.get(0), uiTableDataAlongWithHeadersWithoutMapping.get(i));
				retLst.add(rowMap);
			}
			
			return retLst;
		}
		
		Map<String, String> combineHeaderAndDataRowsToMap(List<String> headerRow, List<String> dataRow)
		{
			Map<String, String> rowMap = new LinkedHashMap<>();		
			for(int i=0;i<headerRow.size();i++)		
				rowMap.put(headerRow.get(i), dataRow.get(i));
			
			return rowMap;		
		}
		
		List<String> getTableHeaders()
		{
			return cu.ElementsToList("SupplierCoverage_AllTableHeaders");
		}
		
		
		List<List<String>> seperateTableDataAlone(List<List<String>> rawDataAlongWithEnabledStatus)
		{
			List<List<String>> extarctedTableDataAlone=new LinkedList<>();
			int i=0;
			for(List<String> rawRowData : rawDataAlongWithEnabledStatus)
			{
				if(i!=0)						
				{
					List<String> dataExtarctedRow=new LinkedList<>();
					for(String coloumn: rawRowData)
					{
						if(coloumn!=null && coloumn.contains("~SplitDelimit~"))
							dataExtarctedRow.add(coloumn.split("\\~SplitDelimit\\~")[0]);
						else
							dataExtarctedRow.add(coloumn);
					}
					extarctedTableDataAlone.add(dataExtarctedRow);
				}
				else
					extarctedTableDataAlone.add(rawRowData);
				
				i++;
			}
			return extarctedTableDataAlone;
		}
		
	
		List<Map<String, String>> seperateUITableMainColumnStatusAlone(List<List<String>> rawDataAlongWithEnabledStatus)
		{
			
			List<Map<String, String>> rawDataAlongWithEnabledStatusMapLst = convertListTableDataToListOfMapData(rawDataAlongWithEnabledStatus);
			
			for(Map<String, String> rawDataAlongWithEnabledStatusMapRow : rawDataAlongWithEnabledStatusMapLst)
			{
				for(String key : rawDataAlongWithEnabledStatusMapRow.keySet())
				{
					if(!("Country".equals(key) || "Company".equals(key) || "Destination".equals(key) || "MCC".equals(key) || "MNC".equals(key)))
					{
						String[] spltArry = rawDataAlongWithEnabledStatusMapRow.get(key).split("\\~SplitDelimit\\~");
						rawDataAlongWithEnabledStatusMapRow.put(key, spltArry.length>1?spltArry[1]:null);
					}					
				}
			}
			
			return rawDataAlongWithEnabledStatusMapLst;
		}
		
	
		List<List<String>> getVisibleRowsData(boolean includeEnabledStatus)
		{
			List<List<String>> lst = new LinkedList<>();			
			lst.add(cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_Country_Text"));
			lst.add(cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_Company_Text"));
			lst.add(cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_Destination_Text"));
			lst.add(cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_MCC_Text"));
			lst.add(cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_MNC_Text"));
			lst.add(cu.getMultipleTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_RouteType_List_Textbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_Coverage_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_AN_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_SC_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_LC_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_DR_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_DRHandset_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_DRSMSC_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_ConcatenationSupport_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_MNPSupport_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleTxtBoxValueAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_DataCodingSupport_List_Textbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_PreRegAN_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_Default_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_FormatTemplateRegistration_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_TATforPreReg_Checkbox", includeEnabledStatus));			
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_ContentWhitelist_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_FormatTemplateContent_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_TATforContent_Checkbox", includeEnabledStatus));			
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_NoObjectionCertificate_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleChkBoxStatusAlongWithEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_TradeLicenseRequired_Checkbox", includeEnabledStatus));
			lst.add(cu.getMultipleTxtBoxValue("SupplierCoverage_Table_All_Row_Coloumn_Remarks_Textbox"));					
			lst = transpose(lst);
			return lst;
		}
		
		
		Map<String, String> getUITableCoverageChkBoxEnabledStatusAloneDestination(Map<String, String>  retMap)
		 {
			 driver.manage().timeouts().implicitlyWait(1, TimeUnit.MILLISECONDS);
			int tableRowSize = cu.ElementsSizeCount("SupplierCoverage_AllTableDataRows");
			
			Map<String, String> currentVisibleDestionData = getVisibleRowsCoverageChkBoxEnabledStatusWithDestination();			
			retMap.putAll(currentVisibleDestionData);
					
			cu.scrollPageToViewElement("SupplierCoverage_Table_DynamicIndex_Row_AllColoumn", "$index$", tableRowSize+"");
			cu.sleep(300);
			
			String lastDestinationName = cu.getText("SupplierCoverage_Table_DynamicIndex_Row_Coloumn_Destination_Text", "$index$", tableRowSize+"");
			if(!retMap.containsKey(lastDestinationName))
				getUITableCoverageChkBoxEnabledStatusAloneDestination(retMap);					
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			return retMap;
		 }
		
		 Map<String, String> getVisibleRowsCoverageChkBoxEnabledStatusWithDestination()
		{
			Map<String, String> retMap = new LinkedHashMap<>();
			List<String> destinations = cu.ElementsToList("SupplierCoverage_Table_All_Row_Coloumn_Destination_Text");
			List<String> covChkBxEnabledStatus = cu.getMultipleEnabledStatus("SupplierCoverage_Table_All_Row_Coloumn_Coverage_Checkbox");			
			for(int i=0;i<destinations.size();i++)
				retMap.put(destinations.get(i), covChkBxEnabledStatus.get(i));
			return retMap;
		}
		
		
		
		
		 List<List<String>> transpose(List<List<String>> table) {
	        List<List<String>> ret = new LinkedList<List<String>>();
	        final int N = table.get(0).size();
	        for (int i = 0; i < N; i++) {
	            List<String> col = new LinkedList<String>();
	            for (List<String> row : table) {
	                col.add(row.get(i));
	            }
	            ret.add(col);
	        }
	        return ret;
	    }
		
		 
		 
		 void expandMainFilter()
		 {
		 	if("false".equals(cu.getAttribute("SupplierCoverage_MainFilter_ExpandedStatus", "aria-expanded")))
		 	{
		 		cu.clickElement("SupplierCoverage_MainFilter_ExpandCollapseToggleDiv");
		 	}
		 	
		 	cu.getScreenShot("MainFilter Expanded");
		 }

		 void collapseMainFilter()
		 {
		 	String status = cu.getAttribute("SupplierCoverage_MainFilter_ExpandedStatus", "aria-expanded");
		 	if(status==null || "true".equals(status))
		 	{
		 		cu.clickElement("SupplierCoverage_MainFilter_ExpandCollapseToggleDiv");
		 	}
		 	
		 	cu.getScreenShot("MainFilter Collapsed");
		 }
		 
		 void selectService(String service)
		 {
			//Select service
				cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_ServiceListToggleDiv","SupplierCoverage_ServiceListDynamicOption", "$optionvalue$", service);
				//cu.clickElement("SupplierCoveragePage");
				cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
				
		 }
		 
		 void selectSupplier(String supplier)
		 {			
			//Select SupplierName
			cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_SupplierNameListToggleDiv","SupplierCoverage_SupplierNameListDynamicOption", "$optionvalue$", supplier);
			//cu.clickElement("SupplierCoveragePage");
			cu.sleep(500);
			cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");				
		 }
		 
		 void selectSupplierAccount(String supplierAccount)
		 {
			//Select Supplier Account Name
			cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_SupplierAccountNameListToggleDiv","SupplierCoverage_SupplierAccountNameListDynamicOption", "$optionvalue$", supplierAccount);
			//cu.clickElement("SupplierCoveragePage");
			cu.sleep(500);
			cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");		
		 }
		 
		 void selectFirstHistory()
		 {
			// select first history option
			cu.clickElement("SupplierCoverage_CoverageHistoryListToggleDiv");
			cu.sleep(300);
			cu.clickElement("SupplierCoverage_CoverageHistoryListToggleDiv");
			List<String> historyLst = cu.ElementsToList("SupplierCoverage_CoverageHistoryListAllOptions");		
			if(historyLst.size()>0)
			{
				cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_CoverageHistoryListToggleDiv","SupplierCoverage_CoverageHistoryListDynamicOption", "$optionvalue$", historyLst.get(0));
				
				if(cu.elementDisplayed("SupplierCoverage_PopupWindow", 1))
					cu.clickElement("SupplierCoverage_PopupWindow_YesButton");
				//cu.clickElement("SupplierCoveragePage");
				cu.sleep(500);
				cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
			}
		 }
		 
		 void clickOnDisplayButton()
		 {
			// click on display button
			cu.clickElement("SupplierCoverage_DisplayBtn");
			cu.sleep(1000);
			cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
		 }
		 
		 void clickOnSubmitButton()
		 {
			// click on display button
			cu.clickElement("SupplierCoverage_SubmitBtn");
			cu.sleep(1000);
			cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
		 }
		 
}
