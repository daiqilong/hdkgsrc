package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;

public class PeopleStatusBD extends HibernateBase
{
  public String getStatus(String empId, String domainId)
    throws HibernateException, SQLException
  {
    String result = "";
    String statusId = "";
    begin();
    Connection conn = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    try
    {
      conn = this.session.connection();
      conn.setAutoCommit(false);
      stmt = conn.createStatement();

      String sql = 
        "select a.status_id from OA_STATUS_DETAIL a where a.CREATEDATE in (select max(b.createdate) from OA_STATUS_DETAIL b where b.createempid='" + empId + "')";
      rs = stmt.executeQuery(sql);
      while (rs.next()) {
        statusId = rs.getString(1);
      }

      String sql1 = 
        "select status_name from OA_EMPLOYEE_STATUS where status_id='" + statusId + "'";
      rs1 = stmt.executeQuery(sql1);
      while (rs1.next())
        result = rs1.getString(1);
    }
    catch (Exception e) {
      conn.rollback();
      System.out.println("error message:" + e.getMessage());

      if (rs != null)
        try {
          rs.close();
        }
        catch (Exception localException1) {
        }
      if (stmt != null)
        try
        {
          stmt.close();
        }
        catch (Exception localException2)
        {
        }
      if (conn != null)
        try
        {
          conn.close();
        }
        catch (Exception localException3)
        {
        }
    }
    finally
    {
      if (rs != null)
        try {
          rs.close();
        }
        catch (Exception localException4) {
        }
      if (stmt != null)
        try
        {
          stmt.close();
        }
        catch (Exception localException5)
        {
        }
      if (conn != null)
        try
        {
          conn.close();
        }
        catch (Exception localException6)
        {
        }
    }
    return result;
  }
}