package com.tata.selenium.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.tata.selenium.constants.ApplicationConstants;

public class TestNGXMLGenerator {
	
	private static final Logger LOGGER = Logger.getLogger(TestNGXMLGenerator.class.getName());

	public static void main(String[] args) throws IOException {
		String mainSheet = "Main";
		List<Map<String, String>> allRows =null;
		try {
			ExcelUtils excel = new ExcelUtils();
			excel.setExcelFile(ApplicationConstants.DATA_FILEPATH, mainSheet);
			allRows = excel.getSheetAllData(mainSheet);
			
		} catch (Exception e) {
			LOGGER.error("Error occured while reading data from EXCEL file > Sheet : 'ModuleControl' for test case Exceptions : " + e);
			CommonUtils.printConsole("Error occured while reading data from EXCEL file > Sheet : 'ModuleControl' for test case Exceptions : " + e);
			Reporter.log("Error occured while reading data from EXCEL file > Sheet : 'ModuleControl' for test case Exceptions : " + e);			
			Assert.fail("Error occured while reading data from EXCEL file > Sheet : 'ModuleControl' for test case Exceptions : " + e);
		}
		
		//Create an instance on TestNG
		 TestNG myTestNG = new TestNG();
		 
		//Create an instance of XML Suite and assign a name for it.
		 XmlSuite mySuite = new XmlSuite();
		 mySuite.setName("MMX-UIAutomation");
		 
		 mySuite.addListener("com.tata.selenium.driver.Driver");

		Set<String> uniqeModule = new HashSet<>();
		//Create a list tests which can contain the class that you want to run.
		List<XmlTest> xmlTests = new ArrayList<XmlTest>();

		for(Map<String, String> row : allRows)
		{
			if("Y".equalsIgnoreCase(row.get("Enable").trim()))
			{
				XmlTest xmlTest = new XmlTest(mySuite);
				xmlTest.setName(row.get("TestName"));		
				xmlTest.addParameter("testCaseId", row.get("testCaseId"));
				xmlTest.addParameter("uniqueDataId", row.get("uniqueDataId"));
				// set class
				List<XmlClass> classes = new ArrayList<XmlClass>();
				classes.add(new XmlClass(row.get("ClassName")));
				xmlTest.setXmlClasses(classes);				
				xmlTests.add(xmlTest);
					
			}
		}
		
						 
		//add the list of tests to your Suite.
		 mySuite.setTests(xmlTests);
		 
		//Add the suite to the list of suites.
		 List<XmlSuite> mySuites = new ArrayList<XmlSuite>();
		 mySuites.add(mySuite);
		 
		//Set the list of Suites to the testNG object you created earlier.
		 myTestNG.setXmlSuites(mySuites);
		 
		 FileUtils.write(new File("testNGtest.xml"),  mySuite.toXml(), Charset.defaultCharset());
		System.out.println("Done");

	}
	
}
