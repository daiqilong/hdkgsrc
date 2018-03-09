package com.whir.portal.backlog.actionsupport;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;

import net.sf.hibernate.HibernateException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dofull.huadong.g51.TodoWorkItemWSForEastChina;
import com.dofull.huadong.g51.TodoWorkItemWSForEastChina_Service;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class CapitalConstructionAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(CapitalConstructionAction.class.getName());
	/**
	 * 基建待办
	 */
	public String capitalConstructionPage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "capitalConstructionPage";
	}

	
	/**
	 * 查找基建待办待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer capitalConstructionPageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("14");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jj_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("58598301617b958a8b8343b731d0b931");
			Element parameter = input.addElement("parameter");
			Element useridElement = parameter.addElement("userid");
			if(userName==null || userName.equals("")){
				useridElement.setText("");
			}else{
				useridElement.setText(userName);
			}
			Element flagElement = parameter.addElement("flag");
			flagElement.setText("0");
			Element pageSizeElement = parameter.addElement("pageSize");
			pageSizeElement.setText("6");
			Element currentPageElement = parameter.addElement("currentPage");
			currentPageElement.setText("1");
			
			String res = null;
			try {
				  String inputStr = "<input>"+rec.asXML().split("\\<input>")[1];
				  URL url = new URL(wsdl_address);
				  QName serviceName = new QName("http://g51.huadong.dofull.com/", "TodoWorkItemWSForEastChina");  
				  TodoWorkItemWSForEastChina_Service client = new TodoWorkItemWSForEastChina_Service(url,serviceName);
				  TodoWorkItemWSForEastChina port = client.getTodoWorkItemWSForEastChinaPort();
				  res = port.getTodoItems(inputStr);
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr.trim());
				      System.out.println("--------获取基建待办总记录数据--------"+pageCount);
			      } else {
			    	  System.out.println("--------获取基建待办总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("14",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找基建待办待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> capitalConstructionData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("14");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jj_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("58598301617b958a8b8343b731d0b931");
			Element parameter = input.addElement("parameter");
			Element useridElement = parameter.addElement("userid");
			if(userName==null || userName.equals("")){
				useridElement.setText("");
			}else{
				useridElement.setText(userName);
			}
			Element flagElement = parameter.addElement("flag");
			flagElement.setText("1");
			Element pageSizeElement = parameter.addElement("pageSize");
			pageSizeElement.setText(Integer.toString(pageSize));
			Element currentPageElement = parameter.addElement("currentPage");
			if(currentPage==0){
				currentPageElement.setText("1");
			} else {
				currentPageElement.setText(Integer.toString(currentPage));
			}
			
			String res = null;
			try {
				String inputStr = "<input>"+rec.asXML().split("\\<input>")[1];
				  URL url = new URL(wsdl_address);
				  QName serviceName = new QName("http://g51.huadong.dofull.com/", "TodoWorkItemWSForEastChina");  
				  TodoWorkItemWSForEastChina_Service client = new TodoWorkItemWSForEastChina_Service(url,serviceName);
				  TodoWorkItemWSForEastChina port = client.getTodoWorkItemWSForEastChinaPort();
				  res = port.getTodoItems(inputStr);
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "filetype", "dataLink", "sum","dateTime", "sendPeople","status","colour" }, list);
			      } else {
			    	  System.out.println("--------获取基建申请待办数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("14",res);
			    }
			return list2;
		}
	}
}