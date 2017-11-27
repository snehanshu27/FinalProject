package com.tata.selenium.test.supplierCostManagementCases;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_09_UploadWithValidCost implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_09_UploadWithValidCost.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/SupplierCostManagement.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - TC_09_UploadWithValidCost -with TestdataId: " + uniqueDataId);
		String sheetName = "Supplier_Cost_Management_Screen";

		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
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
			Assert.fail("Error occured while trying to login to the application  -  " + e);
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

		// Select the parameters
		cu.SelectDropDownByVisibleText("CostManagement_Supplier_NameLst",
				dataMap.get("CostManagement_Supplier_NameLst"));
		cu.validatePopulatedDropDownValue("CostManagement_Supplier_Acc_NameLst",
				dataMap.get("CostManagement_Supplier_Acc_NameLst"));
		cu.checkNonEditableBox("CostManagement_CurrencyTxt", dataMap.get("CostManagement_CurrencyTxt"));

		cu.clickElement("CostManagement_DisplayBtn");
		cu.getScreenShot("Validation Of the screen displayed");

		exportCSVAndCoverageFieldsUpdated(cu, dataMap);

		// Upload the modified excel
		modifyCoverageUsingUploadOption(cu, dataMap);

		// verifying the PDF file generated after submitting
		String parentWindow = cu.getCurrWindowName();
		cu.newWindowHandles(cu.getCurrWindowName());
		String newWindowTitle = cu.getTitle();

		if (cu.existsElement("pdfEmbed"))
			test.log(LogStatus.PASS, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after new coverage addition</span>");
		else {
			cu.getScreenShot("PDF file loading validation");
			test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after new coverage addition",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after new coverage addition -contains no pdf in title: acutal title : "
							+ newWindowTitle + "</span>");
		}
		cu.DriverClose();

		// switch parent window
		cu.switchToWindow(parentWindow);
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public void exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap) throws Exception {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("CostManagement_ExportBtn");
		cu.waitForPageLoad("Supplier Cost Management");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		String expectedFileName = "\\" + dataMap.get("CostManagement_Supplier_NameLst") + "-"
				+ dataMap.get("CostManagement_Supplier_Acc_NameLst") + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'</span>");

		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'CostManagement_Supplier_NameLst-CostManagement_Supplier_Acc_NameLst.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
			
		}

	}

	public void modifyCoverageUsingUploadOption(CommonUtils cu, Map<String, String> dataMap) throws Exception {
		// create csv file from input data
		String tempPath = System.getProperty("user.dir") + "\\temp\\" + cu.getCurrentTimeStamp();

		new File(tempPath).mkdirs();

		String csvFilePath = tempPath + "\\" + dataMap.get("CostManagement_Supplier_NameLst") + "-"
				+ dataMap.get("CostManagement_Supplier_Acc_NameLst") + "-Cost Update.csv";

		CSVWriter wr = new CSVWriter(new FileWriter(csvFilePath));
		List<String[]> allLines = new ArrayList<>();

		allLines.add(new String[] { "Country", "Company", "Destination", "MCC", "MNC", "Cost", "Effective Date" });

		allLines.add(new String[] { dataMap.get("CostManagement_Country_FilterLst"),
				dataMap.get("CostManagement_Company_FilterLst"), dataMap.get("CostManagement_Destination_FilterLst"),
				dataMap.get("CostManagement_Mcc_FilterLst"), dataMap.get("CostManagement_Mnc_FilterLst"),
				dataMap.get("CostManagement_EditCostTxt"), dataMap.get("CostManagement_EditEffectiveDateTxt") });

		wr.writeAll(allLines, false);
		wr.close();

		cu.printLogs("CSV file has been generated as the input: " + csvFilePath);
		test.log(LogStatus.PASS, "EXPECTECD: CSV file should be generated as the input",
				"Usage: <span style='font-weight:bold;'>ACTUAL:: EXPECTECD: CSV file has been generated (fileupload) as the input: "
						+ csvFilePath + "</span>");

		// upload the files
		cu.sendKeys("CostManagement_UploadBtn", csvFilePath, false);
		cu.sleep(2000);

		// Check warning message and accept popup
		cu.checkMessage("application_PopUpMessage", "Check popup waring message after uploading the file",
				"Warning: This action will upload the Selected CSV. Do you want to Continue?");
		cu.waitForPageLoad("");

		// Click submit button
		cu.clickElement("CostManagement_SubmitBtn");

		cu.checkMessage("application_PopUpTitle", "Clicking Submit button after passing cost and the calendar value",
				"The new cost(s) have been successfully entered and will become active on the effective date. You will receive a confirmation email for reference.");
	}

	public Map<String, String> getCurrentCoverageUIStatus(CommonUtils cu, String country) throws Exception {
		Map<String, String> ret = new HashMap<>();
		try {
			ret.put("Country", country);

			return ret;
		} catch (Exception e) {
			LOGGER.info("Get webtable coverage data for Country: " + country +" throwed exception :"+e);
			cu.getScreenShot("Get webtable coverage data for Country: " + country);
			test.log(LogStatus.FAIL,
					"EXPECTECD: Webtable coverage data for Country: " + country + " Should be obtained sucessfully",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Failed to get webtable coverage data for Country: "
							+ country + "</span>");
			return ret;
		}
	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("SupplierCostManagement");
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

}
