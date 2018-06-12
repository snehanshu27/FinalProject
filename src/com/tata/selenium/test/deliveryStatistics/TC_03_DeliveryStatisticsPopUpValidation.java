package com.tata.selenium.test.deliveryStatistics;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_03_DeliveryStatisticsPopUpValidation implements ApplicationConstants {

	private static final Logger LOGGER = Logger.getLogger(TC_03_DeliveryStatisticsPopUpValidation.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - TC_03_DeliveryStatisticsPopUpValidation -with TestdataId: " + uniqueDataId);
		String sheetName = "Delivery_Statistics_Screen";

		// Reading excel values
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH, sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		} catch (Exception e) {
			LOGGER.info("Exception while reading data from EXCEL file for test case : " + testCaseId
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

		if(cu.elementDisplayed("application_PopUpMessage", 2))
			cu.checkMessage("application_PopUpMessage", "After loading the page",
				"No data for the selected input parameters.");

		//Select filters
				//Select ServiceLst filter
				if(!dataMap.get("DeliveryStat_ServiceLst").trim().isEmpty())
					cu.selectDropDownByVisibleText("DeliveryStat_ServiceLst", dataMap.get("DeliveryStat_ServiceLst"));
				
				//Select Dimension filter
				if(!dataMap.get("Dimension").trim().isEmpty())
					cu.selectDropDownByVisibleText("DeliveryStat_DimensionLst", dataMap.get("Dimension"));
				
				//Select Customer filter
				if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole")) && !dataMap.get("DeliveryStat_Customer_NameLst").trim().isEmpty())
				{
					cu.deselectDropDownAllOptions("DeliveryStat_Customer_NameLst");
					cu.selectDropDownByVisibleText("DeliveryStat_Customer_NameLst", dataMap.get("DeliveryStat_Customer_NameLst"));
					cu.clickElement("DeliveryStatisticsPage");
					cu.waitForPageLoadWithSleep("", 500);
				}
				
				//Select Supplier filter
				if(!"MMX-Customer Manager".equals(dataMap.get("UserRole")) 
						&& !"MMX-Customer Finance".equals(dataMap.get("UserRole")) 
							&& !"MMX-Customer Routing".equals(dataMap.get("UserRole"))  )
				{
					if(!dataMap.get("DeliveryStat_Supplier_NameLst").trim().isEmpty())
					{
						cu.deselectDropDownAllOptions("DeliveryStat_Supplier_NameLst");
						cu.selectDropDownByVisibleText("DeliveryStat_Supplier_NameLst", dataMap.get("DeliveryStat_Supplier_NameLst"));
						cu.clickElement("DeliveryStatisticsPage");
						cu.waitForPageLoadWithSleep("", 500);
					}
				}
				
				//Select Country filter
				if(!dataMap.get("DeliveryStat_CountryLst").trim().isEmpty())
				{
					cu.deselectDropDownAllOptions("DeliveryStat_CountryLst");
					cu.selectDropDownByVisibleText("DeliveryStat_CountryLst", dataMap.get("DeliveryStat_CountryLst"));
					cu.clickElement("DeliveryStatisticsPage");
					cu.waitForPageLoadWithSleep("", 500);
				}
				
				//Select Destination filter
				if(!dataMap.get("DeliveryStat_DestinationLst").trim().isEmpty())
				{
					cu.deselectDropDownAllOptions("DeliveryStat_DestinationLst");
					cu.selectDropDownByVisibleText("DeliveryStat_DestinationLst", dataMap.get("DeliveryStat_DestinationLst"));
					cu.clickElement("DeliveryStatisticsPage");
					cu.waitForPageLoadWithSleep("", 500);
				}
				
				//Select Product filter
				if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole")) 
						&& !"MMX-Customer Manager".equals(dataMap.get("UserRole")) 
							&& !"MMX-Customer Finance".equals(dataMap.get("UserRole")) 
								&& !"MMX-Customer Routing".equals(dataMap.get("UserRole"))  )	
				{
					if(!dataMap.get("DeliveryStat_ProductLst").isEmpty() && !dataMap.get("DeliveryStat_ProductLst").contains("Select ALL"))
					{
						cu.clickElement("DeliveryStat_ProductListToggleButton");
						cu.unSelectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", "Select ALL");
						for(String currProduct : dataMap.get("DeliveryStat_ProductLst").split("\\~"))
						{
							cu.setData("DeliveryStat_ProductListSearchTxtBox", currProduct);
							cu.sleep(500);
							cu.selectCheckBox("DeliveryStat_ProductList_Dynamic_Checkbox", "$productname$", currProduct);
						}
						cu.clickElement("DeliveryStat_ProductLabel");
						cu.waitForPageLoadWithSleep("", 500);						
					}
				}
				
				//Select Instance filter
				if(!"MMX-Supplier Manager".equals(dataMap.get("UserRole")) 
						&& !"MMX-Customer Manager".equals(dataMap.get("UserRole")) 
							&& !"MMX-Customer Finance".equals(dataMap.get("UserRole")) 
								&& !"MMX-Customer Routing".equals(dataMap.get("UserRole"))  )		
				{
					if(!dataMap.get("DeliveryStat_InstanceLst").trim().isEmpty())
					{
						cu.deselectDropDownAllOptions("DeliveryStat_InstanceLst");
						cu.selectDropDownByVisibleText("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));
						cu.clickElement("DeliveryStatisticsPage");
						cu.waitForPageLoadWithSleep("", 500);
					}
				}
				
		// Select From DATE
		cu.moveAndClick("DeliveryStat_FromDateTxt");
		Thread.sleep(2000);

		cu.moveAndClick("clickYear");
		Thread.sleep(2000);
		cu.calYear(dataMap.get("FromYear"));
		Thread.sleep(2000);
		cu.moveAndClick("selectMonth");
		Thread.sleep(2000);
		cu.calMonth(dataMap.get("FromMonth"));
		Thread.sleep(2000);
		cu.calDate(dataMap.get("FromDay"));
		Thread.sleep(2000);
		cu.clickElement("clickOutside");

		// Select TO DATE
		cu.moveAndClick("DeliveryStat_ToDateTxt");
		Thread.sleep(2000);
		cu.moveAndClick("clickYear");
		Thread.sleep(2000);
		cu.calYear(dataMap.get("ToYear"));

		cu.moveAndClick("selectMonth_ToDate");
		Thread.sleep(2000);
		cu.calMonth(dataMap.get("ToMonth"));
		Thread.sleep(2000);
		cu.calDate(dataMap.get("ToDay"));
		Thread.sleep(2000);
		cu.clickElement("DeliveryStatisticsPage");

		cu.clickElement("DeliveryStat_DisplayBtn");

		cu.checkMessage("application_PopUpMessage", "Displaying the data",
				"Error: To Date should be greater than or equal to From");
		//cu.getScreenShot("Validate the pop up message on the screen");

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
		extent = ExtReport.instance("DeliveryStatistics");
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
			LOGGER.info(" App Logout failed () :: Exception: " + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
