package com.whir.evo.weixin.bd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;
import com.whir.component.util.PropertiesUtils;

public class InformationBD extends HibernateBase {
	
	private Connection conn = null;
	private String dbDriver = "";
	private String dbURL = "";
	private String dbUser = "";
	private String dbPassword = "";
	private String targetPath = "";
	private String channelId = "";
	private static Properties props;
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static String URL_PATH = "http://10.12.1.25:81/ezsite/hdkg/upload/";  
	private static final String ACTIONPATH = "kgxw.properties";  
	
	public  List<Map<String,String>> getInformation(){
		if (props == null) {
	        try {
	        	props = getProperties();
	        }
	        catch (Exception e) {
	          e.printStackTrace();
	        }
	      }
	      this.dbDriver = props.getProperty("dbDriver");
	      this.dbURL = props.getProperty("dbURL");
	      this.dbUser = props.getProperty("dbUser");
	      this.dbPassword = props.getProperty("dbPassword");
	      this.targetPath = props.getProperty("targetPath");
	      this.channelId = props.getProperty("channelId");
	      System.out.println("this.dbDriver:::"+this.dbDriver);
	      try {
	          Class.forName("oracle.jdbc.driver.OracleDriver").getInterfaces();
	          if ((this.conn == null) || (this.conn.isClosed()))
	            this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
	          System.out.println("this.conn:::"+this.conn);
	        }
	        catch (Exception ex)
	        {
	          ex.printStackTrace();
	        }
	        
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	    Statement stmt = null;
	    try {
	      String sql = "";
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select a.article_id, a.arti_title,a.arti_shorttitle,a.arti_keyword,a.arti_presentation,a.arti_author, "
	      		+ "b.empname,d.orgname,c.article_content,a.arti_created_time,a.arti_start_time,a.arti_end_time,a.ARTI_SYMBOL_PHOTO_IDS,a.CHANGE_FLAG "
	      		+ "from cms_article a, org_employee b,cms_arti_content c,org_organization d "
	      		+ "WHERE a.arti_creator_id=b.emp_id and a.article_id=c.article_id and a.site_id='220666' and channel_id='237343' and a.site_id=c.site_id and a.arti_creator_orgid=d.org_id and a.CHANGE_FLAG is null and to_char(a.arti_created_time,'yyyy-mm-dd')>'2018-01-01'";
	      System.out.println("sql:::"+sql);
	      rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	Map<String,String> map = new HashMap<String,String>();
	        	oracle.sql.CLOB clob = (oracle.sql.CLOB) rs  
	                    .getClob("article_content");  
	            String informationContent = clob.getSubString(1, (int) clob.length());  
	            String articleId = rs.getString("article_id");
	        	map.put("informationId",articleId);
	        	map.put("informationTitle",rs.getString("arti_title"));
	        	map.put("informationSubtitle",rs.getString("arti_shorttitle"));
	        	map.put("informationKey",rs.getString("arti_keyword"));
	        	map.put("informationSummary",rs.getString("arti_presentation"));
	        	map.put("informationContent",informationContent);
	        	map.put("informationAuthor",rs.getString("arti_author"));
	        	map.put("informationIssuer",rs.getString("empname"));
	        	map.put("informationIssueorg",rs.getString("orgname"));
	        	map.put("informationIssuetime",rs.getString("arti_created_time"));
	        	map.put("validBeginTime",rs.getString("arti_start_time"));
	        	map.put("validendTime",rs.getString("arti_end_time"));
	        	String photoId = rs.getString("ARTI_SYMBOL_PHOTO_IDS");
	        	map.put("photoId",photoId);
	        	map.put("changeFlag",rs.getString("CHANGE_FLAG"));
	        	list.add(map);
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
	   return list;
	}
	
	
	public boolean putDateToInformation(List<Map<String,String>> listMap) throws ParseException{
			System.out.println("获取空管新闻的listMap个数::"+listMap.size());
		    boolean flag = false;
		    StringBuffer informationIdSb = new StringBuffer(); 
		  	for (Map<String,String> map:listMap) {
		  		informationIdSb.append("'").append(map.get("informationId")).append("',");
		  	}
		  	DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    PreparedStatement pst = null;
		    Statement stmt = null;
		    try {
				conn = dsb.getDataSource().getConnection();
			    conn.setAutoCommit(false);
			    stmt = conn.createStatement();
			    String str = "DELETE FROM OA_INFORMATION WHERE third_ID IN ("+informationIdSb.substring(0, informationIdSb.toString().length()-1)+") and THIRD_NAME='空管新闻'";
			    stmt.executeQuery(str);
			    
				pst = conn.prepareStatement("INSERT INTO OA_INFORMATION (INFORMATION_ID,CHANNEL_ID,INFORMATIONTITLE,INFORMATIONSUBTITLE,INFORMATIONKEY," +
	    				"INFORMATIONHEAD,INFORMATIONTYPE,INFORMATIONSUMMARY,INFORMATIONCONTENT,INFORMATIONSTATUS,INFORMATIONAUTHOR,"
	    				+ "INFORMATIONISSUER,INFORMATIONISSUEORG,INFORMATIONREADERNAME,INFORMATIONREADERORG,INFORMATIONISSUETIME,INFORMATIONVALIDTYPE,"
	    				+ "VALIDBEGINTIME,VALIDENDTIME,INFORMATIONISCOMMEND,INFORMATIONKITS,INFORMATIONVERSION,INFORMATIONCOMMONNUM,INFODEPAFLAG,"
	    				+ "INFODEPAFLAG2,FORBIDCOPY,TRANSMITTOEZSITE,DISPLAYTITLE,OTHERCHANNEL,TITLECOLOR,DOSSIERSTATUS,MUSTREAD,ISCONF,DOCUMENTTYPE,"
	    				+ "DOMAIN_ID,ISSUEORGIDSTRING,DISPLAYIMAGE,INFORMATIONISSUEORGID,ORDERCODE,INFORMATIONCANREMARK,ISPUBLIC,THIRD_ID,THIRD_NAME) values "
	    				+ "(hibernate_sequence.nextval,'"+channelId+"',?,?,?,'0','1',?,?,'0',?,?,?,'民航华东空管局,','*19769*',?,'0',?,?,'0','0',"
	    				+ "'1.00','0','0','0','0','0','1',',0,','0','0','0','0','0','0','$8867$','1','8867','1000','1','1',?,'空管新闻')");
				for (Map<String,String> map:listMap) {
				        if(map.get("informationTitle")!=null && !"".equals(map.get("informationTitle"))){
				        	pst.setString(1, map.get("informationTitle").toString());   
				        } else {
				        	pst.setString(1, "");   
				        }
				        if(map.get("informationSubtitle")!=null && !"".equals(map.get("informationSubtitle"))){
				        	pst.setString(2, map.get("informationSubtitle").toString());   
				        } else {
				        	pst.setString(2, "");   
				        }
				        if(map.get("informationKey")!=null && !"".equals(map.get("informationKey"))){
				        	pst.setString(3, map.get("informationKey").toString());   
				        } else {
				        	pst.setString(3, "");   
				        }
				        if(map.get("informationSummary")!=null && !"".equals(map.get("informationSummary"))){
				        	pst.setString(4, map.get("informationSummary").toString());   
				        } else {
				        	pst.setString(4, "");   
				        }
				        if(map.get("informationContent")!=null && !"".equals(map.get("informationContent"))){
				        	StringReader reader = new StringReader(map.get("informationContent").toString());  
				        	pst.setCharacterStream(5, reader, map.get("informationContent").toString().length());     
				        } else {
				        	pst.setCharacterStream(5, null);   
				        }
				        
				        if(map.get("informationAuthor")!=null && !"".equals(map.get("informationAuthor"))){
				        	pst.setString(6, map.get("informationAuthor").toString());   
				        } else {
				        	pst.setString(6, "");   
				        }
				        if(map.get("informationIssuer")!=null && !"".equals(map.get("informationIssuer"))){
				        	pst.setString(7, map.get("informationIssuer").toString());   
				        } else {
				        	pst.setString(7, "");   
				        }
				        if(map.get("informationIssueorg")!=null && !"".equals(map.get("informationIssueorg"))){
				        	pst.setString(8, map.get("informationIssueorg").toString());   
				        } else {
				        	pst.setString(8, "");   
				        }
				        if(map.get("informationIssuetime")!=null && !"".equals(map.get("informationIssuetime"))){
				        	pst.setDate(9, strToDate(map.get("informationIssuetime").toString()));   
				        } else {
				        	pst.setDate(9, null);   
				        }
				        if(map.get("validBeginTime")!=null && !"".equals(map.get("validBeginTime"))){
				        	pst.setDate(10, strToDate(map.get("validBeginTime").toString()));   
				        } else {
				        	pst.setDate(10, null);   
				        }
				        if(map.get("validendTime")!=null && !"".equals(map.get("validendTime"))){
				        	pst.setDate(11, strToDate(map.get("validendTime").toString()));   
				        } else {
				        	pst.setDate(11, null);  
				        }
				        if(map.get("informationId")!=null && !"".equals(map.get("informationId"))){
				        	pst.setString(12, map.get("informationId").toString());   
				        } else {
				        	continue;  
				        }
				        // 把一个SQL命令加入命令列表   
				        pst.addBatch();   
				 }  
			    // 执行批量更新   
				 pst.executeBatch();   
			    // 语句执行完毕，提交本事务   
			    conn.commit(); 
			    flag=true;
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
		  return flag;
	  }
	
	public void updateInformationData(List<Map<String,String>> listMap) throws SQLException, ClassNotFoundException {
		StringBuffer informationIdSb = new StringBuffer(); 
		for (Map<String,String> map:listMap) {
	  		String articleId = map.get("informationId");
	  		informationIdSb.append("'").append(articleId).append("',");
	  	}
	  	Statement stmt = null;
	    try {
    	  Class.forName("oracle.jdbc.driver.OracleDriver").getInterfaces();
    	  if ((this.conn == null) || (this.conn.isClosed()))
    		  this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
	      stmt = conn.createStatement();
	      String sql = 
	        "UPDATE cms_article SET CHANGE_FLAG=3 WHERE article_id IN("+informationIdSb.toString().substring(0, informationIdSb.toString().length()-1)+")";
	      stmt.executeUpdate(sql);
	      conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("updateInformationData error message:" + e.getMessage());
	    }finally{
	    	for (Map<String,String> map:listMap) {
		  		String articleId = map.get("informationId");
		  		String photoId = map.get("photoId");
		  		System.out.println("获取空管新闻的带图片的photoId值::"+photoId);
		  		if(!"".equals(photoId) && photoId!=null){
		  			saveInformationAccessory(articleId,photoId);
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
	}
	
	/**
	 * String类型转换成java.sql.Date类型不能直接进行转换，首先要将String转换成java.util.Date，在转化成java.sql.Date
     * @param 返回java.sql.Date格式的
     * */
    public static java.sql.Date strToDate(String strDate) {
        String str = strDate;
        java.util.Date d = null;
        try {
            d = sdf.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.sql.Date date = new java.sql.Date(d.getTime());
        return date;
    }
    
    
    public  void saveInformationAccessory(String articleId,String photoId) throws SQLException, ClassNotFoundException{
    	 boolean result = false;
    	List<Map<String,String>> list = getInformationAccessory(articleId,photoId);
	    System.out.println("获取图片数据个数list:::"+list.size());	
    	if(!list.isEmpty() && list!=null){
			try {
				result = putDateToInformationAccessory(list);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(result){
			try {
				updateInformationAccessoryData(list);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
    
    
    public  List<Map<String,String>> getInformationAccessory(String articleId,String photoId) throws SQLException, ClassNotFoundException{
    	String[] str = photoId.split(",");
    	StringBuffer photoIdSb = new StringBuffer(); 
	  	for (String id:str) {
	  		photoIdSb.append("'").append(id).append("',");
	  	}
	  	String relevanceId = getRelevanceId(articleId);
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Statement stmt = null;
	    try {
    	  String sql = "";
    	  Class.forName("oracle.jdbc.driver.OracleDriver").getInterfaces();
    	  if ((this.conn == null) || (this.conn.isClosed()))
	            this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
	      stmt = this.conn.createStatement();
	      ResultSet rs = null;
	      sql = "select files_id,files_filename,files_savename,files_path,files_type,files_suffix from cms_files where files_id in ("+photoIdSb.substring(0, photoIdSb.toString().length()-1)+") and change_flag is null";
	      System.out.println("根据pohotoID获取对应的数据:"+sql);
	      rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	Map<String,String> map = new HashMap<String,String>();
	        	map.put("accessoryId",rs.getString("files_id"));
	        	map.put("informationId",relevanceId);
	        	String filesType = rs.getString("files_type");
	        	if("0".equals(filesType)){
	        		map.put("accessoryIsImage","1");
	        	} else {
	        		map.put("accessoryIsImage","0");
	        	}
	        	map.put("filesPath",rs.getString("files_path"));
	        	map.put("accessoryType",rs.getString("files_suffix"));
	        	map.put("accessoryName",rs.getString("files_filename"));
	        	map.put("accessorySaveName",rs.getString("files_savename"));
	        	list.add(map);
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
	   return list;
	}
    
    
    public boolean putDateToInformationAccessory(List<Map<String,String>> listMap) throws ParseException, IOException{
    	boolean flag = false;
    	StringBuffer accessoryIdSb = new StringBuffer(); 
	  	for (Map<String,String> map:listMap) {
	  		accessoryIdSb.append("'").append(map.get("accessoryId")).append("',");
	  	}
    	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pst = null;
	    Statement stmt = null;
	    try {
			conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    String str = "DELETE FROM oa_informationaccessory WHERE third_ID IN ("+accessoryIdSb.substring(0, accessoryIdSb.toString().length()-1)+") and THIRD_NAME='空管新闻'";
		    stmt.executeQuery(str);
		    
			pst = conn.prepareStatement("INSERT INTO oa_informationaccessory (ACCESSORY_ID,INFORMATION_ID,ACCESSORYISIMAGE,ACCESSORYTYPE,ACCESSORYNAME," +
    				"ACCESSORYSAVENAME,DOMAIN_ID,third_id,third_name) values "
    				+ "(hibernate_sequence.nextval,?,?,?,?,?,'0',?,'空管新闻')");
			for (Map<String,String> map:listMap) {
			        if(map.get("informationId")!=null && !"".equals(map.get("informationId"))){
			        	pst.setString(1, map.get("informationId"));   
			        } else {
			        	pst.setString(1, "");   
			        }
			        if(map.get("accessoryIsImage")!=null && !"".equals(map.get("accessoryIsImage"))){
			        	pst.setString(2, map.get("accessoryIsImage").toString());   
			        } else {
			        	pst.setString(2, "");   
			        }
			        if(map.get("accessoryType")!=null && !"".equals(map.get("accessoryType"))){
			        	String accessoryType=map.get("accessoryType").toString();
			        	pst.setString(3, accessoryType);  
			        } else {
			        	pst.setString(3, ""); 
			        }
			        if(map.get("accessoryName")!=null && !"".equals(map.get("accessoryName"))){
			        	pst.setString(4, map.get("accessoryName").toString());   
			        } else {
			        	pst.setString(4, "");   
			        }
			        if(map.get("accessorySaveName")!=null && !"".equals(map.get("accessorySaveName"))){
			        	String accessorySaveName=map.get("accessorySaveName").toString();
			        	pst.setString(5, accessorySaveName);  
			        	uploadInformationAccessory(accessorySaveName,map.get("filesPath").toString());
			        } else {
			        	pst.setString(5, ""); 
			        }
			        if(map.get("accessoryId")!=null && !"".equals(map.get("accessoryId"))){
			        	pst.setString(6, map.get("accessoryId").toString());   
			        } else {
			        	pst.setString(6, "");   
			        }
			        // 把一个SQL命令加入命令列表   
			        pst.addBatch();   
			 }  
		    // 执行批量更新   
			 pst.executeBatch();   
		    // 语句执行完毕，提交本事务   
		    conn.commit(); 
		    flag=true;
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
	  return flag;
  }
    
    public void updateInformationAccessoryData(List<Map<String,String>> listMap) throws SQLException {
		StringBuffer accessoryIdSb = new StringBuffer(); 
	  	for (Map<String,String> map:listMap) {
	  		accessoryIdSb.append("'").append(map.get("accessoryId")).append("',");
	  	}
	  	
	  	Statement stmt = null;
	    try {
    	  Class.forName("oracle.jdbc.driver.OracleDriver").getInterfaces();
    	  if ((this.conn == null) || (this.conn.isClosed()))
    		  this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
	      stmt = conn.createStatement();
	      String sql = 
	        "UPDATE cms_files SET CHANGE_FLAG=3 WHERE files_ID IN("+accessoryIdSb.toString().substring(0, accessoryIdSb.toString().length()-1)+")";
	      stmt.executeUpdate(sql);
	      conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("updateInformationAccessoryData error message:" + e.getMessage());
	    }finally{
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
	}
    
    
    public String getRelevanceId(String articleId) throws SQLException {
    	DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String oaInformationId="";
	    try
	    {
	    	conn = dsb.getDataSource().getConnection();
		    conn.setAutoCommit(false);
		    stmt = conn.createStatement();
		    ResultSet rs = null;
	        String sql = 
	        "select INFORMATION_ID from OA_INFORMATION where third_id='"+articleId+"' and THIRD_NAME='空管新闻'";
	        rs = stmt.executeQuery(sql);
	        while (rs.next()) {
	        	oaInformationId=rs.getString("INFORMATION_ID");
	        }
	        rs.close();
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("getOaInformation error message:" + e.getMessage());
	    }finally{
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
		return oaInformationId;
	}
    
    
    private void uploadInformationAccessory(String accessorySaveName, String path) throws IOException {
    	InputStream inputStream = getInputStream(accessorySaveName,path);
    	byte[] data = new byte[1024];
		int len = 0;
		FileOutputStream fileOutputStream = null;
		File file=new File(targetPath+accessorySaveName.substring(0,6), accessorySaveName);
	    if(!file.exists()){
	        file.getParentFile().mkdirs();
            try {
              file.createNewFile();
             } catch (IOException e) {
               // TODO: handle exception
                e.printStackTrace();
             }
	    } 
		try {
			fileOutputStream = new FileOutputStream(targetPath+accessorySaveName.substring(0,6)+"/"+accessorySaveName);
			System.out.println("targetPath::::"+targetPath+accessorySaveName.substring(0,6)+"/"+accessorySaveName);
			while ((len = inputStream.read(data)) != -1) {
				fileOutputStream.write(data, 0, len);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
    }
    
 // 从服务器获得一个输入流(本例是指从服务器获得一个image输入流)
	public static InputStream getInputStream(String accessorySaveName, String path) {
		InputStream inputStream = null;
		HttpURLConnection httpURLConnection = null;

		try {
			URL url = new URL(URL_PATH+path+"/"+accessorySaveName);
			System.out.println("URL_PATH::::"+URL_PATH+path+"/"+accessorySaveName);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置网络连接超时时间
			httpURLConnection.setConnectTimeout(3000);
			// 设置应用程序要从网络连接读取数据
			httpURLConnection.setDoInput(true);

			httpURLConnection.setRequestMethod("GET");
			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode == 200) {
				// 从服务器返回一个输入流
				inputStream = httpURLConnection.getInputStream();

			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return inputStream;

	}
	
	/**
	 * 读取配置文件
	 * 
	 * @return
	 */
	public static Properties getProperties() {
		Properties props=null;
		if (props == null) {
	        props = new Properties();
	        try {
	        	String path = PropertiesUtils.class.getClassLoader().getResource("").toURI().getPath().substring(1); 
	        	// 把文件读入文件输入流，存入内存中  
	        	FileInputStream fis = new FileInputStream(new File(path + ACTIONPATH));     
	        	//加载文件流的属性     
	        	props.load(fis); 
	        }
	        catch (Exception e) {
	          e.printStackTrace();
	        }
	    }
		return props;
	}
}
