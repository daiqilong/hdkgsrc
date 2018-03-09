package com.whir.evo.weixin.actionsupport;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.whir.component.util.StringUtils;
import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.XmlHelper;

/**
 * 信息管理控制层
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/information")
public class InformationAction {
	private final static Log LOGGER = LogFactory.getLog(InformationAction.class);

	/**
	 * 全部信息列表
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/infoList")
	public String infoList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------全部信息列表开始---------");
		String target = "";
		String homePage = ServletRequestUtils.getStringParameter(request, "homePage", "");
		//从主页应用打开
		if (homePage.equals("1")) {
			target = "/homepage/homepage_information_allinfo";
		}else {
			target = "/information/information_allinfo";
		}
		String userId = session.getAttribute("userId").toString();
		String userKey = session.getAttribute("userKey").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "");
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "全部信息");
		String channelType = ServletRequestUtils.getStringParameter(request, "channelType", "0"); // 查询条件
		String userDefine = ServletRequestUtils.getStringParameter(request, "userDefine", ""); // 查询条件
		String title = ServletRequestUtils.getStringParameter(request, "title", ""); // 查询条件
		String userChannelId = ServletRequestUtils.getStringParameter(request, "userChannelId", ""); // 查询条件
		String pagerOffset = ServletRequestUtils.getStringParameter(request, "pagerOffset", "0"); // 查询条件
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_getMobileList");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("type", "all");
		params.put("userDefine", userDefine);
		params.put("informationId", "");
		params.put("userChannelName", "");
		params.put("channelType", channelType);
		params.put("search", "1");
		params.put("depart", "");
		params.put("searchDate", "");
		params.put("subtitle", "");
		params.put("searchBeginDate", "");
		params.put("searchEndDate", "");
		params.put("searchIssuerName", "");
		params.put("title", title);
		params.put("informationKey", "");
		params.put("append", "");
		params.put("searchChannel", channelId);
		params.put("userChannelId", userChannelId);//用户自定义频道id
		params.put("pager_offset", pagerOffset);
		params.put("pageSize", "15");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}

		model.addAttribute("channelId", channelId);
		model.addAttribute("channelName", channelName);
		model.addAttribute("title", title);
		model.addAttribute("channelType", channelType);
		model.addAttribute("userDefine", userDefine);
		model.addAttribute("userChannelId", userChannelId);

		String results = XmlHelper.getElement(docXml, "//result");
		String recordCount = XmlHelper.getElement(docXml, "//recordCount");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			if ((Integer.parseInt(pagerOffset) + 15) < Integer.parseInt(recordCount)) {
				model.addAttribute("nomore", "true");
				model.addAttribute("pagerOffset", Integer.parseInt(pagerOffset) + 15);
			}
		}
		
		//获取自定义信息频道
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getAllUserChannel");
		params1.put("domain", domainId);
		params1.put("userId", userId);
		params1.put("orgId", orgId);
		params1.put("orgIdString", orgIdString);
		String docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey);
		description = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey);
		}
		
		String results1 = XmlHelper.getElement(docXml1, "//result");
		if (results1 != null && "1".equals(results1)) {
			model.addAttribute("docXml1", docXml1);
		}
		
		LOGGER.debug("---------全部信息列表结束---------");
		return target;
	}

	/**
	 * 栏目页面跳转方法
	 * 
	 * @param request
	 * @param response
	 * @param sessoin
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/channelList")
	public String channelList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------指定栏目列表开始---------");
		String userId = session.getAttribute("userId").toString();
		// 当channelId为"0"时，则取信息的一级栏目
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		model.addAttribute("channelId", channelId);
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
		LOGGER.debug("channelName---->" + channelName);
		String channelNeedCheckup = ServletRequestUtils.getStringParameter(request, "channelNeedCheckup", "");
		model.addAttribute("channelName", channelName);
		model.addAttribute("channelId", channelId);
		model.addAttribute("channelNeedCheckup", channelNeedCheckup);
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getChannelByParentChannel");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("channelId", channelId);
		params.put("channelType", "0");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}

		String results = XmlHelper.getElement(docXml, "//result");
		String recordCount = XmlHelper.getElement(docXml, "//recordCount");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("recordCount", recordCount);
		}
		LOGGER.debug("---------指定栏目列表结束---------");
		return "/information/information_channellist";
	}

	/**
	 * 信息栏目--》栏目列表--》查询到的信息列表 查询指定栏目下的信息
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/searchInfoList")
	public String searchInfoList(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		LOGGER.debug("---------查询指定栏目下列表开始---------");
		String userId = session.getAttribute("userId").toString();
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
		String channelNeedCheckup = ServletRequestUtils.getStringParameter(request, "channelNeedCheckup", "");
		String title = ServletRequestUtils.getStringParameter(request, "title", "");
		String pagerOffset = ServletRequestUtils.getStringParameter(request, "pagerOffset", "0"); // 查询条件
		String userKey = session.getAttribute("userKey").toString();
		model.addAttribute("channelId", channelId);
		model.addAttribute("channelName", channelName);
		model.addAttribute("title", title);

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_getMobileList");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("type", "all");
		params.put("userDefine", "");
		params.put("informationId", "");
		params.put("userChannelName", "");
		params.put("channelType", "");
		params.put("search", "1");
		params.put("depart", "");
		params.put("searchDate", "");
		params.put("subtitle", "");
		params.put("searchBeginDate", "");
		params.put("searchEndDate", "");
		params.put("searchIssuerName", "");
		params.put("title", title);
		params.put("informationKey", "");
		params.put("append", "");
		params.put("searchChannel", channelId);
		params.put("pager_offset", pagerOffset);
		params.put("pageSize", "15");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}

		String inforesults = XmlHelper.getElement(docXml, "//result");
		String inforecordCount = XmlHelper.getElement(docXml, "//recordCount");
		if (inforesults != null && "1".equals(inforesults)) {
			model.addAttribute("docXml", docXml);
			if ((Integer.parseInt(pagerOffset) + 15) < Integer.parseInt(inforecordCount)) {
				model.addAttribute("nomore", "true");
				model.addAttribute("pagerOffset", Integer.parseInt(pagerOffset) + 15);
			}
		}
		model.addAttribute("channelNeedCheckup", channelNeedCheckup);
		LOGGER.debug("---------查询指定栏目下列表结束---------");
		return "/information/information_searchinfo";
	}

	/**
	 * 分页查询所有一级栏目列表
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/allChannelList")
	public String allChannelList(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		LOGGER.debug("---------分页一级栏目列表开始---------");
		String userId = session.getAttribute("userId").toString();
		// 当channelId为"0"时，则取信息的一级栏目
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
		model.addAttribute("channelId", channelId);
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAllInformationChannel");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("userDefine", "");
		params.put("pager_offset", "0");
		params.put("channelId", channelId);
		params.put("channelType", "0");
		params.put("searchChannelName", channelName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}

		String results = XmlHelper.getElement(docXml, "//result");
		//	
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			String recordCount = XmlHelper.getElement(docXml, "//recordCount");

			model.addAttribute("recordCount", recordCount);
		}
		model.addAttribute("channelName", channelName);
		LOGGER.debug("---------分页一级栏目列表结束---------");
		return "/information/information_allchannellist";
	}

	/**
	 * 查询信息详情
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/infoDetail")
	public String infoDetail(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		String t =request.getParameter("t");
		if(t==null || "".equals(t)){
			return null;
		}
		LOGGER.debug("---------查询信息详情开始---------");
		String userName = session.getAttribute("userName").toString();
		String orgName = session.getAttribute("orgName").toString();
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		String infoId = ServletRequestUtils.getStringParameter(request, "infoId", "");
		if (StringUtils.isEmpty(infoId)) {
			LOGGER.error("---获取信息id为空，无法查询指定信息详情！---");
			return null;
		}
		String informationType = ServletRequestUtils.getStringParameter(request, "informationType", "");
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_loadMobileInformation");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("userName", userName);
		params.put("orgName", orgName);
		params.put("informationId", infoId);
		params.put("userChannelName", "信息管理");
		params.put("informationType", informationType);
		params.put("channelType ", "0");
		LOGGER.debug("信息类型------>" + informationType);

		String docXml1 = WebServiceUtils.getWebServiceData(params,request,userKey);
		String docXml=docXml1.replace("&", "&amp;");
		
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");

		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("channelId", channelId);
			model.addAttribute("infoId", infoId);
			String informationTitle = XmlHelper.getElement(docXml, "//informationTitle");
			model.addAttribute("informationTitle", informationTitle);
			String informationCanRemark = XmlHelper.getElement(docXml, "//informationCanRemark");
			model.addAttribute("informationCanRemark", informationCanRemark);
		}
		LOGGER.debug("---------查询信息详情结束---------");
		return "/information/information_view";
	}

	/**
	 * 打开选择栏目一级列表
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/selectChannelList")
	public String selectChannelList(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		LOGGER.debug("---------打开选择栏目一级列表begin---------");
		String userId = session.getAttribute("userId").toString();
		// 当channelId为"0"时，则取信息的一级栏目
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		model.addAttribute("channelId", channelId);
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getChannelByParentChannel");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("channelId", channelId);
		params.put("channelType", "0");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		String recordCount = XmlHelper.getElement(docXml, "//recordCount");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
			String channelNeedCheckup = ServletRequestUtils.getStringParameter(request, "channelNeedCheckup", "");
			model.addAttribute("channelName", channelName);
			model.addAttribute("channelId", channelId);
			model.addAttribute("channelNeedCheckup", channelNeedCheckup);
			model.addAttribute("recordCount", recordCount);
		}
		LOGGER.debug("---------打开选择栏目一级列表end---------");
		return "/information/information_select_channel";
	}

	/**
	 * 获取所有能发布信息的栏目
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/canIssueChannel")
	public String canIssueChannel(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		LOGGER.debug("---------查询获取所有能发布信息的栏目开始---------");
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String searchChannelName = ServletRequestUtils.getStringParameter(request, "searchChannelName", "");
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		model.addAttribute("channelId", channelId);
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "Information_getCanIssueChannel");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("containFlow", "0");
		params.put("searchChannelName", searchChannelName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		String recordCount = XmlHelper.getElement(docXml, "//infomationChannel");

		// if (results != null && "1".equals(results)) {
		model.addAttribute("docXml", docXml);
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
		String channelNeedCheckup = ServletRequestUtils.getStringParameter(request, "channelNeedCheckup", "");
		model.addAttribute("channelName", channelName);
		model.addAttribute("channelId", channelId);
		model.addAttribute("channelNeedCheckup", channelNeedCheckup);
		if (recordCount != null) {
			model.addAttribute("recordCount", "1");
		} else {
			model.addAttribute("recordCount", "0");
		}
		// }

		LOGGER.debug("---------查询获取所有能发布信息的栏目结束---------" + docXml);
		return "/information/information_canissue_channel";
	}

	/**
	 * 进入信息添加页面
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/create")
	public String create(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------进入添加页面开始---------");
		return "/information/information_create";
	}

	/**
	 * 保存信息
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/save")
	public String save(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------保存信息开始---------");
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgName = session.getAttribute("orgName").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		String channelName = ServletRequestUtils.getStringParameter(request, "channelName", "");
		String popOpen = ServletRequestUtils.getStringParameter(request, "popOpen", "");
		String displayTitle = ServletRequestUtils.getStringParameter(request, "displayTitle", "");
		String titleColor = ServletRequestUtils.getStringParameter(request, "titleColor", "");
		String userKey = session.getAttribute("userKey").toString();
		model.addAttribute("channelId", channelId);
		model.addAttribute("channelName", channelName);
		model.addAttribute("popOpen", popOpen);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "saveInformation");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("username", userName);
		params.put("orgId", orgId);
		params.put("orgName", orgName);
		params.put("orgIdString", orgIdString);
		params.put("infoTitle ", ServletRequestUtils.getStringParameter(request, "title", ""));
		params.put("infoContent", ServletRequestUtils.getStringParameter(request, "content", ""));
		params.put("channelId", channelId);
		params.put("displayTitle", displayTitle);
		params.put("titleColor", titleColor);
		// 附件名
		String[] fileNames = ServletRequestUtils.getStringParameters(request, "fileName");
		LOGGER.debug("数组fileNames--->" + Arrays.toString(fileNames));
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if (fileNames != null) {
			String saveName = "";
			String realName = "";
			Map<String, String> childs = null;
			String decodeName = "";
			for (int i = 0, leagth = fileNames.length; i < leagth; i++) {
				if (StringUtils.isEmpty(fileNames[i])) {
					continue;
				}
				saveName = fileNames[i].split("\\|")[0].substring(fileNames[i].split("\\|")[0].lastIndexOf("\\") + 1);
				realName = fileNames[i].split("\\|")[1];
				try {
					decodeName = URLDecoder.decode(saveName, "utf-8");
					childs = new HashMap<String, String>();
					childs.put("f_name", decodeName);
					childs.put("f_body", realName);
					list.add(childs);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				params.put("pics", list);
			}
		}
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String data = XmlHelper.parseXmlToJson(docXml);
			LOGGER.debug("data=[" + data + "]");
			XmlHelper.printResult(response, "success", data);
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		LOGGER.debug("---------保存信息结束---------");
		return null;
	}

	/**
	 * 信息管理文件办理
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/process")
	public String process(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String workId = ServletRequestUtils.getStringParameter(request, "workId", "");
		String userKey = session.getAttribute("userKey").toString();
		// 信息表单信息
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_loadMobileByWorkId");
		params.put("domain", domainId);
		params.put("workId", workId);
		String docXml1 = WebServiceUtils.getWebServiceData(params,request,userKey);
		String docXml=docXml1.replace("&", "&amp;");
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		model.addAttribute("docXml", docXml);
		// 流程信息
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
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
		}
		String personId = ServletRequestUtils.getStringParameter(request, "personId", "");
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
		
		String results = XmlHelper.getElement(docXml3, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml3", docXml3);
		}
		return "/information/information_process";
	}
	
	/**
	 * 我的发布信息列表展示
	 * 
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/myInfoList")
	public String myInfoList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------我的发布信息列表展示开始---------");
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String title = ServletRequestUtils.getStringParameter(request, "title", ""); // 查询条件
		String pagerOffset = ServletRequestUtils.getStringParameter(request, "pagerOffset", "0"); // 查询条件
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_getMobileList");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("type", "all");
		params.put("userDefine", "");
		params.put("informationId", "");
		params.put("userChannelName", "");
		params.put("channelType", "");
		params.put("search", "1");
		params.put("depart", "");
		params.put("searchDate", "");
		params.put("subtitle", "");
		params.put("searchBeginDate", "");
		params.put("searchEndDate", "");
		params.put("searchIssuerName", userName);
		params.put("title", title);
		params.put("informationKey", "");
		params.put("append", "");
		params.put("searchChannel","");
		params.put("userChannelId","");
		params.put("pager_offset", pagerOffset);
		params.put("pageSize", "15");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		model.addAttribute("title", title);

		String results = XmlHelper.getElement(docXml, "//result");
		String recordCount = XmlHelper.getElement(docXml, "//recordCount");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			if ((Integer.parseInt(pagerOffset) + 15) < Integer.parseInt(recordCount)) {
				model.addAttribute("nomore", "true");
				model.addAttribute("pagerOffset", Integer.parseInt(pagerOffset) + 15);
			}
		}
		LOGGER.debug("---------我的发布信息列表展示结束---------");
		return "/information/myinfo_list";
	}
	
	/**
	 * 跳转到我的发布编辑页面
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/toEdit")
	public String toEdit(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		LOGGER.debug("---------跳转到我的发布编辑页面开始---------");
		String userName = session.getAttribute("userName").toString();
		String orgName = session.getAttribute("orgName").toString();
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgId = session.getAttribute("orgId").toString();
		String infoId = ServletRequestUtils.getStringParameter(request, "infoId", "");
		if (StringUtils.isEmpty(infoId)) {
			LOGGER.error("---获取信息id为空，无法查询指定信息详情！---");
			return null;
		}
		String userKey = session.getAttribute("userKey").toString();
		String channelId = ServletRequestUtils.getStringParameter(request, "channelId", "0");
		String informationType = ServletRequestUtils.getStringParameter(request, "informationType", "");
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "information_loadMobileInformation");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		params.put("userName", userName);
		params.put("orgName", orgName);
		params.put("informationId", infoId);
		params.put("userChannelName", "信息管理");
		params.put("informationType", informationType);
		params.put("channelType ", "0");
		LOGGER.debug("信息类型------>" + informationType);

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
			model.addAttribute("infoId", infoId);
			model.addAttribute("channelId", channelId);
			String informationTitle = XmlHelper.getElement(docXml, "//informationTitle");
			model.addAttribute("informationTitle", informationTitle);
			String informationCanRemark = XmlHelper.getElement(docXml, "//informationCanRemark");
			model.addAttribute("informationCanRemark", informationCanRemark);
		}
		LOGGER.debug("---------跳转到我的发布编辑页面结束---------");
		return "/information/edit_info";
	}
}
