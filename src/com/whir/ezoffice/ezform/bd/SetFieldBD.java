package com.whir.ezoffice.ezform.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.hibernate.HibernateException;
import com.whir.common.hibernate.HibernateBase;

public class SetFieldBD extends HibernateBase {
	/**
	 * 获取编号
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public String getNumber(String bhStyle) throws HibernateException, SQLException {
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowDate = format.format(new Date());
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      if("".equals(bhStyle) || null==bhStyle){
	    	  return result;
	      } else {
	    	  String sql = "";
	    	  sql = "select max(SUBSTR(whir$kgzdhsjb_f3318,8,9)) as bh from whir$kgzdhsjb where whir$kgzdhsjb_f3317='" +bhStyle+
	  	        "' and SUBSTR(whir$kgzdhsjb_f3318,1,6)= '"+nowDate+"'"; 
		      System.out.println("sql----:"+sql);
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  result=rs.getString(1);
		      }
		      System.out.println("result----:"+result);
		      if("".equals(result) || null==result){
		    	  result=nowDate+"-01";
		      } else {
		    	  int number = Integer.parseInt(result);
		    	  int addone = number+1;
		    	  System.out.println("addone----:"+addone);
		    	  if(addone<10){
		    		  result = nowDate+"-0"+addone;
		    	  } else {
		    		  result = nowDate+"-"+addone;
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
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		return result;
	}
	/**
	 * 获取编号
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public String getNumber1(String bhStyle) throws HibernateException, SQLException {
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowDate = format.format(new Date());
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      if("".equals(bhStyle) || null==bhStyle){
	    	  return result;
	      } else {
	    	  String sql = "";
	    	  sql = "select max(SUBSTR(whir$kgzdhsjb_f3542,8,9)) as bh from whir$kgzdhsjb where whir$kgzdhsjb_f3543='" +bhStyle+
	  	        "' and SUBSTR(whir$kgzdhsjb_f3542,1,6)= '"+nowDate+"'"; 
		      System.out.println("sql----:"+sql);
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  result=rs.getString(1);
		      }
		      System.out.println("result----:"+result);
		      if("".equals(result) || null==result){
		    	  result=nowDate+"-01";
		      } else {
		    	  int number = Integer.parseInt(result);
		    	  int addone = number+1;
		    	  System.out.println("addone----:"+addone);
		    	  if(addone<10){
		    		  result = nowDate+"-0"+addone;
		    	  } else {
		    		  result = nowDate+"-"+addone;
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
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		return result;
	}
	
	/**
	 * 获取编号
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public String getNumber2(String bhStyle) throws HibernateException, SQLException {
		String result = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String nowDate = format.format(new Date());
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      if("".equals(bhStyle) || null==bhStyle){
	    	  return result;
	      } else {
	    	  String sql = "";
	    	  sql = "select max(SUBSTR(whir$ldsjxgb_f4151,9)) as bh from whir$ldsjxgb where whir$ldsjxgb_f3699='" +bhStyle+
	  	        "' and SUBSTR(whir$ldsjxgb_f4151,1,8)= '"+nowDate+"'"; 
		      System.out.println("sql----:"+sql);
		      rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		    	  result=rs.getString(1);
		      }
		      System.out.println("result----:"+result);
		      if("".equals(result) || null==result){
		    	  result=nowDate+"1";
		      } else {
		    	  int number = Integer.parseInt(result);
		    	  int addone = number+1;
		    	  System.out.println("addone----:"+addone);
		    	  result = nowDate+addone;
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
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		return result;
	}
	
	/**
	 * 获取重复模块
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public String getRepeatMoudle(String moduleName,String moduleUserId) throws HibernateException, SQLException {
		String result = "";
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
    	  String sql = "";
    	  sql = "select COUNT(*) AS COUNT from whir$set_notice where whir$set_notice_userid='"+moduleUserId+"' and whir$set_notice_module='"+moduleName+"'"; 
	      rs = stmt.executeQuery(sql);
	      while (rs.next())
	        {
	    	  result = rs.getString("COUNT");
	        }
	      System.out.println("result----:"+result);
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
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
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		return result;
	}
	
	/**
	 * 获取重复模块
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public String getRepeatBackLogMoudle(String moduleName,String moduleUserId) throws HibernateException, SQLException {
		String result = "";
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
    	  String sql = "";
    	  sql = "select COUNT(*) AS COUNT from whir$backlog where whir$backlog_userid='"+moduleUserId+"' and whir$backlog_module='"+moduleName+"'"; 
	      rs = stmt.executeQuery(sql);
	      while (rs.next())
	        {
	    	  result = rs.getString("COUNT");
	        }
	      System.out.println("result----:"+result);
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
	    	if(rs != null){
	    	try{
	    	rs.close();
	    	}catch(Exception e){
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
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
		return result;
	}
}
