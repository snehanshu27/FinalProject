package com.tata.selenium.test.CustomErrorScreen;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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

public class TC_008_FiledValidationsInEditErrorCode implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_008_FiledValidationsInEditErrorCode.class.getName());
	String properties = "./data/CustomError.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws InterruptedException {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_008_FiledValidationsInEditErrorCode -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_002_SupplierVerfication -with TestdataId: " + uniqueDataId);
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
		
		
		test.log(LogStatus.INFO, "Launch Application", "Usage: <span style='font-weight:bold;'>Going to Launch App</span>");
		
		CommonUtils hu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		hu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);
		driver =hu.LaunchUrl(dataMap.get("URL"));
		hu.deleteCookies();
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
		hu.clickElement("Supplier_CustomError");
		
		
		hu.newWindowHandles(parentWindow);
		Thread.sleep(500);
		
		hu.selectDropDownByVisibleTextCustomMMX3("supplierName_List_Button", "supplierName_List_SearchTextbox", "supplierName_List_Dynamic_LabelOPtion", dataMap.get("Supplier_Name"));
		System.out.println(dataMap.get("Supplier_Name"));
		hu.waitForPageLoad("");

		
		String value = hu.getAttribute("supplierID_txt","value");
		System.out.println(value);
		Thread.sleep(2000);
		hu.checkElementNotclicked("supplier_SubmitBtn");
		hu.clickElement("supplier_DisplayBtn");

		if(hu.elementDisplayed("application_PopUpMessage", 3))		
			hu.checkMessage("application_PopUpMessage", "click display button. No record found", "Error: No records found for the selected supplier.");

		
		if(hu.elementDisplayed("dynamicCustomErrorCode_ColText", "$errorcode$", dataMap.get("ErrorCode")))
		{
			LOGGER.error("Unable to find a record/row with error code - "+dataMap.get("ErrorCode"));
			test.log(LogStatus.PASS, "Error code Record validation",
					"Record with error code - "+dataMap.get("ErrorCode")+" is displayed");
			
			//click edit button of a errorcode row
			hu.clickElement("dynamicCustomErrorCode_EditButton", "$errorcode$", dataMap.get("ErrorCode"));
			hu.sleep(1000);
			
			//1. Keep row empty (text fields), click save button and check
			clearTextFields(hu);
			fieldEmptyCheck(hu, "1. Empty Row cannot be saved", "Leave row empty and click save", "Unable to save empty row", "Empty row allowed to save");
			
			//2. Keep errorcode empty, click save button and check		
			fillFieldsAndClearExcluded(hu, "errorcode");
			fieldEmptyCheck(hu, "2. Row with empty ErrorCode cannot be saved", "Leave ErrorCode empty and click save", 
													"Unable to save Row with empty ErrorCode", "Row with empty ErrorCode allowed to save");
					
			//3. Keep error code description empty, click save button and check		
			fillFieldsAndClearExcluded(hu, "errorcodedesc");
			fieldEmptyCheck(hu, "3. Row with empty ErrorCode Description cannot be saved", "Leave ErrorCode Description empty and click save",
													"Unable to save Row with empty ErrorCode Description", "Row with empty ErrorCode Description allowed to save");
			
			//4. Row with ErrorCode Description exceed MAX length cannot be saved
			fillFieldsAndClearExcluded(hu, "errorcodedesc");
			long descMax = 100;
			String descStrExceedMax = hu.generateRadnomString(descMax+1);
			hu.setData("customErrorCode_DescTxt",  descStrExceedMax);		
			hu.clickElement("customError_PageTitle");
			hu.sleep(500);
			fieldEmptyCheck(hu, "4. Row with ErrorCode Description exceed MAX length cannot be saved", "Enter error code description more than Max ("+descMax+")",
					"Unable to save Row with ErrorCode Description exceed MAX length ("+descMax+").", "Row with ErrorCode Description exceed MAX length allowed to save. Max val='"+descMax+"'. Entered valLength='"+(descStrExceedMax+1)+"'");

			//5. Error code exceeds Max value will rounded to Max Value
			fillFieldsAndClearExcluded(hu, "errorcodetype");
			String errMax = "4294967295";
			String errExceedMax = "4294967296";
			hu.setData("customError_CodeTxt",  errMax);		
			hu.clickElement("customError_PageTitle");
			hu.sleep(500);
			String uiErrCodeTxt = hu.getAttribute("customError_CodeTxt", "value");
			
			passfailBasedOnCondition(hu, "5. Error code exceeds Max value will rounded to Max Value", "Enter error code more than Max ("+errMax+")", 
												"Error code rounded to Max Value  ("+errMax+") from "+errExceedMax, "Error code has not rounded to Max Value  ("+errMax+") from "+errExceedMax
													, errMax.equals(uiErrCodeTxt));
			
			//6. Duplicate error code not allowed
			if(!hu.elementDisplayed("customErrorCode_CancelBtn", 1))					
				hu.clickElement("customErrorCode_CancelBtn");		
					
			hu.sleep(500);
			fillFieldsAndClearExcluded(hu, "");
			hu.clickElement("customErrorCode_SaveBtn");	
			hu.sleep(500);
			
			hu.clickElement("customError_AddRowBtn");
			hu.sleep(500);
			fillFieldsAndClearExcluded(hu, "");
			hu.clickElement("customErrorCode_SaveBtn");	
			hu.sleep(500);
			
			//click Submit Button
			hu.clickElement("supplier_SubmitBtn");
			hu.sleep(500);
			
			//Check error message and confirm
			hu.checkMessage("application_PopUpMessage", "6. Duplicate Error message validation", "Error: There are duplicate Custom Error Codes in the list. Please verify.");
			
			
			
		}
		else
		{			
			LOGGER.error("Unable to find a record/row with error code - "+dataMap.get("ErrorCode"));
			test.log(LogStatus.FAIL, "Error code Edit valiadtion",
					"Unable to find a record/row with error code - "+dataMap.get("ErrorCode"));
		}
	
		hu.getScreenShot("Error code Edit valiadtion");
		hu.switchToWindow(parentWindow);
		hu.waitForPageLoad("");
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
	
				
		test = hu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		hu.checkRunStatus();
		

	}	
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("SupplierCoverage");			 
//		  Driver myDriver = new oracle.jdbc.driver.OracleDriver();
//		   DriverManager.registerDriver( myDriver );
		//step1 load the driver class  
//		  Class.forName("oracle.jdbc.driver.OracleDriver");  
//		    //jdbc:oracle:thin:@hostname:port Number:databaseName
//		  //step2 create  the connection object  
//		  Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1522:comtst","mmx","bonjour");
//		  System.out.println("connected");
//		    
//		  //step3 create the statement object  
//		  Statement stmt=con.createStatement();  
//		    
//		  //step4 execute query  
////		  ResultSet rs=stmt.executeQuery("select * from emp");  
////		  while(rs.next())  
////		  System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3));  
////		    
//		  //step5 close the connection object  
//		  con.close();  
//		    
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
	  
	  
		private void fillFieldsAndClearExcluded(CommonUtils hu, String exceptionalField)
		{
			if(!"errorcode".equalsIgnoreCase(exceptionalField))
			{
				//Set error code
				hu.setData("customError_CodeTxt",  dataMap.get("ErrorCode"));		
				hu.clickElement("customError_PageTitle");
				hu.sleep(500);
			}
			else
			{
				//Clear error code
				hu.clearData("customError_CodeTxt");	
				hu.clickElement("customError_PageTitle");
				hu.sleep(500);
			}
			
			if(!"errorcodetype".equalsIgnoreCase(exceptionalField))
			{
				//select Error Code Type
				hu.clickElement("customErrorCode_TypeTxt");
				hu.sleep(500);
				hu.clickElement("dynamicCustomErrorCode_TypeList", "$errorcodetype$", dataMap.get("TypeOfErrorCode"));
			}
			
			if(!"errorcodedesc".equalsIgnoreCase(exceptionalField))
			{
				//Set Description
				hu.setData("customErrorCode_DescTxt",  dataMap.get("Description"));
				hu.clickElement("customError_PageTitle");
				hu.sleep(500);
			}
			else
			{
				//Clear Description
				hu.clearData("customErrorCode_DescTxt");
				hu.clickElement("customError_PageTitle");
				hu.sleep(500);
			}
		}
		
		private void clearTextFields(CommonUtils hu)
		{
			//Clear error code
			hu.clearData("customError_CodeTxt");	
			hu.clickElement("customError_PageTitle");
			hu.sleep(500);
			
			//Clear Description
			hu.clearData("customErrorCode_DescTxt");
			hu.clickElement("customError_PageTitle");
			hu.sleep(500);
		}
		
		private void fieldEmptyCheck(CommonUtils hu, String validationTitle, String stepMsg, String passMsg, String failMsg)
		{
			hu.clickElement("customErrorCode_SaveBtn");	
			hu.sleep(500);
			passfailBasedOnCondition(hu, validationTitle, stepMsg, passMsg, failMsg, hu.elementDisplayed("customErrorCode_SaveBtn"));
				
		}
		
		private void passfailBasedOnCondition(CommonUtils hu, String validationTitle, String stepMsg, String passMsg, String failMsg, boolean pass)
		{
			LOGGER.info(validationTitle);
			test.log(LogStatus.INFO, validationTitle, stepMsg);
			if(pass)
			{
				LOGGER.info(passMsg);
				test.log(LogStatus.PASS, validationTitle, passMsg);
			}
			else
			{
				LOGGER.error(failMsg);
				test.log(LogStatus.PASS, validationTitle, failMsg);
			}
			hu.getScreenShot(validationTitle);
			
		}
	
}
