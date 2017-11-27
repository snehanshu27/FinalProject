package com.tata.selenium.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.*;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.tata.selenium.constants.ApplicationConstants;



/**
 * @date 
 * @author Devbrath Singh
 * @description This class is Util class for reading and writing data from an excel source.
 */

public class ExcelUtils implements ApplicationConstants {
	
	private static final Logger LOGGER = Logger.getLogger(ExcelUtils.class.getName());

	private XSSFSheet excelWSheet;
	private XSSFWorkbook excelWBook;
	private XSSFCell cellGlobal;
	private XSSFRow rowGlobal;
    int reqcellrowno;
	int reqcellcolno;
	
	PropertyUtility putility = new PropertyUtility(OBJECT_REPO_FILEPATH);
	
	public void setExcelFile(String path, String sheetName) throws Exception {
		try {
			// Open the Excel file
			FileInputStream excelFile = new FileInputStream(path);
			// Access the required test data sheet
			excelWBook = new XSSFWorkbook(excelFile);
			excelWSheet = excelWBook.getSheet(sheetName);
		} catch (Exception e) {
			LOGGER.error("Error while intializing the excel file " + e);
			CommonUtils.printConsole("Error while intializing the excel file " + e);
		}
	}

	// This method is to write in the Excel cell, Row num and Col num are the
	// parameters

	public void setCellData(String sheetName, String cellValue, int rowNum, int colNum) throws Exception {
		try {
			InputStream inp = new FileInputStream(DATA_FILEPATH);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheet(sheetName);

			Row row = sheet.getRow(rowNum);
			Cell cellWrite = row.getCell(colNum);
			if (cellWrite == null) {
				cellWrite = row.createCell(colNum);
				cellWrite.setCellValue(cellValue);
			} else {
				cellWrite.setCellValue(cellValue);
			}
			FileOutputStream fileOut = new FileOutputStream(DATA_FILEPATH);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			LOGGER.info("Error while setting the cell data by setCellData(Str1, Str2, int1, int2) for column : "+colNum+" and row : "+rowNum+" Exception : " +e);
			CommonUtils.printConsole("Error while setting the cell data by setCellData(Str1, Str2, int1, int2) for column : "+colNum+" and row : "+rowNum+" Exception : " + e.getMessage());
		}
	}

	public void setCellData(Sheet mySheet, String cellValue, int rowNum, int colNum) throws Exception {
		try {
			Row row = mySheet.getRow(rowNum);
			Cell cellWrite = row.getCell(colNum);
			if (cellWrite == null) {
				cellWrite = row.createCell(colNum);
				cellWrite.setCellValue(cellValue);
			} else {
				cellWrite.setCellValue(cellValue);
				
			}
		} catch (Exception e) {
			LOGGER.info("Error while setting the cell data by setCellData(Sheet, Str1, int1, int2) for column : "+colNum+" and row : "+rowNum+" Exception : " + e);
			CommonUtils.printConsole("Error while setting the cell data by setCellData(Sheet, Str1, int1, int2) for column : "+colNum+" and row : "+rowNum+" Exception : " + e.getMessage());
		}
	}

	public void setCellData(Sheet sheet, String cellValue, String rowName, String colName) throws Exception {
		try {
			int rowNum = 0;
			int colNum = 0;
			Row row = sheet.getRow(0);

			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				Cell c = rowLoop.getCell(0);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcaserowname = c.getStringCellValue();
				if (rowName.equalsIgnoreCase(testcaserowname)) {
					rowNum = i;
					break;
				}
			}
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell c = row.getCell(j);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcasecellname = c.getStringCellValue();
				if (colName.equalsIgnoreCase(testcasecellname)) {
					colNum = j;
					break;
				}
			}
			Cell cellWrite = sheet.getRow(rowNum).getCell(colNum);
			if (cellWrite == null) {
				cellWrite = sheet.getRow(rowNum).createCell(colNum);
				cellWrite.setCellValue(cellValue);
			} else {
				cellWrite.setCellValue(cellValue);
			}
		} catch (Exception e) {
			LOGGER.info("Error while setting the cell data by setCellData(Sheet, Str1, Str2, Str3) for column : "+colName+" and row : "+rowName+" Exception : " + e);
			CommonUtils.printConsole("Error while setting the cell data by setCellData(Sheet, Str1, Str2, Str3) for column : "+colName+" and row : "+rowName+" Exception : " + e.getMessage());
		}
	}

	public void setCellData(String sheetName, String cellValue, String rowName, String colName)  {
		try {/*
			InputStream inp = new FileInputStream(DATA_FILEPATH);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheet(sheetName);
			int rowNum = 0;
			int colNum = 0;
			Row row = sheet.getRow(0);

			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				Cell c = rowLoop.getCell(0);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcaserowname = c.getStringCellValue();
				if (rowName.equalsIgnoreCase(testcaserowname)) {
					rowNum = i;
					break;
				}
			}
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell c = row.getCell(j);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcasecellname = c.getStringCellValue();
				if (colName.equalsIgnoreCase(testcasecellname)) {
					colNum = j;
					break;
				}
			}
			Cell cellWrite = sheet.getRow(rowNum).getCell(colNum);
			if (cellWrite == null) {
				cellWrite = sheet.getRow(rowNum).createCell(colNum);
				cellWrite.setCellValue(cellValue);
			} else {
				cellWrite.setCellValue(cellValue);
			}
			FileOutputStream fileOut = new FileOutputStream(DATA_FILEPATH);
			wb.write(fileOut);
			fileOut.close();
		*/} catch (Exception e) {
			LOGGER.info("Error while setting the cell data by setCellData(Str1, Str2, Str3, Str4) for column : "+colName+" and row : "+rowName+" Exception : " + e);
			CommonUtils.printConsole("Error while setting the cell data by setCellData(Str1, Str2, Str3, Str4) for column : "+colName+" and row : "+rowName+" Exception : " + e.getMessage());
		}
	}

	public String getExcelData(String columnname, String rowname)  {
		List<String> testcasenamedata = new ArrayList<>();
		List<String> colnamedata = new ArrayList<>();
		int writtenrowno = excelWSheet.getPhysicalNumberOfRows();
		boolean colFound = false;
		boolean rowFound = false;	
		String cellData = "";
		try{
			rowGlobal = excelWSheet.getRow(0);
			int writtencolno = rowGlobal.getLastCellNum();
			for (int i = 0; i < writtenrowno; i++) {
				rowGlobal = excelWSheet.getRow(i);
				cellGlobal = rowGlobal.getCell(0);
				if (cellGlobal != null) {
					testcasenamedata.add(cellGlobal.getStringCellValue());
					if (rowname.equalsIgnoreCase(testcasenamedata.get(i))) {
						reqcellrowno = i;
						rowFound = true;
						break;
					}
				}
			}
			if (rowFound) {
				for (int j = 0; j < writtencolno; j++) {
					rowGlobal = excelWSheet.getRow(0);
					cellGlobal = rowGlobal.getCell(j);
					if (cellGlobal != null) {
						colnamedata.add(cellGlobal.getStringCellValue());
						if (columnname.equalsIgnoreCase(colnamedata.get(j))) {
							reqcellcolno = j;
							colFound = true;
							break;
						}
					}
				}
			}
			if (colFound) {
				cellGlobal = excelWSheet.getRow(reqcellrowno).getCell(reqcellcolno);
				cellGlobal.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				cellData = cellGlobal.getStringCellValue();
			}
			return cellData;
		}
		catch (Exception e) {
			LOGGER.info("Error while getting the cell data by getExcelData(Str1,Str2) for column : "+columnname+" and row : "+rowname+" Exception : " + e);
			CommonUtils.printConsole("Error while getting the cell data by getExcelData(Str1,Str2) for column : "+columnname+" and row : "+rowname+" Exception : " + e.getMessage());
			return cellData;
		}		
	}

	public String getExcelData(String sheetName, String columnname, String rowname) throws Exception {
		InputStream inp = new FileInputStream(DATA_FILEPATH);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet = wb.getSheet(sheetName);
		String cellData = "";
		int rowNum = 0;
		int colNum = 0;
		boolean colFound = false;
		boolean rowFound = false;
		try{
			Row row = sheet.getRow(0);
	
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				if (rowLoop != null){
					Cell c = rowLoop.getCell(0);
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String testcaserowname = c.getStringCellValue();
						if (rowname.equalsIgnoreCase(testcaserowname)) {
							rowNum = i;
							rowFound = true;
							break;
						}
					}
				}
			}
			if (rowFound) {
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell c = row.getCell(j);
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String testcasecellname = c.getStringCellValue();
						if (columnname.equalsIgnoreCase(testcasecellname)) {
							colNum = j;
							colFound = true;
							break;
						}
					}
				}
			}
			if (colFound) {
				Cell cell = sheet.getRow(rowNum).getCell(colNum);
				cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				cellData = cell.getStringCellValue();
			}
			return cellData;
		}
		catch (Exception e) {
			LOGGER.info("Error while getting the cell data by getExcelData(Str1,Str2,Str3) for column : "+columnname+" and row : "+rowname+" Exception : " + e);
			CommonUtils.printConsole("Error while getting the cell data by getExcelData(Str1,Str2,Str3) for column : "+columnname+" and row : "+rowname+" Exception : " + e.getMessage());
			return cellData;
		}		
	}

	public String getExcelData(Sheet sheet, String rowName, String colName) throws Exception {
		String cellData = "";
		try {
			int rowNum = 0;
			int colNum = 0;
			boolean colFound = false;
			boolean rowFound = false;
			Row row = sheet.getRow(0);
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				if (rowLoop != null){
					Cell c = rowLoop.getCell(0);
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String testcaserowname = c.getStringCellValue();
						if (rowName.equalsIgnoreCase(testcaserowname)) {
							rowNum = i;
							rowFound = true;
							break;
						}
					}
				}
			}
			if (rowFound) {
				for (int j = 0; j < row.getLastCellNum(); j++) {
					Cell c = row.getCell(j);
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String testcasecellname = c.getStringCellValue();
						if (colName.equalsIgnoreCase(testcasecellname)) {
							colNum = j;
							colFound = true;
							break;
						}
					}
				}
			}
			if (colFound) {
				Cell cell = sheet.getRow(rowNum).getCell(colNum);
				cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				cellData = cell.getStringCellValue();
			}
			return cellData;
		} 
		catch (Exception e) {
			LOGGER.info("Error while getting the cell data by getExcelData(Sheet,Str1,Str2) for sheet : "+sheet.getSheetName() +" for column : "+colName+" and row : "+rowName+" Exception : " + e);
			CommonUtils.printConsole("Error while getting the cell data by getExcelData(Sheet,Str1,Str2) for sheet : "+sheet.getSheetName() +" for column : "+colName+" and row : "+rowName+" Exception : " + e.getMessage());
			return cellData;
		}
	}

	public List<String> getCellCount(Sheet mySheet) throws Exception {
		List<String> testCases = new ArrayList<>();
		try {
			int i = 1;
			boolean loop = true;
			while (loop) {
				Row row = mySheet.getRow(i);
				if (row == null)
					loop = false;
				else {
					Cell cellWrite = row.getCell(0);
					if (cellWrite == null)
						loop = false;
					else {
						cellWrite.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						testCases.add(cellWrite.getStringCellValue());
					}
				}
				i++;
			}
			return testCases;
		} catch (Exception e) {
			LOGGER.info("Error while getting the cell data by getCellCount(Sheet) Exception : " + e);
			CommonUtils.printConsole("Error while getting the cell data by getCellCount(Sheet) Exception : " + e.getMessage());
			return testCases;
		}
	}
	
	public void clearResultData(String sheetName, String resultStatus, String resultError, String resultJO) throws Exception {
		InputStream inp = new FileInputStream(DATA_FILEPATH);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet = wb.getSheet(sheetName);
		List<String> testCases = getCellCount(sheet);
		int colResultStatus = 0 ;
		int colResultError = 0;
		int colResultJO = 0;
		Row row = sheet.getRow(0);
		
		if(resultJO !=null && resultJO.trim().length() > 0){
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell c = row.getCell(j);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcasecellname = c.getStringCellValue();
				if (resultJO.equalsIgnoreCase(testcasecellname)) {
					colResultJO = j;
					break;
				}
			}
		}
		
		if(resultStatus !=null && resultStatus.trim().length() > 0){
			for (int i = 0; i < row.getLastCellNum(); i++) {
				Cell c = row.getCell(i);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcasecellname = c.getStringCellValue();
				if (resultStatus.equalsIgnoreCase(testcasecellname)) {
					colResultStatus = i;
					break;
				}
			}
		}
		
		if(resultError !=null && resultError.trim().length() > 0){
			for (int j = 0; j < row.getLastCellNum(); j++) {
				Cell c = row.getCell(j);
				c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				String testcasecellname = c.getStringCellValue();
				if (resultError.equalsIgnoreCase(testcasecellname)) {
					colResultError = j;
					break;
				}
			}
		}
		if (colResultStatus > 0){
			for (int k = 1; k <= testCases.size(); k++) {
				Cell cellResultStatus = sheet.getRow(k).getCell(colResultStatus); 
				if (cellResultStatus != null)
					cellResultStatus.setCellValue("");				
			}				
		}			
		if (colResultError > 0){
			for (int k = 1; k <= testCases.size(); k++) {
				Cell cellResultError = sheet.getRow(k).getCell(colResultError);
				if (cellResultError != null)
					cellResultError.setCellValue("");					
			}
			
		}
		if (colResultJO > 0){
			for (int k = 1; k <= testCases.size(); k++) {
				Cell cellResultJO = sheet.getRow(k).getCell(colResultJO); 
				if (cellResultJO != null)
					cellResultJO.setCellValue("");				
			}				
		}
		
		FileOutputStream fileOut = new FileOutputStream(DATA_FILEPATH);
		wb.write(fileOut);
		fileOut.close();
	}
	
	
	public String getExcelDate(String columnname,String rowname) throws Exception
    {
    	List<String> testcasenamedata = new ArrayList<>();
    	List<String> colnamedata = new ArrayList<>();
    	int writtenrowno= excelWSheet.getPhysicalNumberOfRows();
    	rowGlobal =  excelWSheet.getRow(0);
    	int writtencolno= rowGlobal.getLastCellNum();
    	Date cellData = null;
    	String reportDate;
    	for(int i=0;i<writtenrowno;i++)
     	{
    		rowGlobal =  excelWSheet.getRow(i);
     		cellGlobal = rowGlobal.getCell(0);
     		testcasenamedata.add(cellGlobal.getStringCellValue());
     		if(rowname.equalsIgnoreCase(testcasenamedata.get(i)))
     		{
     			reqcellrowno=i;
     			break;
     		}
     	}
     	for(int j=0;j<writtencolno;j++)
     	{
     		rowGlobal =  excelWSheet.getRow(0);
     		cellGlobal= rowGlobal.getCell(j);
     		colnamedata.add(cellGlobal.getStringCellValue());
     		if(columnname.equalsIgnoreCase(colnamedata.get(j)))
     		{
     			reqcellcolno=j;
     			break;
     		}
     	}

     	cellGlobal = excelWSheet.getRow(reqcellrowno).getCell(reqcellcolno);
     	if (DateUtil.isCellDateFormatted(cellGlobal)) {
           cellData = cellGlobal.getDateCellValue();             
           } 

     	if(cellData==null)
     	{
     		reportDate = null;	
     	}
     	else
     	{
     		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		reportDate = df.format(cellData);
     	}	
     	return reportDate;
    }
	
	public String getQuestionAnswer(String question) throws Exception {
		InputStream inp = new FileInputStream(DATA_FILEPATH);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet = wb.getSheet(putility.getProperty("QUESTION_ANSWER_SHEET").trim());
		String answer = "NULL";
		int rowNum = 0;
		boolean rowFound = false;
		try{
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				if (rowLoop != null){
					Cell c = rowLoop.getCell(new Integer(putility.getProperty("COLUMN_NUMBER_QUESTION").trim()).intValue());
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String rowQuestion = c.getStringCellValue().trim();
						if (question.trim().equalsIgnoreCase(rowQuestion)) {
							rowNum = i;
							rowFound = true;
							break;
						}
					}
				}
			}
			if (rowFound) {
				Cell cell = sheet.getRow(rowNum).getCell(new Integer(putility.getProperty("COLUMN_NUMBER_ANSWER").trim()).intValue());
				cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
				answer = cell.getStringCellValue();
			}
			return answer;
		}
		catch (Exception e) {
			LOGGER.info("Error while getting the answer by getQuestionAnswer(Str1) for question : "+question+" Exception : " + e);
			CommonUtils.printConsole("Error while getting the answer by getQuestionAnswer(Str1) for question : "+question+" Exception : " + e.getMessage());
			return answer;
		}		
	} 
	
	
	
	public List<String> getAllAnswer(String question) throws Exception {
		List <String> answers = new ArrayList<>() ;
		InputStream inp = new FileInputStream(DATA_FILEPATH);
		Workbook wb = WorkbookFactory.create(inp);
		Sheet sheet = wb.getSheet(putility.getProperty("QUESTION_ANSWER_SHEET"));
		try{
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row rowLoop = sheet.getRow(i);
				if (rowLoop != null){
					Cell c = rowLoop.getCell(new Integer(putility.getProperty("COLUMN_NUMBER_QUESTION")).intValue());
					if (c != null) {
						c.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
						String rowQuestion = c.getStringCellValue().trim();
						if (question.trim().equalsIgnoreCase(rowQuestion)) {
							Cell cell = sheet.getRow(i).getCell(new Integer(putility.getProperty("COLUMN_NUMBER_ANSWER")).intValue());
							cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
							String answer = cell.getStringCellValue();
							answers.add(answer);
						}
					}
				}
			}
			return answers;
		}
		catch (Exception e) {
			LOGGER.info("Error while getting the answer by getAllAnswer(Str1) for question : "+question+" Exception : " + e);
			CommonUtils.printConsole("Error while getting the answer by getAllAnswer(Str1) for question : "+question+" Exception : " + e.getMessage());
			return answers;
		}		
	}	

	public boolean isAnswerExists(String answerToCompare, List<String> allAnswers) throws Exception {
		boolean result = false;
		try{
			if (allAnswers != null && !allAnswers.isEmpty() && answerToCompare != null && !answerToCompare.isEmpty()){
				for (int i = 0; i < allAnswers.size(); i++) {
					if(allAnswers.get(i).equalsIgnoreCase(answerToCompare)){
						result = true;
						break;
					}
				}
			}
			return result;
		}
		catch (Exception e) {
			LOGGER.info("Error while checking the answer by isAnswerExists(Str1, ArrayList<Str>) for answer : "+answerToCompare+" Exception : " + e);
			CommonUtils.printConsole("Error while checking the answer by isAnswerExists(Str1, ArrayList<Str>) for answer : "+answerToCompare+" Exception : " + e.getMessage());
			return result;
		}		
	}	 
	public Map<String, String> getSheetData(int rownum, String sheetName) {
		final List<String> rowData = new ArrayList<>();
		final Map<String, String> rowVal = new LinkedHashMap<>();
		Object value;
		final Sheet sheet = getSheet(sheetName);
		final List<String> coulmnNames = getColumns(sheet);
		final Row row = sheet.getRow(rownum);
		final int firstCellNum = row.getFirstCellNum();
		final int lastCellNum = row.getLastCellNum();
		for (int j = firstCellNum; j < lastCellNum; j++) {
			final Cell cell = row.getCell(j);
			if (cell == null || cell.getCellType() == cellGlobal.CELL_TYPE_BLANK) {
				rowData.add("");
			} else if (cell.getCellType() == cellGlobal.CELL_TYPE_NUMERIC) {
				final Double val = cell.getNumericCellValue();
				value = val.intValue();
				rowData.add(value.toString());
			} else if (cell.getCellType() == cellGlobal.CELL_TYPE_STRING) {
				rowData.add(cell.getStringCellValue());
			} else if (cell.getCellType() == cellGlobal.CELL_TYPE_BOOLEAN
					|| cell.getCellType() == cellGlobal.CELL_TYPE_ERROR
					|| cell.getCellType() == cellGlobal.CELL_TYPE_FORMULA) {
				throw new CellTypeNotSupportedException(" Cell Type is not supported ");
			}
			rowVal.put(coulmnNames.get(j), rowData.get(j));
		}
		return rowVal;

	}

	private Sheet getSheet(String sheetName)
	{
		return excelWBook.getSheet(sheetName);
	}
	
	private List<String> getColumns(Sheet sheet) {
		final Row row = sheet.getRow(0);
		final List<String> columnValues = new ArrayList<>();
		final int firstCellNum = row.getFirstCellNum();
		final int lastCellNum = row.getLastCellNum();
		for (int i = firstCellNum; i < lastCellNum; i++) {
			final Cell cell = row.getCell(i);
			columnValues.add(cell.getStringCellValue());
		}
		return columnValues;
	}
	
	public Map<String, String> getSheetData(String tcID, String sheetName) {
		System.out.println("TCID passed is"+tcID);
		System.out.println("SheetName passed is"+sheetName);
		final List<String> rowData = new ArrayList<>();
		final LinkedHashMap<String, String> rowVal = new LinkedHashMap<>();
		Object value;
		final Sheet sheet = getSheet(sheetName);
		final List<String> coulmnNames = getColumns(sheet);
		final int totalRows = sheet.getPhysicalNumberOfRows();
		final Row row = sheet.getRow(0);
		final int firstCellNum = row.getFirstCellNum();
		final int lastCellNum = row.getLastCellNum();
		for (int i = 1; i < totalRows; i++) {
			final Row rows = sheet.getRow(i);
			final String testLinkID = rows.getCell(0).getStringCellValue();
			if (tcID.equalsIgnoreCase(testLinkID)) {
				for (int j = firstCellNum; j < lastCellNum; j++) {
					final Cell cell = rows.getCell(j);
					if (cell == null
							|| cell.getCellType() == cellGlobal.CELL_TYPE_BLANK) {
						rowData.add("");
					} else if (cell.getCellType() == cellGlobal.CELL_TYPE_NUMERIC) {
						final Double val = cell.getNumericCellValue();
						value = val.longValue();
						rowData.add(value.toString());
					} else if (cell.getCellType() == cellGlobal.CELL_TYPE_STRING) {
						rowData.add(cell.getStringCellValue());
					} else if (cell.getCellType() == cellGlobal.CELL_TYPE_FORMULA) {
						rowData.add(cell.getStringCellValue());
					} else if (DateUtil.isCellDateFormatted(cell)) {
						rowData.add(cell.getDateCellValue().toString());
					} else if (cell.getCellType() == cellGlobal.CELL_TYPE_BOOLEAN
							|| cell.getCellType() == cellGlobal.CELL_TYPE_ERROR
							|| cell.getCellType() == cellGlobal.CELL_TYPE_FORMULA) {
						throw new CellTypeNotSupportedException(" Cell Type is not supported ");
					}
					rowVal.put(coulmnNames.get(j), rowData.get(j).trim());

				}
				break;
			}

		}
		return rowVal;

	}
	
	 public List<Map<String, String>> getSheetAllData(String sheetName) {
		  
		  List<Map<String, String>> ret = new LinkedList<>();
		  Object value;
		  final Sheet sheet = getSheet(sheetName);
		  final List<String> coulmnNames = getColumns(sheet);
		  final int totalRows = sheet.getPhysicalNumberOfRows();
		  final Row row = sheet.getRow(0);
		  final int firstCellNum = row.getFirstCellNum();
		  final int lastCellNum = row.getLastCellNum();
		  for (int i = 1; i < totalRows; i++) {
		   LinkedHashMap<String, String> rowVal = new LinkedHashMap<>();
		   final Row rows = sheet.getRow(i);
		   final List<String> rowData = new LinkedList<>();   
		    for (int j = firstCellNum; j < lastCellNum; j++) {
		     final Cell cell = rows.getCell(j);
		     if (cell == null
		       || cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
		      rowData.add("");
		     } else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
		      final Double val = cell.getNumericCellValue();
		      value = val.intValue();
		      rowData.add(value.toString());
		     } else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
		      rowData.add(cell.getStringCellValue());
		     } else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
		      rowData.add(cell.getStringCellValue());
		     } else if (DateUtil.isCellDateFormatted(cell)) {
		      rowData.add(cell.getDateCellValue().toString());
		     } else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN
		       || cell.getCellType() == XSSFCell.CELL_TYPE_ERROR
		       || cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
		      throw new RuntimeException(" Cell Type is not supported ");
		     }
		     rowVal.put(coulmnNames.get(j), rowData.get(j).trim());
		    }
		    ret.add(rowVal);   

		  }
		  return ret;

		 }
} 
