package com.tata.selenium.test.edr.three.x;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

public class TC_01_EDRUIValidation implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_EDRUIValidation.class.getName());
	Map<String, String> dataMap = new HashMap<>();
	String properties = "./data/DeliveryStatistics.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO2(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest(
				"Execution triggered for - "+TC_01_EDRUIValidation.class.getName()+" -with TestdataId: " + uniqueDataId);
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

		if(cu.elementDisplayed("application_PopUpMessage", 2))
			cu.checkMessage("application_PopUpMessage", "After loading the page",
				"No data for the selected input parameters.");
		
	// Validating all editable drop down	
		//ServiceLst
		cu.checkEditableDropDown("DeliveryStat_ServiceLst", dataMap.get("DeliveryStat_ServiceLst"));	

		//CustomerLst
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole")))		
			cu.checkElementNotPresence("DeliveryStat_Customer_NameLst");		
		else			
			cu.checkEditableDropDown("DeliveryStat_Customer_NameLst",dataMap.get("DeliveryStat_Customer_NameLst"));
		
		
		//SupplierLst
		if("MMX-Customer Manager".equals(dataMap.get("UserRole")) 
				|| "MMX-Customer Finance".equals(dataMap.get("UserRole")) 
					|| "MMX-Customer Routing".equals(dataMap.get("UserRole"))  )		
			cu.checkElementNotPresence("DeliveryStat_Supplier_NameLst");		
		else			
			cu.checkEditableDropDown("DeliveryStat_Supplier_NameLst", dataMap.get("DeliveryStat_Supplier_NameLst"));
		
		//CountryLst
		cu.checkEditableDropDown("DeliveryStat_CountryLst", dataMap.get("DeliveryStat_CountryLst"));
		
		//DestinationLst
		cu.checkEditableDropDown("DeliveryStat_DestinationLst", dataMap.get("DeliveryStat_DestinationLst"));
		
		//ProductLst
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole")) 
				|| "MMX-Customer Manager".equals(dataMap.get("UserRole")) 
					|| "MMX-Customer Finance".equals(dataMap.get("UserRole")) 
						|| "MMX-Customer Routing".equals(dataMap.get("UserRole"))  )		
			cu.checkElementNotPresence("DeliveryStat_ProductListToggleButton");		
		else			
			cu.checkElementPresence("DeliveryStat_ProductListToggleButton");

		//InstanceLst
		if("MMX-Supplier Manager".equals(dataMap.get("UserRole")) 
				|| "MMX-Customer Manager".equals(dataMap.get("UserRole")) 
					|| "MMX-Customer Finance".equals(dataMap.get("UserRole")) 
						|| "MMX-Customer Routing".equals(dataMap.get("UserRole"))  )		
			cu.checkElementNotPresence("DeliveryStat_InstanceLst");		
		else			
			cu.checkEditableDropDown("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));
		
		//DimensionLst
		cu.checkEditableDropDown("DeliveryStat_DimensionLst", dataMap.get("Dimension"));	
		
		if (dataMap.get("DeliveryStat_FromDateTxt") != null && dataMap.get("DeliveryStat_FromDateTxt").trim().length() >0 ){
			cu.checkEditableDate("DeliveryStat_FromDateTxt",dataMap.get("DeliveryStat_FromDateTxt"));
		}else{
			String todayAsString = new SimpleDateFormat("dd-MM-yyyy 00:00").format(new Date());
			cu.checkEditableDate("DeliveryStat_FromDateTxt",todayAsString);
		}
		
		if (dataMap.get("DeliveryStat_ToDateTxt") != null && dataMap.get("DeliveryStat_ToDateTxt").trim().length() >0 ){
			cu.checkEditableDate("DeliveryStat_ToDateTxt",dataMap.get("DeliveryStat_ToDateTxt"));
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
	        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	        Date dtMain = new Date();
	        Date dtWOoff = new Date(dtMain.getTime());
	        Date dtW1minoff = new Date(dtMain.getTime() + 60000);
			String todayAsStringWOoff = sdf.format(dtWOoff);
			String todayAsStringW1minoff = sdf.format(dtW1minoff);
			
			if(todayAsStringWOoff.equals(cu.getAttribute("DeliveryStat_ToDateTxt", "value")))					
					cu.checkEditableDate("DeliveryStat_ToDateTxt",todayAsStringWOoff);
			else 
				if(todayAsStringW1minoff.equals(cu.getAttribute("DeliveryStat_ToDateTxt", "value")))		
					cu.checkEditableDate("DeliveryStat_ToDateTxt",todayAsStringW1minoff);
				else
					cu.checkEditableDate("DeliveryStat_ToDateTxt",todayAsStringWOoff);
		}
		
		// Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Delivery Statistics Screen");
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
			  
			  test.log(LogStatus.FAIL, "Error Ocuured in while executing the test case.", "Exception trace:<br/><br/> "
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
			LOGGER.info(" App Logout failed () :: Exception: " +e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}


}
