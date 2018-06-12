package com.tata.selenium.test.ContactManagement;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
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


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_02_ContactManagementCSVExportValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_ContactManagementCSVExportValidation.class.getName());
	String properties =  "./data/ContactManagement.properties";
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
		test = extent.startTest("Execution triggered for - "+TC_02_ContactManagementCSVExportValidation.class.getName()+" - "+uniqueDataId);
		String sheetName="ContactManagement_Screen";
		
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
		
		cu.selectDropDownByVisibleText("ContactManagement_CustomerNameLst", dataMap.get("CustomerNameLst"));
		cu.waitForPageLoadWithSleep("", 500);
		cu.selectDropDownByVisibleText("ContactManagement_CustomerAccountNameLst", dataMap.get("CustomerAccountNameLst"));
		cu.waitForPageLoadWithSleep("", 500);
		cu.clickElement("ContactManagement_DisplayButton");
		cu.waitForPageLoadWithSleep("", 500);
		
		List<Map<String, String>> uiDataRows = getUIData();
		
		exportCSVAndValidateWithUI(navMenuPage, uiDataRows);
		// Taking screenshot and Logging out
		cu.getScreenShot("CSV Export Validation");
		
		
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
		  extent = ExtReport.instance("Contact_Management");
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
	  
	  
	  
	private List<Map<String, String>> getUIData()
	{
			int tableDataRowsSize = cu.ElementsToList("ContactManagement_AllRows").size();
						 
			String customerName = cu.getDropDownSelectedVal("ContactManagement_CustomerNameLst");
			String customerAccountName = cu.getDropDownSelectedVal("ContactManagement_CustomerAccountNameLst");
			String lastUpdatedDate = cu.getAttribute("ContactManagement_LastUpdatedDateTxtBox", "value").trim();
			String lastUpdatedUser = cu.getAttribute("ContactManagement_LastUpdatedUserTxtBox", "value").trim();
			
			List<Map<String, String>> retRowLst = new LinkedList<>();
			for(int i=1;i<tableDataRowsSize+1;i++)
			{
				 Map<String, String> curRowMap= new LinkedHashMap<>();
				 curRowMap.put("Customer Name", customerName);
				 curRowMap.put("Customer Account Name", customerAccountName);
				 curRowMap.put("Category", cu.getText("ContactManagement_Dynamic_ContactCategoryColoumn", "$index$", i+"").trim());
				 curRowMap.put("Type", cu.getText("ContactManagement_Dynamic_ContactTypeColoumn", "$index$", i+"").trim());
				 curRowMap.put("Name", cu.getText("ContactManagement_Dynamic_ContactNameColoumn", "$index$", i+"").trim());
				 curRowMap.put("Phone", cu.getText("ContactManagement_Dynamic_ContactPhoneColoumn", "$index$", i+"").trim());
				 curRowMap.put("Email", cu.getText("ContactManagement_Dynamic_ContactEmailColoumn", "$index$", i+"").trim());
				 curRowMap.put("Used For", cu.getText("ContactManagement_Dynamic_ContactUsedForColoumn", "$index$", i+"").trim());			 
				 curRowMap.put("Last Updated Date", lastUpdatedDate);
				 curRowMap.put("Last Updated User", lastUpdatedUser);
				 retRowLst.add(curRowMap);
			}
			
			return retRowLst;
		}
	
	public void exportCSVAndValidateWithUI(CommonUtils cu,List<Map<String, String>> uiDataMapRows)  
	  {
		  	cu.deleteAllFilesInDownloadFolder();
		  	cu.clickElement("ContactManagement_ExportCSVLink");
			cu.waitForPageLoad("");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			String fileName ="ContactListPerCustomerAccount";
			
			
			//validate file name
			String expectedFileName = fileName+"*.csv";
			if(matchWildcard(FilenameUtils.getName(csvFilePath),expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in '"+expectedFileName+"' pattern", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+expectedFileName+"' pattern. FileName: '"+csvFilePath+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation failed");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in '"+expectedFileName+"' pattern", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as as '"+expectedFileName+"' pattern. Acutal FileName: '"+csvFilePath+"'</span>");
			}
						
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			
			List<Map<String, String>> csvDataMapRows = csvu.getAllRowData();
			
			/*if(csvDataMapRows.size() == uiDataMapRows.size())			
				test.log(LogStatus.PASS, "EXPECTECD: Records size in UI and CSV should matched"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have matched. (Rows Size: "+csvDataMapRows.size()+")</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Records size in UI and CSV should matched"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have not matched. ( UI_Rows_Size: '"+uiDataMapRows.size()
						+"'. CSV_Rows_Size: '"+csvDataMapRows.size()+"' )</span>");*/
			
			int uiRowPointer = 1;
			for(Map<String, String>  uiDataMap : uiDataMapRows)
			{
				boolean currRowmatched = false;
				for(Map<String, String>  csvDataMap : csvDataMapRows)
				{
					/*test.log(LogStatus.INFO, "Row: "+uiRowPointer+" ui: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiDataMap.toString()),
							" csv: <br/><br/>"+StringEscapeUtils.escapeHtml3(csvDataMap.toString()));*/
					if(uiDataMap.equals(csvDataMap))
					{
						currRowmatched = true;
						break;
					}
				}
				
				if(currRowmatched)
					test.log(LogStatus.PASS, "EXPECTECD: UI Row '"+uiRowPointer+"' should be matched with CSV"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI Row '"+uiRowPointer+"'  matched with CSV. (UIData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiDataMap.toString())+")</span>");
				else
					test.log(LogStatus.FAIL, "EXPECTECD: UI Row '"+uiRowPointer+"' should be matched with CSV"
							, "Usage: <span style='font-weight:bold;'>ACTUAL:: UI Row '"+uiRowPointer+"' not matched with CSV. (UIData: <br/><br/>"+StringEscapeUtils.escapeHtml3(uiDataMap.toString())+")</span>");
				
				uiRowPointer++;
			}
					
	  }
	
	public static boolean matchWildcard(String text, String pattern)
	{
	  return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
	}
}
