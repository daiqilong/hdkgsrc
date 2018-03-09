package com.whir.portal.basedata.bd;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.util.PropertiesUtils;

public class RunConditionBD {
	//管制信息
	public Map<String, String> getControlInfoList() throws SQLException{
		Map<String,String> map = new HashMap<String,String>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        String nowDate1 = format.format(new Date());
	        String sql = "SELECT DATETIME,RUNCONDITION FROM VIDEO_MEETING P WHERE SUBSTR(P.DATETIME,0,10) = '"+nowDate1+"' ORDER BY P.DATETIME DESC";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        String datetime = "";
	        String runCondition = "";
	        while(rs.next()){
	        	runCondition = rs.getString("RUNCONDITION");
	        	datetime = rs.getString("DATETIME");
	        	map.put(datetime.substring(11, 19), runCondition);
	        	break;
	        }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	//log
	    	}
	    	}
	    	if(pstmt != null){
	    	try
	    	{
	    	pstmt.close();
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
	    return map;
	}
	
	//气象信息
	public Map<String, String> getWeatherInfoList() throws SQLException{
		Map<String,String> map = new HashMap<String,String>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        String nowDate1 = format.format(new Date());
	        String sql = "SELECT DATETIME,WEATHER FROM VIDEO_MEETING P WHERE SUBSTR(P.DATETIME,0,10) = '"+nowDate1+"' ORDER BY P.DATETIME DESC";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        String datetime = "";
	        String weather = "";
	        while(rs.next()){
	        	weather = rs.getString("WEATHER");
	        	datetime = rs.getString("DATETIME");
	        	map.put(datetime.substring(11, 19), weather);
	        	break;
	        }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	//log
	    	}
	    	}
	    	if(pstmt != null){
	    	try
	    	{
	    	pstmt.close();
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
	    return map;
	}
	
	//停机信息
	public List<Map<String,Object>> getHaltInfoList() throws SQLException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> filterSort = new ArrayList<Map<String,Object>>();
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate1 = format.format(new Date());
        
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("stop");
		Element parameter = output.addElement("parameter");
		
		Element applyDateStartElement = parameter.addElement("applyDateStart");
		applyDateStartElement.setText("");
		Element applyDateEndElement = parameter.addElement("applyDateEnd");
		applyDateEndElement.setText("");
		Element applicatElement = parameter.addElement("applicat");
		applicatElement.setText("");
		Element haltStartTimeElement = parameter.addElement("haltStartTime");
		haltStartTimeElement.setText(nowDate1);
		Element haltEndTimeElement = parameter.addElement("haltEndTime");
		haltEndTimeElement.setText("");
		Element haltReasonElement = parameter.addElement("haltReason");
		haltReasonElement.setText("");
		Element applyUnitElement = parameter.addElement("applyUnit");
		applyUnitElement.setText("");
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		applyDepartmentElement.setText("");
		Element systemNameElement = parameter.addElement("systemName");
		systemNameElement.setText("");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(1000000));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(1));
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlDataToList(res);
			      filterSort=filterSortData(list);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return filterSort;
	}
	
	//筛选符合条件的数据
	public List<Map<String,Object>> filterSortData(List<Map<String,Object>> list) throws SQLException{
		List<Map<String,Object>> listNum = new ArrayList<Map<String,Object>>();
		for(Map<String,Object> map:list){
			if(null!=map.get("haltStartTime") && !"".equals(map.get("haltStartTime")) 
					&& null!=map.get("haltEndTime") && !"".equals(map.get("haltEndTime"))
					&& null!=map.get("haltState") && !"".equals(map.get("haltState"))){
				String haltStartTime = map.get("haltStartTime").toString().substring(0, 10);
				String haltEndTime = map.get("haltEndTime").toString().substring(0, 10);
				String haltState = map.get("haltState").toString();
				if("未完成".equals(haltState.trim())){
					boolean result = compare_date(haltStartTime,haltEndTime);
					if(result){
						listNum.add(map);
					}
				}
			}
		}
		return listNum;
	}
	
	public boolean compare_date(String DATE1, String DATE2) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		boolean result=false;
        String nowDate1 = format.format(new Date());
        try {
        	Date date1= format.parse(nowDate1);
        	Date dt1 = format.parse(DATE1);
  		  	Date dt2 = format.parse(DATE2);
            if (date1.getTime() >= dt1.getTime() && date1.getTime() <= dt2.getTime()) {
            	result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
	
	//干扰信息信息
	public List<Map<String,Object>> getDistractInfoList() throws SQLException{
		List<Map<String,Object>> listNum = new ArrayList<Map<String,Object>>();
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate1 = format.format(new Date());
        
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("jam");
		Element parameter = output.addElement("parameter");
		
		Element disturbDateElement = parameter.addElement("disturbStartDate");
		disturbDateElement.setText(nowDate1);
		Element endDateElement = parameter.addElement("disturbEndDate");
		endDateElement.setText("");
		
		Element registerUnitElement = parameter.addElement("registerUnit");
		registerUnitElement.setText("");
		
		Element registerDepartmentElement = parameter.addElement("registerDepartment");
		registerDepartmentElement.setText("");
		Element frequencyElement = parameter.addElement("frequency");
		frequencyElement.setText("");
		
		Element disturbTypeElement = parameter.addElement("disturbType");
		disturbTypeElement.setText("");
		
		Element disturbCharcaterElement = parameter.addElement("disturbCharcater");
		disturbCharcaterElement.setText("");
		
		Element disturbInfuenceElement = parameter.addElement("disturbInfuence");
		disturbInfuenceElement.setText("");
		
		Element disturbPurposeElement = parameter.addElement("disturbPurpose");
		disturbPurposeElement.setText("");
		
		Element coupleUnitElement = parameter.addElement("coupleUnit");
		coupleUnitElement.setText("");
		//首页中当天的停机信息不回超过二十条（跟客户确认了）
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(20));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(1));
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      listNum = xmlDataParse.parseXmlDataToList(res);
		      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      res = e.getMessage();
	    }
	    return listNum;
	}
	
	
	//跑道信息信息
	public List<Map<String,Object>> getTrackInfoList() throws SQLException{
		List<Map<String,Object>> listNum = new ArrayList<Map<String,Object>>();
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("runway");
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      System.out.println(res);
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      listNum = xmlDataParse.parseXmlDataToList(res);
		      }
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      res = e.getMessage();
	    }
	    return listNum;
	}
}