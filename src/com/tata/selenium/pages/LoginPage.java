package com.tata.selenium.pages;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.PropertyUtility;

/**
 * @date 
 * @author 
 * @description This class is login page of TATA messaging exchange application where all elements and their methods are written
 */
public class LoginPage extends CommonUtils {
	
	private static final Logger LOGGER = Logger.getLogger(LoginPage.class.getName());
	static String properties =  "./data/Login.properties";
	PropertyUtility putility=new PropertyUtility(properties, getExTest());
	
	
	By username = putility.getObject("login_Usrname");
	By passwd = putility.getObject("login_Pwd");
	By loginSubmitBtn = putility.getObject("login_SubmitBtn");
	
	public LoginPage(WebDriver driver,ExtentTest test, String sheetName, String uniqueDataId, String testCaseId, String objetResPath) {
		super(driver,test, sheetName, uniqueDataId, testCaseId, properties);
		
		if(elementDisplayed("login_Usrname"))
			printLogs("TATA Messaging Login Page appear/loaded successfully");
		else
		{
			LOGGER.info("TATA Messaging Login Page did not appear/loaded ");
			printLogs("TATA Messaging Login Page did not appear/loaded");
			getExTest().log(LogStatus.FAIL, "TATA Messaging Login Page should be loaded", "TATA Messaging Login Page did not appear/loaded");
			Assert.fail("LoginPage did not get displayed as the username field did not appear");
		}
	}
	
	
	private void setUsername(String user){
		
		driver.findElement(username).sendKeys(user);
		printLogs("Username '"+user+"' entered sucessfully");
	}
	
	private void setPasswd(String pwd){
		driver.findElement(passwd).sendKeys(pwd);
		printLogs("Password '"+pwd+"' entered sucessfully");
	}
	
	private void clickSignInBtn(){
		driver.findElement(loginSubmitBtn).click();
		printLogs("Submit Btn clicked sucessfully");
	}
	
	public void dologin(String user,String pwd){
		setUsername(user);
		setPasswd(pwd);
		clickSignInBtn();
	}
	
}
	
