package com.tata.selenium.utils;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.HTMLLayout;
import org.apache.log4j.helpers.Transform;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.LoggingEvent;

import com.opencsv.CSVWriter;


public class Log4jCustomCSVLayout extends HTMLLayout{
	

	 @Override
	 public
	 String format(LoggingEvent event) {
		 StringWriter strwr = new StringWriter();
	   if(strwr.getBuffer().capacity() > MAX_CAPACITY) {
	     strwr = new StringWriter(BUF_SIZE);
	   } else {
	     //sbuf.setLength(0);
	   }

	   CSVWriter csvWR = new CSVWriter(strwr);
	   	   
	   
	   String eventTime = Long.toString((event.timeStamp - LoggingEvent.getStartTime())/1000);
	   String thread = Transform.escapeTags(event.getThreadName());
	   String threadid = Transform.escapeTags(Long.toString(Thread.currentThread().getId()));
	   String level = Transform.escapeTags(String.valueOf(event.getLevel()));
	   String category = Transform.escapeTags(event.getLoggerName());
	   LocationInfo locInfo = event.getLocationInformation();
	   String fileline = Transform.escapeTags(locInfo.getFileName()+":"+locInfo.getLineNumber());
	   String message = Transform.escapeTags(event.getRenderedMessage());
	   
	   String ndc = "";
	   if (event.getNDC() != null) {
	   ndc = "NDC: " + Transform.escapeTags(event.getNDC());
	   }
	   
	  String[] rowLine = new String[]{new SimpleDateFormat("MMM/dd/yyyy HH:mm:ss.SSS").format(new Date()), eventTime, thread, threadid, level, category, fileline, message, ndc};
	   
	   String[] s = event.getThrowableStrRep();
	   if(s != null && s.length != 0)
		   rowLine = ArrayUtils.addAll(rowLine, s);
	   
	   csvWR.writeNext(rowLine);
	   
	   return strwr.toString();
	 }
	 
	 
	 @Override
	 public
	 String getHeader() {
		 
		 StringWriter strwr = new StringWriter();
		 CSVWriter csvWR = new CSVWriter(strwr);
		 
		 csvWR.writeNext(new String[]{"Timestamp", "Event time (seconds)","Thread", "Thread-id", "Level", "Category", "File:Line", "Message"});
		 
	   return strwr.toString();
	 }
	 
	 

}
