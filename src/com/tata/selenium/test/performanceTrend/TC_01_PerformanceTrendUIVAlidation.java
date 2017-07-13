package com.tata.selenium.test.performanceTrend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


public class TC_01_PerformanceTrendUIVAlidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_PerformanceTrendUIVAlidation.class.getName());
	String properties =  "./data/PerformanceTrend.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_01_PerformanceTrendUIVAlidation - "+uniqueDataId);
		String sheetName="Performance_Trend";
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
		cu.default_content();
		cu.SwitchFrames("//iframe[@name='bottom']");
		cu.SwitchFrames("index");
		driver.findElement(By.xpath("//*[text()='Dashboard']/..//following-sibling::ul//font[text()='"+dataMap.get("Navigation")+"']")).click();
		cu.waitForPageLoad("Performance Trend");
		cu.default_content();
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		if(cu.existsElement("application_PopUpOkBtn"))
			cu.clickElement("application_PopUpOkBtn");
		
		//Validating all fields
		cu.SelectDropDownByVisibleText("ServiceLst", dataMap.get("ServiceLst"));
		cu.waitForPageLoad("");
		cu.checkEditableDropDown("ServiceLst",dataMap.get("ServiceLst"));
		cu.checkEditableDropDown("ProductLst",dataMap.get("ProductLst"));
		cu.checkEditableDropDown("CountryLst",dataMap.get("CountryLst"));
		cu.checkEditableDropDown("CustomerLst",dataMap.get("CustomerLst"));
		cu.checkEditableDropDown("SupplierLst",dataMap.get("SupplierLst"));
		if("MT SMS".equalsIgnoreCase(dataMap.get("ServiceLst")))
			cu.checkEditableDropDown("DestinationLst",dataMap.get("DestinationLst"));
		
		cu.checkEditableDropDown("CustomerAccountNameLst",dataMap.get("CustomerAccountNameLst"));
		cu.checkEditableDropDown("SupplierAccountNameLst",dataMap.get("SupplierAccountNameLst"));
		cu.checkElementPresence("DisplayBtn");
		cu.checkElementPresence("CancelBtn");
		
		//Validating from date
		WebElement fromdate = cu.returnElement("FromDate");
		String strfromdate=fromdate.getAttribute("value");
		String todayAsString = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
		if(strfromdate.contains(todayAsString)){
			test.log(LogStatus.PASS, "From date displayed should be Todays date","From date displayed is "+strfromdate);
		}else{
			test.log(LogStatus.FAIL, "From date displayed should be Todays date","From date displayed is "+strfromdate);
		}
		//Validating to date
		WebElement toDate = cu.returnElement("FromDate");
		String strToDate=toDate.getAttribute("value");
		if(strToDate.contains(todayAsString)){
			test.log(LogStatus.PASS, "From date displayed should be Todays date","From date displayed is "+strToDate);
		}else{
			test.log(LogStatus.FAIL, "From date displayed should be Todays date","From date displayed is "+strToDate);
		}
		
		cu.getScreenShot("Validation Of UI elements in Performance Trend");
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
		  extent = ExtReport.instance("PerformanceTrend");
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
			  LOGGER.info(" App Logout failed () :: Exception:"+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
