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
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class TrainManageAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(TrainManageAction.class.getName());
	/*
	 * 培训管理待办
	 */
	public String trainManagePage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "trainManagePage";
	}
	/**
	 * 查找培训管理待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> trainManageListData(String userName,int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("12");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("px_backlog_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("to_do");
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
					  list2 = util.createResultList(new String[] { "trainName", "auditState", "trainAddress", "dataLink" }, list);
			      } else {
			    	  System.out.println("--------获取培训管理待办总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      //e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("12",res);
			    }
			return list2;
		}
	}
	/**
	 * 查找培训管理待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer trainManagePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("12");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("px_backlog_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("to_do");
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
			    	  System.out.println("--------获取培训管理待办总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			    	//e.printStackTrace();
				      res = e.getMessage();
				      MonitorServerBD monitorServerBD = new MonitorServerBD();
				      monitorServerBD.putErrorToDataBase("12",res);
			    }
			return pageCount;
		}
	}
}