package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.whir.common.util.DataSourceBase;

public class WainingInformationBD {
	public Map<String, Object> getWainingInforList() throws SQLException{
		Map<String,Object> map = new HashMap<String,Object>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    ResultSet rs = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	        String nowDate = format.format(new Date());
	        System.out.println("nowDate"+nowDate);
	        String nowDate1 = format1.format(new Date());
	        String sql = "SELECT WHIR$WARN_INFOR_CONTENT,WHIR$WARN_INFOR_DATETIME,WHIR$WARN_INFOR_COLOUR,WHIR$WARN_INFOR_PREENDDATE FROM WHIR$WARN_INFOR where WHIR$WARN_INFOR_DATETIME='"+nowDate+"'";
	        pstmt = conn.prepareStatement(sql);
	        rs = pstmt.executeQuery();
	        String content = "";
	        String datetime = "";
	        String colour = "";
	        String preenddate = "";
	        while(rs.next()){
	        	content = rs.getString("WHIR$WARN_INFOR_CONTENT");
	        	datetime = rs.getString("WHIR$WARN_INFOR_DATETIME");
	        	colour = rs.getString("WHIR$WARN_INFOR_COLOUR");
	        	preenddate = rs.getString("WHIR$WARN_INFOR_PREENDDATE");
	        	//1表示只比较日期,2表示比较日期时间
	        	if(compare_date(nowDate,datetime,format,"1")){
	        		if("".equals(preenddate) || preenddate==null){
	        			map.put("datetime", datetime);
			        	map.put("content", content);
			        	map.put("colour", colour);
			        	map.put("preenddate", preenddate);
	        		}else if(compare_date(nowDate1,preenddate,format1,"2")){
	        			map.put("datetime", datetime);
			        	map.put("content", content);
			        	map.put("colour", colour);
			        	map.put("preenddate", preenddate);	
		        	}
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
	    return map;
	}
	
	public Boolean compare_date(String DATE1, String DATE2,SimpleDateFormat format1,String type) {
        try {
        	Date dt1 = format1.parse(DATE1);
  		  	Date dt2 = format1.parse(DATE2);
  		  	if("1".equals(type)){
  		  		if(dt1.getTime() == dt2.getTime()){
  		  			return true;
  		  		} else {
  		  			return false;
  		  		}
  		  	}
  		  	if("2".equals(type)){
		  		if(dt1.getTime() < dt2.getTime()){
		  			return true;
		  		} else {
		  			return false;
		  		}
		  	}
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }
}
