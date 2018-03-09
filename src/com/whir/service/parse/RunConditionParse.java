package com.whir.service.parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.jdom.Document;

import com.whir.portal.basedata.bd.RunConditionBD;
import com.whir.service.common.AbstractParse;

public class RunConditionParse extends AbstractParse{
	public RunConditionParse(Document doc){
	    super(doc);
	}
	public String getControlInfoData() throws SQLException
	  {
		System.out.println("获取getControlInfoData::");
		String result = "";
	    String message = "数据传输成功。";
	    RunConditionBD runConditionBD = new RunConditionBD();
	    Map<String, String> map = runConditionBD.getControlInfoList();
	    if(!map.isEmpty()){
	    	Set<Entry<String,String>> entrySet = map.entrySet();
	    	for(Entry<String,String> entry : entrySet){
	    		String timenow = entry.getKey();
	    		String value = entry.getValue()!=null?entry.getValue():"无信息";
	    		result += "<result>";
	            result += "<runcondition><![CDATA[" + value +"]]></runcondition>";
	            result += "<datetime><![CDATA[" + timenow + "]]></datetime>";
	            result += "</result>";
	    	}
	    }else{
	    	result += "<result>";
            result += "<runcondition><![CDATA[正常]]></runcondition>";
            result += "<datetime><![CDATA[无]]></datetime>";
            result += "</result>";
	    }
	    this.setMessage("1", message);
        return result;
	 }
	
	public String getWeatherInfoData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    RunConditionBD runConditionBD = new RunConditionBD();
	    Map<String, String> map = runConditionBD.getWeatherInfoList();
	    if(!map.isEmpty()){
	    	Set<Entry<String,String>> entrySet = map.entrySet();
	    	for(Entry<String,String> entry : entrySet){
	    		String timenow = entry.getKey();
	    		String value = entry.getValue()!=null?entry.getValue():"无信息";
	    		result += "<result>";
	            result += "<weather><![CDATA[" + value +"]]></weather>";
	            result += "<datetime><![CDATA[" + timenow + "]]></datetime>";
	            result += "</result>";
	    	}
	    }else{
	    	result += "<result>";
          result += "<weather><![CDATA[正常]]></weather>";
          result += "<datetime><![CDATA[无]]></datetime>";
          result += "</result>";
	    }
	    this.setMessage("1", message);
      return result;
	 }
	
	
	public String getHaltInfoData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    RunConditionBD runConditionBD = new RunConditionBD();
	    List<Map<String,Object>> list = runConditionBD.getHaltInfoList();
	    System.out.println("停机信息数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
            	Map<String,Object> map = list.get(i);
                result += "<result>";
                result += "<haltStartTime><![CDATA[" + map.get("haltStartTime") +"]]></haltStartTime>";
                result += "<haltEndTime><![CDATA[" + map.get("haltEndTime") + "]]></haltEndTime>";
                result += "<system><![CDATA[" + map.get("system") +"]]></system>";
                result += "<planType><![CDATA[" + map.get("planType") +"]]></planType>";
                result += "<haltReason><![CDATA[" + map.get("haltReason") +"]]></haltReason>";
                result += "</result>";
            }
        }
	    this.setMessage("1", message);
	    return result;
	 }
	
	
	public String getDistractInfoData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    RunConditionBD runConditionBD = new RunConditionBD();
	    List<Map<String,Object>> list = runConditionBD.getDistractInfoList();
	    System.out.println("干扰信息数据list:::"+list.size());
	    if (list != null && list.size() > 0) {
          for (int i = 0; i < list.size(); i++) {
          	Map<String,Object> map = list.get(i);
              result += "<result>";
              result += "<disturbDate><![CDATA[" + map.get("disturbDate") +"]]></disturbDate>";
              result += "<frequency><![CDATA[" + map.get("frequency") + "]]></frequency>";
              result += "<sector><![CDATA[" + map.get("sector") +"]]></sector>";
              result += "<disturbType><![CDATA[" + map.get("disturbType") +"]]></disturbType>";
              result += "<disturbCharcater><![CDATA[" + map.get("disturbCharcater") +"]]></disturbCharcater>";
              result += "</result>";
          }
      }
	    this.setMessage("1", message);
	    return result;
	 }
	
	public String getTrackInfoData() throws SQLException
	  {
		String result = "";
	    String message = "数据传输成功。";
	    RunConditionBD runConditionBD = new RunConditionBD();
	    List<Map<String,Object>> list = runConditionBD.getTrackInfoList();
	    if (list != null && list.size() > 0) {
        for (int i = 0; i < list.size(); i++) {
        	Map<String,Object> map = list.get(i);
            result += "<result>";
            result += "<airportName><![CDATA[" + map.get("airportName") +"]]></airportName>";
            result += "<firstTrack><![CDATA[" + map.get("firstTrack") + "]]></firstTrack>";
            result += "<secondTrack><![CDATA[" + map.get("secondTrack") +"]]></secondTrack>";
            result += "<threeTrack><![CDATA[" + map.get("threeTrack") +"]]></threeTrack>";
            result += "<fourTrack><![CDATA[" + map.get("fourTrack") +"]]></fourTrack>";
            result += "<fiveTrack><![CDATA[" + map.get("fiveTrack") +"]]></fiveTrack>";
            result += "<sixTrack><![CDATA[" + map.get("sixTrack") +"]]></sixTrack>";
            result += "</result>";
        }
    }
	    this.setMessage("1", message);
	    return result;
	 }
}