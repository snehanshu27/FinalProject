package com.tata.selenium.test.productRoutingDetailsReportCases;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
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
import com.tata.selenium.utils.CommonUtils1;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_04_RoutingDetailsRoutesReportValidations implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_RoutingDetailsRoutesReportValidations.class.getName());
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
				"Execution triggered for - TC_04_RoutingDetailsRoutesReportValidations -with TestdataId: " + uniqueDataId);
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
		
		cu.checkElementPresence("Product_Routing_Details_Routes_MainTableHeaders");
		
		List<List<String>> allMainRowsData = getTableBodyDataString(cu);
		int allMainRowsSize =  allMainRowsData.size();
		
		for(int i=0;i<allMainRowsSize; i++)
		{
			List<String> currentColumnMainData = allMainRowsData.get(i);
			
			if(currentColumnMainData.size()==7)
			{
				String indexNoMainUI = currentColumnMainData.get(0);
				String runStartTimeMainUI = currentColumnMainData.get(1);
				String runEndTimeMainUI = currentColumnMainData.get(2);
				String productMainUI = currentColumnMainData.get(4);
				String noOfRoutesMainUI = currentColumnMainData.get(5);
				
				if("0".equals(noOfRoutesMainUI))
				{
					if("ALL".equalsIgnoreCase(productMainUI))
						test.log(LogStatus.PASS,
								"EXPECTECD: Product should be ALL when No. Of Routes: 0. (mainindex: "+indexNoMainUI+")", 
								"Validation:  <span style='font-weight:bold;'>ACTUAL::Product is ALL for No. Of Routes: 0</span>");
					else
						test.log(LogStatus.FAIL,
								"EXPECTECD: Product should be ALL when No. Of Routes: 0. (mainindex: "+indexNoMainUI+")", 
								"Validation:  <span style='font-weight:bold;'>ACTUAL::Product is not ALL when No. Of Routes: 0</span>");
				
					if(!cu.existElement("Dynamic_IndexTextLink_Product_Routing_Details_Table_FirstColumnLinks", "$index$", indexNoMainUI, 100))
						test.log(LogStatus.PASS,
								"EXPECTECD: Drilldown link should be not avaliable when No. Of Routes: 0. (mainindex: "+indexNoMainUI+")", 
								"Validation:  <span style='font-weight:bold;'>ACTUAL::Drilldown link is not avaliable when No. Of Routes: 0</span>");
					else
						test.log(LogStatus.FAIL,
								"EXPECTECD: Drilldown link should be not avaliable when No. Of Routes: 0. (mainindex: "+indexNoMainUI+")", 
								"Validation:  <span style='font-weight:bold;'>ACTUAL::Drilldown link is avaliable when No. Of Routes: 0</span>");
				}
				
				if(!noOfRoutesMainUI.isEmpty() && !noOfRoutesMainUI.equals("0"))
				{
					//Drilldown
					test.log(LogStatus.INFO, "Entering Performance Drilldown for index: "+ indexNoMainUI+" ^ Product: "+productMainUI);
					cu.clickElement("Dynamic_IndexTextLink_Product_Routing_Details_Table_FirstColumnLinks", "$index$", indexNoMainUI);
					cu.checkElementPresence("Product_Routing_Details_Routes_DrillDownTableHeaders");
					List<List<String>> allDrilldownRowsData = getTableBodyDataString(cu);
					int allDrilldownRowsSize =  allDrilldownRowsData.size();
											
						if(allDrilldownRowsSize == Integer.parseInt(noOfRoutesMainUI))
							test.log(LogStatus.PASS,
									"EXPECTECD: No of routues in main table should match with number of rows in Drilldown table. No. of routes: "+allDrilldownRowsSize+". (main row index: "+ indexNoMainUI+" ^ Product: "+productMainUI, 
									"Validation:  <span style='font-weight:bold;'>ACTUAL::No of routues in main table is matching with number of rows in Drilldown table. No. of routes: "+allDrilldownRowsSize+".</span>");
						else
							test.log(LogStatus.FAIL,
									"EXPECTECD: No of routues in main table should match with number of rows in Drilldown table. No. of routes: "+allDrilldownRowsSize+". (main row index: "+ indexNoMainUI+" ^ Product: "+productMainUI, 
									"Validation:  <span style='font-weight:bold;'>ACTUAL::No of routues in main table is not matching with number of rows in Drilldown table. No. of routes in maintable: "+noOfRoutesMainUI+". Number of rows in Drilldown: "+allDrilldownRowsSize+"</span>");
						
						String drillDownSelectedProductUI = cu.getAttribute("Product_Routing_Details_DrillDown_Product_Textbox", "value");
						if(drillDownSelectedProductUI.equals(productMainUI))
							test.log(LogStatus.PASS,
									"EXPECTECD: Product in both Drilldown and Main table should be matched. (main row index: "+ indexNoMainUI+" ^ Product: "+productMainUI, 
									"Validation:  <span style='font-weight:bold;'>ACTUAL::Product in both Drilldown and Main table is matching. </span>");						
						else
							test.log(LogStatus.FAIL,
									"EXPECTECD: Product in both Drilldown and Main table should be matched. (main row index: "+ indexNoMainUI+" ^ Product: "+productMainUI, 
									"Validation:  <span style='font-weight:bold;'>ACTUAL::Product in both Drilldown and Main table is not matching. MainTable Product: "+productMainUI+" ^ DrillDown Product: "+drillDownSelectedProductUI+"</span>");
												
					cu.clickElement("Product_Routing_Details_DrillDown_BackButton");
				}
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
	
	private String getUIDate(CommonUtils1 cu)
	{	String s1 = cu.getText("Product_Routing_Details_UI_GMT_Time");
		Date date = new Date();
		try {
			date = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z").parse(s1);
		} catch (ParseException e) {
			LOGGER.error(e);
		}
		String ret = new SimpleDateFormat("dd-MM-yyyy").format(date);		
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



