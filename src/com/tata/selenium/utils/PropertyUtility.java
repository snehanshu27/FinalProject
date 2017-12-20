package com.tata.selenium.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.testng.Assert;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

/**
 * @date 
 * @author Devbrath Singh
 * @description Property file containing methods to read values from property file
 */

public class PropertyUtility {
	
	private static final Logger LOGGER = Logger.getLogger(PropertyUtility.class.getName());

	Properties prop = new Properties();	
	String fileName;
	ExtentTest test;
	
	public PropertyUtility(String fileName,ExtentTest test)
	{
		this.fileName = fileName;
		this.test = test;
		try{
			InputStream input =  null;
			input = new FileInputStream(fileName);
			prop.load(input);
		}
		catch (IOException e) {
			LOGGER.info("error:  " +e);
		}
				
	}
	

	
	public String getProperty(String propertyKey){
		String propertyValue;
		propertyValue=prop.getProperty(propertyKey);
		return propertyValue;
	}
	
	public String getProperty(String propertyKey, String replaceKeys, String replaceValues){
		String finalStrObj = getProperty(propertyKey);
		String[] rKys = replaceKeys.split("\\~");
		String[] rVals = replaceValues.split("\\~");

		for (int i = 0; i < rKys.length; i++)
			finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);
		return finalStrObj;
	}
	private By getObjectFromStr(String strObj)
	{
		By ret = null;
		
		String[] keyVal = strObj.split("\\~");
		String key = keyVal[0].trim();
		String value = keyVal[1].trim();
		
		switch(key.toLowerCase())
		{
		case "class":
				ret = By.className(value);
			break;
		case "css":	
			ret = By.cssSelector(value);
			break;
		case "id":
			ret = By.id(value);
			break;
		case "link":
			ret = By.linkText(value);
			break;
		case "name":
			ret = By.name(value);
			break;
		case "partiallink":
			ret = By.partialLinkText(value);
			break;
		case "tagname":
			ret = By.tagName(value);
			break;
		case "xpath":
			ret = By.xpath(value);
			break;
		default:
			break;
		}
		
		return ret;
	}
	
	public By getObject(String name, String replaceKeys, String replaceValues )
	{
		String finalStrObj = getProperty(name);
		if(finalStrObj==null)			
		{
			test.log(LogStatus.FAIL, "Property should be present", "Property: '"+name+"' missing in object repository file: "+fileName);
			Assert.fail("Property: '"+name+"' missing in object repository file: "+fileName);
		}
		
		String[] rKys = replaceKeys.split("\\~");
		String[] rVals = replaceValues.split("\\~");

		for (int i = 0; i < rKys.length; i++)
			finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

		return getObjectFromStr(finalStrObj);

		
	}
	public By getObject(String name)
	{
		By ret = null;
		
		String proStr = getProperty(name);
		if(proStr!=null)
			ret = getObjectFromStr(proStr);
		else	
		{
			test.log(LogStatus.FAIL, "Property should be present", "Property: '"+name+"' missing in object repository file: "+fileName);
			Assert.fail("Property: '"+name+"' missing in object repository file: "+fileName);
		}
						
		return ret;
	}
}