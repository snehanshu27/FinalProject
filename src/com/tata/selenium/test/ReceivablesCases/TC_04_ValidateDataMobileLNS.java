package com.tata.selenium.test.ReceivablesCases;

import java.util.HashMap;
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
public class TC_04_ValidateDataMobileLNS implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_ValidateDataMobileLNS.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/Receivables.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId)  {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - TC_04_ValidateDataMobileLNS -with TestdataId: " + uniqueDataId);
		String sheetName = "Receivables_Screen";
		
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

		// passing data
		cu.SelectDropDownByVisibleText("ServiceLst", dataMap.get("ServiceLst"));
		cu.SelectDropDownByVisibleText("CustomerLst", dataMap.get("CustomerLst"));
		cu.SelectDropDownByVisibleText("CustomerAccLst", dataMap.get("CustomerAccLst"));
		cu.SelectDropDownByVisibleText("CountryLst", dataMap.get("CountryLst"));
		cu.SelectDropDownByVisibleText("DestinationLst", dataMap.get("DestinationLst"));

		cu.clickElement("FromDate");
		cu.selectPreviousCalendarDate("FromDate", dataMap.get("FromDate"));
		cu.clickElement("ToDate");
		cu.selectPreviousCalendarDate("ToDate", dataMap.get("ToDate"));

		cu.clickElement("DisplayBtn");
		String popUpName=null;
		
		if(cu.existsElement("application_PopUpTitle"))
			 popUpName = cu.getText("application_PopUpMessage");

		if(!"No data for the selected input parameters".equalsIgnoreCase(popUpName)){
		
		String custAccName = cu.getText("dynamicCustomerAcc", "$CustomerAcc$", dataMap.get("CustAccName"));
		String accStatus = cu.getText("dynamicAccStatus", "$CustomerAcc$", dataMap.get("CustAccName"));
		String product = cu.getText("dynamicProduct", "$CustomerAcc$", dataMap.get("CustAccName"));
		String country = cu.getText("dynamicCountry", "$CustomerAcc$", dataMap.get("CustAccName"));
		String effdate = cu.getText("dynamicEffectiveDate", "$CustomerAcc$", dataMap.get("CustAccName"));
		String price = cu.getText("dynamicPrice", "$CustomerAcc$", dataMap.get("CustAccName"));
		String volume = cu.getText("dynamicVolume", "$CustomerAcc$", dataMap.get("CustAccName"));
		String totalAmt = cu.getText("dynamicTotalAmt", "$CustomerAcc$", dataMap.get("CustAccName"));

		//Check row colour is red if price is empty
		if("--".equals(price.trim()))
		{
			if(cu.existElement("rowColorPath", "$CustomerAcc$", dataMap.get("CustAccName")))
				test.log(LogStatus.PASS, "EXPECTECD: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row is in red colour since price is empty</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row displayed in some othercolor than red color for price is empty</span>");
			
		}
		
		//compare UI and data sheet values for respective CustomerAcc
		if (cu.existElement("dynamicCustomerAcc", "$CustomerAcc$", dataMap.get("CustAccName"))) {

			test.log(LogStatus.PASS, "EXPECTECD: CustomerAccountName should be displayed",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
							+ custAccName + "</span>");
			if (dataMap.get("CustomerAccLst").equalsIgnoreCase(custAccName)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: CustomerAccountName should be displayed as- " + dataMap.get("CustAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
								+ custAccName + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: CustomerAccountName should be displayed as- " + dataMap.get("CustAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
								+ custAccName + "</span>");
			}

			if (dataMap.get("dynamicAccStatus").equalsIgnoreCase(accStatus)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			}

			if (dataMap.get("dynamicProduct").equalsIgnoreCase(product)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			}

			if (dataMap.get("dynamicCountry").equalsIgnoreCase(country)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: CountryName should be displayed as- " + dataMap.get("dynamicCountry"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CountryName is displayed as - "
								+ country + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: CountryName should be displayed as- " + dataMap.get("dynamicCountry"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CountryName is displayed as - "
								+ country + "</span>");
			}

			if (dataMap.get("dynamicEffectiveDate").equalsIgnoreCase(effdate)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: UpcomingCostEffdate should be displayed as- " + dataMap.get("dynamicEffectiveDate"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  UpcomingCostEffdate is displayed as - "
								+ effdate + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: UpcomingCostEffdate should be displayed as- " + dataMap.get("dynamicEffectiveDate"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  UpcomingCostEffdate is displayed as - "
								+ effdate + "</span>");
			}

			if (dataMap.get("dynamicPrice").equalsIgnoreCase(price)) {
				test.log(LogStatus.PASS, "EXPECTECD: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - " + price
								+ "</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTECD: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - " + price
								+ "</span>");
			}

			if (dataMap.get("dynamicVolume").equalsIgnoreCase(volume)) {
				test.log(LogStatus.PASS, "EXPECTECD: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - " + volume
								+ "</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTECD: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - " + volume
								+ "</span>");
			}

			if (dataMap.get("dynamicTotalAmt").equalsIgnoreCase(totalAmt)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			}

		} else {
			test.log(LogStatus.FAIL, "EXPECTECD: CustomerAccountName should be displayed",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName did not get displayed in the page</span>");
			Assert.fail("Customer Acc Name record did not get displayed.");
		}

		exportCSVFieldsUpdated(cu, dataMap);

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		}else{
			cu.checkMessage("application_PopUpTitle", "Checking for any error when results are expected.", "No data for the selected input parameters");
		}

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public void exportCSVFieldsUpdated(CommonUtils cu, Map<String, String> dataMap)  {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("ExportBtn");
		cu.waitForPageLoad("Receivables");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + "Receivables" + ".csv";
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

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);

		Map<String, String> csvDatamap = csvu.getData("Customer Account", dataMap.get("CustAccName"));

		if (csvDatamap.get("Account Status").equals(dataMap.get("dynamicAccStatus"))
				&& csvDatamap.get("Product").equals(dataMap.get("dynamicProduct"))
				&& csvDatamap.get("Country").equals(dataMap.get("dynamicCountry"))
				&& csvDatamap.get("Effective Date").equals(dataMap.get("dynamicEffectiveDate"))
				&& csvDatamap.get("Price").equals(dataMap.get("dynamicPrice"))
				&& csvDatamap.get("Volume").equals(dataMap.get("Volume"))
				&& csvDatamap.get("Total Amount").equals(dataMap.get("TotalAmt")))

		{
			test.log(LogStatus.PASS, "EXPECTECD: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are same in both csv and UI'</span>");
		} else {

			String actualDiff = "Account_Status_csv: " + csvDatamap.get("Account Status") + " Account_Status_UI: "
					+ dataMap.get("dynamicAccStatus") + "\n   " + "Product_csv: " + csvDatamap.get("Product")
					+ " Product_UI: " + dataMap.get("dynamicProduct") + "\n   " + "Country_csv: "
					+ csvDatamap.get("Country") + " Country_UI: " + dataMap.get("dynamicCountry") + "\n   "
					+ "Effective_Date_csv: " + csvDatamap.get("Effective Date") + " Effective_Date_UI: "
					+ dataMap.get("dynamicEffectiveDate") + "\n   " + "Price_csv: " + csvDatamap.get("Price")
					+ " Price_UI: " + dataMap.get("dynamicPrice") + "\n   " + "\n   " + "Volume_csv: "
					+ csvDatamap.get("Volume") + " Volume_UI: " + dataMap.get("Volume") + "\n   "
					+ "Total_Amount_csv: " + csvDatamap.get("Total Amount") + " Total_Amount_UI: "
					+ dataMap.get("TotalAmt");

			test.log(LogStatus.FAIL, "EXPECTECD: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are NOT same in both csv and UI - Actual diifernce between UI and CSV is : "
							+ actualDiff + " </span>");
		}

	}

	@BeforeMethod
	@Parameters("testCaseId")
	public void beforeMethod(String testCaseId) throws Exception {
		DOMConfigurator.configure("log4j.xml");
		Log.startTestCase("Start Execution");
		Log.startTestCase(testCaseId);
		extent = ExtReport.instance("Receivables");
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
