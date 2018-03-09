package com.whir.qd.portal.module.bd;

import java.sql.Connection;
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

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class DutyMessageBD extends HibernateBase {
	
	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	public  List<Map<String,String>> getDutyMessage() throws Exception{
		Date currentDate =new Date();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    List<Map<String,String>> list = new ArrayList<Map<String,String>>();
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select dutymessage_date,weather,temperature,department,duty_name,duty_contact_way from qd_dutymessage " +
	      		"where dutymessage_date='"+simpleDateFormat.format(currentDate)+"' order by department_id";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	Map<String,String> map = new HashMap<String,String>();
	        	String dutyMessageDate = rs.getString("dutymessage_date");
	        	//String[] dutyMessageDateArray = dutyMessageDate.split("-");
	        	map.put("dutymessageDate", dutyMessageDate.substring(0, 4)+"年"+dutyMessageDate.substring(4, 6)+"月"+dutyMessageDate.substring(6, 8)+"日");
	        	int day = dayForWeek(dutyMessageDate);
	        	map.put("day", "星期"+day);
	        	map.put("weather", rs.getString("weather"));
	        	map.put("temperature", rs.getString("temperature"));
	        	map.put("department", rs.getString("department"));
	        	map.put("duty_name", rs.getString("duty_name"));
	        	map.put("dutyContactWay", rs.getString("duty_contact_way"));
	        	list.add(map);
	        }
	        rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	   return list;
	}
	
	/** 
	    * 判断当前日期是星期几<br> 
	    * <br> 
	    * @param pTime 修要判断的时间<br> 
	    * @return dayForWeek 判断结果<br> 
	    * @Exception 发生异常<br> 
	    */  
	public static int dayForWeek(String pTime) throws Exception {  
		 Calendar c = Calendar.getInstance();  
		 c.setTime(simpleDateFormat.parse(pTime));  
		 int dayForWeek = 0;  
		 if(c.get(Calendar.DAY_OF_WEEK) == 1){  
		  dayForWeek = 7;  
		 }else{  
		  dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;  
		 }  
		 return dayForWeek;  
	} 
}
