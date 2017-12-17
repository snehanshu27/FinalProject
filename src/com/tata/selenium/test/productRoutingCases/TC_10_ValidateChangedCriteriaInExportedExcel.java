package com.tata.selenium.test.productRoutingCases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.tata.selenium.utils.CSVUtil1;
import com.tata.selenium.utils.CommonUtils1;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_10_ValidateChangedCriteriaInExportedExcel implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_10_ValidateChangedCriteriaInExportedExcel.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	
	String properties = "./data/ProductRouting.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_10_ValidateChangedCriteriaInExportedExcel -with TestdataId: " + uniqueDataId);
		String sheetName = "Product_Routing_Screen";
		
		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
			CommonUtils1.printConsole("Exception while reading data from EXCEL file for test case : " + testCaseId
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

		CommonUtils1 cu = new CommonUtils1(driver, test, sheetName, uniqueDataId, testCaseId, properties);
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

		cu.SelectDropDownByVisibleText("Product_NameLst", dataMap.get("Product_NameLst"));
		cu.SelectDropDownByVisibleText("Product_CountryLst", dataMap.get("Product_CountryLst"));
		cu.clickElement("Product_DisplayBtn");
		
		expandRouteHierarchy(cu);

		exportCSVAndCoverageFieldsUpdated(cu,dataMap);
		
		cu.getScreenShot("current screen");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}
	
	public void exportCSVAndCoverageFieldsUpdated(CommonUtils1 cu, Map<String, String> dataMap) throws Exception {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("Product_ExportBtn");
		cu.waitForPageLoad("Product Routing");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + dataMap.get("Product_NameLst");
		
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'Product_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Product_NameLst.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'Product_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Product_NameLst.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil1 csvu = new CSVUtil1(csvFilePath, 1);
		
		//get all rows of specific Destination
		List<Map<String, String>> destinationRows = csvu.getDataMultipleRows("Destination", dataMap.get("Product_DestinationLst"));		
		for(Map<String, String> desRow: destinationRows)
		{
			if(!desRow.get("Routing Criteria").equalsIgnoreCase(dataMap.get("Product_Routing_CriteriaLst")))
			{
				test.log(LogStatus.FAIL,
						"EXPECTECD: In the Exported file, the criteria for Destination: "+dataMap.get("Product_DestinationLst")+"has been changed to "
								+ dataMap.get("Product_Routing_CriteriaLst") + "'",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: In the Exported file, the criteria has not changed to"
								+ dataMap.get("Product_Routing_CriteriaLst") + "'. It is still having  criteria as '"+desRow.get("Routing Criteria")+"'</span>");
			}
			else{
				test.log(LogStatus.PASS,
						"EXPECTECD: In the Exported file, the criteria for Destination: "+dataMap.get("Product_DestinationLst")+"has been changed to "
								+ dataMap.get("Product_Routing_CriteriaLst") + "'",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: In the Exported file, the criteria has been changed to"
								+ dataMap.get("Product_Routing_CriteriaLst") + "'</span>");
			}
		}		
	}

	
	private void expandRouteHierarchy(CommonUtils1 cu)
	{
		//Expand Country
		if(cu.existsElement("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst")))
		{
			if(cu.getText("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst")).contains("+"))
				cu.clickElement("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst"));
		}
		else
		{
			test.log(LogStatus.FAIL,
					"EXPECTECD: Country: '"+dataMap.get("Product_CountryLst")+"' should be present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Country: '"+dataMap.get("Product_CountryLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen</span>");
			Assert.fail("Country: '"+dataMap.get("Product_CountryLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen");
		}
		//Expand Destination
		if(cu.existsElement("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")))
		{
			if(cu.getText("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")).contains("+"))
				cu.clickElement("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));			
		}
		else
		{
			test.log(LogStatus.FAIL,
					"EXPECTECD: Destination: '"+dataMap.get("Product_DestinationLst")+"' should be present for the product: '"+dataMap.get("Product_NameLst")+"' >> Country: '"+dataMap.get("Product_CountryLst")+"' in routing screen",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Destination: '"+dataMap.get("Product_DestinationLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' >> Country: '"+dataMap.get("Product_CountryLst")+"' in routing screen</span>");
			Assert.fail("Destination: '"+dataMap.get("Product_DestinationLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' >> Country: '"+dataMap.get("Product_CountryLst")+"' in routing screen");
		}
	}
	
	
	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Product_Routing");
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
