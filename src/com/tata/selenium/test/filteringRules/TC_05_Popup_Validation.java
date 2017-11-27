package com.tata.selenium.test.filteringRules;

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
import org.apache.poi.hwmf.record.HwmfEscape.EscapeFunction;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.gson.JsonParser;
import com.google.gson.JsonStreamParser;
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

public class TC_05_Popup_Validation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_05_Popup_Validation.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/filteringRules.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	
	private WebDriver driver;
	private CommonUtils cu;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - "+TC_05_Popup_Validation.class.getName()+" -with TestdataId: " + uniqueDataId);
				String sheetName = "FilteringRules";
		
				// Reading excel values
				try {
					ExcelUtils excel = new ExcelUtils();
					excel.setExcelFile(DATA_FILEPATH, sheetName);
					dataMap = excel.getSheetData(uniqueDataId, sheetName);
				} catch (Exception e) {
					LOGGER.error("Exception while reading data from EXCEL file for test case : " + testCaseId
							+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
					CommonUtils.printConsole("Exception while reading data from EXCEL file for test case : " + testCaseId
							+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
					Reporter.log("Exception while reading data from EXCEL file for test case : " + testCaseId
							+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
					test.log(LogStatus.FAIL, "Exception while reading data from EXCEL file for test case : " + testCaseId
							+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e);
					excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
					excelUtils.setCellData(sheetName,
							"Exception while reading data from EXCEL file for test case : " + testCaseId
									+ " -with TestdataId : " + uniqueDataId + " Exceptions : " + e,
							uniqueDataId, "Result_Errors");
					Assert.fail("Error occured while trying to login to the application  -  " +e);

				}
				
				test.log(LogStatus.INFO, "Launch Application",
						"Usage: <span style='font-weight:bold;'>Going to Launch App</span>");

				cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
				cu.printLogs("Executing Test Case -" + testCaseId + " -with TestdataId : " + uniqueDataId);
				driver = cu.LaunchUrl(dataMap.get("URL"));

				LoginPage loginPage = new LoginPage(driver, test, sheetName, uniqueDataId, testCaseId, properties);
				loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
				cu.waitForPageLoad("MessagingInstanceHomePage");

				MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver, test, sheetName, uniqueDataId,
						testCaseId, properties);
				msgInsHomePage.verifyLogin(test, testCaseId, sheetName);

				NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
						properties);
				navMenuPage.navigateToMenu(dataMap.get("Navigation"));
				cu.SwitchFrames("bottom");
				cu.SwitchFrames("target");
				
//				selectMainDropdowns();
//				cu.clickElement("filteringRules_diplayButton");
//				cu.waitForPageLoadWithSleep("", 500);
				
				clickElementAfterScrolling("filteringRules_AddRuleButton");
				cu.sleep(500);
				
				//validating unique keys without making any changes
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation1: Validating Pop Up after Clicking new Row save button (Without selecting Country, Destination, Customer and Supplier)"
							, "Error: At least one of the keys should be different from ALL.");
				
				
				//validating popup without filling data in rules fields
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_CoutryLst", dataMap.get("Country"));
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation2: Validating Pop Up after Clicking new Row save button ( Without filling data in rules fields)"
						, "Error: At least one of the actions Blacklist, Passthrough or Replace must be defined.");
					
				
				clickElementAfterScrolling("filteringRules_EditableRow_SubRule_AddSubRuleButton");
				cu.sleep(500);				
				
				//validating popup for same values in all OA fields				
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value1");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value1");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation3: Validating Pop Up after Clicking new Row save button ( With same data in all OA fields )"
						, "Error: OA BL Condition cannot have the same value(s) as OA Passthrough [value1] .");
				
				//validating popup for same values in OA_BL_Cond and OA_PassThrough
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value1");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value1");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value2", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value3", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation4: Validating Pop Up after Clicking new Row save button ( With same values in OA_BL_Cond and OA_PassThrough fileds )"
						, "Error: OA BL Condition cannot have the same value(s) as OA Passthrough [value1] .");
				
				
				//validating popup for same values in OA_BL_Cond and SubRule_OACond
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value1");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value2");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value3", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation5: Validating Pop Up after Clicking new Row save button ( With same values in OA_BL_Cond and SubRule_OACond fileds )"
						, "Error: OA BL Condition and OA Condition and OA Result cannot have the same value(s) [value1] .");
				
				
				//validating popup for same values in OA_BL_Cond and SubRule_OAResult
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value1");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value2");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value3", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation6: Validating Pop Up after Clicking new Row save button ( With same values in OA_BL_Cond and SubRule_OAResult fileds )"
						, "Error: OA BL Cond cannot have the same value(s) as OA Result [value1] .");
				
				
				//validating popup for same values in OA_PassThrough and SubRule_OACond
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value2");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value1");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value3", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation7: Validating Pop Up after Clicking new Row save button ( With same values in OA_PassThrough and SubRule_OACond fileds )"
						, "Error: OA PT cannot have the same value(s) as OA Condition [value1] .");
				
				//validating popup for same values in OA_PassThrough and SubRule_OAResult
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value2");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value1");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value3", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation8: Validating Pop Up after Clicking new Row save button ( With same values in OA_PassThrough and SubRule_OAResult fileds )"
						, "Error: OA PT cannot have the same value(s) as OA Result [value1]");
				
				//validating popup for same values in SubRule_OACond and SubRule_OAResult
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value2");
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value3");				
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation9: Validating Pop Up after Clicking new Row save button ( With same values in SubRule_OACond and SubRule_OAResult fileds )"
						, "Error: OA Result cannot have the same value(s) as OA Condition [value1] .");
				
				
				//validating popup for same values in all TON fields		
				clearAllRuleFields();
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_TON_BL_CondLst", "AN(5)");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "AN(5)", "$index$", "1");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "AN(5)", "$index$", "1");
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "test", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation10: Validating Pop Up after Clicking new Row save button ( With same values in all TON fields )"
						, "Error: TON BL Condition and TON Condition cannot have the same value(s) [AN(5)] .");
				
				//validating popup for same values in TON_BL_Cond and Subrule_TONCond
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_TON_BL_CondLst", "AN(5)");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "AN(5)", "$index$", "1");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "LN(1)", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation11: Validating Pop Up after Clicking new Row save button ( With same values in in TON_BL_Cond and Subrule_TONCond fields )"
						, "Error: TON BL Condition and TON Condition cannot have the same value(s) [AN(5)] .");
				
				//validating popup for same values in TON_BL_Cond and Subrule_TONResult
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_TON_BL_CondLst", "AN(5)");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "LN(1)", "$index$", "1");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "AN(5)", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation12: Validating Pop Up after Clicking new Row save button ( With same values in in TON_BL_Cond and Subrule_TONResult fields )"
						, "Error: TON BL Condition and TON Condition cannot have the same value(s) [AN(5)] .");
				
				//validating popup for same values in Subrule_TONCond and Subrule_TONResult
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_TON_BL_CondLst", "LN(1)");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "AN(5)", "$index$", "1");
				selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "AN(5)", "$index$", "1");
				clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validation13: Validating Pop Up after Clicking new Row save button ( With same values in in Subrule_TONCond and Subrule_TONResult fields )"
						, "Error: TON BL Condition and TON Condition cannot have the same value(s) [AN(5)] .");
				
				//Duplicate removal validation - OA_BL_Cond
				clearAllRuleFields();
				setDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt", "value1,value2,value1");				
				cu.sleep(500);
				clickElementAfterScrolling("filteringRules_EditableRow_CommentsTxt");
				cu.checkMessage("application_PopUpTitle", "Validation14: Validating Pop Up after entering duplicate vaules in OA_BL_Cond"
							, "Warning : OA BL Cond contain duplicate values(s) and duplicates has been removed.");
				cu.checkEditableBox("filteringRules_EditableRow_OA_BL_CondTxt", "value1,value2");
								
				
				//Duplicate removal validation - OA_PassThrough
				setDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt", "value1,value2,value1");
				cu.sleep(500);
				clickElementAfterScrolling("filteringRules_EditableRow_CommentsTxt");
				cu.checkMessage("application_PopUpTitle", "Validation15: Validating Pop Up after entering duplicate vaules in OA_PassThrough"
							, "Warning : OA PassThrough contain duplicate values(s) and duplicates has been removed.");
				cu.checkEditableBox("filteringRules_EditableRow_OA_PassThroughTxt", "value1,value2", "$index$", "1");
				
				
				//Duplicate removal validation - Subrule_OACond
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1,value2,value1", "$index$", "1");
				cu.sleep(500);
				clickElementAfterScrolling("filteringRules_EditableRow_CommentsTxt");
				cu.checkMessage("application_PopUpTitle", "Validation16: Validating Pop Up after entering duplicate vaules in Subrule_OACond"
							, "Warning : OA RP Cond contain duplicate values(s) and duplicates has been removed.");
				cu.checkEditableBox("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "value1,value2", "$index$", "1");
				
				
				//Duplicate removal validation - Subrule_OAResult
				setDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1,value2,value1", "$index$", "1");
				cu.sleep(500);
				clickElementAfterScrolling("filteringRules_EditableRow_CommentsTxt");
				cu.checkMessage("application_PopUpTitle", "Validation17: Validating Pop Up after entering duplicate vaules in Subrule_OAResult"
							, "Warning : RP Result contain duplicate values(s) and duplicates has been removed.");
				cu.checkEditableBox("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "value1,value2", "$index$", "1");
				
				
				// Taking screenshot and Logging out
				cu.getScreenShot("Nagative validation");
						
				test = cu.getExTest();				
				msgInsHomePage.doLogOut(test);

				// Printing pass/fail in the test data sheet
				cu.checkRunStatus();
				
	}
	
	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Filtering_Rules");
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
		try {
			Log.endTestCase(testCaseId);
			driver.quit();
			// Ending the Extent test
			extent.endTest(test);
			// Writing the report to HTML format
			extent.flush();
		} catch (Exception e) {
			LOGGER.info(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}
	

	List<Map<String, String>> getSubRules(String jsonStr)
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
	
	String getRuleId()
	{
		String ruleId = null;
		if(!dataMap.get("RuleID").trim().isEmpty())
			ruleId = dataMap.get("RuleID").trim();
		else
		{			
			if(cu.existElement("filteringRules_dynamic_Rule_ID_Link"
					, "$country$~$destination$~$supplier$~$supplieracc$~$customer$~$customeracc$"
						, dataMap.get("Country")+"~"+ dataMap.get("Destination")+"~"+ dataMap.get("SupplierName")
							+"~"+ dataMap.get("SupplierAccountName")+"~"+ dataMap.get("CustomerName")+"~"+ dataMap.get("CustomerAccountName")))
			{
				ruleId = cu.getText("filteringRules_dynamic_Rule_ID_Link"
					, "$country$~$destination$~$supplier$~$supplieracc$~$customer$~$customeracc$"
						, dataMap.get("Country")+"~"+ dataMap.get("Destination")+"~"+ dataMap.get("SupplierName")
							+"~"+ dataMap.get("SupplierAccountName")+"~"+ dataMap.get("CustomerName")+"~"+ dataMap.get("CustomerAccountName"));
				
				test.log(LogStatus.PASS, "Rule ID with combination of Country: "+dataMap.get("Country")+" > Destination: "+dataMap.get("Destination")
				+" > SupplierName: "+dataMap.get("SupplierName")+" > SupplierAccountName: "+dataMap.get("SupplierAccountName")
				+" > CustomerName: "+dataMap.get("CustomerName")+" > CustomerAccountName: "+dataMap.get("CustomerAccountName")+" should be present", "RuleId has been found: '"+ruleId+"'");
				cu.getScreenShot("rule ID: '"+ruleId+"' found");
			}
			else
			{
				String failMsgStr = "Unable to find rule ID with combination of Country: "+dataMap.get("Country")+" > Destination: "+dataMap.get("Destination")
				+" > SupplierName: "+dataMap.get("SupplierName")+" > SupplierAccountName: "+dataMap.get("SupplierAccountName")
				+" > CustomerName: "+dataMap.get("CustomerName")+" > CustomerAccountName: "+dataMap.get("CustomerAccountName");
				
				test.log(LogStatus.FAIL, failMsgStr);
				cu.getScreenShot("Unable to find rule ID");
				LOGGER.error(failMsgStr);
				Assert.fail(failMsgStr);						
			}
		}
		
		return ruleId;
	}
	
	void selectMainDropdowns()
	{
		cu.SelectDropDownByVisibleText("filteringRules_CountryLst", dataMap.get("Country"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.SelectDropDownByVisibleText("filteringRules_DestinationLst", dataMap.get("Destination"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.SelectDropDownByVisibleText("filteringRules_SupplierNameLst", dataMap.get("SupplierName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.SelectDropDownByVisibleText("filteringRules_SupplierAccNameLst", dataMap.get("SupplierAccountName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.SelectDropDownByVisibleText("filteringRules_CustomerNameLst", dataMap.get("CustomerName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.SelectDropDownByVisibleText("filteringRules_CustomerAccNameLst", dataMap.get("CustomerAccountName"));
		cu.waitForPageLoadWithSleep("", 100);
	}
	
	void addSubRule(Map<String, String> subRule, String index)
	{
		if(!subRule.get("OA_Cond").isEmpty())
			cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", subRule.get("OA_Cond"), "$index$", index);
		else
			cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", subRule.get("TON_Cond"), "$index$", index);
		cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", subRule.get("OA_Result"), "$index$", index);
		cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", subRule.get("TON_Result"), "$index$", index);
		
		changeSubRuleStatus(subRule, index);
		
	}
	
	void modifySubRule(Map<String, String> subRule, String index)
	{
		if(!subRule.get("OA_Cond_New").isEmpty() && !subRule.get("TON_Cond_New").isEmpty())
		{
			if(cu.checkDisabledEelement("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", index))
				cu.clearData("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", index);
			
			cu.clickElement("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt",  "$index$", index);
			
			if(cu.checkDisabledEelement("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "$index$", index))
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "--Select--", "$index$", index);
			
			cu.clickElement("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt",  "$index$", index);
		}
		
		if(!subRule.get("OA_Cond_New").isEmpty())
			cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", subRule.get("OA_Cond_New"), "$index$", index);
		else
			cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", subRule.get("TON_Cond_New"), "$index$", index);
		cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", subRule.get("OA_Result_New"), "$index$", index);
		cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", subRule.get("TON_Result_New"), "$index$", index);
		
		changeSubRuleStatus(subRule, index);
		
	}
	
	void changeSubRuleStatus(Map<String, String> subRule, String index)
	{
		String currStatusAttribute = cu.getAttribute("filteringRules_EditableRow_SubRule_Dynamic_StatusToggleButton", "value", "$index$", index);						
		if(currStatusAttribute.equalsIgnoreCase(subRule.get("Status")))
		{
			cu.clickElement("filteringRules_EditableRow_SubRule_Dynamic_StatusToggleButton", "$index$", index);
			if("A".equalsIgnoreCase(subRule.get("Status")))
				cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Subrule-Ativate button", "Warning : Do you want to change status from Inactive to Active ?");
			else
				if("D".equalsIgnoreCase(subRule.get("Status")))
					cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Subrule-Deactivate button", "Warning : Do you want to change status from Active to Inactive ?");
		}
	}
	
	void deleteSubRule(Map<String, String> subRule, String index)
	{		
		cu.clickElement("filteringRules_EditableRow_SubRule_Dynamic_DeleteButton", "$index$", index);
		cu.sleep(500);
		cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Subrule-Delete button", "Warning : Do you want to delete the subrule ?");
	}
	
	
	String getSubRuleIndex(Map<String, String> subRule, String mainRuleId)
	{
		int subRuleRowSize = cu.ElementsToList("filteringRules_EditableRow_SubRule_AllRow").size();
		for(int i=1;i<subRuleRowSize+1;i++)
		{			
			String OACondTxtVal = cu.getText("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", i+"");
			String TONCondLstVal = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "$index$", i+"").replace("--Select--", "");
			String OAResultTxtVal = cu.getText("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "$index$", i+"");
			String TONResultLstVal = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "$index$", i+"").replace("--Select--", "");
			
			if(!subRule.get("OA_Cond").isEmpty())
			{
				if(OACondTxtVal.equals(subRule.get("OA_Cond")) && OAResultTxtVal.equals(subRule.get("OA_Result")) && TONResultLstVal.equals(subRule.get("TON_Result")))
						return i+"";
			}
			else
			{
				if(TONCondLstVal.equals(subRule.get("TON_Cond")) && OAResultTxtVal.equals(subRule.get("OA_Result")) && TONResultLstVal.equals(subRule.get("TON_Result")))
					return i+"";
			}
		}
		
		if(!subRule.get("OA_Cond").isEmpty())
			test.log(LogStatus.FAIL, "Sub rule row should be present with combination of OA_Cond: '"+subRule.get("OA_Cond")+"' >  OA_Result: '"+subRule.get("OA_Result")+"'"
																						+"' >  TON_Result: '"+subRule.get("TON_Result")+"' for RuleId: '"+mainRuleId+"'"
																						  ,"Sub rule row not found with the comination");
		
		else
			test.log(LogStatus.FAIL, "Sub rule row should be present with combination of TON_Cond: '"+subRule.get("TON_Cond")+"' >  OA_Result: '"+subRule.get("OA_Result")+"'"
					+"' >  TON_Result: '"+subRule.get("TON_Result")+"' for RuleId: '"+mainRuleId+"'"
					  ,"Sub rule row not found with the comination");
		
		cu.getScreenShot("Sub rule row not found with the comination");
		
		return null;
	}
	
	void clearAllRuleFields()
	{
		selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_TON_BL_CondLst", "--Select--");
		clearDataAfterScrolling("filteringRules_EditableRow_OA_BL_CondTxt");
		clearDataAfterScrolling("filteringRules_EditableRow_OA_PassThroughTxt");
		
		if(cu.checkDisabledEelement("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "$index$", "1"))
			selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "--Select--", "$index$", "1");
		
		if(cu.checkDisabledEelement("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", "1"))
			clearDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", "1");
						
		clearDataAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "$index$", "1");
		
		selectDropDownByVisibleTextAfterScrolling("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "--Select--", "$index$", "1");
	}
	

	void clickElementAfterScrolling(String fieldName)
	{		
		cu.scrollPageToViewElement(fieldName);
		cu.clickElement(fieldName);	
	}	
	
	void clickElementAfterScrolling(String fieldName, String replaceKeys, String replaceValues)
	{		
		cu.scrollPageToViewElement(fieldName, replaceKeys, replaceValues);
		cu.clickElement(fieldName, replaceKeys, replaceValues);	
	}
	
	void setDataAfterScrolling(String fieldName, String value)
	{		
		cu.scrollPageToViewElement(fieldName);
		cu.setData(fieldName, value);
	}
	
	void setDataAfterScrolling(String fieldName, String value, String replaceKeys, String replaceValues)
	{		
		cu.scrollPageToViewElement(fieldName);
		cu.setData(fieldName, value, replaceKeys, replaceValues);
	}
	
	void clearDataAfterScrolling(String fieldName)
	{		
		cu.scrollPageToViewElement(fieldName);
		cu.clearData(fieldName);
	}
	
	void clearDataAfterScrolling(String fieldName, String replaceKeys, String replaceValues)
	{		
		cu.scrollPageToViewElement(fieldName, replaceKeys, replaceValues);
		cu.clearData(fieldName, replaceKeys, replaceValues);
	}
	
	void selectDropDownByVisibleTextAfterScrolling(String fieldName, String value)
	{
		cu.scrollPageToViewElement(fieldName);
		cu.SelectDropDownByVisibleText(fieldName, value);
	}
	
	void selectDropDownByVisibleTextAfterScrolling(String fieldName, String value, String replaceKeys, String replaceValues)
	{
		cu.scrollPageToViewElement(fieldName);
		cu.SelectDropDownByVisibleText(fieldName, value, replaceKeys, replaceValues);
	}
	
	
	
}
