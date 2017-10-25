package com.tata.selenium.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.constants.ApplicationConstants;
import com.tata.selenium.constants.MyConstants;

import bsh.Capabilities;

/**
 * @date
 * @author
 * @description Utility class containing all the reusable methods
 */

public class CommonUtils implements ApplicationConstants {
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
	CSVUtil csvUtil;

	public enum identifiers {
		ID, NAME, XPATH, CSS, LINKTEXT
	}

	public static String getTimeStamp() {
		return timeStamp;
	}

	public static void setTimeStamp(String timeStamp) {
		CommonUtils.timeStamp = timeStamp;
	}

	public CommonUtils(WebDriver driver, ExtentTest test, String sheetName, String uniqueDataId, String testCaseId,
			String objetResPath) {
		this.driver = driver;
		this.test = test;
		this.sheetName = sheetName;
		this.uniqueDataId = uniqueDataId;
		this.testCaseId = testCaseId;
		this.putility = new PropertyUtility(objetResPath);
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
	public WebDriver LaunchUrl(String strUrl1) {
		try {
			Log.info("Launching url");
			LOGGER.info("Launching url :: " + MyConstants.getBrowser());
			// Create a new instance of the Firefox driver
			
			switch(MyConstants.getBrowser().trim().toUpperCase())
			{
				case "FIREFOX":		
						LOGGER.info("IN FIREFOX LOOP");
						if(RUN_IN_REMOTE)
						{
							driver = new RemoteWebDriver(new URL("http://"+REMOTE_HOST_IP+":"+GRID_HUB_PORT+"/wd/hub/"), DesiredCapabilities.firefox());
							printLogs("New FIREFOX REMOTE driver instantiated");
						}
						else
						{
							driver = new FirefoxDriver();		
							printLogs("New FIREFOX LOCAL driver instantiated");
						}
					break;
					
				case "IE":
						LOGGER.info("IN IE LOOP");
						if(RUN_IN_REMOTE)
						{
							driver = new RemoteWebDriver(new URL("http://"+REMOTE_HOST_IP+":"+GRID_HUB_PORT+"/wd/hub/"), DesiredCapabilities.internetExplorer());
							printLogs("New IE REMOTE driver instantiated");
						}
						else
						{
							System.setProperty("webdriver.ie.driver", DRIVER_PATH + "/IEDriverServer.exe");
							driver = new InternetExplorerDriver();
							printLogs("New IE LOCAL driver instantiated");
						}
					break;
					
				case "CHROME":
					LOGGER.info("IN CHROME LOOP");
					DesiredCapabilities capabilities = DesiredCapabilities.chrome();
					ChromeOptions options = new ChromeOptions();
					Map<String, Object> prefs = new HashMap<>();
					if(RUN_IN_REMOTE)
						RestAssured.given().body("{\"directory\":\""+downloadpathstr+"\"}")
						.post("http://"+REMOTE_HOST_IP+":"+DESKTOP_WIN_CONTROL_PORT+"/files/createDirectory").body();
					else
						new File(downloadpathstr).mkdirs();
					prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", 1);
					prefs.put("download.prompt_for_download", false);
					prefs.put("profile.default_content_settings.popups", 0);
					prefs.put("download.default_directory", downloadpathstr);
					options.setExperimentalOption("prefs", prefs);
					capabilities.setCapability(ChromeOptions.CAPABILITY, options);
					
					if(RUN_IN_REMOTE)
					{
						driver = new RemoteWebDriver(new URL("http://"+REMOTE_HOST_IP+":"+GRID_HUB_PORT+"/wd/hub/"), capabilities);
						printLogs("New CHROME REMOTE driver instantiated");
					}
					else
					{
						System.setProperty("webdriver.chrome.driver", DRIVER_PATH + "/chromedriver.exe");
						driver = new ChromeDriver(capabilities);
						printLogs("New CHROME LOCAL driver instantiated");
					}
					
					break;
			}
			
			// Deleting all browser cookies
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			// Command to launch URL
			driver.get(APP_URL);
			printLogs("URL launched is : " + APP_URL);
			driver.manage().window().maximize();
			test.log(LogStatus.PASS, "URL should be launched",
					"TATA Messaging Exchange App  '" + APP_URL + "' Launced sucessfully");
		} catch (Exception e) {
			LOGGER.info(" Url launch failed : " + e);
			printLogs(" Url launch failed : " + e);
			getScreenShot("Url_Launch_Failed");
			test.log(LogStatus.FAIL, "URL:  " + APP_URL + " could not be launched - " + e);
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
			LOGGER.info("Error occured on waiting for the element to appear  - " + e);
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
	
	public void waitForPageLoadWithSleep(String pageName, long initialSleepmilisec) {
		sleep(initialSleepmilisec);
		waitForPageLoad(pageName);
	}

	public void checkRunStatus() {
		LogStatus value = test.getRunStatus();
		if ("pass".equalsIgnoreCase(value.toString())) {
			LOGGER.info("LogStatus " + value);
			excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
		} else {
			LOGGER.info("LogStatus " + value);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			Assert.fail("Test case is having some failed/warning steps. Please check in the report");
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
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable and default value selected is '" + value + "</span>");
			} else {
				printLogs(fieldName + " field is not editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is non editable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	
	public void checkEditableDropDown(String fieldName, String value, String replaceKeys, String replaceValues) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			Select oSelect = new Select(dropDownField);
			String defaultSelVal = oSelect.getFirstSelectedOption().getText();
			LOGGER.info("defaultSelVal is " + defaultSelVal);
			boolean val = dropDownField.isEnabled();
			if (val && defaultSelVal.trim().equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.PASS,
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable and default value selected is '" + value + "</span>");
			} else {
				printLogs(fieldName + " field is not editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is non editable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	public void checkEditableDropDownButton(String fieldName, String value) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			String defaultSelVal = dropDownField.getText();
			LOGGER.info("defaultSelVal is " + defaultSelVal);
			boolean val = dropDownField.isEnabled();
			if (val && defaultSelVal.trim().equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is clickable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.PASS,
						"EXPECTED: Drop down Button " + fieldName + " should be clickable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down Button" + fieldName
								+ " is clickable and default value selected is '" + value + "</span>");
			} else {
				printLogs(fieldName + " field is not clickable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTED: Drop down Button " + fieldName + " should be clickable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down Button " + fieldName
								+ " is non clickable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info(fieldName + " -Dropdown Button validation failed..." + e);
			printLogs(fieldName + " -Dropdown Button validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down Button validation",
					"Drop down Button validation failed failed because  -" + e);
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
				test.log(LogStatus.FAIL, "EXPECTED: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non-editable");
				test.log(LogStatus.PASS, "EXPECTED: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is Non-editable</span>");
			}
		} catch (Exception e) {
			getScreenShot("Checking dropdown status " + fieldName);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	
	public void checkEditableDropDown(String fieldName) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			boolean val = dropDownField.isEnabled();
			if (val) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Drop down " + fieldName + " should be editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non-editable");
				test.log(LogStatus.FAIL, "EXPECTED: Drop down " + fieldName + " should be editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is Non-editable</span>");
			}
		} catch (Exception e) {
			getScreenShot("Checking dropdown status " + fieldName);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	
	public void checkNonEditableDropDown(String fieldName, String replaceKeys, String replaceValues) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			boolean val = dropDownField.isEnabled();
			if (val) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non-editable");
				test.log(LogStatus.PASS, "EXPECTED: Drop down " + fieldName + " should be Non-editable",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is Non-editable</span>");
			}
		} catch (Exception e) {
			getScreenShot("Checking dropdown status " + fieldName);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
			printLogs(fieldName + " -Dropdown validation failed..." + e);
			test.log(LogStatus.FAIL, "Drop down validation", "Drop down validation failed failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	
	public void checkNonEditableDropDown(String fieldName, String value) {
		try {
			WebElement dropDownField = driver.findElement(putility.getObject(fieldName));
			String selectedVal =  new Select(dropDownField).getFirstSelectedOption().getText();
			
			if (dropDownField.isEnabled() && !selectedVal.equalsIgnoreCase(value)) {
				printLogs(fieldName + " field is editable and selected option is " + selectedVal);
				test.log(LogStatus.FAIL,
						"EXPECTED: Dropdown field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Dropdown field " + fieldName
								+ " is editable and default value present is -'" + value + "</span>");
			} else {
				printLogs(fieldName + " field non editable and selected option is " + selectedVal);
				test.log(LogStatus.PASS,
						"EXPECTED: Dropdown field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Dropdown field " + fieldName
								+ " is not editable and default value present is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Checking dropdown status " + fieldName);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
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
						"EXPECTED: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable and default value present is -'" + value + "</span>");
			} else {
				printLogs(fieldName + " field is non editable and the value present is " + editFieldval);
				test.log(LogStatus.PASS,
						"EXPECTED: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is not editable and default value present is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkNonEditableBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			String readOnly;
			if (textField.getAttribute("readonly") == null)
				readOnly = "";
			else
				readOnly = textField.getAttribute("readonly");

			if (textField.isEnabled() && !"true".equals(readOnly)) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should not be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be Non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is Non editable</span>");

			}
		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed..." + e);
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
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");

			}
			if (strText.equalsIgnoreCase(value.trim())) {
				printLogs(fieldName + " field has default value as -" + strText);
				test.log(LogStatus.PASS,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is has default value as " + strText + "</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " has default value as - " + strText + "</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	
	public void checkEditableBox(String fieldName, String value, String replaceKeys, String replaceValues) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			String strText = textField.getAttribute("value").trim();
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");

			}
			if (strText.equalsIgnoreCase(value.trim())) {
				printLogs(fieldName + " field has default value as -" + strText);
				test.log(LogStatus.PASS,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is has default value as " + strText + "</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " has default value as - " + strText + "</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	public boolean waitForElementInvisiblity(String fieldName, int timeout) {
		boolean val = false;
		try {
			By objPath = putility.getObject(fieldName);
			WebDriverWait wait = new WebDriverWait(driver, timeout);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(objPath));
			val = true;
		} catch (Exception e) {
			LOGGER.info("Error occured on waiting for the invisiblity of element  - " + e);
			val = false;
			Assert.fail("Error occured on waiting for the invisiblity of element  - " + e);
		}
		return val;
	}

	public void checkElementPresence(String fieldName) {
		try {
			WebElement uiElement = driver.findElement(putility.getObject(fieldName));
			if (uiElement.isDisplayed()) {
				printLogs(fieldName + " Element is present and displayed in the page");
				test.log(LogStatus.PASS, "EXPECTED: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is displayed</span>");
			} else {
				printLogs(fieldName + " Element is not present and not displayed in the page");
				test.log(LogStatus.FAIL, "EXPECTED: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is not displayed</span>");
			}
		} catch (Exception e) {
			getScreenShot("UI Element  " + fieldName);
			LOGGER.info(fieldName + " -UI Element validation failed..." + e);
			printLogs(fieldName + " -UI Element validation failed..." + e);
			test.log(LogStatus.FAIL, "UI Element validation", "UI Element validation failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkElementPresent(String fieldName) {
		try {
			WebElement uiElement = driver.findElement(By.xpath(fieldName));
			if (uiElement.isDisplayed()) {
				printLogs(fieldName + " Element is present and displayed in the page");
				test.log(LogStatus.PASS, "EXPECTED: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is displayed</span>");
			} else {
				printLogs(fieldName + " Element is not present and not displayed in the page");
				test.log(LogStatus.FAIL, "EXPECTED: Element " + fieldName + " should be displayed",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Element " + fieldName
								+ " is not displayed</span>");
			}
		} catch (Exception e) {
			getScreenShot("UI Element  " + fieldName);
			LOGGER.info(fieldName + " -UI Element validation failed..." + e);
			printLogs(fieldName + " -UI Element validation failed..." + e);
			test.log(LogStatus.FAIL, "UI Element validation", "UI Element validation failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public boolean elementDisplayed(String fieldName) {
		try {
			WebElement uiElement = driver.findElement(putility.getObject(fieldName));
			if (uiElement.isDisplayed())
				return true;
			else
				return false;

		} catch (Exception e) {
			getScreenShot("UI Element  " + fieldName);
			LOGGER.info(fieldName + " -UI Element validation failed..." + e);
			printLogs(fieldName + " -UI Element validation failed..." + e);
			test.log(LogStatus.FAIL, "UI Element validation", "UI Element validation failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}

	}

	public boolean elementDisplayed(String fieldName, String replaceKeys, String replaceValues) {
		try {

			WebElement uiElement = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			if (uiElement.isDisplayed())
				return true;
			else
				return false;

		} catch (Exception e) {
			getScreenShot("UI Element  " + fieldName);
			LOGGER.info(fieldName + " -UI Element validation failed..." + e);
			printLogs(fieldName + " -UI Element validation failed..." + e);
			test.log(LogStatus.FAIL, "UI Element validation", "UI Element validation failed because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}

	}

	public boolean elementDisplayed(String fieldName, long timeoutsec) {
		try {
			driver.manage().timeouts().implicitlyWait(timeoutsec, TimeUnit.SECONDS);
			WebDriverWait wait = new WebDriverWait(driver, timeoutsec);
			wait.until(ExpectedConditions.visibilityOfElementLocated(putility.getObject(fieldName)));
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			return true;
		} catch (Exception e) {
			LOGGER.info(fieldName + " - UI element displyed checked ..." + e);
			driver.manage().timeouts().implicitlyWait(implicitWait, TimeUnit.SECONDS);
			return false;
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
	public void setData(String fieldName, String setValue) {
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
			LOGGER.info("Error occured while setting data  " + setValue + "  - " + e);
			printLogs("Error occured while setting data  " + setValue + "  - " + e);
			test.log(LogStatus.FAIL, "Enter value in " + fieldName,
					"Value - '" + setValue + "' could not be entered - " + e);
		}
	}
	
	public void setData(String fieldName, String setValue, String replaceKeys, String replaceValues) {
		try {
			WebElement objPath = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			if (setValue != null && setValue.trim().length() > 0) {
				objPath.clear();
				objPath.sendKeys(setValue);
				printLogs("Data  '" + setValue + "' entered sucessfully in the " + fieldName + " box");
				test.log(LogStatus.PASS, "Enter value in the field",
						"Value - '" + setValue + "' entered sucessfully in " + fieldName + "field");
			}
		} catch (Exception e) {
			LOGGER.info("Error occured while setting data  " + setValue + "  - " + e);
			printLogs("Error occured while setting data  " + setValue + "  - " + e);
			test.log(LogStatus.FAIL, "Enter value in " + fieldName,
					"Value - '" + setValue + "' could not be entered - " + e);
		}
	}
	
	public void clearData(String fieldName) {
		try {
			WebElement objPath = driver.findElement(putility.getObject(fieldName));
				objPath.clear();				
				LOGGER.info(fieldName + " box has been cleared");
				printLogs(fieldName + " box has been cleared");
				test.log(LogStatus.PASS, "Clear a text field",
						fieldName + "' text field cleared sucessfully");
			
		} catch (Exception e) {
			LOGGER.info("Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
			printLogs("Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
			test.log(LogStatus.FAIL, "Clear a text field",
					"Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
		}
	}
	
	public void clearData(String fieldName, String replaceKeys, String replaceValues) {
		try {
			WebElement objPath = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
				objPath.clear();				
				LOGGER.info(fieldName + " box has been cleared");
				printLogs(fieldName + " box has been cleared");				
				test.log(LogStatus.PASS, "Clear a text field",
						fieldName + "' text field cleared sucessfully");
			
		} catch (Exception e) {
			LOGGER.info("Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
			printLogs("Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
			getScreenShot("Error occured while clearing text field  - "+fieldName);
			test.log(LogStatus.FAIL, "Clear a text field",
					"Error occured while clearing text field  - "+fieldName+"    Exception - " + e);
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
			LOGGER.info("Error occured while setting data  " + setValue + "  - " + e);
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
			LOGGER.info(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			return false;
		}
	}

	public boolean checkElementNotclicked(String fieldname) {

		By locator = putility.getObject(fieldname);
		try {
			WebElement clkObject = driver.findElement(locator);
			clkObject.click();
			printLogs(fieldname + " - having xpath '" + locator + "'not clickable ");
			test.log(LogStatus.PASS, "Element not clickable ", fieldname + " - not clickable ");
			return true;
		} catch (Exception e) {
			getScreenShot("'" + fieldname + "' clicked");
			test.log(LogStatus.WARNING, "Element should not be clicked", fieldname + " -   clicked because -" + e);
			printLogs(fieldname + " - having xpath '" + locator + "'  click passed - " + e);
			LOGGER.info(fieldname + " - having xpath '" + locator + "'  click passed  - " + e);
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
	public boolean SelectDropDownByVisibleText(String fieldname, String value) {
		try {
			if (value != null && value.trim().length() > 0) {
				WebElement supplierName = driver.findElement(putility.getObject(fieldname));

				Select oSelect = new Select(supplierName);
				oSelect.selectByVisibleText(value);
				printLogs("Selected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be selected",
						"Drop down value -'" + value + "'- selected from field "+fieldname+" sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info("Dropdown selection failed..." + e);
			printLogs("Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, "Drop down should be selected",
					"Drop down value -'" + value + "'- could not be selected from filedname "+fieldname+" because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}

	
	public boolean SelectDropDownByVisibleText(String fieldname, String value, String replaceKeys, String replaceValues) {
		try {
			if (value != null && value.trim().length() > 0) {
				WebElement supplierName = driver.findElement(putility.getObject(fieldname, replaceKeys, replaceValues));

				Select oSelect = new Select(supplierName);
				oSelect.selectByVisibleText(value);
				printLogs("Selected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be selected",
						"Drop down value -'" + value + "'- selected from field "+fieldname+" sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info("Dropdown selection failed..." + e);
			printLogs("Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, "Drop down should be selected",
					"Drop down value -'" + value + "'- could not be selected from filedname "+fieldname+" because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}

	public boolean SelectDropDownByVisibleTextCustomMMX(String dropDownButton, String dropDownSearchTextbox,
			String dynamicLabelOption, String replaceKey, String replaceValue) {
		try {
			if (replaceValue != null && replaceValue.trim().length() > 0) {

				WebElement dropDownButtonEle = driver.findElement(putility.getObject(dropDownButton));
				dropDownButtonEle.click();
				sleep(700);
				WebElement dropDownSearchTextboxEle = driver.findElement(putility.getObject(dropDownSearchTextbox));
				dropDownSearchTextboxEle.sendKeys(replaceValue);

				sleep(500);

				String finalStrObj = putility.getProperty(dynamicLabelOption).replace(replaceKey, replaceValue);
				By locator = putility.getObjectFromStr(finalStrObj);
				WebElement dynamicLabelOptionEle = driver.findElement(locator);
				dynamicLabelOptionEle.click();

				printLogs("Selected - '" + replaceValue + "' from the custom dropdown " + dropDownButton);
				test.log(LogStatus.PASS, dropDownButton + "Custom Drop down should be selected",
						dropDownButton + " Custom Drop down value -'" + replaceValue + "'- selected sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + replaceValue);
			LOGGER.info(dropDownButton + " Custom Dropdown selection failed..." + e);
			printLogs(dropDownButton + " Custom Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, dropDownButton + " Custom Drop down should be selected",
					dropDownButton + " Custom Drop down value -'" + replaceValue + "'- could not be selected because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}
	public boolean deselectDropDownByVisibleText(String filedname, String value) {
		try {
			if (value != null && value.trim().length() > 0) {
				WebElement supplierName = driver.findElement(putility.getObject(filedname));

				Select oSelect = new Select(supplierName);
				oSelect.deselectByVisibleText(value);
				printLogs("DeSelected - '" + value + "' from the dropdown");
				test.log(LogStatus.PASS, "Drop down should be DeSelected",
						"Drop down value -'" + value + "'- DeSelected from field "+filedname+" sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info("Dropdown DeSelected failed..." + e);
			printLogs("Dropdown DeSelected failed..." + e);
			test.log(LogStatus.FAIL, "Drop down should be DeSelected",
					"Drop down value -'" + value + "'- could not be DeSelected from filedname "+filedname+" because -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
			return false;
		}
	}
	public boolean SelectDropDownByVisibleTextCustomMMX(String dropDownButton, String dropDownSearchTextbox,
			String dynamicLabelOption, String value) {
		try {
			if (value != null && value.trim().length() > 0) {

				WebElement dropDownButtonEle = driver.findElement(putility.getObject(dropDownButton));
				dropDownButtonEle.click();
				sleep(700);
				WebElement dropDownSearchTextboxEle = driver.findElement(putility.getObject(dropDownSearchTextbox));
				dropDownSearchTextboxEle.sendKeys(value);

				sleep(500);

				String finalStrObj = putility.getProperty(dynamicLabelOption).replace("$option$", value);
				By locator = putility.getObjectFromStr(finalStrObj);
				WebElement dynamicLabelOptionEle = driver.findElement(locator);
				dynamicLabelOptionEle.click();

				printLogs("Selected - '" + value + "' from the custom dropdown " + dropDownButton);
				test.log(LogStatus.PASS, dropDownButton + "Custom Drop down should be selected",
						dropDownButton + " Custom Drop down value -'" + value + "'- selected sucessfully");
			}
			return true;
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info(dropDownButton + " Custom Dropdown selection failed..." + e);
			printLogs(dropDownButton + " Custom Dropdown selection failed..." + e);
			test.log(LogStatus.FAIL, dropDownButton + " Custom Drop down should be selected",
					dropDownButton + " Custom Drop down value -'" + value + "'- could not be selected because -" + e);
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
				LOGGER.info(frameName + " - Frame could not be changed - " + e);
				printLogs(frameName + " - Frame could not be changed - " + e);
			}
		} else {
			try {
				driver.switchTo().frame(frameName);
				printLogs("Successfully switched  to " + frameName + " frame");
			} catch (Exception e) {
				LOGGER.info(frameName + " - Frame could not be changed - " + e);
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
			LOGGER.info("ERROR " + e);
		}
		if (val)
			Assert.fail("Error occured because  of POP Up - '" + popUpName + "' at step - " + testStep);
	}

	public void check_Pop_Up(String popFieldName, String testStep) {
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
				test.log(LogStatus.PASS, "Test failed during  " + testStep,
						"Pop up is being displayed and it is  - " + popUpName);
				driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
				excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Error occured because  Pop up is being displayed and it is  - " + popUpName);
			}
		} catch (Exception e) {
			LOGGER.info("ERROR " + e);
		}
		/*
		 * if (val) Assert.fail("Error occured because  of POP Up - '" +
		 * popUpName + "' at step - " + testStep);
		 */
	}
	
	public void check_Pop_Up1(String popFieldName, String testStep) {
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
				test.log(LogStatus.PASS, "Test failed during  " + testStep,
						"Pop up is being displayed and it is  - " + popUpName);
				driver.findElement(putility.getObject("application_PopUpOk")).click();
				excelUtils.setCellData(sheetName, "PASS", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Error occured because  Pop up is being displayed and it is  - " + popUpName);
			}
		} catch (Exception e) {
			LOGGER.info("ERROR " + e);
		}
		/*
		 * if (val) Assert.fail("Error occured because  of POP Up - '" +
		 * popUpName + "' at step - " + testStep);
		 */
	}

	public void checkMessage(String popFieldName, String testStep, String expectedPopUpMsg) {
		String popUpName = null;
		try {
			WebElement objPath = driver.findElement(putility.getObject(popFieldName));
			if ((objPath).isDisplayed()) {
				printLogs("POP Up has appeared now");
				getScreenShot(testStep);
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
					driver.findElement(putility.getObject("application_PopUpOkBtn")).click();
				}
			} else {
				excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Validation failed as expected Pop up did not get displayed");
				test.log(LogStatus.FAIL, "POP Up is expected at " + testStep,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up did not get displayed</span>");
			}
		} catch (Exception e) {

			getScreenShot("Error occured while checking popup message/ accepting popup. ");
			LOGGER.info("Error occured while checking popup message/ accepting popup.   Exception - " + e);
			printLogs("Error occured while checking popup message/ accepting popup.   Exception - " + e);
			test.log(LogStatus.FAIL, "Popup message should be verified and accecpted",
					"Error occured while checking popup message/ accepting popup.   Exception - " + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");

		}
	}
	
	
	public void checkMessage1(String popFieldName, String testStep, String expectedPopUpMsg) {
		String popUpName = null;
		try {
			WebElement objPath = driver.findElement(putility.getObject(popFieldName));
			if ((objPath).isDisplayed()) {
				printLogs("POP Up has appeared now");
				getScreenShot(testStep);
				WebElement element = driver.findElement(putility.getObject("application_PopUpMessage"));
				popUpName = element.getText().trim();
				if (popUpName.equalsIgnoreCase(expectedPopUpMsg.trim())) {
					test.log(LogStatus.PASS, "POP Up is expected at " + testStep,
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up is being displayed and it is  - "
									+ popUpName + "</span>");
					driver.findElement(putility.getObject("application_PopUpOk")).click();
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
					driver.findElement(putility.getObject("application_PopUpOk")).click();
				}
			} else {
				excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
				excelUtils.setCellData(sheetName, popUpName, uniqueDataId, "Result_Errors");
				printLogs("Validation failed as expected Pop up did not get displayed");
				test.log(LogStatus.FAIL, "POP Up is expected at " + testStep,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Pop up did not get displayed</span>");
			}
		} catch (Exception e) {

			getScreenShot("Error occured while checking popup message/ accepting popup. ");
			LOGGER.info("Error occured while checking popup message/ accepting popup.   Exception - " + e);
			printLogs("Error occured while checking popup message/ accepting popup.   Exception - " + e);
			test.log(LogStatus.FAIL, "Popup message should be verified and accecpted",
					"Error occured while checking popup message/ accepting popup.   Exception - " + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");

		}
	}


	public WebElement returnElement(String fieldName) {
		WebElement objPath = null;
		try {
			objPath = driver.findElement(putility.getObject(fieldName));

		} catch (Exception e) {
			LOGGER.info("Element " + fieldName + " not found because - " + e);
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
			LOGGER.info("Error " + e);
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
			LOGGER.info("element not found  " + e);
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
			sleep(1000);
			driver.switchTo().window(givenWindow);
		} catch (Exception e) {
			LOGGER.info("error  " + e);

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
	
	
	public String getText(By path, String name) {
		
		String ret =null;
		
		try{
		ret = driver.findElement(path).getText();
		}catch(Exception e)
		{
			getScreenShot("Error occured while getting text from element");
			test.log(LogStatus.FAIL, "Error occured while getting text from element "+name+"  - Exception - "+e);
			LOGGER.error("Error occured while getting text from element "+name+"  - Exception - "+e);
			Assert.fail("Error occured while getting text from element "+name+"  - Exception - "+e);
		}
		
		return ret;

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
			LOGGER.info("Exception is - " + e);
		}
	}

	/**
	 * @description Method used to accept the alert
	 */
	public void ConfirmAlert() {
		try {
			driver.switchTo().alert().accept();
		} catch (Exception e) {
			LOGGER.info("Exception occure while looking for alert  - " + e);
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
			LOGGER.info("Exception:  " + e);
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
			LOGGER.info("Exception:  " + e);
			Log.error("Exception :" + e.getMessage());
		}
		return value;

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
			LOGGER.info("Exception:  " + e);
			return null;
		}

	}
	
	public Set<String> getAllWindowNames() {
		try {
			return driver.getWindowHandles();
		} catch (Exception e) {
			LOGGER.info("Exception:  " + e);
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
			LOGGER.info("Exception on Selecting Check Box ::  " + e);
			printLogs("Exception on Selecting Check Box :" + e.getMessage());
			return false;
		}
	}

	/**
	 * @description Method used to switch to new window if it exists
	 */
	public void newWindowHandles(String pwindow) {
		try {
			sleep(5000);

			for (String handle : driver.getWindowHandles()) {
				if (!pwindow.equals(handle)) {
					driver.switchTo().window(handle);
					LOGGER.info("Child Window   " + handle);
				}
			}
		} catch (Exception e) {
			LOGGER.info("New Handle window error - " + e);
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

	public void deleteCookies() {

		driver.manage().deleteAllCookies();
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
			String screenshotB64 = "data:image/png;base64,"+((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);			
			test.log(LogStatus.INFO, "Snapshot for  " + testStep + "  : "+ test.addBase64ScreenShot(screenshotB64));
		} catch (Exception e) {
			System.out.println("Inside catch block");
			LOGGER.info("ERROR IN SCREENSHOT." + e);
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
			LOGGER.info("Error occured on waiting for the element to appear  - " + e);
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
			int j=0;
			for (WebElement opele : optionElemens)
			{
				retStrOPs.add(opele.getText().trim());
				System.out.println(j);
				j++;
				
			}

			printLogs(retStrOPs.size() + " were obtianed from the " + filedname + "dropdown");
			return retStrOPs;
		} catch (Exception e) {
			LOGGER.info("get All Options From DropDown failed..." + e);
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
			LOGGER.info("get selected option From DropDown failed..." + e);
			getScreenShot("selectedOpStr - " + filedname);
			printLogs("get selected option From DropDown failed..." + e);
			return selectedOpStr;
		}
	}
	
	public String getSelectVauleFromDropDown(String filedname, String replaceKeys, String replaceValues) {
		String selectedOpStr = "";
		try {
			WebElement supplierName = driver.findElement(putility.getObject(filedname, replaceKeys, replaceValues));
			Select oSelect = new Select(supplierName);
			selectedOpStr = oSelect.getFirstSelectedOption().getText();
			printLogs(selectedOpStr + " seems to selected in " + filedname + " dropdown");
			return selectedOpStr;
		} catch (Exception e) {
			LOGGER.info("get selected option From DropDown failed..." + e);
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
			LOGGER.info("Exception:  " + e);
			val = false;
		}
		return val;
	}

	public void checkValFromDropDown(String filedname, List<String> dropDwonValues) {
		int historyValueCount = dropDwonValues.size();
		for (int i = 1; i < historyValueCount; i++) {
			String history = dropDwonValues.get(i);
			SelectDropDownByVisibleText("History", history);
			clickElement("Customer_EditBtn");
			if (verifyHistoryTabFields("application_PopUpTitle")) {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.FAIL, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i + "nd/th Value which is  '"
									+ history + "'  from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTED: " + i + "nd/th Value from List should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i + "nd/th Value which is   '"
									+ history + "'  from List is editable</span>");
				}
			}
		}
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
					test.log(LogStatus.FAIL, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i + "nd/th Value which is  '"
									+ history + "'  from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTED: " + i + "nd/th Value from List should be non editable",
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
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");

			}
			if (strText.equalsIgnoreCase(value.trim())) {
				printLogs(fieldName + " field has default value as -" + strText);
				test.log(LogStatus.PASS,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ "  has default value as " + strText + "</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL,
						"EXPECTED: Text field " + fieldName + " should have default value as -" + value,
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " has default value as - " + strText + "</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed...:  " + e);
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
			LOGGER.info("Exception:  " + e);
			return false;
		}
	}
	
	public boolean checkDisabledEelement(String fieldName, String replaceKeys, String replaceValues) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
			if (textField.isEnabled()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			LOGGER.info("Exception:  " + e);
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
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is by default selected",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is editable and default value selected is '" + value + "</span>");

			} else {
				printLogs(fieldName + " field is not editable and the default value selected is " + defaultSelVal);
				test.log(LogStatus.FAIL,
						"EXPECTED: Drop down " + fieldName + " should be editable and " + value
								+ " is selected by default",
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Drop down " + fieldName
								+ " is non editable and default value selected is -'" + value + "</span>");
			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info(fieldName + " -Dropdown validation failed..." + e);
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
			LOGGER.info("Exception:  " + e);
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
			LOGGER.info("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			return false;
		}
	}

	public boolean existsElement(String fieldName, String replaceKeys, String replaceValues) {
		try {
			String finalStrObj = putility.getProperty(fieldName);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			driver.findElement(locator);
			printLogs(fieldName + " element Exist");

			return true;
		} catch (NoSuchElementException e) {
			LOGGER.info("Exception occured while looking for element : " + fieldName + "  - " + e);
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
					test.log(LogStatus.FAIL, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTED: " + i + "nd/th Value from List should be non editable",
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
			LOGGER.info("Exception : " + e);
		}
	}

	public String getText(String fieldName) {
		try {
			By locBY = putility.getObject(fieldName);
			return driver.findElement(locBY).getText();
		} catch (NoSuchElementException e) {
			LOGGER.info("Exception occured while getting text from element : " + fieldName + "  - " + e);
			printLogs("Exception occured while getting text from element : " + fieldName + "  - " + e);
			return "";
		}
	}

	public String getAttribute(String fieldname, String attribute, String replaceKeys, String replaceValues) {
		try {
			String finalStrObj = putility.getProperty(fieldname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			return driver.findElement(locator).getAttribute(attribute);
		} catch (NoSuchElementException e) {
			LOGGER.info("Exception occured while getting attribute " + attribute + " from element : " + fieldname
					+ "  - " + e);
			printLogs("Exception occured while getting attribute " + attribute + " from element : " + fieldname + "  - "
					+ e);
			return "";
		}
	}

	public String getAttribute(String fieldname, String attribute) {
		try {

			By locator = putility.getObjectFromStr(putility.getProperty(fieldname));

			return driver.findElement(locator).getAttribute(attribute);

		} catch (NoSuchElementException e) {
			LOGGER.info("Exception occured while getting attribute " + attribute + " from element : " + fieldname
					+ "  - " + e);
			printLogs("Exception occured while getting attribute " + attribute + " from element : " + fieldname + "  - "
					+ e);
			return "";
		}
	}

	public void deleteAllFilesInDownloadFolder() {
		if(RUN_IN_REMOTE)
		{
			RestAssured.given().body("{\"directory\":\""+downloadpathstr+"\"}")
							.post("http://"+REMOTE_HOST_IP+":"+DESKTOP_WIN_CONTROL_PORT+"/files/deleteallfiles").body();
		}
		else
		{
			File folder = new File(downloadpathstr);
			File[] allfilesInFolder = folder.listFiles();
	
			printLogs("AllfilesInFolder :" + allfilesInFolder.length);
			for (int i = 0; i < allfilesInFolder.length; i++) {
				printLogs("file: " + allfilesInFolder[i].getName());
				allfilesInFolder[i].delete();
			}
	
			printLogs("deleted all files from download loaction");
		}
	}

	public String getDownlaodedFileName() {
		checkforanydownloadsinprogress();
		String retfilename = null;

		if(RUN_IN_REMOTE)
		{
			Response resp = RestAssured.given().body("{\"directory\":\""+downloadpathstr+"\"}")
			.post("http://"+REMOTE_HOST_IP+":"+DESKTOP_WIN_CONTROL_PORT+"/files/getDownloadedCSVFileName");	
			if(resp.statusCode()==200)
				retfilename =resp.body().asString();
			else
			{
				LOGGER.error("Error occured while getting downloaded file name from remote system");
				Assert.fail("Error occured while getting downloaded file name from remote system");
			}
		}
		else
		{
			File folder = new File(downloadpathstr);
			File[] allfilesInFolder = folder.listFiles();
			Arrays.sort(allfilesInFolder, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	
			if (allfilesInFolder[0].getName().contains(".csv")) {
	
				retfilename = allfilesInFolder[0].getName();
			}
		}
		printLogs("dowloaded file path: " + downloadpathstr + retfilename);
		return downloadpathstr + retfilename;

	}

	private void checkforanydownloadsinprogress() {
		if(RUN_IN_REMOTE)
		{
			RestAssured.given().body("{\"directory\":\""+downloadpathstr+"\"}")
			.post("http://"+REMOTE_HOST_IP+":"+DESKTOP_WIN_CONTROL_PORT+"/files/checkforanydownloadsinprogress").body();	
		}
				
		else
		{
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
	}

	public static String copyRemoteFileToLocalAndGetLocalPath(String remoteFilepath)
	{
		String ret = null;
		try
		{
		byte[] bytes = RestAssured.given().body("{\"filepath\":\""+remoteFilepath+"\"}")
			.post("http://"+REMOTE_HOST_IP+":"+DESKTOP_WIN_CONTROL_PORT+"/files/getFile").body().asByteArray();
		
		String tempDirPath = "temp/"+getCurrentTimeStamp();
		new File(tempDirPath).mkdirs();
		String tempFilePath = tempDirPath+"/"+FilenameUtils.getName(remoteFilepath);
		new File(tempFilePath).createNewFile();
		FileOutputStream fos = new FileOutputStream(tempFilePath);
		fos.write(bytes);
		fos.close();
		
		ret = new File(tempFilePath).getAbsolutePath();
		}catch(Exception e)
		{
			LOGGER.error("Error occured while getting remote file. -- Excetion "+e);
			Assert.fail("Error occured while getting remote file. -- Excetion "+e);
		}
		return ret;
	}
	
	
	public void selectCheckBox(String filedname, String replaceKeys, String replaceValues) {

		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			if (!driver.findElement(locator).isSelected())
				driver.findElement(locator).click();

			printLogs(filedname + " checkbox has been selected");
			test.log(LogStatus.PASS, "EXPECTED: checkbox " + filedname + " should be selected",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
							+ " has been selected</span>");

		} catch (Exception e) {
			getScreenShot("selecting checkbox " + filedname);
			LOGGER.info("Selecting checkbox failed..." + e);
			printLogs("Selecting checkbox failed..." + e);
			test.log(LogStatus.FAIL, "Selecting checkbox failed", "Selecting checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void unSelectCheckBox(String filedname, String replaceKeys, String replaceValues) {

		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			if (driver.findElement(locator).isSelected())
				driver.findElement(locator).click();

			printLogs(filedname + " checkbox has been unselected");
			test.log(LogStatus.PASS, "EXPECTED: checkbox " + filedname + " should be unselected",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
							+ " has been unselected</span>");

		} catch (Exception e) {
			getScreenShot("Unselecting checkbox " + filedname);
			LOGGER.info("Unselecting checkbox failed..." + e);
			printLogs("Unselecting checkbox failed..." + e);
			test.log(LogStatus.FAIL, "Unselecting checkbox failed", "Unselecting checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void unSelectCheckBox(String filedname) {

		try {			
			By locator = putility.getObject(filedname);

			if (driver.findElement(locator).isSelected())
				driver.findElement(locator).click();

			printLogs(filedname + " checkbox has been unselected");
			test.log(LogStatus.PASS, "EXPECTED: checkbox " + filedname + " should be unselected",
					"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
							+ " has been unselected</span>");

		} catch (Exception e) {
			getScreenShot("Unselecting checkbox " + filedname);
			LOGGER.info("Unselecting checkbox failed..." + e);
			printLogs("Unselecting checkbox failed..." + e);
			test.log(LogStatus.FAIL, "Unselecting checkbox failed", "Unselecting checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}
	public void checkCheckBoxSelected(String filedname, String replaceKeys, String replaceValues) {

		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			if (driver.findElement(locator).isSelected()) {

				test.log(LogStatus.PASS, "EXPECTED: checkbox " + filedname + " should be in selected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in selected status</span>");
				printLogs(filedname + " checkbox is in selected status");
			} else {
				getScreenShot(filedname + " checkbox in Unselected status");
				test.log(LogStatus.FAIL, "EXPECTED: checkbox " + filedname + " should be in selected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Unselected status</span>");
				printLogs(filedname + " checkbox in Unselected status");
			}

		} catch (Exception e) {
			getScreenShot("validate checkbox " + filedname);
			LOGGER.info("validate checkbox failed..." + e);
			printLogs("validate checkbox failed..." + e);
			test.log(LogStatus.FAIL, "validate checkbox failed", "validate checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkCheckBoxUnselected(String filedname, String replaceKeys, String replaceValues) {

		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			if (!driver.findElement(locator).isSelected()) {

				test.log(LogStatus.PASS, "EXPECTED: checkbox " + filedname + " should be in Unselected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Unselected status</span>");
				printLogs(filedname + " checkbox is in Unselected status");
			} else {
				getScreenShot(filedname + " checkbox in Unselected status");
				test.log(LogStatus.FAIL, "EXPECTED: checkbox " + filedname + " should be in Unselected status",
						"Usage: <span style='font-weight:bold;'>ACTUAL:: checkbox " + filedname
								+ " is in Selected status</span>");
				printLogs(filedname + " checkbox in Selected status");
			}

		} catch (Exception e) {
			getScreenShot("validate checkbox " + filedname);
			LOGGER.info("validate checkbox failed..." + e);
			printLogs("validate checkbox failed..." + e);
			test.log(LogStatus.FAIL, "validate checkbox failed", "validate checkbox failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "Selecting checkbox failed " + e, uniqueDataId, "Result_Errors");
		}
	}

	public Boolean isCheckBoxSelected(String filedname, String replaceKeys, String replaceValues) {

		Boolean ret = null;
		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			if (driver.findElement(locator).isSelected()) {
				printLogs(filedname + " checkbox in Selected status");
				ret = true;
			} else {
				printLogs(filedname + " checkbox in Unselected status");
				ret = false;
			}

		} catch (Exception e) {
			LOGGER.info("failed to get checkbox status..." + e);
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
			LOGGER.info("Exception occure while looking for alert  - " + e);
			printLogs("Exception occure while looking for alert  - " + e);
			return ret;
		}

	}

	public String getText(String fieldName, String replaceKeys, String replaceValues) {
		try {
			String finalStrObj = putility.getProperty(fieldName);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			return driver.findElement(locator).getText().trim();

		} catch (NoSuchElementException e) {
			LOGGER.info("Exception occured while getting text from element : " + fieldName + "  - " + e);
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
			LOGGER.info("failed to sent/entered in text in element " + filedname + " .... " + e);
			printLogs("failed to sent/entered in text in element " + filedname + " .... " + e);
		}
	}

	public void sendTabKeys(String filedname) {

		try {

			By locator = putility.getObject(filedname);
			WebElement ele = driver.findElement(locator);

			ele.sendKeys(Keys.TAB);

		} catch (Exception e) {
			LOGGER.info("failed to press Tab from element " + filedname + " .... " + e);
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
			LOGGER.info("failed to enter text in element " + filedname + " .... " + e);
			printLogs("failed to enter text in element " + filedname + " .... " + e);
		}
	}

	public void checkEditableBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed..." + e);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	
	public void checkEditableCheckBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Checkbox " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Checkbox " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Checkbox " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Checkbox " + fieldName
								+ " is non editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Checkbox validations failed..." + e);
			printLogs("Checkbox validations failed..." + e);
			test.log(LogStatus.FAIL, "Checkbox validation", "Checkbox validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}
	
	public void checkNonEditableCheckBox(String fieldName) {
		try {
			WebElement textField = driver.findElement(putility.getObject(fieldName));
			if (!textField.isEnabled()) {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.PASS, "EXPECTED: Checkbox " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Checkbox " + fieldName
								+ " is non editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Checkbox " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Checkbox " + fieldName
								+ " is editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Checkbox validations failed..." + e);
			printLogs("Checkbox validations failed..." + e);
			test.log(LogStatus.FAIL, "Checkbox validation", "Checkbox validation failed  because  -" + e);
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
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			}

		} catch (Exception e) {
			getScreenShot("Validating field" + fieldName);
			LOGGER.info("Text field validations failed..." + e);
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
						"EXPECTED: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable and default value present is -'" + value + "</span>");
			} else {
				printLogs(fieldName + " field is non editable and the value present is " + editFieldval);
				test.log(LogStatus.PASS,
						"EXPECTED: Text field " + fieldName + " should not be editable and default value should be "
								+ value,
						"Validation:  <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is not editable and default value present is -'" + value + "</span>");

			}
		} catch (Exception e) {
			getScreenShot("Selecting  " + value);
			LOGGER.info("Text field validations failed..." + e);
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
			LOGGER.info(fieldName + " doesnt element Exist " + e);
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
			LOGGER.info("Exception:  " + e);
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
			LOGGER.info("Exception occured while looking for element : " + fieldName + "  - " + e);
			printLogs("Exception occured while looking for element : " + fieldName + "  - " + e);
			test.log(LogStatus.FAIL, fieldName + " element is not clicked");

		}
	}

	public void calMonth(String monthname) {
		
		WebElement scrollEle = driver.findElement(By.xpath("//div[contains(@class,'xdsoft_month') and contains(@style,'block')]/div[@style]"));
			
		if(monthname.equalsIgnoreCase("September") || monthname.equalsIgnoreCase("October") || monthname.equalsIgnoreCase("November") || monthname.equalsIgnoreCase("December"))
			((JavascriptExecutor) driver).executeScript("$(\"div[class='xdsoft_select xdsoft_monthselect xdsoft_scroller_box'] > div[style]\").eq(0).attr(\"style\", \"margin-top: -82px;\");");
		else
			((JavascriptExecutor) driver).executeScript("$(\"div[class='xdsoft_select xdsoft_monthselect xdsoft_scroller_box'] > div[style]\").eq(0).attr(\"style\", \"margin-top: -0px;\");");
		
		
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
			LOGGER.info("Exception:  " + e);
		}
		return strtext;
	}

	public boolean clickElement(String fieldname, String replaceKeys, String replaceValues) {

		String finalStrObj = putility.getProperty(fieldname);
		String[] rKys = replaceKeys.split("\\~");
		String[] rVals = replaceValues.split("\\~");

		for (int i = 0; i < rKys.length; i++)
			finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

		By locator = putility.getObjectFromStr(finalStrObj);
		printConsole("locator  " + locator);
		try {
			WebElement clkObject = driver.findElement(locator);
			clkObject.click();
			printLogs(fieldname + " - having xpath '" + locator + "' clicked sucessfully");
			test.log(LogStatus.PASS, "Element should be clicked sucessfully", fieldname + " - clicked sucessfully");
			return true;
		} catch (Exception e) {
			getScreenShot("'" + fieldname + "'  could not be clicked");
			LOGGER.info(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			test.log(LogStatus.WARNING, "Element should be clicked sucessfully",
					fieldname + " - (locator: "+locator+") could not be clicked because -" + e);
			printLogs(fieldname + " - having xpath '" + locator + "'  click failed  - " + e);
			return false;
		}
	}

	public boolean existElement(String filedname, String replaceKeys, String replaceValues) {
		boolean val = false;
		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);
			printConsole("locator is " + locator);
			if (driver.findElement(locator).isDisplayed()) {
				printLogs("Element exist");
				val = true;
			}
		} catch (Exception e) {
			getScreenShot("Element does not exist " + filedname);
			LOGGER.info("Element does not exist ..." + e);
			printLogs("Element does not exist ..." + e);
			val = false;
		}
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
			LOGGER.info(path + " - Element click failed  - " + e);
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
				test.log(LogStatus.PASS, "EXPECTED:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTED:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having read only access to "
								+ subTabName + "  screen</span>");
			}
		} else if ("R".equals(strAccessvalue)) {
			if (!presenceOfElement(locator)) {
				test.log(LogStatus.PASS, "EXPECTED:User should have read only access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having Read only access to "
								+ subTabName + "  screen</span>");

			} else {
				test.log(LogStatus.FAIL, "EXPECTED:User should have read only access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
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
			LOGGER.info("fieldName element does not Exist" + e);
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
							"EXPECTED: " + filedname + " field  should have total : " + fieldVal + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + value + " values</span>");
				} else {
					test.log(LogStatus.FAIL,
							"EXPECTED: " + filedname + " field  should have total : " + fieldVal + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + value + " values</span>");
				}
			}
		} catch (Exception e) {
			LOGGER.info("Exception :  " + e);
			test.log(LogStatus.FAIL, "Error occured while validating dropdown options ( count ) from "+filedname+"   Exception :  " + e);
		}
		return value;

	}

	public void validateFieldsInDropDown(String filedname, String fieldVal) {
		
		try {
			if (fieldVal != null && fieldVal.trim().length() > 0) {
				String[] arr = fieldVal.split(";");
				List<String> optionList = getAllOptionsFromDropDown(filedname);			
				
				//validating count
				if (optionList.size() == arr.length) {
					test.log(LogStatus.PASS,
							"EXPECTED: " + filedname + " field  should have total : " + arr.length + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + optionList.size() + " values</span>");
				} else {
					test.log(LogStatus.FAIL,
							"EXPECTED: " + filedname + " field  should have total : " + arr.length + " values",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
									+ " field  has total : " + optionList.size() + " values</span>");
				}
				
				//validating field text
				boolean allFieldsMatched =true;
				for (int i = 0; i < arr.length; i++) {				
					if(!arr[i].trim().isEmpty())
					{
						if ( !optionList.contains(arr[i].trim())) {
								test.log(LogStatus.FAIL,
										"EXPECTED: " + filedname + " field  should have : '" + arr[i] + "' value in dropdown",
										"Validation:  <span style='font-weight:bold;'>ACTUAL::  " + filedname
												+ " field does not have the value : '" + arr[i] + "' in the dropdown</span>");
								allFieldsMatched= false;
							}
					}
				}
				
				if(allFieldsMatched)
					test.log(LogStatus.PASS,
							"EXPECTED: All options within dropdown " + filedname + " should be matched",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::  All options within dropdown " + filedname + " have ben matched</span>");
				
			}
		} catch (Exception e) {
			LOGGER.info("Exception :  " + e);
		}

	}

	public void checkScreenExistence(String locator, String strAccessvalue, String subTabName) {
		if ("Y".equals(strAccessvalue)) {
			if (presenceOfElement(locator)) {
				test.log(LogStatus.PASS, "EXPECTED:User should have write access to " + subTabName + "  screen",
						"Validation:  <span style='font-weight:bold;'>" + "User is having write access to " + subTabName
								+ "  screen</span>");
			} else {
				test.log(LogStatus.FAIL, "EXPECTED:User should have write access to " + subTabName + "  screen",
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
			LOGGER.info("exception :" + e);
			printLogs("failed to sent/entered in text in element " + filedname + " .... " + e);
		}
	}

	public void checkEditableBox(String fieldName, String replaceKeys, String replaceValues) {
		try {
			String finalStrObj = putility.getProperty(fieldName);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			WebElement textField = driver.findElement(locator);

			if (textField.isEnabled()) {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			}

		} catch (Exception e) {
			LOGGER.info("exception :" + e);
			getScreenShot("Validating field" + fieldName);
			printLogs("Text field validations failed..." + e);
			test.log(LogStatus.FAIL, "Text field validation", "Text field validation failed  because  -" + e);
			excelUtils.setCellData(sheetName, "FAIL", uniqueDataId, "Result_Status");
			excelUtils.setCellData(sheetName, "" + e, uniqueDataId, "Result_Errors");
		}
	}

	public void checkReadonlyProperty(String fieldName, String replaceKeys, String replaceValues) {
		try {

			String finalStrObj = putility.getProperty(fieldName);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

			By locator = putility.getObjectFromStr(finalStrObj);

			WebElement textField = driver.findElement(locator);
			String editFieldval = textField.getAttribute("readonly");
			if (textField.isDisplayed()
					&& ("true".equalsIgnoreCase(editFieldval) || "readonly".equalsIgnoreCase(editFieldval))) {
				printLogs(fieldName + " field is non editable");
				test.log(LogStatus.PASS, "EXPECTED: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is non editable</span>");
			} else {
				printLogs(fieldName + " field is editable");
				test.log(LogStatus.FAIL, "EXPECTED: Text field " + fieldName + " should be non editable",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Text field " + fieldName
								+ " is editable</span>");
			}

		} catch (Exception e) {
			LOGGER.info("exception :" + e);
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
			LOGGER.info("exceptin : " + e);
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
					test.log(LogStatus.FAIL, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is non editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and is non editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: " + i + "nd/th Value from List is should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is non editable</span>");
				}
			} else {
				if (i == 1) {
					printLogs("First Value from List is -" + history + " and is editable in the page");
					test.log(LogStatus.PASS, "EXPECTED: First Value from List should be editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL:: First Value from List is -" + history
									+ " and is editable in the page</span>");
				} else {
					printLogs(i + "nd/th Value from List is -" + history + " and editable in the page");
					test.log(LogStatus.FAIL, "EXPECTED: " + i + "nd/th Value from List should be non editable",
							"Validation:  <span style='font-weight:bold;'>ACTUAL::" + i
									+ "nd/th Value from List is editable</span>");
				}
			}
		}
	}

	public void selectCalendarDate(String filedname, String replaceKeys, String replaceValues, String date) {
		try {
			String finalStrObj = putility.getProperty(filedname);
			String[] rKys = replaceKeys.split("\\~");
			String[] rVals = replaceValues.split("\\~");

			for (int i = 0; i < rKys.length; i++)
				finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

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

	public String getDropDownSelectedVal(String fieldName, String val) {
		WebElement dropDownField = null;
		String defaultSelVal = null;
		if (val.trim().length() > 0 && val != null) {
			try {
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);
				defaultSelVal = oSelect.getFirstSelectedOption().getText();
				LOGGER.info("defaultSelVal is " + defaultSelVal);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);

			}
		}
		return defaultSelVal;
	}

	public String getTxtBoxValue(String fieldName, String val) {
		WebElement textField = null;
		String strText = null;
		if (val.trim().length() > 0 && val != null) {
			try {
				textField = driver.findElement(putility.getObject(fieldName));
				strText = textField.getAttribute("value").trim();
				LOGGER.info("Txt Box value is " + strText);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);
			}
		}
		return strText;
	}
	
	public String getTxtBoxValue(String fieldName) {
		WebElement textField = null;
		String strText = null;
			try {
				textField = driver.findElement(putility.getObject(fieldName));
				strText = textField.getAttribute("value").trim();
				LOGGER.info("Txt Box value is " + strText);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from textbox: '"+fieldName+"'        " + e);
				test.log(LogStatus.FAIL, "Error occured while getting value from textbox: '"+fieldName+"'          " + e);
			}
		return strText;
	}
	
	public String getTxtBoxValue(String fieldName, String replaceKeys, String replaceValues) {
		WebElement textField = null;
		String strText = null;
			try {
				textField = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
				strText = textField.getAttribute("value").trim();
				LOGGER.info("Txt Box value is " + strText);
			} catch (Exception e) {
				LOGGER.info("Error occured while getting value from textbox: '"+fieldName+"'.   " + e);
				test.log(LogStatus.FAIL, "Error occured while getting value from textbox: '"+fieldName+"'.   " + e);
			}
		return strText;
	}

	public String getChkBoxStatus(String fieldName, String val) {
		WebElement chkBoxField = null;
		String strText = null;
		if (val.trim().length() > 0 && val != null) {
			try {
				chkBoxField = driver.findElement(putility.getObject(fieldName));
				if (chkBoxField.isSelected())
					strText = "Y";
				else
					strText = "N";
				LOGGER.info("Check box status is " + strText);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);

			}
		}
		return strText;
	}

	public String getDropDownMultiSelectedVal(String fieldName, String val) {
		WebElement dropDownField = null;
		String allSelectedOptions = "";
		if (val.trim().length() > 0 && val != null) {
			try {
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);

				int i = 0;
				for (WebElement ele : oSelect.getAllSelectedOptions()) {
					String eletext = ele.getText();
					if (i == 0)
						allSelectedOptions = eletext + ",";
					else
						allSelectedOptions = allSelectedOptions + "," + eletext;

					i++;
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);

			}
		}
		return allSelectedOptions;
	}

	public String getDropDownAllVal(String fieldName, String val) {
		WebElement dropDownField = null;
		String allSelectedOptions = "";
		if (val.trim().length() > 0 && val != null) {
			try {
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);

				int i = 0;
				for (WebElement ele : oSelect.getOptions()) {
					String eletext = ele.getText();
					if (i == 0)
						allSelectedOptions = eletext;
					else {
						if (i == 1)
							allSelectedOptions = allSelectedOptions + ",";

						allSelectedOptions = allSelectedOptions + "," + eletext;
					}

					i++;
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);

			}
		}
		return allSelectedOptions;
	}

	/* Get the newest file for a specific extension */
	public File getTheNewestFile(String filePath, String supplierID) {
		File theNewestFile = null;
		File dir = new File(filePath);
		FileFilter fileFilter = new WildcardFileFilter(
				"CustomErrorCodePerSupplier-" + supplierID + "-" + GetCurrentDate() + "*.csv");
		File[] files = dir.listFiles(fileFilter);

		if (files.length > 0) {
			/** The newest file comes first **/
			Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
			theNewestFile = files[0];
		}

		return theNewestFile;
	}

	public String GetCurrentDate() {
		Date curDate = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		String DateToStr = format.format(curDate);
		format = new SimpleDateFormat("dd-MMMM-yyyy", Locale.ENGLISH);
		DateToStr = format.format(curDate);

		System.out.println(DateToStr);

		return DateToStr;

	}

	public Set<String> getDropDownAllVal23(String fieldName, String val) {
		WebElement dropDownField = null;
		Set<String> allSelectedOptions = new HashSet<>();
		if (val.trim().length() > 0 && val != null) {
			try {
				dropDownField = driver.findElement(putility.getObject(fieldName));
				Select oSelect = new Select(dropDownField);

				int i = 0;
				for (WebElement ele : oSelect.getOptions()) {
					String eletext = ele.getText();
					allSelectedOptions.add(eletext);
				}
				LOGGER.info("defaultSelVal is " + allSelectedOptions);
			} catch (Exception e) {
				LOGGER.info("Exception in getting value from drop down:  " + e);

			}
		}
		return allSelectedOptions;
	}

	public List<String> ElementsToList(String fieldName) {
		List<String> list1 = new LinkedList<>();

		List<WebElement> listOfElement = driver.findElements(putility.getObject(fieldName));
		Iterator<WebElement> iter = listOfElement.iterator();

		// this will check whether list has some element or not
		while (iter.hasNext()) {
			WebElement item = iter.next();

			list1.add(item.getText());
		}
		return list1;

	}
	
	public List<String> ElementsToListWithTrim(String fieldName) {
		List<String> list1 = new LinkedList<>();

		List<WebElement> listOfElement = driver.findElements(putility.getObject(fieldName));
		Iterator<WebElement> iter = listOfElement.iterator();

		// this will check whether list has some element or not
		while (iter.hasNext()) {
			WebElement item = iter.next();

			list1.add(item.getText().trim());
		}
		return list1;

	}

	public List<String> ElementsToListWithTrim(String fieldName, String replaceKeys, String replaceValues) {
		List<String> list1 = new LinkedList<>();

		List<WebElement> listOfElement = driver.findElements(putility.getObject(fieldName, replaceKeys, replaceValues));
		Iterator<WebElement> iter = listOfElement.iterator();

		// this will check whether list has some element or not
		while (iter.hasNext()) {
			WebElement item = iter.next();

			list1.add(item.getText().trim());
		}
		return list1;

	}
	
	public ArrayList<String> readCSVToList(File newFile) {
		BufferedReader crunchifyBuffer = null;
		ArrayList<String> CSVData = new ArrayList<String>();
		try {
			String crunchifyLine;
			crunchifyBuffer = new BufferedReader(new FileReader(newFile));

			// How to read file in java line by line?
			while ((crunchifyLine = crunchifyBuffer.readLine()) != null) {
				System.out.println("Raw CSV data: " + crunchifyLine);
				System.out.println("Converted ArrayList data: " + crunchifyCSVtoArrayList(crunchifyLine) + "\n");
				CSVData.addAll(crunchifyCSVtoArrayList(crunchifyLine));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (crunchifyBuffer != null)
					crunchifyBuffer.close();
			} catch (IOException crunchifyException) {
				crunchifyException.printStackTrace();
			}
		}
		return CSVData;
	}

	// Utility which converts CSV to ArrayList using Split Operation
	public static ArrayList<String> crunchifyCSVtoArrayList(String crunchifyCSV) {
		ArrayList<String> crunchifyResult = new ArrayList<String>();

		if (crunchifyCSV != null) {
			String[] splitData = crunchifyCSV.split("\\s*,\\s*");
			for (int i = 0; i < splitData.length; i++) {
				if (!(splitData[i] == null) || !(splitData[i].length() == 0)) {
					crunchifyResult.add(splitData[i].trim());
				}
			}
		}

		return crunchifyResult;
	}

	public void CheckCSVWithUI(String filePath, String SupplierID, String fieldName) {
		ArrayList<String> csvData = new ArrayList<String>();
		List<String> UIElementList;

		File newFile;
		newFile = getTheNewestFile(filePath, SupplierID);
		System.out.println(newFile);
		csvData = readCSVToList(newFile);
		UIElementList = ElementsToList(fieldName);
		boolean b = csvData.equals(UIElementList);
		if (!b) {
			printLogs(
					"Value from csv file is  -" + csvData + " and are not matched with UI elements -" + UIElementList);
			test.log(LogStatus.FAIL, "EXPECTED: Values from List should be matched with UI Data in pages",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  Values from List is -" + csvData
							+ " and are not matched with UI elements- </span>" + UIElementList);
		} else {
			printLogs("Value from csv file is  -" + csvData + " and are matched with UI elements -" + UIElementList);
			test.log(LogStatus.PASS, "EXPECTED: Values from List should be matched with UI Data in pages",
					"Validation:  <span style='font-weight:bold;'>ACTUAL::  Values from List is -" + csvData
							+ " and are matched with UI elements- </span>" + UIElementList);
		}
	}

	public String generateRadnomString(long strLength) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < strLength; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);	
		}
		return sb.toString();
	}

	public void scrollDownPage()
	{
		try{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollTo(0,document.body.scrollHeight)");
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page. Exception - "+e);
		}
	}
	
	public void scrollUpPage()
	{
		try{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollTo(document.body.scrollHeight,0)");		
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page. Exception - "+e);
		}
	}
	
	public void scrollRightPage()
	{
		try{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(2000,0)", "");
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page. Exception - "+e);
		}
	}
	
	public void scrollLeftPage()
	{
		try{
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollBy(-2000,0)", "");
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page. Exception - "+e);
		}
	}
	
	public void scrollPageToViewElement(String fieldName)
	{
		try{
			
		WebElement ele = driver.findElement(putility.getObject(fieldName));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", ele);		
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page to element. Exception - "+e);
		}
	}
	
	public void scrollPageToViewElement(String fieldName, String replaceKeys, String replaceValues)
	{
		try{
			
		WebElement ele = driver.findElement(putility.getObject(fieldName, replaceKeys, replaceValues));
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", ele);		
		}catch(Exception e)
		{
			LOGGER.error("Error occured while scrolling the page to element. Exception - "+e);
		}
	}
	
	// public void readPDFContent() throws IOException
	// {
	//
	//
	// File input= new File("C:\\Invoice.pdf"); // The PDF file from where you
	// would like to extract
	// File output = new File("C:\\SampleText.txt"); // The text file where you
	// are going to store the extracted data
	// PDDocument pd = PDDocument.load(input);
	// System.out.println(pd.getNumberOfPages());
	//
	// System.out.println(pd.isEncrypted());
	// pd.save("CopyOfInvoice.pdf"); // Creates a copy called
	// "CopyOfInvoice.pdf"
	// PDFTextStripper stripper = new PDFTextStripper();
	// BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new
	// FileOutputStream(output)));
	// stripper.writeText(pd, wr);
	// if (pd != null) {
	// pd.close();
	// }
	// // I use close() to flush the stream.
	// wr.close();
	//
	// }
	
	
	public String getPropery(String fieldsName)
	{
		return putility.getProperty(fieldsName);
	}

	public String getPropery(String fieldsName, String replaceKeys, String replaceValues)
	{
		return putility.getProperty(fieldsName, replaceKeys, replaceValues);
	}
}