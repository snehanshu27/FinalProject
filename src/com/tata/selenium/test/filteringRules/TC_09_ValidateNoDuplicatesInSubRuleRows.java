package com.tata.selenium.test.filteringRules;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.codehaus.groovy.runtime.dgmimpl.arrays.IntegerArrayGetAtMetaMethod;
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

public class TC_09_ValidateNoDuplicatesInSubRuleRows implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_09_ValidateNoDuplicatesInSubRuleRows.class.getName());
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
				"Execution triggered for - "+TC_09_ValidateNoDuplicatesInSubRuleRows.class.getName()+" -with TestdataId: " + uniqueDataId);
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
				
				selectMainDropdowns();
				cu.clickElement("filteringRules_diplayButton");
				cu.waitForPageLoadWithSleep("", 500);
				
				 Map<String, List<Map<String, String>>> uiSubRuleData = getSubRuleUIData();
				 
				 for(String cuuRuleId : uiSubRuleData.keySet())
				 {
					 List<Map<String, String>> subRuleRowData = uiSubRuleData.get(cuuRuleId);	
					 List<Map<String, String>> subRuleRowDataUniqueRows = getUniqeRows(subRuleRowData);
					 Map<String, String[]> dupmap = new LinkedHashMap<>();
					 for(Map<String, String> currData : subRuleRowData)
					 {						 											 
						 for(Map<String, String> currDataUnique :subRuleRowDataUniqueRows)
						 {
							 String key = currDataUnique.get("OA Condition")+"~"+currDataUnique.get("TON Condition")+"~"+currDataUnique.get("OA Result")+"~"+currDataUnique.get("TON Result");
							 if(currDataUnique.get("OA Condition").equals(currData.get("OA Condition"))	&& currDataUnique.get("TON Condition").equals(currData.get("TON Condition"))
									 	&&currDataUnique.get("OA Result").equals(currData.get("OA Result"))	&& currDataUnique.get("TON Result").equals(currData.get("TON Result")))
							 {
								 if(!dupmap.containsKey(key))
								 {
									 dupmap.put(key, new String[]{currDataUnique.toString(), "1"});
								 }
								 else
								 {
									 dupmap.put(key, new String[]{currDataUnique.toString(), (Integer.valueOf(dupmap.get(key)[1]).intValue()+1)+""   });
								 }
							 }
						 }						 
						 
					 }					 
					 boolean noDuplicates = true;
					 for(String key: dupmap.keySet())
					 {
						 if(!dupmap.get(key)[1].isEmpty() && !dupmap.get(key)[1].equals("1"))
						 {
							 test.log(LogStatus.FAIL, "Duplicates appeared in subrules of RuleId: "+cuuRuleId
									 			+" <br/><br/>Following Data Appeared '"+dupmap.get(key)[1]+"' times"
									 					+ "+<br/>"+StringEscapeUtils.escapeHtml3(dupmap.get(key)[0]));
							 noDuplicates =false;
						 }
						 
					 }
					 
					 if(noDuplicates)
						 test.log(LogStatus.PASS, "No Duplicate appeared in subrules of RuleId: "+cuuRuleId);
				 }
				
				// Taking screenshot and Logging out
				cu.getScreenShot("Nagative validation");
						
				test = cu.getExTest();				
				msgInsHomePage.doLogOut(test);

				// Printing pass/fail in the test data sheet
				cu.checkRunStatus();
				
	}
	
	private List<Map<String, String>> getUniqeRows(List<Map<String, String>> subRuleRowData) {
		// 
		
		Map<String , Map<String, String>> tempMap = new LinkedHashMap<>();
		for(Map<String, String> subRuleMap : subRuleRowData)
		{
			String key = subRuleMap.get("OA Condition")+"~"+subRuleMap.get("TON Condition")+"~"+subRuleMap.get("OA Result")+"~"+subRuleMap.get("TON Result");
			tempMap.put(key, subRuleMap);
		}
		
		List<Map<String, String>> uniqueLst =  new LinkedList<>();
		for(String key : tempMap.keySet())
		{
			uniqueLst.add(tempMap.get(key));
		}
		return uniqueLst;
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
	
	
	Map<String, List<Map<String, String>>> getSubRuleUIData()
	{
		List<String> allRuleIds = cu.ElementsToList("filteringRules_AllRowIDs");
		
		Map<String, List<Map<String, String>>> retSubRuleData = new LinkedHashMap<>();
		
		for(String currRuleId : allRuleIds)
		{
			clickElementAfterScrolling("filteringRules_dynamic_Row_EditButton_BasedOn_RuleID", "$ruleid$", currRuleId);	
			driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
			int subRuleRowsSize = cu.ElementsToList("filteringRules_EditableRow_SubRule_AllRow").size();
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			if(subRuleRowsSize>0)
			{
				List<Map<String, String>> subRuleRows = new LinkedList<>();
				for(int i=1;i<subRuleRowsSize+1;i++)
				{					
					 String subRuleOACondition = cu.getTxtBoxValue("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", i+"");
					 String subRuleTONCondCondition = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "$index$", i+"").replace("--Select--", "");
					 String subRuleOAResult = cu.getTxtBoxValue("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "$index$", i+"");
					 String subRuleTONResult = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "$index$", i+"").replace("--Select--", "");
					 
					 Map<String, String> curMap= new LinkedHashMap<>();								
					 curMap.put("OA Condition", subRuleOACondition);
					 curMap.put("TON Condition", subRuleTONCondCondition);
					 curMap.put("OA Result", subRuleOAResult);
					 curMap.put("TON Result", subRuleTONResult);		
					 subRuleRows.add(curMap);
				}
				 retSubRuleData.put(currRuleId, subRuleRows);
			}
			else
			{
				List<Map<String, String>> subRuleRows = new LinkedList<>();				
				 Map<String, String> curMap= new LinkedHashMap<>();								
				 curMap.put("OA Condition", "");
				 curMap.put("TON Condition", "");
				 curMap.put("OA Result", "");
				 curMap.put("TON Result", "");	
				subRuleRows.add(curMap);
				retSubRuleData.put(currRuleId, subRuleRows);
			}			
			clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
		}
		
		return retSubRuleData;
	}
	
	  public void exportCSVAndValidateWithUI(CommonUtils cu,List<Map<String, String>> uiDataMapRows)  
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			clickElementAfterScrolling("filteringRules_ExportButton");
			cu.waitForPageLoad("");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
			String fileName ="SenderID";
			
			//validate file name
			String expectedFileName = fileName+".csv";
			if(csvFilePath.trim().contains(expectedFileName.trim()))
				test.log(LogStatus.PASS, "EXPECTECD: Exported file name should be in '"+fileName+".csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as '"+fileName+".csv' - '"+expectedFileName+"'</span>");
			else
			{
				cu.getScreenShot("Exported file name validation failed");
				test.log(LogStatus.FAIL, "EXPECTECD: Exported file name should be in '"+fileName+".csv' - '"+expectedFileName+"'", "Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in '"+fileName+".csv'  - '"+expectedFileName+"' Acutal file name: "+csvFilePath+"</span>");
			}
						
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			
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

}
