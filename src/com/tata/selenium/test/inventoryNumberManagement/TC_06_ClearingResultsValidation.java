package com.tata.selenium.test.inventoryNumberManagement;

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


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_06_ClearingResultsValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_06_ClearingResultsValidation.class.getName());
	String properties =  "./data/NumberInventory.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test()
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_06_ClearingResultsValidation - "+uniqueDataId);
		String sheetName="Number_Inventory_Screen";
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
		//Runtime.getRuntime().exec("C:\\Users\\devbraths\\Desktop\\HandleAuthentication.exe");
		cu.waitForPageLoad("MessagingInstanceHomePage");
		
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		//Selecting appropriate option to display
		if(("Y").equalsIgnoreCase(dataMap.get("Add_Inventory"))){
			cu.clickElement("Add_Inventory");
		}
		
		cu.SelectDropDownByVisibleText("Number_Inventory_SupplierNameLst",dataMap.get("Number_Inventory_SupplierNameLst"));
		cu.SelectDropDownByVisibleText("Number_Inventory_TONLst",dataMap.get("Number_Inventory_TONLst"));
		cu.SelectDropDownByVisibleText("Number_Inventory_CountryLst",dataMap.get("Number_Inventory_CountryLst"));
		cu.waitForPageLoad("Number Inventory");
		
		cu.selectDate("Number_Inventory_StartDateTxt", dataMap.get("Number_Inventory_StartDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");
		cu.selectDate("Number_Inventory_EndDateTxt", dataMap.get("Number_Inventory_EndDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");
		
		if(dataMap.get("Number_Inventory_NumbersTxt").contains(";")){
			String[] data=dataMap.get("Number_Inventory_NumbersTxt").split(";");
			for(String val : data){
				cu.enterData("Number_Inventory_NumbersTxt", val);
				cu.returnElement("Number_Inventory_NumbersTxt").sendKeys(Keys.RETURN);
			}
		}else{
			cu.enterData("Number_Inventory_NumbersTxt", dataMap.get("Number_Inventory_NumbersTxt"));
		}
			
		cu.clickElement("Number_Inventory_AddBtn");
		cu.waitForPageLoad("");
		cu.clickElement("Search_Update_Delete_Inventory");
		if(cu.returnElement("application_PopUpTitle") != null){
			WebElement element = cu.returnElement("application_PopUpMessage");
			String PopUp_Msg = element.getText().trim();
			if(("Warning: This action will cancel any unsaved changes. Do you want to Continue?").equalsIgnoreCase(PopUp_Msg)){
				test.log(LogStatus.PASS, "Proper pop up expected", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up is being displayed and it is  - "+PopUp_Msg+"</span>");
				if(("Y").equals(dataMap.get("ClearResultSection"))){
					cu.returnElement("application_PopUpOkBtn").click();
					cu.waitForPageLoad("");
					if(cu.existsElement("Number_Inventory_DisplayBtn")){
						test.log(LogStatus.PASS, "Details should be cleard and Search Radio Btn should be clicked", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results screen cleared and Search/UPdate/Del radio btn is clicked automatically after clicking Yes BTN</span>");
					}else{
						test.log(LogStatus.FAIL, "Details should be cleard and Search Radio Btn should be clicked", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results screen did not get cleared even after clicking Yes BTN.</span>");
					}
				}else if(("N").equals(dataMap.get("ClearResultSection"))){
					cu.returnElement("application_PopUpCancelBtn").click();
					if(driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Number_Inventory_CountryLst")+"')]"))!= null){
						test.log(LogStatus.PASS,"Number added should be displayed as it is", "Number '"+dataMap.get("Number_Inventory_NumbersTxt")+"' added for country "+dataMap.get("Number_Inventory_CountryLst")+" in the result section after clicking No Btn");	
					}
					else{
						test.log(LogStatus.FAIL, "Details should be cleard and Search Radio Btn should be clicked", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results screen did not get cleared.</span>");
					}		
			}else{
				test.log(LogStatus.FAIL, "Proper pop up expected", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up is not displayed</span>");
			}
			}
		}
		
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of "+dataMap.get("Number_Inventory_TONLst")+" in InventoryScreen");
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
		  extent = ExtReport.instance("NumberInventory");
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
			  LOGGER.info(" App Logout failed () :: Exception:"+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
}
