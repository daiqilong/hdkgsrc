package com.whir.service.parse;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.portal.module.actionsupport.GeneralGuideAction;
import com.whir.service.common.AbstractParse;

public class CommuNavigationParse extends AbstractParse{
	public CommuNavigationParse(Document doc){
	    super(doc);
	}
	public String getSystemRecordCount() throws SQLException {
			System.out.println("获取getSystemRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element majorElement = rootElement.getChild("major");
		    String major = majorElement.getValue();
		    Element stationElement = rootElement.getChild("station");
		    String station = stationElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    int recordCount = generalGuideAction.getSystemRecordCount(userKey,major,station);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getSystemListDate() throws SQLException
	  {
			System.out.println("获取getSystemListDate::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element pageSizeElement = rootElement.getChild("pageSize");
		    String pageSize = pageSizeElement.getValue();
		    Element currentPageElement = rootElement.getChild("currentPage");
		    String currentPage = currentPageElement.getValue();
		    Element stationElement = rootElement.getChild("station");
		    String station = stationElement.getValue();
		    Element majorElement = rootElement.getChild("major");
		    String major = majorElement.getValue();
		    Element systemCategoryElement = rootElement.getChild("systemCategory");
		    String systemCategory = systemCategoryElement.getValue();
		    Element systemRegionElement = rootElement.getChild("systemRegion");
		    String systemRegion = systemRegionElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    List<List<Map<String,Object>>> list = generalGuideAction.getSystemListDate(userKey,pageSize,currentPage,station,major,systemCategory,systemRegion);
		    
		    if (list != null && list.size() > 0) {
	          for (int i = 0; i < list.size(); i++) {
	        	  List<Map<String,Object>> listMap = list.get(i);
	        	  result += "<result>";
	        	  for(Map<String,Object> map:listMap){
	        		  for(String key : map.keySet()){ 
	        			  Object value = map.get(key);
	        			  if("major".equals(key)){
	        				  result += "<major><![CDATA[" + value +"]]></major>";
	        			  }
	        			  if("systemName".equals(key)){
	        				  result += "<systemName><![CDATA[" + value +"]]></systemName>";
	        			  }
	        			  if("systemRegion".equals(key)){
	        				  result += "<systemRegion><![CDATA[" + value +"]]></systemRegion>";
	        			  }
	        			  if("maintainDepartment".equals(key)){
	        				  result += "<maintainDepartment><![CDATA[" + value +"]]></maintainDepartment>";
	        			  }
	        			  if("category".equals(key)){
	        				  result += "<category><![CDATA[" + value +"]]></category>";
	        			  }
	        			  if("model".equals(key)){
	        				  result += "<model><![CDATA[" + value +"]]></model>";
	        			  }
	        			  if("station".equals(key)){
	        				  result += "<station><![CDATA[" +value +"]]></station>";
	        			  }
	        	      }
	        	  }
	        	  result += "</result>";
	          }
		    }
		    this.setMessage("1", message);
		    System.out.println("result:::::"+result);
		    return result;
	 }
	
	
	public String getRepairsInformationRecordCount() throws SQLException
	  {
			System.out.println("获取getRepairsInformationRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element spareNameElement = rootElement.getChild("spareName");
		    String spareName = spareNameElement.getValue();
		    Element specificationTypeElement = rootElement.getChild("specificationType");
		    String specificationType = specificationTypeElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    int recordCount = generalGuideAction.getRepairsInformationRecordCount(userKey,specificationType,spareName);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	
	public String getRepairsInformationListDate() throws SQLException
	  {
			System.out.println("获取getRepairsInformationListDate::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element pageSizeElement = rootElement.getChild("pageSize");
		    String pageSize = pageSizeElement.getValue();
		    Element currentPageElement = rootElement.getChild("currentPage");
		    String currentPage = currentPageElement.getValue();
		    Element applyUnitElement = rootElement.getChild("applyUnit");
		    String applyUnit = applyUnitElement.getValue();
		    Element applyDepartmentElement = rootElement.getChild("applyDepartment");
		    String applyDepartment = applyDepartmentElement.getValue();
		    Element systemCategoryElement = rootElement.getChild("systemCategory");
		    String systemCategory = systemCategoryElement.getValue();
		    Element majorElement = rootElement.getChild("major");
		    String major = majorElement.getValue();
		    Element systemElement = rootElement.getChild("system");
		    String system = systemElement.getValue();
		    Element spareNameElement = rootElement.getChild("spareName");
		    String spareName = spareNameElement.getValue();
		    Element specificationTypeElement = rootElement.getChild("specificationType");
		    String specificationType = specificationTypeElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    List<List<Map<String,Object>>> list = generalGuideAction.getRepairsInformationListDate(userKey,pageSize,currentPage,applyUnit,applyDepartment,systemCategory,major,system,spareName,specificationType);
		    
		    if (list != null && list.size() > 0) {
	          for (int i = 0; i < list.size(); i++) {
	        	  List<Map<String,Object>> listMap = list.get(i);
	        	  result += "<result>";
	        	  for(Map<String,Object> map:listMap){
	        		  for(String key : map.keySet()){ 
	        			  Object value = map.get(key);
	        			  if("spareName".equals(key)){
	        				  result += "<spareName><![CDATA[" + value +"]]></spareName>";
	        			  }
	        			  if("equipmentPartNo".equals(key)){
	        				  result += "<equipmentPartNo><![CDATA[" + value +"]]></equipmentPartNo>";
	        			  }
	        			  if("equipmentNo".equals(key)){
	        				  result += "<equipmentNo><![CDATA[" + value +"]]></equipmentNo>";
	        			  }
	        			  if("place".equals(key)){
	        				  result += "<place><![CDATA[" + value +"]]></place>";
	        			  }
	        			  if("department".equals(key)){
	        				  result += "<department><![CDATA[" + value +"]]></department>";
	        			  }
	        			  if("systemName".equals(key)){
	        				  result += "<systemName><![CDATA[" + value +"]]></systemName>";
	        			  }
	        			  if("specificationType".equals(key)){
	        				  result += "<specificationType><![CDATA[" +value +"]]></specificationType>";
	        			  }
	        			  if("equipmentFunction".equals(key)){
	        				  result += "<equipmentFunction><![CDATA[" +value +"]]></equipmentFunction>";
	        			  }
	        	      }
	        	  }
	        	  result += "</result>";
	          }
		    }
		    this.setMessage("1", message);
		    System.out.println("result:::::"+result);
		    return result;
	 }
	
	
	public String getHaltStatisticsRecordCount() throws SQLException
	  {
			System.out.println("获取getHaltStatisticsRecordCount::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element haltReasonElement = rootElement.getChild("haltReason");
		    String haltReason = haltReasonElement.getValue();
		    Element applyUnitElement = rootElement.getChild("applyUnit");
		    String applyUnit = applyUnitElement.getValue();
		    Element haltStartTimeElement = rootElement.getChild("haltStartTime");
		    String haltStartTime = haltStartTimeElement.getValue();
		    Element haltEndTimeElement = rootElement.getChild("haltEndTime");
		    String haltEndTime = haltEndTimeElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    int recordCount = generalGuideAction.getHaltStatisticsRecordCount(userKey, haltReason, applyUnit,haltStartTime,haltEndTime);
		    
		    result += "<result>";
		    result += "<recordCount><![CDATA[" + recordCount +"]]></recordCount>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	 }
	
	public String getHaltStatisticsListDate() throws SQLException
	  {
			System.out.println("获取getHaltStatisticsListDate::");
			String result = "";
		    String message = "数据传输成功。";
			
		    Element rootElement = doc.getRootElement();
		    Element userKeyElement = rootElement.getChild("userKey");
		    String userKey = userKeyElement.getValue();
		    Element pageSizeElement = rootElement.getChild("pageSize");
		    String pageSize = pageSizeElement.getValue();
		    Element currentPageElement = rootElement.getChild("currentPage");
		    String currentPage = currentPageElement.getValue();
		    Element applyDateStartElement = rootElement.getChild("applyDateStart");
		    String applyDateStart = applyDateStartElement.getValue();
		    Element applyDateEndElement = rootElement.getChild("applyDateEnd");
		    String applyDateEnd = applyDateEndElement.getValue();
		    Element applicatElement = rootElement.getChild("applicat");
		    String applicat = applicatElement.getValue();
		    Element haltStartTimeElement = rootElement.getChild("haltStartTime");
		    String haltStartTime = haltStartTimeElement.getValue();
		    Element haltEndTimeElement = rootElement.getChild("haltEndTime");
		    String haltEndTime = haltEndTimeElement.getValue();
		    Element haltReasonElement = rootElement.getChild("haltReason");
		    String haltReason = haltReasonElement.getValue();
		    Element applyUnitElement = rootElement.getChild("applyUnit");
		    String applyUnit = applyUnitElement.getValue();
		    Element applyDepartmentElement = rootElement.getChild("applyDepartment");
		    String applyDepartment = applyDepartmentElement.getValue();
		    Element systemNameElement = rootElement.getChild("systemName");
		    String systemName = systemNameElement.getValue();
		    GeneralGuideAction generalGuideAction = new GeneralGuideAction();
		    List<List<Map<String,Object>>> list = generalGuideAction.getHaltStatisticsListDate(userKey,pageSize,currentPage,applyDateStart,applyDateEnd,applicat,haltStartTime,haltEndTime,haltReason,applyUnit,applyDepartment,systemName);
		    
		    if (list != null && list.size() > 0) {
	          for (int i = 0; i < list.size(); i++) {
	        	  List<Map<String,Object>> listMap = list.get(i);
	        	  result += "<result>";
	        	  for(Map<String,Object> map:listMap){
	        		  for(String key : map.keySet()){ 
	        			  Object value = map.get(key);
	        			  if("applyDate".equals(key)){
	        				  result += "<applyDate><![CDATA[" + value +"]]></applyDate>";
	        			  }
	        			  if("applyUnit".equals(key)){
	        				  result += "<applyUnit><![CDATA[" + value +"]]></applyUnit>";
	        			  }
	        			  if("applyDepartment".equals(key)){
	        				  result += "<applyDepartment><![CDATA[" + value +"]]></applyDepartment>";
	        			  }
	        			  if("applicat".equals(key)){
	        				  result += "<applicat><![CDATA[" + value +"]]></applicat>";
	        			  }
	        			  if("haltStartTime".equals(key)){
	        				  result += "<haltStartTime><![CDATA[" + value +"]]></haltStartTime>";
	        			  }
	        			  if("haltEndTime".equals(key)){
	        				  result += "<haltEndTime><![CDATA[" + value +"]]></haltEndTime>";
	        			  }
	        			  if("planType".equals(key)){
	        				  result += "<planType><![CDATA[" +value +"]]></planType>";
	        			  }
	        			  if("system".equals(key)){
	        				  result += "<system><![CDATA[" +value +"]]></system>";
	        			  }
	        			  if("haltReason".equals(key)){
	        				  result += "<haltReason><![CDATA[" +value +"]]></haltReason>";
	        			  }
	        			  if("register".equals(key)){
	        				  result += "<register><![CDATA[" +value +"]]></register>";
	        			  }
	        			  if("haltState".equals(key)){
	        				  result += "<haltState><![CDATA[" +value +"]]></haltState>";
	        			  }
	        			  if("auditState".equals(key)){
	        				  result += "<auditState><![CDATA[" +value +"]]></auditState>";
	        			  }
	        	      }
	        	  }
	        	  result += "</result>";
	          }
		    }
		    this.setMessage("1", message);
		    System.out.println("result:::::"+result);
		    return result;
	 }
}