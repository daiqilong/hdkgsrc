package com.whir.rd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.whir.common.util.DataSourceBase;

/***
 * 工具类
 * @author Administrator
 *
 */

public class WatchArrangeUtils {
	
	//获得员工RIGHTSCOPETYPE
	public Object[] getScopeType(String userId,String rightCode){
		Object[] obj = new Object[2];
		obj[0] = "-1";
		obj[1] = "";
		String sql ="select s.rightscopetype,s.rightscopescope from org_right r,org_rightscope s where r.RIGHT_ID = s.RIGHT_ID  and r.rightcode ='"+rightCode+"' and s.emp_id = '"+userId+"'";
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
	    	while(rs.next()){
	    		
	    		obj[0] = rs.getString(1);
	    		obj[1] = rs.getString(2);
	    	}
//	    	System.out.println("scopeType====:"+obj[1]+"----"+obj[0]);
		}catch (SQLException ex) {
			ex.printStackTrace();
			try{
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null){
		        	conn.close();
		        }
		     }catch (SQLException localSQLException1){
		     }
		 }finally{
	    	try{
	    		if (stmt != null){
	    			stmt.close();
	    			}
	    		if (conn != null){
	    			conn.close();
	    		}
	    	}catch (SQLException localSQLException2) {
	    	}
		 }
		 return obj;
	}
	
	//获取单行数据
	public Object[] getArray(String sql) throws IOException{
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
		Object[] obj=null;
	    try {
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sql);
	      ResultSetMetaData rsmd = rs.getMetaData();
	      int columnNum = rsmd.getColumnCount();
	      if (rs.next()) {
	         obj = new Object[columnNum];
	        for (int i = 0; i < columnNum; i++) {
	        	obj[i] = rs.getObject(i + 1);
	        	if(obj[i] instanceof Clob){
	        		Clob clob = (Clob)obj[i];
	        		obj[i] = ClobToString(clob);
	        	}
	        }
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
	      catch (SQLException localSQLException1){
	      }
	    }
	    finally
	    {
	      try
	      {
	        if (stmt != null) {
	          stmt.close();
	        }
	        if (conn != null)
	          conn.close();
	      }
	      catch (SQLException localSQLException2) {
	      }
	    }
	    return obj;
	  }
	
	//java.sql.Clob类型转换成String类型
	public String ClobToString(Clob clob) throws SQLException, IOException {
		String reString = "";
		Reader is = clob.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出付值给StringBuffer由StringBuffer转成STRING
		sb.append(s);
		s = br.readLine();
		}
		reString = sb.toString();
		return reString;
	}
	
	//执行sql语句

	public void executeSql(String sql){

				DataSourceBase dsb = new DataSourceBase();
				Connection conn = null;
				Statement stmt = null;
				 
				try{
					conn = dsb.getDataSource().getConnection();
					stmt = conn.createStatement();
					 stmt.executeUpdate(sql);				

					
				}catch(SQLException e1){
					e1.printStackTrace();
				}finally {
					try {

						if (stmt != null) {
							stmt.close();
						}
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException ex1) {
					}
				}



	}
	
	
	//执行prepareStatement sql语句

	public void executePrepSql(String sql){

				DataSourceBase dsb = new DataSourceBase();
				Connection conn = null;
				PreparedStatement prep=null;
				 
				try{
					conn = dsb.getDataSource().getConnection();
					prep=conn.prepareStatement(sql);
					prep.executeUpdate();			

				}catch(SQLException e1){
					e1.printStackTrace();
				}finally {
					try {

						if (prep != null) {
							prep.close();
						}
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException ex1) {
					}
				}



	}
	
	//批量执行sql语句。
	public void executeBatchSql(List sqlList){

		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		Statement stmt = null;
		//PreparedStatement prep=null; 
		 
		try{
			conn = dsb.getDataSource().getConnection();
			//prep=conn.prepareStatement(sql);
			//prep.executeUpdate();


			stmt = conn.createStatement();
			 //stmt.executeUpdate(sql);	
			for(int i=0;i<sqlList.size();i++){
				String sql=(String)sqlList.get(i);
				stmt.addBatch(sql);
				//if(sqlList.size()<5000){
					//System.out.println("total=="+sqlList.size()+",cur==="+i+",sql==========="+sql); 
				//}
				
			}
			
			if(sqlList!=null && sqlList.size()>0){
				stmt.executeBatch();
				
			}
			

			
		}catch(SQLException e1){
			e1.printStackTrace();
		}finally {
			try {

				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex1) {
			}
		}

	}
	
	
	//根据查询sql获取List数据，list内部是Object[]
	public List getList(String sql){
		System.out.println("开始获取数据。。。。");
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    List resultList = new ArrayList();
	    try {
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = stmt.executeQuery(sql);
	      ResultSetMetaData rsmd = rs.getMetaData();
	      int columnNum = rsmd.getColumnCount();
	      while (rs.next()) {
	        Object[] obj = new Object[columnNum];
	        for (int i = 0; i < columnNum; i++) {
	          obj[i] = rs.getObject(i + 1);
	        }
	        resultList.add(obj);
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
	      catch (SQLException localSQLException1){
	      }
	    }
	    finally
	    {
	      try
	      {
	        if (stmt != null) {
	          stmt.close();
	        }
	        if (conn != null)
	          conn.close();
	      }
	      catch (SQLException localSQLException2) {
	    	  System.out.println("获取出错啦。。");
	    	  localSQLException2.printStackTrace();
	      }
	    }
	    return resultList;
	  }
	
	
	//根据查询sql获取List数据，list内部是Object对象
	public List getList2(String sql){

		 DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    List resultList = new ArrayList();
		    try {
		      conn = dsb.getDataSource().getConnection();
		      stmt = conn.createStatement();
		      ResultSet rs = stmt.executeQuery(sql);
		      while (rs.next()) {
		        resultList.add(rs.getObject(1));
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
		      catch (SQLException localSQLException1){
		      }
		    }
		    finally
		    {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2) {
		      }
		    }
		    return resultList;


		}
	

}
