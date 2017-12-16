package com.tata.selenium.test.supplierProvisioningCases;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
import com.tata.selenium.pages.NavigationMenuPage;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_10_NewSupplierEditing implements ApplicationConstants{
	

	
	private static final Logger LOGGER = Logger.getLogger(TC_10_NewSupplierEditing.class.getName());
	String properties = OBJECT_REPO_FILEPATH;
	ExcelUtils excelUtils = new ExcelUtils();
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	private ExtentReports extent;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_07_SupplierEditing -with TestdataId: "+uniqueDataId);
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
		
		/*cu.SelectDropDownByVisibleText("Supplier_Name", dataMap.get("Supplier_Name"));
		cu.SelectDropDownByVisibleText("Supplier_Account_Name" , dataMap.get("Supplier_Account_Name"));*/
		
		cu.clickElement("Supplier_Name_DropDown_Button");
		cu.sendKeys("Supplier_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Name"), false);
		cu.sleep(1000);
		cu.clickElement("Supplier_Name_DropDown_Dynamic_LabelOPtion", "$suppliername$", dataMap.get("Supplier_Name"));
		
         System.out.println(dataMap.get("Supplier_Name"));
         System.out.println(dataMap.get("Supplier_Account_Name"));
         
		cu.clickElement("Supplier_Account_Name_DropDown_Button");
		cu.sendKeys("Supplier_Account_Name_DropDown_SearchTextbox", dataMap.get("Supplier_Account_Name"), false);
		cu.sleep(1000);
		cu.clickElement("Supplier_Account_Name_DropDown_Dynamic_LabelOPtion", "$supplieraccountname$", dataMap.get("Supplier_Account_Name"));
		//cu.clickElement("supplier_DisplayBtn");
		cu.waitForPageLoad("");
		
		//Validating Submit btn should be disabled by default before clicking Edit Btn
		
		if(cu.checkDisabledBtn("supplier_SubmitBtn")){
			cu.printLogs("Validation Failed: SubmitBtn is editable before clicking Edit Btn");
			test.log(LogStatus.FAIL, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is editable  before clicking Edit Btn</span>");
		}
		else{
			cu.printLogs("Validation Passed: SubmitBtn should be non editable before clicking Edit Btn");
			test.log(LogStatus.PASS, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is non-editable  before clicking Edit Btn</span>");
		}	
		
		Thread.sleep(5000);
		
		cu.clickElement("supplier_EditBtn");
		cu.waitForPageLoad("");
		
		//Selecting values from main Tab
		cu.SelectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.SelectDropDownByVisibleText("History", dataMap.get("History"));
		cu.SelectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		
		//Entering values in Instance Info Tab if data is present in test data
		if(("Y").equalsIgnoreCase(dataMap.get("Ins_TAB"))){
			cu.SelectDropDownByVisibleText("Ins_Instance_StateLst",dataMap.get("Ins_Instance_StateLst"));
			if(("Y").equalsIgnoreCase(dataMap.get("Ins_Sms_FireWallChk")))
				cu.clickElement("Ins_Sms_FireWallChk");
			
			cu.setData("Ins_commentsTxt", dataMap.get("Ins_commentsTxt"));
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("Traffic_InformationTAB"))){
			
			cu.clickElement("Traffic_InfoTab");

			if(("Y").equalsIgnoreCase(dataMap.get("SS7_TAB")))
					{
				Thread.sleep(5000);
			cu.checkNonEditableBox("Traffic_ThrottlingTxt");
					}
			else
			{
			cu.SetDataWithoutClearing("Traffic_ThrottlingTxt", dataMap.get("Traffic_ThrottlingTxt"));
			}
			cu.setData("Traffic_OAWhiteTxt", dataMap.get("Traffic_OAWhiteTxt"));
			cu.setData("Traffic_DAWhiteTxt", dataMap.get("Traffic_DAWhiteTxt"));
			cu.setData("Traffic_OAPoolTxt", dataMap.get("Traffic_OAPoolTxt"));
			cu.setData("Traffic_OAListTxt", dataMap.get("Traffic_OAListTxt"));
			//Checking for multiple option to get selected based on input
			if(dataMap.get("Traffic_OASupportLst").trim().length() >0){
				String[] data=dataMap.get("Traffic_OASupportLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("Traffic_OASupportLst", val);
			}
			cu.setData("Traffic_OABlackListTxt", dataMap.get("Traffic_OABlackListTxt"));
			cu.setData("Traffic_DABlackListTxt", dataMap.get("Traffic_DABlackListTxt"));
		}
		
		
		
		if(("Y").equalsIgnoreCase(dataMap.get("SMPP_InfoTab"))){
			
			Thread.sleep(5000);
			cu.clickElement("SMPP_InfoTab");
			cu.waitForPageLoad("SMPP_InfoTab");
			cu.SelectDropDownByVisibleText("SMPP_VersionLst", dataMap.get("SMPP_VersionLst"));
			cu.setData("SMPP_BindAdderTonTxt", dataMap.get("SMPP_BindAdderTonTxt"));
			cu.setData("SMPP_BindAdderNpiTxt", dataMap.get("SMPP_BindAdderNpiTxt"));
			cu.SelectDropDownByVisibleText("SMPP_Dlr_SupportLst", dataMap.get("SMPP_Dlr_SupportLst"));
			cu.SelectDropDownByVisibleText("SMPP_MsgIDTypeLst", dataMap.get("SMPP_MsgIDTypeLst"));
			cu.SelectDropDownByVisibleText("SMPP_MsgLengthLst", dataMap.get("SMPP_MsgLengthLst"));
			cu.setData("SMPP_WindowSizeTxt", dataMap.get("SMPP_WindowSizeTxt"));
			cu.SetDataWithoutClearing("SMPP_SysIdTxt", dataMap.get("SMPP_SysIdTxt"));
			cu.SetDataWithoutClearing("SMPP_PasswordTxt", dataMap.get("SMPP_PasswordTxt"));
			cu.setData("SMPP_SystemTypetxt", dataMap.get("SMPP_SystemTypetxt"));
			cu.setData("SMPP_PortTRXTxt", dataMap.get("SMPP_PortTRXTxt"));
			cu.setData("SMPP_PortRXTxt", dataMap.get("SMPP_PortRXTxt"));
			cu.setData("SMPP_MaxFragmentationTxt", dataMap.get("SMPP_MaxFragmentationTxt"));
			cu.setData("SMPP_OATonTxt", dataMap.get("SMPP_OATonTxt"));
			cu.setData("SMPP_OANpiTxt", dataMap.get("SMPP_OANpiTxt"));
			cu.setData("SMPP_DATonTxt", dataMap.get("SMPP_DATonTxt"));
			cu.setData("SMPP_DANpiTxt", dataMap.get("SMPP_DANpiTxt"));
			cu.SelectDropDownByVisibleText("SMPP_SMSC_DefaultLst", dataMap.get("SMPP_SMSC_DefaultLst"));
			cu.SelectDropDownByVisibleText("SMPP_SMSC_MsgModeLst", dataMap.get("SMPP_SMSC_MsgModeLst"));
			cu.setData("SMPP_EnqLinkTimerTxt", dataMap.get("SMPP_EnqLinkTimerTxt"));
			cu.setData("SMPP_InactivityTimerTxt", dataMap.get("SMPP_InactivityTimerTxt"));
			cu.setData("SMPP_ResponceTimerTxt", dataMap.get("SMPP_ResponceTimerTxt"));
			cu.setData("SMPP_DelayTimerTxt", dataMap.get("SMPP_DelayTimerTxt"));
			cu.SetDataWithoutClearing("SMPP_HostIPTxt", dataMap.get("SMPP_HostIPTxt"));
			//Handling multi select options
			if(dataMap.get("SMPP_DataCodeLst").trim().length() >0){
				String[] data=dataMap.get("SMPP_DataCodeLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("SMPP_DataCodeLst", val);
			}
			Thread.sleep(5000);
			cu.clickElement("SS7_InfoTab");
			Thread.sleep(5000);
			cu.checkNonEditableBox("SS7_Throttling");
			
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("SS7_TAB"))){
			cu.clickElement("SS7_InfoTab");
			cu.setData("SS7_CallingPtyTxt", dataMap.get("SS7_CallingPtyTxt"));
			cu.setData("SS7_CalledPtyTxt", dataMap.get("SS7_CalledPtyTxt"));
			cu.setData("SS7_OA_TONTxt", dataMap.get("SS7_OA_TONTxt"));
			cu.setData("SS7_OANpiTxt", dataMap.get("SS7_OANpiTxt"));
			cu.setData("SS7_DA_TONTxt", dataMap.get("SS7_DA_TONTxt"));
			cu.setData("SS7_DANpiTxt", dataMap.get("SS7_DANpiTxt"));
			cu.SelectDropDownByVisibleText("SS7_SM_RP_PRI_FlagLst", dataMap.get("SS7_SM_RP_PRI_FlagLst"));
			cu.setData("SS7_MaxFragmentationTxt", dataMap.get("SS7_MaxFragmentationTxt"));
			//Checking for multiple option to get selected based on input
			if(dataMap.get("SS7_MT_DCSSupportLst").trim().length() >0){
				String[] data=dataMap.get("SS7_MT_DCSSupportLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("SS7_MT_DCSSupportLst", val);
			}
			
			if(dataMap.get("Traffic_ThrottlingTxt")==""){
			
			WebElement Webelement =driver.findElement(By.id("ss7ThrottlCap"));
			System.out.println("In if block");
			Webelement.sendKeys(Keys.chord(Keys.CONTROL,"a"));
			Webelement.sendKeys(Keys.BACK_SPACE);
				
			}
			
			else
			{
				System.out.println("In else block");
			Thread.sleep(5000);
			cu.SetDataWithoutClearing("SS7_Throttling", dataMap.get("Traffic_ThrottlingTxt"));
		}
			
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("NumberManagement_TAB"))){
			cu.clickElement("NumberMgt_InfoTab");
			
			if(("Y").equalsIgnoreCase(dataMap.get("NumberMgt_TON_SC_Chk")))
				cu.selectCheckBox("NumberMgt_TON_SC_Chk");
			
			if(("Y").equalsIgnoreCase(dataMap.get("NumberMgt_TON_LN_Chk")))
				cu.selectCheckBox("NumberMgt_TON_LN_Chk");
			
			if(dataMap.get("NumberMgt_NumbersLst").trim().length() >0){
				String[] data=dataMap.get("NumberMgt_NumbersLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("NumberMgt_NumbersLst", val);
				
				cu.clickElement("NumberMgt_AllRightSelectBtn");
			}
			
			if(dataMap.get("NumberMgt_AssignedNumbersLst").trim().length() >0){
				String[] data=dataMap.get("NumberMgt_AssignedNumbersLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("NumberMgt_AssignedNumbersLst", val);
			}	
	
		}
		
		//First validating Save Btn which does not throw any error for wrong data
		/*cu.clickElement("supplier_SaveBtn");
		cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Save Btn", "This action will also save the details for other instances, but will not send the details to the platform. Do you want to Continue?");
		cu.waitForPageLoad("");
		cu.checkMessage("application_PopUpTitle", "Validating sucess Msg after submitting details", "The supplier provisioning information have been successfully entered");*/
		
		//Validating Submit Btn and pop up dislayed after clicking it
		cu.clickElement("supplier_SubmitBtn");
		cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Submit Btn", "Warning: This action will submit the details and create an order. Do you want to Continue?");
		
		cu.waitForPageLoad("");
		cu.checkMessage("application_PopUpTitle", "Validating sucess Msg after submitting details", "The supplier provisioning information have been successfully entered.");
		
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
