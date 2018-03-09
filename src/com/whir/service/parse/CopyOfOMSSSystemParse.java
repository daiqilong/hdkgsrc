package com.whir.service.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.common.util.DataSourceBase;
import com.whir.service.common.AbstractParse;

public class CopyOfOMSSSystemParse extends AbstractParse{
	public CopyOfOMSSSystemParse(Document doc){
	    super(doc);
	}
	public String saveDutyInformation()
	  {
		System.out.println("-------保存值班信息-------");
		String result = "";
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Element rootElement = this.doc.getRootElement();
		
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    
	    System.out.println("-------进入值班信息接口-------");
	    List<Element> dataList = rootElement.getChildren("data");
      if ((dataList != null) && (dataList.size() > 0))
        for (int i = 0; i < dataList.size(); i++) {
          Element dataElement = (Element)dataList.get(i);
          List methodList = dataElement.getChildren("list");
          if ((methodList != null) && (methodList.size() > 0))
            for (int j = 0; j < methodList.size(); j++) {
              Element methodElement = (Element)methodList.get(j);
              Map<String,Object> masterMap = new HashMap<String,Object>();
              for(int m=0;m<methodElement.getContentSize();m++){
              	String nodeName = methodElement.getContent(m).toString();
              	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
              	masterMap.put(keyName, methodElement.getContent(m).getValue());
              }
              masterMap.put("subject",subject);
              list.add(masterMap);
            }
        }
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    PreparedStatement pst1 = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
			pst = conn.prepareStatement("INSERT INTO DUTY_INFORMATION (ID,SUBJECT,TYPE,NAME,DATETIME) values (SEQ_DUTY_INFORMATION.NEXTVAL,?,?,?,?)");
			for (Map<String,Object> map:list) {
			        pst.setString(1, map.get("subject").toString());  
			        if(map.get("type")!=null ||"".equals(map.get("type"))){
			        	pst.setString(2, map.get("type").toString());   
			        } else {
			        	pst.setString(2, "");   
			        }
			        if(map.get("name")!=null ||"".equals(map.get("name"))){
			        	pst.setString(3, map.get("name").toString());   
			        } else {
			        	pst.setString(3, "");   
			        }
			        if(map.get("time")!=null ||"".equals(map.get("time"))){
			        	pst.setString(4, map.get("time").toString());   
			        } else {
			        	pst.setString(4, "");   
			        }
			        // 把一个SQL命令加入命令列表   
			        pst.addBatch();   
			 }  
		    // 执行批量更新   
			 pst.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    setMessage(code+"", message);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(-1+"", "数据传输不成功。");
		} finally{
      	if(pst != null){
      	try
      	{
      		pst.close();
      	}catch(Exception e)
      	{
      	}
      	}
      	if(pst1 != null){
          	try
          	{
          		pst1.close();
          	}catch(Exception e)
          	{
          	}
          	}
      	if(conn != null){
      	try
      	{
      	conn.close();
      	}catch(Exception e){
      		
      	}
      	}
      } 
      return result;
	 } 
	
	
	public String saveVideoMeeting()
	  {
		System.out.println("-------进入视频会议接口-------");
		String result = "";
		String strDt = "";
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> masterList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>();
		
		Element rootElement = this.doc.getRootElement();
		
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    System.out.println("-------date-------"+date);
	    strDt = date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
	    
	    System.out.println("-------进入视频会议接口-------");
	    List<Element> dataList = rootElement.getChildren("data");
        if ((dataList != null) && (dataList.size() > 0))
          for (int i = 0; i < dataList.size(); i++) {
            Element dataElement = (Element)dataList.get(i);
            List methodList = dataElement.getChildren("list");
            if ((methodList != null) && (methodList.size() > 0))
              for (int j = 0; j < methodList.size(); j++) {
                Element methodElement = (Element)methodList.get(j);
                Map<String,Object> masterMap = new HashMap<String,Object>();
                for(int m=0;m<methodElement.getContentSize();m++){
                	String nodeName = methodElement.getContent(m).toString();
                	System.out.println("-------nodeName------"+nodeName);
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	System.out.println("-------数据1-------"+methodElement.getContent(m).getValue());
                	masterMap.put(keyName, methodElement.getContent(m).getValue());
                }
                masterMap.put("subject",subject);
                masterMap.put("date",strDt);
                masterList.add(masterMap);
              }
        }
        
        List<Element> subDataList = rootElement.getChildren("subdata");
        if ((subDataList != null) && (subDataList.size() > 0))
          for (int i = 0; i < subDataList.size(); i++) {
            Element subDataElement = (Element)subDataList.get(i);
            List methodList = subDataElement.getChildren("list");
            if ((methodList != null) && (methodList.size() > 0))
              for (int j = 0; j < methodList.size(); j++) {
                Element methodElement = (Element)methodList.get(j);
                Map<String,Object> subMap = new HashMap<String,Object>();
                for(int m=0;m<methodElement.getContentSize();m++){
                	String nodeName = methodElement.getContent(m).toString();
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	subMap.put(keyName, methodElement.getContent(m).getValue());
                }
                subMap.put("subject",subject);
                subMap.put("date",strDt);
                subList.add(subMap);
              }
        }
        
	    System.out.println("-------主表数据-------"+masterList.size());
	    System.out.println("-------字表数据-------"+subList.size());
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    PreparedStatement pst1 = null;
	    Statement stmt = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String str = "DELETE FROM VIDEO_MEETING WHERE DATETIME="+strDt;
		    String str1 = "DELETE FROM SUB_VIDEO_MEETING WHERE DATETIME="+strDt;
		    System.out.println("-------str------"+str);
		    stmt.executeQuery(str);
		    stmt.executeQuery(str1);
		    System.out.println("-------str1------"+str1);
		    
			pst = conn.prepareStatement("INSERT INTO VIDEO_MEETING (ID,SUBJECT,DATETIME,RUNCONDITION,FACILITY,WEATHER) values (SEQ_VIDEO_MEETING.NEXTVAL,?,?,?,?,?)");
			for (Map<String,Object> map:masterList) {
			        pst.setString(1, map.get("subject").toString());  
			        pst.setString(2, map.get("date").toString());  
			        if(map.get("runCondition")!=null ||"".equals(map.get("runCondition"))){
			        	pst.setString(3, map.get("runCondition").toString());   
			        } else {
			        	pst.setString(3, "");   
			        }
			        if(map.get("facility")!=null ||"".equals(map.get("facility"))){
			        	pst.setString(4, map.get("facility").toString());   
			        } else {
			        	pst.setString(4, "");   
			        }
			        if(map.get("WEATHER")!=null ||"".equals(map.get("WEATHER"))){
			        	pst.setString(5, map.get("WEATHER").toString());   
			        } else {
			        	pst.setString(5, "");   
			        }
			        // 把一个SQL命令加入命令列表   
			        pst.addBatch();   
			 }  
			pst1 = conn.prepareStatement("INSERT INTO SUB_VIDEO_MEETING (ID,SUBJECT,DATETIME,TYPE,NUM," +
					"RELEASEUNIT,RECEIVEUNIT,LIMITPOINT,LIMITTIME,CONTENT,RENSON) values " +
					"(SEQ_SUB_VIDEO_MEETING.NEXTVAL,?,?,?,?,?,?,?,?,?,?)");
			 for (Map<String,Object> map:subList) {
				 	pst1.setString(1, map.get("subject").toString());  
				 	pst1.setString(2, map.get("date").toString());
			        if(map.get("type")!=null ||"".equals(map.get("type"))){
			        	pst1.setString(3, map.get("type").toString());         
			        } else {
			        	pst1.setString(3, "");        
			        }
			        if(map.get("num")!=null ||"".equals(map.get("num"))){
			        	pst1.setString(4, map.get("num").toString());      
			        } else {
			        	pst1.setString(4, "");      
			        }
			        if(map.get("releaseUnit")!=null ||"".equals(map.get("releaseUnit"))){
			        	pst1.setString(5, map.get("releaseUnit").toString()); 
			        } else {
			        	pst1.setString(5, ""); 
			        }
			        if(map.get("receiveUnit")!=null ||"".equals(map.get("receiveUnit"))){
			        	pst1.setString(6, map.get("receiveUnit").toString()); 
			        } else {
			        	pst1.setString(6, ""); 
			        }
			        if(map.get("limitPoint")!=null ||"".equals(map.get("limitPoint"))){
			        	pst1.setString(7, map.get("limitPoint").toString());
			        } else {
			        	pst1.setString(7, "");
			        }
			        if(map.get("limitTime")!=null ||"".equals(map.get("limitTime"))){
			        	pst1.setString(8, map.get("limitTime").toString());
			        } else {
			        	pst1.setString(8, "");
			        }
			        if(map.get("content")!=null ||"".equals(map.get("content"))){
			        	pst1.setString(9, map.get("content").toString());
			        } else {
			        	pst1.setString(9, "");
			        }
			        if(map.get("renson")!=null ||"".equals(map.get("renson"))){
			        	pst1.setString(10, map.get("renson").toString());   
			        } else {
			        	pst1.setString(10, "");
			        }
			        // 把一个SQL命令加入命令列表   
			        pst1.addBatch();   
			 }
		    // 执行批量更新   
			 pst.executeBatch();   
			 pst1.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    setMessage(code+"", message);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(-1+"", "数据传输不成功。");
		} finally{
        	if(pst != null){
        	try
        	{
        		pst.close();
        	}catch(Exception e)
        	{
        	}
        	}
        	if(pst1 != null){
            	try
            	{
            		pst1.close();
            	}catch(Exception e)
            	{
            	}
            	}
        	if(conn != null){
        	try
        	{
        	conn.close();
        	}catch(Exception e){
        		
        	}
        	}
        } 
        return result;
	 }  
	
	
	public String savEeverydayRunSituation() throws ParseException
	  {
		System.out.println("-------进入每日运行情况接口-------");
		String result = "";
		String strDt = "";
		String flagDt = "";
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Element rootElement = this.doc.getRootElement();
		
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    System.out.println("-------date-------"+date);
	    strDt = date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
		String[] flagArray = date.split(" ");
	    if(flagArray.length==2){
	    	flagDt = flagArray[1];
	    }
	    List<Element> dataList = rootElement.getChildren("data");
	    if ((dataList != null) && (dataList.size() > 0))
          for (int i = 0; i < dataList.size(); i++) {
            Element dataElement = (Element)dataList.get(i);
            List methodList = dataElement.getChildren("list");
            if ((methodList != null) && (methodList.size() > 0))
              for (int j = 0; j < methodList.size(); j++) {
                Element methodElement = (Element)methodList.get(j);
                Map<String,Object> masterMap = new HashMap<String,Object>();
                for(int m=0;m<methodElement.getContentSize();m++){
                	String nodeName = methodElement.getContent(m).toString();
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	masterMap.put(keyName, methodElement.getContent(m).getValue());
                }
                masterMap.put("subject",subject);
                masterMap.put("date",strDt);
                list.add(masterMap);
              }
        }
	    
	    System.out.println("-------数据-------"+list.size());
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    PreparedStatement pst = null;
		try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    
		    if(flagDt.equals("09:00:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_ONE WHERE DATETIME="+strDt);
		    	pst = conn.prepareStatement("INSERT INTO EVERYDAY_RUN_SITUATION_ONE(ID,SUBJECT,DATETIME,ONWATCH," +
		    			"YAIRDROMENAME,YAIRDROMEPLAN,YAIRDROMEFACT,YAIRDROMERUSHHOUR,YAIRDROMERUSH,YENSUREMSG," +
		    			"YEVENT,YWEATHEREXCEPTION,YRECEIVEFLUX,YRELEASEFLUX,YEQU,EVENT,WEATHER,WEATHEREXTHOUR," +
		    			"WEATHEREXTAREA,WEATHEREXTDIFAIRDROMENAME,WEATHEREXTDIFAIRDROMEHOUR,WEATHEREXTDIFAIRDROMEBACK," +
		    			"WEATHEREXTDIFAIRDROMEBY,WEATHEREXTDIFAIRDROMECAL,WEATHEREXTSHUTAIRDROMENAME," +
		    			"WEATHEREXTSHUTAIRDROMEHOUR,WEATHEREXTSHUTAIRDROMEBACK,WEATHEREXTSHUTAIRDROMEBY," +
		    			"WEATHEREXTSHUTAIRDROMECAL,WEATHEREXTCHANGE,RECEIVEFLUX,FLIGHTLATEAIRDROMENAME," +
		    			"FLIGHTLATEREASON,FLIGHTLATE,FLIGHTLATEGETWOHOUR,FUTUREFLUXREASON,FUTUREFLUXHOUR," +
		    			"FUTUREFLUXMEASURES,AIRPORTALTERNATEMSG,EQU,TEMPAIRROUTE) values (SEQ_EVERYDAY_RUN_SITUATION_ONE.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
		    			"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (Map<String,Object> map:list) {
					    //前日运行情况
				        pst.setString(1, map.get("subject").toString());  
				        pst.setString(2, map.get("date").toString());
				        if(map.get("onWatch")!=null ||"".equals(map.get("onWatch"))){
				        	pst.setString(3, map.get("onWatch").toString()); 
				        } else {
				        	pst.setString(3, ""); 
				        }
				        if(map.get("yAirdromeName")!=null ||"".equals(map.get("yAirdromeName"))){
				        	pst.setString(4, map.get("yAirdromeName").toString());   
				        } else {
				        	pst.setString(4, "");   
				        }
				        if(map.get("yAirdromePlan")!=null ||"".equals(map.get("yAirdromePlan"))){
				        	pst.setString(5, map.get("yAirdromePlan").toString()); 
				        } else {
				        	pst.setString(5, ""); 
				        }  
				        if(map.get("yairdromeFact")!=null ||"".equals(map.get("yairdromeFact"))){
				        	pst.setString(6, map.get("yairdromeFact").toString()); 
				        } else {
				        	pst.setString(6, ""); 
				        } 
				        if(map.get("yAirdromeRushHour")!=null ||"".equals(map.get("yAirdromeRushHour"))){
				        	pst.setString(7, map.get("yAirdromeRushHour").toString()); 
				        } else {
				        	pst.setString(7, ""); 
				        }
				        if(map.get("yAirdromeRush")!=null ||"".equals(map.get("yAirdromeRush"))){
				        	pst.setString(8, map.get("yAirdromeRush").toString()); 
				        } else {
				        	pst.setString(8, ""); 
				        }
				        if(map.get("yEnsureMsg")!=null ||"".equals(map.get("yEnsureMsg"))){
				        	pst.setString(9, map.get("yEnsureMsg").toString());
				        } else {
				        	pst.setString(9, ""); 
				        }
				        if(map.get("yEvent")!=null ||"".equals(map.get("yEvent"))){
				        	pst.setString(10, map.get("yEvent").toString()); 
				        } else {
				        	pst.setString(10, ""); 
				        }
				        if(map.get("yWeatherException")!=null ||"".equals(map.get("yWeatherException"))){
				        	pst.setString(11, map.get("yWeatherException").toString());  
				        } else {
				        	pst.setString(11, ""); 
				        }
				        if(map.get("yReceiveFlux")!=null ||"".equals(map.get("yReceiveFlux"))){
				        	pst.setString(12, map.get("yReceiveFlux").toString());  
				        } else {
				        	pst.setString(12, ""); 
				        }
				        if(map.get("yReceiveFlux")!=null ||"".equals(map.get("yReceiveFlux"))){
				        	pst.setString(13, map.get("yReleaseFlux").toString()); 
				        } else {
				        	pst.setString(13, "");  
				        }
				        if(map.get("yEqu")!=null ||"".equals(map.get("yEqu"))){
				        	pst.setString(14, map.get("yEqu").toString());
				        } else {
				        	pst.setString(14, "");
				        }
				        if(map.get("event")!=null ||"".equals(map.get("event"))){
				        	pst.setString(15, map.get("event").toString()); 
				        } else {
				        	pst.setString(15, ""); 
				        }
				        if(map.get("weather")!=null ||"".equals(map.get("weather"))){
				        	pst.setString(16, map.get("weather").toString()); 
				        } else {
				        	pst.setString(16, ""); 
				        }
				        //当日运行情况
				        if(map.get("weatherExtHour")!=null ||"".equals(map.get("weatherExtHour"))){
				        	pst.setString(17, map.get("weatherExtHour").toString()); 
				        } else {
				        	pst.setString(17, ""); 
				        }
				        if(map.get("weatherExtArea")!=null ||"".equals(map.get("weatherExtArea"))){
				        	pst.setString(18, map.get("weatherExtArea").toString()); 
				        } else {
				        	pst.setString(18, ""); 
				        }
				        
				        if(map.get("weatherExtDifAirdromeName")!=null ||"".equals(map.get("weatherExtDifAirdromeName"))){
				        	pst.setString(19, map.get("weatherExtDifAirdromeName").toString()); 
				        } else {
				        	pst.setString(19, ""); 
				        }
				        if(map.get("weatherExtDifAirdromeHour")!=null ||"".equals(map.get("weatherExtDifAirdromeHour"))){
				        	pst.setString(20, map.get("weatherExtDifAirdromeHour").toString()); 
				        } else {
				        	pst.setString(20, ""); 
				        }
				        if(map.get("weatherExtDifAirdromeBack")!=null ||"".equals(map.get("weatherExtDifAirdromeBack"))){
				        	pst.setString(21, map.get("weatherExtDifAirdromeBack").toString()); 
				        } else {
				        	pst.setString(21, ""); 
				        }
				        if(map.get("weatherExtDifAirdromeBy")!=null ||"".equals(map.get("weatherExtDifAirdromeBy"))){
				        	pst.setString(22, map.get("weatherExtDifAirdromeBy").toString());
				        } else {
				        	pst.setString(22, "");
				        }
				        if(map.get("weatherExtDifairdromeCal")!=null ||"".equals(map.get("weatherExtDifairdromeCal"))){
				        	pst.setString(23, map.get("weatherExtDifairdromeCal").toString()); 
				        } else {
				        	pst.setString(23, ""); 
				        }
				        if(map.get("weatherExtShutAirdromeName")!=null ||"".equals(map.get("weatherExtShutAirdromeName"))){
				        	pst.setString(24, map.get("weatherExtShutAirdromeName").toString()); 
				        } else {
				        	pst.setString(24, ""); 
				        }
				        if(map.get("weatherExtShutAirdromeHour")!=null ||"".equals(map.get("weatherExtShutAirdromeHour"))){
				        	pst.setString(25, map.get("weatherExtShutAirdromeHour").toString()); 
				        } else {
				        	pst.setString(25, ""); 
				        }
				        if(map.get("weatherExtShutAirdromeBack")!=null ||"".equals(map.get("weatherExtShutAirdromeBack"))){
				        	pst.setString(26, map.get("weatherExtShutAirdromeBack").toString()); 
				        } else {
				        	pst.setString(26, ""); 
				        }
				        if(map.get("weatherExtShutAirdromeBy")!=null ||"".equals(map.get("weatherExtShutAirdromeBy"))){
				        	pst.setString(27, map.get("weatherExtShutAirdromeBy").toString()); 
				        } else {
				        	pst.setString(27, ""); 
				        }
				        if(map.get("weatherExtShutAirdromeCal")!=null ||"".equals(map.get("weatherExtShutAirdromeCal"))){
				        	pst.setString(28, map.get("weatherExtShutAirdromeCal").toString()); 
				        } else {
				        	pst.setString(28, ""); 
				        }
				        if(map.get("weatherExtChange")!=null ||"".equals(map.get("weatherExtChange"))){
				        	pst.setString(29, map.get("weatherExtChange").toString()); 
				        } else {
				        	pst.setString(29, ""); 
				        }
				        if(map.get("receiveFlux")!=null ||"".equals(map.get("receiveFlux"))){
				        	pst.setString(30, map.get("receiveFlux").toString());
				        } else {
				        	pst.setString(30, "");
				        }
				        if(map.get("flightLateAirdromeName")!=null ||"".equals(map.get("flightLateAirdromeName"))){
				        	pst.setString(31, map.get("flightLateAirdromeName").toString());
				        } else {
				        	pst.setString(31, "");
				        }
				        if(map.get("flightLateReason")!=null ||"".equals(map.get("flightLateReason"))){
				        	pst.setString(32, map.get("flightLateReason").toString());
				        } else {
				        	pst.setString(32, "");
				        }
				        if(map.get("flightLate")!=null ||"".equals(map.get("flightLate"))){
				        	pst.setString(33, map.get("flightLate").toString());
				        } else {
				        	pst.setString(33, "");
				        }
				        if(map.get("flightLateGeTwohour")!=null ||"".equals(map.get("flightLateGeTwohour"))){
				        	pst.setString(34, map.get("flightLateGeTwohour").toString());
				        } else {
				        	pst.setString(34, "");
				        }
				        if( map.get("futureFluxReason")!=null ||"".equals( map.get("futureFluxReason"))){
				        	pst.setString(35, map.get("futureFluxReason").toString());
				        } else {
				        	pst.setString(35, "");
				        }
				        if( map.get("futureFluxHour")!=null ||"".equals(map.get("futureFluxHour"))){
				        	pst.setString(36, map.get("futureFluxHour").toString());
				        } else {
				        	pst.setString(36, "");
				        }
				        if( map.get("futureFluxMeasures")!=null ||"".equals(map.get("futureFluxMeasures"))){
				        	pst.setString(37, map.get("futureFluxMeasures").toString());
				        } else {
				        	pst.setString(37, "");
				        }
				        if( map.get("airportAlternateMsg")!=null ||"".equals(map.get("airportAlternateMsg"))){
				        	pst.setString(38, map.get("airportAlternateMsg").toString());
				        } else {
				        	pst.setString(38, "");
				        }
				        if( map.get("equ")!=null ||"".equals(map.get("equ"))){
				        	pst.setString(39, map.get("equ").toString());
				        } else {
				        	pst.setString(39, "");
				        }
				        
				        if(map.get("tempAirRoute")!=null ||"".equals(map.get("tempAirRoute"))){
				        	pst.setString(40, map.get("tempAirRoute").toString());
				        } else {
				        	pst.setString(40, "");
				        }
				        // 把一个SQL命令加入命令列表   
				        pst.addBatch();   
				 } 
		    } else if(flagDt.equals("13:30:00") || flagDt.equals("16:30:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_TWO WHERE DATETIME="+strDt);
		    	pst = conn.prepareStatement("INSERT INTO EVERYDAY_RUN_SITUATION_TWO(ID,SUBJECT,DATETIME,EVENT,WEATHEREXTHOUR,WEATHEREXTAREA,WEATHEREXTDIFAIRDROMENAME," +
		    			"WEATHEREXTDIFAIRDROMEHOUR,WEATHEREXTDIFAIRDROMEBACK,WEATHEREXTDIFAIRDROMEBY,WEATHEREXTDIFAIRDROMECAL," +
		    			"WEATHEREXTSHUTAIRDROMENAME,WEATHEREXTSHUTAIRDROMEHOUR,WEATHEREXTSHUTAIRDROMEBACK,WEATHEREXTSHUTAIRDROMEBY," +
		    			"WEATHEREXTSHUTAIRDROMECAL,WEATHEREXTCHANGE,FLIGHTLATEAIRDROMENAME,FLIGHTLATEREASON,FLIGHTLATE," +
		    			"FLIGHTLATEGETWOHOUR,GETWOHOURWEATHERREASON,GETWOHOURMILITARYREASON,GETWOHOURFLUXREASON,GETWOHOURCOMPANYREASON," +
		    			"GETWOHOUROTHERREASON,GETWOHOURMEASURES,RECEIVEFLUX,RELEASEFLUX,AIRCRAFTERRORMSG,AIRPORTALTERNATEMSG," +
		    			"EQU,TOCOORDINATEMATTERS,TEMPAIRROUTE)values (SEQ_EVERYDAY_RUN_SITUATION_TWO.NEXTVAL,?,?,?,?,?,?,?,?,?," +
		    			"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				for (Map<String,Object> map:list) {
					pst.setString(1, map.get("subject").toString());  
			        pst.setString(2, map.get("date").toString()); 
			        if(map.get("event")!=null ||"".equals(map.get("event"))){
			        	pst.setString(3, map.get("event").toString());   
			        } else {
			        	pst.setString(3, "");   
			        }
			        if(map.get("weatherExtHour")!=null ||"".equals(map.get("weatherExtHour"))){
			        	pst.setString(4, map.get("weatherExtHour").toString());   
			        } else {
			        	pst.setString(4, "");   
			        }
			        if(map.get("weatherExtArea")!=null ||"".equals(map.get("weatherExtArea"))){
			        	pst.setString(5, map.get("weatherExtArea").toString());    
			        } else {
			        	pst.setString(5, ""); 
			        }
			        if(map.get("weatherExtDifAirdromeName")!=null ||"".equals(map.get("weatherExtDifAirdromeName"))){
			        	pst.setString(6, map.get("weatherExtDifAirdromeName").toString());   
			        } else {
			        	pst.setString(6, ""); 
			        }
			        if(map.get("weatherExtDifAirdromeHour")!=null ||"".equals(map.get("weatherExtDifAirdromeHour"))){
			        	pst.setString(7, map.get("weatherExtDifAirdromeHour").toString()); 
			        } else {
			        	pst.setString(7, ""); 
			        }
			        if(map.get("weatherExtDifAirdromeBack")!=null ||"".equals(map.get("weatherExtDifAirdromeBack"))){
			        	pst.setString(8, map.get("weatherExtDifAirdromeBack").toString()); 
			        } else {
			        	pst.setString(8, ""); 
			        }
			        if(map.get("weatherExtDifAirdromeBy")!=null ||"".equals(map.get("weatherExtDifAirdromeBy"))){
			        	pst.setString(9, map.get("weatherExtDifAirdromeBy").toString()); 
			        } else {
			        	pst.setString(9, ""); 
			        }
			        if(map.get("weatherExtDifairdromeCal")!=null ||"".equals(map.get("weatherExtDifairdromeCal"))){
			        	pst.setString(10, map.get("weatherExtDifairdromeCal").toString()); 
			        } else {
			        	pst.setString(10, ""); 
			        }
			        if(map.get("weatherExtShutAirdromeName")!=null ||"".equals(map.get("weatherExtShutAirdromeName"))){
			        	pst.setString(11, map.get("weatherExtShutAirdromeName").toString());  
			        } else {
			        	pst.setString(11, ""); 
			        }
			        if(map.get("weatherExtShutAirdromeHour")!=null ||"".equals(map.get("weatherExtShutAirdromeHour"))){
			        	pst.setString(12, map.get("weatherExtShutAirdromeHour").toString());   
			        } else {
			        	pst.setString(12, ""); 
			        }
			        if(map.get("weatherExtShutAirdromeBack")!=null ||"".equals(map.get("weatherExtShutAirdromeBack"))){
			        	pst.setString(13, map.get("weatherExtShutAirdromeBack").toString()); 
			        } else {
			        	pst.setString(13, ""); 
			        }
			        if(map.get("weatherExtShutAirdromeBy")!=null ||"".equals(map.get("weatherExtShutAirdromeBy"))){
			        	pst.setString(14, map.get("weatherExtShutAirdromeBy").toString());
			        } else {
			        	pst.setString(14, "");
			        }
			        if(map.get("weatherExtShutAirdromeCal")!=null ||"".equals(map.get("weatherExtShutAirdromeCal"))){
			        	pst.setString(15, map.get("weatherExtShutAirdromeCal").toString()); 
			        } else {
			        	pst.setString(15, ""); 
			        }
			        if(map.get("weatherExtChange")!=null ||"".equals(map.get("weatherExtChange"))){
			        	pst.setString(16, map.get("weatherExtChange").toString()); 
			        } else {
			        	pst.setString(16, ""); 
			        }
			        if(map.get("flightLateAirdromeName")!=null ||"".equals(map.get("flightLateAirdromeName"))){
			        	pst.setString(17, map.get("flightLateAirdromeName").toString()); 
			        } else {
			        	pst.setString(17, ""); 
			        }
			        if(map.get("flightLateReason")!=null ||"".equals(map.get("flightLateReason"))){
			        	pst.setString(18, map.get("flightLateReason").toString()); 
			        } else {
			        	pst.setString(18, ""); 
			        }
			        if(map.get("flightLate")!=null ||"".equals(map.get("flightLate"))){
			        	pst.setString(19, map.get("flightLate").toString()); 
			        } else {
			        	pst.setString(19, ""); 
			        }
			        if(map.get("flightLateGeTwohour")!=null ||"".equals(map.get("flightLateGeTwohour"))){
			        	pst.setString(20, map.get("flightLateGeTwohour").toString()); 
			        } else {
			        	pst.setString(20, ""); 
			        }
			        if(map.get("geTwohourWeatherReason")!=null ||"".equals(map.get("geTwohourWeatherReason"))){
			        	 pst.setString(21, map.get("geTwohourWeatherReason").toString()); 
			        } else {
			        	 pst.setString(21, ""); 
			        }
			        if(map.get("geTwohourMilitaryReason")!=null ||"".equals(map.get("geTwohourMilitaryReason"))){
			        	pst.setString(22, map.get("geTwohourMilitaryReason").toString()); 
			        } else {
			        	pst.setString(22, ""); 
			        }
			        if(map.get("geTwohourFluxReason")!=null ||"".equals(map.get("geTwohourFluxReason"))){
			        	pst.setString(23, map.get("geTwohourFluxReason").toString()); 
			        } else {
			        	pst.setString(23, ""); 
			        }
			        if(map.get("geTwohourCompanyReason")!=null ||"".equals(map.get("geTwohourCompanyReason"))){
			        	pst.setString(24, map.get("geTwohourCompanyReason").toString()); 
			        } else {
			        	pst.setString(24, ""); 
			        }
			        if(map.get("geTwohourOtherReason")!=null ||"".equals(map.get("geTwohourOtherReason"))){
			        	pst.setString(25, map.get("geTwohourOtherReason").toString()); 
			        } else {
			        	pst.setString(25, ""); 
			        }
			        if(map.get("geTwohourMeasures")!=null ||"".equals(map.get("geTwohourMeasures"))){
			        	pst.setString(26, map.get("geTwohourMeasures").toString()); 
			        } else {
			        	pst.setString(26, ""); 
			        }
			        if(map.get("receiveFlux")!=null ||"".equals(map.get("receiveFlux"))){
			        	pst.setString(27, map.get("receiveFlux").toString()); 
			        } else {
			        	pst.setString(27, ""); 
			        }
			        if(map.get("releaseFlux")!=null ||"".equals(map.get("releaseFlux"))){
			        	pst.setString(28, map.get("releaseFlux").toString()); 
			        } else {
			        	pst.setString(28, ""); 
			        }
			        if(map.get("aircraftErrorMsg")!=null ||"".equals(map.get("aircraftErrorMsg"))){
			        	pst.setString(29, map.get("aircraftErrorMsg").toString()); 
			        } else {
			        	pst.setString(29, ""); 
			        }
			        if(map.get("airportAlternateMsg")!=null ||"".equals(map.get("airportAlternateMsg"))){
			        	pst.setString(30, map.get("airportAlternateMsg").toString()); 
			        } else {
			        	pst.setString(30, "");
			        }
			        if(map.get("equ")!=null ||"".equals(map.get("equ"))){
			        	pst.setString(31, map.get("equ").toString());
			        } else {
			        	pst.setString(31, "");
			        }
			        if(map.get("toCoordinateMatters")!=null ||"".equals(map.get("toCoordinateMatters"))){
			        	pst.setString(32, map.get("toCoordinateMatters").toString());
			        } else {
			        	pst.setString(32, "");
			        }
			        if(map.get("tempAirRoute")!=null ||"".equals(map.get("tempAirRoute"))){
			        	pst.setString(33, map.get("tempAirRoute").toString());
			        } else {
			        	pst.setString(33, "");
			        }
			        // 把一个SQL命令加入命令列表   
			        pst.addBatch();   
				 }
		    } else if(flagDt.equals("20:30:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_THREE WHERE DATETIME="+strDt);
		    	pst =  conn.prepareStatement("INSERT INTO EVERYDAY_RUN_SITUATION_THREE (ID,SUBJECT,DATETIME,EVENT,PUBLIC1,PUBLIC2," +
		    			"EQU,PUBLIC4,PUBLIC6,RECEIVEFLUX,RELEASEFLUX,TEMPAIRROUTE)values (SEQ_EVERYDAY_RUN_SITUAT_THREE.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?)");
				for (Map<String,Object> map:list) {
			        pst.setString(1, map.get("subject").toString());  
			        pst.setString(2, map.get("date").toString()); 
			        if(map.get("event")!=null ||"".equals(map.get("event"))){
			        	pst.setString(3, map.get("event").toString());   
			        } else {
			        	pst.setString(3, "");   
			        }
			        if(map.get("public1")!=null ||"".equals(map.get("public1"))){
			        	pst.setString(4, map.get("public1").toString()); 
			        } else {
			        	pst.setString(4, ""); 
			        }
			        if(map.get("public2")!=null ||"".equals(map.get("public2"))){
			        	pst.setString(5, map.get("public2").toString()); 
			        } else {
			        	pst.setString(5, ""); 
			        } 
			        if(map.get("equ")!=null ||"".equals(map.get("equ"))){
			        	pst.setString(6, map.get("equ").toString()); 
			        } else {
			        	pst.setString(6, ""); 
			        }
			        if(map.get("public4")!=null ||"".equals(map.get("public4"))){
			        	pst.setString(7, map.get("public4").toString()); 
			        } else {
			        	pst.setString(7, ""); 
			        }
			        if(map.get("public6")!=null ||"".equals(map.get("public6"))){
			        	pst.setString(8, map.get("public6").toString()); 
			        } else {
			        	pst.setString(8, ""); 
			        }
			        if(map.get("receiveFlux")!=null ||"".equals(map.get("receiveFlux"))){
			        	pst.setString(9, map.get("receiveFlux").toString()); 
			        } else {
			        	pst.setString(9, ""); 
			        }
			        if(map.get("releaseFlux")!=null ||"".equals(map.get("releaseFlux"))){
			        	pst.setString(10, map.get("releaseFlux").toString()); 
			        } else {
			        	pst.setString(10, ""); 
			        }
			        if(map.get("tempAirRoute")!=null ||"".equals(map.get("tempAirRoute"))){
			        	pst.setString(11, map.get("tempAirRoute").toString()); 
			        } else {
			        	pst.setString(11, ""); 
			        }
			        // 把一个SQL命令加入命令列表   
			        pst.addBatch();   
				 }
		    }
		    // 执行批量更新   
		    pst.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    setMessage(code+"", message);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(-1+"", "数据传输不成功。");
		} finally{
	        	if(pst != null){
	        	try
	        	{
	        		pst.close();
	        	}catch(Exception e)
	        	{
	        	}
	        	}
	        	if(conn != null){
	        	try
	        	{
	        	conn.close();
	        	}catch(Exception e){
	        		
	        	}
	        	}
	        } 
		return  result;
	 }
	
	
	public String saveRealTimeFlow()
	  {
		System.out.println("-------进入实时流量情况接口-------");
		String result = "";
		String strDt = "";
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Element rootElement = this.doc.getRootElement();
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    strDt = date.replaceAll("-", "").replaceAll(":", "").replaceAll(" ", "");
	    System.out.println("-------date-------"+strDt);
		
	    List dataList = rootElement.getChildren("data");
        if ((dataList != null) && (dataList.size() > 0))
          for (int i = 0; i < dataList.size(); i++) {
            Element dataElement = (Element)dataList.get(i);
            List methodList = dataElement.getChildren("list");
            System.out.println("-------methodList数据-------"+methodList.size());
            if ((methodList != null) && (methodList.size() > 0))
              for (int j = 0; j < methodList.size(); j++) {
                Element methodElement = (Element)methodList.get(j);
                Map<String,Object> serviceMap = new HashMap<String,Object>();
                for(int m=0;m<methodElement.getContentSize();m++){
                	String nodeName = methodElement.getContent(m).toString();
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	serviceMap.put(keyName, methodElement.getContent(m).getValue());
                }
                serviceMap.put("subject",subject);
                serviceMap.put("date",strDt);
                list.add(serviceMap);
              }
        }
	    System.out.println("-------主表数据-------"+list.size());
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    PreparedStatement pst = null;
		try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    stmt.executeQuery("DELETE FROM REALTIMEFLOW WHERE DATETIME="+strDt);
		    
		    System.out.println("DELETE FROM REALTIMEFLOW WHERE DATETIME="+strDt);
		    
			pst = conn.prepareStatement("INSERT INTO REALTIMEFLOW (ID,SUBJECT,DATETIME,RELEASEUNIT,RECEIVEUNIT,RELEASETIME,CONTENT,EFFECTTIME,ENDTIME,REASON) values (SEQ_REALTIMEFLOW.NEXTVAL,?,?,?,?,?,?,?,?,?)");
			for (Map<String,Object> map:list) {
		        pst.setString(1, map.get("subject").toString());  
		        pst.setString(2, map.get("date").toString());
		        if(map.get("releaseUnit")!=null ||"".equals(map.get("releaseUnit"))){
		        	pst.setString(3, map.get("releaseUnit").toString());  
		        } else {
		        	pst.setString(3, "");  
		        }
		        if(map.get("receiveUnit")!=null ||"".equals(map.get("receiveUnit"))){
		        	pst.setString(4, map.get("receiveUnit").toString());   
		        } else {
		        	pst.setString(4, "");   
		        }
		        if(map.get("releaseTime")!=null ||"".equals(map.get("releaseTime"))){
		        	pst.setString(5, map.get("releaseTime").toString());  
		        } else {
		        	pst.setString(5, "");  
		        }
		        if(map.get("content")!=null ||"".equals(map.get("content"))){
		        	pst.setString(6, map.get("content").toString());
		        } else {
		        	pst.setString(6, ""); 
		        }
		        if(map.get("effectTime")!=null ||"".equals(map.get("effectTime"))){
		        	pst.setString(7, map.get("effectTime").toString());
		        } else {
		        	pst.setString(7, "");
		        }
		        if(map.get("endTime")!=null ||"".equals(map.get("endTime"))){
		        	pst.setString(8, map.get("endTime").toString());
		        } else {
		        	pst.setString(8, "");
		        }
		        if(map.get("reason")!=null ||"".equals(map.get("reason"))){
		        	pst.setString(9, map.get("reason").toString());
		        } else {
		        	pst.setString(9, "");
		        }
		        // 把一个SQL命令加入命令列表   
		        pst.addBatch();   
			} 
		    // 执行批量更新   
		    pst.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    setMessage(code+"", message);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(-1+"", "数据传输不成功。");
		} finally{
	        	if(pst != null){
	        	try
	        	{
	        		pst.close();
	        	}catch(Exception e)
	        	{
	        	}
	        	}
	        	if(conn != null){
	        	try
	        	{
	        	conn.close();
	        	}catch(Exception e){
	        		
	        	}
	        	}
	        }  
		return result;
	 }
}