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

public class TC_06_SelectDestination implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_06_SelectDestination.class.getName());
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
				.startTest("Execution triggered for - "+TC_06_SelectDestination.class.getName()+" -with TestdataId: " + uniqueDataId);
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
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 180);
		//cu.checkElementPresent("DeliveryStat_SubmitButton");
		selectMainFilter();
		cu.clickElement("DeliveryStat_SubmitButton");
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 180);
				
		collapseMainFilter();
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in Destination Dimension Main Page");		
		validateTableHeaders(dataMap.get("MainTableHeaders"), "Destination Dimension Main Page", "allMainTableHeaders");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in Destination Dimension Main Page");
		
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
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_Main, "MMX-DeliveryStatisticsData*.csv", "Destination Dimension Main Page", 17, "DeliveryStat_ExportButton", "DeliveryStat_ExportAllRecords");
		else
			exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_Main, "MMX-DeliveryStatisticsData*.csv", "Destination Dimension Main Page", 17, "DeliveryStat_ExportButton", "DeliveryStat_ExportAllRecords");
						
		List<String> destinationsValues = getColoumnListFromRawTableData(uiTableDataAlongWithHeadersWithoutMapping_Main, "Destination");

		for (String destinationVal : destinationsValues) 
		{
			mainPageAndSubsequenceDrillDownValidation(destinationVal, uiTableDataAlongWithHeadersWithoutMapping_Main);
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

		Map<String, Map<String, String>> tableMap = convertListTableDataToMapOfMapData(uiTableDataAlongWithHeadersWithoutMapping_Main, "Destination");

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
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 180);
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
	
	
	void mainPageAndSubsequenceDrillDownValidation(String destinationVal, List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_Main)
	{
		test.log(LogStatus.INFO, "$$$$$$$$$$$ Validating for Destination: "+destinationVal);
		Map<String, Double> uiDataMainPage = getUIValesMainPage(destinationVal, uiTableDataAlongWithHeadersWithoutMapping_Main);
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
					"EXPECTED: Submitted Percentage (main page)for Destination: "+destinationVal+" should be "+roundDobule(submittedPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Percentage (main page)for Destination: "+destinationVal+" is same as  "+submittedPercentageActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Submitted Percentage (main page)for Destination: "+destinationVal+" should be "+roundDobule(submittedPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Destination: "+destinationVal+" is not same as "+submittedPercentageActual+"</span>");
		}
		
		//main page Delivered Percentage validation
		if(compareDoubleWithTwoDigit(roundDobule(deliveredPercentageExpected), deliveredPercentageActual))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Delivered Percentage (main page)for Destination: "+destinationVal+" should be "+roundDobule(deliveredPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Destination: "+destinationVal+" is same as  "+deliveredPercentageActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Delivered Percentage (main page)for Destination: "+destinationVal+" should be "+roundDobule(deliveredPercentageExpected)+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Destination: "+destinationVal+" is not same as "+deliveredPercentageActual+"</span>");
		}
		
		
		//main page Enroute validation
		if(compareDoubleWithTwoDigit(roundDobule(enrouteExpected), enrouteActual))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Enroute (main page)for Destination: "+destinationVal+" should be "+enrouteExpected+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Enroute (main page)for Destination: "+destinationVal+" is same as  "+enrouteActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Enroute (main page)for Destination: "+destinationVal+" should be "+enrouteExpected+" (+/- 0.01)",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Enroute (main page)for Destination: "+destinationVal+" is not same as "+enrouteActual+"</span>");
		}
		
		firstAndSubsequenceDrillDownValidation(destinationVal, uiDataMainPage);
	}
 
	
	
	void firstAndSubsequenceDrillDownValidation(String destinationVal, Map<String, Double> uiDataMainPage)
	{
		//Entering Customer Account Distribution Page ( 1st drilldown		
//		cu.clickElement("dynamicCustAccMainTableLink", "$customerAccName$", cusAccVal);
		cu.clickElementAfterScrollToView("dynamicDestinationMainTableLink", "$destinationName$", destinationVal);
		cu.waitForPageLoadWithSleep("", 500);
		cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 180);
		test.log(LogStatus.INFO, "######### Validating 1st drilldown ( Destination Distribution Page ) for Customer account: "+destinationVal);
		
		if(!cu.elementDisplayed("drillDownDestinationWin"))
		{
			cu.getScreenShot("Destination Distribution Window Not Opened");
			test.log(LogStatus.FAIL, "Destination Distribution Window for Destination: "+destinationVal+"  not opened");
			Assert.fail("Destination Distribution Window for Destination: "+destinationVal+"  not opened");
		}
		
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 1st drilldown ( Destination Distribution Page ) for Destination: "+destinationVal);		
		validateTableHeaders(dataMap.get("FisrtDrilldownHeaders"), "Destination Distribution Page", "drillDownDestinationWin_AllTableHeaders");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 1st drilldown ( Destination Distribution Page ) for Destination: "+destinationVal);	
		
		
		List<List<String>> uiTableDataAlongWithHeadersWithoutMapping_1stDrill = getUITableDataAlongWithHeadersWithoutMapping(new LinkedList<>()
																					, "drillDownDestinationWin_AllTableHeaders"
																						, "drillDownDestinationWin_AllTableDataRows"
																							, "drillDownDestinationWin_Table_Dynamic_Row_AllColoumn");
		
		cu.executeJavaScrpit("drillDownDestinationWin_TableBody", "arguments[0].scrollTop = 0;");
		exportCSVAndValidateWithUI(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "MMX-DestinationData*.csv", "Destination Distribution Page", 6, "drillDownDestinationWin_ExportButton", "drillDownDestinationWin_ExportAllRecords");
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 1st drilldown ( Destination Distribution Page ) for Destination: "+destinationVal);				
		cu.checkElementPresence("drillDownDestinationWin_ExportButton");
		cu.checkElementPresence("drillDownDestinationWin_BackButton");
		cu.checkElementPresence("drillDownDestinationWin_CountryTextBox");
		cu.checkElementPresence("drillDownDestinationWin_DestinationNameTextBox");
		cu.checkElementPresence("drillDownDestinationWin_FromDateTextBox");
		cu.checkElementPresence("drillDownDestinationWin_ToDateTextBox");

		//Map<String, Integer> csvData = exportCSVAndGetCoverageFieldsUpdated();
		Map<String, Integer> csvData = new LinkedHashMap<>();		
		csvData.put("Attempted Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Attempted Failure"));
		csvData.put("Submitted Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Submit Failure"));
		csvData.put("Delivered Failure", getSummationOfColoumnFromTableData(uiTableDataAlongWithHeadersWithoutMapping_1stDrill, "Delivered Failure"));
		
		// Validate summation of 1st drilldown ( Destination Distribution Page ) with MAIN page
		test.log(LogStatus.INFO, "Validate summation of 1st drilldown ( Destination Distribution Page ) with MAIN page for Destination: "+destinationVal);	
		for(String colName : csvData.keySet())
		{
				
		    	Integer uiValue = Integer.valueOf((int) (uiDataMainPage.get(colName)-0));
		    	
				if(csvData.get(colName).equals(uiValue))					
					test.log(LogStatus.PASS,
							"EXPECTED: "
									+ "Validate the summation of 1st drilldown and the MAIN page of this Destination"
									+ destinationVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
							"Validation:  <span style='font-weight:bold;'>ACTUAL::"
									+ "Validate the summation of 1st drilldown and the MAIN page of this Destination"
									+ destinationVal + " are same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
				else
					test.log(LogStatus.FAIL,
							"EXPECTED: "
							+ "Validate the summation of 1st drilldown and the MAIN page of this Destination"
							+ destinationVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
					"Validation:  <span style='font-weight:bold;'>ACTUAL::"
							+ "Validate the summation of 1st drilldown and the MAIN page of this Destination"
							+ destinationVal + " are not same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
		}
		
		
		//Returning back to Main page
		cu.clickElement("drillDownSuppAccWin_BackButton");
		
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
			cu.waitForElementInvisiblity("DeliveryStatisticsPageLoad", 180);
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
