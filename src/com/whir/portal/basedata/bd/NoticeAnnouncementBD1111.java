package com.whir.portal.basedata.bd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.sf.hibernate.HibernateException;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.ezoffice.information.channelmanager.po.InformationChannelPO;
import com.whir.org.manager.bd.ManagerBD;


public class NoticeAnnouncementBD1111 extends HibernateBase {
	
	public List<Map<String, Object>> getNoticeList(String userId,String userName,String channelId,String orgId,String orgIdString,String domainId) throws SQLException, HibernateException{
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    StringBuffer sql = new StringBuffer();
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        sql.append("SELECT WHIR$SET_NOTICE_MODULE,WHIR$SET_NOTICE_SORT FROM WHIR$SET_NOTICE WHERE WHIR$SET_NOTICE_USERID=? ORDER BY WHIR$SET_NOTICE_SORT ASC");
	        pstmt = conn.prepareStatement(sql.toString());
	        pstmt.setString(1, userId);
	        rs = pstmt.executeQuery();
	        String noticeSort = "";
	        String noticeModule = "";
	        String noticeModuleName = "";
	        boolean flag = true;
	        int noticeNum=0;
	        while(rs.next()){
	        	flag = false;
	        	Map<String,Object> map = new HashMap<String,Object>();
	        	noticeSort = rs.getString("WHIR$SET_NOTICE_SORT");
	        	noticeModule = rs.getString("WHIR$SET_NOTICE_MODULE");
	        	if("1".equals(noticeModule)){
	        		noticeNum = oaPageCount(userName);
	        		noticeModuleName = "OA通知";
	        	} else if("2".equals(noticeModule)){
	        		noticeNum = mhPageCount(channelId, userId, orgId, orgIdString, domainId);
	        		noticeModuleName = "门户通知";
	        	} else if("3".equals(noticeModule)){
	        		noticeNum = anguanPageCount(userName);
	        		noticeModuleName = "安管通知";
	        	} else if("4".equals(noticeModule)){
	        		noticeNum = yunweiPageCount(userName);
	        		noticeModuleName = "运维通知";
	        	} else if("5".equals(noticeModule)){
	        		noticeNum = peixunPageCount(userName);
	        		noticeModuleName = "培训通知";
	        	}
	        	map.put("noticeSort", noticeSort);
	        	map.put("noticeModule", noticeModule);
	        	map.put("noticeNum", noticeNum);
	        	map.put("noticeModuleName", noticeModuleName);
	        	list.add(map);
	        }
	        if(flag){
	        	Map<String,Object> map1 = new HashMap<String,Object>();
	        	Map<String,Object> map2 = new HashMap<String,Object>();
	        	Map<String,Object> map3 = new HashMap<String,Object>();
	        	Map<String,Object> map4 = new HashMap<String,Object>();
	        	Map<String,Object> map5 = new HashMap<String,Object>();
	        	
	        	map1.put("noticeSort", 1);
	        	map1.put("noticeModule", 1);
	        	map1.put("noticeNum", oaPageCount(userName));
	        	map1.put("noticeModuleName", "OA通知");
	        	list.add(map1);
	        	map2.put("noticeSort", 2);
	        	map2.put("noticeModule", 2);
	        	map2.put("noticeNum", mhPageCount(channelId, userId, orgId, orgIdString, domainId));
	        	map2.put("noticeModuleName", "门户通知");
	        	list.add(map2);
	        	map3.put("noticeSort", 3);
	        	map3.put("noticeModule", 3);
	        	map3.put("noticeNum", anguanPageCount(userName));
	        	map3.put("noticeModuleName", "安管通知");
	        	list.add(map3);
	        	map4.put("noticeSort", 4);
	        	map4.put("noticeModule", 4);
	        	map4.put("noticeNum", yunweiPageCount(userName));
	        	map4.put("noticeModuleName", "运维通知");
	        	list.add(map4);
	        	map5.put("noticeSort", 5);
	        	map5.put("noticeModule", 5);
	        	map5.put("noticeNum", peixunPageCount(userName));
	        	map5.put("noticeModuleName", "培训通知");
	            list.add(map5);
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
	    return list;
	}
	
	/**
	 * 查找培训管理通知公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> peixunListData(String userName, int currentPage,int pageSize) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("px_notice_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("Notive");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(res!=null && !"".equals(res)){
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      JacksonUtil util = new JacksonUtil();
				  list2 = util.createResultList(new String[] { "title", "receiver", "startDate","endDate", "dataLink" }, list);
		      } else {
		    	  System.out.println("--------获取培训管理通知公告总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		return list2;
	}
	
	/**
	 * 查找培训管理通知公告总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer peixunPageCount(String userName) {
//		Properties properties = PropertiesUtils.getProperties();
//		String wsdl_address = properties.getProperty("px_notice_wsdl");
//		
//		Document rec = DocumentHelper.createDocument();
//		rec.setXMLEncoding("UTF-8");
//		Element input = rec.addElement("input");
//		Element key = input.addElement("key");
//		key.addText("Notive");
//		Element parameter = input.addElement("parameter");
//		Element userNameElement = parameter.addElement("userName");
//		if(userName==null || userName.equals("")){
//			userNameElement.setText("");
//		}else{
//			userNameElement.setText(userName);
//		}
//		Element pageSizeElement = parameter.addElement("pageSize");
//		pageSizeElement.setText("6");
//		Element currentPageElement = parameter.addElement("currentPage");
//		currentPageElement.setText("1");
//		
		int pageCount=0;
//		String res = null;
//		try {
//		      Client client = new Client(new URL(wsdl_address));
//		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
//		      Object[] results = client.invoke("service", new Object[] { send });
//		      res = (String)results[0];
//		      if(res!=null && !"".equals(res)){
//			      XmlDataParse xmlDataParse = new XmlDataParse();
//			      String listStr = xmlDataParse.parseXmlRecord(res);
//			      pageCount=Integer.parseInt(listStr);
//		      } else {
//		    	  System.out.println("--------获取培训管理通知公告总记录数据-------"+0);
//		      }
//		    }
//		    catch (Exception e) {
//		      e.printStackTrace();
//		      res = e.getMessage();
//		    }
		return pageCount;
	}
	
	
	/**
	 * 查找安全管理通知公告总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer anguanPageCount(String userName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element nowDateElement = parameter.addElement("publishDate");
		nowDateElement.setText("");
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("0");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText("6");
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText("1");
		
		int pageCount=0;
		String str="";
		try {
			  String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getSecurityManage");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!=null && !"".equals(str)){
			      String listStr = xmlDataParse.parseXmlRecord(str);
			      pageCount=Integer.parseInt(listStr);
		      } else {
		    	  System.out.println("--------获取安管系统通知公告总记录数据--------"+0);
		      }
		    }catch (Exception e) {
		      e.printStackTrace();
		      str = e.getMessage();
		    }
		return pageCount;
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
	
	/**
	 * 查找安管系统通知公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> anguanListData(String userName, int currentPage,int pageSize) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("anquan_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("DFFD512F3C274EC11AF53753FC82B483");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element nowDateElement = parameter.addElement("publishDate");
		nowDateElement.setText("");
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("1");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String str="";
		try {
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			  str = getInterfaceData(send,wsdl_address,"getSecurityManage");
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(str!=null && !"".equals(str)){
		    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			      JacksonUtil util = new JacksonUtil();
			      list2 = util.createResultList(new String[] { "title", "dataLink", "noticeType","publishDate", "publisher" }, list);
		      } else {
		    	  System.out.println("--------获取安管系统通知公告总记录数据--------"+0);
		      }
	    }catch (Exception e) {
	      e.printStackTrace();
	      str = e.getMessage();
	    }
		return list2;
	}
	
	
	/**
	 * 查找运维系统通知公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> yunweiListData(String userName, int currentPage,int pageSize) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("notice");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(res!=null && !"".equals(res)){
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      JacksonUtil util = new JacksonUtil();
				  list2 = util.createResultList(new String[] { "informName", "publisher", "release","recipient", "dataLink" }, list);
		      } else {
		    	  System.out.println("--------获取运维系统通知公告总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		return list2;
	}
	/**
	 * 查找培训管理通知公告总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer yunweiPageCount(String userName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("notice");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText("6");
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText("1");
		
		int pageCount=1;
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !"".equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      pageCount=Integer.parseInt(listStr);
		      } else {
		    	  System.out.println("--------获取运维系统通知公告总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		return pageCount;
	}
	
	
	/**
	 * 查找OA系统通知公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> oaListData(String userName, int currentPage,int pageSize) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("oa_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		//whir2011
		key.addText("dffd512f3c274ec11af53753fc82b483");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("1");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("GetNotice", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !"".equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      JacksonUtil util = new JacksonUtil();
				  list2 = util.createResultList(new String[] {  "title", "dataLink", "publishTime","status" }, list);
		      } else {
		    	  System.out.println("--------获取OA系统通知公告总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		return list2;
	}
	/**
	 * 查找OA通知公告总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer oaPageCount(String userName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("oa_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		//whir2011
		key.addText("dffd512f3c274ec11af53753fc82b483");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("0");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText("6");
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText("1");
		
		int pageCount=1;
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("GetNotice", new Object[] { send });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(res!=null && !"".equals(res)){
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      pageCount=Integer.parseInt(listStr);
		      } else {
		    	  System.out.println("--------获取OA系统通知公告总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		return pageCount;
	}
	
	
	/**
	 * 查找门户通知公告总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer mhPageCount(String channelId, String userId, String orgId, String orgIdString, String domainId){
		int count=0;
	    StringBuffer buffer = new StringBuffer();
	    try {
			begin();
		} catch (HibernateException e1) {
			e1.printStackTrace();
		}
	    String sideLineOrg = "";
	    String[] sarr = (String[])null;
	    try
	    {
	      List sideOrgList = this.session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" + 
	        userId).list();
	      if ((sideOrgList != null) && (sideOrgList.size() > 0)) {
	        sideLineOrg = (String)sideOrgList.get(0);
	        if ((sideLineOrg != null) && (!sideLineOrg.equals("")) && (sideLineOrg.length() > 2)) {
	          sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
	          sarr = sideLineOrg.split("\\*\\*");
	        }
	      }

	      InformationChannelPO informationChannelPO = new InformationChannelPO();
	      Iterator it = this.session.createQuery("select po from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where po.channelId=" + 
	        channelId).iterate();
	      if (it.hasNext()) {
	        informationChannelPO = (InformationChannelPO)it.next();
	      }

	      orgIdString = "$" + orgIdString + "$";

	      String[] orgIdArray = orgIdString.split("\\$\\$");

	      StringBuffer orgIdStringdgnx = new StringBuffer("0");

	      for (int i = 0; i < orgIdArray.length; i++) {
	        if (!"".equals(orgIdArray[i])) {
	          orgIdStringdgnx.append(",").append(orgIdArray[i]);
	        }
	      }

	      if ((sarr != null) && (sarr.length > 0)) {
	        for (int i = 0; i < sarr.length; i++) {
	          orgIdStringdgnx.append(",").append(sarr[i]);
	        }

	      }

	      StringBuffer channelIdStringdgnx = new StringBuffer();

	      if (informationChannelPO.getIncludeChild() == 0) {
	        channelIdStringdgnx.append(channelId);
	      } else if (informationChannelPO.getIncludeChild() == 1) {
	        channelIdStringdgnx.append("0");

	        List subChanList = this.session.createQuery(" select po.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where po.channelIdString like '" + informationChannelPO.getChannelIdString() + "%'").list();
	        int subchancnt = 0;
	        if (subChanList != null) subchancnt = subChanList.size();
	        for (int k = 0; k < subchancnt; k++) {
	          channelIdStringdgnx.append(",").append(subChanList.get(k).toString());
	        }
	      }

	      boolean hasRight = false;
	      String rightCode = "01*03*03";
	      List rightScopeList = new ManagerBD().getRightScope(userId, rightCode);
	      if ((rightScopeList != null) && (rightScopeList.size() > 0) && (rightScopeList.get(0) != null)) {
	        Object[] obj = (Object[])rightScopeList.get(0);
	        if ("0".equals(obj[0].toString())) {
	          hasRight = true;
	        }

	      }

	      StringBuffer sqlBuf = new StringBuffer();
	      sqlBuf.append("SELECT count(*) as count FROM (  select  ch.CHANNELNAME , inf.INFORMATION_ID , inf.INFORMATIONTITLE ,")
          .append(" inf.INFORMATIONKITS, inf.INFORMATIONISSUETIME, inf.INFORMATIONHEAD, inf.INFORMATIONTYPE,")
          .append(" ch.CHANNELTYPE, ch.CHANNEL_ID, ch.CHANNELSHOWTYPE, inf.titleColor, ch.USERDEFINE,")
          .append(" inf.ISCONF, inf.INFORMATIONCONTENT, inf.INFORMATIONVALIDTYPE, inf.VALIDENDTIME, ch.newInfoDeadLine, inf.informationmodifytime ")
          .append(" from OA_INFORMATION inf,OA_INFORMATIONCHANNEL ch , (")
          .append(" select bbb.information_id as info_id, bbb.channel_id as ch_id ")
          .append(" from oa_information bbb ")
          .append(" where bbb.informationstatus=0 ")
          .append(!hasRight ? " and (bbb.INFORMATIONVALIDTYPE = 0 OR (sysdate BETWEEN bbb.VALIDBEGINTIME AND bbb.VALIDENDTIME)) " : "")
          .append(" and bbb.ispublic=1")
          .append(" and (bbb.channel_id  in (").append(channelIdStringdgnx)
          .append(") or bbb.otherChannel =',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_user_info where user_id='").append(userId).append("'")
          .append(" and (channel_id in (").append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_grp_info, org_user_group ")
          .append(" where group_id=grp_id and emp_id='").append(userId).append("' and (channel_id  in (")
          .append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_org_info ")
          .append(" where (channel_id in (").append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" and org_id in (").append(orgIdStringdgnx).append(")")
          .append(") TT ")
          .append(" where ch.channel_id=inf.channel_id  and  inf.informationstatus=0 ")
          .append(" and inf.information_id=TT.info_id)");
	      
	      Connection conn = this.session.connection();
	      Statement stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sqlBuf.toString());
	      while (rs.next()) {
	    	  count = rs.getInt("count");
	      }
	      rs.close();
	      stmt.close();
	      conn.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return count;
	}
	
	
	/**
	 * 查找门户通知公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> mhPageListData(String channelId, String userId, String orgId, String orgIdString, String domainId,String curPage,String pageSize){
		int max = Integer.parseInt(curPage)*Integer.parseInt(pageSize);
		int min = (Integer.parseInt(curPage)-1)*Integer.parseInt(pageSize)+1;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count=0;
	    StringBuffer buffer = new StringBuffer();
	    try {
			begin();
		} catch (HibernateException e1) {
			e1.printStackTrace();
		}
	    String sideLineOrg = "";
	    String[] sarr = (String[])null;
	    try
	    {
	      List sideOrgList = this.session.createQuery("select eee.sidelineOrg from com.whir.org.vo.usermanager.EmployeeVO eee where eee.empId=" + 
	        userId).list();
	      if ((sideOrgList != null) && (sideOrgList.size() > 0)) {
	        sideLineOrg = (String)sideOrgList.get(0);
	        if ((sideLineOrg != null) && (!sideLineOrg.equals("")) && (sideLineOrg.length() > 2)) {
	          sideLineOrg = sideLineOrg.substring(1, sideLineOrg.length() - 1);
	          sarr = sideLineOrg.split("\\*\\*");
	        }
	      }

	      InformationChannelPO informationChannelPO = new InformationChannelPO();
	      Iterator it = this.session.createQuery("select po from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where po.channelId=" + 
	        channelId).iterate();
	      if (it.hasNext()) {
	        informationChannelPO = (InformationChannelPO)it.next();
	      }

	      orgIdString = "$" + orgIdString + "$";

	      String[] orgIdArray = orgIdString.split("\\$\\$");

	      StringBuffer orgIdStringdgnx = new StringBuffer("0");

	      for (int i = 0; i < orgIdArray.length; i++) {
	        if (!"".equals(orgIdArray[i])) {
	          orgIdStringdgnx.append(",").append(orgIdArray[i]);
	        }
	      }

	      if ((sarr != null) && (sarr.length > 0)) {
	        for (int i = 0; i < sarr.length; i++) {
	          orgIdStringdgnx.append(",").append(sarr[i]);
	        }

	      }

	      StringBuffer channelIdStringdgnx = new StringBuffer();

	      if (informationChannelPO.getIncludeChild() == 0) {
	        channelIdStringdgnx.append(channelId);
	      } else if (informationChannelPO.getIncludeChild() == 1) {
	        channelIdStringdgnx.append("0");

	        List subChanList = this.session.createQuery(" select po.channelId from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po where po.channelIdString like '" + informationChannelPO.getChannelIdString() + "%'").list();
	        int subchancnt = 0;
	        if (subChanList != null) subchancnt = subChanList.size();
	        for (int k = 0; k < subchancnt; k++) {
	          channelIdStringdgnx.append(",").append(subChanList.get(k).toString());
	        }
	      }

	      boolean hasRight = false;
	      String rightCode = "01*03*03";
	      List rightScopeList = new ManagerBD().getRightScope(userId, rightCode);
	      if ((rightScopeList != null) && (rightScopeList.size() > 0) && (rightScopeList.get(0) != null)) {
	        Object[] obj = (Object[])rightScopeList.get(0);
	        if ("0".equals(obj[0].toString())) {
	          hasRight = true;
	        }

	      }

	      StringBuffer sqlBuf = new StringBuffer();
	      sqlBuf.append("SELECT * FROM ( select A.*,rownum rn from (select  ch.CHANNELNAME , inf.INFORMATION_ID , inf.INFORMATIONTITLE ,")
          .append(" inf.INFORMATIONISSUETIME, inf.INFORMATIONTYPE,")
          .append(" ch.CHANNELTYPE,inf.informationissuer")
          .append(" from OA_INFORMATION inf,OA_INFORMATIONCHANNEL ch , (")
          .append(" select bbb.information_id as info_id, bbb.channel_id as ch_id ")
          .append(" from oa_information bbb ")
          .append(" where bbb.informationstatus=0 ")
          .append(!hasRight ? " and (bbb.INFORMATIONVALIDTYPE = 0 OR (sysdate BETWEEN bbb.VALIDBEGINTIME AND bbb.VALIDENDTIME)) " : "")
          .append(" and bbb.ispublic=1")
          .append(" and (bbb.channel_id  in (").append(channelIdStringdgnx)
          .append(") or bbb.otherChannel =',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_user_info where user_id='").append(userId).append("'")
          .append(" and (channel_id in (").append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_grp_info, org_user_group ")
          .append(" where group_id=grp_id and emp_id='").append(userId).append("' and (channel_id  in (")
          .append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" union select info_id as info_id,channel_id as ch_id ")
          .append(" from zl_org_info ")
          .append(" where (channel_id in (").append(channelIdStringdgnx).append(") or otherchannel = ',").append(channelId).append(",')")
          .append(" and org_id in (").append(orgIdStringdgnx).append(")")
          .append(") TT ")
          .append(" where ch.channel_id=inf.channel_id  and  inf.informationstatus=0 ")
          .append(" and inf.information_id=TT.info_id) where rownum<="+max+") where rn>="+min);
	      
	      System.out.println("sqlBuf.toString()::::::"+sqlBuf.toString());
	      Connection conn = this.session.connection();
	      Statement stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sqlBuf.toString());
	      while (rs.next()) {
	    	  Map<String, Object> map = new HashMap<String, Object>();
	    	  String channelName = rs.getString(1);
	    	  String informationId = rs.getString(2);
	    	  String informationTitle = rs.getString(3);
	    	  String informationIssueTime = rs.getString(4);
	    	  String informationType = rs.getString(5);
	    	  String channelType = rs.getString(6);
	    	  String informationIssuer = rs.getString(7);
	    	  map.put("channelName", channelName);
	    	  map.put("informationId", informationId);
	    	  map.put("informationTitle", informationTitle);
	    	  map.put("informationIssueTime", informationIssueTime);
	    	  map.put("informationType", informationType);
	    	  map.put("channelType", channelType);
	    	  map.put("informationIssuer", informationIssuer);
	    	  list.add(map);
	      }
	      rs.close();
	      stmt.close();
	      conn.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return list;
	}
}
