package com.tata.selenium.test.customerProvisioningCases;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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


public class TC_07_CustomerEditing implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_07_CustomerEditing.class.getName());
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
		test = extent.startTest("Execution triggered for  - TC_07_CustomerEditing -with TestdataId: "+uniqueDataId);
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
		
		cu.SelectDropDownByVisibleText("Customer_Name", dataMap.get("Customer_Name"));
		cu.SelectDropDownByVisibleText("Customer_Account_Name" , dataMap.get("Customer_Account_Name"));
		//cu.clickElement("Customer_EditBtn");
		cu.waitForPageLoad("");
		
		//Validating Submit btn should be disabled by default before clicking Edit Btn
		
		if(cu.checkDisabledBtn("Customer_SubmitBtn")){
			cu.printLogs("Validation Failed: SubmitBtn is editable before clicking Edit Btn");
			test.log(LogStatus.FAIL, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is editable  before clicking Edit Btn</span>");
		}
		else{
			cu.printLogs("Validation Passed: SubmitBtn should be non editable before clicking Edit Btn");
			test.log(LogStatus.PASS, "EXPECTECD: Submit Btn should be Non editable  before clicking Edit Btn", "Validation: <span style='font-weight:bold;'>ACTUAL:: Submit Btn is non-editable  before clicking Edit Btn</span>");
		}	
		
		cu.clickElement("Customer_EditBtn");
		cu.waitForPageLoad("");
		
		//Selecting values from main Tab
		cu.SelectDropDownByVisibleText("Instance", dataMap.get("Instance"));
		cu.SelectDropDownByVisibleText("History", dataMap.get("History"));
		cu.SelectDropDownByVisibleText("Account_Status", dataMap.get("Account_Status"));
		cu.SetData("Rate_Change_Notification_Period", dataMap.get("RateChangeNotification"));
		
		//Entering values in Instance Info Tab if data is present in test data
		if(("Y").equalsIgnoreCase(dataMap.get("Ins_TAB"))){
			cu.SelectDropDownByVisibleText("Ins_Instance_StateLst",dataMap.get("Ins_Instance_StateLst"));	
			cu.SetData("Ins_commentsTxt", dataMap.get("Ins_commentsTxt"));
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("Traffic_InformationTAB"))){
			cu.clickElement("Traffic_InfoTab");
			
			cu.SetData("Traffic_ThrottlingTxt", dataMap.get("Traffic_ThrottlingTxt"));
			cu.SetData("Traffic_OAWhiteTxt", dataMap.get("Traffic_OAWhiteTxt"));
			cu.SetData("Traffic_DAShortCodeTxt", dataMap.get("Traffic_DAWhiteTxt"));
			cu.SetData("Traffic_AllowedIPTxt", dataMap.get("Traffic_OAPoolTxt"));
			
			if(dataMap.get("Traffic_OASupportLst").trim().length() >0){
				String[] data=dataMap.get("Traffic_OASupportLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("Traffic_OASupportLst", val);
			}
			
			cu.SetData("Traffic_OABlackListTxt", dataMap.get("Traffic_OABlackListTxt"));
			cu.SetData("Traffic_OADCInfoTxt", dataMap.get("Traffic_DABlackListTxt"));
			cu.SetData("Traffic_DeniedIPTxt", dataMap.get("Traffic_DABlackListTxt"));
			if(("Y").equalsIgnoreCase(dataMap.get("Enhanced_Dlr")))
			{
				cu.selectCheckBox("Enhanced_Dlr");
			}
			
			
			if(dataMap.get("Ehanced_Dlr_Parameter").trim().length() >0){
				String[] data=dataMap.get("Ehanced_Dlr_Parameter").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("Ehanced_Dlr_Parameter", val);
			}
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("SMPP_InfoTab"))){
			cu.clickElement("SMPP_InfoTab");
			cu.waitForPageLoad("SMPP_InfoTab");
			cu.SelectDropDownByVisibleText("SMPP_VersionLst", dataMap.get("SMPP_VersionLst"));
			cu.SelectDropDownByVisibleText("SMPP_SMSC_DefaultLst", dataMap.get("SMPP_SMSC_DefaultLst"));
			cu.SelectDropDownByVisibleText("SMPP_MsgIDTypeLst", dataMap.get("SMPP_MsgIDTypeLst"));
			cu.SelectDropDownByVisibleText("SMPP_MsgLengthLst", dataMap.get("SMPP_MsgLengthLst"));
			cu.SetData("SMPP_SysIdTxt", dataMap.get("SMPP_SysIdTxt"));
			cu.SetDataWithoutClearing("SMPP_PasswordTxt", dataMap.get("SMPP_PasswordTxt"));
			cu.SetData("SMPP_MaxConnectionsTxt", dataMap.get("SMPP_MaxConnectionsTxt"));
			cu.SetData("SMPP_OATonTxt", dataMap.get("SMPP_OATonTxt"));
			cu.SetData("SMPP_OANpiTxt", dataMap.get("SMPP_OANpiTxt"));
			cu.SetData("SMPP_DATonTxt", dataMap.get("SMPP_DATonTxt"));
			cu.SetData("SMPP_DANpiTxt", dataMap.get("SMPP_DANpiTxt"));
			cu.SelectDropDownByVisibleText("Enhanced_DLR_Format", dataMap.get("Enhanced_DLR_Format"));
			
			//Handling multi select options
			if(dataMap.get("SMPP_DataCodeLst").trim().length() >0){
				String[] data=dataMap.get("SMPP_DataCodeLst").split(";");
				for(String val : data)
					cu.SelectDropDownByVisibleText("SMPP_DataCodeLst", val);
			}
		}
		
		if(("Y").equalsIgnoreCase(dataMap.get("HTTP_TAB"))){
			cu.clickElement("HTTPS_InfoTab");

			cu.SetDataWithoutClearing("HTTPS_UserID", dataMap.get("HTTPS_UserID"));
			cu.SetDataWithoutClearing("HTTPS_Password", dataMap.get("HTTPS_Password"));
			
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
		/*WebElement element1=driver.findElement(By.xpath("//*[@id='saveButton']"));
		Actions action1 = new Actions(driver);
		action1.moveToElement(element1).click().perform();
		cu.clickElement("Customer_SaveBtn");
		cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Save Btn", "This action will also save the details for other instances, but will not send the details to the platform. Do you want to Continue?");
		cu.waitForPageLoad("");
		cu.checkMessage("application_PopUpTitle", "Validating success Msg after submitting details", "The supplier provisioning information have been successfully entered");
		*/
		//Validating Submit Btn and pop up dislayed after clicking it
		//cu.clickElement("Customer_SubmitBtn");
		WebElement element=driver.findElement(By.xpath("//*[@id='submitButton']"));
		//Actions action = new Actions(driver);
		//action.moveToElement(element).click().perform();
		new Actions(driver).moveToElement(element).perform();
		element.click();
		cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Submit Btn", "Warning: This action will submit the details and create an order. Do you want to Continue?");
		cu.waitForPageLoad("");
		cu.checkMessage("application_PopUpTitle", "Validating success Msg after submitting details", "The customer provisioning information have been successfully entered");
		
		
		
		

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
			  LOGGER.info(" App Logout failed () :: Exception: " +e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	

}
