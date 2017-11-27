package com.tata.selenium.test.inventoryNumberManagement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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


/**
 * @date 
 * @author Devbrath Singh
 * @description This class will perform a login and logout in Gmail application
 */

public class TC_07_DisplayResultsBasedOn_Input implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(TC_07_DisplayResultsBasedOn_Input.class.getName());
	String properties =  "./data/NumberInventory.properties";
	ExcelUtils excelUtils = new ExcelUtils();
	private ExtentReports extent;
	Map<String, String> dataMap = new HashMap<>();
	private WebDriver driver;
	private ExtentTest test ;
	
	@Test()
	@Parameters({"uniqueDataId", "testCaseId"})	
	public void DO (String uniqueDataId, String testCaseId) throws Exception {
		//Starting the extent report
		test = extent.startTest("Execution triggered for - TC_07_DisplayResultsBasedOn_Input - "+uniqueDataId);
		String sheetName="Number_Inventory_Screen";
		//Reading excel values
		try{
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(DATA_FILEPATH,sheetName);
			dataMap = excel.getSheetData(uniqueDataId, sheetName);
		}
		catch (Exception e){
			CommonUtils.printConsole("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			Reporter.log("Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			test.log(LogStatus.FAIL,"Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Exception while reading data from EXCEL file for test case : "+ testCaseId+" -with TestdataId : "+uniqueDataId+" Exceptions : "+ e, uniqueDataId, "Result_Errors");
			Assert.fail("Error occured while trying to login to the application  -  " +e);
		}	
		
		
		test.log(LogStatus.INFO, "Launch Application", "Usage: <span style='font-weight:bold;'>Going to Launch App</span>");
		
		CommonUtils cu=new CommonUtils(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		cu.printLogs("Executing Test Case -"+testCaseId+" -with TestdataId : "+uniqueDataId);		
		driver =cu.LaunchUrl(dataMap.get("URL"));

		LoginPage loginPage= new LoginPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		loginPage.dologin(dataMap.get("Username"), dataMap.get("Password"));
		//Runtime.getRuntime().exec("C:\\Users\\devbraths\\Desktop\\HandleAuthentication.exe");
		cu.waitForPageLoad("MessagingInstanceHomePage");
		
		MessagingInstanceHomePage msgInsHomePage = new MessagingInstanceHomePage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		msgInsHomePage.verifyLogin(test, testCaseId,sheetName);
		
		NavigationMenuPage navMenuPage=new NavigationMenuPage(driver,test, sheetName, uniqueDataId, testCaseId, properties);	
		navMenuPage.navigateToMenu(dataMap.get("Navigation"));
		cu.SwitchFrames("bottom");
		cu.SwitchFrames("target");

		//Selecting appropriate option to display
		if(("Y").equalsIgnoreCase(dataMap.get("Search_Update_Delete_Inventory"))){
			cu.clickElement("Search_Update_Delete_Inventory");
		}
		
		cu.SelectDropDownByVisibleText("Number_Inventory_SupplierNameLst",dataMap.get("Number_Inventory_SupplierNameLst"));
		cu.SelectDropDownByVisibleText("Number_Inventory_TONLst",dataMap.get("Number_Inventory_TONLst"));
		cu.SelectDropDownByVisibleText("Number_Inventory_CountryLst",dataMap.get("Number_Inventory_CountryLst"));
		cu.SelectDropDownByVisibleText("Number_Inventory_StatusLst",dataMap.get("Number_Inventory_StatusLst"));
		cu.waitForPageLoad("Number Inventory");
		
		cu.selectDate("Number_Inventory_StartDateTxt", dataMap.get("Number_Inventory_StartDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");
		cu.selectDate("Number_Inventory_EndDateTxt", dataMap.get("Number_Inventory_EndDateTxt"));
		cu.waitForPageLoad("");
		cu.clickElement("Number_Inventory_NumbersTxt");
		
		if(dataMap.get("Number_Inventory_NumbersTxt").trim().length() >0){
			if(dataMap.get("Number_Inventory_NumbersTxt").contains(";")){
				String[] data=dataMap.get("Number_Inventory_NumbersTxt").split(";");
				for(String val : data){
					cu.enterData("Number_Inventory_NumbersTxt", val);
					cu.returnElement("Number_Inventory_NumbersTxt").sendKeys(Keys.RETURN);
				}
			}else{
				cu.enterData("Number_Inventory_NumbersTxt", dataMap.get("Number_Inventory_NumbersTxt"));
			}
		}		
		cu.clickElement("Number_Inventory_DisplayBtn");
		cu.waitForPageLoad("");
		
		//export file and validate if flag value is Y
				if(("ON").equalsIgnoreCase(dataMap.get("ValidateExportCSVFile")))
					exportCSVAndValidateStatus(cu,dataMap.get("Number_Inventory_StatusLst"));
				
				
		if(driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr/td[contains(text(),'"+dataMap.get("Number_Inventory_SupplierNameLst")+"')]"))!= null){
			test.log(LogStatus.PASS,"Details should be displayed", "Details displayed sucessfully");
			List<WebElement>ele= driver.findElements(By.xpath("//*[@id='myTable']/tbody/tr"));
			int rowNum=ele.size();
			CommonUtils.printConsole("Total Rows dsplayed are "+rowNum);
			for(int i=1;i<=rowNum;i++){
				String strText;
				strText=driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr["+i+"]")).getText();
				//Validating if ALL is selected as Status
				if(("ALL").equalsIgnoreCase(dataMap.get("Number_Inventory_StatusLst"))){
					if(rowNum > 0){
						test.log(LogStatus.PASS, "EXPECTECD: Al types of Status should be displayed", "Validation:  <span style='font-weight:bold;'>ACTUAL:: All Types of Status displayed successfully.Details are :"+strText+"</span>");
					}		
				}
				else if(driver.findElement(By.xpath("//*[@id='myTable']/tbody/tr["+i+"]/td[8]")).getText().trim().equalsIgnoreCase(dataMap.get("Number_Inventory_StatusLst"))){
					test.log(LogStatus.PASS, "EXPECTECD: Status sgould be "+dataMap.get("Number_Inventory_StatusLst"), "Validation:  <span style='font-weight:bold;'>ACTUAL:: STatus for record '"+strText+"' is -"+dataMap.get("Number_Inventory_StatusLst")+"</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Status sgould be "+dataMap.get("Number_Inventory_StatusLst"), "Validation:  <span style='font-weight:bold;'>ACTUAL:: STatus for record '"+strText+"' is -"+dataMap.get("Number_Inventory_StatusLst")+"</span>");
				}
			}
		}else{
			test.log(LogStatus.FAIL, "EXPECTECD: Details should be displayed", "Validation:  <span style='font-weight:bold;'>ACTUAL:: Details could not be displayed in the result sectio</span>");
		}
		
		if(("Y").equals(dataMap.get("ClickCancelBtn"))){
			cu.clickElement("Number_Inventory_CancelBtn");
			cu.waitForPageLoad("");
		}
		
		//Validation: Un-checking a record and than submitting the records
		if(("Y").equalsIgnoreCase(dataMap.get("dynamicIncludeCheckbox"))){
			cu.unSelectCheckBox("dynamicIncludeCheckbox", "$destinationVal$", dataMap.get("Number_Inventory_NumbersTxt"));
			cu.clickElement("Number_Inventory_SubmitBtn");
			cu.waitForPageLoad("");
			
			//Condition to check if number unchecked belongs to Supplier/Customer Account
			String PopUp_Name = cu.returnElement("application_PopUpMessage").getText().trim();
			if(("Error : Number that is assigned to a Supplier Account or Customer account cannot be deleted. Kindly unassign them to proceed.").equalsIgnoreCase(PopUp_Name)){
				cu.checkMessage("application_PopUpTitle", "Clicking Submit button after removing a number from Supplier or Customer Acc","Error : Number that is assigned to a Supplier Account or Customer account cannot be deleted. Kindly unassign them to proceed.");
			}else{
				cu.checkMessage("application_PopUpTitle", "Clicking Submit button after deleting a number from Country",
						"SC / LN successfully added to the inventory. You will receive a confirmation email for reference.");
				
				//verifying the PDF file generated after submitting
				String parentWindow = cu.getCurrWindowName();
				cu.newWindowHandles(cu.getCurrWindowName());
				String newWindowTitle = cu.getTitle();
				
				if(newWindowTitle != null){
					if (cu.existsElement("pdfEmbed"))
						test.log(LogStatus.PASS, "EXPECTECD: PDF file should load after adding and submitting new number",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has loaded after adding and submitting new number in Inventory</span>");
					else {
						cu.getScreenShot("PDF file loading validation");
						test.log(LogStatus.FAIL, "EXPECTECD: PDF file should loaded after adding and submitting new number in Inventory",
								"Usage: <span style='font-weight:bold;'>ACTUAL:: PDF file has not loaded after adding and submitting new number in Inventory -contains no pdf in title: acutal title : "
										+ newWindowTitle + "</span>");
					}
					cu.getScreenShot("Validation Of pdf file generated after adding inventory in InventoryScreen");
					cu.DriverClose();	
				}
				// switch parent window
				cu.switchToWindow(parentWindow);
			}
			
		}
		
		
		//Taking screenshot and Logging out
		cu.getScreenShot("Validation Of Status in InventoryScreen");
		test = cu.getExTest();
		msgInsHomePage.doLogOut(test);
		
		//Printing pass/fail in the test data sheet
		cu.checkRunStatus();
	}
	
	  @BeforeMethod
	  @Parameters("testCaseId")
	  public void beforeMethod(String testCaseId) throws Exception {
		  DOMConfigurator.configure("log4j.xml");
		  Log.startTestCase("Start Execution");
		  Log.startTestCase(testCaseId);
		  extent = ExtReport.instance("NumberInventory");
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
		  try{
			  Log.endTestCase(testCaseId);
			  driver.quit();
			  //Ending the Extent test
			  extent.endTest(test);
			  //Writing the report to HTML format
			  extent.flush();    
		  } catch(Exception e){
			  LOGGER.info(" App Logout failed () :: Exception:"+e);
			  Log.error(" App Logout failed () :: Exception:"+e);
			  driver.quit();
			  Log.endTestCase(testCaseId);
			  extent.endTest(test);
			  extent.flush();  
		  }
	  }	
	  
	  
	  public void exportCSVAndValidateStatus(CommonUtils cu,String Status) throws Exception
	  {
		  	cu.deleteAllFilesInDownloadFolder();
			cu.clickElement("Number_Inventory_ExportBtn");
			cu.waitForPageLoad("NumberInventory");
			cu.sleep(2000);
			String csvFilePath = cu.getDownlaodedFileName();
						
			CSVUtil csvu = new CSVUtil(csvFilePath, 1);
			List<String> csvDatamap = csvu.getSingleColAllData("Status");
			
			for (String strSTatus: csvDatamap){
				if(strSTatus.equals(Status)){
					test.log(LogStatus.PASS, "EXPECTECD: Status should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Status in CSV is '"+strSTatus+"'  andd same in UI is "+Status+"'</span>");
				}else{
					test.log(LogStatus.FAIL, "EXPECTECD: Status should be same in both csv and UI", "Usage: <span style='font-weight:bold;'>ACTUAL:: Status is not same as in CSV it is '"+strSTatus+"'  and in UI it is "+Status+"'</span>");
				}
			}
				
	  }
	
}
