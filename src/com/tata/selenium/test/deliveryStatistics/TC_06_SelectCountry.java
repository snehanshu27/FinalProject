package com.tata.selenium.test.deliveryStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
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

public class TC_06_SelectCountry implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_06_SelectCountry.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - TC_04_SelectCustomerAccount -with TestdataId: " + uniqueDataId);
		String sheetName = "Delivery_Statistics_Screen";
		
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

		cu.checkMessage("application_PopUpMessage", "After loading the page",
				"No data for the selected input parameters.");

		WebElement drpEle = driver.findElement(By.xpath("//select[@id='dimension']"));
		Select drpSele = new Select(drpEle);
		drpSele.selectByVisibleText("Country");
		cu.clickElement("DeliveryStatisticsPage");
		
		WebElement drpSupEle = driver.findElement(By.xpath("//select[@id='suppName']"));
		Select drpSupSele = new Select(drpSupEle);
		drpSupSele.deselectAll();
		drpSupSele.selectByVisibleText("CENBONG INT'L HOLDINGS LIMITED");
		cu.clickElement("DeliveryStatisticsPage");
		
		
		// Select From DATE
		cu.moveAndClick("DeliveryStat_FromDateTxt");
		cu.sleep(2000);		
		cu.moveAndClick("selectMonth");
		cu.sleep(2000);
		cu.calMonth(dataMap.get("FromMonth"));
		cu.sleep(2000);
		cu.calDate(dataMap.get("FromDay"));
		cu.sleep(2000);
		cu.clickElement("clickOutside");

		// Select TO DATE
		cu.moveAndClick("DeliveryStat_ToDateTxt");
		cu.sleep(2000);
		cu.moveAndClick("selectMonth_ToDate");
		cu.sleep(2000);
		cu.calMonth(dataMap.get("ToMonth"));
		cu.sleep(2000);
		cu.calDate(dataMap.get("ToDay"));
		cu.sleep(2000);

		cu.clickElement("DeliveryStat_DisplayBtn");

		List<WebElement> countriesElemets = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[1]/a"));
		List<String> countriesValues = new ArrayList<>();
		for (WebElement ele : countriesElemets)
			countriesValues.add(ele.getText());

		for (String countryVal : countriesValues) {

			test.log(LogStatus.INFO, "$$$$$$$$$$$ Validating for Countries: "+countryVal);
			Map<String, Double> uiData = getUIVales(cu, countryVal);
			
			//checking % and enroute valdiations on main pagedd
			
			Double submitPerMainExpected = uiData.get("Submitted Failure Percentage");
			Double deliveredPerMainExpected = uiData.get("Delivered Percentage");
			Double deliveredFailedPerMainExpected = uiData.get("Failed Percentage");
			int enrouteMainExpected = (int) (uiData.get("Enroute")-0);
			
			Double submitPerMainActualRaw= (uiData.get("Submitted")/uiData.get("Attempted"))*100.0;
			Double submitPerMainActual= Math.round(submitPerMainActualRaw * 100.0) / 100.0;
			Double deliveredPerMainActualRaw= (uiData.get("Delivered")/uiData.get("Submitted"))*100.0;
			Double deliveredPerMainActual= Math.round(deliveredPerMainActualRaw * 100.0) / 100.0;			
			Double deliveredFailedPerMainActualRaw= (uiData.get("Failed")/uiData.get("Submitted"))*100.0;
			Double deliveredFailedPerMainActual= Math.round(deliveredFailedPerMainActualRaw * 100.0) / 100.0;			
			int enrouteMainActual=(int) (uiData.get("Submitted")-(uiData.get("Delivered")+uiData.get("Failed")));
			
			//main page Submitted Failure Percentage validation 
			if(String.valueOf(submitPerMainExpected).equals(String.valueOf(submitPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Submitted Failure Percentage (main page)for Country: "+countryVal+" should be "+submitPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+submitPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Submitted Failure Percentage (main page)for Country: "+countryVal+" should be "+submitPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+submitPerMainActual+"</span>");
			}
			
			//main page Delivered Percentage validation
			if(String.valueOf(deliveredPerMainExpected).equals(String.valueOf(deliveredPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Delivered Percentage (main page)for Country: "+countryVal+" should be "+deliveredPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+deliveredPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Delivered Percentage (main page)for Country: "+countryVal+" should be "+deliveredPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+deliveredPerMainActual+"</span>");
			}
			
			//main page Delivered Failed Percentage validation
			if(String.valueOf(deliveredFailedPerMainExpected).equals(String.valueOf(deliveredFailedPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Delivered Failed Percentage (main page)for Country: "+countryVal+" should be "+deliveredFailedPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+deliveredFailedPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Delivered Failed Percentage (main page)for Country: "+countryVal+" should be "+deliveredFailedPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+deliveredFailedPerMainActual+"</span>");
			}
			
			//main page Enroute validation
			if(enrouteMainExpected == enrouteMainActual)
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Enroute (main page)for Country: "+countryVal+" should be "+enrouteMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is same as  "+enrouteMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Enroute (main page)for Country: "+countryVal+" should be "+enrouteMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Country: "+countryVal+" is not same as "+enrouteMainActual+"</span>");
			}
			

			cu.clickElement("dynamicContryValue", "$ContryVal$", countryVal);

			//UI fields validation
			cu.checkElementPresence("exportBtn");
			cu.checkElementPresence("backBtn");

			cu.checkReadonlyProperty("DrillDown_CountryNameTxt");
			cu.checkReadonlyProperty("DrillDown_SupplierNameTxt");
			cu.checkReadonlyProperty("DeliveryStat_FromDateTxt");
			cu.checkReadonlyProperty("DeliveryStat_ToDateTxt");

			Map<String, Integer> csvData = exportCSVAndCoverageFieldsUpdated(cu, dataMap);

			// compare summation of CSV data (of detailed page )with UI data(of main page)
			for(String colName : csvData.keySet())
			{
//			    try {
					
			    	Integer uiValue = Integer.valueOf((int) (uiData.get(colName)-0));
			    	
					if(csvData.get(colName).equals(uiValue))					
						test.log(LogStatus.PASS,
								"EXPECTED: " + countryVal
										+ "The values of the csv data and the Ui Values of this Country "
										+ countryVal + " -> should be same. Filed Name : "+colName+"  - expected value: "+uiValue,
								"Validation:  <span style='font-weight:bold;'>ACTUAL::" + countryVal
										+ "The values of the csv data and the Ui Values of this Country"
										+ countryVal + " are same. Filed Name : "+colName+"  - expected value: "+csvData.get(colName)+"</span>");
					else
						test.log(LogStatus.FAIL,
								"EXPECTED: " + countryVal
								+ "The values of the csv data and the Ui Values of this Country"
								+ countryVal + " -> should be same. Filed Name : "+colName+"  - expected value: "+uiValue,
						"Validation:  <span style='font-weight:bold;'>ACTUAL::" + countryVal
								+ "The values of the csv data and the Ui Values of this Country"
								+ countryVal + " are not same. Filed Name : "+colName+"  - expected value: "+csvData.get(colName)+"</span>");
//				} catch (Exception e) {
//					
//					cu.printLogs("Exception occur while comparing the data  - " + e);
//					test.log(LogStatus.FAIL,"", "Exception occur while comparing the data  - " + e);
//				}
			}
			
			cu.clickElement("backBtn");

		}

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public Map<String, Double> getUIVales(CommonUtils cu, String countryValue)  {
		Map<String, Double> ret = new HashMap<>();

		try {
			ret.put("Attempted", Double.valueOf(cu.getText("DynamicAttempted_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Attempted Failure",Double.valueOf(cu.getText("DynamicAttemptedFailure_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submitted", Double.valueOf(cu.getText("DynamicSubmitted_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submit Failure", Double.valueOf(cu.getText("DynamicSubmittedFailure_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Submitted Failure Percentage", Double.valueOf(cu.getText("DynamicSubmittedFailurePercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));
			ret.put("Enroute", Double.valueOf(cu.getText("DynamicEnroute_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Delivered", Double.valueOf(cu.getText("DynamicDelivered_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Delivered Percentage", Double.valueOf(cu.getText("DynamicDeliveredPercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));
			ret.put("Failed", Double.valueOf(cu.getText("DynamicFailed_DimensionContry", "$ContryVal$", countryValue).replace(",", "")));
			ret.put("Failed Percentage", Double.valueOf(cu.getText("DynamicFailedPercentage_DimensionContry", "$ContryVal$", countryValue).replace("%", "")));

			return ret;
		} catch (Exception e) {
			LOGGER.error("Error occured while reading the data from UI -- "+e);
			cu.getScreenShot("Get webtable data for country Value: " + countryValue);
			test.log(LogStatus.FAIL,"", "Exception occur while fething the data from UI - " + e);
			return ret;
		}
	}

	public Map<String, Integer> exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap)
    {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("exportBtn");
		cu.waitForPageLoad("DeliveryStatistics");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + "Country-SMSCustomerAccountDistribution" + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTED: Exported file name should be in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTED: Exported file name should be in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Country-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 6);

		List<String> attemStrs = csvu.getSingleColAllData("Attempted");
		int AttemptedCount = csvu.getSingleColSum(attemStrs);

		List<String> attemFailureStrs = csvu.getSingleColAllData("Attempted Failure");
		int AttemptedFailureCount = csvu.getSingleColSum(attemFailureStrs);

		List<String> submittedStrs = csvu.getSingleColAllData("Submitted");
		int Submittedcount = csvu.getSingleColSum(submittedStrs);

		List<String> submittedFailureStrs = csvu.getSingleColAllData("Submit Failure");
		int SubmitedFailureCount = csvu.getSingleColSum(submittedFailureStrs);

		List<String> deliveredStrs = csvu.getSingleColAllData("Delivered");
		int DeliveredCount = csvu.getSingleColSum(deliveredStrs);

		
		Map<String, Integer> exceldata = new HashMap<>();
		try {
			exceldata.put("Attempted", AttemptedCount);
			exceldata.put("Attempted Failure", AttemptedFailureCount);
			exceldata.put("Submitted", Submittedcount);
			exceldata.put("Submit Failure", SubmitedFailureCount);
			exceldata.put("Delivered", DeliveredCount);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		return exceldata;
	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("DeliveryStatistics");
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

}
