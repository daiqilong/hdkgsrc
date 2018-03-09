package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.DataSourceBase;

public class MyScheduleBD {
	/**
	 * 
	 */
	private static final long serialVersionUID = -719868959247581033L;
	private static Logger logger = Logger.getLogger(MyScheduleBD.class.getName());

	public  List<Map<String,Object>> getMyScheduleInfo(String userId,String dataString ) throws SQLException, ParseException{
		Date date = new Date(Long.parseLong(dataString)); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		String receptionDate = sdf.format(date); 
		Date dt1 = sdf.parse(receptionDate);
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    Statement stmt =  null;
	    ResultSet rs1 = null;
	    PreparedStatement pstmt1 = null;
	    
	    String eventtitle = "";
	    String eventbegindate = "";
	    String eventenddate = "";
	    String eventbegintime = "";
	    String eventendtime = "";
        String beginTime = "";
        String endTime = "";
        String eventcontent = "";
        String eventfullday = "";
	 try{
		 conn = dsb.getDataSource().getConnection();
	     conn.setAutoCommit(false);
	     String sql= "SELECT EVENTTITLE,EVENTBEGINDATE,EVENTENDDATE,EVENTBEGINTIME,EVENTENDTIME,EVENTCONTENT,EVENTFULLDAY FROM OA_EVENT WHERE EVENTEMPID="+userId+" OR ATTENDID LIKE '%"+userId+"%'";
	     stmt = conn.createStatement();
	     rs = stmt.executeQuery(sql);
         while (rs.next()) {
        	 Map<String,Object> map = new HashMap<String,Object>();
        	 eventtitle = rs.getString("EVENTTITLE");
        	 eventbegindate = rs.getString("EVENTBEGINDATE");
        	 eventenddate = rs.getString("EVENTENDDATE");
        	 eventbegintime = rs.getString("EVENTBEGINTIME");
        	 eventendtime = rs.getString("EVENTENDTIME");
        	 eventcontent = rs.getString("EVENTCONTENT");
        	 eventfullday = rs.getString("EVENTFULLDAY");
        	 Date date1 = new Date(Long.parseLong(eventbegindate)); 
        	 Date date2 = new Date(Long.parseLong(eventenddate));
        	 beginTime = randerTime(eventbegintime);
        	 endTime = randerTime(eventendtime);
     		 if(dt1.getTime()==date1.getTime() || dt1.getTime()==date2.getTime()){
     			if("1".equals(eventfullday)){
     				map.put("time", "全天"); 
     			} else {
     				map.put("time", beginTime+"-"+endTime);
     			}
				map.put("title",eventtitle);
				map.put("eventcontent",eventcontent);
				list.add(map);
     		 }
         }
         if(list.isEmpty() || list.size()==0){
        	 String sql1 = 
      	        "SELECT DISTINCT T.WHIR$T3037_ID,T.WHIR$T3037_F3203,T.WHIR$T3037_F3204,T.WHIR$T3037_F3206," +
      	        "T.WHIR$T3037_F3207,A.WORKMAINLINKFILE " +
      	        "FROM EZOFFICE.WF_WORK A,  EZOFFICE.WHIR$T3037 T WHERE A.WF_CUREMPLOYEE_ID=? AND " +
      	        "A.WORKFILETYPE='会议通知流程' AND T.WHIR$T3037_ID=A.WORKRECORD_ID AND " +
      	        "SUBSTR(T.WHIR$T3037_F3206,0,10) =?";
        	 pstmt1 = conn.prepareStatement(sql1);
             pstmt1.setInt(1, Integer.parseInt(userId));
             pstmt1.setString(2, receptionDate);
             rs1 = pstmt1.executeQuery();
        	 while (rs1.next()) {
        		Map<String,Object> map = new HashMap<String,Object>();
   	    	  	map.put("title", rs1.getString("whir$t3037_f3204"));
   	    	  	map.put("time", rs1.getString("whir$t3037_f3206"));
   	    	  	map.put("eventcontent","");
   	    	  	list.add(map);
               }
         }
	    } catch (Exception e) {
	      conn.rollback();
	      System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
	    	//log
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
	    	if(rs1 != null){
	    	try{
	    	rs1.close();
	    	}catch(Exception e){
	    	//log
	    	}
	    	}
	    	if(pstmt1 != null){
	    	try
	    	{
	    	pstmt1.close();
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
	
	 public String randerTime(String time)
	  {
	    StringBuffer sbRet = new StringBuffer();
	    if (!"".equals(time))
	    {
	      sbRet.append(Integer.parseInt(time) / 3600 < 10 ? "0" : "")
	        .append(Integer.parseInt(time) / 3600).append(":").append(
	        Integer.parseInt(time) % 3600 / 60 < 10 ? "0" : 
	        "").append(
	        Integer.parseInt(time) % 3600 / 60);
	    }
	    return sbRet.toString();
	  }
}
