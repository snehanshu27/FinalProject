package com.tata.selenium.test.supplierCoverageCases.three.x;

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

public class TC_003_SupplierCoverageMOSMSAndMLNS implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_003_SupplierCoverageMOSMSAndMLNS.class.getName());
	
	String properties = "./data/SupplierCoverageObjects.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	
	private WebDriver driver;
	Map<String, String> dataMap = new HashMap<>();
	private ExtentTest test ;
	
	@Test
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_003_SupplierCoverageMOSMSAndMLNS -with TestdataId: "+uniqueDataId);
		LOGGER.info("Execution triggered for - TC_003_SupplierCoverageMOSMSAndMLNS -with TestdataId: " + uniqueDataId);
		String sheetName="Supplier_Coverage_Screen";
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
		cu.selectDropDownByVisibleText("supplierServiceLst", dataMap.get("Service"));
		cu.selectDropDownByVisibleText("supplierNameLst", dataMap.get("Supplier_Name"));
		
		//validate 1st AccName has been auto populated
		if("ON".equals(dataMap.get("ValidateFirstAccNameAutoPopulated")))
			validateFirstAccNameAutoPopulated(cu);
		
		//Selecting required values from drop down based on input
		cu.selectDropDownByVisibleText("supplierAccNameLst", dataMap.get("Supplier_Account_Name"));
		cu.waitForPageLoad("SupplierCoverage");
		
		//validate Historys
		if("ON".equals(dataMap.get("ValidateHistory")))
			validateHistory(dataMap, cu, dataMap.get("Supplier_Account_Name"));
		
		//select history as per data
		cu.selectDropDownByVisibleText("supplierCoverageHistoryLst", dataMap.get("Coverage_History"));
		//click on display button
		cu.clickElement("displayBtn");

		//Check input and UI coverage is same
		checkInputAndUICoverageIsSame(cu, dataMap);

		//export file and validate
		if("ON".equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
			exportCSVAndCoverageFieldsUpdated(cu, dataMap);
		
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Coverage Screen");		
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
		  extent = ExtReport.instance("SupplierCoverage");		
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
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	 
	
	  
	  public void validateHistory( Map<String, String> dataMap, CommonUtils cu, String selectedAccName)
	  {
		  
			List<String> coverageHistories = cu.getAllOptionsFromDropDown("supplierCoverageHistoryLst");
			boolean isthisfresh = coverageHistories.size() == 1 ;
		  
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
					test.log(LogStatus.PASS, "EXPECTECD: Only 5 history records should be displayed", "Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records has been displayed, Total records found: "+coverageHistories.size()+"</span>");
					
					for(int i=1; i<coverageHistories.size(); i++)
					{					
						cu.selectDropDownByVisibleText("supplierCoverageHistoryLst", coverageHistories.get(i));
						cu.clickElement("displayBtn");
						cu.waitForPageLoad("");
						String disAtt = cu.getAttribute("allCoverageCheckBoxes", "disabled");
						
						
						if("true".equalsIgnoreCase(disAtt.trim()))	
							test.log(LogStatus.PASS, "EXPECTECD: All history should be non editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: history record is non editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
						else
						{
							cu.getScreenShot("old record non editable for index "+(i+1));
							test.log(LogStatus.FAIL, "EXPECTECD: All history should be non editable", "Usage: <span style='font-weight:bold;'>ACTUAL:: history record is Editable  record name: "+coverageHistories.get(i)+"  optionNo: "+(i+1)+"</span>");
							
						}
					}				
				}
				else
				{
					cu.getScreenShot("Only 5 history records should be displayed");
					test.log(LogStatus.FAIL, "EXPECTECD: Only 5 history records should be displayed", "Usage: <span style='font-weight:bold;'>ACTUAL:: Only 5 history records is not displayed for old record total records deisplayed : "+(coverageHistories.size()-1)+"</span>");
					Assert.fail();
				}
			}
	  }
	 
	    
	  public void validateFirstAccNameAutoPopulated(CommonUtils cu)
	  {
			cu.waitForPageLoad("SupplierCoverage");	
			String firstAccNameExp = cu.getAllOptionsFromDropDown("supplierAccNameLst").get(1).trim();
			String selectedAccName = cu.getSelectVauleFromDropDown("supplierAccNameLst").trim();
			
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
		  
		  String[] countrySplt = dataMap.get("Country").split("\\~");
		  String[] coverageSplt = dataMap.get("Coverage").split("\\~");
		  String[] SCSplt = dataMap.get("SC").split("\\~");
		  String[] LNSplt = dataMap.get("LN").split("\\~");
		  
		  //check for proper inputs
		  if(!(countrySplt.length == coverageSplt.length &&  countrySplt.length == SCSplt.length && countrySplt.length == LNSplt.length))
		  {
			  test.log(LogStatus.FAIL, "EXPECTECD: Input sheet vaule should have proer value", "Usage: <span style='font-weight:bold;'>ACTUAL:: Incorrect input values provides in input sheet   vaule is Country: "+dataMap.get("Country")+" Coverage: "+dataMap.get("Coverage")+" SC: "+dataMap.get("SC")+" LN: "+dataMap.get("LN")+"</span>");
			  Assert.fail();
		  }
		  
		  //download csv
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("exportImgBtn");
			cu.waitForPageLoad("SupplierCoverage");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			
			//validate file name
			String expectedFileName = "\\"+dataMap.get("Supplier_Name")+"-"+dataMap.get("Supplier_Account_Name")+".csv";
			if(csvFilePath.trim().contains(expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Supplier Name-Supplier Account Name.csv' - '"+expectedFileName+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Supplier Name-Supplier Account Name.csv' - '"+expectedFileName+"' Acutal file name: "+csvFilePath+"</span>");
			}
			
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			
			  for(int i=0;i<countrySplt.length; i++)
			  {
				  Map<String, String> csvDatamap = csvu.getData("Country", countrySplt[i]);
				  
				  if(csvDatamap.get("Coverage").equals(coverageSplt[i]) && csvDatamap.get("SC").equals(SCSplt[i])				
							&& csvDatamap.get("LN").equals(LNSplt[i]))
					{
						test.log(LogStatus.PASS, "EXPECTECD: Coverage should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is same in both csv for country: '"+countrySplt[i]+"</span>");
					}
					else
					{
						String actualDiff = "Coverage_csv: "+csvDatamap.get("Coverage")+" Coverage_UI: "+countrySplt[i]+"\n   "
												+"SC_csv: "+csvDatamap.get("SC")+" SC_UI: "+SCSplt[i]+"\n   "
														+"LN_csv: "+csvDatamap.get("LN")+" LN_UI: "+LNSplt[i];
						
						test.log(LogStatus.FAIL, "EXPECTECD: Coverage should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Coverage is NOT same in both csv and UI for country: '"+countrySplt[i]+" - Actual diifernce between UI and CSV is : "+actualDiff+" '</span>");
					}
				  }
				
	  }
	  
	  public void validateCoverageFieldsUpdatedinUI(CommonUtils cu, Map<String, String> dataMap) throws Exception
	  {
			cu.selectCheckBox("dynamicCoverageCheckbox", "$destinationVal$", dataMap.get("Destination"));
			
			if("Y".equalsIgnoreCase(dataMap.get("AN")))
				cu.checkCheckBoxSelected("dynamicAlphaCheckboxAN", "$destinationVal$", dataMap.get("Destination"));
			else{
				if("N".equalsIgnoreCase(dataMap.get("AN")))
					cu.checkCheckBoxUnselected("dynamicAlphaCheckboxAN", "$destinationVal$", dataMap.get("Destination"));
			}
			
			if("Y".equalsIgnoreCase(dataMap.get("SC")))
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
			}
	  }
	  

	  
	  public Map<String, String> getCurrentCoverageUIStatus(CommonUtils cu, String country)
	  {
		  Map<String, String> ret = new HashMap<>();
		  try{					  
				  ret.put("Country", country);
				  
				  if(cu.isCheckBoxSelected("dynamicCoverageCheckbox_ByCountry", "$countryVal$", country))
					  ret.put("Coverage", "Y");
				  else
					  ret.put("Coverage", "N");
					  
				  if(cu.isCheckBoxSelected("dynamicAlphaCheckboxAN_ByCountry", "$countryVal$", country))
					  ret.put("AN", "Y");
				  else
					  ret.put("AN", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicShortCheckboxSC_ByCountry", "$countryVal$", country))
					  ret.put("SC", "Y");
				  else
					  ret.put("SC", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicLongCheckboxLN_ByCountry", "$countryVal$", country))
					  ret.put("LN", "Y");
				  else
					  ret.put("LN", "N");
				  
				  if(cu.isCheckBoxSelected("dynamicDlrCheckboxDR_ByCountry", "$countryVal$", country))
					  ret.put("DR", "Y");
				  else
					  ret.put("DR", "N");		  
				
				return ret;
		  }catch(Exception e)
		  {
			  LOGGER.info("exception "+e);
			  cu.getScreenShot("Get webtable coverage data for Country: "+country);
			  test.log(LogStatus.FAIL, "EXPECTECD: Webtable coverage data for Country: "+country+" Should be obtained sucessfully", "Usage: <span style='font-weight:bold;'>ACTUAL:: Failed to get webtable coverage data for Country: "+country+"</span>");
			  Assert.fail();
			  return ret;
		  }
	  }
	 
	  
	  public void checkInputAndUICoverageIsSame(CommonUtils cu, Map<String, String> dataMap)
	  {
		  String[] countrySplt = dataMap.get("Country").split("\\~");
		  String[] coverageSplt = dataMap.get("Coverage").split("\\~");
		  String[] SCSplt = dataMap.get("SC").split("\\~");
		  String[] LNSplt = dataMap.get("LN").split("\\~");
		  
		  //check for proper inputs
		  if(!(countrySplt.length == coverageSplt.length &&  countrySplt.length == SCSplt.length && countrySplt.length == LNSplt.length))
		  {
			  test.log(LogStatus.FAIL, "EXPECTECD: Input sheet vaule should have proer value", "Usage: <span style='font-weight:bold;'>ACTUAL:: Incorrect input values provides in input sheet   vaule is Country: "+dataMap.get("Country")+" Coverage: "+dataMap.get("Coverage")+" SC: "+dataMap.get("SC")+" LN: "+dataMap.get("LN")+"</span>");
			  Assert.fail();
		  }
		  
		  //check for UI vaules
		  for(int i=1;i<countrySplt.length; i++)
		  {
			Map<String, String> uiData = getCurrentCoverageUIStatus(cu, countrySplt[i]);
			if(uiData.get("Coverage").equalsIgnoreCase(coverageSplt[i]) 				
					&& uiData.get("SC").equalsIgnoreCase(SCSplt[i])
						&& uiData.get("LN").equalsIgnoreCase(LNSplt[i]))
							
			{
				test.log(LogStatus.PASS, "EXPECTECD: Input sheet and UI coverage data should be same","Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is same for country: "+countrySplt[i]+"</span>");
			}
			else
			{
				cu.getScreenShot("input and UI coverage is not same for counrty");
				test.log(LogStatus.FAIL, "EXPECTECD: Input sheet and UI coverage data should be same", "Usage: <span style='font-weight:bold;'>ACTUAL:: Input sheet and UI coverage data is not same for country: "+countrySplt[i]
																																																					+" ,data is  UI_Coverage: "+uiData.get("Coverage")+" ?? Data_Coverage: "+coverageSplt[i]
																																																							+"\n  UI_SC: "+uiData.get("SC")+" ?? Data_SC: "+SCSplt[i]
																																																							+"\n  UI_LN: "+uiData.get("LN")+" ?? Data_LN: "+LNSplt[i]+"</span>");
				Assert.fail();
			}
		  }
	  }
	  


}
