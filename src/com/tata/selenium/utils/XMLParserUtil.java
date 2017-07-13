package com.tata.selenium.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

/**
 * @date
 * @author Devbrath Singh
 * @description XML parser containing mthods to read valus from XML
 */

public class XMLParserUtil {

	public List<String> getAllTestCases(String xmlFilePath) {
		List<String> testCases = new ArrayList<>();
		try {
			File xmlFile = new File(xmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("test");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String testCase = eElement.getAttribute("name");
					testCases.add(testCase);
				}
			}
			return testCases;
		} catch (Exception e) {
			CommonUtils.printConsole("Error while reading the testng.xml " + e);
			return testCases;
		}
	}

	public String getAttributeValue(String xmlFilePath, String attributeName) {
		String paramValue;
		if (("env").equalsIgnoreCase(attributeName))
			paramValue = "Devops";
		else
			paramValue = "Firefox";
		try {
			File xmlFile = new File(xmlFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("parameter");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElementParam = (Element) nNode;
					if (eElementParam.getAttribute("name").equalsIgnoreCase(attributeName))
						paramValue = eElementParam.getAttribute("value");
				}
			}
			return paramValue;
		} catch (Exception e) {
			CommonUtils.printConsole("Error while reading the testng.xml in getEnviorenment " + e);
			return paramValue;
		}
	}

	public String newXMLFile(String xmlFilePath, String nodeName) {

		try {
			File xmlFile = new File(xmlFilePath);
			CommonUtils.printConsole("Inside newXML file method");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList list = doc.getElementsByTagName("*");
			for (int i = 0; i < list.getLength(); i++) {

				Node node = list.item(i);
				CommonUtils.printConsole("NODE   " + node);
				// Look through entire settings file
				if (node.getNodeName().contains("!DOCTYPE suite SYSTEM ")) {
					doc.getNodeName().replaceAll("!DOCTYPE suite SYSTEM 'http://testng.org/testng-1.0.dtd'", "");
					CommonUtils.printConsole("Node found and deleted");

				}
			}

			return nodeName;
		} catch (Exception e) {
			CommonUtils.printConsole("Error while READING the testng.xml in getEnviorenment " + e);
			return nodeName;

		}

	}

}
