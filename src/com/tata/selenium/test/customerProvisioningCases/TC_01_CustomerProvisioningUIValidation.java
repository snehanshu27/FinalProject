package com.tata.selenium.test.customerProvisioningCases;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
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
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


public class TC_01_CustomerProvisioningUIValidation implements ApplicationConstants {

private static final Logger LOGGER = Logger.getLogger(TC_01_CustomerProvisioningUIValidation.class.getName());
	
	String properties = "./data/CustomerProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	
	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_01_CustomerProvisioningUIValidation  -with TestdataId: "+uniqueDataId);
		String sheetName="Customer_Provisioning_Screen";
		
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
	
		//Validating all editable drop down
		cu.checkEditableDropDown("Customer_Name", dataMap.get("Customer_Name"));
		cu.checkEditableDropDown("Customer_Account_Name", dataMap.get("Customer_Account_Name"));
		cu.checkEditableDropDown("Instance", dataMap.get("Instance"));
		cu.checkEditableDropDown("History", dataMap.get("History"));
		cu.checkEditableDropDown("Account_Status", dataMap.get("Account_Status"));
		//cu.checkEditableDropDown("Rate_Change_Notification_Period", dataMap.get("RateChangeNotification") );
		cu.checkEditableBox("Rate_Change_Notification_Period");
		
		//Validating all non editable text box
		cu.checkNonEditableBox("Customer_ID", dataMap.get("Customer_ID"));
		cu.checkNonEditableBox("Customer_Short_Name", dataMap.get("Customer_Short_Name"));
		cu.checkNonEditableBox("Customer_Category", dataMap.get("Customer_Category"));
		cu.checkNonEditableBox("Customer_Currency", dataMap.get("Customer_Currency"));
		cu.checkNonEditableBox("Connectivity_Type", dataMap.get("Connectivity_Type"));
		cu.checkNonEditableBox("Security", dataMap.get("Security"));
		cu.checkNonEditableBox("Customer_Account_ID", dataMap.get("Customer_Account_ID"));
		cu.checkNonEditableBox("Product_ID",dataMap.get("Product_ID"));
		cu.checkNonEditableBox("Product_Name",dataMap.get("Product_Name"));
		cu.checkNonEditableBox("Product_Service", dataMap.get("Service"));	
		cu.checkNonEditableBox("User_ID", dataMap.get("User_ID"));
	
		
		//Validating all buttons
		cu.checkElementPresence("Customer_NewBtn");
		cu.checkElementPresence("Customer_DisplayBtn");
		cu.checkElementPresence("Customer_EditBtn");
		cu.checkElementPresence("Customer_SaveBtn");
		cu.checkElementPresence("Customer_SubmitBtn");
		cu.checkElementPresence("Customer_ClearBtn");
		
		//Validating Instance Info Tab fields
		cu.clickElement("Instance_InfoTab");
		cu.checkNonEditableDropDown("Ins_Instance_StateLst");
		//	cu.checkNonEditableBox("Ins_Sms_FireWallChk");
		cu.checkNonEditableBox("Ins_commentsTxt");
		
		//Validating Traffic Info Tab fields
		cu.clickElement("Traffic_InfoTab");
		cu.checkNonEditableBox("Traffic_ThrottlingTxt");
		cu.checkNonEditableBox("Traffic_OAWhiteTxt");
		//cu.checkNonEditableBox("Traffic_DAShortCodeTxt");
		cu.checkNonEditableBox("Traffic_AllowedIPTxt");
		//cu.checkNonEditableBox("Traffic_OAListTxt");
		cu.checkNonEditableDropDown("Traffic_OASupportLst");
		cu.checkNonEditableBox("Traffic_OABlackListTxt");
		//cu.checkNonEditableBox("Traffic_OADCInfoTxt");
		cu.checkNonEditableBox("Traffic_DeniedIPTxt");
		cu.checkNonEditableBox("Enhanced_Dlr");
		cu.checkNonEditableBox("Ehanced_Dlr_Parameter");
		
		//Validating SMPP Info Tab fields
		cu.clickElement("SMPP_InfoTab");
		cu.checkNonEditableDropDown("SMPP_OATonTxt");
		cu.checkNonEditableBox("SMPP_OANpiTxt");
		cu.checkNonEditableBox("SMPP_DATonTxt");
		cu.checkNonEditableDropDown("SMPP_DANpiTxt");
		cu.checkNonEditableDropDown("Enhanced_DLR_Format");
		cu.checkNonEditableBox("SMPP_VersionLst");
		cu.checkNonEditableBox("SMPP_SMSC_DefaultLst");
		cu.checkNonEditableBox("SMPP_MsgIDTypeLst");
		cu.checkNonEditableBox("SMPP_MsgLengthLst");
		cu.checkNonEditableBox("SMPP_SysIdTxt");
		cu.checkNonEditableBox("SMPP_PasswordTxt");
		cu.checkNonEditableBox("SMPP_MaxConnectionsTxt");
		cu.checkNonEditableBox("SMPP_DataCodeLst");
	
		//Validating HTTP Info Tab fields
		cu.clickElement("HTTPS_InfoTab");
		cu.checkNonEditableBox("HTTPS_UserID");
		cu.checkNonEditableBox("HTTPS_Password");
		
		//Validating Number Management Tab fields
		cu.clickElement("NumberMgt_InfoTab");
		cu.checkNonEditableBox("NumberMgt_TON_SC_Chk");
		cu.checkNonEditableBox("NumberMgt_TON_LN_Chk");
		cu.checkNonEditableDropDown("NumberMgt_NumbersLst");
		cu.checkNonEditableDropDown("NumberMgt_AssignedNumbersLst");
	
	
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Customer Provisioning Screen");		
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
		  extent = ExtReport.instance("CustomerProvisioning");
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
			  LOGGER.info(" App Logout failed () :: Exception: "+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
	
}
