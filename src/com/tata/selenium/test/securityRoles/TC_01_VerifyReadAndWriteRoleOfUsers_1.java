package com.tata.selenium.test.securityRoles;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;



public class TC_01_VerifyReadAndWriteRoleOfUsers_1 implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_VerifyReadAndWriteRoleOfUsers_1.class.getName());
	String properties =  "./data/SecurityRoles.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_01_VerifyReadAndWriteRoles - "+uniqueDataId+ " for "+dataMap.get("Description"));
		String sheetName="Checking_Roles_Access";
		
		//Reading excel values
		try{
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH,sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		}
		catch (Exception e){
			CommonUtils.printConsole("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			Reporter.log("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			test.log(LogStatus.FAIL,"Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e, uniqueDataId, "Result_Errors");
			Assert.fail("Error occured while trying to login to the application  -  " +e);
		}	
		
		
		test.log(LogStatus.INFO, "Launch Application", "Usage: <span style='font-weight:bold;'>Going to Launch App</span>");
		
		CommonUtils cu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		cu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);		
		driver =cu.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		cu.waitForPageLoad("MessagingInstanceHomePage");
		
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		cu.default_content();
		
		//Validating user for eXchange Tab Access
		if(("YES").equalsIgnoreCase(dataMap.get("eXchange_TAB"))){
			
			if(("YES").equals(dataMap.get("Dashboard"))){
				checkAccess(cu,"dashboard_Tab","Country Status", dataMap.get("Country Status"));
				checkAccess(cu,"dashboard_Tab","Performance Trend", dataMap.get("Performance Trend"));
				checkAccess(cu,"dashboard_Tab","Destination Trend", dataMap.get("Destination Trend"));
			}
			
			if(("YES").equals(dataMap.get("Customer"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Customer");
				checkAccess(cu,"Customer_Tab","Provisioning", dataMap.get("Provisioning"));
				checkAccess(cu,"Customer_Tab","Coverage View", dataMap.get("Coverage View"));
				//checkAccess(cu,"Customer_Tab","Price Management", dataMap.get("Price Management"));
			}
			
			if(("YES").equals(dataMap.get("Product"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Product");
				checkAccess(cu,"Product_Tab","Provisioning", dataMap.get("Provisioning"));
				checkAccess(cu,"Product_Tab","Coverage Management", dataMap.get("Coverage Management"));
				checkAccess(cu,"Product_Tab","Price Management", dataMap.get("Price ManagementProduct"));
			}
			
			if(("YES").equals(dataMap.get("Supplier"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Supplier");
				checkAccess(cu,"Supplier_Tab","Provisioning", dataMap.get("Provisioning"));
				checkAccess(cu,"Supplier_Tab","Coverage Management", dataMap.get("Coverage ManagementSupplier"));
				checkAccess(cu,"Supplier_Tab","Cost Management", dataMap.get("Cost Management"));
			}
			
			if(("YES").equals(dataMap.get("Inventory"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Inventory");
				checkAccess(cu,"Inventory_Tab","Number Inventory", dataMap.get("Number Inventory"));
			}
			
			if(("YES").equals(dataMap.get("Routing"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Routing");
				checkAccess(cu,"Routing_Tab","Product Routing", dataMap.get("Product Routing"));
				//checkAccess(cu,"Routing_Tab","Force Routing", dataMap.get("Force Routing"));
			}
			
			if(("YES").equals(dataMap.get("Financial"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Financial");
				checkAccess(cu,"Financial_Tab","Payable", dataMap.get("Payable"));
				checkAccess(cu,"Financial_Tab","Receivable", dataMap.get("Receivable"));
				checkAccess(cu,"Financial_Tab","Margin Statement", dataMap.get("Margin Statement"));
				checkAccess(cu,"Financial_Tab","RA Daily Report", dataMap.get("RA Daily Report"));
			}
			
			
			if(("YES").equals(dataMap.get("Reporting"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Reporting");
				checkAccess(cu,"Reporting_Tab","Delivery Statistics", dataMap.get("Delivery Statistics"));
				checkAccess(cu,"Reporting_Tab","Delivery Statistics Uncorrelated", dataMap.get("Delivery Statistics Uncorrelated"));
			}
			
			if(("YES").equals(dataMap.get("Administration"))){
				cu.SwitchFrames("//iframe[@name='bottom']");
				cu.SwitchFrames("index");
				cu.clickElement("mainTabLink", "$destinationVal$", "Administration");
				checkAccess(cu,"Administration_Tab","Email Distribution List", dataMap.get("Email Distribution List"));
				checkAccess(cu,"Administration_Tab","Togglz Console", dataMap.get("Togglz Console"));
				checkAccess(cu,"Administration_Tab","Notice Configuration", dataMap.get("Notice Configuration"));
			}
			
		}
		
		if(("YES").equalsIgnoreCase(dataMap.get("Search_TAB"))){
			cu.default_content();
			cu.SwitchFrames("//iframe[@scrolling='no']");
			cu.clickElement("SearchTab");
			cu.waitForPageLoad("Search Tab");
			cu.default_content();
			checkAccess(cu,"SMS_Tab","Events Search", dataMap.get("Events Search"));	
		}
		
		if(("YES").equalsIgnoreCase(dataMap.get("Performance_TAB"))){
			cu.default_content();
			cu.SwitchFrames("//iframe[@scrolling='no']");
			cu.clickElement("performanceTab");
			cu.waitForPageLoad("Performance Tab");
			cu.default_content();
			checkAccess(cu,"KPI_Alerts_Tab","Alert Configured", dataMap.get("Alert Configured"));
			checkAccess(cu,"KPI_Alerts_Tab","Alert History", dataMap.get("Alert History"));	
			checkAccess(cu,"KPI_Alerts_Tab","Alert Creation", dataMap.get("Alert Creation"));	
		}
	
				
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	

	}
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("CheckingAccesOfDifferentRoles");
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
		  try{
			  Log.endTestCase(testCaseId);
			  driver.quit();
			  //Ending the Extent test
			  extent.endTest(test);
			  //Writing the report to HTML format
			  extent.flush();    
		  } catch(Exception e){
			  LOGGER.info(" App Logout failed () :: Exception: " +e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	  
	  public void checkAccess(CommonUtils cu,String elemntPath,String subTabName,String strAccessvalue) throws Exception{
		  if(!strAccessvalue.isEmpty()){
			  cu.default_content();
			  cu.SwitchFrames("//iframe[@name='bottom']");
			  cu.SwitchFrames("//*[contains(@name,'index')]");
			  cu.clickElement(elemntPath, "$destinationVal$", subTabName);
			  cu.waitForPageLoad("subTabName");
			  cu.default_content();
			  cu.SwitchFrames("bottom");
			  cu.SwitchFrames("target");
			  if(!subTabName.equalsIgnoreCase("Alert Configured"))
			  {
				  cu.checkUserAccess("writeAccessElememt",strAccessvalue, subTabName);
				 	  
			  }
			  else
				  cu.checkUserAccess("writeDeActivateElememt",strAccessvalue, subTabName);
			  
		  }
		  cu.default_content();	
	  }
}
