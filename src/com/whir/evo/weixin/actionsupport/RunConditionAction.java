package com.whir.evo.weixin.actionsupport;

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
 * 天气预报
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/runCondition")
public class RunConditionAction {
	private final Log logger = LogFactory.getLog(RunConditionAction.class);

	/**
	 * 打开当日运行页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/runConditionInfo")
	public String runConditionInfo(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		return  "/rd/hdkg-today-work";
	}
	
	
	/**
	 * 获取管制信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getControlInfoData")
	public String getControlInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		System.out.println("userKey===:"+userKey);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getControlInfoData");
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
	 * 获取气象信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getWeatherInfoData")
	public String getWeatherInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getWeatherInfoData");
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
	 * 获取停机信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getHaltInfoData")
	public String getHaltInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getHaltInfoData");
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
	 * 获取干扰信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getDistractInfoData")
	public String getDistractInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getDistractInfoData");
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
	 * 获取跑道信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getTrackInfoData")
	public String getTrackInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getTrackInfoData");
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
