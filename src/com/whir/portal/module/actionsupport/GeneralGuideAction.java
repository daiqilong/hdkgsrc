package com.whir.portal.module.actionsupport;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;

public class GeneralGuideAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957611521728273579L;
	private static Logger logger = Logger.getLogger(GeneralGuideAction.class.getName());
	
	/**
	 *  系统信息
	 *  @param request
	 *            HttpServletRequest
	 */
	public String systemList()
	  {
	    return "systemList";
	  }
	
	/**
	 * 查找系统信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String systemListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("system");
		Element parameter = output.addElement("parameter");
		
		String station = request.getParameter("station");
		String major = request.getParameter("major");
		String systemCategory = request.getParameter("systemCategory");
		String fixAssetSerial = request.getParameter("fixAssetSerial");
		String systemRegion = request.getParameter("systemRegion");
		String installSite = request.getParameter("installSite");
		String maintainDepartment = request.getParameter("maintainDepartment");
		String maintainUnit = request.getParameter("maintainUnit");
		String assistMaintainDepartment = request.getParameter("assistMaintainDepartment");
		String userName = request.getParameter("userName");
		
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element stationElement = parameter.addElement("station");
		if(station==null || station.equals("")){
			stationElement.setText("");
		}else{
			stationElement.setText(station);
		}
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		
		Element fixAssetSerialElement = parameter.addElement("fixAssetSerial");
		if(fixAssetSerial==null || fixAssetSerial.equals("")){
			fixAssetSerialElement.setText("");
		}else{
			fixAssetSerialElement.setText(fixAssetSerial);
		}
		
		Element systemRegionElement = parameter.addElement("systemRegion");
		if(systemRegion==null || systemRegion.equals("")){
			systemRegionElement.setText("");
		}else{
			systemRegionElement.setText(systemRegion);
		}
		
		Element installSiteElement = parameter.addElement("installSite");
		if(installSite==null || installSite.equals("")){
			installSiteElement.setText("");
		}else{
			installSiteElement.setText(installSite);
		}
		
		Element maintainDepartmentElement = parameter.addElement("maintainDepartment");
		if(maintainDepartment==null || maintainDepartment.equals("")){
			maintainDepartmentElement.setText("");
		}else{
			maintainDepartmentElement.setText(maintainDepartment);
		}
		
		Element maintainUnitElement = parameter.addElement("maintainUnit");
		if(maintainUnit==null || maintainUnit.equals("")){
			maintainUnitElement.setText("");
		}else{
			maintainUnitElement.setText(maintainUnit);
		}
		
		Element assistMaintainDepartmentElement = parameter.addElement("assistMaintainDepartment");
		if(assistMaintainDepartment==null || assistMaintainDepartment.equals("")){
			assistMaintainDepartmentElement.setText("");
		}else{
			assistMaintainDepartmentElement.setText(assistMaintainDepartment);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------input---------"+rec.asXML());
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","major", "systemName",
									"systemRegion", "maintainDepartment", "category",
									"model","assistMaintainDepartment","station","fixAssetSerial","installSite"}, list);
					int recordCount = Integer.parseInt(listStr);
					System.out.println("recordCount::::"+recordCount);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	public Integer getSystemRecordCount(String userKey,String major,String station) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		int pageSize = 15;
		int currentPage = 1;
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("system");
		Element parameter = output.addElement("parameter");
		
		String systemCategory = "";
		String fixAssetSerial = "";
		String systemRegion = "";
		String installSite = "";
		String maintainDepartment = "";
		String maintainUnit = "";
		String assistMaintainDepartment = "";
		String userName = userKey;
		
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element stationElement = parameter.addElement("station");
		if(station==null || station.equals("")){
			stationElement.setText("");
		}else{
			stationElement.setText(station);
		}
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		
		Element fixAssetSerialElement = parameter.addElement("fixAssetSerial");
		if(fixAssetSerial==null || fixAssetSerial.equals("")){
			fixAssetSerialElement.setText("");
		}else{
			fixAssetSerialElement.setText(fixAssetSerial);
		}
		
		Element systemRegionElement = parameter.addElement("systemRegion");
		if(systemRegion==null || systemRegion.equals("")){
			systemRegionElement.setText("");
		}else{
			systemRegionElement.setText(systemRegion);
		}
		
		Element installSiteElement = parameter.addElement("installSite");
		if(installSite==null || installSite.equals("")){
			installSiteElement.setText("");
		}else{
			installSiteElement.setText(installSite);
		}
		
		Element maintainDepartmentElement = parameter.addElement("maintainDepartment");
		if(maintainDepartment==null || maintainDepartment.equals("")){
			maintainDepartmentElement.setText("");
		}else{
			maintainDepartmentElement.setText(maintainDepartment);
		}
		
		Element maintainUnitElement = parameter.addElement("maintainUnit");
		if(maintainUnit==null || maintainUnit.equals("")){
			maintainUnitElement.setText("");
		}else{
			maintainUnitElement.setText(maintainUnit);
		}
		
		Element assistMaintainDepartmentElement = parameter.addElement("assistMaintainDepartment");
		if(assistMaintainDepartment==null || assistMaintainDepartment.equals("")){
			assistMaintainDepartmentElement.setText("");
		}else{
			assistMaintainDepartmentElement.setText(assistMaintainDepartment);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------input---------"+rec.asXML());
		
		String res = null;
		int recordCount =0;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      String listStr = xmlDataParse.parseXmlRecord(res);
					recordCount = Integer.parseInt(listStr);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return recordCount;
	}
	
	
	public List<List<Map<String,Object>>> getSystemListDate(String userKey,String pageSize,String currentPage,String station,String major,String systemCategory,String systemRegion) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("system");
		Element parameter = output.addElement("parameter");
		
		String fixAssetSerial = "";
		String installSite = "";
		String maintainDepartment = "";
		String maintainUnit = "";
		String assistMaintainDepartment = "";
		String userName = userKey;
		
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element stationElement = parameter.addElement("station");
		if(station==null || station.equals("")){
			stationElement.setText("");
		}else{
			stationElement.setText(station);
		}
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		
		Element fixAssetSerialElement = parameter.addElement("fixAssetSerial");
		if(fixAssetSerial==null || fixAssetSerial.equals("")){
			fixAssetSerialElement.setText("");
		}else{
			fixAssetSerialElement.setText(fixAssetSerial);
		}
		
		Element systemRegionElement = parameter.addElement("systemRegion");
		if(systemRegion==null || systemRegion.equals("")){
			systemRegionElement.setText("");
		}else{
			systemRegionElement.setText(systemRegion);
		}
		
		Element installSiteElement = parameter.addElement("installSite");
		if(installSite==null || installSite.equals("")){
			installSiteElement.setText("");
		}else{
			installSiteElement.setText(installSite);
		}
		
		Element maintainDepartmentElement = parameter.addElement("maintainDepartment");
		if(maintainDepartment==null || maintainDepartment.equals("")){
			maintainDepartmentElement.setText("");
		}else{
			maintainDepartmentElement.setText(maintainDepartment);
		}
		
		Element maintainUnitElement = parameter.addElement("maintainUnit");
		if(maintainUnit==null || maintainUnit.equals("")){
			maintainUnitElement.setText("");
		}else{
			maintainUnitElement.setText(maintainUnit);
		}
		
		Element assistMaintainDepartmentElement = parameter.addElement("assistMaintainDepartment");
		if(assistMaintainDepartment==null || assistMaintainDepartment.equals("")){
			assistMaintainDepartmentElement.setText("");
		}else{
			assistMaintainDepartmentElement.setText(assistMaintainDepartment);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(pageSize);
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(currentPage);
		System.out.println("--------input---------"+rec.asXML());
		
		String res = null;
		List<List<Map<String,Object>>> list = new ArrayList<List<Map<String,Object>>>();
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlData(res);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return list;
	}
	/**
	 * 备件信息模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String repairsInformationList()
	  {
	    return "repairsInformationList";
	  }
	
	/**
	 * 查找备件信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String repairsInformationListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("spare");
		Element parameter = output.addElement("parameter");
		String applyUnit = request.getParameter("applyUnit");
		String applyDepartment = request.getParameter("applyDepartment");
		String major = request.getParameter("major");
		String systemCategory = request.getParameter("systemCategory");
		String system = request.getParameter("system");
		String spareName = request.getParameter("spareName");
		String specificationType = request.getParameter("specificationType");
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		Element systemElement = parameter.addElement("system");
		if(system==null || system.equals("")){
			systemElement.setText("");
		}else{
			systemElement.setText(system);
		}
		
		Element spareNameElement = parameter.addElement("spareName");
		if(spareName==null || spareName.equals("")){
			spareNameElement.setText("");
		}else{
			spareNameElement.setText(spareName);
		}
		Element specificationTypeElement = parameter.addElement("specificationType");
		if(specificationType==null || specificationType.equals("")){
			specificationTypeElement.setText("");
		}else{
			specificationTypeElement.setText(specificationType);
		}
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
	    System.out.println("--------备件信息input---------"+rec.asXML());
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","spareName", "equipmentPartNo",
									"equipmentNo", "place", "department",
									"systemName","specificationType","equipmentFunction",
									"status"}, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
		       }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	public Integer getRepairsInformationRecordCount(String userKey,String specificationType,String spareName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		int pageSize = 15;
		int currentPage = 1;
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("spare");
		Element parameter = output.addElement("parameter");
		String applyUnit = "";
		String applyDepartment = "";
		String major = "";
		String systemCategory = "";
		String system = "";
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		Element systemElement = parameter.addElement("system");
		if(system==null || system.equals("")){
			systemElement.setText("");
		}else{
			systemElement.setText(system);
		}
		
		Element spareNameElement = parameter.addElement("spareName");
		if(spareName==null || spareName.equals("")){
			spareNameElement.setText("");
		}else{
			spareNameElement.setText(spareName);
		}
		Element specificationTypeElement = parameter.addElement("specificationType");
		if(specificationType==null || specificationType.equals("")){
			specificationTypeElement.setText("");
		}else{
			specificationTypeElement.setText(specificationType);
		}
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
	    System.out.println("--------备件信息input---------"+rec.asXML());
		String res = null;
		int recordCount =0;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
				  recordCount = Integer.parseInt(listStr);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return recordCount;
	}
	
	
	public List<List<Map<String,Object>>> getRepairsInformationListDate(String userKey,String pageSize,String currentPage,String applyUnit,String applyDepartment,String systemCategory,String major,String system,String spareName,String specificationType) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("spare");
		Element parameter = output.addElement("parameter");
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		Element systemElement = parameter.addElement("system");
		if(system==null || system.equals("")){
			systemElement.setText("");
		}else{
			systemElement.setText(system);
		}
		
		Element spareNameElement = parameter.addElement("spareName");
		if(spareName==null || spareName.equals("")){
			spareNameElement.setText("");
		}else{
			spareNameElement.setText(spareName);
		}
		Element specificationTypeElement = parameter.addElement("specificationType");
		if(specificationType==null || specificationType.equals("")){
			specificationTypeElement.setText("");
		}else{
			specificationTypeElement.setText(specificationType);
		}
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(pageSize);
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(currentPage);
	    System.out.println("--------备件信息input---------"+rec.asXML());
		String res = null;
		List<List<Map<String,Object>>> list = new ArrayList<List<Map<String,Object>>>();
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlData(res);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return list;
	}
	
	/**
	 * 频率信息模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String frequencyInformationList()
	  {
	    return "frequencyInformationList";
	  }
	
	/**
	 * 查找频率信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String frequencyInformationListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("frequency");
		Element parameter = output.addElement("parameter");
		String systemCategory = request.getParameter("systemCategory");
		String sector = request.getParameter("sector");
		String major = request.getParameter("major");
		String frequency = request.getParameter("frequency");
		String station = request.getParameter("station");
		String frequencyType = request.getParameter("frequencyType");
		
		Element systemCategoryElement = parameter.addElement("systemCategory");
		if(systemCategory==null || systemCategory.equals("")){
			systemCategoryElement.setText("");
		}else{
			systemCategoryElement.setText(systemCategory);
		}
		Element sectorElement = parameter.addElement("sector");
		if(sector==null || sector.equals("")){
			sectorElement.setText("");
		}else{
			sectorElement.setText(sector);
		}
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		Element frequencyElement = parameter.addElement("frequency");
		if(frequency==null || frequency.equals("")){
			frequencyElement.setText("");
		}else{
			frequencyElement.setText(frequency);
		}
		Element stationElement = parameter.addElement("station");
		if(station==null || station.equals("")){
			stationElement.setText("");
		}else{
			stationElement.setText(station);
		}
		
		Element frequencyTypeElement = parameter.addElement("frequencyType");
		if(frequencyType==null || frequencyType.equals("")){
			frequencyTypeElement.setText("");
		}else{
			frequencyTypeElement.setText(frequencyType);
		}
		
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
	    System.out.println("--------频率信息input---------"+rec.asXML());
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","major", "systemCategory",
									"sector", "frequency", "station",
									"frequencyType","isQuick","isValid",
									"identification","placeCode "}, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
		       }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	public Integer getHaltStatisticsRecordCount(String userKey,String haltReason,String applyUnit,String haltStartTime,String haltEndTime) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		int pageSize = 15;
		int currentPage = 1;
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("stop");
		Element parameter = output.addElement("parameter");
		
		String applyDateStart = "";
		String applyDateEnd = "";
		String applicat = "";
		String applyDepartment = "";
		String systemName = "";
		
		Element applyDateStartElement = parameter.addElement("applyDateStart");
		if(applyDateStart==null || applyDateStart.equals("")){
			applyDateStartElement.setText("");
		}else{
			applyDateStartElement.setText(applyDateStart);
		}
		Element applyDateEndElement = parameter.addElement("applyDateEnd");
		if(applyDateEnd==null || applyDateEnd.equals("")){
			applyDateEndElement.setText("");
		}else{
			applyDateEndElement.setText(applyDateEnd);
		}
		
		Element applicatElement = parameter.addElement("applicat");
		if(applicat==null || applicat.equals("")){
			applicatElement.setText("");
		}else{
			applicatElement.setText(applicat);
		}
		
		Element haltStartTimeElement = parameter.addElement("haltStartTime");
		if(haltStartTime==null || haltStartTime.equals("")){
			haltStartTimeElement.setText("");
		}else{
			haltStartTimeElement.setText(haltStartTime);
		}
		
		Element haltEndTimeElement = parameter.addElement("haltEndTime");
		if(haltEndTime==null || haltEndTime.equals("")){
			haltEndTimeElement.setText("");
		}else{
			haltEndTimeElement.setText(haltEndTime);
		}
		
		Element haltReasonElement = parameter.addElement("haltReason");
		if(haltReason==null || haltReason.equals("")){
			haltReasonElement.setText("");
		}else{
			haltReasonElement.setText(haltReason);
		}
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element systemNameElement = parameter.addElement("systemName");
		if(systemName==null || systemName.equals("")){
			systemNameElement.setText("");
		}else{
			systemNameElement.setText(systemName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------停机统计input---------"+rec.asXML());
		
		String res = null;
		int recordCount=0;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      String listStr = xmlDataParse.parseXmlRecord(res);
				  recordCount = Integer.parseInt(listStr);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return recordCount;
	}
	
	public List<List<Map<String,Object>>> getHaltStatisticsListDate(String userKey,String pageSize,String currentPage,String applyDateStart,String applyDateEnd,String applicat,String haltStartTime,String haltEndTime,String haltReason,String applyUnit,String applyDepartment,String systemName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("stop");
		Element parameter = output.addElement("parameter");
		
		Element applyDateStartElement = parameter.addElement("applyDateStart");
		if(applyDateStart==null || applyDateStart.equals("")){
			applyDateStartElement.setText("");
		}else{
			applyDateStartElement.setText(applyDateStart);
		}
		Element applyDateEndElement = parameter.addElement("applyDateEnd");
		if(applyDateEnd==null || applyDateEnd.equals("")){
			applyDateEndElement.setText("");
		}else{
			applyDateEndElement.setText(applyDateEnd);
		}
		
		Element applicatElement = parameter.addElement("applicat");
		if(applicat==null || applicat.equals("")){
			applicatElement.setText("");
		}else{
			applicatElement.setText(applicat);
		}
		
		Element haltStartTimeElement = parameter.addElement("haltStartTime");
		if(haltStartTime==null || haltStartTime.equals("")){
			haltStartTimeElement.setText("");
		}else{
			haltStartTimeElement.setText(haltStartTime);
		}
		
		Element haltEndTimeElement = parameter.addElement("haltEndTime");
		if(haltEndTime==null || haltEndTime.equals("")){
			haltEndTimeElement.setText("");
		}else{
			haltEndTimeElement.setText(haltEndTime);
		}
		
		Element haltReasonElement = parameter.addElement("haltReason");
		if(haltReason==null || haltReason.equals("")){
			haltReasonElement.setText("");
		}else{
			haltReasonElement.setText(haltReason);
		}
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element systemNameElement = parameter.addElement("systemName");
		if(systemName==null || systemName.equals("")){
			systemNameElement.setText("");
		}else{
			systemNameElement.setText(systemName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(pageSize);
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(currentPage);
		System.out.println("--------停机统计input---------"+rec.asXML());
		
		List<List<Map<String,Object>>> list = new ArrayList<List<Map<String,Object>>>();
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      list = xmlDataParse.parseXmlData(res);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return list;
	}
	
	/**
	 * 停机统计模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String haltStatisticsList()
	  {
	    return "haltStatisticsList";
	  }
	
	/**
	 * 查找停机统计数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String haltStatisticsListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("stop");
		Element parameter = output.addElement("parameter");
		
		String applyDateStart = request.getParameter("applyDateStart");
		String applyDateEnd = request.getParameter("applyDateEnd");
		String applicat = request.getParameter("applicat");
		String haltStartTime = request.getParameter("haltStartTime");
		String haltEndTime = request.getParameter("haltEndTime");
		String haltReason = request.getParameter("haltReason");
		String applyUnit = request.getParameter("applyUnit");
		String applyDepartment = request.getParameter("applyDepartment");
		String systemName = request.getParameter("systemName");
		
		Element applyDateStartElement = parameter.addElement("applyDateStart");
		if(applyDateStart==null || applyDateStart.equals("")){
			applyDateStartElement.setText("");
		}else{
			applyDateStartElement.setText(applyDateStart);
		}
		Element applyDateEndElement = parameter.addElement("applyDateEnd");
		if(applyDateEnd==null || applyDateEnd.equals("")){
			applyDateEndElement.setText("");
		}else{
			applyDateEndElement.setText(applyDateEnd);
		}
		
		Element applicatElement = parameter.addElement("applicat");
		if(applicat==null || applicat.equals("")){
			applicatElement.setText("");
		}else{
			applicatElement.setText(applicat);
		}
		
		Element haltStartTimeElement = parameter.addElement("haltStartTime");
		if(haltStartTime==null || haltStartTime.equals("")){
			haltStartTimeElement.setText("");
		}else{
			haltStartTimeElement.setText(haltStartTime);
		}
		
		Element haltEndTimeElement = parameter.addElement("haltEndTime");
		if(haltEndTime==null || haltEndTime.equals("")){
			haltEndTimeElement.setText("");
		}else{
			haltEndTimeElement.setText(haltEndTime);
		}
		
		Element haltReasonElement = parameter.addElement("haltReason");
		if(haltReason==null || haltReason.equals("")){
			haltReasonElement.setText("");
		}else{
			haltReasonElement.setText(haltReason);
		}
		
		Element applyUnitElement = parameter.addElement("applyUnit");
		if(applyUnit==null || applyUnit.equals("")){
			applyUnitElement.setText("");
		}else{
			applyUnitElement.setText(applyUnit);
		}
		
		Element applyDepartmentElement = parameter.addElement("applyDepartment");
		if(applyDepartment==null || applyDepartment.equals("")){
			applyDepartmentElement.setText("");
		}else{
			applyDepartmentElement.setText(applyDepartment);
		}
		
		Element systemNameElement = parameter.addElement("systemName");
		if(systemName==null || systemName.equals("")){
			systemNameElement.setText("");
		}else{
			systemNameElement.setText(systemName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------停机统计input---------"+rec.asXML());
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","applyDate", "applyUnit",
									"applyDepartment", "applicat", "haltStartTime",
									"haltEndTime","planType","system","haltReason","register","haltState","auditState"}, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
			    }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	
	
	/**
	 * 干扰统计模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String distractStatisticsList()
	  {
	    return "distractStatisticsList";
	  }
	
	/**
	 * 查找干扰统计数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String distractStatisticsListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("jam");
		Element parameter = output.addElement("parameter");
		String disturbStartDate = request.getParameter("disturbStartDate");
		String disturbEndDate = request.getParameter("disturbEndDate");
		String registerUnit = request.getParameter("registerUnit");
		String registerDepartment = request.getParameter("registerDepartment");
		String frequency = request.getParameter("frequency");
		String disturbType = request.getParameter("disturbType");
		String disturbCharcater = request.getParameter("disturbCharcater");
		String disturbInfuence = request.getParameter("disturbInfuence");
		String disturbPurpose = request.getParameter("disturbPurpose");
		String coupleUnit = request.getParameter("coupleUnit");
		
		Element disturbStartDateElement = parameter.addElement("disturbStartDate");
		if(disturbStartDate==null || disturbStartDate.equals("")){
			disturbStartDateElement.setText("");
		}else{
			disturbStartDateElement.setText(disturbStartDate);
		}
		Element disturbEndDateElement = parameter.addElement("disturbEndDate");
		if(disturbEndDate==null || disturbEndDate.equals("")){
			disturbEndDateElement.setText("");
		}else{
			disturbEndDateElement.setText(disturbEndDate);
		}
		
		Element registerUnitElement = parameter.addElement("registerUnit");
		if(registerUnit==null || registerUnit.equals("")){
			registerUnitElement.setText("");
		}else{
			registerUnitElement.setText(registerUnit);
		}
		
		Element registerDepartmentElement = parameter.addElement("registerDepartment");
		if(registerDepartment==null || registerDepartment.equals("")){
			registerDepartmentElement.setText("");
		}else{
			registerDepartmentElement.setText(registerDepartment);
		}
		
		Element frequencyElement = parameter.addElement("frequency");
		if(frequency==null || frequency.equals("")){
			frequencyElement.setText("");
		}else{
			frequencyElement.setText(frequency);
		}
		
		Element disturbTypeElement = parameter.addElement("disturbType");
		if(disturbType==null || disturbType.equals("")){
			disturbTypeElement.setText("");
		}else{
			disturbTypeElement.setText(disturbType);
		}
		
		Element disturbCharcaterElement = parameter.addElement("disturbCharcater");
		if(disturbCharcater==null || disturbCharcater.equals("")){
			disturbCharcaterElement.setText("");
		}else{
			disturbCharcaterElement.setText(disturbCharcater);
		}
		
		Element disturbInfuenceElement = parameter.addElement("disturbInfuence");
		if(disturbInfuence==null || disturbInfuence.equals("")){
			disturbInfuenceElement.setText("");
		}else{
			disturbInfuenceElement.setText(disturbInfuence);
		}
		
		Element disturbPurposeElement = parameter.addElement("disturbPurpose");
		if(disturbPurpose==null || disturbPurpose.equals("")){
			disturbPurposeElement.setText("");
		}else{
			disturbPurposeElement.setText(disturbPurpose);
		}
		
		Element coupleUnitElement = parameter.addElement("coupleUnit");
		if(coupleUnit==null || coupleUnit.equals("")){
			coupleUnitElement.setText("");
		}else{
			coupleUnitElement.setText(coupleUnit);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		System.out.println("--------干扰统计input---------"+rec.asXML());
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
			      // 这里需要修改ejb,加个字段
			      String json = util.writeNewArrayJSON(
						new String[] {"seqNum","disturbDate", "endDate",
								"section", "frequency", "disturbCharcater",
								"disturbType","disturbStrength","state","registerUnit","register","registerDate","moditifiDate","coupleDate","sector"}, list);
			      int recordCount = Integer.parseInt(listStr);
			      int pageCount = (recordCount / pageSize);
			      int mod = recordCount % pageSize;
			      if (mod != 0)
			    	 pageCount += 1;
				json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
						+ "},data:" + json + "}";
				printResult(this.G_SUCCESS, json);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	/**
	 * 故障统计模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String faultStatisticsList()
	  {
	    return "faultStatisticsList";
	  }
	
	/**
	 * 查找故障信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String faultStatisticsListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("fault");
		Element parameter = output.addElement("parameter");
		
		String fillUnit = request.getParameter("fillUnit");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		String major = request.getParameter("major");
		String grade = request.getParameter("grade");
		String system = request.getParameter("system");
		String userName = session.getAttribute("userAccount").toString();
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		
		Element fillUnitElement = parameter.addElement("fillUnit");
		if(fillUnit==null || fillUnit.equals("")){
			fillUnitElement.setText("");
		}else{
			fillUnitElement.setText(fillUnit);
		}
		Element startDateElement = parameter.addElement("startDate");
		if(startDate==null || startDate.equals("")){
			startDateElement.setText("");
		}else{
			startDateElement.setText(startDate);
		}
		
		Element endDateElement = parameter.addElement("endDate");
		if(endDate==null || endDate.equals("")){
			endDateElement.setText("");
		}else{
			endDateElement.setText(endDate);
		}
		
		Element majorElement = parameter.addElement("major");
		if(major==null || major.equals("")){
			majorElement.setText("");
		}else{
			majorElement.setText(major);
		}
		
		Element gradeElement = parameter.addElement("grade");
		if(grade==null || grade.equals("")){
			gradeElement.setText("");
		}else{
			gradeElement.setText(grade);
		}
		
		Element systemElement = parameter.addElement("system");
		if(system==null || system.equals("")){
			systemElement.setText("");
		}else{
			systemElement.setText(system);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
	    System.out.println("--------故障统计input---------"+rec.asXML());
	    
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","faultEvent", "startDate",
									"endDate", "fillUnit", "major",
									"systemSort","system","grade",
									"classify","reason","isComeWeekly",
									"filler","fillDate","reportCheckState","addCheckstate"}, list);
					int recordCount = Integer.parseInt(listStr);
					int pageCount = (recordCount / pageSize);
				      int mod = recordCount % pageSize;
				      if (mod != 0)
				    	 pageCount += 1;
					json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
							+ "},data:" + json + "}";
					printResult(this.G_SUCCESS, json);
		       }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
	
	
	/**
	 * 设备数据模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String equipmentInformList()
	  {
	    return "equipmentInformList";
	  }
	
	/**
	 * 查找故障信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String equipmentInformListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("equipment");
		Element parameter = output.addElement("parameter");
		
		String standardModel = request.getParameter("standardModel");
		String installSite = request.getParameter("installSite");
		String equipmentName = request.getParameter("equipmentName");
		String equipmentSort = request.getParameter("equipmentSort");
		String useDepartment = request.getParameter("useDepartment");
		
		Element standardModelElement = parameter.addElement("standardModel");
		if(standardModel==null || standardModel.equals("")){
			standardModelElement.setText(null);
		}else{
			standardModelElement.setText(standardModel);
		}
		Element installSiteElement = parameter.addElement("installSite");
		if(installSite==null || installSite.equals("")){
			installSiteElement.setText(null);
		}else{
			installSiteElement.setText(installSite);
		}
		
		Element equipmentNameElement = parameter.addElement("equipmentName");
		if(equipmentName==null || equipmentName.equals("")){
			equipmentNameElement.setText(null);
		}else{
			equipmentNameElement.setText(equipmentName);
		}
		
		Element equipmentSortElement = parameter.addElement("equipmentSort");
		if(equipmentSort==null || equipmentSort.equals("")){
			equipmentSortElement.setText(null);
		}else{
			equipmentSortElement.setText(equipmentSort);
		}
		
		Element useDepartmentElement = parameter.addElement("useDepartment");
		if(useDepartment==null || useDepartment.equals("")){
			useDepartmentElement.setText(null);
		}else{
			useDepartmentElement.setText(useDepartment);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
	    System.out.println("--------故障统计input---------"+rec.asXML());
	    
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String input = rec.asXML();
		      Object[] results = client.invoke("service", new Object[] { input });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
		      JacksonUtil util = new JacksonUtil();
				// 这里需要修改ejb,加个字段
				String json = util.writeNewArrayJSON(
						new String[] {"seqNum", "standardModel", "anotherName",
								"installSite", "applyState", "startDate",
								"equipmentName","system","functionDescribe",
								"fixAssetSerial","equipmentSort","systemName",
								"useDepartment"}, list);
				int recordCount = list.size();
				int pageCount = (recordCount / pageSize);
			      int mod = recordCount % pageSize;
			      if (mod != 0)
			    	 pageCount += 1;
				json = "{pager:{pageCount:" + pageCount + ",recordCount:" + list.size()
						+ "},data:" + json + "}";
				printResult(this.G_SUCCESS, json);
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    return null;
	}
}
