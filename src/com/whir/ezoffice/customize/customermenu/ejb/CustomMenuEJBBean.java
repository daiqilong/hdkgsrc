package com.whir.ezoffice.customize.customermenu.ejb;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.CreateException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.whir.common.db.NamedParameterStatement;
import com.whir.common.hibernate.HibernateBase;
import com.whir.common.util.StringSplit;

import java.util.*;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import com.whir.ezoffice.budget.po.BudgetCostPO;
import com.whir.ezoffice.budget.po.BudgetCostWorkFlowInfoPO;
import com.whir.ezoffice.budget.po.BudgetCostWorkFlowPO;
import com.whir.ezoffice.budget.po.BudgetSectionPO;
import com.whir.ezoffice.budget.po.BudgetSubjectPO;
import com.whir.ezoffice.customize.customermenu.bd.CustomerMenuDB;
import com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO;
import com.whir.ezoffice.customize.customermenu.po.CustomerMenuQLCasePO;
import com.whir.ezoffice.customize.customermenu.po.CustomerMenuButtonPO;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import com.whir.ezoffice.customize.customermenu.po.CustomerFieldControlPO;
import java.sql.ResultSet;
import com.whir.org.vo.organizationmanager.DomainVO;
import com.whir.org.bd.organizationmanager.OrganizationBD;

public class CustomMenuEJBBean extends HibernateBase implements SessionBean {
	SessionContext sessionContext;
	private static Logger logger = Logger.getLogger(CustomMenuEJBBean.class.getName());
	public void ejbCreate() throws CreateException {
	}

	public void ejbRemove() {
	}

	public void ejbActivate() {
	}

	public void ejbPassivate() {
	}

	public void setSessionContext(SessionContext sessionContext) {
		this.sessionContext = sessionContext;
	}

	/**
	 * 提取运营域中某个左侧菜单的所有自定义菜单对象
	 * 
	 * @param domainId
	 * @param menuId
	 *            左侧菜单id
	 * @param show
	 * @return
	 * @throws HibernateException
	 */
	public List getAllCustomMenu(String domainId, String menuId, String show,
			String curUserId, String orgIdString) throws HibernateException {

		String Sql = "select po.id,po.menuName,po.menuBlone,po.menuLevel,po.menuAction,po.menuActionParams1,"
				// 0 1 2 3 4 5
				+ "po.menuActionParams2,po.menuActionParams3, po.menuActionParams4, po.menuActionParams4Value,"
				// 6 7 8 9
				+ "po.menuListTableMap,po.menuMaintenanceTableMap,po.menuStartFlow,po.menuFileLink,po.menuHtmlLink,"
				// 10 11 12 13 14
				+ "po.menuAccess, po.menuScope, po.menuMaintenanceTableName, po.menuOpenStyle, po.menuIsValide,"
				// 15 16 17 18 19
				+ "po.menuViewUser, po.menuViewOrg, po.menuViewGroup,po.menuCount,po.menuCodeSet,po.menuLevelSet "
				// 20 21 22 23 24 25
				+ " from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.domainId ="
				+ domainId;

		// 取隐藏或者显示的模块
		if (show != null && !"".equals(show)) {
			Sql += " and po.menuIsValide=" + show;
		}
		if (menuId != null && menuId.length() > 0) {
			Sql += " and po.menuMaintenanceSubTableMap = " + menuId
					+ " and po.id <> " + menuId;
		}
		String _sql = "";
		// 管理员权限
		if ("sysManager".equals(orgIdString)) {
			_sql = " 1=1 ";
		} else {

			_sql = getRightSql(curUserId, orgIdString, domainId,
					"po.menuViewUser", "po.menuViewOrg", "po.menuViewGroup");
		}
		Sql = Sql + " and (" + _sql + ") order by po.menuLevel asc";

		// System.out.println("@@@@@@@@@@@@@@@@@@@==getAllCustomMenu--@@@@@@@@@@@@@@@@@@@");
		// System.out.println("Sql:" + Sql);
		List list = null;
		begin();
		try {
			list = session.createQuery(Sql).list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return list;
	}

	/**
	 * 获取模块的子模块
	 * 
	 * @param domainId
	 * @param menuId
	 * @param curUserId
	 * @param orgId
	 * @return
	 * @throws HibernateException
	 */
	public List getSubMenusById(String domainId, String menuId,
			String curUserId, String orgIdString) throws HibernateException {
		String _sql = getRightSql(curUserId, orgIdString, domainId,
				"po.menuViewUser", "po.menuViewOrg", "po.menuViewGroup");

		String Sql = "select po.id,po.menuName "
				+ " from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.domainId ="
				+ domainId + " and po.menuBlone = " + menuId + " and (" + _sql
				+ ") order by po.menuLevel asc";
		// System.out.println("------Sql:" + Sql);
		List list = null;
		begin();
		try {
			list = session.createQuery(Sql).list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return list;
	}

	/**
	 * 获取栏目菜单
	 * 
	 * @param userId
	 * @param orgIdString
	 * @param domainId
	 * @return
	 * @throws Exception
	 */
	public List getAllUserTopMenu(String userId, String orgIdString,
			String domainId) throws Exception {

		List list = null;
		String _sql = getRightSql(userId, orgIdString, domainId,
				"po.menuViewUser", "po.menuViewOrg", "po.menuViewGroup");
		begin();
		try {
			DomainVO domainVO = (DomainVO) session.load(DomainVO.class, Long
					.valueOf(domainId));
			// 取当前用户的所有上级组织ID
			StringBuffer buffer = new StringBuffer();
			buffer
					.append(
							"("
									+ _sql
									+ ") and po.menuLevelSet=0 and po.inUseSet=1 and po.domainId=")
					.append(domainId);
			buffer
					.append(" and (po.isSystemInitSet=0 or (po.isSystemInitSet=1 and ");
			String databaseType = com.whir.common.config.SystemCommon
					.getDatabaseType();
			if (databaseType.indexOf("mysql") >= 0) {
				buffer.append("'").append(domainVO.getModule()).append(
						"' like concat('%', po.menuCodeSet, '%')");
			} else if (databaseType.indexOf("db2") >= 0) {
				buffer.append("locate(po.menuCode,'").append(
						domainVO.getModule()).append("')>0");
			} else {
				buffer
						.append("'")
						.append(domainVO.getModule())
						.append(
								"' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%',po.menuCodeSet),'%')");
			}
			buffer.append("))");
			// System.out.println("@@@@@@@@@buffer.toString():"+buffer.toString());
			list = session
					.createQuery(
							"select po.menuName, po.leftURLSet, po.rightURLSet,po.isSystemInitSet,po.id,"
									+
									// 0 1 2 3 4
									"po.menuURLSet,po.menuCodeSet,po.menuOpenStyle,po.menuActionParams1 "
									+
									// 5 6 7 8
									" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where "
									+ buffer.toString()
									+ " order by po.menuOrderSet").list();
		} catch (Exception ex) {
			System.out.println("---------------------------------------------");
			ex.printStackTrace();
			System.out.println("---------------------------------------------");
			throw ex;
		} finally {
			session.close();
		}
		return list;
	}

	/**
	 * 返回可以显示的菜单
	 * 
	 * @param menuCode
	 *            String 菜单代码 workflow,information
	 * @param domainId
	 *            String 单位代码
	 * @throws Exception
	 * @return String
	 */
	public String getShowMenu(String menuCode, String domainId)
			throws Exception {
		StringBuffer buffer = new StringBuffer(",");
		begin();
		try {
			Connection conn = session.connection();
			DatabaseMetaData dbmd = conn.getMetaData();
			String databaseName = dbmd.getDatabaseProductName();
			//
			List list = null;
			String databaseType = com.whir.common.config.SystemCommon
					.getDatabaseType();
			if (databaseType.indexOf("db2") >= 0) {
				list = session
						.createQuery(
								"select po.menuCodeSet from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR(',', po.menuCodeSet), ','),'"
										+ menuCode
										+ "' )>0  and po.inUseSet=1 and po.domainId="
										+ domainId).list();
			} else if (databaseType.indexOf("mysql") >= 0) {
				list = session
						.createQuery(
								"select po.menuCodeSet from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where '"
										+ menuCode
										+ "' like concat('%,', po.menuCodeSet,',%')  and po.inUseSet=1 and po.domainId=0"
										+ domainId).list();
			} else {
				list = session
						.createQuery(
								"select po.menuCodeSet from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where '"
										+ menuCode
										+ "' like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%,', po.menuCodeSet), ',%')  and po.inUseSet=1 and po.domainId="
										+ domainId).list();
			}
			for (int i = 0; i < list.size(); i++) {
				buffer.append(list.get(i).toString()).append(",");
			}
			session.close();
		} catch (Exception ex) {
			if (session != null) {
				session.close();
			}
			throw ex;
		}
		return buffer.toString();
	}

	/**
	 * 设置菜单显示/隐藏
	 * 
	 * @param menuId
	 * @param menuLevel
	 * @param menuLocation
	 * @param menuCount
	 * @param domainId
	 * @param show
	 * @return
	 * @throws Exception
	 */
	public String setMenuDisplay(String menuId, String menuLevel,
			String domainId, Integer show) throws Exception {

		// System.out.println("---------------------setMenuDisplay--------------------");
		begin();
		try {
			List list = session
					.createQuery(
							"select po from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po "
									+ " where po.domainId ="
									+ domainId
									+ " and po.id = "
									+ menuId
									+ " or po.menuLevel like '%"
									+ menuLevel
									+ "%'").list();

			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					CustomerMenuConfigerPO po = (CustomerMenuConfigerPO) list
							.get(i);
					po.setInUseSet(show);
					po.setMenuIsValide(show.intValue());
					session.update(po);

				}
			}
			session.flush();

		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return null;
	}

	/**
	 * 删除菜单以及相关信息
	 * 
	 * @param domainId
	 * @param menuId
	 * @param menuLevel
	 * @return
	 * @throws Exception
	 */
	public boolean delBatchCustmizeMenus(String domainId, String menuId,
			String menuLevel) throws Exception {
		boolean flag = false;
		DbOpt opt = null;
		CustomerMenuConfigerPO po = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			List list = session.createQuery(
					" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po "
							+ " where (po.id=" + menuId
							+ " or po.menuLevel like '%" + menuLevel
							+ "%') and po.domainId =" + domainId).list();

			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					po = (CustomerMenuConfigerPO) list.get(i);
					session
							.delete(" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po "
									+ " where po.id=" + po.getId());
					// 删除自定义按钮
					session
							.delete(" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuButtonPO po "
									+ " where po.menuID =" + po.getId());
					// 删除字段设置
					session
							.delete(" from com.whir.ezoffice.customize.customermenu.po.CustomerFieldControlPO po "
									+ " where po.menuId =" + po.getId());
					// 删除权限
					if (po.getMenuCount() == 8 || po.getMenuCount() == 0) {
						String sql = " delete from org_role_right where right_id in (select right_id from org_right where rightCode like '%-"
								+ po.getId() + "%')";
						stat.execute(sql);
						sql = " delete from org_rightscope where right_id in (select right_id from org_right where rightCode like '%-"
								+ po.getId() + "%')";
						stat.execute(sql);
						sql = " delete from ORG_ROLE_OGP_RIGHTSCOPE where right_id in (select right_id from org_right where rightCode like '%-"
								+ po.getId() + "%')";
						stat.execute(sql);
						sql = " delete from ORG_RIGHT where rightCode like '%-"
								+ po.getId() + "%'";
						stat.execute(sql);
					}
				}
			}
			session.flush();
			flag = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return flag;
	}

	/**
	 * GET THE WORK FLOW LIST
	 * 
	 * @param domainId
	 *            String
	 * @throws HibernateException
	 * @return List
	 */
	public List getWFProcesses(String domainId) throws HibernateException {
		List list = null;
		begin();
		try {
			String hSql = " select bbb.wfPackageId, bbb.packageName, aaa.wfWorkFlowProcessId, aaa.workFlowProcessName,"
					+ " aaa.accessDatabaseId, aaa.processType,  aaa.remindField "
					+ "from com.whir.ezoffice.workflow.po.WFWorkFlowProcessPO  aaa join aaa.wfPackage bbb "
					+ " where aaa.isPublish=1 and bbb.moduleId=1 and aaa.domainId = "
					+ domainId + " order by bbb.wfPackageId ";
			list = session.createQuery(hSql).list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return list;
	}

	/**
	 * 提取运营域中的所有指定自定义菜单项子菜单对象
	 * 
	 * @param domainId
	 *            String
	 * @return List
	 */
	public List getAllSubMenus(String domainId, String menuId)
			throws HibernateException {
		Long doId = Long.valueOf(domainId);
		List list = null;
		begin();
		try {
			list = session
					.createQuery(
							"select po.id, po.menuName, po.menuBlone, po.menuLevel,po.createdEmp,po.createdOrg "
									+ "from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.domainId ="
									+ doId
									+ " and po.menuBlone = "
									+ menuId
									+ " and po.id<>"
									+ menuId
									+ " order by po.menuLevel ").list();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return list;
	}

	/**
	 * 获取用户所在群组
	 * 
	 * @param userId
	 * @param domainId
	 * @return
	 * @throws HibernateException
	 */
	public List getAllGroupsByUserId(String userId, String domainId)
			throws HibernateException {
		List list = null;
		begin();
		try {
			list = session
					.createQuery(
							"select po.groupId,po.groupName "
									+ " from com.whir.org.vo.groupmanager.GroupVO po join po.employees emp "
									+ " where emp.empId=" + userId
									+ " and po.domainId = " + domainId).list();
		} catch (HibernateException ex) {
			throw ex;
		} finally {
			session.close();
		}
		return list;
	}

	/**
	 * 获取判断权限的sql
	 * 
	 * @param curUserId
	 * @param orgIdString
	 * @param domainId
	 * @param menuViewUser
	 * @param menuViewOrg
	 * @param menuViewGroup
	 * @return
	 */
	private String getRightSql(String curUserId, String orgIdString,
			String domainId, String menuViewUser, String menuViewOrg,
			String menuViewGroup) throws HibernateException {

		StringBuffer buffer = new StringBuffer("");
		List groupIds = getAllGroupsByUserId(curUserId, domainId);
		for (int i = 0; i < groupIds.size(); i++) {
			Object[] obj = (Object[]) groupIds.get(i);
			buffer.append(menuViewGroup + " like '%*").append(obj[0]).append(
					"*%' or ");
		}
		for (int i = 0; i < groupIds.size(); i++) {
			Object[] obj = (Object[]) groupIds.get(i);
			buffer.append(menuViewGroup + " like '%@").append(obj[0]).append(
					"@%' or ");
		}
		String[] orgIdArray = ("$" + orgIdString + "$").split("\\$\\$");
		for (int i = 0; i < orgIdArray.length; i++) {
			if (!"".equals(orgIdArray[i]))
				buffer.append(menuViewOrg + " like '%*").append(orgIdArray[i])
						.append("*%' or ");
		}
		// 加兼职组织
		List _list = getSidelineOrgList(curUserId);
		if (_list != null) {
			for (int kk = 0; kk < _list.size(); kk++) {
				String _orgIdString = _list.get(kk) + "";
				String[] _orgIdArray = ("$" + _orgIdString + "$")
						.split("\\$\\$");
				for (int i = 0; i < _orgIdArray.length; i++) {
					if (!"".equals(_orgIdArray[i]))
						buffer.append(menuViewOrg + " like '%*").append(
								_orgIdArray[i]).append("*%' or ");
				}

			}
		}
		// 加兼职组织
		buffer.append(menuViewUser + " like '%$").append(curUserId).append(
				"$%' or ");
		buffer.append("((" + menuViewUser + " is null or " + menuViewUser
				+ "='') and ( " + menuViewOrg + " is null or " + menuViewOrg
				+ "='') and (" + menuViewGroup + " is null or " + menuViewGroup
				+ "='')) ");

		return buffer.toString();

	}

	/**
	 * 取兼职
	 * 
	 * @param userId
	 * @return
	 */
	private List getSidelineOrgList(String userId) {

		List result = new ArrayList();
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;

		try {

			dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
			String Sql = "select sidelineorg  from org_employee where emp_id="
					+ userId;
			String SidelineOrg = dbopt.executeQueryToStr(Sql);

			if (SidelineOrg != null && !"".equals(SidelineOrg)
					&& !"null".equals(SidelineOrg)) {

				SidelineOrg = SidelineOrg.substring(1, SidelineOrg.length())
						+ "*";
				String[] SidelineOrgs = SidelineOrg.split("\\*\\*");

				for (int i = 0; i < SidelineOrgs.length; i++) {

					String orgidstring = dbopt
							.executeQueryToStr("select orgidstring from org_organization where org_id="
									+ SidelineOrgs[i]);
					orgidstring = StringSplit.splitOrgIdString(orgidstring,
							"$", "_");
					result.add(orgidstring);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbopt.close();
			} catch (SQLException ex) {
			}
			return result;
		}

	}

	/**
	 * 获取方案
	 * 
	 * @param listCaseId
	 * @param domainId
	 * @return
	 * @throws Exception
	 */
	public String[][] getListField(String listCaseId, String domainId)
			throws Exception {
		String[][] retList = null;
		DbOpt dbopt = null;
		begin();
		try {
			List list = session
					.createQuery(
							" select po.id, po.qlFields from com.whir.ezoffice.customize.customermenu.po.CustomerMenuQLCasePO po "
									+ " where  po.id = "
									+ listCaseId
									+ " and po.domainId = " + domainId).list();
			if (list != null && list.size() > 0) {
				Object[] obj = (Object[]) list.get(0);
				dbopt = new DbOpt();
				String sql = "select a.field_id,a.field_desname,a.field_name,a.field_width,a.field_show,a.field_value,field_type from tfield a where a.field_id in ("
						+ obj[1]
						+ ") and a.DOMAIN_ID="
						+ domainId
						+ " order by  field_sequence asc ";			 
				// System.out.println("1111111111111111111111111111111111111111111");
				// System.out.println("sql="+sql);
				retList = dbopt.executeQueryToStrArr2(sql, 7);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			dbopt.close();
			session.close();
			transaction = null;
			session = null;

		}
		return retList;

	}
	
	
	
	/**
	 * 获取方案(自定义门户的)
	 * 
	 * @param listCaseId
	 * @param domainId
	 * @return
	 * @throws Exception
	 */
	public String[][] getListField_portal(String listCaseId, String domainId)
			throws Exception {
		String[][] retList = null;
		DbOpt dbopt = null;
		begin();
		try {
			List list = session
					.createQuery(
							" select po.id, po.qlFields from com.whir.ezoffice.customize.customermenu.po.CustomerMenuQLCasePO po "
									+ " where  po.id = "
									+ listCaseId
									+ " and po.domainId = " + domainId).list();
			if (list != null && list.size() > 0) {
				Object[] obj = (Object[]) list.get(0);
				dbopt = new DbOpt();
				String sql = "select a.field_id,a.field_desname,a.field_name,a.field_width,a.field_show,a.field_value,field_type from tfield a where a.field_id in ("
						+ obj[1]
						+ ") and a.DOMAIN_ID="
						+ domainId
						+ " order by a.field_sequence asc , a.field_id";
			 
				// System.out.println("1111111111111111111111111111111111111111111");
				// System.out.println("sql="+sql);
				retList = dbopt.executeQueryToStrArr2(sql, 7);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			dbopt.close();
			session.close();
			transaction = null;
			session = null;

		}
		return retList;

	}

	/**
	 * 权限判断
	 * 
	 * @param tblId
	 * @param orgIdString
	 * @param userId
	 * @param orgId
	 * @param menuId
	 * @return
	 * @throws HibernateException
	 */
	public String getViewScope2(String tblId, String orgIdString,
			String userId, String orgId, String menuId)
			throws HibernateException {
		String scope = null;
		begin();
		try {
			StringBuffer buffer = new StringBuffer();

			List list = session
					.createQuery(
							" select aaa.rightScopeType,aaa.rightScopeScope,aaa.rightScopeUser,aaa.rightScopeGroup  from com.whir.org.vo.rolemanager.RightScopeVO aaa  join aaa.right bbb join aaa.employee ccc "
									+ " where (bbb.rightCode = '99-"
									+ menuId
									+ "-03'  or  bbb.rightCode = '99-"
									+ menuId
									+ "-01')  and ccc.empId = " + userId)
					.list();

			if (list != null && list.size() > 0) {
				Object[] obj = (Object[]) list.get(0);
				String scopeType = obj[0].toString();
				if (!scopeType.equals("0")) {
					buffer.append("(");
					if (scopeType.equals("1")) {
						// 可以维护本人的数据
						buffer.append(tblId + "_OWNER = ").append(userId)
								.append(" or ");
					} else if (scopeType.equals("2")) {
						// 可以维护本组织及下级组织的数据
						// 取所有的下级组织
						List orgList = session
								.createQuery(
										"select org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where org.orgIdString like '%"
												+ orgId + "%'").list();
						for (int i = 0; i < orgList.size(); i++) {
							buffer.append(tblId + "_ORG = '").append(
									orgList.get(i)).append("' or ");
						}
						buffer.append(tblId + "_ORG = '").append(orgId).append(
								"' or ");
					} else if (scopeType.equals("3")) {
						// 可以维护本组织的数据
						buffer.append(tblId + "_ORG = '").append(orgId).append(
								"' or ");
					} else if (scopeType.equals("4")) {
						// 维护定义的范围
						String scopeScope = obj[1] == null ? "" : obj[1]
								.toString();
						if (!scopeScope.equals("")) {
							try {
								scopeScope = getJuniorOrg1(scopeScope);
							} catch (Exception e) {
							}
							buffer.append(tblId + "_ORG in (").append(
									scopeScope).append(") or ");

						}
						scopeScope = obj[2] == null ? "" : obj[2].toString();
						if (!scopeScope.equals("")) {
							try {
								scopeScope = getJuniorOrg2(scopeScope);
							} catch (Exception e) {
							}
							buffer.append(tblId + "_OWNER in (").append(
									scopeScope).append(") or ");
						}
					}
					scope = buffer.length() > 0 ? buffer.toString().substring(
							0, buffer.toString().lastIndexOf("or"))
							+ ")" : "";
				} else if (scopeType.equals("0")) {
					buffer.append(" 1=1 ");
					scope = buffer.toString();
				}
			} else {
				buffer.append("(");
				buffer.append(tblId + "_OWNER = ").append(userId);
				buffer.append(")");
				scope = buffer.toString();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;
		}
		return scope;
	}

	/**
	 * 权限判断(add by tianml 2015/11/16 用于设置导出的数据范围)
	 * 
	 * @param tblId
	 * @param orgIdString
	 * @param userId
	 * @param orgId
	 * @param menuId
	 * @return
	 * @throws HibernateException
	 */
	public String getViewScopeExport(String tblId, String orgIdString,
			String userId, String orgId, String menuId)
			throws HibernateException {
		String scope = null;
		begin();
		try {
			StringBuffer buffer = new StringBuffer();

			List list = session
					.createQuery(
							" select aaa.rightScopeType,aaa.rightScopeScope,aaa.rightScopeUser,aaa.rightScopeGroup  from com.whir.org.vo.rolemanager.RightScopeVO aaa  join aaa.right bbb join aaa.employee ccc "
									+ " where bbb.rightCode = '99-"
									+ menuId
									+ "-04' and ccc.empId = " + userId).list();

			if (list != null && list.size() > 0) {
				Object[] obj = (Object[]) list.get(0);
				String scopeType = obj[0].toString();
				if (!scopeType.equals("0")) {
					buffer.append("(");
					if (scopeType.equals("1")) {
						// 可以维护本人的数据
						buffer.append(tblId + "_OWNER = ").append(userId)
								.append(" or ");
					} else if (scopeType.equals("2")) {
						// 可以维护本组织及下级组织的数据
						// 取所有的下级组织
						List orgList = session
								.createQuery(
										"select org.orgId from com.whir.org.vo.organizationmanager.OrganizationVO org where org.orgIdString like '%"
												+ orgId + "%'").list();
						for (int i = 0; i < orgList.size(); i++) {
							buffer.append(tblId + "_ORG = '").append(
									orgList.get(i)).append("' or ");
						}
						buffer.append(tblId + "_ORG = '").append(orgId).append(
								"' or ");
						
						//11.5.0.0新加的兼职组织 2016/06/29
						List userList = session.createQuery("select e.empId from com.whir.org.vo.usermanager.EmployeeVO e where e.sidelineOrg like '%"+ orgId + "%'").list();
						for (int i = 0; i < userList.size(); i++) {
							buffer.append(tblId + "_OWNER = '").append(
									userList.get(i)).append("' or ");
						}
					} else if (scopeType.equals("3")) {
						// 可以维护本组织的数据
						buffer.append(tblId + "_ORG = '").append(orgId).append("' or ");
						
						//11.5.0.0新加的兼职组织 2016/06/29
						List userList = session.createQuery("select e.empId from com.whir.org.vo.usermanager.EmployeeVO e where e.sidelineOrg like '%"+ orgId + "%'").list();
						for (int i = 0; i < userList.size(); i++) {
							buffer.append(tblId + "_OWNER = '").append(
									userList.get(i)).append("' or ");
						}
						
					} else if (scopeType.equals("4")) {
						// 维护定义的范围
						String scopeScope = obj[1] == null ? "" : obj[1]
								.toString();
						if (!scopeScope.equals("")) {
							try {
								scopeScope = getJuniorOrg1(scopeScope);
							} catch (Exception e) {
							}
							buffer.append(tblId + "_ORG in (").append(
									scopeScope).append(") or ");

						}
						scopeScope = obj[2] == null ? "" : obj[2].toString();
						if (!scopeScope.equals("")) {
							try {
								scopeScope = getJuniorOrg2(scopeScope);
							} catch (Exception e) {
							}
							buffer.append(tblId + "_OWNER in (").append(
									scopeScope).append(") or ");
						}
					}
					scope = buffer.length() > 0 ? buffer.toString().substring(
							0, buffer.toString().lastIndexOf("or"))
							+ ")" : "";
				} else if (scopeType.equals("0")) {
					buffer.append(" 1=1 ");
					scope = buffer.toString();
				}
			} else {
				buffer.append("(");
				buffer.append(tblId + "_OWNER = ").append(userId);
				buffer.append(")");
				scope = buffer.toString();
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;
		}
		return scope;
	}

	private String getJuniorOrg1(String range) throws Exception {
		StringBuffer buffer = new StringBuffer(" where ");
		range = "*" + range + "*";
		String[] rangeArray = range.split("\\*\\*");
		int i = 0;
		for (i = 0; i < rangeArray.length; i++) {
			if (i > 0) {
				buffer.append(" or ");
			}
			buffer.append(" org.orgIdString like '%$");
			buffer.append(rangeArray[i]);
			buffer.append("$%' ");
		}
		List list = session.createQuery(
				"SELECT org.orgId FROM com.whir.org.vo.organizationmanager.OrganizationVO org "
						+ buffer.toString()).list();
		buffer = new StringBuffer();
		for (i = 0; i < list.size(); i++) {
			buffer.append("'" + list.get(i)).append("',");
		}
		range = buffer.toString();
		i = range.length();
		if (i > 0) {
			range = range.substring(0, i - 1);
		} else {
			range = "-1";
		}
		return range;
	}

	private String getJuniorOrg2(String range) throws Exception {

		range = "$" + range + "$";
		String[] rangeArray = range.split("\\$\\$");
		range = "";
		int i = 0;
		for (i = 1; i < rangeArray.length; i++) {
			range += rangeArray[i] + ",";
		}
		i = range.length();
		if (i > 0) {
			range = range.substring(0, i - 1);
		} else {
			range = "-1";
		}
		return range;
	}

	/**
	 * 保存自定义按钮
	 * 
	 * @param menuId
	 *            String
	 * @param alist
	 *            List
	 * @return Boolean
	 */
	public Boolean setCustButtons(String menuId, List alist)
			throws HibernateException {

		Boolean ret = null;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();

			String sql = " delete from oa_custmenu_custbutton where menuid = "
					+ menuId;
			stat.execute(sql);
			if (alist != null && alist.size() > 0) {
				for (int i = 0; i < alist.size(); i++) {
					CustomerMenuButtonPO po = (CustomerMenuButtonPO) alist
							.get(i);
					session.save(po);
				}
				session.flush();
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

	/**
	 * 删除自定义按钮
	 * 
	 * @param menuId
	 *            String
	 * @param alist
	 *            List
	 * @return Boolean
	 */
	public Boolean delCustButtons(String menuIds) throws HibernateException {

		Boolean ret = null;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			String sql = "";
			if (menuIds != null && !"".equals(menuIds)) {

				String[] menuIdss = menuIds.split(",");
				for (int i = 0; i < menuIdss.length; i++) {
					sql = "delete from oa_custmenu_custbutton where menuid = "
							+ menuIdss[i];
					stat.execute(sql);
				}
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

	/**
	 * 获取自定义按钮信息
	 * 
	 * @param menuId
	 *            String
	 * 
	 * @return List
	 */
	public List getCustButtons(String menuId) throws HibernateException {
		List retList = null;
		begin();
		try {
			retList = session
					.createQuery(
							" select po from com.whir.ezoffice.customize.customermenu.po.CustomerMenuButtonPO po "
									+ " where  po.menuID = "
									+ menuId
									+ " order by po.id ").list();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return retList;

	}

	/**
	 * 保存字段控制
	 * 
	 * @param menuId
	 *            String
	 * @param alist
	 *            List
	 * @return Boolean
	 */
	public Boolean setFieldControl(String menuId, List alist)
			throws HibernateException {

		Boolean ret = null;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();

			String sql = " delete from oa_custmenu_fieldcontrol where menuid = "
					+ menuId;
			stat.execute(sql);
			if (alist != null && alist.size() > 0) {
				for (int i = 0; i < alist.size(); i++) {

					CustomerFieldControlPO po = (CustomerFieldControlPO) alist
							.get(i);
					session.save(po);
				}
				session.flush();
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

	/**
	 * 删除字段控制
	 * 
	 * @param menuId
	 *            String
	 * @param alist
	 *            List
	 * @return Boolean
	 */
	public Boolean delFieldControls(String menuIds) throws HibernateException {

		Boolean ret = null;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			String sql = "";
			if (menuIds != null && !"".equals(menuIds)) {

				String[] menuIdss = menuIds.split(",");
				for (int i = 0; i < menuIdss.length; i++) {
					sql = "delete from oa_custmenu_fieldcontrol where menuid = "
							+ menuIdss[i];
					stat.execute(sql);
				}
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

	/**
	 * 获取字段控制信息
	 * 
	 * @param menuId
	 *            String
	 * @param controlType
	 *            String 字段控制类型：0:源字段 1：读字段 2：写字段 3：隐藏字段
	 * @return List
	 */
	public List getFieldControls(String menuId, String controlType)
			throws HibernateException {

		List retList = new ArrayList();
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();

			String sql = "select t.fieldid,t.fieldname,tt.field_desname,tt.field_type,tt.field_show "
					+ " from oa_custmenu_fieldcontrol t,tfield tt "
					+ " where t.fieldid=tt.field_id and t.MENUID="
					+ menuId
					+ " and t.controltype=" + controlType;

			ResultSet res = stat.executeQuery(sql);
			while (res.next()) {
				List _list = new ArrayList();
				_list.add(res.getString(1));
				_list.add(res.getString(2));
				_list.add(res.getString(3));
				_list.add(res.getString(4));
				_list.add(res.getString(5));
				retList.add(_list);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return retList;

	}

	/**
	 * 频道增加自定义模块接口
	 * 
	 * @param channelName
	 * @param menuscope
	 * @param channelReader
	 * @param menuvieworg
	 * @param menuviewgroup
	 * @param createdemp
	 * @param createdorg
	 * @param resultId
	 * @param leftURL
	 * @param rightURL
	 * @param domainId
	 * @param channelType
	 * @return
	 * @throws Exception
	 */
	public Long addUserChannel(String channelName, String menuscope,
			String channelReader, String menuvieworg, String menuviewgroup,
			String createdemp, String createdorg, String resultId,
			String leftURL, String rightURL, String domainId, String channelType)
			throws Exception {

		CustomerMenuConfigerPO configerPO = new CustomerMenuConfigerPO();
		configerPO.setMenuName(channelName);
		configerPO.setMenuBlone(new Long(0));
		configerPO.setMenuLocation(new Long(0));
		configerPO.setMenuListTableMap(new Long(0));
		configerPO.setMenuRefFlowStatue(new Integer(0));
		configerPO.setMenuMaintenanceSubTableMap(new Long(0));
		configerPO.setMenuOpenStyle(0);
		configerPO.setMenuIsValide(1);
		configerPO.setMenuMessageSend(0);
		configerPO.setDomainId(new Long(domainId));
		configerPO.setMenuLevel("0");
		configerPO.setMenuScope(menuscope);
		configerPO.setMenuCount(9);
		configerPO.setParentOrder(new Long(0));
		configerPO.setMenuViewUser(channelReader); // po.getChannelReader()
		configerPO.setMenuViewOrg(menuvieworg); // po.getChannelReadOrg()
		configerPO.setMenuViewGroup(menuviewgroup); // po.getChannelReadGroup()
		configerPO.setCreatedEmp(new Long(createdemp)); // po.getUserchannelIssueId()
		configerPO.setCreatedOrg(new Long(createdorg)); // po.getUserchannelIssueOrgId()
		configerPO.setMenuURLSet("");
		configerPO.setMenuLevelSet(new Long(0));
		configerPO.setMenuOrderSet(new Long(0));
		configerPO.setMenuIdStringSet(null);
		configerPO.setMenuParentSet("0");
		configerPO.setDeskTop1Set(new Long(0));
		configerPO.setDeskTop2Set(new Long(0));
		configerPO.setInUseSet(new Integer(1));
		configerPO.setIsSystemInitSet(new Integer(0));
		configerPO.setLeftURLSet(leftURL);
		configerPO.setRightURLSet(rightURL);
		configerPO.setMenuCodeSet(channelType + "Channel_" + resultId);

		Long res = saveMenuConfig(configerPO);
		return res;

	}

	/**
	 * 频道保存自定义模块
	 * 
	 * @param po
	 * @return
	 * @throws HibernateException
	 */
	private Long saveMenuConfig(CustomerMenuConfigerPO po)
			throws HibernateException {
		// boolean retFlg = false;
		Long menuId = null;
		begin();
		try {
			menuId = (Long) session.save(po);
			if ("0".equals(po.getMenuLevel())) {
				po.setMenuLevel("0-" + menuId);
				po.setMenuMaintenanceSubTableMap(menuId);
			}
			session.flush();
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return menuId;
	}

	/**
	 * 频道修改自定义模块接口
	 * 
	 * @param channelId
	 * @param channelName
	 * @param menuScope
	 * @param channelReader
	 * @param channelReadOrg
	 * @param channelReadGroup
	 * @param channelType
	 * @return
	 * @throws Exception
	 */
	public boolean updateUserChannel(String channelId, String channelName,
			String menuScope, String channelReader, String channelReadOrg,
			String channelReadGroup, String channelType) throws Exception {

		boolean result = false;
		begin();
		try {
			List list = session
					.createQuery(
							"select po.id from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where  po.menuCodeSet = '"
									+ channelType
									+ "Channel_"
									+ channelId
									+ "' ").list();
			String configerId = "";
			if (list != null && list.size() > 0) {
				Object obj = (Object) list.get(0);
				configerId = obj + "";
			}
			if (!"".equals(configerId)) {
				CustomerMenuConfigerPO modiPO = (CustomerMenuConfigerPO) session
						.load(CustomerMenuConfigerPO.class,
								new Long(configerId));
				modiPO.setMenuName(channelName);
				modiPO.setMenuScope(menuScope);
				modiPO.setMenuViewUser(channelReader);
				modiPO.setMenuViewOrg(channelReadOrg);
				modiPO.setMenuViewGroup(channelReadGroup);

				session.update(modiPO);
				session.flush();
				result = true;
			}

		} catch (Exception ex) {
			// System.out.println("---------------------------------------------");
			ex.printStackTrace();
			// System.out.println("---------------------------------------------");
			throw ex;
		} finally {
			transaction = null;
			session.close();
		}
		return result;

	}

	/**
	 * 频道删除自定义模块接口
	 * 
	 * @param domainId
	 * @param channelId
	 * @param channelType
	 * @return
	 * @throws HibernateException
	 */
	public boolean delChannelCustmizeMenus(String domainId, String[] channelId,
			String[] channelType) throws HibernateException {
		boolean flag = false;
		begin();
		try {
			if (channelId != null) {
				String userchanneltResultId = "";
				for (int i = 0; i < channelId.length; i++) {

					userchanneltResultId = channelType[i] + "Channel_"
							+ channelId[i];
					session
							.delete(" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.domainId ="
									+ domainId
									+ " and po.menuCodeSet = '"
									+ userchanneltResultId + "'");

				}
			}
			session.flush();
			flag = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			transaction = null;
			session = null;

		}
		return flag;
	}

	/**
	 * 获取字段信息
	 * 
	 * @param fieldId
	 * @return
	 * @throws HibernateException
	 */
	public List getFieldInfoById(String fieldId) throws HibernateException {

		List retList = new ArrayList();
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			String sql = "select field_desname,field_name,field_width,field_show,"
					+ "field_value,field_type,field_len,field_id "
					+ " from tfield where field_id=" + fieldId;
			ResultSet res = stat.executeQuery(sql);
			if (res.next()) {
				retList.add(res.getString(1));
				retList.add(res.getString(2));
				retList.add(res.getString(3));
				retList.add(res.getString(4));
				retList.add(res.getString(5));
				retList.add(res.getString(6));
				retList.add(res.getString(7));
				retList.add(res.getString(8));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return retList;

	}

	/**
	 * 保存列表字段
	 * 
	 * @param menuId
	 *            String
	 * @param alist
	 *            List
	 * @return Boolean
	 */
	public Boolean saveListFields(String tabname, String field,
			String fieldtype, String[] ids, String[] value)
			throws HibernateException {

		String tabIdName = tabname + "_id";

		Boolean ret = null;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			for (int i = 0; i < ids.length; i++) {
				String sql = "";
				if ("1000002".equals(fieldtype)) {
					sql = "update " + tabname + " set " + field + "='"
							+ value[i] + "' where " + tabIdName + "=" + ids[i];
				} else {
					sql = "update "
							+ tabname
							+ " set "
							+ field
							+ "="
							+ ((value[i] == null || "null".equals(value[i]) || ""
									.equals(value[i])) ? "0" : value[i])
							+ " where " + tabIdName + "=" + ids[i];
				}
				stat.execute(sql);
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

	/**
	 * 删除数据(列表)
	 * 
	 * @param tableName
	 * @param recordId
	 * @return
	 * @throws Exception
	 */
	public boolean deleteBizDatas(String tableName, String recordId)
			throws Exception {
		boolean flag = false;
		DbOpt dbopt = null;
		String[] recordIds = recordId.split(",");
		try {
			dbopt = new DbOpt();

			Statement stat = dbopt.getStatement();
			String delStr = "";
			if (recordIds != null) {
				for (int i = 0; i < recordIds.length; i++) {
					delStr = "delete from " + tableName + " where " + tableName
							+ "_id=" + recordIds[i];
					stat.addBatch(delStr);
					stat.executeBatch();
				}
			}

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			dbopt.close();
		}
		return flag;
	}

	/**
	 * 获取符合条件的id串   2016-01-26
	 * @param name
	 * @param menuLevel
	 * @param menuLevelSet
	 * @return
	 * @throws HibernateException
	 */
	public String getMobileId(String name,String menuLevel,String menuLevelSet) throws HibernateException {
		String ids="";		 
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();			 
			String sql = "select distinct a.id from oa_custmenu a ,oa_custmenu b where b.menu_name like '%"+name+"%' and b.menu_level like  ezoffice.fn_linkchar(ezoffice.fn_linkchar('%',a.menu_level),'%') ";					 
			//logger.debug("getMobileId***********sql:"+sql);
			ResultSet res = stat.executeQuery(sql);
			while (res.next()) {
				ids=ids+res.getString(1)+",";			 
			}
			//logger.debug("getMobileId***********ids:"+ids);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		if(StringUtils.isNotEmpty(ids)){
			ids=ids.substring(0, ids.length()-1);				
		}
		return ids;

	}
/**
 * 在办理查阅的时候删除流程记录的同时 删除自定义模块的表单记录  2016/03/30  11.5.0需求
 * @param ids
 * @return
 * @throws Exception
 */
	public boolean deleteWorkLogByIds(String ids)throws Exception {
		boolean ret = false;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			String sql = "";
			if (ids != null && !"".equals(ids)) {
				String[] menuIdss = ids.split(",");
				for (int i = 0; i < menuIdss.length; i++) {				 
					sql = "delete from EZ_FLOW_HI_PROCINST hip where hip.id_ = "+ menuIdss[i];
					stat.execute(sql);
				}
			}

			ret = new Boolean(true);

		} catch (Exception ex) {
			ex.printStackTrace();
			ret = new Boolean(false);
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return ret;
	}

    /**
     * 在办理查阅中删除了流程记录，自定义模块关联的数据表记录也要删除，包括主表记录和子表记录
     * @param ids
     * @param tableName
     * @return
     * @throws Exception
     */
	public boolean deleteWorkLogByIds(String ids,String tableName) throws Exception {
		boolean result = false;
		DbOpt opt = null;
		begin();
		try {
			opt = new DbOpt();
			Statement stat = opt.getStatement();
			String str="";
			String sqlS="select business_key_ from EZ_FLOW_HI_PROCINST where whir_isdeleted=0 and id_ in("+ids+") ";
			//logger.debug("------------------sqlS:"+sqlS);
			ResultSet res = stat.executeQuery(sqlS);
			while (res.next()) {
				str=str+res.getString("business_key_")+",";			 
			}
			if(StringUtils.isNotEmpty(str)){
				str=str.substring(0, str.length()-1);				
			}
			String delSql="delete from "+tableName+" where "+tableName+"_id in("+str+")";
			result=stat.execute(delSql);			 

		} catch (Exception e) {			 
			e.printStackTrace();			 
			throw e;
		} finally {
			try {
				opt.close();
			} catch (SQLException ex1) {
			}
			session.close();
		}
		return result;
	}
	/**
	 * 获取新自定义表单的字段联动列表  11.5需求  2016/04/11
	 * @param formCode
	 * @return
	 * @throws Exception
	 */
	 public String[][] getRelaTrigByFormId(String formId) throws Exception {
		    DbOpt dbopt = null;
		    String[][] result = null;

		    String querySql = "";
		    try {
		      dbopt = new DbOpt();

		      querySql = querySql + "select ";
		      querySql = querySql + " a.setting_id,";
		      querySql = querySql + " a.name";

		      querySql = querySql + " from ez_form_s_setting a, ez_form b ";
		      querySql = querySql + " where a.form_id=b.form_id";
		      querySql = querySql + " and a.form_id=" +formId;
		      querySql = querySql + " and b.form_type='0'";
		      querySql = querySql + " order by a.create_date desc, a.setting_id desc";

		      result = dbopt.executeQueryToStrArr2(querySql, 2);
		    } catch (Exception e) {
		      e.printStackTrace();
		      }finally {
		    	 try {
				      dbopt.close();
				    } catch (Exception e) {
				      e.printStackTrace();
				    }
		       }		   
		    return result;
		  }
	 public List getCustomMenuList_portal(String domainId,String curUserId, String orgIdString) throws HibernateException {
			
			String Sql = "select po.id,po.menuName,po.menuBlone,po.menuLevel,po.menuAction,po.menuActionParams1,"
					// 0 1 2 3 4 5
					+ "po.menuActionParams2,po.menuActionParams3, po.menuActionParams4, po.menuActionParams4Value,"
					// 6 7 8 9
					+ "po.menuListTableMap,po.menuMaintenanceTableMap,po.menuStartFlow,po.menuFileLink,po.menuHtmlLink,"
					// 10 11 12 13 14
					+ "po.menuAccess, po.menuScope, po.menuMaintenanceTableName, po.menuOpenStyle, po.menuIsValide,"
					// 15 16 17 18 19
					+ "po.menuViewUser, po.menuViewOrg, po.menuViewGroup,po.menuCount,po.menuCodeSet,po.menuLevelSet,po.menuSearchBound "
					// 20 21 22 23 24 25
					+ " from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.domainId ="
					+ domainId+" and po.menuIsValide=1 ";// 取显示的模块
	        
			Sql += " and ( po.menuListTableMap is not null and po.menuListTableMap<>0 ) ";//过滤只显示启动信息项的模块	
			
			// 管理员权限
			String _sql = "";
			if ("sysManager".equals(orgIdString)) {
				_sql = " 1=1 ";
			} else {
				_sql = getRightSql(curUserId, orgIdString, domainId,
						"po.menuViewUser", "po.menuViewOrg", "po.menuViewGroup");
			}
			Sql = Sql + " and (" + _sql + ") order by po.menuLevel asc";
	 
			logger.debug("*************getCustomMenuList_portal*******Sql:" + Sql);
			List list = null;
			begin();
			try {
				list = session.createQuery(Sql).list();
			} catch (HibernateException e) {
				e.printStackTrace();
				throw e;
			} finally {
				session.close();
				transaction = null;
				session = null;
			}
			return list;
		}
	 /**
	  * 获取符合条件的id串   2016-05-03  安全性 修改 原来的方法是getMobileId
	  * @param name
	  * @param menuLevel
	  * @param menuLevelSet
	  * @return
	  * @throws Exception
	  */
	 public String getMobileId_new(String name,String menuLevel,String menuLevelSet) throws Exception {
		    
		    String ids = "";
		    Connection conn = null;
			begin();
			try {
				conn=session.connection();
				String sql="select distinct a.id from oa_custmenu a ,oa_custmenu b where b.menu_name like :menuName and b.menu_level like ezoffice.fn_linkchar(ezoffice.fn_linkchar('%',a.menu_level),'%')";
				NamedParameterStatement pstate=new NamedParameterStatement(conn, sql);
				Map varMap=new HashMap();
				varMap.put("menuName", "%"+name+"%");				 
				pstate.setVarMap(varMap);
				ResultSet res=pstate.executeQuery();
				while (res.next()) { 
					ids=ids+res.getString(1)+",";			 
				}				 
				res.close();				
			} catch (Exception ex) {
				ex.printStackTrace();
				throw ex;
			} finally {
				conn.close();			
				session.close();
				transaction = null;
			}			
			if(StringUtils.isNotEmpty(ids)){
				ids=ids.substring(0, ids.length()-1);				
			}
			logger.debug("ids----------getMobileId_new------------:"+ids);
			return ids;
		}
	 
	 /**
	  * 修改自定义模块名称的时候同步修改权限模块的自定义模块名称  2016/07/18
	  * @param menuName
	  * @param menuBlone
	  * @param menuId
	  * @return
	  * @throws Exception
	  */
	 public boolean updCustNameInRoleJsp(CustomerMenuConfigerPO po) throws Exception {
		    
		    Long menuId= po.getId();//模块id
		    String menuName=po.getMenuName();//模块名称
		    String menuNextName="";//模块+下级模块名称
		    String menuBeforeName="";//上级模块+模块名称
		    Long menuBlone=po.getMenuBlone();//父模块id
		    boolean flag=false;
		    Long menuIdNext= new Long(0);//下级模块id
		    Long menuIdBefore= new Long(0);//上级模块id
		    
		    CustomerMenuConfigerPO poNext = null;
		    CustomerMenuConfigerPO poBefore = null;
		    DbOpt opt = null;
	        begin();
	        try {
	        	opt = new DbOpt();
	            Statement stat = opt.getStatement();
	            List listNext = session.createQuery(" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.menuBlone = "+ menuId).list();
	            if (listNext!=null&&listNext.size() > 0) {
	            	poNext = (CustomerMenuConfigerPO) listNext.get(0);
	            	menuNextName=menuName+"-"+poNext.getMenuName();
	            	menuIdNext=poNext.getId();
	            }
	            
	            List listBefore = session.createQuery(" from com.whir.ezoffice.customize.customermenu.po.CustomerMenuConfigerPO po where po.id = "+ menuBlone).list();
	            if (listBefore!=null&&listBefore.size() > 0) {
	            	poBefore = (CustomerMenuConfigerPO) listBefore.get(0);
	            	menuBeforeName=poBefore.getMenuName()+"-"+menuName;
	            	menuIdBefore=poBefore.getId();
	            }
	            if(menuBeforeName!=null&&(!"".equals(menuBeforeName))){
	            	String sql1 = "update org_right set RIGHTTYPE = '"+menuBeforeName+"',RIGHTCLASS='" +menuBeforeName + "' where RIGHTCODE like '99-"+ menuId+"%' ";           
		            stat.execute(sql1);
	            }
	            
	            if(menuNextName!=null&&(!"".equals(menuNextName))){
	            String sql2 = "update org_right set RIGHTTYPE = '"+menuNextName+"',RIGHTCLASS='" + menuNextName + "' where RIGHTCODE like '99-"+menuIdNext+"%' ";           
	            stat.execute(sql2);
	            }
	            
	            flag=true;	            	            
	            stat.close();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	        	opt.close();
	            session.close();
	            transaction = null;
	            session = null;
	        }

			return flag;
		}
}
