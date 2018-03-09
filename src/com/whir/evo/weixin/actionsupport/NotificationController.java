package com.whir.evo.weixin.actionsupport;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.XmlHelper;

@Controller
@RequestMapping("/notification")
public class NotificationController {
	@RequestMapping(value = "/oaNotificationPage")
	public String oaNotificationPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getOaNotificationRecordCount");
		params.put("userId", "wangsh");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			 model.addAttribute("recordCount", XmlHelper.getElement(docXml, "//recordCount"));
		}
		
		String menu = "/rd/oaNotificationPage";
		return menu;
	}
	
	@RequestMapping(value = "/agNotificationPage")
	public String agNotificationPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAgNotificationRecordCount");
		params.put("userId", "wangsh");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			 model.addAttribute("recordCount", XmlHelper.getElement(docXml, "//recordCount"));
		}
		
		String menu = "/rd/agNotificationPage";
		return menu;
	}
	
	@RequestMapping(value = "/pxNotificationPage")
	public String pxNotificationPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getPxNotificationRecordCount");
		params.put("userId", "wangsh");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			 model.addAttribute("recordCount", XmlHelper.getElement(docXml, "//recordCount"));
		}
		
		String menu = "/rd/pxNotificationPage";
		return menu;
	}
	
	@RequestMapping(value = "/ywNotificationPage")
	public String zcNotificationPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getYwNotificationRecordCount");
		params.put("userId", "wangsh");
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			 model.addAttribute("recordCount", XmlHelper.getElement(docXml, "//recordCount"));
		}
		
		String menu = "/rd/ywNotificationPage";
		return menu;
	}	
	
	
	/**
	 * 获取OA通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getOaNotificationData")
	public String getOaNotificationData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getOaNotificationData");
		params.put("userId", "wangsh");
		params.put("curPage", curPage);
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
	 * 获取安管通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getAgNotificationData")
	public String getAgNotificationData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAgNotificationData");
		params.put("userId", "wangsh");
		params.put("curPage", curPage);
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
	 * 获取培训通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getPxNotificationData")
	public String getPxNotificationData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getPxNotificationData");
		params.put("userId", "wangsh");
		params.put("curPage", curPage);
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
	 * 获取运维通知数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getYwNotificationData")
	public String getYwNotificationData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getYwNotificationData");
		params.put("userId", "wangsh");
		params.put("curPage", curPage);
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
