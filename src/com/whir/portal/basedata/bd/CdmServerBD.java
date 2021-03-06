package com.whir.portal.basedata.bd;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import net.sf.hibernate.HibernateException;

import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.DataSourceBase;

public class CdmServerBD  extends HibernateBase {
	/**
	 * 获取接收到的CDM数据
	 * 
	 * @param request
	 *         HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public List<Map<String, String>> getCdmServerData() throws HibernateException, SQLException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
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
	        "SELECT ID,XMLSTRUCTURE FROM SAVE_CDM_XML WHERE FLAG=0 and rownum<=500";
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  Map<String,String> map = new HashMap<String,String>();
	    	  map.put("id", rs.getInt("ID")+"");
	    	  map.put("xmlStructure", rs.getString("XMLSTRUCTURE"));
	    	  list.add(map);
	      }
	      conn.commit();
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("getCdmServerData error message:" + e.getMessage());
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
		return list;
	}
	
	
	/**
	 * 更新接收到的CDM数据以防止下次再次解析
	 * 
	 * @param request
	 *         HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public void updateCdmServerData(String ids) throws HibernateException, SQLException {
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
	        "UPDATE SAVE_CDM_XML SET FLAG=1 WHERE ID IN("+ids.substring(0, ids.length()-1)+")";
	      stmt.executeUpdate(sql);
	      conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("updateCdmServerData error message:" + e.getMessage());
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
	}
	
	
	/**
	 * 将接收到的CDM数据解析并保存至CDMINFOR表中
	 * 
	 * @param request
	 *         HttpServletRequest
	 * @throws HibernateException 
	 * @throws SQLException 
	 */
	public void putCdmServerBD(List<String> listXml) throws HibernateException, SQLException {
		begin();
	    Connection conn = null;
	    Statement stmt =  null;
	    ResultSet rs = null;
	    try
	    {
	      conn = this.session.connection();
	      conn.setAutoCommit(false);
	      stmt = conn.createStatement();
	      for(String xmlData: listXml){
	    	  Map<String,String> map = PraseXMLData(xmlData);
	    	  if(map!=null){
          	     putDateToDataBase(map);
              } else {
          	      System.out.println("由于丢包接收的xml数据结构不准确不能准确的解析");
              }
	      }
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("putCdmServerBD error message:" + e.getMessage());
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
	}
	
	
	public void TextToFile(String content){
		  String fileName = "D:/file.xml";    
		  try {    
		        // 打开一个随机访问文件流，按读写方式    
		        RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");    
		        // 文件长度，字节数    
		        long fileLength = randomFile.length();    
		        //将写文件指针移到文件尾。    
		        randomFile.seek(fileLength);    
		        randomFile.writeBytes(content);    
		        randomFile.close();    
	       } catch (IOException e){    
	        e.printStackTrace();    
	       } 
	  }
	
	
	 public Map<String,String> PraseXMLData(String xmlData) throws IOException{
		  String[] strArray = xmlData.split("####");
		  Map<String,String> map = new HashMap<String,String>();
		  //解决org.xml.sax.SAXParseException: The reference to entity "S" must end with the ';' delimiter.报错
		  String xmlStr1 = strArray[1].replaceAll("P&S", "<![CDATA[P&S]]>");
		  //解决White space is required before the encoding pseudo attribute in the XML declaration.报错
		  String xmlStr = xmlStr1.replace("encoding", " encoding");
		  StringReader read = new StringReader(xmlStr.trim());
		  InputSource source = new InputSource(read);
		  SAXBuilder sb = new SAXBuilder();
		  Document doc=null;
		try {
			doc = sb.build(source);
			Element rootElement = doc.getRootElement(); // 获取根节点
			  List<Element> metaList = rootElement.getChildren("META");
	          //获取传输标签
	    	  for (Element element : metaList) {
	              //获取name属性值
	    		  map.put("time", element.getChildText("TIME"));
	    		  map.put("qdm", element.getChildText("QDM"));
	    		  map.put("qrc", element.getChildText("QRC"));
	    		  map.put("ddm", element.getChildText("DDM"));
	    		  map.put("dst", element.getChildText("DST"));
	    		  map.put("msgType", element.getChildText("MSGTYPE"));
	          }
	    	  List<Element> bizList = rootElement.getChildren("BIZ");
	          //获取传输标签
	    	  for (Element element : bizList) {
	              //获取name属性值
	    		  map.put("key", element.getChildText("KEY"));
	    		  map.put("retc", element.getChildText("RETC"));
	    		  map.put("rets", element.getChildText("RETS"));
	    		  map.put("rid", element.getChildText("RID"));
	    		  map.put("mid", element.getChildText("MID"));
	          }
	    	  List<Element> datList = rootElement.getChildren("DAT");
	    	  for (Element element : datList) {
	    		  map.put("sqcnum", element.getChildText("SQCNUM"));
	    		  
	    		  List<Element> flightIdentityList = element.getChildren("FlightIdentity");
	    		  List<Element> stateTimeList = element.getChildren("StateTime");
	    		  List<Element> flightInfoList = element.getChildren("FlightInfo");
	              //获取name属性值
	    		  for (Element flightIdentityElement : flightIdentityList) {
	                  //获取name属性值
	    			  map.put("fuid", flightIdentityElement.getChildText("FUID"));
	    			  map.put("flno", flightIdentityElement.getChildText("FLNO"));
	    			  map.put("carrier", flightIdentityElement.getChildText("Carrier"));
	    			  map.put("adep", flightIdentityElement.getChildText("ADEP"));
	    			  map.put("ades", flightIdentityElement.getChildText("ADES"));
	    			  map.put("stod", flightIdentityElement.getChildText("STOD"));
	    			  map.put("stoa", flightIdentityElement.getChildText("STOA"));
	              }
	    		  for (Element stateTimeElement : stateTimeList) {
	                  //获取name属性值
	    			  map.put("type", stateTimeElement.getChildText("TYPE"));
	    			  map.put("seat", stateTimeElement.getChildText("SEAT"));
	    			  map.put("state", stateTimeElement.getChildText("STATE"));
	    			  map.put("stateTime",stateTimeElement.getChildText("TIME"));
	              }
	    		  for (Element flightInfoElement : flightInfoList) {
	                  //获取name属性值
	    			  map.put("rwy", flightInfoElement.getChildText("RWY"));
	    			  map.put("pbay", flightInfoElement.getChildText("PBAY"));
	    			  map.put("act", flightInfoElement.getChildText("ACT"));
	    			  map.put("atot", flightInfoElement.getChildText("ATOT"));
	    			  map.put("aldt", flightInfoElement.getChildText("ALDT"));
	              }
	    	  }
		} catch (JDOMException e) {
			TextToFile(xmlData+"####"+e.getMessage());
			e.printStackTrace();
		}
		return map;
	  }
	 
	 
	 public boolean putDateToDataBase(Map<String,String> map) throws SQLException{
		  	boolean flag = false;
		    StringBuffer flnoSb = new StringBuffer(); 
	  		flnoSb.append("'").append(map.get("flno")).append("'");
		  	//删除之前接收的同一航班数据以最后一次接收的为准
		  	deleteDoubleInfo(flnoSb.toString());
		  	
		  	DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    PreparedStatement pst = null;
		    try {
				conn = dsb.getDataSource().getConnection();
			    conn.setAutoCommit(false);
			    pst = conn.prepareStatement("INSERT INTO CDMINFOR (ID,SENDTIME,QDM,QRC,DDM,DST,MSGTYPE,INFORKEY,RETC,RETS,RID,MID,SQCNUM,FUID,FLNO" +
				",Carrier,ADEP,ADES,STOD,STOA,STATETYPE,SEAT,STATE,STSTETIME,RWY,PBAY,ACT,ATOT,ALDT) values (SEQ_CDMINFOR.NEXTVAL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		        if(map.get("time")!=null && !"".equals(map.get("time"))){
		        	pst.setString(1, map.get("time").toString());   
		        }
		        if(map.get("qdm")!=null && !"".equals(map.get("qdm"))){
		        	pst.setString(2, map.get("qdm").toString());   
		        } else {
		        	pst.setString(2, "");   
		        }
		        if(map.get("qrc")!=null && !"".equals(map.get("qrc"))){
		        	pst.setString(3, map.get("qrc").toString());   
		        } else {
		        	pst.setString(3, "");   
		        }
		        if(map.get("ddm")!=null && !"".equals(map.get("ddm"))){
		        	pst.setString(4, map.get("ddm").toString());   
		        } else {
		        	pst.setString(4, "");   
		        }
		        if(map.get("dst")!=null && !"".equals(map.get("dst"))){
		        	pst.setString(5, map.get("dst").toString());   
		        } else {
		        	pst.setString(5, "");   
		        }
		        if(map.get("msgType")!=null && !"".equals(map.get("msgType"))){
		        	pst.setString(6, map.get("msgType").toString());   
		        } else {
		        	pst.setString(6, "");   
		        }
		        if(map.get("key")!=null && !"".equals(map.get("key"))){
		        	pst.setString(7, map.get("key").toString());   
		        } else {
		        	pst.setString(7, "");   
		        }
		        if(map.get("retc")!=null && !"".equals(map.get("retc"))){
		        	pst.setString(8, map.get("retc").toString());   
		        } else {
		        	pst.setString(8, "");   
		        }
		        if(map.get("rets")!=null && !"".equals(map.get("rets"))){
		        	pst.setString(9, map.get("rets").toString());   
		        } else {
		        	pst.setString(9, "");   
		        }
		        if(map.get("rid")!=null && !"".equals(map.get("rid"))){
		        	pst.setString(10, map.get("rid").toString());   
		        } else {
		        	pst.setString(10, "");   
		        }
		        if(map.get("mid")!=null && !"".equals(map.get("mid"))){
		        	pst.setString(11, map.get("mid").toString());   
		        } else {
		        	pst.setString(11, "");   
		        }
		        if(map.get("sqcnum")!=null && !"".equals(map.get("sqcnum"))){
		        	pst.setString(12, map.get("sqcnum").toString());   
		        } else {
		        	pst.setString(12, "");   
		        }
		        if(map.get("fuid")!=null && !"".equals(map.get("fuid"))){
		        	pst.setString(13, map.get("fuid").toString());   
		        } else {
		        	pst.setString(13, "");   
		        }
		        if(map.get("flno")!=null && !"".equals(map.get("flno"))){
		        	pst.setString(14, map.get("flno").toString());   
		        }
		        if(map.get("carrier")!=null && !"".equals(map.get("carrier"))){
		        	pst.setString(15, map.get("carrier").toString());   
		        } else {
		        	pst.setString(15, "");   
		        }
		        if(map.get("adep")!=null && !"".equals(map.get("adep"))){
		        	pst.setString(16, map.get("adep").toString());   
		        } else {
		        	pst.setString(16, "");   
		        }
		        if(map.get("ades")!=null && !"".equals(map.get("ades"))){
		        	pst.setString(17, map.get("ades").toString());   
		        } else {
		        	pst.setString(17, "");   
		        }
		        if(map.get("stod")!=null && !"".equals(map.get("stod")) && map.get("stod").contains("T")){
		        	StringBuffer sb = new StringBuffer();
		        	String[] aldtArry = map.get("stod").toString().split("T");
		        	sb.append(aldtArry[0]).append(" ").append(aldtArry[1]);
					try {
						String newtime = formatTimeEight(sb.toString());
						pst.setString(18, newtime); 
					} catch (Exception e) {
						e.printStackTrace();
					}
		        } else {
		        	pst.setString(18, "");   
		        }
		        if(map.get("stoa")!=null && !"".equals(map.get("stoa")) && map.get("stoa").contains("T")){
		        	StringBuffer sb = new StringBuffer();
		        	String[] aldtArry = map.get("stoa").toString().split("T");
		        	sb.append(aldtArry[0]).append(" ").append(aldtArry[1]);
					try {
						String newtime = formatTimeEight(sb.toString());
						pst.setString(19, newtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        } else {
		        	pst.setString(19, "");   
		        }
		        if(map.get("type")!=null && !"".equals(map.get("type"))){
		        	pst.setString(20, map.get("type").toString());   
		        } else {
		        	pst.setString(20, "");   
		        }
		        if(map.get("seat")!=null && !"".equals(map.get("seat"))){
		        	pst.setString(21, map.get("seat").toString());   
		        } else {
		        	pst.setString(21, "");   
		        }
		        if(map.get("state")!=null && !"".equals(map.get("state"))){
		        	pst.setString(22, map.get("state").toString());   
		        } else {
		        	pst.setString(22, "");   
		        }
		        if(map.get("stateTime")!=null && !"".equals(map.get("stateTime"))){
		        	pst.setString(23, map.get("stateTime").toString());   
		        } else {
		        	pst.setString(23, "");   
		        }
		        if(map.get("rwy")!=null && !"".equals(map.get("rwy"))){
		        	pst.setString(24, map.get("rwy").toString());   
		        } else {
		        	pst.setString(24, "");   
		        }
		        if(map.get("pbay")!=null && !"".equals(map.get("pbay"))){
		        	pst.setString(25, map.get("pbay").toString());   
		        } else {
		        	pst.setString(25, "");   
		        }
		        if(map.get("act")!=null && !"".equals(map.get("act"))){
		        	pst.setString(26, map.get("act").toString());   
		        } else {
		        	pst.setString(26, "");   
		        }
		        if(map.get("atot")!=null && !"".equals(map.get("atot")) && map.get("atot").contains("T")){
		        	StringBuffer sb = new StringBuffer();
		        	String[] aldtArry = map.get("atot").toString().split("T");
		        	sb.append(aldtArry[0]).append(" ").append(aldtArry[1]);
					try {
						String newtime = formatTimeEight(sb.toString());
						pst.setString(27, newtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        } else {
		        	pst.setString(27, "");   
		        }
		        if(map.get("aldt")!=null && !"".equals(map.get("aldt")) && map.get("aldt").contains("T")){
		        	StringBuffer sb = new StringBuffer();
		        	String[] aldtArry = map.get("aldt").toString().split("T");
		        	sb.append(aldtArry[0]).append(" ").append(aldtArry[1]);
					try {
						String newtime = formatTimeEight(sb.toString());
						pst.setString(28, newtime);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        } else {
		        	pst.setString(28, "");   
		        }
		        // 把一个SQL命令加入命令列表   
		        pst.addBatch();   
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
	  
	  public static String formatTimeEight(String time) throws Exception {
		  Date d = null;
		  String time1=time.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
		  SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		  d = sd.parse(time1);
		  long rightTime = (long) (d.getTime() + 8 * 60 * 60 * 1000); //把当前得到的时间用date.getTime()的方法写成时间戳的形式，再加上8小时对应的毫秒数
		  String newtime = sd.format(rightTime);
		  StringBuffer sb = new StringBuffer();
		  if(newtime.length()==14){
			 sb.append(newtime.substring(0, 4)).append("-").append(newtime.substring(4, 6)).append("-").append(newtime.substring(6, 8)).append(" ")
			 .append(newtime.substring(8, 10)).append(":").append(newtime.substring(10, 12)).append(":").append(newtime.substring(12, 14));
		   }
		   return sb.toString();
	  }
	  
	  public void deleteDoubleInfo(String flnoSb) throws SQLException{
		  	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		  	Date date= new Date();
		  	String dateStr = format.format(date);
		  	DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    Statement stmt = null;
		    try {
				conn = dsb.getDataSource().getConnection();
			    conn.setAutoCommit(false);
			    stmt = conn.createStatement();
			    String sql = "DELETE FROM CDMINFOR WHERE FLNO IN ("+flnoSb+") AND SENDTIME LIKE '%"+dateStr+"%'";
			    int updateCount = stmt.executeUpdate(sql);
		        conn.commit(); 
		    } catch (Exception e) {
		    	conn.rollback();
		        System.out.println("deleteDoubleInfo error message:" + e.getMessage());
		    }finally{
		    	if(conn != null){
		    	try
		    	{
		    	conn.close();
		    	}catch(Exception e){
		    		
		    	}
		    	}
		    }
	  }
}
