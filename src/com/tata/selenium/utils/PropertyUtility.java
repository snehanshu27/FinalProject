package com.tata.selenium.utils;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;

/**
 * @date 
 * @author Devbrath Singh
 * @description Property file containing methods to read values from property file
 */

public class PropertyUtility {
	
	private static final Logger LOGGER = Logger.getLogger(PropertyUtility.class.getName());

	Properties prop = new Properties();	
	
	public PropertyUtility(String fileName)
	{
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
	
	public By getObjectFromStr(String strObj)
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
		String[] rKys = replaceKeys.split("\\~");
		String[] rVals = replaceValues.split("\\~");

		for (int i = 0; i < rKys.length; i++)
			finalStrObj = finalStrObj.replace(rKys[i], rVals[i]);

		return getObjectFromStr(finalStrObj);

		
	}
	public By getObject(String name)
	{
		By ret = null;
		
//		String[] keyVal = getProperty(name).split("\\~");
//		String key = keyVal[0].trim();
//		String value = keyVal[1].trim();
		
		String keyVal = getProperty(name).trim();		
		int index = keyVal.indexOf('~');
		String key = keyVal.substring(0, index).trim();
		String value = keyVal.substring(index+1, keyVal.length()).trim();
		
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
}