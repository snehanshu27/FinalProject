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

public class TC_009_CancelOrder implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_009_CancelOrder.class.getName());
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
		test = extent.startTest("Execution triggered for - TC_002_SupplierVerfication -with TestdataId: "+uniqueDataId);
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
		
		
		test.log(LogStatus.INFO, "Click on submit button before selecting any supplier", "Usage: <span style='font-weight:bold;'>submit button should not be clickable</span>");
		hu.checkElementNotclicked("supplier_SubmitBtn");
		if(hu.elementDisplayed("application_PopUpMessage")==true)
		{
		System.out.println(hu.getText("popUp_Message"));
		hu.clickElement("application_PopUpMessage_ok");
		//printLogs("application_PopUpMessage" + " Element is present and displayed in the page");
		test.log(LogStatus.PASS, "EXPECTED: Element " + "application_PopUpMessage" + " should be displayed",
				"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + "application_PopUpMessage"
						+ " is displayed</span>");
		}
		
		hu.selectDropDownByVisibleText("supplierName_List", dataMap.get("Supplier_Name"));
		System.out.println(dataMap.get("Supplier_Name"));
		hu.waitForPageLoad("");
		//Thread.sleep(200);
		
		String value = hu.getAttribute("supplierID_txt","value");
		System.out.println(value);
		hu.clickElement("supplier_DisplayBtn");
		Thread.sleep(2000);
		
		test.log(LogStatus.INFO, "Click on Cancel button ", "Usage: <span style='font-weight:bold;'>Click on Cancel button will return to home page</span>");
		hu.clickElement("supplier_CancelBtn");
		
		
		//if(hu.checkElementPresenc=e("customErrorList_TableElements"))
		Thread.sleep(2000);
	//	String str = hu.getCurrWindowName();
		//hu.newWindowHandles(str);
		//System.out.println(str);
		if(hu.elementDisplayed("application_PopUpMessage")==true)
		{
		System.out.println(hu.getText("popUp_Message"));
		hu.clickElement("application_PopUpMessage_yes");
		hu.waitForPageLoad("");
		//printLogs("application_PopUpMessage" + " Element is present and displayed in the page");
		test.log(LogStatus.PASS, "EXPECTED: Element " + "application_PopUpMessage" + " should be displayed",
				"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + "application_PopUpMessage"
						+ " is displayed</span>");
		}
		//hu.waitForElement("supplierID_txtEmpty");
		hu.waitForElementInvisiblity("supplierName_EmptyList",implicitWait);
		hu.waitForElementInvisiblity("supplierID_txtEmpty",implicitWait);
	//	hu.ConfirmAlert();
		//hu.checkMessage("application_PopUpMessage", "click submit button", "Error: No records found for the selected supplier.");
		/*Thread.sleep(1000);
		hu.clickElement("customError_AddRowBtn");
		hu.SetData("customError_CodeTxt",  dataMap.get("ErrorCode"));
		String xpath = "customErrorCode_TypeList" + dataMap.get("TypeOfErrorCode")+"']";
				//div[contains(@id,'easyui_combobox_i') and text() = 'Temporary (0)']
				//div[contains(@id,'easyui_combobox_i') and text() = '
		Thread.sleep(2000);
		hu.clickElement("customErrorCode_TypeTxt");
		Thread.sleep(2000);
		//	hu.clickElement("customErrorCode_TypeList","$destination$",dataMap.get("TypeOfErrorCode"));
		//	hu.SelectDropDownByVisibleText("customErrorCode_TypeList",dataMap.get("TypeOfErrorCode"));
		hu.clickElement("customErrorCode_TypeList");
		hu.enterData("customErrorCode_DescTxt",  dataMap.get("Description"));
		hu.checkElementPresence("customErrorDescription_MsgTxt");
		hu.checkElementNotclicked("customErrorCode_SaveBtn");		
		hu.clickElement("supplier_CancelBtn");
		hu.checkElementPresence("application_PopUpMessage");
		hu.clickElement("application_PopUpMessage_ok");*/
		//hu.checkMessage("application_PopUpText", "click submit button", "Warning: This action will discard any changes made to this screen. Do you want to Continue ?");
		//hu.checkMessage("application_PopUpMessage", "click submit button", "Warning: This action will submit the details and create an order. Do you want to Continue?");
		hu.waitForPageLoad("");
		
		hu.switchToWindow(parentWindow);
		hu.waitForPageLoad("");
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
	
		
		//Warning: This action will submit the details and create an order. Do you want to Continue?
		//The custom error codes for this supplier has been successfully submited.
		//Taking screenshot and Logging out
		hu.getScreenShot("Validation Of Coverage Screen");		
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
