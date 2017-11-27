package com.tata.selenium.test.customerProvisioningCases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
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
import com.tata.selenium.test.supplierProvisioningCases.TC_11_InstanceSpecificParametersValidation;
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;

import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;
import com.tata.selenium.utils.PropertyUtility;

public class TC_09_TrafficSpecificParameterValidation implements ApplicationConstants {
	

	private static final Logger LOGGER = Logger.getLogger(TC_09_TrafficSpecificParameterValidation.class.getName());
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
		test = extent.startTest("Execution triggered for  - TC_09_TrafficSpecificParameterValidation  -with TestdataId: "+uniqueDataId);
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
		
		CommonUtils cu1=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		cu1.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);
		driver =cu1.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		cu1.waitForPageLoad("MessagingInstanceHomePage");
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu1.SwitchFrames("bottom");
		cu1.SwitchFrames("target");
		Thread.sleep(5000);
		cu1.SelectDropDownByVisibleText("Customer_Name", dataMap.get("Customer_Name"));
		Thread.sleep(5000);
		cu1.SelectDropDownByVisibleText("Customer_Account_Name" , dataMap.get("Customer_Account_Name"));
		Thread.sleep(5000);
		//cu1.clickElement("Customer_DisplayBtn");
		//cu1.waitForPageLoad("");
		System.out.println(dataMap.get("Customer_Name"));
		System.out.println(dataMap.get("Customer_Account_Name"));
		String Throttling=dataMap.get("Traffic_ThrottlingTxt");
		String OASupport=dataMap.get("Traffic_OASupportLst");
		String AllowedIP=dataMap.get("Traffic_AllowedIPTxt");
		String OABlack=dataMap.get("Traffic_OABlackListTxt");
		String Traffic_DeniedIP=dataMap.get("Traffic_DeniedIPTxt");
		String EnhancedDLR=dataMap.get("Enhanced_Dlr");
		String EnhancedDlrParameter=dataMap.get("Ehanced_Dlr_Parameter");
		
		//First validation: History Tab has 5 latest values or not.
		List<String> retStrOPs=cu1.getAllOptionsFromDropDown("Instance");
		
//		cu1.checkFromDropDowntrafficCustomer("Instance","Traffic_ThrottlingTxt","Traffic_OASupportLst","Traffic_AllowedIPTxt", 
//		    		"Traffic_OABlackListTxt","Traffic_DeniedIPTxt","Enhanced_Dlr","Ehanced_Dlr_Parameter",retStrOPs,Throttling,OASupport,AllowedIP,
//		    		OABlack,Traffic_DeniedIP,EnhancedDLR,EnhancedDlrParameter);
		
		
		
		cu1.clickElement("Customer_EditBtn");
	    cu1.waitForPageLoad("");
		
				
		
	    cu1.getScreenShot("ScreenShot of Traffic Information Section");	
		test = cu1.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu1.checkRunStatus();
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
			  LOGGER.info(" App Logout failed () :: Exception: " +e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
