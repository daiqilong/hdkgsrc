package com.whir.ezoffice.watchmanager.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;


import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.util.JacksonUtil;
import com.whir.rd.util.WatchArrangeUtils;

public class WatcharrangeAction extends BaseActionSupport {
	/**
	 * 
	 */
	private static final long serialVersionUID = 209574667905937320L;
	//值班查询开始日期
	private String arrangeDateFrom;
	//值班查询结束日期
	private String arrangeDateEnd;
	//值班表id
	private String watchtab_id;
	//主班人员id
	private String postemptab_mpid;
	//主班人员姓名
	private String postemptab_mpname;
	//副班人员id
	private String postemptab_apid;
	//副班人员姓名
	private String postemptab_apname;
	//岗位id
	private String postId;
	//部门id
	private String departmentId;
	//值班日期
	private String dates;
	
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	public String getArrangeDateFrom() {
		return arrangeDateFrom;
	}
	public void setArrangeDateFrom(String arrangeDateFrom) {
		this.arrangeDateFrom = arrangeDateFrom;
	}
	public String getArrangeDateEnd() {
		return arrangeDateEnd;
	}
	public void setArrangeDateEnd(String arrangeDateEnd) {
		this.arrangeDateEnd = arrangeDateEnd;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getDepartmentId() {
		return departmentId;
	}
	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}
	
	public String getWatchtab_id() {
		return watchtab_id;
	}
	public void setWatchtab_id(String watchtabId) {
		watchtab_id = watchtabId;
	}
	public String getPostemptab_mpid() {
		return postemptab_mpid;
	}
	public void setPostemptab_mpid(String postemptabMpid) {
		postemptab_mpid = postemptabMpid;
	}
	public String getPostemptab_mpname() {
		return postemptab_mpname;
	}
	public void setPostemptab_mpname(String postemptabMpname) {
		postemptab_mpname = postemptabMpname;
	}
	public String getPostemptab_apid() {
		return postemptab_apid;
	}
	public void setPostemptab_apid(String postemptabApid) {
		postemptab_apid = postemptabApid;
	}
	public String getPostemptab_apname() {
		return postemptab_apname;
	}
	public void setPostemptab_apname(String postemptabApname) {
		postemptab_apname = postemptabApname;
	}
	//进入员工排班页面
	public String watcharrange(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString();
		Object[] obj  = new WatchArrangeUtils().getScopeType(userId, "ygpbsz*01*01");
		int scopeType = Integer.parseInt(obj[0].toString());
		String dsql = "select distinct whir$posttab_dno,whir$posttab_dname from whir$posttab where whir$posttab_class='0'";
		switch(scopeType){
		case 1:dsql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 2:dsql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 3:dsql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 4:
			String reorgId = obj[1].toString();
			reorgId = reorgId.substring(1,reorgId.length()-1);
			dsql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+reorgId+"$%')";break;
		case -1:dsql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
			
		case 0:break;
		}
		List dlist = new WatchArrangeUtils().getList(dsql);
		request.setAttribute("departmentId", dlist);
		return "watcharrange";
	}
	//进入中心领导排班页面
	public String leaderwatcharrange(){
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String psql = "select whir$posttab_id,whir$posttab_pname from whir$posttab where whir$posttab_class='10'";
		Object[] obj = new WatchArrangeUtils().getScopeType(userId, "ygpbsz*01*01");
		int scopeType = Integer.parseInt(obj[0].toString());
		switch(scopeType){
		case 1:psql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 2:psql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 3:psql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
		case 4:
			String reorgId = obj[1].toString();
			reorgId = reorgId.substring(1,reorgId.length()-1);
			psql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+reorgId+"$%')";break;
		case -1:psql += " and whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"+orgId+"$%')";break;
			
		case 0:break;
		}
		List plist = new WatchArrangeUtils().getList(psql);
		
		request.setAttribute("postId", plist);
		return "leaderwatcharrange";
	}
	//进入局领导排班页面
	public String bigleaderwatcharrange(){
		String psql = "select whir$posttab_id,whir$posttab_pname from whir$posttab where whir$posttab_class='100'";
		List plist = new WatchArrangeUtils().getList(psql);
		//只有局领导一个岗位
		Object[] obj = (Object[])plist.get(0);
		String postId = obj[0].toString();
		setPostId(postId);
		return "bigleaderwatcharrange";
	}
	//自动排班
	public String autoarrange(){
		String rsql = "select distinct(ppp.whir$postemptab_sortno),ppp.whir$postemptab_mpa,ppp.whir$postemptab_mpname,ppp.whir$postemptab_apa,ppp.whir$postemptab_apname"
        			+" from whir$postemptab ppp where ppp.whir$postemptab_postno = '"+postId+"' order by ppp.whir$postemptab_sortno"; 
		List emplist = new WatchArrangeUtils().getList(rsql);
		if(emplist.size()==0){
			printResult("请先配置值班人员");
			return null;
		}
//		System.out.println("得到emplist。。。emplist.size="+emplist.size());
		
		List dayList = new ArrayList();
		String datefrom = arrangeDateFrom;
		String dateto = arrangeDateEnd;
		try {
			dayList = getDays(datefrom, dateto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List duplicateDates = getDuplicateDates(dayList, postId);
		String sql = "";
		List desqlList = new ArrayList();
		if(duplicateDates.size()>0){
			for(int i=0;i<duplicateDates.size()/2;i++){
				sql = "delete from whir$watchtab where whir$watchtab_id='"+duplicateDates.get(2*i)+"'";
				desqlList.add(sql);
			}
			new WatchArrangeUtils().executeBatchSql(desqlList);
		}
	
		//自动排班
		List sqlList = new ArrayList();
		for(int i=0,j=0;i<dayList.size()/2;i++,j++){
			if(j<emplist.size()){
				Object[] objs = (Object[])emplist.get(j);
				if(objs[3] != null && !"".equals(objs[3].toString()) && !"null".equals(objs[3].toString())){
					sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
					"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dayList.get(2*i)+"','"+dayList.get(2*i+1)+"','"+objs[1].toString()+"','"+objs[2].toString()+"','"+objs[3].toString()+"','"+objs[4].toString()+"')";
				}else{
					sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
					"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dayList.get(2*i)+"','"+dayList.get(2*i+1)+"','"+objs[1].toString()+"','"+objs[2].toString()+"','','')";
				}
				sqlList.add(sql);
			} else{
				j=0;
				Object[] objs = (Object[])emplist.get(j);
				if(objs[3] != null && !"".equals(objs[3].toString()) && !"null".equals(objs[3].toString())){
					sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
					"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dayList.get(2*i)+"','"+dayList.get(2*i+1)+"','"+objs[1].toString()+"','"+objs[2].toString()+"','"+objs[3].toString()+"','"+objs[4].toString()+"')";
				}else{
					sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
					"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dayList.get(2*i)+"','"+dayList.get(2*i+1)+"','"+objs[1].toString()+"','"+objs[2].toString()+"','','')";
				}
				sqlList.add(sql);
			}
		}
		new WatchArrangeUtils().executeBatchSql(sqlList);
		printResult("success");
		return null;
	}
	//检查是否已排班
	public String checkArrangeDate(){
		List dayList = new ArrayList();
		String datefrom = arrangeDateFrom;
		String dateto = arrangeDateEnd;
		try {
			dayList = getDays(datefrom, dateto);
		} catch (Exception e) {
			e.printStackTrace();
		}
		List duplicateDates = getDuplicateDates(dayList, postId);
		String result = "日期：";
		for(int i=0;i<duplicateDates.size()/2;i++){
			result = result+duplicateDates.get(2*i+1).toString()+",";
		}
		if(result.length()>4){
			result += "已经排过班，是否重新排？";
			printResult(result);
		}else{
			printResult("success");
		}
		return null;
	}
	//手动排班及默认排班页面加载
	public String autoarrangequery(){
		List showList = new ArrayList();
		String sql = "select whir$watchtab_id,concat(whir$watchtab_dates,whir$watchtab_weeks) dates,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname " +
				"from whir$watchtab where whir$watchtab_postno='"+postId+"' and (whir$watchtab_dates between '"+arrangeDateFrom+"' and '"+arrangeDateEnd+"') order by whir$watchtab_dates";
		showList = new WatchArrangeUtils().getList(sql);
		String viewsql = "ppp.whir$watchtab_id,ppp.dates,ppp.whir$watchtab_mpa,ppp.whir$watchtab_mpname,ppp.whir$watchtab_apa,ppp.whir$watchtab_apname";
		JacksonUtil util = new JacksonUtil();
        int showlistsize = showList.size();
        //判断是否属于手动排班
        if(arrangeDateFrom != null && !"".equals(arrangeDateFrom) && arrangeDateEnd != null && !"".equals(arrangeDateEnd) && postId !=null && !"".equals(postId)){
        	try {
    			List dayList = getDays(arrangeDateFrom, arrangeDateEnd);
    			//对于未进行排班的日期默认显示日期
    			if(dayList.size()/2>showlistsize){
    				List showarrangelist = new ArrayList();
    				for(int i=0;i<dayList.size()/2;i++){
    					String inday = dayList.get(2*i).toString();
    					boolean index = true;
    					for(int j=0;j<showlistsize;j++){
    						Object[] objshow = (Object[])showList.get(j);
    						String showday = objshow[1].toString().substring(0,10);
    						if(showday.equals(inday)){
    							showarrangelist.add(objshow);
    							index = false;
    							break;
    						}
    					}
    					if(index){
    						Object[] obj = new Object[6];
        					obj[0] = "";
        					obj[1] = dayList.get(2*i).toString()+dayList.get(2*i+1);
        					obj[2] = "";
        					obj[3] = "";
        					obj[4] = "";
        					obj[5] = "";
        					showarrangelist.add(obj);
    					}
    					
    				}
    				
    				String json = util.writeArrayJSON(viewsql, showarrangelist);
    				json = "{pager:{pageCount:"+1+",recordCount:"+1+"},data:"+json+"}";
    				printResult(G_SUCCESS,json);
    				return null;
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			return null;
    		}
        }
		String json = util.writeArrayJSON(viewsql, showList);
		json = "{pager:{pageCount:"+1+",recordCount:"+1+"},data:"+json+"}";
		printResult(G_SUCCESS,json);
		System.out.println("班表查询完成。。。。。。");
		return null;
	}
	//进入修改值班人员
	public String load(){
		System.out.println("postId==="+postId);
		
		String sql = "select whir$postemptab_mpa,whir$postemptab_mpname,whir$postemptab_apa,whir$postemptab_apname from whir$postemptab where whir$postemptab_postno='"+postId+"' order by whir$postemptab_sortno";
		List empList = new WatchArrangeUtils().getList(sql);
		List postEmpList = new ArrayList();
		//得到岗位下设定的值班人员
		for(int i=0;i<empList.size();i++){
        	Object[] objs = (Object[])empList.get(i);
        		String res = objs[0].toString();
        		if(res.startsWith("$")){
        			String[] empIds = (res.substring(1) + "$").split("\\$\\$");
        			String[] empnames = objs[1].toString().split(",");
        			for(int j=0;j<empIds.length;j++){
        				Object[] emps = new Object[2];
        				emps[0] = empIds[j];
        				emps[1] = empnames[j];
        				postEmpList.add(emps);
        			}
        		}else{
        			Object[] emps = new Object[2];
        			emps[0] = res;
    				emps[1] = objs[1].toString();
    				postEmpList.add(emps);
        		}
        		if(objs[2]!=null && !"".equals(objs[2].toString()) && !"null".equals(objs[2].toString())){
        			String ras = objs[2].toString();
        			if(ras.startsWith("$")){
        				String[] empIds = (ras.substring(1) + "$").split("\\$\\$");
            			String[] empnames = objs[3].toString().split(",");
            			for(int j=0;j<empIds.length;j++){
            				Object[] emps = new Object[2];
            				emps[0] = empIds[j];
            				emps[1] = empnames[j];
            				postEmpList.add(emps);
            			}
        			}else{
        				Object[] emps = new Object[2];
            			emps[0] = ras;
        				emps[1] = objs[3].toString();
        				postEmpList.add(emps);
        			}
        		}
        		
        }
		//去除重复的人员
		List postEmps = new ArrayList();
		for(int i=0;i<postEmpList.size();i++){
			boolean index = true;
			Object[] emp1 = (Object[])postEmpList.get(i);
			
			for(int j=0;j<postEmps.size();j++){
				Object[] emp2 = (Object[])postEmps.get(j);
				if(emp1[0].equals(emp2[0])){
					index = false;
					break;
				}
			}
			if(index){
				postEmps.add(emp1);
			}
		}
		request.setAttribute("postEmps", postEmps);
		return "load";
	}
	//进入领导岗位值班人员修改页面
	public String loadLeader(){
//		String sql = "select whir$postemptab_mpa,whir$postemptab_mpname from whir$postemptab where whir$postemptab_postno='"+postId+"' order by whir$postemptab_sortno";
//		List empList = new WatchArrangeUtils().getList(sql);
//		List postEmpList = new ArrayList();
//		
//		for(int i=0;i<empList.size();i++){
//        	Object[] objs = (Object[])empList.get(i);
//        		String res = objs[0].toString();
//        		if(res.startsWith("$")){
////        			System.out.println("111111111");
//        			String[] empIds = (res.substring(1) + "$").split("\\$\\$");
//        			String[] empnames = objs[1].toString().split(",");
//        			for(int j=0;j<empIds.length;j++){
//        				Object[] emps = new Object[2];
//        				emps[0] = empIds[j];
//        				emps[1] = empnames[j];
////        				System.out.println(emps[0]+"-----"+emps[1]);
//        				postEmpList.add(emps);
//        			}
//        		}else{
////        			System.out.println("2222222");
//        			Object[] emps = new Object[2];
//        			emps[0] = res;
//    				emps[1] = objs[1].toString();
//    				postEmpList.add(emps);
//        		}
//        }
//		request.setAttribute("postLeaderList", postEmpList);
		return "loadLeader";
	}
	public String updateWatchEmps(){
//		System.out.println("开始修改值班人员。。。。processPackageDisName:"+processPackageDisName);
		String apId = "";
		if(postemptab_apid !=null && !"".equals(postemptab_apid) && !postemptab_apid.startsWith("$")){
			String[] apid = postemptab_apid.split(",");
			for(int i=0;i<apid.length;i++){
				apId += "$"+apid[i]+"$"; 
			}
		}else{
			apId = postemptab_apid;
		}
		String mpId = "";
		if(!postemptab_mpid.startsWith("$")){
			String[] mpIds = postemptab_mpid.split(",");
			for(int i=0;i<mpIds.length;i++){
				mpId += "$"+mpIds[i]+"$"; 
			}
		}else{
			mpId = postemptab_mpid;
		}
		String sql = "update whir$watchtab set whir$watchtab_mpa='"+mpId+"',whir$watchtab_mpname='"+postemptab_mpname+"',whir$watchtab_apa='"+apId+"',whir$watchtab_apname='"+postemptab_apname+"' where whir$watchtab_id='"+watchtab_id+"'";
		if(watchtab_id == null || "".equals(watchtab_id)){
			sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
			"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dates.substring(0,10)+"','"+dates.substring(10)+"','"+mpId+"','"+postemptab_mpname+"','"+apId+"','"+postemptab_apname+"')";;
		}
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}
	public String updateWatchLeaders(){
		String mpId = "";
		if(!postemptab_mpid.startsWith("$")){
			String[] mpIds = postemptab_mpid.split(",");
			for(int i=0;i<mpIds.length;i++){
				mpId += "$"+mpIds[i]+"$"; 
			}
		}else{
			mpId = postemptab_mpid;
		}
		String sql = "update whir$watchtab set whir$watchtab_mpa='"+mpId+"',whir$watchtab_mpname='"+postemptab_mpname+"' where whir$watchtab_id='"+watchtab_id+"'";
		if(watchtab_id == null || "".equals(watchtab_id)){
			sql = "insert into whir$watchtab (whir$watchtab_id,whir$watchtab_postno,whir$watchtab_dates,whir$watchtab_weeks,whir$watchtab_mpa,whir$watchtab_mpname,whir$watchtab_apa,whir$watchtab_apname) "+
			"values (whir$watchtab_SEQ.nextval,'"+postId+"','"+dates.substring(0,10)+"','"+dates.substring(10)+"','"+mpId+"','"+postemptab_mpname+"','','')";;
		}
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}
	//获取部门下岗位信息
	public String getRelatedPosts(){
		System.out.println("获取部门下的岗位信息。。。。departmentId"+departmentId);
		String sql = "";
		StringBuffer json = new StringBuffer("[");
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
    		sql = "select whir$posttab_id,whir$posttab_pname from whir$posttab where whir$posttab_dno in (select org_id from org_organization where orgidstring like '%$"+departmentId+"$%')";
    		rs = stmt.executeQuery(sql);
	    	while (rs.next()) {
				String postId = (rs.getString(1) != null && !"".equals(rs.getString(1)))?rs.getString(1):"";
				json.append("{\"postId\":\""+postId+"\",");
				String postName = (rs.getString(2) != null && !"".equals(rs.getString(2)))?rs.getString(2):"";
				json.append("\"postName\":\""+postName+"\"},");
	    	}
	    	json.delete(json.length()-1, json.length());
	    	json.append("]");
	    	rs.close();
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
	    response.setContentType("text/plain;charSet=UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    try {
	        PrintWriter pw = this.response.getWriter();
	        pw.print(json);
	        pw.close();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
		return null;
		
	}
	
	public Date getNextDate(Date date,int i){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, i);
		Date date1 = new Date(calendar.getTimeInMillis());
		return date1;
	}
	public String getWeeks(int weeks){
		String wks = "";
		switch(weeks){
		case 1:wks="星期一";break;
		case 2:wks="星期二";break;
		case 3:wks="星期三";break;
		case 4:wks="星期四";break;
		case 5:wks="星期五";break;
		case 6:wks="星期六";break;
		case 7:wks="星期日";break;
		}
		return wks;
	}
	//获取两个日期之间的所有日期和星期
	public List getDays(String datefrom,String dateto) throws Exception{
		Date da = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		da = df.parse(datefrom);
		long from = df.parse(datefrom).getTime();
		long to = df.parse(dateto).getTime();
		int i=0;
		Calendar calend = Calendar.getInstance();
		List dayList = new ArrayList();
		
		for(long daytime=from;daytime<to;i++){
			Date nextdate = getNextDate(da, i);
			String days = df.format(nextdate);
			dayList.add(days);
			calend.setTime(nextdate);
			int week = calend.get(Calendar.DAY_OF_WEEK);
			int weeks = (week==1?7:week-1);
			dayList.add(getWeeks(weeks));
			daytime = nextdate.getTime();
		}
		return dayList;
	}
	//获取已排班的日期
	public List getDuplicateDates(List dayList,String postId){
		List duplicateDates = new ArrayList();
		
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	    	String sql = "";
	    	conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	ResultSet rs = null;
	    	for(int i=0;i<dayList.size()/2;i++){
	    		sql = "select whir$watchtab_id,whir$watchtab_dates from whir$watchtab where whir$watchtab_postno='"+postId+"' and whir$watchtab_dates='"+dayList.get(2*i)+"'";
	    		rs = stmt.executeQuery(sql);
		    	while (rs.next()){
		    		duplicateDates.add(rs.getObject(1));
		    		duplicateDates.add(rs.getObject(2));
		    	}
	    	}
	    	rs.close();
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
		return duplicateDates;
	}
	
}
