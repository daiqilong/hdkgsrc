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
@RequestMapping("/flightLineSearch")
public class FlightLineSearchController {
	@RequestMapping(value = "/flightLineSearchPage")
	public String flightLineSearchPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getFlightLineSearchRecordCount");
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
		
		String menu = "/rd/hdkg-line-search";
		return menu;
	}
	
	
	/**
	 * 获取航班信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getFlightLineSearchData")
	public String getFlightLineSearchData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String queryDate = request.getParameter("queryDate") == null ? "" : request.getParameter("queryDate").toString();
		String flno = request.getParameter("flno") == null ? "" : request.getParameter("flno").toString();
		String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getFlightLineSearchData");
		params.put("curPage", curPage);
		params.put("queryDate", queryDate);
		params.put("flno", flno);
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
