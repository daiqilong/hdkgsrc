package com.whir.portal.backlog.actionsupport;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import net.sf.hibernate.HibernateException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.jiuqi.kgj.workitem.send.webservice.todoservice.ISynTodoInfoWs;
import com.jiuqi.kgj.workitem.send.webservice.todoservice.ISynTodoInfoWs_Service;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class OnlineReimburseAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(OnlineReimburseAction.class.getName());
	/**
	 * 网报待办
	 */
	public String onlineReimbursePage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "onlineReimbursePage";
	}
	/**
	 * 新网报待办
	 */
	public String newOnlineReimbursePage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "newOnlineReimbursePage";
	}
	/**
	 * 查找网报待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> onlineReimburseListData(String userName,int currentPage,int pageSize) throws HibernateException, SQLException {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("wb_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("1qazse4!@#$");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element interfaceTypeElement = parameter.addElement("interfaceType");
		interfaceTypeElement.setText("backlog");
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("1");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String res = null;
		try {
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
			  call.setOperationName("getdata1");// WSDL里面描述的接口名称  
			  call.setSOAPActionURI("urn:getdata1");  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  call.setTimeout(4000);
			  String string = rec.asXML();
	          res = (String) call.invoke(new Object[] { string });
		      if(null != res && !"".equals(res)){ 
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      JacksonUtil util = new JacksonUtil();
				  list2 = util.createResultList(new String[] { "title", "dataLink", "dmessage"}, list);
		      } else {
		    	  System.out.println("--------获取网报待办总记录数据---------"+0);
		      }
		    }
		    catch (Exception e) {
		      //e.printStackTrace();
		      res = e.getMessage();
		    }
		return list2;
	}
	/**
	 * 查找找网报待办待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer onlineReimbursePageCount(String userName) {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("wb_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("1qazse4!@#$");
		Element parameter = input.addElement("parameter");
		Element userNameElement = parameter.addElement("userName");
		if(userName==null || userName.equals("")){
			userNameElement.setText("");
		}else{
			userNameElement.setText(userName);
		}
		Element interfaceTypeElement = parameter.addElement("interfaceType");
		interfaceTypeElement.setText("backlog");
		Element flagElement = parameter.addElement("flag");
		flagElement.setText("0");
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText("6");
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText("1");
		
		int pageCount=0;
		String res = null;
		try {
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
			  call.setOperationName("getdata1");// WSDL里面描述的接口名称  
			  call.setSOAPActionURI("urn:getdata1");  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  call.setTimeout(4000);
			  String string = rec.asXML();
	          res = (String) call.invoke(new Object[] { string });  
		      if(null != res && !"".equals(res)){ 
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      pageCount=Integer.parseInt(listStr);
		      } else {
		    	  System.out.println("--------获取网报待办总记录数据---------"+0);
		      }
		    }
		    catch (Exception e) {
		      //e.printStackTrace();
		      res = e.getMessage();
		      if(res.contains("connect timed out")){
		    	  System.out.println("网报接口异常:接口连接超时，请检查接口地址是否通!"); 
		      } else {
		    	  System.out.println("网报接口异常："+res);
		      }
		    }
		return pageCount;
	}
	
	/**
	 * 查找找新网报待办待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer newOnlineReimbursePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("9");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqwb_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("58598301617b958a8b8343b731d0b931");
			Element parameter = input.addElement("parameter");
			Element userNameElement = parameter.addElement("userName");
			if(userName==null || userName.equals("")){
				userNameElement.setText("");
			}else{
				userNameElement.setText(userName.toUpperCase());
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
				  QName serviceName = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");  
				  ISynTodoInfoWs_Service client = new ISynTodoInfoWs_Service(url,serviceName);
				  ISynTodoInfoWs port = client.getSynTodoInfoWsImplPort();
				  res = port.getTodoData(inputStr);
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取久其网报申请待办总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("9",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找找新网报待办待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> todoListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("9");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqwb_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
			key.addText("58598301617b958a8b8343b731d0b931");
			Element parameter = input.addElement("parameter");
			Element userNameElement = parameter.addElement("userName");
			if(userName==null || userName.equals("")){
				userNameElement.setText("");
			}else{
				userNameElement.setText(userName.toUpperCase());
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
				  QName serviceName = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");  
				  ISynTodoInfoWs_Service client = new ISynTodoInfoWs_Service(url,serviceName);
				  ISynTodoInfoWs port = client.getSynTodoInfoWsImplPort();
				  res = port.getTodoData(inputStr);
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "receiptNum", "receiptDate", "agent","receiptGroup", "dataLink" }, list);
			      } else {
			    	  System.out.println("--------获取久其网报申请待办数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("9",res);
			    }
			return list2;
		}
	}
}