package com.whir.portal.module.actionsupport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpSession;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.codehaus.xfire.client.Client;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.jdom.input.SAXBuilder;

import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.PropertiesUtils;
import com.whir.ezoffice.bpm.bd.BPMProxyBD;
import com.whir.ezoffice.bpm.po.BPMProxyPO;
import com.whir.rd.util.WatchArrangeUtils;

public class WainingInformationAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957611521728273579L;
	private static Logger logger = Logger.getLogger(WainingInformationAction.class.getName());
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 判断设定的通知公告是否重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String judgeRepetition() {
		String module = request.getParameter("module");
		String sort = request.getParameter("sort");
		String userId = request.getParameter("userId");
		
	    int count = getJudgeRepetition(module,sort,userId);
	    System.out.println("-----count-------"+count);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"count\":\"" + count + "\"}");
	    json.append("]");

	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public int getJudgeRepetition(String module,String sort,String userId){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) as count from whir$set_notice where whir$set_notice_userid='"+userId+"' and (whir$set_notice_module='"+count+"' or whir$set_notice_sort='"+sort+"')";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count = rs.getInt("count");
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
	
	
	/**
	 * 判断飞行报告工作统计表是否重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws ParseException 
	 */
	public String judgeRepeatFxbgtjb() throws ParseException {
		String pfdb = request.getParameter("pfdb");
		String cldb = request.getParameter("cldb");
		String rq = request.getParameter("rq");
		
	    int count = getJudgeFxbgtjb(pfdb,cldb,rq);
	    System.out.println("-----count-------"+count);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"count\":\"" + count + "\"}");
	    json.append("]");

	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public int getJudgeFxbgtjb(String pfdb,String cldb,String rq) throws ParseException{
		Calendar dayc1 = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date daystart = df.parse(rq);    
		dayc1.setTime(daystart);   
		dayc1.add(Calendar.MONTH, -1);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
	    String time = format.format(dayc1.getTime());
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) as count from whir$fxbgtjb where whir$fxbgtjb_f4144='"+pfdb+"' and whir$fxbgtjb_f4147='"+cldb+"' and SUBSTR(whir$fxbgtjb_f4142,1,7)='"+time+"'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count = rs.getInt("count");
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
	
	
	/**
	 * 判断华东地区临时航线使用情况统计表是否重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws ParseException 
	 */
	public String judgeRepeatLshxsytj() throws ParseException {
		String hyhb = request.getParameter("hyhb");
		String bnljsyhb = request.getParameter("bnljsyhb");
		String jsjl = request.getParameter("jsjl");
		String bnljjsjl = request.getParameter("bnljjsjl");
		String tbzz = request.getParameter("tbzz");
		String rq = request.getParameter("rq");
		System.out.println("-----hyhb-------"+hyhb);
	    int count = getJudgeFxbgtjb(hyhb,bnljsyhb,jsjl,bnljjsjl,tbzz,rq);
	    System.out.println("-----count-------"+count);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"count\":\"" + count + "\"}");
	    json.append("]");

	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public int getJudgeFxbgtjb(String hyhb,String bnljsyhb,String jsjl,String bnljjsjl,String tbzz,String rq) throws ParseException{
		Calendar dayc1 = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date daystart = df.parse(rq);    
		dayc1.setTime(daystart);   
		dayc1.add(Calendar.MONTH, -1);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
	    String time = format.format(dayc1.getTime());
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) as count from whir$lshxsytj where whir$lshxsytj_f4114='"+hyhb+"' and whir$lshxsytj_f4115='"+bnljsyhb+"' and whir$lshxsytj_f4116='"+jsjl+"' and whir$lshxsytj_f4117='"+bnljjsjl+"' and whir$lshxsytj_f4118='"+tbzz+"' and SUBSTR(whir$lshxsytj_f4112,1,7)='"+time+"'";
	      System.out.println("sql:::"+sql);
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count = rs.getInt("count");
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
	
	
	/**
	 * 判断航空情报业务统计表是否重复
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @throws ParseException 
	 */
	public String judgeRepeatHkqbywtjb() throws ParseException {
		String cxlhx = request.getParameter("cxlhx");
		String dxlhx = request.getParameter("dxlhx");
		String hxtgcl = request.getParameter("hxtgcl");
		String hkzlxd = request.getParameter("hkzlxd");
		String zlsbjcs = request.getParameter("zlsbjcs");
		String zlsbxdsjx = request.getParameter("zlsbxdsjx");
		String rq = request.getParameter("rq");
		
	    int count = getJudgeHkqbywtjb(cxlhx,dxlhx,hxtgcl,hkzlxd,zlsbjcs,zlsbxdsjx,rq);
	    System.out.println("-----count-------"+count);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"count\":\"" + count + "\"}");
	    json.append("]");

	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public int getJudgeHkqbywtjb(String cxlhx,String dxlhx,String hxtgcl,String hkzlxd,String zlsbjcs,String zlsbxdsjx,String rq) throws ParseException{
		Calendar dayc1 = new GregorianCalendar();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date daystart = df.parse(rq);    
		dayc1.setTime(daystart);   
		dayc1.add(Calendar.MONTH, -1);
		SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM");
	    String time = format.format(dayc1.getTime());
	    
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) as count from whir$hkqbtjb whir$hkqbtjb_f4121='"+cxlhx+"' and whir$hkqbtjb_f4124='"+dxlhx+"' and whir$hkqbtjb_f4127='"+hxtgcl+"' and whir$hkqbtjb_f4130='"+hkzlxd+"' and whir$hkqbtjb_f4133='"+zlsbjcs+"' and whir$hkqbtjb_f4139='"+zlsbxdsjx+"' and SUBSTR(whir$hkqbtjb_f4119,1,7)='"+time+"'";
	      System.out.println("sql:::"+sql);
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count = rs.getInt("count");
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
	
	/**
	 * 判断当日新增预警信息是否已经存在
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String checkDate() {
		String datetime = request.getParameter("datetime");
	    int count = getAllWainDate(datetime);
	    System.out.println("-----count-------"+count);
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"count\":\"" + count + "\"}");
	    json.append("]");

	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public void wxPress(){
		HttpSession session = request.getSession();
		/*-------------------从request取值部分-----------------------*/
		// 当前办理人帐号
		String curUserAccount = session.getAttribute("userAccount") + "";
		String curUserId = session.getAttribute("userId") + "";
		String curUserName = session.getAttribute("userName") + "";
		String curOrgId = session.getAttribute("orgId") + "";
		String curOrgName = session.getAttribute("orgName") + "";
		String domainId = "0";
		
		// 流程定义名
		com.whir.evo.weixin.bd.WeiXinBD weixinbd = new  com.whir.evo.weixin.bd.WeiXinBD();
		
		String datetime=request.getParameter("datetime");
		String preenddate=request.getParameter("preenddate");
		String colour=request.getParameter("colour");
		String type=request.getParameter("type");
		String content=request.getParameter("content");
		String state=request.getParameter("state");
		
        Map<String,String> params = new HashMap<String,String>();
        params.put("datetime", datetime);
        params.put("preenddate", preenddate);
        params.put("colour", colour);
        params.put("type", type);
        params.put("content", content);
        params.put("state", state);
        System.out.println("您接收一条"+colour+"预警："+type+content);
        //for(String userId:userIdArray){
	    	//System.out.println("userId:::"+userId);
	    	boolean weixinsuccess = weixinbd.sendMsg("71706","您接收一条"+colour+"预警："+type+content,"",null,null,"yjpress",params);
	        logger.debug("weixinsuccess--dealRemindInCache--->"+weixinsuccess);
	    //}
		logger.debug("结束催办1");
		
	}
	public int getAllWainDate(String datetime){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "SELECT WHIR$WARN_INFOR_DATETIME FROM WHIR$WARN_INFOR where WHIR$WARN_INFOR_DATETIME='"+datetime+"'";
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	count++;
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
	   return count;
	}
	
	
	/**
	 * 统一委托页面展示
	 */
	public String unityAgencyShow() {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		Map<String,String> map =getRecorderList(userId);
		if(map.get("system")!=null && !"".equals(map.get("system"))){
			session.setAttribute("system", map.get("system"));
			session.setAttribute("mandatary", map.get("mandatary"));
			session.setAttribute("state", map.get("state"));
		} else {
			session.setAttribute("system", "0,1,2,3");
			session.setAttribute("mandatary", "");
			session.setAttribute("state", "1");
		}
	    return "unityAgencyShow";
	}
	
	/**
     * 新增自定义模块数据
     * 
     * @return
     * @throws Exception
     */
    public String saveUnityAgency() throws Exception {

        HttpSession session = request.getSession(true);
        Calendar cal = Calendar.getInstance();
		Date date=cal.getTime();
		String startTime = df.format(date);
        String userId = session.getAttribute("userId").toString();
        String userName = session.getAttribute("userName").toString();
        String orgId = session.getAttribute("orgId").toString();
        
		cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)+1);
		Date date1=cal.getTime();
		String endTime = df.format(date1);
		
		String state = request.getParameter("state");
		String mandataryId = request.getParameter("mandataryId");
		String mandataryName = request.getParameter("mandataryName");
		String systemStr = request.getParameter("system");
		String principal = request.getParameter("principal");
        
		String mandatary = mandataryName+";"+mandataryId;
		List sqlList = new ArrayList();
		String sql = "";
		String key = getRecorder(userId);
		if(!"".equals(key)){
			sql = "update  WHIR$ENTRUST set whir$entrust_system='"+systemStr+"',whir$entrust_mandatary='"+mandatary+"'" +
					",whir$entrust_principal='"+principal+"',whir$entrust_state='"+state+"' where WHIR$ENTRUST_id='"+key+"'";
			sqlList.add(sql);
		} else {
			sql = "insert into WHIR$ENTRUST (WHIR$ENTRUST_id,WHIR$ENTRUST_OWNER," +
					"WHIR$ENTRUST_DATE,WHIR$ENTRUST_ORG,WHIR$ENTRUST_WORKSTATUS," +
					"whir$entrust_system,whir$entrust_mandatary,whir$entrust_principal," +
					"whir$entrust_startTime,whir$entrust_endTime,whir$entrust_state) "+
			"values (hibernate_sequence.nextval,'"+userId+"','"+startTime+"','"+orgId+"','100','"+systemStr+"','"+mandatary+"','"+principal+"','"+startTime+"','"+endTime+"','"+state+"')";
			sqlList.add(sql);
		}
		new WatchArrangeUtils().executeBatchSql(sqlList);
		String flag="1";
	    StringBuffer json = new StringBuffer("[");
	    json.append("{\"flag\":\"" + flag + "\"}");
	    json.append("]");

	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
    }
    
    public String getRecorder(String userId){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    String key="";
	    try {
	      StringBuffer sql = new StringBuffer();
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql.append("select WHIR$ENTRUST_id from WHIR$ENTRUST A  WHERE A.WHIR$ENTRUST_OWNER='"+userId+"' ");
	      System.out.println(sql.toString());
	      rs = stmt.executeQuery(sql.toString());
	        while (rs.next())
	        {
	        	key = rs.getString(1);
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
	   return key;
	}
    
    public Map<String,String> getRecorderList(String userId){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    Map<String,String> map = new HashMap<String,String>();
	    try {
	      StringBuffer sql = new StringBuffer();
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql.append("select A.whir$entrust_system,A.whir$entrust_mandatary,A.whir$entrust_state from WHIR$ENTRUST A  WHERE A.WHIR$ENTRUST_OWNER='"+userId+"' ");
	      System.out.println(sql.toString());
	      rs = stmt.executeQuery(sql.toString());
	        while (rs.next())
	        {
	        	map.put("system", rs.getString(1));
	        	map.put("mandatary", rs.getString(2));
	        	map.put("state", rs.getString(3));
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
	 * 当设置代理后将数据提交给各系统
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String unityAgency() {
		Map<String,String> map = new HashMap<String,String>();
		String oaresult = "1";
		String oadescription = "OA设置成功！";
		String wbresult = "1";
		String wbdescription = "网报设置成功！";
		String htresult = "1";
		String htdescription = "合同设置成功！";
		String mhresult = "1";
		String mhdescription = "门户设置成功！";
		
		HttpSession session = this.request.getSession();
		String userAccount = session.getAttribute("userAccount").toString();
		Map<String,String> mandataryMap = getMandataryAccount(userAccount,"0");
		String wtrEmpId = mandataryMap.get("empId");
		String wtrEmpName = mandataryMap.get("empName");
		//被委托人账号(适应本系统委托开始时间结束时间必须要而设置)
		Calendar cal = Calendar.getInstance();
		Date date=cal.getTime();
		String startTime = df.format(date);
		cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)+1);
		Date date1=cal.getTime();
		String endTime = df.format(date1);
		
		String state = request.getParameter("state");
		String mandataryId = request.getParameter("mandataryId");
		String systemStr = request.getParameter("system");
		String principal = request.getParameter("principal");
		String[] system = systemStr.substring(0, systemStr.length()-1).split(",");
		List sysList = java.util.Arrays.asList(system);
		
		Map<String,String> mandataryMap1 = getMandataryAccount(mandataryId,"1");
		String mandatary = mandataryMap1.get("mandatary");
		String bwtrEmpId = mandataryMap1.get("empId");
		String bwtrEmpName = mandataryMap1.get("empName");
		//委托人账号
		map.put("wtr", userAccount);
		//被委托人账号
		map.put("bwtr", mandatary);
		//委托开始时间
		map.put("sdate", startTime);
		//委托截止时间
		map.put("edate", endTime);
		for(int i=0;i<system.length;i++){
			if("0".equals(system[i])){
				//增加数据OA
				//委托状态
				map.put("wtzt", state);
				Map<String,String> oaMap = addDataToOA(map);
				oaresult = oaMap.get("result");
				oadescription = oaMap.get("description");
			} else if("1".equals(system[i])){
				//增加数据值网报
				//委托状态
				map.put("wtzt", state);
				map.put("interfaceType", "unifyLog");
				Map<String,String> wbMap = addDataToWB(map);
				wbresult = wbMap.get("result");
				wbdescription = wbMap.get("description");
			} else if("2".equals(system[i])){
				//增加数据合同
				String type = "4";
				if("0".equals(state)){
					type = "4";
				} else {
					type = "5";
				}
				//委托状态
				map.put("wtzt", state);
				map.put("type", type);
				Map<String,String> htMap = addDataToHt(map);
				htresult = htMap.get("result");
				htdescription = htMap.get("description");
			} else if("3".equals(system[i])){
				String type = "1";
				if("0".equals(state)){
					type = "1";
				} else {
					type = "0";
				}
				Map<String,String> mhMap = saveHmProxy(startTime,endTime,wtrEmpId,wtrEmpName,bwtrEmpId,bwtrEmpName,type);
				mhresult = mhMap.get("result");
				mhdescription = mhMap.get("description");
			}
		}
		if(!sysList.contains("0") && "0".equals(state)){
			map.put("wtzt", "1");
			Map<String,String> oaMap = addDataToOA(map);
			oaresult = oaMap.get("result");
			oadescription = oaMap.get("description");
		} else if(!sysList.contains("1") && "0".equals(state)){
			//委托状态
			map.put("wtzt", "1");
			map.put("interfaceType", "unifyLog");
			Map<String,String> wbMap = addDataToWB(map);
			wbresult = wbMap.get("result");
			wbdescription = wbMap.get("description");
		} else if(!sysList.contains("2") && "0".equals(state)){
			//委托状态
			map.put("wtzt", "1");
			map.put("type", "5");
			Map<String,String> htMap = addDataToHt(map);
			htresult = htMap.get("result");
			htdescription = htMap.get("description");
		} else if(!sysList.contains("3") && "0".equals(state)){
			String type = "0";
			Map<String,String> mhMap = saveHmProxy(startTime,endTime,wtrEmpId,wtrEmpName,bwtrEmpId,bwtrEmpName,type);
			mhresult = mhMap.get("result");
			mhdescription = mhMap.get("description");
		}
		StringBuffer json = new StringBuffer("[");
		json.append("{\"wbdescription\":\""+wbdescription+"\",");
    	json.append("\"oadescription\":\""+oadescription+"\",");
    	json.append("\"htdescription\":\""+htdescription+"\",");
    	json.append("\"mhdescription\":\""+mhdescription+"\",");
    	json.append("\"oaresult\":\""+oaresult+"\",");
    	json.append("\"wbresult\":\""+wbresult+"\",");
    	json.append("\"htresult\":\""+htresult+"\",");
    	json.append("\"mhresult\":\""+mhresult+"\"}");
	    json.append("]");
	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	/**
	 * 修改保存
	 * @return
	 */
	public Map<String,String> saveHmProxy(String beginTime,String endTime,String wtrEmpId,String wtrEmpName,String bwtrEmpId,String bwtrEmpName,String type){
		Map<String,String> serviceMap = new HashMap<String,String>();
		if("0".equals(type)){
			boolean deleteResult = deleteHmProxy(beginTime,endTime,wtrEmpId,wtrEmpName,bwtrEmpId,bwtrEmpName);
			System.out.println("deleteResult:"+deleteResult);
			serviceMap.put("result", "1");
        	serviceMap.put("description", "门户设置成功!");
		} else {
			System.out.println("开始保存代理");
			BPMProxyPO po =new BPMProxyPO();
			if(beginTime!=null&&!beginTime.equals("")&&!beginTime.equals("null")){ 
				try {
					System.out.println("beginTime::"+simpleDateFormat2.parse(beginTime));
					po.setBeginTime(simpleDateFormat2.parse(beginTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		    if(endTime!=null&&!endTime.equals("")&&!endTime.equals("null")){
		    	try {
		    		System.out.println("endTime::"+simpleDateFormat2.parse(endTime));
					po.setEndTime(simpleDateFormat2.parse(endTime));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
			}
		    System.out.println("开始保存代理1");
			HttpSession session=request.getSession();
			String userId=""+session.getAttribute("userId")+"";
			String userName=""+session.getAttribute("userName");
			String domainId=""+session.getAttribute("domainId");
			
	        BPMProxyBD bd=new BPMProxyBD();
	        
	        String bpmProcessIds="";
	        po.setProxyBPMProcessID(bpmProcessIds);
	        po.setProxyAllProcess(1);
	        System.out.println("开始保存代理2");
	        //保存或者 修改 的 结果
	        String saveorupdateResult="";
	        po.setEmpId(Long.parseLong(bwtrEmpId));
	        po.setEmpName(bwtrEmpName);
	        
	        po.setProxyEmpId(Long.parseLong(wtrEmpId));
	        po.setProxyEmpName(wtrEmpName);
	        po.setDomainId(domainId);
	        po.setCreateEmpId(Long.parseLong(userId));
	        po.setCreateEmpName(userName);
	        po.setProxyState(Integer.parseInt(type));
			//新增
			po.setCreateTime(new java.util.Date());
			saveorupdateResult=bd.add(po, new Long(userId));
			System.out.println("saveorupdateResult::"+saveorupdateResult);
			if ("system".equals(request.getParameter("from"))) {
				String logContent = "代理人：" + po.getProxyEmpName()+ "，被代理人：" + po.getEmpName();
				com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
				java.util.Date date = new java.util.Date();
				logBD.log(userId, userName, session .getAttribute("orgName").toString(),
						"system_work_agent", "系统管理", date, date, "1",
						logContent, session.getAttribute("userIP").toString(), domainId);
			}
	        bd.setUnavailableProxy(); 
	        /*if(saveorupdateResult.equals("repeat")){
	        	serviceMap.put("result", "0");
	        	serviceMap.put("description", "代理流程已设置，不能重复设置。");
	        }else{*/
	        	serviceMap.put("result", "1");
	        	serviceMap.put("description", "门户设置成功!");
			/*}*/
		}
		return serviceMap;
	}
	
	/**
	 * 删除当设置代理后将数据提交给各系统
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String delUnityAgency() {
		HttpSession session = this.request.getSession();
		String userAccount = session.getAttribute("userAccount").toString();
		
		Map<String,String> mandataryMap = getMandataryAccount(userAccount,"0");
		String wtrEmpId = mandataryMap.get("empId");
		String wtrEmpName = mandataryMap.get("empName");
		
		String infoIds = request.getParameter("infoIds");
		List<Map<String, String>> listMap = getDelData(infoIds);
		int result = 0;
		String description ="删除成功！";
		System.out.println("listMap:::"+listMap.size());
		for(Map<String, String> map :listMap){
			Map<String,String> seyMap = new HashMap<String,String>();
			String mandataryStr = map.get("mandataryStr");
			String mandataryId = mandataryStr.split(";")[1];
			Map<String,String> mandataryMap1 = getMandataryAccount(mandataryId,"1");
			String mandatary = mandataryMap1.get("mandatary");
			String bwtrEmpId = mandataryMap1.get("empId");
			String bwtrEmpName = mandataryMap1.get("empName");
			System.out.println("bwtrEmpName:::"+bwtrEmpName);
			
//			String startTime = map.get("startTime");
//			String endTime = map.get("endTime");
			Calendar cal = Calendar.getInstance();
			Date date=cal.getTime();
			String startTime = df.format(date);
			cal.set(Calendar.YEAR,cal.get(Calendar.YEAR)+1);
			Date date1=cal.getTime();
			String endTime = df.format(date1);
			
			String state = map.get("state");
			String systemStr = map.get("systemStr");
			String[] system = systemStr.substring(0, systemStr.length()-1).split(",");
			//委托人账号
			seyMap.put("wtr", userAccount);
			//被委托人账号
			seyMap.put("bwtr", mandatary);
			//委托开始时间
			seyMap.put("sdate", startTime);
			//委托截止时间
			seyMap.put("edate", endTime);
			//委托状态
			seyMap.put("wtzt", "1");
			
			for(int i=0;i<system.length;i++){
				if("0".equals(system[i])){
					//增加数据OA
					Map<String,String> oaMap = addDataToOA(seyMap);
					if(!"1".equals(oaMap.get("result"))){
						description = oaMap.get("description");
						result = 1;
						break;
					}
				} else if("1".equals(system[i])){
					//增加数据值网报
					seyMap.put("interfaceType", "unifyLog");
					Map<String,String> wbMap = addDataToWB(seyMap);
					
					if(!"1".equals(wbMap.get("result"))){
						description = wbMap.get("description");
						result = 1;
						break;
					}
				} else if("2".equals(system[i])){
					seyMap.put("type", "5");
					Map<String,String> htMap = addDataToHt(seyMap);
					
					if(!"1".equals(htMap.get("result"))){
						description = htMap.get("description");
						result = 1;
						break;
					}
				} else if("3".equals(system[i])){
					boolean deleteResult = deleteHmProxy(startTime,endTime,wtrEmpId,wtrEmpName,bwtrEmpId,bwtrEmpName);
					System.out.println("deleteResult:"+deleteResult);
				}
			}
		}
		StringBuffer json = new StringBuffer("[");
		json.append("{\"description\":\""+description+"\",");
    	json.append("\"result\":\""+result+"\"}");
	    json.append("]");
	    System.out.println(json.toString());
	    this.response.setContentType("text/plain;charSet=UTF-8");
	    this.response.setCharacterEncoding("UTF-8");
	    try {
	      PrintWriter pw = this.response.getWriter();
	      pw.print(json);
	      pw.close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    return null;
	}
	
	public boolean deleteHmProxy(String startTime,String endTime,String wtrEmpId,String wtrEmpName,String bwtrEmpId,String bwtrEmpName){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    boolean rs = true;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      stmt.execute("delete from EZ_BPM_WORKPROXY where EMP_ID='" + bwtrEmpId + "' and EMPNAME='"+bwtrEmpName+"' and PROXYEMPID='"+wtrEmpId+"' and PROXYEMPNAME='"+wtrEmpName+"'");
	    } catch (SQLException ex) {
	    	  rs = false;
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
	   return rs;
	}
	
	
	public List<Map<String, String>> getDelData(String infoIds){
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      
	      String startTime = "";
		  String endTime = "";
		  String state = "";
		  String mandataryStr = "";
		  String systemStr = "";
		  
		  conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "SELECT WHIR$ENTRUST_SYSTEM,WHIR$ENTRUST_MANDATARY,WHIR$ENTRUST_ENDTIME,WHIR$ENTRUST_STARTTIME,WHIR$ENTRUST_STATE FROM WHIR$ENTRUST A  WHERE A.WHIR$ENTRUST_ID IN ("+infoIds+")";
	      System.out.println(sql.toString());
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	Map<String,String> serviceMap = new HashMap<String,String>();
	        	startTime = rs.getString("WHIR$ENTRUST_STARTTIME");
	        	endTime = rs.getString("WHIR$ENTRUST_ENDTIME");
	        	state = rs.getString("WHIR$ENTRUST_STATE");
	        	systemStr = rs.getString("WHIR$ENTRUST_SYSTEM");
	        	mandataryStr = rs.getString("WHIR$ENTRUST_MANDATARY");
	        	serviceMap.put("startTime", startTime);
	        	serviceMap.put("endTime", endTime);
	        	serviceMap.put("state", state);
	        	serviceMap.put("systemStr", systemStr);
	        	serviceMap.put("mandataryStr", mandataryStr);
	        	listMap.add(serviceMap);
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
	   return listMap;
	}
	
	
	public int getRepeatSet(String principal,String[] system){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    int count=0;
	    try {
	      StringBuffer sql = new StringBuffer();
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql.append("SELECT COUNT(*) as COUNT FROM WHIR$ENTRUST A  WHERE A.WHIR$ENTRUST_PRINCIPAL='"+principal+"' ");
	      for(int i=0;i<system.length;i++){
	    	  if(i==0){
	    		 sql.append("AND (A.WHIR$ENTRUST_SYSTEM LIKE '%"+system[i]+"%'"); 
	    	  } else {
	    		  sql.append("OR A.WHIR$ENTRUST_SYSTEM LIKE '%"+system[i]+"%'"); 
	    	  }
	      }
	      sql.append(")");
	      System.out.println(sql.toString());
	      rs = stmt.executeQuery(sql.toString());
	        while (rs.next())
	        {
	        	count = Integer.parseInt(rs.getString(1));
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
	   return count;
	}
	
	
	public Map<String,String> getMandataryAccount(String mandataryId,String flag){
	    DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    Map<String,String> map = new HashMap<String,String>();
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      if("0".equals(flag)){
	    	  sql = "SELECT USERACCOUNTS,EMP_ID,EMPNAME FROM ORG_EMPLOYEE A WHERE A.USERACCOUNTS='"+mandataryId+"'";
	      } else {
	    	  sql = "SELECT USERACCOUNTS,EMP_ID,EMPNAME FROM ORG_EMPLOYEE A WHERE A.EMP_ID='"+mandataryId+"'";
	      }
	      rs = stmt.executeQuery(sql);
	        while (rs.next())
	        {
	        	String mandatary = rs.getString("USERACCOUNTS");
	        	String empId = rs.getString("EMP_ID");
	        	String empName = rs.getString("EMPNAME");
	        	map.put("mandatary", mandatary);
	        	map.put("empId", empId);
	        	map.put("empName", empName);
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
	   return map;
	}
	
	
	public Map<String,String> addDataToOA(Map<String,String> map){
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("oa_wsdl");
		
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element input = rec.addElement("input");
		Element key = input.addElement("key");
		key.addText("dffd512f3c274ec11af53753fc82b483");
		Element parameter = input.addElement("parameter");
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		   Map.Entry<String, String> entry = it.next();
		   Element element = parameter.addElement(entry.getKey());
		   element.setText(entry.getValue());
		}
		System.out.println("----传入的值:---------"+rec.asXML());
		Map<String,String> serviceMap = new HashMap<String,String>();
		try {
		      Client client = new Client(new URL(wsdl_address));
		      String send = "<input>"+rec.asXML().split("\\<input>")[1];
		      Object[] results = client.invoke("GetAgent", new Object[] { send });
		      String res = (String)results[0];
		      SAXBuilder builder = new SAXBuilder();
		      byte[] b = res.getBytes("UTF-8");
		      InputStream is = new ByteArrayInputStream(b);
		      org.jdom.Document doc = builder.build(is);
		      org.jdom.Element root = doc.getRootElement();
		      List dataList = root.getChildren("message");
			  if ((dataList != null) && (dataList.size() > 0))
			          for (int i = 0; i < dataList.size(); i++) {
			        	org.jdom.Element dataElement = (org.jdom.Element)dataList.get(i);
			            for(int m=0;m<dataElement.getContentSize();m++){
		                	String nodeName = dataElement.getContent(m).toString();
		                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
		                	serviceMap.put(keyName, dataElement.getContent(m).getValue());
			            }
			  }
		    }
		    catch (Exception e) {
		      e.printStackTrace();
		    }
		return serviceMap;
	}
	
	public Map<String,String> addDataToGWDB(Map<String,String> map){
		 Map<String,String> serviceMap = new HashMap<String,String>();
		 return serviceMap;
	}
	
	
	public Map<String,String> addDataToWB(Map<String,String> map){
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("wb_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("1qazse4!@#$");
		Element parameter = output.addElement("parameter");
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		   Map.Entry<String, String> entry = it.next();
		   Element element = parameter.addElement(entry.getKey());
		   element.setText(entry.getValue());
		}
		System.out.println("----传入的值:---------"+rec.asXML());
		Map<String,String> serviceMap = new HashMap<String,String>();
		String res = null;
		try {
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
			  call.setOperationName("getdata1");// WSDL里面描述的接口名称  
			  call.setSOAPActionURI("urn:getdata1");  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  String string = rec.asXML();
	          res = (String) call.invoke(new Object[] { string });
		      SAXBuilder builder = new SAXBuilder();
		      byte[] b = res.getBytes("UTF-8");
		      InputStream is = new ByteArrayInputStream(b);
		      org.jdom.Document doc = builder.build(is);
		      org.jdom.Element root = doc.getRootElement();
		      
		      List dataList = root.getChildren("message");
			  if ((dataList != null) && (dataList.size() > 0))
			          for (int i = 0; i < dataList.size(); i++) {
			        	org.jdom.Element dataElement = (org.jdom.Element)dataList.get(i);
			            for(int m=0;m<dataElement.getContentSize();m++){
		                	String nodeName = dataElement.getContent(m).toString();
		                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
		                	serviceMap.put(keyName, dataElement.getContent(m).getValue());
			            }
			  }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    
	    return serviceMap;
	}
	
	public Map<String,String> addDataToHt(Map<String,String> map){
		Properties properties = PropertiesUtils.getProperties();
		String wsdl_address = properties.getProperty("htgl_wsdl");
		Document rec = DocumentHelper.createDocument();
		rec.setXMLEncoding("UTF-8");
		Element output = rec.addElement("input");
		Element key = output.addElement("key");
		key.addText("58598301617b958a8b8343b731d0b931");
		Element parameter = output.addElement("parameter");
		
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
		   Map.Entry<String, String> entry = it.next();
		   Element element = parameter.addElement(entry.getKey());
		   element.setText(entry.getValue());
		}
		System.out.println("----合同待办传入的值:---------"+rec.asXML());
		Map<String,String> serviceMap = new HashMap<String,String>();
		String res = null;
		try {
			  Service service = new Service(); 
			  Call call = (Call) service.createCall();  
			  call.setTargetEndpointAddress(new java.net.URL(wsdl_address));  
			  call.setOperationName("getToDoList");// WSDL里面描述的接口名称  
			  call.addParameter("string", XMLType.XSD_STRING, ParameterMode.IN);// 接口的参数  
			  call.setReturnType(XMLType.XSD_STRING);// 设置返回类型  
			  call.setTimeout(4000);
			  String string = rec.asXML();
	          res = (String) call.invoke(new Object[] { string });
		      SAXBuilder builder = new SAXBuilder();
		      byte[] b = res.getBytes("UTF-8");
		      InputStream is = new ByteArrayInputStream(b);
		      org.jdom.Document doc = builder.build(is);
		      org.jdom.Element root = doc.getRootElement();
		      
		      List dataList = root.getChildren("message");
			  if ((dataList != null) && (dataList.size() > 0))
			          for (int i = 0; i < dataList.size(); i++) {
			        	org.jdom.Element dataElement = (org.jdom.Element)dataList.get(i);
			            for(int m=0;m<dataElement.getContentSize();m++){
		                	String nodeName = dataElement.getContent(m).toString();
		                	String keyName = nodeName.substring(nodeName.indexOf("<")+1, nodeName.indexOf("/"));
		                	serviceMap.put(keyName, dataElement.getContent(m).getValue());
			            }
			  }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    
	    return serviceMap;
	}
}
