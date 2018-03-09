package com.whir.portal.basedata.bd;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.namespace.QName;

import net.sf.hibernate.HibernateException;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.jiuqi.kgj.workitem.send.webservice.todoservice.ISynTodoInfoWs;
import com.jiuqi.kgj.workitem.send.webservice.todoservice.ISynTodoInfoWs_Service;
import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.portal.backlog.actionsupport.CapitalConstructionAction;
import com.whir.portal.backlog.actionsupport.ContractManageAction;
import com.whir.portal.backlog.actionsupport.OnlineReimburseAction;
import com.whir.portal.backlog.actionsupport.OperationSystemAction;
import com.whir.portal.backlog.actionsupport.SecureManageAction;
import com.whir.portal.backlog.actionsupport.TrainManageAction;

public class BackLogBD extends HibernateBase {
	
	public static MonitorServerBD monitorServerBD = new MonitorServerBD();
	
	public List<Map<String, Object>> getBackLogList(String userId,String userName) throws SQLException, HibernateException{
		System.out.println("进入获取待办模块");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    StringBuffer sql = new StringBuffer();
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        sql.append("SELECT WHIR$BACKLOG_SORT,WHIR$BACKLOG_MODULE FROM WHIR$BACKLOG WHERE WHIR$BACKLOG_USERID=? AND WHIR$BACKLOG_SHOWFLAG=? ORDER BY WHIR$BACKLOG_SORT ASC");
	        pstmt = conn.prepareStatement(sql.toString());
	        pstmt.setString(1, userId);
	        pstmt.setString(2, "0");
	        rs = pstmt.executeQuery();
	        String backlogSort = "";
	        String backlogModule = "";
	        String backlogModuleName="";
	        String backlogModuleEName="";
	        //判断各系统是否被委托-----开始
	        String wtFlag="0";
	        String oawt = "0";
//	        String wbwt = "0";
	        String lcwt = "0";
	        String agwt = "0";
	        String ywwt = "0";
	        String pxwt = "0";
	        String zcwt = "0";
	        String htwt = "0";
	        String xwbwt = "0";
	        String jjwt = "0";
	        Map<String,String> wtMap = getBacklogEntrust(userId);
	        oawt=wtMap.get("oa");
//	        wbwt=wtMap.get("wb");
	        htwt=wtMap.get("ht");
	        lcwt=wtMap.get("mh");
	      //判断各系统是否被委托-----结束
	        boolean flag = true;
	        int backlogNum=0;
	        while(rs.next()){
	        	flag = false;
	        	Map<String,Object> map = new HashMap<String,Object>();
	        	backlogSort = rs.getString("WHIR$BACKLOG_SORT");
	        	backlogModule = rs.getString("WHIR$BACKLOG_MODULE");
	        	if("0".equals(backlogModule)){
	        		//backlogNum = readDocPageCount(userName)+backlogDocPageCount(userName)+adminAffairPageCount(userName)+entrustPageCount(userName);
	        		backlogNum = backlogDocPageCount(userName)+adminAffairPageCount(userName)+entrustPageCount(userName);
	        		backlogModuleName = "OA待办";
	        		backlogModuleEName = "oadb";
	        		wtFlag=oawt;
	        	} else if("1".equals(backlogModule)){
	        		OnlineReimburseAction onlineReimburseAction = new OnlineReimburseAction();
	        		backlogNum = onlineReimburseAction.newOnlineReimbursePageCount(userName);
	        		backlogModuleName = "久其网报";
	        		backlogModuleEName = "xwbdb";
	        		wtFlag=xwbwt;
	        	} else if("2".equals(backlogModule)){
	        		backlogNum = portalBacklog(userId);
	        		backlogModuleName = "流程待办";
	        		backlogModuleEName = "lcdb";
	        		wtFlag=lcwt;
	        	} else if("3".equals(backlogModule)){
	        		SecureManageAction secureManageAction = new SecureManageAction();
	        		//backlogNum = secureManageAction.secureManagePageCount(userName);
	        		backlogNum = 0;
	        		backlogModuleName = "安管待办";
	        		backlogModuleEName = "agdb";
	        		wtFlag=agwt;
	        	} else if("4".equals(backlogModule)){
	        		OperationSystemAction operationSystemAction = new OperationSystemAction();
	        		backlogNum = operationSystemAction.operationSystemPageCount(userName);
	        		backlogModuleName = "运维待办";
	        		backlogModuleEName = "ywdb";
	        		wtFlag=ywwt;
	        	} else if("5".equals(backlogModule)){
	        		TrainManageAction trainManageAction = new TrainManageAction();
	        		//backlogNum = 0;
	        		backlogNum = trainManageAction.trainManagePageCount(userName);
	        		backlogModuleName = "培训待办";
	        		backlogModuleEName = "pxdb";
	        		wtFlag=pxwt;
	        	}  else if("6".equals(backlogModule)){
	        		backlogNum = disPosalPageCount(userName)+purchasePageCount(userName)+changePageCount(userName);
	        		backlogModuleName = "资产待办";
	        		backlogModuleEName = "zcdb";
	        		wtFlag=zcwt;
	        	}  else if("7".equals(backlogModule)){
	        		ContractManageAction contractManageAction = new ContractManageAction();
	        		backlogNum = contractManageAction.contractManagePageCount(userName);
	        		backlogModuleName = "合同待办";
	        		backlogModuleEName = "htdb";
	        		wtFlag=htwt;
	        	}  
//	        	else if("8".equals(backlogModule)){
//	        		OnlineReimburseAction onlineReimburseAction = new OnlineReimburseAction();
//	        		backlogNum = onlineReimburseAction.onlineReimbursePageCount(userName);
//	        		backlogModuleName = "网报待办";
//	        		backlogModuleEName = "wbdb";
//	        		wtFlag=wbwt;
//	        	}  
	        	else if("8".equals(backlogModule)){
	        		CapitalConstructionAction capitalConstructionAction = new CapitalConstructionAction();
	        		backlogNum = capitalConstructionAction.capitalConstructionPageCount(userName);
//	        		backlogNum = 0;
	        		backlogModuleName = "基建财务";
	        		backlogModuleEName = "jjdb";
	        		wtFlag=jjwt;
	        	}
	        	map.put("backlogSort", backlogSort);
	        	map.put("backlogModule", backlogModule);
	        	map.put("backlogNum", backlogNum);
	        	map.put("backlogModuleName", backlogModuleName);
	        	map.put("backlogModuleEName", backlogModuleEName);
	        	map.put("wtFlag", wtFlag);
	        	list.add(map);
	        }
	        if(flag){
	        	Map<String,Object> map1 = new HashMap<String,Object>();
	        	Map<String,Object> map2 = new HashMap<String,Object>();
	        	Map<String,Object> map3 = new HashMap<String,Object>();
	        	Map<String,Object> map4 = new HashMap<String,Object>();
	        	Map<String,Object> map5 = new HashMap<String,Object>();
	        	Map<String,Object> map6 = new HashMap<String,Object>();
	        	Map<String,Object> map7 = new HashMap<String,Object>();
	        	Map<String,Object> map8 = new HashMap<String,Object>();
//	        	Map<String,Object> map9 = new HashMap<String,Object>();
	        	Map<String,Object> map10 = new HashMap<String,Object>();
	        	TrainManageAction trainManageAction = new TrainManageAction();
	        	OperationSystemAction operationSystemAction = new OperationSystemAction();
	        	SecureManageAction secureManageAction = new SecureManageAction();
	        	OnlineReimburseAction onlineReimburseAction = new OnlineReimburseAction();
	        	ContractManageAction contractManageAction = new ContractManageAction();
	        	CapitalConstructionAction capitalConstructionAction = new CapitalConstructionAction();
	        	//backlogNum = readDocPageCount(userName)+backlogDocPageCount(userName)+adminAffairPageCount(userName)+entrustPageCount(userName);
	        	backlogNum = backlogDocPageCount(userName)+adminAffairPageCount(userName)+entrustPageCount(userName);
	        	map1.put("backlogSort", "1");
	        	map1.put("backlogModule", "0");
	        	map1.put("backlogNum", backlogNum);
	        	map1.put("backlogModuleName", "OA待办");
	        	map1.put("backlogModuleEName", "oadb");
	        	map1.put("wtFlag", oawt);
	        	list.add(map1);
	        	map2.put("backlogSort", "2");
	        	map2.put("backlogModule", "1");
	            map2.put("backlogNum", onlineReimburseAction.newOnlineReimbursePageCount(userName));
	            map2.put("backlogModuleName", "久其网报");
	            map2.put("backlogModuleEName", "xwbdb");
	            map2.put("wtFlag", xwbwt);
	            list.add(map2);
	        	map3.put("backlogSort", "3");
			    map3.put("backlogModule", "2");
			    map3.put("backlogNum", portalBacklog(userId));
			    map3.put("backlogModuleName", "流程待办");
			    map3.put("backlogModuleEName", "lcdb");
			    map3.put("wtFlag", lcwt);
		        list.add(map3);
		        map4.put("backlogSort", "4");
		        map4.put("backlogModule", "3");
	        	map4.put("backlogNum", contractManageAction.contractManagePageCount(userName));
	        	map4.put("backlogModuleName", "合同待办");
	        	map4.put("backlogModuleEName", "htdb");
	        	map4.put("wtFlag", htwt);
	        	list.add(map4);
	        	map5.put("backlogSort", "5");
	        	map5.put("backlogModule", "4");
	        	map5.put("backlogNum", disPosalPageCount(userName)+purchasePageCount(userName)+changePageCount(userName));
	        	map5.put("backlogModuleName", "资产待办");
	        	map5.put("backlogModuleEName", "zcdb");
	        	map5.put("wtFlag", zcwt);
	        	list.add(map5);
	        	map6.put("backlogSort", "6");
	        	map6.put("backlogModule", "5");
	        	map6.put("backlogNum", operationSystemAction.operationSystemPageCount(userName));
	        	map6.put("backlogModuleName", "运维待办");
	        	map6.put("backlogModuleEName", "ywdb");
	        	map6.put("wtFlag", ywwt);
	        	list.add(map6);
	        	map7.put("backlogSort", "7");
	        	map7.put("backlogModule", "6");
	        	map7.put("backlogNum", trainManageAction.trainManagePageCount(userName));
	        	//map7.put("backlogNum", "0");
	        	map7.put("backlogModuleName", "培训待办");
	        	map7.put("backlogModuleEName", "pxdb");
	        	map7.put("wtFlag", pxwt);
	        	list.add(map7);
	        	map8.put("backlogSort", "8");
	        	map8.put("backlogModule", "9");
	        	map8.put("backlogNum", "0");
//		        map8.put("backlogNum", secureManageAction.secureManagePageCount(userName));
		        map8.put("backlogModuleName", "安管待办");
		        map8.put("backlogModuleEName", "agdb");
		        map8.put("wtFlag", agwt);
	            list.add(map8);
//	            map2.put("backlogSort", "2");
//	        	map2.put("backlogModule", "1");
//	        	map2.put("backlogNum", onlineReimburseAction.onlineReimbursePageCount(userName));
//	        	map2.put("backlogModuleName", "网报待办");
//	        	map2.put("backlogModuleEName", "wbdb");
//	        	map2.put("wtFlag", wbwt);
//	        	list.add(map2);
	            map10.put("backlogSort", "10");
	            map10.put("backlogModule", "11");
	            map10.put("backlogNum", capitalConstructionAction.capitalConstructionPageCount(userName));
//	            map10.put("backlogNum", "0");
	            map10.put("backlogModuleName", "基建待办");
	            map10.put("backlogModuleEName", "jjdb");
	            map10.put("wtFlag", jjwt);
	            list.add(map10);
	        }
	    } catch (Exception e) {
	      conn.rollback();
	      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
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
	 * 查找门户待办记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer portalBacklog(String userId) throws SQLException, HibernateException {
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    int count=0;
	    try
	    {
	      conn = this.session.connection();
	      stmt = conn.createStatement();

	      String sql = 
	        " select count(wf_work_id) from EZOFFICE.wf_work where workStatus = 0 and  wf_curEmployee_id = " + 
	        userId + 
	        " and workListControl = 1 and workDelete<>1 ";
	      rs = stmt.executeQuery(sql);
	      if (rs.next()) {
	    	  count = Integer.parseInt(rs.getString(1));
	      }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
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
		return count;
	}
	
	/**
	 * 查找被委托的系统
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Map<String,String> getBacklogEntrust(String userId) throws SQLException, HibernateException {
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    String system="";
	    try
	    {
	      conn = this.session.connection();
	      stmt = conn.createStatement();
	      String sql = "select whir$entrust_system from whir$entrust where whir$entrust_owner='"+userId+"' and whir$entrust_state='0'";
	      rs = stmt.executeQuery(sql);
	      if (rs.next()) {
	    	  system = rs.getString(1);
	      }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
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
	    Map<String,String> map = new HashMap<String,String>();
	    if(system!="" && !"".equals(system)){
	    	String str = system.substring(0, system.length()-1);
	    	String[] strArray = str.split(",");
	    	if(useArraysBinarySearch(strArray,"0")){
	    		map.put("oa", "1");
    		} else {
    			map.put("oa", "0");
    		}
	    	if(useArraysBinarySearch(strArray,"1")){
	    		map.put("wb", "1");
    		} else {
    			map.put("wb", "0");
    		}
	    	if(useArraysBinarySearch(strArray,"2")){
	    		map.put("ht", "1");
    		} else {
    			map.put("ht", "0");
    		}
	    	if(useArraysBinarySearch(strArray,"3")){
	    		map.put("mh", "1");
    		} else {
    			map.put("mh", "0");
    		}
	    } else {
	    	map.put("oa", "0");
	    	map.put("wb", "0");
	    	map.put("ht", "0");
	    	map.put("mh", "0");
	    }
		return map;
	}
	
	public static boolean useArraysBinarySearch(String[] arr,String targtValue){
		int a = Arrays.binarySearch(arr, targtValue);
		if(a>=0){
			return true;
		} else {
			return false;
		}
	}
	/**
	 * 查找待办公文数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> backlogDocListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("1");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetToDo", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "title", "dataLink", "draftDepartment","documentType", "currentNode" }, list);
			      } else {
			    	  System.out.println("--------获取待办公文总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("1",res);
			    }
			return list2;
		}
	}
	/**
	 * 查找待办公文总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer backlogDocPageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("1");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetToDo", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取待办公文总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("1",res);
			    }
			return pageCount;
		}
	}
	
	
	/**
	 * 查找待阅公文数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> readDocListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("2");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetToRead", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "title", "dataLink", "fileType","source"}, list);
			      } else {
			    	  System.out.println("--------获取待阅公文总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("2",res);
			    }
			return list2;
		}
	}
	/**
	 * 查找待阅公文总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer readDocPageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("2");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetToRead", new Object[] { send });
			      res = (String)results[0];
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取待阅公文总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("2",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找行政事务公告数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws Exception 
	 */
	public List<Map<String, Object>> adminAffairListData(String userName, int currentPage,int pageSize) throws Exception {
		XmlDataParse xmlDataParse = new XmlDataParse();
		List<List<Map<String,Object>>> oalist = null;
		List<List<Map<String,Object>>> cbflist = null;
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		int cbfCurrentPage=1;
		int cbfCurrentPageSize=pageSize;
		int oaCount = getOaCount(userName);
		int oaCurrentCount = currentPage*pageSize;
		if(oaCount>=oaCurrentCount){
			String oaStr = oaXZSWListData(userName,currentPage,pageSize);
			if(oaStr!="" && !"".equals(oaStr)){
				oalist = xmlDataParse.parseXmlData(oaStr);
		    }
		} else {
			  cbfCurrentPage = ((oaCurrentCount-oaCount)/ pageSize);
			  int modyw = (oaCurrentCount-oaCount) % pageSize;
			  if (modyw != 0){
				  cbfCurrentPage += 1;
			  }
			  if((oaCurrentCount-oaCount)<pageSize){
				  cbfCurrentPageSize=oaCurrentCount-oaCount;
				  String oaStr = oaXZSWListData(userName,currentPage,pageSize);
				  if(oaStr!="" && !"".equals(oaStr)){
						oalist = xmlDataParse.parseXmlData(oaStr);
				  }
				  String cbfStr = cbfListData(userName,cbfCurrentPage,cbfCurrentPageSize);
		    	  if(cbfStr!="" && !"".equals(cbfStr)){
		    		  cbflist = xmlDataParse.parseXmlData(cbfStr);
			      }
		    	  if(cbflist!=null && !"".equals(cbflist)){
					oalist.addAll(cbflist);
				  }
			  } else {
				  String cbfStr = cbfListData(userName,cbfCurrentPage-1,pageSize);
		    	  if(cbfStr!="" && !"".equals(cbfStr)){
		    		  oalist = xmlDataParse.parseXmlData(cbfStr);
			      }
		    	  for(int i=0;i<modyw;i++){
		    		  oalist.remove(oalist.get(0));
		    	  }
				  String cbfStr1 = cbfListData(userName,cbfCurrentPage,pageSize);
		    	  if(cbfStr1!="" && !"".equals(cbfStr1)){
		    		  List<List<Map<String,Object>>> nextList = xmlDataParse.parseXmlData(cbfStr1);
		    		  if(nextList.size()<modyw){
		    			  for(int j=0;j<nextList.size();j++){
				    		  oalist.add(nextList.get(j));
				    	  }
		    		  } else {
		    			  for(int j=0;j<modyw;j++){
				    		  oalist.add(nextList.get(j));
				    	  }
		    		  }
			      }
			  }
		}
		JacksonUtil util = new JacksonUtil();
	    list2 = util.createResultList(new String[] { "title", "dataLink", "draftDepartment","affairType", "currentNode" }, oalist);
	    return list2;
	}
	
	/**
	 * 查找OA行政事务数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public String oaXZSWListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		String res = null;
		String runCondition = getSystemRunCondition("3");
		if("1".equals(runCondition)){
			return res;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetAffair", new Object[] { send });
			      res = (String)results[0];
			    }  catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("3",res);
			    }
			return res;
		}
	}
	
	/**
	 * 查找双阳采编发数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public String cbfListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		String res = null;
		String runCondition = getSystemRunCondition("4");
		if("1".equals(runCondition)){
			return res;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("cbf_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("findToDo", new Object[] { send });
			      res = (String)results[0];
			    } catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("4",res);
			    }
			return res;
		}
	}
	/**
	 * 查找行政事务总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer adminAffairPageCount(String userName) throws HibernateException, SQLException {
		int oaCount = getOaCount(userName);
		System.out.println("--------获取OA行政事务总记录数据---------"+oaCount);
		int sycbf = getSycbfCount(userName);
		System.out.println("--------获取双阳采编发总记录数据---------"+sycbf);
//		int sydb = getSydbCount(userName);
//		System.out.println("--------获取双阳督办总记录数据---------"+sydb);
		return oaCount+sycbf;
	}
	
	public Integer getOaCount(String userName) throws HibernateException, SQLException{
		int pageCount=0;
		String runCondition = getSystemRunCondition("3");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetAffair", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取行政事务总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("3",res);
			    }
			return pageCount;
		}
	}
	
	public Integer getSycbfCount(String userName) throws HibernateException, SQLException{
		int pageCount=0;
		String runCondition = getSystemRunCondition("4");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("cbf_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("findToDo", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取采编发总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("4",res);
			    }
			return pageCount;
		}
	}
	
	
	public Integer getSydbCount(String userName) throws HibernateException, SQLException{
		int pageCount=0;
		String runCondition = getSystemRunCondition("19");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("db_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("getToDoList", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取督办总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("19",res);
			    }
			return pageCount;
		}
	}
	/**
	 * 查找委托事项数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> entrustListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("5");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetDelegate", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "title", "dataLink", "draftDepartment","documentType", "currentNode","byBackLog" }, list);
			      } else {
			    	  System.out.println("--------获取委托事项总记录数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("5",res);
			    }
			return list2;
		}
	}
	/**
	 * 查找委托事项总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer entrustPageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("5");
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("oa_wsdl");
			
			Document rec = DocumentHelper.createDocument();
			rec.setXMLEncoding("UTF-8");
			Element input = rec.addElement("input");
			Element key = input.addElement("key");
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
			
			String res = null;
			try {
			      Client client = new Client(new URL(wsdl_address));
			      String send = "<input>"+rec.asXML().split("\\<input>")[1];
			      Object[] results = client.invoke("GetDelegate", new Object[] { send });
			      res = (String)results[0];
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取委托事项总记录数据---------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("5",res);
			    }
			return pageCount;
		}
	}
	
	
	/**
	 * 查找固定资产申请待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer disPosalPageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("6");
		System.out.println("查找固定资产申请待办总记录数运行情况:"+runCondition);
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				  String inputStr = "<input>"+rec.asXML().split("\\<input>")[1];
				  URL url = new URL(wsdl_address);
				  QName serviceName = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");  
				  ISynTodoInfoWs_Service client = new ISynTodoInfoWs_Service(url,serviceName);
				  ISynTodoInfoWs port = client.getSynTodoInfoWsImplPort();
				  res = port.getDisposeData(inputStr);
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取固定资产申请待办总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("6",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找固定资产申购待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer purchasePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("7");
		System.out.println("查找固定资产申购待办总记录数运行情况:"+runCondition);
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				String inputStr = "<input>"+rec.asXML().split("\\<input>")[1];
				  URL url = new URL(wsdl_address);
				  QName serviceName = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");  
				  ISynTodoInfoWs_Service client = new ISynTodoInfoWs_Service(url,serviceName);
				  ISynTodoInfoWs port = client.getSynTodoInfoWsImplPort();
				  res = port.getPurchaseData(inputStr);
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取固定资产申购待办总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("7",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找固定资产变动待办总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public Integer changePageCount(String userName) throws HibernateException, SQLException {
		int pageCount=0;
		String runCondition = getSystemRunCondition("8");
		System.out.println("查找固定资产变动待办总记录数运行情况:"+runCondition);
		if("1".equals(runCondition)){
			return pageCount;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				String inputStr = "<input>"+rec.asXML().split("\\<input>")[1];
				  URL url = new URL(wsdl_address);
				  QName serviceName = new QName("http://todoservice.webservice.send.workitem.kgj.jiuqi.com/", "ISynTodoInfoWs");  
				  ISynTodoInfoWs_Service client = new ISynTodoInfoWs_Service(url,serviceName);
				  ISynTodoInfoWs port = client.getSynTodoInfoWsImplPort();
				  res = port.getAlterData(inputStr);
			      if(res!="" && !"".equals(res)){
				      XmlDataParse xmlDataParse = new XmlDataParse();
				      String listStr = xmlDataParse.parseXmlRecord(res);
				      pageCount=Integer.parseInt(listStr);
			      } else {
			    	  System.out.println("--------获取固定资产变动待办总记录数据--------"+0);
			      }
			    }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("8",res);
			    }
			return pageCount;
		}
	}
	
	/**
	 * 查找固定资产申请待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> disposeListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("6");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				  res = port.getDisposeData(inputStr);
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "receiptNum", "receiptDate", "agent","agentPhone", "dealShape","dealDepartment","dataLink" }, list);
			      } else {
			    	  System.out.println("--------获取固定资产申请待办数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("6",res);
			    }
			return list2;
		}
	}
	
	/**
	 * 查找固定资产申购待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> purchaseListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("7");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				  res = port.getPurchaseData(inputStr);
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "receiptNum", "receiptDate", "agent","applicatDepartment","dataLink" }, list);
			      } else {
			    	  System.out.println("--------获取固定资产申购待办数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("7",res);
			    }
			return list2;
		}
	}
	
	/**
	 * 查找固定资产变动待办数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> alterListData(String userName, int currentPage,int pageSize) throws HibernateException, SQLException {
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		String runCondition = getSystemRunCondition("6");
		if("1".equals(runCondition)){
			return list2;
		} else {
			Properties properties = PropertiesUtils.getProperties();
			String wsdl_address = properties.getProperty("jqzcgl_wsdl");
			
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
				  res = port.getAlterData(inputStr);
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      if(res!="" && !"".equals(res)){
				      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
				      JacksonUtil util = new JacksonUtil();
					  list2 = util.createResultList(new String[] { "receiptNum", "receiptDate", "agent","applicatDepartment","dataLink" }, list);
			      } else {
			    	  System.out.println("--------获取固定资产变动待办数据---------"+0);
			      }
			   }
			    catch (Exception e) {
			      e.printStackTrace();
			      res = e.getMessage();
			      monitorServerBD.putErrorToDataBase("6",res);
			    }
			return list2;
		}
	}
	
	
	/**
	 * 查找系统运行状况
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public String getSystemRunCondition(String moudle) throws SQLException, HibernateException {
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    String runCondition="";
	    try
	    {
	      conn = this.session.connection();
	      stmt = conn.createStatement();
	      String sql = "select whir$mhxtyxjc_f3548 from whir$mhxtyxjc  where whir$mhxtyxjc_f3550='"+moudle+"'";
	      rs = stmt.executeQuery(sql);
	      if (rs.next()) {
	    	  runCondition = rs.getString(1);
	      }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	}
	    	}
	    	if(stmt != null){
	    	try
	    	{
	    		stmt.close();
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
		return runCondition;
	}
}
