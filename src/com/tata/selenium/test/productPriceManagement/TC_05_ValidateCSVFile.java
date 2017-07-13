package com.tata.selenium.test.productPriceManagement;

import java.util.HashMap;
import java.util.Map;

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



public class TC_05_ValidateCSVFile implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_05_ValidateCSVFile.class.getName());
	String properties =  "./data/ProductPriceManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_05_ValidateCSVFile - "+uniqueDataId);
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
		cu.SelectDropDownByVisibleText("Service_NameLst", dataMap.get("Service_NameLst"));
		cu.SelectDropDownByVisibleText("Product_NameLst", dataMap.get("Product_NameLst"));
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
			cu.SelectDropDownByVisibleText("CountryFilterLst", dataMap.get("CountryFilterLst"));
			cu.SelectDropDownByVisibleText("DestinationFilterLst", dataMap.get("DestinationFilterLst"));
			cu.SelectDropDownByVisibleText("Mcc_FilterLst", dataMap.get("Mcc_FilterLst"));
			cu.SelectDropDownByVisibleText("Mnc_FilterLst", dataMap.get("Mnc_FilterLst"));
			cu.SetData("CriteriaTxt", dataMap.get("CriteriaTxt"));
			cu.clickElement("FilterBtn");
			cu.waitForPageLoad("");
		}
		
		//export file and validate
		if("ON".equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
			exportCSVAndProductPriceValidation(cu, dataMap,dataMap.get("PriceCardLst"),dataMap.get("Service_NameLst"));
		
		cu.getScreenShot("Validating CSV file with UI value");
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
		  extent = ExtReport.instance("ProductPriceManagement");
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
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	  
	  public void checkAllElements(CommonUtils cu,String serviceType) throws Exception{
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
	  
	  
	  
	  public void exportCSVAndProductPriceValidation(CommonUtils cu,Map<String, String> dataMap,String fileName,String serviceType)  
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("Price_Management_ExportBtn");
			cu.waitForPageLoad("");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			
			//validate file name
			String expectedFileName = fileName+".csv";
			if(csvFilePath.trim().contains(expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in '"+fileName+".csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+fileName+".csv' - '"+expectedFileName+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation failed");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in '"+fileName+".csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in '"+fileName+".csv'  - '"+expectedFileName+"' Acutal file name: "+csvFilePath+"</span>");
			}
			
			Map<String, String> csvDatamap;
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			if("MT SMS".equalsIgnoreCase(serviceType)){
				csvDatamap = csvu.getData("Destination", dataMap.get("Destination"));
			}else{
				csvDatamap = csvu.getData("Country", dataMap.get("Country"));
			}
				
			
			if(csvDatamap.get("Country").equals(dataMap.get("Country")) && csvDatamap.get("New Price").equals(dataMap.get("newTab_NewPrice"))				
					&& csvDatamap.get("Upcoming Effective Date").equals(dataMap.get("newTab_NewEffectiveDate")))
			{
				test.log(LogStatus.PASS, "EXPECTECD: Values should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Value is same in both csv and UI'</span>");
			}else{
				String actualDiff = "Country_csv: "+csvDatamap.get("Country")+" Country_UI: "+dataMap.get("Country")+"\n   "
						+"New Price_csv: "+csvDatamap.get("New Price")+" New Price_UI: "+dataMap.get("newTab_NewPrice")+"\n   "
								+"Upcoming Effective Date_csv: "+csvDatamap.get("Upcoming Effective Date")+" Upcoming Effective Date_UI: "+dataMap.get("newTab_NewEffectiveDate");
				test.log(LogStatus.FAIL, "EXPECTECD: New Price should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: New Price is NOT same in both csv and UI - Actual diifernce between UI and CSV is : "+actualDiff+" '</span>");
			}
				
	  }

}
