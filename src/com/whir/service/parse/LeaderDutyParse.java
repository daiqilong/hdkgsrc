package com.whir.service.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.common.util.DataSourceBase;
import com.whir.ezoffice.watchmanager.actionsupport.WatchpostAction;
import com.whir.ezoffice.watchmanager.actionsupport.WatchshowAction;
import com.whir.portal.basedata.bd.WatchInfosOnHomePage;
import com.whir.rd.util.WatchArrangeUtils;
import com.whir.service.common.AbstractParse;

public class LeaderDutyParse extends AbstractParse{
	public LeaderDutyParse(Document doc){
	    super(doc);
	}
	
	public String getCurrentLeaderDutyDate() throws Exception {
			System.out.println("获取getCurrentLeaderDutyDate::");
			String result = "";
		    String message = "数据传输成功。";
		    Element rootElement = doc.getRootElement();
		    Element userIdElement = rootElement.getChild("userId");
		    String userId = userIdElement.getValue();
		    //leaderDutyDate当前人是局领导时的换班时间
		    String dutyName = getDutyName(userId);
		    //leaderDutyDate当前人是局领导时的换班时间
		    String leaderDutyDate = getCurrentLeaderDutyDate(userId);
		    //centerLeaderDutyDate当前人是分局站/中心领导时的换班时间
		    String centerLeaderDutyDate = getCurrentCenterLeaderDutyDate(userId);
		    result += "<result>";
		    result += "<dutyName><![CDATA[" + dutyName +"]]></dutyName>";
		    result += "<leaderDutyDate><![CDATA[" + leaderDutyDate +"]]></leaderDutyDate>";
		    result += "<centerLeaderDutyDate><![CDATA[" + centerLeaderDutyDate +"]]></centerLeaderDutyDate>";
		    result += "</result>";
		    this.setMessage("1", message);
		    return result;
	}
	
	public String getDutyName(String userId) throws Exception{
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String dutyName="";
		try {
			String sql = "select empname from org_employee where emp_id='"+userId+"'";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	rs = stmt.executeQuery(sql);
	    	while (rs.next()){
	    		dutyName = rs.getString(1);
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
		return dutyName;
	}
	
	public String getCurrentLeaderDutyDate(String userId) throws Exception{
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String leaderDutyDate="";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String nowDate = format.format(new Date());
		StringBuffer str = new StringBuffer();
		try {
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data " +
			" from whir$dutyroster a, org_employee b where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) like '%2017-12%' and b.emp_id='"+userId+"' and WHIR$DUTYROSTER_DUTYSTAFF=b.empname order by data";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	rs = stmt.executeQuery(sql);
	    	while (rs.next()){
	    		str.append(rs.getString(1)).append(",");
	    	}
	    	System.out.println("strstrstrstr:::::"+str.toString());
	    	if(!"".equals(str.toString()) && str.toString()!=null){
	    		leaderDutyDate = str.toString().substring(0,str.toString().length()-1);
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
		return leaderDutyDate;
	}
	
	public String getCurrentCenterLeaderDutyDate(String userId) throws Exception{
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String centerLeaderDutyDate="";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String nowDate = format.format(new Date());
		StringBuffer str = new StringBuffer();
		try {
			String sql = "select distinct concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) as data " +
			" from whir$centreduty a, org_employee b where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) like '%2017-12%' and b.emp_id='20989' and WHIR$centreduty_DUTYSTAFF=b.empname order by data";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	rs = stmt.executeQuery(sql);
	    	while (rs.next()){
	    		str.append(rs.getString(1)).append(",");
	    	}
	    	System.out.println("str1str1str1str1:::::"+str);
	    	if(!"".equals(str.toString()) && str.toString()!=null){
	    		centerLeaderDutyDate = str.toString().substring(0,str.toString().length()-1);
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
		return centerLeaderDutyDate;
	}
	/**
	 * 获取局领导值班日历数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String getLeaderDutyCalender() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element queryDateElement = rootElement.getChild("queryDate");
        String queryDate = queryDateElement.getValue();
        
        List excelInfos = showLeaderDutyCalender(queryDate);
      
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<dates><![CDATA[" + obj[0] +"]]></dates>";
          result += "<mWatchInfo><![CDATA[" + obj[2] + "]]></mWatchInfo>";
          result += "<dates1><![CDATA[" + obj[4] +"]]></dates1>";
          result += "<mWatchInfo1><![CDATA[" + obj[6] + "]]></mWatchInfo1>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	
	//默认显示领导当月的值班表微信端数据接入
	public List showLeaderDutyCalender(String dutyDate) throws Exception{
		List excelInfos = new ArrayList();
		if(dutyDate != null && !"".equals(dutyDate)){
			int year = Integer.parseInt(dutyDate.substring(0, 4));
			int mouth = Integer.parseInt(dutyDate.substring(5, 7));
			String arrangeDateFrom = getFirstDayOfMonth(year,mouth);
			String arrangeDateEnd = getLastDayOfMonth(year,mouth);
			
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) as data ,  whir$dutyroster_dutystaff,whir$dutyRoster_f3537 " +
					" from whir$dutyroster where concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' order by data,whir$dutyRoster_f3537";
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowExcelInfos(excelList);
			}
		}
		return excelInfos;
	}
	
	
	public static String getFirstDayOfMonth(int year,int month)  
	  {  
	      Calendar cal = Calendar.getInstance();  
	      //设置年份  
	      cal.set(Calendar.YEAR,year);  
	      //设置月份  
	      cal.set(Calendar.MONTH, month-1);  
	      //获取某月最大天数  
	      int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);  
	      //设置日历中月份的最大天数  
	      cal.set(Calendar.DAY_OF_MONTH, firstDay);  
	      //格式化日期  
	      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	      String firstDayOfMonth = sdf.format(cal.getTime());  
	         
	      return firstDayOfMonth;  
	  }
	
	public static String getLastDayOfMonth(int year,int month)  
	  {  
	      Calendar cal = Calendar.getInstance();  
	      //设置年份  
	      cal.set(Calendar.YEAR,year);  
	      //设置月份  
	      cal.set(Calendar.MONTH, month-1);  
	      //获取某月最大天数  
	      int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);  
	      //设置日历中月份的最大天数  
	      cal.set(Calendar.DAY_OF_MONTH, lastDay);  
	      //格式化日期  
	      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
	      String lastDayOfMonth = sdf.format(cal.getTime());  
	         
	      return lastDayOfMonth;  
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
    			sql = "select concat(concat(empname,','),empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[1]+"' and useraccounts!='sunt' and useraccounts!='sunt2'";
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
    			sql = "select concat(concat(empname,','),empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[5]+"' and useraccounts!='sunt' and useraccounts!='sunt2'";
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
	/**
	 * 新改版后局领导换班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String changeLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        Element pickerDateElement = rootElement.getChild("pickerDate");
        String pickerDate = pickerDateElement.getValue();
        Element nowDutyDateElement = rootElement.getChild("nowDutyDate");
        String nowDutyDate = nowDutyDateElement.getValue();
        Element pickerLingdaoElement = rootElement.getChild("pickerLingdao");
        String pickerLingdao = pickerLingdaoElement.getValue();
        Boolean returnVal = changeLeaderDuty(pickerDate,pickerLingdao,nowDutyDate,userId);
        System.out.println("returnVal:::::"+returnVal);
	    if (returnVal) {
	    	//换班后该当前领导值班日期改变
	    	String currentLenderDutyDate = getCurrentLeaderDutyDate(userId);
    	  result += "<result>";
          result += "<dutyDate><![CDATA["+currentLenderDutyDate+"]]></dutyDate>";
          result += "</result>";
         }
	  this.setMessage("1", message);
      return result;
	}
	
	/**
	 * 新改版后局领导替班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String relayLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        Element nowDutyDateElement = rootElement.getChild("nowDutyDate");
        String nowDutyDate = nowDutyDateElement.getValue();
        Element pickerLingdaoElement = rootElement.getChild("pickerLingdao");
        String pickerLingdao = pickerLingdaoElement.getValue();
        Boolean returnVal = relayLeaderDuty(pickerLingdao,nowDutyDate,userId,"0");
        System.out.println("returnVal:::::"+returnVal);
	    if (returnVal) {
	    	//换班后该当前领导值班日期改变
	    	String currentLenderDutyDate = getCurrentLeaderDutyDate(userId);
    	  result += "<result>";
          result += "<dutyDate><![CDATA["+currentLenderDutyDate+"]]></dutyDate>";
          result += "</result>";
         }
	  this.setMessage("1", message);
      return result;
	}
	/**
	 * 新改版后分局站/中心领导换班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String changeCenterLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        Element pickerDateElement = rootElement.getChild("pickerDate");
        String pickerDate = pickerDateElement.getValue();
        Element centerNowDutyDateElement = rootElement.getChild("centerNowDutyDate");
        String centerNowDutyDate = centerNowDutyDateElement.getValue();
        Element pickerLingdaoElement = rootElement.getChild("pickerLingdao");
        String pickerLingdao = pickerLingdaoElement.getValue();
        Boolean returnVal = changeCenterLeaderDuty(pickerDate,pickerLingdao,centerNowDutyDate,userId);
        System.out.println("returnVal:::::"+returnVal);
	    if (returnVal) {
	    	//换班后该当前领导值班日期改变
	    	String currentLenderDutyDate = getCurrentCenterLeaderDutyDate(userId);
    	  result += "<result>";
          result += "<dutyDate><![CDATA["+currentLenderDutyDate+"]]></dutyDate>";
          result += "</result>";
         }
	  this.setMessage("1", message);
      return result;
	}
	
	
	/**
	 * 新改版后分局站/中心领导替班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String relayCenterLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
	    Element userIdElement = rootElement.getChild("userId");
        String userId = userIdElement.getValue();
        Element centerNowDutyDateElement = rootElement.getChild("centerNowDutyDate");
        String centerNowDutyDate = centerNowDutyDateElement.getValue();
        Element pickerLingdaoElement = rootElement.getChild("pickerLingdao");
        String pickerLingdao = pickerLingdaoElement.getValue();
        Boolean returnVal = relayLeaderDuty(pickerLingdao,centerNowDutyDate,userId,"1");
        System.out.println("returnVal:::::"+returnVal);
	    if (returnVal) {
	    	//换班后该当前领导值班日期改变
	    	String currentLenderDutyDate = getCurrentCenterLeaderDutyDate(userId);
    	  result += "<result>";
          result += "<dutyDate><![CDATA["+currentLenderDutyDate+"]]></dutyDate>";
          result += "</result>";
         }
	  this.setMessage("1", message);
      return result;
	}
	
	/**
	 * 微信新改版后局领导换班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean changeLeaderDuty(String pickerDate,String pickerLingdao,String nowDutyDate,String userId) throws Exception {
		String inforId = getInfoDutyrosterId(pickerDate,pickerLingdao,nowDutyDate,userId,"0");
		System.out.println("inforId::::"+inforId);
		List<Map<String, String>> list = getInfochangeShifts(inforId.substring(0,inforId.length()-1),"0");
	    Map<String, String> map = list.get(0);
	    Map<String, String> map1 = list.get(1);
	    
	    for(int i=0;i<list.size();i++){
	    	String id = "";
			String dutyStaff = "";
	    	if(i==0){
	    		id = map.get("id");
	    		dutyStaff = map1.get("dutyStaff");
	    	} else {
	    		id = map1.get("id");
	    		dutyStaff = map.get("dutyStaff");
	    	}
	    	String sql = "UPDATE WHIR$DUTYROSTER SET WHIR$DUTYROSTER_DUTYSTAFF=? WHERE WHIR$DUTYROSTER_ID=?";
	    	changeShiftsSql(sql,id,dutyStaff);
	    } 
	    return true;
	}
	
	/**
	 * 微信新改版后局领导替班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean relayLeaderDuty(String pickerLingdao,String nowDutyDate,String userId,String flag) throws Exception {
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        String sql = "";
	        if("0".equals(flag)){
	        	sql ="UPDATE whir$dutyroster SET whir$dutyroster_dutystaff = (select empname from org_employee where emp_id=?) where whir$dutyroster_dutystaff=? and concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data))=?";
	        } else {
	        	sql ="UPDATE WHIR$centreduty SET WHIR$centreduty_dutystaff = (select empname from org_employee where emp_id=?) where WHIR$centreduty_dutystaff=? and concat(concat(WHIR$centreduty_year,concat('-',WHIR$centreduty_mouth)),concat('-',WHIR$centreduty_data))=?";
	        }
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, Integer.parseInt(userId));
	        pstmt.setString(2, pickerLingdao);
	        pstmt.setString(3, nowDutyDate);
	        pstmt.executeUpdate();
	        conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
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
	    return true;
	}
	
	public String getInfoDutyrosterId(String pickerDate,String pickerLingdao,String nowDutyDate,String userId,String flag) throws SQLException{
		StringBuffer sb = new StringBuffer();
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    String sql = "";
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        if("0".equals(flag)){
	        	sql = "SELECT distinct WHIR$DUTYROSTER_ID FROM WHIR$DUTYROSTER a,org_employee b " +
        		" WHERE concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) ='"+nowDutyDate+"' and WHIR$DUTYROSTER_DUTYSTAFF='"+pickerLingdao+"' or "+
        		" (concat(concat(whir$dutyroster_year,concat('-',whir$dutyroster_mouth)),concat('-',whir$dutyroster_data)) ='"+pickerDate+"' and b.emp_id='"+userId+"' and WHIR$DUTYROSTER_DUTYSTAFF=b.empname)";
	        	pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	sb.append("'").append(rs.getString("WHIR$DUTYROSTER_ID")).append("',");
		        }
	        } else {
	        	sql = "SELECT distinct WHIR$centreduty_ID FROM WHIR$centreduty a,org_employee b " +
        		" WHERE concat(concat(WHIR$centreduty_year,concat('-',WHIR$centreduty_mouth)),concat('-',WHIR$centreduty_data)) ='"+nowDutyDate+"' and WHIR$centreduty_DUTYSTAFF='"+pickerLingdao+"' or "+
        		" (concat(concat(WHIR$centreduty_year,concat('-',WHIR$centreduty_mouth)),concat('-',WHIR$centreduty_data)) ='"+pickerDate+"' and b.emp_id='"+userId+"' and WHIR$centreduty_DUTYSTAFF=b.empname)";
	        	pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	sb.append("'").append(rs.getString("WHIR$centreduty_ID")).append("',");
		        }
	        }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message11:" + e.getMessage());
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
	    return sb.toString();
	}
	
	
	public List<Map<String, String>> getInfochangeShifts(String infoIds,String flag) throws SQLException{
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    String sql = "";
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        if("0".equals(flag)){
	        	sql = "SELECT WHIR$DUTYROSTER_ID,WHIR$DUTYROSTER_DUTYSTAFF FROM WHIR$DUTYROSTER WHERE WHIR$DUTYROSTER_ID IN ("+infoIds+")";
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	Map<String,String> map = new HashMap<String,String>();
		        	String id = rs.getString("WHIR$DUTYROSTER_ID");
		        	String dutyStaff = rs.getString("WHIR$DUTYROSTER_DUTYSTAFF");
		        	map.put("id", id);
		        	map.put("dutyStaff", dutyStaff);
		            list.add(map);
		        }
	        } else {
	        	sql = "SELECT WHIR$centreduty_ID,WHIR$centreduty_DUTYSTAFF FROM WHIR$centreduty WHERE WHIR$centreduty_ID IN ("+infoIds+")";
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	Map<String,String> map = new HashMap<String,String>();
		        	String id = rs.getString("WHIR$centreduty_ID");
		        	String dutyStaff = rs.getString("WHIR$centreduty_DUTYSTAFF");
		        	map.put("id", id);
		        	map.put("dutyStaff", dutyStaff);
		            list.add(map);
		        }
	        }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message11:" + e.getMessage());
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
	
	public void changeShiftsSql(String sql,String id,String dutyStaff) throws SQLException{
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, dutyStaff);
	        pstmt.setInt(2, Integer.parseInt(id));
	        pstmt.executeUpdate();
	        conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
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
  }
	
	/**
	 * 微信新改版后分局站/中心局领导换班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean changeCenterLeaderDuty(String pickerDate,String pickerLingdao,String nowDutyDate,String userId) throws Exception {
		String inforId = getInfoDutyrosterId(pickerDate,pickerLingdao,nowDutyDate,userId,"1");
		System.out.println("inforId::::"+inforId);
		List<Map<String, String>> list = getInfochangeShifts(inforId.substring(0,inforId.length()-1),"1");
	    Map<String, String> map = list.get(0);
	    Map<String, String> map1 = list.get(1);
	    
	    for(int i=0;i<list.size();i++){
	    	String id = "";
			String dutyStaff = "";
	    	if(i==0){
	    		id = map.get("id");
	    		dutyStaff = map1.get("dutyStaff");
	    	} else {
	    		id = map1.get("id");
	    		dutyStaff = map.get("dutyStaff");
	    	}
	    	String sql = "UPDATE WHIR$centreduty SET WHIR$centreduty_DUTYSTAFF=? WHERE WHIR$centreduty_ID=?";
	    	changeShiftsSql(sql,id,dutyStaff);
	    } 
	    return true;
	}
	/**
	 * 微信新旧局领导当日值班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	
	public String getLeaderDutyData()
	  {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
      Element styleElement = rootElement.getChild("style");
      String style = styleElement.getValue();
      List dutyList = null;
	    WatchInfosOnHomePage wiohp = new WatchInfosOnHomePage();
	    if("100".equals(style)){
	    	dutyList = wiohp.getWatchLeaders("100");//局领导岗位等级为100
	    } else if("10".equals(style)){
	    	dutyList = wiohp.getWatchCenterLeader("10");//中心领导岗位等级10
	    }
		System.out.println("dutyList::::"+dutyList.size());
	    if (dutyList != null && dutyList.size() > 0) {
        for (int i = 0; i < dutyList.size(); i++) {
      	  Object[] obj = (Object[]) dutyList.get(i);
      	  String butyName="";
      	  if(obj[1]!=null){
      		  butyName = obj[1].toString().substring(0, obj[1].toString().length()-1);
      	  }
            result += "<result>";
            result += "<empObj0><![CDATA[" + obj[0] +"]]></empObj0>";
            result += "<empObj1><![CDATA[" + butyName + "]]></empObj1>";
            result += "</result>";
        }
      }
	    this.setMessage("1", message);
    return result;
	 }
	
	
	public String getCenterLeaderDutyQuery() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element dutyDateElement = rootElement.getChild("dutyDate");
        String dutyDate = dutyDateElement.getValue();
        Element centerNameElement = rootElement.getChild("centerName");
        String centerName = centerNameElement.getValue();
        WatchshowAction watchshowAction = new WatchshowAction();
        List excelInfos = watchshowAction.showWxCenterPostquery(dutyDate,centerName);
      
		System.out.println("excelInfos::::"+excelInfos.size());
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<dates><![CDATA[" + obj[0] +"]]></dates>";
          result += "<mWatchInfo><![CDATA[" + obj[2] + "]]></mWatchInfo>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	
	
	/**
	 * 获取分局站/中心值班领导值班日历数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	public String getCenterLeaderDutyCalender() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element queryDateElement = rootElement.getChild("queryDate");
        String queryDate = queryDateElement.getValue();
        Element departmentElement = rootElement.getChild("department");
        String department = departmentElement.getValue();
        List excelInfos = showCenterLeaderDutyCalender(queryDate,department);
      
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<dates><![CDATA[" + obj[0] +"]]></dates>";
          result += "<mWatchInfo><![CDATA[" + obj[2] + "]]></mWatchInfo>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	
	//默认显示领导当月的值班表微信端数据接入
	public List showCenterLeaderDutyCalender(String dutyDate,String department) throws Exception{
		List excelInfos = new ArrayList();
		if(dutyDate != null && !"".equals(dutyDate)){
			int year = Integer.parseInt(dutyDate.substring(0, 4));
			int mouth = Integer.parseInt(dutyDate.substring(5, 7));
			String arrangeDateFrom = getFirstDayOfMonth(year,mouth);
			String arrangeDateEnd = getLastDayOfMonth(year,mouth);
			
			List excelList = new ArrayList();
			String sql = "select distinct concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) as data ,  whir$centreduty_dutystaff " +
			" from whir$centreduty where concat(concat(whir$centreduty_year,concat('-',whir$centreduty_mouth)),concat('-',whir$centreduty_data)) between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"' and whir$centreduty_department='"+department+"' order by data";
			excelList = new WatchArrangeUtils().getList(sql);
			if(excelList!=null && !excelList.isEmpty()){
				excelInfos = getShowCenterExcelInfos(excelList);
			}
		}
		return excelInfos;
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
    			sql = "select concat(concat(empname,','),empmobilephone) infos,useraccounts from org_employee where empname='"+excelObj[1]+"'";
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
	//-------------------------------下面是微信端老值班管理方法------------------------------//
	
	public String getLeaderDutyQuery() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element dutyDateElement = rootElement.getChild("dutyDate");
        String dutyDate = dutyDateElement.getValue();
      
        WatchshowAction watchshowAction = new WatchshowAction();
        List excelInfos = watchshowAction.showWxLeaderWatchINMonth(dutyDate);
      
		System.out.println("excelInfos::::"+excelInfos.size());
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<dates><![CDATA[" + obj[0] +"]]></dates>";
          result += "<mWatchInfo><![CDATA[" + obj[2] + "]]></mWatchInfo>";
          result += "<dates1><![CDATA[" + obj[4] +"]]></dates1>";
          result += "<mWatchInfo1><![CDATA[" + obj[6] + "]]></mWatchInfo1>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	
	
	
	
	public String getChangeLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    
        WatchshowAction watchshowAction = new WatchshowAction();
        List excelInfos = watchshowAction.getChangeLeaderDuty();
      
		System.out.println("excelInfos::::"+excelInfos.size());
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<dutyrosterId><![CDATA[" + obj[0] +"]]></dutyrosterId>";
          result += "<dutyrosterYear><![CDATA[" + obj[1] + "]]></dutyrosterYear>";
          result += "<dutyrosterMouth><![CDATA[" + obj[2] + "]]></dutyrosterMouth>";
          result += "<dutyrosterData><![CDATA[" + obj[3] + "]]></dutyrosterData>";
          result += "<dutyrosterDutystaff><![CDATA[" + obj[4] + "]]></dutyrosterDutystaff>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	public String getChangeCenterLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element centerNameElement = rootElement.getChild("centerName");
        String centerName = centerNameElement.getValue();
        WatchshowAction watchshowAction = new WatchshowAction();
        List excelInfos = watchshowAction.getChangeCenterLeaderDuty(centerName);
      
		System.out.println("excelInfos::::"+excelInfos.size());
	    if (excelInfos != null && excelInfos.size() > 0) {
        for (int i = 0; i < excelInfos.size(); i++) {
      	  Object[] obj = (Object[]) excelInfos.get(i);
          result += "<result>";
          result += "<centredutyId><![CDATA[" + obj[0] +"]]></centredutyId>";
          result += "<centredutyYear><![CDATA[" + obj[1] + "]]></centredutyYear>";
          result += "<centredutyMouth><![CDATA[" + obj[2] + "]]></centredutyMouth>";
          result += "<centredutyData><![CDATA[" + obj[3] + "]]></centredutyData>";
          result += "<centredutyDutystaff><![CDATA[" + obj[4] + "]]></centredutyDutystaff>";
          result += "</result>";
        }
      }
	  this.setMessage("1", message);
      return result;
	}
	
	public String setChangeLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element chestrsElement = rootElement.getChild("chestrs");
        String chestrs = chestrsElement.getValue();
        WatchpostAction watchpostAction = new WatchpostAction();
        Boolean returnVal = watchpostAction.setChangeLeaderDuty(chestrs);
      
	    if (returnVal) {
    	  result += "<result>";
          result += "<flag><![CDATA[0]]></flag>";
          result += "</result>";
      } else {
    	  result += "<result>";
          result += "<flag><![CDATA[1]]></flag>";
          result += "</result>";
      }
	  this.setMessage("1", message);
      return result;
	}
	
	public String setChangeCenterLeaderDuty() throws Exception {
		String result = "";
	    String message = "数据传输成功。";
	    Element rootElement = doc.getRootElement();
        Element chestrsElement = rootElement.getChild("chestrs");
        String chestrs = chestrsElement.getValue();
        WatchpostAction watchpostAction = new WatchpostAction();
        Boolean returnVal = watchpostAction.setChangeCenterLeaderDuty(chestrs);
      
	    if (returnVal) {
    	  result += "<result>";
          result += "<flag><![CDATA[0]]></flag>";
          result += "</result>";
      } else {
    	  result += "<result>";
          result += "<flag><![CDATA[1]]></flag>";
          result += "</result>";
      }
	  this.setMessage("1", message);
      return result;
	}
}