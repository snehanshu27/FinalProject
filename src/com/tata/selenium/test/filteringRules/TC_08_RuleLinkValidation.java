package com.tata.selenium.test.filteringRules;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang3.StringEscapeUtils;
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

public class TC_08_RuleLinkValidation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_08_RuleLinkValidation.class.getName());
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
				"Execution triggered for - "+TC_08_RuleLinkValidation.class.getName()+" -with TestdataId: " + uniqueDataId);
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
				
				cu.setData("filteringRules_RuleIdFilterTxt", dataMap.get("RuleID"));
				cu.clickElement("filteringRules_diplayButton");
				cu.waitForPageLoadWithSleep("", 500);
				
				if(cu.existsElement("filteringRules_dynamic_Row_Rule_ID_Link", "$ruleid$", dataMap.get("RuleID")))
				{
					test.log(LogStatus.PASS, "Rule Id: '"+dataMap.get("RuleID")+"' should be present", "Rule Id: '"+dataMap.get("RuleID")+"' is  present");
					cu.clickElement("filteringRules_dynamic_Row_Rule_ID_Link", "$ruleid$", dataMap.get("RuleID"));					
					checkRuleIdLogRowsPopupWindow(dataMap.get("RuleID"), getRuleIdLogs(dataMap.get("RuleIDLog")));
				}
				else
				{
					test.log(LogStatus.FAIL, "Rule Id: '"+dataMap.get("RuleID")+"' should be present", "Rule Id: '"+dataMap.get("RuleID")+"' is not present");
					cu.getScreenShot("Rule Id: '"+dataMap.get("RuleID")+"' not present");
				}
												
				// Taking screenshot and Logging out
				cu.getScreenShot("Popup Rules Validation");
						
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
	
	
	List<Map<String, String>> getRuleIdLogs(String jsonStr)
	{
		JsonReader jsonReader = Json.createReader(new StringReader(jsonStr));
		JsonArray arrayObj = jsonReader.readArray();
		List<Map<String, String>> allSubRules = new LinkedList<>();
		
		for(JsonValue jsonVal : arrayObj)
		{
			JsonObject obj = jsonVal.asJsonObject();
			Map<String, String> map = new LinkedHashMap<>();
			map.put("User", obj.getString("User"));
			map.put("Date", obj.getString("Date"));
			map.put("Active", obj.getString("Active"));
			map.put("PushedToHubs", obj.getString("PushedToHubs"));
			allSubRules.add(map);
		}		
		
		return allSubRules;
	}
	 
	private void checkRuleIdLogRowsPopupWindow(String refStr, List<Map<String, String>> expectedRuleIdLogData) {
		String paraentWinName = cu.getCurrWindowName();
		Set<String> allWinNames = cu.getAllWindowNames();
		if(allWinNames.size()>1)
		{
			for(String curWinName: allWinNames)
			{
				if(!curWinName.equals(paraentWinName))
				{
					cu.switchToWindow(curWinName);
					cu.waitForPageLoad("New Pop page");
				}
			}
			
			
			List<Map<String, String>> uiRuleIdLogData = getUILogData();
			
			if(uiRuleIdLogData.equals(expectedRuleIdLogData))
			{
				test.log(LogStatus.PASS, "Rule Id Log Data should be matched in both UI and Excelsheet for Rule ID : "+refStr
										, "Rule Id Log Data matched in both UI and Excelsheet for Rule ID : "+refStr);
				cu.getScreenShot("Rule Id Log Data matched ");
			}
			else
			{
				test.log(LogStatus.FAIL, "Rule Id Log Data should be matched in both UI and Excelsheet for Rule ID : "+refStr
						, "Rule Id Log Data not matched in both UI and Excelsheet for Rule ID : "+refStr+" <br/><br/>UiLogData:<br/><br/>"+StringEscapeUtils.escapeHtml3(uiRuleIdLogData.toString())
																										+" <br/><br/>ExcelLogData(Expected):<br/><br/>"+StringEscapeUtils.escapeHtml3(expectedRuleIdLogData.toString()));
				cu.getScreenShot("Rule Id Log Data not matched ");
			}
						
			driver.close();
			cu.switchToWindow(paraentWinName);
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
		}
		else
		{
			test.log(LogStatus.FAIL, "Popup window should be opened after clicking on the RuleId link", "Popup window is not opened after clicking on the RuleId ('"+refStr+"') link");
		}
	}
	
	 
	List<Map<String, String>> getUILogData()
	 {
		 List<Map<String, String>> rowsList = new LinkedList<>();
		 List<String> ruleIdLogRows = cu.ElementsToList("filteringRules_NewPop_RuleIdLogRows");
			for(int i=1;i<ruleIdLogRows.size()+1;i++)
			{
				Map<String, String> rowMap = new LinkedHashMap<>();
				rowMap.put("User", cu.getText("filteringRules_NewPop_DynamicRuleIdLog_UserColoum", "$index$", i+""));
				rowMap.put("Date", cu.getText("filteringRules_NewPop_DynamicRuleIdLog_DateColoum", "$index$", i+""));
				rowMap.put("Active", cu.getText("filteringRules_NewPop_DynamicRuleIdLog_ActiveColoum", "$index$", i+""));
				rowMap.put("PushedToHubs", cu.getText("filteringRules_NewPop_DynamicRuleIdLog_PushedToHubColoum", "$index$", i+""));
				rowsList.add(rowMap);
			}
		return rowsList;
	 }
}
