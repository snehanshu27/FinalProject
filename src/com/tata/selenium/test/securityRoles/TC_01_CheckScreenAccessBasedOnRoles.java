package com.tata.selenium.test.securityRoles;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_01_CheckScreenAccessBasedOnRoles implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_CheckScreenAccessBasedOnRoles.class.getName());
	String properties =  "./data/SecurityRoles.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_01_CheckScreenAccessBasedOnRoles -with TestdataId: " + uniqueDataId);
		String sheetName = "ScreenAccessBasedOnRoles";
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
		cu.default_content();		
		
		if(("YES").equalsIgnoreCase(dataMap.get("eXchange_TAB"))){
			
			if(("YES").equals(dataMap.get("Dashboard"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				checkMainMenuPresence(cu,"Dashboard", dataMap.get("Dashboard"));
				checkSubMenu(cu,"dashboard_Tab","Country Status", dataMap.get("Country Status"));
				checkSubMenu(cu,"dashboard_Tab","Performance Trend", dataMap.get("Performance Trend"));
				checkSubMenu(cu,"dashboard_Tab","Destination Trend", dataMap.get("Destination Trend"));
				cu.getScreenShot("Validating Screen Presence for Dashborad Tab");
			}
			
			if(("YES").equals(dataMap.get("Customer"))){
				cu.default_content();	
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Customer");
				checkMainMenuPresence(cu, "Customer", dataMap.get("Customer"));
				checkSubMenu(cu,"Customer_Tab","Provisioning", dataMap.get("Provisioning"));
				checkSubMenu(cu,"Customer_Tab","Coverage View", dataMap.get("Coverage View"));
				checkSubMenu(cu,"Customer_Tab","Price Management", dataMap.get("Price Management"));
				cu.getScreenShot("Validating Screen Presence for Customer Tab");
			}
			
			if(("YES").equals(dataMap.get("Product"))){
				cu.default_content();	
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Product");
				checkMainMenuPresence(cu, "Product", dataMap.get("Product"));
				checkSubMenu(cu,"Product_Tab","Provisioning", dataMap.get("Provisioning"));
				checkSubMenu(cu,"Product_Tab","Coverage Management", dataMap.get("Coverage Management"));
				checkSubMenu(cu,"Product_Tab","Price Management", dataMap.get("Price ManagementProduct"));
				cu.getScreenShot("Validating Screen Presence for Product Tab");
			}
			
			if(("YES").equals(dataMap.get("Supplier"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Supplier");
				checkMainMenuPresence(cu, "Supplier", dataMap.get("Supplier"));
				checkSubMenu(cu,"Supplier_Tab","Provisioning", dataMap.get("Provisioning"));
				checkSubMenu(cu,"Supplier_Tab","Coverage Management", dataMap.get("Coverage ManagementSupplier"));
				checkSubMenu(cu,"Supplier_Tab","Cost Management", dataMap.get("Cost Management"));
				cu.getScreenShot("Validating Screen Presence for Supplier Tab");
			}
			
			if(("YES").equals(dataMap.get("Inventory"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Inventory");
				checkMainMenuPresence(cu, "Inventory", dataMap.get("Inventory"));
				checkSubMenu(cu,"Inventory_Tab","Number Inventory", dataMap.get("Number Inventory"));
				cu.getScreenShot("Validating Screen Presence for Inventory Tab");
			}
			
			if(("YES").equals(dataMap.get("Routing"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Routing");
				checkMainMenuPresence(cu, "Routing", dataMap.get("Routing"));
				checkSubMenu(cu,"Routing_Tab","Product Routing", dataMap.get("Product Routing"));
				checkSubMenu(cu,"Routing_Tab","Force Routing", dataMap.get("Force Routing"));
				cu.getScreenShot("Validating Screen Presence for Routing Tab");
			}
			
			if(("YES").equals(dataMap.get("Financial"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Financial");
				checkMainMenuPresence(cu, "Financial", dataMap.get("Financial"));
				checkSubMenu(cu,"Financial_Tab","Payable", dataMap.get("Payable"));
				checkSubMenu(cu,"Financial_Tab","Receivable", dataMap.get("Receivable"));
				checkSubMenu(cu,"Financial_Tab","Margin Statement", dataMap.get("Margin Statement"));
				checkSubMenu(cu,"Financial_Tab","RA Daily Report", dataMap.get("RA Daily Report"));
				cu.getScreenShot("Validating Screen Presence for Financial Tab");
			}
			
			if(("YES").equals(dataMap.get("Reporting"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Reporting");
				checkMainMenuPresence(cu, "Reporting", dataMap.get("Reporting"));
				checkSubMenu(cu,"Reporting_Tab","Delivery Statistics", dataMap.get("Delivery Statistics"));
				checkSubMenu(cu,"Reporting_Tab","Delivery Statistics Uncorrelated", dataMap.get("Delivery Statistics Uncorrelated"));
				cu.getScreenShot("Validating Screen Presence for Reporting Tab");
			}
			
			if(("YES").equals(dataMap.get("Administration"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("//*[contains(@name,'index')]");
				cu.clickElement("mainTabLink", "$destinationVal$", "Administration");
				checkMainMenuPresence(cu, "Administration", dataMap.get("Administration"));
				checkSubMenu(cu,"Administration_Tab","Email Distribution List", dataMap.get("Email Distribution List"));
				checkSubMenu(cu,"Administration_Tab","Togglz Console", dataMap.get("Togglz Console"));
				checkSubMenu(cu,"Administration_Tab","Notice Configuration", dataMap.get("Notice Configuration"));
				cu.getScreenShot("Validating Screen Presence for Administration Tab");
			}
			
		}
		
		if(("YES").equalsIgnoreCase(dataMap.get("Search_TAB"))){
			cu.default_content();
			cu.SwitchFrames("//iframe[@scrolling='no']");
			cu.clickElement("SearchTab");
			cu.waitForPageLoad("Search Tab");
			cu.default_content();
			checkMainMenuPresence(cu, "SMS", dataMap.get("Search_TAB"));
			checkSubMenu(cu,"SMS_Tab","Events Search", dataMap.get("Events Search"));
			cu.getScreenShot("Validating Screen Presence for Search Tab");
		}
		
		if(("YES").equalsIgnoreCase(dataMap.get("Performance_TAB"))){
			cu.default_content();
			cu.SwitchFrames("//iframe[@scrolling='no']");
			cu.clickElement("performanceTab");
			cu.waitForPageLoad("Performance Tab");
			cu.default_content();
			checkSubMenu(cu,"KPI_Alerts_Tab","Alert Configured", dataMap.get("Alert Configured"));
			checkSubMenu(cu,"KPI_Alerts_Tab","Alert History", dataMap.get("Alert History"));	
			checkSubMenu(cu,"KPI_Alerts_Tab","Alert Creation", dataMap.get("Alert Creation"));	
			cu.getScreenShot("Validating Screen Presence for Performance Tab");
		}

		cu.default_content();
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");		
		
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
		extent = ExtReport.instance("ScreenAccessBasedOnRoles");
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
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}
	
	
	  public void checkMainMenuPresence(CommonUtils cu,String TabName,String val) throws Exception{
		  if(("YES").equalsIgnoreCase(val)){
			  try{
				  cu.default_content();
				  cu.SwitchFrames("//iframe[@name='bottom']");
				  cu.SwitchFrames("//*[contains(@name,'index')]");
				  if(cu.existElement("mainTabObject", "$destinationVal$", TabName)){
						test.log(LogStatus.PASS, "EXPECTECD: '"+TabName+"' menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL::'"+TabName+"' menu is present</span>");
					}else{
						test.log(LogStatus.FAIL, "EXPECTECD: '"+TabName+"' menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL:: '"+TabName+"' menu is not present</span>");
					} 
			  }catch(Exception e){
				  LOGGER.info(TabName+" tab does not exist because "+e);
				  cu.printLogs(TabName+" tab does not exist");
			  }
		  }
		  cu.default_content();	
	  }
	  
	  public void checkSubMenuPresence(CommonUtils cu,String TabName,String val) throws Exception{
		  if(("Y").equalsIgnoreCase(val)){
			  try{
				  cu.default_content();
				  cu.SwitchFrames("//iframe[@name='bottom']");
				  cu.SwitchFrames("//*[contains(@name,'index')]");
				  if(cu.existElement("subTabObject", "$destinationVal$", TabName)){
						test.log(LogStatus.PASS, "EXPECTECD: '"+TabName+"' Sub menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL::'"+TabName+"' Sub menu is present</span>");
					}else{
						test.log(LogStatus.FAIL, "EXPECTECD: '"+TabName+"' Sub menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL:: '"+TabName+"' Sub menu is not present</span>");
					} 
			  }catch(Exception e){
				  LOGGER.info(TabName+" Sub tab does not exist because "+e);
				  cu.printLogs(TabName+" Sub tab does not exist");
				  cu.default_content();
			  }
		  }
		  cu.default_content();
	  }
	  
	  public void checkSubMenu(CommonUtils cu,String mainTabPath,String TabName,String val) throws Exception{
		  if(("Y").equalsIgnoreCase(val)){
			  try{
				  
				  cu.default_content();
				  cu.SwitchFrames("//iframe[@name='bottom']");
				  cu.SwitchFrames("//*[contains(@name,'index')]");
				  if(cu.existElement(mainTabPath, "$destinationVal$", TabName)){
						test.log(LogStatus.PASS, "EXPECTECD: '"+TabName+"' Sub menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL::'"+TabName+"' Sub menu is present</span>");
					}else{
						test.log(LogStatus.FAIL, "EXPECTECD: '"+TabName+"' Sub menu should be present",
								"Validation: <span style='font-weight:bold;'>ACTUAL:: '"+TabName+"' Sub menu is not present</span>");
					} 
			  }catch(Exception e){
				  LOGGER.info(TabName+" Sub tab does not exist because "+e);
				  cu.printLogs(TabName+" Sub tab does not exist");
				  cu.default_content();
			  }
		  }
		  cu.default_content();
	  }

}
