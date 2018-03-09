package com.whir.portal.module.actionsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.ezoffice.information.channelmanager.bd.ChannelBD;
import com.whir.ezoffice.information.infomanager.bd.InformationBD;
import com.whir.org.manager.bd.ManagerBD;

public class InformationMenuAction extends BaseActionSupport{
	private static final long serialVersionUID = 1L;

	  public String informationMenu() throws Exception
	  {
	    HttpSession session = this.request.getSession(true);
	    String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
	    String userDefine = this.request.getParameter("userDefine") == null ? "0" : this.request.getParameter("userDefine");
	    String channelType = this.request.getParameter("channelType");
	    String userChannelName = this.request.getParameter("userChannelName");
	    String channelId = this.request.getParameter("channelId");
	    String channelName = this.request.getParameter("channelName");
	    String moduleName = this.request.getParameter("moduleName");
	    
	    HttpSession httpSession = this.request.getSession(true);
	    String userId = httpSession.getAttribute("userId").toString();
	    String orgId = httpSession.getAttribute("orgId").toString();
	    String orgIdString = httpSession.getAttribute("orgIdString").toString();
	    ChannelBD channelBD = new ChannelBD();
	    ManagerBD managerBD = new ManagerBD();
	    String rightWhere = ""; String scopeWhere = "";
	    String po = "po1.";

	    if ((channelType.equals("0")) || (userDefine.equals("1")))
	      rightWhere = managerBD.getRightFinalWhere(userId, orgId, "01*03*03", po + "createdOrg", po + "createdEmp");
	    else {
	      rightWhere = managerBD.getRightFinalWhere(userId, orgId, "01*01*02", po + "createdOrg", po + "createdEmp");
	    }

	    scopeWhere = managerBD.getScopeFinalWhere(userId, orgId, orgIdString, po + "channelReader", po + "channelReaderOrg", po + "channelReaderGroup");

	    String tempChannelType = channelType;
	    if (userDefine.equals("1")) {
	      tempChannelType = channelType + "  and " + po + "userDefine=1  ";
	    }
	    scopeWhere = scopeWhere + " or " + managerBD.getScopeFinalWhere(userId, orgId, orgIdString, new StringBuilder(String.valueOf(po)).append("channelManager").toString(), new StringBuilder(String.valueOf(po)).append("channelManagerOrg").toString(), new StringBuilder(String.valueOf(po)).append("channelManagerGroup").toString());
	    
	    System.out.print("rightWhere"+rightWhere);
	    System.out.print("scopeWhere"+scopeWhere);
	    
		try {
			List<Map<String, Object>> listMap = getChannelMenu(channelId, domainId, channelType);
			this.request.setAttribute("channelList", listMap);
		    if (managerBD.hasRight(userId, "01*02*01"))
		      this.request.setAttribute("infoMana", "1");
		    else {
		      this.request.setAttribute("infoMana", "0");
		    }
		    List list = channelBD.getUserViewCh(userId, orgId, channelType, userDefine, domainId);
		    StringBuffer canReadChannel = new StringBuffer();
		    Object[] obj = (Object[])null;
		    if ((list != null) && (list.size() > 0) && (list.get(0) != null)) {
		      for (int i = 0; i < list.size(); i++) {
		        obj = (Object[])list.get(i);
		        canReadChannel.append("$").append(obj[0]).append("$");
		      }
		    }
		    list = channelBD.getUserManageList(userId, orgId, orgIdString, channelType, userDefine, domainId);
		    if ((list != null) && (list.size() > 0)) {
		      for (int i = 0; i < list.size(); i++) {
		        canReadChannel.append("$").append(list.get(i).toString()).append("$");
		      }
		    }
		    this.request.setAttribute("canReadChannel", canReadChannel.toString());
		    this.request.setAttribute("canIssue", "1");
		    this.request.setAttribute("channelType", channelType);
		    this.request.setAttribute("userChannelName", userChannelName);
		    this.request.setAttribute("channelId", channelId);
		    this.request.setAttribute("channelName", channelName);
		    this.request.setAttribute("moduleName", moduleName);
		    String addTemplate = "0";
		    if (managerBD.hasRight(userId, "01*02")) {
		      addTemplate = "1";
		    }
		    this.request.setAttribute("addTemplate", addTemplate);
		    this.request.setAttribute("userIssueInfoCount", new InformationBD()
		      .getUserIssueInfoCount(httpSession.getAttribute("userId").toString()));
		    
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return "informationMenu";
	  }
	  
	  
	  public List<Map<String, Object>> getChannelMenu(String channelId, String domainId, String channelType) throws SQLException{
		    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    ResultSet rs = null;
		    PreparedStatement pstmt = null;
		    try {  
		    	conn = dsb.getDataSource().getConnection();
		        conn.setAutoCommit(false);
		        String sql = "SELECT DISTINCT po1.CHANNEL_ID,po1.CHANNELNAME,po1.CHANNELLEVEL,po1.CHANNELPARENTID,po1.CHANNELSHOWTYPE," +
		        		"po1.CHANNELIDSTRING FROM OA_INFORMATIONCHANNEL po1 WHERE po1.DOMAIN_ID="+domainId+
		        		" AND po1.CHANNELTYPE="+channelType+" start with po1.CHANNEL_ID = '"+channelId+"' connect by prior po1.CHANNEL_ID = po1.CHANNELPARENTID ORDER BY po1.CHANNELIDSTRING";
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	Map<String,Object> map = new HashMap<String,Object>();
		        	map.put("channelId", rs.getString("CHANNEL_ID"));
		        	map.put("channelName", rs.getString("CHANNELNAME"));
		        	map.put("channelLevel", rs.getString("CHANNELLEVEL"));
		        	map.put("channelParentId", rs.getString("CHANNELPARENTID"));
		        	map.put("channelShowType", rs.getString("CHANNELSHOWTYPE"));
		        	map.put("channelIdString", rs.getString("CHANNELIDSTRING"));
		        	list.add(map);
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
		    	if(pstmt != null){
		    	try
		    	{
		    	pstmt.close();
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
	  
	  
	  public List<Map<String, Object>> getChannelMenuInfo(String channelName) throws SQLException{
		    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    ResultSet rs = null;
		    PreparedStatement pstmt = null;
		    try {  
		    	conn = dsb.getDataSource().getConnection();
		        conn.setAutoCommit(false);
		        String sql = "select po2.CHANNEL_ID,po2.CHANNELNAME  from OA_INFORMATIONCHANNEL po1, OA_INFORMATIONCHANNEL po2 where po1.CHANNELNAME='"+channelName+"'  and po2.channelparentid=po1.CHANNEL_ID and po2.channellevel='2' and po2.DOMAIN_ID=0 order by po2.CHANNELSORT";
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		        	Map<String,Object> map = new HashMap<String,Object>();
		        	int count = getInfrmationCount(rs.getString("CHANNEL_ID"));
		        	map.put("channelId", rs.getString("CHANNEL_ID"));
		        	map.put("channelName", rs.getString("CHANNELNAME"));
		        	map.put("count", count+"");
		        	System.out.println("count:" + count);
		        	list.add(map);
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
		    	if(pstmt != null){
		    	try
		    	{
		    	pstmt.close();
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
	  
	  public int getInfrmationCount(String channelId) throws SQLException{
		  	int count =0;
		    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	        String nowDate1 = format.format(new Date());
		    DataSourceBase dsb = new DataSourceBase();
		    Connection conn = null;
		    ResultSet rs = null;
		    PreparedStatement pstmt = null;
		    try {  
		    	conn = dsb.getDataSource().getConnection();
		        conn.setAutoCommit(false);
		        String sql = "select count(*) as count from oa_information a where a.channel_id='"+channelId+"'" +
		        		"and a.INFORMATIONISSUETIME = to_date('"+nowDate1+"','yyyy-MM-dd')";
		        System.out.println("sql:" + sql);
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
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
		    	if(pstmt != null){
		    	try
		    	{
		    	pstmt.close();
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
