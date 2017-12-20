package com.tata.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.PropertyUtility;
import java.util.logging.Logger;


public class NavigationMenuPage extends CommonUtils {
	private static final Logger LOGGER = Logger.getLogger(NavigationMenuPage.class.getName());
	PropertyUtility putility=new PropertyUtility(OBJECT_REPO_FILEPATH, getExTest());
	ExtentTest test;
	By navBar = putility.getObject("navigation_Dashboard");
		
	public NavigationMenuPage(WebDriver driver,ExtentTest test, String sheetName, String uniqueDataId, String testCaseId, String objetResPath) {
		super(driver,test, sheetName, uniqueDataId, testCaseId, objetResPath);
		try {
			default_content();
			SwitchFrames("//iframe[@name='bottom']");
			SwitchFrames("index");
			printLogs("Navigation Bar appear/loaded successfully");
		} catch (Exception e) {
			LOGGER.info("error" +e);
			printLogs("Navigation Bar did not appear/loaded");
			Assert.fail("Navigation Bar did not appear");
		}
	}
	
	private void supplierArrowBtnClick(String firstLink){
		String arrowBtn="//*[text()='"+firstLink+"']/..//following-sibling::span";
		driver.findElement(By.xpath(arrowBtn)).click();
		
	}
	
	private void supplierProvisioningBtnClick(String firstLink,String secondLink){
		String linkName="//*[text()='"+firstLink+"']/..//following-sibling::ul//font[text()='"+secondLink+"']";
		driver.findElement(By.xpath(linkName)).click();
	}
	
	public void navigateToMenu(String allLinks){
		String[] navigation = allLinks.split(">");
		String firstLink = navigation[0].trim();
		String secondLink=navigation[1].trim();
		
		supplierArrowBtnClick(firstLink);
		supplierProvisioningBtnClick(firstLink,secondLink);
		printLogs(firstLink+" "+secondLink+" page opened sucessfully");
		default_content();
	}

	public void navigateToMenuPageAndMenu(String allLinks){
		try {
			default_content();
			SwitchFrames("//iframe[@name='bottom']");
			SwitchFrames("index");
			driver.findElement(navBar);
			printLogs("Navigation Bar appear/loaded successfully");
			navigateToMenu(allLinks);
		} catch (NoSuchElementException e) {
			printLogs("Navigation Bar did not appear/loaded");
			Assert.fail("Navigation Bar did not appear");
			LOGGER.info("error " +e);
		}
	}
	
	private void Menu_BtnClick(String firstLink) {
		String linkName = "//*[@id='sitemap']/li/a/font[text()='" + firstLink + "']";
		driver.findElement(By.xpath(linkName)).click();
	}

	public void navigateMenu(String allLinks) {
		String[] navigation = allLinks.split(",");
		int len = navigation.length;
		for (int i = 0; i < len; i++) {
			try{
				Menu_BtnClick(navigation[i].trim());
			printLogs(navigation[i] + " page opened sucessfully");
			test.log(LogStatus.PASS, "EXPECTECD: Menu " + navigation[i] + " is present",
					"Validation: <span style='font-weight:bold;'>ACTUAL:: Menu " + navigation[i]
							+ " is present</span>");
			}catch(Exception e){
				printLogs(navigation[i] + " page not opened sucessfully");
				test.log(LogStatus.FAIL, "EXPECTECD: Menu " + navigation[i] + " is not present",
						"Validation: <span style='font-weight:bold;'>ACTUAL:: Menu " + navigation[i]
								+ " is not present</span>");
				LOGGER.info("error " +e);
			}
		}

	}

	private void subMenu_BtnClick(String firstLink) {
		String linkName = "//ul//font[text()='" + firstLink + "']";
		driver.findElement(By.xpath(linkName)).click();
	}


	 public void enableRouting() {
		  default_content();
		  SwitchFrames("//iframe[@name='bottom']");
		  SwitchFrames("index");
		  driver.findElement(By.xpath("//*[text()='Routing']/..//following-sibling::ul//font[text()='Force Routing']"))
		    .click();
		  waitForPageLoad("Performance Trend");
		  default_content();
		  SwitchFrames("bottom");
		  SwitchFrames("target");
		  // Validating all buttons
		  checkElementPresence("Force_RoutingBtn");

		  clickElement("Force_RoutingBtn");

		  checkMessage("application_PopUpMessage", "Enabling force route", "Force Routing is enabled");
		  
		  sleep(5000);
		  
		 }
}
