package com.tata.selenium.test.productProvisioningCases;

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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_08_VerifyExcel implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_08_VerifyExcel.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/ProductProvisioning.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId){
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_08_VerifyExcel -with TestdataId: " + uniqueDataId);
		String sheetName = "Product_Provisioning_Screen";
		
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

		cu.SelectDropDownByVisibleText("Product_Provisioning_ProductNameLst",
				dataMap.get("Product_Provisioning_ProductNameLst"));

		cu.clickElement("Product_Provisioning_DisplayBtn");

		exportCSVAndCoverageFieldsUpdated(cu, dataMap);

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public void exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap) {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("Product_Provisioning_ExportBtn");
		cu.waitForPageLoad("Product Provisioning");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + dataMap.get("Product_Provisioning_ProductNameLst") + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Supplier Name-Supplier Account Name.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);
		
			List<String> attemStrs = csvu.getSingleColAllData("Priority (PClass)");
			int len = attemStrs.size();
			
			for (int i = 0; i < len; i++) {
				if((attemStrs.get(i)).equals(dataMap.get("Product_Provisioning_PriorityLst")))
				{
					test.log(LogStatus.PASS,
							"EXPECTECD: In the Exported file, the priority has been changed to "
									+ dataMap.get("Product_Provisioning_PriorityLst") + "'",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: In the Exported file, the priority has been changed to"
									+ dataMap.get("Product_Provisioning_PriorityLst") + "' -refDataRowNumber: "+i+"</span>");
				}
				else{
					test.log(LogStatus.FAIL,
							"EXPECTECD: In the Exported file, the priority has been changed to "
									+ dataMap.get("Product_Provisioning_PriorityLst") + "'",
							"Usage: <span style='font-weight:bold;'>ACTUAL:: In the Exported file, the priority has been changed to"
									+ dataMap.get("Product_Provisioning_PriorityLst") + "' -refDataRowNumber: "+i+"</span>");
				}
					
			}
		
	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Product_Provisioning");
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
