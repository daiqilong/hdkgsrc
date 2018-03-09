package com.whir.ezoffice.subsidiarywork.bd;

import org.apache.log4j.*;
import com.whir.common.util.ParameterGenerator;
import com.whir.common.util.EJBProxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import com.whir.ezoffice.subsidiarywork.common.util.SubsidiaryWorkEJBProxy;
import com.whir.ezoffice.subsidiarywork.po.QuestionnairePO;
import com.whir.ezoffice.subsidiarywork.ejb.QuestionnaireEJBBean;
import com.whir.ezoffice.subsidiarywork.ejb.QuestionnaireEJBHome;
import com.whir.ezoffice.subsidiarywork.po.QuesthemePO;
import java.util.Map;
import com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO;
import com.whir.common.db.NamedParameterStatement;
import com.whir.common.hibernate.HibernateBase;
import com.whir.component.util.StringUtils;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.Query;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.org.vo.usermanager.EmployeeVO;
import com.whir.ezoffice.boardroom.bd.BoardRoomBD;
import com.whir.ezoffice.boardroom.common.util.BoardRoomEJBProxy;
import com.whir.ezoffice.boardroom.ejb.BoardRoomEJBHome;
import com.whir.ezoffice.officemanager.bd.EmployeeBD;
import java.util.Iterator;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * <p>
 * Title: QuestionnaireBD
 * </p>
 * <p>
 * Description: 问卷管理
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: WanHu Internet Resource(Hefei) C0. Ltd.
 * </p>
 * 
 * @author leeyom
 * @version 1.0
 */
public class QuestionnaireBD {

	/**
	 * 用于记录日志
	 */
	private static Logger logger = Logger.getLogger(QuestionnaireBD.class
			.getName());

	/**
	 * 默认构造函数
	 */
	public QuestionnaireBD() {
	}

	public List getListByHQL(String view, String from, String where) {
		List list = new ArrayList();
		ParameterGenerator pg = new ParameterGenerator(3);
		try {
			EJBProxy ejbProxy = new BoardRoomEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", BoardRoomEJBHome.class);
			pg.put(view, String.class);
			pg.put(from, String.class);
			pg.put(where, String.class);
			list = (List) ejbProxy.invoke("getListByHQL", pg.getParameters());
		} catch (Exception ex) {
			logger.error("getListByHQL BD Exception:" + ex.getMessage());
		} finally {
			return list;
		}
	}

	/**
	 * 复制问卷管理
	 * 
	 * @param ids
	 * @return
	 */
	public boolean copyQuestionnaire(String ids) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(ids, java.lang.String.class);
			ejbProxy.invoke("copyQuestionnaire", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("copyQuestionnaire Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * 新增问卷管理
	 */
	public boolean addQuestionnaire(QuestionnairePO questionnairePO) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnairePO, QuestionnairePO.class);
			ejbProxy.invoke("addQuestionnaire", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("addQuestionnaireBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}
	
	/**
	 * 新增问卷管理  2016-09-18 修改需要返回问卷的id 微信推送的时候手机端打开页面的时候需要用
	 */
	public Long addQuestionnaire_new(QuestionnairePO questionnairePO) {
		Long questionnaireId=new Long(0);
		QuestionnaireEJBBean bean =new QuestionnaireEJBBean();
		try {
			questionnaireId=bean.addQuestionnaire_new(questionnairePO);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return questionnaireId;
	}

	/**
	 * 删除问卷管理
	 */
	public boolean deleteQuestionnaire(Long questionnaireId) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnaireId, java.lang.Long.class);
			ejbProxy.invoke("deleteQuestionnaire", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("deleteQuestionnaireBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}

	}

	/**
	 * 批量删除问卷管理
	 */
	public boolean deleteBatchQuestionnaire(String questionnaireIds) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnaireIds, java.lang.String.class);
			ejbProxy.invoke("deleteBatchQuestionnaire", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("deleteBatchQuestionnaireBD Exception:"
					+ ex.getMessage());
		} finally {
			return result;
		}

	}

	/**
	 * 取需修改的问卷管理
	 */
	public QuestionnairePO selectQuestionnaireView(Long questionnaireId) {
		QuestionnairePO result = null;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnaireId, java.lang.Long.class);
			result = (QuestionnairePO) ejbProxy.invoke(
					"selectQuestionnaireView", pg.getParameters());
		} catch (Exception ex) {
			logger.error("selectQuestionnaireViewBD Exception:"
					+ ex.getMessage());
		} finally {
			return result;
		}

	}

	/**
	 * 更新问卷管理
	 */
	public boolean updateQuestionnaire(QuestionnairePO questionnairePO) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnairePO, QuestionnairePO.class);
			ejbProxy.invoke("updateQuestionnaire", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("updateQuestionnaireBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}
	
	/**
	 * 新增问卷设计
	 * 
	 * @param questionnairePO
	 *            QuestionnairePO
	 * @return boolean
	 */
	public boolean addQuestheme(QuesthemePO questhemePO,
			String[] solutionTitle, String[] optionScore, String[] pitchon) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(4);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questhemePO, QuesthemePO.class);
			pg.put(solutionTitle, java.lang.String[].class);
			pg.put(optionScore, java.lang.String[].class);
			pg.put(pitchon, java.lang.String[].class);
			ejbProxy.invoke("addQuestheme", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("addQuesthemeBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}
	
	/**
	 * 新增问卷设计
	 * 
	 * @param questionnairePO
	 *            QuestionnairePO
	 * @return boolean
	 */
	public boolean addQuestheme_new(QuesthemePO questhemePO,
			String[] solutionTitle, String[] optionScore, String[] pitchon,String[] imgRealName, String[] imgSaveName, String[] customAnswer) {
		boolean result = false;
		QuestionnaireEJBBean bean=new QuestionnaireEJBBean();
		try {
			result=bean.addQuestheme_new(questhemePO, solutionTitle, optionScore, pitchon, imgRealName, imgSaveName, customAnswer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return result;
 
	}

	/**
	 * 删除问卷设计
	 */
	public boolean deleteQuestheme(Long questhemeId) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questhemeId, java.lang.Long.class);
			ejbProxy.invoke("deleteQuestheme", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("deleteQuesthemeBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}

	}

	/**
	 * 批量删除问卷设计
	 */
	public boolean deleteBatchQuestheme(String questhemeIds) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questhemeIds, java.lang.String.class);
			ejbProxy.invoke("deleteBatchQuestheme", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("deleteBatchQuesthemeBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}

	}

	/**
	 * 提取问卷设计
	 */
	public Map selectQuesthemeView(Long questhemeId) {
		Map result = null;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questhemeId, java.lang.Long.class);
			result = (Map) ejbProxy.invoke("selectQuesthemeView", pg
					.getParameters());

		} catch (Exception e) {
			logger.error("selectQuesthemeViewBD:" + e.getMessage());
			e.printStackTrace();
		} finally {
			return result;
		}
	}
	
	/**
	 * 提取问卷设计(达蒙数据库获取时候单选/多选分数为0，获取QuesthemePO报错：数字溢出 2016-09-03)
	 */
	public Map selectQuesthemeView_new(Long questhemeId) {		
		QuestionnaireEJBBean bean=new QuestionnaireEJBBean();
		Map result=null;
		try { 
			result = bean.selectQuesthemeView_new(questhemeId);
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 更新问卷设计
	 */
	public boolean updateQuestheme(QuesthemePO questhemePO,
			String[] solutionTitle, String[] optionScore, String[] pitchon) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(4);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questhemePO, QuesthemePO.class);
			pg.put(solutionTitle, java.lang.String[].class);
			pg.put(optionScore, java.lang.String[].class);
			pg.put(pitchon, java.lang.String[].class);
			ejbProxy.invoke("updateQuestheme", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("updateQuesthemeBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * 更新问卷设计
	 */
	public boolean updateQuestheme_new(QuesthemePO questhemePO,
			String[] solutionTitle, String[] optionScore, String[] pitchon,String[] imgRealName,String[] imgSaveName,String[] customAnswer) {
		boolean result = false;
		QuestionnaireEJBBean bean=new QuestionnaireEJBBean();
		try {
			result=bean.updateQuestheme_new(questhemePO, solutionTitle, optionScore, pitchon, imgRealName, imgSaveName, customAnswer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 问卷管理预览
	 */
	public Map selectQuestionnairePreview(Long questionnaireId) {
		Map result = null;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(questionnaireId, java.lang.Long.class);
			result = (Map) ejbProxy.invoke("selectQuestionnairePreview", pg
					.getParameters());
		} catch (Exception e) {
			logger.error("selectQuestionnairePreviewBD:" + e.getMessage());
			e.printStackTrace();
		} finally {
			return result;
		}
	}
	
	/**
	 * 问卷管理预览   2016/08/10
	 */
	public Map selectQuestionnairePreview_move(Long questionnaireId) {
		Map result = null;
		QuestionnaireEJBBean  bean=new QuestionnaireEJBBean();
		try {
			result=bean.selectQuestionnairePreview_move(questionnaireId);  
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result; 
	}

	/**
	 * 取投票用户
	 * 
	 * @param questionnaireId
	 *            String 信息ID
	 * @throws Exception
	 * @return List
	 */
	public List getBrowser(String questionnaireId, String searchName,
			String read, int volume, int currentPage, String domainId,
			String searchOrgName) throws Exception {
		String sql = "";
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and empPO.empName like '%" + searchName + "%' ";
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and orgPO.orgName like '%" + searchOrgName + "%' ";
		}
		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			if ("1".equals(read)) {
				sql = "select orgPO.orgId,orgPO.orgNameString,orgPO.orgName,orgPO.orgIdString from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where answerSheetPO.ballotEmp=empPO.empId and answerSheetPO.questionnaireId ="+ questionnaireId+ " "+ sWhere
						+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString order by orgPO.orgIdString";			 
				query = session.createQuery(sql);
			} else if (!"1".equals(read)) {
				sql = "select po.actorEmp,po.actorOrg from com.whir.ezoffice.subsidiarywork.po.QuestionnairePO po where po.questionnaireId="
						+ questionnaireId;
				Object[] obj = null;
				Iterator iter = session.iterate(sql);
				if (iter.hasNext()) {
					obj = (Object[]) iter.next();
				}
				String emps = "-1";
				String orgs = "-1";
				if (obj != null) {
					emps = obj[0] == null ? "" : obj[0].toString();
					orgs = obj[1] == null ? "" : obj[1].toString();
				}
				if (!emps.equals("")) {
					// 选择了查看用户
					if (emps.startsWith("$") && emps.endsWith("$")) {
						emps = emps.substring(1, emps.length() - 1);
					}
					String[] tmp = new String[1];
					emps = emps.replace('$', ',');
					emps = emps.replaceAll(",,", ",");
				}
				if (emps.equals("")) {
					emps = "-1";
				} else {
					emps = "-1," + emps;
				}
				if (!orgs.equals("")) {
					// 选择了查看组织
					orgs = orgs.replace('*', ',');
					if (orgs.startsWith(",") && orgs.endsWith(",")) {
						orgs = orgs.substring(1, orgs.length() - 1);
					}
					String[] tmp = new String[1];
					if (orgs.indexOf(",,") >= 0) {
						tmp = orgs.split(",,");
					} else {
						tmp[0] = orgs;
					}
					StringBuffer sb = new StringBuffer();
					StringBuffer sb1 = new StringBuffer();
					for (int i = 0; i < tmp.length; i++) {
						sb.append(" orgVO.orgIdString like '%$" + tmp[i]
								+ "$%' or ");
						sb1.append(" po.sidelineOrg like '%*" + tmp[i]
								+ "*%' or ");
					}
					String tmpSql = "select orgVO.orgId from com.whir.org.vo.organizationmanager.OrganizationVO orgVO where ("
							+ sb.toString() + " 1 > 1) ";
					Query q1 = session.createQuery(tmpSql);
					List l1 = q1.list();
					orgs = "-1";
					if (l1 != null && l1.size() > 0) {
						for (int m = 0; m < l1.size(); m++) {
							orgs += "," + l1.get(m);
						}
					}
					// EmployeeVO vo=new EmployeeVO();
					// vo.getEmpId()empPO.empId
					// 取兼职用户
					tmpSql = "select po.empId from com.whir.org.vo.usermanager.EmployeeVO po where ("
							+ sb1.toString()
							+ " 1 > 1) and po.domainId="
							+ Integer.parseInt(domainId);
					Query query2 = session.createQuery(tmpSql);
					List list3 = query2.list();
					if (list3 != null && list3.size() > 0) {
						for (int kk = 0; kk < list3.size(); kk++) {
							emps += "," + String.valueOf(list3.get(kk));
						}
					}
				}
				// -------------处理orgs为空时保存问题---------------------
				if (orgs == null || "NUll".equalsIgnoreCase(orgs)
						|| "".equals(orgs)) {
					orgs = "-1";
				}
				// -------------处理orgs为空时保存问题---------------------
				String __where = " and (empPO.empId in (" + emps
						+ ") or orgPO.orgId in (" + orgs + ")) ";
				sWhere += __where;
				if ("0".equals(read)) {
					sql = " select orgPO.orgId,orgPO.orgNameString,orgPO.orgName,orgPO.orgIdString from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where empPO.empId not in ( select distinct {b}.empId from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO {a},com.whir.org.vo.usermanager.EmployeeVO {b} where {a}.ballotEmp={b}.empId  and {a}.questionnaireId ='"
							+ questionnaireId
							+ "') "
							+ sWhere
							+ " and empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
							+ domainId
							+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString order by orgPO.orgIdString";
					query = session.createQuery(sql);
				} else {
					sql = " select orgPO.orgId,orgPO.orgNameString,orgPO.orgName,orgPO.orgIdString from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where "
							+ " empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
							+ domainId
							+ sWhere
							+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString order by orgPO.orgIdString";
					query = session.createQuery(sql);
				}
			}
		 
			query.setFirstResult((currentPage - 1) * volume);
			query.setMaxResults(volume);
			list = query.list();
 
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;			
		}
		return list;
	}

	public int getBrowserCount(String questionnaireId, String searchName,
			String read, String domainId, String searchOrgName)
			throws Exception {
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and empPO.empName like '%" + searchName + "%' ";
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and orgPO.orgName like '%" + searchOrgName + "%' ";
		}

		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			if ("1".equals(read)) {
				query = session
						.createQuery("select orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where answerSheetPO.ballotEmp=empPO.empId and answerSheetPO.questionnaireId ="
								+ questionnaireId
								+ " "
								+ sWhere
								+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString "); // order
				// by
				// orgPO.orgIdString
				// ");
			} else if (!"1".equals(read)) {
				Query query1 = session
						.createQuery("select po.actorEmp,po.actorOrg from com.whir.ezoffice.subsidiarywork.po.QuestionnairePO po where po.questionnaireId="
								+ questionnaireId);
				List list2 = query1.list();
				String emps = "-1";
				String orgs = "-1";
				if (list2 != null && list2.size() > 0) {
					Object[] obj = (Object[]) list2.get(0);
					String actorEmp = obj[0] != null ? String.valueOf(obj[0])
							: "";
					String actorOrg = obj[1] != null ? String.valueOf(obj[1])
							: "";
					String[] _emp = ("$" + actorEmp + "$").split("\\$\\$");
					String[] _org = ("*" + actorOrg + "*").split("\\*\\*");

					if (_emp != null && _emp.length > 0) {
						for (int i = 0; i < _emp.length; i++) {
							if (_emp[i] != null && !"".equals(_emp[i])) {
								emps += "," + _emp[i];
							}
						}
					}
					if (_org != null && _org.length > 0) {
						for (int i = 0; i < _org.length; i++) {
							if (_org[i] != null && !"".equals(_org[i])) {
								Query q = session
										.createQuery("select po.orgHasJunior from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgId = "
												+ _org[i]);
								List l = q.list();
								if ("0".equals(l.get(0))) { // 没有子组织
									orgs += "," + _org[i];
								} else { // 有子组织
									Query q1 = session
											.createQuery("select po.orgId from com.whir.org.vo.organizationmanager.OrganizationVO po where po.orgIdString like '%"
													+ _org[i] + "%'");
									List l1 = q1.list();
									for (int m = 0; m < l1.size(); m++) {
										orgs += "," + l1.get(m);
									}
								}
								// 取兼职用户
								Query query2 = session
										.createQuery("select po.empId from com.whir.org.vo.usermanager.EmployeeVO po where po.sidelineOrg like '%*"
												+ _org[i]
												+ "*%' and po.domainId="
												+ domainId);
								List list3 = query2.list();
								if (list3 != null && list3.size() > 0) {
									for (int kk = 0; kk < list3.size(); kk++) {
										emps += ","
												+ String.valueOf(list3.get(kk));
									}
								}

							}
						}
					}
				}
				String __where = " and (empPO.empId in (" + emps
						+ ") or orgPO.orgId in (" + orgs + ")) ";
				sWhere += __where;
				if ("0".equals(read)) {
					query = session
							.createQuery(" select orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where empPO.empId not in ( select distinct {b}.empId from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO {a},com.whir.org.vo.usermanager.EmployeeVO {b} where {a}.ballotEmp={b}.empId and {a}.questionnaireId ='"
									+ questionnaireId
									+ "') "
									+ sWhere
									+ " and empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
									+ domainId
									+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString "); // order
					// by
					// orgPO.orgIdString");
				} else {
					query = session
							.createQuery(" select orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where "
									+ " empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
									+ domainId
									+ sWhere
									+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString "); // order
					// by
					// orgPO.orgIdString");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map getAllBrowser(String questionnaireId, String searchName,
			String read, int volume, int currentPage, String domainId,
			String searchOrgName) throws Exception {
		//logger.error("getAllBrowser read:"+read);
		String sWhere = "";
		String allSize = "0";
		String readSize = "0";
		String recordCount = "";
		String readSizeSql = " select count(emp.emp_Id) from oa_answersheet t,org_employee emp ,org_organization org,org_organization_user o "
				+ " where t.ballotemp = emp.emp_id and t.questionnaireid = "
				+ questionnaireId
				+ " and emp.emp_id = o.emp_id and org.org_id = o.org_id";
		String allSizeSql = " select count(distinct emp.emp_Id) from oa_answersheet t,org_employee emp ,"
				+ "org_organization org,org_organization_user o "
				+ " where  emp.emp_id = o.emp_id and org.org_id = o.org_id ";
		String recordCountSql = "";
		String rWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and empPO.empName like '%" + searchName + "%' ";
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and orgPO.orgName like '%" + searchOrgName + "%' ";
		}
		Map map = new HashMap();
		List list = new ArrayList();
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Connection con = null;
		Statement stat = null;
		String sql = "";
		try {
			con = session.connection();
			stat = con.createStatement();
			if ("1".equals(read)) {
				//logger.error("111getAllBrowser read:"+read);
				sql = "select emp.emp_Id,emp.empName,org.org_Id,org.orgName,t.ballotDate,emp.empSex,emp.imId,emp.userAccounts,1 "
						+ " from oa_answersheet t,org_employee emp ,org_organization org,org_organization_user o "
						+ " where t.ballotemp = emp.emp_id and t.questionnaireid = "
						+ questionnaireId
						+ " and emp.emp_id = o.emp_id and org.org_id = o.org_id";
				recordCountSql = "select orgPO.orgId from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where answerSheetPO.ballotEmp=empPO.empId and answerSheetPO.questionnaireId ="
						+ questionnaireId
						+ " "
						+ sWhere
						+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString ";

			} else if (!"1".equals(read)) {
				//logger.error("222getAllBrowser read:"+read);
				Object[] obj = null;
				Iterator iter = session
						.iterate("select po.actorEmp,po.actorOrg from com.whir.ezoffice.subsidiarywork.po.QuestionnairePO po where po.questionnaireId="
								+ questionnaireId);
				if (iter.hasNext()) {
					obj = (Object[]) iter.next();
				}

				String emps = "-1";
				String orgs = "-1";
				if (obj != null) {
					emps = obj[0] == null ? "" : obj[0].toString();
					orgs = obj[1] == null ? "" : obj[1].toString();
				}
				if (!emps.equals("")) {
					// 选择了查看用户
					if (emps.startsWith("$") && emps.endsWith("$")) {
						emps = emps.substring(1, emps.length() - 1);
					}
					String[] tmp = new String[1];
					emps = emps.replace('$', ',');
					emps = emps.replaceAll(",,", ",");
				}
				if (emps.equals("")) {
					emps = "-1";
				} else {
					emps = "-1," + emps;
				}
				if (!orgs.equals("")) {
					// 选择了查看组织
					orgs = orgs.replace('*', ',');
					if (orgs.startsWith(",") && orgs.endsWith(",")) {
						orgs = orgs.substring(1, orgs.length() - 1);
					}
					String[] tmp = new String[1];
					if (orgs.indexOf(",,") >= 0) {
						tmp = orgs.split(",,");
					} else {
						tmp[0] = orgs;
					}
					StringBuffer sb = new StringBuffer();
					StringBuffer sb1 = new StringBuffer();
					for (int i = 0; i < tmp.length; i++) {
						sb.append(" orgVO.orgIdString like '%$" + tmp[i]
								+ "$%' or ");
						sb1.append(" po.sidelineOrg like '%*" + tmp[i]
								+ "*%' or ");
					}
					String tmpSql = "select orgVO.orgId from com.whir.org.vo.organizationmanager.OrganizationVO orgVO where ("
							+ sb.toString() + " 1 > 1) ";
					Query q1 = session.createQuery(tmpSql);
					List l1 = q1.list();
					orgs = "-1";
					if (l1 != null && l1.size() > 0) {
						for (int m = 0; m < l1.size(); m++) {
							orgs += "," + l1.get(m);
						}
					}
					// 取兼职用户
					tmpSql = "select po.empId from com.whir.org.vo.usermanager.EmployeeVO po where ("
							+ sb1.toString()
							+ " 1 > 1) and po.domainId="
							+ domainId;
					Query query2 = session.createQuery(tmpSql);
					List list3 = query2.list();
					if (list3 != null && list3.size() > 0) {
						for (int kk = 0; kk < list3.size(); kk++) {
							emps += "," + String.valueOf(list3.get(kk));
						}
					}
				}
				// -------------处理orgs为空时保存问题---------------------
				if (orgs == null || "NUll".equalsIgnoreCase(orgs)
						|| "".equals(orgs)) {
					orgs = "-1";
				}
				// -------------处理orgs为空时保存问题---------------------
				String __where = " and (emp.emp_Id in (" + emps
						+ ") or org.org_Id in (" + orgs + ")) ";
				String __rwhere = " and (empPO.empId in (" + emps
						+ ") or orgPO.orgId in (" + orgs + ")) ";
				rWhere = sWhere + __rwhere;
				 
				// 处理查询报错的问题
				// ------------start----------------------
				if (!"".equals(searchName)) {
					sWhere = " and emp.empName like '%" + searchName + "%' ";
				}
				if (!"".equals(searchOrgName)) {
					sWhere = " and org.orgName like '%" + searchOrgName + "%' ";
				}
				// ------------end----------------------
				sWhere += __where;
				if ("0".equals(read)) {
					//logger.error("333getAllBrowser read:"+read);
					sql = " select distinct emp.emp_Id,emp.empName,org.org_Id,org.orgName,'',emp.empSex,emp.imId,emp.userAccounts,0 "
							+ " from org_employee emp ,org_organization org,org_organization_user o "
							+ " where emp.emp_id not in (select e.emp_Id from oa_answersheet a,org_employee e where "
							+ " a.ballotemp = e.emp_id and a.questionnaireid = "
							+ questionnaireId
							+ " ) "
							+ sWhere
							+ " and emp.emp_id = o.emp_id and org.org_id = o.org_id "
							+ " and emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null and emp.domain_Id="
							+ domainId + " order by emp.empName ";
					recordCountSql = " select orgPO.orgId from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where empPO.empId not in ( select distinct {b}.empId from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO {a},com.whir.org.vo.usermanager.EmployeeVO {b} where {a}.ballotEmp={b}.empId and {a}.questionnaireId ='"
							+ questionnaireId
							+ "') "
							+ rWhere
							+ " and empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
							+ domainId
							+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString ";

				} else {
					//logger.error("4441getAllBrowser read:"+read);
					sql = " select distinct emp.emp_Id,emp.empName,org.org_Id,org.orgName,'',emp.empSex,emp.imId,emp.userAccounts, "
							+ "(case when  emp.emp_id in (select e.emp_Id from oa_answersheet a,org_employee e where "
							+ " a.ballotemp = e.emp_id and a.questionnaireid = "
							+ questionnaireId
							+ " ) then 1 else 0 end) as isread "
							+ " from org_employee emp ,org_organization org,org_organization_user o "
							+ " where  emp.emp_id = o.emp_id and org.org_id = o.org_id "
							+ sWhere
							+ " and emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null and emp.domain_Id="
							+ domainId + " order by emp.empName ";
					recordCountSql = " select orgPO.orgId from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where "
							+ " empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.userAccounts is not null and empPO.domainId="
							+ domainId
							+ rWhere
							+ " group by orgPO.orgId,orgPO.orgName,orgPO.orgIdString,orgPO.orgNameString ";

				}
			}
			// 处理查询报错的问题
			// ------------start----------------------
			if (!"".equals(searchName)) {
				sWhere = " and emp.empName like '%" + searchName + "%' ";
			}
			if (!"".equals(searchOrgName)) {
				sWhere = " and org.orgName like '%" + searchOrgName + "%' ";
			}
			// ------------end----------------------
			allSizeSql += sWhere
					+ " and emp.userIsActive=1 and emp.userIsDeleted=0 and emp.userAccounts is not null and emp.domain_Id="
					+ domainId;
			 
			List recordCountList = session.createQuery(recordCountSql).list();		    
			if (recordCountList != null) {
				recordCount = recordCountList.size() + "";
			} else {
				recordCount = "0";
			}

			ResultSet rsAllSize = stat.executeQuery(allSizeSql);			
			
			while (rsAllSize.next()) {
				allSize = rsAllSize.getString(1) != null ? rsAllSize
						.getString(1) : "";
			}
			ResultSet rsReadSize = stat.executeQuery(readSizeSql);
			while (rsReadSize.next()) {
				readSize = rsReadSize.getString(1) != null ? rsReadSize
						.getString(1) : "";
			}
            
			ResultSet rs = stat.executeQuery(sql);
			
			while (rs.next()) {
				Object[] obj = new Object[] { rs.getString(1), rs.getString(2),
						rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getString(6),
						rs.getString(7) != null ? rs.getString(7) : "",
						rs.getString(8), rs.getString(9) };
				list.add(obj);
			}
			/*
			 * System.out.println("list size========================"+list.size()
			 * ); for (int i = 0; i < list.size(); i++) { Object[] obj =
			 * (Object[])list.get(i); for (int j = 0; j < obj.length; j++)
			 * {//emp_Id
			 * ,empName,org_Id,orgName,'',empSex,imId,userAccounts,isread
			 * System.out.println("obj"+j+"===="+obj[j]); } }
			 */
			map.put("allSize", allSize);
			map.put("readSize", readSize);
			map.put("resultList", list);
			map.put("recordCount", recordCount);
			long end = System.currentTimeMillis();
			rs.close();
			stat.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			con.close();
			session.close();
			session = null;			
		}
		return map;
	}

	public int getBrowserCount_bak(String questionnaireId, String searchName,
			String read, String domainId) throws Exception {
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere = " and empPO.empName like '%" + searchName + "%'";
		}
		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			if ("1".equals(read)) {
				query = session
						.createQuery("select distinct empPO.empName,orgPO.orgName,empPO.userAccounts from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where answerSheetPO.ballotEmp=empPO.empId and answerSheetPO.questionnaireId ="
								+ questionnaireId
								+ " "
								+ sWhere
								+ " order by orgPO.orgName desc");
			} else {
				query = session
						.createQuery(" select distinct empPO.empName,orgPO.orgName,empPO.userAccounts from com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO where empPO.empId not in ( select distinct {b}.empId from com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO {a},com.whir.org.vo.usermanager.EmployeeVO {b} where {a}.ballotEmp={b}.empId and {a}.questionnaireId ='"
								+ questionnaireId
								+ "') "
								+ sWhere
								+ " and empPO.userIsActive=1 and empPO.userIsDeleted=0 and empPO.domainId="
								+ domainId);
			}
			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}
		}
	}

	/**
	 * 查找选择答案的人
	 * 
	 * @param themeId
	 *            String 选项ID
	 * @throws Exception
	 * @return List
	 */
	public List getBrowserOption(String themeId, String searchName, int volume,
			int currentPage, String searchOrgName) throws Exception {
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and {d}.empName like '%" + searchName + "%' ";			 
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and {e}.orgName like '%" + searchOrgName + "%' ";
		}

		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			query = session
					.createQuery(" select {e}.orgId,{e}.orgName,{e}.orgIdString,{e}.orgNameString from com.whir.ezoffice.subsidiarywork.po.AnswerSheetOptionPO {a},com.whir.ezoffice.subsidiarywork.po.AnswerSheetContentPO {b} join {b}.answerSheet {c},com.whir.org.vo.usermanager.EmployeeVO {d} join {d}.organizations {e} where {a}.answerSheetContent={b}.contentId and {c}.ballotEmp={d}.empId and  {a}.themeOptionId='"
							+ themeId
							+ "' "
							+ sWhere
							+ " group by {e}.orgId,{e}.orgName,{e}.orgIdString,{e}.orgNameString order by {e}.orgIdString"); 
			 
			query.setFirstResult((currentPage - 1) * volume);
			query.setMaxResults(volume);
			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;			
		}
		return list;
	}

	public int getBrowserOptionCount(String themeId, String searchName,
			String searchOrgName) throws Exception {
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and {d}.empName like '%" + searchName + "%' ";
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and {e}.orgName like '%" + searchOrgName + "%' ";
		}

		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			query = session
					.createQuery(" select {e}.orgId,{e}.orgName,{e}.orgIdString,{e}.orgNameString from com.whir.ezoffice.subsidiarywork.po.AnswerSheetOptionPO {a},com.whir.ezoffice.subsidiarywork.po.AnswerSheetContentPO {b} join {b}.answerSheet {c},com.whir.org.vo.usermanager.EmployeeVO {d} join {d}.organizations {e} where {a}.answerSheetContent={b}.contentId and {c}.ballotEmp={d}.empId and  {a}.themeOptionId='"
							+ themeId
							+ "' "
							+ sWhere
							+ " group by {e}.orgId,{e}.orgName,{e}.orgIdString,{e}.orgNameString "); // order
			// by
			// {e}.orgIdString");
			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;
			if (list != null) {
				return list.size();
			} else {
				return 0;
			}

		}
	}

	public List getAllBrowserOption(String themeId, String searchName,
			int volume, int currentPage, String searchOrgName) throws Exception {
		String sWhere = "";
		if (!"".equals(searchName)) {
			sWhere += " and {d}.empName like '%" + searchName + "%' ";
		}
		if (!"".equals(searchOrgName)) {
			sWhere += " and {e}.orgName like '%" + searchOrgName + "%' ";
		}

		List list = null;
		HibernateBase hb = new HibernateBase();
		hb.begin();
		Session session = hb.getSession();
		Query query = null;
		try {
			query = session
					.createQuery(" select distinct {d}.empName,{e}.orgName,{d}.userAccounts,{e}.orgId,{c}.ballotDate,{d}.imId,{d}.empId,{d}.empSex from com.whir.ezoffice.subsidiarywork.po.AnswerSheetOptionPO {a},com.whir.ezoffice.subsidiarywork.po.AnswerSheetContentPO {b} join {b}.answerSheet {c},com.whir.org.vo.usermanager.EmployeeVO {d} join {d}.organizations {e} where {a}.answerSheetContent={b}.contentId and {c}.ballotEmp={d}.empId and  {a}.themeOptionId='"
							+ themeId
							+ "' "
							+ sWhere
							+ " order by {d}.empName ");
						
			query.setFirstResult(0);
			query.setMaxResults(999999999);
			list = query.list();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			session.close();
			session = null;			
		}
		return list;
	}

	/**
	 * 提交答卷
	 */
	public boolean addQuestionnaireAnswer(AnswerSheetPO answerSheetPO,
			List list, List essayList) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(3);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(answerSheetPO, AnswerSheetPO.class);
			pg.put(list, List.class);
			pg.put(essayList, List.class);
			ejbProxy.invoke("addQuestionnaireAnswer", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("addQuestionnaireAnswerBD Exception:"
					+ ex.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * 答卷管理预览
	 */
	public Map selectAnswerPreview(Long answerSheetId) {
		Map result = null;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(answerSheetId, java.lang.Long.class);
			result = (Map) ejbProxy.invoke("selectAnswerPreview", pg
					.getParameters());
		} catch (Exception e) {
			logger.error("selectAnswerPreviewBD:" + e.getMessage());
			e.printStackTrace();
		} finally {
			return result;
		}
	}

	/**
	 * 提交答卷评分
	 */
	public boolean addAnswerGraded(List essayList) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(essayList, List.class);
			ejbProxy.invoke("addAnswerGraded", pg.getParameters());
			result = true;
		} catch (Exception ex) {
			logger.error("addAnswerGradedBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * 答题列表页面
	 */
	public List answerQuestionnaireList(String where) {
		List result = new ArrayList();
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(where, java.lang.String.class);
			result = (List) ejbProxy.invoke("answerQuestionnaireList", pg
					.getParameters());
		} catch (Exception ex) {
			logger.error("answerQuestionnaireListBD Exception:"
					+ ex.getMessage());
		} finally {
			return result;
		}
	}

	/**
	 * 答题列表页面(增加对兼职的支持)
	 */
	public List answerQuestionnaireList(String userId, String orgId,
			String orgIdString, String domainId) {
		ManagerBD mbd = new ManagerBD();
		String where = mbd.getScopeFinalWhere(userId, orgId, orgIdString,
				"questionnairePO.actorEmp", "questionnairePO.actorOrg",
				"questionnairePO.actorGroup");
		where += " and questionnairePO.domainId=" + domainId;

		// 增加对兼职的支持--------------------------------------------------------
		EmployeeBD empBD = new EmployeeBD();
		List singleEmpList = empBD.selectSingle(new Long(userId));
		String sidelineorgStr = "";
		if (singleEmpList != null && singleEmpList.size() > 0) {
			Object[] __empObj = (Object[]) singleEmpList.get(0);
			com.whir.org.vo.usermanager.EmployeeVO __empVO = (com.whir.org.vo.usermanager.EmployeeVO) __empObj[0];
			sidelineorgStr = __empVO.getSidelineOrg() != null ? __empVO
					.getSidelineOrg() : "";
		}
		if (!"".equals(sidelineorgStr)) {
			if (sidelineorgStr.startsWith("*") && sidelineorgStr.endsWith("*")) {
				String ___tmp = sidelineorgStr.substring(1, sidelineorgStr
						.length() - 1);
				String[] ___tmpArr = ___tmp.split("\\*\\*");
				for (int _i0 = 0; _i0 < ___tmpArr.length; _i0++) {
					where += " or questionnairePO.actorOrg like '%*"
							+ ___tmpArr[_i0] + "*%' ";
				}
			}
		}
		// ----------------------------------------------------------------------

		List result = new ArrayList();
		ParameterGenerator pg = new ParameterGenerator(1);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(where, java.lang.String.class);
			result = (List) ejbProxy.invoke("answerQuestionnaireList", pg
					.getParameters());
		} catch (Exception ex) {
			logger.error("answerQuestionnaireListBD Exception:"
					+ ex.getMessage());
		} finally {
			return result;
		}
	}
	
	/**
	 * 答题列表页面(增加对兼职的支持)
	 */
	public Map answerQuestionnaireList_new(String userId, String orgId,
			String orgIdString, String domainId,int pageSize, int currentPage,String title) {
		ManagerBD mbd = new ManagerBD();
		String where = mbd.getScopeFinalWhere(userId, orgId, orgIdString,
				"questionnairePO.actorEmp", "questionnairePO.actorOrg",
				"questionnairePO.actorGroup");
		where += " and questionnairePO.domainId=" + domainId;
		
		// 增加对兼职的支持--------------------------------------------------------
		EmployeeBD empBD = new EmployeeBD();
		List singleEmpList = empBD.selectSingle(new Long(userId));
		String sidelineorgStr = "";
		if (singleEmpList != null && singleEmpList.size() > 0) {
			Object[] __empObj = (Object[]) singleEmpList.get(0);
			com.whir.org.vo.usermanager.EmployeeVO __empVO = (com.whir.org.vo.usermanager.EmployeeVO) __empObj[0];
			sidelineorgStr = __empVO.getSidelineOrg() != null ? __empVO
					.getSidelineOrg() : "";
		}
		if (!"".equals(sidelineorgStr)) {
			if (sidelineorgStr.startsWith("*") && sidelineorgStr.endsWith("*")) {
				String ___tmp = sidelineorgStr.substring(1, sidelineorgStr
						.length() - 1);
				String[] ___tmpArr = ___tmp.split("\\*\\*");
				for (int _i0 = 0; _i0 < ___tmpArr.length; _i0++) {
					where += " or questionnairePO.actorOrg like '%*"
							+ ___tmpArr[_i0] + "*%' ";
				}
			}
		}
		// ----------------------------------------------------------------------

		
		QuestionnaireEJBBean bean =new QuestionnaireEJBBean();
		Map result = new HashMap();
		try {
			result=bean.answerQuestionnaireList_new(where,pageSize,currentPage,title);
		} catch (HibernateException e) {
			e.printStackTrace();
		}
	    return result;
 
	}

	/**
	 * 判断是否重名
	 */
	public boolean isRepeatName(String from, String where) {
		boolean result = false;
		ParameterGenerator pg = new ParameterGenerator(2);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(from, java.lang.String.class);
			pg.put(where, java.lang.String.class);
			Boolean b = (Boolean) ejbProxy.invoke("isRepeatName", pg
					.getParameters());
			result = b.booleanValue();
		} catch (Exception ex) {
			logger.error("isRepeatNameBD Exception:" + ex.getMessage());
		} finally {
			return result;
		}
	}
	
	
	/**
	 * 判断是否重名   安全性  2016/03/25
	 */
	public boolean isRepeatName_Security(String from, String where,String title, String questionnaireId) {
		boolean result = false;
		QuestionnaireEJBBean  questionnaireEJBBean=new QuestionnaireEJBBean();
		try {			 
			result = questionnaireEJBBean.isRepeatName_Security(from, where, title, questionnaireId);
		} catch (Exception ex) {
			logger.error("isRepeatName_Security Exception:" + ex.getMessage());
		}
			return result;
	}


	/**
	 * 判断是否重名
	 */
	public List isMyAnswer(String from, String where) {
		List list = null;
		ParameterGenerator pg = new ParameterGenerator(2);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(from, java.lang.String.class);
			pg.put(where, java.lang.String.class);
			list = (List) ejbProxy.invoke("isMyAnswer", pg.getParameters());
		} catch (Exception ex) {
			logger.error("isRepeatNameBD Exception:" + ex.getMessage());
		} finally {
			return list;
		}
	}

	/**
	 * 取范维权限内可维护的记录
	 */
	public String maintenance(String selectValue, String from, String where) {
		String maintenanceIds = "";
		ParameterGenerator pg = new ParameterGenerator(3);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(selectValue, java.lang.String.class);
			pg.put(from, java.lang.String.class);
			pg.put(where, java.lang.String.class);
			maintenanceIds = (String) ejbProxy.invoke("maintenance", pg
					.getParameters());
		} catch (Exception ex) {
			logger.error("maintenance BD Exception:" + ex.getMessage());
		} finally {
			return maintenanceIds;
		}
	}

	/**
	 * GET RADIO OR CHECKBOX VALUE FROM THEMEOPTIONS
	 * 
	 * @param themeOptionId
	 *            String
	 * @return String
	 */
	public String getThemeOptionByOptionId(String themeOptionId, String domainId) {
		if (themeOptionId != null && themeOptionId.length() > 0) {
			String id = "";
			ParameterGenerator pg = new ParameterGenerator(2);
			try {
				EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy(
						"QuestionnaireEJB", "QuestionnaireEJBLocal",
						QuestionnaireEJBHome.class);
				pg.put(themeOptionId, java.lang.String.class);
				pg.put(domainId, java.lang.String.class);
				id = (String) ejbProxy.invoke("getThemeOptionByOptionId", pg
						.getParameters());
			} catch (Exception ex) {
				logger.error("getThemeOptionByOptionId Exception:"
						+ ex.getMessage());
			} finally {
				return id;
			}
		}
		return "";
	}

	/**
	 * GET TEXTAREA VALUE FROM THEMEOPTIONS
	 * 
	 * @param themeOptionId
	 *            String
	 * @return String
	 */
	public String getThemeOptionByThemeId(String themeId, String domainId) {
		if (themeId != null && themeId.length() > 0) {
			String id = "";
			ParameterGenerator pg = new ParameterGenerator(2);
			try {
				EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy(
						"QuestionnaireEJB", "QuestionnaireEJBLocal",
						QuestionnaireEJBHome.class);
				pg.put(themeId, java.lang.String.class);
				pg.put(domainId, java.lang.String.class);
				id = (String) ejbProxy.invoke("getThemeOptionByThemeId", pg
						.getParameters());
			} catch (Exception ex) {
				logger.error("getThemeOptionByThemeId Exception:"
						+ ex.getMessage());
			} finally {
				return id;
			}
		}
		return "";
	}

	public void setReadedUser(String userID, Long recordID) {
		ParameterGenerator pg = new ParameterGenerator(2);
		try {
			EJBProxy ejbProxy = new SubsidiaryWorkEJBProxy("QuestionnaireEJB",
					"QuestionnaireEJBLocal", QuestionnaireEJBHome.class);
			pg.put(userID, java.lang.String.class);
			pg.put(recordID, java.lang.Long.class);
			ejbProxy.invoke("setReadedUser", pg.getParameters());
		} catch (Exception ex) {
			logger.error("setReadedUser Exception:" + ex.getMessage());
		}
	}

	/**
	 * 显示投票人列表
	 * 
	 * @throws Exception
	 */
	public List viewbrowserUser(String searchuserName, String searchOrg,
			String searchtype, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession(true);
		String questionnaireId = request.getParameter("id")==null?"":request.getParameter("id");	
		if(!com.whir.component.security.crypto.EncryptUtil.isNumeric(questionnaireId)){
			throw new Exception();
		}
		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();
		List listAll = new ArrayList();
		int pageSize = 9999;
		int currentPage = 1;
		// 新参数 0:全部用户;1：已查看用户;2：未查看用户
		// 老参数 "":全部用户;1：已投票用户;0：未投票用户
		String read = "";// 新旧参数转换
		if ("0".equals(searchtype)) {
			read = "";
		} else if ("1".equals(searchtype)) {
			read = "1";
		} else if ("2".equals(searchtype)) {
			read = "0";
		}
		String allSize = "";
		String readSize = "0";
		String recordCount = "0";
		String readRatio = "";
		// 已读时间查询begin
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String viewSQL_2 = " po.ballotEmp,po.ballotDate ";
		String fromSQL_2 = " com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO po ";
		String whereSQL_2 = " where po.questionnaireId=" + questionnaireId;
		//List list_2 = getListByHQL(viewSQL_2, fromSQL_2, whereSQL_2);
		BoardRoomBD boardRoomBD=new BoardRoomBD();
		List list_2 = boardRoomBD.export2Excel(viewSQL_2, fromSQL_2, whereSQL_2);
		int ydcount = 0;
		if (list_2 != null && list_2.size() > 0) {
			ydcount = list_2.size();
		}
		request.setAttribute("ydcount", ydcount + "");
		request.setAttribute("list_2", list_2);
		String ydIds = "";
		Map timeMap = new HashMap();
		if (list_2 != null && list_2.size() > 0) {
			for (int i = 0; i < list_2.size(); i++) {
				Object[] obj = (Object[]) list_2.get(i);
				String id = obj[0].toString();
				ydIds += obj[0] + ",";
				String date = obj[1] != null ? sdf.format((Date) obj[1]) : "";
				timeMap.put(id, date);
			}
		}
		// 已读时间查询end
		List browserOptionList = getBrowser(questionnaireId, searchuserName,
				read, pageSize, currentPage, domainId, searchOrg);
		Map allBrowsermap = getAllBrowser(questionnaireId, searchuserName,
				read, pageSize, currentPage, domainId, searchOrg);
		if (allBrowsermap != null && allBrowsermap.size() > 0) {
			allSize = (String) allBrowsermap.get("allSize");
			readSize = (String) allBrowsermap.get("readSize");
		}
		if (allSize != null && !allSize.equals("") && !"0".equals(allSize)) {
			readRatio = String
					.valueOf((Float.valueOf(readSize).floatValue() / Float
							.valueOf(allSize).floatValue()) * 100);
		}
		if (readRatio.indexOf(".") > -1) {
			readRatio = readRatio.substring(0, readRatio.indexOf(".") + 2);
		} else {
			readRatio = readRatio + ".0";
		}
		List allBrowserList = (List) allBrowsermap.get("resultList");
		if (browserOptionList != null && browserOptionList.size() > 0) {
			for (int i = 0; i < browserOptionList.size(); i++) {
				Map map = new HashMap();
				Object[] obj_1 = (Object[]) browserOptionList.get(i);
				String orgId = obj_1[0] != null ? obj_1[0] + "" : "";// 组织Id
				String orgName = obj_1[2] != null ? obj_1[2] + "" : "";// 组织名
				map.put("orgid", orgId);
				map.put("orgname", orgName);
				List list = new ArrayList();
				if (allBrowserList != null && allBrowserList.size() > 0) {
					for (int j = 0; j < allBrowserList.size(); j++) {
						Object[] obj_2 = (Object[]) allBrowserList.get(j);
						boolean b = obj_2[2] != null
								&& orgId.equals(obj_2[2] + "");
						if (obj_2[2] != null && orgId.equals(obj_2[2] + "")) {
							Object[] obj = new Object[8];
							obj[0] = obj_2[2];// orgId
							obj[1] = obj_2[0];// empId
							obj[2] = obj_2[1];// empName
							obj[3] = obj_2[5];// empSex
							obj[4] = obj_2[7];// userAccounts
							obj[5] = obj_2[6];// imId
							String flag = "2";
							String date = "";
							if (ydIds.indexOf(obj_2[0].toString()) > -1) {
								flag = "1";
								date = (String) timeMap
										.get(obj_2[0].toString());
							}
							obj[6] = flag;// notRead
							obj[7] = date;
							list.add(obj);
						}
					}

				}
				map.put("userlist", list);
				listAll.add(map);
			}

		}
		return listAll;
	}

	/**
	 * 显示选择某选择项的人员列表
	 * 
	 * @throws Exception
	 */
	public List viewbrowserOption(String searchuserName, String searchOrg,
			String searchtype, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession(true);
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		String themeId = request.getParameter("id");
		
		List listAll = new ArrayList();
		int pageSize = 9999;
		int currentPage = 1;
		List browserOptionList = questionnaireBD.getBrowserOption(themeId,
				searchuserName, pageSize, currentPage, searchOrg);
		List allBrowserOptionList = questionnaireBD.getAllBrowserOption(
				themeId, searchuserName, pageSize, currentPage, searchOrg);
		if (browserOptionList != null && browserOptionList.size() > 0) {
			for (int i = 0; i < browserOptionList.size(); i++) {
				Map map = new HashMap();
				Object[] obj_1 = (Object[]) browserOptionList.get(i);
				String orgId = obj_1[0] != null ? obj_1[0] + "" : "";// 组织Id
				String orgName = obj_1[1] != null ? obj_1[1] + "" : "";// 组织名
				map.put("orgid", orgId);
				map.put("orgname", orgName);
				List list = new ArrayList();
				if (allBrowserOptionList != null
						&& allBrowserOptionList.size() > 0) {
					for (int j = 0; j < allBrowserOptionList.size(); j++) {
						Object[] obj_2 = (Object[]) allBrowserOptionList.get(j);
						if (obj_1[0] != null && obj_2[3] != null
								&& (obj_1[0] + "").equals(obj_2[3] + "")) {
							Object[] obj = new Object[8];
							obj[0] = obj_2[3];// orgId
							obj[1] = obj_2[6];// empId
							obj[2] = obj_2[0];// empName
							obj[3] = obj_2[7];// empSex
							obj[4] = obj_2[2];// userAccounts
							obj[5] = obj_2[5];// imId
							obj[6] = "0";// notRead
							obj[7] = obj_2[4];// notRead
							list.add(obj);
						}
					}

				}
				map.put("userlist", list);
				listAll.add(map);
			}

		}

		return listAll;
	}
	/**
	 * 获取用户的提交问卷的得分  2016/08/11 问卷调查新接口
	 * @param userId
	 * @param questionnaireId
	 * @return
	 */
	public Float getVoteUserScore(String userId, String questionnaireId){
		QuestionnaireEJBBean bean=new QuestionnaireEJBBean();
		Float voteUserScore=new Float(0);
		try {
			voteUserScore = bean.getVoteUserScore(userId, questionnaireId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voteUserScore;
	}
	
	/**
	 * 获取用户的提交问卷的得分  2016/08/15 问卷调查新接口
	 * @param userId
	 * @param questionnaireId
	 * @return
	 */
	public Float getVoteUserScore_muilt(String userId, String questionnaireId,List list){
		QuestionnaireEJBBean bean=new QuestionnaireEJBBean();
		Float voteUserScore=new Float(0);
		try {
			voteUserScore = bean.getVoteUserScore_muilt(userId, questionnaireId,list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voteUserScore;
	}

}
