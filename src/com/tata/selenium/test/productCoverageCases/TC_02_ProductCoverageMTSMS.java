package com.tata.selenium.test.productCoverageCases;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
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

import com.opencsv.CSVWriter;
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
 * @author Sonali Das
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_02_ProductCoverageMTSMS implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_02_ProductCoverageMTSMS.class.getName());
	String properties = "./data/ProductCoverageObjects.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_02_ProductCoverageMTSMS -with TestdataId: "+uniqueDataId);
		String sheetName="Product_Coverage_Screen";
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
	
		//Selecting required values from drop down based on input
		cu.selectDropDownByVisibleText("Product_Coverage_ServiceLst", dataMap.get("Service"));
		cu.waitForPageLoad("");
		cu.selectDropDownByVisibleText("Product_Coverage_ProductNameLst", dataMap.get("Product_Name"));
		cu.waitForPageLoad("");
		cu.selectDropDownByVisibleText("Product_Coverage_SupplierNameLst", dataMap.get("Supplier_Name"));
		
		//validate 1st AccName has been auto populated
		if("ON".equals(dataMap.get("ValidateFirstAccNameAutoPopulated")))
			validateFirstAccNameAutoPopulated(cu);
		
		//Selecting required values from drop down based on input
		cu.selectDropDownByVisibleText("Product_Coverage_SupplierAccNameLst", dataMap.get("Supplier_Account_Name"));
		cu.waitForPageLoad("ProductCoverage");
		
		//validate Historys
		if("ON".equals(dataMap.get("ValidateHistory")))
			validateHistory(dataMap, cu, dataMap.get("Product_Coverage_SupplierAccNameLst"));
		
		//select history as per data
		cu.selectDropDownByVisibleText("Product_Coverage_HistoryLst", dataMap.get("Coverage_History"));
		//click on display button
		cu.clickElement("Product_Coverage_DisplayBtn");

		//Check input and UI coverage is not same
		checkInputAndUICoverageIsNotSame(cu, dataMap);
		
		
		//add\modify coverage as per input data
		modifyCoverage(cu, dataMap);
		
		
		cu.sleep(4000);
		
		//Alert message validations
		validateSuccessAlertMessageAndPDFPageLoaded(cu);
		
		
		//Selecting required values from drop down based on input
		cu.selectDropDownByVisibleText("Product_Coverage_ServiceLst", dataMap.get("Service"));
		cu.selectDropDownByVisibleText("Product_Coverage_ProductNameLst", dataMap.get("Product_Name"));
		cu.selectDropDownByVisibleText("Product_Coverage_SupplierNameLst", dataMap.get("Supplier_Name"));		
		cu.waitForPageLoad("ProductCoverage");
		cu.selectDropDownByVisibleText("Product_Coverage_SupplierAccNameLst", dataMap.get("Supplier_Account_Name"));
		cu.waitForPageLoad("ProductCoverage");
		
		//get time string as date-month-year and validate date on which order is created should be appended to the order number (history)
		cu.default_content();
		cu.SwitchFrames("//iframe[@scrolling='no']");
		String[] uiTime = cu.getText("uiTimestamp").split(" ");
		String expectedUIDateFormat = uiTime[1].trim()+"-"+uiTime[2].trim()+"-"+uiTime[3].trim();
		
		cu.default_content();
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		String selectedHistoryVal = cu.getSelectVauleFromDropDown("Product_Coverage_HistoryLst");
		if(selectedHistoryVal.toLowerCase().contains(expectedUIDateFormat.toLowerCase()))
			test.log(LogStatus.PASS, "EXPECTECD: Date on which order is created should be appended to the order number (coverage history)", "Usage: <span style='font-weight:bold;'>ACTUAL:: Date on which order is created has been appended to the order number (coverage history)</span>");
		else
		{
			cu.getScreenShot("Date on which order is created should be appended to the order number (coverage history) validation");
			test.log(LogStatus.FAIL, "EXPECTECD: Date on which order is created should be appended to the order number (coverage history)", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage history is loaded with date : "+expectedUIDateFormat+" acutal coverage history option which was populated is : "+selectedHistoryVal+"</span>");
		}
		
		//click on display button
		cu.clickElement("Product_Coverage_DisplayBtn");
		
		//vaildate coverage fields
		validateCoverageFieldsUpdatedinUI(cu, dataMap);

	
		//export file and validate
		
		if("ON".equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
			exportCSVAndCoverageFieldsUpdated(cu, dataMap);
		
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Product Coverage Screen");		
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
		  extent = ExtReport.instance("ProductCoverage");		
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
			  LOGGER.info("App Logout failed () :: Exception: " +e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
	  
	  public void validateHistory( Map<String, String> dataMap, CommonUtils cu, String selectedAccName)
	  {
		  
			List<String> coverageHistories = cu.getAllOptionsFromDropDown("Product_Coverage_HistoryLst");
			boolean isthisfresh = coverageHistories.size() == 1;
		  
		  //check fresh record has been entered in data sheet
		  if(isthisfresh && !coverageHistories.get(0).equals(dataMap.get("Coverage_History")))
			{
				cu.getScreenShot("");
				test.log(LogStatus.FAIL, "EXPECTECD: Vaule of Coverage_History should be '"+coverageHistories.get(0)+"' for fresh record", "Usage: <span style='font-weight:bold;'>ACTUAL:: Vaule of Coverage_History is '"+dataMap.get("Coverage_History")+"' instead of '"+coverageHistories.get(0)+"' for fresh record	</span>");
				Assert.fail();
			}
		  
			if(isthisfresh)
			{
				//validate no history is displayed for fresh account name		
				if(coverageHistories.size() == 1 && coverageHistories.get(0).equals(dataMap.get("Coverage_History")))
					test.log(LogStatus.PASS, "EXPECTECD: supplierCoverageHistoryLst should not have any history for fresh account", "Usage: <span style='font-weight:bold;'>ACTUAL:: supplierCoverageHistoryLst doesn't have any history for fresh account </span>");
				else
				{
					cu.getScreenShot("supplierCoverageHistoryLst should not have any history for fresh account validation");
					test.log(LogStatus.FAIL, "EXPECTECD: supplierCoverageHistoryLst should not have any history for fresh account", "Usage: <span style='font-weight:bold;'>ACTUAL:: supplierCoverageHistoryLst is have "+coverageHistories.size()+" records for fresh account "+selectedAccName+"</span>");
					Assert.fail();
				}
			}
			else
			{
				if(coverageHistories.size() ==6 || coverageHistories.size() <7)
				{
					//validate only 5 history is displayed and latest one is editable
					test.log(LogStatus.PASS, "EXPECTECD: Only 5 history records should be displayed", "Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records has been displayed, Total records found: "+(coverageHistories.size()-1)+"</span>");
					
					for(int i=1; i<coverageHistories.size(); i++)
					{					
						cu.selectDropDownByVisibleText("Product_Coverage_HistoryLst", coverageHistories.get(i));
						cu.clickElement("Product_Coverage_DisplayBtn");
						cu.waitForPageLoad("");
						String disAtt = cu.getAttribute("dynamicCoverageCheckbox", "disabled", "$destinationVal$", dataMap.get("Destination"));
						
						if(i!=1){
							if("true".equalsIgnoreCase(disAtt.trim()))	
								test.log(LogStatus.PASS, "EXPECTECD: Other than lastest history should be non editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: Old record is not editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
							else
							{
								cu.getScreenShot("old record non editable for index "+(i+1));
								test.log(LogStatus.FAIL, "EXPECTECD: Other than lastest history should be non editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: Old record is Editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
								
							}
						}
						if(i==1){
							if(disAtt==null)
								test.log(LogStatus.PASS, "EXPECTECD: Lastest history should be editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: Lastest history is Editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
							else
							{
								cu.getScreenShot("old record non editable for index "+(i+1));
								test.log(LogStatus.FAIL, "EXPECTECD: Lastest history should be editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: Lastest history is Not Editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
								
							}	
						}
					}				
				}
				else
				{
					cu.getScreenShot("Only 5 history records should be displayed");
					test.log(LogStatus.FAIL, "EXPECTECD: Only 5 history records should be displayed", "Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records is not displayed for old record total records deisplayed : "+(coverageHistories.size()-1)+"</span>");
				}
			}
	  }
	  
	  public void validateErrorPopupWithoutAddingCoverage(CommonUtils cu, NavigationMenuPage navMenuPage,  Map<String, String> dataMap) throws Exception
	  {
		  	cu.selectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			cu.unSelectCheckBox("dynamicAlphaCheckboxAN", "$destinationVal$", dataMap.get("Destination"));
			cu.unSelectCheckBox("dynamicShortCheckboxSC", "$destinationVal$", dataMap.get("Destination"));
			cu.unSelectCheckBox("dynamicLongCheckboxLN", "$destinationVal$", dataMap.get("Destination"));
			cu.unSelectCheckBox("dynamicDlrCheckboxDR", "$destinationVal$", dataMap.get("Destination"));
			cu.clickElement("submitBtn");		
			cu.checkMessage("application_PopUpMessage", "Check popup without adding/checking SC/LN/DR/AN", "Error: Please provide at least one Route feature");
			
			
			cu.default_content();
			cu.SwitchFrames("//iframe[@scrolling='no']");
			cu.clickElement("exchange");
			cu.sleep(2000);
		    cu.ConfirmAlert();
			cu.waitForPageLoad("");
			  
			navMenuPage.navigateToMenuPageAndMenu(dataMap.get("Navigation"));
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
				
			//Selecting required values from drop down based on input
			cu.selectDropDownByVisibleText("Product_Coverage_ServiceLst", dataMap.get("Service"));
			cu.selectDropDownByVisibleText("Product_Coverage_SupplierNameLst", dataMap.get("Supplier_Name"));		
			cu.waitForPageLoad("ProductCoverage");
			cu.selectDropDownByVisibleText("Product_Coverage_SupplierAccNameLst", dataMap.get("Supplier_Account_Name"));
			cu.waitForPageLoad("ProductCoverage");
			
			cu.clickElement("Product_Coverage_DisplayBtn");
	  }
	  
	  public void modifyCoverage(CommonUtils cu, Map<String, String> dataMap) 
	  {
		  if("Y".equalsIgnoreCase(dataMap.get("Coverage")))			
				cu.selectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));			
			else
				cu.unSelectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			
			//click submit
			cu.clickElement("Product_Coverage_SubmitBtn");
	  }
	  

	  
	  public void validateFirstAccNameAutoPopulated(CommonUtils cu)
	  {
			cu.waitForPageLoad("ProductCoverage");	
			String firstAccNameExp = cu.getAllOptionsFromDropDown("Product_Coverage_SupplierAccNameLst").get(1).trim();
			String selectedAccName = cu.getSelectVauleFromDropDown("Product_Coverage_SupplierAccNameLst").trim();
			
			if(firstAccNameExp.equals(selectedAccName))
				 test.log(LogStatus.PASS, "EXPECTECD: supplierAccNameLst should be auto populated by the first supplier account: "+firstAccNameExp, "Usage: <span style='font-weight:bold;'>ACTUAL:: supplierAccNameLst has been populated by the first supplier account: "+selectedAccName+"</span>");
			else
			{
				cu.getScreenShot("supplierAccNameLst should be auto populated by the first supplier account: "+firstAccNameExp);
				test.log(LogStatus.FAIL, "EXPECTECD: supplierAccNameLst should be auto populated by the first supplier account: "+firstAccNameExp, "Usage: <span style='font-weight:bold;'>ACTUAL:: supplierAccNameLst has been populated by "+selectedAccName+" instead of the first supplier account: "+firstAccNameExp+"</span>");
			}
	  }
	  
	  public void exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap)
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("Product_Coverage_ExportBtn");
			cu.waitForPageLoad("ProductCoverage");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();

			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			Map<String, String> csvDatamap = csvu.getData("Destination", dataMap.get("Destination"));
			
			if(csvDatamap.get("Coverage").equals(dataMap.get("Coverage"))) /*&& csvDatamap.get("AN").equals(dataMap.get("AN")) && csvDatamap.get("SC").equals(dataMap.get("SC"))				
					&& csvDatamap.get("LN").equals(dataMap.get("LN")) && csvDatamap.get("DR").equals(dataMap.get("DR")))*/
			{
				test.log(LogStatus.PASS, "EXPECTECD: Coverage should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is same in both csv and UI'</span>");
			}
			else
			{
				String actualDiff = "Coverage_csv: "+csvDatamap.get("Coverage")+" Coverage_UI: "+dataMap.get("Coverage")+"\n   ";
										/*+"AN_csv: "+csvDatamap.get("AN")+" AN_UI: "+dataMap.get("AN")+"\n   "
											+"SC_csv: "+csvDatamap.get("SC")+" SC_UI: "+dataMap.get("SC")+"\n   "
													+"LN_csv: "+csvDatamap.get("LN")+" LN_UI: "+dataMap.get("LN")+"\n   "
															+"DR_csv: "+csvDatamap.get("DR")+" DR_UI: "+dataMap.get("DR");*/
				
				test.log(LogStatus.FAIL, "EXPECTECD: Coverage should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is NOT same in both csv and UI - Actual diifernce between UI and CSV is : "+actualDiff+" '</span>");
				Assert.fail();
			}
				
	  }
	  
	  public void validateCoverageFieldsUpdatedinUI(CommonUtils cu, Map<String, String> dataMap)
	  {	
			if("Y".equalsIgnoreCase(dataMap.get("Coverage")))
				cu.checkCheckBoxSelected("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("Coverage")))
					cu.checkCheckBoxUnselected("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			}			
			
			/*if("Y".equalsIgnoreCase(dataMap.get("AN")))
				cu.checkCheckBoxSelected("dynamicAlphaCheckboxAN", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("AN")))
					cu.checkCheckBoxUnselected("dynamicAlphaCheckboxAN", "$destinationVal$", dataMap.get("Destination"));
			}
			*/
			/*if("Y".equalsIgnoreCase(dataMap.get("SC")))
				cu.checkCheckBoxSelected("dynamicShortCheckboxSC", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("SC")))
					cu.checkCheckBoxUnselected("dynamicShortCheckboxSC", "$destinationVal$", dataMap.get("Destination"));
			}
			
			if("Y".equalsIgnoreCase(dataMap.get("LN")))
				cu.checkCheckBoxSelected("dynamicLongCheckboxLN", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("LN")))
					cu.checkCheckBoxUnselected("dynamicLongCheckboxLN", "$destinationVal$", dataMap.get("Destination"));
			}

			if("Y".equalsIgnoreCase(dataMap.get("DR")))
				cu.checkCheckBoxSelected("dynamicDlrCheckboxDR", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("DR")))
					cu.checkCheckBoxUnselected("dynamicDlrCheckboxDR", "$destinationVal$", dataMap.get("Destination"));
			}*/
	  }
	  
	  public void validateSuccessAlertMessageAndPDFPageLoaded(CommonUtils cu) 
	  {			
		  cu.checkMessage("application_PopUpMessage", "Sucess popup message after modifing the coverage", "The new coverage(s) have been successfully entered and will become active immediately. You will receive a confirmation email for reference.");
		  
			//switch to new window and check pdf loaded
			String parentWindow = cu.getCurrWindowName();
			cu.newWindowHandles(cu.getCurrWindowName());
			String newWindowTitle = cu.getTitle();
			if(cu.existsElement("pdfEmbed"))
				test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded after coverage modification", "Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after new coverage addition</span>");
			else
			{
				cu.getScreenShot("PDF file loading validation");
				test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after new coverage addition", "Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after new coverage addition -contains no pdf in title: acutal title : "+newWindowTitle+"</span>");
				Assert.fail();	
			}
			cu.DriverClose();
			
			//switch parent window
			cu.switchToWindow(parentWindow);
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
	  }
	  
	  public Map<String, String> getCurrentCoverageUIStatus(CommonUtils cu, String destinationName)
	  {
		  Map<String, String> ret = new HashMap<>();
		  try{					  
				  ret.put("Country", cu.getText("dynamicCountryTd", "$destinationVal$", destinationName));
				  ret.put("Company", cu.getText("dynamicCompanyTd", "$destinationVal$", destinationName));
				  ret.put("Destination", destinationName);
				  ret.put("MCC", cu.getText("dynamicMCCTd", "$destinationVal$", destinationName));
				  ret.put("MNC", cu.getText("dynamicMNCTd", "$destinationVal$", destinationName));
				  
				  if(cu.isCheckBoxSelected("dynamicCoverageCheckbox", "$destinationVal$", destinationName))
					  ret.put("Coverage", "Y");
				  else
					  ret.put("Coverage", "N");
					  
				 /* if(cu.isCheckBoxSelected("dynamicAlphaCheckboxAN", "$destinationVal$", destinationName))
					  ret.put("AN", "Y");
				  else
					  ret.put("AN", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicShortCheckboxSC", "$destinationVal$", destinationName))
					  ret.put("SC", "Y");
				  else
					  ret.put("SC", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicLongCheckboxLN", "$destinationVal$", destinationName))
					  ret.put("LN", "Y");
				  else
					  ret.put("LN", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicDlrCheckboxDR", "$destinationVal$", destinationName))
					  ret.put("DR", "Y");
				  else
					  ret.put("DR", "N");		*/  
				
				return ret;
		  }catch(Exception e)
		  {
			  LOGGER.info("Get webtable coverage data for destination: " +e);
			  cu.getScreenShot("Get webtable coverage data for destination: "+destinationName);
			  test.log(LogStatus.FAIL, "EXPECTECD: Webtable coverage data for destination: "+destinationName+" Should be obtained sucessfully", "Usage: <span style='font-weight:bold;'>ACTUAL:: Failed to get webtable coverage data for destination: "+destinationName+"</span>");
			  return ret;
		  }
	  }
	  
	  public void checkInputAndUICoverageIsNotSame(CommonUtils cu, Map<String, String> dataMap) 
	  {
			Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"));
			if(uiData.get("Coverage").equalsIgnoreCase(dataMap.get("Coverage")) 
			)
			{
				cu.getScreenShot("input and UI coverage is same");
				test.log(LogStatus.FAIL, "EXPECTECD: Input sheet and UI coverage data should not be same, Because then only we can edit the coverage sucessfully", "Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is same, data is  Coverage: "+uiData.get("Coverage")+"</span>");
				Assert.fail();
			}
	  }
	  
	  public void checkInputAndUICoverageIsSame(CommonUtils cu, Map<String, String> dataMap) throws Exception
	  {
			Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"));
			if(!(uiData.get("Coverage").equalsIgnoreCase(dataMap.get("Coverage"))))
				/*&& uiData.get("AN").equalsIgnoreCase(dataMap.get("AN"))
					&& uiData.get("SC").equalsIgnoreCase(dataMap.get("SC"))
						&& uiData.get("LN").equalsIgnoreCase(dataMap.get("LN"))
							&& uiData.get("DR").equalsIgnoreCase(dataMap.get("DR"))))*/
			{
				cu.getScreenShot("input and UI coverage is not same");
				test.log(LogStatus.FAIL, "EXPECTECD: Input sheet and UI coverage data should be same, After changed were made", "Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is not same, data is  UI_Coverage: "+uiData.get("Coverage")+" ?? Data_Coverage: "+dataMap.get("Coverage")																																																						+"\n  UI_DR: "+uiData.get("DR")+" ?? Data_DR: "+dataMap.get("DR")+"</span>");
				Assert.fail();
			}
	  }
	  
	 /* public void modifyCoverageUsingUploadOption(CommonUtils cu, Map<String, String> dataMap) throws Exception
	  {
		  	//create csv file from input data
		  	Map<String, String> uiData = getCurrentCoverageUIStatus(cu, dataMap.get("Destination"));
		  	String tempPath = System.getProperty("user.dir")+"\\temp\\"+cu.getCurrentTimeStamp();
		  	new File(tempPath).mkdirs();
		  	String csvFilePath = tempPath+"\\"+dataMap.get("Supplier_Name")+"-"+dataMap.get("Supplier_Account_Name")+"-Coverage Update.csv";
		  	CSVWriter wr = new CSVWriter(new FileWriter(csvFilePath));
			List<String[]> allLines = new ArrayList<>();
			allLines.add(new String[]{"Country", "Company", "Destination", "MCC", "MNC", "Coverage", "AN", "SC", "LN", "DR", "Restriction"});
			allLines.add(new String[]
							{uiData.get("Country")	,uiData.get("Company") ,uiData.get("Destination"),uiData.get("MCC") ,uiData.get("MNC")
								,dataMap.get("Coverage") ,dataMap.get("AN") ,dataMap.get("SC") ,dataMap.get("LN") ,dataMap.get("DR") 
								,uiData.get("Restriction")							
							});	
			
			wr.writeAll(allLines, false);
			wr.close();
			
			cu.printLogs("CSV file has been generated as the input: "+ csvFilePath);
			test.log(LogStatus.PASS, "EXPECTECD: CSV file should be generated as the input", "Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: CSV file has been generated (fileupload) as the input: "+ csvFilePath+ "</span>");
			//upload the files
			cu.sendKeys("uploadInptFileBtn", csvFilePath, false);
			cu.sleep(2000);
			
			//Check warnig message and accept popup
			cu.checkMessage("application_PopUpMessage", "Check popup waring message after uploading the file", "Warning: This action will upload the Selected CSV. Do you want to Continue?");
			cu.waitForPageLoad("");
			
			//checkInputAndUICoverageIsSame
			checkInputAndUICoverageIsSame(cu, dataMap);
			
			//click submit
			cu.clickElement("submitBtn");
	  }
*/
}