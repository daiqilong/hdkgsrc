package com.whir.portal.backlog.actionsupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import net.sf.hibernate.HibernateException;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.basedata.bd.BackLogBD;
import com.whir.portal.basedata.bd.MonitorServerBD;

public class SecureManageAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(SecureManageAction.class.getName());
	
	/**
	 * 安管系统待办
	 */
	public String secureManagePage() {
		HttpSession session = request.getSession();
		this.request.setAttribute("userName", session.getAttribute("userAccount"));
		this.request.setAttribute("curPage", "1");
	    return "secureManagePage";
	}
	/**
	 * 查找安管系统待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> secureManageListData(String userName,int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("10");
		if("1".equals(runCondition)){
			return list2;
		} else {
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
			Element flagElement = parameter.addElement("flag");
			flagElement.setText("1");
			Element pageSizeElement = parameter.addElement("pageSize");
			pageSizeElement.setText(Integer.toString(pageSize));
			Element currentPageElement = parameter.addElement("currentPage");
			currentPageElement.setText(Integer.toString(currentPage));
			
			
			String str="";
			try {
				  String send = "<input>"+rec.asXML().split("\\<input>")[1];
				  str = getInterfaceData(send,wsdl_address,"getMyTask");
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(str!=null && !"".equals(str)){
			    	  List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(str);
			    	  JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "title", "demandFinishDate", "establishDate","backlogURL","backlogType" }, list);
			      } else {
			    	  System.out.println("--------获取安管系统待办总记录数据--------"+0);
			      }
			    }catch (Exception e) {
			    	//e.printStackTrace();
				      str = e.getMessage();
				      MonitorServerBD monitorServerBD = new MonitorServerBD();
				      monitorServerBD.putErrorToDataBase("10",str);
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
	public Integer secureManagePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		BackLogBD backLogBD = new BackLogBD();
		String runCondition = backLogBD.getSystemRunCondition("10");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
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
			Element flagElement = parameter.addElement("flag");
			flagElement.setText("0");
			Element pageSizeElement = parameter.addElement("pageSize");
			pageSizeElement.setText("6");
			Element currentPageElement = parameter.addElement("currentPage");
			currentPageElement.setText("1");
			
			String str="";
			try {
				  String send = "<input>"+rec.asXML().split("\\<input>")[1];
				  str = getInterfaceData(send,wsdl_address,"getMyTask");
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(str!=null && !"".equals(str)){
				      String listStr = xmlDataParse.parseXmlRecord(str);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取安管系统待办总记录数据--------"+0);
			      }
			    }catch (Exception e) {
			    	//e.printStackTrace();
				      str = e.getMessage();
				      MonitorServerBD monitorServerBD = new MonitorServerBD();
				      monitorServerBD.putErrorToDataBase("10",str);
			    }
			return pageCount;
		}
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