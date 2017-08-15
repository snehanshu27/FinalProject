package com.tata.selenium.test.supplierProvisioningCases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


/**
 * @date 
 * @author Sonali Das
 * @description This class will perform action in MMX application
 */

public class TC_08_SupplierUICsvValidation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_08_SupplierUICsvValidation.class.getName());
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
		test = extent.startTest("Execution triggered for  - TC_08_SupplierUICsvValidation -with TestdataId: "+uniqueDataId);
		String sheetName="Supplier_Provisioning_Screen";
		String Traffic_ThrottlingTxt=null;
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
		
		cu.clickElement("supplier_DisplayBtn");
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
		
		cu.clickElement("supplier_EditBtn");
		cu.waitForPageLoad("");
		
		//Selecting values from main Tab
		cu.SelectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.SelectDropDownByVisibleText("History", dataMap.get("History"));
		cu.SelectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		
		
		String Supplier_ID=cu.getTxtBoxValue("Supplier_ID", dataMap.get("Supplier_ID"));
		String Supplier_Short_Name=cu.getTxtBoxValue("Supplier_Short_Name", dataMap.get("Supplier_Short_Name"));
		String Supplier_Category=cu.getTxtBoxValue("Supplier_Category", dataMap.get("Supplier_Category"));
		String Supplier_Currency=cu.getTxtBoxValue("Supplier_Currency", dataMap.get("Supplier_Currency"));
		String User_ID=cu.getTxtBoxValue("User_ID", dataMap.get("User_ID"));
		String Supplier_Account_ID=cu.getTxtBoxValue("Supplier_Account_ID", dataMap.get("Supplier_Account_ID"));
		String Connectivity_Type=cu.getTxtBoxValue("Connectivity_Type", dataMap.get("Connectivity_Type"));
		String Security=cu.getTxtBoxValue("Security", dataMap.get("Security"));
		String Service=cu.getTxtBoxValue("Service", dataMap.get("Service"));

		
		//Fetching the value from each field for INS Tab
		String Ins_Instance_StateLst=cu.getDropDownSelectedVal("Ins_Instance_StateLst", dataMap.get("Ins_Instance_StateLst"));
		String Ins_commentsTxt=cu.getTxtBoxValue("Ins_commentsTxt", dataMap.get("Ins_commentsTxt"));
		String Ins_Sms_FireWallChk=cu.getChkBoxStatus("Ins_Sms_FireWallChk", dataMap.get("Ins_Sms_FireWallChk"));	

		//Fetching details of Traffic_InformationTAB
		Thread.sleep(5000);
		cu.clickElement("Traffic_InfoTab");
		if(("SMPP").equalsIgnoreCase(dataMap.get("Connectivity_Type")))
		{
		Traffic_ThrottlingTxt=cu.getTxtBoxValue("Traffic_ThrottlingTxt", dataMap.get("Traffic_ThrottlingTxt"));
		}
		String Traffic_OAWhiteTxt1=cu.getTxtBoxValue("Traffic_OAWhiteTxt", dataMap.get("Traffic_OAWhiteTxt"));
		String Traffic_OAWhiteTxt=Traffic_OAWhiteTxt1.trim();
		String Traffic_DAWhiteTxt1=cu.getTxtBoxValue("Traffic_DAWhiteTxt", dataMap.get("Traffic_DAWhiteTxt"));
		String Traffic_DAWhiteTxt=Traffic_DAWhiteTxt1.trim();
		String Traffic_OAPoolTxt1=cu.getTxtBoxValue("Traffic_OAPoolTxt", dataMap.get("Traffic_OAPoolTxt"));
		String Traffic_OAPoolTxt=Traffic_OAPoolTxt1.trim();
		String Traffic_OAListTxt1=cu.getTxtBoxValue("Traffic_OAListTxt", dataMap.get("Traffic_OAListTxt"));
		String Traffic_OAListTxt=Traffic_OAListTxt1.trim();
		String Traffic_OABlackListTxt1=cu.getTxtBoxValue("Traffic_OABlackListTxt", dataMap.get("Traffic_OABlackListTxt"));
		String Traffic_OABlackListTxt=Traffic_OABlackListTxt1.trim();
		String Traffic_DABlackListTxt1=cu.getTxtBoxValue("Traffic_DABlackListTxt", dataMap.get("Traffic_DABlackListTxt"));
		String Traffic_DABlackListTxt=Traffic_DABlackListTxt1.trim();
		String Traffic_OASupportLst1=cu.getDropDownMultiSelectedVal("Traffic_OASupportLst", dataMap.get("Traffic_OASupportLst"));
		String Traffic_OASupportLst=Traffic_OASupportLst1.replace(",","");
		System.out.println(Traffic_OASupportLst);	
		//Fetching details of SMPP_InfoTab
		Thread.sleep(5000);
		cu.clickElement("SMPP_InfoTab");
		cu.waitForPageLoad("SMPP_InfoTab");
		Thread.sleep(50);
		String SMPP_VersionLst=cu.getDropDownSelectedVal("SMPP_VersionLst", dataMap.get("SMPP_VersionLst"));
		String SMPP_BindAdderTonTxt=cu.getTxtBoxValue("SMPP_BindAdderTonTxt", dataMap.get("SMPP_BindAdderTonTxt"));
		String SMPP_BindAdderNpiTxt=cu.getTxtBoxValue("SMPP_BindAdderNpiTxt", dataMap.get("SMPP_BindAdderNpiTxt"));
		String SMPP_Dlr_SupportLst=cu.getDropDownSelectedVal("SMPP_Dlr_SupportLst", dataMap.get("SMPP_Dlr_SupportLst"));
		String SMPP_MsgIDTypeLst=cu.getDropDownSelectedVal("SMPP_MsgIDTypeLst", dataMap.get("SMPP_MsgIDTypeLst"));
		String SMPP_MsgLengthLst=cu.getDropDownSelectedVal("SMPP_MsgLengthLst", dataMap.get("SMPP_MsgLengthLst"));
		String SMPP_WindowSizeTxt=cu.getTxtBoxValue("SMPP_WindowSizeTxt", dataMap.get("SMPP_WindowSizeTxt"));
		String SMPP_SysIdTxt=cu.getTxtBoxValue("SMPP_SysIdTxt", dataMap.get("SMPP_SysIdTxt"));
		String SMPP_PasswordTxt=cu.getTxtBoxValue("SMPP_PasswordTxt", dataMap.get("SMPP_PasswordTxt"));
		String SMPP_SystemTypetxt=cu.getTxtBoxValue("SMPP_SystemTypetxt", dataMap.get("SMPP_SystemTypetxt"));
		String SMPP_PortTRXTxt=cu.getTxtBoxValue("SMPP_PortTRXTxt", dataMap.get("SMPP_PortTRXTxt"));
		String SMPP_PortRXTxt=cu.getTxtBoxValue("SMPP_PortRXTxt", dataMap.get("SMPP_PortRXTxt"));
		String SMPP_MaxFragmentationTxt=cu.getTxtBoxValue("SMPP_MaxFragmentationTxt", dataMap.get("SMPP_MaxFragmentationTxt"));
		String SMPP_OATonTxt=cu.getTxtBoxValue("SMPP_OATonTxt", dataMap.get("SMPP_OATonTxt"));
		String SMPP_OANpiTxt=cu.getTxtBoxValue("SMPP_OANpiTxt", dataMap.get("SMPP_OANpiTxt"));
		String SMPP_DATonTxt=cu.getTxtBoxValue("SMPP_DATonTxt", dataMap.get("SMPP_DATonTxt"));
		String SMPP_DANpiTxt=cu.getTxtBoxValue("SMPP_DANpiTxt", dataMap.get("SMPP_DANpiTxt"));
		String SMPP_SMSC_DefaultLst=cu.getDropDownSelectedVal("SMPP_SMSC_DefaultLst", dataMap.get("SMPP_SMSC_DefaultLst"));
		String SMPP_SMSC_MsgModeLst=cu.getDropDownSelectedVal("SMPP_SMSC_MsgModeLst", dataMap.get("SMPP_SMSC_MsgModeLst"));
		String SMPP_EnqLinkTimerTxt=cu.getTxtBoxValue("SMPP_EnqLinkTimerTxt", dataMap.get("SMPP_EnqLinkTimerTxt"));
		String SMPP_InactivityTimerTxt=cu.getTxtBoxValue("SMPP_InactivityTimerTxt", dataMap.get("SMPP_InactivityTimerTxt"));
		String SMPP_ResponceTimerTxt=cu.getTxtBoxValue("SMPP_ResponceTimerTxt", dataMap.get("SMPP_ResponceTimerTxt"));
		String SMPP_DelayTimerTxt=cu.getTxtBoxValue("SMPP_DelayTimerTxt", dataMap.get("SMPP_DelayTimerTxt"));
		String SMPP_HostIPTxt=cu.getTxtBoxValue("SMPP_HostIPTxt", dataMap.get("SMPP_HostIPTxt"));
		String SMPP_DataCodeLst = cu.getDropDownAllVal("SMPP_DataCodeLst", dataMap.get("SMPP_DataCodeLst"));

		
		//Fetching details of SMPP_InfoTab
		Thread.sleep(5000);
		cu.clickElement("SS7_InfoTab");
		String SS7_CallingPtyTxt =cu.getTxtBoxValue("SS7_CallingPtyTxt", dataMap.get("SS7_CallingPtyTxt"));
		String SS7_CalledPtyTxt =cu.getTxtBoxValue("SS7_CalledPtyTxt", dataMap.get("SS7_CalledPtyTxt"));
		String SS7_OA_TONTxt =cu.getTxtBoxValue("SS7_OA_TONTxt", dataMap.get("SS7_OA_TONTxt"));
		String SS7_OANpiTxt =cu.getTxtBoxValue("SS7_OANpiTxt", dataMap.get("SS7_OANpiTxt"));
		String SS7_DA_TONTxt =cu.getTxtBoxValue("SS7_DA_TONTxt", dataMap.get("SS7_DA_TONTxt"));
		String SS7_DANpiTxt =cu.getTxtBoxValue("SS7_DANpiTxt", dataMap.get("SS7_DANpiTxt"));
		String SS7_SM_RP_PRI_FlagLst =cu.getDropDownSelectedVal("SS7_SM_RP_PRI_FlagLst", dataMap.get("SS7_SM_RP_PRI_FlagLst"));
		String SS7_MaxFragmentationTxt =cu.getTxtBoxValue("SS7_MaxFragmentationTxt", dataMap.get("SS7_MaxFragmentationTxt"));
		String SS7_MT_DCSSupportLst1=cu.getDropDownMultiSelectedVal("SS7_MT_DCSSupportLst", dataMap.get("SS7_MT_DCSSupportLst"));
		String SS7_MT_DCSSupportLst=SS7_MT_DCSSupportLst1.replace(",","");
		if(("SS7").equalsIgnoreCase(dataMap.get("Connectivity_Type")))
		{
		Traffic_ThrottlingTxt=cu.getTxtBoxValue("SS7_Throttling", dataMap.get("Traffic_ThrottlingTxt"));
		}
		//Fetching details of SMPP_InfoTab
		Thread.sleep(5000);
		cu.clickElement("NumberMgt_InfoTab");
		String NumberMgt_NumbersLst= cu.getDropDownMultiSelectedVal("NumberMgt_NumbersLst", dataMap.get("NumberMgt_NumbersLst"));
		String NumberMgt_AssignedNumbersLst = cu.getDropDownMultiSelectedVal("NumberMgt_AssignedNumbersLst", dataMap.get("NumberMgt_AssignedNumbersLst"));	
		
		//get csv data
		Thread.sleep(5000);
		Map<String, String> csvData = exportCSVAndGetCSVvalues(cu, dataMap.get("Instance"));
		
		compareDataAndLog("Supplier_ID", Supplier_ID, csvData.get("Supplier ID"));
		compareDataAndLog("Supplier_Short_Name", Supplier_Short_Name, csvData.get("Supplier Short Name"));
		compareDataAndLog("Supplier_Category", Supplier_Category, csvData.get("Supplier Category"));
		compareDataAndLog("Supplier_Currency", Supplier_Currency, csvData.get("Supplier Currency"));
		compareDataAndLog("User_ID", User_ID, csvData.get("User Id"));
		compareDataAndLog("Supplier_Account_ID", Supplier_Account_ID, csvData.get("Supplier Account ID"));
		compareDataAndLog("Connectivity_Type", Connectivity_Type, csvData.get("Connectivity Type"));
		compareDataAndLog("Security", Security, csvData.get("Security"));
		compareDataAndLog("Service", Service, csvData.get("Service"));
		
		
		compareDataAndLog("Ins_Instance_StateLst", Ins_Instance_StateLst, csvData.get("Instance State"));
		compareDataAndLog("Ins_commentsTxt", Ins_commentsTxt, csvData.get("Comments"));
		compareDataAndLog("Ins_Sms_FireWallChk", Ins_Sms_FireWallChk, csvData.get("SMS Firewall Required"));
		
		
		compareDataAndLog("Traffic_ThrottlingTxt", Traffic_ThrottlingTxt, csvData.get("Throttling (SMS/Second)"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_OAWhiteTxt", Traffic_OAWhiteTxt, csvData.get("OA (Sender) Whitelist").trim());
		int l1=Traffic_OAWhiteTxt.length();
		int l2=csvData.get("OA (Sender) Whitelist").trim().length();
		System.out.println(l1);
		System.out.println(l2);
		System.out.println("UI OAWhite"+Traffic_OAWhiteTxt);
		System.out.println("Export OAWhite"+csvData.get("OA (Sender) Whitelist"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_DAWhiteTxt", Traffic_DAWhiteTxt, csvData.get("DA (Receiver) Whitelist").trim());
		System.out.println("UI DAWhite"+Traffic_DAWhiteTxt);
		System.out.println("Export DAWhite"+csvData.get("DA (Receiver) Whitelist"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_OAPoolTxt", Traffic_OAPoolTxt, csvData.get("OA (Sender) Pool").trim());
		System.out.println("UI Traffic_OAPoolTxt"+Traffic_OAPoolTxt);
		System.out.println("Export Traffic_OAPoolTxt"+csvData.get("OA (Sender) Pool"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_OAListTxt", Traffic_OAListTxt, csvData.get("OA (Pass-Through) List").trim());
		System.out.println("UI Traffic_OAListTxt"+Traffic_OAListTxt);
		System.out.println("Export Traffic_OAListTxt"+csvData.get("OA (Pass-Through) List"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_OABlackListTxt", Traffic_OABlackListTxt, csvData.get("OA (Sender) Blacklist").trim());
		System.out.println("UI Traffic_OABlackListTxt"+Traffic_OABlackListTxt);
		System.out.println("Export Traffic_OABlackListTxt"+csvData.get("OA (Sender) Blacklist"));
		Thread.sleep(5000);
		compareDataAndLog1("Traffic_DABlackListTxt", Traffic_DABlackListTxt, csvData.get("DA (Receiver) Blacklist").trim());
		System.out.println("UI Traffic_DABlackListTxt"+Traffic_DABlackListTxt);
		System.out.println("Export Traffic_DABlackListTxt"+csvData.get("DA (Receiver) Blacklist"));
		Thread.sleep(5000);
		compareDataAndLog("Traffic_OASupportLst", Traffic_OASupportLst, csvData.get("OA (Sender-Type) Support"));
		System.out.println("UI OA"+Traffic_OASupportLst);
		System.out.println("Export OA"+csvData.get("OA (Sender-Type) Support"));
		Thread.sleep(5000);
		compareDataAndLog("SMPP_VersionLst", SMPP_VersionLst, csvData.get("SMPP Version"));
		System.out.println("UI version"+SMPP_VersionLst);
		System.out.println("Export version"+csvData.get("SMPP Version"));
		compareDataAndLog("SMPP_BindAdderTonTxt", SMPP_BindAdderTonTxt, csvData.get("Bind Addr TON"));
		compareDataAndLog("SMPP_BindAdderNpiTxt", SMPP_BindAdderNpiTxt, csvData.get("Bind Addr NPI"));
		Thread.sleep(50);
		compareDataAndLog("SMPP_Dlr_SupportLst", SMPP_Dlr_SupportLst, csvData.get("DLR Support"));
		System.out.println("UI DLR"+SMPP_Dlr_SupportLst);
		System.out.println("Export DLR"+csvData.get("DLR Support"));
		compareDataAndLog("SMPP_MsgIDTypeLst", SMPP_MsgIDTypeLst, csvData.get("Msg ID Type"));
		System.out.println("UI MSGID"+SMPP_MsgIDTypeLst);
		System.out.println("Export MSGID"+csvData.get("Msg ID Type"));
		compareDataAndLog("SMPP_MsgLengthLst", SMPP_MsgLengthLst, csvData.get("Msg ID Length"));
		System.out.println("UI MSGLN"+SMPP_MsgLengthLst);
		System.out.println("Export MSGLN"+csvData.get("Msg ID Length"));
		compareDataAndLog("SMPP_WindowSizeTxt", SMPP_WindowSizeTxt, csvData.get("Window Size"));
		compareDataAndLog("SMPP_SysIdTxt", SMPP_SysIdTxt, csvData.get("System ID"));
		compareDataAndLog("SMPP_PasswordTxt", SMPP_PasswordTxt, csvData.get("Password"));
		compareDataAndLog("SMPP_SystemTypetxt", SMPP_SystemTypetxt, csvData.get("System Type"));
		compareDataAndLog("SMPP_PortTRXTxt", SMPP_PortTRXTxt, csvData.get("Port (TRX,TX)"));
		compareDataAndLog("SMPP_PortRXTxt", SMPP_PortRXTxt, csvData.get("Port (RX)"));
		//compareDataAndLog("SMPP_MaxFragmentationTxt", SMPP_MaxFragmentationTxt, csvData.get("dummy"));
		compareDataAndLog("SMPP_OATonTxt", SMPP_OATonTxt, csvData.get("OA (Sender) TON"));
		compareDataAndLog("SMPP_OANpiTxt", SMPP_OANpiTxt, csvData.get("OA (Sender) NPI"));
		compareDataAndLog("SMPP_DATonTxt", SMPP_DATonTxt, csvData.get("DA (Reciever) TON"));
		compareDataAndLog("SMPP_DANpiTxt", SMPP_DANpiTxt, csvData.get("DA (Reciever) NPI"));
		compareDataAndLog("SMPP_SMSC_DefaultLst", SMPP_SMSC_DefaultLst, csvData.get("SMSC Default Alphabet"));
		System.out.println("UI SMSCDefault"+SMPP_SMSC_DefaultLst);
		System.out.println("Export SMSCDefault"+csvData.get("SMSC Default Alphabet"));
		compareDataAndLog("SMPP_SMSC_MsgModeLst", SMPP_SMSC_MsgModeLst, csvData.get("SMSC Msg Mode"));
		System.out.println("UI SMPP_SMSC_MsgModeLst"+SMPP_SMSC_MsgModeLst);
		System.out.println("Export SMPP_SMSC_MsgModeLst"+csvData.get("SMSC Msg Mode"));
		compareDataAndLog("SMPP_EnqLinkTimerTxt", SMPP_EnqLinkTimerTxt, csvData.get("Enquire Link Timer"));
		compareDataAndLog("SMPP_InactivityTimerTxt", SMPP_InactivityTimerTxt, csvData.get("Inactivity Timer"));
		compareDataAndLog("SMPP_ResponceTimerTxt", SMPP_ResponceTimerTxt, csvData.get("Response Timer"));
		compareDataAndLog("SMPP_DelayTimerTxt", SMPP_DelayTimerTxt, csvData.get("Reconnect Delay Timer"));
		compareDataAndLog("SMPP_HostIPTxt", SMPP_HostIPTxt, csvData.get("Host IP Address"));
		//compareDataAndLog("SMPP_DataCodeLst", SMPP_DataCodeLst, csvData.get("Data Coding Supported"));
		
		
		compareDataAndLog("SS7_CallingPtyTxt", SS7_CallingPtyTxt, csvData.get("Calling Pty GT"));
		compareDataAndLog("SS7_CalledPtyTxt", SS7_CalledPtyTxt, csvData.get("Called Pty Gt"));
		compareDataAndLog("SS7_OA_TONTxt", SS7_OA_TONTxt, csvData.get("OA (Sender) TON-SS7"));
		compareDataAndLog("SS7_OANpiTxt", SS7_OANpiTxt, csvData.get("OA (Sender) NPI-SS7"));
		compareDataAndLog("SS7_DA_TONTxt", SS7_DA_TONTxt, csvData.get("DA (Receiver) TON-SS7"));
		compareDataAndLog("SS7_DANpiTxt", SS7_DANpiTxt, csvData.get("DA (Receiver) NPI-SS7"));
		compareDataAndLog("SS7_SM_RP_PRI_FlagLst", SS7_SM_RP_PRI_FlagLst, csvData.get("SM-RP-PRI Flag"));
		//compareDataAndLog("SS7_MaxFragmentationTxt", SS7_MaxFragmentationTxt	, csvData.get("dummy"));
		Thread.sleep(500);
		compareDataAndLog("SS7_MT_DCSSupportLst", SS7_MT_DCSSupportLst, csvData.get("MT DCS Support"));
		System.out.println("UI SS7_MT_DCSSupportLst"+SS7_MT_DCSSupportLst);
		System.out.println("Export SS7_MT_DCSSupportLst"+csvData.get("MT DCS Support"));
		
		//compareDataAndLog("NumberMgt_NumbersLst", NumberMgt_NumbersLst, csvData.get("dummy"));
		//compareDataAndLog("NumberMgt_AssignedNumbersLst", NumberMgt_AssignedNumbersLst, csvData.get("Assigned Numbers"));
	
		
		
		
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
			  LOGGER.info(" App Logout failed () :: Exception: " +e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	  
	  
	  public Map<String, String> exportCSVAndGetCSVvalues(CommonUtils cu, String instanceName) throws InterruptedException {
			cu.deleteAllFilesInDownloadFolder();
			Thread.sleep(5000);
			cu.clickElement("ExportBtn");
			cu.waitForPageLoad("Supplier Provisioning");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();

			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			Map<String, String> csvDatamap = csvu.getData("Instance", instanceName);
			
			return trimMapKeys(csvDatamap);


		}
	  private void compareDataAndLog1(String fieldname, String expected, String actual)
	  {
		  if(expected != null){
		if(expected.equalsIgnoreCase(actual))		
			test.log(LogStatus.PASS, "EXPECTED: "+fieldname+" should be same "+expected+" in both csv and UI",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTED: "+fieldname+" is same as "+actual+" in both csv and UI</span>");
		else 
			test.log(LogStatus.FAIL, "EXPECTED: "+fieldname+" should be same "+expected+" in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTED: "+fieldname+" is not same as "+actual+" in both csv and UI</span>");
		  }
	  }

	  private void compareDataAndLog(String fieldname, String expected, String actual)
	  {
		  if(expected != null){
		if(expected.equals(actual))		
			test.log(LogStatus.PASS, "EXPECTED: "+fieldname+" should be same "+expected+" in both csv and UI",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTED: "+fieldname+" is same as "+actual+" in both csv and UI</span>");
		else 
			test.log(LogStatus.FAIL, "EXPECTED: "+fieldname+" should be same "+expected+" in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTED: "+fieldname+" is not same as "+actual+" in both csv and UI</span>");
		  }
	  }
	  
	  private void compareDataAndLogSet(String fieldname, Set<String> expected, String actual)
	  {
		  if(expected != null){
			  
			  Set<String> acutalset = arrayToSet(actual.split(","));
			  String expectedstr = "";
			  boolean ispassed = false;
			  if(expected.size() == acutalset.size())
			  {
				  int i =0;
				  for(String autalsetelem : acutalset)
				  {
					  if(expected.contains(autalsetelem))
						  ispassed = true;	
					  
					  
						if(i==0)					
							expectedstr = autalsetelem;						
						else
						{
							if(i==1)
								expectedstr = expectedstr+",";
							
							expectedstr = expectedstr+","+autalsetelem;
						}
				  }
			  }
			  
			  
			if(ispassed)		
				test.log(LogStatus.PASS, "EXPECTECD: "+fieldname+" should be same "+expectedstr+" in both csv and UI",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: "+fieldname+" is same as "+actual+" in both csv and UI</span>");
			else 
				test.log(LogStatus.FAIL, "EXPECTECD: "+fieldname+" should be same "+expectedstr+" in both csv and UI",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: "+fieldname+" is not same as "+actual+" in both csv and UI</span>");
			  }
	  }
	  
	  private Map<String, String> trimMapKeys(Map<String, String> mapdata)
	  {
		  Map<String, String> retMap = new HashMap<>();
		  
		  for(String key : mapdata.keySet())		  
			  retMap.put(key.trim(), mapdata.get(key));
			  
		  return retMap;
	  }
	  
	  private Set<String> arrayToSet(String[] arry)
	  {
		  Set<String> retset = new HashSet<>();
		  for(String arraele : arry)
			  retset.add(arraele);
		  
		  return retset;
	  }
}
