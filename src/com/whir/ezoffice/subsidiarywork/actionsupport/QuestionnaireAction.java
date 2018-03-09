package com.whir.ezoffice.subsidiarywork.actionsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.ConversionString;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.CollectionUtil;
import com.whir.component.util.JacksonUtil;
import com.whir.evo.weixin.bd.WeiXinBD;
import com.whir.ezoffice.message.bd.MessageBD;
import com.whir.ezoffice.officemanager.bd.EmployeeBD;
import com.whir.ezoffice.subsidiarywork.bd.QuestionnaireBD;
import com.whir.ezoffice.subsidiarywork.ejb.QuestionnaireEJBBean;
import com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO;
import com.whir.ezoffice.subsidiarywork.po.QuesthemePO;
import com.whir.ezoffice.subsidiarywork.po.QuestionnairePO;
import com.whir.integration.realtimemessage.Realtimemessage;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.org.vo.usermanager.EmployeeVO;

/**
 * 问卷调查
 * 
 */
public class QuestionnaireAction extends BaseActionSupport {

	private static Logger logger = Logger.getLogger(QuestionnaireAction.class
			.getName());

	private QuestionnairePO questionnairePO;
	private QuesthemePO questhemePO;
	private AnswerSheetPO AnswerSheetPO;
	private EmployeeVO empPO;
	private String searchTitle;// 标题
	private String searchStatus;// 状态
	private String searchStartDate;// 起始日期
	private String searchEndDate;// 结束日期
	private String isSearch;// 是否做了查询
	private String htrq;// 按日期查询标识
	private String actorId;// 可投票人
	private String examineId;// 可查看人
	private String questionnaireId;// 问卷Id
	private String questhemeId;// 设计问卷Id
	private List questhemeRadioList;// 设计问卷单选
	private List questhemeCheckList;// 设计问卷多选
	private List questhemeEssayList;// 设计问卷问答
	private String statisticAnswerSheetSum;// 答卷
	private String voters;// 投票人
	private String notVoters;// 非投票人
	private String answerSheetId;// 答卷
	private String themeOptionIds;// 答卷
	private String ballotName;// 投票人
	private String ballotDate;// 答卷投票时间
	private String userId;// 判断修改页面按钮显隐权限
	private String themeId;//

	/**
	 * 复制问卷管理
	 */
	public String copyQuestionnaire() {
		HttpSession session = request.getSession(true);
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		java.util.Date date = new java.util.Date();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
		String ids = request.getParameter("ids");
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();

		boolean result = questionnaireBD.copyQuestionnaire(ids);
		if (result) {
			java.util.Date endDate1 = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate1, "1", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);
			printResult("success");
		}
		return null;
	}

	/**
	 * 问卷管理列表页面
	 * 
	 * @return
	 */
	public String questionnaireList() {
		return "questionnaire_list";
	}

	/**
	 * 获得问卷管理列表
	 * 
	 * @return
	 * @throws ParseException
	 */
	public String questionnaireListData() throws ParseException {
		// logger.debug("查询列表开始");
		HttpSession session = request.getSession(true);
		ManagerBD managerBD = new ManagerBD();
		String answerWhere = managerBD.getRightFinalWhere(session.getAttribute(
				"userId").toString(), session.getAttribute("orgId").toString(),
				session.getAttribute("orgIdString").toString(), "问卷管理", "维护",
				"questionnairePO.cratedOrg", "questionnairePO.createdEmp");

		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();

		String where = answerWhere + " and questionnairePO.domainId="
				+ domainId;
		/*
		 * pageSize每页显示记录数，即list jsp中分页部分select中的值
		 */
		int pageSize = com.whir.common.util.CommonUtils
				.getUserPageSize(request);

		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}

		String viewSQL = " questionnairePO.questionnaireId,questionnairePO.title,questionnairePO.actorName,questionnairePO.status";
		String fromSQL = " com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO ";
		String whereSQL = " where " + where;
		String order = " order by questionnairePO.questionnaireId desc";
		/*
		 * varMap，查询参数Map
		 */
		Map varMap = new HashMap();
		if (searchTitle != null && !"".equals(searchTitle)) {
			whereSQL += "and questionnairePO.title like :searchTitle ";
			varMap.put("searchTitle", "%" + searchTitle + "%");
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if ("2".equals(searchStatus)) {
			String databaseType = com.whir.common.config.SystemCommon
					.getDatabaseType();
			/*
			 * if (databaseType.indexOf("mysql") >= 0) { whereSQL += (htrq !=
			 * null && "1".equals(htrq)) ?
			 * " and (questionnairePO.startDate between '" + searchStartDate +
			 * " 00:00:00" + "' and '" + searchEndDate + " 23:59:59" +
			 * "' or questionnairePO.endDate between '" + searchStartDate +
			 * " 00:00:00" + "' and '" + searchEndDate + " 23:59:59" + "')" :
			 * "";
			 * 
			 * } else { whereSQL += ((htrq != null && "1".equals(htrq)) ?
			 * " and (questionnairePO.startDate between  EZOFFICE.FN_STRTODATE('"
			 * + searchStartDate + " 00:00:00" +
			 * "','L') and EZOFFICE.FN_STRTODATE('" + searchEndDate +
			 * " 23:59:59" +
			 * "','L') or questionnairePO.endDate between EZOFFICE.FN_STRTODATE('"
			 * + searchStartDate + " 00:00:00" +
			 * "','L') and EZOFFICE.FN_STRTODATE('" + searchEndDate +
			 * " 23:59:59" + "','L'))" : ""); }
			 */
			if (searchStartDate != null && !"".equals(searchStartDate)) {
				whereSQL += " and questionnairePO.startDate >= :searchStartDate ";
				varMap.put("searchStartDate", sdf.parse(searchStartDate
						+ " 00:00:00"));

			}
			if (searchEndDate != null && !"".equals(searchEndDate)) {
				whereSQL += " and questionnairePO.endDate <= :searchEndDate ";
				varMap.put("searchEndDate", sdf.parse(searchEndDate
						+ " 23:59:59"));

			}

		} else {
			/*
			 * String databaseType = com.whir.common.config.SystemCommon.
			 * getDatabaseType(); if (databaseType.indexOf("mysql") >= 0) {
			 * if(searchStatus!=null && !"".equals(searchStatus)){ whereSQL +=
			 * "and questionnairePO.status = :searchStatus ";
			 * varMap.put("searchStatus", searchStatus); } whereSQL += (htrq !=
			 * null && "1".equals(htrq)) ?
			 * " and (questionnairePO.startDate between '" + searchStartDate +
			 * " 00:00:00" + "' and '" + searchEndDate + " 23:59:59" +
			 * "' or questionnairePO.endDate between '" + searchStartDate +
			 * " 00:00:00" + "' and '" + searchEndDate + " 23:59:59" + "')" :
			 * ""; } else { if(searchStatus!=null && !"".equals(searchStatus)){
			 * whereSQL += "and questionnairePO.status = :searchStatus ";
			 * varMap.put("searchStatus", searchStatus); } whereSQL += ((htrq !=
			 * null && "1".equals(htrq)) ?
			 * " and (questionnairePO.startDate between  EZOFFICE.FN_STRTODATE('"
			 * + searchStartDate + " 00:00:00" +
			 * "','L') and EZOFFICE.FN_STRTODATE('" + searchEndDate +
			 * " 23:59:59" +
			 * "','L') or questionnairePO.endDate between EZOFFICE.FN_STRTODATE('"
			 * + searchStartDate + " 00:00:00" +
			 * "','L') and EZOFFICE.FN_STRTODATE('" + searchEndDate +
			 * " 23:59:59" + "','L'))" : "");
			 * 
			 * }
			 */

			if (searchStatus != null && !"".equals(searchStatus)) {
				whereSQL += "and questionnairePO.status = :searchStatus ";
				varMap.put("searchStatus", searchStatus);
			}

			if (searchStartDate != null && !"".equals(searchStartDate)) {
				whereSQL += " and questionnairePO.startDate >= :searchStartDate ";
				varMap.put("searchStartDate", sdf.parse(searchStartDate
						+ " 00:00:00"));

			}
			if (searchEndDate != null && !"".equals(searchEndDate)) {
				whereSQL += " and questionnairePO.endDate <= :searchEndDate ";
				varMap.put("searchEndDate", sdf.parse(searchEndDate
						+ " 23:59:59"));

			}

		}
		/*
		 * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
		 */
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL,
				order);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		/*
		 * page.setVarMap(varMap);若没有查询条件，可略去此行
		 */
		page.setVarMap(varMap);

		/*
		 * list，数据结果
		 */
		List<Object[]> list = page.getResultList();

		/* ......如果需要对查询结果进行扩充 start....... */
		CollectionUtil collectionUtil = new CollectionUtil();
		List newList = new ArrayList();
		int addLength = 1;// 需要扩充的长度，自定义
		for (Object[] item : list) {
			Object[] obj = collectionUtil.getExtendArray(item, addLength);
			boolean right = false;// 修改设计权限
			if (!CommonUtils.isEmpty(obj[0])) {
				QuestionnaireBD bd = new QuestionnaireBD();
				try {
					List _ll = bd.getBrowser(obj[0] + "", "", "1", 1, 1,
							session.getAttribute("domainId").toString(), "");
					if (_ll != null && _ll.size() > 0) {
						right = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			obj[4] = right;
			newList.add(obj);
		}
		list = newList;
		/* ......如果需要对查询结果进行扩充 end....... */

		/*
		 * pageCount，总页数
		 */
		int pageCount = page.getPageCount();
		/*
		 * recordCount，总记录数
		 */
		int recordCount = page.getRecordCount();
		/*
		 * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
		 */
		JacksonUtil util = new JacksonUtil();
		String[] fields = { "questionnaireId", "title", "actorName", "status",
				"right" };

		String json = util.writeArrayJSON(fields, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		// System.out.println("list------------"+json);
		printResult(G_SUCCESS, json);
		// logger.debug("查询列表结束");

		return null;

	}

	/**
	 * 打开新增问卷管理-新增页面
	 */
	public String addQuestionnaireView() {
		return "questionnaire_addView";
	}

	/**
	 * 新增问卷管理
	 */
	public String saveQuestionnaire() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();

		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Long userID = new Long(session.getAttribute("userId").toString()); // 取当前用户的ID
		Long orgID = new Long(session.getAttribute("orgId").toString()); // 取当前用户的ID

		String from = "com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO";
		String where = "questionnairePO.title ='" + questionnairePO.getTitle()
				+ "' and questionnairePO.domainId=" + domainId;
		boolean isRepeatName = questionnaireBD.isRepeatName(from, where);
		if (isRepeatName) {
			printResult("您填写的内容已存在，请重新填写。");
			return null;
		} else {
			ConversionString conversionActorIdString = new ConversionString(
					actorId);
			String actorUserIds = conversionActorIdString.getUserString();
			questionnairePO.setActorEmp(actorUserIds);
			String actorOrgIds = conversionActorIdString.getOrgString();
			questionnairePO.setActorOrg(actorOrgIds);
			String actorGroupIds = conversionActorIdString.getGroupString();
			questionnairePO.setActorGroup(actorGroupIds);

			ConversionString conversionExamineString = new ConversionString(
					examineId);
			String examineUserIds = conversionExamineString.getUserString();
			questionnairePO.setExamineEmp(examineUserIds);
			String examineOrgIds = conversionExamineString.getOrgString();
			questionnairePO.setExamineOrg(examineOrgIds);
			String examineGroupIds = conversionExamineString.getGroupString();
			questionnairePO.setExamineGroup(examineGroupIds);

			questionnairePO.setCreatedEmp(userID);
			questionnairePO.setCratedOrg(orgID);
			questionnairePO.setDomainId(domainId);

			if (null == questionnairePO.getGrade()
					|| "".equals(questionnairePO.getGrade())) {
				questionnairePO.setGrade(0);
			}
			//提醒类型添加 begin——add by lifan 20150317
			String remindIm = request.getParameter("remind_im");
			String remindSms = request.getParameter("remind_sms");
			String remindMail = request.getParameter("remind_mail");
			String remind = "";
			if (remindIm != null && !"".equals(remindIm)) {
				remind = remindIm;
			}
			if (remindSms != null && !"".equals(remindSms)) {
				if (remind == null || "".equals(remind)) {
					remind = remindSms;
				} else {
					remind = remind + "|" + remindSms;
				}
			}
			if (remindMail != null && !"".equals(remindMail)) {
				if (remind == null || "".equals(remind)) {
					remind = remindMail;
				} else {
					remind = remind + "|" + remindMail;
				}
			}
			logger.debug("remindStr--->"+remind);
			questionnairePO.setRemindType(remind);
			//提醒类型添加 end
			//boolean result = questionnaireBD.addQuestionnaire(questionnairePO);
			Long questionnaireId = questionnaireBD.addQuestionnaire_new(questionnairePO); //2016-09-18 修改需要返回问卷的id 微信推送的时候手机端打开页面的时候需要用
			if (questionnaireId!=0) {
				// 参与人不为空时 发送消息开始 add by lifan
//				String actorIds1 = questionnairePO.getActorEmp().concat(questionnairePO.getActorGroup()).concat(questionnairePO.getActorOrg());
				String actorIds = "";
				logger.debug("actorIds1111----->"+actorId);
				if (actorId != null && 1==questionnairePO.getStatus()) {
					// 发送RTX提醒
					try {
						actorIds = new QuestionnaireEJBBean().getAllUserIsd(actorId);
					} catch (Exception e1) {
						logger.debug("获取用户id异常");
						e1.printStackTrace();
					}
//					actorIds = new ConversionString(actorIds1).getUserIdString();
					logger.debug("actorIds2222----->"+actorIds);
					StringBuffer nodifyTitle = new StringBuffer();
					java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
					nodifyTitle.append(questionnairePO.getTitle()).append("\r\n").append("开始时间：")
					.append(sdf.format(questionnairePO.getStartDate())).append("\r\n").append(" 结束时间：").append(sdf.format(questionnairePO.getEndDate()));
					if ("im".equals(request.getParameter("remind_im"))) {
						ManagerBD mbd = new ManagerBD();
						String actorAcc = mbd.getEmployeesAccounts(actorIds);
						if (!"".equals(actorAcc)) {
							Realtimemessage util = new Realtimemessage();
							try {
								util.sendNotify(actorAcc, "问卷调查提醒",nodifyTitle.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if ("sms".equals(request.getParameter("remind_sms"))) {
						logger.debug("---发送短信开始---");
						// 发送短信提醒
						MessageBD messageSend = new MessageBD();
						String message = "问卷调查提醒" + nodifyTitle.toString();
						messageSend.modelSendMsg(actorIds, message, "",domainId.toString(), userId.toString());
						logger.debug("---发送短信结束---");
					}
					if ("mail".equals(request.getParameter("remind_mail"))) {
						String actorers = questionnairePO.getActorName();
						logger.debug("actorers----->"+actorers);
						String mail_pass = com.whir.common.util.CommonUtils
								.getSessionUserPassword(request);
						String mail_account = com.whir.common.util.CommonUtils
								.getSessionUserAccount(request);
						com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD innermailBD = new com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD();
						try { 
							String titleUrl = "<a href='/defaultroot/questionnaire!questionnaireAnswer.action?questionnaireId="+questionnairePO.getQuestionnaireId()+"'>"+questionnairePO.getTitle()+"</a>";
							boolean flag = innermailBD.sendInnerMail("[问卷调查提醒]"+ nodifyTitle.toString(), titleUrl, "1", userName, Long.parseLong(userId),
									actorId, actorers, domainId + "",mail_account, mail_pass, true);
							logger.debug("flag------>"+flag);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
										
					//2016-09-18 添加微信推送  新需求 start 
					WeiXinBD weixinbd=new WeiXinBD();
					Map params  =new HashMap();
					logger.debug("questionnaireId------>"+questionnaireId);
		            params.put("questionnaireId",questionnaireId);
					String remindTitle="新调查:【"+questionnairePO.getTitle()+"】等待您的参与！";
					logger.debug("remindTitle------>"+remindTitle);					 
		            weixinbd.sendMsg(actorIds,remindTitle,null,null,null,"questionnaire",params); 		           					
					//2016-09-18 添加微信推送  新需求 end
					
				}
				//发送提醒 结束
				java.util.Date endDate1 = new java.util.Date();
				logBD.log(userId, userName, userOrgName,
						" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
						endDate1, "1", "综合办公-问卷调查", session.getAttribute(
								"userIP").toString(), domainId);
				printResult("success");
								
			}
		}
		return null;
	}

	/**
	 * 取需修改问卷管理
	 */
	public String modiQuestionnaireView() {
		HttpSession session = request.getSession(true);
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		QuestionnairePO questionnairePO = questionnaireBD
				.selectQuestionnaireView(Long.valueOf(questionnaireId));

		String actorUserIds = questionnairePO.getActorEmp();
		if (actorUserIds == null || "null".equals(actorUserIds)) {
			actorUserIds = "";
		}
		String actorOrgIds = questionnairePO.getActorOrg();
		if (actorOrgIds == null || "null".equals(actorOrgIds)) {
			actorOrgIds = "";
		}
		String actorGroupIds = questionnairePO.getActorGroup();
		if (actorGroupIds == null || "null".equals(actorGroupIds)) {
			actorGroupIds = "";
		}

		String examineUserIds = questionnairePO.getExamineEmp();
		if (examineUserIds == null || "null".equals(examineUserIds)) {
			examineUserIds = "";
		}
		String examineOrgIds = questionnairePO.getExamineOrg();
		if (examineOrgIds == null || "null".equals(examineOrgIds)) {
			examineOrgIds = "";
		}
		String examineGroupIds = questionnairePO.getExamineGroup();
		if (examineGroupIds == null || "null".equals(examineGroupIds)) {
			examineGroupIds = "";
		}
		actorId = actorUserIds + actorOrgIds + actorGroupIds;
		examineId = examineUserIds + examineOrgIds + examineGroupIds;
		QuestionnaireBD bd = new QuestionnaireBD();
		boolean _flag = false;// 问卷是否有提交了
		try {
			List _ll = bd.getBrowser(questionnaireId + "", "", "1", 1, 1,
					session.getAttribute("domainId").toString(), "");
			if (_ll != null && _ll.size() > 0) {
				_flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ManagerBD managerBD = new ManagerBD();
		boolean isModiRight = managerBD.hasRight(session.getAttribute("userId")
				.toString(), "07*07*02");// 是否有修改权限
		if (isModiRight) {
			request.setAttribute("isModiRight", "yes");
		}
		request.setAttribute("_flag", _flag);
		request.setAttribute("remind_value", questionnairePO.getRemindType());

		this.setQuestionnairePO(questionnairePO);
		return "questionnaire_modiView";
	}

	/**
	 * 更新问卷管理
	 */
	public String updateQuestionnaire() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();

		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Long userID = new Long(session.getAttribute("userId").toString()); // 取当前用户的ID
		Long orgID = new Long(session.getAttribute("orgId").toString()); // 取当前用户的ID
		String from = "com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO";
		String where = "questionnairePO.title ='" + questionnairePO.getTitle()
				+ "' and questionnairePO.questionnaireId <>"
				+ questionnairePO.getQuestionnaireId()
				+ " and questionnairePO.domainId=" + domainId;

		boolean isRepeatName = questionnaireBD.isRepeatName(from, where);
		if (isRepeatName) {
			printResult("您填写的内容已存在，请重新填写。");
			return null;
		} else {
			ConversionString conversionActorIdString = new ConversionString(
					actorId);
			String actorUserIds = conversionActorIdString.getUserString();
			questionnairePO.setActorEmp(actorUserIds);
			String actorOrgIds = conversionActorIdString.getOrgString();
			questionnairePO.setActorOrg(actorOrgIds);
			String actorGroupIds = conversionActorIdString.getGroupString();
			questionnairePO.setActorGroup(actorGroupIds);

			ConversionString conversionExamineString = new ConversionString(
					examineId);
			String examineUserIds = conversionExamineString.getUserString();
			questionnairePO.setExamineEmp(examineUserIds);
			String examineOrgIds = conversionExamineString.getOrgString();
			questionnairePO.setExamineOrg(examineOrgIds);
			String examineGroupIds = conversionExamineString.getGroupString();
			questionnairePO.setExamineGroup(examineGroupIds);

			questionnairePO.setCreatedEmp(userID);
			questionnairePO.setCratedOrg(orgID);
			questionnairePO.setDomainId(domainId);
			if (null == questionnairePO.getGrade()
					|| "".equals(questionnairePO.getGrade())) {
				questionnairePO.setGrade(0);
			}
			//提醒类型添加 begin——add by lifan 20150317
			String remindIm = request.getParameter("remind_im");
			String remindSms = request.getParameter("remind_sms");
			String remindMail = request.getParameter("remind_mail");
			String remind = "";
			if (remindIm != null && !"".equals(remindIm)) {
				remind = remindIm;
			}
			if (remindSms != null && !"".equals(remindSms)) {
				if (remind == null || "".equals(remind)) {
					remind = remindSms;
				} else {
					remind = remind + "|" + remindSms;
				}
			}
			if (remindMail != null && !"".equals(remindMail)) {
				if (remind == null || "".equals(remind)) {
					remind = remindMail;
				} else {
					remind = remind + "|" + remindMail;
				}
			}
			questionnairePO.setRemindType(remind);
			//提醒类型添加 end
			boolean result = questionnaireBD.updateQuestionnaire(questionnairePO);
			if (result) {
				// 参与人不为空时 发送消息开始 add by lifan
//				String actorIds1 = questionnairePO.getActorEmp().concat(questionnairePO.getActorGroup()).concat(questionnairePO.getActorOrg());
				String actorIds = "";
				logger.debug("actorIds1111----->"+actorId);
				if (actorId != null && 1==questionnairePO.getStatus()) {
					// 发送RTX提醒
					try {
						actorIds = new QuestionnaireEJBBean().getAllUserIsd(actorId);
					} catch (Exception e1) {
						logger.debug("获取用户id异常");
						e1.printStackTrace();
					}
//					actorIds = new ConversionString(actorIds1).getUserIdString();
					logger.debug("actorIds2222----->"+actorIds);
					StringBuffer nodifyTitle = new StringBuffer();
					java.text.DateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
					nodifyTitle.append(questionnairePO.getTitle()).append("\r\n").append("开始时间：")
					.append(sdf.format(questionnairePO.getStartDate())).append("\r\n").append(" 结束时间：").append(sdf.format(questionnairePO.getEndDate()));
					if ("im".equals(request.getParameter("remind_im"))) {
						ManagerBD mbd = new ManagerBD();
						String actorAcc = mbd.getEmployeesAccounts(actorIds);
						if (!"".equals(actorAcc)) {
							Realtimemessage util = new Realtimemessage();
							try {
								util.sendNotify(actorAcc, "问卷调查提醒",nodifyTitle.toString());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					if ("sms".equals(request.getParameter("remind_sms"))) {
						logger.debug("---发送短信开始---");
						// 发送短信提醒
						MessageBD messageSend = new MessageBD();
						String message = "问卷调查提醒" + nodifyTitle.toString();
						messageSend.modelSendMsg(actorIds, message, "",domainId.toString(), userId.toString());
						logger.debug("---发送短信结束---");
					}
					if ("mail".equals(request.getParameter("remind_mail"))) {
						String actorers = questionnairePO.getActorName();
						logger.debug("actorers----->"+actorers);
						String mail_pass = com.whir.common.util.CommonUtils
								.getSessionUserPassword(request);
						String mail_account = com.whir.common.util.CommonUtils
								.getSessionUserAccount(request);
						com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD innermailBD = new com.whir.ezoffice.personalwork.innermailbox.bd.InnerMailBD();
						try { 
							String titleUrl = "<a href='/defaultroot/questionnaire!questionnaireAnswer.action?questionnaireId="+questionnairePO.getQuestionnaireId()+"'>"+questionnairePO.getTitle()+"</a>";
							boolean flag = innermailBD.sendInnerMail("[问卷调查提醒]"+ nodifyTitle.toString(), titleUrl, "1", userName, Long.parseLong(userId),
									actorId, actorers, domainId + "",mail_account, mail_pass, true);
							logger.debug("flag------>"+flag);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					//2016-09-18 添加微信推送  新需求 start
					WeiXinBD weixinbd=new WeiXinBD();
					Map params  =new HashMap();					 
		            params.put("questionnaireId",questionnairePO.getQuestionnaireId());
					String remindTitle="新调查:【"+questionnairePO.getTitle()+"】等待您的参与！";				 
		            weixinbd.sendMsg(actorIds,remindTitle,null,null,null,"questionnaire",params);    					
					//2016-09-18 添加微信推送  新需求 end
				}
				//发送提醒 结束
				java.util.Date endDate1 = new java.util.Date();
				logBD.log(userId, userName, userOrgName,
						" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
						endDate1, "1", "综合办公-问卷调查", session.getAttribute(
								"userIP").toString(), domainId);
				printResult("success");
			}
		}
		return null;
	}

	/**
	 * 删除问卷管理
	 */
	public String deleteQuestionnaire() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		ManagerBD managerBD = new ManagerBD();
		boolean result = questionnaireBD.deleteQuestionnaire(Long
				.valueOf(questionnaireId));
		if (result) {
			java.util.Date endDate = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate, "3", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);
			printResult("success");

		}
		return null;
	}

	/**
	 * 批量删除问卷管理
	 */
	public String deleteBatchQuestionnaire() {
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		boolean result = false;
		String questionnaireIds = request.getParameter("questionnaireId");
		if (questionnaireIds != null) {
			result = questionnaireBD.deleteBatchQuestionnaire(questionnaireIds);
		}
		if (result)
			printResult("success");
		return null;
	}

	/**
	 * 问卷预览
	 */
	public String questionnairePreview() {
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Map map = questionnaireBD.selectQuestionnairePreview(Long
				.valueOf(questionnaireId));
		QuestionnairePO questionnairePO = (QuestionnairePO) map
				.get("questionnaire");
		this.setQuestionnairePO(questionnairePO);

		List questhemeRadioList = (List) map.get("questhemeRadio");// 单选题
		this.setQuesthemeRadioList(questhemeRadioList);
		if (questhemeRadioList.size() != 0) {
			request.setAttribute("questhemeRadioList", questhemeRadioList);
		}

		List questhemeCheckList = (List) map.get("questhemeCheck");// 多选题
		this.setQuesthemeCheckList(questhemeCheckList);
		if (questhemeCheckList.size() != 0) {
			request.setAttribute("questhemeCheckList", questhemeCheckList);
		} else {
			request.setAttribute("questhemeCheckList", "NULL");
		}

		List questhemeEssayList = (List) map.get("questhemeEssay");// 问答题
		this.setQuesthemeEssayList(questhemeEssayList);
		if (questhemeEssayList.size() != 0) {
			request.setAttribute("questhemeEssayList", questhemeEssayList);
		} else {
			request.setAttribute("questhemeEssayList", "NULL");
		}

		request.setAttribute("title", questionnairePO.getTitle());

		return "questionnaire_preview";
	}

	/**
	 * 打开问卷设计-列表页面
	 */
	public String questhemeList() {
		return "questheme_list";
	}

	/**
	 * 打开问卷设计-获取列表页面信息
	 */
	public String questhemeListData() {
		HttpSession session = request.getSession(true);
		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();

		/*
		 * pageSize每页显示记录数，即list jsp中分页部分select中的值
		 */
		int pageSize = com.whir.common.util.CommonUtils
				.getUserPageSize(request);

		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}

		String viewSQL = " questhemePO.questhemeId,questhemePO.title,questhemePO.type,questhemePO.orderCode ";
		String fromSQL = " com.whir.ezoffice.subsidiarywork.po.QuesthemePO questhemePO ";
		String whereSQL = " where questhemePO.questionnaire.questionnaireId = '"
				+ questionnaireId + "' and questhemePO.domainId=" + domainId;
		String order = " order by questhemePO.orderCode ";
		/*
		 * varMap，查询参数Map
		 */
		Map varMap = new HashMap();
		if (searchTitle != null && !"".equals(searchTitle)) {
			whereSQL += "and questhemePO.title like :searchTitle ";
			varMap.put("searchTitle", "%" + searchTitle + "%");
		}
		if (!"3".equals(searchStatus)) {
			if (searchStatus != null && !"".equals(searchStatus)) {
				whereSQL += "and questhemePO.type = :searchStatus ";
				varMap.put("searchStatus", searchStatus);
			}
		}
		/*
		 * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
		 */
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL,
				order);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		/*
		 * page.setVarMap(varMap);若没有查询条件，可略去此行
		 */
		page.setVarMap(varMap);

		/*
		 * list，数据结果
		 */
		List<Object[]> list = page.getResultList();

		/* ......如果需要对查询结果进行扩充 start....... */

		// CollectionUtil collectionUtil = new CollectionUtil();
		// List newList = new ArrayList();
		// int addLength = 1;//需要扩充的长度，自定义

		/* ......如果需要对查询结果进行扩充 end....... */

		/*
		 * pageCount，总页数
		 */
		int pageCount = page.getPageCount();
		/*
		 * recordCount，总记录数
		 */
		int recordCount = page.getRecordCount();
		/*
		 * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
		 */
		JacksonUtil util = new JacksonUtil();
		String[] fields = { "questhemeId", "title", "type", "orderCode" };

		String json = util.writeArrayJSON(fields, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);
		return null;
	}

	/**
	 * 打开新增问卷设计-新增页面
	 */
	public String addQuesthemeView() {
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		QuestionnairePO questionnairePO = questionnaireBD
				.selectQuestionnaireView(Long.valueOf(questionnaireId));
		Integer grade = questionnairePO.getGrade();
		request.setAttribute("grade", grade);
		return "questheme_addView";
	}

	/**
	 * 新增问卷设计
	 */
	public String addQuestheme() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		String type = questhemePO.getType() + "";
		String grade = request.getParameter("gradeHidden");
		
	 
		String[] solutionTitle = null;
		String[] pitchon = null;
		String[] optionScore = null;
		
		String[] imgRealName=null;
		String[] imgSaveName=null;
		String[] customAnswer=null;
		
		if (questhemePO.getSelanswernum() == null
				|| "".equals(questhemePO.getSelanswernum().toString())
				|| "null".equals(questhemePO.getSelanswernum().toString())) {
			questhemePO.setSelanswernum(null);
		}

		questhemePO.setDomainId(domainId);
		
		if (type != null && "0".equals(type)) {
			solutionTitle = request.getParameterValues("solutionTitle");
			optionScore = request.getParameterValues("optionScore");
			//添加问卷调查上传图片和自定义答案  2016/08/08 start
			customAnswer=request.getParameterValues("customAnswer");
			if(solutionTitle!=null){
				imgRealName=new String[solutionTitle.length];
				imgSaveName=new String[solutionTitle.length];
				for(int i=0;i<solutionTitle.length;i++){
					imgRealName[i]=request.getParameter("opImgRealName"+i);
					imgSaveName[i]=request.getParameter("opImgSaveName"+i);						
				}
			}
			//添加问卷调查上传图片和自定义答案  2016/08/08 end
		}
		if (type != null && "1".equals(type)) {
			solutionTitle = request.getParameterValues("solutionTitle2");
			pitchon = request.getParameterValues("pitchon");
			//添加问卷调查上传图片和自定义答案  2016/08/08 start
			customAnswer=request.getParameterValues("customAnswer");
			if(solutionTitle!=null){
				imgRealName=new String[solutionTitle.length];
				imgSaveName=new String[solutionTitle.length];
				for(int j=0;j<solutionTitle.length;j++){
					imgRealName[j]=request.getParameter("opImgRealName"+j);
					imgSaveName[j]=request.getParameter("opImgSaveName"+j);						
				}
			}
			//添加问卷调查上传图片和自定义答案  2016/08/08 end
		}
		if (type != null && "2".equals(type)) {
		}
		questhemePO.setQuestionnaireId(Long.valueOf(questionnaireId));
		questhemePO.setType(new Integer(type));
		String from = "com.whir.ezoffice.subsidiarywork.po.QuesthemePO questhemePO";
		String where = "questhemePO.title ='" + questhemePO.getTitle()
				+ "' and questhemePO.questionnaire.questionnaireId="
				+ questionnaireId;
		boolean isRepeatName = questionnaireBD.isRepeatName(from, where);
		if (isRepeatName) {
			request.setAttribute("grade", grade);
			return null;
		} else {
			//boolean result = questionnaireBD.addQuestheme(questhemePO,solutionTitle, optionScore, pitchon);
			boolean result = questionnaireBD.addQuestheme_new(questhemePO, solutionTitle, optionScore, pitchon, imgRealName, imgSaveName, customAnswer);
			if (result) {
				java.util.Date endDate = new java.util.Date();
				logBD.log(userId, userName, userOrgName,
						" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
						endDate, "1", "综合办公-问卷调查", session.getAttribute(
								"userIP").toString(), domainId);
				printResult(G_SUCCESS);

			}

			if (!result) {
				return null;
			}
		}

		return null;
	}

	/**
	 * 取需修改问卷设计
	 */
	public String modiQuesthemeView() { 
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
 
		//Map map = questionnaireBD.selectQuesthemeView(Long.valueOf(questhemeId));
		Map map = questionnaireBD.selectQuesthemeView_new(Long.valueOf(questhemeId));//(达蒙数据库获取时候单选/多选分数为0，获取QuesthemePO报错：数字溢出 2016-09-03)
		QuesthemePO questhemePO = (QuesthemePO) map.get("questheme");
		List themeOptionList = (List) map.get("themeOption");
		QuestionnairePO questionnairePO = questionnaireBD
				.selectQuestionnaireView(Long.valueOf(questionnaireId));
		Integer grade = questionnairePO.getGrade();
		// System.out.println("grade==modiQuesthemeView==="+grade);
		this.setQuesthemePO(questhemePO);
		this.setQuestionnairePO(questionnairePO);
		request.setAttribute("grade", grade);
		request.setAttribute("themeOptionList", themeOptionList);
		request.setAttribute("listCount", themeOptionList.size());
		
		return "questheme_modiView";
	}

	/**
	 * 更新问卷设计
	 */
	public String updateQuestheme() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();

		String type = questhemePO.getType() + ""; 
		String[] solutionTitle = null;
		String[] pitchon = null;
		String[] optionScore = null;

		String[] imgRealName=null;
		String[] imgSaveName=null;
		String[] customAnswer=null;
		
		if (questhemePO.getSelanswernum() == null
				|| "".equals(questhemePO.getSelanswernum())
				|| "null".equals(questhemePO.getSelanswernum())) {
			questhemePO.setSelanswernum(null);
		}

		if (type != null && "0".equals(type)) {
			solutionTitle = request.getParameterValues("solutionTitle");
			optionScore = request.getParameterValues("optionScore");
			//添加问卷调查上传图片和自定义答案  2016/08/08 start
			customAnswer=request.getParameterValues("customAnswer");
			if(solutionTitle!=null){
				imgRealName=new String[solutionTitle.length];
				imgSaveName=new String[solutionTitle.length];
				for(int i=0;i<solutionTitle.length;i++){
					imgRealName[i]=request.getParameter("opImgRealName"+i);
					imgSaveName[i]=request.getParameter("opImgSaveName"+i);						
				}
			}
			//添加问卷调查上传图片和自定义答案  2016/08/08 end
		}
		if (type != null && "1".equals(type)) {
			solutionTitle = request.getParameterValues("solutionTitle2");
			pitchon = request.getParameterValues("pitchon");
			//添加问卷调查上传图片和自定义答案  2016/08/08 start
			customAnswer=request.getParameterValues("customAnswer");
			if(solutionTitle!=null){
				imgRealName=new String[solutionTitle.length];
				imgSaveName=new String[solutionTitle.length];
				for(int j=0;j<solutionTitle.length;j++){
					imgRealName[j]=request.getParameter("opImgRealName"+j);
					imgSaveName[j]=request.getParameter("opImgSaveName"+j);						
				}
			}
			//添加问卷调查上传图片和自定义答案  2016/08/08 end
		}
		questhemePO.setQuesthemeId(Long.valueOf(questhemeId));
		questhemePO.setQuestionnaireId(Long.valueOf(questionnaireId));
		questhemePO.setType(new Integer(type));

		/*boolean result = questionnaireBD.updateQuestheme(questhemePO,
				solutionTitle, optionScore, pitchon);*/
		boolean result = questionnaireBD.updateQuestheme_new(questhemePO,
				solutionTitle, optionScore, pitchon,imgRealName,imgSaveName,customAnswer);//添加问卷调查上传图片和自定义答案  2016/08/08 start
		if (result) {
			java.util.Date endDate = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate, "2", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);
			printResult(G_SUCCESS);

		}

		if (!result) {
			return null;
		}
		return null;
	}

	/**
	 * 只读问卷设计
	 */
	public String showQuesthemeView() {
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Map map = questionnaireBD
				.selectQuesthemeView(Long.valueOf(questhemeId));
		QuesthemePO questhemePO = (QuesthemePO) map.get("questheme");
		List themeOptionList = (List) map.get("themeOption");
		QuestionnairePO questionnairePO = questionnaireBD
				.selectQuestionnaireView(Long.valueOf(questionnaireId));

		Integer grade = questionnairePO.getGrade();
		this.setQuesthemePO(questhemePO);
		this.setQuestionnairePO(questionnairePO);
		request.setAttribute("themeOptionList", themeOptionList);
		return "questheme_modiView";
	}

	/**
	 * 删除单条问卷设计
	 */
	public String deleteQuestheme() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		boolean result = true;
		if (questhemeId != null && !"".equals(questhemeId)) {
			result = questionnaireBD.deleteQuestheme(Long.valueOf(questhemeId));
			if (result) {
				java.util.Date endDate = new java.util.Date();
				logBD.log(userId, userName, userOrgName,
						" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
						endDate, "3", "综合办公-问卷调查", session.getAttribute(
								"userIP").toString(), domainId);
				printResult(G_SUCCESS);

			}

		}
		if (!result) {
			request.setAttribute("delResult", "noDelete"); // 删除返回结果：不能删除，因为此问卷已生成答卷了。
			printResult("不能删除，因为此问卷已生成答卷了");
			return null;
		}

		return null;
	}

	/**
	 * 批量删除问卷设计
	 */
	public String deleteBatchQuestheme() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		String questhemeIds = request.getParameter("questhemeId");
		boolean result = questionnaireBD.deleteBatchQuestheme(questhemeIds);
		if (result) {
			java.util.Date endDate = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate, "3", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);
			printResult(G_SUCCESS);

		}

		if (!result) {
			return null;
		}
		return null;
	}

	/**
	 * 打开答卷管理-列表页面
	 */
	public String questionnaireAnswerList() {
		return "questionnaire_answerList";
	}

	/**
	 * 打开答卷管理-列表数据
	 */
	public String questionnaireAnswerListData() {
		HttpSession session = request.getSession(true);
		ManagerBD managerBD = new ManagerBD();
		String answerWhere = managerBD.getScopeFinalWhere(session.getAttribute(
				"userId").toString(), session.getAttribute("orgId").toString(),
				session.getAttribute("orgIdString").toString(),
				"questionnairePO.examineEmp", "questionnairePO.examineOrg",
				"questionnairePO.examineGroup");
		String where = managerBD.getRightFinalWhere(session.getAttribute(
				"userId").toString(), session.getAttribute("orgId").toString(),
				"07*07*02", "questionnairePO.cratedOrg",
				"questionnairePO.createdEmp");
		// 增加对兼职的支持----------------------------------------------------
		EmployeeBD empBD = new EmployeeBD();
		List singleEmpList = empBD.selectSingle(new Long(session.getAttribute(
				"userId").toString()));
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
					answerWhere += " or questionnairePO.examineOrg like '%*"
							+ ___tmpArr[_i0] + "*%' ";
				}
			}
		}
		answerWhere = "( " + answerWhere + ") ";

		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();

		/*
		 * pageSize每页显示记录数，即list jsp中分页部分select中的值
		 */
		int pageSize = com.whir.common.util.CommonUtils
				.getUserPageSize(request);

		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}

		String viewSQL = " distinct questionnairePO.questionnaireId,questionnairePO.title,questionnairePO.actorName,questionnairePO.status,questionnairePO.grade ";
		String fromSQL = " com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO ";
		String whereSQL = " where " + answerWhere
				+ " and questionnairePO.domainId=" + domainId;
		String order = " order by questionnairePO.questionnaireId desc ";

		/*
		 * varMap，查询参数Map
		 */
		Map varMap = new HashMap();
		if (searchTitle != null && !"".equals(searchTitle)) {
			whereSQL += " and questionnairePO.title like :searchTitle ";
			varMap.put("searchTitle", "%" + searchTitle + "%");
		}

		if (!"2".equals(searchStatus)) {
			if (searchStatus != null && !"".equals(searchStatus)) {
				whereSQL += " and questionnairePO.status = :searchStatus ";
				varMap.put("searchStatus", searchStatus);
			}
		}

		/*
		 * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
		 */
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL,
				order);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		/*
		 * page.setVarMap(varMap);若没有查询条件，可略去此行
		 */
		page.setVarMap(varMap);

		/*
		 * list，数据结果
		 */
		List<Object[]> list = page.getResultList();

		/* ......如果需要对查询结果进行扩充 start....... */
		CollectionUtil collectionUtil = new CollectionUtil();
		List newList = new ArrayList();
		int addLength = 1;// 需要扩充的长度，自定义

		/* ......如果需要对查询结果进行扩充 end....... */

		/*
		 * pageCount，总页数
		 */
		int pageCount = page.getPageCount();
		/*
		 * recordCount，总记录数
		 */
		int recordCount = page.getRecordCount();
		/*
		 * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
		 */
		JacksonUtil util = new JacksonUtil();
		String[] fields = { "questionnaireId", "title", "actorName", "status",
				"grade" };

		String json = util.writeArrayJSON(fields, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);

		request.setAttribute("answerList", list);

		return null;
	}

	/**
	 * 打开答卷管理-按投票人列表页面
	 */
	public String actorNameList() {
		return "answer_actorNameList";
	}

	/**
	 * 打开答卷管理-按投票人列表数据
	 * 
	 * @throws ParseException
	 */
	public String actorNameListData() throws ParseException {
		HttpSession session = request.getSession(true);
		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		String selectValue = "answerSheetPO.answerSheetId";
		String from = "com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.ezoffice.subsidiarywork.po.QuesthemePO questhemePO";
		String where = "answerSheetPO.questionnaireId=questhemePO.questionnaire.questionnaireId and questhemePO.type=2 and answerSheetPO.domainId="
				+ domainId;
		String gradeAnswerSheetIds = questionnaireBD.maintenance(selectValue,
				from, where);
		request.setAttribute("gradeAnswerSheetIds", gradeAnswerSheetIds);

		/*
		 * pageSize每页显示记录数，即list jsp中分页部分select中的值
		 */
		int pageSize = com.whir.common.util.CommonUtils
				.getUserPageSize(request);
		/*
		 * orderByFieldName，用来排序的字段
		 */
		String orderByFieldName = request.getParameter("orderByFieldName") != null ? request
				.getParameter("orderByFieldName")
				: "";
		/*
		 * orderByType，排序的类型
		 */
		String orderByType = request.getParameter("orderByType") != null ? request
				.getParameter("orderByType")
				: "";

		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}

		String viewSQL = " answerSheetPO.answerSheetId,empPO.empName,orgPO.orgNameString,answerSheetPO.ballotDate,answerSheetPO.readedman ";
		String fromSQL = " com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO,com.whir.org.vo.usermanager.EmployeeVO empPO join empPO.organizations orgPO ";
		//String whereSQL = " where answerSheetPO.ballotEmp=empPO.empId and answerSheetPO.questionnaireId ='"+ questionnaireId + "' and orgPO.domainId=" + domainId;
		String whereSQL = " where answerSheetPO.ballotEmp=empPO.empId and orgPO.domainId=" + domainId;//安全性   2016/03/25 
		String order = " order by answerSheetPO.questionnaireId desc ";

		if (orderByFieldName != null && !"".equals(orderByFieldName)) {
			if ("ballotDate".equals(orderByFieldName)) {
				order = " order by answerSheetPO." + orderByFieldName + " "
						+ orderByType;
			} else if ("orgNameString".equals(orderByFieldName)) {
				order = " order by orgPO." + orderByFieldName + " "
						+ orderByType;
			}
		}

		/*
		 * varMap，查询参数Map
		 */
		Map varMap = new HashMap();
		//安全性  2016/03/25  start
		whereSQL += " and answerSheetPO.questionnaireId =:questionnaireId_s ";
		varMap.put("questionnaireId_s", questionnaireId);
		//安全性  2016/03/25  end
		if (!"2".equals(searchStatus)) {
			if (searchStatus != null && !"".equals(searchStatus)) {
				whereSQL += " and questionnairePO.status = :searchStatus ";
				varMap.put("searchStatus", searchStatus);
			}
		}

		if (actorId != null && !"".equals(actorId)) {
			if (actorId.indexOf("$") >= 0) {
				whereSQL += " and answerSheetPO.ballotEmp = "
						+ actorId.substring(actorId.indexOf("$") + 1, actorId
								.lastIndexOf("$"));
			} else {
				whereSQL += " and answerSheetPO.ballotEmp = " + actorId;
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (searchStartDate != null && !"".equals(searchStartDate)) {
			whereSQL += " and answerSheetPO.ballotDate >= :searchStartDate ";
			varMap.put("searchStartDate", sdf.parse(searchStartDate
					+ " 00:00:00"));

		}
		if (searchEndDate != null && !"".equals(searchEndDate)) {
			whereSQL += " and answerSheetPO.ballotDate <= :searchEndDate ";
			varMap.put("searchEndDate", sdf.parse(searchEndDate + " 23:59:59"));

		}

		/*
		 * String databaseType =
		 * com.whir.common.config.SystemCommon.getDatabaseType(); if
		 * (databaseType.indexOf("mysql") >= 0) { whereSQL += (htrq != null &&
		 * "1".equals(htrq)) ? " and answerSheetPO.ballotDate between '" +
		 * searchStartDate + " 00:00:00" + "' and '" + searchEndDate +
		 * " 23:59:59" + "'" : "";
		 * 
		 * } else { whereSQL += (htrq != null && "1".equals(htrq)) ?
		 * " and answerSheetPO.ballotDate between EZOFFICE.FN_STRTODATE('" +
		 * searchStartDate + " 00:00:00" + "','L') and EZOFFICE.FN_STRTODATE('"
		 * + searchEndDate + " 23:59:59" + "','L')" : "";
		 * 
		 * }
		 */

		/*
		 * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
		 */
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL,
				order);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		/*
		 * page.setVarMap(varMap);若没有查询条件，可略去此行
		 */
		page.setVarMap(varMap);

		/*
		 * list，数据结果
		 */
		List<Object[]> list = page.getResultList();

		/* ......如果需要对查询结果进行扩充 start....... */
		CollectionUtil collectionUtil = new CollectionUtil();
		List newList = new ArrayList();
		int addLength = 1;// 需要扩充的长度，自定义

		/* ......如果需要对查询结果进行扩充 end....... */

		/*
		 * pageCount，总页数
		 */
		int pageCount = page.getPageCount();
		/*
		 * recordCount，总记录数
		 */
		int recordCount = page.getRecordCount();
		/*
		 * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
		 */
		JacksonUtil util = new JacksonUtil();
		String[] fields = { "answerSheetId", "empName", "orgNameString",
				"ballotDate", "readedman" };

		String json = util.writeArrayJSON(fields, list);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
				+ "},data:" + json + "}";
		printResult(G_SUCCESS, json);

		return null;
	}

	/**
	 * 评分
	 */
	public String answerGraded() {
		HttpSession session = request.getSession(true);
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();

		questionnaireBD.setReadedUser(request.getSession(true).getAttribute(
				"userId").toString(), Long.valueOf(answerSheetId));

		this.setUserId(request.getParameter("showuserId"));
		Map map = questionnaireBD.selectQuestionnairePreview(Long
				.valueOf(questionnaireId));
		QuestionnairePO questionnairePO = (QuestionnairePO) map
				.get("questionnaire");
		this.setQuestionnairePO(questionnairePO);
		List questhemeRadioList = (List) map.get("questhemeRadio");
		this.setQuesthemeRadioList(questhemeRadioList);
		if (questhemeRadioList.size() != 0) {
			request.setAttribute("questhemeRadioList", questhemeRadioList);
		}

		List questhemeCheckList = (List) map.get("questhemeCheck");
		this.setQuesthemeCheckList(questhemeCheckList);
		if (questhemeCheckList.size() != 0) {
			request.setAttribute("questhemeCheckList", questhemeCheckList);
		}
		request.setAttribute("title", questionnairePO.getTitle());

		Map map2 = questionnaireBD.selectAnswerPreview(Long
				.valueOf(answerSheetId));
		List answerSheetList = (List) map2.get("answerSheet");
		if (answerSheetList.size() != 0) {
			Object[] obj = (Object[]) answerSheetList.get(0);
			String ballotName = obj[0].toString();
			this.setBallotName(ballotName);
			request.setAttribute("ballotName", ballotName);
			String ballotDate = obj[1].toString();
			this.setBallotDate(ballotDate);
			request.setAttribute("ballotDate", ballotDate);
		}
		String themeOptionIds = (String) map2.get("themeOptionIds");
		this.setThemeOptionIds(themeOptionIds);
		if (themeOptionIds != null && !"".equals(themeOptionIds)) {
			request.setAttribute("themeOptionIds", themeOptionIds);
		}
		List answerSheetContentPOList = (List) map2.get("answerSheetContentPO");
		if (answerSheetContentPOList != null
				&& answerSheetContentPOList.size() != 0) {
			request
					.setAttribute("questhemeEssayList",
							answerSheetContentPOList);
		}
		// otherMap
		Map otherMap = (Map) map2.get("otherMap");
		request.setAttribute("otherMap", otherMap);
		return "answer_Graded";
	}

	/**
	 * 提交答卷评分
	 */
	public String saveAnswerGraded() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		List essayList = new ArrayList(); // 问答

		String[] essayQuesthemeID = request
				.getParameterValues("essayQuesthemeID");
		if (essayQuesthemeID != null) {
			for (int i = 0; i < essayQuesthemeID.length; i++) {
				essayList.add(essayQuesthemeID[i].toString());
				String score = request.getParameter("Text_"
						+ essayQuesthemeID[i]);
				essayList.add(score);
			}
		}

		boolean result = questionnaireBD.addAnswerGraded(essayList);
		if (result) {
			java.util.Date endDate = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate, "1", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);
			printResult(G_SUCCESS);

		}

		if (!result) {
			return null;
		}

		return null;
	}

	/**
	 * 已交答卷预览
	 */
	public String answerPreview() {
		return "answer_Preview";
	}

	/**
	 * 答卷统计
	 */
	public String answerStatisticList() {
		HttpSession session = request.getSession(true);
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Map map = questionnaireBD.selectQuestionnairePreview(Long
				.valueOf(questionnaireId));
		QuestionnairePO questionnairePO = (QuestionnairePO) map
				.get("questionnaire");
		this.setQuestionnairePO(questionnairePO);

		List questhemeRadioList = (List) map.get("questhemeRadio");
		this.setQuesthemeRadioList(questhemeRadioList);
		if (questhemeRadioList.size() != 0) {
			request.setAttribute("questhemeRadioList", questhemeRadioList);
		}

		List questhemeCheckList = (List) map.get("questhemeCheck");
		this.setQuesthemeCheckList(questhemeCheckList);
		if (questhemeCheckList.size() != 0) {
			request.setAttribute("questhemeCheckList", questhemeCheckList);
		}

		List statisticAnswerSheetOptionSumList = (List) map
				.get("statisticAnswerSheetOptionSum");
		if (statisticAnswerSheetOptionSumList.size() != 0) {
			request.setAttribute("statisticAnswerSheetOptionSumList",
					statisticAnswerSheetOptionSumList);
		}

		String statisticAnswerSheetSum = (String) map
				.get("statisticAnswerSheetSum");
		this.setStatisticAnswerSheetSum(statisticAnswerSheetSum);
		if (statisticAnswerSheetSum != null) {
			request.setAttribute("statisticAnswerSheetSum",
					statisticAnswerSheetSum);
		}

		String voters = (String) map.get("voters");
		this.setVoters(voters);
		if (voters != null) {
			request.setAttribute("voters", voters);
		}

		String notVoters = (String) map.get("notVoters");
		this.setNotVoters(notVoters);
		if (notVoters != null) {
			request.setAttribute("notVoters", notVoters);
		}

		request.setAttribute("title", questionnairePO.getTitle());

		// 其它答案
		List otherAnswersList = (List) map.get("otherAnswers");
		if (otherAnswersList.size() != 0) {
			request.setAttribute("otherAnswersList", otherAnswersList);
		}
		return "answer_statisticList";

	}

	/**
	 * 答卷统计数据
	 */
	public String answerStatisticListData() {
		return null;

	}

	/**
	 * 显示投票人列表
	 * 
	 * @throws Exception
	 */
	public String viewbrowserUser() throws Exception {
		HttpSession session = request.getSession(true);
		String domainId = session.getAttribute("domainId") == null ? "0"
				: session.getAttribute("domainId").toString();
		String read = request.getParameter("read") == null ? "" : request
				.getParameter("read");
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		List listAll = new ArrayList();
		int pageSize = 9999;
		int currentPage = 1;
		String searchName = request.getParameter("searchName") == null ? ""
				: request.getParameter("searchName");
		String searchOrgName = request.getParameter("searchOrgName") == null ? ""
				: request.getParameter("searchOrgName");

		String allSize = "";
		String readSize = "0";
		String recordCount = "0";
		String readRatio = "";

		List browserOptionList = questionnaireBD.getBrowser(questionnaireId,
				searchName, read, pageSize, currentPage, domainId,
				searchOrgName);
		Map allBrowsermap = questionnaireBD.getAllBrowser(questionnaireId,
				searchName, read, pageSize, currentPage, domainId,
				searchOrgName);
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

		request.setAttribute("readRatio", readRatio);
		request.setAttribute("read", read);
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
							Object[] obj = new Object[7];
							obj[0] = obj_2[2];// orgId
							obj[1] = obj_2[0];// empId
							obj[2] = obj_2[1];// empName
							obj[3] = obj_2[5];// empSex
							obj[4] = obj_2[7];// userAccounts
							obj[5] = obj_2[6];// imId
							obj[6] = read;// notRead
							list.add(obj);
						}
					}

				}
				map.put("userlist", list);
				listAll.add(map);
			}

		}
		request.setAttribute("listAll", listAll);
		return "answer_viewbrowserUser";
	}

	/**
	 * 显示投票人列表数据
	 */
	public String viewbrowserUserData() {
		return null;
	}

	/**
	 * 显示选择某选择项的人员列表
	 * 
	 * @throws Exception
	 */
	public String viewbrowserOption() throws Exception {
		HttpSession session = request.getSession(true);
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		List listAll = new ArrayList();
		int pageSize = 9999;
		int currentPage = 1;
		String searchName = request.getParameter("searchName") == null ? ""
				: request.getParameter("searchName");
		String searchOrgName = request.getParameter("searchOrgName") == null ? ""
				: request.getParameter("searchOrgName");

		List browserOptionList = questionnaireBD.getBrowserOption(themeId,
				searchName, pageSize, currentPage, searchOrgName);
		List allBrowserOptionList = questionnaireBD.getAllBrowserOption(
				themeId, searchName, pageSize, currentPage, searchOrgName);
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
							Object[] obj = new Object[7];
							obj[0] = obj_2[3];// orgId
							obj[1] = obj_2[6];// empId
							obj[2] = obj_2[0];// empName
							obj[3] = obj_2[7];// empSex
							obj[4] = obj_2[2];// userAccounts
							obj[5] = obj_2[5];// imId
							obj[6] = "0";// notRead
							list.add(obj);
						}
					}

				}
				map.put("userlist", list);
				listAll.add(map);
			}

		}
		request.setAttribute("listAll", listAll);

		return "answer_viewbrowserOption";
	}

	/**
	 * 问卷回答
	 */
	public String questionnaireAnswer() {
		javax.servlet.http.HttpSession session = request.getSession(true);
		Long userID = new Long(session.getAttribute("userId").toString()); // 取当前用户的ID
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Long questionnaireId = Long.valueOf(request
				.getParameter("questionnaireId"));
		Map map = questionnaireBD.selectQuestionnairePreview(questionnaireId);
		QuestionnairePO questionnairePO = (QuestionnairePO) map
				.get("questionnaire");

		List questhemeRadioList = (List) map.get("questhemeRadio");
		if (questhemeRadioList.size() != 0) {
			request.setAttribute("questhemeRadioList", questhemeRadioList);
		}

		List questhemeCheckList = (List) map.get("questhemeCheck");
		if (questhemeCheckList.size() != 0) {
			request.setAttribute("questhemeCheckList", questhemeCheckList);
		}

		List questhemeEssayList = (List) map.get("questhemeEssay");
		if (questhemeEssayList.size() != 0) {
			request.setAttribute("questhemeEssayList", questhemeEssayList);
		}

		request.setAttribute("title", questionnairePO.getTitle());

		String from = "com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO";
		String where = "answerSheetPO.ballotEmp ='" + userID
				+ "' and answerSheetPO.questionnaireId =" + questionnaireId
				+ "";
		boolean isRepeatName = questionnaireBD.isRepeatName(from, where);
		if (isRepeatName) {
			request.setAttribute("isRepeatName", "isRepeatName");
		}
		return "gotoAnswerQuestionnaire";
	}

	/**
	 * 新增问卷回答
	 */
	public String addQuestionnaireAnswer() {
		HttpSession session = request.getSession(true);
		java.util.Date date = new java.util.Date();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String userOrgName = session.getAttribute("orgName").toString();
		String domainId = session.getAttribute("domainId").toString();
		com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();

		String saveType = request.getParameter("saveType");

		Long userID = new Long(session.getAttribute("userId").toString()); // 取当前用户的ID
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		Long questionnaireId = Long.valueOf(request
				.getParameter("questionnaireId"));
		AnswerSheetPO answerSheetPO = new AnswerSheetPO();
		answerSheetPO.setBallotEmp(userID);
		answerSheetPO.setQuestionnaireId(questionnaireId);
		answerSheetPO.setBallotDate(new Date());
		answerSheetPO.setDomainId(domainId);
		List list = new ArrayList(); // 单选和多选
		List essayList = new ArrayList(); // 问答
		String[] questhemeID = request.getParameterValues("questhemeID");
		if (questhemeID != null) {
			for (int i = 0; i < questhemeID.length; i++) {
				List list2 = new ArrayList();
				list.add(questhemeID[i].toString());
				String[] questhemeOptionID = request.getParameterValues("Box_"
						+ questhemeID[i]);
				if (questhemeOptionID != null) {
					for (int j = 0; j < questhemeOptionID.length; j++) {
						list2.add(questhemeOptionID[j].toString());
					}
				}
				list.add(list2);
				// 可输入其他答案
				String otherAnswer = null;
				String other = request.getParameter("Box_" + questhemeID[i]
						+ "_other");
				if (other != null) {
					otherAnswer = request.getParameter("Box_" + questhemeID[i]
							+ "_otherAnswer");
				}
				list.add(otherAnswer);
			}
		}

		String[] essayQuesthemeID = request
				.getParameterValues("essayQuesthemeID");
		if (essayQuesthemeID != null) {
			for (int i = 0; i < essayQuesthemeID.length; i++) {
				essayList.add(essayQuesthemeID[i].toString());
				String content = request.getParameter("Textarea_"
						+ essayQuesthemeID[i]);
				essayList.add(content);
			}
		}

		boolean result = questionnaireBD.addQuestionnaireAnswer(answerSheetPO,
				list, essayList);
		if (result) {
			java.util.Date endDate = new java.util.Date();
			logBD.log(userId, userName, userOrgName,
					" oa_officemanager_questionnaire", "综合办公-问卷调查", date,
					endDate, "1", "综合办公-问卷调查", session.getAttribute("userIP")
							.toString(), domainId);

		}

		if (!result) {
			return null;
		}
		printResult(G_SUCCESS);
		return null;
	}

	/**
	 * VIEW MY QUESTIONAIR ANSWER
	 */
	public String viewMyQuestionare() {
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		javax.servlet.http.HttpSession session = request.getSession(true);
		Long userID = new Long(session.getAttribute("userId").toString()); // 取当前用户的ID
		Long questionnaireId = Long.valueOf(request
				.getParameter("questionnaireId"));
		Map map = questionnaireBD.selectQuestionnairePreview(questionnaireId);
		QuestionnairePO questionnairePO = (QuestionnairePO) map
				.get("questionnaire");

		List questhemeRadioList = (List) map.get("questhemeRadio");
		if (questhemeRadioList.size() != 0) {
			request.setAttribute("questhemeRadioList", questhemeRadioList);
		}

		List questhemeCheckList = (List) map.get("questhemeCheck");
		if (questhemeCheckList.size() != 0) {
			request.setAttribute("questhemeCheckList", questhemeCheckList);
		}

		List questhemeEssayList = (List) map.get("questhemeEssay");
		if (questhemeEssayList.size() != 0) {
			request.setAttribute("questhemeEssayList", questhemeEssayList);
		}

		request.setAttribute("title", questionnairePO.getTitle());

		String from = "com.whir.ezoffice.subsidiarywork.po.AnswerSheetPO answerSheetPO";
		String where = "answerSheetPO.ballotEmp ='" + userID
				+ "' and answerSheetPO.questionnaireId =" + questionnaireId
				+ "";
		List isMyAnswer = questionnaireBD.isMyAnswer(from, where);
		if (isMyAnswer != null && isMyAnswer.size() > 0) {
			request.setAttribute("isRepeatName", "isRepeatName");
			request.setAttribute("isMyAnswer", isMyAnswer);
		}
		return "viewMyQuestionare";
	}

	/**
	 * 校验标题
	 */
	public String validTitle() {
		HttpSession session = request.getSession(true);
		String domainId = session.getAttribute("domainId").toString();
		QuestionnaireBD questionnaireBD = new QuestionnaireBD();
		String validTarg = request.getParameter("validTarg")==null?"":com.whir.component.security.crypto.EncryptUtil.htmlcode(request.getParameter("validTarg"));
		String title = request.getParameter("title");
		String questionnaireId = request.getParameter("questionnaireId");
		if (null == title|| "".equals(title.trim())) {
			return null;
		}
		String from = "";
		String where = "";
		if ("1".equals(validTarg)) {// 更新问卷调查
			from = "com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO ";
			//where = "questionnairePO.title ='" + title+ "' and questionnairePO.questionnaireId <>"+ questionnaireId + " and questionnairePO.domainId="+ domainId;
			where = "questionnairePO.title =:title and questionnairePO.questionnaireId <>:questionnaireId and questionnairePO.domainId="+ domainId;//安全性  2016/03/25
		} else if ("0".equals(validTarg)) {// 新增问卷调查
			from = "com.whir.ezoffice.subsidiarywork.po.QuestionnairePO questionnairePO ";
			//where = "questionnairePO.title ='" + title+ "' and questionnairePO.domainId=" + domainId;
			where = "questionnairePO.title =:title and questionnairePO.domainId=" + domainId;//安全性  2016/03/25
		} else if ("11".equals(validTarg)) {// 更新设计问卷
			from = "com.whir.ezoffice.subsidiarywork.po.QuesthemePO questhemePO ";
			//where = "questhemePO.title ='" + title+ "' and questhemePO.questionnaire.questionnaireId="+ questionnaireId;
			where = "questhemePO.title =:title and questhemePO.questionnaire.questionnaireId=:questionnaireId ";//安全性  2016/03/25
		} else if ("10".equals(validTarg)) {// 新增设计问卷
			from = "com.whir.ezoffice.subsidiarywork.po.QuesthemePO questhemePO ";
			//where = "questhemePO.title ='" + title+ "' and questhemePO.questionnaire.questionnaireId="+ questionnaireId;
			where = "questhemePO.title =:title and questhemePO.questionnaire.questionnaireId=:questionnaireId ";//安全性  2016/03/25
		}
		 
		if (null == from || "".equals(from) || null == where
				|| "".equals(where)) {
			printResult("校验传入的参数validTarg=" + validTarg
					+ ",有误,修改validTarg=1,新增validTarg=0");
			return null;
		}
		//boolean isRepeatName = questionnaireBD.isRepeatName(from, where);		
		boolean isRepeatName =  questionnaireBD.isRepeatName_Security(from, where, title, questionnaireId);//安全性  2016/03/25
		if (isRepeatName) {
			printResult("您填写的内容已存在，请重新填写");
		} else {
			printResult("您填写的内容可以使用");
		}

		return null;
	}

	public QuestionnairePO getQuestionnairePO() {
		return questionnairePO;
	}

	public void setQuestionnairePO(QuestionnairePO questionnairePO) {
		this.questionnairePO = questionnairePO;
	}

	public QuesthemePO getQuesthemePO() {
		return questhemePO;
	}

	public void setQuesthemePO(QuesthemePO questhemePO) {
		this.questhemePO = questhemePO;
	}

	public AnswerSheetPO getAnswerSheetPO() {
		return AnswerSheetPO;
	}

	public void setAnswerSheetPO(AnswerSheetPO answerSheetPO) {
		AnswerSheetPO = answerSheetPO;
	}

	public EmployeeVO getEmpPO() {
		return empPO;
	}

	public void setEmpPO(EmployeeVO empPO) {
		this.empPO = empPO;
	}

	public String getSearchTitle() {
		return searchTitle;
	}

	public void setSearchTitle(String searchTitle) {
		this.searchTitle = searchTitle;
	}

	public String getSearchStatus() {
		return searchStatus;
	}

	public void setSearchStatus(String searchStatus) {
		this.searchStatus = searchStatus;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public String getSearchEndDate() {
		return searchEndDate;
	}

	public void setSearchEndDate(String searchEndDate) {
		this.searchEndDate = searchEndDate;
	}

	public String getIsSearch() {
		return isSearch;
	}

	public void setIsSearch(String isSearch) {
		this.isSearch = isSearch;
	}

	public String getHtrq() {
		return htrq;
	}

	public void setHtrq(String htrq) {
		this.htrq = htrq;
	}

	public String getActorId() {
		return actorId;
	}

	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	public String getExamineId() {
		return examineId;
	}

	public void setExamineId(String examineId) {
		this.examineId = examineId;
	}

	public String getQuestionnaireId() {
		return questionnaireId;
	}

	public void setQuestionnaireId(String questionnaireId) {
		this.questionnaireId = questionnaireId;
	}

	public String getQuesthemeId() {
		return questhemeId;
	}

	public void setQuesthemeId(String questhemeId) {
		this.questhemeId = questhemeId;
	}

	public List getQuesthemeRadioList() {
		return questhemeRadioList;
	}

	public void setQuesthemeRadioList(List questhemeRadioList) {
		this.questhemeRadioList = questhemeRadioList;
	}

	public List getQuesthemeCheckList() {
		return questhemeCheckList;
	}

	public void setQuesthemeCheckList(List questhemeCheckList) {
		this.questhemeCheckList = questhemeCheckList;
	}

	public List getQuesthemeEssayList() {
		return questhemeEssayList;
	}

	public void setQuesthemeEssayList(List questhemeEssayList) {
		this.questhemeEssayList = questhemeEssayList;
	}

	public String getStatisticAnswerSheetSum() {
		return statisticAnswerSheetSum;
	}

	public void setStatisticAnswerSheetSum(String statisticAnswerSheetSum) {
		this.statisticAnswerSheetSum = statisticAnswerSheetSum;
	}

	public String getVoters() {
		return voters;
	}

	public void setVoters(String voters) {
		this.voters = voters;
	}

	public String getNotVoters() {
		return notVoters;
	}

	public void setNotVoters(String notVoters) {
		this.notVoters = notVoters;
	}

	public String getAnswerSheetId() {
		return answerSheetId;
	}

	public void setAnswerSheetId(String answerSheetId) {
		this.answerSheetId = answerSheetId;
	}

	public String getThemeOptionIds() {
		return themeOptionIds;
	}

	public void setThemeOptionIds(String themeOptionIds) {
		this.themeOptionIds = themeOptionIds;
	}

	public String getBallotName() {
		return ballotName;
	}

	public void setBallotName(String ballotName) {
		this.ballotName = ballotName;
	}

	public String getBallotDate() {
		return ballotDate;
	}

	public void setBallotDate(String ballotDate) {
		this.ballotDate = ballotDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getThemeId() {
		return themeId;
	}

	public void setThemeId(String themeId) {
		this.themeId = themeId;
	}

}
