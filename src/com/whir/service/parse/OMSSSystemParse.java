package com.whir.service.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

public class OMSSSystemParse extends AbstractParse{
	public OMSSSystemParse(Document doc){
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
			        if(map.get("type")!=null && !"".equals(map.get("type"))){
			        	pst.setString(2, map.get("type").toString());   
			        } else {
			        	pst.setString(2, "");   
			        }
			        if(map.get("name")!=null && !"".equals(map.get("name"))){
			        	pst.setString(3, map.get("name").toString());   
			        } else {
			        	pst.setString(3, "");   
			        }
			        if(map.get("time")!=null && !"".equals(map.get("time"))){
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
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> masterList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> subList = new ArrayList<Map<String,Object>>();
		
		Element rootElement = this.doc.getRootElement();
		
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    System.out.println("-------date-------"+date);
	    
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
                masterMap.put("date",date);
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
                subMap.put("date",date);
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
		    String str = "DELETE FROM VIDEO_MEETING WHERE DATETIME='"+date+"'";
		    String str1 = "DELETE FROM SUB_VIDEO_MEETING WHERE DATETIME='"+date+"'";
		    System.out.println("-------str------"+str);
		    stmt.executeQuery(str);
		    stmt.executeQuery(str1);
		    System.out.println("-------str1------"+str1);
		    
		    String runCondition="";
		    String facility="";
	    	String weather="";
	    	
	    	//获得一个主键  
	        String seqSql ="SELECT SEQ_VIDEO_MEETING.NEXTVAL AS ID FROM DUAL";  
			ResultSet rs = stmt.executeQuery(seqSql);
			Integer nextval=-1;
			if(rs.next()){
				nextval = rs.getInt("ID");
			}  
	    	System.out.println("插入视频会议的数据ID:"+nextval);
	    	if(-1!=nextval){
				pst = conn.prepareStatement("INSERT INTO VIDEO_MEETING (ID,SUBJECT,DATETIME,RUNCONDITION,FACILITY,WEATHER) values (SEQ_VIDEO_MEETING.NEXTVAL,?,?,?,?,?)");
				for (Map<String,Object> map:masterList) {
				        if(map.get("runCondition")!=null && !"".equals(map.get("runCondition"))){
				        	runCondition=map.get("runCondition").toString();
				        } else {
				        	runCondition="";  
				        }
				        if(map.get("facility")!=null && !"".equals(map.get("facility"))){
				        	facility= map.get("facility").toString();   
				        } else {
				        	facility="";   
				        }
				        if(map.get("weather")!=null && !"".equals(map.get("weather"))){
				        	weather= map.get("weather").toString();   
				        } else {
				        	weather= "";   
				        }
				 } 
				
				String insertSql = "INSERT INTO VIDEO_MEETING (ID,SUBJECT,DATETIME,RUNCONDITION,FACILITY,WEATHER) values ("+nextval+",'"+subject+"','"+date+"','"+runCondition+"','"+facility+"',empty_clob())";
	    		int flag = stmt.executeUpdate(insertSql);
	    	    System.out.println("插入视频会议的数据:"+flag);
	    	    if(1==flag){
	    		    //从数据库中重新获取CLOB对象写入数据  
			        	String sql2 = "SELECT WEATHER FROM VIDEO_MEETING WHERE ID=" + nextval + " FOR UPDATE";  
			        	//锁定数据行进行更新，注意“for update”语句  
			        	System.out.println("视频会议的数据:"+sql2);
	   		        ResultSet rsq = stmt.executeQuery(sql2);  
	   		        while(rsq.next()) {
	   		        	java.sql.Clob weather1 = (java.sql.Clob) rsq.getClob(1);
	   		        	weather1.setString(1, weather); 
			            	
	   		            String sql3 = "UPDATE VIDEO_MEETING SET WEATHER=? WHERE ID=" + nextval;  
	   		            System.out.println("视频会议的数据:"+sql3);
	   		            pst1 = conn.prepareStatement(sql3);    
		            	pst1.setClob(1, weather1);
						pst1.executeUpdate();    
	   		     }   
	    	 }
    	    
			pst1 = conn.prepareStatement("INSERT INTO SUB_VIDEO_MEETING (ID,SUBJECT,DATETIME,TYPE,NUM," +
					"RELEASEUNIT,RECEIVEUNIT,LIMITPOINT,LIMITTIME,CONTENT,RENSON) values " +
					"(SEQ_SUB_VIDEO_MEETING.NEXTVAL,?,?,?,?,?,?,?,?,?,?)");
			 for (Map<String,Object> map:subList) {
				 	pst1.setString(1, map.get("subject").toString());  
				 	pst1.setString(2, map.get("date").toString());
			        if(map.get("type")!=null && !"".equals(map.get("type"))){
			        	pst1.setString(3, map.get("type").toString());         
			        } else {
			        	pst1.setString(3, "");        
			        }
			        if(map.get("num")!=null && !"".equals(map.get("num"))){
			        	pst1.setString(4, map.get("num").toString());      
			        } else {
			        	pst1.setString(4, "");      
			        }
			        if(map.get("releaseUnit")!=null && !"".equals(map.get("releaseUnit"))){
			        	pst1.setString(5, map.get("releaseUnit").toString()); 
			        } else {
			        	pst1.setString(5, ""); 
			        }
			        if(map.get("receiveUnit")!=null && !"".equals(map.get("receiveUnit"))){
			        	pst1.setString(6, map.get("receiveUnit").toString()); 
			        } else {
			        	pst1.setString(6, ""); 
			        }
			        if(map.get("limitPoint")!=null && !"".equals(map.get("limitPoint"))){
			        	pst1.setString(7, map.get("limitPoint").toString());
			        } else {
			        	pst1.setString(7, "");
			        }
			        if(map.get("limitTime")!=null && !"".equals(map.get("limitTime"))){
			        	pst1.setString(8, map.get("limitTime").toString());
			        } else {
			        	pst1.setString(8, "");
			        }
			        if(map.get("content")!=null && !"".equals(map.get("content"))){
			        	pst1.setString(9, map.get("content").toString());
			        } else {
			        	pst1.setString(9, "");
			        }
			        if(map.get("renson")!=null && !"".equals(map.get("renson"))){
			        	pst1.setString(10, map.get("renson").toString());   
			        } else {
			        	pst1.setString(10, "");
			        }
			        // 把一个SQL命令加入命令列表   
			        pst1.addBatch();   
			 }
		    // 执行批量更新   
			 pst1.executeBatch();   
	    }
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
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
		setMessage(code+"", message);
        return result;
	 }  
	
	
	public String savEeverydayRunSituation() throws ParseException
	  {
		System.out.println("-------进入每日运行情况接口-------");
		String flagDt = "";
		int code = 0;
	    String message = "数据传输成功。";
	    Map<String,Object> masterMap = new HashMap<String,Object>();
		Element rootElement = this.doc.getRootElement();
		
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    System.out.println("-------date-------"+date);
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
                for(int m=0;m<methodElement.getContentSize();m++){
                	String nodeName = methodElement.getContent(m).toString();
                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
                	masterMap.put(keyName, methodElement.getContent(m).getValue());
                }
                masterMap.put("subject",subject);
              }
        }
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    PreparedStatement pst = null;
	    PreparedStatement pst1 = null;
		try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    
		    if(flagDt.equals("09:00:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_ONE WHERE DATETIME='"+date+"'");
		    	//获得一个主键  
		        String seqSql ="SELECT SEQ_EVERYDAY_RUN_SITUATION_ONE.NEXTVAL AS ID FROM DUAL";  
				ResultSet rs = stmt.executeQuery(seqSql);
				Integer nextval=-1;
				if(rs.next()){
					nextval = rs.getInt("ID");
				}  
		    	System.out.println("插入09:00:00的数据ID:"+nextval);
		    	
		    	String onWatch="";String yAirdromeName="";
		    	String yAirdromePlan="";String yairdromeFact="";
		    	String yAirdromeRushHour="";String yAirdromeRush="";
		    	String yEnsureMsg="";String weather="";
		    	String weatherExtHour="";String weatherExtArea="";
		    	String weatherExtDifAirdromeName="";String weatherExtDifAirdromeHour="";
		    	String weatherExtDifAirdromeBack="";String weatherExtDifAirdromeBy="";
		    	String weatherExtDifairdromeCal="";String weatherExtShutAirdromeName="";
		    	String weatherExtShutAirdromeHour="";String weatherExtShutAirdromeBack="";
		    	String weatherExtShutAirdromeBy="";String weatherExtShutAirdromeCal="";
		    	String weatherExtChange="";String flightLateAirdromeName="";
		    	String flightLateReason="";String flightLate="";
		    	String flightLateGeTwohour="";String futureFluxReason="";
		    	String futureFluxHour="";String futureFluxMeasures="";
		    	String airportAlternateMsg="";String tempAirRoute="";
		    	
		    	String yEvent="";String yWeatherException="";
        	    String yReceiveFlux="";String yReleaseFlux="";
        	    String yEqu="";String event="";
        	    String receiveFlux="";String equ="";
		    	if(-1!=nextval){
			    	for (Map.Entry<String, Object> entry : masterMap.entrySet()) {
			        	   
			        	   if("onWatch".equals(entry.getKey())){
			        		   onWatch=entry.getValue().toString();
			        	   }
			        	   if("yAirdromeName".equals(entry.getKey())){
			        		   yAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("yAirdromePlan".equals(entry.getKey())){
			        		   yAirdromePlan=entry.getValue().toString();
			        	   }
			        	   if("yairdromeFact".equals(entry.getKey())){
			        		   yairdromeFact=entry.getValue().toString();
			        	   }
			        	   if("yAirdromeRushHour".equals(entry.getKey())){
			        		   yAirdromeRushHour=entry.getValue().toString();
			        	   }
			        	   if("yAirdromeRush".equals(entry.getKey())){
			        		   yAirdromeRush=entry.getValue().toString();
			        	   }
			        	   if("yEnsureMsg".equals(entry.getKey())){
			        		   yEnsureMsg=entry.getValue().toString();
			        	   }
			        	   if("weather".equals(entry.getKey())){
			        		   weather=entry.getValue().toString();
			        	   }
			        	   if("weatherExtHour".equals(entry.getKey())){
			        		   weatherExtHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtArea".equals(entry.getKey())){
			        		   weatherExtArea=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeName".equals(entry.getKey())){
			        		   weatherExtDifAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeHour".equals(entry.getKey())){
			        		   weatherExtDifAirdromeHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeBack".equals(entry.getKey())){
			        		   weatherExtDifAirdromeBack=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeBy".equals(entry.getKey())){
			        		   weatherExtDifAirdromeBy=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifairdromeCal".equals(entry.getKey())){
			        		   weatherExtDifairdromeCal=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeName".equals(entry.getKey())){
			        		   weatherExtShutAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeHour".equals(entry.getKey())){
			        		   weatherExtShutAirdromeHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeBack".equals(entry.getKey())){
			        		   weatherExtShutAirdromeBack=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeBy".equals(entry.getKey())){
			        		   weatherExtShutAirdromeBy=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeCal".equals(entry.getKey())){
			        		   weatherExtShutAirdromeCal=entry.getValue().toString();
			        	   }
			        	   if("weatherExtChange".equals(entry.getKey())){
			        		   weatherExtChange=entry.getValue().toString();
			        	   }
			        	   if("flightLateAirdromeName".equals(entry.getKey())){
			        		   flightLateAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("flightLateReason".equals(entry.getKey())){
			        		   flightLateReason=entry.getValue().toString();
			        	   }
			        	   if("flightLate".equals(entry.getKey())){
			        		   flightLate=entry.getValue().toString();
			        	   }
			        	   if("flightLateGeTwohour".equals(entry.getKey())){
			        		   flightLateGeTwohour=entry.getValue().toString();
			        	   }
			        	   if("futureFluxReason".equals(entry.getKey())){
			        		   futureFluxReason=entry.getValue().toString();
			        	   }
			        	   if("futureFluxHour".equals(entry.getKey())){
			        		   futureFluxHour= entry.getValue().toString();
			        	   }
			        	   if("futureFluxMeasures".equals(entry.getKey())){
			        		   futureFluxMeasures=entry.getValue().toString();
			        	   }
			        	   if("airportAlternateMsg".equals(entry.getKey())){
			        		   airportAlternateMsg=entry.getValue().toString();
			        	   }
			        	   if("tempAirRoute".equals(entry.getKey())){
			        		   tempAirRoute=entry.getValue().toString();
			        	   }
			        	   if("yEvent".equals(entry.getKey())){
			        		   yEvent=entry.getValue().toString(); 
			        	    }
		   		            if("yWeatherException".equals(entry.getKey())){
		   		            	yWeatherException=entry.getValue().toString(); 
			        	    }
		   		            if("yReceiveFlux".equals(entry.getKey())){
		   		            	yReceiveFlux=entry.getValue().toString(); 
		   		            }
		   		            if("yReleaseFlux".equals(entry.getKey())){
		   		            	yReleaseFlux=entry.getValue().toString(); 
		   		            }
		   		            if("yEqu".equals(entry.getKey())){
		   		            	yEqu=entry.getValue().toString(); 
		   		            }
		   		            if("event".equals(entry.getKey())){
		   		            	event=entry.getValue().toString(); 
		   		            }
		   		            if("receiveFlux".equals(entry.getKey())){
		   		            	receiveFlux=entry.getValue().toString(); 
		   		            }
		   		            if("equ".equals(entry.getKey())){
		   		            	equ=entry.getValue().toString(); 
		   		            }
			    		}
			    		String insertSql = "INSERT INTO EVERYDAY_RUN_SITUATION_ONE(ID,SUBJECT,DATETIME,ONWATCH," +
			    			"YAIRDROMENAME,YAIRDROMEPLAN,YAIRDROMEFACT,YAIRDROMERUSHHOUR,YAIRDROMERUSH,YENSUREMSG," +
			    			"YEVENT,YWEATHEREXCEPTION,YRECEIVEFLUX,YRELEASEFLUX,YEQU,EVENT,WEATHER,WEATHEREXTHOUR," +
			    			"WEATHEREXTAREA,WEATHEREXTDIFAIRDROMENAME,WEATHEREXTDIFAIRDROMEHOUR,WEATHEREXTDIFAIRDROMEBACK," +
			    			"WEATHEREXTDIFAIRDROMEBY,WEATHEREXTDIFAIRDROMECAL,WEATHEREXTSHUTAIRDROMENAME," +
			    			"WEATHEREXTSHUTAIRDROMEHOUR,WEATHEREXTSHUTAIRDROMEBACK,WEATHEREXTSHUTAIRDROMEBY," +
			    			"WEATHEREXTSHUTAIRDROMECAL,WEATHEREXTCHANGE,RECEIVEFLUX,FLIGHTLATEAIRDROMENAME," +
			    			"FLIGHTLATEREASON,FLIGHTLATE,FLIGHTLATEGETWOHOUR,FUTUREFLUXREASON,FUTUREFLUXHOUR," +
			    			"FUTUREFLUXMEASURES,AIRPORTALTERNATEMSG,EQU,TEMPAIRROUTE) values ("+nextval+",'"+subject+"','"+date+"','"+onWatch+"','" +
			    			yAirdromeName+"','"+yAirdromePlan+"','"+yairdromeFact+"','"+yAirdromeRushHour+"','"+yAirdromeRush+"','"+yEnsureMsg+"',empty_clob(),empty_clob(),empty_clob()" +
			    			",empty_clob(),empty_clob(),empty_clob(),'"+weather+"','"+weatherExtHour+"','"+weatherExtArea+"','" +
			    			weatherExtDifAirdromeName+"','"+weatherExtDifAirdromeHour+"','"+weatherExtDifAirdromeBack+"','"+weatherExtDifAirdromeBy+"','"+weatherExtDifairdromeCal+"','" +
			    			weatherExtShutAirdromeName+"','"+weatherExtShutAirdromeHour+"','"+weatherExtShutAirdromeBack+"','"+weatherExtShutAirdromeBy+"','" +
			    			weatherExtShutAirdromeCal+"','"+weatherExtChange+"',empty_clob(),'"+flightLateAirdromeName+"','"+flightLateReason+"','"+flightLate+"','"+flightLateGeTwohour+"','"+
			    			futureFluxReason+"','"+futureFluxHour+"','"+futureFluxMeasures+"','"+airportAlternateMsg+"',empty_clob(),'"+tempAirRoute+"')";
			    		
			    		int flag = stmt.executeUpdate(insertSql);
		        	    System.out.println("插入09:00:00的数据:"+flag);
		        	    if(1==flag){
		        		    //从数据库中重新获取CLOB对象写入数据  
		   		        	String sql2 = "SELECT YEVENT,YWEATHEREXCEPTION,YRECEIVEFLUX,YRELEASEFLUX,YEQU,EVENT,RECEIVEFLUX,EQU" +
		   		        		" FROM EVERYDAY_RUN_SITUATION_ONE WHERE ID=" + nextval + " FOR UPDATE";  
		   		        	//锁定数据行进行更新，注意“for update”语句  
		   		        	System.out.println("SQL13:30:00-16:30的数据:"+sql2);
			   		        ResultSet rsq = stmt.executeQuery(sql2);  
			   		        while(rsq.next()) {
			   		        	java.sql.Clob yEvent1 = (java.sql.Clob) rsq.getClob(1);
			   		        	java.sql.Clob yWeatherException1 = (java.sql.Clob)rsq.getClob(2); 
			   		        	java.sql.Clob yReceiveFlux1 = (java.sql.Clob)rsq.getClob(3); 
			   		        	java.sql.Clob yReleaseFlux1 = (java.sql.Clob)rsq.getClob(4); 
			   		        	java.sql.Clob yEqu1 = (java.sql.Clob)rsq.getClob(5); 
			   		        	java.sql.Clob event1 = (java.sql.Clob)rsq.getClob(6); 
			   		        	java.sql.Clob receiveFlux1 = (java.sql.Clob)rsq.getClob(7); 
			   		        	java.sql.Clob equ1 = (java.sql.Clob)rsq.getClob(8); 
			   		        	yEvent1.setString(1, yEvent); 
			   		        	yWeatherException1.setString(2, yWeatherException); 
			   		        	yReceiveFlux1.setString(3, yReceiveFlux); 
			   		        	yReleaseFlux1.setString(4, yReleaseFlux); 
			   		        	yEqu1.setString(5, yEqu); 
			   		        	event1.setString(6, event); 
		   		            	receiveFlux1.setString(7, receiveFlux); 
		   		            	equ1.setString(8, equ); 
		   		            	
			   		            String sql3 = "UPDATE EVERYDAY_RUN_SITUATION_ONE SET YEVENT=?,YWEATHEREXCEPTION=?,YRECEIVEFLUX=?,YRELEASEFLUX=?,YEQU=?" +
			   		            		",EVENT=?,RECEIVEFLUX=?,EQU=? WHERE ID=" + nextval;  
			   		            System.out.println("SQL13:30:00-16:30的数据:"+sql3);
			   		            pst1 = conn.prepareStatement(sql3);    
		   		            	pst1.setClob(1, yEvent1);
			   		         	pst1.setClob(2, yWeatherException1);
			   		      		pst1.setClob(3, yReceiveFlux1);
			   		   			pst1.setClob(4, yReleaseFlux1);
			   					pst1.setClob(5, yEqu1);
			   					pst1.setClob(6, event1);
			   					pst1.setClob(7, receiveFlux1);
		   						pst1.setClob(8, equ1);
		   						pst1.executeUpdate();    
			   		     }   
		        	 }
		    	}
		    } else if(flagDt.equals("13:30:00") || flagDt.equals("16:30:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_TWO WHERE DATETIME='"+date+"'");
		    	
		    	String weatherExtHour="";String weatherExtArea="";
		    	String weatherExtDifAirdromeName="";String weatherExtDifAirdromeHour="";
		    	String weatherExtDifAirdromeBack="";String weatherExtDifAirdromeBy="";
		    	String weatherExtDifairdromeCal="";String weatherExtShutAirdromeName="";
		    	String weatherExtShutAirdromeHour="";String weatherExtShutAirdromeBack="";
		    	String weatherExtShutAirdromeBy="";String weatherExtShutAirdromeCal="";
		    	String weatherExtChange="";String flightLateAirdromeName="";
		    	String flightLateReason="";String flightLate="";
		    	String flightLateGeTwohour="";String geTwohourWeatherReason="";
		    	String geTwohourMilitaryReason="";String geTwohourFluxReason="";
		    	String geTwohourCompanyReason="";String geTwohourOtherReason="";
		    	String geTwohourMeasures="";String aircraftErrorMsg="";
		    	String airportAlternateMsg="";String toCoordinateMatters="";
		    	String tempAirRoute="";String event="";
		    	String receiveFlux="";String releaseFlux="";
		    	String equ="";
		    	//获得一个主键  
		        String seqSql ="SELECT SEQ_EVERYDAY_RUN_SITUATION_TWO.NEXTVAL AS ID FROM DUAL";  
				ResultSet rs = stmt.executeQuery(seqSql);
				System.out.println("22222222222222222222");
				Integer nextval=-1;
				if(rs.next()){
					nextval = rs.getInt("ID");
				} 
				System.out.println("插入13:30:0016:30:00的数据ID:"+nextval);
				if(-1!=nextval){
			    	for (Map.Entry<String, Object> entry : masterMap.entrySet()) {
			        	   if("weatherExtHour".equals(entry.getKey())){
			        		   weatherExtHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtArea".equals(entry.getKey())){
			        		   weatherExtArea=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeName".equals(entry.getKey())){
			        		   weatherExtDifAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeHour".equals(entry.getKey())){
			        		   weatherExtDifAirdromeHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeBack".equals(entry.getKey())){
			        		   weatherExtDifAirdromeBack=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifAirdromeBy".equals(entry.getKey())){
			        		   weatherExtDifAirdromeBy=entry.getValue().toString();
			        	   }
			        	   if("weatherExtDifairdromeCal".equals(entry.getKey())){
			        		   weatherExtDifairdromeCal=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeName".equals(entry.getKey())){
			        		   weatherExtShutAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeHour".equals(entry.getKey())){
			        		   weatherExtShutAirdromeHour=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeBack".equals(entry.getKey())){
			        		   weatherExtShutAirdromeBack=entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeBy".equals(entry.getKey())){
			        		   weatherExtShutAirdromeBy= entry.getValue().toString();
			        	   }
			        	   if("weatherExtShutAirdromeCal".equals(entry.getKey())){
			        		   weatherExtShutAirdromeCal= entry.getValue().toString();
			        	   }
			        	   if("weatherExtChange".equals(entry.getKey())){
			        		   weatherExtChange=entry.getValue().toString();
			        	   }
			        	   if("flightLateAirdromeName".equals(entry.getKey())){
			        		   flightLateAirdromeName=entry.getValue().toString();
			        	   }
			        	   if("flightLateReason".equals(entry.getKey())){
			        		   flightLateReason=entry.getValue().toString();
			        	   }
			        	   if("flightLate".equals(entry.getKey())){
			        		   flightLate=entry.getValue().toString();
			        	   }
			        	   if("flightLateGeTwohour".equals(entry.getKey())){
			        		   flightLateGeTwohour=entry.getValue().toString();
			        	   }
			        	   if("geTwohourWeatherReason".equals(entry.getKey())){
			        		   geTwohourWeatherReason=entry.getValue().toString();
			        	   }
			        	   if("geTwohourMilitaryReason".equals(entry.getKey())){
			        		   geTwohourMilitaryReason=entry.getValue().toString();
			        	   }
			        	   if("geTwohourFluxReason".equals(entry.getKey())){
			        		   geTwohourFluxReason=entry.getValue().toString();
			        	   }
			        	   if("geTwohourCompanyReason".equals(entry.getKey())){
			        		   geTwohourCompanyReason=entry.getValue().toString();
			        	   }
			        	   if("geTwohourOtherReason".equals(entry.getKey())){
			        		   geTwohourOtherReason=entry.getValue().toString();
			        	   }
			        	   if("geTwohourMeasures".equals(entry.getKey())){
			        		   geTwohourMeasures=entry.getValue().toString();
			        	   }
			        	   if("aircraftErrorMsg".equals(entry.getKey())){
			        		   aircraftErrorMsg=entry.getValue().toString();
			        	   }
			        	   if("airportAlternateMsg".equals(entry.getKey())){
			        		   airportAlternateMsg=entry.getValue().toString();
			        	   }
			        	   if("toCoordinateMatters".equals(entry.getKey())){
			        		   toCoordinateMatters=entry.getValue().toString();
			        	   }
			        	   if("tempAirRoute".equals(entry.getKey())){
			        		   tempAirRoute=entry.getValue().toString();
			        	   }
			        	   if("event".equals(entry.getKey())){
			        		    event=entry.getValue().toString(); 
			        	   }
		   		           if("receiveFlux".equals(entry.getKey())){
		   		            	receiveFlux=entry.getValue().toString(); 
			        	   }
		   		           if("releaseFlux".equals(entry.getKey())){
		   		            	releaseFlux = entry.getValue().toString(); 
		   		           }
		   		           if("equ".equals(entry.getKey())){
		   		            	equ= entry.getValue().toString(); 
		   		           }
			    	   }
			    	   String insertSql="INSERT INTO EVERYDAY_RUN_SITUATION_TWO(ID,SUBJECT,DATETIME,EVENT,WEATHEREXTHOUR,WEATHEREXTAREA,WEATHEREXTDIFAIRDROMENAME," +
			    			"WEATHEREXTDIFAIRDROMEHOUR,WEATHEREXTDIFAIRDROMEBACK,WEATHEREXTDIFAIRDROMEBY,WEATHEREXTDIFAIRDROMECAL," +
			    			"WEATHEREXTSHUTAIRDROMENAME,WEATHEREXTSHUTAIRDROMEHOUR,WEATHEREXTSHUTAIRDROMEBACK,WEATHEREXTSHUTAIRDROMEBY," +
			    			"WEATHEREXTSHUTAIRDROMECAL,WEATHEREXTCHANGE,FLIGHTLATEAIRDROMENAME,FLIGHTLATEREASON,FLIGHTLATE," +
			    			"FLIGHTLATEGETWOHOUR,GETWOHOURWEATHERREASON,GETWOHOURMILITARYREASON,GETWOHOURFLUXREASON,GETWOHOURCOMPANYREASON," +
			    			"GETWOHOUROTHERREASON,GETWOHOURMEASURES,RECEIVEFLUX,RELEASEFLUX,AIRCRAFTERRORMSG,AIRPORTALTERNATEMSG," +
			    			"EQU,TOCOORDINATEMATTERS,TEMPAIRROUTE)values ("+nextval+",'"+subject+"','"+date+"',empty_clob(),'"+weatherExtHour+"','"+weatherExtArea+"','" +
			    			 weatherExtDifAirdromeName+"','"+weatherExtDifAirdromeHour+"','"+weatherExtDifAirdromeBack+"','"+weatherExtDifAirdromeBy+"','" +
			    			 weatherExtDifairdromeCal+"','"+weatherExtShutAirdromeName+"','"+weatherExtShutAirdromeHour+"','" +
			    			 weatherExtShutAirdromeBack+"','"+weatherExtShutAirdromeBy+"','"+weatherExtShutAirdromeCal+"','" +
			    		     weatherExtChange+"','"+flightLateAirdromeName+"','"+flightLateReason+"','"+flightLate+"','"+flightLateGeTwohour+"','"+geTwohourWeatherReason+"','"+
			    		     geTwohourMilitaryReason+"','"+geTwohourFluxReason+"','"+geTwohourCompanyReason+"','"+geTwohourOtherReason+"','"+geTwohourMeasures+"'," +
			    		     "empty_clob(),empty_clob(),'"+aircraftErrorMsg+"','"+airportAlternateMsg+"',empty_clob(),'"+toCoordinateMatters+"','"+tempAirRoute+"')";
			    	   System.out.println("插入13:30:00-16:30的insertSql:"+insertSql);
			    	   
			           int flag = stmt.executeUpdate(insertSql);
		        	   System.out.println("插入13:30:00-16:30的数据:"+flag);
		        	   if(1==flag){
		        		    //从数据库中重新获取CLOB对象写入数据  
		   		        	String sql2 = "SELECT EVENT,RECEIVEFLUX,RELEASEFLUX,EQU" +
		   		        		" FROM EVERYDAY_RUN_SITUATION_TWO WHERE ID=" + nextval + " FOR UPDATE";  
		   		        	//锁定数据行进行更新，注意“for update”语句  
		   		        	System.out.println("SQL13:30:00-16:30的数据:"+sql2);
			   		        ResultSet rsq = stmt.executeQuery(sql2); 
			   		        while(rsq.next()) { 
			   		        	java.sql.Clob event1 = (java.sql.Clob) rsq.getClob(1);
			   		        	java.sql.Clob receiveFlux1 = (java.sql.Clob)rsq.getClob(2); 
			   		        	java.sql.Clob releaseFlux1 = (java.sql.Clob)rsq.getClob(3); 
			   		        	java.sql.Clob equ1 = (java.sql.Clob)rsq.getClob(4); 
		   		            	event1.setString(1, event); 
		   		            	receiveFlux1.setString(2, receiveFlux); 
		   		            	releaseFlux1.setString(3, releaseFlux); 
		   		            	equ1.setString(4, equ); 
		   		            	
			   		            String sql3 = "UPDATE EVERYDAY_RUN_SITUATION_TWO SET EVENT=?,RECEIVEFLUX=?,RELEASEFLUX=?,EQU=?" +
			   		            		" WHERE ID=" + nextval;  
			   		            System.out.println("SQL13:30:00-16:30的数据:"+sql3);
			   		            pst1 = conn.prepareStatement(sql3);    
			   		            pst1.setClob(1, event1);
			   		            pst1.setClob(2, receiveFlux1);
			   		            pst1.setClob(3, releaseFlux1);
			   		            pst1.setClob(4, equ1);
			   					pst1.executeUpdate();    
			   		      }   
			    	  }
	        	}
		    } else if(flagDt.equals("20:30:00")){
		    	stmt.executeQuery("DELETE FROM EVERYDAY_RUN_SITUATION_THREE WHERE DATETIME='"+date+"'");
		    	
		    	String public1="";String public2="";
		    	String public4="";String public6="";
		    	String tempAirRoute="";
		    	String event="";
		    	String receiveFlux="";String releaseFlux="";
		    	String equ="";
		    	//获得一个主键  
		        String seqSql ="SELECT SEQ_EVERYDAY_RUN_SITUAT_THREE.NEXTVAL AS ID FROM DUAL";  
				ResultSet rs = stmt.executeQuery(seqSql);
				
				Integer nextval=-1;
				if(rs.next()){
					nextval = rs.getInt("ID");
				} 
				System.out.println("插入20:30:00的数据ID:"+nextval);
				if(-1!=nextval){
			    	for (Map.Entry<String, Object> entry : masterMap.entrySet()) {
			        	   if("public1".equals(entry.getKey())){
			        		   public1=entry.getValue().toString();
			        	   }
			        	   if("public2".equals(entry.getKey())){
			        		   public2=entry.getValue().toString();
			        	   }
			        	   if("public4".equals(entry.getKey())){
			        		   public4=entry.getValue().toString();
			        	   }
			        	   if("public6".equals(entry.getKey())){
			        		   public6=entry.getValue().toString();
			        	   }
			        	   if("tempAirRoute".equals(entry.getKey())){
			        		   tempAirRoute=entry.getValue().toString();
			        	   }
			        	   if("event".equals(entry.getKey())){
			        		    event=entry.getValue().toString(); 
			        	   }
		   		           if("receiveFlux".equals(entry.getKey())){
		   		            	receiveFlux=entry.getValue().toString(); 
			        	   }
		   		           if("releaseFlux".equals(entry.getKey())){
		   		            	releaseFlux = entry.getValue().toString(); 
		   		           }
		   		           if("equ".equals(entry.getKey())){
		   		            	equ= entry.getValue().toString(); 
		   		           }
		   		           if("receiveFlux".equals(entry.getKey())){
		   		        	   receiveFlux=entry.getValue().toString(); 
		   		           }
		   		           if("releaseFlux".equals(entry.getKey())){
	   		            	releaseFlux = entry.getValue().toString(); 
		   		           }
			    	   }
			    	   String insertSql ="INSERT INTO EVERYDAY_RUN_SITUATION_THREE (ID,SUBJECT,DATETIME,EVENT,PUBLIC1,PUBLIC2," +
			    			"EQU,PUBLIC4,PUBLIC6,RECEIVEFLUX,RELEASEFLUX,TEMPAIRROUTE)values ("+nextval+",'"+subject+"','"+date+"',empty_clob(),'" +
			    			 public1+"','"+public2+"',empty_clob(),'"+public4+"','"+public6+"',empty_clob(),empty_clob(),'"+tempAirRoute+"')";
			    	   System.out.println("插入20:30:00的insertSql:"+insertSql);
			    	   int flag = stmt.executeUpdate(insertSql);
		        	   System.out.println("插入20:30:00的数据:"+flag);
		        	   if(1==flag){
		        		    //从数据库中重新获取CLOB对象写入数据  
		   		        	String sql2 = "SELECT EVENT,RECEIVEFLUX,RELEASEFLUX,EQU" +
		   		        		" FROM EVERYDAY_RUN_SITUATION_THREE WHERE ID=" + nextval + " FOR UPDATE";  
		   		        	//锁定数据行进行更新，注意“for update”语句 
		   		        	System.out.println("SQL20:30:00的数据:"+sql2);
			   		        ResultSet rsq = stmt.executeQuery(sql2);  
			   		        while(rsq.next()) {
			   		            java.sql.Clob event1 = (java.sql.Clob) rsq.getClob(1);
			   		            java.sql.Clob receiveFlux1 = (java.sql.Clob)rsq.getClob(2); 
			   		         	java.sql.Clob releaseFlux1 = (java.sql.Clob)rsq.getClob(3); 
			   		        	java.sql.Clob equ1 = (java.sql.Clob)rsq.getClob(4); 
			   		        	event1.setString(1, event);
			   		        	receiveFlux1.setString(2, receiveFlux);
			   		        	releaseFlux1.setString(3, releaseFlux);
			   		        	equ1.setString(4, equ);
			   		        	
			   		            String sql3 = "UPDATE EVERYDAY_RUN_SITUATION_THREE SET EVENT=?,RECEIVEFLUX=?,RELEASEFLUX=?,EQU=?" +
			   		            		" WHERE ID=" + nextval;  
			   		            System.out.println("SQL20:30:00的数据:"+sql3);
			   		            pst1 = conn.prepareStatement(sql3);    
			   		            pst1.setClob(1, event1);
			   		            pst1.setClob(2, receiveFlux1);
			   		            pst1.setClob(3, releaseFlux1);
			   		            pst1.setClob(4, equ1);
			   					pst1.executeUpdate();    
			   		    }   
		        	}
	        	}
		    }
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
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
		setMessage(code+"", message);
		return "";
	 }
	
	public String saveRealTimeFlow()
	  {
		System.out.println("-------进入实时流量情况接口-------");
		String result = "";
		int code = 0;
	    String message = "数据传输成功。";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		Element rootElement = this.doc.getRootElement();
	    String subject = rootElement.getChild("subject") == null ? "" : rootElement.getChild("subject").getValue().trim();
	    String date = rootElement.getChild("date") == null ? "" : rootElement.getChild("date").getValue().trim();
	    System.out.println("-------date-------"+date);
		
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
                serviceMap.put("date",date);
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
		    stmt.executeQuery("DELETE FROM REALTIMEFLOW WHERE DATETIME='"+date+"'");
		    
		    System.out.println("DELETE FROM REALTIMEFLOW WHERE DATETIME='"+date+"'");
		    
			pst = conn.prepareStatement("INSERT INTO REALTIMEFLOW (ID,SUBJECT,DATETIME,RELEASEUNIT,RECEIVEUNIT,RELEASETIME,CONTENT,EFFECTTIME,ENDTIME,REASON) values (SEQ_REALTIMEFLOW.NEXTVAL,?,?,?,?,?,?,?,?,?)");
			for (Map<String,Object> map:list) {
		        pst.setString(1, map.get("subject").toString());  
		        pst.setString(2, map.get("date").toString());
		        if(map.get("releaseUnit")!=null && !"".equals(map.get("releaseUnit"))){
		        	pst.setString(3, map.get("releaseUnit").toString());  
		        } else {
		        	pst.setString(3, "");  
		        }
		        if(map.get("receiveUnit")!=null && !"".equals(map.get("receiveUnit"))){
		        	pst.setString(4, map.get("receiveUnit").toString());   
		        } else {
		        	pst.setString(4, "");   
		        }
		        if(map.get("releaseTime")!=null && !"".equals(map.get("releaseTime"))){
		        	pst.setString(5, map.get("releaseTime").toString());  
		        } else {
		        	pst.setString(5, "");  
		        }
		        if(map.get("content")!=null && !"".equals(map.get("content"))){
		        	pst.setString(6, map.get("content").toString());
		        } else {
		        	pst.setString(6, ""); 
		        }
		        if(map.get("effectTime")!=null && !"".equals(map.get("effectTime"))){
		        	pst.setString(7, map.get("effectTime").toString());
		        } else {
		        	pst.setString(7, "");
		        }
		        if(map.get("endTime")!=null && !"".equals(map.get("endTime"))){
		        	pst.setString(8, map.get("endTime").toString());
		        } else {
		        	pst.setString(8, "");
		        }
		        if(map.get("reason")!=null && !"".equals(map.get("reason"))){
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