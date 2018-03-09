package com.whir.evo.weixin.actionsupport;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.XmlHelper;
import com.whir.ezoffice.customdb.common.util.DbOpt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 文件办理
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/dealfile")
public class DealFileAction {
	private final Log logger = LogFactory.getLog(DealFileAction.class);

	
	/**
	 * wangshuai
	 * 根据流程id获取对应的流程记录信息
	 * @param workId
	 * @return
	 */
	@RequestMapping("/getProcessRecordInfo")
	public 
	@ResponseBody
	Map<String,Object> getProcessRecordInfo(Long workId) {
		DbOpt dbopt  = new DbOpt();;
		Map<String,Object> map = new HashMap<String,Object>();
		String processId = getProcessId(workId,dbopt) ;
		String dealSql = "";
		ResultSet rs = null;
		try {
	    	dealSql = "select a.id_,a.dealusername,a.dealuserid, a.dealtime, a.dealtype, a.curactivityname, a.dealcontent,a.receivenames ,o.orgName " +
	    			"from (select l.id_,l.dealusername,l.dealuserid, l.dealtime, l.dealtype, l.curactivityname, c.dealcontent,l.receivenames ";
		    dealSql = dealSql + "from ez_flow_action_log l left join ez_flow_re_comment c on l.processinstanceid =c.processinstanceid ";
		    dealSql = dealSql + "and l.curactivityid =c.activityid and l.dealuserid =c.dealuserid  and c.commenttype =0 ";
		    dealSql = dealSql + "where l.processinstanceid ="+processId+" and l.dealtype !='VIEW' ) a,org_organization o," +
		    		"org_organization_user u where a.dealuserid=u.emp_id and u.org_id=o.org_id ";
		    dealSql = dealSql + " order by a.dealtime asc ";
		    //======上面是流程跟踪单的记录===暂时不需要了==
		    dealSql = " select a.DEALUSERNAME,a.DEALTIME,a.DEALACTION,a.RECEIVENAMES from ez_flow_action_log" +
		    " a where a.PROCESSINSTANCEID = "+processId+" order by  a.dealtime asc";
		    
	    	List<String[]> recordList = new ArrayList<String[]>();
			rs  = dbopt.executeQuery(dealSql);
			/*   获取的是流程跟踪单的数据 暂时移除
			while(rs.next()){
				String[] str = new String[9];
				str[0] = rs.getString(1) ;//编号
				str[1] = rs.getString(2) ;//申请人
				str[2] = rs.getString(3) ;//申请人id
				str[3] = rs.getString(9);//申请人所属组织
				str[4] = rs.getString(4) ;//活动生成对应的时间
				str[5] = rs.getString(5) ;//类型
				str[6] = rs.getString(6) ;//活动环节
				str[7] = rs.getString(7) ;//批示内容
				str[8] = rs.getString(8) ;//环节下一个人
				recordList.add(str) ;
			}
			*/
			while(rs.next()){
				String[] str = new String[4];
				str[0] = rs.getString(1) ;//办理人
				str[1] = rs.getString(2) ;//办理时间
				str[2] = rs.getString(3) ;//处理操作
				str[3] = rs.getString(4)==null?"":rs.getString(4);//接收人
				recordList.add(str) ;
			}
			map.put("status","0");
			map.put("data", recordList);
		} catch (Exception e) {
			map.put("status","-1");
			map.put("data", null);
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(dbopt!=null){
				try {
					dbopt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}	
	}
		
		return map ;
		
	}
	
	/**
	 * 根据wf_work表的workId获取对应的ezflowprocessinstanceid参数
	 * @param workId
	 * @param dbopt
	 * @return
	 */
	public String getProcessId(Long workId,DbOpt dbopt){
		String processId = "" ;
		String sql = "select ezflowprocessinstanceid from wf_work where wf_work_id="+workId ;
		try {
			processId = dbopt.executeQueryToStr(sql) ;
		} catch (Exception e) {
			processId = "" ;
			System.out.println("-------ProcessRecord----------getOrgName-----------------获取所属组织出错--------------");
			e.printStackTrace();
		}
		return processId ;
	}
	
	
	
	/**
	 * 文件办阅
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/list")
	public String list(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("-----enter dealfile detail-----");
		String workStatus = ServletRequestUtils.getStringParameter(request, "workStatus", "0");
		logger.debug("workStatus----->" + workStatus);
		model.addAttribute("workStatus", workStatus);
		String title = request.getParameter("title") == null ? "" : request.getParameter("title").toString();
		String offset = request.getParameter("offset") == null ? "0" : request.getParameter("offset").toString();
		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "workflow_searchDealWithList");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("title", title);
		params.put("pageOffset", offset);
		params.put("pageSize", "1");
		params.put("workStatus", "2");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String numTwoRecordCount = XmlHelper.getElement(docXml, "//recordcount");
		model.addAttribute("numTwoRecordCount", numTwoRecordCount);
		String target = "";
		if (workStatus.equals("0") || workStatus.equals("2")) {// 待办OR待阅
			target = "/dealfile/dealfile_list";
		} else if (workStatus.equals("101") || workStatus.equals("102")) {// 已办OR已阅
			target = "/dealfile/dealfile_ed_list";
		} else if (workStatus.equals("1100")) {
			target = "/dealfile/dealfile_my_list";
		}
		return target;
	}

	/**
	 * ajax方式获取列表数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getListData")
	public String getListData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String workStatus = ServletRequestUtils.getStringParameter(request, "workStatus", "0");
		String title = request.getParameter("title") == null ? "" : request.getParameter("title").toString();
		System.out.println("title----------------->"+title);
		title=title.replace("&", "&amp;");
		title=title.replace("%", "\\\\%");
		String offset = request.getParameter("offset") == null ? "0" : request.getParameter("offset").toString();
		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "workflow_searchDealWithList");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("title", title);
		params.put("pageOffset", offset);
		params.put("pageSize", "15");
		params.put("workStatus", workStatus);
		if("1".equals(workStatus)){
			params.put("openType", "waitingRead");
		}else if("101".equals(workStatus)){//已办文件
			params.put("openType", "dealed");
		}else if("102".equals(workStatus)){//已阅
			params.put("openType", "readed");
		}else if("1100".equals(workStatus)){//我的文件
			params.put("openType", "myTask");
		}
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("========workStatus======getListData===="+workStatus+"\n\ndocXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		String recordCount = XmlHelper.getElement(docXml, "//recordcount");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			String nomore = "\"\"";
			if (recordCount != null && Integer.parseInt(offset) + 15 < Integer.parseInt(recordCount)) {
				nomore = "\"" + "true" + "\"";
			}
			XmlHelper.printResult(response, "success", new String[] { docData, nomore,
					"\"" + String.valueOf(Integer.parseInt(offset) + 15) + "\"",
					"\"" + String.valueOf(recordCount) + "\"" });
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}

	/**
	 * 进入流程详情页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/process")
	public String detail(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String workId, Model model) {
		logger.debug("-----enter dealfile detail-----");
		String workStatus = request.getParameter("workStatus") == null ? "0" : request.getParameter("workStatus")
				.toString();
		String target = "/dealfile/dealfile_process";// 待办文件
		if ("1100".equals(workStatus)) {
			target = "/dealfile/dealfile_myprocess";// 我的文件
		} else if ("101".equals(workStatus)) {
			target = "/dealfile/dealfile_processed";// 已办文件
		} else if ("2".equals(workStatus)) {
			target = "/dealfile/dealfile_detail";// 待阅文件
		} else if ("102".equals(workStatus)) {
			target = "/dealfile/dealfile_detail_ezflow";// 已阅文件
		}
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "workFlow_getWorkInfoByworkId");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		logger.debug("description1--------------->"+description);
		//令牌超期
		if("TokenError".equals(description)){
			logger.debug("1111111111111111111111");
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results1--------------->"+results);
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		logger.debug("docXml----->" + docXml);
		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "getDetailResult");
		params2.put("domain", domainId);
		params2.put("workId", workId);
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		description = XmlHelper.getElement(docXml2, "//description");
		logger.debug("description2--------------->"+description);
		//令牌超期
		if("TokenError".equals(description)){
			logger.debug("2222222222222222222222");
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		}
		
		String results2 = XmlHelper.getElement(docXml2, "//result");
		logger.debug("results2--------------->"+results2);
		if (results2 != null && "1".equals(results2)) {
			model.addAttribute("docXml2", docXml2);
		}
		logger.debug("docXml2----->" + docXml2);
		Map<String, String> accParams = new HashMap<String, String>();
		accParams.put("cmd", "workflow_getWFAcc");
		accParams.put("domain", domainId);
		accParams.put("workId", workId);
		String accDocXml = WebServiceUtils.getWebServiceData(accParams,request,userKey);
		String accResults = XmlHelper.getElement(accDocXml, "//result");
		if (accResults != null && "1".equals(accResults)) {
			model.addAttribute("accDocXml", accDocXml);
		}
		logger.debug("docXml2----->" + accDocXml);
		//String orgId = session.getAttribute("orgId").toString();
		//String orgIdString = session.getAttribute("orgIdString").toString();
		String personId = ServletRequestUtils.getStringParameter(request, "personId", "");
		if("".equals(personId) || personId == null ){
			personId = XmlHelper.getElement(docXml2, "//paramList/wf_curemployee_id");
		}
		if("".equals(personId) || personId == null ){
			personId = userId;
		}
		params = new HashMap<String, String>();
		params.put("cmd", "LoadPerson");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("linkManId", personId);
		params.put("linkManType", "3");
		String docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		description = XmlHelper.getElement(docXml3, "//description");
		logger.debug("description3-------------->"+description);
		//令牌超期
		if("TokenError".equals(description)){
			logger.debug("333333333333333333333");
			session.setAttribute("OaToken","");
			docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		results = XmlHelper.getElement(docXml3, "//result");
		logger.debug("results3------------------>"+results);
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml3", docXml3);
		}

		logger.debug("target-------------->"+target);
		
		return target;
	}

	// 进入催办页面
	@RequestMapping(value = "/pressInfo")
	public String pressInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("-----enter dealfile detail-----");
		String target = "/dealfile/dealfile_pressInfo";

		String domainId = session.getAttribute("domainId") == null ? "" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		logger.debug("domainId----->" + domainId);
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);

		String workId = request.getParameter("workId") == null ? "" : request.getParameter("workId");
		String smsRight = request.getParameter("smsRight") == null ? "" : request.getParameter("smsRight");
		logger.debug("workId----->" + workId);
		logger.debug("smsRight----->" + smsRight);
		String userKey = session.getAttribute("userKey").toString();

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "bpm_getPressInfo");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}

		return target;
	}

	// 待办文件 加签页面
	@RequestMapping(value = "/addSign")
	public String addSign(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("-----enter dealfile addSign-----");
		String target = "/dealfile/dealfile_addSign";

		String domainId = session.getAttribute("domainId") == null ? "" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		logger.debug("domainId----->" + domainId);
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);

		String workId = request.getParameter("workId") == null ? "" : request.getParameter("workId");
		logger.debug("workId----->" + workId);
		String userKey = session.getAttribute("userKey").toString();

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "bpm_getAddSignInfo");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}

		return target;
	}

	// 待办文件 转办页面
	@RequestMapping(value = "/tranInfo")
	public String tranInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("-----enter dealfile tranInfo-----");
		String target = "/dealfile/dealfile_tranInfo";
		String domainId = session.getAttribute("domainId") == null ? "" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		logger.debug("domainId----->" + domainId);
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);

		String workId = request.getParameter("workId") == null ? "" : request.getParameter("workId");
		logger.debug("workId----->" + workId);
		String userKey = session.getAttribute("userKey").toString();

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "bpm_getTranInfo");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		return target;
	}

	// 待办文件阅件页面
	@RequestMapping(value = "/selfSend")
	public String selfSend(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("--------阅件开始-------------");
		String domainId = session.getAttribute("domainId") == null ? "" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String userKey = session.getAttribute("userKey").toString();
		logger.debug("domainId----->" + domainId);
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);

		String workId = request.getParameter("workId") == null ? "" : request.getParameter("workId");
		logger.debug("workId----->" + workId);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "bpm_getTranReadInfo");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		params.put("readType", "sendread");
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		logger.debug("docXml（阅件）--------------->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		logger.debug("--------阅件结束-------------");
		return "/dealfile/dealfile_tranreadInfo";
	}
	
	// 查看子表
	@RequestMapping(value = "/subprocess")
	public String subdetail(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String workId, Model model) {
		logger.debug("-----enter dealfile subdetail-----");
		String target = "/dealfile/dealfile_subprocess";
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "workFlow_getWorkInfoByworkId");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}

		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "getDetailResult");
		params2.put("domain", domainId);
		params2.put("workId", workId);
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		description = XmlHelper.getElement(docXml2, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		}
		
		logger.debug("docXml2----->" + docXml2);
		String results2 = XmlHelper.getElement(docXml2, "//result");
		if (results2 != null && "1".equals(results2)) {
			model.addAttribute("docXml2", docXml2);
		}
		return target;
	}

	@RequestMapping(value = "/send1")
	public String send1(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String workId, Model model) {
		logger.debug("-----enter dealfile send1-----");
		String target = "/dealfile/docSend_process";
		logger.debug("workId----->" + workId);
		String userId = session.getAttribute("userId").toString();
		;
		String domainId = session.getAttribute("domainId").toString();
		;
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getSendDocument");
		params.put("workId", workId);
		params.put("userId", userId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("workId", workId);
		}

		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "workFlow_getWorkInfoByworkId");
		params2.put("domain", domainId);
		params2.put("workId", workId);
		params2.put("userId", userId);
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		description = XmlHelper.getElement(docXml2, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		}
		
		String results2 = XmlHelper.getElement(docXml2, "//result");
		if (results2 != null && "1".equals(results2)) {
			model.addAttribute("docXml2", docXml2);
		}
		
		
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String personId = ServletRequestUtils.getStringParameter(request, "personId", "");
		if("".equals(personId) || personId == null ){
			personId = XmlHelper.getElement(docXml2, "//paramList/wf_curemployee_id");
		}
		if("".equals(personId) || personId == null ){
			personId = userId;
		}
		params = new HashMap<String, String>();
		params.put("cmd", "LoadPerson");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("linkManId", personId);
		params.put("linkManType", "3");
		String docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		description = XmlHelper.getElement(docXml3, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		results = XmlHelper.getElement(docXml3, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml3", docXml3);
		}


		return target;
	}

	@RequestMapping(value = "/receive1")
	public String receive1(HttpSession session, HttpServletRequest request, HttpServletResponse response,
			@RequestParam String workId, Model model) {
		logger.debug("-----enter dealfile receive1-----");
		String target = "/dealfile/docReceive_process";
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String userKey = session.getAttribute("userKey").toString();

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getReceiveDocument");
		params.put("workId", workId);
		params.put("userId", userId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("workId", workId);
		}

		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "workFlow_getWorkInfoByworkId");
		params2.put("domain", domainId);
		params2.put("workId", workId);
		params2.put("userId", userId);
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		description = XmlHelper.getElement(docXml2, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey);
		}
		
		String results2 = XmlHelper.getElement(docXml2, "//result");
		if (results2 != null && "1".equals(results2)) {
			model.addAttribute("docXml2", docXml2);
		}
		
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String personId = ServletRequestUtils.getStringParameter(request, "personId", "");
		if("".equals(personId) || personId == null ){
			personId = XmlHelper.getElement(docXml2, "//paramList/wf_curemployee_id");
		}
		if("".equals(personId) || personId == null ){
			personId = userId;
		}
		params = new HashMap<String, String>();
		params.put("cmd", "LoadPerson");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("linkManId", personId);
		params.put("linkManType", "3");
		String docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		description = XmlHelper.getElement(docXml3, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml3 = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		results = XmlHelper.getElement(docXml3, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml3", docXml3);
		}

		return target;
	}
	
	/**
	 * 车辆管理文件办理
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/voitureProcess")
	public String voitureProcess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-------车辆管理文件办理开始------->>");
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String workId = ServletRequestUtils.getStringParameter(request, "workId", "");
		String userKey = session.getAttribute("userKey").toString();
		//车辆表单信息
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getVoitureModInfo");
		params.put("domain", domainId);
		params.put("orgIdString", orgIdString);
		params.put("workId", workId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			logger.debug("--------docXml-------->>"+docXml);
		}
		//流程信息
		Map<String, String> workInfoParams = new HashMap<String, String>();
		workInfoParams.put("cmd", "workFlow_getWorkInfoByworkId");
		workInfoParams.put("domain", domainId);
		workInfoParams.put("workId", workId);
		workInfoParams.put("userId", userId);
		workInfoParams.put("orgId", orgId);
		workInfoParams.put("orgIdString", orgIdString);
		String workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		description = XmlHelper.getElement(workInfoDocXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		}
		
		String workInfoResults = XmlHelper.getElement(workInfoDocXml, "//result");
		if (workInfoResults != null && "1".equals(workInfoResults)) {
			model.addAttribute("workInfoDocXml", workInfoDocXml);
			logger.debug("--------workInfoDocXml-------->>"+workInfoDocXml);
		}
		logger.debug("-------车辆管理文件办理结束------->>");
		return "/dealfile/dealfile_voiture_process";
	}
	
	/**
	 * 领用出库领用退库文件办理
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getOutStockResult")
	public String getOutStockResult(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-------领用出库领用退库文件办理开始------->>");
		String userId = session.getAttribute("userId").toString();
		logger.debug("----userId---->>"+userId);
		String domainId = session.getAttribute("domainId").toString();
		logger.debug("----domainId---->>"+domainId);
		String orgId = session.getAttribute("orgId").toString();
		logger.debug("----orgId---->>"+orgId);
		String orgIdString = session.getAttribute("orgIdString").toString();
		logger.debug("----orgIdString---->>"+orgIdString);
		String workId = ServletRequestUtils.getStringParameter(request, "workId", "");
		logger.debug("----workId---->>"+workId);
		String userKey = session.getAttribute("userKey").toString();
		//出库/领用退库
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getOutStockResult");
		params.put("domain", domainId);
		params.put("workId", workId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("----docXml---->>"+docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		//流程信息
		Map<String, String> workInfoParams = new HashMap<String, String>();
		workInfoParams.put("cmd", "workFlow_getWorkInfoByworkId");
		workInfoParams.put("domain", domainId);
		workInfoParams.put("workId", workId);
		workInfoParams.put("userId", userId);
		workInfoParams.put("orgId", orgId);
		workInfoParams.put("orgIdString", orgIdString);
		String workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		description = XmlHelper.getElement(workInfoDocXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		}
		
		logger.debug("----workInfoDocXml---->>"+workInfoDocXml);
		String workInfoResults = XmlHelper.getElement(workInfoDocXml, "//result");
		if (workInfoResults != null && "1".equals(workInfoResults)) {
			model.addAttribute("workInfoDocXml", workInfoDocXml);
		}
		logger.debug("----出库退库结束---->>");
		return "/dealfile/dealfile_outstock_process";
	}
	
	/**
	 * 采购进货物品退货文件办理
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getIntoStockResult")
	public String getIntoStockResult(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("----进货退货开始---->>");
		String userId = session.getAttribute("userId").toString();
		logger.debug("----userId---->>"+userId);
		String domainId = session.getAttribute("domainId").toString();
		logger.debug("----domainId---->>"+domainId);
		String orgId = session.getAttribute("orgId").toString();
		logger.debug("----orgId---->>"+orgId);
		String orgIdString = session.getAttribute("orgIdString").toString();
		logger.debug("----orgIdString---->>"+orgIdString);
		String workId = ServletRequestUtils.getStringParameter(request, "workId", "");
		logger.debug("----workId---->>"+workId);
		String userKey = session.getAttribute("userKey").toString();
		//出库/领用退库
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getIntoStockResult");
		params.put("domain", domainId);
		params.put("workId", workId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("----docXml---->>"+docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		//流程信息
		Map<String, String> workInfoParams = new HashMap<String, String>();
		workInfoParams.put("cmd", "workFlow_getWorkInfoByworkId");
		workInfoParams.put("domain", domainId);
		workInfoParams.put("workId", workId);
		workInfoParams.put("userId", userId);
		workInfoParams.put("orgId", orgId);
		workInfoParams.put("orgIdString", orgIdString);
		String workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		description = XmlHelper.getElement(workInfoDocXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			workInfoDocXml = WebServiceUtils.getWebServiceData(workInfoParams,request,userKey);
		}
		
		String workInfoResults = XmlHelper.getElement(workInfoDocXml, "//result");
		logger.debug("----workInfoDocXml---->>"+workInfoDocXml);
		if (workInfoResults != null && "1".equals(workInfoResults)) {
			model.addAttribute("workInfoDocXml", workInfoDocXml);
		}
		logger.debug("----进货退货结束---->>");
		return "/dealfile/dealfile_intostock_process";
	}
}
