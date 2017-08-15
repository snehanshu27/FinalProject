package com.tata.selenium.test.supplierProvisioningCases;

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


/**
 * @date 
 * @author Sonali Das
 * @description This class will perform required action in MMX application
 */

public class TC_01_SupplierProvisioningUIValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_SupplierProvisioningUIValidation.class.getName());
	
	String properties = OBJECT_REPO_FILEPATH;
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	
	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_01_SupplierProvisioningUIValidation  -with TestdataId: "+uniqueDataId);
		String sheetName="Supplier_Provisioning_Screen";
		
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
		/*cu.checkEditableDropDown("Supplier_Account_Name", dataMap.get("Supplier_Account_Name"));
		cu.checkEditableDropDown("Supplier_Name", dataMap.get("Supplier_Name"));
		*/
		
		cu.clickElement("Supplier_Name_DropDown_Button");
		/*cu.sendKeys("Supplier_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Name"), false);
		cu.sleep(1000);
		cu.clickElement("Supplier_Name_DropDown_Dynamic_LabelOPtion", "$suppliername$", dataMap.get("Supplier_Name"));*/
		
		cu.clickElement("Supplier_Account_Name_DropDown_Button");
		/*cu.sendKeys("Supplier_Account_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Account_Name"), false);
		cu.sleep(5000);
		cu.clickElement("Supplier_Account_Name_DropDown_Dynamic_LabelOPtion", "$supplieraccountname$", dataMap.get("Supplier_Account_Name"));
		*/
		
		cu.checkEditableDropDown("Instance", dataMap.get("Instance"));
		cu.checkEditableDropDown("History", dataMap.get("History"));
		cu.checkEditableDropDown("Account_Status", dataMap.get("Account_Status"));
		
		
		//Validating all non editable text box
		cu.checkNonEditableBox("Supplier_ID", dataMap.get("Supplier_ID"));
		cu.checkNonEditableBox("Supplier_Short_Name", dataMap.get("Supplier_Short_Name"));
		cu.checkNonEditableBox("Supplier_Category", dataMap.get("Supplier_Category"));
		cu.checkNonEditableBox("Supplier_Currency", dataMap.get("Supplier_Currency"));
		cu.checkNonEditableBox("User_ID", dataMap.get("User_ID"));
		cu.checkNonEditableBox("Supplier_Account_ID", dataMap.get("Supplier_Account_ID"));
		cu.checkNonEditableBox("Connectivity_Type", dataMap.get("Connectivity_Type"));
		cu.checkNonEditableBox("Security", dataMap.get("Security"));
		cu.checkNonEditableBox("Service", dataMap.get("Service"));
	
		
		//Validating all buttons
		cu.checkElementPresence("supplier_NewBtn");
		cu.checkElementPresence("supplier_DisplayBtn");
		cu.checkElementPresence("supplier_EditBtn");
		cu.checkElementPresence("supplier_SaveBtn");
		cu.checkElementPresence("supplier_SubmitBtn");
		cu.checkElementPresence("supplier_ClearBtn");
		
		//Validating Instance Info Tab fields
		cu.clickElement("Instance_InfoTab");
		cu.checkNonEditableDropDown("Ins_Instance_StateLst");
		cu.checkNonEditableBox("Ins_Sms_FireWallChk");
		cu.checkNonEditableBox("Ins_commentsTxt");
		
		//Validating Traffic Info Tab fields
		cu.clickElement("Traffic_InfoTab");
		cu.checkNonEditableBox("Traffic_ThrottlingTxt");
		cu.checkNonEditableBox("Traffic_OAWhiteTxt");
		cu.checkNonEditableBox("Traffic_DAWhiteTxt");
		cu.checkNonEditableBox("Traffic_OAPoolTxt");
		cu.checkNonEditableBox("Traffic_OAListTxt");
		cu.checkNonEditableDropDown("Traffic_OASupportLst");
		cu.checkNonEditableBox("Traffic_OABlackListTxt");
		cu.checkNonEditableBox("Traffic_DABlackListTxt");
		
		//Validating SMPP Info Tab fields
		cu.clickElement("SMPP_InfoTab");
		cu.checkNonEditableDropDown("SMPP_VersionLst");
		cu.checkNonEditableBox("SMPP_BindAdderTonTxt");
		cu.checkNonEditableBox("SMPP_BindAdderNpiTxt");
		cu.checkNonEditableDropDown("SMPP_Dlr_SupportLst");
		cu.checkNonEditableDropDown("SMPP_MsgIDTypeLst");
		cu.checkNonEditableDropDown("SMPP_MsgLengthLst");
		cu.checkNonEditableBox("SMPP_WindowSizeTxt");
		cu.checkNonEditableBox("SMPP_SysIdTxt");
		cu.checkNonEditableBox("SMPP_PasswordTxt");
		cu.checkNonEditableBox("SMPP_SystemTypetxt");
		cu.checkNonEditableBox("SMPP_PortTRXTxt");
		cu.checkNonEditableBox("SMPP_PortRXTxt");
		cu.checkNonEditableBox("SMPP_MaxFragmentationTxt");
		cu.checkNonEditableBox("SMPP_OATonTxt");
		cu.checkNonEditableBox("SMPP_OANpiTxt");
		cu.checkNonEditableBox("SMPP_DATonTxt");
		cu.checkNonEditableBox("SMPP_DANpiTxt");
		cu.checkNonEditableDropDown("SMPP_MsgLengthLst");
		cu.checkNonEditableDropDown("SMPP_MsgLengthLst");
		cu.checkNonEditableBox("SMPP_EnqLinkTimerTxt");
		cu.checkNonEditableBox("SMPP_InactivityTimerTxt");
		cu.checkNonEditableBox("SMPP_ResponceTimerTxt");
		cu.checkNonEditableBox("SMPP_DelayTimerTxt");
		cu.checkNonEditableBox("SMPP_HostIPTxt");
		cu.checkNonEditableDropDown("SMPP_DataCodeLst");
		
		//Validating SS7 Info Tab fields
		cu.clickElement("SS7_InfoTab");
		cu.checkNonEditableBox("SS7_CallingPtyTxt");
		cu.checkNonEditableBox("SS7_CalledPtyTxt");
		cu.checkNonEditableBox("SS7_OA_TONTxt");
		cu.checkNonEditableBox("SS7_OANpiTxt");
		cu.checkNonEditableBox("SS7_DA_TONTxt");
		cu.checkNonEditableBox("SS7_DANpiTxt");
		cu.checkNonEditableDropDown("SS7_SM_RP_PRI_FlagLst");
		cu.checkNonEditableBox("SS7_MaxFragmentationTxt");
		cu.checkNonEditableDropDown("SS7_MT_DCSSupportLst");
		cu.checkNonEditableBox("SS7_Throttling");
		
		//Validating Number Management Tab fields
		cu.clickElement("NumberMgt_InfoTab");
		cu.checkNonEditableBox("NumberMgt_TON_SC_Chk");
		cu.checkNonEditableBox("NumberMgt_TON_LN_Chk");
		cu.checkNonEditableDropDown("NumberMgt_NumbersLst");
		cu.checkNonEditableDropDown("NumberMgt_AssignedNumbersLst");
	
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Provisioning Screen");		
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
