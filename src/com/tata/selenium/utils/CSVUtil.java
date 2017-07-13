package com.tata.selenium.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.jayway.restassured.RestAssured;
import com.opencsv.CSVReader;
import com.tata.selenium.constants.ApplicationConstants;

public class CSVUtil {

	private static final Logger LOGGER = Logger.getLogger(CSVUtil.class.getName());

	List<String[]> allLines;
//	Map<String,String> returnMap = new HashMap<>();
//	List<String> returnList = new ArrayList<>();
	int headerRowNo;

	public CSVUtil(String fileStr, int headerRow) 
	{
		CSVReader csvReader;
		try {
			
			if(ApplicationConstants.RUN_IN_REMOTE)
				fileStr = CommonUtils.copyRemoteFileToLocalAndGetLocalPath(fileStr);
			
			csvReader = new CSVReader(new FileReader(fileStr));		
			allLines = csvReader.readAll();
			csvReader.close();
		} catch (IOException e) {
			LOGGER.info("error in getSingleColAllData method: ", e);
			Assert.fail(e.getMessage());
		}	
		this.headerRowNo = headerRow-1;		
	}

	public Map<String,String> getData(String columnNameForReferance ,String columnValue)
	{	
		Map<String,String> returnMap = new LinkedHashMap<>();
		String[] headerLine = allLines.get(headerRowNo);
		int colNum;
		try {
			colNum = getColoumnName(headerLine, columnNameForReferance);

			int dataRowNum = -1;
			for(int i=headerRowNo+1;i<allLines.size();i++)
			{
				String currColVal = allLines.get(i)[colNum];
				if(currColVal.equals(columnValue))
				{
					dataRowNum =i;
					break;
				}
			}
			String[] dataLine = allLines.get(dataRowNum);
			returnMap = getData(headerLine, dataLine);				
		} catch (Exception e) {
			LOGGER.info("error in CSVUtil.getData method", e);
		}
		return returnMap;

	}

	public List<String> getSingleColAllData(String columnNameForReferance) {
		List<String> returnList = new LinkedList<>();
		String[] headerLine = allLines.get(headerRowNo);

		try {
			int colNum = getColoumnName(headerLine, columnNameForReferance);
			for (int i = headerRowNo + 1; i < allLines.size(); i++) {
				returnList.add(allLines.get(i)[colNum]);
			}
		} catch (Exception e) {
			LOGGER.info("error in getSingleColAllData method: ", e);
		}
		return returnList;
	}


	private int getColoumnName(String[] headerLine, String columnName)
	{

		int flag = -1;
		boolean isfound = false;
		for(int i=0;i<headerLine.length;i++)
		{
			String currColName = headerLine[i];
			if(currColName.trim().equals(columnName.trim()))
			{
				flag = i;
				isfound= true;
				break;
			}
		}
		if(!isfound)
			throw new ColumnNameNotFoundException("No such colunm exists ", columnName);

		return flag;
	}


	private Map<String, String> getData(String[] headerLine, String[] dataLine) 
	{
		Map<String,String> returnMap = new LinkedHashMap<>();		
		for(int i=0; i<headerLine.length; i++)
		{
			try{
				String key = headerLine[i].trim();
				String val = dataLine[i].trim();
				returnMap.put(key, val) ;
			}catch(Exception e)
			{
				LOGGER.info("problem with headerLine or dataLine", e);
				returnMap.put(headerLine[i].trim(), "");
			}
		}
		return returnMap;
	}

	public int getSingleColSum(List<String> getSingleColAllData)  {
		int sum = 0;

		List<Integer> intList = new ArrayList<>();
		for (String s : getSingleColAllData) {
			intList.add(Integer.valueOf(s));
		}
		int len = intList.size();
		for (int i = 0; i < len; i++) {
			sum += intList.get(i);
		}

		return sum;
	}




public ArrayList<String> readCSVToList(String newFile)
{
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
			if (crunchifyBuffer != null) crunchifyBuffer.close();
		} catch (IOException crunchifyException) {
			crunchifyException.printStackTrace();
		}
	}
	return CSVData;
}

// Utility which converts CSV to ArrayList using Split Operation
public static ArrayList<String> crunchifyCSVtoArrayList(String crunchifyCSV) {
	ArrayList<String> crunchifyResult = new ArrayList<String>();

	if (crunchifyCSV != null)
	{
		String[] splitData = crunchifyCSV.split("\\s*,\\s*");
		for (int i = 0; i < splitData.length; i++)
		{
			if (!(splitData[i] == null) || !(splitData[i].length() == 0))
			{
				crunchifyResult.add(splitData[i].trim());
			}
		}
	}

	return crunchifyResult;
}

public List<Map<String,String>> getAllRowData()
{		
	
	List<Map<String,String>> returnList = new LinkedList<>();
	try {
		String[] headerLine = allLines.get(headerRowNo);
		
		for(int i=1;i<allLines.size();i++)
		{
			String[] dataLine = allLines.get(i);
			returnList.add(getData(headerLine, dataLine));		
		}
	} catch (Exception e) {
		LOGGER.info("error in CSVUtil.getAllRowData method", e);
	}
	
	return returnList;

}

}
