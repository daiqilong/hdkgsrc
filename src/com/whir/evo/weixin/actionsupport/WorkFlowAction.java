package com.whir.evo.weixin.actionsupport;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.whir.component.security.crypto.EncryptUtil;
import com.whir.evo.weixin.bd.WeiXinBD;
import com.whir.evo.weixin.util.WebServiceUtils;
import com.whir.evo.weixin.util.XmlHelper;

@Controller
@RequestMapping("/workflow")
public class WorkFlowAction {
	private final Log logger = LogFactory.getLog(WorkFlowAction.class);

	private final static String G_SUCCESS = "success";
	private final static String G_FAILURE = "failure";

	/**
	 * 异步输出JSON
	 * 
	 * @param HttpServletResponse
	 *            response
	 * @param String
	 *            content
	 * @return String
	 */
	public void printResult(HttpServletResponse response, String content, String dataJson) {
		logger.debug("=====@printResult()=====B");
		response.setContentType("text/plain;charSet=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Pragma", "no-cache");
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Cache-Control", "no-store");
		response.setDateHeader("Expires", 0L);
		String result = "{result:'" + content + "', data:" + dataJson + "}";
		logger.debug("result:" + result);
		try {
			PrintWriter pw = response.getWriter();
			pw.print(result);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("异常:" + e.getMessage());
		}
		logger.debug("=====@printResult()=====E");
	}

	/** 流程列表-----开始 */
	  @RequestMapping(value = "/test")
	public String test(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		System.out.println("===进入test======");
		return "/rd/informationMenu";
	}
	
	
	
	@RequestMapping(value = "/topWorkFlowList")
	  public String topWorkFlowList(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
	  {
	    System.out.println("=========常用流程===start=======");
	    String domainId = session.getAttribute("domainId").toString();
	    String userId = session.getAttribute("userId").toString();
	    String userKey = session.getAttribute("userKey").toString();
	    Map param = new HashMap();
	    param.put("cmd", "bpm_getTopFlowList");
	    param.put("domain", domainId);
	    param.put("userId", userId);
	    param.put("mobileType", "");
	    String docXml = WebServiceUtils.getWebServiceData(param, request, userKey);
	    String description = XmlHelper.getElement(docXml, "//description");

	    if ("TokenError".equals(description)) {
	      session.setAttribute("OaToken", "");
	      docXml = WebServiceUtils.getWebServiceData(param, request, userKey);
	    }
	    String results = XmlHelper.getElement(docXml, "//result");
	    String recordCount = XmlHelper.getElement(docXml, "//recordcount");
	    if ((results != null) && ("1".equals(results))) {
	      String docData = XmlHelper.parseXmlToJson(docXml);
	      XmlHelper.printResult(response, "success", new String[] { docData, 
	        "\"" + String.valueOf(recordCount) + "\"" });
	    } else {
	      XmlHelper.printResult(response, "fail", new String[] { "[]" });
	    }
	    System.out.println("=========常用流程===end=======");
	    return null;
	  }
	
		

	  
	  @RequestMapping(value = "/listFlowData")
	  public String listFlowData(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
	  {
		System.out.println("=========全部流程===start=======");
	    String domainId = session.getAttribute("domainId").toString();
	    String userId = session.getAttribute("userId").toString();
	    String userKey = session.getAttribute("userKey").toString();
	    String orgIdString = session.getAttribute("orgIdString").toString();
	    String processName = request.getParameter("processName") == null ? "" :
	    	EncryptUtil.sqlcode(request.getParameter("processName"));
	    this.logger.debug("processName------------------>" + processName);
	    if (processName != null) {
	      processName = processName.replace("&", "&amp;");
	      processName = processName.replace("%", "\\\\%");
	    }

	    Map params = new HashMap();
	    params.put("cmd", "bpm_getUserProcessByModuleIds_new");
	    params.put("domain", domainId);
	    params.put("userId", userId);
	    params.put("orgIdString", orgIdString);
	    params.put("moduleIds", "1,11");
	    params.put("processName", processName);
	    String jsonperson = WebServiceUtils.getJsonDataNoReplace(params, request, userKey);
	    jsonperson = jsonperson.replaceAll("\\[\\]", "\" \"").replaceAll("\\&nbsp;", " ");
	    try {
	      response.setContentType("text/html;charset=UTF-8");
	      response.getWriter().print(jsonperson);
	      response.getWriter().close();
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
	    System.out.println("=========全部流程===end=======");
	    return null;
	  }
	 
	
	/** 流程列表-----开始 */
	  @RequestMapping(value = "/listflow")
	public String listflow(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----listflow-----");
		//判断车辆流程是否有可用的车辆
		String selVoiture = request.getParameter("selVoiture");
		if(selVoiture != null && selVoiture != ""){
			model.addAttribute("selVoiture", selVoiture);
		}
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String processName = request.getParameter("processName") == null ?"":request.getParameter("processName");
		String processName1=processName;
		System.out.println("processName------------------>"+processName);
		if(processName!=null){
			processName=processName.replace("&", "&amp;");
			processName=processName.replace("%", "\\\\%");//由于模糊查询是 like '%processName%'
		}
		String userKey = session.getAttribute("userKey").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		/**
		 * 2015-05-08 params.put("cmd", "bpm_getUserPackage");//取所有的流程分类
		 * params.put("domain", domainId); params.put("moduleIds", "1");
		 * params.put("userId", userId); params.put("orgIdString", orgIdString);
		 */
		params.put("cmd", "bpm_getUserProcessByModuleIds_new");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("moduleIds", "1,11");
		params.put("processName", processName);

		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("processName", processName1);
		}
		return "/workflow/workflow_list";
	}
	

	/**
	 * 2015-05-08 此方法已作废
	 */
	@RequestMapping(value = "/getprocess")
	public String getProcessesByPackageId(HttpServletRequest request, HttpServletResponse response,
			HttpSession session, Model model) {
		logger.debug("-----getprocess-----");
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String packageId = request.getParameter("packageId");
		String userKey = session.getAttribute("userKey").toString();
		logger.debug("packageId----->" + packageId);

		Map<String, String> params = new HashMap<String, String>();
		// params.put("cmd", "workflow_getUserProcessByModuleIds");
		params.put("cmd", "bpm_getUserProcessByModuleIds");// 取能发起的流程
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("moduleIds", "1");
		if (packageId != null && !"".equals(packageId)) {
			params.put("packageId", packageId);
		}
		String jsonXml = "[]";
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		jsonXml = readStringProcessXml(docXml);
		logger.debug("jsonXml----->" + jsonXml);
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().print(jsonXml);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 2015-05-08 此方法已作废
	 */
	public String readStringProcessXml(String xml) {
		String json = "[";
		Document doc = null;
		// 下面的是通过解析xml字符串的
		try {
			doc = DocumentHelper.parseText(xml); // 将字符串转为XML
			Element rootElt = doc.getRootElement(); // 获取根节点
			List<Element> es = rootElt.elements();
			for (Element e : es) {
				// 得到该元素下的所有子节点
				List<Element> lists = e.elements();
				for (Element list : lists) {
					if (!"process".equals(list.getName())) {
						continue;
					}
					json += "{";
					json += "\"" + "processId" + "\":\"" + list.element("pool_processId").getText() + "\"" + ",";// 流程id
					json += "\"" + "processName" + "\":\"" + list.element("pool_processName").getText() + "\"" + ",";// 流程名
					json += "\"" + "process_type" + "\":\"" + list.element("pool_process_type").getText() + "\"" + ",";// 流程类型：0：老
					// 1：ezFLOW
					json += "\"" + "oldprocess_id" + "\":\"" + list.element("pool_oldprocess_id").getText() + "\""
							+ ",";// 老流程processId
					json += "\"" + "oldprocess_formid" + "\":\"" + list.element("pool_oldprocess_formid").getText()
							+ "\"" + ",";// 老流程tableId
					json += "\"" + "ezflowprocess_id" + "\":\"" + list.element("pool_ezflowprocess_defid").getText()
							+ "\"" + ",";// ezFLOW定义id
					json += "\"" + "ezflowprocess_formKey" + "\":\""
							+ list.element("pool_ezflowprocess_formKey").getText() + "\"" + "";// ezFLOW
					// 表单key
					json += "},";
				}
				if (json.endsWith(",")) {
					json = json.substring(0, json.length() - 1);
				}
			}
			if (json.endsWith(",")) {
				json = json.substring(0, json.length() - 1);
			}
			json += "]";
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return json;
	}

	/** 流程列表-----结束 */

	/** ezFlow-----新建流程开始 */
	@RequestMapping(value = "/newezform")
	public String newezform(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("<-----newezform----->");
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String pageId = request.getParameter("pageId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String processId = request.getParameter("processId");
		String userKey = session.getAttribute("userKey").toString();
		String moduleId = request.getParameter("moduleId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "getNewForm");
		params.put("domain", domainId);
		params.put("pageId", pageId);
		params.put("processId", processId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("formType", "ezform");
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("cmd", "workflow_getFirstNoWriteField");
		params1.put("processId", processId);
		String docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
		description = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
		}
		String results1 = XmlHelper.getElement(docXml1, "//result");
		logger.debug("results1----->" + results1);
        if (results1 != null && "1".equals(results1)) {
            model.addAttribute("docXml1", docXml1);
        }       
        model.addAttribute("moduleId", moduleId);
		return "/workflow/newezprocess";
	}

	@RequestMapping(value = "/firstezflowsend")
	public String newezflowsend(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		logger.debug("<-----firstezflowsend----->");
		String infoId = null;
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String userId = session.getAttribute("userId").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();
			String processId = request.getParameter("processId");
			String userKey = session.getAttribute("userKey").toString();
			String moduleId = request.getParameter("moduleId");
			StringBuffer formData = new StringBuffer();
			formData.append("<moduleType>").append("ezform").append("</moduleType>");
			formData.append("<operateType>1</operateType>");
			formData.append("<infoId></infoId>");
			formData.append("<pageId>").append(request.getParameter("__sys_pageId")).append("</pageId>");
			formData.append("<userId>").append(userId).append("</userId>");
			formData.append("<mainTable>");
			formData.append("<tableName>").append(request.getParameter("__main_tableName")).append("</tableName>");
			formData.append("<fields>");
			Enumeration it = request.getParameterNames();
			int fieldcount = 0;
			// 附件显示名和附件保存名 拼接格式：12222.jpg,11121.jpg,;图片.jpg,图片.jpg
			String[] fileNameArray = null;
			StringBuilder fileFieldName = null;
			StringBuilder fileFieldSaveName = null;
			String oldFileNames = "";
			String oldFileName = "";
			String oldFileSaveName = "";
			while (it.hasMoreElements()) {
				String key = (String) it.nextElement();
				if (key.startsWith("_main_")) {
					fieldcount++;
					// 循环保存主表字段值
					String value;
					Object[] os = request.getParameterValues(key);
					if (os == null || os.length < 2) {
						value = request.getParameter(key);
					} else {
						value = "";
						for (int i = 0; i < os.length; i++) {
							value += (os[i]);
						}
					}
					formData.append("<field>");
					formData.append("<fieldName>").append(key.substring(6)).append("</fieldName>");
					formData.append("<value><![CDATA[").append(value).append("]]></value>");
					formData.append("</field>");
				} else if (key.startsWith("_mainfile_")) {// 附件
					oldFileNames = request.getParameter("fileNames" + key.replace("_mainfile_", ""));
					if (oldFileNames != null) {
						oldFileName = oldFileNames.split(";")[0];
						oldFileSaveName = oldFileNames.split(";")[1];
					}
					fileFieldName = new StringBuilder(oldFileName);
					fileFieldSaveName = new StringBuilder(oldFileSaveName);
					fileNameArray = request.getParameterValues(key);
					if (fileNameArray != null) {
						for (int index = 0, length = fileNameArray.length; index < length; index++) {
							fileFieldName.append(fileNameArray[index].split("\\|")[0]).append(",");
							fileFieldSaveName.append(
									fileNameArray[index].split("\\|")[1].substring(fileNameArray[index].split("\\|")[1]
											.lastIndexOf("\\") + 1)).append(",");
						}
						formData.append("<field>");
						formData.append("<fieldName>").append(key.substring(10)).append("</fieldName>");
						formData.append("<value>").append(
								fileFieldName.append(";").append(fileFieldSaveName).toString()).append("</value>");
						formData.append("</field>");
					}
				}
			}
			formData.append("</fields>");
			formData.append("</mainTable>");
			// 所有子表表名
			String[] subTableNames = request.getParameterValues("subTableName");
			// 添加子表
			formData.append("<subTables>");
			if (subTableNames != null) {
				Map<String, String[]> fieldMap = null;
				int fieldLength = 0;
				String[] dataIds;
				String key;
				String mapKey;
				for (int i = 0, length = subTableNames.length; i < length; i++) {
					it = request.getParameterNames();
					logger.debug("subTableName-------->" + subTableNames[i]);
					formData.append("<table>");
					formData.append("<tableName>").append(subTableNames[i]).append("</tableName>");
					fieldMap = new HashMap<String, String[]>();
					while (it.hasMoreElements()) {
						key = (String) it.nextElement();
						logger.debug("key---->" + key);
						if (key.startsWith("_sub_") && key.indexOf(subTableNames[i]) > 0) {
							fieldMap.put(key, request.getParameterValues(key));
						} else if (key.startsWith(subTableNames[i]) && key.indexOf("_subdataId") > 0) {
							dataIds = request.getParameterValues(key);
							fieldLength = dataIds.length;
						}
					}
					// 拼接记录
					for (int index = 0; index < fieldLength; index++) {
						formData.append("<fields>");
						// 拼接字段
						formData.append("<dataId></dataId>");
						for (Map.Entry<String, String[]> map : fieldMap.entrySet()) {
							formData.append("<field>");
							mapKey = map.getKey();
							if (mapKey.indexOf("_sub_file_") < 0) {
								// 非子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(5)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(map.getValue()[index]).append("]]></value>");
							} else {
								oldFileNames = request.getParameter("fileNames" + mapKey.replace("_subfile_", ""));
								// 子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(10)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(
										this.handleSubFileVal(map.getValue()[index], oldFileNames, oldFileName,
												oldFileSaveName)).append("]]></value>");
							}
							formData.append("</field>");
						}
						formData.append("</fields>");
					}
					formData.append("</table>");
				}
			}
			formData.append("</subTables>");
			logger.debug("" +
					"" + formData.toString());
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("cmd", "saveForm");
			params1.put("domain", domainId);
			params1.put("formData", formData.toString());
			String docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
			String description = XmlHelper.getElement(docXml1, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
			}
			
			logger.debug("docXml1----->" + docXml1);
			String results1 = XmlHelper.getElement(docXml1, "//result");
			if (!(results1 != null && "1".equals(results1))) {
				throw new RuntimeException("保存表单信息出错!" + docXml1);
			} else {
				infoId = XmlHelper.getElement(docXml1, "//infoId");
				model.addAttribute("businessId", infoId);
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "ezflow_findNextActivityWithStart");
			params.put("domain", domainId);
			params.put("processId", processId);
			logger.debug("infoId----->" + infoId);
			if (infoId == null) {
				params.put("businessKey", "2830443");
				model.addAttribute("businessId", "2830443");
			} else {
				params.put("businessKey", infoId);
			}
			params.put("userId", userId);
			params.put("orgId", orgId);
			params.put("orgIdString", orgIdString);
			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			if (results != null && "1".equals(results)) {
				model.addAttribute("docXml", docXml);
				if (request.getParameter("fileTitle") != null) {
					model.addAttribute("docTitle", request.getParameter("fileTitle"));
				}
			}
			model.addAttribute("moduleId",moduleId);
		}
		return "/workflow/firstezflowsend";
	}

	/**
	 * 按照接口要求处理子表内容
	 * 
	 * @param oldFileName
	 * @return
	 */
	private String handleSubFileVal(String formFileNames, String oldFileNames, String oldFileName,
			String oldFileSaveName) {
		if (oldFileNames != null) {
			oldFileName = oldFileNames.split(";")[0];
			oldFileSaveName = oldFileNames.split(";")[1];
		}
		String[] formFileArray = formFileNames.split(";");
		StringBuilder fileFieldName = new StringBuilder(oldFileName);
		StringBuilder fileFieldSaveName = new StringBuilder(oldFileSaveName);
		String[] nameArray;
		if (formFileArray == null) {
			return "";
		}
		for (int i = 0, length = formFileArray.length; i < length; i++) {
			nameArray = formFileArray[i].split("\\|");
			if (nameArray == null || "".equals(nameArray[0])) {
				break;
			}
			fileFieldName.append(nameArray[0]).append(",");
			fileFieldSaveName.append(nameArray[1].substring(nameArray[1].lastIndexOf("\\") + 1)).append(",");
		}
		if (fileFieldName.length() == 0 || fileFieldSaveName.length() == 0) {
			return "";
		}
		return fileFieldName.append(";").append(fileFieldSaveName).toString();
	}

	@RequestMapping(value = "/sendezflowprocess")
	public String sendezflowprocess(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam String[] activity,Model model) {
		logger.debug("<-----sendezflowprocess----->");
		boolean ret = false;
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String uId = session.getAttribute("userId").toString();
			String users = session.getAttribute("userName").toString();
			String orgName = session.getAttribute("orgName").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();

			String processId = request.getParameter("processId");
			String moduleId = request.getParameter("moduleId");
			//String activityId = request.getParameter("activity");
			//String transactors = request.getParameter("userId");
			String businessId = request.getParameter("businessId");
			String gateNum = request.getParameter("gateNum");
			//String activityName = request.getParameter("activityName");
			//String activityType = request.getParameter("activityType");
			String userKey = session.getAttribute("userKey").toString();
			String mainLinkFile = request.getParameter("mainLinkFile");
			mainLinkFile = "<![CDATA["+ mainLinkFile + "]]>";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "ezflow_start");
			params.put("domain", domainId);
			params.put("userId", uId);
			params.put("userName", users);
			params.put("orgId", orgId);
			params.put("orgName", orgName);
			params.put("orgIdString", orgIdString);
			params.put("businessKey", businessId);
			params.put("processId", processId);
			params.put("gateNum", gateNum);
			params.put("mainLinkFile", mainLinkFile);
			params.put("moduleId", moduleId);
			String gateType = request.getParameter("gateType");
			List<Map<String, String>> nextAct = new ArrayList<Map<String, String>>();
			if (activity != null) {
				Map<String, String> map;
				for (int i = 0; i < activity.length; i++) {
					map = new HashMap<String, String>();
					logger.debug("gateType----->" + gateType);
					logger.debug("activity[" + i + "]----->" + activity[i]);
					map.put("activityId", activity[i]);
					if ("".equals(gateType) || "XOR".equals(gateType) || gateType == null) {
						logger.debug("userI=========" + request.getParameter("userId"));
						logger.debug("activityName=========" + request.getParameter("activityName"));
						logger.debug("activityType=========" + request.getParameter("activityType"));
						String transactors = request.getParameter("userId");
						if (transactors != null){
							transactors = transactors.replaceAll("\\$\\$", ",");
						}
				        if (transactors != null){
				    	  transactors = transactors.replaceAll("\\$", "");  
				        }
						map.put("userIds", transactors);
						map.put("activityName", request.getParameter("activityName"));
						map.put("activityType", request.getParameter("activityType"));
						map.put("userNames", request.getParameter("userName"));
					} else {
						logger.debug("userId[" + i + "]----->" + request.getParameter("userId" + activity[i]));
						logger.debug("activityName[" + i + "]----->" + request.getParameter("activityName" + activity[i]));
						logger.debug("activityType[" + i + "]----->" + request.getParameter("activityType" + activity[i]));
						String transactors = request.getParameter("userId" + activity[i]);
						if (transactors != null){
							transactors = transactors.replaceAll("\\$\\$", ",");
						}
				        if (transactors != null){
				    	  transactors = transactors.replaceAll("\\$", "");  
				        }
						map.put("userIds", transactors);
						map.put("activityName", request.getParameter("activityName" + activity[i]));
						map.put("activityType", request.getParameter("activityType" + activity[i]));
						map.put("userNames", request.getParameter("userName" + activity[i]));
					}
					nextAct.add(map);
				}
			}
			params.put("activity", nextAct);
			if (request.getParameter("needMailRemind") != null && !"".equals(request.getParameter("needMailRemind"))) {
				params.put("needMailRemind", request.getParameter("needMailRemind"));
			}
			if (request.getParameter("needSmsRemind") != null && !"".equals(request.getParameter("needSmsRemind"))) {
				params.put("needSmsRemind", request.getParameter("needSmsRemind"));
			}
			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			String description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			logger.debug("results----->" + results);
			ret = true;
		}
		model.addAttribute("ret", String.valueOf(ret));
		if (ret) {
			XmlHelper.printResult(response, "success", "\"\"");
		} else {
			XmlHelper.printResult(response, "fail", "\"\"");
		}
		return null;
	}

	/** ezFlow-----新建流程结束 */

	/** 老工作流-----新建流程开始 */
	@RequestMapping(value = "/newform")
	public String newform(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("<-----newform----->");
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String pageId = request.getParameter("pageId");
		String processId = request.getParameter("processId");
		String userKey = session.getAttribute("userKey").toString();
		String moduleId = request.getParameter("moduleId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "getNewForm");
		params.put("domain", domainId);
		params.put("pageId", pageId);
		params.put("userId", userId);
		params.put("processId", processId);
		params.put("orgIdString", orgIdString);
		params.put("formType", "");
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
		}
		Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("cmd", "workflow_getFirstNoWriteField");
        params1.put("processId", processId);
        String docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
        description = XmlHelper.getElement(docXml1, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
		}
        String results1 = XmlHelper.getElement(docXml1, "//result");
        logger.debug("results1----->" + results1);
        if (results1 != null && "1".equals(results1)) {
            model.addAttribute("docXml1", docXml1);
        }
        model.addAttribute("moduleId", moduleId);
		return "/workflow/newprocess";
	}

	@RequestMapping(value = "/firstsend")
	public String newsend(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("<-----firstsend----->");
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String userId = session.getAttribute("userId").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();
			String processId = request.getParameter("processId");
			String userKey = session.getAttribute("userKey").toString();
			String moduleId = request.getParameter("moduleId");
			Map<String, Object> params1 = new HashMap<String, Object>();
			StringBuffer formData = new StringBuffer();
			formData.append("<moduleType>").append(
					request.getParameter("__sys_formType") == null ? "form" : request.getParameter("__sys_formType"))
					.append("</moduleType>");
			formData.append("<operateType>1</operateType>");
			formData.append("<infoId>").append(
					request.getParameter("__sys_infoId") == null ? "" : request.getParameter("__sys_infoId")).append(
					"</infoId>");
			formData.append("<pageId>").append(request.getParameter("__sys_pageId")).append("</pageId>");
			formData.append("<userId>").append(userId).append("</userId>");
			formData.append("<mainTable>");
			formData.append("<tableName>").append(request.getParameter("__main_tableName")).append("</tableName>");
			formData.append("<fields>");
			String[] fileNameArray = null;
			StringBuilder fileFieldName = null;
			StringBuilder fileFieldSaveName = null;
			String oldFileNames = "";
			String oldFileName = "";
			String oldFileSaveName = "";
			Enumeration it = request.getParameterNames();
			int fieldcount = 0;
			while (it.hasMoreElements()) {
				String key = (String) it.nextElement();
				if (key.startsWith("_main_")) {
					fieldcount++;
					// 循环保存主表字段值
					String value;
					Object[] os = request.getParameterValues(key);
					if (os == null || os.length < 2) {
						value = request.getParameter(key);
					} else {
						value = "";
						for (int i = 0; i < os.length; i++) {
							value += (os[i]);
						}
					}
					formData.append("<field>");
					formData.append("<fieldName>").append(key.substring(6)).append("</fieldName>");
					formData.append("<value><![CDATA[").append(value).append("]]></value>");
					formData.append("</field>");
				} else if (key.startsWith("_mainfile_")) {// 附件
					oldFileNames = request.getParameter("fileNames" + key.replace("_mainfile_", ""));
					if (oldFileNames != null) {
						oldFileName = oldFileNames.split(";")[0];
						oldFileSaveName = oldFileNames.split(";")[1];
					}
					fileFieldName = new StringBuilder(oldFileName);
					fileFieldSaveName = new StringBuilder(oldFileSaveName);
					fileNameArray = request.getParameterValues(key);
					if (fileNameArray != null) {
						for (int index = 0, length = fileNameArray.length; index < length; index++) {
							fileFieldName.append(fileNameArray[index].split("\\|")[0]).append(",");
							if(fileNameArray[index] != null && !"".equals(fileNameArray[index])){
								fileFieldSaveName.append(
										fileNameArray[index].split("\\|")[1].substring(fileNameArray[index].split("\\|")[1]
												.lastIndexOf("\\") + 1)).append(",");
							}
						}
						formData.append("<field>");
						formData.append("<fieldName>").append(key.substring(10)).append("</fieldName>");
						formData.append("<value>").append(
								fileFieldName.append(";").append(fileFieldSaveName).toString()).append("</value>");
						formData.append("</field>");
					}
				}
			}
			formData.append("</fields>");
			formData.append("</mainTable>");
			// 所有子表表名
			String[] subTableNames = request.getParameterValues("subTableName");
			// 添加子表
			formData.append("<subTables>");
			if (subTableNames != null) {
				Map<String, String[]> fieldMap = null;
				int fieldLength = 0;
				String[] dataIds;
				String key;
				String mapKey;
				for (int i = 0, length = subTableNames.length; i < length; i++) {
					it = request.getParameterNames();
					logger.debug("subTableName-------->" + subTableNames[i]);
					formData.append("<table>");
					formData.append("<tableName>").append(subTableNames[i]).append("</tableName>");
					fieldMap = new HashMap<String, String[]>();
					while (it.hasMoreElements()) {
						key = (String) it.nextElement();
						if (key.startsWith("_sub_") && key.indexOf(subTableNames[i]) > 0) {
							fieldMap.put(key, request.getParameterValues(key));
						} else if (key.startsWith(subTableNames[i]) && key.indexOf("_subdataId") > 0) {
							dataIds = request.getParameterValues(key);
							fieldLength = dataIds.length;
							logger.debug("fieldLength---->" + fieldLength);
						}
					}
					logger.debug("fieldLength---->" + fieldLength);
					logger.debug("fieldMap---->" + fieldMap);
					// 拼接记录
					for (int index = 0; index < fieldLength; index++) {
						formData.append("<fields>");
						// 拼接字段
						formData.append("<dataId></dataId>");
						for (Map.Entry<String, String[]> map : fieldMap.entrySet()) {
							formData.append("<field>");
							mapKey = map.getKey();
							if (mapKey.indexOf("_sub_file_") < 0) {
								// 非子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(5)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(map.getValue()[index]).append("]]></value>");
							} else {
								oldFileNames = request.getParameter("fileNames" + mapKey.replace("_subfile_", ""));
								// 子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(10)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(
										this.handleSubFileVal(map.getValue()[index], oldFileNames, oldFileName,
												oldFileSaveName)).append("]]></value>");
							}
							formData.append("</field>");
						}
						formData.append("</fields>");
					}
					formData.append("</table>");
				}
			}
			formData.append("</subTables>");

			logger.debug("formData----->" + formData.toString());

			params1.put("cmd", "saveForm");
			params1.put("domain", domainId);
			params1.put("formData", formData.toString());
			String docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
			String description = XmlHelper.getElement(docXml1, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
			}
			logger.debug("docXml1----->" + docXml1);
			String results1 = XmlHelper.getElement(docXml1, "//result");
			if (!(results1 != null && "1".equals(results1))) {
				throw new RuntimeException("保存表单信息出错!" + docXml1);
			} else {
				String infoId = XmlHelper.getElement(docXml1, "//infoId");
				model.addAttribute("businessId", infoId);
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "workflow_getFirstNextList");
			params.put("domain", domainId);
			params.put("processId", processId);
			params.put("userId", userId);
			params.put("orgId", orgId);
			params.put("orgIdString", orgIdString);
			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			if (results != null && "1".equals(results)) {
				model.addAttribute("docXml", docXml);
				if (request.getParameter("fileTitle") != null) {
					model.addAttribute("docTitle", request.getParameter("fileTitle"));
				}
			}
			model.addAttribute("moduleId", moduleId);
		}
		return "/workflow/firstsend";
	}

	@RequestMapping(value = "/sendprocess")
	public String sendprocess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("<-----sendprocess----->");
		boolean ret = false;
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String uId = session.getAttribute("userId").toString();
			String users = session.getAttribute("userName").toString();
			String orgName = session.getAttribute("orgName").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();

			String processId = request.getParameter("processId");
			String activityId = request.getParameter("activity");
			String transactors = request.getParameter("userId");
			String transactorNames = request.getParameter("userName");
			String businessId = request.getParameter("businessId");
			String mainLinkFile = request.getParameter("mainLinkFile");
			logger.debug("processId----->" + processId);
			logger.debug("activityId----->" + activityId);
			logger.debug("transactors----->" + transactors);
			logger.debug("transactorNames----->" + transactorNames);
			logger.debug("businessId----->" + businessId);
			logger.debug("mainLinkFile----->" + mainLinkFile);
			String userKey = session.getAttribute("userKey").toString();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "workflow_sendFirst");
			params.put("domain", domainId);
			params.put("userId", uId);
			params.put("userName", users);
			params.put("orgId", orgId);
			params.put("orgName", orgName);
			params.put("orgIdString", orgIdString);
			StringBuffer workflowstr = new StringBuffer();
			workflowstr.append("<processId>" + processId + "</processId>");
			workflowstr.append("<activityId>" + activityId + "</activityId>");
			workflowstr.append("<mainLinkFile><![CDATA["+ mainLinkFile + "]]></mainLinkFile>");
			workflowstr.append("<cancelHref></cancelHref>");
			if (transactors != null) {
				transactors = transactors.replaceAll("\\$\\$", ",");
			}
			if (transactors != null) {
				transactors = transactors.replaceAll("\\$", "");
			}
			workflowstr.append("<transactors>" + transactors + "</transactors>");
			workflowstr.append("<transactorNames>" + transactorNames + "</transactorNames>");
			workflowstr.append("<businessId>" + businessId + "</businessId>");
			params.put("workflow", workflowstr.toString());
			if (request.getParameter("needMailRemind") != null && !"".equals(request.getParameter("needMailRemind"))) {
				params.put("needMailRemind", request.getParameter("needMailRemind"));
			}
			if (request.getParameter("needSmsRemind") != null && !"".equals(request.getParameter("needSmsRemind"))) {
				params.put("needSmsRemind", request.getParameter("needSmsRemind"));
			}
			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			String description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			logger.debug("results----->" + results);
			ret = true;
		}
		model.addAttribute("ret", String.valueOf(ret));
		if (ret) {
			XmlHelper.printResult(response, "success", "\"\"");
		} else {
			XmlHelper.printResult(response, "fail", "\"\"");
		}
		return null;
	}

	/** 阅件办理结束-----开始 */
	@RequestMapping(value = "/readover")
	// 新的发送,无批示意见
	public String readover(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow readover-----");
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String userId = session.getAttribute("userId").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();
			String userName = session.getAttribute("userName").toString();
			String workId = request.getParameter("workId");
			String comment = request.getParameter("comment_input") == null ?  ""  : request.getParameter("comment_input");
			String userKey = session.getAttribute("userKey").toString();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "bpm_completeRead");
			params.put("domain", domainId);
			params.put("wf_work_id", workId);
			params.put("commentType", "0");
			params.put("comment", comment);
			params.put("userName", userName);
			params.put("userId", userId);
			params.put("orgId", orgId);
			params.put("orgIdString", orgIdString);

			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			String description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			// logger.debug("docXml----->" + docXml);
			boolean ret = false;
			String results = XmlHelper.getElement(docXml, "//result");
			// logger.debug("results----->" + results);
			if (results != null && "1".equals(results)) {
				// model.addAttribute("docXml", docXml);
				// model.addAttribute("workId", workId);
				// if (request.getParameter("fileTitle") != null) {
				// model.addAttribute("docTitle",
				// request.getParameter("fileTitle"));
				// }
				ret = true;
			}
			;
			if (ret) {
				XmlHelper.printResult(response, "success", "\"\"");
			} else {
				XmlHelper.printResult(response, "fail", "\"\"");
			}

		}
		return null;
	}

	/** 老工作流-----新建流程结束 */

	/** 文件办理-----开始 */
	@RequestMapping(value = "/sendnew")
	// 新的发送,无批示意见
	public String sendnew(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow sendnew-----");
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String userId = session.getAttribute("userId").toString();
			String orgId = session.getAttribute("orgId").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();
			
			String tableId = request.getParameter("tableId");
			logger.debug("tableId----->" + tableId);
			String recordId = request.getParameter("recordId");
			logger.debug("recordId----->" + recordId);
			String activityId = request.getParameter("activityId");
			logger.debug("activityId----->" + activityId);
			String workId = request.getParameter("workId");
			logger.debug("workId----->" + workId);
			String stepCount = request.getParameter("stepCount");
			logger.debug("stepCount----->" + stepCount);

			// 新添加参数 2012/4/5
			String isForkTask = request.getParameter("isForkTask");
			logger.debug("isForkTask----->" + isForkTask);
			String forkStepCount = request.getParameter("forkStepCount");
			logger.debug("forkStepCount----->" + forkStepCount);
			String forkId = request.getParameter("forkId");
			logger.debug("forkId----->" + forkId);
			String isDossier = request.getParameter("isDossier");
			logger.debug("isDossier----->"+isDossier);
			model.addAttribute("isDossier", isDossier);
			
			String commentType = request.getParameter("commentType");
			logger.debug("commentType----->" + commentType);
			model.addAttribute("commentType", commentType);
			String[] commentAcc = request.getParameterValues("commentacc");			
			String commentAccSt="";
			if(commentAcc!=null && commentAcc.length>0){
				for(int i=0;i<commentAcc.length;i++){
					if(i != commentAcc.length-1){
						commentAccSt += commentAcc[i]+",";
					}else{
						commentAccSt += commentAcc[i];
					}
				}
			}
			request.setAttribute("commentAcc", commentAccSt);

			// 保存表单信息
			StringBuffer formData = new StringBuffer();
			formData.append("<moduleType>").append(request.getParameter("__sys_formType")).append("</moduleType>");
			formData.append("<operateType>2</operateType>");
			formData.append("<infoId>").append(request.getParameter("__sys_infoId")).append("</infoId>");
			formData.append("<pageId>").append(request.getParameter("__sys_pageId")).append("</pageId>");
			formData.append("<userId>").append(userId).append("</userId>");
			formData.append("<mainTable>");
			formData.append("<tableName>").append(request.getParameter("__main_tableName")).append("</tableName>");
			formData.append("<fields>");
			Enumeration it = request.getParameterNames();
			String[] fileNameArray = null;
			StringBuilder fileFieldName = null;
			StringBuilder fileFieldSaveName = null;
			String oldFileNames = "";
			String oldFileName = "";
			String oldFileSaveName = "";
			int fieldcount = 0;
			int subFieldCount = 0;
			while (it.hasMoreElements()) {
				String key = (String) it.nextElement();
				if (key.startsWith("_main_")) {
					fieldcount++;
					// 循环保存主表字段值
					String value;
					Object[] os = request.getParameterValues(key);
					if (os == null || os.length < 2) {
						value = request.getParameter(key);
					} else {
						value = "";
						for (int i = 0; i < os.length; i++) {
							value += (os[i]);
						}
					}
					formData.append("<field>");
					formData.append("<fieldName>").append(key.substring(6)).append("</fieldName>");
					formData.append("<value><![CDATA[").append(value).append("]]></value>");
					formData.append("</field>");
				} else if (key.startsWith("_mainfile_")) {// 附件
					fieldcount++;
					oldFileNames = request.getParameter("fileNames" + key.replace("_mainfile_", ""));
					if (oldFileNames != null) {
						oldFileName = oldFileNames.split(";")[0];
						oldFileSaveName = oldFileNames.split(";")[1];
					}
					logger.debug("oldFileName--->" + oldFileName);
					logger.debug("oldFileSaveName--->" + oldFileSaveName);
					fileFieldName = new StringBuilder(oldFileName);
					fileFieldSaveName = new StringBuilder(oldFileSaveName);
					fileNameArray = request.getParameterValues(key);
					if (fileNameArray != null) {
						for (int index = 0, length = fileNameArray.length; index < length; index++) {
							fileFieldName.append(fileNameArray[index].split("\\|")[0]).append(",");
							fileFieldSaveName.append(
									fileNameArray[index].split("\\|")[1].substring(fileNameArray[index].split("\\|")[1]
											.lastIndexOf("\\") + 1)).append(",");
						}
						logger.debug("fileFieldName--->" + fileFieldName.toString());
						logger.debug("fileFieldSaveName--->" + fileFieldSaveName.toString());
						formData.append("<field>");
						formData.append("<fieldName>").append(key.substring(10)).append("</fieldName>");
						formData.append("<value><![CDATA[").append(
								fileFieldName.append(";").append(fileFieldSaveName).toString()).append("]]></value>");
						formData.append("</field>");
					}
				}
			}
			formData.append("</fields>");
			formData.append("</mainTable>");
			// 所有子表表名
			String[] subTableNames = request.getParameterValues("subTableName");
			// 添加子表
			formData.append("<subTables>");
			if (subTableNames != null) {
				Map<String, String[]> fieldMap = null;
				int fieldLength = 0;
				String[] dataIds = null;
				String key;
				String mapKey;
				String newMapKey;
				String[] feildNameArr = null;
				String[] feildNameArr1 = null;
				for (int i = 0, length = subTableNames.length; i < length; i++) {
					it = request.getParameterNames();
					logger.debug("subTableName-------->" + subTableNames[i]);
					formData.append("<table>");
					formData.append("<tableName>").append(subTableNames[i]).append("</tableName>");
					fieldMap = new HashMap<String, String[]>();
					while (it.hasMoreElements()) {
						key = (String) it.nextElement();
						if (key.startsWith("_sub_") && key.indexOf(subTableNames[i]) > 0) {
							subFieldCount ++;
							fieldMap.put(key, request.getParameterValues(key));
						} else if (key.startsWith(subTableNames[i]) && key.indexOf("_subdataId") > 0) {
							dataIds = request.getParameterValues(key);
							fieldLength = dataIds.length;
							logger.debug("fieldLength---->" + fieldLength);
						}
					}
					logger.debug("fieldLength---->" + fieldLength);
					logger.debug("fieldMap---->" + fieldMap);
					// 拼接记录
					for (int index = 0; index < fieldLength; index++) {
						formData.append("<fields>");
						// 拼接字段
						if (null != dataIds) {
							formData.append("<dataId>" + dataIds[index] + "</dataId>");
						}
						for (Map.Entry<String, String[]> map : fieldMap.entrySet()) {
							formData.append("<field>");
							mapKey = map.getKey();
							//处理字段名老格式保存丢失
							if(mapKey.indexOf("|")>0){
								feildNameArr = mapKey.split("t");
								feildNameArr1 = mapKey.split("_");
								newMapKey = feildNameArr[0]+feildNameArr1[3];
								newMapKey = newMapKey.substring(0, newMapKey.length()-1);
								mapKey = newMapKey;
							}
							if (mapKey.indexOf("_sub_file_") < 0) {
								// 非子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(5)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(map.getValue()[index]).append("]]></value>");
							} else {
								oldFileNames = request.getParameter("fileNames" + mapKey.replace("_sub_file_", ""));
								// 子表附件数据
								formData.append("<fieldName>").append(mapKey.substring(10)).append("</fieldName>");
								formData.append("<value><![CDATA[").append(
										this.handleSubFileVal(map.getValue()[index], oldFileNames, oldFileName,
												oldFileSaveName)).append("]]></value>");
							}
							formData.append("</field>");
						}
						formData.append("</fields>");
					}
					formData.append("</table>");
				}
			}
			formData.append("</subTables>");
			logger.debug("formData----->" + formData.toString());
			String userKey = session.getAttribute("userKey").toString();
			String description = "";
			Map<String, Object> params1 = new HashMap<String, Object>();
			params1.put("cmd", "saveForm");
			params1.put("domain", domainId);
			params1.put("formData", formData.toString());
			System.out.println("@@##formData="+formData);
			logger.debug("ll@@##formData="+formData);
			if (fieldcount > 0 || subFieldCount > 0) {
				String docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
				description = XmlHelper.getElement(docXml1, "//description");
				//令牌超期
				if("TokenError".equals(description)){
					session.setAttribute("OaToken","");
					docXml1 = WebServiceUtils.getWebServiceDataNoReplace(params1,request,userKey);
				}
				logger.debug("docXml1----->" + docXml1);
				String results1 = XmlHelper.getElement(docXml1, "//result");
				logger.debug("results1----->" + results1);
				if (!(results1 != null && "1".equals(results1))) {
					throw new RuntimeException("保存表单信息出错!" + docXml1);
				}
			}

			Map<String, Object> params = new HashMap<String, Object>();
			params.put("cmd", "workFlow_getNextActivity");
			params.put("domain", domainId);
			params.put("tableId", tableId);
			params.put("recordId", recordId);
			params.put("activityId", activityId);
			params.put("workId", workId);
			params.put("stepCount", stepCount);
			params.put("userId", userId);
			params.put("orgId", orgId);
			params.put("orgIdString", orgIdString);
			// 添加参数
			params.put("isForkTask", isForkTask);
			params.put("forkStepCount", forkStepCount);
			params.put("forkId", forkId);
			String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
			}
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			logger.debug("results----->" + results);
			if (results != null && "1".equals(results)) {
				model.addAttribute("docXml", docXml);
				model.addAttribute("workId", workId);
				if (request.getParameter("fileTitle") != null) {
					model.addAttribute("docTitle", request.getParameter("fileTitle"));
				}
			}
		}
		model.addAttribute("comment", request.getParameter("comment"));
		model.addAttribute("comment_input", request.getParameter("comment_input"));
		model.addAttribute("workType", request.getParameter("worktype"));
		return "/workflow/workflow_send";
	}

	@RequestMapping(value = "/updateprocess1")
	public void updateprocess1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam String workId, @RequestParam String comment, @RequestParam String activity,
			@RequestParam String userId, Model model) {
		logger.debug("-----enter workflow updateprocess1-----");
		boolean ret = false;
		logger.debug("userId----->" + userId);
		logger.debug("workId----->" + workId);
		logger.debug("comment----->" + comment);
		logger.debug("activity----->" + activity);
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String uId = session.getAttribute("userId").toString();
			String users = session.getAttribute("userName").toString();
			String orgIdString = session.getAttribute("orgIdString").toString();
			String userKey = session.getAttribute("userKey").toString();
			String afterInsertTaskIds = request.getParameter("afterInsertTaskIds");
			if (afterInsertTaskIds == null) {
				afterInsertTaskIds = "";
			}
			logger.debug("afterInsertTaskIds----->" + afterInsertTaskIds);

			String commentType = request.getParameter("commentType") == null ? "0" : request
					.getParameter("commentType");
			logger.debug("commentType----->" + commentType);

			Map<String, String> params = new HashMap<String, String>();
			params.put("cmd", "workFlow_getWorkInfoByworkId");
			params.put("domain", domainId);
			params.put("workId", workId);
			params.put("userId", uId);
			String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
			String description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
			}
			
			docXml = docXml.replaceAll("&amp;", "&");
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			logger.debug("results----->" + results);
			if (results != null && "1".equals(results)) {
				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put("cmd", "updateProcess");
				params2.put("domain", domainId);
				params2.put("creatorcancellink", XmlHelper.getElement(docXml, "//creatorcancellink"));
				params2.put("workstepcount", XmlHelper.getElement(docXml, "//workstepcount"));
				params2.put("processdeadlinedate", XmlHelper.getElement(docXml, "//processdeadlinedate"));
				params2.put("workallowcancel", XmlHelper.getElement(docXml, "//workallowcancel"));
				params2.put("workdonewithdate", XmlHelper.getElement(docXml, "//workdonewithdate"));
				params2.put("workcreatedate", XmlHelper.getElement(docXml, "//workcreatedate"));
				params2.put("wf_curemployee_id", XmlHelper.getElement(docXml, "//wf_curemployee_id"));
				params2.put("initactivity", XmlHelper.getElement(docXml, "//initactivity"));
				params2.put("tranfrompersonid", XmlHelper.getElement(docXml, "//tranfrompersonid"));
				params2.put("workcurstep", XmlHelper.getElement(docXml, "//workcurstep"));
				params2.put("workmainlinkfile", "<![CDATA[" + XmlHelper.getElement(docXml, "//workmainlinkfile")
						+ "]]>");
				params2.put("worksubmitperson", XmlHelper.getElement(docXml, "//worksubmitperson"));
				params2.put("initactivityname", XmlHelper.getElement(docXml, "//initactivityname"));
				params2.put("worktable_id", XmlHelper.getElement(docXml, "//worktable_id"));
				params2.put("standforuserid", XmlHelper.getElement(docXml, "//standforuserid"));
				params2.put("wf_work_id", XmlHelper.getElement(docXml, "//wf_work_id"));
				params2.put("worksubmittime", XmlHelper.getElement(docXml, "//worksubmittime"));
				params2.put("wf_submitemployee_id", XmlHelper.getElement(docXml, "//wf_submitemployee_id"));
				params2.put("worktype", XmlHelper.getElement(docXml, "//worktype"));
				params2.put("workrecord_id", XmlHelper.getElement(docXml, "//workrecord_id"));
				params2.put("workprocess_id", XmlHelper.getElement(docXml, "//workprocess_id"));
				params2.put("workactivity", XmlHelper.getElement(docXml, "//workactivity"));
				params2.put("submitorg", XmlHelper.getElement(docXml, "//submitorg"));
				params2.put("isstandforwork", XmlHelper.getElement(docXml, "//isstandforwork"));
				params2.put("standforusername", XmlHelper.getElement(docXml, "//standforusername"));
				params2.put("workdeadline", XmlHelper.getElement(docXml, "//workdeadline"));
				params2.put("trantype", XmlHelper.getElement(docXml, "//trantype"));
				params2.put("emergence", XmlHelper.getElement(docXml, "//emergence"));
				params2.put("workfiletype", "<![CDATA[" + XmlHelper.getElement(docXml, "//workfiletype") + "]]>");
				params2.put("worktitle", "<![CDATA[" + XmlHelper.getElement(docXml, "//worktitle") + "]]>");
				params2.put("curIsForkTask", XmlHelper.getElement(docXml, "//isForkTask"));
				params2.put("curForkStepCount", XmlHelper.getElement(docXml, "//forkStepCount"));
				params2.put("curForkId", XmlHelper.getElement(docXml, "//forkId"));
				params2.put("fromforkActivityId", XmlHelper.getElement(docXml, "//fromforkActivityId"));
				params2.put("nextIsForkTask", XmlHelper.getElement(docXml, "//isForkTask"));
				params2.put("toJoinActivityId", "0");
				if (request.getParameter("docTitle") != " ") {
					params2.put("docTitle", "<![CDATA[" + request.getParameter("docTitle") + "]]>");
				}
				params2.put("commentType", commentType);
				params2.put("comment", comment);
				params2.put("userId", uId);
				params2.put("afterInsertTaskIds", afterInsertTaskIds);
				params2.put("orgIdString", orgIdString);
				params2.put("workflow_userId", userId);
				params2.put("nextActivityId", activity);
				params2.put("userName", users);
				String uname = request.getParameter("userName");
				if (StringUtils.isNotEmpty(uname)) {
					params2.put("workflow_userName", uname);
				} else {
					params2.put("workflow_userName", "");
				}
				params2.put("commentField", request.getParameter("commentField"));
				params2.put("activityClass", XmlHelper.getElement(docXml, "//activityClass"));
				if (request.getParameter("needMailRemind") != null
						&& !"".equals(request.getParameter("needMailRemind"))) {
					params2.put("needMailRemind", request.getParameter("needMailRemind"));
				}
				if (request.getParameter("needSmsRemind") != null && !"".equals(request.getParameter("needSmsRemind"))) {
					params2.put("needSmsRemind", request.getParameter("needSmsRemind"));
				}
				String docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
				description = XmlHelper.getElement(docXml2, "//description");
				//令牌超期
				if("TokenError".equals(description)){
					session.setAttribute("OaToken","");
					docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
				}
				logger.debug("docXml2----->" + docXml2);
				String results2 = XmlHelper.getElement(docXml2, "//result");
				logger.debug("results2----->" + results2);
				ret = true;
			}
		}
		if (ret) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	@RequestMapping(value = "/updateprocess2")
	public void updateprocess2(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			@RequestParam String workId, @RequestParam String comment, @RequestParam String[] activity, Model model) {
		logger.debug("-----enter workflow updateprocess2-----");
		boolean ret = false;
		logger.debug("workId----->" + workId);
		logger.debug("comment----->" + comment);
		logger.debug("activity----->" + activity);
		String userKey = session.getAttribute("userKey").toString();
		if (session.getAttribute("userId") != null) {
			String domainId = session.getAttribute("domainId").toString();
			String userId = session.getAttribute("userId").toString();
			String userName = session.getAttribute("userName").toString();
			//System.out.println("------------userName-------"+userName);
			String orgIdString = session.getAttribute("orgIdString").toString();

			String afterInsertTaskIds = request.getParameter("afterInsertTaskIds");
			if (afterInsertTaskIds == null) {
				afterInsertTaskIds = "";
			}
			logger.debug("afterInsertTaskIds----->" + afterInsertTaskIds);

			String commentType = request.getParameter("commentType") == null ? "0" : request
					.getParameter("commentType");
			logger.debug("commentType----->" + commentType);

			Map<String, String> params = new HashMap<String, String>();
			params.put("cmd", "workFlow_getWorkInfoByworkId");
			params.put("domain", domainId);
			params.put("workId", workId);
			params.put("userId", userId);
			String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
			String description = XmlHelper.getElement(docXml, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
			}
			
			docXml = docXml.replaceAll("&amp;", "&");
			logger.debug("docXml----->" + docXml);
			String results = XmlHelper.getElement(docXml, "//result");
			logger.debug("results----->" + results);
			if (results != null && "1".equals(results)) {
				Map<String, Object> params2 = new HashMap<String, Object>();
				params2.put("cmd", "bpm_updateProcess");
				params2.put("domain", domainId);
				params2.put("wf_work_id", XmlHelper.getElement(docXml, "//wf_work_id"));
				params2.put("wf_submitemployee_id", XmlHelper.getElement(docXml, "//wf_submitemployee_id"));
				params2.put("worktype", XmlHelper.getElement(docXml, "//worktype"));
				params2.put("workrecord_id", XmlHelper.getElement(docXml, "//workrecord_id"));
				params2.put("workprocess_id", XmlHelper.getElement(docXml, "//workprocess_id"));
				params2.put("workactivity", XmlHelper.getElement(docXml, "//workactivity"));
				params2.put("submitorg", XmlHelper.getElement(docXml, "//submitorg"));
				params2.put("isstandforwork", XmlHelper.getElement(docXml, "//isstandforwork"));
				params2.put("standforusername", XmlHelper.getElement(docXml, "//standforusername"));
				params2.put("workdeadline", XmlHelper.getElement(docXml, "//workdeadline"));
				params2.put("trantype", XmlHelper.getElement(docXml, "//trantype"));
				params2.put("emergence", XmlHelper.getElement(docXml, "//emergence"));
				params2.put("workfiletype", "<![CDATA[" + XmlHelper.getElement(docXml, "//workfiletype") + "]]>");
				params2.put("worktitle", "<![CDATA[" + XmlHelper.getElement(docXml, "//worktitle") + "]]>");
				params2.put("curIsForkTask", XmlHelper.getElement(docXml, "//isForkTask"));
				params2.put("curForkStepCount", XmlHelper.getElement(docXml, "//forkStepCount"));
				params2.put("curForkId", XmlHelper.getElement(docXml, "//forkId"));
				params2.put("fromforkActivityId", XmlHelper.getElement(docXml, "//fromforkActivityId"));
				params2.put("nextIsForkTask", XmlHelper.getElement(docXml, "//isForkTask"));
				params2.put("userId", userId);
				params2.put("userName", userName);
				params2.put("orgIdString", orgIdString);
				if (request.getParameter("needMailRemind") != null
						&& !"".equals(request.getParameter("needMailRemind"))) {
					params2.put("needMailRemind", request.getParameter("needMailRemind"));
				}
				if (request.getParameter("needSmsRemind") != null && !"".equals(request.getParameter("needSmsRemind"))) {
					params2.put("needSmsRemind", request.getParameter("needSmsRemind"));
				}
				if (request.getParameter("docTitle") != "") {
					params2.put("docTitle", request.getParameter("docTitle"));
				}
				if (request.getParameter("beginForkActivityId") != ""
						&& request.getParameter("beginForkActivityId") != null) {
					params2.put("beginForkActivityId", request.getParameter("beginForkActivityId"));
				}
				if (request.getParameter("beginForkActivityName") != ""
						&& request.getParameter("beginForkActivityName") != null) {
					params2.put("beginForkActivityName", request.getParameter("beginForkActivityName"));
				}

				params2.put("commentType", commentType);
				params2.put("comment", comment);
				params2.put("nextIsForkTask", XmlHelper.getElement(docXml, "//isForkTask"));
				params2.put("toJoinActivityId", ServletRequestUtils
						.getStringParameter(request, "toJoinActivityId", "0"));
				params2.put("afterInsertTaskIds", afterInsertTaskIds);
				String gateType = request.getParameter("gateType");
				List<Map<String, String>> nextAct = new ArrayList<Map<String, String>>();
				if (activity != null) {
					Map<String, String> map;
					for (int i = 0; i < activity.length; i++) {
						map = new HashMap<String, String>();
						logger.debug("gateType----->" + gateType);
						logger.debug("activity[" + i + "]----->" + activity[i]);
						map.put("activityId", activity[i]);
						if ("".equals(gateType) || "XOR".equals(gateType) || gateType == null) {
							logger.debug("userI=========" + request.getParameter("userId"));
							String userIds = request.getParameter("userId");
							String newUserIds = "";
							if (userIds.indexOf(",") > 0) {
								newUserIds = "$" + userIds.replace(",", "$") + "$";
							} else {
								newUserIds = userIds;
							}
							map.put("userIds", newUserIds);
							map.put("userNames", request.getParameter("userName"));
						} else {
							logger.debug("userId[" + i + "]----->" + request.getParameter("userId" + activity[i]));
							logger.debug("userName[" + i + "]----->" + request.getParameter("userName" + activity[i]));
							String userIds = request.getParameter("userId" + activity[i]);
							String newUserIds = "";
							if (userIds.indexOf(",") > 0) {
								newUserIds = "$" + userIds.replace(",", "$");
							} else {
								newUserIds = userIds;
							}
							map.put("userIds", newUserIds);
							map.put("userNames", request.getParameter("userName" + activity[i]));
						}
						nextAct.add(map);
					}
				}
				params2.put("nextAct", nextAct);
				params2.put("commentField", request.getParameter("commentField").replace(",", ""));
				params2.put("activityClass", XmlHelper.getElement(docXml, "//activityClass"));
				StringBuilder fileFieldName = new StringBuilder();
				StringBuilder fileFieldSaveName = new StringBuilder();
				String fileName = request.getParameter("_mainfile_commentacc");
				String[] fileNameArray = null;
				if(fileName != null && !"".equals(fileName)){
					fileNameArray = fileName.split("\\,");
				}
				if (fileNameArray != null && fileNameArray.length>0) {
					for (int index = 0, length = fileNameArray.length; index < length; index++) {
						fileFieldName.append(fileNameArray[index].split("\\|")[0]).append(",");
						fileFieldSaveName.append(
								fileNameArray[index].split("\\|")[1].substring(fileNameArray[index].split("\\|")[1]
										.lastIndexOf("\\") + 1)).append(",");
					}
					logger.debug("fileFieldSaveName--->" + fileFieldSaveName.toString());
					logger.debug("fileFieldSaveName--->" + fileFieldSaveName.toString());
					params2.put("commentFileShowName",fileFieldSaveName.substring(0,fileFieldSaveName.length()-1));//保存名
					params2.put("commentFileSaveName",fileFieldName.substring(0, fileFieldName.length()-1));//文件真实名称
				}				
				String docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
				description = XmlHelper.getElement(docXml2, "//description");
				//令牌超期
				if("TokenError".equals(description)){
					session.setAttribute("OaToken","");
					docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
				}
				logger.debug("docXml2----->" + docXml2);
				String results2 = XmlHelper.getElement(docXml2, "//result");
				logger.debug("results2----->" + results2);
				ret = true;
			}
		}
		if (ret) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	/** 文件办理-----结束 */

	// 撤办功能
	@RequestMapping(value = "/workfolwUndo")
	public void workfolwUndo(HttpServletRequest request,HttpServletResponse response, HttpSession session, @RequestParam String workId, Model model) {
		logger.debug("-----enter workflow workfolwUndo-----");
		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String userKey = session.getAttribute("userKey").toString();
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);
		logger.debug("workId----->" + workId);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "bpm_undo");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);

		if ("1".equals(results)) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	// 催办功能
	@RequestMapping(value = "/workflowPress")
	public void workflowPress(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow workflowPress-----");

		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String orgIdString = session.getAttribute("orgIdString") == null ? "" : session.getAttribute("orgIdString")
				.toString();

		String workId = request.getParameter("workId");
		String cbUserIds[] = request.getParameterValues("cbUserId");
		String cbTitle = request.getParameter("cbTitle");
		String cbContent = request.getParameter("cbContent");
		String needSmsRemind = request.getParameter("needSmsRemind") == null ? "0" : request
				.getParameter("needSmsRemind");
		String chooseUserId = "";
		String chooseUserName = "";
		String userKey = session.getAttribute("userKey").toString();
		logger.debug("userId----->" + userId);
		logger.debug("userName----->" + userName);
		logger.debug("orgIdString----->" + orgIdString);
		logger.debug("workId----->" + workId);
		logger.debug("cbTitle----->" + cbTitle);
		logger.debug("cbContent----->" + cbContent);
		logger.debug("needSmsRemind----->" + needSmsRemind);
		if (cbUserIds != null && cbUserIds.length > 0) {
			for (int i = 0; i < cbUserIds.length; i++) {
				logger.debug("cbUserIds[" + i + "]----->" + cbUserIds[i]);
				String[] userInfo = cbUserIds[i].split(",");
				chooseUserId += "$" + userInfo[0] + "$";
				chooseUserName += userInfo[1] + ",";
			}
			chooseUserName = chooseUserName.substring(0, chooseUserName.length() - 1);
		}
		logger.debug("chooseUserId----->" + chooseUserId);
		logger.debug("chooseUserName----->" + chooseUserName);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "bpm_press");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("userName", userName);
		params.put("chooseUserId", chooseUserId);
		params.put("chooseUserName", chooseUserName);
		params.put("title", cbTitle);
		params.put("content", cbContent);
		params.put("pressSMS", needSmsRemind);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);

		if ("1".equals(results)) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	// 加签功能
	@RequestMapping(value = "/workflowAddSign")
	public void workflowAddSign(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			Model model) {
		logger.debug("-----enter workflow workflowAddSign-----");

		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String orgIdString = session.getAttribute("orgIdString") == null ? "" : session.getAttribute("orgIdString")
				.toString();

		String workId = request.getParameter("workId");
		String chooseUserId = request.getParameter("chooseUserId");
		String chooseUserName = request.getParameter("chooseUserName");
		logger.debug("workId----->" + workId);
		// 格式：$user1$$user2$
		logger.debug("chooseUserId----->" + chooseUserId);
		chooseUserId = "$" + chooseUserId + "$";
		// 格式：username1,username2
		logger.debug("chooseUserName----->" + chooseUserName);
		logger.debug("chooseUserId----->" + chooseUserId);
		String userKey = session.getAttribute("userKey").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "bpm_addSign");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("userName", userName);
		params.put("chooseUserId", chooseUserId);
		params.put("chooseUserName", chooseUserName);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);

		if ("1".equals(results)) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	// 转办功能
	@RequestMapping(value = "/workflowTran")
	public void workflowTran(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow workflowTran-----");

		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String orgIdString = session.getAttribute("orgIdString") == null ? "" : session.getAttribute("orgIdString")
				.toString();

		String workId = request.getParameter("workId");
		String chooseUserId = request.getParameter("chooseUserId");
		System.out.println("chooseUserId------>>"+chooseUserId);
		String chooseUserName = request.getParameter("chooseUserName");
		// 转办类型：1：默认 2：转办后自动返回
		String isMustBack = request.getParameter("isMustBack") == null ? "0" : request.getParameter("isMustBack");
		// 是否有邮件提醒：0：不需要 1：需要
		String needMailRemind = request.getParameter("needMailRemind") == null ? "0" : request
				.getParameter("needMailRemind");
		//批示意见
		String comment_input = request.getParameter("comment_input");
		logger.debug("workId----->" + workId);
		// 格式：$user1$$user2$
		logger.debug("chooseUserId----->" + chooseUserId);
		if (chooseUserId != null && chooseUserId.endsWith(",")) {
			chooseUserId = chooseUserId.substring(0, chooseUserId.length() - 1);
		}
		String [] arr = chooseUserId.split(",");
		String chooseUserId1 = "";
		for (int i = 0; i < arr.length; i++) {
			chooseUserId1 += "$" + arr[i] + "$";
		}
		//chooseUserId = "$" + chooseUserId + "$";
		// 格式：username1,username2
		logger.debug("chooseUserName----->" + chooseUserName);
		logger.debug("selfRecovery----->" + isMustBack);
		logger.debug("needMailRemind----->" + needMailRemind);
		logger.debug("chooseUserId----->" + chooseUserId);
		System.out.println("chooseUserId------>>"+chooseUserId);
		System.out.println("chooseUserId1------>>"+chooseUserId1);
		System.out.println("comment_input------>>"+comment_input);
		String userKey = session.getAttribute("userKey").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "bpm_tran");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("orgIdString", orgIdString);
		params.put("userName", userName);
		params.put("chooseUserId", chooseUserId1);
		params.put("chooseUserName", chooseUserName);
		params.put("tranType", isMustBack);
		params.put("commentType", "");
		params.put("comment", comment_input);
		params.put("needMail", needMailRemind);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);

		if ("1".equals(results)) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}

	// 退回
	@RequestMapping(value = "/back")
	public String back(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow back-----");
		String domainId = session.getAttribute("domainId").toString();
		String tableId = request.getParameter("tableId");
		String recordId = request.getParameter("recordId");
		String stepCount = request.getParameter("stepCount");
		String forkId = request.getParameter("forkId");
		String forkStepCount = request.getParameter("forkStepCount");
		String isForkTask = request.getParameter("isForkTask");
		String workId = request.getParameter("workId");
		String userKey = session.getAttribute("userKey").toString();
		logger.debug("tableId----->" + tableId);
		logger.debug("recordId----->" + recordId);
		logger.debug("stepCount----->" + stepCount);
		logger.debug("forkId----->" + forkId);
		logger.debug("forkStepCount----->" + forkStepCount);
		logger.debug("isForkTask----->" + isForkTask);
		logger.debug("workId----->" + workId);

		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getFlowedActivity_workFlow");
		params.put("domain", domainId);
		params.put("tableId", tableId);
		params.put("recordId", recordId);
		params.put("stepCount", stepCount);
		params.put("curIsForkTask", isForkTask);
		params.put("curForkStepCount", forkStepCount);
		params.put("curForkId", forkId);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		logger.debug("docXml----->" + docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("workId", workId);
		}
		return "/workflow/workflow_back";
	}

	// 退回处理
	@RequestMapping(value = "/backprocess")
	public void backprocess(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("-----enter workflow backprocess-----");

		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();

		String workId = request.getParameter("workId");
		logger.debug("workId----->" + workId);

		String commentField = request.getParameter("commentField");
		logger.debug("commentField----->" + commentField);

		String comment = request.getParameter("comment");
		logger.debug("comment----->" + comment);

		String activityId = request.getParameter("activity");
		logger.debug("activityId----->" + activityId);
		String userKey = session.getAttribute("userKey").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "workFlow_getWorkInfoByworkId");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			Map<String, Object> params2 = new HashMap<String, Object>();
			params2.put("cmd", "workFlow_back");
			params2.put("domain", domainId);
			params2.put("userId", userId);
			params2.put("userName", userName);

			params2.put("worktable_id", XmlHelper.getElement(docXml, "//workInfo/worktable_id"));
			params2.put("workrecord_id", XmlHelper.getElement(docXml, "//workInfo/workrecord_id"));
			params2.put("wf_work_id", XmlHelper.getElement(docXml, "//workInfo/wf_work_id"));
			params2.put("workprocess_id", XmlHelper.getElement(docXml, "//workInfo/workprocess_id"));
			params2.put("worktype", XmlHelper.getElement(docXml, "//workInfo/worktype"));
			params2.put("workmainlinkfile", "<![CDATA[" + XmlHelper.getElement(docXml, "//workInfo/workmainlinkfile")
					+ "]]>");
			params2.put("worksubmitperson", XmlHelper.getElement(docXml, "//workInfo/worksubmitperson"));
			params2.put("worksubmittime", XmlHelper.getElement(docXml, "//workInfo/worksubmittime"));
			params2.put("workactivity", XmlHelper.getElement(docXml, "//workInfo/workactivity"));
			params2.put("workcurstep", XmlHelper.getElement(docXml, "//workInfo/workcurstep"));
			params2.put("standforuserid", XmlHelper.getElement(docXml, "//workInfo/standforuserid"));
			params2.put("isstandforwork", XmlHelper.getElement(docXml, "//workInfo/isstandforwork"));
			params2.put("workstepcount", XmlHelper.getElement(docXml, "//workInfo/workstepcount"));

			if ("0".equals(activityId)) {// 退回发起人
				params2.put("backTotype", "0");
				params2.put("backToStep", "");
				params2.put("backToActivityId", "");
				params2.put("backToActivityName", "");
				params2.put("backToUserId", "");
				params2.put("backToUserName", "");
				// 新添加
				params2.put("backToForkStepCount", "");
			} else {
				params2.put("backTotype", "1");
				String[] pams = activityId.split(";");
				params2.put("backToStep", pams[2]);
				params2.put("backToActivityId", pams[0]);
				params2.put("backToActivityName", pams[1]);
				params2.put("backToUserId", pams[3]);
				params2.put("backToUserName", pams[4]);
				// 新添加
				params2.put("backToForkStepCount", pams[5]);
			}

			params2.put("backTocomment", comment);
			params2.put("include_commField", commentField);

			// 接口新添加参数
			params2.put("curIsForkTask", XmlHelper.getElement(docXml, "//workInfo/isForkTask"));
			params2.put("curForkStepCount", XmlHelper.getElement(docXml, "//workInfo/forkStepCount"));
			params2.put("curForkId", XmlHelper.getElement(docXml, "//workInfo/forkId"));

			// 邮件提醒范围0:退回环节经办人1:所有办理人
			params2.put("backMailRange", request.getParameter("backMailRange"));
			// 是否短信提醒 0：不提醒 1：提醒
			params2.put("needSmsRemind", request.getParameter("needSmsRemind"));
			// 是否短信提醒 0：不提醒 1：提醒
			params2.put("needMailRemind", request.getParameter("needMailRemind"));

			String docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
			description = XmlHelper.getElement(docXml2, "//description");
			//令牌超期
			if("TokenError".equals(description)){
				session.setAttribute("OaToken","");
				docXml2 = WebServiceUtils.getWebServiceDataNoReplace(params2,request,userKey);
			}
			logger.debug("docXml2----->" + docXml2);
			String results2 = XmlHelper.getElement(docXml2, "//result");

			if ("1".equals(results2)) {
				printResult(response, G_SUCCESS, "[]");
			} else {
				printResult(response, G_FAILURE, "[]");
			}
		} else {
			printResult(response, G_FAILURE, "[]");
		}
	}
	
	/**
	 * 新发起车辆申请流程表单详细信息
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/getVoitureNewInfo")
	public String getVoitureInfo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("--------车辆流程开始-------->>");
		String domainId = session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String orgName = session.getAttribute("orgName").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> params = new HashMap<String, String>();
		params.put("cmd", "getVoitureNewInfo");
		params.put("domain", domainId);
		params.put("userId", userId);
		params.put("username", userName);
		params.put("orgId", orgId);
		params.put("orgname", orgName);
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceData(params,request,userKey);
		}
		
		System.out.println("---->>>----"+docXml);
		String results = XmlHelper.getElement(docXml, "//result");
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			logger.debug("--------车辆流程结束-------->>");
			return "/workflow/newcarprocess";
		} else {
			model.addAttribute("selVoiture", "1");
			return "redirect:/workflow/listflow.controller";
		}
	}
	
	/**
	 * 保存车辆表单信息
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sendVoitureApply")
	public String sendVoitureApply(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("--------保存车辆表单开始-------->>");
		String voitureId = request.getParameter("voitureId");
		logger.debug("--------voitureId-------->>"+voitureId);
		String startDateTime = request.getParameter("startDateTime");
		String endDateTime = request.getParameter("endDateTime");
		String startDate = startDateTime.substring(0,10);
		String startTime = startDateTime.substring(11,16);
		String endDate = endDateTime.substring(0,10);
		String endTime = endDateTime.substring(11,16);
		String domainId = session.getAttribute("domainId").toString();
		String orgId = request.getParameter("orgId");
		String orgName = request.getParameter("orgName");
		String empId = request.getParameter("empId");
		String empName = request.getParameter("empName");
		String destination = request.getParameter("destination");
		String personNum = request.getParameter("personNum");
		String genchePerson = request.getParameter("genchePerson");
		String motorMan = request.getParameter("motorMan");
		String voitureStyle = request.getParameter("voitureStyle");
		String reason = request.getParameter("reason");
		String remark = request.getParameter("remark");
		String userKey = session.getAttribute("userKey").toString();
		Map<String, String> voitureParam = new HashMap<String, String>();
		voitureParam.put("cmd", "sendVoitureApply");
		voitureParam.put("domain", domainId);
		voitureParam.put("voitureId", voitureId);
		voitureParam.put("orgId", orgId);
		voitureParam.put("orgName", orgName);
		voitureParam.put("empId", empId);
		voitureParam.put("empName", empName);
		voitureParam.put("destination", destination);
		voitureParam.put("personNum", personNum);
		voitureParam.put("genchePerson", genchePerson);
		voitureParam.put("motorMan", motorMan);
		voitureParam.put("startDate", startDate);
		voitureParam.put("startTime", startTime);
		voitureParam.put("endDate", endDate);
		voitureParam.put("endTime", endTime);
		voitureParam.put("voitureStyle", voitureStyle);
		voitureParam.put("reason", reason);
		voitureParam.put("remark", remark);
		String voitureDocXml = WebServiceUtils.getWebServiceData(voitureParam,request,userKey);
		String description = XmlHelper.getElement(voitureDocXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			voitureDocXml = WebServiceUtils.getWebServiceData(voitureParam,request,userKey);
		}
		
		String voitureResult = XmlHelper.getElement(voitureDocXml, "//result");
		logger.debug("--------voitureDocXml-------->>"+voitureDocXml);
		String infoId = null;
		if(voitureResult != null && "1".equals(voitureResult)){
			infoId = XmlHelper.getElement(voitureDocXml, "//infoid");
			System.out.println("----infoId----->"+infoId);
			try {
				response.setContentType("text/html;charset=UTF-8");
				response.getWriter().write(infoId);
				response.getWriter().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("保存表单信息出错!" + voitureDocXml);
		}
		logger.debug("--------保存表单结束-------->>");
		return null;
	}
	
	/**
	 *  发送流程(老)
	 * 
	 * @param session
	 * @param workId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/sendFlow")
	public String sendFlow(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		System.out.println("------开始------>>");
		String infoId = request.getParameter("infoId");
		String processId = request.getParameter("processId");
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String userKey = session.getAttribute("userKey").toString();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "workflow_getFirstNextList");
		params.put("domain", domainId);
		params.put("processId", processId);
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("--------docXml-------->>"+docXml);
		if (results != null && "1".equals(results)) {
			model.addAttribute("docXml", docXml);
			model.addAttribute("businessId", infoId);
			if (request.getParameter("fileTitle") != null) {
				model.addAttribute("docTitle", request.getParameter("fileTitle"));
			}
		}
		System.out.println("------结束------>>");
		return "/workflow/firstsend";
	}
	
	/**
	 * 发送流程申请(新)
	 */
	@RequestMapping(value = "/sendezFlow")
	public String sendezFlow(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		System.out.println("------开始------>>");
		String infoId = request.getParameter("infoId");
		String processId = request.getParameter("processId");
		String userId = session.getAttribute("userId").toString();
		String domainId = session.getAttribute("domainId").toString();
		String orgId = session.getAttribute("orgId").toString();
		String orgIdString = session.getAttribute("orgIdString").toString();
		String userKey = session.getAttribute("userKey").toString();
		String moduleId = request.getParameter("moduleId");
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "ezflow_findNextActivityWithStart");
		params.put("domain", domainId);
		params.put("processId", processId);
		if (infoId == null) {
			params.put("businessKey", "2830443");
			model.addAttribute("businessId", "2830443");
		} else {
			params.put("businessKey", infoId);
		}
		params.put("userId", userId);
		params.put("orgId", orgId);
		params.put("orgIdString", orgIdString);
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String description = XmlHelper.getElement(docXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		}
		
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("--------docXml-------->>"+docXml);
		if (results != null && "1".equals(results)) {
			model.addAttribute("businessId", infoId);
			model.addAttribute("docXml", docXml);
			if (request.getParameter("fileTitle") != null) {
				model.addAttribute("docTitle", request.getParameter("fileTitle"));
			}
		}
		model.addAttribute("moduleId", moduleId);
		System.out.println("------结束------>>");
		return "/workflow/firstezflowsend";
	}
	/**
	 * 验证该时间段车辆是否被申请
	 */
	@RequestMapping(value = "/checkVoitureApplyed")
	public String checkVoitureApplyed(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		logger.debug("--------验证开始-------->>");
		String voitureId = request.getParameter("voitureId");
		logger.debug("--------voitureId-------->>"+voitureId);
		String startDateTime = request.getParameter("startDateTime");
		logger.debug("--------startDateTime-------->>"+startDateTime);
		String endDateTime = request.getParameter("endDateTime");
		logger.debug("--------endDateTime-------->>"+endDateTime);
		String startDate = startDateTime.substring(0,10);
		String startTime = startDateTime.substring(11,16);
		String endDate = endDateTime.substring(0,10);
		String endTime = endDateTime.substring(11,16);
		String userKey = session.getAttribute("userKey").toString();
		//新发起车辆申请流程表单时判断申请时间是否已存在
		Map<String, String> checkParam = new HashMap<String, String>();
		checkParam.put("cmd", "checkVoitureApplyed");
		checkParam.put("voitureId", voitureId);
		checkParam.put("startDate", startDate);
		checkParam.put("startTime", startTime);
		checkParam.put("endDate", endDate);
		checkParam.put("endTime", endTime);
		String checkDocXml = WebServiceUtils.getWebServiceData(checkParam,request,userKey);
		String description = XmlHelper.getElement(checkDocXml, "//description");
		//令牌超期
		if("TokenError".equals(description)){
			session.setAttribute("OaToken","");
			checkDocXml = WebServiceUtils.getWebServiceData(checkParam,request,userKey);
		}
		
		String checkResult = XmlHelper.getElement(checkDocXml, "//result");
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(checkResult);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("--------结束-------->>");
		return null;
	}
	
	/***
	 * 阅件功能
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 */
	@RequestMapping(value = "/workflowSelfSend")
	public void workflowSelfSend(HttpServletRequest request, HttpServletResponse response, HttpSession session,Model model) {
		logger.debug("-----发送阅件开始-----");

		String domainId = session.getAttribute("domainId") == null ? "0" : session.getAttribute("domainId").toString();
		String userId = session.getAttribute("userId") == null ? "" : session.getAttribute("userId").toString();
		String userName = session.getAttribute("userName") == null ? "" : session.getAttribute("userName").toString();
		String orgIdString = session.getAttribute("orgIdString") == null ? "" : session.getAttribute("orgIdString").toString();
		String userKey = session.getAttribute("userKey").toString();
		String workId = request.getParameter("workId");
		String chooseUserId = request.getParameter("chooseUserId");
		String chooseUserName = request.getParameter("chooseUserName");
		logger.debug("workId----->" + workId);
		// 格式：$user1$$user2$
		logger.debug("chooseUserId----->" + chooseUserId);
		chooseUserId = "$" + chooseUserId + "$";
		// 格式：username1,username2
		logger.debug("chooseUserName----->" + chooseUserName);
		logger.debug("chooseUserId----->" + chooseUserId);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cmd", "bpm_tranRead");
		params.put("domain", domainId);
		params.put("workId", workId);
		params.put("userId", userId);
		params.put("userName", userName);
		params.put("orgIdString", orgIdString);
		params.put("chooseUserId", chooseUserId);
		params.put("chooseUserName", chooseUserName);
		params.put("readType", "sendread");
		params.put("sendType", "cover");
		params.put("showSmsRemind", "false");
		
		String docXml = WebServiceUtils.getWebServiceDataNoReplace(params,request,userKey);
		String results = XmlHelper.getElement(docXml, "//result");
		logger.debug("results----->" + results);
		
		if ("1".equals(results)) {
			printResult(response, G_SUCCESS, "[]");
		} else {
			printResult(response, G_FAILURE, "[]");
		}
		logger.debug("-----发送阅件结束-----");
	}
}
