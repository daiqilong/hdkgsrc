package com.whir.portal.backlog.actionsupport;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ParameterMode;

import net.sf.hibernate.HibernateException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class ContractManageAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(ContractManageAction.class.getName());
	/**
	 * 合同管理
	 */
	public String contractManagePage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "contractManagePage";
	}
	/**
	 * 合同管理数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> contractManageListData(String userName,int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("13");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("htgl_wsdl");
			
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
				  Service service = new Service(); 
				  Call call = (Call) service.createCall();  
				  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
				  call.setOperationName("getToDoList");// WSDL里面描述的接口名称  
				  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
				  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
				  call.setTimeout(4000);
				  String string = rec.asXML();
		          res = (String) call.invoke(new Object[] { string });
			      if(null != res && !"".equals(res)){ 
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "title", "dataLink", "draftDepartment","filetype"}, list);
			      } else {
			    	  System.out.println("--------获取合同管理总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      //e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("13",res);
			    }
			return list2;
		}
	}
	/**
	 * 查找合同待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer contractManagePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("13");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("htgl_wsdl");
			
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
				  Service service = new Service(); 
				  Call call = (Call) service.createCall();  
				  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
				  call.setOperationName("getToDoList");// WSDL里面描述的接口名称  
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
			    	  System.out.println("--------获取合同待办总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      //e.printStackTrace();
			      res = e.getMessage();
			      MonitorServerBD monitorServerBD = new MonitorServerBD();
			      monitorServerBD.putErrorToDataBase("13",res);
			    }
			return pageCount;
		}
	}
}