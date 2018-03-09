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
@RequestMapping("/kgNew")
public class KgNewController {
	@RequestMapping(value = "/kgNewPage")
	public String kgNewPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		//String channelId = session.getAttribute("channelId").toString();
		//System.out.println("channelId:::"+channelId);
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getKgNewRecordCount");
		params.put("queryTitle", "");
		params.put("channelId", "6696");
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
		
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getPhotoImage");
		params1.put("channelId", "6696");
		String docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml1===:"+docXml1);
		String description1 = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results1 = XmlHelper.getElement(docXml1, "//result");
		if (results1 != null && "1".equals(results1)) {
			model.addAttribute("docXml1", docXml1);
		}
		String menu = "/rd/kgNewPage";
		return menu;
	}
	
	
	@RequestMapping(value = "/getKgNewRecordCount")
	public String getKgNewRecordCount(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String queryTitle = request.getParameter("queryTitle") == null ? "" : request.getParameter("queryTitle").toString();
		String channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getKgNewRecordCount");
		params.put("queryTitle", queryTitle);
		params.put("channelId", channelId);
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
	 * 获取空管新闻数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getKgNewInfoList")
	public String getKgNewInfoList(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String queryTitle = request.getParameter("queryTitle") == null ? "" : request.getParameter("queryTitle").toString();
		String channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId").toString();
		String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getKgNewInfoList");
		params.put("queryTitle", queryTitle);
		params.put("channelId", channelId);
		params.put("curPage", curPage);
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
	 * 获取空管新闻图片数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getPhotoImage")
	public String getPhotoImage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		String channelId = request.getParameter("channelId") == null ? "" : request.getParameter("channelId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getPhotoImage");
		params.put("channelId", channelId);
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
}
