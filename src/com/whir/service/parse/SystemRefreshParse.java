package com.whir.service.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;

import com.whir.common.util.DataSourceBase;
import com.whir.service.common.AbstractParse;

public class SystemRefreshParse extends AbstractParse{
	public SystemRefreshParse(Document doc){
	    super(doc);
	}
	public String setRefreshFlag()
	  {
		String result = "";
		int code = 0;
	    String message = "数据传输成功。";
		Element rootElement = this.doc.getRootElement();
		
        String systemName = "";
        String userName = "";
        String refreshFlag = "";
        
      //获得所有子元素
        List<Element> list = rootElement.getChildren("data");
        for (Element element : list) {
            //获取name属性值
        	systemName=element.getChildText("systemName");
	    	userName=element.getChildText("userName").toUpperCase();
	    	refreshFlag=element.getChildText("refreshFlag");
        }
        System.out.println("系统办理的系统名::"+systemName+"刷新的值::"+refreshFlag);
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    try {
	    	String sql = "";
	    	String id = getFieldId(systemName,userName);
	    	if(null!=id && !"".equals(id)){
	    		sql= "UPDATE REFRESH T SET T.SYSTEMNAME=?,T.USERNAME=?,T.REFRESHFLAG=? WHERE T.ID='"+id+"'";
	    	} else {
	    		sql = "INSERT INTO REFRESH (ID,SYSTEMNAME,USERNAME,REFRESHFLAG) values (SEQ_REFRESH.NEXTVAL,?,?,?)";
	    	}
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
	        pst.setString(1, systemName);  
        	pst.setString(2, userName);   
        	pst.setString(3, refreshFlag);   
		    // 执行批量更新   
			pst.executeQuery();
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    setMessage(code+"", message);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setMessage(-1+"", "数据传输不成功。");
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
      return result;
	 }
	
	public String getFieldId(String systemName,String userName){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String id="";
	    try {
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      String sql ="SELECT T.ID FROM REFRESH T WHERE T.SYSTEMNAME='"+systemName+"' AND T.USERNAME='"+userName+"'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	id = rs.getString(1);
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
	   return id;
	}
}