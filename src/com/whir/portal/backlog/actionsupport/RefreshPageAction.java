package com.whir.portal.backlog.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.PropertiesUtils;
import com.whir.evo.weixin.util.XmlHelper;

public class RefreshPageAction extends BaseActionSupport{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8777838403430447258L;
	private static Logger logger = Logger.getLogger(RefreshPageAction.class.getName());
	
	/**
	 * 判断代办页面是否刷新
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String judgeRefresh() {
		String module = request.getParameter("module");
		String currentTab = request.getParameter("currentTab");
		String userName = request.getParameter("userName").toUpperCase();
		String refreshFlag="0";
		String sql ="SELECT T.REFRESHFLAG FROM REFRESH T WHERE T.SYSTEMNAME='"+module+"' AND T.USERNAME='"+userName+"'";
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
	    	while(rs.next()){
	    		refreshFlag = rs.getString(1);
	    	}
		}catch (SQLException ex) {
			ex.printStackTrace();
			try{
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null){
		        	conn.close();
		        }
		     }catch (SQLException localSQLException1){
		     }
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
	    int flag = Integer.parseInt(refreshFlag);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"flag\":\"" + flag + "\"},");
	    if(currentTab!=null && !"".equals(currentTab)){
	    	json.append("{\"currentTab\":\"" + Integer.parseInt(currentTab) + "\"}");
	    } else {
	    	json.delete(json.length()-1, json.length());
	    }
	    json.append("]");

	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * 改变代办页面刷新值
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public void changeRefreshFlag() {
		String module = request.getParameter("module");
		String userName = request.getParameter("userName").toUpperCase();
		String sql ="UPDATE REFRESH T SET T.REFRESHFLAG='0' WHERE T.SYSTEMNAME='"+module+"' AND T.USERNAME='"+userName+"'";
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	stmt.executeQuery(sql);
		}catch (SQLException ex) {
			ex.printStackTrace();
			try{
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null){
		        	conn.close();
		        }
		     }catch (SQLException localSQLException1){
		     }
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
	}
	
	/**
	 * 取消按钮
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String cancleButton() {
		String reportid = request.getParameter("reportid");
		String userName = request.getParameter("userName");
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
		Element reportidElement = parameter.addElement("reportid");
		reportidElement.setText(reportid);
		
		String res = null;
		String results = "";
		String description = "";
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      System.out.println("send::::"+send);
		      Object[] returnResults = client.invoke("CancelNotice", new Object[] { send });
		      res = (String)returnResults[0];
		      if(res!=null && !"".equals(res)){
		    	  description = XmlHelper.getElement(res, "//description");
		    	  results = XmlHelper.getElement(res, "//result");
		      }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		      res = e.getMessage();
		    }
		    StringBuffer json = new StringBuffer("[");
		    json.append("{\"results\":\"" + results + "\"},");
	    	json.append("{\"description\":\"" + description + "\"}");
		    json.append("]");

		    this.response.setContentType("text/plain;charSet=UTF-8");
		    this.response.setCharacterEncoding("UTF-8");
		    try {
		      PrintWriter pw = this.response.getWriter();
		      pw.print(json);
		      pw.close();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    return null;
	}
}