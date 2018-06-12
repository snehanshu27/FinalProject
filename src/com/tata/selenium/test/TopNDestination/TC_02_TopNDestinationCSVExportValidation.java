package com.tata.selenium.test.TopNDestination;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.cookie.ClientCookie;
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

public class TC_02_TopNDestinationCSVExportValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_TopNDestinationCSVExportValidation.class.getName());
	String properties =  "./data/TopN_Destination.properties";
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
		test = extent.startTest("Execution triggered for - "+TC_02_TopNDestinationCSVExportValidation.class.getName()+" - "+uniqueDataId);
		String sheetName="TopN_Destination_Screen";
		
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
		
		
		
		//check for popup
		if(cu.elementPresentAndDisplayed("application_PopUpOkBtn"))		
			cu.moveAndClick("application_PopUpOkBtn");
			
		//formfilling
		selectMainFilter();
		
		//Clik on display button
		cu.clickElement("TopNDestination_Display_Button");
		cu.waitForPageLoadWithSleep("", 500);
		
		//Get ui result table data
		List<Map<String, String>> uiDataRows = getUIData();
		
		//Export table data in CSV and compare with UI data
		exportCSVAndValidateWithUI(navMenuPage, uiDataRows);
		
		// Taking screenshot and Logging out			
		cu.getScreenShot("CSV Export Validation");
		
		
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	
	}
	
	  private void selectMainFilter() {
            
			cu.selectDropDownByVisibleText("TopNDestination_Service_DropDown", dataMap.get("Service"));
			cu.selectDropDownByVisibleText("TopNDestination_Granularity_DropDown",dataMap.get("Granularity"));
			cu.selectDropDownByVisibleText("TopNDestination_Granularity_Period_DropDown",dataMap.get("Granularity_Period") );
			cu.selectDropDownByVisibleText("TopNDestination_Selection_Based_On_DropDown", dataMap.get("Selection_Based_On"));
			cu.checkEditableBox("TopNDestination_N_Text", dataMap.get("N"));
			cu.checkElementPresence("TopNDestination_From_Date_SelectfromCalendar");
			cu.checkElementPresence("TopNDestination_To_Date_SelectformCalendar");

			//Customer List
			cu.clickElement("TopNDestination_CustomerListButton");
			for(String CustomerName : dataMap.get("Customer").split("\\~")){
				 //Unselect "SelectAll" checkbox
				 cu.unSelectCheckBox("TopNDestination_CustomerUnSelectOption");
				 //placing CustomerName in searchbox
			     cu.setData("TopNDestination_CustomerSearchTextBox", CustomerName);
			     cu.sleep(500);
			     //select CustomerName checkbox
			     cu.selectCheckBox("TopNDestination_Dynamic_CustomerListCheckbox", "$customer$", CustomerName);
			     }
			cu.clickElement("TopNDestination_Customerlabel");
			cu.waitForPageLoadWithSleep("", 500);
		
	        //Supplier List		
			cu.clickElement("TopNDestination_SupplierListButton");
			for(String SupplierName : dataMap.get("Supplier").split("\\~")){
				//Unselect "SelectAll" checkbox
				cu.unSelectCheckBox("TopNDestination_SupplierUnSelectOption");
				//placing SupplierName in searchbox
				cu.setData("TopNDestination_SupplierSearchTextBox", SupplierName);
				cu.sleep(500);
				//select SupplierName checkbox
				cu.selectCheckBox("TopNDestination_Dynamic_SupplierListCheckbox", "$Supplier$", SupplierName);
				
			}
			cu.clickElement("TopNDestination_Supplierlabel");
			cu.waitForPageLoadWithSleep("", 500);
			
	        // Country list		
			cu.clickElement("TopNDestination_CountryListButton");			
	        for(String CountryName : dataMap.get("Country").split("\\~")){
	        	//Unselect "SelectAll" checkbox
	        	cu.unSelectCheckBox("TopNDestination_CountryUnSelectOption");
	        	//placing CountryName in searchbox  
	        	cu.setData("TopNDestination_CountrySearchTextBox", CountryName);
	        	cu.sleep(500);
	        	//select CountryName checkbox
	        	cu.selectCheckBox("TopNDestination_Dynamic_CountryListCheckbox", "$Country$", CountryName);
	        }
	        cu.clickElement("TopNDestination_Countrylabel");
	        cu.waitForPageLoadWithSleep("", 500);
	        
	        // Product List   
	        cu.clickElement("TopNDestination_ProductListButton");
	        for(String ProductName: dataMap.get("Product").split("\\~")){
	        	//Unselect "SelectAll" checkbox
	        	cu.unSelectCheckBox("TopNDestination_ProductUnSelectOption");
	        	//placing ProductName in searchbox
	        	cu.setData("TopNDestination_ProductSearchTextBox", ProductName);
	        	cu.sleep(500);
	        	//select ProductName checkbox
	        	cu.selectCheckBox("TopNDestination_Dynamic_ProductListCheckbox", "$Product$", ProductName);
	        }
	        cu.clickElement("TopNDestination_Productlabel");
	        cu.waitForPageLoadWithSleep("", 500);
		
	}

	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("TopN_Destination");
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
		List<String> allHeaderNames = cu.ElementsToListWithTrim("TopNDestination_Table_AllHeaders");
		int tableRowSize = cu.ElementsToList("TopNDestination_Table_AllDataRows").size();
		List<Map<String, String>> retRowLst = new LinkedList<>();
		for(int i=1;i<tableRowSize+1;i++)
		{
			 Map<String, String> curRowMap= new LinkedHashMap<>();
			 List<String> curRowColoumTxts = cu.ElementsToListWithTrim("TopNDestination_Table_Dynamic_Row_AllColoumn", "$index$", i+"");
			 for(int k=0;k<allHeaderNames.size();k++)
			 {
				 if(!"Margin %".equals(allHeaderNames.get(k)))
					 curRowMap.put(allHeaderNames.get(k), curRowColoumTxts.get(k));
				 else
					 curRowMap.put("Margin%", curRowColoumTxts.get(k));
			 }				 
			 retRowLst.add(curRowMap);
		}
		
		return retRowLst;
	}
	
	
	public void exportCSVAndValidateWithUI(CommonUtils cu,List<Map<String, String>> uiDataMapRows)  
	  {
		  	cu.deleteAllFilesInDownloadFolder();
		  	cu.clickElement("TopNDestination_Export_Link");
			cu.waitForPageLoad("");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			String fileName ="TOPNDestinationReport";
			
			
			//validate file name
			String expectedFileName = fileName+".csv";
	//		System.out.println(expectedFileName);
			if(matchWildcard(FilenameUtils.getName(csvFilePath),expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in '"+expectedFileName+"' pattern", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+expectedFileName+"' pattern. FileName: '"+csvFilePath+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation failed");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in '"+expectedFileName+"' pattern", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as as '"+expectedFileName+"' pattern. Acutal FileName: '"+csvFilePath+"'</span>");
			}
						
			CSVUtil csvu = new CSVUtil(csvFilePath, 11);
			
			List<Map<String, String>> csvDataMapRows = csvu.getAllRowData();
			
			
			if(csvDataMapRows.size() == uiDataMapRows.size())			
				test.log(LogStatus.PASS, "EXPECTECD: Records size in UI and CSV should matched"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have matched. (Rows Size: "+csvDataMapRows.size()+")</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Records size in UI and CSV should matched"
						, "Usage: <span style='font-weight:bold;'>ACTUAL:: Records size in UI and CSV have not matched. ( UI_Rows_Size: '"+uiDataMapRows.size()
						+"'. CSV_Rows_Size: '"+csvDataMapRows.size()+"' )</span>");
			
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
