package com.tata.com.selenium.test.RADailyReport;

import java.util.HashMap;
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

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.pages.LoginPage;
import com.tata.selenium.pages.MessagingInstanceHomePage;
import com.tata.selenium.pages.NavigationMenuPage;
//import com.tata.selenium.test.ReceivablesCases.TC_05_ValidateDataMTSMS;
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_05_RAValidateDataMTSMS implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_05_RAValidateDataMTSMS.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/RADailyReport.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId)  {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - TC_05_RAValidateDataMTSMS -with TestdataId: " + uniqueDataId);
		String sheetName = "RADailyReport_Screen";
		
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
		
	System.out.println(dataMap.get("FromDate"));
			System.out.println(dataMap.get("ToDate"));

		cu.clickElement("FromDate");
		cu.selectPreviousCalendarDate("FromDate", dataMap.get("FromDate"));
		cu.clickElement("ToDate");
		cu.selectPreviousCalendarDate("ToDate", dataMap.get("ToDate"));

		cu.clickElement("DisplayBtn");
		
		String popUpName=null;
		if(cu.existsElement("application_PopUpTitle"))
			 popUpName = cu.getText("application_PopUpMessage");
		
		if(!"No data for the selected input parameters".equalsIgnoreCase(popUpName)){
		
		String custAccName = cu.getText("dynamicCustomerAcc", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String accStatus = cu.getText("dynamicAccStatus", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String product = cu.getText("dynamicProduct", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String country = cu.getText("dynamicCountry", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String destination = cu.getText("dynamicDestination", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String mcc = cu.getText("dynamicMCC", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String mnc = cu.getText("dynamicMNC", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String effdate = cu.getText("dynamicEffectiveDate", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String price = cu.getText("dynamicPrice", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String volume = cu.getText("dynamicVolume", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String totalAmt = cu.getText("dynamicTotalAmt", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"));
		String volumediff = cu.getText("dynamicVolumeDiff", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate")); 
		//Check row colour is red if price is empty
		/*if("--".equals(price.trim()))
		{
			if(cu.existElement("rowColorPath", "$CustomerAcc$", dataMap.get("CustAccName")))
				test.log(LogStatus.PASS, "EXPECTED: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row is in red colour since price is empty</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTED: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row displayed in some othercolor than red color for price is empty</span>");
			
		}*/
		
		//compare UI and data sheet values for respective CustomerAcc
		if (cu.existElement("dynamicCustomerAcc", "$CustomerAcc$", dataMap.get("dynamicEffectiveDate"))) {
			
			test.log(LogStatus.PASS, "EXPECTED: CustomerAccountName should be displayed",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
							+ custAccName + "</span>");
			if (dataMap.get("CustAccName").equalsIgnoreCase(custAccName)) {
				test.log(LogStatus.PASS,
						"EXPECTED: CustomerAccountName should be displayed as- " + dataMap.get("CustAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
								+ custAccName + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: CustomerAccountName should be displayed as- " + dataMap.get("CustAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
								+ custAccName + "</span>");
			}

			if (dataMap.get("dynamicAccStatus").equalsIgnoreCase(accStatus)) {
				test.log(LogStatus.PASS,
						"EXPECTED: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			}

			if (dataMap.get("dynamicProduct").equalsIgnoreCase(product)) {
				test.log(LogStatus.PASS,
						"EXPECTED: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			}

			if (dataMap.get("dynamicCountry").equalsIgnoreCase(country)) {
				test.log(LogStatus.PASS,
						"EXPECTED: CountryName should be displayed as- " + dataMap.get("dynamicCountry"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CountryName is displayed as - "
								+ country + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: CountryName should be displayed as- " + dataMap.get("dynamicCountry"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CountryName is displayed as - "
								+ country + "</span>");
			}
			
			if (dataMap.get("dynamicDestination").equalsIgnoreCase(destination)) {
				test.log(LogStatus.PASS,
						"EXPECTED: Destination should be displayed as- " + dataMap.get("dynamicDestination"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Destination is displayed as - "
								+ destination + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: Destination should be displayed as- " + dataMap.get("dynamicDestination"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Destination is displayed as - "
								+ destination + "</span>");
			}
			
			if (dataMap.get("dynamicMCC").equalsIgnoreCase(mcc)) {
				test.log(LogStatus.PASS,
						"EXPECTED: MCC should be displayed as- " + dataMap.get("dynamicMCC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MCC is displayed as - "
								+ mcc + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: MCC should be displayed as- " + dataMap.get("dynamicMCC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MCC is displayed as - "
								+ mcc + "</span>");
			}
			
			if (dataMap.get("dynamicMNC").equalsIgnoreCase(mnc)) {
				test.log(LogStatus.PASS,
						"EXPECTED: MNC should be displayed as- " + dataMap.get("dynamicMNC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MNC is displayed as - "
								+ mnc + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: MNC should be displayed as- " + dataMap.get("dynamicMNC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MNC is displayed as - "
								+ mnc + "</span>");
			}

			if (dataMap.get("dynamicEffectiveDate").equalsIgnoreCase(effdate)) {
				test.log(LogStatus.PASS,
						"EXPECTED: UpcomingCostEffdate should be displayed as- " + dataMap.get("dynamicEffectiveDate"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  UpcomingCostEffdate is displayed as - "
								+ effdate + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: UpcomingCostEffdate should be displayed as- " + dataMap.get("dynamicEffectiveDate"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  UpcomingCostEffdate is displayed as - "
								+ effdate + "</span>");
			}

			if (dataMap.get("dynamicPrice").equalsIgnoreCase(price)) {
				test.log(LogStatus.PASS,
						"EXPECTED: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - "
								+ price + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - "
								+ price + "</span>");
			}

			if (dataMap.get("dynamicVolume").equalsIgnoreCase(volume)) {
				test.log(LogStatus.PASS,
						"EXPECTED: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - "
								+ volume + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - "
								+ volume + "</span>");
			}
			if (dataMap.get("dynamicTotalAmt").equalsIgnoreCase(totalAmt)) {
				test.log(LogStatus.PASS,
						"EXPECTED: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			}
			
			if (dataMap.get("dynamicTotalAmt").equalsIgnoreCase(totalAmt)) {
				test.log(LogStatus.PASS,
						"EXPECTED: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: TotalAmount should be displayed as- " + dataMap.get("dynamicTotalAmt"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  TotalAmount is displayed as - "
								+ totalAmt + "</span>");
			}
			
			if (dataMap.get("dynamicVolumeDiff").equalsIgnoreCase(volumediff)) {
				test.log(LogStatus.PASS,
						"EXPECTED: VolumeDiff should be displayed as- " + dataMap.get("dynamicVolumeDiff"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  VolumeDiff is displayed as - "
								+ volumediff + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTED: VolumeDiff should be displayed as- " + dataMap.get("dynamicVolumeDiff"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  VolumeDiff is displayed as - "
								+ volumediff + "</span>");
			}
		} else {
			test.log(LogStatus.FAIL, "EXPECTED: CustomerAccountName should be displayed",
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
		cu.waitForPageLoad("RA Daily Report");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name= 
		String expectedFileName = "\\" + "RA_Daily_Report" + ".csv" ;
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTED: Exported file name should be in 'RA_Daily_Report.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'RA_Daily_Report.csv' - '"
							+ expectedFileName + "'</span>");

		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTED: Exported file name should be in 'RA_Daily_Report.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'RA_Daily_Report.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);

		Map<String, String> csvDatamap = csvu.getData("Effective Date", dataMap.get("dynamicEffectiveDate"));

		if ((csvDatamap.get("Account Status").equals(dataMap.get("dynamicAccStatus")))
				&& csvDatamap.get("Product").equals(dataMap.get("dynamicProduct"))
				&& csvDatamap.get("Country").equals(dataMap.get("dynamicCountry"))
				&& csvDatamap.get("Destination").equals(dataMap.get("dynamicDestination"))
				&& csvDatamap.get("MCC").equals(dataMap.get("dynamicMCC"))
				&& csvDatamap.get("MNC").equals(dataMap.get("dynamicMNC"))
				&& csvDatamap.get("Effective Date").equals(dataMap.get("dynamicEffectiveDate"))
				&& csvDatamap.get("Price").equals(dataMap.get("dynamicPrice"))
				&& csvDatamap.get("Volume").equals(dataMap.get("Volume"))
				&& csvDatamap.get("Total Amount").equals(dataMap.get("TotalAmt"))
				&& csvDatamap.get("Volume Diff").equals(dataMap.get("VolumeDiff")))

		{
			test.log(LogStatus.PASS, "EXPECTED: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are same in both csv and UI'</span>");
		} else {

			String actualDiff = "Account_Status_csv: " + csvDatamap.get("Account Status") + " Account_Status_UI: "
					+ dataMap.get("dynamicAccStatus") + "\n   " 
					+ "Product_csv: " + csvDatamap.get("Product")
					+ " Product_UI: " + dataMap.get("dynamicProduct") + "\n   " 
					+ "Country_csv: "
					+ csvDatamap.get("Country") + " Country_UI: " + dataMap.get("dynamicCountry") + "\n   "
					+ "Effective_Date_csv: " + csvDatamap.get("Effective Date") 
					+ " Effective_Date_UI: "
					+ dataMap.get("dynamicEffectiveDate") + "\n   " 
					+ "Price_csv: " + csvDatamap.get("Price")
					+ " Price_UI: " + dataMap.get("dynamicPrice") + "\n   " + "\n   " 
					+ "Volume_csv: "
					+ csvDatamap.get("Volume") + " Volume_UI: " + dataMap.get("Volume") + "\n   "
					+ "Total_Amount_csv: " + csvDatamap.get("Total Amount") + " Total_Amount_UI: "
					+ dataMap.get("TotalAmt")+"Volume_Diff_csv: " + csvDatamap.get("Volume Diff") + " Volume_Diff_UI: "
							+ dataMap.get("VolumeDiff");

			test.log(LogStatus.FAIL, "EXPECTED: The values should be same in both csv and UI",
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
		extent = ExtReport.instance("RA Daily Report");
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
