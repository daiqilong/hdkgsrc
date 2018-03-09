package com.whir.ezoffice.watchmanager.actionsupport;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.rd.util.WatchArrangeUtils;

public class WatchempdeployAction extends BaseActionSupport{
	//人员表id
	private String postemptab_id;
	//岗位id
	private String postId;
	//排班序号
	private String postemptab_sortno;
	//主班人员id
	private String postemptab_mpid;
	//主班人员名称
	private String postemptab_mpname;
	//副班人员id
	private String postemptab_apid;
	//副班人员名称
	private String postemptab_apname;
	//private String
	public String getPostemptab_id() {
		return postemptab_id;
	}

	public void setPostemptab_id(String postemptabId) {
		postemptab_id = postemptabId;
	}
	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public String getPostemptab_sortno() {
		return postemptab_sortno;
	}

	public void setPostemptab_sortno(String postemptabSortno) {
		postemptab_sortno = postemptabSortno;
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
	//进入员工值班人员配置页面
	public String empdeploy(){
		System.out.println("进入员工岗位人员配置页面。。。");
//		System.out.println("postId======:"+postId);
		return "empdeploy";
	}
	//进入领导值班人员配置页面
	public String leaderempdeploy(){
		System.out.println("进入领导岗位人员配置页面。。。");
//		System.out.println("postId======:"+postId);
		return "leaderempdeploy";
	}
	//加载list
	public String empdeployList(){
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        
        String viewSQL = "ppp.whir$postemptab_id,ppp.whir$postemptab_sortno,ppp.whir$postemptab_mpa,ppp.whir$postemptab_mpname,ppp.whir$postemptab_apa,ppp.whir$postemptab_apname";
        String fromSQL = "whir$postemptab ppp ";
        String whereSQL = "where ppp.whir$postemptab_postno = '"+postId+"'"; 
        String orderSQL = "order by ppp.whir$postemptab_sortno";
        Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL, orderSQL);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        List list = page.getResultList();
        for(int i=0;i<list.size();i++){
        	Object[] objs = (Object[])list.get(i);
        	objs[1] = i+1;
        }
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		printResult(G_SUCCESS,json);
		return null;
	}
	//进入增加值班人员
	public String addEmp() throws IOException{
		
		String sql = "select whir$posttab_dno from whir$posttab where whir$posttab_id ='"+postId+"'";
		Object[] objs = new WatchArrangeUtils().getArray(sql);
		request.setAttribute("orgId", objs[0]);
		return "addEmp";
	}
	public String addLeaderEmp() throws IOException{
		String sql = "select whir$posttab_dno from whir$posttab where whir$posttab_id ='"+postId+"'";
		Object[] objs = new WatchArrangeUtils().getArray(sql);
		request.setAttribute("orgId", objs[0]);
		
		return "addLeaderEmp";
	}
	public String saveEmp(){
		String sql = "select max(whir$postemptab_sortno) max_sortno from whir$postemptab where whir$postemptab_postno='"+postId+"'";
		int sort = 0;
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
	    	while(rs.next()){
	    		sort = rs.getInt(1);
	    	}
	    	sort++;
	    	sql =  "insert into whir$postemptab (whir$postemptab_id,whir$postemptab_postno,whir$postemptab_sortno,whir$postemptab_mpa,whir$postemptab_mpname,whir$postemptab_apa,whir$postemptab_apname) " +
			"values (whir$postemptab_SEQ.nextval,'"+postId+"',"+sort+",'"+postemptab_mpid+"','"+postemptab_mpname+"','"+postemptab_apid+"','"+postemptab_apname+"')";
	    	stmt.executeQuery(sql);
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
		 
		System.out.println("新增员工完成。。。。");
		printResult("success");
		return null;
	}
	public String saveLeaderEmp(){
		String sql = "select max(whir$postemptab_sortno) max_sortno from whir$postemptab where whir$postemptab_postno='"+postId+"'";
		int sort = 0;
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    ResultSet rs = null;
		try{
			conn = dsb.getDataSource().getConnection();
	    	stmt = conn.createStatement();
	    	rs = stmt.executeQuery(sql);
	    	while(rs.next()){
	    		sort = rs.getInt(1);
	    	}
	    	sort++;
	    	sql = "insert into whir$postemptab (whir$postemptab_id,whir$postemptab_postno,whir$postemptab_sortno,whir$postemptab_mpa,whir$postemptab_mpname) " +
			"values (whir$postemptab_SEQ.nextval,'"+postId+"',"+sort+",'"+postemptab_mpid+"','"+postemptab_mpname+"')";
	    	stmt.executeQuery(sql);
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
		System.out.println("新增领导岗位值班人员完成。。。。");
		printResult("success");
		return null;
	}
	//修改值班人员配置
	public String load() throws IOException{
		//得到改岗位下的值班人员
		String sql = "select whir$posttab_dno from whir$posttab where whir$posttab_id ='"+postId+"'";
		Object[] objs = new WatchArrangeUtils().getArray(sql);
		request.setAttribute("orgId", objs[0]);
		
		return "load";
	}
	public String loadLeader() throws IOException{
		String sql = "select whir$posttab_dno from whir$posttab where whir$posttab_id ='"+postId+"'";
		Object[] objs = new WatchArrangeUtils().getArray(sql);
		request.setAttribute("orgId", objs[0]);
		return "loadLeader";
	}
	public String updateEmp(){
		String sql = "update whir$postemptab set whir$postemptab_mpa='"+postemptab_mpid+"',whir$postemptab_mpname='"+postemptab_mpname+"',whir$postemptab_apa='"+postemptab_apid+"',whir$postemptab_apname='"+postemptab_apname+"' " +
				"where whir$postemptab_id='"+postemptab_id+"' ";
		new WatchArrangeUtils().executeSql(sql);
		System.out.println("修改完成。。。。");
		printResult("success");
		return null;
	}
	public String updateLeaderEmp(){
		String sql = "update whir$postemptab set whir$postemptab_mpa='"+postemptab_mpid+"',whir$postemptab_mpname='"+postemptab_mpname+"'" +
				"where whir$postemptab_id='"+postemptab_id+"' ";
		new WatchArrangeUtils().executeSql(sql);
		System.out.println("修改完成。。。。");
		printResult("success");
		return null;
	}
	//删除
	public String delEmp(){
		String sql = "delete whir$postemptab where whir$postemptab_id='"+postemptab_id+"'";
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}
}
