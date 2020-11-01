package com.selenium.pages;

import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;


public class TC01_VerifyText {
	
	@Test
	public void DO() throws Exception {
	
		String DRIVER_PATH="./lib";
		System.setProperty("webdriver.chrome.driver", DRIVER_PATH + "/chromedriver.exe");
		WebDriver driver=new ChromeDriver();
		//String baseWebUrl = "http://localhost/projCert/website/";
		String baseWebUrl ="https://www.google.com/";
		String expectedWebsiteTitle = "About Us";
		 
		/*Launch Firefox browser and browse the Base URL*/
		driver.get(baseWebUrl);
		
		/*Get ID of About Us element*/
		//WebElement about=driver.findElement(By.id("About Us"));
		
		/*Get text of About Us element*/
		//String Text = about.getText();
		
		/*Verify text of About Us*/
		//assertEquals(Text, expectedWebsiteTitle,"Text of About us element is as expected");
		
	}


}
