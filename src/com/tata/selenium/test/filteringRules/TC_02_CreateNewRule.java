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
import org.openqa.selenium.WebDriver;
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

public class TC_02_CreateNewRule implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_02_CreateNewRule.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/filteringRules.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	
	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - "+TC_02_CreateNewRule.class.getName()+" -with TestdataId: " + uniqueDataId);
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

				CommonUtils cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
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
				
				cu.clickElement("filteringRules_AddRuleButton");
				cu.sleep(500);
				
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_CoutryLst", dataMap.get("Country"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("Country")))
					cu.sleep(3000);
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_DestinationLst", dataMap.get("Destination"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("Destination")))
					cu.sleep(3000);
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SupplierNameLst", dataMap.get("SupplierName"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("SupplierName")))
					cu.sleep(3000);				
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SupplierAccNameLst", dataMap.get("SupplierAccountName"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("SupplierAccountName")))	
					cu.sleep(3000);
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_CustomerNameLst", dataMap.get("CustomerName"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("CustomerName")))	
					cu.sleep(3000);
				cu.SelectDropDownByVisibleText("filteringRules_EditableRow_CustomerAccNameLst", dataMap.get("CustomerAccountName"));
				if(!"ALL".equalsIgnoreCase(dataMap.get("CustomerAccountName")))	
					cu.sleep(3000);
				
				if(!dataMap.get("TON_BL_Cond").trim().isEmpty())					
					cu.SelectDropDownByVisibleText("filteringRules_EditableRow_TON_BL_CondLst", dataMap.get("TON_BL_Cond"));
				
				if(!dataMap.get("OA_BL_Cond").trim().isEmpty())
					cu.setData("filteringRules_EditableRow_OA_BL_CondTxt", dataMap.get("OA_BL_Cond"));
				
				if(!dataMap.get("OA_PassThrough").trim().isEmpty())
					cu.setData("filteringRules_EditableRow_OA_PassThroughTxt", dataMap.get("OA_PassThrough"));
				
				cu.scrollRightPage();
				
				if(!dataMap.get("SubRules_Json").trim().isEmpty())
				{
					List<Map<String, String>> subRules = getSubRules(dataMap.get("SubRules_Json"));		
					int i=1;
					for(Map<String, String> subRule : subRules)
					{
						cu.clickElement("filteringRules_EditableRow_SubRule_AddSubRuleButton");
						cu.sleep(500);
						
						cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OACondTxt", subRule.get("OA_Cond"), "$index$", i+"");
						cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONCondLst", subRule.get("TON_Cond"), "$index$", i+"");
						cu.setData("filteringRules_EditableRow_SubRule_Dynamic_OAResultTxt", subRule.get("OA_Result"), "$index$", i+"");
						cu.SelectDropDownByVisibleText("filteringRules_EditableRow_SubRule_Dynamic_TONResultLst", subRule.get("TON_Result"), "$index$", i+"");
						
						String currStatusAttribute = cu.getAttribute("filteringRules_EditableRow_SubRule_Dynamic_StatusToggleButton", "value", "$index$", i+"");						
						if(currStatusAttribute.equalsIgnoreCase(subRule.get("Status")))
						{
							cu.clickElement("filteringRules_EditableRow_SubRule_Dynamic_StatusToggleButton", "$index$", i+"");
							if("A".equalsIgnoreCase(subRule.get("Status")))
								cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Subrule-Ativate button", "Warning : Do you want to change status from Inactive to Active ?");
							else
								if("D".equalsIgnoreCase(subRule.get("Status")))
									cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Subrule-Deactivate button", "Warning : Do you want to change status from Active to Inactive ?");
						}
						
					}
				}
				
				if(!dataMap.get("Comments").trim().isEmpty())
					cu.setData("filteringRules_EditableRow_CommentsTxt", dataMap.get("Comments"));
				
				cu.scrollLeftPage();
				
				cu.clickElement("filteringRules_EditableRow_SaveButton");
				cu.sleep(500);
				
				cu.clickElement("filteringRules_SubmitButton");
				cu.sleep(500);
				cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Clicking Submit button", "Warning : Do you want to save the changes ?");
				cu.sleep(3000);
				cu.checkMessage("application_PopUpTitle", "Validating Pop Up after Accepting to proceed with changes", "Rule has been successfully provisioned.");
								
				// Taking screenshot and Logging out
				cu.getScreenShot("Validation Of UI parameters for Default Screen");
						
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
			map.put("OA_Cond", obj.getString("OA_Cond"));
			map.put("TON_Cond", obj.getString("TON_Cond"));
			map.put("OA_Result", obj.getString("OA_Result"));
			map.put("TON_Result", obj.getString("TON_Result"));
			map.put("Status", obj.getString("Status"));	
			allSubRules.add(map);
		}		
		
		return allSubRules;
	}
	
}
