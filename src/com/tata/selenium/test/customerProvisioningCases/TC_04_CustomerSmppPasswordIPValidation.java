package com.tata.selenium.test.customerProvisioningCases;

import com.tata.selenium.constants.ApplicationConstants;

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
//import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.pages.LoginPage;
import com.tata.selenium.pages.MessagingInstanceHomePage;
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_04_CustomerSmppPasswordIPValidation implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_04_CustomerSmppPasswordIPValidation.class.getName());
	String properties = "./data/CustomerProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	
	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test ;
	private ExtentReports extent;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_04_CustomerSmppPasswordIPValidation  -with TestdataId: "+uniqueDataId);
		String sheetName="Customer_Provisioning_Screen";
		
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
		
		cu.SelectDropDownByVisibleText("Customer_Name", dataMap.get("Customer_Name"));
		cu.SelectDropDownByVisibleText("Customer_Account_Name" , dataMap.get("Customer_Account_Name"));
		cu.SelectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.SelectDropDownByVisibleText("History", dataMap.get("History"));
		cu.SelectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		cu.clickElement("Customer_DisplayBtn");
		cu.clickElement("Customer_EditBtn");
		
		if(("Y").equalsIgnoreCase(dataMap.get("SMPP_InfoTab"))){
			cu.clickElement("SMPP_InfoTab");
			cu.waitForPageLoad("CustomerProvisioning");
			//Doing validation if password field has some value in test data
			if(dataMap.get("SMPP_PasswordTxt").trim().length() >0){
			cu.SetDataWithoutClearing("SMPP_PasswordTxt", dataMap.get("SMPP_PasswordTxt"));
		    System.out.println("Before submit button");
			cu.clickElement("Customer_SubmitBtn");
			System.out.println("After Submit button");
			cu.checkMessage("application_PopUpTitle", "Validation of SMPP Password","Error: SMPP Password cannot be more than 8 characters");
			
			}
			//Doing validation if Host IP field has some value in test data
			/*if(dataMap.get("SMPP_HostIPTxt").trim().length() >0){
				cu.SetData("SMPP_HostIPTxt", dataMap.get("SMPP_HostIPTxt"));
				cu.clickElement("supplier_SubmitBtn");
				cu.checkMessage("application_PopUpTitle", "Validation of SMPP Host IP","Error: Please enter valid Host IP address");
			}*/
			
			if(dataMap.get("SMPP_DataCodeLst").trim().length() >0){
				String[] data=dataMap.get("SMPP_DataCodeLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("SMPP_DataCodeLst", val);
			}
			
		}
	
		//Taking screenshot and Logging out
		cu.getScreenShot("Creation of New Customer");
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
		  extent = ExtReport.instance("CustomerProvisioning");	
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
			  LOGGER.info("App Logout failed () :: Exception: " +e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
		
}
