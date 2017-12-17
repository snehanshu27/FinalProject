package com.tata.selenium.test.productRoutingDetailsReportCases;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
import com.tata.selenium.utils.CSVUtil1;
import com.tata.selenium.utils.CommonUtils1;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_08_RoutingDetailsDistributionReportUIAndCSVDataCompare implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_08_RoutingDetailsDistributionReportUIAndCSVDataCompare.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	
	String properties = "./data/ProductRouting.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_08_RoutingDetailsDistributionReportUIAndCSVDataCompare -with TestdataId: " + uniqueDataId);
		String sheetName = "Product_Routing_Screen_Details";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
			CommonUtils1.printConsole("Exception while reading data from EXCEL file for test case : " + testCaseId
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
				"Validation:  <span style='font-weight:bold;'>ACTUAL::Going to Launch App</span>");

		CommonUtils1 cu = new CommonUtils1(driver, test, sheetName, uniqueDataId, testCaseId, properties);
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

		cu.clickElement("Product_DetailsBtn");
		cu.SetData("Product_Routing_Details_FromDate_TextBox", dataMap.get("FromDate"));
		cu.SetData("Product_Routing_Details_ToDate_TextBox", dataMap.get("ToDate"));
		cu.SelectDropDownByVisibleText("Product_Routing_Details_ReportType_Dropdown", dataMap.get("Report_Type"));
		cu.clickElement("Product_Routing_Details_Display_Button");
		
		cu.checkElementPresence("Product_Routing_Details_Distribution_MainTableHeaders");
		
		List<List<String>> allMainRowsUIData = getTableBodyDataString(cu);
		int allMainRowsSize =  allMainRowsUIData.size();
		List<List<String>> allMainRowsCSVData = exportCSVAndGetData(cu, "Distribution.csv");
		
		//main table compare
		if(compareUIAndCSV(allMainRowsUIData, allMainRowsCSVData))
			test.log(LogStatus.PASS,
					"EXPECTECD: Both UI and exported CSV maintable data should be macthed", 
					"Validation:  <span style='font-weight:bold;'>ACTUAL::Both UI and exported CSV maintable data has been macthed</span>");
		else
			test.log(LogStatus.FAIL,
					"EXPECTECD: Both UI and exported CSV maintable data should be macthed", 
					"Validation:  <span style='font-weight:bold;'>ACTUAL::Both UI and exported CSV maintable data has not macthed</span>");
			
		
		for(int i=0;i<allMainRowsSize; i++)
		{
			List<String> currentColumnUIMainData = allMainRowsUIData.get(i);
			
			if(currentColumnUIMainData.size()==3)
			{		
				String routePriorityMainUI = currentColumnUIMainData.get(0);
				String noOfRoutesMainUI = currentColumnUIMainData.get(1);
				String productMainUI = currentColumnUIMainData.get(2);
				
				//Drilldown
				test.log(LogStatus.INFO, "Entering Performance Drilldown for main index"+ (i+1)+" ^Product: "+productMainUI+" ^ Priority: "+routePriorityMainUI);
				cu.clickElement("Dynamic_Product_Routing_Details_Table_FirstColumnLinks", "$index$", String.valueOf(i+1));
				cu.checkElementPresence("Product_Routing_Details_Distribution_DrillDownTableHeaders");
				
				String drillDownSelectedProductUI = cu.getAttribute("Product_Routing_Details_DrillDown_Product_Textbox", "value");
				List<List<String>> allDrilldownUIRowsData = getTableBodyDataString(cu);
				List<List<String>> allDrilldownCSVRowsData = exportCSVAndGetData(cu, "Distribution - Details - "+drillDownSelectedProductUI+".csv");
				
				//Drilldown table compare
				if(compareUIAndCSV(allDrilldownUIRowsData, allDrilldownCSVRowsData))
					test.log(LogStatus.PASS,
							"EXPECTECD: (Drilldown) Both UI and exported CSV Drilldown data should be macthed. (mainindex: "+(i+1)+" ^Product: "+productMainUI+" ^ Priority: "+routePriorityMainUI+" )", 
							"Validation:  <span style='font-weight:bold;'>ACTUAL::Both UI and exported CSV Drilldown data has been macthed</span>");
				else
					test.log(LogStatus.FAIL,
							"EXPECTECD: (Drilldown) Both UI and exported CSV Drilldown data should be macthed. (mainindex: "+(i+1)+" ^Product: "+productMainUI+" ^ Priority: "+routePriorityMainUI+" )",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::Both UI and exported CSV Drilldown data has not macthed</span>");
												
				cu.clickElement("Product_Routing_Details_DrillDown_BackButton");
			}
			
		}
				
		// Taking screenshot and Logging ou
		cu.getScreenShot("Validation Of Routing Details Screen");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	private String getTimeDiffMins(String t1, String t2)
	{
		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String diffMinutes = "";
		Date d1 = null;
		Date d2 = null;
		try {
		    d1 = format.parse(t1);
		    d2 = format.parse(t2);
		    long diff = d2.getTime() - d1.getTime(); 
		   
		    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		    calendar.setTimeInMillis(diff);
		    int min = calendar.get(Calendar.MINUTE) ;
		    String sec = (calendar.get(Calendar.SECOND) < 10 ? "0" : "") + calendar.get(Calendar.SECOND);
		    diffMinutes = min+":"+sec;
		    
		} catch (ParseException e) {
		  e.printStackTrace();
		}    
		return diffMinutes;
	
	}
	private List<List<String>> getTableBodyDataString(CommonUtils1 cu)
	{
		List<List<String>> ret = new ArrayList<>();
		int allMainRowsSize = 0;
		List<WebElement> allMainRows = cu.getElements("Product_Routing_Details_Table_Rows");
		if(allMainRows!=null)
			allMainRowsSize = allMainRows.size();
		
		for(int i=0;i<allMainRowsSize; i++)
		{			
			WebElement currentRow = cu.getElements("Product_Routing_Details_Table_Rows").get(i);
			List<WebElement> mainTds = currentRow.findElements(By.tagName("td"));
			List<String> currRow = new ArrayList<>();			
			
			for(int j=0; j<mainTds.size();j++)
				currRow.add(mainTds.get(j).getText());	
			
			ret.add(currRow);
		}
		
		return ret;
	}
	
	public List<List<String>> exportCSVAndGetData(CommonUtils1 cu, String expectedFileName){
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("Product_ExportBtn");
		cu.waitForPageLoad("Product Routing Details");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();
		// validate file name
				
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in '"+expectedFileName+"'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in '"+expectedFileName+"'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil1 csvu = new CSVUtil1(csvFilePath, 1);
		
		//get all rows data
		List<List<String>> allRowData = csvu.getAllRowsData();
		return allRowData;
	}

	private boolean compareUIAndCSV(List<List<String>> ui, List<List<String>> csv)
	{
		boolean ret = false;
		if(ui.size() == csv.size())		
			for(int i=0; i<ui.size(); i++)
			{
				Object[] uiArry = ui.get(i).toArray();
				Object[] csvArry = ui.get(i).toArray();
				if(Arrays.deepEquals(uiArry, csvArry))
					ret = true;
			}
				
		return ret;
	}
	
	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Product_Routing");
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



