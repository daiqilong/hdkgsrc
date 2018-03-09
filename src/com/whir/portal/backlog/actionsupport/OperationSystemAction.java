package com.whir.portal.backlog.actionsupport;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import net.sf.hibernate.HibernateException;

import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class OperationSystemAction extends BaseActionSupport{
	
	// action配置文件路径  
	private static final String ACTIONPATH = "refresh.properties";  
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(OperationSystemAction.class.getName());
	/**
	 * 运维系统待办
	 */
	public String operationSystemPage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "operationSystemPage";
	}
	/**
	 * 查找运维系统待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> operationSystemListData(String userName,int currentPage,int pageSize) throws HibernateException, SQLException {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("ready");
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
		      client.setTimeout(4000);
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(res!=null && !"".equals(res)){
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      JacksonUtil util = new JacksonUtil();
				  list2 = util.createResultList(new String[] { "backlogCategory", "backlogContent", "backlogNode" }, list);
		      } else {
		    	  System.out.println("--------获取运维系统待办总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		    	//e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("11",res);
		    }
		return list2;
	}
	/**
	 * 查找培训管理待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer operationSystemPageCount(String userName) throws HibernateException, SQLException {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("ready");
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
		
		int pageCount=0;
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      client.setTimeout(4000);
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      XmlDataParse xmlDataParse = new XmlDataParse();
		      if(res!=null && !"".equals(res)){
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      pageCount=Integer.parseInt(listStr);
		      } else {
		    	  System.out.println("--------获取运维系统待办总记录数据-------"+0);
		      }
		    }
		    catch (Exception e) {
		    	//e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("11",res);
		    }
		return pageCount;
	}
}