package com.tata.selenium.test.fixeddropdown;

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
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

/**
 * @date
 * @author Maqdoom Sharief
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_01_ValidateOptionsInFixedDropdowns implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_01_ValidateOptionsInFixedDropdowns.class.getName());
	String properties = "./data/dropdownOptionInAllPages.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) throws Exception {
		// Starting the extent report
		test = extent.startTest("Execution triggered for - TC_01_ValidateOptionsInFixedDropdowns - " + uniqueDataId + " for "
				+ dataMap.get("Description"));
		String sheetName = "ValidatingOptions_FixedDropdown";

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
		cu.default_content();

		// Validating user for eXchange Tab Access
		if (("YES").equalsIgnoreCase(dataMap.get("Dashboard"))) {
			if (("YES").equalsIgnoreCase(dataMap.get("Country Status"))) {				
				//navigate to tab
				navigateToSubPage(cu, "Dashboard-Country Status", "dashboard_Tab", "Country Status");	
				//Dropdowns validation
				validateRecords(cu, "cs_service", dataMap.get("CS_Service_Val"));
				validateRecords(cu, "cs_destinationCountry", dataMap.get("CS_DestinationCountry_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Country Status screen");
			}

			if (("YES").equalsIgnoreCase(dataMap.get("Performance Trend"))) {
				//navigate to tab
				navigateToSubPage(cu, "Dashboard-Performance Trend", "dashboard_Tab", "Performance Trend");	
				//Dropdowns validation
				validateRecords(cu, "pt_ServiceLst", dataMap.get("PT_ServiceLst_Val"));				
				validateRecords(cu, "pt_CountryLst", dataMap.get("PT_CountryLst_Val"));				
				validateRecords(cu, "pt_DestinationLst", dataMap.get("PT_DestinationLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Performance Trend screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Destination Trend"))) {
				//navigate to tab
				navigateToSubPage(cu, "Dashboard-Destination Trend", "dashboard_Tab", "Destination Trend");	
				//Dropdowns validation
				validateRecords(cu, "dt_ServiceLst", dataMap.get("DT_ServiceLst_Val"));				
				validateRecords(cu, "dt_CountryLst", dataMap.get("DT_CountryLst_Val"));				
				validateRecords(cu, "dt_DestinationLst", dataMap.get("DT_DestinationLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Destination Trend screen");
			}
		}

		if (("YES").equalsIgnoreCase(dataMap.get("Customer"))) {
			navigateToMainPage(cu, "Customer", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Customer_Provisioning"))) {
				//navigate to tab
				navigateToSubPage(cu, "Customer Provisioning", "Customer_Tab", "Provisioning");	
				//Dropdowns validation ( common )
				validateRecords(cu, "cusProvisioning_InstanceLst", dataMap.get("CP_InstanceLst_Val"));
				validateRecords(cu, "cusProvisioning_AccountStausLst", dataMap.get("CP_Account_Status"));				
				
				//Dropdowns in validationInstance_InfoTab
				cu.clickElement("cusProvisioning_Instance_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "cusProvisioning_Ins_InstanceStateLst", dataMap.get("CP_Ins_InstanceStateLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Customer Provisioning-Instance InfoTab screen");
				
				//Dropdowns in Traffic_InfoTab
				cu.clickElement("cusProvisioning_Traffic_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "cusProvisioning_Traffic_TONBlacklistLst", dataMap.get("CP_Traffic_TONBlacklistLst_Val"));
				validateRecords(cu, "cusProvisioning_Traffic_EnhancedDLRParameterLst", dataMap.get("CP_Traffic_EnhancedDLRParameterLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Customer Provisioning-Traffic InfoTab screen");
				
				//Dropdowns in SMPP_InfoTab
				cu.scrollDownPage();
				cu.clickElement("cusProvisioning_SMPP_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "cusProvisioning_SMPP_SMPPVersionLst", dataMap.get("CP_SMPP_SMPPVersionLst_Val"));
				validateRecords(cu, "cusProvisioning_SMPP_SMSCDefaultAlphabetLst", dataMap.get("CP_SMPP_SMSCDefaultAlphabetLst_Val"));
				validateRecords(cu, "cusProvisioning_SMPP_MsgIDTypeLst", dataMap.get("CP_SMPP_MsgIDTypeLst_Val"));
				validateRecords(cu, "cusProvisioning_SMPP_MsgIDLengthLst", dataMap.get("CP_SMPP_MsgIDLengthLst_Val"));
				validateRecords(cu, "cusProvisioning_SMPP_DataCodingSupportedLst", dataMap.get("CP_SMPP_DataCodingSupportedLst_Val"));
				validateRecords(cu, "cusProvisioning_SMPP_EnhancedDLRFormatLst", dataMap.get("CP_SMPP_EnhancedDLRFormatLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Customer Provisioning-SMPP InfoTab screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Customer_Price_List_Emailer"))) {				
				//navigate to tab
				navigateToSubPage(cu, "Customer_Price_List_Emailer", "Customer_Tab", "Price List Emailer");	
				//Dropdowns validation
				validateRecords(cu, "cusPriceListEmailer_ServiceLst", dataMap.get("CPLE_ServiceLst_Val"));				
				cu.getScreenShot("Validation of options in fixed Dropdowns for Customer_Price_List_Emailer screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Customer_Price_Management"))) {
				//navigate to tab
				navigateToSubPage(cu, "Customer_Price_Management", "Customer_Tab", "Price Management");	
				//Dropdowns validation
				validateRecords(cu, "cusPriceManagement_ServiceLst", dataMap.get("CPM_ServiceLst_Val"));				
				cu.getScreenShot("Validation of options in fixed Dropdowns for Customer Price Management screen");
			}
		}

		if (("YES").equalsIgnoreCase(dataMap.get("Product"))) {
			navigateToMainPage(cu, "Product", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Product_Provisioning"))) {
				//navigate to tab
				navigateToSubPage(cu, "Product_Provisioning", "Product_Tab", "Provisioning");	
				//Dropdowns validation
				validateRecords(cu, "Product_Provisioning_ServiceLst", dataMap.get("PP_ServiceLst_Val"));
				validateRecords(cu, "Product_Provisioning_ProductStatusLst", dataMap.get("PP_ProductStatusLst_Val"));
				validateRecords(cu, "Product_Provisioning_ProductCurrencyLst", dataMap.get("PP_CurrencyLst_Val"));
				validateRecords(cu, "Product_Provisioning_PriorityLst", dataMap.get("PP_PriorityLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Product Provisioning screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Product_Coverage_Management"))) {
				//navigate to tab
				navigateToSubPage(cu, "Product_Coverage_Management", "Product_Tab", "Coverage Management");	
				//Dropdowns validation
				validateRecords(cu, "Product_Coverage_ServiceLst", dataMap.get("PP_ServiceLst_Val"));				
				cu.getScreenShot("Validation of options in fixed Dropdowns for Product_Coverage_Management screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Product_Price_Management"))) {
				//navigate to tab
				navigateToSubPage(cu, "Product_Price_Management", "Product_Tab", "Price Management");	
				//Dropdowns validation
				validateRecords(cu, "Product_Coverage_ServiceLst", dataMap.get("PPM_ServiceLst_Val"));				
				cu.getScreenShot("Validation of options in fixed Dropdowns for Product_Price_Management screen");
			}
			
		}
		
		if (("YES").equalsIgnoreCase(dataMap.get("Supplier"))) {
			navigateToMainPage(cu, "Supplier", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Supplier_Provisioning"))) {
				//navigate to tab
				navigateToSubPage(cu, "Supplier_Provisioning", "Supplier_Tab", "Provisioning");	
				//Dropdowns validation ( Common )
				validateRecords(cu, "Supplier_Provisioning_Instance", dataMap.get("SP_InstanceLst"));
				validateRecords(cu, "Supplier_Provisioning_Account_Status", dataMap.get("SP_AccountStatusLst"));
				
				//Dropdowns in Instance_InfoTab
				cu.clickElement("suppProvisioning_Instance_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "suppProvisioning_Ins_InstanceStateLst", dataMap.get("SP_Ins_InstanceStateLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier Provisioning-Instance InfoTab screen");
				
				//Dropdowns in Traffic_InfoTab
				cu.scrollDownPage();
				cu.clickElement("suppProvisioning_Traffic_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "suppProvisioning_Traffic_OASenderTypeSupportLst", dataMap.get("SP_Traffic_OASenderTypeSupportLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier Provisioning-Traffic InfoTab screen");
				
				//Dropdowns in SMPP_InfoTab
				cu.scrollDownPage();
				cu.clickElement("suppProvisioning_SMPP_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "suppProvisioning_SMPP_SMPPVersionLst", dataMap.get("SP_SMPP_SMPPVersionLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_DLRSupportLst", dataMap.get("SP_SMPP_DLRSupportLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_MsgIDTypeLst", dataMap.get("SP_SMPP_MsgIDTypeLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_MsgIDLengthLst", dataMap.get("SP_SMPP_MsgIDLengthLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_SMSCDefaultAlphabetLst", dataMap.get("SP_SMPP_SMSCDefaultAlphabetLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_SMSCMsgModeLst", dataMap.get("SP_SMPP_SMSCMsgModeLst_Val"));
				validateRecords(cu, "suppProvisioning_SMPP_DataCodingSupportedLst", dataMap.get("SP_SMPP_DataCodingSupportedLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier Provisioning-SMPP InfoTab screen");
								
				//Dropdowns in SS7_InfoTab
				cu.scrollDownPage();
				cu.clickElement("suppProvisioning_SS7_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "suppProvisioning_SS7_SM_RP_PRIFlagLst", dataMap.get("SP_SS7_SM_RP_PRIFlagLst_Val"));
				validateRecords(cu, "suppProvisioning_SS7_MTDCSSupportLst", dataMap.get("SP_SS7_MTDCSSupportLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier Provisioning-SS7 InfoTab screen");
				
				//Dropdowns in HTTP_InfoTab
				cu.scrollDownPage();
				cu.clickElement("suppProvisioning_HTTP_InfoTab");
				cu.sleep(1000);
				validateRecords(cu, "suppProvisioning_HTTP_DLRSupportLst", dataMap.get("SP_HTTP_DLRSupportLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier Provisioning-HTTP InfoTab screen");
				
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Supplier_Coverage_Management"))) {
				//navigate to tab
				navigateToSubPage(cu, "Supplier_Coverage_Management", "Supplier_Tab", "Coverage Management");	
				//Dropdowns validation
				validateRecords(cu, "SupplierCovergaeServiceLst", dataMap.get("SCovM_ServiceLst_Val"));							
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier_Coverage_Management screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Supplier_Cost_Management"))) {
				//navigate to tab
				navigateToSubPage(cu, "Supplier_Cost_Management", "Supplier_Tab", "Cost Management");	
				//Dropdowns validation
				validateRecords(cu, "SupplierCostManagement_ServiceLst", dataMap.get("SCostM_ServiceLst_Val"));							
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier_Cost_Management screen");
			}
			
			if (("YES").equalsIgnoreCase(dataMap.get("Supplier_Destination_Cost"))) {
				//navigate to tab
				navigateToSubPage(cu, "Supplier_Destination_Cost", "Supplier_Tab", "Destination Cost");	
				//Dropdowns validation
				//validateRecords(cu, "SupplierCostManagement_ServiceLst", dataMap.get("SCostM_ServiceLst_Val"));							
				cu.getScreenShot("Validation of options in fixed Dropdowns for Supplier_Destination_Cost screen");
			}
		}
		
		
		if (("YES").equalsIgnoreCase(dataMap.get("Inventory"))) {
			navigateToMainPage(cu, "Inventory", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Number_Inventory"))) {
				//navigate to tab
				navigateToSubPage(cu, "Number_Inventory", "Inventory_Tab", "Number Inventory");	
				//Dropdowns validation
				validateRecords(cu, "Number_Inventory_TONLst", dataMap.get("NI_TON"));
				validateRecords(cu, "Number_Inventory_CountryLst", dataMap.get("NI_CountryLst_Val"));
				validateRecords(cu, "Number_Inventory_StatusLst", dataMap.get("NI_StatusLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Number_Inventory screen");
			}			
		}
		
		if (("YES").equalsIgnoreCase(dataMap.get("Routing"))) {
			navigateToMainPage(cu, "Routing", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Product_Routing"))) {
				//navigate to tab
				navigateToSubPage(cu, "Product_Routing", "Routing_Tab", "Product Routing");	
				//Dropdowns validation
				validateRecords(cu, "ProductRouting_CountryLst", dataMap.get("PR_CountryLst"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Product_Routing screen");
			}		
			
			if (("YES").equalsIgnoreCase(dataMap.get("Filtering_Rules"))) {
				//navigate to tab
				navigateToSubPage(cu, "Filtering_Rules", "Routing_Tab", "Filtering Rules");	
				//Dropdowns validation
				validateRecords(cu, "FilteringRules_CountryLst", dataMap.get("FR_CountryLst"));
				validateRecords(cu, "FilteringRules_DestinationLst", dataMap.get("FR_DestinationLst"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Filtering_Rules screen");
			}		
		}
		
		if (("YES").equalsIgnoreCase(dataMap.get("Reporting"))) {
			navigateToMainPage(cu, "Reporting", "mainTabLink");
			
			if (("YES").equalsIgnoreCase(dataMap.get("Delivery_Statistics_Uncorrelated"))) {
				//navigate to tab
				navigateToSubPage(cu, "Delivery_Statistics_Uncorrelated", "Reporting_Tab", "Delivery Statistics Uncorrelated");	
				//Dropdowns validation
				validateRecords(cu, "DeliveryStatUncorrelated_ServiceLst", dataMap.get("DSU_ServiceLst_Val"));
				validateRecords(cu, "DeliveryStatUncorrelated_CountryLst", dataMap.get("DSU_CountryLst_Val"));
				validateRecords(cu, "DeliveryStatUncorrelated_DestinationLst", dataMap.get("DSU_DestinationLst_Val"));
				validateRecords(cu, "DeliveryStatUncorrelated_InstanceLst", dataMap.get("DSU_InstanceLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Delivery_Statistics_Uncorrelated screen");
			}	
			
			if (("YES").equalsIgnoreCase(dataMap.get("Delivery_Statistics"))) {
				//navigate to tab
				navigateToSubPage(cu, "Delivery_Statistics", "Reporting_Tab", "Delivery Statistics");	
				//Dropdowns validation
				validateRecords(cu, "DeliveryStatistics_ServiceLst", dataMap.get("DS_ServiceLst_Val"));
				validateRecords(cu, "DeliveryStatistics_CountryLst", dataMap.get("DS_CountryLst_Val"));
				validateRecords(cu, "DeliveryStatistics_DestinationLst", dataMap.get("DS_DestinationLst_Val"));
				validateRecords(cu, "DeliveryStatistics_InstanceLst", dataMap.get("DS_InstanceLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Delivery_Statistics screen");
			}	
			
			if (("YES").equalsIgnoreCase(dataMap.get("TopN_Destination"))) {
				//navigate to tab
				navigateToSubPage(cu, "TopN_Destination", "Reporting_Tab", "TopN Destination");	
				//Dropdowns validation
				validateRecords(cu, "TopNDestination_ServiceLst", dataMap.get("TND_ServiceLst_Val"));
				validateRecords(cu, "TopNDestination_DestinationGranularityLst", dataMap.get("TND_DestinationGranularityLst_Val"));
				validateRecords(cu, "TopNDestination_SelectionBasedOnLst", dataMap.get("TND_SelectionBasedOnLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for TopN_Destination screen");
			}	
		}
		
		
		
		if (("YES").equalsIgnoreCase(dataMap.get("Financial"))) {
			navigateToMainPage(cu, "Financial", "mainTabLink");

			if (("YES").equalsIgnoreCase(dataMap.get("Receivable"))) {
				//navigate to tab
				navigateToSubPage(cu, "Receivable", "Financial_Tab", "Receivable");	
				//Dropdowns validation
				validateRecords(cu, "Receivables_ServiceLst", dataMap.get("Recivable_ServiceLst_Val"));
				validateRecords(cu, "Receivables_CountryLst", dataMap.get("Recivable_CountryLst_Val"));
				validateRecords(cu, "Receivables_DestinationLst", dataMap.get("Recivable_DestinationLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Receivable screen");
			}		
			
			if (("YES").equalsIgnoreCase(dataMap.get("Payable"))) {
				//navigate to tab
				navigateToSubPage(cu, "Payable", "Financial_Tab", "Payable");	
				//Dropdowns validation
				validateRecords(cu, "Payable_ServiceLst", dataMap.get("Payable_ServiceLst_Val"));
				validateRecords(cu, "Payable_CountryLst", dataMap.get("Payable_CountryLst_Val"));
				validateRecords(cu, "Payable_DestinationLst", dataMap.get("Payable_DestinationLst_Val"));
				validateRecords(cu, "Payable_BreakDownLst", dataMap.get("Payable_BreakDownLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Receivable screen");
			}	
			
			if (("YES").equalsIgnoreCase(dataMap.get("Rerating"))) {
				//navigate to tab
				navigateToSubPage(cu, "Rerating", "Financial_Tab", "Rerating");	
				//Dropdowns validation
				validateRecords(cu, "Rerating_ServiceLst", dataMap.get("Rerating_ServiceLst_Val"));
				validateRecords(cu, "Rerating_CountryLst", dataMap.get("Rerating_CountryLst_Val"));
				validateRecords(cu, "Rerating_DestinationLst", dataMap.get("Rerating_DestinationLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for Rerating screen");
			}	
			
			if (("YES").equalsIgnoreCase(dataMap.get("RA_Daily_Report"))) {
				//navigate to tab
				navigateToSubPage(cu, "RA_Daily_Report", "Financial_Tab", "RA Daily Report");	
				//Dropdowns validation
				validateRecords(cu, "RADailyReport_ServiceLst", dataMap.get("RADR_ServiceLst_Val"));
				validateRecords(cu, "RADailyReport_CountryLst", dataMap.get("RADR_CountryLst_Val"));
				validateRecords(cu, "RADailyReport_DestinationLst", dataMap.get("RADR_DestinationLst_Val"));
				cu.getScreenShot("Validation of options in fixed Dropdowns for RA_Daily_Report screen");
			}	
		}

		
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
		extent = ExtReport.instance("Validation_Of_Options_In_Fixed_Dropdown");
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
			LOGGER.info(" App Logout failed () :: Exception: " + e);
			Log.error(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

	public void validateRecords(CommonUtils cu, String elemntPath,  String fieldValue)
			throws Exception {				
			
		cu.validateFieldsInDropDown(elemntPath, fieldValue);
	}
	
	public void navigateToSubPage(CommonUtils cu, String fullName, String mainFieldName, String childNavgicationName)
	{
		cu.default_content();
		cu.SwitchFrames("//iframe[@name='bottom']");
		cu.SwitchFrames("//*[contains(@name,'index')]");
		cu.clickElement(mainFieldName, "$destinationVal$", childNavgicationName);
		cu.waitForPageLoad(fullName);
		test.log(LogStatus.PASS, "EXPECTECD:  "+fullName+" page should get opened",
				"Validation:  <span style='font-weight:bold;'>ACTUAL::  "+fullName+" page opened successfully</span>");
		cu.default_content();
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");
		
		cu.sleep(2000);		
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		if(cu.existsElement("application_PopUpOkBtn"))
		{
			cu.clickElement("application_PopUpOkBtn");
		}
		cu.sleep(1000);
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
	}
	
	public void navigateToMainPage(CommonUtils cu, String name, String mainFieldName)
	{
		cu.default_content();
		cu.SwitchFrames("//iframe[@name='bottom']");
		cu.SwitchFrames("//*[contains(@name,'index')]");
		cu.clickElement(mainFieldName, "$destinationVal$", name);
		cu.waitForPageLoad(name);
		cu.default_content();		
	}
	
	void print()
	{
		System.out.println("-----------------------------------");
		for(String key: dataMap.keySet())
		{
			System.out.println(key+"~~~~"+dataMap.get(key));
		}
		System.out.println("-----------------------------------");
	}
}
