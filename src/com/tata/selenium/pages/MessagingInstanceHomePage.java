package com.tata.selenium.pages;

import java.util.logging.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.tata.selenium.utils.CommonUtils;
import com.tata.selenium.utils.ExcelUtils;
import com.tata.selenium.utils.PropertyUtility;

/**
 * @date 
 * @author 
 * @description This class is HomePage page of TATA messaging Instance app where all elements and their methods are written
 */
public class MessagingInstanceHomePage extends CommonUtils {
	private static final Logger LOGGER = Logger.getLogger(MessagingInstanceHomePage.class.getName());
	ExcelUtils excelUtils= new ExcelUtils();
	PropertyUtility putility=new PropertyUtility(OBJECT_REPO_FILEPATH, getExTest());
	
	
	By signOutBtn = putility.getObject("homepage_logoutBtn");
	
	
	public MessagingInstanceHomePage(WebDriver driver,ExtentTest test, String sheetName, String uniqueDataId, String testCaseId, String objetResPath){
		super(driver,test, sheetName, uniqueDataId, testCaseId, objetResPath);
		try {
			default_content();
			SwitchFrames("//iframe[@scrolling='no']");
			waitForElement(driver, signOutBtn,120);
			driver.findElement(signOutBtn);
			printLogs("TATA Messaging Home Page appear/loaded successfully");			
		} catch (NoSuchElementException e) {
			LOGGER.info("error " +e);
			printLogs("TATA Messaging Home Page did not appear/loaded successfully");
			Assert.fail("TATA Messaging Home Page did not appear/loaded successfully");
		} catch (Exception e) {
			LOGGER.info("error" +e);
		}
	}
	
	public void verifyLogin(ExtentTest test,String testCaseId,String sheetName){
		if(driver.findElement(signOutBtn).isDisplayed()){
			printLogs("After Login Screen verified sucessfully");
			test.log(LogStatus.PASS,"After Login Screen should be verified","After Login Screen  verified sucessfully");
		}
		else{
			printLogs("After Login Screen  could not be verified and test case failed");
			test.log(LogStatus.FAIL," After Login Screen should be verified","Sign Out Element could not be verified and test case failed");
		}
	}
	
	public void doLogOut(ExtentTest test){
		try{
			default_content();
			scrollUpPage();
			SwitchFrames("//iframe[@scrolling='no']");			
			driver.findElement(signOutBtn).click();
			printLogs("logout Successful");
			test.log(LogStatus.PASS, "EXPECTECD: Sigout from application", "Usage: <span style='font-weight:bold;'>ACTUAL:: Sigout is sucessfull from application</span>");
		}catch(Exception e){
			LOGGER.info("error " +e);
			printLogs("logout Failed");
			test.log(LogStatus.FAIL, "EXPECTECD: Sigout from application", "Usage: <span style='font-weight:bold;'>ACTUAL:: Sigout failed</span>");
		}
	}

}
	

	
