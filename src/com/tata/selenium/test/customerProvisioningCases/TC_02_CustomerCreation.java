package com.tata.selenium.test.customerProvisioningCases;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
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
//import com.tata.selenium.test.supplierProvisioningCases.TC_02_SupplierCreation;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_02_CustomerCreation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_02_CustomerCreation.class.getName());
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
		test = extent.startTest("Execution triggered for  - TC_02_CustomerCreation  -with TestdataId: "+uniqueDataId);
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
		
		String parentWindow = cu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		cu.clickElement("Customer_NewBtn");
		
		cu.newWindowHandles(parentWindow);
		
		//Selecting required values from drop down based on input
		cu.SelectDropDownByVisibleText("nws_customer_name", dataMap.get("Customer_Name"));
		cu.SelectDropDownByVisibleText("nws_customer_Category", dataMap.get("Customer_Category"));
		cu.SelectDropDownByVisibleText("nws_customer_Currency", dataMap.get("Customer_Currency"));
		cu.SelectDropDownByVisibleText("nws_customer_Service", dataMap.get("Service"));
		cu.SelectDropDownByVisibleText("nws_customer_ProductName", dataMap.get("Product_Name"));
		cu.SelectDropDownByVisibleText("nws_customer_connectivityType", dataMap.get("Connectivity_Type"));
		cu.SelectDropDownByVisibleText("nws_customer_Security", dataMap.get("Security"));
		//Entering data in text field
		
		if(driver.findElement(By.xpath("//input[@id='custShortChild']")).isEnabled())
				{
		cu.SetData("nws_customer_ShortName" , dataMap.get("Customer_Short_Name"));
				}
		
		cu.SetData("nws_customer_Acc_name" , dataMap.get("Customer_Account_Name"));
	
		cu.clickElement("nws_CreateBtn");		

	
		//Checking if any error occured in creating new Supplier
		cu.check_Pop_Up("application_PopUpTitle", "Creation of new Customer");
		cu.switchToWindow(parentWindow);
		cu.waitForPageLoad("");
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
	
		
	/*	if(cu.currentSelectedVal("Customer_Account_Name").equalsIgnoreCase(dataMap.get("Customer_Account_Name"))){
				&& ("ALL").equalsIgnoreCase(cu.currentSelectedVal("Instance"))){
			test.log(LogStatus.PASS, "EXPECTED:: All values should be reflected in Input parameter", "Usage: <span style='font-weight:bold;'>ACTUAL:: All values reflected in Input parameter sucessfully</span>");
			cu.printLogs("All values reflected in Input parameter sucessfully");
			excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
		}
		else{
			test.log(LogStatus.FAIL, "EXPECTED:: All values should be reflected in Input parameter", "Usage: <span style='font-weight:bold;'>ACTUAL:: All values did not get reflected in Input parameter</span>");
			cu.printLogs("All values did not get reflected in Input parameter");
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
		}*/
		
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
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	

}
