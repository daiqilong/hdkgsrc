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
 * 工资查询
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/salaryQuery")
public class SalaryQueryAction {
	private final Log logger = LogFactory.getLog(SalaryQueryAction.class);

	/**
	 * 打开工资查询页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/salaryQueryPage")
	public String salaryQueryPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		return  "/rd/hdkg-saler";
	}
	
	
	/**
	 * 获取工资详细信息数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getSalaryQueryData")
	public String getSalaryQueryData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String queryDate = request.getParameter("queryDate") == null ? "" : request.getParameter("queryDate").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getSalaryQueryData");
		params.put("userId", "19783");
		params.put("queryDate", queryDate);
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
