package com.tata.selenium.test.productPriceManagement;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

import com.opencsv.CSVWriter;
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

public class TC_06_UploadWithInvalidCost implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_06_UploadWithInvalidCost.class.getName());
	String properties =  "./data/ProductPriceManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_06_UploadWithInvalidCost - "+uniqueDataId);
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
		
		// Select the parameters
		
		cu.SelectDropDownByVisibleText("Service_NameLst",dataMap.get("Service_NameLst"));
		cu.SelectDropDownByVisibleText("Product_NameLst",dataMap.get("Product_NameLst"));
		cu.checkNonEditableBox("CurrencyTxt", dataMap.get("CurrencyTxt"));

		cu.clickElement("DisplayBtn");
		
		// Export the file
		exportCSVAndCoverageFieldsUpdated(cu, dataMap);
		
		modifyCoverageUsingUploadOption(cu, dataMap);
		
		
		cu.getScreenShot("Validation Of UI elements in Product Price Management");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();	

		
	}

	public void modifyCoverageUsingUploadOption(CommonUtils cu, Map<String, String> dataMap) throws Exception {
		// create csv file from input data
		String tempPath = System.getProperty("user.dir") + "\\temp\\" + cu.getCurrentTimeStamp();

		new File(tempPath).mkdirs();

		String csvFilePath = tempPath + "\\" + dataMap.get("PriceCardLst") + ".csv";

		CSVWriter wr = new CSVWriter(new FileWriter(csvFilePath));
		List<String[]> allLines = new ArrayList<>();

		allLines.add(new String[] { "Country","Destination", "MCC", "MNC", "Criteria","Price", "Effective Date","Suppliers","New Price","Upcoming Effective Date" });

		allLines.add(new String[] { dataMap.get("CountryFilterLst"),dataMap.get("DestinationFilterLst"),
				dataMap.get("Mcc_FilterLst"), dataMap.get("Mnc_FilterLst"),
				dataMap.get("CriteriaTxt"), dataMap.get("PriceTxt"), dataMap.get("Effective_DateTxt"),
				dataMap.get("SuppliersFiltertxt"), dataMap.get("New_PriceTxt"), dataMap.get("Upcoming_Effective_DateTxt")});

		wr.writeAll(allLines, false);
		wr.close();

		cu.printLogs("CSV file has been generated as the input: " + csvFilePath);
		test.log(LogStatus.PASS, "EXPECTECD: CSV file should be generated as the input",
				"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTED: CSV file has been generated (fileupload) as the input: "
						+ csvFilePath + "</span>");

		// upload the files
		cu.sendKeys("PriceManagement_UploadBtn", csvFilePath, false);
		cu.sleep(2000);

		// Check warning message and accept popup
		cu.checkMessage("application_PopUpMessage", "Check popup waring message after uploading the file",
				"Warning: This action will upload the Selected CSV. Do you want to Continue ?");
		cu.waitForPageLoad("");

		// Validate the pop up message
		cu.checkMessage("application_PopUpMessage", "Check popup waring message after uploading the file",
				dataMap.get("DestinationFilterLst")
						+ " - The format for New Price is wrong - found in the row 1");
	}
	
	
	public void exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap) throws Exception {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("exportImgBtn");
		cu.waitForPageLoad("Product Price Management");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + dataMap.get("PriceCardLst") +".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'</span>");

		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

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






}



