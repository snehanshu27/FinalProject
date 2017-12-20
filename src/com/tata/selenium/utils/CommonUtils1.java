package com.tata.selenium.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.constants.MyConstants;

/**
 * @date
 * @author
 * @description Utility class containing all the reusable methods
 */

public class CommonUtils1 implements ApplicationConstants {
	private static final Logger LOGGER = Logger.getLogger(CommonUtils.class.getName());
	// Creating required variables

	protected WebDriver driver;
	ExtentTest test;
	String sheetName;
	String uniqueDataId;
	String testCaseId;
	private static String timeStamp = "";
	private String downloadpathstr = FILE_DOWNLOAD_PATH;
	
	PropertyUtility putility;
	
	protected static ThreadLocal<RemoteWebDriver> threadDriver = null;

	ExcelUtils excelUtils = new ExcelUtils();

	public enum identifiers {
		ID, NAME, XPATH, CSS, LINKTEXT
	}
	
	public static String getTimeStamp() {
		return timeStamp;
	}

	public static void setTimeStamp(String timeStamp) {
		CommonUtils1.timeStamp = timeStamp;
	}
	
	public CommonUtils1(WebDriver driver, ExtentTest test, String sheetName, String uniqueDataId, String testCaseId,
			String objetResPath) {
		this.driver = driver;
		this.test = test;
		this.sheetName = sheetName;
		this.uniqueDataId = uniqueDataId;
		this.testCaseId = testCaseId;
		this.putility = new PropertyUtility(objetResPath, test);
	}

	/**
	 * @description Method used to instantiate and Launch URL
	 * @param extent
	 *            extent report variable
	 * @param test
	 *            extent report variable
	 * @param strUrl
	 *            variable containing url of application
	 * @param sheetName
	 *            variable holding SheetName from where data is retrieved
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @return returning true or false depending on the
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public WebDriver LaunchUrl(String strUrl) {
		try {
			Log.info("Launching url");
			LOGGER.info("Launching url :: " + MyConstants.getBrowser());
			// Create a new instance of the Firefox driver
			if ("FIREFOX".equalsIgnoreCase(MyConstants.getBrowser())) {
				driver = new FirefoxDriver();
				printLogs("New FIREFOX driver instantiated");
			}

			if ("IE".equalsIgnoreCase(MyConstants.getBrowser())) {
				// Create a new instance of the IE driver
				LOGGER.info("");
				LOGGER.info("IN IE LOOP");
				System.setProperty("webdriver.ie.driver", DRIVER_PATH + "/IEDriverServer.exe");
				driver = new InternetExplorerDriver();
				printLogs("New IE driver instantiated");
			}

			if ("CHROME".equalsIgnoreCase(MyConstants.getBrowser())) {
				// Create a new instance of the IE driver
				LOGGER.info("IN CHROME LOOP");
				System.setProperty("webdriver.chrome.driver", DRIVER_PATH + "/chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				Map<String, Object> prefs = new HashMap<>();
				new File(downloadpathstr).mkdirs();
				prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", 1);
				prefs.put("download.prompt_for_download", false);
				prefs.put("profile.default_content_settings.popups", 0);
				prefs.put("download.default_directory", downloadpathstr);
				options.setExperimentalOption("prefs", prefs);
				driver = new ChromeDriver(options);
				printLogs("New CHROME driver instantiated");
			}

			// Deleting all browser cookies
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);

			driver.get(strUrl);
			printLogs("URL launched is : " + strUrl);
			driver.manage().window().maximize();
			test.log(LogStatus.PASS, "URL should be launched",
					"TATA Messaging Exchange App  '" + strUrl + "' Launced sucessfully");
		} catch (Exception e) {
			LOGGER.error(" Url launch failed : " + e);
			printLogs(" Url launch failed : " + e);
			getScreenShot("Url_Launch_Failed");
			test.log(LogStatus.FAIL, "URL:  " + strUrl + " could not be launched - " + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			Assert.fail("Error occured while launching the url -   " + e);
		}
		return driver;

	}

	public boolean waitForElement(WebDriver driver, By objPath, int timeout) {
		boolean val = false;
		try {
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.visibilityOfElementLocated(objPath));
			val = true;
		} catch (Exception e) {
			LOGGER.error("Error occured on waiting for the element to appear  - " + e);
			Reporter.log("Error occured on waiting for the element to appear  - " + e);
			val = false;
			Assert.fail("Error occured on waiting for the element to appear  - " + e);
		}
		return val;
	}

	/*
	 * @description Method used to print logs
	 */
	public void printLogs(String stepDetail) {
		LOGGER.info(stepDetail);
		Reporter.log(stepDetail);
		Log.info(stepDetail);
	}

	/*
	 * @description Method used to wait for the complete page to load
	 */
	public void waitForPageLoad(String pageName) {

		JavascriptExecutor js = (JavascriptExecutor) driver;
		if ("complete".equals(js.executeScript("return document.readyState").toString())) {
			printLogs(pageName + " Page loaded successfully");
			return;
		}
	}

	public void checkRunStatus() {
		LogStatus value = test.getRunStatus();
		if ("pass".equalsIgnoreCase(value.toString())) {
			LOGGER.info("LogStatus " + value);
			excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
		} else {
			LOGGER.info("LogStatus " + value);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
		}

	}

	public void checkEditableDropDown(String fieldName, String value) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			Select oSelect = new Select(dropDownField);
			String defaultSelVal = oSelect.getFirstSelectedOption().getText();
			LOGGER.info("defaultSelVal is " + defaultSelVal);
			boolean val = dropDownField.isEnabled();
			if (val && defaultSelVal.trim().equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.PASS,
						"EXPECTECD: Drop down " + fieldName + " should be editable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable and default value selected is '" + value + "</span>");
			} else {
				printLogs(fieldName + " field is not editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTECD: Drop down " + fieldName + " should be editable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is non editable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkNonEditableDropDown(String fieldName) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			boolean val = dropDownField.isEnabled();
			if (val) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non-editable");
				test.log(LogStatus.PASS, "EXPECTECD: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is Non-editable</span>");
			}
		} catch (Exception e) {
			getScreenShot("Checking dropdown status " + fieldName);
			LOGGER.error(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkNonEditableBox(String fieldName, String value) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String editFieldval = textField.getAttribute("value").trim();
			if (textField.isEnabled() && !editFieldval.equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is editable and the value present is " + editFieldval);
				test.log(LogStatus.FAIL,
						"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable and default value present is -'" + editFieldval + "</span>");
			} else {
				printLogs(fieldName + " field is non editable and the value present is " + editFieldval);
				test.log(LogStatus.PASS,
						"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is not editable and default value present is -'" + editFieldval + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkNonEditableBox(String fieldName, String value, String replaceKeys, String replaceValues) {		
		try {				
				WebElement textField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
	
				String editFieldval = textField.getAttribute("value").trim();
				if (textField.isEnabled() && !editFieldval.equalsIgnoreCase(value)) 
				{
					printLogs(fieldName + " field is editable and the value present is " + editFieldval);
					test.log(LogStatus.FAIL,
							"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
									+ value,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
									+ " is editable and default value present is -'" + editFieldval + "</span>");
				}			
				else
				{
					printLogs(fieldName + " field is non editable and the value present is " + editFieldval);
					test.log(LogStatus.PASS,
							"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
									+ value,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
									+ " is not editable and default value present is -'" + editFieldval + "</span>");
				}
			
			}catch (Exception e) {
				getScreenShot("Selecting  " + value);
				LOGGER.error("Text field validations failed..." + e);
				printLogs("Text field validations failed..." + e);
				test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
				excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			}
	}
	
	public void checkNonEditableBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should not be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be Non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is Non editable</span>");

			}
		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkEditableBox(String fieldName, String value) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String strText = textField.getAttribute("value").trim();
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");

			}
			if (strText.equalsIgnoreCase(value.trim())) {
				printLogs(fieldName + " field has default value as -" + strText);
				test.log(LogStatus.PASS,
						"EXPECTECD: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is has default value as " + strText + "</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL,
						"EXPECTECD: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " has default value as - " + strText + "</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkElementPresence(String fieldName) {
		try {
			WebElement uiElement = driver.findElement(putility.getObject(fieldName));
			if (uiElement.isDisplayed()) {
				printLogs(fieldName + " Element is present and displayed in the page");
				test.log(LogStatus.PASS, "EXPECTECD: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is displayed</span>");
			} else {
				printLogs(fieldName + " Element is not present and not displayed in the page");
				test.log(LogStatus.FAIL, "EXPECTECD: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is not displayed</span>");
			}
		} catch (Exception e) {
			getScreenShot("UI Element  " + fieldName);
			LOGGER.error(fieldName + " -UI Element validation failed..." + e);
			printLogs(fieldName + " -UI Element validation failed..." + e);
			test.log(LogStatus.FAIL, "UI Element validation", "UI Element validation failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	/**
	 * @description Method used to set data in editbox in app
	 * @param test
	 *            extent report variable
	 * @param objPath
	 *            Variable holding locator of the object in the web page
	 * @param setValue
	 *            Variable holding value which needs to be set
	 */
	public void SetData(String fieldName, String setValue) {
		try {
			WebElement objPath = driver.findElement(putility.getObject(fieldName));
			if (setValue != null && setValue.trim().length() > 0) {
				objPath.clear();
				objPath.sendKeys(setValue);
				printLogs("Data  '" + setValue + "' entered sucessfully in the " + fieldName + " box");
				test.log(LogStatus.PASS, "Enter value in the field",
						"Value - '" + setValue + "' entered sucessfully in " + fieldName + "field");
			}
		} catch (Exception e) {
			LOGGER.error("Error occured while setting data  " + setValue + "  - " + e);
			printLogs("Error occured while setting data  " + setValue + "  - " + e);
			test.log(LogStatus.FAIL, "Enter value in " + fieldName,
					"Value - '" + setValue + "' could not be entered - " + e);
		}
	}

	public void SetDataWithoutClearing(String fieldName, String setValue) {
		try {
			WebElement objPath = driver.findElement(putility.getObject(fieldName));
			if (setValue != null && setValue.trim().length() > 0) {
				objPath.sendKeys(Keys.chord(Keys.CONTROL, "a"));
				objPath.sendKeys(setValue);
				printLogs("Data  '" + setValue + "' entered sucessfully in the " + fieldName + " box");
				test.log(LogStatus.PASS, "Enter value in the field",
						"Value - '" + setValue + "' entered sucessfully in " + fieldName + "field");
			}
		} catch (Exception e) {
			LOGGER.error("Error occured while setting data  " + setValue + "  - " + e);
			printLogs("Error occured while setting data  " + setValue + "  - " + e);
			test.log(LogStatus.FAIL, "Enter value in " + fieldName,
					"Value - '" + setValue + "' could not be entered - " + e);
		}
	}

	/**
	 * @description Method used to click any object the web page
	 * @param test
	 *            extent report variable
	 * @param clkObject
	 *            Variable holding object locator value
	 * @param fieldname
	 *            Variable holding object name to uniquely identify field in the
	 *            web page
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @return returning true or false depending on the
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public boolean clickElement(String fieldname) {

		By locator = putility.getObject(fieldname);
		try {
			WebElement clkObject = driver.findElement(locator);
			clkObject.click();
			printLogs(fieldname + " - having xpath '" + locator + "' clicked sucessfully");
			test.log(LogStatus.PASS, "Element should be clicked sucessfully", fieldname + " - clicked sucessfully");
			return true;
		} catch (Exception e) {
			getScreenShot("'" + fieldname + "'  could not be clicked");
			test.log(LogStatus.WARNING, "Element should be clicked sucessfully",
					fieldname + " -  could not be clicked because -" + e);
			printLogs(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			LOGGER.error(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			return false;
		}
	}
	
	public boolean clickElementNOExtRep(String fieldname, String replaceKeys, String replaceValues) {		
		By locator = null;
		try {	
						
			locator = putility.getObject(fieldname, replaceKeys, replaceValues);	
			WebElement clkObject = driver.findElement(locator);
			clkObject.click();
			printLogs(fieldname + " - having xpath '" + locator + "' clicked sucessfully");
			return true;
		} catch (Exception e) {
			getScreenShot("'" + fieldname + "'  could not be clicked");
			printLogs(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			LOGGER.error(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			return false;
		}
	}

	/**
	 * @description Method used to select values from drop down using visible
	 *              text
	 * @param test
	 *            extent report variable
	 * @param supplierName
	 *            Variable holding locator of the object in the web page
	 * @param value
	 *            Variable holding value whch needs to be selected from drop
	 *            down
	 * @param sheetName
	 *            variable holding SheetName from where data is retrieved
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @return returning true or false depending on the
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public boolean SelectDropDownByVisibleText(String filedname, String value) {
		try {
			if (value != null && value.trim().length() > 0) {
			WebElement supplierName = driver.findElement(putility.getObject(filedname));
			
				Select oSelect = new Select(supplierName);
				oSelect.selectByVisibleText(value);
				printLogs("Selected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be selected",
						"Drop down value -'" + value + "'- selected sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error("Dropdown selection failed..." + e);
			printLogs("Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, "Drop down should be selected",
					"Drop down value -'" + value + "'- could not be selected because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}
	
	public boolean SelectDropDownByVisibleText(String fieldname, String value, String replaceKeys, String replaceValues) {		
		try {
			if (value != null && value.trim().length() > 0) {
				
				By locator = putility.getObject(fieldname, replaceKeys, replaceValues);
				
				WebElement element = driver.findElement(locator);
			
				Select oSelect = new Select(element);
				oSelect.selectByVisibleText(value);
				printLogs("Selected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be selected",
						"Drop down value -'" + value + "'- selected sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error("Dropdown selection failed..." + e);
			printLogs("Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, "Drop down should be selected",
					"Drop down value -'" + value + "'- could not be selected because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}

	/**
	 * @description Methos used to switch to different frame using frame name
	 * @param frameName
	 *            Variable holding frame name
	 */
	public void SwitchFrames(String frameName) {
		if (frameName.contains("//")) {
			try {
				WebElement frame = driver.findElement(By.xpath(frameName));
				driver.switchTo().frame(frame);
				printLogs("Successfully switched  to " + frameName + " frame");
			} catch (Exception e) {
				LOGGER.error(frameName + " - Frame could not be changed - " + e);
				printLogs(frameName + " - Frame could not be changed - " + e);
			}
		} else {
			try {
				driver.switchTo().frame(frameName);
				printLogs("Successfully switched  to " + frameName + " frame");
			} catch (Exception e) {
				LOGGER.error(frameName + " - Frame could not be changed - " + e);
				printLogs(frameName + " - Frame could not be changed - " + e);
			}
		}
	}

	/**
	 * @description Method used to check for any pop up currently being
	 *              displayed in the application
	 * @param test
	 *            extent report variable
	 * @param objPath
	 *            Variable holding the locator value to identify elemnt in web
	 *            page
	 * @param sheetName
	 *            variable holding SheetName from where data is retrieved
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @param testStep
	 *            Varibale holding the description for the current step during
	 *            execution
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public void checkPopUp(String popFieldName, String testStep) {
		boolean val = false;
		String popUpName = null;
		try {
			WebElement objPath = driver.findElement(putility.getObject(popFieldName));

			if ((objPath).isDisplayed()) {
				val = true;
				printLogs("POP Up has appeared now");
				getScreenShot(testStep);
				WebElement element = driver.findElement(putility.getObject("application_PopUpMessage"));
				popUpName = element.getText();
				test.log(LogStatus.FAIL, "Test failed during  " + testStep,
						"Pop up is being displayed and it is  - " + popUpName);
				driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
				excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Error occured because  Pop up is being displayed and it is  - " + popUpName);
			}
		} catch (Exception e) {
			LOGGER.error("ERROR " + e);
		}
		if (val)
			Assert.fail("Error occured because  of POP Up - '" + popUpName + "' at step - " + testStep);
	}

	public void checkMessage(String popFieldName, String testStep, String expectedPopUpMsg) {
		String popUpName = null;
		try {
			WebElement objPath = driver.findElement(putility.getObject(popFieldName));
			if ((objPath).isDisplayed()) {
				printLogs("POP Up has appeared now");
				getScreenShot(testStep.replace("\\:", "\\-"));
				WebElement element = driver.findElement(putility.getObject("application_PopUpMessage"));
				popUpName = element.getText().trim();
				if (popUpName.equalsIgnoreCase(expectedPopUpMsg.trim())) {
					test.log(LogStatus.PASS, "POP Up is expected at " + testStep,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up is being displayed and it is  - "
									+ popUpName + "</span>");
					driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
					excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
					excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
					printLogs("Validation passed as Pop up is being displayed and it is  - " + popUpName);
				} else {
					printLogs("Appeared Pop is  - " + popUpName + "  but expected POP Up was " + expectedPopUpMsg);
					excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
					excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
					test.log(LogStatus.WARNING, "Expected POP Up is-  " + expectedPopUpMsg,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up appeared is different than expected."
									+ popUpName + "</span>");
				}
			} else {
				excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Validation failed as expected Pop up did not get displayed");
				test.log(LogStatus.FAIL, "POP Up is expected at " + testStep,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up did not get displayed</span>");
			}
		} catch (Exception e) {
			LOGGER.error("Error " + e);
		}
	}

	public void acceptUIPopupMessage() {
		String popUpName = null;
		try {
			
			driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
			LOGGER.info("UI Popup Message accepted");
		} catch (Exception e) {
			LOGGER.error("Error occured while accepting UI popup Message" + e);
		}
	}

	public WebElement returnElement(String fieldName) {
		WebElement objPath = null;
		try {
			objPath = driver.findElement(putility.getObject(fieldName));

		} catch (Exception e) {
			LOGGER.error("Element " + fieldName + " not found because - " + e);
			printLogs("Element " + fieldName + " not found because - " + e);
		}
		return objPath;
	}

	public static String getCurrentTimeStamp() {
		String currentTimeStampL;
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		currentTimeStampL = "-" + c.get(Calendar.DATE) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.YEAR)
				+ "_" + c.get(Calendar.HOUR_OF_DAY) + "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
		return currentTimeStampL;
	}

	/**
	 * @description Method used to get the current system date
	 * @return Returning the current date
	 */
	public String GetCurrentSystemDate() {
		GregorianCalendar date = new GregorianCalendar();
		String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH));
		String month = String.valueOf(date.get(Calendar.MONTH));
		String year = String.valueOf(date.get(Calendar.YEAR));
		return "" + (month + 1) + "/" + day + "/" + year;
	}

	/**
	 * @description Method used to get previous date,one day before current date
	 * @return Returning the previous date
	 */
	public String GetPreviousDate() {
		GregorianCalendar date = new GregorianCalendar();
		String day = String.valueOf(date.get(Calendar.DAY_OF_MONTH) - 1);
		String month = String.valueOf(date.get(Calendar.MONTH));
		String year = String.valueOf(date.get(Calendar.YEAR));
		return "" + (month + 1) + "/" + (day) + "/" + year;
	}

	/**
	 * @description Method used to check the existence of any element in the web
	 *              page
	 * @param strElement
	 *            Variable holding the locator value
	 * @return returning true or false depending on the criteria
	 */
	public boolean existsElement(By strElement) {
		try {
			WebElement element = driver.findElement(strElement);
			element.getTagName();
			Log.info(element + "Exist");
			return true;
		} catch (NoSuchElementException e) {
			LOGGER.error("Error " + e);
			return false;
		}
	}

	public WebElement getElement(By element) {
		WebElement ele = null;
		try {
			ele = driver.findElement(element);
			printLogs(ele + " element found");
			return ele;
		} catch (Exception e) {
			LOGGER.error("element not found  " + e);
			printLogs(ele + " element not found");
		}
		return ele;
	}
	


	/**
	 * @description Method used to switch to different window based on the
	 *              window id provided
	 * @param givenWindow
	 *            Variable holding the window id
	 */
	public void switchToWindow(String givenWindow) {
		try {
			sleep(3000);
			driver.switchTo().window(givenWindow);
		} catch (Exception e) {
			LOGGER.error("error  " + e);

		}

	}

	/**
	 * @description Method used to fetch the text value from the locator tag
	 * @param path
	 *            Variable holding the locator value
	 * @return
	 * @return Returning the text
	 */
	public String getText(By path) {
		return driver.findElement(path).getText();

	}

	/**
	 * @description Method used to pause execution for the given time
	 * @param millis
	 *            Varible holding the value until which it will wait in
	 *            miliseconds
	 */
	public void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			LOGGER.log(Level.WARN, "Interrupted!", e);
			try {
				throw e;
			} catch (Exception ex) {

				LOGGER.info("error  " + ex);
			}

		}
	}

	/**
	 * @description Method used to close the current browser
	 */
	public void DriverClose() {
		driver.close();
	}

	/**
	 * @description Method used to switch to an alert and enter text into it
	 * @param msg
	 *            Variable holding value to be passed to the alert
	 */
	public void HandleAlert(String msg) {
		try {
			driver.switchTo().alert().sendKeys(msg);
		} catch (Exception e) {
			LOGGER.error("Exception is - " + e);
		}
	}

	/**
	 * @description Method used to accept the alert
	 */
	public void ConfirmAlert() {
		try {
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			LOGGER.error("Exception occure while looking for alert  - " + e);
			printLogs("Exception occure while looking for alert  - " + e);
		}

	}

	/**
	 * @description Method used to check which element is selected by default in
	 *              webpage
	 * @param objPath
	 *            Variable holding the locator value
	 */
	public String currentSelectedVal(WebElement objPath) {
		String value = null;
		try {
			Select sel = new Select(objPath);
			WebElement option = sel.getFirstSelectedOption();
			value = option.getText();
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception:  " + e);
			Log.error("Exception :" + e.getMessage());
		}
		return value;

	}

	/**
	 * @description Method used to check which element is selected by default in
	 *              webpage
	 * @param filedName
	 *            Variable holding the locator value
	 */
	public String currentSelectedVal(String filedName) {
		String value = null;
		try {
			WebElement elem = driver.findElement(putility.getObject(filedName));
			Select sel = new Select(elem);
			WebElement option = sel.getFirstSelectedOption();
			value = option.getText();
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception:  " + e);
			Log.error("Exception :" + e.getMessage());
		}
		return value;

	}

	public WebElement getElement(String filedName) {
		WebElement ele = null;
		try {
			ele = driver.findElement(putility.getObject(filedName));
			printLogs(filedName + " element found");
			return ele;
		} catch (Exception e) {
			LOGGER.error(filedName + " element not found  " + e);
			printLogs(filedName + " element not found");
		}
		return ele;
	}
	
	public List<WebElement> getElements(String filedName) {
		List<WebElement> elemts = null;
		try {
			elemts = driver.findElements(putility.getObject(filedName));
			printLogs(filedName + " elements found. Size: "+elemts.size());
			return elemts;
		} catch (Exception e) {
			LOGGER.error(filedName+" elements not found  " + e);
			printLogs(filedName + " elements not found");
		}
		return elemts;
	}
	
	/**
	 * @description Method used to switch to default content in the web page
	 */
	public void default_content() {
		driver.switchTo().defaultContent();
	}

	/**
	 * @description Method used to get the current window id
	 * @return Returning the current window id
	 */
	public String getCurrWindowName() {
		try {
			return driver.getWindowHandle();
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
			return null;
		}

	}

	public Set<String> getCurrWindowNames() {
		try {
			return driver.getWindowHandles();
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
			return null;
		}

	}
	
	/**
	 * @description Method used to select check boxes
	 * @param objPath
	 *            Variable holding the locator value
	 * @return returning true or false depending on the criteria
	 */
	public boolean selectCheckBox(String filedname) {
		try {
			WebElement supplierName = driver.findElement(putility.getObject(filedname));
			if (!supplierName.isSelected()) {
				supplierName.click();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Exception on Selecting Check Box ::  " + e);
			printLogs("Exception on Selecting Check Box :" + e.getMessage());
			return false;
		}
	}

	/**
	 * @description Method used to switch to new window if it exists
	 */
	public void newWindowHandles(String pwindow) {
		try {
			sleep(2000);

			for (String handle : driver.getWindowHandles()) {
				if (!pwindow.equals(handle)) {
					driver.switchTo().window(handle);
					LOGGER.info("Child Window   " + handle);
				}
			}
		} catch (Exception e) {
			LOGGER.error("New Handle window error - " + e);
		}
	}

	/**
	 * @description Method used to take screenshot of the page by calling
	 *              another method in it
	 * @param test
	 *            extent report variable
	 * @param sheetName
	 *            variable holding SheetName from where data is retrieved
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @param testStep
	 *            Variable holding the step description
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public void takeScreenShot(ExtentTest test, String sheetName, String testCaseId, String testStep) throws Exception {
		getScreenShot(testStep);

	}

	/**
	 * description Method used to take screenshot
	 * 
	 * @param driver
	 *            Varibale containg the driver instance
	 * @param testCase
	 *            Variable holding the Test Case Id
	 * @param testStep
	 *            Variable holding the step description
	 * @param test
	 *            extent report variable
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public void getScreenShot(String testStep) {
		try {
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			String fileName = "SS_" + uniqueDataId + "_" + testCaseId + "_" + testStep + "-" + calendar.get(Calendar.DATE)
					+ "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.YEAR) + "_" + calendar.get(Calendar.HOUR_OF_DAY)
					+ "-" + calendar.get(Calendar.MINUTE) + "-" + calendar.get(Calendar.SECOND) + ".png";
			File file = new File(SCREENSHOT_PATH + "" + fileName);
			FileUtils.copyFile(screenshot, file);
			test.log(LogStatus.INFO, "Snapshot for  " + testStep + "  : "
					+ test.addScreenCapture(REPORT_SCREENSHOT_PATH + "" + fileName));
		} catch (Exception e) {
			LOGGER.error("ERROR IN SCREENSHOT." + e);
		}
	}

	public ExtentTest getExTest() {
		return test;
	}

	/**
	 * @description Method used to wait for any element to appear in the page
	 * @param objPath
	 *            Variable holding the locator value
	 * @return returning true or false depending on the criteria
	 */
	public boolean waitForElement(By objPath) {

		try {
			WebDriverWait wait = new WebDriverWait(driver, 40);
			wait.until(ExpectedConditions.elementToBeClickable(objPath));
			return true;
		} catch (Exception e) {
			LOGGER.error("Error occured on waiting for the element to appear  - " + e);
			printLogs("Error occured on waiting for the element to appear  - " + e);
			return false;
		}
	}

	/**
	 * @description Method used to de select the drop down selection
	 * @param test
	 *            extent report variable
	 * @param objPath
	 *            Variable holding the locator value
	 * @param value
	 *            Variable holding the drop down value
	 * @param sheetName
	 *            variable holding SheetName from where data is retrieved
	 * @param testCaseId
	 *            variable holding current Test Case ID which is being executed
	 * @throws Exception
	 *             throwing exception for any error occurring in try block
	 */
	public void DeSelectDropDown(ExtentTest test, By objPath, String value, String sheetName, String testCaseId) {

		try {
			if (value != null) {
				Select oSelect = new Select(driver.findElement(objPath));
				oSelect.selectByValue(value);
				Log.info("Selected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be selected",
						"Drop down value -'" + value + "'- selected sucessfully");
			}
		} catch (Exception e) {
			printLogs("Value - '" + value + "' from the dropdown could not be selected   - " + e);
			LOGGER.info("Value - '" + value + "' from the dropdown could not be selected   - " + e);
			test.log(LogStatus.WARNING, "Drop down should be selected",
					"Drop down value -'" + value + "'- could not be selected because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", testCaseId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, testCaseId, "Result_Errors");
		}
	}

	public List<String> getAllOptionsFromDropDown(String filedname) {
		List<String> retStrOPs = new ArrayList<>();
		try {
			WebElement supplierName = driver.findElement(putility.getObject(filedname));
			Select oSelect = new Select(supplierName);
			List<WebElement> optionElemens = oSelect.getOptions();
			for (WebElement opele : optionElemens)
				retStrOPs.add(opele.getText());

			printLogs(retStrOPs.size() + " were obtianed from the " + filedname + "dropdown");
			return retStrOPs;
		} catch (Exception e) {
			LOGGER.error("get All Options From DropDown failed..." + e);
			getScreenShot("Options From DropDown - " + filedname);
			printLogs("get All Options From DropDown failed..." + e);
			return retStrOPs;
		}
	}

	public String getSelectVauleFromDropDown(String filedname) {
		String selectedOpStr = "";
		try {
			WebElement supplierName = driver.findElement(putility.getObject(filedname));
			Select oSelect = new Select(supplierName);
			selectedOpStr = oSelect.getFirstSelectedOption().getText();
			printLogs(selectedOpStr + " seems to selected in " + filedname + " dropdown");
			return selectedOpStr;
		} catch (Exception e) {
			LOGGER.error("get selected option From DropDown failed..." + e);
			getScreenShot("selectedOpStr - " + filedname);
			printLogs("get selected option From DropDown failed..." + e);
			return selectedOpStr;
		}
	}

	public boolean verifyHistoryTabFields(String popFieldName) {
		boolean val = false;
		try {
			WebElement objPath = driver.findElement(putility.getObject(popFieldName));
			if ((objPath).isDisplayed()) {
				val = true;
				getScreenShot("Validation of " + popFieldName);
				driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
			} else {
				val = false;
			}
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
			val = false;
		}
		return val;
	}

	public void checkFiveLatestValFromDropDown(String filedname, List<String> dropDwonValues) {
		int historyValueCount = dropDwonValues.size();
		for (int i = 1; i < historyValueCount; i++) {
			String history = dropDwonValues.get(i);
			SelectDropDownByVisibleText("History", history);
			clickElement("supplier_EditBtn");
			if (verifyHistoryTabFields("application_PopUpTitle")) {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i + "nd/th Value which is  '"
									+ history + "'  from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: " + i + "nd/th Value from List should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i + "nd/th Value which is   '"
									+ history + "'  from List is editable</span>");
				}
			}
		}
	}

	public void checkEditableDate(String fieldName, String value) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String strText = textField.getAttribute("value");
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");

			}
			if (strText.equalsIgnoreCase(value.trim())) {
				printLogs(fieldName + " field has default value as -" + strText);
				test.log(LogStatus.PASS,
						"EXPECTECD: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ "  has default value as " + strText + "</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL,
						"EXPECTECD: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " has default value as - " + strText + "</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.error("Text field validations failed...:  " + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public boolean checkDisabledBtn(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (textField.isEnabled()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
			return false;
		}
	}

	public void validatePopulatedDropDownValue(String fieldName, String value) {
		try {

			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			Select oSelect = new Select(dropDownField);
			String defaultSelVal = oSelect.getFirstSelectedOption().getText();
			LOGGER.info("defaultSelVal is " + defaultSelVal);

			if (defaultSelVal.trim().equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.PASS,
						"EXPECTECD: Drop down " + fieldName + " should be editable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable and default value selected is '" + value + "</span>");

			} else {
				printLogs(fieldName + " field is not editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTECD: Drop down " + fieldName + " should be editable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is non editable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void getCalendarDate() {
		WebElement calendar = driver.findElement(By.xpath("//*[@id='ui-datepicker-div']"));
		WebElement next = driver.findElement(By.cssSelector(".ui-datepicker-next"));
		next.click();
		List<WebElement> columns = calendar.findElements(By.tagName("td"));
		for (WebElement cell : columns) {
			// Select 13th Date
			if ("13".equals(cell.getText())) {
				cell.findElement(By.linkText("13")).click();
				break;
			}
		}
	}

	public String getTitle() {
		try {
			return driver.getTitle();
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
			return "";
		}

	}

	public boolean existsElement(String fieldName) {
		try {
			By locBY = putility.getObject(fieldName);
			driver.findElement(locBY);
			printLogs(fieldName + " element Exist");

			return true;
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			return false;
		}
	}
	
	public boolean existDisplayed(String fieldName) {
		try {
			By locBY = putility.getObject(fieldName);
			boolean ret = driver.findElement(locBY).isDisplayed();
			printLogs(fieldName + " element Exist");

			return ret;
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			return false;
		}
	}
	
	public boolean existsElement(String fieldName, String replaceKeys, String replaceValues) {
		try {
			By locator = putility.getObject(fieldName, replaceKeys, replaceValues);
			
			driver.findElement(locator);
			printLogs(fieldName + " element Exist");

			return true;
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			return false;
		}
	}

	public void checkFiveLatestValFromDropDownForCostMgmt(String filedname, List<String> dropDwonValues) {
		int historyValueCount = dropDwonValues.size();
		for (int i = 1; i < historyValueCount; i++) {
			String history = dropDwonValues.get(i);
			SelectDropDownByVisibleText("CostManagement_Cost_CardLst", history);
			clickElement("CostManagement_DisplayBtn");
			clickElement("CostManagement_SubmitBtn");
			if (i == 1) {
				clickElement("application_PopUpNoBtn");
			}
			if (verifyHistoryTabFields("application_PopUpTitle")) {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: " + i + "nd/th Value from List should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is editable</span>");
				}
			}
		}
	}

	private String mapMothNumberToAlphabet(String monthNumber) {
		String monthAlpabets = null;
		switch (monthNumber) {
		case "01":
			monthAlpabets = "January";
			break;
		case "02":
			monthAlpabets = "February";
			break;
		case "03":
			monthAlpabets = "March";
			break;
		case "04":
			monthAlpabets = "April";
			break;
		case "05":
			monthAlpabets = "May";
			break;
		case "06":
			monthAlpabets = "June";
			break;
		case "07":
			monthAlpabets = "July";
			break;
		case "08":
			monthAlpabets = "August";
			break;
		case "09":
			monthAlpabets = "September";
			break;
		case "10":
			monthAlpabets = "October";
			break;
		case "11":
			monthAlpabets = "November";
			break;
		case "12":
			monthAlpabets = "December";
			break;

		default:
			LOGGER.error("Incorrect Month");
			break;
		}

		return monthAlpabets;
	}

	public void selectDate(String filedname, String date) {
		try {
			if (date.trim().length() > 0) {
				String[] dates = date.split("-");
				String day = String.valueOf(Integer.valueOf(dates[0].trim()));
				String month = dates[1].trim();
				String year = dates[2].trim();
				WebElement dropDownField = driver.findElement(putility.getObject(filedname));
				dropDownField.click();
				SelectDropDownByVisibleText("selectYear", year);
				String month1 = mapMothNumberToAlphabet(month);
				String getmonthVal = driver.findElement(By.xpath("//div[@class='ui-datepicker-title']/span")).getText()
						.trim();

				while (!getmonthVal.equalsIgnoreCase(month1)) {
					driver.findElement(putility.getObject("clickNextBtn")).click();
					getmonthVal = driver.findElement(By.xpath("//div[@class='ui-datepicker-title']/span")).getText()
							.trim();
				}

				WebElement ele = driver.findElement(By.linkText(day));
				Actions action = new Actions(driver);
				action.moveToElement(ele).click().perform();
			}

		} catch (Exception e) {
			LOGGER.error("Exception : " + e);
		}
	}

	public String getText(String fieldName) {
		try {
			By locBY = putility.getObject(fieldName);
			return driver.findElement(locBY).getText();
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while getting text from element : " + fieldName + "  - " + e);
			printLogs("Exception occured while getting text from element : " + fieldName + "  - " + e);
			return "";
		}
	}

	public String getAttribute(String fieldname, String attribute, String replaceKeys, String replaceValues) {
		try {
			By locator = putility.getObject(fieldname, replaceKeys, replaceValues);

			return driver.findElement(locator).getAttribute(attribute);
		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while getting attribute " + attribute + " from element : " + fieldname
					+ "  - " + e);
			printLogs("Exception occured while getting attribute " + attribute + " from element : " + fieldname + "  - "
					+ e);
			return "";
		}
	}

	public String getAttribute(String fieldname, String attribute) {
		try {

			By locator = putility.getObject(fieldname);

			return driver.findElement(locator).getAttribute(attribute);

		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while getting attribute " + attribute + " from element : " + fieldname
					+ "  - " + e);
			printLogs("Exception occured while getting attribute " + attribute + " from element : " + fieldname + "  - "
					+ e);
			return "";
		}
	}

	public void deleteAllFilesInDownloadFolder() {
		File folder = new File(downloadpathstr);
		File[] allfilesInFolder = folder.listFiles();

		printLogs("AllfilesInFolder :" + allfilesInFolder.length);
		for (int i = 0; i < allfilesInFolder.length; i++) {
			printLogs("file: " + allfilesInFolder[i].getName());
			allfilesInFolder[i].delete();
		}

		printLogs("deleted all files from download loaction");
	}

	public String getDownlaodedFileName() {
		checkforanydownloadsinprogress();
		String retfilename = null;

		File folder = new File(downloadpathstr);
		File[] allfilesInFolder = folder.listFiles();
		Arrays.sort(allfilesInFolder, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

		if (allfilesInFolder[0].getName().contains(".csv")) {

			retfilename = allfilesInFolder[0].getName();
		}

		printLogs("dowloaded file path: " + downloadpathstr + retfilename);
		return downloadpathstr + retfilename;

	}

	private void checkforanydownloadsinprogress() {
		printLogs("checking for download completed");
		Boolean noinprogress = false;
		File folder = new File(downloadpathstr);
		File[] allfilesInFolder = folder.listFiles();
		Arrays.sort(allfilesInFolder, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

		for (int i = 0; i < 15; i++) {
			if (allfilesInFolder.length != 0)
				break;
			sleep(2000);
			allfilesInFolder = folder.listFiles();
			Arrays.sort(allfilesInFolder, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		}

		while (!noinprogress) {
			if (!allfilesInFolder[0].getName().contains(".crdownload"))
				noinprogress = true;
			allfilesInFolder = folder.listFiles();
			Arrays.sort(allfilesInFolder, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		}

		printLogs("no downloads pending");
	}

	public void selectCheckBox(String filedname, String replaceKeys, String replaceValues) {

		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			if (!driver.findElement(locator).isSelected())
				driver.findElement(locator).click();

			printLogs(filedname + " checkbox has been selected");
			test.log(LogStatus.PASS, "EXPECTECD: checkbox " + filedname + " should be selected",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
							+ " has been selected</span>");

		} catch (Exception e) {
			getScreenShot("selecting checkbox " + filedname);
			LOGGER.error("Selecting checkbox failed..." + e);
			printLogs("Selecting checkbox failed..." + e);
			test.log(LogStatus.FAIL, "Selecting checkbox failed", "Selecting checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void unSelectCheckBox(String filedname, String replaceKeys, String replaceValues) {

		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			if (driver.findElement(locator).isSelected())
				driver.findElement(locator).click();

			printLogs(filedname + " checkbox has been unselected");
			test.log(LogStatus.PASS, "EXPECTECD: checkbox " + filedname + " should be unselected",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
							+ " has been unselected</span>");

		} catch (Exception e) {
			getScreenShot("Unselecting checkbox " + filedname);
			LOGGER.error("Unselecting checkbox failed..." + e);
			printLogs("Unselecting checkbox failed..." + e);
			test.log(LogStatus.FAIL, "Unselecting checkbox failed", "Unselecting checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkCheckBoxSelected(String filedname, String replaceKeys, String replaceValues) {

		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			if (driver.findElement(locator).isSelected()) {

				test.log(LogStatus.PASS, "EXPECTECD: checkbox " + filedname + " should be in selected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in selected status</span>");
				printLogs(filedname + " checkbox is in selected status");
			} else {
				getScreenShot(filedname + " checkbox in Unselected status");
				test.log(LogStatus.FAIL, "EXPECTECD: checkbox " + filedname + " should be in selected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Unselected status</span>");
				printLogs(filedname + " checkbox in Unselected status");
			}

		} catch (Exception e) {
			getScreenShot("validate checkbox " + filedname);
			LOGGER.error("validate checkbox failed..." + e);
			printLogs("validate checkbox failed..." + e);
			test.log(LogStatus.FAIL, "validate checkbox failed", "validate checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkCheckBoxUnselected(String filedname, String replaceKeys, String replaceValues) {

		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			if (!driver.findElement(locator).isSelected()) {

				test.log(LogStatus.PASS, "EXPECTECD: checkbox " + filedname + " should be in Unselected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Unselected status</span>");
				printLogs(filedname + " checkbox is in Unselected status");
			} else {
				getScreenShot(filedname + " checkbox in Unselected status");
				test.log(LogStatus.FAIL, "EXPECTECD: checkbox " + filedname + " should be in Unselected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Selected status</span>");
				printLogs(filedname + " checkbox in Selected status");
			}

		} catch (Exception e) {
			getScreenShot("validate checkbox " + filedname);
			LOGGER.error("validate checkbox failed..." + e);
			printLogs("validate checkbox failed..." + e);
			test.log(LogStatus.FAIL, "validate checkbox failed", "validate checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public Boolean isCheckBoxSelected(String filedname, String replaceKeys, String replaceValues) {

		Boolean ret = null;
		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			if (driver.findElement(locator).isSelected()) {
				printLogs(filedname + " checkbox in Selected status");
				ret = true;
			} else {
				printLogs(filedname + " checkbox in Unselected status");
				ret = false;
			}

		} catch (Exception e) {
			LOGGER.error("failed to get checkbox status..." + e);
			printLogs("failed to get checkbox status..." + e);

		}
		return ret;
	}

	public String getAlertMessage() {
		String ret = "";
		try {
			ret = driver.switchTo().alert().getText();
			printLogs("alertMessage Obtained as : " + ret);
			return ret;
		} catch (Exception e) {
			LOGGER.error("Exception occure while looking for alert  - " + e);
			printLogs("Exception occure while looking for alert  - " + e);
			return ret;
		}

	}

	public String getText(String fieldName, String replaceKeys, String replaceValues) {
		try {
			By locator = putility.getObject(fieldName, replaceKeys, replaceValues);
			return driver.findElement(locator).getText().trim();

		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while getting text from element : " + fieldName + "  - " + e);
			printLogs("Exception occured while getting text from element : " + fieldName + "  - " + e);
			return "";
		}
	}

	public void sendKeys(String filedname, String value, boolean clearAndSend) {

		try {

			By locator = putility.getObject(filedname);
			WebElement ele = driver.findElement(locator);
			if (clearAndSend)
				ele.clear();
			ele.sendKeys(value);

			printLogs(value + " has been sent/entered to elemet " + filedname);

		} catch (Exception e) {
			LOGGER.error("failed to sent/entered in text in element " + filedname + " .... " + e);
			printLogs("failed to sent/entered in text in element " + filedname + " .... " + e);
		}
	}

	public void sendTabKeys(String filedname) {

		try {

			By locator = putility.getObject(filedname);
			WebElement ele = driver.findElement(locator);

			ele.sendKeys(Keys.TAB);

		} catch (Exception e) {
			LOGGER.error("failed to press Tab from element " + filedname + " .... " + e);
			printLogs("failed to press Tab from element " + filedname + " .... " + e);
		}
	}

	public void enterData(String filedname, String value) {
		try {
			if (value.trim().length() > 0) {
				By locator = putility.getObject(filedname);
				WebElement ele = driver.findElement(locator);
				ele.sendKeys(value);
			}

		} catch (Exception e) {
			LOGGER.error("failed to enter text in element " + filedname + " .... " + e);
			printLogs("failed to enter text in element " + filedname + " .... " + e);
		}
	}

	public void checkEditableBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkReadonlyProperty(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String editFieldval = textField.getAttribute("readonly");
			if (textField.isDisplayed()
					&& ("true".equalsIgnoreCase(editFieldval) || "readonly".equalsIgnoreCase(editFieldval))) {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkReadonlyProperty(String fieldName, String value) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String text = textField.getAttribute("readonly");
			LOGGER.info("the text value is : " + text);
			String editFieldval = textField.getText().trim();

			if (textField.isDisplayed() && "readonly".equalsIgnoreCase(text)) {
				printLogs(fieldName + " field is editable and the value present is " + editFieldval);
				test.log(LogStatus.FAIL,
						"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable and default value present is -'" + value + "</span>");
			} else {
				printLogs(fieldName + " field is non editable and the value present is " + editFieldval);
				test.log(LogStatus.PASS,
						"EXPECTECD: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is not editable and default value present is -'" + value + "</span>");

			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.error("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public boolean ElementNotExists(String fieldName) {
		try {
			By locBY = putility.getObject(fieldName);
			driver.findElement(locBY);
			printLogs(fieldName + " element Exist");

			return false;
		} catch (NoSuchElementException e) {
			LOGGER.error(fieldName + " doesnt element Exist " + e);
			printLogs(fieldName + " doesnt element Exist");
			test.log(LogStatus.PASS, fieldName + " doesnt element Exist");
			return true;
		}
	}

	public void selectCalendarDate(String filedname, String date) {
		try {
			String[] dates = date.split("-");
			String day = String.valueOf(Integer.valueOf(dates[0].trim()));
			String month = dates[1].trim();
			String year = dates[2].trim();

			String month1 = mapMothNumberToAlphabet(month);

			String yearVal = driver
					.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
					.getText().trim();

			By nextOrPreviousButton;
			if (Integer.valueOf(yearVal) > Integer.valueOf(year))
				nextOrPreviousButton = By.xpath("//a[contains(@class,'ui-datepicker-prev')]");
			else
				nextOrPreviousButton = By.xpath("//a[contains(@class,'ui-datepicker-next')]");

			while (!yearVal.equalsIgnoreCase(year)) {
				driver.findElement(nextOrPreviousButton).click();
				yearVal = driver
						.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
						.getText().trim();
				if (yearVal.equalsIgnoreCase(year))
					break;
			}

			String getmonthVal = driver
					.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-month']"))
					.getText().trim();

			while (!getmonthVal.equalsIgnoreCase(month1)) {
				driver.findElement(nextOrPreviousButton).click();
				getmonthVal = driver
						.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-month']"))
						.getText().trim();
			}

			WebElement ele = driver.findElement(By.linkText(day));
			Actions action = new Actions(driver);
			action.moveToElement(ele).click().perform();
			test.log(LogStatus.PASS, "Date should be selected", "Date selected sucessfully as " + date);
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
		}
	}

	public void moveAndClick(String fieldName) {
		try {

			WebElement elem = driver.findElement(putility.getObject(fieldName));
			Actions actions = new Actions(driver);
			actions.moveToElement(elem).click().perform();
			printLogs(fieldName + " element is clicked");
			test.log(LogStatus.PASS, fieldName + " element is clicked");

		} catch (NoSuchElementException e) {
			LOGGER.error("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			test.log(LogStatus.FAIL, fieldName + " element is not clicked");

		}
	}

	public void calMonth(String monthname) {
		String monthName = "//div[contains(@class,'xdsoft_month') and contains(@style,'block')]//div[text()='"
				+ monthname + "']";
		driver.findElement(By.xpath(monthName)).click();
	}

	public void calYear(String year) {
		String strYear = "//div[contains(@class,'xdsoft_year')]/div[contains(@class,'dsoft_yearselect') and contains(@style,'block')]//div[text()='"
				+ year + "']";
		driver.findElement(By.xpath(strYear)).click();
	}

	public void calDate(String date) {
		String dateNum = "//div[contains(@class,'xdsoft_datetimepicker') and contains(@style,'block')]//td[@data-date='"
				+ date + "' and not(contains(@class, 'other_month'))]";
		driver.findElement(By.xpath(dateNum)).click();
	}

	public void selectMultipleVaFromDropDown(String fieldname, String dropDownValues) {
		if (dropDownValues.trim().length() > 0) {
			String[] data = dropDownValues.split(";");
			for (String val : data)
				SelectDropDownByVisibleText(fieldname, val);
		}
	}

	public String returnText(String path) {
		String strtext = null;
		try {
			WebElement strCurrentPrice = driver.findElement(By.xpath(path));
			strtext = strCurrentPrice.getText().trim();
			return strtext;
		} catch (Exception e) {
			LOGGER.error("Exception:  " + e);
		}
		return strtext;
	}

	public boolean clickElement(String fieldname, String replaceKeys, String replaceValues) {

		By locator = putility.getObject(fieldname, replaceKeys, replaceValues);
		printConsole("locator  " + locator);
		try {
			WebElement clkObject = driver.findElement(locator);
			clkObject.click();
			printLogs(fieldname + " - having xpath '" + locator + "' clicked sucessfully");
			test.log(LogStatus.PASS, "Element should be clicked sucessfully", fieldname + " - clicked sucessfully");
			return true;
		} catch (Exception e) {
			getScreenShot("'" + fieldname + "'  could not be clicked");
			LOGGER.error(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			test.log(LogStatus.WARNING, "Element should be clicked sucessfully",
					fieldname + " -  could not be clicked because -" + e);
			printLogs(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			return false;
		}
	}

	public boolean existElement(String filedname, String replaceKeys, String replaceValues) {
		boolean val = false;
		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);
			printConsole("locator is " + locator);
			if (driver.findElement(locator).isDisplayed()) {
				printLogs("Element exist");
				val = true;
			}
		} catch (Exception e) {
			getScreenShot("Element does not exist " + filedname);
			LOGGER.error("Element does not exist ..." + e);
			printLogs("Element does not exist ..." + e);
			val = false;
		}
		return val;
	}

	public boolean existElement(String filedname, String replaceKeys, String replaceValues, long timeOutMilisec) {
		driver.manage().timeouts().implicitlyWait(timeOutMilisec, TimeUnit.MILLISECONDS);
		boolean val = false;
		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);
			printConsole("locator is " + locator);
			if (driver.findElement(locator).isDisplayed()) {
				printLogs("Element exist");
				val = true;
			}
		} catch (Exception e) {
			LOGGER.error("Element does not exist ..." + e);
			printLogs("Element does not exist ..." + e);
			val = false;
		}
		
		driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
		return val;
	}
	public boolean click(String path) {
		boolean val = false;
		WebElement clkObject = driver.findElement(By.xpath(path));
		try {
			clkObject.click();
			printLogs(path + " -  clicked sucessfully");
			test.log(LogStatus.PASS, "Element should be clicked sucessfully", path + " - clicked sucessfully");
			val = true;
		} catch (Exception e) {
			LOGGER.error(path + " - Element click failed  - " + e);
			getScreenShot("'" + path + "'  could not be clicked");
			LOGGER.info(path + " - Element click failed  - " + e);
			test.log(LogStatus.WARNING, "Element ", path + " - could not be clicked because -" + e);
			printLogs(path + " - Element click failed  - " + e);
			val = false;
		}
		return val;
	}

	public void checkUserAccess(String locator, String strAccessvalue, String subTabName) {
		if ("W".equals(strAccessvalue)) {
			if (presenceOfElement(locator)) {
				test.log(LogStatus.PASS, "EXPECTECD:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTECD:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having read only access to "
								+ subTabName + "  screen</span>");
			}
		} else if ("R".equals(strAccessvalue)) {
			if (presenceOfElement(locator)) {
				test.log(LogStatus.FAIL, "EXPECTECD:User should have read only access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
			} else {
				test.log(LogStatus.PASS, "EXPECTECD:User should have read only access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having Read only access to "
								+ subTabName + "  screen</span>");
			}
		}
	}

	public boolean presenceOfElement(String fieldName) {
		boolean val = false;
		try {
			By locBY = putility.getObject(fieldName);
			driver.findElement(locBY);
			printLogs(fieldName + " element Exist");
			val = true;
		} catch (NoSuchElementException e) {
			LOGGER.error("fieldName element does not Exist" + e);
			val = false;
		}
		return val;
	}

	public int checkNumberOfFieldsInDropDown(String filedname, int fieldVal) {
		int value = 0;
		WebElement supplierName = null;
		try {
			if (fieldVal > 0) {
				supplierName = driver.findElement(putility.getObject(filedname));
				Select oSelect = new Select(supplierName);
				List<WebElement> optionElemens = oSelect.getOptions();
				value = optionElemens.size();
				if (fieldVal == value) {
					test.log(LogStatus.PASS,
							"EXPECTECD: " + filedname + " field  should have total : " + fieldVal + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + value + " values</span>");
				} else {
					test.log(LogStatus.FAIL,
							"EXPECTECD: " + filedname + " field  should have total : " + fieldVal + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + value + " values</span>");
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception :  " + e);
		}
		return value;

	}

	public void validateFieldsInDropDown(String filedname, String fieldVal) {

		List<WebElement> optionElemens;
		WebElement supplierName = null;

		try {
			if (fieldVal != null && fieldVal.trim().length() > 0) {
				String[] arr = fieldVal.split(";");
				supplierName = driver.findElement(putility.getObject(filedname));
				Select oSelect = new Select(supplierName);
				optionElemens = oSelect.getOptions();
				for (int i = 0; i < arr.length; i++) {
					boolean isfound = false;
					for (WebElement opele : optionElemens) {
						if (opele.getText().equalsIgnoreCase(arr[i])) {
							isfound = true;
							break;
						}
					}
					if (isfound) {
						test.log(LogStatus.PASS,
								"EXPECTECD: " + filedname + " field  should have : '" + arr[i] + "' value in dropdown",
								"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
										+ " field  has the value : '" + arr[i] + "' in the dropdown</span>");
					} else {
						test.log(LogStatus.FAIL,
								"EXPECTECD: " + filedname + " field  should have : '" + arr[i] + "' value in dropdown",
								"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
										+ " field does not have the value : '" + arr[i] + "' in the dropdown</span>");
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception :  " + e);
		}

	}

	public void checkScreenExistence(String locator, String strAccessvalue, String subTabName) {
		if ("Y".equals(strAccessvalue)) {
			if (presenceOfElement(locator)) {
				test.log(LogStatus.PASS, "EXPECTECD:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTECD:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having read only access to "
								+ subTabName + "  screen</span>");
			}
		}
	}

	public void sendKeys(String filedname, String value, boolean clearAndSend, String replaceKeys,
			String replaceValues) {

		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			WebElement ele = driver.findElement(locator);
			if (clearAndSend)
				ele.clear();
			ele.sendKeys(value);

			printLogs(value + " has been sent/entered to elemet " + filedname);

		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			printLogs("failed to sent/entered in text in element " + filedname + " .... " + e);
		}
	}

	
	public void clearTextBox(String filedname, String replaceKeys, String replaceValues){
		try {
			By locator = putility.getObject(filedname, replaceKeys, replaceValues);

			WebElement ele = driver.findElement(locator);			
			ele.clear();
			

			printLogs(filedname + " has been cleared");

		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			printLogs("failed to clear in text in element " + filedname + " .... " + e);
		}
	}
	
	
	public void checkEditableBox(String fieldName, String replaceKeys, String replaceValues) {
		try {
			By locator = putility.getObject(fieldName, replaceKeys, replaceValues);

			WebElement textField = driver.findElement(locator);

			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			}

		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			getScreenShot("Validating field" + fieldName);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkReadonlyProperty(String fieldName, String replaceKeys, String replaceValues) {
		try {

			By locator = putility.getObject(fieldName, replaceKeys, replaceValues);
			WebElement textField = driver.findElement(locator);
			String editFieldval = textField.getAttribute("readonly");
			if (textField.isDisplayed()
					&& ("true".equalsIgnoreCase(editFieldval) || "readonly".equalsIgnoreCase(editFieldval))) {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.PASS, "EXPECTECD: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTECD: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			}

		} catch (Exception e) {
			LOGGER.error("Exception :" + e);
			getScreenShot("Validating field" + fieldName);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void selectPreviousCalendarDate(String filedname, String date) {
		try {
			String[] dates = date.split("-");
			String day = dates[0].trim();
			String month = dates[1].trim();
			String year = dates[2].trim();

			String yearVal = driver
					.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
					.getText().trim();
			while (!yearVal.equalsIgnoreCase(year)) {
				driver.findElement(putility.getObject("clickPrevBtn")).click();
				yearVal = driver
						.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
						.getText().trim();
				if (yearVal.equalsIgnoreCase(year))
					break;
			}

			String getmonthVal = driver.findElement(By.xpath("//div[@class='ui-datepicker-title']/span")).getText()
					.trim();
			String month1 = mapMothNumberToAlphabet(month);
			while (!getmonthVal.equalsIgnoreCase(month1)) {
				driver.findElement(putility.getObject("clickPrevBtn")).click();
				getmonthVal = driver.findElement(By.xpath("//div[@class='ui-datepicker-title']/span")).getText().trim();
			}

			WebElement ele = driver.findElement(By.linkText(day));
			Actions action = new Actions(driver);
			action.moveToElement(ele).click().perform();
		} catch (Exception e) {
			LOGGER.error("exceptin : " + e);
		}
	}

	public void checkFiveLatestValFromDropDownForPriceMgmt(String filedname, List<String> dropDwonValues) {
		int historyValueCount = dropDwonValues.size();
		for (int i = 1; i < historyValueCount; i++) {
			String history = dropDwonValues.get(i);
			SelectDropDownByVisibleText("PriceHistoryLst", history);
			clickElement("DisplayBtn");
			clickElement("SubmitBtn");
			if (i == 1) {
				printConsole("Inside the  loop");
				clickElement("application_PopUpNoBtn");
			}
			if (verifyHistoryTabFields("application_PopUpTitle")) {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTECD: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTECD: " + i + "nd/th Value from List should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is editable</span>");
				}
			}
		}
	}

	public void selectCalendarDate(String filedname, String replaceKeys, String replaceValues, String date) {
		try {
			

			String[] dates = date.split("-");
			String day = dates[0].trim();
			String month = dates[1].trim();
			String year = dates[2].trim();

			String month1 = mapMothNumberToAlphabet(month);

			String yearVal = driver
					.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
					.getText().trim();

			By nextOrPreviousButton;
			if (Integer.valueOf(yearVal) > Integer.valueOf(year))
				nextOrPreviousButton = putility.getObject("clickPrevBtn");
			else
				nextOrPreviousButton = putility.getObject("clickNextBtn");

			while (!yearVal.equalsIgnoreCase(year)) {
				driver.findElement(nextOrPreviousButton).click();
				yearVal = driver
						.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-year']"))
						.getText().trim();
				if (yearVal.equalsIgnoreCase(year))
					break;
			}

			String getmonthVal = driver
					.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-month']"))
					.getText().trim();

			while (!getmonthVal.equalsIgnoreCase(month1)) {
				driver.findElement(nextOrPreviousButton).click();
				getmonthVal = driver
						.findElement(By.xpath("//div[@class='ui-datepicker-title']/span[@class='ui-datepicker-month']"))
						.getText().trim();
			}

			WebElement ele = driver.findElement(By.linkText(day));
			Actions action = new Actions(driver);
			action.moveToElement(ele).click().perform();
		} catch (Exception e) {
			printConsole(e.getMessage());
			LOGGER.error(e);
		}
	}

	public static void printConsole(String msg) {
		System.out.println(msg);
	}
	
	public String getDropDownSelectedVal(String fieldName,String val){
		WebElement dropDownField = null;
		String defaultSelVal = null;
		if(val.trim().length()>0 && val!= null){
			try{
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);
				defaultSelVal = oSelect.getFirstSelectedOption().getText();
				LOGGER.info("defaultSelVal is " + defaultSelVal);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return defaultSelVal;	
	}
	
	public String getTxtBoxValue(String fieldName,String val){
		WebElement textField = null;
		String strText = null;
		if(val.trim().length()>0 && val!= null){
			try{
				textField = driver.findElement(putility.getObject(fieldName));
				strText = textField.getAttribute("value").trim();
				LOGGER.info("Txt Box value is " + strText);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return strText;	
	}
	
	public String getChkBoxStatus(String fieldName,String val){
		WebElement chkBoxField = null;
		String strText = null;
		if(val.trim().length()>0 && val!= null){
			try{
				chkBoxField = driver.findElement(putility.getObject(fieldName));
				if(chkBoxField.isSelected())
					strText="Y";
					else
						strText="N";
				LOGGER.info("Check box status is " + strText);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return strText;	
	}
	
	public String getDropDownMultiSelectedVal(String fieldName,String val){
		WebElement dropDownField = null;
		String allSelectedOptions = "";
		if(val.trim().length()>0 && val!= null){
			try{
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);
				
				int i =0;
				for(WebElement ele :oSelect.getAllSelectedOptions())	
				{
					String eletext = ele.getText();
					if(i==0)					
						allSelectedOptions = eletext+",";					
					else
						allSelectedOptions = allSelectedOptions+","+eletext;
					
					i++;
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return allSelectedOptions;	
	}
	
	public String getDropDownAllVal(String fieldName,String val){
		WebElement dropDownField = null;
		String allSelectedOptions = "";
		if(val.trim().length()>0 && val!= null){
			try{
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);
				
				int i =0;
				for(WebElement ele :oSelect.getOptions())	
				{
					String eletext = ele.getText();
					if(i==0)					
						allSelectedOptions = eletext;						
					else
					{
						if(i==1)
							allSelectedOptions = allSelectedOptions+",";
						
						allSelectedOptions = allSelectedOptions+","+eletext;
					}
						
					
					i++;
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return allSelectedOptions;	
	}
	
	
	public Set<String> getDropDownAllVal23(String fieldName,String val){
		WebElement dropDownField = null;
		Set<String> allSelectedOptions = new HashSet<>();
		if(val.trim().length()>0 && val!= null){
			try{
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);
				
				int i =0;
				for(WebElement ele :oSelect.getOptions())	
				{
					String eletext = ele.getText();
					allSelectedOptions.add(eletext);
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			}catch(Exception e){
				LOGGER.error("Exception in getting value from drop down:  " + e);
				
			}
		}
		return allSelectedOptions;	
	}


	
}
