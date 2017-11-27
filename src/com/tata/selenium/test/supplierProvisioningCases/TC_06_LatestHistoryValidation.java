package com.tata.selenium.test.supplierProvisioningCases;

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
 * @author Sonali Das
 * @description This class will perform action in MMX application
 */

public class TC_06_LatestHistoryValidation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_06_LatestHistoryValidation.class.getName());
	String properties = OBJECT_REPO_FILEPATH;
	ExcelUtils excelUtils = new ExcelUtils();
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	private ExtentReports extent;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_06_LatestHistoryValidation  -with TestdataId: "+uniqueDataId);
		String sheetName="Supplier_Provisioning_Screen";
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
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		/*cu.SelectDropDownByVisibleText("Supplier_Name", dataMap.get("Supplier_Name"));
		cu.SelectDropDownByVisibleText("Supplier_Account_Name" , dataMap.get("Supplier_Account_Name"));*/

		cu.clickElement("Supplier_Name_DropDown_Button");
		cu.sendKeys("Supplier_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Name"), false);
		cu.sleep(1000);
		cu.clickElement("Supplier_Name_DropDown_Dynamic_LabelOPtion", "$suppliername$", dataMap.get("Supplier_Name"));
		
         System.out.println(dataMap.get("Supplier_Name"));
         System.out.println(dataMap.get("Supplier_Account_Name"));
         
		cu.clickElement("Supplier_Account_Name_DropDown_Button");
		cu.sendKeys("Supplier_Account_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Account_Name"), false);
		cu.sleep(1000);
		cu.clickElement("Supplier_Account_Name_DropDown_Dynamic_LabelOPtion", "$supplieraccountname$", dataMap.get("Supplier_Account_Name"));
		
		cu.SelectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.SelectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		cu.clickElement("supplier_DisplayBtn");
		
		//First validation: History Tab has 5 latest values or not.
		 List<String> retStrOPs=cu.getAllOptionsFromDropDown("History");
		 int historyValueCount=retStrOPs.size();
		 if((historyValueCount-1)<=5){
			cu.printLogs((historyValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.PASS, "EXPECTECD: History Drop down should have "+(historyValueCount-1)+" latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: History Drop down has '"+(historyValueCount-1)+"' values</span>");
		 }else{
			cu.printLogs((historyValueCount-1)+"  latest values are available in dropdown");
			test.log(LogStatus.FAIL, "EXPECTECD: History Drop down should have 5 latest values", "Validation:  <span style='font-weight:bold;'>ACTUAL:: History Drop down has '"+(historyValueCount-1)+"' values</span>");
		 }
		 
		 //Validation 2: Checking out of 5 fields available, except first remaining all should be non editable
		 cu.checkFiveLatestValFromDropDown("History", retStrOPs);

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
		  extent = ExtReport.instance("SupplierProvisioning");	
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
			  LOGGER.info("error" +e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
