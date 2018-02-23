package com.tata.selenium.test.deliveryStatistics.three.x;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.jayway.restassured.RestAssured;
import com.relevantcodes.extentreports.LogStatus;

public class test {

	public static void main1(String[] args) {
		
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		ChromeOptions options = new ChromeOptions();
		Map<String, Object> prefs = new HashMap<>();
		
		prefs.put("profile.content_settings.pattern_pairs.*.multiple-automatic-downloads", 1);
		prefs.put("download.prompt_for_download", false);
		prefs.put("profile.default_content_settings.popups", 0);
		options.setExperimentalOption("prefs", prefs);
		capabilities.setCapability(ChromeOptions.CAPABILITY, options);
		
		
			System.setProperty("webdriver.chrome.driver", "./lib/chromedriver.exe");
		WebDriver	driver = new ChromeDriver(capabilities);
		driver.get("https://www.google.co.in");				
		
//		WebDriverWait wait = new WebDriverWait(driver, 2);	
		FluentWait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(2, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS);
		
		wait.ignoring(NoSuchElementException.class, TimeoutException.class);
//		wait.ignoring(NoSuchElementException.class);
//		
//		wait.ignoring(TimeoutException.class);
//		
		
		WebElement validElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q")));
		
		try{
		WebElement validElement1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("q1")));										
		}catch (NoSuchElementException e) {
			System.out.println("In NoSuchElementException catch block");
		}catch(TimeoutException e)
		{
			System.out.println("In TimeoutException catch block");
		}
		catch (Exception e) {
			System.out.println("In Exception catch block");
		}
		
	
		
	}
	
	public static void main(String[] args) {
		String s = "Cus *tomer *";
		System.out.println(s.lastIndexOf(" *"));
		
		System.out.println(s.substring(0, s.lastIndexOf(" *")));
	}
	

}
