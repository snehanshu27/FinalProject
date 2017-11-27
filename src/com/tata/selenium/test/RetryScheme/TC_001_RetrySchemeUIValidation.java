package com.tata.selenium.test.RetryScheme;

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
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


/**
 * @date 
 * @author Meenakshi Chelliah
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_001_RetrySchemeUIValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_001_RetrySchemeUIValidation.class.getName());
	String properties = "./data/RetryScheme.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	public LogStatus DB_Result;
	private String testResult;
	
	@Test
	
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
	//	if(checkDataBase().equalsIgnoreCase("pass"))
		//{
		test = extent.startTest("Execution triggered for - TC_001_RetrySchemeUIValidation -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_001_RetrySchemeUIValidation -with TestdataId: " + uniqueDataId);
		String sheetName="Custom_Error_Screen";
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
		//DB_Result = checkDataBase();
		
		test.log(LogStatus.INFO, "Launch Application", "Usage: <span style='font-weight:bold;'>Going to Launch App</span>");
		
		CommonUtils hu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		hu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);
		driver =hu.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		hu.waitForPageLoad("MessagingInstanceHomePage");
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
		
		String parentWindow = hu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		hu.clickElement("retrySchemeBtn");
		
		
		hu.newWindowHandles(parentWindow);
		
		//Validating all editable drop down
	//	hu.checkEditableDropDown("supplierName_List", dataMap.get("Supplier_Name"));
		
		//Validating all non editable text box
		
	//	hu.checkNonEditableBox("supplierID_txt", dataMap.get("supplierID_txt"));
		
		//Validating all labels and Header text
		hu.checkElementPresence("supplierName_Label");
		hu.checkElementPresence("supplierAccName_Label");
		hu.checkElementPresence("supplierAccID_Label");
		hu.checkElementPresence("connectivityType_Label");
		hu.checkElementPresence("retrySchemeList_header");
		
		//Validating all buttons
		hu.checkElementPresence("displayBtn");
		hu.checkElementPresence("submitBtn");
		hu.checkElementPresence("cancelBtn");
		hu.checkElementPresence("addRowBtn");
		
		hu.switchToWindow(parentWindow);
		hu.waitForPageLoad("");
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
		 //Taking screenshot and Logging out
		hu.getScreenShot("Validation Of Coverage Screen");		
		test = hu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		hu.checkRunStatus();
				}
//		else
//		{
//			test.log(LogStatus.FAIL, "DB Check for this database is failed", "Usage: <span style='font-weight:bold;'>Since DB check is failed,Stopping the exceution with UI here</span>");
//		}
//	}
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("SupplierCoverage");		
		 
		
		
//		customErrorScreen.DO("TC_01", "Data_01");
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
			//  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
