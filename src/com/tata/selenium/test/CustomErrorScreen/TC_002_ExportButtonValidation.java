package com.tata.selenium.test.CustomErrorScreen;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

import net.sourceforge.htmlunit.corejs.javascript.GeneratedClassLoader;


/**
 * @date 
 * @author Meenakshi Chelliah
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_002_ExportButtonValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_002_ExportButtonValidation.class.getName());
	String properties = "./data/CustomError.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws InterruptedException {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_002_ExportButtonValidation -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_002_ExportButtonValidation -with TestdataId: " + uniqueDataId);
		String sheetName="Custom_Error_Screen";
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
		
		CommonUtils hu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		hu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);
		driver =hu.LaunchUrl(dataMap.get("URL"));
		hu.deleteCookies();
		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		hu.waitForPageLoad("MessagingInstanceHomePage");
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
		
		String parentWindow = hu.getCurrWindowName();
		CommonUtils.printConsole("parentWindow   "+parentWindow);
		hu.clickElement("Supplier_CustomError");
		
		
		hu.newWindowHandles(parentWindow);
		Thread.sleep(500);
		
		hu.SelectDropDownByVisibleTextCustomMMX("supplierName_List_Button", "supplierName_List_SearchTextbox", "supplierName_List_Dynamic_LabelOPtion", dataMap.get("Supplier_Name"));
		System.out.println(dataMap.get("Supplier_Name"));
		hu.waitForPageLoad("");

		
		String value = hu.getAttribute("supplierID_txt","value");
		System.out.println(value);
		Thread.sleep(2000);
		hu.checkElementNotclicked("supplier_SubmitBtn");
		hu.clickElement("supplier_DisplayBtn");

		if(hu.elementDisplayed("application_PopUpMessage", 3))		
			hu.checkMessage("application_PopUpMessage", "click display button. No record found", "Error: No records found for the selected supplier.");

		//get total rows count
		int totalrows = hu.ElementsToList("customErrorCode_TableFirstValidRow").size();		
		LOGGER.error("Total rows present "+totalrows);
		test.log(LogStatus.INFO, "Total rows to be deleted", "Total rows present "+totalrows);
		
		if(totalrows>0)
		{
			hu.getScreenShot("UI and CSV validation");
			exportCSVAndValidateWithUI(hu, dataMap);
		}
		else
		{
			hu.getScreenShot("No record found");
			LOGGER.error("No record found. Atleast one row should be presenet for export button validation");
			test.log(LogStatus.FAIL, "No Record found", "No record found. Atleast one row should be presenet for export button validation");
		}
		
		hu.switchToWindow(parentWindow);
		hu.waitForPageLoad("");
		hu.SwitchFrames("bottom");
		hu.SwitchFrames("target");
					
		test = hu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		hu.checkRunStatus();
		

	}	
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("CustomErrorScreen");			
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
		  	  LOGGER.info(" App Logout failed () :: Exception: "+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
	  public List<Map<String,String>> getUITableData(CommonUtils cu)
	  {
		  List<Map<String,String>> retList = new LinkedList<>();
		  int rowCount = cu.ElementsToList("customErrorCode_TableAllValidRow").size();
		  for(int i=1;i<rowCount+1;i++)
		  {
			  Map<String,String> curRowMap = new LinkedHashMap<>();
			  
			  curRowMap.put("Error Code", cu.getText("dynamic_CustomErrorCode_TableRow_ErrorCodeCol", "$index$", ""+i).trim());
			  curRowMap.put("Type Of Error Code", cu.getText("dynamic_CustomErrorCode_TableRow_ErrorCodeTypeCol", "$index$", ""+i).replaceAll("\\D+","").trim());
			  curRowMap.put("Description", cu.getText("dynamic_CustomErrorCode_TableRow_ErrorCodeDescriptionCol", "$index$", ""+i).trim());
			  
			  retList.add(curRowMap);
		  }		  
		  return retList;
	  }
	  
		public void exportCSVAndValidateWithUI(CommonUtils cu, Map<String, String> dataMap) {
			
			 List<Map<String, String>> uiRows = getUITableData(cu);
			 			
			
			System.out.println("Inside exportCSVAndCoverageFieldsUpdated");
			cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("ExportBtn");
			cu.waitForPageLoad("");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();


			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			for(Map<String, String> uiCuRow : uiRows)
			{
				test.log(LogStatus.INFO, "Validating data in both CSV and UI for error code - "+uiCuRow.get("Error Code"));
				Map<String, String> csvCuRow = csvu.getData("Error Code", uiCuRow.get("Error Code"));
				if(!csvCuRow.isEmpty())
				{
					//errorcode
					if(uiCuRow.get("Error Code").equals(csvCuRow.get("Error Code")))
					{
						test.log(LogStatus.PASS, "EXPECTECD: Error code should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Error code is same in UI and CSV. Errorcode: "+uiCuRow.get("Error Code")+"</span>");
					}
					else
					{
						test.log(LogStatus.FAIL, "EXPECTECD: Error code should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Error code is not same in UI and CSV. ErrorcodeUI: "+uiCuRow.get("Error Code")+" ErrorcodeCSV: "+csvCuRow.get("Error Code")+"</span>");
					}
					
					//errorcode type
					if(uiCuRow.get("Type Of Error Code").equals(csvCuRow.get("Type Of Error Code")))
					{
						test.log(LogStatus.PASS, "EXPECTECD: Type Of Error Code should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Type Of Error Code is same in UI and CSV. (Errorcode_row_key: "+uiCuRow.get("Error Code")+") Errorcode: "+uiCuRow.get("Type Of Error Code")+"</span>");
					}
					else
					{
						test.log(LogStatus.FAIL, "EXPECTECD: Type Of Error Code should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Type Of Error code is not same in UI and CSV. (Errorcode_row_key: "+uiCuRow.get("Error Code")+") ErrorcodeTypeUI: "+uiCuRow.get("Type Of Error Code")+" ErrorcodeTypeCSV: "+csvCuRow.get("Type Of Error Code")+"</span>");
					}
					
					//errorcode desc
					if(uiCuRow.get("Description").equals(csvCuRow.get("Description")))
					{
						test.log(LogStatus.PASS, "EXPECTECD: Description should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Description is same in UI and CSV. (Errorcode_row_key: "+uiCuRow.get("Error Code")+") Description: "+uiCuRow.get("Description")+"</span>");
					}
					else
					{
						test.log(LogStatus.FAIL, "EXPECTECD: Description should be same in both csv and UI",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: Description is not same in UI and CSV. (Errorcode_row_key: "+uiCuRow.get("Error Code")+") DescriptionUI: "+uiCuRow.get("Description")+" DescriptionCSV: "+csvCuRow.get("Description")+"</span>");
					}
										
				}
				else
				{
					test.log(LogStatus.FAIL, "EXPECTECD: Error code should be present in both csv and UI",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: Unable to find the record with error code "+uiCuRow.get("Error Code")+" in CSV</span>");
				}
			}
			
			

		}

}
