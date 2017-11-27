package com.tata.selenium.test.performanceTrend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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



public class TC_02_DisplayingResults implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_DisplayingResults.class.getName());
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
		test = extent.startTest("Execution triggered for - TC_02_DisplayingResults - "+uniqueDataId);
		String sheetName="Performance_Trend";
		String popUpName=null;
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
	
		cu.selectMultipleVaFromDropDown("ProductLst", dataMap.get("ProductLst"));
		cu.selectMultipleVaFromDropDown("CountryLst",dataMap.get("CountryLst"));
		cu.selectMultipleVaFromDropDown("CustomerLst",dataMap.get("CustomerLst"));
		cu.selectMultipleVaFromDropDown("SupplierLst",dataMap.get("SupplierLst"));
		if("MT SMS".equalsIgnoreCase(dataMap.get("DestinationLst")))
			cu.selectMultipleVaFromDropDown("DestinationLst",dataMap.get("DestinationLst"));
		
		cu.selectMultipleVaFromDropDown("CustomerAccountNameLst",dataMap.get("CustomerAccountNameLst"));
		cu.selectMultipleVaFromDropDown("SupplierAccountNameLst",dataMap.get("SupplierAccountNameLst"));
		
		
		//Select From DATE
		  cu.moveAndClick("FromDate");
		  Thread.sleep(2000);
		  cu.moveAndClick("clickYear");
		  Thread.sleep(2000);
		  cu.calYear(dataMap.get("FromYear"));
		  Thread.sleep(2000);
		  cu.moveAndClick("selectMonth");
		  Thread.sleep(2000);
		  cu.calMonth(dataMap.get("FromMonth"));
		  Thread.sleep(2000);
		  cu.calDate(dataMap.get("FromDay"));
		  Thread.sleep(2000);
		  cu.clickElement("clickOutside");
		  
		//Select TO DATE
		  cu.moveAndClick("ToDate");
		  Thread.sleep(2000);
		  cu.moveAndClick("clickYear");
		  Thread.sleep(2000);
		  cu.calYear(dataMap.get("ToYear"));
		  
		  cu.moveAndClick("selectMonth_ToDate");
		  Thread.sleep(2000);
		  cu.calMonth(dataMap.get("ToMonth"));
		  Thread.sleep(2000);
		  cu.calDate(dataMap.get("ToDay"));
		  Thread.sleep(2000);
		
		/*//Validating from date
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
		}*/
				
		cu.clickElement("DisplayBtn");
		cu.waitForPageLoad("");
		cu.getScreenShot("Sucessfully clicked display btn after selecting the appropriate Service");
		//cu.clickElement("application_PopUpOkBtn");
		
		if(cu.existsElement("application_PopUpTitle"))
		{
		popUpName = cu.getText("application_PopUpMessage");
		cu.clickElement("application_PopUpOkBtn");
		cu.getScreenShot("Validation Of UI elements in Performance Trend");
		}else{
			cu.checkMessage("application_PopUpTitle", "Checking for any error when results are expected.", "No data for the selected input parameters");
		}
		
		
		//cu.getScreenShot("Validation Of UI elements in Performance Trend");
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
			  LOGGER.info(" App Logout failed () :: Exception:"+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
