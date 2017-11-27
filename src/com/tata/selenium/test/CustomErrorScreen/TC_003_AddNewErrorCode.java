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

public class TC_003_AddNewErrorCode implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_003_AddNewErrorCode.class.getName());
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
		test = extent.startTest("Execution triggered for - TC_003_AddNewErrorCode -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_003_AddNewErrorCode -with TestdataId: " + uniqueDataId);
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
		
		hu.SelectDropDownByVisibleTextCustomMMX("supplierName_List_Button", "supplierName_List_SearchTextbox", "supplierName_List_Dynamic_LabelOPtion", dataMap.get("Supplier_Name"));
		System.out.println(dataMap.get("Supplier_Name"));
		hu.waitForPageLoad("");

		
		String value = hu.getAttribute("supplierID_txt","value");
		System.out.println(value);
		Thread.sleep(2000);
		hu.checkElementNotclicked("supplier_SubmitBtn");
		hu.clickElement("supplier_DisplayBtn");

		if(hu.elementDisplayed("application_PopUpMessage", 3))		
			hu.checkMessage("application_PopUpMessage", "click display button. No record found", "Error: No records found for the selected supplier.");

		//click add row button
		hu.clickElement("customError_AddRowBtn");
		hu.sleep(1000);
		
		//Set error code
		hu.setData("customError_CodeTxt",  dataMap.get("ErrorCode"));		
		hu.clickElement("customError_PageTitle");
		hu.sleep(1000);
		
		//select Error Code Type
		hu.clickElement("customErrorCode_TypeTxt");
		hu.sleep(500);
		hu.clickElement("dynamicCustomErrorCode_TypeList", "$errorcodetype$", dataMap.get("TypeOfErrorCode"));
		
		//Set Description
		hu.setData("customErrorCode_DescTxt",  dataMap.get("Description"));
		hu.sleep(500);
		
		//click on save button
		hu.clickElement("customErrorCode_SaveBtn");		
		
		hu.getScreenShot("Add New Row without clicking submit button");
		
		//click Submit Button
		hu.clickElement("supplier_SubmitBtn");
		hu.sleep(500);
		
		//Check Popup message and confirm
		hu.checkMessage("application_PopUpMessage", "Confirm the submit action", "Warning: This action will submit the details and create an order. Do you want to Continue?");
		hu.sleep(500);
		hu.waitForPageLoad("");
		
		//Check success message and confirm
		hu.checkMessage("application_PopUpMessage", "Success message validation", "The custom error codes for this supplier has been successfully submited.");
		
		
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
	
}
