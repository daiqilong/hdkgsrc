package com.whir.portal.basedata.bd;

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

import net.sf.hibernate.HibernateException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.whir.common.hibernate.HibernateBase;

public class ConferenceNoticeBD  extends HibernateBase {
	public static FTPClient ftpClient =new FTPClient();
	public static String encoding =System.getProperty("file.encoding");
	/**
	 * 查找会议通知数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Map<String, String>> conferenceNoticeData(String userId) throws HibernateException, SQLException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate1 = format.format(new Date());
		List<Map<String, String>> list2 = new ArrayList<Map<String,String>>();
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    Statement stmt1 =  null;
	    ResultSet rs1 = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      stmt1 = conn.createStatement();
	      
	      String sql = 
	        "SELECT DISTINCT T.WHIR$T3037_ID,T.WHIR$T3037_F3203,T.WHIR$T3037_F3204,T.WHIR$T3037_F3206," +
	        "T.WHIR$T3037_F3207,A.WORKMAINLINKFILE " +
	        "FROM EZOFFICE.WF_WORK A,  EZOFFICE.WHIR$T3037 T WHERE A.WF_CUREMPLOYEE_ID='"+userId+"' AND " +
	        "A.WORKFILETYPE='会议通知流程' AND T.WHIR$T3037_ID=A.WORKRECORD_ID AND " +
	        "SUBSTR(T.WHIR$T3037_F3206,0,10) = '"+nowDate1+"'";
	      String sql1 = 
		        "SELECT DISTINCT T.WHIR$T3037_ID,T.WHIR$T3037_F3203,T.WHIR$T3037_F3204,T.WHIR$T3037_F3206," +
		        "T.WHIR$T3037_F3207,A.WORKMAINLINKFILE " +
		        "FROM EZOFFICE.WF_WORK A,  EZOFFICE.WHIR$T3037 T WHERE A.WF_CUREMPLOYEE_ID='"+userId+"' AND " +
		        "A.WORKFILETYPE='会议通知流程' AND T.WHIR$T3037_ID=A.WORKRECORD_ID " +
		        "ORDER BY T.WHIR$T3037_F3206 DESC";
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  Map<String,String> map = new HashMap<String,String>();
	    	  map.put("title", rs.getString("whir$t3037_f3204"));
	    	  map.put("meetingTime", rs.getString("whir$t3037_f3206"));
	    	  map.put("meetingAddress", rs.getString("whir$t3037_f3207"));
	    	  list2.add(map);
	      }
	      if(list2.isEmpty() || list2.size()==0){
	    	  rs1 = stmt1.executeQuery(sql1);
	    	  while (rs1.next()) {
		    	  Map<String,String> map = new HashMap<String,String>();
		    	  map.put("title", rs1.getString("whir$t3037_f3204"));
		    	  map.put("meetingTime", rs1.getString("whir$t3037_f3206"));
		    	  map.put("meetingAddress", rs1.getString("whir$t3037_f3207"));
		    	  list2.add(map);
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
	    	if(rs1 != null){
		    	try{
		    		rs1.close();
		    	}catch(Exception e){
		    	}
		    	}
		    	if(stmt1 != null){
		    	try
		    	{
		    		stmt1.close();
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
		return list2;
	}
	
	
	/**
	 * 从邮局系统中查找未读邮件个数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public int getExternalMailCount(String userAccounts){
		int count=0;
		String host = "10.12.1.27";
		int port = 21;
		String file1=userAccounts.substring(0, 1);
		String file2=userAccounts.substring(0, 2);
		String username = "portal";
		String password = "portal2016";
		String directory = file1+"/"+file2+"/"+userAccounts+"/Maildir/cur/";
		int reply;
		FTPFile[] list = null;
		try{
		   FTPClientConfig config=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
		   ftpClient.configure(config);
		   ftpClient.setControlEncoding(encoding);//
		   ftpClient.connect(host,port);//建立连接
		   ftpClient.login(username, password);//登录
		   ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);//编码格式
		   reply = ftpClient.getReplyCode();//答应码
		   //验证是否登录成功
		   if (!FTPReply.isPositiveCompletion(reply)) {
	            ftpClient.disconnect();
	            System.err.println("FTP server refused connection.");
	           
	       }else {
	    	   list = ftpClient.listFiles(directory);
	    	   for (int i = 0; i < list.length; i++) {
    		   if (list[i].isFile()) {
    			 if(!list[i].getName().contains("2,S")){
    				 count++;
    			 }  
    		   }
    		 }
	       }
		 }catch(Exception e){
		   e.printStackTrace();
		 }
		return count;
	}
	
	/**
	 * 查找未读邮件个数
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public int getMailCount(String userId) throws HibernateException, SQLException {
		int count=0;
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      String sql = 
	        "select count(*) as count from  (select distinct t.mail_id  " +
	        "from oa_mailinterior t,oa_mail_user m where t.mailtoid_c like '%$"+userId+"$%' and " +
	        		"t.mail_id=m.mail_id and m.notread='1' " +
	        "UNION select distinct t.mail_id from oa_mailinterior t,oa_mail_user m " +
	        "where t.mailccid_c like '%$"+userId+"$%' and t.mail_id=m.mail_id and m.notread='1')";
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  count = Integer.parseInt(rs.getString("count"));
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
		return count;
	}
	
}
