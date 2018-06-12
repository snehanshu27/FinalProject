package com.tata.selenium.test.Payable;

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

/**
 * @date 
 * @author Sonali Das
 * @description This class will perform a login and logout in MMX application
 */

public class TC_05_PayableValidateDataMTSMS implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_05_PayableValidateDataMTSMS.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/Payable.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId)  {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - TC_05_PayableValidateDataMTSMS -with TestdataId: " + uniqueDataId);
		String sheetName = "Payable_Screen";
		
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
		cu.selectDropDownByVisibleText("ServiceLst", dataMap.get("ServiceLst"));
		cu.selectDropDownByVisibleText("SupplierLst", dataMap.get("SupplierLst"));
		cu.selectDropDownByVisibleText("SupplierAccLst", dataMap.get("SupplierAccLst"));
		cu.selectDropDownByVisibleText("CountryLst", dataMap.get("CountryLst"));
		cu.selectDropDownByVisibleText("DestinationLst", dataMap.get("DestinationLst"));

		cu.clickElement("FromDate");
		cu.selectPreviousCalendarDate("FromDate", dataMap.get("FromDate"));
		cu.clickElement("ToDate");
		cu.selectPreviousCalendarDate("ToDate", dataMap.get("ToDate"));

		cu.clickElement("DisplayBtn");
		String popUpName=null;
		
		if(cu.existsElement("application_PopUpTitle"))
			 popUpName = cu.getText("application_PopUpMessage");
		
		if(!"No data for the selected input parameters".equalsIgnoreCase(popUpName)){
		
		String custAccName = cu.getText("dynamicSupplierAcc", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		//String accStatus = cu.getText("dynamicAccStatus", "$SupplierAcc$", dataMap.get("SupplierAccName"));
		//String product = cu.getText("dynamicProduct", "$SupplierAcc$", dataMap.get("SupplierAccName"));
		String country = cu.getText("dynamicCountry", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String destination = cu.getText("dynamicDestination", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String mcc = cu.getText("dynamicMCC", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String mnc = cu.getText("dynamicMNC", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String effdate = cu.getText("dynamicEffectiveDate", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String price = cu.getText("dynamicPrice", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String volume = cu.getText("dynamicVolume", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		String totalAmt = cu.getText("dynamicTotalAmt", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"));
		
		//Check row color is red if price is empty
		/*if("--".equals(price.trim()))
		{
			if(cu.existElement("rowColorPath", "$CustomerAcc$", dataMap.get("CustAccName")))
				test.log(LogStatus.PASS, "EXPECTECD: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row is in red colour since price is empty</span>");
			else
				test.log(LogStatus.FAIL, "EXPECTECD: Row should be in red colour if price is empty", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Row displayed in some othercolor than red color for price is empty</span>");
			
		}*/
		
		//compare UI and data sheet values for respective SupplierAcc
		if (cu.existsElement("Unique_Routing_Row", "$SupplierAccName$~$EffectiveDate$", dataMap.get("SupplierAccName")+"~"+dataMap.get("dynamicEffectiveDate"))) {
			
			test.log(LogStatus.PASS, "EXPECTECD: SupplierAccountName should be displayed",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  SupplierAccountName is displayed as - "
							+ custAccName + "</span>");
			if (dataMap.get("SupplierAccName").equalsIgnoreCase(custAccName)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: SupplierAccountName should be displayed as- " + dataMap.get("SupplierAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  SupplierAccountName is displayed as - "
								+ custAccName + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: CustomerAccountName should be displayed as- " + dataMap.get("SupplierAccName"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  CustomerAccountName is displayed as - "
								+ custAccName + "</span>");
			}

			/*if (dataMap.get("dynamicAccStatus").equalsIgnoreCase(accStatus)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: AccountStatus should be displayed as- " + dataMap.get("dynamicAccStatus"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  AccountStatus is displayed as - "
								+ accStatus + "</span>");
			}*/

			/*if (dataMap.get("dynamicProduct").equalsIgnoreCase(product)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: ProductName should be displayed as- " + dataMap.get("dynamicProduct"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  ProductName is displayed as - "
								+ product + "</span>");
			}*/

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
			
			if (dataMap.get("dynamicDestination").equalsIgnoreCase(destination)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: Destination should be displayed as- " + dataMap.get("dynamicDestination"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Destination is displayed as - "
								+ destination + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: Destination should be displayed as- " + dataMap.get("dynamicDestination"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Destination is displayed as - "
								+ destination + "</span>");
			}
			
			if (dataMap.get("dynamicMCC").equalsIgnoreCase(mcc)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: MCC should be displayed as- " + dataMap.get("dynamicMCC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MCC is displayed as - "
								+ mcc + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: MCC should be displayed as- " + dataMap.get("dynamicMCC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MCC is displayed as - "
								+ mcc + "</span>");
			}
			
			if (dataMap.get("dynamicMNC").equalsIgnoreCase(mnc)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: MNC should be displayed as- " + dataMap.get("dynamicMNC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MNC is displayed as - "
								+ mnc + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: MNC should be displayed as- " + dataMap.get("dynamicMNC"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  MNC is displayed as - "
								+ mnc + "</span>");
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
				test.log(LogStatus.PASS,
						"EXPECTECD: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - "
								+ price + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: Price should be displayed as- " + dataMap.get("dynamicPrice"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Price is displayed as - "
								+ price + "</span>");
			}

			if (dataMap.get("dynamicVolume").equalsIgnoreCase(volume)) {
				test.log(LogStatus.PASS,
						"EXPECTECD: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - "
								+ volume + "</span>");
			} else {
				test.log(LogStatus.FAIL,
						"EXPECTECD: Volume should be displayed as- " + dataMap.get("dynamicVolume"),
						"Validation:  <span style='font-weight:bold;'>ACTUAL::  Volume is displayed as - "
								+ volume + "</span>");
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
			test.log(LogStatus.FAIL, "EXPECTECD: SupplierAccountName should be displayed",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  SupplierAccountName did not get displayed in the page</span>");
			Assert.fail("Supplier Acc Name record did not get displayed.");
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
		cu.waitForPageLoad("Payables");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name= 
		String expectedFileName = "\\" + "Payables" + ".csv" ;
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTECD: Exported file name should be in 'Payables.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Payables.csv' - '"
							+ expectedFileName + "'</span>");

		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTECD: Exported file name should be in 'Payables.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Payables.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 1);

		Map<String, String> csvDatamap = csvu.getData("Effective Date",dataMap.get("dynamicEffectiveDate"));

		if ((csvDatamap.get("Country").equals(dataMap.get("dynamicCountry")))
				&& csvDatamap.get("Destination").equals(dataMap.get("dynamicDestination"))
				&& csvDatamap.get("MCC").equals(dataMap.get("dynamicMCC"))
				&& csvDatamap.get("MNC").equals(dataMap.get("dynamicMNC"))
				&& csvDatamap.get("Effective Date").equals(dataMap.get("dynamicEffectiveDate"))
				&& csvDatamap.get("Cost").equals(dataMap.get("dynamicPrice"))
				&& csvDatamap.get("Volume").equals(dataMap.get("Volume"))
				&& csvDatamap.get("Total Amount").equals(dataMap.get("TotalAmt")))

		{
			test.log(LogStatus.PASS, "EXPECTECD: The values should be same in both csv and UI",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: The values are same in both csv and UI'</span>");
		} else {

			String actualDiff ="Country_csv: "
					+ csvDatamap.get("Country") + " Country_UI: " + dataMap.get("dynamicCountry") + "\n   "
					+ "Effective_Date_csv: " + csvDatamap.get("Effective Date") 
					+ " Effective_Date_UI: "
					+ dataMap.get("dynamicEffectiveDate") + "\n   " 
					+ "Price_csv: " + csvDatamap.get("Cost")
					+ " Price_UI: " + dataMap.get("dynamicPrice") + "\n   " + "\n   " 
					+ "Volume_csv: "
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
		extent = ExtReport.instance("Payables");
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
