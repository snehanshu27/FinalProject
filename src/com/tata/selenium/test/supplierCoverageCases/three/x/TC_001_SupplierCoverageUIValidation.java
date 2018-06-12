package com.tata.selenium.test.supplierCoverageCases.three.x;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_001_SupplierCoverageUIValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_001_SupplierCoverageUIValidation.class.getName());
	String properties = "./data/SupplierCoverageObjects3x.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	private CommonUtils cu;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_001_SupplierCoverageUIValidation -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_001_SupplierCoverageUIValidation -with TestdataId: " + uniqueDataId);
		String sheetName="Supplier_Coverage_Screen3.x";
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
		
		cu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		cu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);		
		driver =cu.LaunchUrl(dataMap.get("URL"));
		
//		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
//		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
//		hu.waitForPageLoad("MessagingInstanceHomePage");
//		
//		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
//		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
//		
//		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
//		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
//		hu.SwitchFrames("bottom");
//		hu.SwitchFrames("target");
		
		cu.waitForPageLoadWithSleep("SupplierCoveragePage", 20);
		cu.waitForElementVisiblity("SupplierCoverage_CancelBtn", 180);
		cu.waitForElementInvisiblity("SupplierCoveragePageLoad", 60);
	
		//Validating all editable drop down
		if("MT".equalsIgnoreCase(dataMap.get("Service")))
			cu.checkEditableBox("SupplierCoverage_ServiceListTextBox", dataMap.get("Service"));
		else
		{	
			cu.selectDropDownByVisibleTextCustomMMX3("SupplierCoverage_ServiceListToggleDiv","SupplierCoverage_ServiceListDynamicOption", "$optionvalue$", dataMap.get("Service"));
			cu.clickElement("SupplierCoveragePage");
			cu.waitUntilElemetDisappearsMMX3("SupplierCoveragePageLoad");
		}
		cu.checkEditableBoxVerifyPlaceholderAttribute("SupplierCoverage_SupplierNameListTextBox", dataMap.get("Supplier_Name"));
		cu.checkEditableBoxVerifyPlaceholderAttribute("SupplierCoverage_SupplierAccountNameListTextBox", dataMap.get("Supplier_Account_Name"));
		cu.checkEditableBoxVerifyPlaceholderAttribute("SupplierCoverage_CoverageHistoryListTextBox", dataMap.get("Coverage_History"));
	
		
		//Validating all buttons		
		cu.checkElementPresence("SupplierCoverage_DisplayBtn");
		cu.checkElementPresence("SupplierCoverage_CancelBtn");
		
		if("MT".equalsIgnoreCase(dataMap.get("Service")))
		{
			cu.checkElementPresence("SupplierCoverage_UploadBtn");
			cu.checkElementPresence("SupplierCoverage_SubmitBtn");
		}
		else
		{
			cu.checkElementNotPresence("SupplierCoverage_UploadBtn");
			cu.checkElementNotPresence("SupplierCoverage_SubmitBtn");
		}
		
		
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Coverage Screen");		
		test = cu.getExTest();
//		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();
		

	}
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("SupplierCoverage");			  
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
		  	  LOGGER.info(" App Logout failed () :: Exception: "+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
