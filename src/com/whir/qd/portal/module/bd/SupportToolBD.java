package com.whir.qd.portal.module.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class SupportToolBD extends HibernateBase {
	
	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public  Map<String,String> getSupportTool() throws Exception{
		Date currentDate =new Date();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String count1="0";
	    String count2="0";
	    String count3="0";
	    String count4="0";
	    String count5="0";
	    String count6="0";
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count1,count2,count3,count4,count5,count6 from (select count(*) as count1 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='1') a," +
	      		"(select count(*) as count2 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='2') b," +
	      		"(select count(*) as count3 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='3') c," +
	      		"(select count(*) as count4 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='4') d," +
	      		"(select count(*) as count5 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='5') e," +
	      		"(select count(*) as count6 from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='6') f ";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count1 = rs.getString(1);
	        	count2 = rs.getString(2);
	        	count3 = rs.getString(3);
	        	count4 = rs.getString(4);
	        	count5 = rs.getString(5);
	        	count6 = rs.getString(6);
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
	   Map<String,String> map = new HashMap<String,String>();
       map.put("tab1", count1);
       map.put("tab2", count2);
       map.put("tab3", count3);
       map.put("tab4", count4);
       map.put("tab5", count5);
       map.put("tab6", count6);
	   return map;
	}
	
	
	/**
	 * 查找支持工具数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> supportToolListData(int curPage,int pageSize,String module){
		Date currentDate =new Date();
		int max = curPage*pageSize;
		int min = (curPage-1)*pageSize+1;
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			conn = dsb.getDataSource().getConnection();
	        stmt = conn.createStatement();
	        StringBuffer sqlBuf = new StringBuffer();
	        sqlBuf.append("SELECT * FROM (select C.*,rownum rn from(select supporttool_date,content from QD_SUPPORTTOOL where supporttool_date='"+simpleDateFormat.format(currentDate)+"' and supporttool_system='"+module+"') C where rownum<="+max+") where rn>="+min);
	      ResultSet rs = stmt.executeQuery(sqlBuf.toString());
	      while (rs.next()) {
	    	  Map<String, Object> map = new HashMap<String, Object>();
	    	  String supporttoolDate = rs.getString(1);
	    	  String content = rs.getString(2);
	    	  map.put("supporttoolDate", supporttoolDate);
	    	  map.put("content", content);
	    	  list.add(map);
	      }
	      rs.close();
	      stmt.close();
	      conn.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return list;
	}
}
