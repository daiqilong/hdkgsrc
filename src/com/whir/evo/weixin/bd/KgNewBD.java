package com.whir.evo.weixin.bd;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class KgNewBD extends HibernateBase {
	
    public Integer getKgNewRecordCount(String queryTitle,String channelId) throws ParseException, IOException{
    	int recordCount = 0;
    	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String sql = "select count(*) from oa_information where channel_id='"+channelId+"'";
		    if(!"".equals(queryTitle) && queryTitle!=null){
		    	sql+=" and informationtitle like '%"+queryTitle+"%'";
		    }
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	recordCount = Integer.parseInt(rs.getString(1));
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
	  return recordCount;
  }
    
    
    /**
	 * 查找空管新闻数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> getKgNewInfoList(String queryTitle,String channelId,int currentPage,int pageSize) throws HibernateException, SQLException {
		int max = currentPage*pageSize;
		int min = (currentPage-1)*pageSize+1;
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
		    String sql = "SELECT * FROM (select A.*,rownum rn from " +
		    		"(select information_id,channel_id,informationtitle,informationtype,informationissuetime " +
		    		"from oa_information where channel_id='"+channelId+"' ";
		    if(!"".equals(queryTitle) && queryTitle!=null){
		    	sql+=" and informationtitle like '%"+queryTitle+"%'";
		    }
		    sql+=" order by informationissuetime desc ) A where rownum<="+max+") where rn>="+min;
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	  Map<String, Object> map = new HashMap<String, Object>();
		    	  String informationId = rs.getString(1);
//		    	  String channelIds = rs.getString(2);
		    	  String informationTitle = rs.getString(3);
		    	  String informationType = rs.getString(4);
		    	  String informationIssueTime = rs.getString(5);
		    	  map.put("informationId", informationId);
		    	  map.put("channelId", channelId);
		    	  map.put("informationTitle", informationTitle);
		    	  map.put("informationIssueTime", informationIssueTime);
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
	 * 查找空管新闻图片数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public List<Map<String, Object>> getPhotoImage(String channelId) throws HibernateException, SQLException {
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
		    		"select b.information_id,b.channel_id,b.informationtitle,b.informationtype,c.accessorysavename from oa_information b, OA_INFORMATIONACCESSORY c where b.channel_id='"+channelId+"' " +
		    		"and b.information_id=c.information_id order by informationissuetime desc) A where rownum<=5) where rn>=1";
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	  Map<String, Object> map = new HashMap<String, Object>();
		    	  String informationId = rs.getString(1);
//		    	  String channelId = rs.getString(2);
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
}
