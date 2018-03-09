package com.whir.ezflow.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.whir.common.util.DataSourceBase;

public class EzFlowExtraHandleAction extends EzFlowBaseAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -18706897063876064L;
	
	/**
	 * 获取关联流程的数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String pageAssignment() {
		String id = request.getParameter("id");
		System.out.println("id值："+id);
		
	    Map<String,String> map = getPageAssignment(id);
	    JSONObject jsonObject = JSONObject.fromObject(map);
	    System.out.println("jsonObject值："+jsonObject.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonObject);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	
	public Map<String,String> getPageAssignment(String id){
	    DataSourceBase dsb = new DataSourceBase();
	    Map<String,String> map = new HashMap<String,String>();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select whir$kgzdhsjb_f3317,whir$kgzdhsjb_f3318,whir$kgzdhsjb_f3320,whir$kgzdhsjb_f3329 from wf_work a,whir$kgzdhsjb b " +
	      		"where wf_work_id = '"+id+"' and a.WORKRECORD_ID=b.whir$kgzdhsjb_id";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        	map.put("bhlx", rs.getString(1));
	        	map.put("bhsz", rs.getString(2));
	        	map.put("xtlx", rs.getString(3));
	        	map.put("xxms", rs.getString(4));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null) {
		          conn.close();
		        }
		      }
		      catch (SQLException localSQLException1)
		      {
		      }

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
	   return map;
	}
	
	
	/**
	 * 获取性别和图像
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String getSexAndPhoto() {
		String userAccount = request.getParameter("userAccount");
		System.out.println("userAccount值："+userAccount);
		
	    Map<String,String> map = getSexAndPhoto(userAccount);
	    JSONObject jsonObject = JSONObject.fromObject(map);
	    System.out.println("jsonObject值："+jsonObject.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonObject);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	
	public Map<String,String> getSexAndPhoto(String userAccount){
	    DataSourceBase dsb = new DataSourceBase();
	    Map<String,String> map = new HashMap<String,String>();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select empsex,useraccounts,empidcard from org_employee where useraccounts='"+userAccount+"'";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	        	map.put("empsex", rs.getString(1));
	        	map.put("useraccounts", rs.getString(2));
	        	map.put("empidcard", rs.getString(3));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null) {
		          conn.close();
		        }
		      }
		      catch (SQLException localSQLException1)
		      {
		      }

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
	   return map;
	}
	
	
	/**
	 * 判断重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String judgeRepeat() {
		String dateVal = request.getParameter("dateVal");
		System.out.println("dateVal值："+dateVal);
		
	    String count = getJudgeRepeat(dateVal);
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("count", count);
	    
	    JSONObject jsonObject = JSONObject.fromObject(map);
	    System.out.println("jsonObject值："+jsonObject.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonObject);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * 判断重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String judgeFlightReport() {
		String dateVal = request.getParameter("dateVal");
		System.out.println("dateVal值："+dateVal);
		
	    String count = getJudgeFlightReport(dateVal);
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("count", count);
	    
	    JSONObject jsonObject = JSONObject.fromObject(map);
	    System.out.println("jsonObject值："+jsonObject.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonObject);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * 判断重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String judgeRepeatLshxsytj() {
		String dateVal = request.getParameter("dateVal");
		System.out.println("dateVal值："+dateVal);
		
	    String count = getJudgeRepeatLshxsytj(dateVal);
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("count", count);
	    
	    JSONObject jsonObject = JSONObject.fromObject(map);
	    System.out.println("jsonObject值："+jsonObject.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(jsonObject);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	
	public String getJudgeRepeat(String dateVal){
		String count = "0";
		String value=dateVal.substring(0, 7);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) from whir$hkqbtjb where substr(whir$hkqbtjb_f4119, 0, 7)='"+value+"' and whir$hkqbtjb_workstatus!='-1'";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  count = rs.getString(1);
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
	
	
	public String getJudgeRepeatLshxsytj(String dateVal){
		String count = "0";
		String value=dateVal.substring(0, 7);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) from whir$lshxsytj where substr(whir$lshxsytj_f4112, 0, 7)='"+value+"' and whir$lshxsytj_workstatus!='-1'";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  count = rs.getString(1);
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
	
	public String getJudgeFlightReport(String dateVal){
		String count = "0";
		String value=dateVal.substring(0, 7);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) from whir$fxbgtjb where substr(whir$fxbgtjb_f4142, 0, 7)='"+value+"' and whir$fxbgtjb_workstatus!='-1'";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  count = rs.getString(1);
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
}
