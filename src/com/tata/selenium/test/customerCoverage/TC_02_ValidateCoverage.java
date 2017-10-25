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
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_02_ValidateCoverage implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_ValidateCoverage.class.getName());
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
		test = extent.startTest("Execution triggered for - "+TC_02_ValidateCoverage.class.getName()+" - "+uniqueDataId);
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
		cu.SelectDropDownByVisibleText("Customer_Coverage_ServiceNameLst",dataMap.get("Customer_Coverage_ServiceNameLst"));
		cu.SelectDropDownByVisibleText("Customer_Coverage_CustomerNameLst",dataMap.get("Customer_Coverage_CustomerNameLst"));
		cu.SelectDropDownByVisibleText("Customer_Coverage_TemplateLst",dataMap.get("Customer_Coverage_TemplateLst"));
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
				
				if(!dataMap.get("Table_ResultData_Json").isEmpty())
				{
					List<Map<String, String>> expectedTableResultData =  getExpectedTableResultData(dataMap.get("Table_ResultData_Json"));
					List<Map<String, String>> uiData = getUIData();
					
					if(expectedTableResultData.equals(uiData))
					{
						test.log(LogStatus.PASS, "UI and InputSheet data should be matched", "UI and InputSheet data has been matched");
					}
					else
						test.log(LogStatus.FAIL, "UI and InputSheet data should be matched", 
														"UI and data sheet data has not matched. UI data:<br/> "+StringEscapeUtils.escapeHtml3(uiData.toString())
																			+"<br/><br/>Expecteddata: "+StringEscapeUtils.escapeHtml3(expectedTableResultData.toString()));
				}
				else				
					test.log(LogStatus.FAIL, "'Table_ResultData_Json' can't be empty in inputsheet. Please correct the input sheet");
							
			}else{
				cu.checkMessage("application_PopUpTitle", "Checking for any error when results are expected.", "No data for the selected input parameters");
			}
		
			//export file and validate
			//if(("ON").equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
				exportCSVAndValidateCoverage();
				
		cu.getScreenShot("Validation Of Coverage in Customer Coverage Screen");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	

	}
	
	  private List<Map<String, String>> getUIData() 
	  {		
		  List<Map<String, String>> retData = new LinkedList<>(); 
		  List<String> tableheaders = getTableResultHeaders();
		  int dataRowsSize = cu.ElementsToList("Customer_Coverage_TableResultDataRows").size();		  
		  for(int i=1;i<dataRowsSize+1;i++)
		  {
			  Map<String, String> currRowData = new LinkedHashMap<>();
			  List<String> rowColoumValues = cu.ElementsToListWithTrim("Customer_Coverage_TableResult_DynamicDataRowColoumns", "$index$", i+"");
			  for(int k=0;k<tableheaders.size();k++)
			  {
				  currRowData.put(tableheaders.get(k), rowColoumValues.get(k));
			  }
			  retData.add(currRowData);
		  }
		
		  return retData;
	}

	private List<String> getTableResultHeaders()
	{			
		  return cu.ElementsToList("Customer_Coverage_TableResultHeaders");		
	}
	  
	private List<Map<String, String>> getExpectedTableResultData(String jsonStr)
	{
		JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
		JsonArray arrayObj = jsonReader.readArray();
		List<Map<String, String>> allSubRules = new LinkedList<>();
		
		for(JsonValue jsonVal : arrayObj)
		{
			JsonObject obj = jsonVal.asJsonObject();
			Map<String, String> map = new LinkedHashMap<>();				
			for(String key : obj.keySet())				
				map.put(key, obj.getString(key));	
			
			allSubRules.add(map);
		}		
		
		return allSubRules;
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
	  
	  
	  public void exportCSVAndValidateCoverage() throws Exception
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("Customer_Coverage_ExportBtn");
			cu.waitForPageLoad("CustomerCoverage");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			List<Map<String, String>> csvData = csvu.getAllRowData();
			List<Map<String, String>> uiData = getUIData();
			
			if(csvData.equals(uiData))
			{
				test.log(LogStatus.PASS, "UI and CSV data should be matched", "UI and CSV data has been matched");
			}
			else
				test.log(LogStatus.FAIL, "UI and CSV data should be matched", 
												"UI and CSV data has not matched. UI data:<br/> "+StringEscapeUtils.escapeHtml3(uiData.toString())
																	+"<br/><br/>CSV_Data: "+StringEscapeUtils.escapeHtml3(csvData.toString()));
				
	  }
	
	  
		
}
