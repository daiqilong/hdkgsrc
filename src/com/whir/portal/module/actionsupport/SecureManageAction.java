package com.whir.portal.module.actionsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;

public class SecureManageAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957611521728273579L;
	private static Logger logger = Logger.getLogger(SecureManageAction.class.getName());
	
	/**
	 * 风险通报模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String riskReportList()
	  {
		HttpSession session = this.request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
	    return "riskReportList";
	  }
	
	/**
	 * 查找风险通报数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String riskReportListData() {

		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
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
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = output.addElement("parameter");
		String informType = request.getParameter("informType");
		String userName = request.getParameter("userName");
		String title = request.getParameter("title");
		String creationTimeStart = request.getParameter("creationTimeStart");
		String creationTimeEnd = request.getParameter("creationTimeEnd");
		
		Element informTypeElement = parameter.addElement("informType");
		if(informType==null || informType.equals("")){
			informTypeElement.setText("");
		}else{
			informTypeElement.setText(informType);
		}
		Element titleElement = parameter.addElement("title");
		if(title==null || title.equals("")){
			titleElement.setText("");
		}else{
			titleElement.setText(title);
		}
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element creationTimeStartElement = parameter.addElement("creationTimeStart");
		if(creationTimeStart==null || creationTimeStart.equals("")){
			creationTimeStartElement.setText("");
		}else{
			creationTimeStartElement.setText(creationTimeStart);
		}
		Element creationTimeEndElement = parameter.addElement("creationTimeEnd");
		if(creationTimeEnd==null || creationTimeEnd.equals("")){
			creationTimeEndElement.setText("");
		}else{
			creationTimeEndElement.setText(creationTimeEnd);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getRiskReport");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!="" && !"".equals(str)){
		    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
			      String json = util.writeNewArrayJSON(
							new String[] {"seqNum", "title", "informType",
									"publishDate", "abateDate", "publisher",
									"state"}, list);
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
		      str = e.getMessage();
		    }
		    return null;
	}
	
	
	/**
	 * 安全检查模块 
	 *  @param request
	 *            HttpServletRequest
	 */
	public String secureExamineList()
	  {
		HttpSession session = this.request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
	    return "secureExamineList";
	  }
	
	/**
	 * 查找安全检查数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String secureExamineListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
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
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = output.addElement("parameter");
		String number = request.getParameter("number");
		String userName = request.getParameter("userName");
		String title = request.getParameter("title");
		String inspectUnit = request.getParameter("inspectUnit");
		String inspectType = request.getParameter("inspectType");
		
		Element numberElement = parameter.addElement("number");
		if(number==null || number.equals("")){
			numberElement.setText("");
		}else{
			numberElement.setText(number);
		}
		Element titleElement = parameter.addElement("title");
		if(title==null || title.equals("")){
			titleElement.setText("");
		}else{
			titleElement.setText(title);
		}
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element inspectUnitElement = parameter.addElement("inspectUnit");
		if(inspectUnit==null || inspectUnit.equals("")){
			inspectUnitElement.setText("");
		}else{
			inspectUnitElement.setText(inspectUnit);
		}
		Element inspectTypeElement = parameter.addElement("inspectType");
		if(inspectType==null || inspectType.equals("")){
			inspectTypeElement.setText("");
		}else{
			inspectTypeElement.setText(inspectType);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getSecurityCheck");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!="" && !"".equals(str)){
		    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
			      String json = util.writeNewArrayJSON(
							new String[] {"seqNum","number", "title", 
									"inspectUnit", "fillUnit", "majorType",
									"inspectType","planInspectDate","projectDepartment","state"}, list);
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
		      str = e.getMessage();
		    }
		    return null;
	}
	
	/**
	 * 航空安全信息模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String secureInformationList()
	  {
		HttpSession session = this.request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
	    return "secureInformationList";
	  }
	
	/**
	 * 查找航空安全信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String secureInformationListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
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
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = output.addElement("parameter");
		String number = request.getParameter("number");
		String userName = request.getParameter("userName");
		String title = request.getParameter("title");
		String eventOccurDateStart = request.getParameter("eventOccurDateStart");
		String eventRelatedUnitEnd = request.getParameter("eventRelatedUnitEnd");
		String eventType = request.getParameter("eventType");
		
		Element numberElement = parameter.addElement("number");
		if(number==null || number.equals("")){
			numberElement.setText("");
		}else{
			numberElement.setText(number);
		}
		Element titleElement = parameter.addElement("title");
		if(title==null || title.equals("")){
			titleElement.setText("");
		}else{
			titleElement.setText(title);
		}
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element eventOccurDateStartElement = parameter.addElement("eventOccurDateStart");
		if(eventOccurDateStart==null || eventOccurDateStart.equals("")){
			eventOccurDateStartElement.setText("");
		}else{
			eventOccurDateStartElement.setText(eventOccurDateStart);
		}
		Element eventRelatedUnitEndElement = parameter.addElement("eventRelatedUnitEnd");
		if(eventRelatedUnitEnd==null || eventRelatedUnitEnd.equals("")){
			eventRelatedUnitEndElement.setText("");
		}else{
			eventRelatedUnitEndElement.setText(eventRelatedUnitEnd);
		}
		Element eventTypeElement = parameter.addElement("eventType");
		if(eventType==null || eventType.equals("")){
			eventTypeElement.setText("");
		}else{
			eventTypeElement.setText(eventType);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getAirSecurityInfo");
			  System.out.println("1111"+str);
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!="" && !"".equals(str)){
		    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
			      String json = util.writeNewArrayJSON(
							new String[] {"seqNum","number", "title", 
									"eventOccurDate", "eventRelatedUnit", "appearUnit",
									"state","eventType","dutyType"}, list);
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
		      str = e.getMessage();
		    }
		    return null;
	}
	
	/**
	 * 风险管理模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String riskManageList()
	  {
	    return "riskManageList";
	  }
	
	/**
	 * 查找风险管理模块数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String riskManageListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
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
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = output.addElement("parameter");
		String number = request.getParameter("number");
		String userName = request.getParameter("userName");
		String title = request.getParameter("title");
		String projectDateStart = request.getParameter("projectDateStart");
		String projectDateEnd = request.getParameter("projectDateEnd");
		
		Element numberElement = parameter.addElement("number");
		if(number==null || number.equals("")){
			numberElement.setText("");
		}else{
			numberElement.setText(number);
		}
		Element titleElement = parameter.addElement("title");
		if(title==null || title.equals("")){
			titleElement.setText("");
		}else{
			titleElement.setText(title);
		}
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element projectDateStartElement = parameter.addElement("projectDateStart");
		if(projectDateStart==null || projectDateStart.equals("")){
			projectDateStartElement.setText("");
		}else{
			projectDateStartElement.setText(projectDateStart);
		}
		Element projectDateEndElement = parameter.addElement("projectDateEnd");
		if(projectDateEnd==null || projectDateEnd.equals("")){
			projectDateEndElement.setText("");
		}else{
			projectDateEndElement.setText(projectDateEnd);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getRiskManage");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!="" && !"".equals(str)){
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","number", "title", 
									"riskManageOrigin", "riskManageRequire", "projectDate",
									"appearUnit","state"}, list);
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
		      str = e.getMessage();
		    }
		    return null;
	}
	
	
	/**
	 * 安全评估模块
	 *  @param request
	 *            HttpServletRequest
	 */
	public String secureAssessList()
	  {
	    return "secureAssessList";
	  }
	
	/**
	 * 查找安全评估模块数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String secureAssessListData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
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
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = output.addElement("parameter");
		String number = request.getParameter("number");
		String userName = request.getParameter("userName");
		String title = request.getParameter("title");
		String projectDateStart = request.getParameter("projectDateStart");
		String projectDateEnd = request.getParameter("projectDateEnd");
		
		Element numberElement = parameter.addElement("number");
		if(number==null || number.equals("")){
			numberElement.setText("");
		}else{
			numberElement.setText(number);
		}
		Element titleElement = parameter.addElement("title");
		if(title==null || title.equals("")){
			titleElement.setText("");
		}else{
			titleElement.setText(title);
		}
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element projectDateStartElement = parameter.addElement("projectDateStart");
		if(projectDateStart==null || projectDateStart.equals("")){
			projectDateStartElement.setText("");
		}else{
			projectDateStartElement.setText(projectDateStart);
		}
		Element projectDateEndElement = parameter.addElement("projectDateEnd");
		if(projectDateEnd==null || projectDateEnd.equals("")){
			projectDateEndElement.setText("");
		}else{
			projectDateEndElement.setText(projectDateEnd);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getSecurityAssessment");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!="" && !"".equals(str)){
		    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"seqNum","number", "title",
									"safetyAssessOrigin", "safetyAssessRequire", "projectDate",
									"appearUnit","state"}, list);
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
		      str = e.getMessage();
		    }
		    return null;
	}
	
	public String getInterfaceData(String send, String wsdl_address,String method) throws IOException, DocumentException {
		String result = "";
		StringBuffer sb = new StringBuffer();
		String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://service.webservice.com/\"><soapenv:Header/><soapenv:Body><ser:"+method+"><!--Optional:--><arg0><![CDATA["+send+"]]></arg0></ser:"+method+"></soapenv:Body></soapenv:Envelope>";
        URL url = new URL(wsdl_address);//传送的URL地址  
        URLConnection conn = url.openConnection();//声明一个URL链接  
        conn.setUseCaches(false);  
        conn.setDoInput(true);  
        conn.setDoOutput(true);  
        conn.setRequestProperty("Content-Length", Integer.toString(soap
                .length()));  
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");  
        conn.setRequestProperty("SOAPAction",  
                "");//POST请求的方法名  
        OutputStream os = conn.getOutputStream();//声明一个输出流  
        OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");  
        osw.write(soap);//读取POST请求的内容  
        osw.flush();  
        osw.close(); 
        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
		String line = "";
		StringBuffer buffer=new StringBuffer();
		while ((line = br.readLine()) != null){
		    buffer.append(line);
		}	
		Document dom = DocumentHelper.parseText(buffer.toString());
		List<Element> ite = dom.content();
		for(Element aa:ite){
			sb.append(aa.getStringValue());
		}
		if(sb.toString()!=null && !"".equals(sb.toString())){
			result= sb.toString().replaceAll("&", "&amp;");
		}
		return result;
	}
}
