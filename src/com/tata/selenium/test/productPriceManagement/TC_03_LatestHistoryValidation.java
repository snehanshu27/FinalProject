package com.tata.selenium.test.productPriceManagement;

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


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_03_LatestHistoryValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_03_LatestHistoryValidation.class.getName());
	String properties =  "./data/ProductPriceManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_03_LatestHistoryValidation  -with TestdataId: "+uniqueDataId);
		String sheetName="Product_Price_Management_Screen";
		//Reading excel values
		try{
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH,sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		}
		catch (Exception e){
			LOGGER.error("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
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
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		
		cu.selectDropDownByVisibleText("Service_NameLst", dataMap.get("Service_NameLst"));
		cu.selectDropDownByVisibleText("Product_NameLst", dataMap.get("Product_NameLst"));
		cu.clickElement("DisplayBtn");
		cu.waitForPageLoad("ProductPriceManagement");
		
		//First validation: History Tab has 5 latest values or not.
		 List<String> retStrOPs=cu.getAllOptionsFromDropDown("PriceCardLst");
		 int historyValueCount=retStrOPs.size();
		 if((historyValueCount-1)<=5){
			cu.printLogs((historyValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.PASS, "EXPECTECD: Price Card Drop down should have "+(historyValueCount-1)+" latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Price Card drop down has '"+(historyValueCount-1)+"' values</span>");
		 }else{
			cu.printLogs((historyValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.FAIL, "EXPECTECD: Price Card Drop down should have "+(historyValueCount-1)+" latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Price Card drop down has '"+(historyValueCount-1)+"' values</span>");
		 }
		 
		 //Validation 2: Checking out of 5 fields available, except first remaining all should be non editable
		 for(int i=1;i<historyValueCount;i++){ 
				String History= retStrOPs.get(i);
				cu.selectDropDownByVisibleText("PriceCardLst", History);
				cu.clickElement("DisplayBtn");
				cu.waitForPageLoad("");
				cu.clickElement("SubmitBtn");
				 if(cu.existsElement("application_PopUpTitle")){
					 cu.getScreenShot("Validation of Cost card");
					 String PopUp_Name = cu.returnElement("application_PopUpMessage").getText().trim();
					 if("Warning: Cost/Effective date information incomplete".equalsIgnoreCase(PopUp_Name)){
						 if(i==1){
							 cu.printLogs(i+"nd/th Value from List is -"+History+" and is non editable in the page");
							 test.log(LogStatus.PASS, "EXPECTECD: "+i+"st Value from List should be editable", "Validation:  <span style='font-weight:bold;'>ACTUAL::"+i+"nd/th Value which is  '"+History+"'  from List is editable</span>");
						 }else{
							 cu.printLogs("Other values other than first  is editable and the value is  -"+History+" ");
							 test.log(LogStatus.FAIL, "EXPECTECD: Except First Value remaining all should be non editable", "Validation:  <span style='font-weight:bold;'>ACTUAL::Other than first value other values are also editable and the value is  -"+History+" </span>");
						 }
						 
					 }else if("Error: Only the latest Price card can be submitted".equalsIgnoreCase(PopUp_Name)){
						 cu.getScreenShot("Validation of Cost card");
						 if(i==1){
							 cu.printLogs(i+"st Value from List is -"+History+" is non editable in the page");
							 test.log(LogStatus.FAIL, "EXPECTECD: "+i+"st Value from List should be editable", "Validation:  <span style='font-weight:bold;'>ACTUAL::"+i+"st Value which is  '"+History+"'  from List is non editable</span>");
						 }else{
							 cu.printLogs("Other values other than first  is non editable and the value is  -"+History+" ");
							 test.log(LogStatus.PASS, "EXPECTECD: Values should be non editable", "Validation:  <span style='font-weight:bold;'>ACTUAL::Number "+i+" Value is non editable and the value is  -"+History+" </span>");
						 }
					 }
				 }
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
		  extent = ExtReport.instance("ProductPriceManagement");	
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
	
}
