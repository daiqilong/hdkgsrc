package com.whir.evo.weixin.actionsupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.XmlHelper;

/**
 * 领导值班
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/leaderDuty")
public class LeaderDutyAction {
	private final Log logger = LogFactory.getLog(LeaderDutyAction.class);

	/**
	 * 值班页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/leaderDutyPage")
	public String leaderDutyPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
//		String psql = "select whir$posttab_id from whir$posttab where whir$posttab_class='100'";
//		Object[] obj = new WatchArrangeUtils().getArray(psql);
//		if(obj[0]!=null && !"".equals(obj[0])){
//			model.addAttribute("postId", obj[0].toString());;
//		}else{
//			model.addAttribute("postId", "");;
//		}
		//-------------------改版后开始-----------------------//
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        String nowDate = format.format(new Date());
        model.addAttribute("nowDate", nowDate);
        
        String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    System.out.println("userId::"+userId);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getCurrentLeaderDutyDate");
		//由于人员未同步只能使用固定值
		//params.put("userId",userId);
		params.put("userId", "54608");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			 model.addAttribute("dutyName", XmlHelper.getElement(docXml, "//dutyName"));
			 model.addAttribute("leaderDutyDate", XmlHelper.getElement(docXml, "//leaderDutyDate"));
			 model.addAttribute("centerLeaderDutyDate", XmlHelper.getElement(docXml, "//centerLeaderDutyDate"));
		}
		//-------------------改版后结束-----------------------//
		return  "/rd/hdkg-leader-duty";
	}
	/**
	 * 获取局领导值班日历数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getLeaderDutyCalender")
	public String getLeaderDutyCalender(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String queryDate = request.getParameter("queryDate") == null ? "" : request.getParameter("queryDate").toString();
		System.out.println("getLeaderDutyCalender:::queryDate===:"+queryDate);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getLeaderDutyCalender");
		//局领导岗位等级为100
		params.put("queryDate", queryDate);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 获取分局站/中心值班领导值班日历数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getCenterLeaderDutyCalender")
	public String getCenterLeaderDutyCalender(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String queryDate = request.getParameter("queryDate") == null ? "" : request.getParameter("queryDate").toString();
		String department = request.getParameter("department") == null ? "" : request.getParameter("department").toString();
		System.out.println("getCenterLeaderDutyCalender:::queryDate===:"+queryDate);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getCenterLeaderDutyCalender");
		params.put("queryDate", queryDate);
		params.put("department", department);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 新改版后局领导换班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/changeLeaderDuty")
	public String changeLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String userId = session.getAttribute("userId").toString();
		String pickerDate = request.getParameter("pickerDate") == null ? "" : request.getParameter("pickerDate").toString();
		String nowDutyDate = request.getParameter("nowDutyDate") == null ? "" : request.getParameter("nowDutyDate").toString();
		String pickerLingdao = request.getParameter("pickerLingdao") == null ? "" : request.getParameter("pickerLingdao").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "changeLeaderDuty");
		//params.put("userId", userId);
		params.put("userId", "54608");
		params.put("pickerDate", pickerDate);
		params.put("pickerLingdao", pickerLingdao);
		params.put("nowDutyDate", nowDutyDate);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 新改版后局领导替班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/relayLeaderDuty")
	public String relayLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String userId = session.getAttribute("userId").toString();
		String nowDutyDate = request.getParameter("nowDutyDate") == null ? "" : request.getParameter("nowDutyDate").toString();
		String pickerLingdao = request.getParameter("pickerLingdao") == null ? "" : request.getParameter("pickerLingdao").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "relayLeaderDuty");
		//params.put("userId", userId);
		params.put("userId", "54608");
		params.put("pickerLingdao", pickerLingdao);
		params.put("nowDutyDate", nowDutyDate);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 新改版后分局站/中心领导换班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/changeCenterLeaderDuty")
	public String changeCenterLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String userId = session.getAttribute("userId").toString();
		String pickerDate = request.getParameter("pickerDate") == null ? "" : request.getParameter("pickerDate").toString();
		String centerNowDutyDate = request.getParameter("centerNowDutyDate") == null ? "" : request.getParameter("centerNowDutyDate").toString();
		String pickerLingdao = request.getParameter("pickerLingdao") == null ? "" : request.getParameter("pickerLingdao").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "changeCenterLeaderDuty");
		//params.put("userId", userId);
		params.put("userId", "20989");
		params.put("pickerDate", pickerDate);
		params.put("pickerLingdao", pickerLingdao);
		params.put("centerNowDutyDate", centerNowDutyDate);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 新改版后分局站/中心领导替班
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/relayCenterLeaderDuty")
	public String relayCenterLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String userId = session.getAttribute("userId").toString();
		String centerNowDutyDate = request.getParameter("centerNowDutyDate") == null ? "" : request.getParameter("centerNowDutyDate").toString();
		String pickerLingdao = request.getParameter("pickerLingdao") == null ? "" : request.getParameter("pickerLingdao").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "relayCenterLeaderDuty");
		//params.put("userId", userId);
		params.put("userId", "20989");
		params.put("pickerLingdao", pickerLingdao);
		params.put("centerNowDutyDate", centerNowDutyDate);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	/**
	 * 获取分局站/中心值班当天的值班情况
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getChangeLeaderDuty")
	public String getCurrentDateCenterLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getCurrentDateCenterLeaderDuty");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 获取局领导值班数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getLeaderDutyData")
	public String getLeaderDutyData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getLeaderDutyData");
		//局领导岗位等级为100
		params.put("style", "100");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 获取中心领导值班数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getCenterLeaderDutyData")
	public String getCenterLeaderDutyData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getLeaderDutyData");
		//中心领导岗位等级10
		params.put("style", "10");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 局领导值班查询
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getLeaderDutyQuery")
	public String getLeaderDutyQuery(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String dutyDate = request.getParameter("dutyDate") == null ? "" : request.getParameter("dutyDate").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getLeaderDutyQuery");
		params.put("dutyDate", dutyDate);
		//中心领导岗位等级10
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	

	/**
	 * 中心领导值班查询
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getCenterLeaderDutyQuery")
	public String getCenterLeaderDutyQuery(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String dutyDate = request.getParameter("dutyDate") == null ? "" : request.getParameter("dutyDate").toString();
		String centerName = request.getParameter("centerName") == null ? "" : request.getParameter("centerName").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getCenterLeaderDutyQuery");
		params.put("dutyDate", dutyDate);
		params.put("centerName", centerName);
		//中心领导岗位等级10
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	//---------------------------------------改版前的方法开始---------------------------//
	/**
	 * 局领导换班数据查询
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getChangeLeaderDuty")
	public String getChangeLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getChangeLeaderDuty");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 中心领导换班数据查询
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getChangeCenterLeaderDuty")
	public String getChangeCenterLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String centerName = request.getParameter("centerName") == null ? "" : request.getParameter("centerName").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getChangeCenterLeaderDuty");
		params.put("centerName", centerName);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 局领导换班设置
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/setChangeLeaderDuty")
	public String setChangeLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String chestrs = request.getParameter("chestrs") == null ? "" : request.getParameter("chestrs").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "setChangeLeaderDuty");
		params.put("chestrs", chestrs);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 中心领导换班设置
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/setChangeCenterLeaderDuty")
	public String setChangeCenterLeaderDuty(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String chestrs = request.getParameter("chestrs") == null ? "" : request.getParameter("chestrs").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "setChangeCenterLeaderDuty");
		params.put("chestrs", chestrs);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
}
