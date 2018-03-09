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
@RequestMapping("/earlyWaringInfo")
public class EarlyWaringInfoAction {
	private final Log logger = LogFactory.getLog(EarlyWaringInfoAction.class);

	/**
	 * 天气预报
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/earlyWaringPage")
	public String earlyWaringPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		return  "/rd/hdkg-yujing";
	}
	
	
	/**
	 * ajax方式获取天气预报数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getEarlyWaringInfoData")
	public String getEarlyWaringInfoData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getEarlyWaringInfoData");
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
