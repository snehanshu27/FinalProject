package com.tata.selenium.test.productPriceManagement;

import java.util.ArrayList;
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

public class TC_04_ValidateNegativePrice implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_ValidateNegativePrice.class.getName());
	String properties =  "./data/ProductPriceManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws InterruptedException {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_04_ValidateNegativePrice - "+uniqueDataId);
		String sheetName="Product_Price_Management_Screen";
		
		//Reading excel values
		try{
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH,sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		}
		catch (Exception e){
			LOGGER.error("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
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
	
		//Validating all fields
		cu.selectDropDownByVisibleText("Service_NameLst", dataMap.get("Service_NameLst"));
		cu.selectDropDownByVisibleText("Product_NameLst", dataMap.get("Product_NameLst"));
		cu.clickElement("DisplayBtn");
		cu.waitForPageLoad("ProductPriceManagement");
		
		cu.checkPopUp("application_PopUpTitle", "Validating for ant pop up");
		//Validating whether records are displayed or not
		if(cu.existsElement("displayedRows")){
			test.log(LogStatus.PASS, "EXPECTECD: Results should be displayed", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed sucessfully after clicking Display Btn</span>");
		}else{
			test.log(LogStatus.FAIL, "EXPECTECD: Results should be displayed", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results could not get displayed after clicking Display Btn</span>");
		}
		
		//Giving option to display result based on filter
		if(!dataMap.get("CountryFilterLst").isEmpty() || !dataMap.get("DestinationFilterLst").isEmpty()){
			cu.selectDropDownByVisibleText("CountryFilterLst", dataMap.get("CountryFilterLst"));
			cu.selectDropDownByVisibleText("DestinationFilterLst", dataMap.get("DestinationFilterLst"));
			cu.selectDropDownByVisibleText("Mcc_FilterLst", dataMap.get("Mcc_FilterLst"));
			cu.selectDropDownByVisibleText("Mnc_FilterLst", dataMap.get("Mnc_FilterLst"));
			cu.setData("CriteriaTxt", dataMap.get("CriteriaTxt"));
			cu.clickElement("FilterBtn");
			cu.waitForPageLoad("");
			/*if(dataMap.get("PriceCardLst").contains("COST") || dataMap.get("PriceCardLst").contains("COVERAGE")){
				if(cu.existElement("rowColorPath", "$destinationVal$", dataMap.get("CountryFilterLst"))){
					test.log(LogStatus.PASS, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in red color</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in some othercolor than red color.</span>");
				}
			}
		}else{
			if(dataMap.get("PriceCardLst").contains("COST") || dataMap.get("PriceCardLst").contains("COVERAGE")){
				if(cu.existElement("rowColorPath", "$destinationVal$", dataMap.get("CountryFilterLst"))){
					test.log(LogStatus.PASS, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in red color</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Results should be displayed red color", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Results displayed in some othercolor than red color.</span>");
				}
			}*/
		}

		//Clicking on the Supplier value: drill down
		// considering that there is only one tab opened in that point.
	    String oldTab = driver.getWindowHandle();
	    String drillDownPath;
	    if("MT SMS".equalsIgnoreCase(dataMap.get("Service_NameLst"))){
	    	drillDownPath="//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Destination")+"')]/../td[8]/a";
	    }else{
	    	drillDownPath="//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[8]/a";
	    }
		cu.click(drillDownPath);
		
		//Checking for newly opened tab
	    ArrayList<String> newTab = new ArrayList<>(driver.getWindowHandles());
	    newTab.remove(oldTab);
	    // change focus to new tab
	    driver.switchTo().window(newTab.get(0));
	    checkAllElements(cu, dataMap.get("Service_NameLst"));
	    //Set New Price and date
	    cu.setData("newTab_NewPrice", dataMap.get("newTab_NewPrice"));
	    cu.sendTabKeys("newTab_NewPrice");
	    Thread.sleep(2000);
	    cu.clickElement("newTab_SaveBtn");
	    Thread.sleep(2000);
	    cu.checkMessage("application_PopUpTitle", "Validating error msg for new price above range", "Warning: The price should be between 0.0000 and 0.2000.");
	    
	    driver.close();
    	driver.switchTo().window(oldTab);
    	cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");		
		
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	

	}
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("ProductPriceManagement");
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
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	  
	  public void checkAllElements(CommonUtils cu,String serviceType){
		  if("MT SMS".equalsIgnoreCase(serviceType)){
		    	cu.checkElementPresence("newTab_Country");
		    	cu.checkElementPresence("newTab_Destination");
		    	cu.checkElementPresence("newTab_MCC_MNC");
		    	cu.checkElementPresence("newTab_Routing_Criteria");
		    	cu.checkElementPresence("newTab_Current_Price");
		    	cu.checkElementPresence("newTab_NewPrice");
		    	cu.checkElementPresence("newTab_NewEffectiveDate");
		    	cu.checkElementPresence("newTab_Currency");
		    	
		    }else{
		    	cu.checkElementPresence("newTab_Country");
		    	cu.checkElementPresence("newTab_Current_Price");
		    	cu.checkElementPresence("newTab_NewPrice");
		    	cu.checkElementPresence("newTab_NewEffectiveDate");
		    	cu.checkElementPresence("newTab_Currency");
		    	if(cu.existsElement("newTab_Destination")){
		    		test.log(LogStatus.FAIL, "EXPECTECD: Destination field should not be displayed for "+serviceType, 
		    				"Validation:  <span style='font-weight:bold;'>ACTUAL:: Destination field is displayed for "+serviceType+"</span>");
		    	}
		    	if(cu.existsElement("newTab_MCC_MNC")){
		    		test.log(LogStatus.FAIL, "EXPECTECD: MCC_MNC field should not be displayed for "+serviceType, 
		    				"Validation:  <span style='font-weight:bold;'>ACTUAL:: MCC_MNC field is displayed for "+serviceType+"</span>");
		    	}
		    	if(cu.existsElement("newTab_Routing_Criteria")){
		    		test.log(LogStatus.FAIL, "EXPECTECD: Routing_Criteria field should not be displayed for "+serviceType, 
		    				"Validation:  <span style='font-weight:bold;'>ACTUAL:: Routing_Criteria field is displayed for "+serviceType+"</span>");
		    	}
		    }
	  }
	  
	
}
