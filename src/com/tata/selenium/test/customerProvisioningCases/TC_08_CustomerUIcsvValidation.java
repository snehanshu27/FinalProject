package com.tata.selenium.test.customerProvisioningCases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_08_CustomerUIcsvValidation implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_08_CustomerUIcsvValidation.class.getName());
	String properties = "./data/CustomerProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	private ExtentReports extent;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})		
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for  - TC_08_CustomerUIcsvValidation -with TestdataId: "+uniqueDataId);
		String sheetName="Customer_Provisioning_Screen";
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
		
		cu.selectDropDownByVisibleText("Customer_Name", dataMap.get("Customer_Name"));
		cu.selectDropDownByVisibleText("Customer_Account_Name" , dataMap.get("Customer_Account_Name"));
		cu.clickElement("Customer_DisplayBtn");
		cu.waitForPageLoad("");
		
		//Validating Submit btn should be disabled by default before clicking Edit Btn
		
		if(cu.isDisabledBtn("Customer_SubmitBtn")){
			cu.printLogs("Validation Failed: SubmitBtn is editable before clicking Edit Btn");
			test.log(LogStatus.FAIL, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is editable  before clicking Edit Btn</span>");
		}
		else{
			cu.printLogs("Validation Passed: SubmitBtn should be non editable before clicking Edit Btn");
			test.log(LogStatus.PASS, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is non-editable  before clicking Edit Btn</span>");
		}	
		
		cu.clickElement("Customer_SubmitBtn");
		cu.waitForPageLoad("");
		
		//Selecting values from main Tab
		cu.selectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.selectDropDownByVisibleText("History", dataMap.get("History"));
		cu.selectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		
		
		String Supplier_ID=cu.getTxtBoxValue("Customer_ID", dataMap.get("Customer_ID"));
		String Supplier_Short_Name=cu.getTxtBoxValue("Customer_Short_Name", dataMap.get("Customer_Account_Name"));
		String Supplier_Category=cu.getTxtBoxValue("Customer_Category", dataMap.get("Customer_Category"));
		String Supplier_Currency=cu.getTxtBoxValue("Customer_Currency", dataMap.get("Customer_Currency"));
		String User_ID=cu.getTxtBoxValue("User_ID", dataMap.get("User_ID"));
		//System.out.println(User_ID);
		String Supplier_Account_ID=cu.getTxtBoxValue("Customer_Account_ID", dataMap.get("Customer_Account_ID"));
		//System.out.println(Supplier_Account_ID);
		String Connectivity_Type=cu.getTxtBoxValue("Connectivity_Type", dataMap.get("Connectivity_Type"));
		String Security=cu.getTxtBoxValue("Security", dataMap.get("Security"));
		//System.out.println(Security);
		String Service=cu.getTxtBoxValue("Service", dataMap.get("Service"));

		
		//Fetching the value from each field for INS Tab
		String Ins_Instance_StateLst=cu.getDropDownSelectedVal("Ins_Instance_StateLst");
		String Ins_commentsTxt=cu.getTxtBoxValue("Ins_commentsTxt", dataMap.get("Ins_commentsTxt"));
		//String Ins_Sms_FireWallChk=cu.getChkBoxStatus("Ins_Sms_FireWallChk", dataMap.get("Ins_Sms_FireWallChk"));	

		//Fetching details of Traffic_InformationTAB
		cu.clickElement("Traffic_InfoTab");
		String Traffic_ThrottlingTxt=cu.getTxtBoxValue("Traffic_ThrottlingTxt", dataMap.get("Traffic_ThrottlingTxt"));
		String Traffic_OAWhiteTxt=cu.getTxtBoxValue("Traffic_OAWhiteTxt", dataMap.get("Traffic_OAWhiteTxt"));
		String Traffic_DAWhiteTxt=cu.getTxtBoxValue("Traffic_DAShortCodeTxt", dataMap.get("Traffic_DAShortCodeTxt"));
		String Traffic_OAPoolTxt=cu.getTxtBoxValue("Traffic_AllowedIPTxt", dataMap.get("Traffic_AllowedIPTxt"));
		String Traffic_OAListTxt=cu.getTxtBoxValue("Traffic_DeniedIPTxt", dataMap.get("Traffic_DeniedIPTxt"));
		String Traffic_OABlackListTxt=cu.getTxtBoxValue("Traffic_OABlackListTxt", dataMap.get("Traffic_OABlackListTxt"));
		String Traffic_DABlackListTxt=cu.getTxtBoxValue("Traffic_OADCInfoTxt", dataMap.get("Traffic_OADCInfoTxt"));
		String Traffic_OASupportLst=cu.getDropDownMultiSelectedVal("Traffic_OASupportLst", dataMap.get("Traffic_OASupportLst"));
		System.out.println(Traffic_OASupportLst);
			
		//Fetching details of SMPP_InfoTab
		cu.clickElement("SMPP_InfoTab");
		cu.waitForPageLoad("SMPP_InfoTab");
		String SMPP_VersionLst=cu.getDropDownSelectedVal("SMPP_VersionLst");
	    String SMPP_MsgIDTypeLst=cu.getDropDownSelectedVal("SMPP_MsgIDTypeLst");
		String SMPP_MsgLengthLst=cu.getDropDownSelectedVal("SMPP_MsgLengthLst");
		String SMPP_SysIdTxt=cu.getTxtBoxValue("SMPP_SysIdTxt", dataMap.get("SMPP_SysIdTxt"));
		String SMPP_PasswordTxt=cu.getTxtBoxValue("SMPP_PasswordTxt", dataMap.get("SMPP_PasswordTxt"));
		String SMPP_MaxConnectionsTxt=cu.getTxtBoxValue("SMPP_MaxConnectionsTxt", dataMap.get("SMPP_MaxConnectionsTxt"));
		String SMPP_OATonTxt=cu.getTxtBoxValue("SMPP_OATonTxt", dataMap.get("SMPP_OATonTxt"));
		String SMPP_OANpiTxt=cu.getTxtBoxValue("SMPP_OANpiTxt", dataMap.get("SMPP_OANpiTxt"));
		String SMPP_DATonTxt=cu.getTxtBoxValue("SMPP_DATonTxt", dataMap.get("SMPP_DATonTxt"));
		String SMPP_DANpiTxt=cu.getTxtBoxValue("SMPP_DANpiTxt", dataMap.get("SMPP_DANpiTxt"));
		String SMPP_SMSC_DefaultLst=cu.getDropDownSelectedVal("SMPP_SMSC_DefaultLst");
		
		//String SMPP_DataCodeLst = cu.getDropDownAllVal("SMPP_DataCodeLst", dataMap.get("SMPP_DataCodeLst"));
		//System.out.println(SMPP_DataCodeLst);

		
		//Fetching details of SMPP_InfoTab
		cu.clickElement("HTTPS_InfoTab");
		String HTTP_SysIdTxt=cu.getTxtBoxValue("HTTPS_UserID", dataMap.get("HTTPS_UserID"));
		String HTTP_PasswordTxt=cu.getTxtBoxValue("HTTPS_Password", dataMap.get("HTTPS_Password"));
		
		//Fetching details of SMPP_InfoTab
		cu.clickElement("NumberMgt_InfoTab");
		
		String NumberMgt_NumbersLst= cu.getDropDownMultiSelectedVal("NumberMgt_NumbersLst", dataMap.get("NumberMgt_NumbersLst"));
		String NumberMgt_AssignedNumbersLst = cu.getDropDownMultiSelectedVal("NumberMgt_AssignedNumbersLst", dataMap.get("NumberMgt_AssignedNumbersLst"));	
		
		//get csv data
		Map<String, String> csvData = exportCSVAndGetCSVvalues(cu, dataMap.get("Instance"));
		
		compareDataAndLog("Customer_ID", Supplier_ID, csvData.get("Customer ID"));
		compareDataAndLog("Customer_Short_Name", Supplier_Short_Name, csvData.get("Customer Short Name"));
		compareDataAndLog("Customer_Category", Supplier_Category, csvData.get("Customer Category"));
		compareDataAndLog("Customer_Currency", Supplier_Currency, csvData.get("Customer Currency"));
		//compareDataAndLog("User_ID", User_ID, csvData.get("UserId"));
		compareDataAndLog("Customer_Account_ID", Supplier_Account_ID, csvData.get("Customer Account Id"));
		System.out.println(csvData.get("Customer Account Id"));
		compareDataAndLog("Connectivity_Type", Connectivity_Type, csvData.get("Connectivity Type"));
		compareDataAndLog("Security", Security, csvData.get("Security"));
		System.out.println(csvData.get("Security"));
		compareDataAndLog("Service", Service, csvData.get("Service"));
		
		
		compareDataAndLog("Ins_Instance_StateLst", Ins_Instance_StateLst, csvData.get("Instance State"));
		compareDataAndLog("Ins_commentsTxt", Ins_commentsTxt, csvData.get("Comments"));
		
		compareDataAndLog("Traffic_ThrottlingTxt", Traffic_ThrottlingTxt, csvData.get("Throttling (SMS/Second)"));
		compareDataAndLog("Traffic_OAWhiteTxt", Traffic_OAWhiteTxt, csvData.get("OA Whitelist"));
		compareDataAndLog("Traffic_DAWhiteTxt", Traffic_DAWhiteTxt, csvData.get("DA Shortcode Mapping"));
		compareDataAndLog("Traffic_OAPoolTxt", Traffic_OAPoolTxt, csvData.get("Allowed IP List"));
		//compareDataAndLog("Traffic_OASupportLst", Traffic_OASupportLst, csvData.get("OA (Sender-Type) Support"));
		System.out.println(csvData.get("OA (Sender-Type) Support"));
		compareDataAndLog("Traffic_OABlackListTxt", Traffic_OABlackListTxt, csvData.get("OA Blacklist"));
		compareDataAndLog("Traffic_DABlackListTxt", Traffic_DABlackListTxt, csvData.get("OAdC Information (mapping)"));
		compareDataAndLog("Traffic_OAListTxt", Traffic_OAListTxt, csvData.get("Denied IP List"));
		
		compareDataAndLog("SMPP_VersionLst", SMPP_VersionLst, csvData.get("SMPP Version"));
		compareDataAndLog("SMPP_MsgIDTypeLst", SMPP_MsgIDTypeLst, csvData.get("Msg ID Type"));
		compareDataAndLog("SMPP_MsgLengthLst", SMPP_MsgLengthLst, csvData.get("Msg ID Length"));
		compareDataAndLog("SMPP_SysIdTxt", SMPP_SysIdTxt, csvData.get("System Id"));
		compareDataAndLog("SMPP_PasswordTxt", SMPP_PasswordTxt, csvData.get("System Password"));
		
		compareDataAndLog("SMPP_OATonTxt", SMPP_OATonTxt, csvData.get("OA (Sender) TON"));
		compareDataAndLog("SMPP_OANpiTxt", SMPP_OANpiTxt, csvData.get(" OA (Sender) NPI"));
		compareDataAndLog("SMPP_DATonTxt", SMPP_DATonTxt, csvData.get(" DA (Reciever) TON"));
		compareDataAndLog("SMPP_DANpiTxt", SMPP_DANpiTxt, csvData.get(" DA (Reciever) NPI"));
		compareDataAndLog("SMPP_SMSC_DefaultLst", SMPP_SMSC_DefaultLst, csvData.get("SMSC Default Alphabet"));
		System.out.println(csvData.get("SMSC Default Alphabet"));
		
		System.out.println("SMPP_SMSC"+dataMap.get("SMPP_SMSC_DefaultLst")+csvData.get("SMSC Default Alphabet"));
		//System.out.println("SMPP"+dataMap.get("SMPP_SMSC_DefaultLst")+csvData.get("SMSC Default Alphabet));
		//compareDataAndLog("SMPP_DataCodeLst", SMPP_DataCodeLst, csvData.get("Data Coding Supported"));
		
		
		
		//compareDataAndLog("SS7_MaxFragmentationTxt", SS7_MaxFragmentationTxt	, csvData.get("dummy"));
		
		
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
		  extent = ExtReport.instance("CustomerProvisioning");	
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
	  
	  
	  public Map<String, String> exportCSVAndGetCSVvalues(CommonUtils cu, String instanceName) {
			cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("ExportBtn");
			cu.waitForPageLoad("CustomerProvisioning");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();

			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			Map<String, String> csvDatamap = csvu.getData("Instance", instanceName);
			
			return trimMapKeys(csvDatamap);


		}

	  private void compareDataAndLog(String fieldname, String expected, String actual)
	  {
		  if(expected != null){
		if(expected.equals(actual))		
			test.log(LogStatus.PASS, "EXPECTECD: "+fieldname+" should be same "+expected+" in both csv and UI",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: "+fieldname+" is same as "+actual+" in both csv and UI</span>");
		else 
			test.log(LogStatus.FAIL, "EXPECTECD: "+fieldname+" should be same "+expected+" in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: "+fieldname+" is not same as "+actual+" in both csv and UI</span>");
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
