package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;

public class QuestionnaireBD extends HibernateBase {
	
	/**
	 * 问卷调查总记录数
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer questPageCount(){
		int count=0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	  	Date date= new Date();
	  	String dateStr = format.format(date);
	    try {
			begin();
		} catch (HibernateException e1) {
			e1.printStackTrace();
		}
	    try
	    {
	      StringBuffer sqlBuf = new StringBuffer();
	      sqlBuf.append("select count(*) as count  from oa_questionnaire where status='1' and domain_Id='0' and to_char(startdate, 'yyyy-MM-dd')<='"+dateStr+"' and  to_char(enddate, 'yyyy-MM-dd')>='"+dateStr+"'");
	      System.out.println("sqlBuf.toString():::"+sqlBuf.toString());
	      Connection conn = this.session.connection();
	      Statement stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sqlBuf.toString());
	      while (rs.next()) {
	    	  count = rs.getInt("count");
	      }
	      System.out.println("count:::"+count);
	      rs.close();
	      stmt.close();
	      conn.close();
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
		return count;
	}
	
	/**
	 * 问卷调查数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String, Object>> questListData(String domainId,int curPage,int pageSize){
		int max = curPage*pageSize;
		int min = (curPage-1)*pageSize+1;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		int count=0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	  	Date date= new Date();
	  	String dateStr = format.format(date);
	    try {
			begin();
		} catch (HibernateException e1) {
			e1.printStackTrace();
		}
		try {
			StringBuffer sqlBuf = new StringBuffer();
			sqlBuf.append("select questionnaireid,title from oa_questionnaire where status='1' and domain_Id='0' and to_char(startdate, 'yyyy-MM-dd')<='"+dateStr+"' and  to_char(enddate, 'yyyy-MM-dd')>='"+dateStr+"'");
	      
			Connection conn = this.session.connection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sqlBuf.toString());
			while (rs.next()) {
	    	  Map<String, Object> map = new HashMap<String, Object>();
	    	  String questionnaireid = rs.getString(1);
	    	  String title = rs.getString(2);
	    	  map.put("questionnaireid", questionnaireid);
	    	  map.put("title", title);
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
