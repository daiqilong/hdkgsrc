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
@RequestMapping("/controlRun")
public class ControlRunController {
	@RequestMapping(value = "/controlRunPage")
	public String controlRunPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String menu = "/rd/hdkg-control-run";
		return menu;
	}
	
	/**
	 * 获取虹桥起降数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getActualUpDownZSSSFlight")
	public String getActualUpDownZSSSFlight(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getActualUpDownZSSSFlight");
		String docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description1 = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	/**
	 * 获取浦东起降数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getActualUpDownZSPDFlight")
	public String getActualUpDownZSPDFlight(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getActualUpDownZSPDFlight");
		String docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description1 = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml, "//recordCount"));
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.parseXmlToJson(docXml);
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 获取实时飞行数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getRealTimeFlow")
	public String getRealTimeFlow(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getRealTimeFlow");
		String docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description1 = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.getElement(docXml, "//record");
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 获取班机延误统计
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getFlightDelay")
	public String getFlightDelay(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getFlightDelay");
		String docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description1 = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.getElement(docXml, "//record");
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
	
	
	/**
	 * 获取7日起降航班
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getSevenActualUpDownFlight")
	public String getSevenActualUpDownFlight(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getSevenActualUpDownFlight");
		String docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml);
		String description1 = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			String docData = XmlHelper.getElement(docXml, "//record");
			XmlHelper.printResult(response, "success", new String[] { docData});
		} else {
			XmlHelper.printResult(response, "fail", "[]");
		}
		return null;
	}
}
