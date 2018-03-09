package com.whir.portal.basedata.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class MonitorServerBD  extends HibernateBase {

	/**
	 * 更新各系统链接状态
	 * 
	 * @param request
	 *         HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public void putErrorToDataBase(String moudle,String reason) throws HibernateException, SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate1 = format.format(new Date());
        String keyId = getKeyId(moudle);
	  	String style = getPortStyle(moudle);
	  	String xtmk="";
	    //1:表示红色，2：表示黄色，3:正常
	  	String yxzt="1";
	    //1:断开链接，2:开启链接;style为1：属于A类，style为2：属于B类
	  	String ljzt="1";
	  	if("1".equals(style)){
	  		ljzt="2";
	  	} else {
	  		ljzt="1";
	  	}
	  	if("1".equals(moudle)){
	  		xtmk="OA系统-待办公文接口";
	  	} else if("2".equals(moudle)){
	  		xtmk="OA系统-待阅公文接口";
	  	} else if("3".equals(moudle)){
	  		xtmk="OA系统-行政事务接口";
	  	} else if("4".equals(moudle)){
	  		xtmk="双阳-采编发系统接口";
	  	} else if("5".equals(moudle)){
	  		xtmk="OA系统-委托事项接口";
	  	} else if("6".equals(moudle)){
	  		xtmk="资产系统-固定资产申请待办接口";
	  	} else if("7".equals(moudle)){
	  		xtmk="资产系统-固定资产申购待办接口";
	  	} else if("8".equals(moudle)){
	  		xtmk="资产系统-固定资产变动待办接口";
	  	} else if("9".equals(moudle)){
	  		xtmk="网报系统-久其网报待办接口";
	  	} else if("10".equals(moudle)){
	  		xtmk="安管系统-安管系统待办接口";
	  	} else if("11".equals(moudle)){
	  		xtmk="运维系统-运维系统待办接口";
	  	} else if("12".equals(moudle)){
	  		xtmk="培训系统-培训管理待办接口";
	  	} else if("13".equals(moudle)){
	  		xtmk="合同系统-合同待办接口";
	  	} else if("14".equals(moudle)){
	  		xtmk="基建系统-基建待办接口";
	  	} else if("15".equals(moudle)){
	  		xtmk="OA系统-通知公告接口";
	  	} else if("16".equals(moudle)){
	  		xtmk="安管系统-通知公告接口";
	  	} else if("17".equals(moudle)){
	  		xtmk="运维系统-通知公告接口";
	  	} else if("18".equals(moudle)){
	  		xtmk="培训系统-通知公告接口";
	  	} else if("19".equals(moudle)){
	  		xtmk="双阳-督办系统接口";
	  	} 
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    if(!"".equals(keyId)){
		    	pst = conn.prepareStatement("UPDATE whir$mhxtyxjc SET whir$mhxtyxjc_f3545=?,whir$mhxtyxjc_f3546=?,whir$mhxtyxjc_f3547=?,whir$mhxtyxjc_f3548=?,whir$mhxtyxjc_f3550=?,whir$mhxtyxjc_f3551=?" +
				" WHERE whir$mhxtyxjc_id='"+keyId+"'");
		    } else {
		    	pst = conn.prepareStatement("INSERT INTO whir$mhxtyxjc (whir$mhxtyxjc_id,whir$mhxtyxjc_f3545,whir$mhxtyxjc_f3546,whir$mhxtyxjc_f3547,whir$mhxtyxjc_f3548,whir$mhxtyxjc_f3550,whir$mhxtyxjc_f3551" +
				") values (SEQ_whir$mhxtyxjc.NEXTVAL,?,?,?,?,?,?)");
		    }
		    pst.setString(1, xtmk); 
	        pst.setString(2, yxzt);   
	        pst.setString(3, reason); 
	        pst.setString(4, ljzt);
	        pst.setString(5, moudle); 
	        pst.setString(6, nowDate1);
	        // 把一个SQL命令加入命令列表   
	        pst.addBatch();   
		    // 执行批量更新   
			 pst.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
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
	}
	
	public String getKeyId(String moudle) throws SQLException{
		String keyId="";
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String sql = "Select whir$mhxtyxjc_id FROM whir$mhxtyxjc WHERE whir$mhxtyxjc_f3550 ='"+moudle+"'";
		    rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	keyId = rs.getString(1);
	      	}
	        conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	    }finally{
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
	    return keyId;
  }
	
	public String getPortStyle(String moudle) throws SQLException{
		String portStyle="1";
	  	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String sql = "select whir$xtjkszb_f3554 FROM whir$xtjkszb WHERE whir$xtjkszb_f3553 ='"+moudle+"'";
		    rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	    	  portStyle = rs.getString(1);
	      	}
	        conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	    }finally{
	    	if(conn != null){
	    	try
	    	{
	    	conn.close();
	    	}catch(Exception e){
	    		
	    	}
	    	}
	    }
	    return portStyle;
  }
}
