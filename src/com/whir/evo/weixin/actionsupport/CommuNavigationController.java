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
@RequestMapping("/commuNavigation")
public class CommuNavigationController {
	@RequestMapping(value = "/commuNavigationPage")
	public String commuNavigationPage(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String menu = "/rd/commuNavigationPage";
		return menu;
	}
	
	/**
	 * 获取通导系统信息总个数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getSystemRecordCount")
	public String getSystemRecordCount(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String stationName = request.getParameter("station") == null ? "" : request.getParameter("station").toString();
	    String station="";
	    if("局机关".equals(stationName)){
	    	station = "101";
	    } else if("技保中心".equals(stationName)){
	    	station = "102";
	    } else if("网络中心".equals(stationName)){
	    	station = "103";
	    } else if("监控中心".equals(stationName)){
	    	station = "115";
	    } else if("维修中心".equals(stationName)){
	    	station = "116";
	    } else if("区管中心".equals(stationName)){
	    	station = "117";
	    } else if("终端中心".equals(stationName)){
	    	station = "118";
	    } else if("飞行中心".equals(stationName)){
	    	station = "119";
	    } else if("运管中心".equals(stationName)){
	    	station = "120";
	    } else if("气象中心".equals(stationName)){
	    	station = "121";
	    } else if("空管中心".equals(stationName)){
	    	station = "122";
	    } else if("山东".equals(stationName)){
	    	station = "104";
	    } else if("安徽".equals(stationName)){
	    	station = "105";
	    } else if("江苏".equals(stationName)){
	    	station = "106";
	    } else if("浙江".equals(stationName)){
	    	station = "107";
	    } else if("江西".equals(stationName)){
	    	station = "108";
	    } else if("福建".equals(stationName)){
	    	station = "109";
	    } else if("青岛".equals(stationName)){
	    	station = "110";
	    }else if("温州".equals(stationName)){
	    	station = "111";
	    } else if("宁波".equals(stationName)){
	    	station = "112";
	    } else if("厦门".equals(stationName)){
	    	station = "113";
	    } else if("华东以外空管局".equals(stationName)){
	    	station = "560";
	    } else if("中南空管局".equals(stationName)){
	    	station = "561";
	    } else if("华北空管局".equals(stationName)){
	    	station = "562";
	    } else if("其他".equals(stationName)){
	    	station = "200";
	    }
	    String majorName = request.getParameter("major") == null ? "" : request.getParameter("major").toString();
	    String major="";
	    if("地空通信".equals(majorName)){
	    	major = "501";
	    } else if("平面通信".equals(majorName)){
	    	major = "502";
	    } else if("导航".equals(majorName)){
	    	major = "503";
	    } else if("监视".equals(majorName)){
	    	major = "504";
	    } else if("动力".equals(majorName)){
	    	major = "505";
	    } else if("信息系统".equals(majorName)){
	    	major = "506";
	    } else if("气象".equals(majorName)){
	    	major = "507";
	    } else if("情报".equals(majorName)){
	    	major = "508";
	    } else if("其他".equals(majorName)){
	    	major = "509";
	    } else if("空管外部专业".equals(majorName)){
	    	major = "510";
	    }
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getSystemRecordCount");
		params.put("userKey", "wangsh");
		params.put("station", station);
		params.put("major", major);
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
	 * 获取通导系统信息
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getSystemListDate")
	public String getSystemListDate(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String pageSize = request.getParameter("pageSize") == null ? "" : request.getParameter("pageSize").toString();
	    String currentPage = request.getParameter("currentPage") == null ? "" : request.getParameter("currentPage").toString();
	    String stationName = request.getParameter("station") == null ? "" : request.getParameter("station").toString();
	    String station="";
	    if("局机关".equals(stationName)){
	    	station = "101";
	    } else if("技保中心".equals(stationName)){
	    	station = "102";
	    } else if("网络中心".equals(stationName)){
	    	station = "103";
	    } else if("监控中心".equals(stationName)){
	    	station = "115";
	    } else if("维修中心".equals(stationName)){
	    	station = "116";
	    } else if("区管中心".equals(stationName)){
	    	station = "117";
	    } else if("终端中心".equals(stationName)){
	    	station = "118";
	    } else if("飞行中心".equals(stationName)){
	    	station = "119";
	    } else if("运管中心".equals(stationName)){
	    	station = "120";
	    } else if("气象中心".equals(stationName)){
	    	station = "121";
	    } else if("空管中心".equals(stationName)){
	    	station = "122";
	    } else if("山东".equals(stationName)){
	    	station = "104";
	    } else if("安徽".equals(stationName)){
	    	station = "105";
	    } else if("江苏".equals(stationName)){
	    	station = "106";
	    } else if("浙江".equals(stationName)){
	    	station = "107";
	    } else if("江西".equals(stationName)){
	    	station = "108";
	    } else if("福建".equals(stationName)){
	    	station = "109";
	    } else if("青岛".equals(stationName)){
	    	station = "110";
	    }else if("温州".equals(stationName)){
	    	station = "111";
	    } else if("宁波".equals(stationName)){
	    	station = "112";
	    } else if("厦门".equals(stationName)){
	    	station = "113";
	    } else if("华东以外空管局".equals(stationName)){
	    	station = "560";
	    } else if("中南空管局".equals(stationName)){
	    	station = "561";
	    } else if("华北空管局".equals(stationName)){
	    	station = "562";
	    } else if("其他".equals(stationName)){
	    	station = "200";
	    }
	    String majorName = request.getParameter("major") == null ? "" : request.getParameter("major").toString();
	    String major="";
	    if("地空通信".equals(majorName)){
	    	major = "501";
	    } else if("平面通信".equals(majorName)){
	    	major = "502";
	    } else if("导航".equals(majorName)){
	    	major = "503";
	    } else if("监视".equals(majorName)){
	    	major = "504";
	    } else if("动力".equals(majorName)){
	    	major = "505";
	    } else if("信息系统".equals(majorName)){
	    	major = "506";
	    } else if("气象".equals(majorName)){
	    	major = "507";
	    } else if("情报".equals(majorName)){
	    	major = "508";
	    } else if("其他".equals(majorName)){
	    	major = "509";
	    } else if("空管外部专业".equals(majorName)){
	    	major = "510";
	    }
	    String systemCategory = request.getParameter("systemCategory") == null ? "" : request.getParameter("systemCategory").toString();
	    String systemRegion = request.getParameter("systemRegion") == null ? "" : request.getParameter("systemRegion").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getSystemListDate");
		params.put("userKey", "wangsh");
		params.put("pageSize", pageSize);
		params.put("currentPage", currentPage);
		params.put("station", station);
		params.put("major", major);
		params.put("systemCategory", systemCategory);
		params.put("systemRegion", systemRegion);
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
	 * 获取通导备件信息总个数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getRepairsInformationRecordCount")
	public String getRepairsInformationRecordCount(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String specificationType = request.getParameter("specificationType") == null ? "" : request.getParameter("specificationType").toString();
	    String spareName = request.getParameter("spareName") == null ? "" : request.getParameter("spareName").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getRepairsInformationRecordCount");
		params.put("userKey", "wangsh");
		params.put("specificationType", specificationType);
		params.put("spareName", spareName);
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
	 * 获取通导备件信息
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getRepairsInformationListDate")
	public String getRepairsInformationListDate(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String pageSize = request.getParameter("pageSize") == null ? "" : request.getParameter("pageSize").toString();
	    String currentPage = request.getParameter("currentPage") == null ? "" : request.getParameter("currentPage").toString();
	    String applyUnit = request.getParameter("applyUnit") == null ? "" : request.getParameter("applyUnit").toString();
	    String applyDepartment = request.getParameter("applyDepartment") == null ? "" : request.getParameter("applyDepartment").toString();
	    String major = request.getParameter("major") == null ? "" : request.getParameter("major").toString();
	    String systemCategory = request.getParameter("systemCategory") == null ? "" : request.getParameter("systemCategory").toString();
	    String system = request.getParameter("system") == null ? "" : request.getParameter("system").toString();
	    String spareName = request.getParameter("spareName") == null ? "" : request.getParameter("spareName").toString();
	    String specificationType = request.getParameter("specificationType") == null ? "" : request.getParameter("specificationType").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getRepairsInformationListDate");
		params.put("userKey", "wangsh");
		params.put("pageSize", pageSize);
		params.put("currentPage", currentPage);
		params.put("applyUnit", applyUnit);
		params.put("applyDepartment", applyDepartment);
		params.put("systemCategory", systemCategory);
		params.put("major", major);
		params.put("system", system);
		params.put("spareName", spareName);
		params.put("specificationType", specificationType);
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
	 * 获取通导停机信息总个数
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getHaltStatisticsRecordCount")
	public String getHaltStatisticsRecordCount(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
	    String applyUnitName = request.getParameter("applyUnit") == null ? "" : request.getParameter("applyUnit").toString();
	    String applyUnit="";
	    if("局机关".equals(applyUnitName)){
	    	applyUnit = "101";
	    } else if("技保中心".equals(applyUnitName)){
	    	applyUnit = "102";
	    } else if("网络中心".equals(applyUnitName)){
	    	applyUnit = "103";
	    } else if("监控中心".equals(applyUnitName)){
	    	applyUnit = "115";
	    } else if("维修中心".equals(applyUnitName)){
	    	applyUnit = "116";
	    } else if("区管中心".equals(applyUnitName)){
	    	applyUnit = "117";
	    } else if("终端中心".equals(applyUnitName)){
	    	applyUnit = "118";
	    } else if("飞行中心".equals(applyUnitName)){
	    	applyUnit = "119";
	    } else if("运管中心".equals(applyUnitName)){
	    	applyUnit = "120";
	    } else if("气象中心".equals(applyUnitName)){
	    	applyUnit = "121";
	    } else if("空管中心".equals(applyUnitName)){
	    	applyUnit = "122";
	    } else if("山东".equals(applyUnitName)){
	    	applyUnit = "104";
	    } else if("安徽".equals(applyUnitName)){
	    	applyUnit = "105";
	    } else if("江苏".equals(applyUnitName)){
	    	applyUnit = "106";
	    } else if("浙江".equals(applyUnitName)){
	    	applyUnit = "107";
	    } else if("江西".equals(applyUnitName)){
	    	applyUnit = "108";
	    } else if("福建".equals(applyUnitName)){
	    	applyUnit = "109";
	    } else if("青岛".equals(applyUnitName)){
	    	applyUnit = "110";
	    }else if("温州".equals(applyUnitName)){
	    	applyUnit = "111";
	    } else if("宁波".equals(applyUnitName)){
	    	applyUnit = "112";
	    } else if("厦门".equals(applyUnitName)){
	    	applyUnit = "113";
	    } else if("华东以外空管局".equals(applyUnitName)){
	    	applyUnit = "560";
	    } else if("中南空管局".equals(applyUnitName)){
	    	applyUnit = "561";
	    } else if("华北空管局".equals(applyUnitName)){
	    	applyUnit = "562";
	    } else if("其他".equals(applyUnitName)){
	    	applyUnit = "200";
	    }
	    String haltReasonName = request.getParameter("haltReason") == null ? "" : request.getParameter("haltReason").toString();
	    String haltReason="";
	    if("切换".equals(haltReasonName)){
	    	haltReason = "612";
	    } else if("维护".equals(haltReasonName)){
	    	haltReason = "602";
	    } else if("检修".equals(haltReasonName)){
	    	haltReason = "601";
	    } else if("更新改造".equals(haltReasonName)){
	    	haltReason = "605";
	    } else if("升级".equals(haltReasonName)){
	    	haltReason = "608";
	    } else if("飞行校验".equals(haltReasonName)){
	    	haltReason = "609";
	    } else if("设备搬迁".equals(haltReasonName)){
	    	haltReason = "606";
	    } else if("测试".equals(haltReasonName)){
	    	haltReason = "610";
	    } else if("数据发布".equals(haltReasonName)){
	    	haltReason = "607";
	    } else if("更换".equals(haltReasonName)){
	    	haltReason = "611";
	    } else if("割接".equals(haltReasonName)){
	    	haltReason = "604";
	    } else if("天气原因".equals(haltReasonName)){
	    	haltReason = "603";
	    } else if("运营商".equals(haltReasonName)){
	    	haltReason = "614";
	    } else if("市电".equals(haltReasonName)){
	    	haltReason = "615";
	    } else if("其他".equals(haltReasonName)){
	    	haltReason = "613";
	    }
	    String haltStartTimeStr = request.getParameter("haltStartTime") == null ? "" : request.getParameter("haltStartTime").toString();
	    String haltEndTimeStr = request.getParameter("haltEndTime") == null ? "" : request.getParameter("haltEndTime").toString();
	    String haltStartTime ="";
	    if(!"".equals(haltStartTimeStr)){
			String[] str = haltStartTimeStr.split("/");
			haltStartTime = str[0]+"-"+str[1]+"-"+str[2];
		}
	    String haltEndTime ="";
	    if(!"".equals(haltEndTimeStr)){
			String[] str = haltEndTimeStr.split("/");
			haltEndTime = str[0]+"-"+str[1]+"-"+str[2];
		}
	    Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getHaltStatisticsRecordCount");
		params.put("userKey", "wangsh");
		params.put("haltReason", haltReason);
		params.put("applyUnit", applyUnit);
		params.put("haltStartTime", haltStartTime);
		params.put("haltEndTime", haltEndTime);
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
	 * 获取通导停机信息
	 * 
	 * @param session
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getHaltStatisticsListDate")
	public String getHaltStatisticsListDate(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model model) {
		String userKey = session.getAttribute("userKey").toString();
	    String userId = session.getAttribute("userId").toString();
		
	    String pageSize = request.getParameter("pageSize") == null ? "" : request.getParameter("pageSize").toString();
	    String currentPage = request.getParameter("currentPage") == null ? "" : request.getParameter("currentPage").toString();
	    String applyDateStart = request.getParameter("applyDateStart") == null ? "" : request.getParameter("applyDateStart").toString();
	    String applyDateEnd = request.getParameter("applyDateEnd") == null ? "" : request.getParameter("applyDateEnd").toString();
	    String applicat = request.getParameter("applicat") == null ? "" : request.getParameter("applicat").toString();
	    String haltStartTime = request.getParameter("haltStartTime") == null ? "" : request.getParameter("haltStartTime").toString();
	    String haltEndTime = request.getParameter("haltEndTime") == null ? "" : request.getParameter("haltEndTime").toString();
	    String applyUnitName = request.getParameter("applyUnit") == null ? "" : request.getParameter("applyUnit").toString();
	    String applyUnit="";
	    if("局机关".equals(applyUnitName)){
	    	applyUnit = "101";
	    } else if("技保中心".equals(applyUnitName)){
	    	applyUnit = "102";
	    } else if("网络中心".equals(applyUnitName)){
	    	applyUnit = "103";
	    } else if("监控中心".equals(applyUnitName)){
	    	applyUnit = "115";
	    } else if("维修中心".equals(applyUnitName)){
	    	applyUnit = "116";
	    } else if("区管中心".equals(applyUnitName)){
	    	applyUnit = "117";
	    } else if("终端中心".equals(applyUnitName)){
	    	applyUnit = "118";
	    } else if("飞行中心".equals(applyUnitName)){
	    	applyUnit = "119";
	    } else if("运管中心".equals(applyUnitName)){
	    	applyUnit = "120";
	    } else if("气象中心".equals(applyUnitName)){
	    	applyUnit = "121";
	    } else if("空管中心".equals(applyUnitName)){
	    	applyUnit = "122";
	    } else if("山东".equals(applyUnitName)){
	    	applyUnit = "104";
	    } else if("安徽".equals(applyUnitName)){
	    	applyUnit = "105";
	    } else if("江苏".equals(applyUnitName)){
	    	applyUnit = "106";
	    } else if("浙江".equals(applyUnitName)){
	    	applyUnit = "107";
	    } else if("江西".equals(applyUnitName)){
	    	applyUnit = "108";
	    } else if("福建".equals(applyUnitName)){
	    	applyUnit = "109";
	    } else if("青岛".equals(applyUnitName)){
	    	applyUnit = "110";
	    }else if("温州".equals(applyUnitName)){
	    	applyUnit = "111";
	    } else if("宁波".equals(applyUnitName)){
	    	applyUnit = "112";
	    } else if("厦门".equals(applyUnitName)){
	    	applyUnit = "113";
	    } else if("华东以外空管局".equals(applyUnitName)){
	    	applyUnit = "560";
	    } else if("中南空管局".equals(applyUnitName)){
	    	applyUnit = "561";
	    } else if("华北空管局".equals(applyUnitName)){
	    	applyUnit = "562";
	    } else if("其他".equals(applyUnitName)){
	    	applyUnit = "200";
	    }
	    String haltReasonName = request.getParameter("haltReason") == null ? "" : request.getParameter("haltReason").toString();
	    String haltReason="";
	    if("切换".equals(haltReasonName)){
	    	haltReason = "612";
	    } else if("维护".equals(haltReasonName)){
	    	haltReason = "602";
	    } else if("检修".equals(haltReasonName)){
	    	haltReason = "601";
	    } else if("更新改造".equals(haltReasonName)){
	    	haltReason = "605";
	    } else if("升级".equals(haltReasonName)){
	    	haltReason = "608";
	    } else if("飞行校验".equals(haltReasonName)){
	    	haltReason = "609";
	    } else if("设备搬迁".equals(haltReasonName)){
	    	haltReason = "606";
	    } else if("测试".equals(haltReasonName)){
	    	haltReason = "610";
	    } else if("数据发布".equals(haltReasonName)){
	    	haltReason = "607";
	    } else if("更换".equals(haltReasonName)){
	    	haltReason = "611";
	    } else if("割接".equals(haltReasonName)){
	    	haltReason = "604";
	    } else if("天气原因".equals(haltReasonName)){
	    	haltReason = "603";
	    } else if("运营商".equals(haltReasonName)){
	    	haltReason = "614";
	    } else if("市电".equals(haltReasonName)){
	    	haltReason = "615";
	    } else if("其他".equals(haltReasonName)){
	    	haltReason = "613";
	    }
	    String applyDepartment = request.getParameter("applyDepartment") == null ? "" : request.getParameter("applyDepartment").toString();
	    String systemName = request.getParameter("systemName") == null ? "" : request.getParameter("systemName").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getHaltStatisticsListDate");
		params.put("userKey", "wangsh");
		params.put("pageSize", pageSize);
		params.put("currentPage", currentPage);
		params.put("applyDateStart", applyDateStart);
		params.put("applyDateEnd", applyDateEnd);
		params.put("applicat", applicat);
		params.put("haltStartTime", haltStartTime);
		params.put("haltEndTime", haltEndTime);
		params.put("haltReason", haltReason);
		params.put("applyUnit", applyUnit);
		params.put("applyDepartment", applyDepartment);
		params.put("systemName", systemName);
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
