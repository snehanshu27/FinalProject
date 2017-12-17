package com.tata.selenium.test.productRoutingCases;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import com.tata.selenium.utils.CommonUtils1;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_12_RoutingPriorityChange implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(TC_12_RoutingPriorityChange.class.getName());
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
		test = extent
				.startTest("Execution triggered for - TC_12_RoutingPriorityChange -with TestdataId: " + uniqueDataId);
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
		cu.selectCheckBox("Product_Routing_Destination_Criteria_CheckBox", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));
		cu.SelectDropDownByVisibleText("Product_Routing_Destination_Criteria_Dropdown", dataMap.get("Product_Routing_CriteriaLst"), "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));
		
		setPriorty(cu);
		
		cu.clickElement("Product_SubmitBtn");
		
		// switch to new window and confim the changes
		String parentWindow = cu.getCurrWindowName();
		cu.sleep(1000);
		if(cu.getCurrWindowNames().size()==1)
		{
			test.log(LogStatus.FAIL,
					"EXPECTECD: New window should be opened after clicking on submit button",
					"Validation:  <span style='font-weight:bold;'>ACTUAL:: New window is not opened after clicking on submit button</span>");
			Assert.fail("New window is not opened after clicking on submit button");
		
		}
		cu.newWindowHandles(parentWindow);
		cu.clickElement("Product_Routing_Confirm_Button");
		
		cu.checkMessage("application_PopUpMessage", "Changing routing criteria",
				"The Product Route for selected Destination has been successfully updated");
		
		

		// switch parent window
		cu.switchToWindow(parentWindow);
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");			
				
		cu.checkEditableDropDown("Product_NameLst", "--Select--");
		cu.checkEditableDropDown("Product_CountryLst", "--Select--");		

		navMenuPage.enableRouting();

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

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
	
	private void setPriorty(CommonUtils1 cu)
	{	
		String[] suppliers = dataMap.get("SupplierAccName").split("\\~");
		String[] priorities = dataMap.get("Product_Routing_PriorityTxt").split("\\~");
		
		for(int i=0; i<suppliers.length; i++)
		{		
			cu.clearTextBox("Product_Routing_SupplierAccount_Priority_Textbox", "$CountryName$~$DestinationName$~$SupplierAccName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")+"~"+suppliers[i]);		
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			cu.acceptUIPopupMessage();		
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			cu.sendKeys("Product_Routing_SupplierAccount_Priority_Textbox", priorities[i], false, "$CountryName$~$DestinationName$~$SupplierAccName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst")+"~"+suppliers[i]);
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			cu.acceptUIPopupMessage();
			if(i==suppliers.length-1)
			{
				cu.clickElementNOExtRep("Product_Routing_Destination_Criteria_TopLayer_Readonly_Textbox", "$CountryName$~$DestinationName$", dataMap.get("Product_CountryLst")+"~"+dataMap.get("Product_DestinationLst"));
				cu.acceptUIPopupMessage();
			}
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
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
