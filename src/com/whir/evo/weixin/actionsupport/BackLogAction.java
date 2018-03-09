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
 * 待办信息查询
 * 
 * @author pa
 * 
 */
@Controller
@RequestMapping("/backLog")
public class BackLogAction {
	private final Log logger = LogFactory.getLog(BackLogAction.class);

	/**
	 * 打开OA待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/oaBackLogPage")
	public String oaBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getBacklogDocRecordCount");
		params1.put("userId", "wangsh");
		String docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml1);
		String description1 = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results1 = XmlHelper.getElement(docXml1, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml1, "//recordCount1"));
		if (results1 != null && "1".equals(results1)) {
			 model.addAttribute("recordCount1", XmlHelper.getElement(docXml1, "//recordCount1"));
		}
		
		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "getReadDocRecordCount");
		params2.put("userId", "wangsh");
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml2===:"+docXml2);
		String description2 = XmlHelper.getElement(docXml2, "//description");
		//令牌超期
		if("TokenError".equals(description2)){
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results2 = XmlHelper.getElement(docXml2, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml2, "//recordCount2"));
		if (results2 != null && "1".equals(results2)) {
			 model.addAttribute("recordCount2", XmlHelper.getElement(docXml2, "//recordCount2"));
		}
		
		
		Map<String, String> params3 = new HashMap<String, String>();
		params3.put("cmd", "getAdminAffairRecordCount");
		params3.put("userId", "wangsh");
		String docXml3 = WebServiceUtils.getWebServiceData(params3,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml3===:"+docXml3);
		String description3 = XmlHelper.getElement(docXml3, "//description");
		//令牌超期
		if("TokenError".equals(description3)){
			session.setAttribute("OaToken","");
			docXml3 = WebServiceUtils.getWebServiceData(params3,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results3 = XmlHelper.getElement(docXml3, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml3, "//recordCount3"));
		if (results3 != null && "1".equals(results3)) {
			 model.addAttribute("recordCount3", XmlHelper.getElement(docXml3, "//recordCount3"));
		}
		
		Map<String, String> params4 = new HashMap<String, String>();
		params4.put("cmd", "getEntrustRecordCount");
		params4.put("userId", "wangsh");
		String docXml4 = WebServiceUtils.getWebServiceData(params4,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml4===:"+docXml4);
		String description4 = XmlHelper.getElement(docXml4, "//description");
		//令牌超期
		if("TokenError".equals(description4)){
			session.setAttribute("OaToken","");
			docXml4 = WebServiceUtils.getWebServiceData(params4,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results4 = XmlHelper.getElement(docXml4, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml4, "//recordCount4"));
		if (results4 != null && "1".equals(results4)) {
			 model.addAttribute("recordCount4", XmlHelper.getElement(docXml4, "//recordCount4"));
		}
		return  "/rd/oa-backlog";
	}
	
	/**
	 * 打开网报待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/wbBackLogPage")
	public String wbBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getWbBackLogRecordCount");
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
		return  "/rd/wb-backlog";
	}
	/**
	 * 打开合同待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/htBackLogPage")
	public String htBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getHtBackLogRecordCount");
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
		return  "/rd/ht-backlog";
	}
	/**
	 * 打开资产待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/zcBackLogPage")
	public String zcBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params1 = new HashMap<String, String>();
		params1.put("cmd", "getZcDisPosalRecordCount");
		params1.put("userId", "wangl");
		String docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml===:"+docXml1);
		String description1 = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description1)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceData(params1,request,userKey).replaceAll("&", "&amp;");
		}
		String results1 = XmlHelper.getElement(docXml1, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml1, "//recordCount1"));
		if (results1 != null && "1".equals(results1)) {
			 model.addAttribute("recordCount1", XmlHelper.getElement(docXml1, "//recordCount1"));
		}
		
		Map<String, String> params2 = new HashMap<String, String>();
		params2.put("cmd", "getZcPurchaseRecordCount");
		params2.put("userId", "wangl");
		String docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml2===:"+docXml2);
		String description2 = XmlHelper.getElement(docXml2, "//description");
		//令牌超期
		if("TokenError".equals(description2)){
			session.setAttribute("OaToken","");
			docXml2 = WebServiceUtils.getWebServiceData(params2,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results2 = XmlHelper.getElement(docXml2, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml2, "//recordCount2"));
		if (results2 != null && "1".equals(results2)) {
			 model.addAttribute("recordCount2", XmlHelper.getElement(docXml2, "//recordCount2"));
		}
		
		
		Map<String, String> params3 = new HashMap<String, String>();
		params3.put("cmd", "getZcAlterRecordCount");
		params3.put("userId", "wangl");
		String docXml3 = WebServiceUtils.getWebServiceData(params3,request,userKey).replaceAll("&", "&amp;");
		System.out.println("docXml3===:"+docXml3);
		String description3 = XmlHelper.getElement(docXml3, "//description");
		//令牌超期
		if("TokenError".equals(description3)){
			session.setAttribute("OaToken","");
			docXml3 = WebServiceUtils.getWebServiceData(params3,request,userKey).replaceAll("&", "&amp;");
		}
		
		String results3 = XmlHelper.getElement(docXml3, "//result");
		System.out.println("总记录数:::"+XmlHelper.getElement(docXml3, "//recordCount3"));
		if (results3 != null && "1".equals(results3)) {
			 model.addAttribute("recordCount3", XmlHelper.getElement(docXml3, "//recordCount3"));
		}
		
		return  "/rd/zc-backlog";
	}
	/**
	 * 打开运维待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/ywBackLogPage")
	public String ywBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getYwBackLogRecordCount");
		params.put("userId", "chenkun");
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
		return  "/rd/yw-backlog";
	}
	/**
	 * 打开培训待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/pxBackLogPage")
	public String pxBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getPxBackLogRecordCount");
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
		return  "/rd/px-backlog";
	}
	/**
	 * 打开安管待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/agBackLogPage")
	public String agBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAgBackLogRecordCount");
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
		return  "/rd/px-backlog";
	}
	/**
	 * 打开基建待办页面
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param workStatus
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/jjBackLogPage")
	public String jjBackLogPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getJjBackLogRecordCount");
		params.put("userId", "wangwenjun");
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
		return  "/rd/jj-backlog";
	}
	/**
	 * 获取待办公文数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getBacklogDocData")
	public String getBacklogDocData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getBacklogDocData");
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
	 * 获取行政事务数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getAdminAffairData")
	public String getAdminAffairData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAdminAffairData");
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
	 * 获取委托事项数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getEntrustData")
	public String getEntrustData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getEntrustData");
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
	 * 获取待阅公文数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getReadDocData")
	public String getReadDocData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getReadDocData");
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
	 * 获取网报待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getWbBackLogData")
	public String getWbBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getWbBackLogData");
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
	 * 获取合同待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getHtBackLogData")
	public String getHtBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getHtBackLogData");
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
	 * 获取固定资产申请待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getZcDisPosalData")
	public String getZcDisPosalData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getZcDisPosalData");
		params.put("userId", "wangl");
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
	 * 获取固定资产申购待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getZcPurchaseData")
	public String getZcPurchaseData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getZcPurchaseData");
		params.put("userId", "wangl");
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
	 * 获取固定资产变动待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getZcAlterData")
	public String getZcAlterData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getZcAlterData");
		params.put("userId", "wangl");
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
	 * 获取运维待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getYwBackLogData")
	public String getYwBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getYwBackLogData");
		params.put("userId", "chenkun");
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
	 * 获取培训待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getPxBackLogData")
	public String getPxBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getPxBackLogData");
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
	 * 获取安管待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getAgBackLogData")
	public String getAgBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getAgBackLogData");
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
	 * 获取基建待办数据
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getJjBackLogData")
	public String getJjBackLogData(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String curPage = request.getParameter("curPage") == null ? "" : request.getParameter("curPage").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getJjBackLogData");
		params.put("userId", "wangwenjun");
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
