package com.tata.selenium.test.productRoutingCases;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.CommonUtils1;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_04_RoutingCriteriaEditableValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_04_RoutingCriteriaEditableValidation.class.getName());
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
		test = extent.startTest("Execution triggered for - TC_04_RoutingCriteriaEditableValidation -with TestdataId: " + uniqueDataId);
		String sheetName = "Product_Routing_Screen";
		
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
			Assert.fail("Error occured while trying to login to the application  -  " +e);
		}

		test.log(LogStatus.INFO, "Launch Application",
				"Usage: <span style='font-weight:bold;'>Going to Launch App</span>");

		//CommonUtils cu = new CommonUtils(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		CommonUtils1 cu1 = new CommonUtils1(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		cu1.printLogs("Executing Test Case -" + testCaseId + " -with TestdataId : " + uniqueDataId);
		driver = cu1.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage = new LoginPage(driver, test, sheetName, uniqueDataId, testCaseId, properties);
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		cu1.waitForPageLoad("MessagingInstanceHomePage");

		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver, test, sheetName, uniqueDataId,
				testCaseId, properties);
		msgInsHomePage.verifyLogin(test, testCaseId, sheetName);

		NavigationMenuPage navMenuPage = new NavigationMenuPage(driver, test, sheetName, uniqueDataId, testCaseId,
				properties);
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu1.SwitchFrames("bottom");
		cu1.SwitchFrames("target");

		cu1.SelectDropDownByVisibleText("Product_NameLst", dataMap.get("Product_NameLst"));
		cu1.SelectDropDownByVisibleText("Product_CountryLst", dataMap.get("Product_CountryLst"));
		cu1.clickElement("Product_DisplayBtn");
		
		expandRouteHierarchy(cu1);
		
		cu1.checkNonEditableBox("Product_Routing_Destination_Criteria_TopLayer_Readonly_Textbox", dataMap.get("Product_Routing_CriteriaLst"), "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));
		cu1.checkEditableBox("Product_Routing_SupplierAccount_Priority_Textbox", "$CountryName$~$DestinationName$~$SupplierAccName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")+"~"+dataMap.get("SupplierAccName"));
		cu1.getScreenShot("current screen");
		
		test = cu1.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu1.checkRunStatus();

	}

	private void expandRouteHierarchy(CommonUtils1 cu1)
	{
		//Expand Country
		if(cu1.existsElement("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst")))
		{
			if(cu1.getText("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst")).contains("+"))
				cu1.clickElement("Product_Routing_Country_Expand_Symbol", "$CountryName$", dataMap.get("Product_CountryLst"));
		}
		else
		{
			test.log(LogStatus.FAIL,
					"EXPECTECD: Country: '"+dataMap.get("Product_CountryLst")+"' should be present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: Country: '"+dataMap.get("Product_CountryLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen</span>");
			Assert.fail("Country: '"+dataMap.get("Product_CountryLst")+"' is not present for the product: '"+dataMap.get("Product_NameLst")+"' in routing screen");
		}
		//Expand Destination
		if(cu1.existsElement("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")))
		{
			if(cu1.getText("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")).contains("+"))
				cu1.clickElement("Product_Routing_Destination_Expand_Symbol", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));			
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
