package com.tata.selenium.test.supplierProvisioningCases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;
import com.tata.selenium.utils.PropertyUtility;


public class TC_14_HTTPTabNegativeValidation implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_14_HTTPTabNegativeValidation.class.getName());
	private static final String String = null;
	String properties = OBJECT_REPO_FILEPATH;
	ExcelUtils excelUtils = new ExcelUtils();
	Map<String, String> dataMap = new HashMap<>();
	protected WebDriver driver;
	private ExtentTest test ;
	private ExtentReports extent;
	PropertyUtility putility;
	
	
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_12_TrafficTabValidation -with TestdataId: "+uniqueDataId);
		String sheetName="Supplier_Provisioning_Screen";
		String Traffic_ThrottlingTxt=null;
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
		
//		cu.SelectDropDownByVisibleText("Supplier_Name", dataMap.get("Supplier_Name"));		
		cu.SelectDropDownByVisibleTextCustomMMX("Supplier_Name_DropDown_Button", "Supplier_Name_DropDown_SearchTextbox", "Supplier_Name_DropDown_Dynamic_LabelOPtion"
				, "$suppliername$", dataMap.get("Supplier_Name"));
		
//		cu.SelectDropDownByVisibleText("Supplier_Account_Name" , dataMap.get("Supplier_Account_Name"));
		cu.SelectDropDownByVisibleTextCustomMMX("Supplier_Account_Name_DropDown_Button", "Supplier_Account_Name_DropDown_SearchTextbox", "Supplier_Account_Name_DropDown_Dynamic_LabelOPtion"
				, "$supplieraccountname$", dataMap.get("Supplier_Account_Name"));
		
         System.out.println(dataMap.get("Supplier_Name"));
         System.out.println(dataMap.get("Supplier_Account_Name"));
         
		cu.clickElement("supplier_DisplayBtn");
		cu.waitForPageLoad("");
		
		cu.clickElement("supplier_EditBtn");
		cu.waitForPageLoad("");
		

		//Checking Popup of value exceeding max limit
		cu.clickElement("HTTP_InfoTab");
		cu.sleep(1000);
		cu.clickElement("HTTP_General_Parameters_Tab");
		cu.sleep(1000);
		
		if(!dataMap.get("HTTP_InterfaceTxt").isEmpty() && dataMap.get("HTTP_InterfaceTxt").length()>64)
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_InterfaceTxt", dataMap.get("HTTP_InterfaceTxt"), "Error: HTTP Interface should have length between 1 to 64 characters.");
		
		if(!dataMap.get("HTTP_Max_Pending_RequestTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Max_Pending_RequestTxt", dataMap.get("HTTP_Max_Pending_RequestTxt"), "Error: Max Pending HTTP Request should be a unsigned numeric value with less than 32 bits.");

		if(!dataMap.get("HTTP_Max_SMS_Octet_LengthTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Max_SMS_Octet_LengthTxt", dataMap.get("HTTP_Max_SMS_Octet_LengthTxt"), "Error: Max SMS Octet Length should be a unsigned numeric value with less than 32 bits.");

		cu.clickElement("HTTP_Outgoing_Message_Parameters_Tab");
		cu.sleep(1000);
		
		if(!dataMap.get("HTTP_Sucess_Status_RegexTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Sucess_Status_RegexTxt", dataMap.get("HTTP_Sucess_Status_RegexTxt"), "Error: Success Status Regex should have length between 1 to 255 characters.");


		if(!dataMap.get("HTTP_Message_ID_RegexTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Message_ID_RegexTxt", dataMap.get("HTTP_Message_ID_RegexTxt"), "Error: Message ID Regex should have length between 1 to 255 characters.");

		if(!dataMap.get("HTTP_Permanent_Failure_Status_RegexTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Permanent_Failure_Status_RegexTxt", dataMap.get("HTTP_Permanent_Failure_Status_RegexTxt"), "Error: Permanent Failure Status Regex should have length between 1 to 255 characters.");

		if(!dataMap.get("HTTP_Send_URLTxt").isEmpty())
			fillHTTPFieldAndValiadateErrorMessage(cu, "HTTP_Send_URLTxt", dataMap.get("HTTP_Send_URLTxt"), "Error: Send-URL should have length between 1 to 255 characters.");

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();
	}
	
	
void fillHTTPFieldAndValiadateErrorMessage(CommonUtils cu, String fieldName, String newExitingMaxValue, String ExpectedMessage)
{
	String oldVal = cu.getAttribute(fieldName, "value");
	
	cu.setData(fieldName, newExitingMaxValue);
	cu.clickElement("supplier_SubmitBtn");
	cu.sleep(500);
	cu.checkMessage("application_PopUpTitle", "Popup valitaion of HTTP field "+fieldName+" for exceeding max value", ExpectedMessage);
	
	cu.setData(fieldName, oldVal);
	
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
