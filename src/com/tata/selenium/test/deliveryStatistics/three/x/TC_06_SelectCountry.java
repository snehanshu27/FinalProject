package com.tata.selenium.test.deliveryStatistics.three.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
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

public class TC_06_SelectCountry implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_06_SelectCountry.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics.properties";
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
				.startTest("Execution triggered for - "+TC_06_SelectCountry.class.getName()+" -with TestdataId: " + uniqueDataId);
		String sheetName = "Delivery_Statistics_Screen";
		
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
		
		selectMainFilter();
		cu.clickElement("DeliveryStat_DisplayBtn");
		cu.waitForPageLoadWithSleep("", 500);
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in Country Main Page");	
		validateTableHeaders(dataMap.get("MainTableHeaders") , "Country Main Page");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in Country Main Page");
		if("MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(getUITableDataAlongWithHeadersWithoutMapping(), "Country-SMSDeliveryStatistics.csv", "Country Main Page", 9);
		else
			exportCSVAndValidateWithUI(getUITableDataAlongWithHeadersWithoutMapping(), "Country-SMSDeliveryStatistics.csv", "Country Main Page", 11);
					
		List<WebElement> countriesElemets = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[count(//*[@id='myTable']/thead/tr/th[text()='Country']/preceding-sibling::*)+1]/a"));
		List<String> countriesValues = new ArrayList<>();
		for (WebElement ele : countriesElemets)
			countriesValues.add(ele.getText());

		for (String countryVal : countriesValues) {
			mainPageAndSubsequenceDrillDownValidation(countryVal);
		}

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public Map<String, Double> getUIValesMainPage(CommonUtils cu, String countryValue)  {
		Map<String, Double> ret = new HashMap<>();

		try {
			ret.put("Attempted", Double.valueOf(cu.getText("DynamicAttempted_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Attempted Failure",Double.valueOf(cu.getText("DynamicAttemptedFailure_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submitted", Double.valueOf(cu.getText("DynamicSubmitted_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submit Failure", Double.valueOf(cu.getText("DynamicSubmittedFailure_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submitted Failure Percentage", Double.valueOf(cu.getText("DynamicSubmittedFailurePercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));
			ret.put("Enroute", Double.valueOf(cu.getText("DynamicEnroute_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Delivered", Double.valueOf(cu.getText("DynamicDelivered_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Delivered Percentage", Double.valueOf(cu.getText("DynamicDeliveredPercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));
			ret.put("Failed", Double.valueOf(cu.getText("DynamicFailed_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Failed Percentage", Double.valueOf(cu.getText("DynamicFailedPercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));

			return ret;
		} catch (Exception e) {
			LOGGER.error("Error occured while reading the data from UI -- "+e);
			cu.getScreenShot("Get webtable data for country Value: " + countryValue);
			test.log(LogStatus.FAIL,"", "Exception occur while fething the data from UI - " + e);
			return ret;
		}
	}

	public Map<String, Integer> exportCSVAndGetCoverageFieldsUpdated()
    {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("exportBtn");
		cu.waitForPageLoadWithSleep("", 500);
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + "Country-SMSCustomerAccountDistribution" + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTED: Exported file name should be in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTED: Exported file name should be in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = null;
		
		if("MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole").trim()))
			csvu = new CSVUtil(csvFilePath, 5);
		else
			csvu = new CSVUtil(csvFilePath, 6);

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
	
	private void validateTableHeaders(String tabHeadsStr, String nameOfTable) 
	{		
		cu.getScreenShot(nameOfTable+" - Table header validation");
		List<String> tableHeaders = Arrays.asList(tabHeadsStr.trim().split("\\~"));
		List<String> actualHeaders = cu.ElementsToList("allTableHeaders");
		
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
		//Select filters
		//Select ServiceLst filter
		if(!dataMap.get("DeliveryStat_ServiceLst").trim().isEmpty())
			cu.SelectDropDownByVisibleText("DeliveryStat_ServiceLst", dataMap.get("DeliveryStat_ServiceLst"));
		
		//Select Dimension filter
		if(!dataMap.get("Dimension").trim().isEmpty())
			cu.SelectDropDownByVisibleText("DeliveryStat_DimensionLst", dataMap.get("Dimension"));
		
		//Select Customer filter
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) && !dataMap.get("DeliveryStat_Customer_NameLst").trim().isEmpty())
		{
			cu.deselectDropDownAllOptions("DeliveryStat_Customer_NameLst");
			cu.SelectDropDownByVisibleText("DeliveryStat_Customer_NameLst", dataMap.get("DeliveryStat_Customer_NameLst"));
			cu.clickElement("DeliveryStatisticsPage");
			cu.waitForPageLoadWithSleep("", 500);
		}
		
		//Select Supplier filter
		if(!"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )
		{
			if(!dataMap.get("DeliveryStat_Supplier_NameLst").trim().isEmpty())
			{
				cu.deselectDropDownAllOptions("DeliveryStat_Supplier_NameLst");
				cu.SelectDropDownByVisibleText("DeliveryStat_Supplier_NameLst", dataMap.get("DeliveryStat_Supplier_NameLst"));
				cu.clickElement("DeliveryStatisticsPage");
				cu.waitForPageLoadWithSleep("", 500);
			}
		}
		
		//Select Country filter
		if(!dataMap.get("DeliveryStat_CountryLst").trim().isEmpty())
		{
			cu.deselectDropDownAllOptions("DeliveryStat_CountryLst");
			cu.SelectDropDownByVisibleText("DeliveryStat_CountryLst", dataMap.get("DeliveryStat_CountryLst"));
			cu.clickElement("DeliveryStatisticsPage");
			cu.waitForPageLoadWithSleep("", 500);
		}
		
		//Select Destination filter
		if(!dataMap.get("DeliveryStat_DestinationLst").trim().isEmpty())
		{
			cu.deselectDropDownAllOptions("DeliveryStat_DestinationLst");
			cu.SelectDropDownByVisibleText("DeliveryStat_DestinationLst", dataMap.get("DeliveryStat_DestinationLst"));
			cu.clickElement("DeliveryStatisticsPage");
			cu.waitForPageLoadWithSleep("", 500);
		}
		
		//Select Product filter
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) 
				&& !"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
						&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )	
		{
			if(!dataMap.get("DeliveryStat_ProductLst").isEmpty() && !dataMap.get("DeliveryStat_ProductLst").contains("Select ALL"))
			{
				cu.clickElement("DeliveryStat_ProductListToggleButton");
				cu.unSelectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", "Select ALL");
				for(String currProduct : dataMap.get("DeliveryStat_ProductLst").split("\\~"))
				{
					cu.setData("DeliveryStat_ProductListSearchTxtBox", currProduct);
					cu.sleep(500);
					cu.selectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", currProduct);
				}
				cu.clickElement("DeliveryStat_ProductLabel");
				cu.waitForPageLoadWithSleep("", 500);
			}
		}
		
		//Select Instance filter
		if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole").trim()) 
				&& !"MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
					&& !"MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
						&& !"MMX-Customer Routing".equals(dataMap.get("UserRole").trim())  )		
		{
			if(!dataMap.get("DeliveryStat_InstanceLst").trim().isEmpty())
			{
				cu.deselectDropDownAllOptions("DeliveryStat_InstanceLst");
				cu.SelectDropDownByVisibleText("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));
				cu.clickElement("DeliveryStatisticsPage");
				cu.waitForPageLoadWithSleep("", 500);
			}
		}

		// Select From DATE			
		cu.moveAndClick("DeliveryStat_FromDateTxt");
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
		cu.clickElement("DeliveryStatisticsPage");
}
	
	
	void mainPageAndSubsequenceDrillDownValidation(String countryVal)
	{

		test.log(LogStatus.INFO, "$$$$$$$$$$$ Validating for Countries: "+countryVal);
		Map<String, Double> uiMainData = getUIValesMainPage(cu, countryVal);
		
		//checking % and enroute valdiations on main pagedd
		
		Double submitPerMainExpected = uiMainData.get("Submitted Failure Percentage");
		Double deliveredPerMainExpected = uiMainData.get("Delivered Percentage");
		Double deliveredFailedPerMainExpected = uiMainData.get("Failed Percentage");
		int enrouteMainExpected = (int) (uiMainData.get("Enroute")-0);
		
		Double submitPerMainActualRaw= (uiMainData.get("Submitted")/uiMainData.get("Attempted"))*100.0;
		Double submitPerMainActual= Math.round(submitPerMainActualRaw * 100.0) / 100.0;
		Double deliveredPerMainActualRaw= (uiMainData.get("Delivered")/uiMainData.get("Submitted"))*100.0;
		Double deliveredPerMainActual= Math.round(deliveredPerMainActualRaw * 100.0) / 100.0;			
		Double deliveredFailedPerMainActualRaw= (uiMainData.get("Failed")/uiMainData.get("Submitted"))*100.0;
		Double deliveredFailedPerMainActual= Math.round(deliveredFailedPerMainActualRaw * 100.0) / 100.0;			
		int enrouteMainActual=(int) (uiMainData.get("Submitted")-(uiMainData.get("Delivered")+uiMainData.get("Failed")));
		
		//main page Submitted Failure Percentage validation 
		if(String.valueOf(submitPerMainExpected).equals(String.valueOf(submitPerMainActual)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Submitted Failure Percentage (main page)for Country: "+countryVal+" should be "+submitPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+submitPerMainActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Submitted Failure Percentage (main page)for Country: "+countryVal+" should be "+submitPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+submitPerMainActual+"</span>");
		}
		
		//main page Delivered Percentage validation
		if(String.valueOf(deliveredPerMainExpected).equals(String.valueOf(deliveredPerMainActual)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Delivered Percentage (main page)for Country: "+countryVal+" should be "+deliveredPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+deliveredPerMainActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Delivered Percentage (main page)for Country: "+countryVal+" should be "+deliveredPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+deliveredPerMainActual+"</span>");
		}
		
		//main page Delivered Failed Percentage validation
		if(String.valueOf(deliveredFailedPerMainExpected).equals(String.valueOf(deliveredFailedPerMainActual)))
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Delivered Failed Percentage (main page)for Country: "+countryVal+" should be "+deliveredFailedPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+deliveredFailedPerMainActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Delivered Failed Percentage (main page)for Country: "+countryVal+" should be "+deliveredFailedPerMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+deliveredFailedPerMainActual+"</span>");
		}
		
		//main page Enroute validation
		if(enrouteMainExpected == enrouteMainActual)
		{	
			test.log(LogStatus.PASS,
					"EXPECTED: Enroute (main page)for Country: "+countryVal+" should be "+enrouteMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+enrouteMainActual+"</span>");
		}
		else
		{		
			test.log(LogStatus.FAIL,
					"EXPECTED: Enroute (main page)for Country: "+countryVal+" should be "+enrouteMainExpected,
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+enrouteMainActual+"</span>");
		}		

		firstDrillDownValidation(countryVal, uiMainData);
	}
	
	void firstDrillDownValidation(String countryVal, Map<String, Double> uiMainData)
	{		
		//Entering Country Distribution Page ( 1st drilldown)
		cu.clickElement("dynamicContryValue", "$ContryVal$", countryVal);
		test.log(LogStatus.INFO, "######### Validating 1st drilldown ( Country Distribution Page )  for country: "+countryVal);
		
		//Table header validation
		test.log(LogStatus.INFO, "Validating Table Headers in 1st drilldown ( Country Distribution Page )  for country: "+countryVal);
		validateTableHeaders(dataMap.get("FisrtDrilldownHeaders"), "Country Distribution Page");
		
		//CSV and UI table validation
		test.log(LogStatus.INFO, "Validating CSV and UI table data in 1st drilldown ( Country Distribution Page )  for country: "+countryVal);	
		if("MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole").trim()))
			exportCSVAndValidateWithUI(getUITableDataAlongWithHeadersWithoutMapping(), "Country-SMSCustomerAccountDistribution.csv", "Country Distribution Page", 5);
		else
			exportCSVAndValidateWithUI(getUITableDataAlongWithHeadersWithoutMapping(), "Country-SMSCustomerAccountDistribution.csv", "Country Distribution Page", 6);
		
		//UI fields validation
		test.log(LogStatus.INFO, "Validating UI fields in 1st drilldown ( Country Distribution Page )  for country: "+countryVal);				
		cu.checkElementPresence("exportBtn");
		cu.checkElementPresence("backBtn");
		cu.checkReadonlyProperty("DrillDown_CountryNameTxt");
		if("MMX-Customer Manager".equals(dataMap.get("UserRole").trim()) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole").trim()) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole").trim()))
			cu.checkElementNotPresence("DrillDown_SupplierNameTxt");
		else
			cu.checkReadonlyProperty("DrillDown_SupplierNameTxt");
		cu.checkReadonlyProperty("DeliveryStat_FromDateTxt");
		cu.checkReadonlyProperty("DeliveryStat_ToDateTxt");

		Map<String, Integer> csvData = exportCSVAndGetCoverageFieldsUpdated();

		
		// Validate summation of CSV data (of detailed page )with UI data(of main page)
		test.log(LogStatus.INFO, "Validate summation of CSV data (1st drilldown [ Country Distribution Page ]) with UI data (of main page) in  for Country: "+countryVal);		
		for(String colName : csvData.keySet())
		{
//		    try {
				
		    	Integer uiValue = Integer.valueOf((int) (uiMainData.get(colName)-0));
		    	
				if(csvData.get(colName).equals(uiValue))					
					test.log(LogStatus.PASS,
							"EXPECTED: "
									+ "Validate the summation of csv values (1st drilldown) and the Ui Values (main page) of this Country: "
									+ countryVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
							"Validation:  <span style='font-weight:bold;'>ACTUAL::"
									+ "Validate the summation of csv values (1st drilldown) and the Ui Values (main page) of this Country: "
									+ countryVal + " are same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
				else
					test.log(LogStatus.FAIL,
							"EXPECTED: "
							+ "Validate the summation of csv values (1st drilldown) and the Ui Values (main page) of this Country: "
							+ countryVal + " -> should be same. Field Name : '"+colName+"'  - Expected value: "+uiValue,
					"Validation:  <span style='font-weight:bold;'>ACTUAL::"
							+ "Validate the summation of csv values (1st drilldown) and the Ui Values (main page) of this Country: "
							+ countryVal + " are not same. Field Name : '"+colName+"'  - Actual value: "+csvData.get(colName)+"</span>");
//			} catch (Exception e) {
//				
//				cu.printLogs("Exception occur while comparing the data  - " + e);
//				test.log(LogStatus.FAIL,"", "Exception occur while comparing the data  - " + e);
//			}
		}
		
		cu.clickElement("backBtn");
	}
	
	
	List<Map<String, String>> getUITableData()
	 {
		List<String> allHeaderNames = cu.ElementsToListWithTrim("allTableHeaders");
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
	
	 List<List<String>> getUITableDataAlongWithHeadersWithoutMapping()
	 {
		List<List<String>>  retRowLst = new LinkedList<>();
		int tableRowSize = cu.ElementsToList("allTableDataRows").size();
		List<String> allHeaderNames = cu.ElementsToListWithTrim("allTableHeaders");
		
		retRowLst.add(allHeaderNames);
		for(int i=1;i<tableRowSize+1;i++)
		{			 
			 List<String> curRowColoumTxts = cu.ElementsToListWithTrim("table_Dynamic_Row_AllColoumn", "$index$", i+"");
			 curRowColoumTxts = modifyTheDataRows(allHeaderNames, curRowColoumTxts);			 
			 retRowLst.add(curRowColoumTxts);
		}		
		return retRowLst;
	 }
	 
	 List<String> modifyTheDataRows(List<String> allHeaderNames, List<String> rowDataColoumn)
	 {
		 //Remove comma from number
		 String[] headersNeedChanges1 = new String[]{"Attempted","Attempted Failure","Submitted", "Submit Failure", "Enroute", "Delivered", "Failed"
				 											, "Total Delivered #", "0-15s #", "15-45s #", "45-90s #", "90-180s #", ">180s #"};
		 
		 for(String header : headersNeedChanges1)				
			 if(allHeaderNames.contains(header))			 
				 rowDataColoumn.set(allHeaderNames.indexOf(header), rowDataColoumn.get(allHeaderNames.indexOf(header)).replace(",", ""));
		 
		 return rowDataColoumn;
	 }
	
	 void exportCSVAndValidateWithUI(List<List<String>> uiDataMapRows, String expectedFileNamePattern, String tableName , int headerRowNumInCSV)  
	  {		 	
		  	cu.deleteAllFilesInDownloadFolder();
		  	cu.clickElement("exportBtn");
		  	cu.waitForPageLoadWithSleep("", 500);
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
			
			if((csvRawRowLines.size()-headerRowNumInCSV) == (uiDataMapRows.size()-headerRowNumInCSV))			
				test.log(LogStatus.PASS, "EXPECTECD: Records size in UI and CSV should matched ( for page '"+tableName+"')"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have matched. (Rows Size: "+(csvRawRowLines.size()-headerRowNumInCSV)+")</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Records size in UI and CSV should matched ( for page '"+tableName+"')"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have not matched. ( UI_Rows_Size: '"+(uiDataMapRows.size()-headerRowNumInCSV)
						+"'. CSV_Rows_Size: '"+(csvRawRowLines.size()-headerRowNumInCSV)+"' )</span>");
			
			if(csvRawRowLines.size()>0 && uiDataMapRows.size()>0)
			{
				csvRawRowLines.get(0);
				
				//Apply changes to headers ( if required )
				if(csvRawRowLines.get(0).contains("Average Latency(Sec)"))			
					csvRawRowLines.get(0).set(csvRawRowLines.get(0).indexOf("Average Latency(Sec)"), "Average Latency (Sec)");
				if(uiDataMapRows.get(0).contains("Average Latency(Sec)"))			
					uiDataMapRows.get(0).set(uiDataMapRows.get(0).indexOf("Average Latency(Sec)"), "Average Latency (Sec)");
				
				//Compare Headers
				if(csvRawRowLines.get(0).equals(uiDataMapRows.get(0)))				
					test.log(LogStatus.PASS, "EXPECTECD: UI and CSV Header Rows should be matched with CSV ( for page '"+tableName+"')"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI and CSV Header Rows matched with CSV. <br/>HeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiDataMapRows.get(0).toString())+"</span>");
				else
					test.log(LogStatus.FAIL, "EXPECTECD: UI and CSV Header Rows should be matched with CSV ( for page '"+tableName+"')"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI and CSV Header Rows not matched with CSV. <br/>UIHeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiDataMapRows.get(0).toString())
																															+"<br/>csvHeaderData: <br/><br/>"+StringEscapeUtils.escapeHtml3(csvRawRowLines.get(0).toString())+"</span>");
								
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
								, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI Row '"+uiRowPointer+"' not matched with CSV. (UIData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiRow.toString())+")</span>");
					
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
}
