package com.tata.selenium.test.xmlComparison;

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

public class TC_001_CompareXMLForCustomErrorScreen implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_001_CompareXMLForCustomErrorScreen.class.getName());
	String properties = "./data/CustomError.properties";
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
	//	{
		test = extent.startTest("Execution triggered for - TC_001_CustomErrorPageUIValidation -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_001_CustomErrorPageUIValidation -with TestdataId: " + uniqueDataId);
		String sheetName="XML_Comparison";
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
		
		cu.SelectDropDownByVisibleText("Supplier_Name", dataMap.get("Supplier_Name"));
		cu.SelectDropDownByVisibleText("Supplier_Account_Name" , dataMap.get("Supplier_Account_Name"));
		cu.clickElement("supplier_DisplayBtn");
		cu.waitForPageLoad("");	 //Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Coverage Screen");		
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		
		
		
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();
				}
//		else
//		{
//			test.log(LogStatus.FAIL, "DB Check for this database is failed", "Usage: <span style='font-weight:bold;'>Since DB check is failed,Stopping the exceution with UI here</span>");
//		}
	

	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("SupplierCoverage");		
		 
		
		
//		customErrorScreen.DO("TC_01", "Data_01");
	  }	
	
	  
	  
	  public String checkDataBase() throws Exception
	  {
		  String sheetName1="Consolidated_Result";
		  ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DB_DATA_FILEPATH,sheetName1);
			dataMap = excel.getSheetData("Total", sheetName1);
			String result = dataMap.get("Result_Status");
			System.out.println("result== "+result);
//			Driver dScript = new Driver();
		//	 TC_DBTest_CustomErrorScreen  customErrorScreen = new TC_DBTest_CustomErrorScreen();
	//		 DB_Result =  customErrorScreen.result;
			//dScript
			 testResult = DB_Result.toString();
			 System.out.println("DB_Result== " + DB_Result.toString());
			return testResult;
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
