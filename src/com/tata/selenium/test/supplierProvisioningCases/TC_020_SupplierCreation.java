package com.tata.selenium.test.supplierProvisioningCases;

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
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_020_SupplierCreation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_020_SupplierCreation.class.getName());
	String properties = OBJECT_REPO_FILEPATH;
	ExcelUtils excelUtils = new ExcelUtils();

	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test ;
	private ExtentReports extent;

	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_02_SupplierCreation  -with TestdataId: "+uniqueDataId);
		String sheetName="Supplier_Provisioning_Screen";

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

		String parentWindow = cu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		cu.clickElement("supplier_NewBtn");

		cu.newWindowHandles(parentWindow);

		//Selecting required values from drop down based on input
//		cu.SelectDropDownByVisibleText("nws_supplier_name", dataMap.get("Supplier_Name"));
		cu.SelectDropDownByVisibleTextCustomMMX("nws_supplier_name", "nws_Supplier_Name_DropDown_SearchTextbox", "nws_Supplier_Name_DropDown_Dynamic_LabelOption"
				, "$suppliername$", dataMap.get("Supplier_Name"));
		
		cu.selectDropDownByVisibleText("nws_supplier_Category", dataMap.get("Supplier_Category"));
		cu.selectDropDownByVisibleText("nws_supplier_Currency", dataMap.get("Supplier_Currency"));
		cu.selectDropDownByVisibleText("nws_supplier_Service", dataMap.get("Service"));
		cu.selectDropDownByVisibleText("nws_supplier_connectivityType", dataMap.get("Connectivity_Type"));
		cu.selectDropDownByVisibleText("nws_supplier_Security", dataMap.get("Security"));
		//Entering data in text field
		cu.setData("nws_supplier_Acc_name" , dataMap.get("Supplier_Account_Name"));
		cu.clickElement("nws_CreateBtn");		


		//Checking if any error occured in creating new Supplier
		//cu.checkPopUp("application_PopUpTitle", "Creation of new Supplier");

		cu.switchToWindow(parentWindow);
		cu.waitForPageLoad("");
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		System.out.println(cu.getText("Supplier_Name_DropDown_Button"));
		System.out.println(cu.currentSelectedVal("Instance"));
		System.out.println(dataMap.get("Supplier_Account_Name"));
		
		Thread.sleep(2000);
		if(cu.getText("Supplier_Name_DropDown_Button").equalsIgnoreCase(dataMap.get("Supplier_Name"))
				&& ("ALL").equalsIgnoreCase(cu.currentSelectedVal("Instance"))){
			Thread.sleep(2000);
			test.log(LogStatus.PASS, "EXPECTED:: All values should be reflected in Input parameter", "Usage: <span style='font-weight:bold;'>ACTUAL:: All values reflected in Input parameter sucessfully</span>");
			cu.printLogs("All values reflected in Input parameter sucessfully");
			excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
		}
		else{
			test.log(LogStatus.FAIL, "EXPECTED:: All values should be reflected in Input parameter", "Usage: <span style='font-weight:bold;'>ACTUAL:: All values did not get reflected in Input parameter</span>");
			cu.printLogs("All values did not get reflected in Input parameter");
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
		}

		//String parentWindow = hu.getCurrWindowName();
		//CommonUtils.printConsole("parentWindow   "+parentWindow);
		//cu.clickElement("Supplier_CustomError");
		
		
		//cu.newWindowHandles(parentWindow);
		//Thread.sleep(500);
		
		//cu.SelectDropDownByVisibleText("supplierName_List", dataMap.get("Supplier_Name"));
		//System.out.println(dataMap.get("Supplier_Name"));
		//cu.waitForPageLoad("");
		
	//	cu.waitForPageLoad("");
		//Thread.sleep(200);

	//	String value = cu.getAttribute("supplierID_txt","value");
	//	System.out.println(value);
		//Thread.sleep(2000);
		//cu.clickElement("supplier_DisplayBtn");

		//if(hu.checkElementPresenc=e("customErrorList_TableElements"))
		//Thread.sleep(1000);


		//cu.checkMessage("application_PopUpMessage", "click submit button", "Error: No records found for the selected supplier.");
		//cu.checkElementPresence("application_PopUpMessage");
		//cu.clickElement("application_PopUpMessage_ok");
		//Thread.sleep(1000);
		
		/*cu.switchToWindow(parentWindow);
		cu.waitForPageLoad("");
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");*/
		//Taking screenshot and Logging out
		cu.getScreenShot("Creation of New Supplier");
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
		extent = ExtReport.instance("SupplierProvisioning");	
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
