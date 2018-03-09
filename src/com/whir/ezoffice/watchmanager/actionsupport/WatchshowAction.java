package com.whir.ezoffice.watchmanager.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;

import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.common.util.parse.XmlDataParse;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.PropertiesUtils;
import com.whir.rd.util.WatchArrangeUtils;

public class WatchshowAction extends BaseActionSupport{
	//开始日期
	private String arrangeDateFrom;
	//结束日期
	private String arrangeDateEnd;
	//岗位id
	private String postId;
	//部门id
	private String departmentId;
	
	public String getArrangeDateFrom() {
		return arrangeDateFrom;
	}
	public void setArrangeDateFrom(String arrangeDateFrom) {
		this.arrangeDateFrom = arrangeDateFrom;
	}
	public String getArrangeDateEnd() {
		return arrangeDateEnd;
	}
	public void setArrangeDateEnd(String arrangeDateEnd) {
		this.arrangeDateEnd = arrangeDateEnd;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	//进入局领导值班查询
	public String leaderShow() throws IOException{
		String psql = "select whir$posttab_id from whir$posttab where whir$posttab_class='100'";
		Object[] obj = new WatchArrangeUtils().getArray(psql);
		if(obj[0]!=null && !"".equals(obj[0])){
			setPostId(obj[0].toString());
		}else{
			setPostId("");
		}
		return "leaderShow";
	}
	
	//默认显示领导当月的值班表
	public String showLeaderWatchINMonth() throws Exception{
		List empInfos = new ArrayList();
		List excelInfos = new ArrayList();
		if(arrangeDateFrom != null && !"".equals(arrangeDateFrom)){
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data ,  whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
					" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' order by data,whir$dutyRoster_f3537";
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowExcelInfos(excelList);
			} else {
				empInfos = getShowPostInfos(postId, arrangeDateFrom, arrangeDateEnd);
			}
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			//获取当前月第一天：
			Calendar c = Calendar.getInstance();    
			//设置为1号,当前日期既为本月第一天 
			c.set(Calendar.DAY_OF_MONTH,1);
			String firstDay = format.format(c.getTime());
			//获取当前月最后一天
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));  
			String lastDay = format.format(c.getTime());
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data , whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
					" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+firstDay+"' and '"+lastDay+"' order by data,whir$dutyRoster_f3537";
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowExcelInfos(excelList);
			} else {
				empInfos = getShowPostInfos(postId, firstDay, lastDay);
			}
		}
		System.out.println("excelInfos.size()::"+excelInfos.size());
		StringBuffer json = new StringBuffer("[");
		if(excelInfos.size()>0){
	        for(int i=0;i<excelInfos.size();i++){
	        	Object[] objs = (Object[])excelInfos.get(i);
	        	json.append("{\"watchtable_id\":\""+i+"\",");
	        	json.append("\"dates\":\""+objs[0]+"\",");
	        	json.append("\"mWatchInfo\":\""+objs[2]+"\",");
	        	json.append("\"mWatchAccount\":\""+objs[3]+"\",");
	        	json.append("\"dates1\":\""+objs[4]+"\",");
	        	json.append("\"mWatchInfo1\":\""+objs[6]+"\",");
	        	json.append("\"mWatchAccount1\":\""+objs[7]+"\"},");
	        }
	        json.delete(json.length()-1, json.length());
		} else {
			if(empInfos.size()>0){
		        for(int i=0;i<empInfos.size();i++){
		        	Object[] objs = (Object[])empInfos.get(i);
		        	json.append("{\"watchtable_id\":\""+objs[0]+"\",");
		        	json.append("\"dates\":\""+objs[1]+"\",");
		        	json.append("\"mWatchInfo\":\""+objs[2]+"\",");
		        	json.append("\"mWatchAccount\":\""+objs[3]+"\"},");
		        }
		        json.delete(json.length()-1, json.length());
			}
		}
    	json.append("]");
		response.setContentType("text/plain;charSet=UTF-8");
		response.setCharacterEncoding("UTF-8");
	   	try {
	        PrintWriter pw = this.response.getWriter();
	        pw.print(json);
	        pw.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return null;
	}
	
	
	//默认显示领导当月的值班表微信端数据接入
	public List showWxLeaderWatchINMonth(String dutyDate) throws Exception{
		List excelInfos = new ArrayList();
		if(dutyDate != null && !"".equals(dutyDate)){
			String[] dutyDates = dutyDate.split(" - ");
			String[] arrangeDateFroms = dutyDates[0].split("/");
			String[] arrangeDateEnds = dutyDates[1].split("/");
			String arrangeDateFrom = arrangeDateFroms[0]+"-"+arrangeDateFroms[1]+"-"+arrangeDateFroms[2];
			String arrangeDateEnd = arrangeDateEnds[0]+"-"+arrangeDateEnds[1]+"-"+arrangeDateEnds[2];
			
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data ,  whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
					" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' order by data,whir$dutyRoster_f3537";
			System.out.println("sql::::"+sql);
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowExcelInfos(excelList);
			}
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
			//获取当前月第一天：
			Calendar c = Calendar.getInstance();    
			//设置为1号,当前日期既为本月第一天 
			c.set(Calendar.DAY_OF_MONTH,1);
			String firstDay = format.format(c.getTime());
			//获取当前月最后一天
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));  
			String lastDay = format.format(c.getTime());
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data , whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
					" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+firstDay+"' and '"+lastDay+"' order by data,whir$dutyRoster_f3537";
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowExcelInfos(excelList);
			}
		}
		return excelInfos;
	}
	
	
	
	
	
	//中心领导班表显示微信数据接入
	public List showWxCenterPostquery(String dutyDate,String centerName) throws Exception{
			List excelInfos = new ArrayList();
			//中心岗位日期范围值班日历
			String[] dutyDates = dutyDate.split(" - ");
			String[] arrangeDateFroms = dutyDates[0].split("/");
			String[] arrangeDateEnds = dutyDates[1].split("/");
			String arrangeDateFrom = arrangeDateFroms[0]+"-"+arrangeDateFroms[1]+"-"+arrangeDateFroms[2];
			String arrangeDateEnd = arrangeDateEnds[0]+"-"+arrangeDateEnds[1]+"-"+arrangeDateEnds[2];
			
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) as data ,  whir$centreduty_dutystaff " +
					" from whir$centreduty where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' and whir$centreduty_department='"+centerName+"' order by data";
			
			System.out.println("sql::::"+sql);
			excelList = new WatchArrangeUtils().getList(sql);
			
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowCenterExcelInfos(excelList);
			} 
			return excelInfos;
	}
	
	
	//进入中心领导值班查询
	public String centerLeaderShow() throws Exception{
		List centerlist = new ArrayList();
		String excelsql = "select distinct whir$centreduty_department as id,whir$centreduty_department from whir$centreduty";
		String centersql = "select distinct whir$posttab_id,whir$posttab_pname from whir$posttab where whir$posttab_class='10'";
		List list = new WatchArrangeUtils().getList(excelsql);
		if(list!=null && !list.isEmpty()){
			centerlist =getSortList(list);
		} else {
			centerlist = new WatchArrangeUtils().getList(centersql);
		}
		request.setAttribute("centerlist", centerlist);
		return "centerLeaderShow";
	}
	//中心领导班表显示
	public String showCenterPostquery() throws Exception{
		if(!"-1".equals(postId)){
			List empInfos = new ArrayList();
			List excelInfos = new ArrayList();
			//中心岗位日期范围值班日历
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) as data ,  whir$centreduty_dutystaff " +
					" from whir$centreduty where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' and whir$centreduty_department='"+postId+"' order by data";
			excelList = new WatchArrangeUtils().getList(sql);
			System.out.println("ZZZZ:::"+sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowCenterExcelInfos(excelList);
			} else {
				empInfos = getShowPostInfos(postId, arrangeDateFrom, arrangeDateEnd);
			}
			StringBuffer json = new StringBuffer("[");
			if(excelInfos.size()>0){
		        for(int i=0;i<excelInfos.size();i++){
		        	Object[] objs = (Object[])excelInfos.get(i);
		        	json.append("{\"watchtable_id\":\""+i+"\",");
		        	json.append("\"dates\":\""+objs[0]+"\",");
		        	json.append("\"mWatchInfo\":\""+objs[2]+"\",");
		        	json.append("\"mWatchAccount\":\""+objs[3]+"\"},");
		        }
		        json.delete(json.length()-1, json.length());
			} else {
				if(empInfos.size()>0){
					for(int i=0;i<empInfos.size();i++){
			        	Object[] objs = (Object[])empInfos.get(i);
			        	json.append("{\"watchtable_id\":\""+objs[0]+"\",");
			        	json.append("\"dates\":\""+objs[1]+"\",");
			        	json.append("\"mWatchInfo\":\""+objs[2]+"\",");
			        	json.append("\"mWatchAccount\":\""+objs[3]+"\"},");
			        }
			        json.delete(json.length()-1, json.length());
				}
			}
	    	json.append("]");
			response.setContentType("text/plain;charSet=UTF-8");
			response.setCharacterEncoding("UTF-8");
		   	try {
		        PrintWriter pw = this.response.getWriter();
		        pw.print(json);
		        pw.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			return null;
		}else{
			//默认显示当天所有中心的值班人员
			int pageSize = CommonUtils.getUserPageSize(request);
			int currentPage = 0;
	        if (request.getParameter("startPage") != null) {
	        	currentPage = Integer.parseInt(request.getParameter("startPage"));
	        }
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date da = new Date();
			String today = sdf.format(da);
			String viewSQL1 = "distinct whir$centreduty_dutystaff, whir$centreduty_department";
			String fromSQL1 = "whir$centreduty ";
			String whereSQL1 = "where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) = '"+today+"'";
			String orderSQL1 = "order by whir$centreduty_department";
			Page page1 = PageFactory.getJdbcPage(viewSQL1, fromSQL1, whereSQL1, orderSQL1);
			page1.setPageSize(pageSize);
	        page1.setCurrentPage(currentPage);
	        List list1 = page1.getResultList();
	        if(list1.size()>0){
	        	List empInfos = new ArrayList();
				empInfos = getEmpInfosFromExcel(list1);
				String viewsql = "ppp.pname,ppp.mpa";
				int pageCount = page1.getPageCount();
		        int recordCount = page1.getRecordCount();
		        JacksonUtil util = new JacksonUtil();
				String json = util.writeArrayJSON(viewsql,empInfos);
				json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
				printResult(G_SUCCESS,json);
				return null;
	        } else {
	        	String viewSQL = "p.whir$posttab_id,p.whir$posttab_pname,w.whir$watchtab_mpa,w.whir$watchtab_mpname,w.whir$watchtab_apa,w.whir$watchtab_apname";
				String fromSQL = "whir$watchtab w join whir$posttab p on p.whir$posttab_id=w.whir$watchtab_postno ";
				String whereSQL = "where p.whir$posttab_class='10' and w.whir$watchtab_dates='"+today+"'";
				String orderSQL = "order by p.whir$posttab_dno";
				Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL, orderSQL);
		        page.setPageSize(pageSize);
		        page.setCurrentPage(currentPage);
		        
		        List list = page.getResultList();
		        List empInfos = new ArrayList();
				if(list.size()>0){
					empInfos = getEmpInfos(list);
				}
				String viewsql = "ppp.id,ppp.pname,ppp.mpa,ppp.mpname,ppp.apa,ppp.apname";
		        int pageCount = page.getPageCount();
		        int recordCount = page.getRecordCount();
		        JacksonUtil util = new JacksonUtil();
				String json = util.writeArrayJSON(viewsql,empInfos);
				json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
				printResult(G_SUCCESS,json);
				return null;
	        }
		}
		
	}
	
	//进入员工值班查询
	public String postShow(){
		System.out.println("进入员工值班查询页面。。。");
		String dsql = "select distinct whir$posttab_dno,whir$posttab_dname from whir$posttab where whir$posttab_class='0'";
		List dlist = new WatchArrangeUtils().getList(dsql);
		request.setAttribute("departmentId", dlist);
		return "postShow";
	}
	//员工班表显示
	public String showPostquery(){
		if(!"-1".equals(postId)){
			//员工岗位日期范围值班日历
			List empInfos = getShowPostInfos(postId, arrangeDateFrom, arrangeDateEnd);
			StringBuffer json = new StringBuffer("[");
			if(empInfos.size()>0){
				for(int i=0;i<empInfos.size();i++){
		        	Object[] objs = (Object[])empInfos.get(i);
		        	json.append("{\"watchtable_id\":\""+objs[0]+"\",");
		        	json.append("\"dates\":\""+objs[1]+"\",");
		        	json.append("\"mWatchInfo\":\""+objs[2]+"\",");
		        	json.append("\"mWatchAccount\":\""+objs[3]+"\",");
		        	json.append("\"aWatchInfo\":\""+objs[4]+"\",");
		        	json.append("\"aWatchAccount\":\""+objs[5]+"\"},");
		        }
		        json.delete(json.length()-1, json.length());
			}
	    	json.append("]");
			response.setContentType("text/plain;charSet=UTF-8");
			response.setCharacterEncoding("UTF-8");
		   	try {
		        PrintWriter pw = this.response.getWriter();
		        pw.print(json);
		        pw.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		    
			System.out.println("岗位下员工班表查询完成。。。。。。");
			return null;
		}else {
			//默认显示当天所有员工岗位的值班人员
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date da = new Date();
			String today = sdf.format(da);
			
			String viewSQL = "p.whir$posttab_dname,p.whir$posttab_pname,w.whir$watchtab_mpa,w.whir$watchtab_mpname,w.whir$watchtab_apa,w.whir$watchtab_apname";
			String fromSQL = "whir$watchtab w join whir$posttab p on p.whir$posttab_id=w.whir$watchtab_postno ";
			String whereSQL = "where p.whir$posttab_class='0' and w.whir$watchtab_dates='"+today+"' ";
			String orderSQL = "order by p.whir$posttab_dno";
			Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL, orderSQL);
			int pageSize = CommonUtils.getUserPageSize(request);
			int currentPage = 0;
	        if (request.getParameter("startPage") != null) {
	        	currentPage = Integer.parseInt(request.getParameter("startPage"));
	        }
			page.setPageSize(pageSize);
	        page.setCurrentPage(currentPage);
	        List list = page.getResultList();
	        List empInfos = new ArrayList();
			if(list.size()>0){
				empInfos = getEmpInfos(list);
			}
			String viewsql = "ppp.dname,ppp.pname,ppp.mpname,ppp.mpa,ppp.apname,ppp.apa";
	        int pageCount = page.getPageCount();
	        int recordCount = page.getRecordCount();
	        JacksonUtil util = new JacksonUtil();
			String json = util.writeArrayJSON(viewsql, empInfos);
			json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
			printResult(G_SUCCESS,json);
			return null;
		}
		
	}
	
	//进入通导员工值班查询
	public String ywPostShow(){
		return "ywPostShow";
	}
	
	/**
	 * 查找通导员工值班数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String ywPostShowData() {
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("yunwei_wsdl");
		HttpSession session = this.request.getSession();
		int pageSize = CommonUtils.getUserPageSize(this.request);
		int currentPage = 0;
	    if (this.request.getParameter("startPage") != null) {
	      currentPage = Integer.parseInt(this.request.getParameter("startPage"));
	    }
	    String queryDate = request.getParameter("queryDate");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("duty");
		Element parameter = output.addElement("parameter");
		
		Element queryDateElement = parameter.addElement("queryDate");
		if(queryDate==null || queryDate.equals("")){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    	queryDate = format.format(new Date());
			queryDateElement.setText(queryDate);
		} else {
			queryDateElement.setText(queryDate);
		}
		Element pageSizeElement = parameter.addElement("pageSize");
		pageSizeElement.setText(Integer.toString(pageSize));
		Element currentPageElement = parameter.addElement("currentPage");
		currentPageElement.setText(Integer.toString(currentPage));
		
		String res = null;
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("service", new Object[] { send });
		      res = (String)results[0];
		      if(res!=null && !("").equals(res)){
			      XmlDataParse xmlDataParse = new XmlDataParse();
			      List<List<Map<String,Object>>> list = xmlDataParse.parseXmlData(res);
			      String listStr = xmlDataParse.parseXmlRecord(res);
			      JacksonUtil util = new JacksonUtil();
					// 这里需要修改ejb,加个字段
					String json = util.writeNewArrayJSON(
							new String[] {"department", "postname", "arrangedate", "unitname", "duty" }, list);
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
		      res = e.getMessage();
		    }
		    return null;
	}
	//获取指定日期下岗位员工值班信息
	public List getShowPostInfos(String postId,String dateFrom,String dateEnd){
		System.out.println("进入值班信息查询。。。。。。");
		List showList = new ArrayList();
		List empInfos = new ArrayList();
		String sql = "select whir$watchtab_id,concat(whir$watchtab_dates,concat(',',whir$watchtab_weeks)) dates,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname " +
				"from whir$watchtab where whir$watchtab_postno='"+postId+"' and (whir$watchtab_dates between '"+dateFrom+"' and '"+dateEnd+"') order by whir$watchtab_dates";
		showList = new WatchArrangeUtils().getList(sql);
		if(showList.size()>0){
			empInfos = getEmpInfos(showList);
		}
		return empInfos;
		
	}
	//获取员工账号信息
	public List getEmpInfos(List showList){
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	String sql = "";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	for(int i=0;i<showList.size();i++){
	    		Object[] objs = (Object[])showList.get(i);
	    		String[] empObj = new String[6];
	    		empObj[0] = objs[0].toString();
	    		empObj[1] = objs[1].toString();
	    		empObj[2] = "";
	    		empObj[3] = "";
	    		empObj[4] = "";
	    		empObj[5] = "";
	    		String mpa = (String)objs[2];
        		if(mpa.startsWith("$")){
        			String[] mailtoIds = (mpa.substring(1) + "$").split("\\$\\$");
        			for(int j=0;j<mailtoIds.length;j++){
        				sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mailtoIds[j]+"'";
        				rs = stmt.executeQuery(sql);
        		    	while (rs.next()){
        		    		empObj[2]+=rs.getString(1)+",";
        		    		empObj[3]+=rs.getString(2)+",";
        		    	}
	        		}
        		} else{
        			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mpa+"'";
    				rs = stmt.executeQuery(sql);
    		    	while (rs.next()){
    		    		empObj[2]=rs.getString(1)+",";
    		    		empObj[3]=rs.getString(2)+",";
    		    	}
        		}
        		if(objs[4] !=null && !"".equals(objs[4]) && !"null".equals(objs[4])){
        			String apa = (String)objs[4];
    	    		if(apa.startsWith("$")){
    	    			String[] mailtoId = (apa.substring(1) + "$").split("\\$\\$");
            			for(int j=0;j<mailtoId.length;j++){
            				sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+mailtoId[j]+"'";
            				rs = stmt.executeQuery(sql);
            		    	while (rs.next()){
            		    		empObj[4]+=rs.getString(1)+",";
            		    		empObj[5]+=rs.getString(2)+",";
            		    	}
    	        		}
    	    		}else{	
    	    			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where emp_id='"+apa+"'";
        				rs = stmt.executeQuery(sql);
        		    	while (rs.next()){
        		    		empObj[4]=rs.getString(1)+",";
        		    		empObj[5]=rs.getString(2)+",";
        		    	}
    	    		
    	    		}
        		}
	    		empInfoList.add(empObj);
	    	}
	    	rs.close();
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
		return empInfoList;
	}
	
	//获取指定日期下岗位员工值班信息
	public List getShowCenterExcelInfos(List excelList) throws Exception{
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	String sql = "";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	for(int i=0;i<excelList.size();i++){
	    		Object[] objs = (Object[])excelList.get(i);
	    		String[] excelObj = new String[4];
	    		int dayForWeek = dayForWeek(objs[0].toString());
	    		if(dayForWeek==1){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期一";
	    		} else if(dayForWeek==2){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期二";
	    		} else if(dayForWeek==3){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期三";
	    		} else if(dayForWeek==4){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期四";
	    		} else if(dayForWeek==5){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期五";
	    		} else if(dayForWeek==6){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期六";
	    		} else if(dayForWeek==7){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期日";
	    		}
	    		//用户名
	    		excelObj[1] = objs[1].toString();
    			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[1]+"'";
				rs = stmt.executeQuery(sql);
		    	while (rs.next()){
	    			excelObj[2]=rs.getString(1)+",";
		    		excelObj[3]=rs.getString(2)+",";
		    	}
	    		empInfoList.add(excelObj);
	    	}
	    	rs.close();
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
		return empInfoList;
	}
	
	//获取指定日期下岗位员工值班信息
	public List getShowExcelInfos(List excelList) throws Exception{
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	String sql = "";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	for(int i=0;i<excelList.size();i++){
	    		Object[] objs = (Object[])excelList.get(i);
	    		Object[] objs1 = (Object[])excelList.get(i+1);
	    		String[] excelObj = new String[8];
	    		int dayForWeek = dayForWeek(objs[0].toString());
	    		if(dayForWeek==1){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期一";
	    		} else if(dayForWeek==2){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期二";
	    		} else if(dayForWeek==3){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期三";
	    		} else if(dayForWeek==4){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期四";
	    		} else if(dayForWeek==5){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期五";
	    		} else if(dayForWeek==6){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期六";
	    		} else if(dayForWeek==7){
	    			//时间
		    		excelObj[0] = objs[0].toString()+",星期日";
	    		}
	    		//用户名
	    		excelObj[1] = objs[1].toString();
    			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[1]+"' and useraccounts!='sunt' and useraccounts!='sunt2'";
				rs = stmt.executeQuery(sql);
		    	while (rs.next()){
	    			excelObj[2]=rs.getString(1)+",";
		    		excelObj[3]=rs.getString(2)+",";
		    	}
		    	
		    	int dayForWeek1 = dayForWeek(objs1[0].toString());
	    		if(dayForWeek1==1){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期一";
	    		} else if(dayForWeek1==2){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期二";
	    		} else if(dayForWeek1==3){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期三";
	    		} else if(dayForWeek1==4){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期四";
	    		} else if(dayForWeek1==5){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期五";
	    		} else if(dayForWeek1==6){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期六";
	    		} else if(dayForWeek1==7){
	    			//时间
		    		excelObj[4] = objs1[0].toString()+",星期日";
	    		}
	    		//用户名
	    		excelObj[5] = objs1[1].toString();
    			sql = "select concat(empname,empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[5]+"' and useraccounts!='sunt' and useraccounts!='sunt2'";
    			rs = stmt.executeQuery(sql);
		    	while (rs.next()){
		    		excelObj[6]=rs.getString(1)+",";
		    		excelObj[7]=rs.getString(2)+",";
		    	}
	    		empInfoList.add(excelObj);
	    		i++;
	    	}
	    	rs.close();
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
		return empInfoList;
	}
	
	//获取指定日期下岗位员工值班信息
	public int dayForWeek(String pTime) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.setTime(format.parse(pTime));
		int dayForWeek = 0;
		if(c.get(Calendar.DAY_OF_WEEK)==1){
			dayForWeek=7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK)-1;
		}
		return dayForWeek;
	}
	

	//获取指定日期下岗位员工值班信息
	public List getEmpInfosFromExcel(List excelList) throws Exception{
		List empInfoList = new ArrayList();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	String sql = "";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	for(int i=0;i<excelList.size();i++){
	    		Object[] objs = (Object[])excelList.get(i);
	    		String[] excelObj = new String[2];
	    		//用户名
	    		excelObj[0] = objs[1].toString();
    			sql = "select concat(empname,empmobilephone) infos from org_employee where empname='"+objs[0].toString()+"'";
				rs = stmt.executeQuery(sql);
		    	while (rs.next()){
		    		excelObj[1]=rs.getString(1)+",";
		    	}
	    		empInfoList.add(excelObj);
	    	}
	    	rs.close();
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
		return empInfoList;
	}
	
	//获取指定日期下岗位员工值班信息
	public List getSortList(List empInfoList) throws Exception{
		List sortlist = new ArrayList(19);
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("空管中心")){
				sortlist.add(0, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("运行管理中心")){
				sortlist.add(1, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("飞服中心")){
				sortlist.add(2, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("飞行计划处理中心")){
				sortlist.add(3, obj);
			}
		}
		
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("设备监控中心")){
				sortlist.add(4, obj);
			}
		}
		
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("技术保障中心")){
				sortlist.add(5, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("设备维修中心")){
				sortlist.add(6, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("网络公司")){
				sortlist.add(7, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("气象中心")){
				sortlist.add(8, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("山东分局")){
				sortlist.add(9, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("安徽分局")){
				sortlist.add(10, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("江苏分局")){
				sortlist.add(11, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("浙江分局")){
				sortlist.add(12, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("江西分局")){
				sortlist.add(13, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("福建分局")){
				sortlist.add(14, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("厦门空管站")){
				sortlist.add(15, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("青岛空管站")){
				sortlist.add(16, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("宁波空管站")){
				sortlist.add(17, obj);
			}
		}
		for(int i=0;i<empInfoList.size();i++){
			Object[] obj = (Object[])empInfoList.get(i);
			if(obj[0].toString().equals("温州空管站")){
				sortlist.add(18, obj);
			}
		}
		return  sortlist;
	 }
	
	
	//微信端局领导换班数据
	public List getChangeLeaderDuty() throws Exception{
		List excelInfos = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		//获取当前月第一天：
		Calendar c = Calendar.getInstance();    
		//设置为1号,当前日期既为本月第一天 
		c.set(Calendar.DAY_OF_MONTH,1);
		String firstDay = format.format(c.getTime());
		//获取当前月最后一天
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));  
		String lastDay = format.format(c.getTime());
		String sql = "select whir$dutyroster_id,whir$dutyroster_year,whir$dutyroster_mouth,whir$dutyroster_data,whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
				" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+firstDay+"' and '"+lastDay+"' order by whir$dutyroster_data,whir$dutyRoster_f3537";
		System.out.println("sql:::"+sql);
		excelInfos = new WatchArrangeUtils().getList(sql);
		return excelInfos;
	}
	
	//微信端中心领导换班数据
	public List getChangeCenterLeaderDuty(String centerName) throws Exception{
		List excelInfos = new ArrayList();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); 
		//获取当前月第一天：
		Calendar c = Calendar.getInstance();    
		//设置为1号,当前日期既为本月第一天 
		c.set(Calendar.DAY_OF_MONTH,1);
		String firstDay = format.format(c.getTime());
		//获取当前月最后一天
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));  
		String lastDay = format.format(c.getTime());
		List excelList = new ArrayList();
		String sql = "select whir$centreduty_id,whir$centreduty_year,whir$centreduty_mouth,whir$centreduty_data ,whir$centreduty_dutystaff " +
		" from whir$centreduty where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) between '"+firstDay+"' and '"+lastDay+"' and whir$centreduty_department='"+centerName+"' order by whir$centreduty_data";
		excelInfos = new WatchArrangeUtils().getList(sql);
		return excelInfos;
	}
}
