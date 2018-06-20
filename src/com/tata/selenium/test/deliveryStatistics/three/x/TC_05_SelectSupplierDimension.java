package com.tata.selenium.test.deliveryStatistics.three.x;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_05_SelectSupplierDimension implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_05_SelectSupplierDimension.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics3x.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	private WebDriver driver;
	private ExtentTest test;
	private CommonUtils cu;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - "+TC_05_SelectSupplierDimension.class.getName()+" -with TestdataId: " + uniqueDataId);
		String sheetName = "Delivery_Statistics_Screen3.x";
		
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

		if(cu.elementDisplayed("application_PopUpMessage", 2))
			cu.checkMessage("application_PopUpMessage", "After loading the page",
				"No data for the selected input parameters.");
		//cu.executeInjectJQuery();
		cu.waitForPageLoadWithSleep("DeliveryStatisticsPage", 20);
		cu.waitForElementVisiblity("DeliveryStat_SubmitButton", 180);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		//cu.checkElementPresent("DeliveryStat_SubmitButton");
		selectMainFilter();
		cu.clickElement("DeliveryStat_SubmitButton");
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
				
		collapseMainFilter();
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in Supplier Dimension Main Page");		
		validateTableHeaders(dataMap.get("MainTableHeaders"), "Supplier Dimension Main Page", "allMainTableHeaders");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in Supplier Dimension Main Page");
		
		//Get UI table data
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_Main = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																																, "allMainTableHeaders"
																																	, "allTableDataRows"
																																		, "table_Dynamic_Row_AllColoumn");
		//Scroll to top of the table
		cu.executeJavaScrpit("mainTableBody", "arguments[0].scrollTop = 0;");
		
		if("MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole").trim()))			
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_Main, "MMX-DeliveryStatisticsData*.csv", "Supplier Dimension Main Page", 17, "DeliveryStat_ExportButton", "DeliveryStat_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_Main, "MMX-DeliveryStatisticsData*.csv", "Supplier Dimension Main Page", 17, "DeliveryStat_ExportButton", "DeliveryStat_ExportAllRecords");
						
		List<String> supplierAccValues = getColoumnListFromRawTableData(uiTableDataAlongWithHeadersWithoutMapping_Main, "Supplier Account");

		for (String suppAccVal : supplierAccValues) 
		{
			mainPageAndSubsequenceDrillDownValidation(suppAccVal, uiTableDataAlongWithHeadersWithoutMapping_Main);
		}

		test = cu.getExTest();
		//msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}
	
	





	private void validateTableHeaders(String tabHeadsStr, String nameOfTable, String locatorFieldNameForAllTableHeaders) 
	{		
		cu.getScreenShot(nameOfTable+" - Table header validation");
		List<String> tableHeaders = Arrays.asList(tabHeadsStr.trim().split("\\~"));
		List<String> actualHeaders = modifyHeaders(cu.ElementsToList(locatorFieldNameForAllTableHeaders));
		
		if(tableHeaders.size() == actualHeaders.size())
			test.log(LogStatus.PASS,
					"EXPECTED: '"+nameOfTable+"' - Table headers count should be matched",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: '"+nameOfTable+"' - Table headers count has been matched. Count: "+actualHeaders.size()+"</span>");
		else
			test.log(LogStatus.FAIL,
					"EXPECTED: '"+nameOfTable+"' - Table headers count should be matched",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: '"+nameOfTable+"' - Table headers count has not been matched."
							+"<br/> Expected:<br/>"+tableHeaders.size()
							+"<br/><br/>Actual: "+actualHeaders.size()+"</span>");
		
		if(tableHeaders.equals(actualHeaders))
			test.log(LogStatus.PASS,
					"EXPECTED: '"+nameOfTable+"' - Table headers should be matched",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: '"+nameOfTable+"' - Table headers have been matched.</span>");
		else
			test.log(LogStatus.FAIL,
					"EXPECTED: '"+nameOfTable+"' - Table headers should be matched",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: '"+nameOfTable+"' - Table headers have not matched.<br/> Expected:<br/>"+tableHeaders.toString()
					+"<br/><br/>Actual: "+actualHeaders.toString()+"</span>");
		
	}

	public Map<String, Double> getUIValesMainPage(String customerAccValue, List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_Main)  {
		Map<String, Double> ret = new HashMap<>();

		Map<String, Map<String, String>> tableMap = convertListTableDataToMapOfMapData(uiTableDataAlongWithHeadersWithoutMapping_Main, "Supplier Account");
		try {			
			ret.put("Attempted Success", Double.valueOf(tableMap.get(customerAccValue).get("Attempted Success").replace(",", "")));
			ret.put("Attempted Failure",Double.valueOf(tableMap.get(customerAccValue).get("Attempted Failure").replace(",", "")));
			ret.put("Submitted Success", Double.valueOf(tableMap.get(customerAccValue).get("Submitted Success").replace(",", "")));
			ret.put("Submitted Failure", Double.valueOf(tableMap.get(customerAccValue).get("Submitted Failure").replace(",", "")));
			ret.put("Submitted Percentage", Double.valueOf(tableMap.get(customerAccValue).get("Submitted %").replace("%", "")));
			ret.put("Enroute", Double.valueOf(tableMap.get(customerAccValue).get("Enroute")));
			ret.put("Delivered Success", Double.valueOf(tableMap.get(customerAccValue).get("Delivered Success").replace(",", "")));
			ret.put("Delivered Failure", Double.valueOf(tableMap.get(customerAccValue).get("Delivered Failure").replace(",", "")));
			ret.put("Delivered Percentage", Double.valueOf(tableMap.get(customerAccValue).get("Delivered %").replace("%", "")));
			ret.put("E2E Latency (s)", Double.valueOf(tableMap.get(customerAccValue).get("E2E Latency (s)").replace(",", "")));
			ret.put("Ack Latency (ms)", Double.valueOf(tableMap.get(customerAccValue).get("Ack Latency (ms)").replace(",", "")));
			ret.put("Platform Latency (ms)", Double.valueOf(tableMap.get(customerAccValue).get("Platform Latency (ms)").replace(",", "")));
			ret.put("Delivery Latency (s)", Double.valueOf(tableMap.get(customerAccValue).get("Delivery Latency (s)").replace(",", "")));
			
			return ret;
		} catch (Exception e) {
			LOGGER.error("Error occured while reading the data from UI -- "+e);
			cu.getScreenShot("Get webtable data for Customer Account Value: " + customerAccValue);
			test.log(LogStatus.FAIL,"", "Exception occur while fething the data from UI - " + e);
			return ret;
		}
	}

	public Map<String, Integer> exportCSVAndGetCoverageFieldsUpdated()
    {
		cu.clickElement("drillDownSuppAccWin_ExportButton");
		cu.moveAndClick("drillDownSuppAccWin_ExportAllRecords");
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + "Customer-SMSCustomerAccountDistribution" + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTED: Exported file name should be in 'Customer-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Customer-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTED: Exported file name should be in 'Customer-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Customer-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 6);

		List<String> attemStrs = csvu.getSingleColAllData("Attempted");
		int AttemptedCount = csvu.getSingleColSum(attemStrs);

		List<String> attemFailureStrs = csvu.getSingleColAllData("Attempted Failure");
		int AttemptedFailureCount = csvu.getSingleColSum(attemFailureStrs);

		List<String> submittedStrs = csvu.getSingleColAllData("Submitted");
		int Submittedcount = csvu.getSingleColSum(submittedStrs);

		List<String> submittedFailureStrs = csvu.getSingleColAllData("Submit Failure");
		int SubmitedFailureCount = csvu.getSingleColSum(submittedFailureStrs);

		List<String> deliveredStrs = csvu.getSingleColAllData("Delivered");
		int DeliveredCount = csvu.getSingleColSum(deliveredStrs);

		
		Map<String, Integer> exceldata = new HashMap<>();
		try {
			exceldata.put("Attempted", AttemptedCount);
			exceldata.put("Attempted Failure", AttemptedFailureCount);
			exceldata.put("Submitted", Submittedcount);
			exceldata.put("Submit Failure", SubmitedFailureCount);
			exceldata.put("Delivered", DeliveredCount);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		return exceldata;
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
	  public void afterMethodFailed(ITestResult result) {		  
		  
		  if(ITestResult.FAILURE ==result.getStatus()
				  && !ExceptionUtils.getRootCauseMessage(result.getThrowable()).startsWith("AssertionError:")){		
			  
			  test.log(LogStatus.FAIL, "Error Ocuured in while executing the test case.", "Exception trace:<br/><br/> "
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
	
	void selectMainFilter()
	{
		
		expandMainFilter();
		//Select filters
		//Select ServiceLst filter
		if(!dataMap.get("DeliveryStat_ServiceLst").trim().isEmpty())			
			cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_ServiceListToggleDiv","DeliveryStat_ServiceListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_ServiceLst"));
					
			cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		    
		//Select Dimension filter
		if(!dataMap.get("Dimension").trim().isEmpty()){
			
			cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_DimensionListToggleDiv","DeliveryStat_DimensionListDynamicOption", "$optionvalue$", dataMap.get("Dimension"));

		cu.clickElement("DeliveryStatisticsPage");
		cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		}	
			
			
		  //Select Product filter
			if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
						&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
							&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )	
			{
				if(!dataMap.get("DeliveryStat_ProductLst").isEmpty() && !dataMap.get("DeliveryStat_ProductLst").contains("Select ALL"))
				{
					/*cu.clickElement("DeliveryStat_ProductListToggleButton");
					cu.unSelectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", "Select ALL");
					for(String currProduct : dataMap.get("DeliveryStat_ProductLst").split("\\~"))
					{
						cu.setData("DeliveryStat_ProductListSearchTxtBox", currProduct);
						cu.sleep(500);
						cu.selectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", currProduct);
					}
					cu.clickElement("DeliveryStat_ProductLabel");*/
					
					cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_ProductListToggleDiv","DeliveryStat_ProductListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_ProductLst"));
					cu.clickElement("DeliveryStatisticsPage");
					cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
				}
			}
		    
					
		//Select Customer filter
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) && !dataMap.get("DeliveryStat_Customer_NameLst").trim().isEmpty())
		{
			cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_Customer_NameListToggleDiv","DeliveryStat_Customer_NameListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_Customer_NameLst"));

			cu.clickElement("DeliveryStatisticsPage");
			cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		}
		
		//Select Supplier filter
		if(!"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )
		{
			if(!dataMap.get("DeliveryStat_Supplier_NameLst").trim().isEmpty())
			{
				cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_Supplier_NameListToggleDiv","DeliveryStat_Supplier_NameListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_Supplier_NameLst"));
				cu.clickElement("DeliveryStatisticsPage");
				cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
			}
		}
		
		
		//Select Country filter
		if(!dataMap.get("DeliveryStat_CountryLst").trim().isEmpty())
		{
			cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_CountryListToggleDiv","DeliveryStat_CountryListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_CountryLst"));

			cu.clickElement("DeliveryStatisticsPage");
			cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		}
		
		
		
		//Select Destination filter
		if(!dataMap.get("DeliveryStat_DestinationLst").trim().isEmpty())
		{
			System.out.println();
			cu.SelectDropDownByVisibleTextCustomMMX3("DeliveryStat_DestinationListToggleDiv", "DeliveryStat_DestinationListTextBox", "DeliveryStat_DestinationListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_DestinationLst"));

			cu.clickElement("DeliveryStatisticsPage");
			cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		}
		
		
		
		//Select Instance filter
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) 
				&& !"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
						&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )		
		{
			if(!dataMap.get("DeliveryStat_InstanceLst").trim().isEmpty())
			{
//				cu.deselectDropDownAllOptions("DeliveryStat_InstanceLst");
//				cu.SelectDropDownByVisibleText("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));
				
				cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_HostsListToggleDiv","DeliveryStat_HostsListDynamicOption", "$optionvalue$", dataMap.get("DeliveryStat_InstanceLst"));
				cu.clickElement("DeliveryStatisticsPage");
				cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
			}
		}

		// Select From DATE			
		/*cu.moveAndClick("DeliveryStat_FromDateTxt");
		cu.sleep(1000);
		cu.moveAndClick("clickYear");
		cu.sleep(1000);
		cu.calYear(dataMap.get("FromYear"));
		cu.sleep(1000);
		cu.moveAndClick("selectMonth");
		cu.sleep(1000);
		cu.calMonth(dataMap.get("FromMonth"));
		cu.sleep(1000);
		cu.calDate(dataMap.get("FromDay"));
		cu.sleep(1000);
		cu.clickElement("clickOutside");

		// Select TO DATE
		cu.moveAndClick("DeliveryStat_ToDateTxt");
		cu.sleep(1000);
		cu.moveAndClick("selectMonth_ToDate");
		cu.sleep(1000);
		cu.calMonth(dataMap.get("ToMonth"));
		cu.sleep(1000);
		cu.calDate(dataMap.get("ToDay"));
		cu.sleep(1000);
		cu.clickElement("DeliveryStatisticsPage");*/
		if(!dataMap.get("PeriodDate").trim().isEmpty()){
		cu.selectCheckBox("DeliveryStat_PeriodRadiobtn");
		cu.selectDropDownByVisibleTextCustomMMX3("DeliveryStat_PeriodComboListToggleDiv", "DeliveryStat_PeriodComboListDynamicOption", "$optionvalue$", dataMap.get("PeriodDate"));
		cu.waitUntilElemetDisappearsMMX3("DeliveryStatisticsPageLoad");
		}
}
	
	
	void mainPageAndSubsequenceDrillDownValidation(String suppAccVal, List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_Main)
	{
		test.log(LogStatus.INFO, "$$$$$$$$$$$ Validating for supplier account: "+suppAccVal);
		Map<String, Double> uiDataMainPage = getUIValesMainPage(suppAccVal, uiTableDataAlongWithHeadersWithoutMapping_Main);
		
		//checking % and enroute valdiations on main page		
		
		Double submittedPercentageExpected = calculatePercentage(uiDataMainPage.get("Submitted Success"), uiDataMainPage.get("Attempted Success"));
		Double enrouteExpected = ( uiDataMainPage.get("Submitted Success") - (uiDataMainPage.get("Delivered Success") + uiDataMainPage.get("Delivered Failure")) );
		Double deliveredPercentageExpected = calculatePercentage(uiDataMainPage.get("Delivered Success"), uiDataMainPage.get("Submitted Success"));
		
		Double submittedPercentageActual = uiDataMainPage.get("Submitted Percentage");
		Double enrouteActual = uiDataMainPage.get("Enroute");
		Double deliveredPercentageActual = uiDataMainPage.get("Delivered Percentage");
		
		//main page Submitted Percentage validation 
		if(compareDoubleWithTwoDigit(roundDobule(submittedPercentageExpected), submittedPercentageActual))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Submitted Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+roundDobule(submittedPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+submittedPercentageActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Submitted Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+roundDobule(submittedPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+submittedPercentageActual+"</span>");
		}
		
		//main page Delivered Percentage validation
		if(compareDoubleWithTwoDigit(roundDobule(deliveredPercentageExpected), deliveredPercentageActual))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Delivered Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+roundDobule(deliveredPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+deliveredPercentageActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Delivered Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+roundDobule(deliveredPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+deliveredPercentageActual+"</span>");
		}
		
		
		//main page Enroute validation
		if(compareDoubleWithTwoDigit(roundDobule(enrouteExpected), enrouteActual))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Enroute (main page)for Supplier Account: "+suppAccVal+" should be "+enrouteExpected+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Enroute (main page)for Supplier Account: "+suppAccVal+" is same as  "+enrouteActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Enroute (main page)for Supplier Account: "+suppAccVal+" should be "+enrouteExpected+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Enroute (main page)for Supplier Account: "+suppAccVal+" is not same as "+enrouteActual+"</span>");
		}
		
		firstAndSubsequenceDrillDownValidation(suppAccVal, uiDataMainPage);
	}
 
	
	
	void firstAndSubsequenceDrillDownValidation(String suppAccVal, Map<String, Double> uiDataMainPage)
	{
		//Entering Customer Account Distribution Page ( 1st drilldown		
//		cu.clickElement("dynamicCustAccMainTableLink", "$customerAccName$", cusAccVal);
		cu.clickElementAfterScrollToView("dynamicSuppAccMainTableLink", "$supplierAccName$", suppAccVal);
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		test.log(LogStatus.INFO, "######### Validating 1st drilldown ( Supplier Account Distribution Page ) for Customer account: "+suppAccVal);
		
		if(!cu.elementDisplayed("drillDownSuppAccWin"))
		{
			cu.getScreenShot("Supplier Account Distribution Window Not Opened");
			test.log(LogStatus.FAIL, "Supplier Account Distribution Window for Supplier account: "+suppAccVal+"  not opened");
			Assert.fail("Supplier Account Distribution Window for Supplier account: "+suppAccVal+"  not opened");
		}
		
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 1st drilldown ( Supplier Account Distribution Page ) for Supplier account: "+suppAccVal);		
		validateTableHeaders(dataMap.get("FisrtDrilldownHeaders"), "Supplier Account Distribution Page", "drillDownSuppAccWin_AllTableHeaders");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 1st drilldown ( Supplier Account Distribution Page ) for Supplier account: "+suppAccVal);	
		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_1stDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																					, "drillDownSuppAccWin_AllTableHeaders"
																						, "drillDownSuppAccWin_AllTableDataRows"
																							, "drillDownSuppAccWin_Table_Dynamic_Row_AllColoumn");
		
		cu.executeJavaScrpit("drillDownSuppAccWin_TableBody", "arguments[0].scrollTop = 0;");
		exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "MMX-SupplierAccountData*.csv", "Supplier Account Distribution Page", 6, "drillDownSuppAccWin_ExportButton", "drillDownSuppAccWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 1st drilldown ( Supplier Account Distribution Page ) for Supplier account: "+suppAccVal);				
		cu.checkElementPresence("drillDownSuppAccWin_ExportButton");
		cu.checkElementPresence("drillDownSuppAccWin_BackButton");
		cu.checkElementPresence("drillDownSuppAccWin_CustomerNameTextBox");
		cu.checkElementPresence("drillDownSuppAccWin_CustomerAccountrNameTextBox");
		cu.checkElementPresence("drillDownSuppAccWin_FromDateTextBox");
		cu.checkElementPresence("drillDownSuppAccWin_ToDateTextBox");

		//Map<String, Integer> csvData = exportCSVAndGetCoverageFieldsUpdated();
		Map<String, Integer> csvData = new LinkedHashMap<>();		
		csvData.put("Attempted Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Attempted Failure"));
		csvData.put("Submitted Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Submit Failure"));
		csvData.put("Delivered Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Delivered Failure"));
		
		// Validate summation of 1st drilldown ( Customer Account Distribution Page ) with MAIN page
		test.log(LogStatus.INFO, "Validate summation of 1st drilldown ( Supplier Account Distribution Page ) with MAIN page for Supplier account: "+suppAccVal);	
		for(String colName : csvData.keySet())
		{
				
		    	Integer uiValue = Integer.valueOf((int) (uiDataMainPage.get(colName)-0));
		    	
				if(csvData.get(colName).equals(uiValue))					
					test.log(LogStatus.PASS,
							"EXPECTED: "
									+ "Validate the summation of 1st drilldown and the MAIN page of this Supplier account"
									+ suppAccVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
							"Validation:  <span style='font-weight:bold;'>ACTUAL::"
									+ "Validate the summation of 1st drilldown and the MAIN page of this Supplier account"
									+ suppAccVal + " are same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
				else
					test.log(LogStatus.FAIL,
							"EXPECTED: "
							+ "Validate the summation of 1st drilldown and the MAIN page of this Supplier account"
							+ suppAccVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
					"Validation:  <span style='font-weight:bold;'>ACTUAL::"
							+ "Validate the summation of 1st drilldown and the MAIN page of this Supplier account"
							+ suppAccVal + " are not same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
		}
		
		
		//second drilldown		
		List<Map<String, String>> allRowsMapList_1stDrill = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill);
		
		for(Map<String, String> currMap_1stDrill : allRowsMapList_1stDrill)
		{
			cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_FirstOrAllColoumnColoum"
					, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$"
						, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
							+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account"));
			
			
			String suppNameDrill = cu.getAttribute("drillDownSuppAccWin_SupplierNameTextBox", "value");
			String suppAccNameDrill = cu.getAttribute("drillDownSuppAccWin_SupplierAccountNameTextBox", "value");
			
			currMap_1stDrill.put("SupplierNameDrillTxtBoxVal", suppNameDrill);
			currMap_1stDrill.put("SupplierAccNameDrillTxtBoxVal", suppAccNameDrill);
			
			if(!"0".equals(currMap_1stDrill.get("Attempted Failure").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Attempted Failure ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownAttemptedFailureValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("Submit Failure").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Submit Failure ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownSubmitFailureValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("Delivered Failure").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Delivered Failure ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownDeliveredFailureValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("Ack Latency (ms)").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Ack Latency ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownAckLatencyValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("E2E Latency (s)").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( E2E Latency ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownE2ELatencyValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("Platform Latency (ms)").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Platform Latency ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownPlatformLatencyValidation(currMap_1stDrill);
			}
			
			if(!"0".equals(currMap_1stDrill.get("Delivery Latency (s)").trim()))
			{				
				test.log(LogStatus.INFO, "Validating 2nd drilldown ( Delivery Latency ) Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				secondDrillDownDeliveryLatencyValidation(currMap_1stDrill);
			}			
		}
		
		//Returning back to Main page
		cu.clickElement("drillDownSuppAccWin_BackButton");
		
 }
	
	
	private void secondDrillDownAttemptedFailureValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_AttemptedFailureColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$AttemptedFailureValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Attempted Failure"));
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillAttemptedFailureDisWin"))
		{
			cu.getScreenShot("Attempted Failure Distribution Window Not Opened");
			test.log(LogStatus.FAIL, "Attempted Failure Distribution Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Attempted Failure Distribution Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Attempted Failure Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_Fail"), "Attempted Failure Distribution", "drillAttemptedFailureDisWin_AllTableHeaders");
				
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Attempted Failure Page )");
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillAttemptedFailureDisWin_AllTableHeaders"
																							, "drillAttemptedFailureDisWin_AllTableDataRows"
																								, "drillAttemptedFailureDisWin_Table_Dynamic_Row_AllColoumn");
				
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-AttemptedFailureData*.csv", "Attempted Failure Page", 9, "drillAttemptedFailureDisWin_ExportButton", "drillAttemptedFailureDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-AttemptedFailureData*.csv", "Attempted Failure Page", 9, "drillAttemptedFailureDisWin_ExportButton", "drillAttemptedFailureDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Attempted Failure Page )");				
		cu.checkElementPresence("drillAttemptedFailureDisWin_ExportButton");
		cu.checkElementPresence("drillAttemptedFailureDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillAttemptedFailureDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillAttemptedFailureDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_SupplierAccountNameTextBox");	
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_DestinationTextBox");
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_FromDateTextBox");
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_ToDateTextBox");
		cu.checkReadonlyProperty("drillAttemptedFailureDisWin_TotalFailureTextBox");	
		
		//Validating Sumation of total failure
		test.log(LogStatus.INFO, "Validating Summation of total failure in 2nd drilldown ( Attempted Failure Page )");
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillAttemptedFailureDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillAttemptedFailureDisWin_CustomerAccountNameTextBox", "value");		
		int totalFailureTop = Integer.valueOf(cu.getAttribute("drillAttemptedFailureDisWin_TotalFailureTextBox", "value"));	
		
		//failure sum validations
		int totalFailureBott = getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "Failed");
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");
		if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Total failures for "+customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is same as "+totalFailureBott+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Total failures for "+ customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is not as "+totalFailureBott+"</span>");
		}
		
		//failure percentage validations
		test.log(LogStatus.INFO, "Validating  Failure percentage in 2nd drilldown ( Attempted Failure Page )");		
		List<Map<String, String>> attFailedTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);				
		for(int j=0;j<attFailedTableRowMapList.size(); j++)
		{
			String failureReason = attFailedTableRowMapList.get(j).get("Failure Reason");
			double actualPercentage = Double.valueOf(attFailedTableRowMapList.get(j).get("Failed %").replace("%", ""));
			
			double expectedPercentageRaw = (Double.valueOf(attFailedTableRowMapList.get(j).get("Failed").replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
			double expectedPercentageRoundOff = Math.round(expectedPercentageRaw * 100.0) / 100.0;
			
			if(compareDoubleWithTwoDigit(expectedPercentageRoundOff, actualPercentage))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is same as "+actualPercentage+"</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is not same as "+actualPercentage+"</span>");
			}
		}
		
		cu.clickElement("drillAttemptedFailureDisWin_BackButton");
	}
	
	private void secondDrillDownSubmitFailureValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_SubmitFailureColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$SubmitFailureValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Submit Failure"));
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillSubmitFailureDisWin"))
		{
			cu.getScreenShot("Submit Failure Distribution Window Not Opened");
			test.log(LogStatus.FAIL, "Submit Failure Distribution Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Submit Failure Distribution Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
				
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Submit Failure Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_Fail"), "Submit Failure Distribution", "drillSubmitFailureDisWin_AllTableHeaders");
				
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Submit Failure Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillSubmitFailureDisWin_AllTableHeaders"
																							, "drillSubmitFailureDisWin_AllTableDataRows"
																								, "drillSubmitFailureDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-SubmitFailureData*.csv", "Submit Failure Page", 9, "drillSubmitFailureDisWin_ExportButton", "drillSubmitFailureDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-SubmitFailureData*.csv", "Submit Failure Page", 9, "drillSubmitFailureDisWin_ExportButton", "drillSubmitFailureDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Submit Failure Page )");				
		cu.checkElementPresence("drillSubmitFailureDisWin_ExportButton");
		cu.checkElementPresence("drillSubmitFailureDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillSubmitFailureDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillSubmitFailureDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_SupplierAccountNameTextBox");	
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_DestinationTextBox");
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_FromDateTextBox");
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_ToDateTextBox");
		cu.checkReadonlyProperty("drillSubmitFailureDisWin_TotalFailureTextBox");	
		
		//Validating Sumation of total failure
		test.log(LogStatus.INFO, "Validating Summation of total failure in 2nd drilldown ( Submit Failure Page )");
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillSubmitFailureDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillSubmitFailureDisWin_CustomerAccountNameTextBox", "value");		
		int totalFailureTop = Integer.valueOf(cu.getAttribute("drillSubmitFailureDisWin_TotalFailureTextBox", "value"));	
		
		//failure sum validations
		int totalFailureBott = getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "Failed");
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");
		if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Total failures for "+customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is same as "+totalFailureBott+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Total failures for "+ customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is not as "+totalFailureBott+"</span>");
		}
		
		//failure percentage validations
		test.log(LogStatus.INFO, "Validating  Failure percentage in 2nd drilldown ( Submit Failure Page )");		
		List<Map<String, String>> attFailedTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);				
		for(int j=0;j<attFailedTableRowMapList.size(); j++)
		{
			String failureReason = attFailedTableRowMapList.get(j).get("Failure Reason");
			double actualPercentage = Double.valueOf(attFailedTableRowMapList.get(j).get("Failed %").replace("%", ""));
			
			double expectedPercentageRaw = (Double.valueOf(attFailedTableRowMapList.get(j).get("Failed").replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
			double expectedPercentageRoundOff = Math.round(expectedPercentageRaw * 100.0) / 100.0;
			
			if(compareDoubleWithTwoDigit(expectedPercentageRoundOff, actualPercentage))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is same as "+actualPercentage+"</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is not same as "+actualPercentage+"</span>");
			}
		}
		
		cu.clickElement("drillSubmitFailureDisWin_BackButton");
	
		
	}
	
	private void secondDrillDownDeliveredFailureValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_DeliveredFailureColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$DeliveredFailureValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Delivered Failure"));	
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillDownDeliveredFailureDisWin"))
		{
			cu.getScreenShot("Delivered Failure Distribution Window Not Opened");
			test.log(LogStatus.FAIL, "Delivered Failure Distribution Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Delivered Failure Distribution Window for Customer Supplier: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
		
				
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Delivered Failure Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_Fail"), "Delivered Failure Distribution", "drillDownDeliveredFailureDisWin_AllTableHeaders");
				
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Delivered Failure Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillDownDeliveredFailureDisWin_AllTableHeaders"
																							, "drillDownDeliveredFailureDisWin_AllTableDataRows"
																								, "drillDownDeliveredFailureDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-DeliveryFailureData*.csv", "Submit Failure Page", 9, "drillDownDeliveredFailureDisWin_ExportButton", "drillDownDeliveredFailureDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-DeliveryFailureData*.csv", "Submit Failure Page", 9, "drillDownDeliveredFailureDisWin_ExportButton", "drillDownDeliveredFailureDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Delivered Failure Page )");				
		cu.checkElementPresence("drillDownDeliveredFailureDisWin_ExportButton");
		cu.checkElementPresence("drillDownDeliveredFailureDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillDownDeliveredFailureDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_SupplierAccountNameTextBox");	
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_DestinationTextBox");
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_FromDateTextBox");
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_ToDateTextBox");
		cu.checkReadonlyProperty("drillDownDeliveredFailureDisWin_TotalFailureTextBox");	
		
		//Validating Sumation of total failure
		test.log(LogStatus.INFO, "Validating Summation of total failure in 2nd drilldown ( Delivered Failure Page )");
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillDownDeliveredFailureDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillDownDeliveredFailureDisWin_CustomerAccountNameTextBox", "value");
		int totalFailureTop = Integer.valueOf(cu.getAttribute("drillDownDeliveredFailureDisWin_TotalFailureTextBox", "value"));	
		
		//failure sum validations
		int totalFailureBott = getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "Failed");
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");
		if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Total failures for "+customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is same as "+totalFailureBott+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Total failures for "+ customerSuppHierarchyString +" should be "+totalFailureTop,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for "+ customerSuppHierarchyString +" is not as "+totalFailureBott+"</span>");
		}
		
		//failure percentage validations
		test.log(LogStatus.INFO, "Validating  Failure percentage in 2nd drilldown ( Delivered Failure Page )");		
		List<Map<String, String>> attFailedTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);				
		for(int j=0;j<attFailedTableRowMapList.size(); j++)
		{
			String failureReason = attFailedTableRowMapList.get(j).get("Failure Reason");
			double actualPercentage = Double.valueOf(attFailedTableRowMapList.get(j).get("Failed %").replace("%", ""));
			
			double expectedPercentageRaw = (Double.valueOf(attFailedTableRowMapList.get(j).get("Failed").replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
			double expectedPercentageRoundOff = Math.round(expectedPercentageRaw * 100.0) / 100.0;
			
			if(compareDoubleWithTwoDigit(expectedPercentageRoundOff, actualPercentage))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is same as "+actualPercentage+"</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') should be "+expectedPercentageRoundOff+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for "+ customerSuppHierarchyString +" (failure reson: '"+failureReason+"') is not same as "+actualPercentage+"</span>");
			}
		}
		
		cu.clickElement("drillDownDeliveredFailureDisWin_BackButton");
	
	}

	private void secondDrillDownAckLatencyValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_AckLatencyColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$AckLatencyValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Ack Latency (ms)"));
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillDownAckLatencyDisWin"))
		{
			cu.getScreenShot("Ack Latency Window Not Opened");
			test.log(LogStatus.FAIL, "Ack Latency Window for Customer account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Ack Latency Window for Customer account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Ack Latency Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_AckLatency"), "Ack Latency Distribution Page", "drillDownAckLatencyDisWin_AllTableHeaders");
				
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Ack Latency Distribution Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillDownAckLatencyDisWin_AllTableHeaders"
																							, "drillDownAckLatencyDisWin_AllTableDataRows"
																								, "drillDownAckLatencyDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-AckLatencyData*.csv", "Ack Latency Distribution Page", 9, "drillDownAckLatencyDisWin_ExportButton", "drillDownAckLatencyDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-AckLatencyData*.csv", "Ack Latency Distribution Page", 9, "drillDownAckLatencyDisWin_ExportButton", "drillDownAckLatencyDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Ack Latency Page )");		
		cu.checkElementPresence("drillDownAckLatencyDisWin_ExportButton");
		cu.checkElementPresence("drillDownAckLatencyDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillDownAckLatencyDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillDownAckLatencyDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillDownAckLatencyDisWin_SupplierAccountNameTextBox");		
		cu.checkReadonlyProperty("drillDownAckLatencyDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillDownAckLatencyDisWin_DestinationTextBox");		
				
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillDownAckLatencyDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillDownAckLatencyDisWin_CustomerAccountNameTextBox", "value");			
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");

		//percentage validations
		List<Map<String, String>> ackLatencyTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);
		if(ackLatencyTableRowMapList.size()>0)
		{		
			//0-100 ms % Validation
			double expectedPercentage0To100msPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("0-100 ms"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage0To100msPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("0-100 ms %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage0To100msPer, actualPercentage0To100msPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (0-100 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To100msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-100 ms %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage0To100msPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (0-100 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To100msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-100 ms %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage0To100msPer+" (+/- 0.01 offset)</span>");
			}

			//100-200 ms % Validation
			double expectedPercentage100To200msPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("100-200 ms"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage100To200msPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("100-200 ms %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage100To200msPer, actualPercentage100To200msPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (100-200 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage100To200msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (100-200 ms %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage100To200msPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (100-200 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage100To200msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (100-200 ms %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage100To200msPer+" (+/- 0.01 offset)</span>");
			}
			
			//200-300 ms % Validation
			double expectedPercentage200To300msPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("200-300 ms"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage200To300msPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("200-300 ms %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage200To300msPer, actualPercentage200To300msPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (200-300 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage200To300msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (200-300 ms %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage200To300msPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (200-300 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage200To300msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (200-300 ms %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage200To300msPer+" (+/- 0.01 offset)</span>");
			}
			
			//300-400 ms % Validation
			double expectedPercentage300To400msPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("300-400 ms"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage300To400msPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("300-400 ms %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage300To400msPer, actualPercentage300To400msPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (300-400 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage300To400msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (300-400 ms %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage300To400msPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (300-400 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage300To400msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (300-400 ms %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage300To400msPer+" (+/- 0.01 offset)</span>");
			}
			
			//400-500 ms % Validation
			double expectedPercentage400To500msPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("400-500 ms"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage400To500msPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("400-500 ms %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage400To500msPer, actualPercentage400To500msPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (400-500 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage400To500msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (400-500 ms %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage400To500msPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (400-500 ms %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage400To500msPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (400-500 ms %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage400To500msPer+" (+/- 0.01 offset)</span>");
			}
			
			//500 ms - 1 sec % Validation
			double expectedPercentage500msTo1SecPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("500 ms - 1 sec"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage500msTo1SecPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("500 ms - 1 sec %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage500msTo1SecPer, actualPercentage500msTo1SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (500 ms - 1 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage500msTo1SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (500 ms - 1 sec %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage500msTo1SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (500 ms - 1 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage500msTo1SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (500 ms - 1 sec %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage500msTo1SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//1 sec - 3 sec % validation
			double expectedPercentage1SecTo3SecPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("1 sec - 3 sec"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage1SecTo3SecPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("1 sec - 3 sec %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage1SecTo3SecPer, actualPercentage1SecTo3SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (1 sec - 3 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage1SecTo3SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (1 sec - 3 sec %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage1SecTo3SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (1 sec - 3 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage1SecTo3SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (1 sec - 3 sec %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage1SecTo3SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//> 3 sec % Validation
			double expectedPercentageGreaterThan3SecPer = roundDobule(calculatePercentage(ackLatencyTableRowMapList.get(0).get("> 3 sec"), ackLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentageGreaterThan3SecPer = roundDobule(Double.valueOf(ackLatencyTableRowMapList.get(0).get("> 3 sec %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentageGreaterThan3SecPer, actualPercentageGreaterThan3SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (> 3 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan3SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 3 sec %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentageGreaterThan3SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (> 3 sec %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan3SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 3 sec %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentageGreaterThan3SecPer+" (+/- 0.01 offset)</span>");
			}			
		}
				
		cu.clickElement("drillDownAckLatencyDisWin_BackButton");
	}
	
	private void secondDrillDownE2ELatencyValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_E2ELatencyColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$E2ELatencyValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("E2E Latency (s)"));
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillDownAverageE2EDisWin"))
		{
			cu.getScreenShot("Average E2E Latency Window Not Opened");
			test.log(LogStatus.FAIL, "Average E2E Latency Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Average E2E Latency Window for Customer Supplier: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( E2E Latency Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_E2ELatency"), "E2E Latency Distribution Page", "drillDownAverageE2EDisWin_AllTableHeaders");
				
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( E2E Latency Distribution Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillDownAverageE2EDisWin_AllTableHeaders"
																							, "drillDownAverageE2EDisWin_AllTableDataRows"
																								, "drillDownAverageE2EDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-E2ELatencyData*.csv", "E2E Latency Distribution Page", 9, "drillDownAverageE2EDisWin_ExportButton", "drillDownAverageE2EDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-E2ELatencyData*.csv", "E2E Latency Distribution Page", 9, "drillDownAverageE2EDisWin_ExportButton", "drillDownAverageE2EDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( E2E Latency Page )");		
		cu.checkElementPresence("drillDownAverageE2EDisWin_ExportButton");
		cu.checkElementPresence("drillDownAverageE2EDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillDownAverageE2EDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillDownAverageE2EDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillDownAverageE2EDisWin_SupplierAccountNameTextBox");	
		cu.checkReadonlyProperty("drillDownAverageE2EDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillDownAverageE2EDisWin_DestinationTextBox");		
				
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillDownAverageE2EDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillDownAverageE2EDisWin_CustomerAccountNameTextBox", "value");	
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");

		//percentage validations
		List<Map<String, String>> e2eLatencyTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);
		if(e2eLatencyTableRowMapList.size()>0)
		{		
			//0-5 Secs Validation
			double expectedPercentage0To5SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("0-5 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage0To5SecPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("0-5 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage0To5SecPer, actualPercentage0To5SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To5SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage0To5SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To5SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage0To5SecPer+" (+/- 0.01 offset)</span>");
			}

			//5-10 Secs % Validation
			double expectedPercentage5To10SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("5-10 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage5To10SecPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("5-10 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage5To10SecPer, actualPercentage5To10SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage5To10SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage5To10SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//10-15 Secs % Validation
			double expectedPercentage10To15SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("10-15 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage10To15SecPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("10-15 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage10To15SecPer, actualPercentage10To15SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To15SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage10To15SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To15SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage10To15SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//15-30 Secs % Validation
			double expectedPercentage15To30SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("15-30 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage15To30SecPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("15-30 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage15To30SecPer, actualPercentage15To30SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage15To30SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage15To30SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage15To30SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage15To30SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//30-60 Secs % Validation
			double expectedPercentage30To60SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("30-60 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage30To60SecPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("30-60 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage30To60SecPer, actualPercentage30To60SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To60SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage30To60SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To60SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage30To60SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//60-120 Secs % Validation
			double expectedPercentage60To120Secs = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("60-120 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage60To120Secs = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("60-120 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage60To120Secs, actualPercentage60To120Secs))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage60To120Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage60To120Secs+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage60To120Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage60To120Secs+" (+/- 0.01 offset)</span>");
			}
			
			//120-180 Secs % validation
			double expectedPercentage120To180SecPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("120-180 Secs"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage120To180SePer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("120-180 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage120To180SecPer, actualPercentage120To180SePer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage120To180SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage120To180SePer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage120To180SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage120To180SePer+" (+/- 0.01 offset)</span>");
			}
			
			//3-10 Min % Validation
			double expectedPercentage3To10MinPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get("3-10 Min"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage3To10MinPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get("3-10 Min %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage3To10MinPer, actualPercentage3To10MinPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage3To10MinPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage3To10MinPer+" (+/- 0.01 offset)</span>");
			}
			
			//>10 Min % Validation
			double expectedPercentageGreaterThan10MinPer = roundDobule(calculatePercentage(e2eLatencyTableRowMapList.get(0).get(">10 Min"), e2eLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentageGreaterThan10MinPer = roundDobule(Double.valueOf(e2eLatencyTableRowMapList.get(0).get(">10 Min %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentageGreaterThan10MinPer, actualPercentageGreaterThan10MinPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (>10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (>10 Min %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentageGreaterThan10MinPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (>10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (>10 Min %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentageGreaterThan10MinPer+" (+/- 0.01 offset)</span>");
			}	
		}
		
		cu.clickElement("drillDownAverageE2EDisWin_BackButton");
	}
	
	private void secondDrillDownPlatformLatencyValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_PlatformLatencyColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$PlatformLatencyValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Platform Latency (ms)"));
		
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillDownPlatformLatencyDisWin"))
		{
			cu.getScreenShot("Platform Latency Window Not Opened");
			test.log(LogStatus.FAIL, "Platform Latency Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Platform Latency Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Platform Latency Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_PlatformLatency"), "Platform Latency Distribution", "drillDownPlatformLatencyDisWin_AllTableHeaders");

		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Platform Latency Distribution Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillDownPlatformLatencyDisWin_AllTableHeaders"
																							, "drillDownPlatformLatencyDisWin_AllTableDataRows"
																								, "drillDownPlatformLatencyDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-PlatformLatencyData*.csv", "Platform Latency Distribution Page", 9, "drillDownPlatformLatencyDisWin_ExportButton", "drillDownPlatformLatencyDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-PlatformLatencyData*.csv", "Platform Latency Distribution Page", 9, "drillDownPlatformLatencyDisWin_ExportButton", "drillDownPlatformLatencyDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Platform Latency Page )");		
		cu.checkElementPresence("drillDownPlatformLatencyDisWin_ExportButton");
		cu.checkElementPresence("drillDownPlatformLatencyDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillDownPlatformLatencyDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillDownPlatformLatencyDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillDownPlatformLatencyDisWin_SupplierAccountNameTextBox");	
		cu.checkReadonlyProperty("drillDownPlatformLatencyDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillDownPlatformLatencyDisWin_DestinationTextBox");		
				
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillDownPlatformLatencyDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillDownPlatformLatencyDisWin_CustomerAccountNameTextBox", "value");		
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");

		//percentage validations
		List<Map<String, String>> platformLatencyTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);
		if(platformLatencyTableRowMapList.size()>0)
		{		
			//0-250 Msecs % Validation
			double expectedPercentage0To250MsecPer = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("0-250 Msecs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage0To250MsecPer = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("0-250 Msecs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage0To250MsecPer, actualPercentage0To250MsecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (0-250 Msecs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To250MsecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-250 Msecs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage0To250MsecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (0-250 Msecs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To250MsecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-250 Msecs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage0To250MsecPer+" (+/- 0.01 offset)</span>");
			}

			//250-500 Msecs % Validation
			double expectedPercentage250To500Msecs = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("250-500 Msecs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage250To500Msecs = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("250-500 Msecs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage250To500Msecs, actualPercentage250To500Msecs))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (250-500 Msecs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage250To500Msecs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (250-500 Msecs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage250To500Msecs+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (250-500 Msecs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage250To500Msecs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (250-500 Msecs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage250To500Msecs+" (+/- 0.01 offset)</span>");
			}
			
			//500 Msecs-1 Secs % Validation
			double expectedPercentage500MsecsTo1SecsPer = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("500 Msecs-1 Secs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage500MsecsTo1SecsPer = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("500 Msecs-1 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage500MsecsTo1SecsPer, actualPercentage500MsecsTo1SecsPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (500 Msecs-1 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage500MsecsTo1SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (500 Msecs-1 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage500MsecsTo1SecsPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (500 Msecs-1 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage500MsecsTo1SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (500 Msecs-1 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage500MsecsTo1SecsPer+" (+/- 0.01 offset)</span>");
			}
			
			//1-3 Secs % Validation
			double expectedPercentage1To3SecsPer = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("1-3 Secs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage1To3SecsPer = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("1-3 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage1To3SecsPer, actualPercentage1To3SecsPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (1-3 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage1To3SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (1-3 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage1To3SecsPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (1-3 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage1To3SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (1-3 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage1To3SecsPer+" (+/- 0.01 offset)</span>");
			}
			
			//3-5 Secs % Validation
			double expectedPercentage3To5SecsPer = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("3-5 Secs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage3To5SecsPer = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("3-5 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage3To5SecsPer, actualPercentage3To5SecsPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (3-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To5SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-5 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage3To5SecsPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (3-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To5SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-5 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage3To5SecsPer+" (+/- 0.01 offset)</span>");
			}
			
			//5-10 Secs % Validation
			double expectedPercentage5To10Secs = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get("5-10 Secs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage5To10Secs = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get("5-10 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage5To10Secs, actualPercentage5To10Secs))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage5To10Secs+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage5To10Secs+" (+/- 0.01 offset)</span>");
			}
			
			//> 10 Secs % validation
			double expectedPercentageGreaterThan10SecsPer = roundDobule(calculatePercentage(platformLatencyTableRowMapList.get(0).get(">10 Secs"), platformLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentageGreaterThan10SecsPer = roundDobule(Double.valueOf(platformLatencyTableRowMapList.get(0).get(">10 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentageGreaterThan10SecsPer, actualPercentageGreaterThan10SecsPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (> 10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan10SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 10 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentageGreaterThan10SecsPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (> 10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGreaterThan10SecsPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 10 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentageGreaterThan10SecsPer+" (+/- 0.01 offset)</span>");
			}
		}

		
		cu.clickElement("drillDownPlatformLatencyDisWin_BackButton");
	}
	
	private void secondDrillDownDeliveryLatencyValidation(Map<String, String> currMap_1stDrill) {
		
		cu.clickElementAfterScrollToView("drillDownSuppAccWin_Table_Dynamic_Row_DeliveryLatencyColoum_Link"
							, "$Country$~$Destination$~$MCC$~$MNC$~$CustomerName$~$CustomerAccountName$~$DeliveryLatencyValue$"
								, currMap_1stDrill.get("Country")+"~"+currMap_1stDrill.get("Destination")+"~"+currMap_1stDrill.get("MCC")
									+"~"+currMap_1stDrill.get("MNC")+"~"+currMap_1stDrill.get("Customer")+"~"+currMap_1stDrill.get("Customer Account")
										+"~"+currMap_1stDrill.get("Delivery Latency (s)"));
				
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
		
		if(!cu.elementDisplayed("drillDownDeliveryLatencyDisWin"))
		{
			cu.getScreenShot("Delivery Latency Window Not Opened");
			test.log(LogStatus.FAIL, "Delivery Latency Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
			Assert.fail("Delivery Latency Window for Supplier account: "+currMap_1stDrill.get("SupplierAccNameDrillTxtBoxVal")+"  not opened");
		}


		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 2nd drilldown ( Delivery Latency Distribution Page )");		
		validateTableHeaders(dataMap.get("SecondDrilldownHeaders_DeliveryLatency"), "Delivery Latency Distribution", "drillDownDeliveryLatencyDisWin_AllTableHeaders");

		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 2nd drilldown ( Delivery Latency Distribution Page )");		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_2ndDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																						, "drillDownDeliveryLatencyDisWin_AllTableHeaders"
																							, "drillDownDeliveryLatencyDisWin_AllTableDataRows"
																								, "drillDownDeliveryLatencyDisWin_Table_Dynamic_Row_AllColoumn");
		
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-DeliveryLatencyData*.csv", "Delivery Latency Distribution Page", 9, "drillDownDeliveryLatencyDisWin_ExportButton", "drillDownDeliveryLatencyDisWin_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill, "MMX-DeliveryLatencyData*.csv", "Delivery Latency Distribution Page", 9, "drillDownDeliveryLatencyDisWin_ExportButton", "drillDownDeliveryLatencyDisWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 2nd drilldown ( Delivery Latency Page )");		
		cu.checkElementPresence("drillDownDeliveryLatencyDisWin_ExportButton");
		cu.checkElementPresence("drillDownDeliveryLatencyDisWin_BackButton");
		cu.checkElementPresence("drillDownDeliveryLatencyDisWin_BackButton");
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))			
			cu.checkElementNotPresence("drillDownDeliveryLatencyDisWin_CustomerAccountNameTextBox");	
		else
			cu.checkReadonlyProperty("drillDownDeliveryLatencyDisWin_CustomerAccountNameTextBox");
		cu.checkReadonlyProperty("drillDownDeliveryLatencyDisWin_CountryTextBox");
		cu.checkReadonlyProperty("drillDownDeliveryLatencyDisWin_DestinationTextBox");		
				
		String cusAcctName = null;
		String suppAccName =cu.getAttribute("drillDownDeliveryLatencyDisWin_SupplierAccountNameTextBox", "value");
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()))
			cusAcctName = cu.getAttribute("drillDownDeliveryLatencyDisWin_CustomerAccountNameTextBox", "value");		
		
		
		String customerSuppHierarchyString = "Supplier Account: "+suppAccName+(cusAcctName!=null?"-> Customer Account: "+cusAcctName:"");

		//percentage validations
		List<Map<String, String>> deliveryLatencyTableRowMapList = convertListTableDataToListOfMapData(uiTableDataAlongWithHeadersWithoutMapping_2ndDrill);
		if(deliveryLatencyTableRowMapList.size()>0)
		{		
			//0-5 Secs Validation
			double expectedPercentage0To5SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("0-5 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage0To5SecPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("0-5 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage0To5SecPer, actualPercentage0To5SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To5SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage0To5SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage0To5SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (0-5 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage0To5SecPer+" (+/- 0.01 offset)</span>");
			}

			//5-10 Secs % Validation
			double expectedPercentage5To10SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("5-10 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage5To10SecPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("5-10 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage5To10SecPer, actualPercentage5To10SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage5To10SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage5To10SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (5-10 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage5To10SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//10-15 Secs % Validation
			double expectedPercentage10To15SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("10-15 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage10To15SecPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("10-15 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage10To15SecPer, actualPercentage10To15SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To15SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage10To15SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To15SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-15 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage10To15SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//15-30 Secs % Validation
			double expectedPercentage15To30SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("15-30 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage15To30SecPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("15-30 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage15To30SecPer, actualPercentage15To30SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage15To30SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage15To30SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage15To30SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (15-30 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage15To30SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//30-60 Secs % Validation
			double expectedPercentage30To60SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("30-60 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage30To60SecPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("30-60 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage30To60SecPer, actualPercentage30To60SecPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To60SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage30To60SecPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To60SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-60 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage30To60SecPer+" (+/- 0.01 offset)</span>");
			}
			
			//60-120 Secs % Validation
			double expectedPercentage60To120Secs = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("60-120 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage60To120Secs = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("60-120 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage60To120Secs, actualPercentage60To120Secs))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage60To120Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage60To120Secs+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage60To120Secs+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (60-120 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage60To120Secs+" (+/- 0.01 offset)</span>");
			}
			
			//120-180 Secs % validation
			double expectedPercentage120To180SecPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("120-180 Secs"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage120To180SePer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("120-180 Secs %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage120To180SecPer, actualPercentage120To180SePer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage120To180SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage120To180SePer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage120To180SecPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (120-180 Secs %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage120To180SePer+" (+/- 0.01 offset)</span>");
			}
			
			//3-10 Min % Validation
			double expectedPercentage3To10MinPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("3-10 Min"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage3To10MinPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("3-10 Min %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage3To10MinPer, actualPercentage3To10MinPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage3To10MinPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage3To10MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (3-10 Min %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage3To10MinPer+" (+/- 0.01 offset)</span>");
			}
			
			//10-30 Min % Validation
			double expectedPercentage10To30MinPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("10-30 Min"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage10To30MinPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("10-30 Min %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage10To30MinPer, actualPercentage10To30MinPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (10-30 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To30MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-30 Min %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage10To30MinPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (10-30 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage10To30MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (10-30 Min %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage10To30MinPer+" (+/- 0.01 offset)</span>");
			}	
			
			
			//30-360 Min % Validation
			double expectedPercentage30To360MinPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("30-360 Min"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage30To360MinPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("30-360 Min %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage30To360MinPer, actualPercentage30To360MinPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (30-360 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To360MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-360 Min %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage30To360MinPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (30-360 Min %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage30To360MinPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (30-360 Min %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage30To360MinPer+" (+/- 0.01 offset)</span>");
			}	
			
			//6-24 Hours % Validation
			double expectedPercentage6To24HoursPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get("6-24 Hours"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentage6To24HoursPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get("6-24 Hours %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentage6To24HoursPer, actualPercentage6To24HoursPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (6-24 Hours %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage6To24HoursPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (6-24 Hours %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentage6To24HoursPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (6-24 Hours %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentage6To24HoursPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (6-24 Hours %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentage6To24HoursPer+" (+/- 0.01 offset)</span>");
			}	
			
			
			//> 24 Hours % Validation
			double expectedPercentageGeaterThan24HoursPer = roundDobule(calculatePercentage(deliveryLatencyTableRowMapList.get(0).get(">24 Hours"), deliveryLatencyTableRowMapList.get(0).get("Total Delivered")));
			double actualPercentageGeaterThan24HoursPer = roundDobule(Double.valueOf(deliveryLatencyTableRowMapList.get(0).get(">24 Hours %").replace("%", "")));
			if(compareDoubleWithTwoDigit(expectedPercentageGeaterThan24HoursPer, actualPercentageGeaterThan24HoursPer))
			{			
				test.log(LogStatus.PASS,
						"EXPECTED: Percentage (> 24 Hours %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGeaterThan24HoursPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 24 Hours %) for "+ customerSuppHierarchyString +"  is same as "+actualPercentageGeaterThan24HoursPer+" (+/- 0.01 offset)</span>");
			}
			else
			{				
				test.log(LogStatus.FAIL,
						"EXPECTED: Percentage (> 24 Hours %) for "+ customerSuppHierarchyString +"  should be "+expectedPercentageGeaterThan24HoursPer+" (+/- 0.01 offset)",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage (> 24 Hours %) for "+ customerSuppHierarchyString +"  is not same as "+actualPercentageGeaterThan24HoursPer+" (+/- 0.01 offset)</span>");
			}	
		}
		
		cu.clickElement("drillDownDeliveryLatencyDisWin_BackButton");
	}
	
	 List<List<String>> getUITableDataAlongWithHeadersWithoutMapping(List<List<String>>  retRowLst
			 , String locatorFieldNameForAllTableHeaders
			 	, String locatorFieldNameForAllTableDataRows
			 		, String locatorFieldNameForDynamicRowAllColoumn)
	 {
		 
		int tableRowSize = cu.ElementsToList(locatorFieldNameForAllTableDataRows).size();
		List<String> allHeaderNames = null;
		if(retRowLst.size()==0)
		{
			allHeaderNames = modifyHeaders(cu.ElementsToListWithTrim(locatorFieldNameForAllTableHeaders));			
			retRowLst.add(allHeaderNames);
		}
		else
			allHeaderNames = retRowLst.get(0);
		
		for(int i=1;i<tableRowSize+1;i++)
		{			 
			 List<String> curRowColoumTxts = cu.ElementsToListWithTrim(locatorFieldNameForDynamicRowAllColoumn, "$index$", i+"");
			 curRowColoumTxts = modifyTheDataRows(allHeaderNames, curRowColoumTxts);	
			 if(!retRowLst.contains(curRowColoumTxts))
				 retRowLst.add(curRowColoumTxts);				 
		}	
		
		cu.scrollPageToViewElement(locatorFieldNameForDynamicRowAllColoumn, "$index$", tableRowSize+"");
		cu.sleep(300);
		List<String> lastRowColoumTxts = cu.ElementsToListWithTrim(locatorFieldNameForDynamicRowAllColoumn, "$index$", tableRowSize+"");
		lastRowColoumTxts = modifyTheDataRows(allHeaderNames, lastRowColoumTxts);
		if(!lastRowColoumTxts.equals(retRowLst.get(retRowLst.size()-1)))			
			getUITableDataAlongWithHeadersWithoutMapping(retRowLst, locatorFieldNameForAllTableHeaders, locatorFieldNameForAllTableDataRows, locatorFieldNameForDynamicRowAllColoumn);
		
		return retRowLst;
	 }
	 
	 
	 
	 
	 List<Map<String, String>> getUITableDataAlongWithHeadersWithoutMappingFromDataStores(List<List<String>>  retRowLst)
	 {
		 
		 String jsStr1 = "var scriptElt = document.createElement('script');"
					+ "scriptElt.type = 'text/javascript';"
					+ "scriptElt.innerHTML='function getTableStoredata(locator){var arr = [];"
												+ "var storeItemsVar = Ext.ComponentQuery.query(locator)[0].store.data.items;"
												+ "for(var i=0; i<storeItemsVar.length; i++){arr.push(storeItemsVar[i].data);}"
												+ "return arr;}';"
			+ "document.getElementsByTagName('head')[0].appendChild(scriptElt);";
			
			JavascriptExecutor jse2 = (JavascriptExecutor)driver;
			jse2.executeScript(jsStr1, driver.findElement(By.id("gridview-1054")));
			
			
			List<Map<String, String>> ret = (List<Map<String, String>>) jse2.executeScript("return getTableStoredata(\"#gridview-1054\")");
			System.out.println(ret);
			
			return ret;
	 }
	 
	 
	 List<Map<String, String>> getHeaderDataStoreMapping(List<Map<String, String>> rawDataStoreKeyValueList, List<String> uiHeaderNames)
	 {
		 Map<String, String> headerNamesStoreUIMap = getHeaderNamesMapOfStoreAndUI();
		 List<Map<String, String>> retLst = new LinkedList<>();
		 for(Map<String, String> rawDataStoreKeyValue : rawDataStoreKeyValueList)
		 {
			 Map<String, String> modifiedMap = new LinkedHashMap<>();
			 for(String uiHeaderName : uiHeaderNames)			 
				 modifiedMap.put(uiHeaderName, rawDataStoreKeyValue.get(headerNamesStoreUIMap.get(uiHeaderName)));
			 retLst.add(modifiedMap);
		 }
		 
		 return retLst;
	 }
	 
	 Map<String, String> getHeaderNamesMapOfStoreAndUI()
	 {
		 Map<String, String> modifiedMap = new LinkedHashMap<>();
		 modifiedMap.put("Customer *", "customerName");
		 modifiedMap.put("Customer Account *", "customerAccount");
		 modifiedMap.put("Attempted Success", "attemptSuccessCount");
		 modifiedMap.put("Attempted Failure", "attemptFailedcount");
		 modifiedMap.put("Submitted Success", "submitSuccessCount");
		 modifiedMap.put("Submitted Failure", "submitFailedcount");
		 modifiedMap.put("Submitted %", "submitPercent");	
		 modifiedMap.put("Enroute", "enroute");
		 modifiedMap.put("Delivered Success", "deliverSuccessCount");
		 modifiedMap.put("Delivered Failure", "deliverFailedCount");
		 modifiedMap.put("Delivered %", "deliveredPercent");
		 modifiedMap.put("E2E Latency (s)", "e2eLatency");		 
		 modifiedMap.put("Ack Latency (ms)", "ackLatency");
		 modifiedMap.put("Platform Latency (ms)", "platformLatency");		 
		 modifiedMap.put("Delivery Latency (s)", "deliveryLatency");
		 
		 modifiedMap.put("Country *", "country");
		 modifiedMap.put("Destination *", "destination");
		 modifiedMap.put("MCC *", "mcc");
		 modifiedMap.put("MNC *", "mnc");
		 modifiedMap.put("Supplier *", "supplier");
		 modifiedMap.put("Supplier Account *", "supplierAccount");
		 modifiedMap.put("Submitted Success %", "submittedSuccessPercent");
		 modifiedMap.put("Submitted Failure %", "submittedFailurePercent");
		 modifiedMap.put("Delivered Success %", "deliveredSuccessPercent");
		 modifiedMap.put("Delivered Failure %", "deliveredFailurePercent");	
		 
		 return modifiedMap;
	 }
	 
	 
	 
	 List<String> modifyTheDataRows(List<String> allHeaderNames, List<String> rowDataColoumn)
	 {
		 //Remove comma from number
		 String[] headersNeedChanges1 = new String[]{"Attempted","Attempted Failure","Submitted", "Submit Failure", "Enroute", "Delivered", "Failed"
					, "Total Delivered #", "0-15s #", "15-45s #", "45-90s #", "90-180s #", ">180s #"
						, "Attempted Success", "Submitted Success", "Delivered Success", "Delivered Failure"
							, "E2E Latency (s)","Ack Latency (ms)", "Platform Latency (ms)", "Delivery Latency (s)", "Submitted Failure"
							,"Total Delivered", "0-100 ms", "100-200 ms", "200-300 ms", "300-400 ms" , "400-500 ms", "500 ms - 1 sec", "1 sec - 3 sec", "> 3 sec"
							, "0-5 Secs", "5-10 Secs", "10-15 Secs", "15-30 Secs", "30-60 Secs", "60-120 Secs", "120-180 Secs", "3-10 Min", ">10 Min"
							, "Total Delivered #", "0-250 Msecs", "250-500 Msecs", "500 Msecs-1 Secs", "1-3 Secs", "3-5 Secs", "3-5 Secs", "5-10 Secs", "> 10 Secs"
							, "10-30 Min", "30-360 Min", "6-24 Hours", "> 24 Hours" };
		 					
		 headersNeedChanges1 =  new LinkedHashSet<String>(Arrays.asList(headersNeedChanges1)).toArray(headersNeedChanges1);
		 
		 for(String header : headersNeedChanges1)				
			 if(allHeaderNames.contains(header))			 
				 rowDataColoumn.set(allHeaderNames.indexOf(header), rowDataColoumn.get(allHeaderNames.indexOf(header)).replace(",", ""));
		 
		 return rowDataColoumn;
	 }
		
	 List<Map<String, String>> getUITableData()
	 {
		List<String> allHeaderNames = modifyHeaders(cu.ElementsToListWithTrim("allMainTableHeaders"));
		int tableRowSize = cu.ElementsToList("allTableDataRows").size();
		List<Map<String, String>> retRowLst = new LinkedList<>();
		for(int i=1;i<tableRowSize+1;i++)
		{
			 Map<String, String> curRowMap= new LinkedHashMap<>();
			 List<String> curRowColoumTxts = cu.ElementsToListWithTrim("table_Dynamic_Row_AllColoumn", "$index$", i+"");
			 for(int k=0;k<allHeaderNames.size();k++)
			 {
				 curRowMap.put(allHeaderNames.get(k), curRowColoumTxts.get(k));
			 }				 
			 retRowLst.add(curRowMap);
		}
		
		return retRowLst;
	 }
	 
	 void exportCSVAndValidateWithUI(List<List<String>> uiDataMapRows, String expectedFileNamePattern, String tableName , int headerRowNumInCSV,
			 String locatorFieldNameForExportButton, String locatorFieldNameForExportAllRecords)  
	  {		 	
		  	cu.deleteAllFilesInDownloadFolder();
		  	cu.clickElement(locatorFieldNameForExportButton);
		  	cu.sleep(200);
		  	cu.clickElement(locatorFieldNameForExportAllRecords);
			cu.waitForPageLoadWithSleep("", 500);
			cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 300);
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			
			//validate file name
			if(matchWildcard(FilenameUtils.getName(csvFilePath),expectedFileNamePattern.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in '"+expectedFileNamePattern+"' pattern ( for page '"+tableName+"')", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+expectedFileNamePattern+"' pattern. FileName: '"+csvFilePath+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation failed");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in '"+expectedFileNamePattern+"' pattern ( for page '"+tableName+"')", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as as '"+expectedFileNamePattern+"' pattern. Acutal FileName: '"+csvFilePath+"'</span>");
			}
						
			CSVUtil csvu = new CSVUtil(csvFilePath, headerRowNumInCSV);
			List<List<String>> csvRawRowLines = csvu.getAllRowLinesFromHeader();
									
			test.log(LogStatus.INFO, "CSV complete data ( for page '"+tableName+"')" , "<span  style=\"background-color:#f9c53a;    padding: 2px;     font-weight: bold;\">"
					+"CSV complete data ( for page '"+tableName+"') : <a style=\"cursor: pointer;\" onclick=\"$(this).parent().next().slideToggle(50, function () {});\">[Click here to expand/collapse]</a></span> "
						+"<div>"+javaRowsToHtmlTable(csvRawRowLines)+"</div>");
			
			if((csvRawRowLines.size()-1) == (uiDataMapRows.size()-1))			
				test.log(LogStatus.PASS, "EXPECTECD: Records size in UI and CSV should matched ( for page '"+tableName+"')"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have matched. (Rows Size: "+(csvRawRowLines.size()-1)+")</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Records size in UI and CSV should matched ( for page '"+tableName+"')"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have not matched. ( UI_Rows_Size: '"+(uiDataMapRows.size()-1)
						+"'. CSV_Rows_Size: '"+(csvRawRowLines.size()-1)+"' )</span>");
			
			if(csvRawRowLines.size()>0 && uiDataMapRows.size()>0)
			{
				List<String> csvHeaders = modifyHeaders(trimListOfString(csvRawRowLines.get(0)));
				List<String> uiHeaders = modifyHeaders(trimListOfString(uiDataMapRows.get(0)));
				
				//Compare Headers
				if(csvHeaders.equals(uiHeaders))				
					test.log(LogStatus.PASS, "EXPECTECD: UI and CSV Header Rows should be matched with CSV ( for page '"+tableName+"')"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI and CSV Header Rows matched. <br/>HeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiHeaders.toString())+"</span>");
				else
					test.log(LogStatus.FAIL, "EXPECTECD: UI and CSV Header Rows should be matched with CSV ( for page '"+tableName+"')"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI and CSV Header Rows not matched. <br/>UIHeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiHeaders.toString())
																															+"<br/>csvHeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(csvHeaders.toString())+"</span>");
								
				int uiRowPointer = 1;
				for(int i=1;i<uiDataMapRows.size();i++)
				{
					List<String> uiRow = uiDataMapRows.get(i);
					boolean currRowmatched = false;
					for(int j=1;j<csvRawRowLines.size();j++)
					{
						if(uiRow.equals(csvRawRowLines.get(j)))
						{
							currRowmatched = true;
							break;
						}
					}
					
					if(currRowmatched)
						test.log(LogStatus.PASS, "EXPECTECD: UI Row '"+uiRowPointer+"' should be matched with CSV ( for page '"+tableName+"')"
								, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI Row '"+uiRowPointer+"'  matched with CSV. (UIData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiRow.toString())+")</span>");
					else
						test.log(LogStatus.FAIL, "EXPECTECD: UI Row '"+uiRowPointer+"' should be matched with CSV ( for page '"+tableName+"')"
								, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI Row '"+uiRowPointer+"' not matched with CSV. (UIData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiRow.toString())+")<br/><br/>CSVData: <br/><br/>"+StringEscapeUtils.escapeHtml3(csvRawRowLines.get(i).toString())+"</span>");
					
					uiRowPointer++;
				}
			}
					
	  }
	
	public static boolean matchWildcard(String text, String pattern)
	{
	  return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
	}
	
	public static String javaRowsToHtmlTable(List<List<String>> rows)
	{
		String ret = "";
		if(rows.size()>1)
		{
				List<String> headers = rows.get(0);
				if(headers.size() >0)
				{
					ret = ret+"<table><thead><tr>";
					for(String header :headers)					
						ret = ret+"<th style=\"white-space:nowrap;max-width:200px;\">"+StringEscapeUtils.escapeHtml3(header)+"</th>";
					ret = ret+"</tr></thead>";
								
					for(int k=1;k<rows.size();k++)
					{
						if(k==1)
							ret = ret+"<tbody>";
						ret = ret+"<tr>";
						for(String dataCol : rows.get(k))
							ret = ret+"<td>"+StringEscapeUtils.escapeHtml3(dataCol)+"</td>";
						ret = ret+"</tr>";
						if(k==rows.size()-1)
							ret = ret+"</tbody></table>";
					}				
				}
		}
		
		return ret;
	}
	
	private List<String> getColoumnListFromRawTableData(List<List<String>> uiTableDataAlongWithHeadersWithoutMapping, String coloumName) 
	{
		List<String> retLst = new LinkedList<>();
		if(uiTableDataAlongWithHeadersWithoutMapping.size()>1)
		{
			List<String> headerRow = uiTableDataAlongWithHeadersWithoutMapping.get(0);
			
			int coloumnIndex = -1; 
			try{
				coloumnIndex = headerRow.indexOf(coloumName);
			}catch (Exception e) {				
				test.log(LogStatus.FAIL, "Error occured while getting index of coloumn '"+coloumName+"' from table header.");
			}
			
			for(int i=1;(i<uiTableDataAlongWithHeadersWithoutMapping.size() && coloumnIndex!=-1);i++)
			{
					List<String> currRow = uiTableDataAlongWithHeadersWithoutMapping.get(i);
					retLst.add( currRow.get(coloumnIndex));					
			}
		}
		
		
		return retLst;
	
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
	
	
	Map<String, Map<String, String>> convertListTableDataToMapOfMapData(List<List<String>> uiTableDataAlongWithHeadersWithoutMapping, String keyColoumnName)
	{
		Map<String, Map<String, String>> retMap = new LinkedHashMap<>();
		
		if(uiTableDataAlongWithHeadersWithoutMapping.size()>1)
		{
			int keyColoumnIndex = -1; 
			try{
				keyColoumnIndex = uiTableDataAlongWithHeadersWithoutMapping.get(0).indexOf(keyColoumnName);
			}catch (Exception e) {				
				test.log(LogStatus.FAIL, "Error occured while getting index of coloumn '"+keyColoumnName+"' from table header.");
			}
		
		
		for(int i=1;(i<uiTableDataAlongWithHeadersWithoutMapping.size() && keyColoumnIndex!=-1);i++)
			retMap.put(uiTableDataAlongWithHeadersWithoutMapping.get(i).get(keyColoumnIndex), combineHeaderAndDataRowsToMap(uiTableDataAlongWithHeadersWithoutMapping.get(0), uiTableDataAlongWithHeadersWithoutMapping.get(i)));
		
		}
		
		return retMap;
	}
	
	
	Map<String, String> combineHeaderAndDataRowsToMap(List<String> headerRow, List<String> dataRow)
	{
		Map<String, String> rowMap = new LinkedHashMap<>();		
		for(int i=0;i<headerRow.size();i++)		
			rowMap.put(headerRow.get(i), dataRow.get(i));
		
		return rowMap;		
	}
	
	
	List<String> modifyHeaders(List<String> rawHeaders)
	{
		Map<String, String> modifyHeadersKeyValues = new LinkedHashMap<>();
		//modifyHeadersKeyValues.put("Customer Account *", "Customer Account");
		//modifyHeadersKeyValues.put("Customer *", "Customer");
		modifyHeadersKeyValues.put("E2E Latency(s)", "E2E Latency (s)");
		modifyHeadersKeyValues.put("Ack Latency(ms)", "Ack Latency (ms)");
		modifyHeadersKeyValues.put("Platform Latency(ms)", "Platform Latency (ms)");
		modifyHeadersKeyValues.put("Delivery Latency(s)", "Delivery Latency (s)");
		
		modifyHeadersKeyValues.put("500 ms-1 sec", "500 ms - 1 sec");
		modifyHeadersKeyValues.put("500 ms-1 sec %", "500 ms - 1 sec %");
		modifyHeadersKeyValues.put("1 sec-3 sec", "1 sec - 3 sec");
		modifyHeadersKeyValues.put("1 sec-3 sec %", "1 sec - 3 sec %");
		modifyHeadersKeyValues.put(">3 sec", "> 3 sec");
		modifyHeadersKeyValues.put(">3 sec %", "> 3 sec %");
		
		modifyHeadersKeyValues.put("Total Delivered #", "Total Delivered");
				
		
		List<String> returnLst = new LinkedList<>();
		for(String rawHeader : rawHeaders)
		{
			if(modifyHeadersKeyValues.keySet().contains(rawHeader))			
				returnLst.add(modifyHeadersKeyValues.get(rawHeader));			
			else
			{
				// It will remove the * at the end of header
				if(rawHeader.endsWith(" *"))
					returnLst.add(rawHeader.substring(0, rawHeader.lastIndexOf(" *")));
				else
					returnLst.add(rawHeader);
			}
		}
		
		return returnLst;
	}
	
	
private double calculatePercentage(String numerator, String denominator ){
		
		if(numerator==null)
			numerator = "0";
		if(denominator==null)
			denominator = "0";
		
		numerator = numerator.replace(",", "").replace("%", "").trim();
		denominator = denominator.replace(",", "").replace("%", "").trim();
		double result=0.0;
		
		if(Double.valueOf(denominator) > 0){
			result = (Double.valueOf(numerator) / Double.valueOf(denominator))*100;
		}
		
		return result;
	}

private double calculatePercentage(double numerator, double denominator ){
		
		double result=0.0;
		
		if(denominator > 0){
			result = ((double)numerator / denominator)*100;
		}
		
		return result;
	}

private double calculateLatency(double numerator, double denominator ){
	
	double result=0;
	
	if(denominator > 0){
		result = (numerator / denominator);
	}
	
	return result;
}


private double roundDobule(double input)
{
	DecimalFormat df = new DecimalFormat("#.##");
	df.setRoundingMode(RoundingMode.CEILING);
	return Double.valueOf(df.format(input));
}


private boolean compareDoubleWithTwoDigit(double input1, double input2)
{
//	input1 = roundDobule(input1);
//	input2 = roundDobule(input2);
		
	if(Double.compare(input1, input2)==0 
			|| Double.compare((input1+0.01), input2)==0 
				|| Double.compare((input1-0.01), input2)==0)
		return true;
	else
		return true;
}

void expandMainFilter()
{
	if("false".equals(cu.getAttribute("DeliveryStat_MainFilter_ExpandedStatus", "aria-expanded")))
	{
		cu.clickElement("DeliveryStat_MainFilter_ExpandCollapseToggleDiv");
	}
	
	cu.getScreenShot("MainFilter Expanded");
}

void collapseMainFilter()
{
	String status = cu.getAttribute("DeliveryStat_MainFilter_ExpandedStatus", "aria-expanded");
	if(status==null || "true".equals(status))
	{
		cu.clickElement("DeliveryStat_MainFilter_ExpandCollapseToggleDiv");
	}
	
	cu.getScreenShot("MainFilter Collapsed");
}

int getSummationOfColoumnFromTableData(List<List<String>> uiTableDataAlongWithHeadersWithoutMapping, String coloumnName)
{
	int ret = 0;
	
	if(uiTableDataAlongWithHeadersWithoutMapping.size()>1)
	{
		int keyColoumnIndex = -1; 
		try{
			keyColoumnIndex = uiTableDataAlongWithHeadersWithoutMapping.get(0).indexOf(coloumnName);
		}catch (Exception e) {				
			test.log(LogStatus.FAIL, "Error occured while getting index of coloumn '"+coloumnName+"' from table header.");
		}
		
		
	
		for(int i=1;(i<uiTableDataAlongWithHeadersWithoutMapping.size() && keyColoumnIndex!=-1);i++)
		{
			int temp = Integer.parseInt(uiTableDataAlongWithHeadersWithoutMapping.get(i).get(keyColoumnIndex).replace(",", "").replace("%", "").trim());
			ret = ret+temp;
		}
	
	}
	return ret;
	
}

List<String> trimListOfString(List<String> rawLst)
{
	List<String> ret = new LinkedList<>();
	
	for(String rEle : rawLst)
		ret.add(rEle.trim());
	
	return ret;
}


}
