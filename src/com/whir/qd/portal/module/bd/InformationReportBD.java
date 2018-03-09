package com.whir.qd.portal.module.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class InformationReportBD extends HibernateBase {
	protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * 查找空管新闻图片数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> getPhotoImage() throws HibernateException, SQLException {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String sql = "SELECT * FROM (select A.*,rownum rn from ( " +
		    		"select b.information_id,b.channel_id,b.informationtitle,b.informationtype,c.accessorysavename from oa_information b, OA_INFORMATIONACCESSORY c where b.channel_id='6695' " +
		    		"and b.information_id=c.information_id order by informationissuetime desc) A where rownum<=5) where rn>=1";
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	  Map<String, Object> map = new HashMap<String, Object>();
		    	  String informationId = rs.getString(1);
		    	  String channelId = rs.getString(2);
		    	  String informationTitle = rs.getString(3);
		    	  String informationType = rs.getString(4);
		    	  String accessorySaveName = rs.getString(5);
		    	  map.put("informationId", informationId);
		    	  map.put("channelId", channelId);
		    	  map.put("informationTitle", informationTitle);
		    	  map.put("filePath", accessorySaveName.substring(0,6));
		    	  map.put("accessorySaveName", accessorySaveName);
		    	  map.put("informationType", informationType);
		    	  list.add(map);
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
	      	if(pst != null){
	      	try
	      	{
	      		pst.close();
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
	 * 查找青岛信息报道总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer informationReportPageCount(String beforeTwoDate,String module){
		int count=0;
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) as count from OA_INFORMATION where " +
	      		"INFORMATIONISSUETIME >(to_date('"+beforeTwoDate+"','yyyy-mm-dd hh24:mi:ss')) and QD_INFORMATION_MODULE='"+module+"'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count = rs.getInt("count");
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
		return count;
	}
	
	/**
	 * 查找青岛信息报道数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> informationReportListData(int curPage,int pageSize,String beforeTwoDate,String module){
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
	        sqlBuf.append("SELECT * FROM (select C.*,rownum rn from(select a.information_id,a.channel_id,a.informationtitle,a.informationtype," +
	        		"a.informationissuetime,a.informationissueorg,a.informationissuer,b.channeltype,b.channelname " +
	        		"from OA_INFORMATION a,OA_INFORMATIONCHANNEL b where a.channel_id=b.channel_id " +
	        		"and a.INFORMATIONISSUETIME >(to_date('"+beforeTwoDate+"','yyyy-mm-dd hh24:mi:ss')) and QD_INFORMATION_MODULE='"+module+"') C where rownum<="+max+") where rn>="+min);
	      ResultSet rs = stmt.executeQuery(sqlBuf.toString());
	      while (rs.next()) {
	    	  Map<String, Object> map = new HashMap<String, Object>();
	    	  String informationId = rs.getString(1);
	    	  String channelId = rs.getString(2);
	    	  String informationTitle = rs.getString(3);
	    	  String informationtype = rs.getString(4);
	    	  String informationIssueTime = rs.getString(5);
	    	  String informationissueorg = rs.getString(6);
	    	  String informationissuer = rs.getString(7);
	    	  String channeltype = rs.getString(8);
	    	  String channelname = rs.getString(9);
	    	  map.put("informationId", informationId);
	    	  map.put("channelId", channelId);
	    	  map.put("informationTitle", informationTitle);
	    	  map.put("informationtype", informationtype);
	    	  map.put("informationissuer", informationissuer);
	    	  map.put("informationissueorg", informationissueorg);
	    	  map.put("informationIssueTime", informationIssueTime);
	    	  map.put("channeltype", channeltype);
	    	  map.put("channelname", channelname);
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
