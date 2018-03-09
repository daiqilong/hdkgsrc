package com.whir.service.parse;

import java.io.IOException;
import java.sql.SQLException;

import org.jdom.Document;

import com.whir.portal.module.actionsupport.StatisticsMapAction;
import com.whir.service.common.AbstractParse;

public class ControlRunParse extends AbstractParse{
	public ControlRunParse(Document doc){
	    super(doc);
	}
	public String getActualUpDownZSSSFlight() throws SQLException, IOException
	  {
			System.out.println("获取getActualUpDownZSSSFlight::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    StatisticsMapAction statisticsMapAction = new StatisticsMapAction();
		    int recordCount = statisticsMapAction.getActualUpDownZSSSFlight();
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getActualUpDownZSPDFlight() throws SQLException, IOException
	  {
			System.out.println("获取getActualUpDownZSPDFlight::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    StatisticsMapAction statisticsMapAction = new StatisticsMapAction();
		    int recordCount = statisticsMapAction.getActualUpDownZSPDFlight();
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getRealTimeFlow() throws SQLException, IOException
	  {
			System.out.println("获取getRealTimeFlow::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    StatisticsMapAction statisticsMapAction = new StatisticsMapAction();
		    String record = statisticsMapAction.getWxRealTimeFlow();
		    
		    result += "<result>";
		    result += "<record><![CDATA[" + record +"]]></record>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	
	public String getFlightDelay() throws SQLException, IOException
	  {
			System.out.println("获取getFlightDelay::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    StatisticsMapAction statisticsMapAction = new StatisticsMapAction();
		    String record = statisticsMapAction.getWxFlightDelay();
		    
		    result += "<result>";
		    result += "<record><![CDATA[" + record +"]]></record>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getSevenActualUpDownFlight() throws SQLException, IOException
	  {
			System.out.println("获取getSevenActualUpDownFlight::");
			String result = "";
		    String message = "数据传输成功。";
		    
		    StatisticsMapAction statisticsMapAction = new StatisticsMapAction();
		    String record = statisticsMapAction.getSevenActualUpDownFlight();
		    
		    result += "<result>";
		    result += "<record><![CDATA[" + record +"]]></record>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
}