package com.tata.selenium.test.customerCoverage;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_04_ValidateHeaders implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_ValidateHeaders.class.getName());
	String properties =  "./data/CustomerCoverage.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	private CommonUtils cu;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - "+TC_04_ValidateHeaders.class.getName()+" - "+uniqueDataId);
		String sheetName="Customer_Coverage_Screen";
		
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
		
		cu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
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
		cu.selectDropDownByVisibleText("Customer_Coverage_ServiceNameLst",dataMap.get("Customer_Coverage_ServiceNameLst"));
		cu.selectDropDownByVisibleText("Customer_Coverage_CustomerNameLst",dataMap.get("Customer_Coverage_CustomerNameLst"));
		cu.selectDropDownByVisibleText("Customer_Coverage_TemplateLst",dataMap.get("Customer_Coverage_TemplateLst"));
		cu.waitForPageLoad("");
		
		//Validating whether the desired results are displayed
		cu.checkEditableDropDown("Customer_Coverage_CustomerAccNameLst",dataMap.get("Customer_Coverage_CustomerAccNameLst"));
		cu.checkNonEditableBox("Customer_Coverage_CustomerProductTxt",dataMap.get("Customer_Coverage_CustomerProductLst"));
		cu.checkNonEditableBox("Customer_Coverage_CurrencyTxt");
		
		
		cu.clickElement("Customer_Coverage_DisplayBtn");
		cu.waitForPageLoad("Customer Coverage");
		String popUpName=null;
		/*//Checking for any pop up or error
		cu.checkPopUp("application_PopUpTitle", "Checking for any error when results are expected.");*/
		if(cu.existsElement("application_PopUpTitle"))
			 popUpName = cu.getText("application_PopUpMessage");
		
			if(!"Error: Please select all required input parameters.".equalsIgnoreCase(popUpName)){				
				
				if(!dataMap.get("Table_Haeders_Json").isEmpty())
				{
					List<String> expectedTableHeaders =  getExpectedTableHeaderData(dataMap.get("Table_Haeders_Json"));
					List<String> uiHeaders = getTableResultUIHeaders();
					
					if(expectedTableHeaders.equals(uiHeaders))
					{
						test.log(LogStatus.PASS, "UI and InputSheet headers should be matched", "UI and InputSheet headers has been matched");
					}
					else
						test.log(LogStatus.FAIL, "UI and InputSheet headers should be matched", 
														"UI and datasheet headers have not matched.<br/>UI headers:<br/> "+StringEscapeUtils.escapeHtml3(uiHeaders.toString())
																			+"<br/><br/>Expected headers:<br/> "+StringEscapeUtils.escapeHtml3(expectedTableHeaders.toString()));
				}
				else				
					test.log(LogStatus.FAIL, "'Table_Haeders_Json' can't be empty in inputsheet. Please correct the input sheet");
							
			}else{
				cu.checkMessage("application_PopUpTitle", "Checking for any error when results are expected.", "No data for the selected input parameters");
			}
					
				
		cu.getScreenShot("Validation Of Coverage in Customer Coverage Screen");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	

	}


	private List<String> getTableResultUIHeaders()
	{			
		  return cu.ElementsToList("Customer_Coverage_TableResultHeaders");		
	}
	  
	private List<String> getExpectedTableHeaderData(String jsonStr)
	{
		JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
		JsonArray arrayObj = jsonReader.readArray();
		List<String> ret = new LinkedList<>();		
		for(JsonValue jsonVal : arrayObj)		
			ret.add(jsonVal.toString().replace("\"", ""));		
		
		return ret;
	}
	  
	@BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("Customer_Coverage");
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
