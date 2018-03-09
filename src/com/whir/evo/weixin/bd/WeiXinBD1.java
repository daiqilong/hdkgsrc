package com.whir.evo.weixin.bd;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.hibernate.HibernateException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import com.whir.common.util.ConversionString;
import com.whir.evo.weixin.bo.HandleOrgResultBO;
import com.whir.evo.weixin.bo.SynStatisticsNumBO;
import com.whir.evo.weixin.constants.ErrorMsgConstans;
import com.whir.evo.weixin.helper.IMenuManager;
import com.whir.evo.weixin.helper.IMsgListener;
import com.whir.evo.weixin.module.AddressMsgListener;
import com.whir.evo.weixin.module.AttendanceMsgListener;
import com.whir.evo.weixin.module.DocMenuManager;
import com.whir.evo.weixin.module.DocMsgListener;
import com.whir.evo.weixin.module.ForumMenuManager;
import com.whir.evo.weixin.module.ForumMsgListener;
import com.whir.evo.weixin.module.InfoMenuManager;
import com.whir.evo.weixin.module.InformationMsgListener;
import com.whir.evo.weixin.module.MailMsgListener;
import com.whir.evo.weixin.module.MeetingMsgListener;
import com.whir.evo.weixin.module.NaireMsgListener;
import com.whir.evo.weixin.module.WorkFlowMenuManager;
import com.whir.evo.weixin.module.WorkFlowMsgListener;
import com.whir.evo.weixin.module.WorkLogMsgListener;
import com.whir.evo.weixin.util.BaseMessage;
import com.whir.evo.weixin.util.Constants;
import com.whir.evo.weixin.util.MessageUtil;
import com.whir.evo.weixin.util.NewsMessage;
import com.whir.evo.weixin.util.OASetLoginSession;
import com.whir.evo.weixin.util.SessionContext;
import com.whir.evo.weixin.util.TextMessage;
import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.WeiXinUtils;
import com.whir.ezoffice.personnelmanager.bd.NewEmployeeBD;
import com.whir.org.bd.MoveOAmanager.MoveOAmanagerBD;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.bd.usermanager.UserBD;
import com.whir.org.vo.MoveOAmanager.CorpSetAppPO;
import com.whir.org.vo.MoveOAmanager.CorpSetPO;
import com.whir.org.vo.MoveOAmanager.UserOrgSynErrLogPO;
import com.whir.org.vo.organizationmanager.OrganizationVO;
import com.whir.org.vo.usermanager.EmployeeVO;
import com.whir.org.vo.usermanager.UserPO;
import com.whir.service.api.system.UserService;

/**
 * 微信业务处理类
 * 
 * @author pa
 * 
 */
public class WeiXinBD1 {

	private static final Logger logger = Logger.getLogger(WeiXinBD.class);

	private final static Map<String, IMsgListener> msgListeners = new HashMap<String, IMsgListener>();

	private final static Map<String, IMenuManager> menuListeners = new HashMap<String, IMenuManager>();

	private int orgCount = 0;

	private int successOrgCount = 0;

	private int failOrgCount = 0;

	private int empCount = 0;

	private int successEmpCount = 0;

	private int failEmpCount = 0;

	private static MoveOAmanagerBD managerBD = new MoveOAmanagerBD();

	private static final String ADD_EVENT = "新增";

	private static final String UPDATE_EVENT = "更新";

	private static final String DELETE_EVENT = "删除";

	private static CorpSetAppPO addressMenu = new CorpSetAppPO();

	private static CorpSetAppPO mailMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO forumMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO infoMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO workflowMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO govMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO meetingMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO naireMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO worklogMenu = new CorpSetAppPO();
	
	private static CorpSetAppPO attendanceMenu = new CorpSetAppPO();
	

	static {
		try {
			String id = "";
			List<CorpSetAppPO> corpAppList = managerBD.getCorpSetAppPO();
			if (corpAppList != null) {
				String appValue = "";
				for (int i = 0, size = corpAppList.size(); i < size; i++) {
					appValue = corpAppList.get(i).getAppid();
					id = corpAppList.get(i).getCorpid();
					logger.debug("-------appValue-------->>gogo"+appValue);
					logger.debug("-------id-------->>gogo"+id);
					if (StringUtils.isEmpty(id)) {
						continue;
					}
					if (Constants.AppIdName.ADDRESS_APPID.getValue().equals(appValue)) {
						// 通讯录
						registerMsgListener(id, new AddressMsgListener());
						addressMenu.setAppid(corpAppList.get(i).getAppid());
						addressMenu.setAppname(corpAppList.get(i).getAppname());
						addressMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.FORUM_APPID.getValue().equals(appValue)) {
						// 论坛
						registerMenuManager(id, new ForumMenuManager());
						registerMsgListener(id, new ForumMsgListener());
						forumMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.GOVDOCUMENT_APPID.getValue().equals(appValue)) {
						// 公文
						registerMsgListener(id, new DocMsgListener());
						registerMenuManager(id, new DocMenuManager());
						govMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.INFORMATION_APPID.getValue().equals(appValue)) {
						// 信息
						registerMenuManager(id, new InfoMenuManager());
						registerMsgListener(id, new InformationMsgListener());
						infoMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.MAIL_APPID.getValue().equals(appValue)) {
						// 邮件
						registerMsgListener(id, new MailMsgListener());
						mailMenu.setAppid(corpAppList.get(i).getAppid());
						mailMenu.setAppname(corpAppList.get(i).getAppname());
						mailMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.WORKFLOW_APPID.getValue().equals(appValue)) {
						// 文件办理
						registerMsgListener(id, new WorkFlowMsgListener());
						registerMenuManager(id, new WorkFlowMenuManager());
						workflowMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.MEET_APPID.getValue().equals(appValue)) {
						// 会议助手
						registerMsgListener(id, new MeetingMsgListener());
						meetingMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.NAIRE_APPID.getValue().equals(appValue)) {
						// 问卷调查
						registerMsgListener(id, new NaireMsgListener());
						naireMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.WORKLOG_APPID.getValue().equals(appValue)) {
						// 工作日志
						registerMsgListener(id, new WorkLogMsgListener());
						worklogMenu.setCorpid(corpAppList.get(i).getCorpid());
					} else if (Constants.AppIdName.ATTENDANCE_APPID.getValue().equals(appValue)) {
						// 微信考勤
						registerMsgListener(id, new AttendanceMsgListener());
						attendanceMenu.setCorpid(corpAppList.get(i).getCorpid());
					}
				}
			}
		} catch (Exception e) {
			logger.debug("<静态代码块>: 注册异常！");
			e.printStackTrace();
		}
	}

	/**
	 * 更新消息和菜单创建集合
	 * 
	 * @param corpAppList
	 */
	public synchronized void updateListener(List<CorpSetAppPO> corpAppList) {
		msgListeners.clear();
		menuListeners.clear();
		String id = "";
		if (corpAppList != null) {
			String appValue = "";
			for (int i = 0, size = corpAppList.size(); i < size; i++) {
				appValue = corpAppList.get(i).getAppid();
				id = corpAppList.get(i).getCorpid();
				if (StringUtils.isEmpty(id)) {
					continue;
				}
				if (Constants.AppIdName.ADDRESS_APPID.getValue().equals(appValue)) {
					// 通讯录
					registerMsgListener(id, new AddressMsgListener());
					addressMenu.setAppid(corpAppList.get(i).getAppid());
					addressMenu.setAppname(corpAppList.get(i).getAppname());
					addressMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.FORUM_APPID.getValue().equals(appValue)) {
					// 论坛
					registerMenuManager(id, new ForumMenuManager());
					registerMsgListener(id, new ForumMsgListener());
				} else if (Constants.AppIdName.GOVDOCUMENT_APPID.getValue().equals(appValue)) {
					// 公文
					registerMsgListener(id, new DocMsgListener());
					registerMenuManager(id, new DocMenuManager());
					govMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.INFORMATION_APPID.getValue().equals(appValue)) {
					// 信息
					registerMenuManager(id, new InfoMenuManager());
					registerMsgListener(id, new InformationMsgListener());
					infoMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.MAIL_APPID.getValue().equals(appValue)) {
					// 邮件
					registerMsgListener(id, new MailMsgListener());
					mailMenu.setAppid(corpAppList.get(i).getAppid());
					mailMenu.setAppname(corpAppList.get(i).getAppname());
					mailMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.WORKFLOW_APPID.getValue().equals(appValue)) {
					// 文件办理
					registerMsgListener(id, new WorkFlowMsgListener());
					registerMenuManager(id, new WorkFlowMenuManager());
					workflowMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.MEET_APPID.getValue().equals(appValue)) {
					// 会议助手
					registerMsgListener(id, new MeetingMsgListener());
					meetingMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.NAIRE_APPID.getValue().equals(appValue)) {
					// 问卷调查
					registerMsgListener(id, new NaireMsgListener());
					naireMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.WORKLOG_APPID.getValue().equals(appValue)) {
					// 工作日志
					registerMsgListener(id, new WorkLogMsgListener());
					worklogMenu.setCorpid(corpAppList.get(i).getCorpid());
				} else if (Constants.AppIdName.ATTENDANCE_APPID.getValue().equals(appValue)) {
					// 微信考勤
					registerMsgListener(id, new AttendanceMsgListener());
					attendanceMenu.setCorpid(corpAppList.get(i).getCorpid());
				} 
			}
		}
	}

	/**
	 * 验证微信登录
	 * 
	 * @param request
	 */
	public boolean auth(HttpServletRequest request, HttpServletResponse response, String agentid) {
		if ("post".equalsIgnoreCase(request.getMethod())) {
			return true;
		}
		Map<String, String> resultMap = null;
		if (request.getSession().getAttribute("userId") != null) {
			logger.debug("---------------------------------userId != null---------------------------------");
			if (request.getParameter("t") == null) {
				logger.debug("---------------------------------t == null---------------------------------");
				Enumeration<?> en = request.getParameterNames();
				int size = request.getParameterMap().size() * 2;
				if (request.getParameter("code") != null) {
					size -= 2;
				}
				String[] names = new String[size];
				int pos = 0;
				while (en.hasMoreElements()) {
					String key = en.nextElement().toString();
					if ("code".equals(key)) {
						continue;
					}
					names[pos++] = key;
					names[pos++] = request.getParameter(key);
				}
				String token = generateToken(request.getSession().getId(), names);
				try {
					String qstring = request.getQueryString();
					if (qstring == null)
						qstring = "";
					qstring = qstring.replaceAll("\\?code=[^&]*\\&", "?");
					qstring = qstring.replaceAll("\\&code=[^&]*\\&", "&");
					qstring = qstring.replaceAll("\\&code=[^&]*$", "");
					String url = request.getRequestURL().toString() + "?" + qstring + "&t=" + token;
					if ("get".equals(request.getMethod())) {
						response.sendRedirect(url);
					} else {
						response.setStatus(307);
						response.setHeader("Location", url);
					}
					return false;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return true;
		}
		
			String userId = request.getParameter("login");
			if (userId == null) {
				logger.debug("---------------------------------userId == null---------------------------------");
				return false;
			} else {
				try {
					MoveOAmanagerBD moBD = new MoveOAmanagerBD();
					String rlid = "";
					try {
						rlid = moBD.getCorpSetPO().getLast_relactionId();
					} catch (HibernateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//匹配同步选择的账号，简码，身份证号
					Map map = new HashMap();
					if("empId".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "empId");					
					}else if("userAccount".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "userAccount");		
					}else if("userSimpleName".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "userSimpleName");		
					}else if("empIdCard".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "empIdCard");		
					}else{
						map = new UserBD().getUserInfoByString(userId, "empId");		
					}
					String account = null;
					if (map.size() > 0) {
						account = map.get("userAccounts").toString();
					}
					resultMap = OASetLoginSession.setSession(account, "",request,"0");
					if (resultMap == null || "false".equals(resultMap.get("result"))) {
						return false;
					}
				} catch (NumberFormatException e) {
					if (userId == null) {
						return false;
					}
					if (resultMap == null || "false".equals(resultMap.get("result"))) {
						return false;
					}
				}
				if (request.getParameter("t") == null) {
					logger
							.debug("---------------------------------------getParameter t == null---------------------------------------");
					Enumeration<?> en = request.getParameterNames();
					int size = request.getParameterMap().size() * 2;
					if (request.getParameter("code") != null) {
						size -= 2;
					}
					String[] names = new String[size];
					int pos = 0;
					while (en.hasMoreElements()) {
						String key = en.nextElement().toString();
						if ("code".equals(key)) {
							continue;
						}
						names[pos++] = key;
						names[pos++] = request.getParameter(key);
					}
					String token = generateToken(request.getSession().getId(), names);

					try {
						String qstring = request.getQueryString();
						qstring = qstring.replaceAll("\\?code=[^&]*\\&", "?");
						qstring = qstring.replaceAll("\\&code=[^&]*\\&", "&");
						qstring = qstring.replaceAll("\\&code=[^&]*$", "");
						// String url = request.getRequestURL().toString() + "?"
						// + qstring + "&t=" + token;
						// System.out.println("=-------=====================qstring=================="+qstring);
						String url = request.getRequestURL().toString() + "?" + qstring + "&t="
								+ java.net.URLEncoder.encode(token, "utf-8");

						// System.out.println("=-------=====================redirecturl=================="+url);
						if ("get".equals(request.getMethod())) {
							response.sendRedirect(url);
						} else {
							response.setStatus(307);
							response.setHeader("Location", url);
						}
						return false;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
	}

	/**
	 * AOP验证微信登录
	 * 
	 * @param request
	 */
	public String authWithAop(HttpServletRequest request, HttpServletResponse response, String agentid) {
		logger.debug("authWithAop------->>GO");
		if ("post".equalsIgnoreCase(request.getMethod())) {
			return "true";
		}
		String userAgent = request.getHeader("USER-AGENT");
		//处理把链接放到外面打开
		if (request.getSession().getAttribute("userId") == null && !(userAgent.indexOf("MicroMessenger")>-1)) {
			try {
				String jumpUrl = request.getRequestURL().toString();
			    String queryString = request.getQueryString()!=null?request.getQueryString():"";
			    if(!"".equals(queryString)){
			    	jumpUrl = jumpUrl+"?"+queryString;
			    }
				//System.out.println("---jumpUrl="+jumpUrl);
				response.sendRedirect("/defaultroot/evo/weixin/common/validate.jsp?jumpUrl="+URLEncoder.encode(jumpUrl, "UTF-8"));
				return "";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		Map<String, String> resultMap = null;
		logger.debug("!!@@##seission="+request.getSession().getAttribute("userId"));
		if (request.getSession().getAttribute("userId") != null) {
			logger.debug("---------------------------------userId != null---------------------------------");
			if (request.getParameter("t") == null) {
				logger.debug("---------------------------------t == null---------------------------------");
				Enumeration<?> en = request.getParameterNames();
				int size = request.getParameterMap().size() * 2;
				if (request.getParameter("code") != null) {
					size -= 2;
				}
				String[] names = new String[size];
				int pos = 0;
				while (en.hasMoreElements()) {
					String key = en.nextElement().toString();
					if ("code".equals(key)) {
						continue;
					}
					names[pos++] = key;
					names[pos++] = request.getParameter(key);
				}
				String token = generateToken(request.getSession().getId(), names); 
				try {
					String qstring = request.getQueryString();
					if (qstring == null) {
						qstring = "";
					}
					qstring = qstring.replaceAll("\\?code=[^&]*\\&", "?");
					qstring = qstring.replaceAll("\\&code=[^&]*\\&", "&");
					qstring = qstring.replaceAll("\\&code=[^&]*$", "");
					String url = request.getRequestURL().toString() + "?" + qstring + "&t=" + token;
					if ("get".equals(request.getMethod())) {
						System.out.println("inininget");
						response.sendRedirect(url);
					} else {
						System.out.println("ininelse");
						MoveOAmanagerBD moBD = new MoveOAmanagerBD();
						String rlid = "";
						try {
							rlid = moBD.getCorpSetPO().getLast_relactionId();
						} catch (HibernateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//匹配同步选择的账号，简码，身份证号
						String userkey ="";
						System.out.println("!!@@----rlid="+rlid);
						if("empId".equals(rlid)){
							userkey = request.getSession().getAttribute("userId").toString();					
						}else if("userAccount".equals(rlid)){
							userkey = request.getSession().getAttribute("userAccount").toString();		
						}else if("userSimpleName".equals(rlid)){
							userkey = request.getSession().getAttribute("userSimpleName").toString();		
						}else if("empIdCard".equals(rlid)){
							userkey = request.getSession().getAttribute("empIdCard").toString();		
						}else{
							userkey = request.getSession().getAttribute("userId").toString();		
						}
						System.out.println("!!@@----userkey="+userkey);
						setUserKey(userkey,request);
						response.setStatus(307);
						response.setHeader("Location", url);
					}
					return "false";
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return "true";
		}
		//String code = request.getParameter("code");
	
			String userId = request.getParameter("login");
			logger.debug("userId----"+userId);
			if (userId == null) {
				logger.debug("---------------------------------userId == null---------------------------------");
				return "非法登录用户";
			} else {
				setUserKey(userId,request);
				try {
					MoveOAmanagerBD moBD = new MoveOAmanagerBD();
					String rlid = "";
					try {
						rlid = moBD.getCorpSetPO().getLast_relactionId();
					} catch (HibernateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					//匹配同步选择的账号，简码，身份证号
					Map map = new HashMap();
					if("empId".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "empId");					
					}else if("userAccount".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "userAccount");		
					}else if("userSimpleName".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "userSimpleName");		
					}else if("empIdCard".equals(rlid)){
						map = new UserBD().getUserInfoByString(userId, "empIdCard");		
					}else{
						map = new UserBD().getUserInfoByString(userId, "empId");		
					}
					String account = null;
					if (map.size() > 0) {
						account = map.get("userAccounts").toString();
					}
					logger.debug("userID:" + userId + "::::" + account);
					if (account == null) {
						return "非法登录用户";
					}
					resultMap = OASetLoginSession.setSession(account, "",request,"0");
					if (resultMap == null || "false".equals(resultMap.get("result"))) {
						return resultMap.get("description");
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (request.getParameter("t") == null) {
					logger.debug("-------------------getParameter t == null----------------------");
					Enumeration<?> en = request.getParameterNames();
					int size = request.getParameterMap().size() * 2;
					if (request.getParameter("code") != null) {
						size -= 2;
					}
					String[] names = new String[size];
					int pos = 0;
					while (en.hasMoreElements()) {
						String key = en.nextElement().toString();
						if ("code".equals(key)) {
							continue;
						}
						names[pos++] = key;
						names[pos++] = request.getParameter(key);
					}
					String token = generateToken(request.getSession().getId(), names);

					try {
						String qstring = request.getQueryString();
						qstring = qstring.replaceAll("\\?code=[^&]*\\&", "?");
						qstring = qstring.replaceAll("\\&code=[^&]*\\&", "&");
						qstring = qstring.replaceAll("\\&code=[^&]*$", "");
						String url = request.getRequestURL().toString() + "?" + qstring + "&t="
								+ java.net.URLEncoder.encode(token, "utf-8");
						if ("get".equals(request.getMethod())) {
							response.sendRedirect(url);
						} else {
							response.setStatus(307);
							response.setHeader("Location", url);
						}
						return "false";
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return "true";
			}
	}

	public String getKey(String... args) {
		String session = "sdfsafdasfsffssdasfd?";
		String name = null;
		String value = null;
		String url = "";
		int index = 0;
		for (String s : args) {
			index++;
			if (index % 2 == 1) {
				name = s;
			} else {
				value = s;
				url += "&" + name + "=" + value;
			}
		}
		return new sun.misc.BASE64Encoder().encode((session + url).getBytes());

	}

	/**
	 * 同步全部用户组织
	 * 
	 * @return
	 * @throws  
	 */
	public SynStatisticsNumBO synAllOrgnization(String secret)  {
		logger.debug("同步开始SYSOUT---------secret"+secret);
		String rlid ="";
		try {
			MoveOAmanagerBD moBD = new MoveOAmanagerBD();
			CorpSetPO oldpo = moBD.getCorpSetPO();
			CorpSetPO newpo = new CorpSetPO();
			newpo.setLast_relactionId(oldpo.getRelactionId());
			newpo.setCorpid(oldpo.getCorpid());
			newpo.setCorpsecret(oldpo.getCorpsecret());
			newpo.setEncodingAESKey(oldpo.getEncodingAESKey());
			newpo.setRelactionId(oldpo.getRelactionId());
			newpo.setToken(oldpo.getToken());
			moBD.deleteCorpSetPO(Integer.parseInt(oldpo.getId()+""));
			moBD.saveCorpSetPO(newpo);
			rlid = moBD.getCorpSetPO().getRelactionId();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SynStatisticsNumBO statisticsNumBO = null;
		String sql = "";
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
		try {
			OrganizationBD bd = new OrganizationBD();
			List<?> list = bd.getSubOrgListByOrgId("0");
			List<UserOrgSynErrLogPO> poList = new ArrayList<UserOrgSynErrLogPO>();
			UserOrgSynErrLogPO po = null;
			for (int i = 0; i < list.size(); i++) {
				Object[] org = (Object[]) list.get(i);
				initOrg(org[0].toString(), org[1].toString(), "1", bd, dbopt, poList, po,secret,org[7].toString());
			}
			logger.debug("同步组织SYSOUT结束---------");
			logger.debug("组织累计数据--->" + orgCount + ";" + successOrgCount + ";" + failOrgCount);
			UserService uus = new UserService();
			List<?> ulist = uus.getUserListByOrgId(null, "0");
			List<String> uidlist = new ArrayList<String>();
			for (int k = 0; k < ulist.size(); k++) {
				com.whir.org.vo.usermanager.EmployeeVO evo = (com.whir.org.vo.usermanager.EmployeeVO) ulist.get(k);
				if ("1".equals(evo.getEnterprisenumber())) {
					uidlist.add(String.valueOf(evo.getEmpId()));
				}
			}
			com.whir.service.api.person.PersonService ps = new com.whir.service.api.person.PersonService();
			Map<?, ?> m2 = ps.getInnerPersonList("0", "0", "", "0", "10000", "1", "", "", "", "1");
			List<?> userList = (List<?>) m2.get("list");
			UserService us = null;
			String updateOrCreateUser = "";
			for (int i = 0; i < userList.size(); i++) {
				Object[] employee = (Object[]) userList.get(i);
				if (!uidlist.contains(employee[0].toString())) {
					continue;
				}
				HandleOrgResultBO resultBO = null;
				if ((employee[3] == null || "".equals(employee[3])) && (employee[4] == null || "".equals(employee[4]))) {
					po = new UserOrgSynErrLogPO();
					empCount++;
					failEmpCount++;
					updateOrCreateUser = ADD_EVENT;
					this.saveFailMsg(resultBO, employee[12].toString(), employee[6].toString(), employee[0].toString(),
							employee[1].toString(), "手机号或者邮箱信息未填写！", updateOrCreateUser, "", "0", poList, po);
					continue;
				}
				empCount++;
				us = new UserService();
				WeiXinUtils.User user = new WeiXinUtils.User();
				user.setName(employee[1].toString());
				java.lang.Object[] person = us.getUserDetails(employee[0].toString());
				String orgId = person[6].toString();
				sql = "select WEIXIN_ORG_ID from  EVO_WEIXIN_ORGMAP where OA_ORG_ID = " + orgId;
				ResultSet rs = dbopt.executeQuery(sql);
				int[] orgids = null;
				if (rs.next()) {
					orgids = new int[] { (rs.getInt(1)) };
				} else {
					rs.close();
					continue;
				}
				rs.close();
				user.setDepartment(orgids);
				user.setGender(Integer.parseInt(employee[2].toString()));
				user.setMobile(employee[4] == null ? null : employee[4].toString());
				user.setEmail(employee[3] == null ? null : employee[3].toString());
				String keyAcc = "";
				if("empId".equals(rlid)){
					user.setUserid(employee[0].toString());	
					keyAcc = employee[0].toString();
				}else if("userAccount".equals(rlid)){
					user.setUserid(employee[16].toString());
					keyAcc = employee[16].toString();
				}else if("userSimpleName".equals(rlid)){
					user.setUserid(employee[17].toString());	
					keyAcc = employee[17].toString();
				}else if("empIdCard".equals(rlid)){
					user.setUserid(employee[18].toString());	
					keyAcc = employee[18].toString();
				}else{
					user.setUserid(employee[0].toString());	
					keyAcc = employee[0].toString();
				}
				user.setPosition(employee[13] == null ? null : employee[13].toString());
				if (WeiXinUtils.getUser(keyAcc)) {
					resultBO = WeiXinUtils.updateEmp(user,secret);
					updateOrCreateUser = UPDATE_EVENT;
				} else {
					resultBO = WeiXinUtils.createEmp(user,secret);
					updateOrCreateUser = ADD_EVENT;
				}
				if (resultBO == null) {
					failEmpCount++;
					po = new UserOrgSynErrLogPO();
					this.saveFailMsg(resultBO, employee[12].toString(), employee[6].toString(), employee[0].toString(),
							employee[1].toString(), "", updateOrCreateUser, employee[4] == null ? "" : employee[4]
									.toString(), "0", poList, po);
					continue;
				}
				if (!"0".equals(resultBO.getErrcode())) {
					failEmpCount++;
					po = new UserOrgSynErrLogPO();
					this.saveFailMsg(resultBO, employee[12].toString(), employee[6].toString(), employee[0].toString(),
							employee[1].toString(), "", updateOrCreateUser, employee[4] == null ? "" : employee[4]
									.toString(), "0", poList, po);
					logger.error("用户创建失败--->" + user.getName());
				} else {
					successEmpCount++;
				}
			}
			logger.debug("用户累计数据--->" + empCount + ";" + successEmpCount + ";" + failEmpCount);
			statisticsNumBO = new SynStatisticsNumBO(orgCount, successOrgCount, failOrgCount, empCount,
					successEmpCount, failEmpCount);
			dbopt.close();
			logger.debug("保存同步错误信息集合总长度----------------------------->" + poList.size());
			for (UserOrgSynErrLogPO sePo : poList) {
				logger.debug("组织名--->" + sePo.getOrgname() + "<>用户名--->" + sePo.getUsername());
			}
			String result = managerBD.saveUserOrgSynErrLogPO(poList);
			if (!"true".equals(result)) {
				logger.debug("---------保存同步组织异常失败---------");
			}
			return statisticsNumBO;
		} catch (Exception ex) {
			logger.error("---------同步组织失败！---------");
			ex.printStackTrace();
			throw new RuntimeException("异常信息---->" + ex.getMessage());
		} finally {
			try {
				dbopt.close();
			} catch (Exception ex1) {
			}
		}
	}

	/**
	 * 获取微信管理平台组织id
	 * 
	 * @param orgId
	 * @return
	 */
	private String getWeiXinOrgId(String orgId) {
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
		String sql = "select WEIXIN_ORG_ID from  EVO_WEIXIN_ORGMAP where OA_ORG_ID = " + orgId;
		ResultSet rs = null;
		try {
			rs = dbopt.executeQuery(sql);
			if (rs.next()) {
				return String.valueOf(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					dbopt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 初始化微信组织结构
	 * 
	 * @param orgId
	 * @param orgName
	 * @param parentId
	 * @param bd
	 * @param dbopt
	 * @throws SQLException
	 * @throws Exception
	 */
	private void initOrg(String orgId, String orgName, String parentId, OrganizationBD bd,
			com.whir.ezoffice.customdb.common.util.DbOpt dbopt, List<UserOrgSynErrLogPO> poList, UserOrgSynErrLogPO po,String secret,String orgOrderCode)
			throws SQLException, Exception {
		po = new UserOrgSynErrLogPO();
		// 同步的组织总数，每进一次加一
		orgCount++;
		WeiXinUtils.Organization worg = new WeiXinUtils.Organization();
		worg.setName(orgName);
		worg.setOrder(orgOrderCode);
		if ("0".equals(parentId)) {
			worg.setParentid("1");
		} else {
			worg.setParentid(parentId);
		}
		String orgSql = "SELECT WEIXIN_ORG_ID FROM EVO_WEIXIN_ORGMAP WHERE OA_ORG_ID = " + orgId;
		ResultSet rs = dbopt.executeQuery(orgSql);
		int[] weixinOrgIds = null;
		if (rs.next()) {
			weixinOrgIds = new int[] { (rs.getInt(1)) };
		}
		HandleOrgResultBO resultBO = null;
		String updateId = null;
		String createId = null;
		// 微信企业号中的父组织id
		String wOrgId = null;
		if (weixinOrgIds != null) {
			// 组织已存在则更新企业号组织
			updateId = String.valueOf(weixinOrgIds[0]);
			wOrgId = updateId;
			worg.setId(updateId);
			resultBO = WeiXinUtils.updateOrg(worg,secret);
		} else {
			// 创建微信企业号中的通讯录组织
			resultBO = WeiXinUtils.createOrg(worg,secret);
			createId = resultBO.getId();
			wOrgId = createId;
		}
		String errorCode = resultBO.getErrcode();
		String errorMsg = resultBO.getErrmsg();
		if (updateId == null && createId == null && !"0".equals(errorCode)) {// 创建组织失败
			// 创建失败 条件：中间表中无此 企业号组织id信息，创建企业号组织时返回信息（组织已存在）
			// 说明该组织是人为手动创建，添加中间表中的关联数据
			if (null != errorMsg && errorMsg.length() > 0 && "60008".equals(errorCode)) {
				String existWxOrgId = errorMsg.replace("department existed: ", "").replace(" is existed", "");
				String sql = "INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + orgId + ","
						+ existWxOrgId + " )";
				// 给企业号组织父ID赋值
				wOrgId = existWxOrgId;
				int addResult = dbopt.executeUpdate(sql);
				if (addResult < 1) {
					// 失败信息的保存整理功能
					logger.error("初始化失败信息-->orgName:" + orgName);
					failOrgCount++;
					this.saveFailMsg(resultBO, orgId, orgName, "", "", "关联数据插入失败！", ADD_EVENT, "", "1", poList, po);
				} else {
					successOrgCount++;
				}
			} else {
				failOrgCount++;
				logger.error("创建组织：" + worg.getName() + "--" + "信息--" + "errorCode：" + resultBO.getErrcode()
						+ "--errorMsg：" + resultBO.getErrmsg());
				this.saveFailMsg(resultBO, orgId, orgName, "", "", "", ADD_EVENT, "", "1", poList, po);
				return;
			}
		} else if (updateId != null && createId == null && !"0".equals(errorCode)) { // 更新组织失败
			// ，更新企业号组织时返回信息（组织已存在）
			// 说明该组织是人为手动创建，更新中间表
			// 失败信息的保存整理功能 --- 更新
			if (null != errorMsg && errorMsg.length() > 0 && "60003".equals(errorCode)) {
				String delSql = "DELETE EVO_WEIXIN_ORGMAP  WHERE OA_ORG_ID = " + orgId;
				int delResult = dbopt.executeUpdate(delSql);
				if (delResult < 1) {
					// 失败信息的保存整理功能
					failOrgCount++;
					logger.error("删除失效中间表数据失败-->orgName:" + orgName);
					this.saveFailMsg(resultBO, orgId, orgName, "", "", "关联数据删除失败！", DELETE_EVENT, "", "1", poList, po);
					return;
				}
				// 更新 转 添加获取企业号组织ID
				resultBO = WeiXinUtils.createOrg(worg,secret);
				String existWxOrgId = "";
				if (null != resultBO.getErrmsg() && resultBO.getErrmsg().length() > 0
						&& "60008".equals(resultBO.getErrcode())) {
					existWxOrgId = resultBO.getErrmsg().replace("department existed: ", "").replace(" is existed", "");
				} else if ("0".equals(resultBO.getErrcode())) {
					existWxOrgId = resultBO.getId();
				} else {
					failOrgCount++;
					this.saveFailMsg(resultBO, orgId, orgName, "", "", "", UPDATE_EVENT, "", "1", poList, po);
					return;
				}
				// 给企业号组织父ID赋值
				wOrgId = existWxOrgId;
				String sql = "INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + orgId + ","
						+ existWxOrgId + " )";
				int addResult = dbopt.executeUpdate(sql);
				if (addResult < 1) {
					// 失败信息的保存整理功能
					failOrgCount++;
					logger.error("更新时添加中间表失败信息-->orgName:" + orgName);
					this.saveFailMsg(resultBO, orgId, orgName, "", "", "删除失效中间数据后再添加新数据失败！", ADD_EVENT, "", "1",
							poList, po);
					return;
				} else {
					successOrgCount++;
				}
			} else {
				failOrgCount++;
				logger.error("更新组织：" + worg.getName() + "--" + "信息--" + "errorCode：" + resultBO.getErrcode()
						+ "--errorMsg：" + resultBO.getErrmsg());
				this.saveFailMsg(resultBO, orgId, orgName, "", "", "", UPDATE_EVENT, "", "1", poList, po);
				return;
			}
		} else if (updateId == null && createId != null && "0".equals(errorCode)) {// 创建组织成功，添加中间表关联数据
			// 构建企业号组织ID与OA中组织的关联关系
			String sql = "INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + orgId + "," + createId
					+ " )";
			int addResult = dbopt.executeUpdate(sql);
			if (addResult < 1) {
				failOrgCount++;
				// 失败信息的保存整理功能
				logger.error("创建时添加中间表失败信息-->orgName:" + orgName);
				this.saveFailMsg(resultBO, orgId, orgName, "", "", "创建企业号组织后，添加关联表数据错误！", ADD_EVENT, "", "1", poList,
						po);
			} else {
				successOrgCount++;
			}
		} else if (updateId != null && createId == null && "0".equals(errorCode)) { // 更新成功，记录更新数据量
			// 
			successOrgCount++;
		}
		List<?> list = bd.getSubOrgListByOrgId(orgId);
		// 递归
		for (int i = 0; i < list.size(); i++) {
			Object[] org = (Object[]) list.get(i);
			initOrg(org[0].toString(), org[1].toString(), wOrgId, bd, dbopt, poList, po,secret, org[7].toString());
		}
	}

	/**
	 * 同步时的异常信息保存功能
	 * 
	 * @param resultBO
	 * @param orgId
	 * @param orgName
	 * @param empId
	 * @param empName
	 */
	private void saveFailMsg(HandleOrgResultBO resultBO, String orgId, String orgName, String empId, String empName,
			String anotherMsg, String event, String phoneNum, String userOrgFlag, List<UserOrgSynErrLogPO> poList,
			UserOrgSynErrLogPO po) {
		String errorMsg = "";
		if (resultBO != null) {
			errorMsg = ErrorMsgConstans.ERROR_MSG_MAP.get(resultBO.getErrcode());
		} else {
			errorMsg = "系统处理响应数据失败！";
		}
		if (!"".equals(anotherMsg)) {
			errorMsg = anotherMsg;
		}
		po.setCreate_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		po.setEmpid(empId);
		po.setEvent(event); // 事件
		po.setOrgid(orgId);
		po.setOrgname(orgName);
		po.setPhonenum(phoneNum);
		po.setResult(errorMsg);
		po.setUser_org_flag(userOrgFlag); // 0:用户 1:组织
		po.setUsername(empName);
		poList.add(po);
	}

	/**
	 * 同步组织
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean updateOrgnization(String id) {
		logger.debug("updateOrgnization------>>");
		//System.out.println("updateOrgnization------>>开始");
		if (!Constants.use) {
			return true;
		}
		OrganizationBD bd = new OrganizationBD();
		OrganizationVO list = bd.getOrgByOrgId(id);
		String orgName = list.getOrgName();

		String parentId = String.valueOf(list.getOrgParentOrgId());
		String secret = loadSyncSecret();
		logger.debug("secret----------->>"+secret);
		// com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new
		// com.whir.ezoffice.customdb.common.util.DbOpt();
		if (this.getWeiXinOrgId(id) == null) {
			logger.debug("in--------------1");
			WeiXinUtils.Organization worg = new WeiXinUtils.Organization();
			worg.setName(orgName);
			if ("0".equals(parentId)) {
				worg.setParentid("1");
			} else {
				parentId = this.getWeiXinOrgId(parentId);
				worg.setParentid(parentId);
			}
			//System.out.println("updateOrgnization-----id----->>"+id);
			String existWxOrgId = WeiXinUtils.createOrganization(worg,secret);
			//System.out.println("updateOrgnization------existWxOrgId---->>"+existWxOrgId);
			String sql = "INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + id + ","
			+ existWxOrgId + " )";
			try {
				com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
				dbopt.executeUpdate(sql);
				dbopt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("updateOrgnization------>>结束");
			return existWxOrgId != null;
		} else  {
			logger.debug("in--------------2");
			logger.debug("in--------------id"+id);
			String existWxOrgId = this.getWeiXinOrgId(id);
			logger.debug("existWxOrgId----------》》"+existWxOrgId);
			//String result = WeiXinUtils.getAllOrgList(parentId,secret);
			WeiXinUtils.Organization worg = new WeiXinUtils.Organization();
			worg.setName(orgName);
			if ("0".equals(parentId)) {
				worg.setParentid("1");
			} else {
				parentId = this.getWeiXinOrgId(parentId);
				worg.setParentid(parentId);
			}
			if(existWxOrgId!=null && "".equals(existWxOrgId)&& !"null".equals(existWxOrgId) ){
				logger.debug("in--------------3");
				existWxOrgId = WeiXinUtils.createOrganization(worg,secret);
				String sql = "INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + id + ","
				+ existWxOrgId + " )";
				try {
					com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
					dbopt.executeUpdate(sql);
					dbopt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				logger.debug("in--------------4");
				worg.setId(this.getWeiXinOrgId(id));
				existWxOrgId = WeiXinUtils.updateOrganization(worg,secret);
			}
			return existWxOrgId != null;
		}
		// return false;
	}

	public String getSqlStrByList(List sqhList, int splitNum, String columnName) {
		if (splitNum > 1000) // 因为数据库的列表sql限制，不能超过1000.
			return null;
		StringBuffer sql = new StringBuffer("");
		if (sqhList != null) {
			sql.append(" ").append(columnName).append(" IN ( ");
			for (int i = 0; i < sqhList.size(); i++) {
				sql.append("'").append(sqhList.get(i) + "',");
				if ((i + 1) % splitNum == 0 && (i + 1) < sqhList.size()) {
					sql.deleteCharAt(sql.length() - 1);
					sql.append(" ) OR ").append(columnName).append(" IN (");
				}
			}
			sql.deleteCharAt(sql.length() - 1);
			sql.append(" )");
		}
		return sql.toString();
	}

	public String getSqlStrByArrays(String[] sqhArrays, int splitNum, String columnName) {
		return getSqlStrByList(Arrays.asList(sqhArrays), splitNum, columnName);
	}

	/**
	 * 增加组织
	 * 
	 * @return
	 */
	public boolean addOrgnization(String id) {
		// , String orgName, String parentId
		if (!Constants.use)
			return true;
		return updateOrgnization(id);
	}

	private static String getUserId(String username) {
		javax.sql.DataSource ds = null;
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;

		String returnValue = "";

		try {
			ds = new com.whir.common.util.DataSourceBase().getDataSource();
			conn = ds.getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery("select emp_id from org_employee where ( useraccounts ='" + username
					+ "' ) and  userIsActive=1 and userIsdeleted=0");

			while (rs.next()) {
				if (rs.getString(1) != null && !rs.getString(1).toString().equals("")
						&& !rs.getString(1).toString().equals("null"))
					returnValue = rs.getString(1);
			}
			rs.next();
			stmt.close();
			conn.close();

			return returnValue;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex1) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex1) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex1) {
				}
			}
		}
		return username;
	}

	// 发送RTX提醒
	private String getAccount(String userIds) {

		javax.sql.DataSource ds = null;
		java.sql.Connection conn = null;
		java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;

		ConversionString con = new ConversionString(userIds);
		String userIdStr = con.getUserIdString() + ",";
		// userIdStr += this.getUserByGroup(con.getGroupIdString()) + ",";
		// userIdStr += this.getUserByOrg(con.getOrgIdString());
		StringBuffer allUserIdBuffer = new StringBuffer();
		userIds = userIds.replace("|", ",").replace(",,", ",");
		userIdStr = userIdStr.replaceAll(",,", ",").replaceAll(",,", ",");
		// System.out.println("=================================:::::::::::::::::::"+userIds);
		// System.out.println("=================================userIdStr:::::::::::::::::::"+userIdStr);
		userIdStr = userIds;
		while (userIdStr.startsWith(",")) {
			userIdStr = userIdStr.substring(1, userIdStr.length() - 1);
		}
		while (userIdStr.endsWith(",")) {
			userIdStr = userIdStr.substring(0, userIdStr.length() - 1);
		}

		try {
			ds = new com.whir.common.util.DataSourceBase().getDataSource();
			conn = ds.getConnection();
			stmt = conn.createStatement();
			String instr = getSqlStrByArrays(userIdStr.split(","), 500, "emp_id");

			rs = stmt.executeQuery("select useraccounts from org_employee where ( " + instr
					+ " ) and  userIsActive=1 and userIsdeleted=0");
			allUserIdBuffer = new StringBuffer();
			while (rs.next()) {
				if (rs.getString(1) != null && !rs.getString(1).toString().equals("")
						&& !rs.getString(1).toString().equals("null"))
					allUserIdBuffer.append(rs.getString(1)).append("|");
			}
			rs.next();
			stmt.close();
			conn.close();

			// if (allUserIdBuffer.length() > 1) {
			// allUserIdBuffer = allUserIdBuffer.deleteCharAt(allUserIdBuffer
			// .length() - 1);
			// }
			// com.whir.integration.realtimemessage.Realtimemessage util = new
			// com.whir.integration.realtimemessage.Realtimemessage();

			// util.setPushType("2");

			// util.sendNotify(allUserIdBuffer.toString(), title, content);
			String ids = allUserIdBuffer.toString();
			return ids;

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException ex1) {
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException ex1) {
				}
			}

			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException ex1) {
				}
			}
		}
		return "";
	}

	/**
	 * 发送news消息
	 * 
	 * @param touser
	 * @param text
	 * @param module
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public boolean sendMsg(String touser, String title, String description, String url, String picurl, String module,
			Map<?, ?> params) {
		logger.debug("-->>touser:"+touser+"title:"+title+"description:"+description+"url:"+url+"picurl:"+picurl+"module:"+module);
		// touser = touser+","+getAccount(touser);
		
		String touserempid = touser;
		UserBD bd  = new UserBD();
		MoveOAmanagerBD moBD = new MoveOAmanagerBD();
		String rlid = "";
		try {
			rlid = moBD.getCorpSetPO().getLast_relactionId();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		touser =  touser.replace(",", "|");
		logger.debug("===touserfinal==="+touser+"======");
		System.out.println("===touserfinal==="+touser+"======");
		System.out.println("rlid::::"+rlid);
		//匹配同步选择的账号，简码，身份证号
		if("userAccount".equals(rlid)){
			touser = touser.replace("|", ",");
			logger.debug("sys!!!@@@"+touser+"======");
			if("govdocument".equals(module)){
				touser = touser.substring(1, touser.length()-1);
			}
	    	List<?> poList = bd.getUserPOList(touser);
	    	touser ="";
	    	for(int i =0 ; i<poList.size(); i++){
	    		UserPO upo = (UserPO)poList.get(i);
	    		touser += upo.getUserAccounts()+"|";
	    	}
	    	touser = touser.substring(0, touser.length()-1);
	    	logger.debug("touser--------------------》"+touser);
	    	logger.debug("!!!@@@%%%"+touser);
		}else if("userSimpleName".equals(rlid)){
			touser = touser.replace("|", ",");
			List<?> poList = bd.getUserPOList(touser);
			touser ="";
			for(int i =0 ; i<poList.size(); i++){
	    		UserPO upo = (UserPO)poList.get(i);
	    		touser += upo.getUserSimpleName()+"|";
	    	}
	    	touser = touser.substring(0, touser.length()-1);
		}else if("empIdCard".equals(rlid)){
			touser = touser.replace("|", ",");
			List<?> poList = bd.getUserPOList(touser);
			touser ="";
			for(int i =0 ; i<poList.size(); i++){
	    		UserPO upo = (UserPO)poList.get(i);
	    		touser += upo.getEmpIdCard()+"|";
	    	}
	    	touser = touser.substring(0, touser.length()-1);
		}
		getAccount(touserempid);
		String[] array = touser.split("\\|");
		if (array.length > 500) {
			StringBuffer touserbuffer = new StringBuffer();
			for (int i = 0; i < array.length; i++) {
				if (array[i] != null && !"".equals(array[i])) {
					touserbuffer.append(array[i]).append("|");
				}
				if (i > 0 && i % 499 == 0) {
					sendMsgInner(touserbuffer.toString().substring(0,touserbuffer.toString().length()-1), title, description, url, picurl, module, params);
					touserbuffer = new StringBuffer();
				}
				if (i == array.length-1) {
					sendMsgInner(touserbuffer.toString().substring(0,touserbuffer.toString().length()-1), title, description, url, picurl, module, params);
				}
			}
		} else {
			sendMsgInner(touser, title, description, url, picurl, module, params);
		}

		return true;
	}

	/**
	 * 发送news消息
	 * 
	 * @param touser
	 * @param text
	 * @param module
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private boolean sendMsgInner(String touser, String title, String description, String url, String picurl,
			String module, Map<?, ?> params) {
		logger.debug("touser----->>"+touser);
		if (!Constants.use)
			return true;
		try {
			// String agentid = "'";
			touser = touser.replace(",", "|");
			// WeiXinUtils.sendNewsMsg(title, description, url, picurl, agentid,
			// touser, "", "");

			Iterator<?> it = params.keySet().iterator();
			String paramString = null;
			String turl = "publicsession";
			while (it.hasNext()) {
				String key = (String) it.next();
				if (paramString == null) {
					paramString = key + "=" + params.get(key);
				} else {
					paramString = paramString + "&" + key + "=" + params.get(key);
				}
				turl = turl + "&" + key + "&" + params.get(key);
			}
			logger.debug("--->>paramString:"+paramString+"turl:"+turl);
			// paramString = paramString + "&t="
			// + new sun.misc.BASE64Encoder().encode((turl).getBytes());
			if ("mail".equals(module)) {
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/mail.jpg";
				// }
				if (url == null) {
					url = Constants.oaserverurl + "/mail/receiveMailDetail.controller?mailId=" + params.get("mailid")
							+ "&mailuserId=" + params.get("mailuserid") + "&cloudcontrol=" + params.get("cloudcontrol") + "&detailType=receive";
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

				WeiXinUtils.sendNewsMsg(title, description, url, picurl, mailMenu.getCorpid(), touser, null, null);
			} else if ("yjpress".equals(module)) {
				System.out.println("进入预警信息提醒模块!");
				// evo\weixin\workflow\workflow_open.jsp
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/flow.jpg";
				// }
				if (url == null) {
					url = Constants.oaserverurl + "/evo/weixin/workflow/yj_press.jsp?" + paramString;
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
				System.out.println("url::::"+url);
				WeiXinUtils.asynSendNewsMsg(title, description, url, picurl,  "1000017", "@all", null, null);
			} else if ("information".equals(module)) {
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/info.jpg";
				// }
				if (url == null) {
					url = Constants.oaserverurl + "/information/infoDetail.controller?infoId="
							+ params.get("informationId") + "&informationType=" + params.get("informationType")
							+ "&channelId=" + params.get("channelId");
					// url = Constants.oaserverurl +
					// "/information/infoDetail.controller?" + paramString;
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

				WeiXinUtils.sendNewsMsg(title, description, url, picurl, infoMenu.getCorpid(), touser, null, null);
			} else if ("workflow".equals(module)) {
				// evo\weixin\workflow\workflow_open.jsp
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/flow.jpg";
				// }
				if (url == null) {
					url = Constants.oaserverurl + "/evo/weixin/workflow/workflow_open.jsp?" + paramString;
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";

				WeiXinUtils.asynSendNewsMsg(title, description, url, picurl,  workflowMenu.getCorpid(), touser, null, null);
			} else if ("govdocument".equals(module)) {
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/gov.jpg";
				// }
				if (url == null) {
					// url = Constants.oaserverurl +
					// "/evo/weixin/govdocument/govdocumentmanager_detail.jsp?"
					// + paramString;

					url = Constants.oaserverurl + "/doc/openReceiveDetail.controller?" + paramString;
				}

				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
				WeiXinUtils.sendNewsMsg(title, description, url, picurl, govMenu.getCorpid(), touser, null, null);

			} else if ("meeting".equals(module)) {
				// if (picurl == null) {
				// picurl = Constants.oaserverurl + "/evo/weixin/pic/gov.jpg";
				// }
				if (url == null) {
					// url = Constants.oaserverurl +
					// "/evo/weixin/govdocument/govdocumentmanager_detail.jsp?"
					// + paramString;

					url = Constants.oaserverurl + "/meeting/meetingNoticeDetail.controller?" + paramString;
				}

				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
				WeiXinUtils.sendNewsMsg(title, description, url, picurl, meetingMenu.getCorpid(), touser, null, null);
			}else if("questionnaire".equals(module)){
				logger.debug("questionnaire---->>推送开始");
				if (url == null) {
					url = Constants.oaserverurl + "/naire/questionnaireAnswer.controller?" + paramString;
					logger.debug("questionnaire---->>推送开始---url"+url);
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
				WeiXinUtils.sendNewsMsg(title, description, url, picurl, naireMenu.getCorpid(), touser, null, null);
			}else if ("0".equals(module)) {
				// 集成小鱼 添加修改 （将推送消息推送到消息小助手中）
				WeiXinUtils.sendNewsMsg(title, description, url, picurl, module, touser, null, null);
			} else {
				if (url == null) {
					url = Constants.oaserverurl + "/evo/weixin/information/information_view.jsp?" + paramString;
				}
				url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + Constants.corpid
						+ "&redirect_uri=" + URLEncoder.encode(url, HTTP.UTF_8)
						+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
				WeiXinUtils.sendNewsMsg(title, description, url, picurl, module, touser, null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 同步用户
	 * 
	 * @return
	 */
	public boolean updateUser(String id) {
		logger.debug("-----ininupdateuser"+id);
		if (!Constants.use)
			return true;
		NewEmployeeBD bd = new NewEmployeeBD();

		EmployeeVO oldEmp = bd.loadEmployee(Long.parseLong(id));

		// , String gender, String phone,
		// String email, String weixinid, String postion, String orgid
		String name = oldEmp.getEmpName();
		String phone = oldEmp.getEmpMobilePhone();
		logger.debug("-----ininphone"+phone);
		String email = oldEmp.getEmpEmail();
		String position = oldEmp.getEmpPosition();
		// String orgIdString = oldEmp.getOrganizations();
		// String gender = String.valueOf(oldEmp.getEmpSex());
		UserService us = new UserService();
		java.lang.Object[] person = us.getUserDetails(id);

		String orgId = person[6].toString();
		String weiXinOrgId = this.getWeiXinOrgId(orgId);
		if (weiXinOrgId == null) {
			return false;
		}
		WeiXinUtils.User user = new WeiXinUtils.User();
		user.setName(name);

		user.setDepartment(new int[] { Integer.parseInt(weiXinOrgId) });
		if (phone == null && email == null) {
			email = person[1].toString() + "@whir.net";
		}
		if ("".equals(phone) && "".equals(email)) {
			email = person[1].toString() + "@whir.net";
		}
		user.setMobile(phone);
		user.setEmail(email);
		MoveOAmanagerBD moBD = new MoveOAmanagerBD();
		String rlid = "";
		try {
			rlid = moBD.getCorpSetPO().getLast_relactionId();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String uid = "";
		//匹配同步选择的账号，简码，身份证号
		if("empId".equals(rlid)){
			uid = id;
		}else if("userAccount".equals(rlid)){
			uid = oldEmp.getUserAccounts();
		}else if("userSimpleName".equals(rlid)){
			uid = oldEmp.getUserSimpleName();
		}else if("empIdCard".equals(rlid)){
			uid = oldEmp.getEmpIdCard();
		}else{
			uid = id;
		}
		logger.debug("updateUser-----uid-------"+uid);
		user.setUserid(uid);
		user.setPosition(position);
		String secret = loadSyncSecret();
		logger.debug("secret----------->>"+secret);
		return WeiXinUtils.updateUser(user,secret);
	}

	/**
	 * 同步用户
	 * 
	 * @return
	 */
	public boolean addUser(String id) {
		logger.debug("addUser------------"+id);
		if (!Constants.use)
			return true;
		NewEmployeeBD bd = new NewEmployeeBD();
		EmployeeVO oldEmp = bd.loadEmployee(Long.parseLong(id));
		MoveOAmanagerBD moBD = new MoveOAmanagerBD();
		String rlid = "";
		try {
			rlid = moBD.getCorpSetPO().getLast_relactionId();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String uid = "";
		//匹配同步选择的账号，简码，身份证号
		if("empId".equals(rlid)){
			uid = id;
		}else if("userAccount".equals(rlid)){
			uid = oldEmp.getUserAccounts();
		}else if("userSimpleName".equals(rlid)){
			uid = oldEmp.getUserSimpleName();
		}else if("empIdCard".equals(rlid)){
			uid = oldEmp.getEmpIdCard();
		}else{
			uid = id;
		}
		logger.debug("addUser-----uid-------"+uid);
		if (WeiXinUtils.getUser(uid)) {
			return updateUser(id);
		}
		String name = oldEmp.getEmpName();
		String phone = oldEmp.getEmpMobilePhone();
		String email = oldEmp.getEmpEmail();
		String position = oldEmp.getEmpPosition();
		UserService us = new UserService();
		java.lang.Object[] person = us.getUserDetails(id);
		String orgId = person[6].toString();
		if (orgId == null || orgId.length() == 0) {
			return false;
		}
		WeiXinUtils.User user = new WeiXinUtils.User();
		user.setName(name);
		user.setDepartment(new int[] { Integer.parseInt(this.getWeiXinOrgId(orgId)) });
		if (phone == null && email == null) {
			email = "whir" + person[0].toString() + "@whir.net";
		}
		if ("".equals(phone) && "".equals(email)) {
			email = "whir" + person[0].toString() + "@whir.net";
		}
		user.setMobile(phone);
		user.setEmail(email);
		user.setUserid(uid);
		user.setPosition(position);
		String secret = loadSyncSecret();
		logger.debug("secret----------->>"+secret);
		return WeiXinUtils.createUser(user,secret);

	}

	/**
	 * 删除用户
	 * 
	 * @param id
	 * @return
	 */
	public boolean delUser(String id) {
		logger.debug("delUser---------->>"+id);
		if (!Constants.use) {
			return true;
		}
		NewEmployeeBD bd = new NewEmployeeBD();
		EmployeeVO oldEmp = bd.loadEmployee(Long.parseLong(id));
		MoveOAmanagerBD moBD = new MoveOAmanagerBD();
		String rlid = "";
		try {
			rlid = moBD.getCorpSetPO().getLast_relactionId();
		} catch (HibernateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String uid = "";
		//匹配同步选择的账号，简码，身份证号
		if("empId".equals(rlid)){
			uid = id;
		}else if("userAccount".equals(rlid)){
			uid = oldEmp.getUserAccounts();
		}else if("userSimpleName".equals(rlid)){
			uid = oldEmp.getUserSimpleName();
		}else if("empIdCard".equals(rlid)){
			uid = oldEmp.getEmpIdCard();
		}else{
			uid = id;
		}
		logger.debug("delUser-----uid----->>"+uid);
		String secret = loadSyncSecret();
		logger.debug("secret----------->>"+secret);
		return WeiXinUtils.deleteUser(uid,secret);
	}

	/**
	 * 删除组织
	 * 
	 * @param id
	 * @return
	 */
	public boolean delOrgnization(String id) {
		logger.debug("删除组织-------------->>开始"+id);
		if (!Constants.use)
			return true;
		id = this.getWeiXinOrgId(id);
		String secret = loadSyncSecret();
		logger.debug("secret----------->>"+secret);
		logger.error("delOrgnization-----secret-------"+secret);
		if (id == null)
			return true;
		if (WeiXinUtils.deleteOrgnization(id,secret)) {
			// 删除关系
			try {
				com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
				String sql = "DELETE EVO_WEIXIN_ORGMAP  WHERE OA_ORG_ID = " + id;
				int result = dbopt.executeUpdate(sql);
				if (result < 1) {
					logger.error("------删除组织失败信息------");
				}
				dbopt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/*
	 * 初始化菜单
	 */
	public void initMenu() {
		logger.debug("initMenu--------------->>同步菜单开始");
		if (!Constants.use)
			return;
		List<CorpSetAppPO> corpAppList = null;
		try {
			corpAppList = managerBD.getCorpSetAppPO();
		} catch (HibernateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*Iterator<?> it = menuListeners.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			Menu menu = null;
			try {
				menu = menuListeners.get(key).getMenu();
			} catch (Exception e) {
				System.out.println("===" + menuListeners.get(key).getClass());
				e.printStackTrace();
			}
			String secret = "";
					
			for (int i = 0, size = corpAppList.size(); i < size; i++) {
				if(key.equals(corpAppList.get(i).getCorpid())){
					logger.debug("initMenu--------secret------->>"+corpAppList.get(i).getAppname());
					secret = corpAppList.get(i).getModuleSecret();
				}
			}
			WeiXinUtils.removeMenu(key,secret);
			String jsonMenu = JSONObject.fromObject(menu).toString();
			if (WeiXinUtils.createMenu(key, jsonMenu,secret)) {
				logger.info("创建菜单成功!agentId=" + key);
			} else {
				logger.error("创建菜单失败 agentid=" + key);
			}
		}*/
		String oaserverurl = Constants.oaserverurl;
		// String addressid = ConfigReader.getReader().getAttribute("Weixin",
		// "address_appid");
		// String mailid = ConfigReader.getReader().getAttribute("Weixin",
		// "mail_appid");
		String addressid = addressMenu.getCorpid();
		String mailid = mailMenu.getCorpid();
		String meetid = meetingMenu.getCorpid();
		String naireid = naireMenu.getCorpid();
		String worklogid = worklogMenu.getCorpid();
		String attendanceid = attendanceMenu.getCorpid();
		
		String workid = workflowMenu.getCorpid();
		String infoid = infoMenu.getCorpid();
		String govid = govMenu.getCorpid();
		String foumid = forumMenu.getCorpid();
		
		List<Object> listCust = new ArrayList<Object>();
		String id ="";
		String secret = "";
		if (corpAppList != null) {
			String appValue = "";
			for (int i = 0, size = corpAppList.size(); i < size; i++) {
				appValue = corpAppList.get(i).getAppid();
				id = corpAppList.get(i).getCorpid();
				if (StringUtils.isEmpty(id)) {
					continue;
				}
				String[] arr = appValue.split("_");
				if(arr.length == 3){//自定义模块					
					listCust.add(appValue+"|"+id+"|"+secret);
				}
				
			}
			
		}
		
		try {
			if (addressid != null && !"".equals(addressid)) {
				initAddressMenu(addressid, oaserverurl,"");
			}
			if (mailid != null && !"".equals(mailid)) {
				initMailMenu(mailid, oaserverurl,"");
			}
			if (meetid != null && !"".equals(meetid)) {
				initMeetingMenu(meetid, oaserverurl,"");
			}
			if (listCust != null && listCust.size()>0){
				logger.debug("~!!~~!!~"+listCust.size());
				String str ="";
				String menuId ="";
				String appid ="";
				String menusecret = "";
				for(int j=0;j<listCust.size();j++){
					str = listCust.get(j).toString();
					logger.debug("~!@@!~"+listCust.size());
					String[] sarr = str.split("\\|");
					if(sarr.length>2){
					   if(sarr[2] != null && !"null".equals(sarr[2])&& !"".equals(sarr[2])){
						   menusecret = sarr[2];
					   }
				    }
					menuId = sarr[0].split("#!#@!@")[0].split("_")[2];
				    appid = sarr[1];
				    logger.debug("!@#!@#appid="+appid+"  menuId="+menuId);
				    String menuName = sarr[0].split("#!#@!@")[1];
					initCustMenu(appid,oaserverurl,menuId,menuName,menusecret);				
				}
			}
			if (naireid != null && !"".equals(naireid)) {
				initNaireMenu(naireid, oaserverurl,"");
			}
			if (worklogid != null && !"".equals(worklogid)) {
				initWorkLogMenu(worklogid, oaserverurl,"");
			}
			if (attendanceid != null && !"".equals(attendanceid)) {
				initAttendanceMenu(attendanceid, oaserverurl,"");
			}
			
			if (workid != null && !"".equals(workid)) {
				initWorkflowMenu(workid, oaserverurl, "");
			}
			if (infoid != null && !"".equals(infoid)) {
				initInformationMenu(infoid, oaserverurl, "");
			}
			if (govid != null && !"".equals(govid)) {
				initGovdocumentMenu(govid, oaserverurl, "");
			}
			if (foumid != null && !"".equals(foumid)) {
				initForumMenu(foumid, oaserverurl, "");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		/*
		 * Iterator<?> keys = menuListeners.keySet().iterator(); while
		 * (keys.hasNext()) { String agentid = (String) keys.next();
		 * IMenuManager menu = menuListeners.get(agentid);
		 * 
		 * String json = JSONObject.fromObject(menu.getMenu()).toString();
		 * 
		 * if (WeiXinUtils.removeMenu(agentid)) { if
		 * (WeiXinUtils.createMenu(agentid, json)) {
		 * 
		 * } else { logger.error("创建菜单失败 agentid=" + agentid); } } else {
		 * logger.error("删除菜单失败 agentid=" + agentid); } }
		 */
	}

	/**
	 * 创建通讯录菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initAddressMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("通讯录创建开始---------------》》"+secret);
		if (!Constants.use)
			return;
		String menuUrl = oaurl + "/persons/personList.controller?WeixinCorpSecret="+secret;
		// String menu1url = oaurl + "/evo/weixin/persons/person_org_list.jsp";
		// String menu2url = oaurl + "/evo/weixin/persons/person_all_list.jsp";

		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"通讯录\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menuUrl, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("AddressMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("通讯录创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("通讯录删除菜单失败 agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}

	}

	/**
	 * 创建工作流菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initWorkflowMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建工作流菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/dealfile/list.controller?workStatus=0&WeixinCorpSecret="+secret;
		String menu2url = oaurl + "/dealfile/list.controller?workStatus=101&WeixinCorpSecret="+secret;
		String menu3url = oaurl + "/dealfile/list.controller?workStatus=1100&WeixinCorpSecret="+secret;
		String menu4url = oaurl + "/workflow/listflow.controller?WeixinCorpSecret="+secret;
		
		String json = "{\"button\": [{\"name\": \"文件办阅\",\"type\": \"view\",\"url\": \"https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +
				Constants.corpid +
				"&redirect_uri=" +
				URLEncoder.encode(menu1url, HTTP.UTF_8) +
				"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"}" +
				",{\"name\": \"文件查阅\",\"sub_button\": [{\"name\": \"已办文件\",\"type\": \"view\",\"url\": \"https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +
				Constants.corpid +
				"&redirect_uri=" +
				URLEncoder.encode(menu2url, HTTP.UTF_8) +
				"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"}" +
				",{\"name\": \"我的文件\",\"type\": \"view\",\"url\": \"https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +
				Constants.corpid +
				"&redirect_uri=" +
				URLEncoder.encode(menu3url, HTTP.UTF_8) +
				"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"}]}," +
				"{\"name\": \"新建流程\",\"type\": \"view\",\"url\": \"https://open.weixin.qq.com/connect/oauth2/authorize?appid=" +
				Constants.corpid  +
				"&redirect_uri=" +
				URLEncoder.encode(menu4url, HTTP.UTF_8) +
				"&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"}" + "] }" + "" + "" + "";
		logger.error("WorkflowMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("删除菜单失败 agentid=" + agentid);
		}
	}

	/**
	 * 创建公文菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initGovdocumentMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建公文菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/doc/getReceiveFileBox.controller?WeixinCorpSecret="+secret;
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"全部公文\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }" + "] }" + "" + "" + "";
		logger.error("GovdocumentMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("删除菜单失败 agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}

	}

	/**
	 * 创建邮件菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initMailMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建邮件菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/mail/mailBox.controller?WeixinCorpSecret="+secret;// mail%2F
		String menu2url = oaurl + "/mail/new.controller?WeixinCorpSecret="+secret;

		// URLEncoder.encode("http://www.blogjava.net/duansky/archive/2012/03/18/372137.html",
		// HTTP.UTF_8)
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"我的邮箱\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"发邮件\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("MailMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("邮件创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("邮件删除菜单失败  agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	
	/**
	 * 创建会议助手菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initMeetingMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建会议菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/meeting/meetingApply.controller?WeixinCorpSecret="+secret;// mail%2F
		String menu2url = oaurl + "/meeting/meetingNoticeList.controller?WeixinCorpSecret="+secret;

		// URLEncoder.encode("http://www.blogjava.net/duansky/archive/2012/03/18/372137.html",
		// HTTP.UTF_8)
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"发起预定\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"会议通知\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("MeetingMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("会议助手创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("会议助手删除菜单失败  agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
    
	/**
	 * 创建自定义模块菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initCustMenu(String agentid, String oaurl,String menuId,String menuName,String secret) throws UnsupportedEncodingException {
		System.out.println("!!!@@##**");
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/custmenu/custMenu.controller?menuId="+menuId+"&menuName="+menuName+"&WeixinCorpSecret="+secret;// mail%2F

		// URLEncoder.encode("http://www.blogjava.net/duansky/archive/2012/03/18/372137.html",
		// HTTP.UTF_8)
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"进入菜单\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
			    + " ] }" + "" + "" + "";
		logger.error("CustMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			System.out.println("^^@@##**");
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("自定义模块创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("自定义模块删除菜单失败  agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	/**
	 * 创建信息菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initInformationMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		if (!Constants.use)
			return;
		logger.error("initInformationMenu------->agentid:" + agentid +"secret:"+secret);
		String menu1url = oaurl + "/information/infoList.controller?WeixinCorpSecret="+secret;
		String menu2url = oaurl + "/information/channelList.controller?WeixinCorpSecret="+secret;
		String menu3url = oaurl + "/information/create.controller?WeixinCorpSecret="+secret;
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"全部信息\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"信息栏目\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"新建信息\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu3url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("InformationMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("删除菜单失败 agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	
	/**
	 * 创建问卷调查菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initNaireMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建问卷调查菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/naire/getAnswerQuestionnaireList.controller?WeixinCorpSecret="+secret;
		// String menu2url =
		// "http%3A%2F%2F192.168.0.179%3A7001%2Fdefaultroot%2Fevo%2Fweixin%2Fpersons%2Fa.jsp";

		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"问卷调查\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }" + "] }" + "" + "" + "";
		logger.error("initNaireMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("问卷调查创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("问卷调查删除菜单失败 agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	
	/**
	 * 创建微信考勤菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initAttendanceMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建微信考勤-->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/attendance/loadWxLocation.controller?WeixinCorpSecret="+secret;// mail%2F
		String menu2url = oaurl + "/attendance/myAttendance.controller?WeixinCorpSecret="+secret;

		// URLEncoder.encode("http://www.blogjava.net/duansky/archive/2012/03/18/372137.html",
		// HTTP.UTF_8)
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"我要打卡\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"我的考勤\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("initAttendanceMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("考勤问卷调查创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("考勤删除菜单失败  agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	
	/**
	 * 创建工作日志菜单
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initWorkLogMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		logger.debug("创建工作日志菜单--->>"+agentid+"oaurl:"+oaurl+"secret:"+secret);
		if (!Constants.use)
			return;
		String menu1url = oaurl + "/worklog/getWorkLogList.controller?WeixinCorpSecret="+secret;// mail%2F
		String menu2url = oaurl + "/worklog/getUnderlingList.controller?WeixinCorpSecret="+secret;

		// URLEncoder.encode("http://www.blogjava.net/duansky/archive/2012/03/18/372137.html",
		// HTTP.UTF_8)
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"我的日志\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"下属日志\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("initWorkLogMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("日志创建菜单失败 agentid=" + agentid);
				// throw new RuntimeException("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("日志删除菜单失败  agentid=" + agentid);
			// throw new RuntimeException("删除菜单失败 agentid=" + agentid);
		}
	}
	
	/**
	 * 创建论坛
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void initForumMenu(String agentid, String oaurl,String secret) throws UnsupportedEncodingException {
		if (!Constants.use)
			return;
		logger.error("initForumMenu------->agentid:" + agentid +"secret:"+secret);
		String menu1url = oaurl + "/post/index.controller?WeixinCorpSecret="+secret;// mail%2F
		String menu2url = oaurl + "/post/list.controller?WeixinCorpSecret="+secret;
		String menu3url = oaurl + "/post/new.controller?WeixinCorpSecret="+secret;
		String json = "{\"button\":[ {\"type\":\"view\",\"name\":\"最新帖子\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu1url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"论坛板块\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu2url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  }"
				+ ",{\"type\":\"view\",\"name\":\"发帖\",\"url\":\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ Constants.corpid
				+ "&redirect_uri="
				+ URLEncoder.encode(menu3url, HTTP.UTF_8)
				+ "&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect\"  } ] }" + "" + "" + "";
		logger.error("initForumMenu------->json:" + json);
		if (WeiXinUtils.removeMenu(agentid,secret)) {
			if (WeiXinUtils.createMenu(agentid, json,secret)) {

			} else {
				logger.error("创建菜单失败 agentid=" + agentid);
			}
		} else {
			logger.error("删除菜单失败  agentid=" + agentid);
		}
	}

	/**
	 * 注册消息处理类
	 */
	public static void registerMsgListener(String id, IMsgListener listener) {
		msgListeners.put(id, listener);
	}

	/**
	 * 注册菜单处理类
	 */
	public static void registerMenuManager(String id, IMenuManager listener) {
		menuListeners.put(id, listener);
	}

	public static String processRequest(String msg) {
		String respMessage = null;
		try {
			// 默认返回的文本消息内容
			// String respContent = "请求处理异常，请稍候尝试！";
			// agentid
			// xml请求解析
			Map<String, String> requestMap = MessageUtil.parseXml(msg);

			// System.out.println("Event=="+requestMap.get("Event"));
			String agentID = requestMap.get("AgentID");
			String userId = requestMap.get("FromUserName");
			try {
				Long.parseLong(userId);

			} catch (Exception ex) {
				requestMap.put("FromUserName", getUserId(userId));
			}
			// 执行
			// Iterator<?> keys = msgListeners.keySet().iterator();
			IMsgListener listener = msgListeners.get(agentID);
			logger.debug("agentID=" + agentID + " listener=" + listener.getClass());
			if (listener != null) {
				BaseMessage message = listener.processMsg(requestMap);

				if (message != null) {
					if (message instanceof TextMessage) {
						respMessage = MessageUtil.textMessageToXml((TextMessage) message);
						;
					} else if (message instanceof NewsMessage) {
						respMessage = MessageUtil.newsMessageToXml((NewsMessage) message);
						;
					}
				}
			}
			logger.debug("agentID=" + agentID + "  END");
		} catch (Exception e) {
			e.printStackTrace();
			// System.out.println(e);
			// respMessage = "有异常了。。。";
		}
		return respMessage;
	}

	public static String generateToken(String token, String... args) {
		String name = null;
		String value = null;
		String url = "";
		int index = 0;
		for (String s : args) {
			index++;
			if (index % 2 == 1) {
				name = s;
			} else {
				value = s;
				url += "&" + name + "&" + value;
			}
		}
		return org.apache.commons.codec.binary.Base64.encodeBase64String((token + url).getBytes()).replace("\r\n", "");
		// return new sun.misc.BASE64Encoder().encode((token + url).getBytes())
		// .replaceAll("\r\n", "");
	}
	public static void setUserKey(String userKey,HttpServletRequest request){
		System.out.println("!!@@----userkeyuserkey="+userKey);
		logger.debug("setUserKey:开始");
		HttpSession session = request.getSession();
		session.setAttribute("userKey", userKey);
		if(session.getAttribute("OaToken")==null||"".equals(session.getAttribute("OaToken").toString())){
			String oaToken = WebServiceUtils.getEzOfficeToken(userKey);
			session.setAttribute("OaToken",oaToken);
		}
		logger.debug("setUserKey:结束");
	}
	
	/**
	 * 获取不同应用对应Token
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> loadWeixinTokenPO(String secret){
		logger.debug("------------------获取token-----------------");
		String sql = "";
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
		Map<String, String> map = new HashMap<String, String>();
		try{
			sql = "select wxToken,tokenTimeStamp from ezmobile_wxtoken where corpsecret = '"+secret+"'";
			ResultSet rs = dbopt.executeQuery(sql);
			if (rs.next()) {
				logger.error("rs查询成功---------"+ rs.getString(1));
				map.put("wxToken", rs.getString(1));
				map.put("tokenTimeStamp", rs.getString(2));
			} else {
				rs.close();
			}
			rs.close();
		} catch (Exception ex) {
			logger.error("---------获取token失败！---------");
			ex.printStackTrace();
			throw new RuntimeException("异常信息---->" + ex.getMessage());
		} finally {
				try {
					dbopt.close();
				} catch (Exception ex1) {
				}
		}
		logger.debug("map-----wxToken----"+ map.get("wxToken"));
		logger.debug("map----tokenTimeStamp-----"+ map.get("tokenTimeStamp"));
		return map;
	}
	
	/**
	 * 获取企业微信对应secret
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public String loadSyncSecret(){
		logger.debug("------------------获取loadSyncSecret-----------------");
		String sql = "";
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
		String secret = "";
		try{
			sql = "select moduleSecret from SYS_CORP_SET_APP where appid='syncOrgUser'";
			ResultSet rs = dbopt.executeQuery(sql);
			if (rs.next()) {
				logger.error("rs查询成功---------"+ rs.getString(1));
				secret =  rs.getString(1);
			} else {
				rs.close();
			}
			rs.close();
		} catch (Exception ex) {
			logger.error("---------获取token失败！---------");
			ex.printStackTrace();
			throw new RuntimeException("异常信息---->" + ex.getMessage());
		} finally {
				try {
					dbopt.close();
				} catch (Exception ex1) {
				}
		}
		logger.debug("----secret-----"+ secret);
		return secret;
	}
	
	/**
	 * 保存不同应用对应Token
	 * @param appId
	 * @return
	 * @throws Exception
	 */
	public boolean saveWeixinToken(String token,String timeStamp,String secret){
		logger.debug("-----------------保存token开始----------------");
		logger.debug("secret--->>"+secret);
		logger.debug("token--->>"+token);
		String sql = "";
		boolean res  = true;
		com.whir.ezoffice.customdb.common.util.DbOpt dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
		String sql1 = "delete from EZMOBILE_WXTOKEN where CORPSECRET='"+secret+"'";
		try{
			int delResult = dbopt.executeUpdate(sql1);
			if (delResult < 1) {
				logger.debug("delResult------删除数据成功");
			}
			
			sql = "INSERT INTO EZMOBILE_WXTOKEN (CORPSECRET,WXTOKEN,TOKENTIMESTAMP) VALUES ('"+secret+"','"+token+"','"+timeStamp+"')";
		//	"INSERT INTO EVO_WEIXIN_ORGMAP (OA_ORG_ID,WEIXIN_ORG_ID) VALUES (" + id + ","
			//+ existWxOrgId + " )";
			int addResult = dbopt.executeUpdate(sql);
			if (addResult < 1) {
				res = false;
			}
			dbopt.close();
		} catch (Exception ex) {
			logger.error("---------保存token失败！---------");
			ex.printStackTrace();
			res = false;
			throw new RuntimeException("异常信息---->" + ex.getMessage());
		} finally {
				try {
					dbopt.close();
				} catch (Exception ex1) {
				}
		}
		logger.debug("-----------------保存token开始----------------"+res);
		return res;
	}
	
}
