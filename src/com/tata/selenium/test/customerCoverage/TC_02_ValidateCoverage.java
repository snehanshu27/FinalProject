package com.tata.selenium.test.customerCoverage;

import java.util.HashMap;
import java.util.Map;

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
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_02_ValidateCoverage - "+uniqueDataId);
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
		cu.SelectDropDownByVisibleText("Customer_Coverage_ServiceNameLst",dataMap.get("Customer_Coverage_ServiceNameLst"));
		cu.SelectDropDownByVisibleText("Customer_Coverage_CustomerNameLst",dataMap.get("Customer_Coverage_CustomerNameLst"));
		cu.waitForPageLoad("");
		
		//Validating whether the desired results are displayed
		cu.checkEditableDropDown("Customer_Coverage_CustomerAccNameLst",dataMap.get("Customer_Coverage_CustomerAccNameLst"));
		cu.checkEditableDropDown("Customer_Coverage_CustomerProductLst",dataMap.get("Customer_Coverage_CustomerProductLst"));
		cu.checkNonEditableBox("Customer_Coverage_CurrencyTxt");
		
		
		cu.clickElement("Customer_Coverage_DisplayBtn");
		cu.waitForPageLoad("Customer Coverage");
		String popUpName=null;
		/*//Checking for any pop up or error
		cu.checkPopUp("application_PopUpTitle", "Checking for any error when results are expected.");*/
		if(cu.existsElement("application_PopUpTitle"))
			 popUpName = cu.getText("application_PopUpMessage");
		
			if(!"No data for the selected input parameters".equalsIgnoreCase(popUpName)){
				//Declaring variables to store value from application
				String currPrice,newPrice,effectiveDate,Impact;
				if(("MT SMS").equalsIgnoreCase(dataMap.get("Customer_Coverage_ServiceNameLst"))){
					 currPrice=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[contains(text(),'"+dataMap.get("Destination")+"')]/../td[5]");
					 newPrice=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[contains(text(),'"+dataMap.get("Destination")+"')]/../td[6]");
					 effectiveDate=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[contains(text(),'"+dataMap.get("Destination")+"')]/../td[7]");
					 Impact=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[contains(text(),'"+dataMap.get("Destination")+"')]/../td[8]");
				}else{
					currPrice=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[5]");
					 newPrice=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[6]");
					 effectiveDate=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[7]");
					 Impact=cu.returnText("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Country")+"')]/../td[8]");
				}

				
				//Validating CurrentPrice
				if(!dataMap.get("CurrentPrice").isEmpty() && !currPrice.isEmpty()){
					if(currPrice.equalsIgnoreCase(dataMap.get("CurrentPrice"))){
						test.log(LogStatus.PASS,"EXPECTECD: CurrentPrice value shoule be  " + dataMap.get("CurrentPrice"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: CurrentPrice value in UI is  " + currPrice
										+ " and the value in Test data is  -'" + dataMap.get("CurrentPrice") + "</span>");
					}else{
						test.log(LogStatus.FAIL,"EXPECTECD: CurrentPrice value shoule be  " + dataMap.get("CurrentPrice"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: CurrentPrice value in UI is  " + currPrice
										+ " and the value in Test data is  -'" + dataMap.get("CurrentPrice") + "</span>");
					}
				}
				
				//Validating NewPrice
				if(!dataMap.get("NewPrice").isEmpty() && !newPrice.isEmpty()){
					if(newPrice.equalsIgnoreCase(dataMap.get("NewPrice"))){
						test.log(LogStatus.PASS,"EXPECTECD: NewPrice value shoule be  " + dataMap.get("NewPrice"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: NewPrice value in UI is  " + newPrice
										+ " and the value in Test data is  -" + dataMap.get("NewPrice") + "</span>");
					}else{
						test.log(LogStatus.FAIL,"EXPECTECD: NewPrice value shoule be  " + dataMap.get("NewPrice"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: NewPrice value in UI is  " + newPrice
										+ " and the value in Test data is  -" + dataMap.get("NewPrice") + "</span>");
					}
				}
				
				//Validating EffectiveDate
				if(!dataMap.get("EffectiveDate").isEmpty() && !effectiveDate.isEmpty()){
					if(effectiveDate.equalsIgnoreCase(dataMap.get("EffectiveDate"))){
						test.log(LogStatus.PASS,"EXPECTECD: EffectiveDate value shoule be  " + dataMap.get("EffectiveDate"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: EffectiveDate value in UI is  " + effectiveDate
										+ " and the value in Test data is  -'" + dataMap.get("EffectiveDate") + "</span>");
					}else{
						test.log(LogStatus.FAIL,"EXPECTECD: NewPrice value shoule be  " + dataMap.get("EffectiveDate"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: NewPrice value in UI is  " + effectiveDate
										+ " and the value in Test data is  -'" + dataMap.get("EffectiveDate") + "</span>");
					}
				}
				
				//Validating Impact
				if(!dataMap.get("Impact").isEmpty() && !Impact.isEmpty()){
					if(Impact.equalsIgnoreCase(dataMap.get("Impact"))){
						test.log(LogStatus.PASS,"EXPECTECD: Impact value shoule be  " + dataMap.get("Impact"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: Impact value in UI is  " + Impact
										+ " and the value in Test data is  -'" + dataMap.get("Impact") + "</span>");
					}else{
						test.log(LogStatus.FAIL,"EXPECTECD: Impact value shoule be  " + dataMap.get("Impact"),"Validation:  "
								+ "<span style='font-weight:bold;'>ACTUAL:: Impact value in UI is  " + Impact
										+ " and the value in Test data is  -'" + dataMap.get("Impact") + "</span>");
					}
				}
				
				//export file and validate
				if(("ON").equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
					exportCSVAndValidateCoverage(cu, dataMap);

			}else{
				cu.checkMessage("application_PopUpTitle", "Checking for any error when results are expected.", "No data for the selected input parameters");
			}
		
				
		cu.getScreenShot("Validation Of Coverage in Customer Coverage Screen");
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
	  
	  
	  public void exportCSVAndValidateCoverage(CommonUtils cu,Map<String, String> dataMap) throws Exception
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("Customer_Coverage_ExportBtn");
			cu.waitForPageLoad("CustomerCoverage");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			
			//validate file name
			String expectedFileName = "CustPriceView"+".csv";
			if(csvFilePath.trim().contains(expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTED: Exported file name should be in 'CustPriceView.csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'CustPriceView.csv' - '"+expectedFileName+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation");
				test.log(LogStatus.FAIL, "EXPECTED: Exported file name should be in 'CustPriceView.csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Supplier Name-Supplier Account Name.csv' - '"+expectedFileName+"' Acutal file name: "+csvFilePath+"</span>");
			}
			
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			Map<String, String> csvDatamap = csvu.getData("Destination", dataMap.get("Destination"));
			
			
			if(csvDatamap.get("Customer Name").equals(dataMap.get("Customer_Coverage_CustomerNameLst")) && csvDatamap.get("Customer Account Name").equals(dataMap.get("Customer_Coverage_CustomerAccNameLst"))				
					&& csvDatamap.get("Currency").equals(dataMap.get("Customer_Coverage_CurrencyTxt")) && csvDatamap.get("Country").equals(dataMap.get("Country")) 
					&& csvDatamap.get("Effective Date").equals(dataMap.get("EffectiveDate"))&&csvDatamap.get("Impact").equals(dataMap.get("Impact")))
			{
				test.log(LogStatus.PASS, "EXPECTED: Values should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Value is same in both csv and UI'</span>");
			}else{
				String actualDiff = "CustName_csv: "+csvDatamap.get("Customer Name")+" CustName_csv_UI: "+dataMap.get("Customer_Coverage_CustomerNameLst")+"\n   "
						+"CustAccName_csv: "+csvDatamap.get("Customer Account Name")+" CustAccName_UI: "+dataMap.get("Customer_Coverage_CustomerAccNameLst")+"\n   "
								+"Currency_csv: "+csvDatamap.get("Currency")+" Currency_UI: "+dataMap.get("Customer_Coverage_CurrencyTxt")+"\n   "
										+"Country_csv: "+csvDatamap.get("Country")+" Country_UI: "+dataMap.get("Country")+"\n   "
												+"EffectiveDate_csv: "+csvDatamap.get("Effective Date")+" EffectiveDate_UI: "+dataMap.get("EffectiveDate")+"\n   "
										+"Impact_csv: "+csvDatamap.get("Impact")+" Impact_UI: "+dataMap.get("Impact");
				test.log(LogStatus.FAIL, "EXPECTED: Coverage should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is NOT same in both csv and UI - Actual diifernce between UI and CSV is : "+actualDiff+" '</span>");
			}
				
	  }
	
}
