package com.whir.ezoffice.watchmanager.actionsupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.rd.util.WatchArrangeUtils;

public class WatchpostAction extends BaseActionSupport {
	// 岗位名称
	private String postName;
	// 岗位id
	private String postId;
	// 岗位名称查询
	private String searchPostName;
	// 部门名称查询
	private String searchDepName;
	// 部门id
	private String menuViewId;
	// 部门名称
	private String menuView;
	// 值班人员数
	private String dutyNumber;

	public String getMenuViewId() {
		return menuViewId;
	}

	public void setMenuViewId(String menuViewId) {
		this.menuViewId = menuViewId;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getMenuView() {
		return menuView;
	}

	public void setMenuView(String menuView) {
		this.menuView = menuView;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public String getSearchPostName() {
		return searchPostName;
	}

	public void setSearchPostName(String searchPostName) {
		this.searchPostName = searchPostName;
	}

	public String getSearchDepName() {
		return searchDepName;
	}

	public void setSearchDepName(String searchDepName) {
		this.searchDepName = searchDepName;
	}

	public String getDutyNumber() {
		return dutyNumber;
	}

	public void setDutyNumber(String dutyNumber) {
		this.dutyNumber = dutyNumber;
	}

	// 普通岗位
	public String postdefinition() {
		System.out.println("进入了岗位列表。。。");
		return "postdefinition";
	}

	// 中心领导岗位
	public String leaderPostDefinition() {

		return "leaderPostDefinition";
	}

	// 进入局领导岗位
	public String bigLeaderPostDefinition() {

		return "bigLeaderPostDefinition";
	}

	// 普通岗位列表
	public String postList() {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		// 查看该用户权限
		Object[] obj = new Object[2];
		int scopeType = -1;
		boolean postSetRight = new ManagerBD().hasRight(userId, "yggwsz*02*01");
		if (postSetRight) {
			// 维护
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*02*01");
			scopeType = Integer.parseInt(obj[0].toString());
		} else {
			// 设置
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*01*01");
			scopeType = Integer.parseInt(obj[0].toString());
		}

		String orgId = session.getAttribute("orgId").toString();

		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		String viewSQL = "ppp.whir$posttab_pname,ppp.whir$posttab_dno,ppp.whir$posttab_dname,ppp.whir$posttab_id";
		String fromSQL = "whir$posttab ppp ";
		StringBuffer whereSQL = new StringBuffer();
		whereSQL.append("where");
		Map varMap = new HashMap();
		if (searchPostName != null && !"".equals(searchPostName)) {
			whereSQL.append(" ppp.whir$posttab_pname like :searchPostName and");
			varMap.put("searchPostName", "%" + searchPostName + "%");
		}
		if (searchDepName != null && !"".equals(searchDepName)) {
			whereSQL.append(" ppp.whir$posttab_dname like :searchDepName and");
			varMap.put("searchDepName", "%" + searchDepName + "%");
		}
		whereSQL.append(" ppp.whir$posttab_class='0'");
		// 判断用户可查看的范围
		switch (scopeType) {
		case -1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 3:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 2:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 4:
			String reorgId = obj[1].toString();
			reorgId = reorgId.substring(1, reorgId.length() - 1);
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ reorgId + "$%')");
			break;
		case 0:
			break;

		}

		String orderSQL = "order by ppp.whir$posttab_dno";
		Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL
				.toString(), orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		page.setVarMap(varMap);
		List list = page.getResultList();

		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);
		return null;
	}

	// 中心领导岗位列表
	public String leaderPostList() {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		// 查看该用户权限
		Object[] obj = new Object[2];
		int scopeType = -1;
		boolean postSetRight = new ManagerBD().hasRight(userId, "yggwsz*02*01");
		if (postSetRight) {
			// 维护
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*02*01");
			scopeType = Integer.parseInt(obj[0].toString());
		} else {
			// 设置
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*01*01");
			scopeType = Integer.parseInt(obj[0].toString());
		}
		String orgId = session.getAttribute("orgId").toString();
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		String viewSQL = "ppp.whir$posttab_pname,ppp.whir$posttab_dno,ppp.whir$posttab_id,ppp.whir$posttab_dutyNumber";
		String fromSQL = "whir$posttab ppp ";
		StringBuffer whereSQL = new StringBuffer();
		whereSQL.append("where");
		whereSQL.append(" ppp.whir$posttab_class='10'");
		// 判断用户可查看的范围
		switch (scopeType) {
		case -1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 3:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 2:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 4:
			String reorgId = obj[1].toString();
			reorgId = reorgId.substring(1, reorgId.length() - 1);
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ reorgId + "$%')");
			break;
		case 0:
			break;
		}
		String orderSQL = "order by ppp.whir$posttab_dno";
		Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL
				.toString(), orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		List list = page.getResultList();

		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);
		return null;
	}

	// 局领导岗位列表
	public String bigLeaderPostList() {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		// 查看该用户权限
		Object[] obj = new Object[2];
		int scopeType = -1;
		boolean postSetRight = new ManagerBD().hasRight(userId, "yggwsz*02*01");
		if (postSetRight) {
			// 维护
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*02*01");
			scopeType = Integer.parseInt(obj[0].toString());
		} else {
			// 设置
			obj = new WatchArrangeUtils().getScopeType(userId, "yggwsz*01*01");
			scopeType = Integer.parseInt(obj[0].toString());
		}
		String orgId = session.getAttribute("orgId").toString();
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		String viewSQL = "ppp.whir$posttab_pname,ppp.whir$posttab_dno,ppp.whir$posttab_id";
		String fromSQL = "whir$posttab ppp ";
		StringBuffer whereSQL = new StringBuffer();
		whereSQL.append("where");
		whereSQL.append(" ppp.whir$posttab_class='100'");
		// 判断用户可查看的范围
		switch (scopeType) {
		case -1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 1:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 3:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 2:
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ orgId + "$%')");
			break;
		case 4:
			String reorgId = obj[1].toString();
			reorgId = reorgId.substring(1, reorgId.length() - 1);
			whereSQL
					.append(" and ppp.whir$posttab_dno in(select org_id from org_organization where orgidstring like '%$"
							+ reorgId + "$%')");
			break;
		case 0:
			break;
		}
		String orderSQL = "order by ppp.whir$posttab_dno";
		Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL
				.toString(), orderSQL);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		List list = page.getResultList();

		int pageCount = page.getPageCount();
		int recordCount = page.getRecordCount();
		JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);
		return null;
	}

	// 增加岗位
	public String addPost() {

		return "addPost";
	}

	public String savePost() {
		String postname = postName.replaceAll("\\s*|\t|\r|\n", "");
		// 获得上级部门名称加入
		String departmentName = "";
		String sql = "select orgname from org_organization where org_id=(select orgparentorgid from org_organization where org_id='"
				+ menuViewId + "')";
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = dsb.getDataSource().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				departmentName = rs.getString(1);
			}
			if (!"".equals(departmentName)) {
				departmentName += "." + menuView;
				sql = "insert into whir$posttab (whir$posttab_id,whir$posttab_pname,whir$posttab_dno,whir$posttab_dname,whir$posttab_class) values(whir$posttab_SEQ.nextval,'"
						+ postname
						+ "','"
						+ menuViewId
						+ "','"
						+ departmentName + "','0')";
			} else {
				sql = "insert into whir$posttab (whir$posttab_id,whir$posttab_pname,whir$posttab_dno,whir$posttab_dname,whir$posttab_class) values(whir$posttab_SEQ.nextval,'"
						+ postname
						+ "','"
						+ menuViewId
						+ "','"
						+ menuView
						+ "','0')";
			}
			stmt.executeQuery(sql);

		} catch (SQLException ex) {
			ex.printStackTrace();
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException localSQLException1) {
			}
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException localSQLException2) {
			}
		}
		printResult("success");
		return null;
	}

	// 增加中心领导岗位
	public String addLeaderPost() {
		return "addLeaderPost";
	}

	public String saveLeaderPost() {
		String sql = "insert into whir$posttab (whir$posttab_id,whir$posttab_pname,whir$posttab_dno,whir$posttab_dutyNumber,whir$posttab_class) values(whir$posttab_SEQ.nextval,'"
				+ postName + "','" + menuViewId + "','" + dutyNumber +"','10')";
		System.out.println("新增中心领导岗位sql===" + sql);
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}

	// 新增局领导岗位
	public String addBigLeaderPost() {

		return "addBigLeaderPost";
	}

	public String saveBigLeaderPost() {
		String sql = "insert into whir$posttab (whir$posttab_id,whir$posttab_pname,whir$posttab_dno,whir$posttab_class) values(whir$posttab_SEQ.nextval,'"
				+ postName + "','" + menuViewId + "','100')";
		System.out.println("新增局领导岗位sql===" + sql);
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}

	// 更新岗位
	public String load() {

		return "load";
	}

	public String updatePost() {
		String postname = postName.replaceAll("\\s*|\t|\r|\n", "");
		String sql = sql = "update whir$posttab set whir$posttab_pname='"
				+ postname + "' where whir$posttab_id='" + postId + "'";
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}

	// 更新中心领导岗位
	public String loadLeader() {
		return "loadLeader";
	}

	public String updateLeaderPost() {
		String sql = "update whir$posttab set whir$posttab_pname='" + postName
				+ "',whir$posttab_dno='" + menuViewId
				+ "' where whir$posttab_id='" + postId + "'";
		new WatchArrangeUtils().executeSql(sql);
		printResult("success");
		return null;
	}

	// 更新局领导岗位
	public String loadBigLeader() {
		return "loadBigLeader";
	}

	// 删除岗位
	public String delPost() {
		List dsqlList = new ArrayList();
		String sql = "delete whir$posttab where whir$posttab_id='" + postId
				+ "'";
		dsqlList.add(sql);
		sql = "delete whir$postemptab where whir$postemptab_postno='" + postId
				+ "'";
		dsqlList.add(sql);
		new WatchArrangeUtils().executeBatchSql(dsqlList);
		printResult("success");
		return null;
	}

	/**
	 * 换班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String changeShifts() throws Exception {
		String infoIds = this.request.getParameter("infoIds");
		String menuName = this.request.getParameter("menuName");
		if ("局值班表".equals(menuName)) {
			List<Map<String, String>> list = getInfochangeShifts(infoIds);
			Map<String, String> map = list.get(0);
			Map<String, String> map1 = list.get(1);

			for (int i = 0; i < list.size(); i++) {
				String id = "";
				String dutyStaff = "";
				if (i == 0) {
					id = map.get("id");
					dutyStaff = map1.get("dutyStaff");
				} else {
					id = map1.get("id");
					dutyStaff = map.get("dutyStaff");
				}
				String sql = "UPDATE WHIR$DUTYROSTER SET WHIR$DUTYROSTER_DUTYSTAFF=? WHERE WHIR$DUTYROSTER_ID=?";
				changeShiftsSql(sql, id, dutyStaff);
			}
			printResult("success");
		} else {
			List<Map<String, String>> list = getInfochangeCenterShifts(infoIds);
			Map<String, String> map = list.get(0);
			Map<String, String> map1 = list.get(1);

			for (int i = 0; i < list.size(); i++) {
				String id = "";
				String dutyStaff = "";
				String department = "";
				if (i == 0) {
					id = map.get("id");
					dutyStaff = map1.get("dutyStaff");
					department = map.get("department");
				} else {
					id = map1.get("id");
					dutyStaff = map.get("dutyStaff");
					department = map.get("department");
				}
				String sql = "UPDATE WHIR$CENTREDUTY SET WHIR$CENTREDUTY_DUTYSTAFF=?,WHIR$CENTREDUTY_DEPARTMENT=? WHERE WHIR$CENTREDUTY_ID=?";
				changeCenterShiftsSql(sql, id, dutyStaff, department);
			}
			printResult("success");
		}
		return null;
	}

	/**
	 * 替班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String relayShifts() throws Exception {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String infoId = this.request.getParameter("infoId");
		String menuName = this.request.getParameter("menuName");
		if ("局值班表".equals(menuName)) {
			relayShiftsSql(userId, infoId,"0");
			printResult("success");
		} else {
			relayShiftsSql(userId, infoId,"1");
			printResult("success");
		}
		return null;
	}

	/**
	 * 微信新改版后局领导替班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean relayShiftsSql(String userId,String infoId,String flag) throws Exception {
		DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {  
	    	conn = dsb.getDataSource().getConnection();
	        conn.setAutoCommit(false);
	        String sql = "";
	        if("0".equals(flag)){
	        	sql ="UPDATE whir$dutyroster SET whir$dutyroster_dutystaff = (select empname from org_employee where emp_id=?) where whir$dutyroster_id=?";
	        } else {
	        	sql ="UPDATE WHIR$centreduty SET WHIR$centreduty_dutystaff = (select empname from org_employee where emp_id=?) where WHIR$centreduty_id=?";
	        }
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setInt(1, Integer.parseInt(userId));
	        pstmt.setString(2, infoId);
	        pstmt.executeUpdate();
	        conn.commit(); 
	    } catch (Exception e) {
	    	conn.rollback();
	        System.out.println("error message:" + e.getMessage());
	    }finally{
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
	    return true;
	}
	// -------------------------------------微信端开发---------------------------------------------//
	/**
	 * 微信局领导换班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean setChangeLeaderDuty(String chestrs) throws Exception {
		StringBuffer sb = new StringBuffer();
		String[] chestrsArray = chestrs.split(",");
		for (String str : chestrsArray) {
			sb.append("'").append(str).append("',");
		}
		String inforId = sb.toString();
		List<Map<String, String>> list = getInfochangeShifts(inforId.substring(
				0, inforId.length() - 1));
		Map<String, String> map = list.get(0);
		Map<String, String> map1 = list.get(1);

		for (int i = 0; i < list.size(); i++) {
			String id = "";
			String dutyStaff = "";
			if (i == 0) {
				id = map.get("id");
				dutyStaff = map1.get("dutyStaff");
			} else {
				id = map1.get("id");
				dutyStaff = map.get("dutyStaff");
			}
			String sql = "UPDATE WHIR$DUTYROSTER SET WHIR$DUTYROSTER_DUTYSTAFF=? WHERE WHIR$DUTYROSTER_ID=?";
			changeShiftsSql(sql, id, dutyStaff);
		}
		return true;
	}

	/**
	 * 微信中心领导换班
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Boolean setChangeCenterLeaderDuty(String chestrs) throws Exception {
		StringBuffer sb = new StringBuffer();
		String[] chestrsArray = chestrs.split(",");
		for (String str : chestrsArray) {
			sb.append("'").append(str).append("',");
		}
		String inforId = sb.toString();
		List<Map<String, String>> list = getInfochangeCenterShifts(inforId
				.substring(0, inforId.length() - 1));
		Map<String, String> map = list.get(0);
		Map<String, String> map1 = list.get(1);

		for (int i = 0; i < list.size(); i++) {
			String id = "";
			String dutyStaff = "";
			String department = "";
			if (i == 0) {
				id = map.get("id");
				dutyStaff = map1.get("dutyStaff");
				department = map.get("department");
			} else {
				id = map1.get("id");
				dutyStaff = map.get("dutyStaff");
				department = map.get("department");
			}
			String sql = "UPDATE WHIR$CENTREDUTY SET WHIR$CENTREDUTY_DUTYSTAFF=?,WHIR$CENTREDUTY_DEPARTMENT=? WHERE WHIR$CENTREDUTY_ID=?";
			changeCenterShiftsSql(sql, id, dutyStaff, department);
		}
		return true;
	}

	public List<Map<String, String>> getInfochangeShifts(String infoIds)
			throws SQLException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			String sql = "SELECT WHIR$DUTYROSTER_ID,WHIR$DUTYROSTER_DUTYSTAFF FROM WHIR$DUTYROSTER WHERE WHIR$DUTYROSTER_ID IN ("
					+ infoIds + ")";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				String id = rs.getString("WHIR$DUTYROSTER_ID");
				String dutyStaff = rs.getString("WHIR$DUTYROSTER_DUTYSTAFF");
				map.put("id", id);
				map.put("dutyStaff", dutyStaff);
				list.add(map);
			}
		} catch (Exception e) {
			conn.rollback();
			System.out.println("error message11:" + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// log
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
		return list;
	}

	public List<Map<String, String>> getInfochangeCenterShifts(String infoIds)
			throws SQLException {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			String sql = "SELECT WHIR$CENTREDUTY_ID,WHIR$CENTREDUTY_DUTYSTAFF,WHIR$CENTREDUTY_DEPARTMENT FROM WHIR$CENTREDUTY WHERE WHIR$CENTREDUTY_ID IN ("
					+ infoIds + ")";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Map<String, String> map = new HashMap<String, String>();
				String id = rs.getString("WHIR$CENTREDUTY_ID");
				String dutyStaff = rs.getString("WHIR$CENTREDUTY_DUTYSTAFF");
				String department = rs.getString("WHIR$CENTREDUTY_DEPARTMENT");
				map.put("id", id);
				map.put("dutyStaff", dutyStaff);
				map.put("department", department);
				list.add(map);
			}
		} catch (Exception e) {
			conn.rollback();
			System.out.println("error message11:" + e.getMessage());
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					// log
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
		return list;
	}

	public void changeShiftsSql(String sql, String id, String dutyStaff)
			throws SQLException {
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dutyStaff);
			pstmt.setInt(2, Integer.parseInt(id));
			pstmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			System.out.println("error message:" + e.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
	}

	public void changeCenterShiftsSql(String sql, String id, String dutyStaff,
			String department) throws SQLException {
		DataSourceBase dsb = new DataSourceBase();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = dsb.getDataSource().getConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dutyStaff);
			pstmt.setString(2, department);
			pstmt.setInt(3, Integer.parseInt(id));
			pstmt.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			System.out.println("error message:" + e.getMessage());
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (Exception e) {

				}
			}
		}
	}
}
