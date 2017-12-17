package com.tata.selenium.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.opencsv.CSVReader;

public class CSVUtil1 {
	
	private static final Logger LOGGER = Logger.getLogger(CSVUtil.class.getName());
	
	List<String[]> allLines;
	Map<String,String> returnMap = new HashMap<>();
	List<Map<String,String>> returnMapMulRows = new ArrayList<>();
	List<String> returnList = new ArrayList<>();
	int headerRowNo;
	
	public CSVUtil1(String fileStr, int headerRow) 
	{
		CSVReader csvReader;
		try {
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
		returnMap.clear();
		String[] headerLine = allLines.get(headerRowNo);
		int colNum;
		try {
			colNum = getColoumnName(headerLine, columnNameForReferance);
		
			int dataRowNum = -1;
			for(int i=0;i<allLines.size();i++)
			{
				if(allLines.get(i)[colNum].equals(columnValue))
					dataRowNum =i;
			}
			String[] dataLine = allLines.get(dataRowNum);
			returnMap = getData(headerLine, dataLine);				
		} catch (Exception e) {
			LOGGER.info("error in CSVUtil.getData method", e);
		}
		return returnMap;		
	}
	
	public List<Map<String, String>> getDataMultipleRows(String columnNameForReferance ,String columnValue)
	{		
		returnMapMulRows.clear();
		String[] headerLine = allLines.get(headerRowNo);
		int colNum;
		try {
			colNum = getColoumnName(headerLine, columnNameForReferance);
		
			int dataRowNum = -1;
			for(int i=0;i<allLines.size();i++)
			{
				if(allLines.get(i)[colNum].equals(columnValue)){					
					String[] dataLine = allLines.get(i);
					returnMapMulRows.add(getData(headerLine, dataLine));
				}
			}				
		} catch (Exception e) {
			LOGGER.info("error in CSVUtil.getDataMultipleRows method", e);
		}
		return returnMapMulRows;		
	}
	
	public List<List<String>> getAllRowsData()
	{		
		List<List<String>> ret = new ArrayList<>();
		try {
			for(int i=headerRowNo+1;i<allLines.size();i++)
			{
				ret.add(Arrays.asList(allLines.get(i)));
			}				
		} catch (Exception e) {
			LOGGER.info("error in CSVUtil.getAllRowsData method", e);
		}
		return ret;		
	}
	
	public List<String> getSingleColAllData(String columnNameForReferance) {
		  returnList.clear();
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
		Map<String, String> ret = new HashMap<>();
		for(int i=0; i<headerLine.length; i++)
		{
			try{
				ret.put(headerLine[i], dataLine[i]);
			}catch(Exception e)
			 {
				LOGGER.info("problem with headerLine or dataLine", e);
				ret.put(headerLine[i], "");
			 }
		}
		return ret;
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
	
	
}
