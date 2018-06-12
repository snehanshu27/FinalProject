package com.tata.selenium.test.deliveryStatistics.Old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
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
import com.tata.selenium.utils.CSVUtil;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.ExtReport;
import com.tata.selenium.utils.Log;

public class TC_05_SelectSupplier implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_05_SelectSupplier.class.getName());
	Map<String, String> dataMap = new HashMap<>();

	String properties = "./data/DeliveryStatistics.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;

	private WebDriver driver;

	private ExtentTest test;

	@Test
	@Parameters({ "uniqueDataId", "testCaseId" })
	public void DO(String uniqueDataId, String testCaseId) {
		// Starting the extent report
		test = extent
				.startTest("Execution triggered for - "+TC_05_SelectSupplier.class.getName()+" -with TestdataId: " + uniqueDataId);
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

//		cu.checkMessage("application_PopUpMessage", "After loading the page",
//				"No data for the selected input parameters.");

		//Select dimension dropdown based on data sheet
		cu.selectDropDownByVisibleText("DeliveryStat_DimensionLst", dataMap.get("Dimension"));
		cu.waitForPageLoad("DeliveryStatics");
		
		//validate table headernames
		validateTableHeaders(cu, dataMap.get("TableHeaders"));
						
		//Select values in Customer, Supplier, Country, Destination and Instance based on excel data
		if(!dataMap.get("DeliveryStat_Customer_NameLst").trim().isEmpty() && !"ALL".equalsIgnoreCase(dataMap.get("DeliveryStat_Customer_NameLst")))
		{
			cu.deselectDropDownByVisibleText("DeliveryStat_Customer_NameLst", "ALL");
			cu.selectDropDownByVisibleText("DeliveryStat_Customer_NameLst", dataMap.get("DeliveryStat_Customer_NameLst"));
			cu.clickElement("DeliveryStatisticsPage");
		}
		
		if(!dataMap.get("DeliveryStat_Supplier_NameLst").trim().isEmpty() && !"ALL".equalsIgnoreCase(dataMap.get("DeliveryStat_Supplier_NameLst")))
		{
			cu.deselectDropDownByVisibleText("DeliveryStat_Supplier_NameLst", "ALL");
			cu.selectDropDownByVisibleText("DeliveryStat_Supplier_NameLst", dataMap.get("DeliveryStat_Supplier_NameLst"));
			cu.clickElement("DeliveryStatisticsPage");
		}
		
		if(!dataMap.get("DeliveryStat_CountryLst").trim().isEmpty() && !"ALL".equalsIgnoreCase(dataMap.get("DeliveryStat_CountryLst")))
		{
			cu.deselectDropDownByVisibleText("DeliveryStat_CountryLst", "ALL");
			cu.selectDropDownByVisibleText("DeliveryStat_CountryLst", dataMap.get("DeliveryStat_CountryLst"));
			cu.clickElement("DeliveryStatisticsPage");
		}
		
		if(!dataMap.get("DeliveryStat_DestinationLst").trim().isEmpty() && !"ALL".equalsIgnoreCase(dataMap.get("DeliveryStat_DestinationLst")))
		{
			cu.deselectDropDownByVisibleText("DeliveryStat_DestinationLst", "ALL");
			cu.selectDropDownByVisibleText("DeliveryStat_DestinationLst", dataMap.get("DeliveryStat_DestinationLst"));
			cu.clickElement("DeliveryStatisticsPage");
		}
		
		if(!dataMap.get("DeliveryStat_InstanceLst").trim().isEmpty() && !"ALL".equalsIgnoreCase(dataMap.get("DeliveryStat_InstanceLst")))
		{
			cu.deselectDropDownByVisibleText("DeliveryStat_InstanceLst", "ALL");
			cu.selectDropDownByVisibleText("DeliveryStat_InstanceLst", dataMap.get("DeliveryStat_InstanceLst"));
			cu.clickElement("DeliveryStatisticsPage");
		}
		
		
		// Select From DATE
		cu.moveAndClick("DeliveryStat_FromDateTxt");
		cu.sleep(2000);		
		cu.moveAndClick("selectMonth");
		cu.sleep(2000);
		cu.calMonth(dataMap.get("FromMonth"));
		cu.sleep(2000);
		cu.calDate(dataMap.get("FromDay"));
		cu.sleep(2000);
		cu.clickElement("clickOutside");

		// Select TO DATE
		cu.moveAndClick("DeliveryStat_ToDateTxt");
		cu.sleep(2000);
		cu.moveAndClick("selectMonth_ToDate");
		cu.sleep(2000);
		cu.calMonth(dataMap.get("ToMonth"));
		cu.sleep(2000);
		cu.calDate(dataMap.get("ToDay"));
		cu.sleep(2000);

		cu.clickElement("DeliveryStat_DisplayBtn");

		List<WebElement> supplierrAccElemets = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[2]/a"));
		List<String> supplierAccValues = new ArrayList<>();
		for (WebElement ele : supplierrAccElemets)
			supplierAccValues.add(ele.getText());

		for (String suppAccVal : supplierAccValues) {

			test.log(LogStatus.INFO, "$$$$$$$$$$$ Validating for supplier account: "+suppAccVal);
			Map<String, Double> uiData = getUIVales(cu, suppAccVal);
			
			//checking % and enroute valdiations on main pagedd
			
			Double submitPerMainExpected = uiData.get("Submitted Failure Percentage");
			Double deliveredPerMainExpected = uiData.get("Delivered Percentage");
			Double deliveredFailedPerMainExpected = uiData.get("Failed Percentage");
			int enrouteMainExpected = (int) (uiData.get("Enroute")-0);
			
			Double submitPerMainActualRaw= (uiData.get("Submitted")/uiData.get("Attempted"))*100.0;
			Double submitPerMainActual= Math.round(submitPerMainActualRaw * 100.0) / 100.0;
			Double deliveredPerMainActualRaw= (uiData.get("Delivered")/uiData.get("Submitted"))*100.0;
			Double deliveredPerMainActual= Math.round(deliveredPerMainActualRaw * 100.0) / 100.0;			
			Double deliveredFailedPerMainActualRaw= (uiData.get("Failed")/uiData.get("Submitted"))*100.0;
			Double deliveredFailedPerMainActual= Math.round(deliveredFailedPerMainActualRaw * 100.0) / 100.0;			
			int enrouteMainActual=(int) (uiData.get("Submitted")-(uiData.get("Delivered")+uiData.get("Failed")));
			
			//main page Submitted Failure Percentage validation 
			if(String.valueOf(submitPerMainExpected).equals(String.valueOf(submitPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+submitPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+submitPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+submitPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+submitPerMainActual+"</span>");
			}
			
			//main page Delivered Percentage validation
			if(String.valueOf(deliveredPerMainExpected).equals(String.valueOf(deliveredPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Delivered Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+deliveredPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+deliveredPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Delivered Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+deliveredPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+deliveredPerMainActual+"</span>");
			}
			
			//main page Delivered Failed Percentage validation
			if(String.valueOf(deliveredFailedPerMainExpected).equals(String.valueOf(deliveredFailedPerMainActual)))
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Delivered Failed Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+deliveredFailedPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+deliveredFailedPerMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Delivered Failed Percentage (main page)for Supplier Account: "+suppAccVal+" should be "+deliveredFailedPerMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+deliveredFailedPerMainActual+"</span>");
			}
			
			//main page Enroute validation
			if(enrouteMainExpected == enrouteMainActual)
			{	
				test.log(LogStatus.PASS,
						"EXPECTED: Enroute (main page)for Supplier Account: "+suppAccVal+" should be "+enrouteMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is same as  "+enrouteMainActual+"</span>");
			}
			else
			{		
				test.log(LogStatus.FAIL,
						"EXPECTED: Enroute (main page)for Supplier Account: "+suppAccVal+" should be "+enrouteMainExpected,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Submitted Failure Percentage (main page)for Supplier Account: "+suppAccVal+" is not same as "+enrouteMainActual+"</span>");
			}
			

			cu.clickElement("dynamicSupORCustAccORDestinationValue", "$SupplierORCustomerAccORDestinationVal$", suppAccVal);
			test.log(LogStatus.INFO, "######### Validating 1st drilldown for supplier account: "+suppAccVal);
			
			//UI fields validation
			cu.checkElementPresence("exportBtn");
			cu.checkElementPresence("backBtn");

			cu.checkReadonlyProperty("DrillDown_SupplierNameTxt");
			cu.checkReadonlyProperty("DrillDown_SupplierAccNameTxt");
			cu.checkReadonlyProperty("DeliveryStat_FromDateTxt");
			cu.checkReadonlyProperty("DeliveryStat_ToDateTxt");

			Map<String, Integer> csvData = exportCSVAndCoverageFieldsUpdated(cu, dataMap);

			// compare summation of CSV data (of detailed page )with UI data(of main page)
			for(String colName : csvData.keySet())
			{
//			    try {
					
			    	Integer uiValue = Integer.valueOf((int) (uiData.get(colName)-0));
			    	
					if(csvData.get(colName).equals(uiValue))					
						test.log(LogStatus.PASS,
								"EXPECTED: " + suppAccVal
										+ "The values of the csv data and the Ui Values of this supplier account"
										+ suppAccVal + " -> should be same. Filed Name : "+colName+"  - expected value: "+uiValue,
								"Validation:  <span style='font-weight:bold;'>ACTUAL::" + suppAccVal
										+ "The values of the csv data and the Ui Values of this supplier account"
										+ suppAccVal + " are same. Filed Name : "+colName+"  - expected value: "+csvData.get(colName)+"</span>");
					else
						test.log(LogStatus.FAIL,
								"EXPECTED: " + suppAccVal
								+ "The values of the csv data and the Ui Values of this supplier account"
								+ suppAccVal + " -> should be same. Filed Name : "+colName+"  - expected value: "+uiValue,
						"Validation:  <span style='font-weight:bold;'>ACTUAL::" + suppAccVal
								+ "The values of the csv data and the Ui Values of this supplier account"
								+ suppAccVal + " are not same. Filed Name : "+colName+"  - expected value: "+csvData.get(colName)+"</span>");
//				} catch (Exception e) {
//					
//					cu.printLogs("Exception occur while comparing the data  - " + e);
//					test.log(LogStatus.FAIL,"", "Exception occur while comparing the data  - " + e);
//				}
			}
			
			
			//second drilldown
			driver.manage().timeouts().implicitlyWait(100, TimeUnit.MILLISECONDS);
			int allAttemptedFailureCount = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[8]/a")).size();
			int allSubmitFailureCount = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[10]/a")).size();
			int allFailedCount = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[15]/a")).size();
			int allAverageLatencyCount = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[17]/a")).size();
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			
			//AttemptedFailure
			for(int i=0; i<allAttemptedFailureCount; i++)
			{
				if(i==0)
				{
					String suppNameDrill = driver.findElement(By.name("supplierName")).getAttribute("value");
					String suppAccNameDrill = driver.findElement(By.name("supplierAccName")).getAttribute("value");
					test.log(LogStatus.INFO, "Validating Attempted Failure DrillDown Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				}
				
				driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[8]/a")).get(i).click();
				
				String cusAcctName =driver.findElement(By.name("custAccNameDrill")).getAttribute("value");
				String suppAccName = driver.findElement(By.name("suppNameDrill")).getAttribute("value");
				int totalFailureTop = Integer.valueOf(driver.findElement(By.id("totalFailure")).getAttribute("value"));	
				
				//failure sum validations
				int totalFailureBott = 0;
				List<WebElement> allFailedTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[2]"));
				for(WebElement failTd : allFailedTds)
					totalFailureBott = totalFailureBott + Integer.valueOf(failTd.getText().replace(",", ""));
				
				
				if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
				{	
					test.log(LogStatus.PASS,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is same as "+totalFailureBott+"</span>");
				}
				else
				{		
					test.log(LogStatus.FAIL,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is not as "+totalFailureBott+"</span>");
				}
				
				//failure percentage validations
				List<WebElement> allPercentageTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[3]"));
				for(int j=0;j<allPercentageTds.size(); j++)
				{
					String failureReason = driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr["+(j+1)+"]/td[1]")).getText();
					double expectedPercentage = Double.valueOf(allPercentageTds.get(j).getText().replace("%", ""));
					
					double actualPercentage = (Double.valueOf(allFailedTds.get(j).getText().replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
					double actualPercentageRoundOff = Math.round(actualPercentage * 100.0) / 100.0;
					
					if(String.valueOf(expectedPercentage).equals(String.valueOf(actualPercentageRoundOff)))
					{			
						test.log(LogStatus.PASS,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is same as "+actualPercentageRoundOff+"</span>");
					}
					else
					{				
						test.log(LogStatus.FAIL,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is not same as "+actualPercentageRoundOff+"</span>");
					}
				}
				
				cu.clickElement("backBtn");
			}
			
			//Submit Failure Validations
			for(int i=0; i<allSubmitFailureCount; i++)
			{				
				if(i==0)
				{
					String suppNameDrill = driver.findElement(By.name("supplierName")).getAttribute("value");
					String suppAccNameDrill = driver.findElement(By.name("supplierAccName")).getAttribute("value");
					test.log(LogStatus.INFO, "Validating Submit Failure DrillDown Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				}
								
				driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[10]/a")).get(i).click();
				
				String cusAcctName =driver.findElement(By.name("custAccNameDrill")).getAttribute("value");
				String suppAccName = driver.findElement(By.name("suppNameDrill")).getAttribute("value");
				int totalFailureTop = Integer.valueOf(driver.findElement(By.id("totalFailure")).getAttribute("value"));	
				
				//failure sum validations
				int totalFailureBott = 0;
				List<WebElement> allFailedTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[2]"));
				for(WebElement failTd : allFailedTds)
					totalFailureBott = totalFailureBott + Integer.valueOf(failTd.getText().replace(",", ""));
				
				if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
				{	
					test.log(LogStatus.PASS,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is same as "+totalFailureBott+"</span>");
				}
				else
				{		
					test.log(LogStatus.FAIL,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is not as "+totalFailureBott+"</span>");
				}
				
				//failure sum validations
				List<WebElement> allPercentageTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[3]"));
				for(int j=0;j<allPercentageTds.size(); j++)
				{
					String failureReason = driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr["+(j+1)+"]/td[1]")).getText();
					double expectedPercentage = Double.valueOf(allPercentageTds.get(j).getText().replace("%", ""));
					
					double actualPercentage = (Double.valueOf(allFailedTds.get(j).getText().replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
					double actualPercentageRoundOff = Math.round(actualPercentage * 100.0) / 100.0;
					
					if(String.valueOf(expectedPercentage).equals(String.valueOf(actualPercentageRoundOff)))
					{			
						test.log(LogStatus.PASS,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is same as "+actualPercentageRoundOff+"</span>");
					}
					else
					{				
						test.log(LogStatus.FAIL,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is not same as "+actualPercentageRoundOff+"</span>");
					}
				}
				
				cu.clickElement("backBtn");
			
			}
			
			
			//Failed Validations
			for(int i=0; i<allFailedCount; i++)
			{

				if(i==0)
				{
					String suppNameDrill = driver.findElement(By.name("supplierName")).getAttribute("value");
					String suppAccNameDrill = driver.findElement(By.name("supplierAccName")).getAttribute("value");
					test.log(LogStatus.INFO, "Validating Failed DrillDown Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				}
								
				driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[15]/a")).get(i).click();
				
				String cusAcctName =driver.findElement(By.name("custAccNameDrill")).getAttribute("value");
				String suppAccName = driver.findElement(By.name("suppNameDrill")).getAttribute("value");
				int totalFailureTop = Integer.valueOf(driver.findElement(By.id("totalFailure")).getAttribute("value"));	
				
				//failure sum validations
				int totalFailureBott = 0;
				List<WebElement> allFailedTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[2]"));
				for(WebElement failTd : allFailedTds)
					totalFailureBott = totalFailureBott + Integer.valueOf(failTd.getText().replace(",", ""));
				
				if(String.valueOf(totalFailureTop).equals(String.valueOf(totalFailureBott)))
				{	
					test.log(LogStatus.PASS,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is same as "+totalFailureBott+"</span>");
				}
				else
				{		
					test.log(LogStatus.FAIL,
							"EXPECTED: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+totalFailureTop,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Total failures for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is not as "+totalFailureBott+"</span>");
				}
				
				//failure percentage validations
				List<WebElement> allPercentageTds = driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[3]"));
				for(int j=0;j<allPercentageTds.size(); j++)
				{
					String failureReason = driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr["+(j+1)+"]/td[1]")).getText();
					double expectedPercentage = Double.valueOf(allPercentageTds.get(j).getText().replace("%", ""));
					
					double actualPercentage = (Double.valueOf(allFailedTds.get(j).getText().replace(",", ""))/Double.valueOf(totalFailureTop))*100.0;
					double actualPercentageRoundOff = Math.round(actualPercentage * 100.0) / 100.0;
					
					if(String.valueOf(expectedPercentage).equals(String.valueOf(actualPercentageRoundOff)))
					{			
						test.log(LogStatus.PASS,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is same as "+actualPercentageRoundOff+"</span>");
					}
					else
					{				
						test.log(LogStatus.FAIL,
								"EXPECTED: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') should be "+expectedPercentage,
								"Validation:  <span style='font-weight:bold;'>ACTUAL:: Percentage for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" (failure reson: '"+failureReason+"') is not same as "+actualPercentageRoundOff+"</span>");
					}
				}
				
				cu.clickElement("backBtn");
			
			}
			
			
			//Average Latency
			for(int i=0; i<allAverageLatencyCount; i++)
			{

				if(i==0)
				{
					String suppNameDrill = driver.findElement(By.name("supplierName")).getAttribute("value");
					String suppAccNameDrill = driver.findElement(By.name("supplierAccName")).getAttribute("value");
					test.log(LogStatus.INFO, "Validating Average Latency DrillDown Validations for Supplier: "+suppNameDrill+" -> SupplierAccount: "+suppAccNameDrill);
				}
				
				driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr/td[17]/a")).get(i).click();
				
				String cusAcctName =driver.findElement(By.name("custAccNameDrill")).getAttribute("value");
				String suppAccName = driver.findElement(By.name("suppNameDrill")).getAttribute("value");
				
								
				int totalDelivered = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[1]"), "Total Delivered Count coloumn td").replace(",", ""));
				int count_0_15 = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[2]"), "count_0_15 coloumn td").replace(",", ""));
				int count_15_45 = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[4]"), "count_15_45 coloumn td").replace(",", ""));
				int count_45_90 = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[6]"), "count_45_90 coloumn td").replace(",", ""));
				int count_90_180 = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[8]"), "count_90_180 coloumn td").replace(",", ""));
				int count_above180 = Integer.valueOf(cu.getText(By.xpath("//*[@id='myTable']/tbody/tr/td[10]"), "count_above180 coloumn td").replace(",", ""));
				
				int avarageExpected = Integer.valueOf(driver.findElement(By.name("avgLatency")).getAttribute("value").replace(",", ""));				
				int avarageActual = ((count_0_15*15)+(count_15_45*45)+(count_45_90*90)+(count_90_180*180)+(count_above180*210))/totalDelivered;
				
				if(String.valueOf(avarageActual).equals(String.valueOf(avarageExpected)))
				{
					test.log(LogStatus.PASS,
							"EXPECTED: Average Latency for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+avarageExpected,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Average Latency for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is same as "+avarageActual+"</span>");
				}
				else
				{
					test.log(LogStatus.FAIL,
							"EXPECTED: Average Latency for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" should be "+avarageExpected,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Average Latency for Customer Account: "+cusAcctName+" -> Supplier Account: "+suppAccName+" is not same as "+avarageActual+"</span>");
				}
				
				cu.clickElement("backBtn");
			
			}
			
			cu.clickElement("backBtn");

		}

		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);

		// Printing pass/fail in the test data sheet
		cu.checkRunStatus();

	}

	public Map<String, Double> getUIVales(CommonUtils cu, String customerAccValue)  {
		Map<String, Double> ret = new HashMap<>();

		try {
			ret.put("Attempted", Double.valueOf(cu.getText("DynamicAttempted", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Attempted Failure",Double.valueOf(cu.getText("DynamicAttemptedFailure", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Submitted", Double.valueOf(cu.getText("DynamicSubmitted", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Submit Failure", Double.valueOf(cu.getText("DynamicSubmittedFailure", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Submitted Failure Percentage", Double.valueOf(cu.getText("DynamicSubmittedFailurePercentage", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace("%", "")));
			ret.put("Enroute", Double.valueOf(cu.getText("DynamicEnroute", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Delivered", Double.valueOf(cu.getText("DynamicDelivered", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Delivered Percentage", Double.valueOf(cu.getText("DynamicDeliveredPercentage", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace("%", "")));
			ret.put("Failed", Double.valueOf(cu.getText("DynamicFailed", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace(",", "")));
			ret.put("Failed Percentage", Double.valueOf(cu.getText("DynamicFailedPercentage", "$SupplierORCustomerAccORDestinationVal$", customerAccValue).replace("%", "")));

			return ret;
		} catch (Exception e) {
			LOGGER.error("Error occured while reading the data from UI -- "+e);
			cu.getScreenShot("Get webtable data for Supplier Account Value: " + customerAccValue);
			test.log(LogStatus.FAIL,"", "Exception occur while fething the data from UI - " + e);
			return ret;
		}
	}

	public Map<String, Integer> exportCSVAndCoverageFieldsUpdated(CommonUtils cu, Map<String, String> dataMap)
    {
		cu.deleteAllFilesInDownloadFolder();
		cu.clickElement("exportBtn");
		cu.waitForPageLoad("DeliveryStatistics");
		cu.sleep(2000);
		String csvFilePath = cu.getDownlaodedFileName();

		// validate file name
		String expectedFileName = "\\" + "Supplier-SMSCustomerAccountDistribution" + ".csv";
		if (csvFilePath.trim().contains(expectedFileName.trim()))
			test.log(LogStatus.PASS,
					"EXPECTED: Exported file name should be in 'Supplier-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is same as 'Supplier-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'</span>");
		else {
			cu.getScreenShot("Exported file name validation");
			test.log(LogStatus.FAIL,
					"EXPECTED: Exported file name should be in 'Supplier-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "'",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: Exported file name is Not same as in 'Supplier-SMSCustomerAccountDistribution.csv' - '"
							+ expectedFileName + "' Acutal file name: " + csvFilePath + "</span>");
		}

		CSVUtil csvu = new CSVUtil(csvFilePath, 6);

		List<String> attemStrs = csvu.getSingleColAllData("Attempted");
		int AttemptedCount = csvu.getSingleColSum(attemStrs);

		List<String> attemFailureStrs = csvu.getSingleColAllData("Attempted Failure");
		int AttemptedFailureCount = csvu.getSingleColSum(attemFailureStrs);

		List<String> submittedStrs = csvu.getSingleColAllData("Submitted");
		int Submittedcount = csvu.getSingleColSum(submittedStrs);

		List<String> submittedFailureStrs = csvu.getSingleColAllData("Submit Failure");
		int SubmitedFailureCount = csvu.getSingleColSum(submittedFailureStrs);

		List<String> deliveredStrs = csvu.getSingleColAllData("Delivered");
		int DeliveredCount = csvu.getSingleColSum(deliveredStrs);

		
		Map<String, Integer> exceldata = new HashMap<>();
		try {
			exceldata.put("Attempted", AttemptedCount);
			exceldata.put("Attempted Failure", AttemptedFailureCount);
			exceldata.put("Submitted", Submittedcount);
			exceldata.put("Submit Failure", SubmitedFailureCount);
			exceldata.put("Delivered", DeliveredCount);

		} catch (Exception e) {
			LOGGER.error(e);
		}

		return exceldata;
	}
	
	private void validateTableHeaders(CommonUtils cu, String tabHeadsStr) {
		
		cu.getScreenShot("Table header validation");
		String[] tableHeaders = tabHeadsStr.trim().split("~");
		for(int i=0;i<tableHeaders.length;i++)
		{
			String currHeaderval = cu.getText("dynamicTableHeader", "$index$", ""+(i+1));
			if(tableHeaders[i].equals(currHeaderval))
				test.log(LogStatus.PASS,
						"EXPECTED: Header name for index "+(i+1)+" should be "+tableHeaders[i],
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Header name for index "+(i+1)+"  is same as "+tableHeaders[i]+"</span>");
			else
			{
				test.log(LogStatus.FAIL,
						"EXPECTED: Header name for index "+(i+1)+" should be "+tableHeaders[i],
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Header name for index "+(i+1)+"  is same not as "+tableHeaders[i]+". It is having this vaule: "+currHeaderval+"</span>");				
			}
			
		}
		
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
			LOGGER.info(" App Logout failed () :: Exception:" + e);
			driver.quit();
			Log.endTestCase(testCaseId);
			extent.endTest(test);
			extent.flush();
		}
	}

}
