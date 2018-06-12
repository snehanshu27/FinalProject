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

public class TC_07_OALinksValidation implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_07_OALinksValidation.class.getName());
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
				"Execution triggered for - "+TC_07_OALinksValidation.class.getName()+" -with TestdataId: " + uniqueDataId);
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
				
				List<Map<String, String>> uiDataRows = getUIData();
				
				clickElementAfterScrolling("filteringRules_cancelButton");
				cu.waitForPageLoadWithSleep("", 500);
				clickElementAfterScrolling("filteringRules_diplayButton");
				cu.waitForPageLoadWithSleep("", 500);
				
				validateLinks(uiDataRows);
				
				
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
		cu.selectDropDownByVisibleText("filteringRules_CountryLst", dataMap.get("Country"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.selectDropDownByVisibleText("filteringRules_DestinationLst", dataMap.get("Destination"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.selectDropDownByVisibleText("filteringRules_SupplierNameLst", dataMap.get("SupplierName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.selectDropDownByVisibleText("filteringRules_SupplierAccNameLst", dataMap.get("SupplierAccountName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.selectDropDownByVisibleText("filteringRules_CustomerNameLst", dataMap.get("CustomerName"));
		cu.waitForPageLoadWithSleep("", 100);
		cu.selectDropDownByVisibleText("filteringRules_CustomerAccNameLst", dataMap.get("CustomerAccountName"));
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
		cu.selectDropDownByVisibleText(fieldName, value);
	}
	
	void selectDropDownByVisibleTextAfterScrolling(String fieldName, String value, String replaceKeys, String replaceValues)
	{
		cu.scrollPageToViewElement(fieldName);
		cu.SelectDropDownByVisibleText(fieldName, value, replaceKeys, replaceValues);
	}
	
	
	List<Map<String, String>> getUIData()
	{
		List<String> allRuleIds = cu.ElementsToList("filteringRules_AllRowIDs");
		
		List<Map<String, String>> retRowLst = new LinkedList<>();
		for(String currRuleId : allRuleIds)
		{
			
			String userModified= cu.getText("filteringRules_dynamic_Row_UserModifiedTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String lastModifiedDate= cu.getText("filteringRules_dynamic_Row_LastModifiedDateTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String status= cu.getText("filteringRules_dynamic_Row_CurrentStatusLabel_BasedOn_RuleID", "$ruleid$", currRuleId);
			
			clickElementAfterScrolling("filteringRules_dynamic_Row_EditButton_BasedOn_RuleID", "$ruleid$", currRuleId);	
			
			String country= cu.getText("filteringRules_dynamic_Row_CountryTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String destination= cu.getText("filteringRules_dynamic_Row_DestinationTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String supplier= cu.getText("filteringRules_dynamic_Row_SupplierTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String supplierAcc= cu.getText("filteringRules_dynamic_Row_SupplierAccTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String customer= cu.getText("filteringRules_dynamic_Row_CustomerTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String customerAcc= cu.getText("filteringRules_dynamic_Row_CustomerAccTdTxt_BasedOn_RuleID", "$ruleid$", currRuleId);
			String tON_BL_Cond = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_TON_BL_CondLst").replace("--Select--", "");
			String oA_BL_Cond = cu.getTxtBoxValue("filteringRules_EditableRow_OA_BL_CondTxt");
			String oA_PassThrough_Cond = cu.getTxtBoxValue("filteringRules_EditableRow_OA_PassThroughTxt");
			String comment = cu.getTxtBoxValue("filteringRules_EditableRow_CommentsTxt");
			
									
			driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
			int subRuleRowsSize = cu.ElementsToList("filteringRules_EditableRow_SubRule_AllRow").size();
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			if(subRuleRowsSize>0)
			{
				for(int i=1;i<subRuleRowsSize+1;i++)
				{					
					 String subRuleOACondition = cu.getTxtBoxValue("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", "$index$", i+"");
					 String subRuleTONCondCondition = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", "$index$", i+"").replace("--Select--", "");
					 String subRuleOAResult = cu.getTxtBoxValue("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", "$index$", i+"");
					 String subRuleTONResult = cu.getSelectVauleFromDropDown("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", "$index$", i+"").replace("--Select--", "");
					 String subRuleStatus = cu.getText("filteringRules_EditableRow_SubRule_Dynamic_CurrentStatusLabel", "$index$", i+"");
					 
					 Map<String, String> curMap= new LinkedHashMap<>();
						curMap.put("Rule ID", currRuleId);
						if(i==1)
						{
							curMap.put("OA Blacklist Condition", oA_BL_Cond);
							curMap.put("OA PassThrough", oA_PassThrough_Cond);			
						}
						else
						{
							curMap.put("OA Blacklist Condition", "");
							curMap.put("OA PassThrough", "");	
						}
						curMap.put("OA Condition", i+"~"+subRuleOACondition);						
						curMap.put("OA Result", i+"~"+subRuleOAResult);						
						retRowLst.add(curMap);
				}
			}
			else
			{
				Map<String, String> curMap= new LinkedHashMap<>();
				curMap.put("Rule ID", currRuleId);
				curMap.put("OA Blacklist Condition", oA_BL_Cond);
				curMap.put("OA PassThrough", oA_PassThrough_Cond);								
				curMap.put("OA Condition", "");				
				curMap.put("OA Result", "");			
				retRowLst.add(curMap);
			}
			
			clickElementAfterScrolling("filteringRules_EditableRow_SaveButton");
		}
		
		return retRowLst;
	}
	
	void validateLinks(List<Map<String, String>> uiDataMapRows)
	{
		int k=0;
		for(Map<String, String> uiDataMapRow : uiDataMapRows)
		{
			String ruleId = uiDataMapRow.get("Rule ID");
			if(k==0)
			{
				clickElementAfterScrolling("filteringRules_dynamic_Row_CountryTdTxt_BasedOn_RuleID",  "$ruleid$", ruleId);
				k++;
			}
			if(!uiDataMapRow.get("OA Blacklist Condition").isEmpty() && uiDataMapRow.get("OA Blacklist Condition").split(",").length >1)
			{
				String[] oABlCon = uiDataMapRow.get("OA Blacklist Condition").split(",");
				if(!cu.existElement("filteringRules_dynamic_Row_OA_BL_Cond_Link_BasedOn_RuleID", "$ruleid$", ruleId))
				{
					test.log(LogStatus.FAIL, "OA Blacklist Condition Link shouble be present", "OA Blacklist Condition Link is not present for ruleID: '"+ruleId
							+"' <br/> property: "+cu.getPropery("filteringRules_dynamic_Row_OA_BL_Cond_Link_BasedOn_RuleID", "$ruleid$", ruleId));
					cu.getScreenShot("OA Blacklist Condition Link shouble be present");
				}
				else
				{
					test.log(LogStatus.PASS, "OA Blacklist Condition Link shouble be present", "OA Blacklist Condition Link is present for ruleID: '"+ruleId+"'");
					clickElementAfterScrolling("filteringRules_dynamic_Row_OA_BL_Cond_Link_BasedOn_RuleID", "$ruleid$", ruleId);
					cu.sleep(1000);
					checkOARowsPopupWindow("RuleId: '"+ruleId+"'"
														,"OA Blacklist Cond"
																, oABlCon);
				}
			}
			
			if(!uiDataMapRow.get("OA PassThrough").isEmpty() && uiDataMapRow.get("OA PassThrough").split(",").length >1)
			{
				String[] oAPassThro = uiDataMapRow.get("OA PassThrough").split(",");
				if(!cu.existElement("filteringRules_dynamic_Row_OA_PassThrough_Link_BasedOn_RuleID", "$ruleid$", ruleId))
				{
					test.log(LogStatus.FAIL, "OA PassThrough Link shouble be present", "OA PassThrough Link is not present for ruleID: '"+ruleId
							+"' <br/> property: "+cu.getPropery("filteringRules_dynamic_Row_OA_PassThrough_Link_BasedOn_RuleID", "$ruleid$", ruleId));
					cu.getScreenShot("OA PassThrough Link shouble be present");
				}
				else
				{
					test.log(LogStatus.PASS, "OA PassThrough Link shouble be present", "OA PassThrough Link is present for ruleID: '"+ruleId+"'");
					clickElementAfterScrolling("filteringRules_dynamic_Row_OA_PassThrough_Link_BasedOn_RuleID", "$ruleid$", ruleId);
					cu.sleep(1000);
					checkOARowsPopupWindow("RuleId: '"+ruleId+"'","OA Passthrough List", oAPassThro);
				}
			}
			
			if(!uiDataMapRow.get("OA Condition").isEmpty() && uiDataMapRow.get("OA Condition").split(",").length >1)
			{
				String index = uiDataMapRow.get("OA Condition").split("\\~")[0];
				
				String[] oABlCon = uiDataMapRow.get("OA Condition").split("\\~")[1].split(",");
				if(!cu.existElement("filteringRules_dynamic_Row_SubRule_Dynamic_OACond_Link_BasedOn_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index))
				{
					test.log(LogStatus.FAIL, "OA Condition Link shouble be present", "OA Condition Link is not present for ruleID: '"+ruleId+"'"+" SubRuleRow: '"+index+"' <br/>"
							+"' <br/> property: "+cu.getPropery("filteringRules_dynamic_Row_SubRule_Dynamic_OACond_Link_BasedOn_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index));
					cu.getScreenShot("OA Condition Link shouble be present");
				}
				else
				{
					test.log(LogStatus.PASS, "OA Condition Link shouble be present", "OA Condition Link is present for ruleID: '"+ruleId+"'"+" SubRuleRow: '"+index+"'");
					//cu.scrollPageToViewElement("filteringRules_dynamic_Row_EditButton_BasedOn_RuleID", "$ruleid$", ruleId);
					clickElementAfterScrolling("filteringRules_dynamic_Row_SubRule_Dynamic_OACond_Link_BasedOn_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index);
					//cu.clickElement("filteringRules_dynamic_Row_SubRule_Dynamic_OACond_Link_BasedOn_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index);
					cu.sleep(1000);
					checkOARowsPopupWindow("RuleId: '"+ruleId+"'"+" SubRuleRow: '"+index+"'"
													,"OA Condition"
														, oABlCon);
				}
			}
			
			if(!uiDataMapRow.get("OA Result").isEmpty() && uiDataMapRow.get("OA Result").split(",").length >1)
			{
				String index = uiDataMapRow.get("OA Result").split("\\~")[0];
				
				String[] oABlCon = uiDataMapRow.get("OA Result").split("\\~")[1].split(",");
				if(!cu.existElement("filteringRules_dynamic_Row_SubRule_Dynamic_OAResult_Link_BasedOn_RuleID_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index))
				{
					test.log(LogStatus.FAIL, "OA Result Link shouble be present", "OA Result Link is not present for ruleID: '"+ruleId+"'"+" SubRuleRow: '"+index+"' <br/>"
							+"' <br/> property: "+cu.getPropery("filteringRules_dynamic_Row_SubRule_Dynamic_OAResult_Link_BasedOn_RuleID_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index));
					cu.getScreenShot("OA Result Link shouble be present");
				}
				else
				{
					test.log(LogStatus.PASS, "OA Result Link shouble be present", "OA Result Link is present for ruleID: '"+ruleId+"'"+" SubRuleRow: '"+index+"'");
					clickElementAfterScrolling("filteringRules_dynamic_Row_SubRule_Dynamic_OAResult_Link_BasedOn_RuleID_RuleIDAndIndex", "$ruleid$~$index$", ruleId+"~"+index);
					cu.sleep(1000);
					checkOARowsPopupWindow("RuleId: '"+ruleId+"'"+" SubRuleRow: '"+index+"'"
													,"OA Condition"
														, oABlCon);
				}
			}
		}			
	}

	private void checkOARowsPopupWindow(String refStr, String oaType , String[] oABlCon) {
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
			
			Set<String> textBoxSet = new LinkedHashSet<String>();
			Set<String> popUpSet = new LinkedHashSet<String>();
			Arrays.asList(oABlCon).addAll(textBoxSet);			
			cu.ElementsToList("filteringRules_NewPop_OAListColoums").addAll(popUpSet);
			
			if(textBoxSet.equals(popUpSet))			
				test.log(LogStatus.PASS, "All values in "+oaType+"  textbox and window should be matched", "All values in "+oaType+"  textbox and window have matched for "+refStr);
			
			else
			{
				List<String> missLstinTextBox = getMissingEle(textBoxSet, popUpSet);
				List<String> missLstinPopup = getMissingEle(popUpSet, textBoxSet);
				test.log(LogStatus.FAIL, "All values in "+oaType+"  textbox and window should be matched", "Values in "+oaType+"  textbox and window not matched for "+refStr
												+". Missing lst in Textbox <br/>"+missLstinTextBox.toString()+". <br/>Missing lst in popwindow <br/>"+missLstinPopup.toString());
				cu.getScreenShot("values in "+oaType+"  textbox and window not Macthed");				
			}
			
			driver.close();
			cu.switchToWindow(paraentWinName);
			cu.SwitchFrames("bottom");
			cu.SwitchFrames("target");
		}
		else
		{
			test.log(LogStatus.FAIL, "Popup window should be opened after clicking on the OA link", "Popup window is not opened after clicking on the "+oaType+" link");
		}
	}
	
	 List<String> getMissingEle(Set<String> set1, Set<String> set2)
	 {
		 List<String> retList = new LinkedList<>();
		 for(String ele1 : set1)
		 {
			 if(!set2.contains(ele1))
				 retList.add(ele1);
		 }
		 
		 return retList;
	 }
}
