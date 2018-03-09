package com.whir.ezflow.actionsupport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.http.HttpSession;
import org.activiti.engine.impl.persistence.entity.WhirEzFlowDesignerEntity; 
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import com.whir.common.db.Dbutil;
import com.whir.common.util.CommonUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezflow.el.SyntaxExtension;
import com.whir.ezflow.util.EzFlowFinals;
import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezoffice.bpm.bd.BPMFormInfoBD;
import com.whir.ezoffice.bpm.bd.BPMOutDataSourceBD;
import com.whir.ezoffice.bpm.bd.BPMPackageBD;
import com.whir.ezoffice.bpm.bd.BPMProcessBD;
import com.whir.ezoffice.bpm.po.BPMFlowDeploymentPO;
import com.whir.ezoffice.bpm.po.BPMOutDataSourcePO;
import com.whir.ezoffice.dossier.bd.DossierBD;
import com.whir.ezoffice.ezform.bd.EzFormBD;
import com.whir.ezoffice.workflow.newBD.ModuleBD;
import com.whir.ezoffice.workflow.vo.ModuleVO;
import com.whir.i18n.Resource;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.bd.rolemanager.RoleBD;
import com.whir.org.manager.bd.ManagerBD; 
import com.whir.service.api.ezflowservice.EzFlowDesignerService;
import com.whir.service.api.ezflowservice.EzFlowMainService;
import com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.tree.TreeBuilder;

/**
 *  ezFLOW 流程设置列表
 * @author wanggl
 *
 */
public class EzFlowProcessAction extends BaseActionSupport{
	private static Logger logger = Logger.getLogger(EzFlowProcessAction.class.getName());
	
	//模块类code
	public  static final  String  MODULE_CODE="EzFlowProcessAction";
	
	private String processName;
	private String packageName;
	
	String moduleCode = "oa_workflow_set";//模块编码
    String moduleName = "流程设置";//子模块
    String oprType = "";//操作类型
    String oprContent = "";// 操作内容
    com.whir.ezoffice.security.log.bd.LogBD logBD=new com.whir.ezoffice.security.log.bd.LogBD();
    java.util.Date startDate = new java.util.Date();
    
	private String local = "zh_CN";
	
	
	//是否是子流程  0: 不是   1： 是
	private String subType="0";
	
	private int moduleId=1;
	private ModuleVO moduleVO=null;
	private String  rightCode="02*02*02";
	 
	private String formType="0"; 
	 
	/**
	 * 
	 * @return
	 */
	public String  ezFlowList(){
		logger.debug("开始进入ezFLOW的流程列表");
		
		this.initModuleVO();
		
		/**
		 * 检验是否有权限打开， 02*02*02 为工作流设置权限code
		 * 新增页面需要验证 验证码 ，所以第一个参数传空 或者null
		 */
		if(!this.judgeCallRight("", rightCode)){
			return  BaseActionSupport.NO_RIGHTS;
		}
		
		/*com.whir.service.api.ezflowservice.EzFlowPackageService flowService = new com.whir.service.api.ezflowservice.EzFlowPackageService();
		List<Map> packageList = flowService.getPackageList(new HashMap());
		for (int i = 0; i < packageList.size(); i++) {
			processPackageList.add(new String[] {
					(String) packageList.get(i).get("PACKAGEID"),
					(String) packageList.get(i).get("PACKAGENAME") });
		}*/
		
		//request.setAttribute("allPackageList", allPackageList);
		//取所有的分类
		setPackageList();
		logger.debug("结束进入ezFLOW的流程列表");
		return "ezFlowList";
	}
	
	/**
	 * 取所有的分类
	 */
	private void setPackageList(){
		// 获取所属分类列表
	    List processPackageList = new ArrayList();
		BPMPackageBD  bd=new BPMPackageBD();
		String domainId="0";
		List allPackageList=bd.getPackageList(""+moduleId, domainId); 
		if(allPackageList!=null&&allPackageList.size()>0){
			for(int i=0;i<allPackageList.size();i++){
				Object [] obj=(Object[])allPackageList.get(i);
				processPackageList.add(new Object[]{obj[2],obj[1]}); 
			}
		}
		request.setAttribute("processPackageList", processPackageList);
	}
	
	
	public String list() {
		logger.debug("开始取ezFLOW流程列表数据");
		this.initModuleVO();
		local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		HttpSession session=request.getSession();
		EzFlowDesignerService flowService = new EzFlowDesignerService();
		Map map = new HashMap();
		// 分页数
		int pageSize = com.whir.common.util.CommonUtils.getUserPageSize(request);
		// 开始位数
		String off = "0";
		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
			off = ((currentPage - 1) * pageSize) + "";
		}
		map.put("pageSize", String.valueOf(pageSize));
		//map.put("off", off); 
		map.put("start", off);
		
		logger.debug("off:"+off);
		
		/*
		 * orderByFieldName，用来排序的字段
		 */
		String orderByFieldName = request.getParameter("orderByFieldName") != null ? request.getParameter("orderByFieldName") : "";
		/*
		 * orderByType，排序的类型
		 */
		String orderByType = request.getParameter("orderByType") != null ? request.getParameter("orderByType") : "";

		if (orderByFieldName != null && !"".equals(orderByFieldName)) {	
			map.put("processName_orderBy",orderByType);
		}
		
		map.put("createUserId", session.getAttribute("userId").toString());
		map.put("createOrgId", session.getAttribute("orgId").toString());
		map.put("orgIdString", session.getAttribute("orgIdString").toString());
		map.put("userAccount", session.getAttribute("userAccount").toString());
		
		//海澜用 
		map.put("iswhirchoosed", request.getParameter("iswhirchoosed"));
 
		if (request.getParameter("processName") != null) {		
			String searchName=request.getParameter("processName");
			if(searchName.equals("_")){
				searchName="[_]";
			} 
			map.put("processName", searchName);
		}
		if (request.getParameter("packageName") != null) {
			map.put("packageName", request.getParameter("packageName")); 
		}
		//是不是子流程
		map.put("subType", subType);
		//哪个模块的流程
		map.put("moduleId", moduleId);

		logger.debug("start off:"+map.get("start"));
		
		map.put("rightcode", rightCode);
 
		// 引用流程列表
	    List<WhirEzFlowDesignerEntity> list = flowService.list(map);
	    if(list.size()<=0&&currentPage>1){
	    	off = ((currentPage - 2) * pageSize) + "";
	    	map.put("start", off);
	    	list =flowService.list(map);
	    } 
		long count = flowService.listCount(map);
		long pageCount = count / pageSize;
		long mod = count % pageSize;
		if (mod != 0) {
			pageCount = pageCount + 1;
		}
		List<Object[]> resultList=new ArrayList<Object[]>();
		for(WhirEzFlowDesignerEntity obj:list){
			resultList.add(new Object[]{obj.getId(),obj.getProcessId(),obj.getPackageName(),obj.getProcessName(),obj.getProcessScopeNames(),
					                    obj.getCreateUserName(),""+obj.getSort(),""+obj.getIsdeployed()});
		}
		JacksonUtil util = new JacksonUtil();
		String[] fields = new String[] { "id","processId","packageName","processName","processScopeNames","createUserName","sort","isdeployed" };
		//"buttonId" 为生成验证码的列名，    this.MODULE_CODE  为模块code  默认为本类的名
		String json = util.writeArrayJSON(fields, resultList,"id",this.MODULE_CODE);
 
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + count + "},data:" + json + "}";
		printResult(this.G_SUCCESS,json);
		logger.debug("结束取ezFLOW流程列表数据");
		return null;
	}
	
	/**
	 * 进入ezFLOW流程版本列表
	 */
	public String  ezFlowProcessVersion(){
		logger.debug("开始进入ezFLOW流程版本列表");
		return "ezFlowProcessVersion";
	}
	
	/**
	 * 获取ezFLOW流程版本列表数据
	 */
	public String getEzFlowProcessVersion(){
		/*
         * pageSize每页显示记录数，即list.jsp中分页部分select中的值
         */
        int pageSize = com.whir.common.util.CommonUtils.getUserPageSize(request);
        /*
         * orderByFieldName，用来排序的字段
         */
        String orderByFieldName = request.getParameter("orderByFieldName") != null ? request.getParameter("orderByFieldName"): "";
        /*
         * orderByType，排序的类型
         */
        String orderByType = request.getParameter("orderByType") != null ? request.getParameter("orderByType"): "";
        /*
         * currentPage，当前页数
         */
        int currentPage = 0;
        if (request.getParameter("startPage") != null) {
            currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        
        String processId =request.getParameter("processId")==null?"":request.getParameter("processId");
        logger.debug("processId--getEzFlowProcessVersion--->"+processId);
        
        StringBuffer viewSQL =new StringBuffer();
        StringBuffer fromSQL =new StringBuffer();
        StringBuffer whereSQL =new StringBuffer();
        StringBuffer orderBy =new StringBuffer();
        
        Map varMap = new HashMap();
        
        viewSQL.append("id_, key_, name_, version_, deployment_userName, deployment_date ");
        fromSQL.append("ez_flow_re_procdef left join EZ_BPMPOOL_FLOWDEPLOYMENT on deployment_id_ =deployment_id ");
        whereSQL.append("where key_ =:processId ");
        orderBy.append(" order by version_ desc ");
        
        varMap.put("processId", ""+processId+"");
        
        logger.debug("viewSQL--getEzFlowProcessVersion--->" + viewSQL);
        logger.debug("fromSQL--getEzFlowProcessVersion--->" + fromSQL);
        logger.debug("whereSQL-getEzFlowProcessVersion---->" + whereSQL);
        logger.debug("orderBy--getEzFlowProcessVersion--->" + orderBy);

        Page page = PageFactory.getJdbcPage(viewSQL.toString(), fromSQL.toString(), whereSQL.toString(), orderBy.toString());
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        /*
         * page.setVarMap(varMap);若没有查询条件，可略去此行
         */
        page.setVarMap(varMap);
        /*
         * list，数据结果
         */
        List<Object[]> list = page.getResultList();
        
        /*
         * pageCount，总页数
         */
        int pageCount = page.getPageCount();
        /*
         * recordCount，总记录数
         */
        int recordCount = page.getRecordCount();
        /*
         * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
         */
        
        String[] arr = {"id", "key", "name", "version", "deployment_userName", "deployment_date"};
        
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, list, "id", "EzFlowProcessAction");
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount+ "},data:" + json + "}";
        
        logger.debug("[json]----->"+json);
        printResult(G_SUCCESS, json);
        
		return null;
	}
	
	/**
	 * 新增页面
	 * @return
	 */
	public String add(){
		logger.debug("开始进入ezFLOW新增页面");
		
		this.initModuleVO();
		/**
		 * 检验是否有权限打开， 02*02*02 为工作流设置权限code
		 * 新增页面需要验证 验证码 ，所以第一个参数传空 或者null
		 */
		if(!this.judgeCallRight("", rightCode)){
			return  BaseActionSupport.NO_RIGHTS;
		}
		logger.debug("结束进入ezFLOW新增页面");
		return "add"; 
	}
	
	/**
	 * 
	 * @return
	 */
	public String modify(){
		logger.debug("开始进入ezFLOW修改页面");
		this.initModuleVO();
		/**
		 * 检验是否有权限打开， 02*02*02 为工作流设置权限code
		 * 新增页面需要验证 验证码 ，所以第一个参数传空 或者null
		 */
		if(!this.judgeCallRight("", rightCode)){
			return  BaseActionSupport.NO_RIGHTS;
		}
		logger.debug("结束进入ezFLOW修改页面");
		return "modify"; 
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public String save() throws JDOMException, IOException{
		logger.debug("开始保存ezFLOW流程设置"); 
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		this.initModuleVO();
		//自由流程  半自由流程  发送时保存
		String isTempSet=request.getParameter("isTempSet")==null?"":request.getParameter("isTempSet").toString();
		
		ManagerBD managerBD = new ManagerBD();
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		if (!isTempSet.equals("1")&&!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}
		
		String xml = request.getParameter("xmlStr");
		String id = request.getParameter("recordId");
		logger.debug("+++++++++++++++++++++++++++++++++++++++++++++++==");
		logger.debug(xml);
		xml=xml.replaceFirst("xmlns-default", "xmlns");
		
		/* try {
	            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
	            FileWriter writer = new FileWriter("D:\\ooo.text", true);
	            writer.write(xml);
	            writer.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        */

		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		/**
		 * 添加流程设置
		 * 
		 * @param map
		 *            ，其中的key param 如下： createOrgId 当前登录人组织id createUserId
		 *            当前登录人id createUserName 当前登录人名称 designerXml 流程定义xml文件
		 *            domainId 当前登录人所在域id isdeployed 是否部署, 0:未部署；1：已部署。默认0
		 *            packageId 流程分类Id,以逗号分割 processId 流程定义id processName 流程定义名
		 *            processScopeIds 流程使用范围id串 processScopeNames 流程使用范围名称串 sort
		 *            排序号
		 * @return 返回添加后的主键值
		 */
		String userId = (String) request.getSession().getAttribute("userId").toString();
		String orgId = (String) request.getSession().getAttribute("orgId").toString();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();
		String username = (String) request.getSession().getAttribute("userName").toString();

		org.jdom.Document doc = new SAXBuilder().build(new java.io.StringReader(xml));
		org.jdom.Element defineElement = doc.getRootElement();

		org.jdom.Element processElement = defineElement.getChild("process", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
		String processId = processElement.getAttribute("id").getValue();
		String packageId = processElement.getAttributeValue("processPackage", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		String packageName = processElement.getAttributeValue("processPackageName", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		String processName = processElement.getAttribute("name").getValue();
		String processScopeIds = processElement.getAttributeValue("processUserScope", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		String processScopeNames = processElement.getAttributeValue("processUserScopeName", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		/*
	     whirProcesssExtensionAttribute.add("processCanReadEmp");//办理查阅查看人ID
	     whirProcesssExtensionAttribute.add("processCanReadEmpName");//办理查阅查看人显示
	     whirProcesssExtensionAttribute.add("processCanModifyEmp");//办理查阅维护人ID
	     whirProcesssExtensionAttribute.add("processCanModifyEmpName");//办理查阅维护人显示
	     whirProcesssExtensionAttribute.add("processAdministrator");//流程管理员ID
	     whirProcesssExtensionAttribute.add("processAdministratorName");//流程管理员显示
		 */
		
		String processCanReadIds = processElement.getAttributeValue("processCanReadEmp", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processCanModifyIds = processElement.getAttributeValue("processCanModifyEmp", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processAdministratorIds = processElement.getAttributeValue("processAdministrator", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String formKey = processElement.getAttributeValue("formKey", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		formType = processElement.getAttributeValue("formType", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String mobileStatus = processElement.getAttributeValue("mobileStatus", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String mobilePhoneStatus = processElement.getAttributeValue("mobilePhoneStatus", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		logger.debug("新增加liucheng   mobilePhoneStatus0  :"+mobilePhoneStatus); 
		
		String processType = processElement.getAttributeValue("processType", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));

		// 找所有节点的formkey
		List userTaskElementList = processElement.getChildren(
						"userTask", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
		String taskFormKey = "";
		for (int j = 0; j < userTaskElementList.size(); j++) {
			org.jdom.Element userTaskElement = (org.jdom.Element) userTaskElementList.get(j);
			String formKeyTemp = userTaskElement.getAttributeValue("formKey", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
			if (taskFormKey.indexOf("," + formKeyTemp + ",") < 0 && taskFormKey.indexOf(formKeyTemp + ",") != 0) {
				taskFormKey += (formKeyTemp + ",");
			}
		}
		if (!"".equals(taskFormKey)) {
			taskFormKey = taskFormKey.substring(0, taskFormKey.length() - 1);
		}
		
		//相关联的子流程
	    List<String>  subProcessKeyList=new ArrayList<String>(); 
		//子流程
		List subProcessList = processElement.getChildren("subProcess", Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
 
		if (subProcessList != null && subProcessList.size() > 0) {
			org.jdom.Element subProcessElement = null;
			for (int i = 0; i < subProcessList.size(); i++) {
				subProcessElement = (org.jdom.Element) subProcessList.get(i);
				// 子流程引用的流程key
				String subProcessKey = subProcessElement.getAttributeValue("subProcessKey", Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
				subProcessKeyList.add(subProcessKey); 
			}
		}
		
		String repeatStr=getRepeatedKey(subProcessKeyList); 
		if(!repeatStr.equals("")){
			logger.error("设置中有多个节点引用同一个子流程。");
			this.ezflowPrint("<xml><result>-2</result><message>设置中有多个节点引用同一个子流程。</message></xml>");
			return null; 
		} 
		if(subProcessKeyList!=null&&subProcessKeyList.size()>0){
			String deleteStr=getDeleteProcessKey(subProcessKeyList); 
			if(!deleteStr.equals("")){
				deleteStr=deleteStr.substring(0,deleteStr.length()-1);
				logger.error("有删除的子流程，流程ID为："+deleteStr);
				this.ezflowPrint("<xml><result>-3</result><message>有删除的子流程，流程ID为："+deleteStr+"</message></xml>");
				return null; 
			} 
		}
		
		Map m = new HashMap();

		m.put("createOrgId", orgId);
		m.put("createUserId", userId);
		m.put("createUserName", username);
		m.put("designerXml", xml);
		m.put("domainId", domainId);
		m.put("packageId", packageId);
		m.put("packageName", packageName);
		m.put("processId", processId);
		// 新加
		m.put("subProcessKeyList", subProcessKeyList);
		m.put("formCode", formKey);
		m.put("activityCodes", taskFormKey);
		m.put("processName", processName);
		m.put("processScopeIds", processScopeIds);
		m.put("processScopeNames", processScopeNames);
		m.put("processCanReadIds", processCanReadIds); 
		m.put("processCanModifyIds", processCanModifyIds); 
		m.put("processAdministratorIds", processAdministratorIds); 
		m.put("mobileStatus", mobileStatus);
		m.put("mobilePhoneStatus", mobilePhoneStatus);
		
		logger.debug("新增加liucheng   mobilePhoneStatus  :"+mobilePhoneStatus); 
		logger.debug("新增加  mobileStatus  :"+mobileStatus); 
		logger.debug("id:"+id); 
		
		m.put("subType", subType);
		m.put("formType", formType);
		m.put("processType", processType);
		
		logger.debug("id:"+id); 
		
		if (id != null && !"".equals(id) && !"-1".equals(id)) {
			m.put("id", id);

			Map queryMap = new HashMap();
			queryMap.put("id", id);
			queryMap.put("processId", processId);
			queryMap.put("processName", processName);
			if (flowService.checkProcessRepeat(queryMap)) {
				logger.debug("流程名或者流程ID重复1:"+id); 
				this.ezflowPrint("<xml><result>-1</result><message>流程名或者流程ID重复</message></xml>");
				return null;
			}
			/*
			 * com.whir.service.api.ezflowservice.EzFlowDesignerService
			 * flowService1 = new
			 * com.whir.service.api.ezflowservice.EzFlowDesignerService(); Map
			 * m1 = new HashMap(); m1.put("id", id); WhirEzFlowDesignerEntity
			 * entity = flowService.get(m1); m.put("sort", entity.getSort());
			 * m.put("isdeployed", entity.getIsdeployed());
			 */
			id = flowService.update(m);
			
			logger.debug("flowService.update(m)完成"); 
			oprType = "2";
			oprContent = "修改流程：" + processName;//
		} else {
			logger.debug("新增开始 "); 
			// 新增传
			m.put("isdeployed", 0);
			m.put("sort", "1");
			
			oprType = "1";
			oprContent = "新建流程：" + processName;//
			
			Map queryMap = new HashMap();
			queryMap.put("processId", processId);
			queryMap.put("processName", processName);
			if(!isTempSet.equals("1")){
				if (flowService.checkProcessRepeat(queryMap)) {
					ezflowPrint("<xml><result>-1</result><message>流程名或者流程ID重复</message></xml>");
					return null;
				} 
			}
            if(isTempSet.equals("1")){
             	com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService defService = 
            			new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService(); 
             	Map resultMap=defService.deployByXML(processId, xml);
             	String new_processDefinitionId=resultMap.get("processDefinitionId")==null?"":resultMap.get("processDefinitionId").toString();
             	this.ezflowOutputPrint("<xml><result>-10</result><recordId>" + id + "</recordId><processDefinitionId>" + new_processDefinitionId + "</processDefinitionId></xml>");
             	return null;
            }else{
            	id = flowService.insert(m);      	
            }
            logger.debug("新增结束"); 
		}
		logger.debug("save 完成"); 

		this.ezflowOutputPrint("<xml><result>1</result><recordId>" + id + "</recordId></xml>");
		
	    java.util.Date endDate = new java.util.Date();
		logBD.log(request.getSession(true).getAttribute("userId").toString(),
				request.getSession(true).getAttribute("userName").toString(), 
				request.getSession(true).getAttribute("orgName").toString(), moduleCode,
				moduleName, startDate, endDate, oprType, oprContent,
				request.getRemoteAddr(), request.getSession(true).getAttribute("domainId").toString());
		
		logger.debug("结束保存ezFLOW流程设置"); 
		return null;
	}
	
	/**
	 * 保存外部数据源配置
	 * @return
	 */
	public String saveOutDataSource(){
		String ds =request.getParameter("ds");
		logger.debug("ds----->"+ds);
		
		String processId =request.getParameter("processId");
		logger.debug("processId----->"+processId);
		
		String taskId =request.getParameter("taskId");
		logger.debug("taskId----->"+taskId);
		
		String isProcessSet =request.getParameter("isProcessSet");
		logger.debug("isProcessSet----->"+isProcessSet);
		
		Long flag = new Long(-1);
		
		//办理中配置外部数据源-----开始
		String outDataSourceSql  =request.getParameter("outDataSourceSql");
		logger.debug("outDataSourceSql----->"+outDataSourceSql);
		
		String outDataSourceFields ="";
		String[] outDataSourceFieldsNo =request.getParameterValues("outDataSourceFieldsNo");
		String[] outDataSourceField =request.getParameterValues("outDataSourceFields");
		if(outDataSourceFieldsNo !=null && outDataSourceFieldsNo.length >0 && outDataSourceField !=null && outDataSourceField.length >0){
			for(int i=0;i<outDataSourceField.length;i++){
				logger.debug("outDataSourceField----->"+outDataSourceField[i]);
				logger.debug("outDataSourceFieldNo----->"+outDataSourceFieldsNo[i]);
				if(!CommonUtils.isEmpty(outDataSourceFieldsNo[i]) && !CommonUtils.isEmpty(outDataSourceField[i])){
					outDataSourceFields += outDataSourceFieldsNo[i] +"|"+ outDataSourceField[i] +",";
				}
			}
			
			logger.debug("outDataSourceFields--1--->"+outDataSourceFields);
			if(outDataSourceFields.endsWith(",")){
				outDataSourceFields =outDataSourceFields.substring(0, outDataSourceFields.length()-1);
			}
			logger.debug("outDataSourceFields--2--->"+outDataSourceFields);
		}
		
		if(!CommonUtils.isEmpty(ds) && !CommonUtils.isEmpty(outDataSourceSql) && !CommonUtils.isEmpty(isProcessSet)){
			BPMOutDataSourcePO po =new BPMOutDataSourcePO();
			po.setOutDataSourceCode(ds);
			po.setOutDataSourceType("DEAL");//办理中
			po.setOutDataSourceSql(outDataSourceSql);
			po.setOutDataSourceFields(outDataSourceFields);
			po.setProcessId(processId);
			po.setTaskId(taskId);
			po.setIsProcessSet(Long.valueOf(isProcessSet));
			
			BPMOutDataSourceBD bd =new BPMOutDataSourceBD();
			flag =bd.saveBPMOutDataSource(po);
			logger.debug("flag--deal--->"+flag);
		}
		//办理中配置外部数据源-----结束
		
		//退回配置外部数据源-----开始
		String outDataSourceSqlBack  =request.getParameter("outDataSourceSqlBack");
		logger.debug("outDataSourceSqlBack----->"+outDataSourceSqlBack);
		
		String outDataSourceFieldsBack ="";
		String[] outDataSourceFieldBackNo =request.getParameterValues("outDataSourceFieldsBackNo");
		String[] outDataSourceFieldBack =request.getParameterValues("outDataSourceFieldsBack");
		if(outDataSourceFieldBackNo !=null && outDataSourceFieldBackNo.length >0 && outDataSourceFieldBack !=null && outDataSourceFieldBack.length >0){
			for(int i=0;i<outDataSourceFieldBack.length;i++){
				logger.debug("outDataSourceFieldBack----->"+outDataSourceFieldBack[i]);
				logger.debug("outDataSourceFieldBackNo----->"+outDataSourceFieldBackNo[i]);
				if(!CommonUtils.isEmpty(outDataSourceFieldBackNo[i]) && !CommonUtils.isEmpty(outDataSourceFieldBack[i])){
					outDataSourceFieldsBack += outDataSourceFieldBackNo[i] +"|"+ outDataSourceFieldBack[i] +",";
				}
			}
			
			logger.debug("outDataSourceFieldsBack--1--->"+outDataSourceFieldsBack);
			if(outDataSourceFieldsBack.endsWith(",")){
				outDataSourceFieldsBack =outDataSourceFieldsBack.substring(0, outDataSourceFieldsBack.length()-1);
			}
			logger.debug("outDataSourceFieldsBack--2--->"+outDataSourceFieldsBack);
		}
		
		if(!CommonUtils.isEmpty(ds) && !CommonUtils.isEmpty(outDataSourceSqlBack) && !CommonUtils.isEmpty(isProcessSet)){
			BPMOutDataSourcePO po =new BPMOutDataSourcePO();
			po.setOutDataSourceCode(ds);
			po.setOutDataSourceType("BACK");//退回
			po.setOutDataSourceSql(outDataSourceSqlBack);
			po.setOutDataSourceFields(outDataSourceFieldsBack);
			po.setProcessId(processId);
			po.setTaskId(taskId);
			po.setIsProcessSet(Long.valueOf(isProcessSet));
			
			BPMOutDataSourceBD bd =new BPMOutDataSourceBD();
			flag =bd.saveBPMOutDataSource(po);
			logger.debug("flag--back--->"+flag);
		}
		//退回配置外部数据源-----结束
		
		String json ="";
		json ="{\"flag\":\""+flag+"\"}";
		printJsonResult(json);
		return null;
	}
	
	public String loadProcess() {
		logger.debug("开始load流程");
		java.util.Date  bDate=new java.util.Date();
		
		local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		this.initModuleVO();
		ManagerBD managerBD = new ManagerBD();

		try {
			// 记录ID
			String id = request.getParameter("recordId");
			logger.debug("id:"+id);
			String processId = request.getParameter("processId");
			
			logger.debug("processId:"+processId);
			WhirEzFlowDesignerEntity flow = null;
			com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();

			if ((id == null || "".equals(id) || "-1".equals(id))
					&& (processId == null || "".equals(processId))) {
                String subTypeStr="";
                String subTypeStr_display=""; 
                
                String  startWidth="80";
                String  startHeight="35";
              
                if(subType.equals("1")){
                	EzFlowMainService  manService=new EzFlowMainService();
                	subTypeStr="sub_"+manService.getId(1);
                	
                    //subTypeStr_display="子流程_";
                	
                	subTypeStr_display=Resource.getValue(local, "workflow","workflow.Sub-workflow")+"_";
                	
                	if(local!=null&&!local.equals("zh_CN")){
                		startWidth="160";
                	}
                }
                
                String classname="EzFormFlow";
                String saveData="save";
                String completeData="complete";
				/*if (EzFlowUtil.judgeNull(moduleVO.getEzflowClassName())) {
					classname=moduleVO.getEzflowClassName();
				} 
				if (EzFlowUtil.judgeNull(moduleVO.getEzflowSaveMethod())) {
					saveData=moduleVO.getEzflowSaveMethod();
				}
				if (EzFlowUtil.judgeNull(moduleVO.getEzflowCompleteMethod())) {
					completeData=moduleVO.getEzflowCompleteMethod(); 
				}*/
				if(moduleId!=1){
					if (EzFlowUtil.judgeNull(moduleVO.getFormClassName())) {
						classname=moduleVO.getFormClassName();
					}
					if (EzFlowUtil.judgeNull(moduleVO.getNewFormMethod())) {
						saveData=moduleVO.getNewFormMethod();
					}
					if (EzFlowUtil.judgeNull(moduleVO.getCompleteMethod())) {
						completeData=moduleVO.getCompleteMethod(); 
					}
				}
                
				String s = "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:activiti=\"http://activiti.org/bpmn\" xmlns:whir=\"http://whir.com/ezFlow\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"Examples\"><process whir:processUserScope=\"\" whir:processUserScopeName=\"\" whir:processPackage=\"\" whir:processPackageName=\"\" whir:formKey=\"\" whir:processCanReadEmp=\"\" whir:processCanReadEmpName=\"\" whir:processCanModifyEmp=\"\" whir:processCanModifyEmpName=\"\" whir:processAdministrator=\"\" whir:processAdministratorName=\"\" id=\"\" name=\"\">"
                        +"<extensionElements><whir:whirextension name=\"taskDealWithClass\">"
						+"<whir:field name=\"classname\"><whir:value>"+classname+"</whir:value></whir:field>"
                        +"<whir:field name=\"saveData\"><whir:value>"+saveData+"</whir:value></whir:field>"
						+"<whir:field name=\"saveStauts\"><whir:value>save_norequest</whir:value></whir:field>"
                        +"<whir:field name=\"completeData\"><whir:value>"+completeData+"</whir:value></whir:field>"
						+"<whir:field name=\"completeStauts\"><whir:value>complete_norequest</whir:value></whir:field>"+
                        "</whir:whirextension></extensionElements><documentation></documentation><startEvent id=\""
						+ subTypeStr
						+ "startevent1\" name=\""
						+ subTypeStr_display
						+ Resource.getValue(local, "workflow", "workflow.d_start")
						+ "\"/><endEvent id=\""
						+ subTypeStr
						+ "endevent1\" name=\""+Resource.getValue(local, "workflow",
								"workflow.d_end")  +"\"/></process><bpmndi:BPMNDiagram><bpmndi:BPMNPlane><bpmndi:BPMNShape bpmnElement=\""
						+ subTypeStr
						+ "startevent1\" id=\"BPMNShape_"
						+ subTypeStr
						+ "startevent1\"><omgdc:Bounds x=\"70\" y=\"20\" width=\""+startWidth+"\" height=\"35\"/></bpmndi:BPMNShape><bpmndi:BPMNShape bpmnElement=\""
						+ subTypeStr
						+ "endevent1\" id=\"BPMNShape_"
						+ subTypeStr
						+ "endevent1\"><omgdc:Bounds x=\"69\" y=\"347\" width=\"80\" height=\"35\"/></bpmndi:BPMNShape></bpmndi:BPMNPlane></bpmndi:BPMNDiagram></definitions>";

				ezflowOutputWriter(s);
			} else {
				Map map = new HashMap();
				if (id != null && !("".equals(id))) {
					// 查看设计有权限控制
					if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
						return null;
					}
					java.util.Date  bDate0=new java.util.Date(); 
					map.put("id", id);
					flow = flowService.get(map);
                    java.util.Date  eDate0=new java.util.Date(); 
					logger.debug("flowService.get(map)  时间："+(eDate0.getTime()-bDate0.getTime())); 
					
					ezflowOutputWriter(flow.getDesignerXml());
				} else if (processId != null) {
					java.util.Date  bDate1=new java.util.Date();
					map.put("processId", processId);
					com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService ds = new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();
					Map map1 = ds.getProcessXmlByProcessDefinitionId(processId);
					java.util.Date  eDate1=new java.util.Date(); 
					logger.debug("getProcessXmlByProcessDefinitionId  时间："+(eDate1.getTime()-bDate1.getTime())); 
					
					String xml=(String) map1.get("processXml"); 
					ezflowOutputWriter(xml);
					
					return null;
					// .findDesignerByProcessId(map);
				}
				if (flow != null) {

				}
				logger.debug("-------------------------xml------------------------------");
				logger.debug(flow.getDesignerXml());			
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("load流程出错：\n",e);
		}
		
		java.util.Date  eDate=new java.util.Date();
		
		logger.debug("结束load流程 时间："+(eDate.getTime()-bDate.getTime())); 
		// */
		return null;
	}
	
	
	
	
	public String loadProcessForFreeFlow() {
		logger.debug("开始load流程");
		java.util.Date  bDate=new java.util.Date(); 
		local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}   
		String xml=getXmlFromProcessDefId();  
		if(xml.indexOf("<userTask whir:taskSequenceType")>=0){
			//表明 已经设置了 活动 
		    request.setAttribute("haveUserTask", "1");
		} 
		java.util.Date  eDate=new java.util.Date(); 
		logger.debug("结束load流程 时间："+(eDate.getTime()-bDate.getTime())); 
		// */
		return "freesetlist";
	}
	
	private String getXmlFromProcessDefId(){
		String xml="";
		try {
			// 记录ID
			String id = request.getParameter("recordId");
			logger.debug("id:"+id);
			String processDefId = request.getParameter("processDefId"); 
			logger.debug("processDefId:"+processDefId); 
			if (processDefId != null) {
				java.util.Date  bDate1=new java.util.Date(); 
				com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService ds = new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();
				Map map1 = ds.getProcessXmlByProcessDefinitionId(processDefId);
				java.util.Date  eDate1=new java.util.Date(); 
				logger.debug("getProcessXmlByProcessDefinitionId  时间："+(eDate1.getTime()-bDate1.getTime()));  
				xml=(String) map1.get("processXml");  
				if(xml.indexOf("<userTask whir:taskSequenceType")>=0){
				    request.setAttribute("haveUserTask", "1");
				}
			} 
			logger.debug("-------------------------xml------------------------------"); 
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("load流程出错：\n",e);
		}
		return xml;
		
	}
	
	/**
	 * 
	 * @return
	 */
	public String  SaveFreeProcessDef(){
		
		java.util.Date  bDate=new java.util.Date(); 
		local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}  
		String id="";
		String  processDefId=request.getParameter("processDefId");
		
		List<Map> actList=buildActListForFreeFlow();
		com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService ds = new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();
	  
		String xml=ds.addOtherRealSubProcessXml(processDefId,actList); 
		String processDefinitionKey="";
		if(processDefId!=null&&!processDefId.equals("")){
		     processDefinitionKey=processDefId.substring(0,processDefId.lastIndexOf(":"));
			 processDefinitionKey=processDefinitionKey.substring(0,processDefinitionKey.lastIndexOf(":"));
		}	
	  
		com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService defService = 
		new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService(); 
	 	Map resultMap=defService.deployByXML(processDefinitionKey, xml);
	 	 
	 	String new_processDefinitionId=resultMap.get("processDefinitionId")==null?"":resultMap.get("processDefinitionId").toString();
	 	this.ezflowOutputPrint("<xml><result>-10</result><recordId>" + id + "</recordId><processDefinitionId>" + new_processDefinitionId + "</processDefinitionId></xml>");
	 	return null; 
	}
   
	/** 
	 * @return
	 */
	private List<Map> buildActListForFreeFlow(){
		List<Map>list=new ArrayList<Map>();  
		
		String  userTasknameArr[]=request.getParameterValues("userTaskname");
		String  taskSequenceTypeArr[]=request.getParameterValues("taskSequenceType");
		String  priorityArr[]=request.getParameterValues("priority");
		String  participantTypeArr[]=request.getParameterValues("participantType");
		String  eachIndexArr[]=request.getParameterValues("eachIndex"); 
		
		if(userTasknameArr!=null&&userTasknameArr.length>0){
			for(int i=0;i<userTasknameArr.length;i++){
				Map actMap=new HashMap();
				actMap.put("userTaskname", userTasknameArr[i]);
				actMap.put("taskSequenceType", taskSequenceTypeArr[i]);
				actMap.put("priority", priorityArr[i]);
				actMap.put("participantType", participantTypeArr[i]); 
				//从候选人指定
				if(participantTypeArr[i]!=null&&participantTypeArr[i].equals("someUsers")){
					actMap.put("passRound_candidateId", request.getParameter("passRound_candidateId"+eachIndexArr[i]));
					actMap.put("passRound_candidate", request.getParameter("passRound_candidate"+eachIndexArr[i]));
				}   
				
				list.add(actMap);
			}
		} 
		
		return list; 
	}
 
	
	/**
	 * 导入流程
	 * @return
	 * @throws IOException 
	 */
	public String importProcess() throws IOException {
		logger.debug("开始导入流程");
		
		logger.debug("moduleId:"+request.getParameter("moduleId"));
		logger.debug("choosed_processPackage:"+request.getParameter("choosed_processPackage"));
		ManagerBD managerBD = new ManagerBD();
		
		this.initModuleVO();
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(),rightCode)) {
			return null;
		}
		 
	    String filename = request.getParameter("fileName");  
	    String year_month = filename.substring(0,6);  
	    String filePath = request.getRealPath("/upload/fileimport")+ "/" + year_month+"/"+filename ;      
	    File file = new File(filePath);  
	    
	    //InputStreamReader     isr = new InputStreamReader(new FileInputStream(filePath), "utf-8"); 
	    
	    //InputStream  imputStram=new StringInputStream(whirEzFlowDesignerEntity.getDesignerXml(),"UTF-8");
		/*InputStream is =new FileInputStream(file);
		int by = -1;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		while ((by = is.read()) != -1) {
			baos.write(by);
		}
		String xml = baos.toString();
 
		logger.debug("--------4-----");
		String xml4= new String(xml.getBytes("GBK"),"UTF-8");
		logger.debug(xml4);*/
 
		byte buffer[] = new byte[(int) file.length()];//fl为一个File对象
		String xml=null;
	    try {
	        FileInputStream fileinput = new FileInputStream(file);
	        fileinput.read(buffer);//读取文件中的内容到buffer中
	        xml = new String(buffer,"UTF-8"); 
	    }  catch (Exception e) {
			e.printStackTrace();
			
			this.printJsonResult("导入的xml文件内容不正确，请重新导入！");
			xml=null;
		}
		try {
			if(xml!=null&&!xml.equals("")){
			   importSingleProcess(xml);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		logger.debug("结束导入流程");
		return null;
	}
	
	public String importSingleProcess(String xml) throws JDOMException, IOException {
		logger.debug("+++++++++++++++++++++++++++++++++++++++++++++++++");
		logger.debug(xml);
		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = 
				new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		String userId = (String) request.getSession().getAttribute("userId").toString();
		String orgId = (String) request.getSession().getAttribute("orgId").toString();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();
		String username = (String) request.getSession().getAttribute("userName").toString();
		boolean result=true;
		org.jdom.Document doc=null;
        try{
        	doc = new SAXBuilder().build(new java.io.StringReader(xml));
		}catch(Exception e){
			this.printJsonResult("导入的xml文件内容不正确，请重新导入！");
			result=false;
		}
        if(!result){
        	return "导入的xml文件内容不正确，请重新导入！";
        }
		org.jdom.Element defineElement = doc.getRootElement();
		
		org.jdom.Element processElement = defineElement.getChild("process",
				Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
		
		String processId = processElement.getAttribute("id").getValue();
		
		String packageId = processElement.getAttributeValue("processPackage", 
				Namespace.getNamespace("whir","http://whir.com/ezFlow"));
		
		String packageName = processElement.getAttributeValue("processPackageName", 
				Namespace.getNamespace("whir","http://whir.com/ezFlow"));
		
		String processName = processElement.getAttribute("name").getValue();
		
		String processScopeIds = processElement.getAttributeValue("processUserScope", 
				Namespace.getNamespace("whir","http://whir.com/ezFlow"));
	    
		String formKey = processElement.getAttributeValue("formKey",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		formType = processElement.getAttributeValue("formType",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processCanReadIds = processElement.getAttributeValue("processCanReadEmp",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processCanModifyIds = processElement.getAttributeValue("processCanModifyEmp",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processAdministratorIds = processElement.getAttributeValue("processAdministrator",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
		
		String processScopeNames = processElement.getAttributeValue("processUserScopeName", 
				Namespace.getNamespace("whir","http://whir.com/ezFlow"));

		String  choosed_processPackage=request.getParameter("choosed_processPackage");
		String  choosed_processPackageName=request.getParameter("choosed_processPackageName");
		
		logger.debug("choosed_processPackage:"+choosed_processPackage);
		logger.debug("choosed_processPackageName:"+choosed_processPackageName);
		
	    if(choosed_processPackage!=null&&!choosed_processPackage.equals("")&&!choosed_processPackage.equals("")){
	    	choosed_processPackage =com.whir.component.security.crypto.EncryptUtil.sqlcode(choosed_processPackage);
	    	choosed_processPackageName =com.whir.component.security.crypto.EncryptUtil.sqlcode(choosed_processPackageName);
	    	
	    	//设置新的分类
	    	String oldpackageId="whir:processPackage=\""+packageId+"\"";
			String oldpackageName="whir:processPackageName=\""+packageName+"\"";
			
			processElement.setAttribute("processPackage",choosed_processPackage,
					Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
			processElement.setAttribute("processPackageName",choosed_processPackageName,
					Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
			packageId=choosed_processPackage;
			packageName=choosed_processPackageName;
			
			//whir:processPackage="fffffff" whir:processPackageName="ffa"
			
			logger.debug("oldpackageId:"+oldpackageId);
			
			logger.debug("oldpackageName:"+oldpackageName);
			
			String newpackageId="whir:processPackage=\""+packageId+"\"";
			String newpackageName="whir:processPackageName=\""+packageName+"\""; 
			
			logger.debug("newpackageId:"+newpackageId);
			
			logger.debug("newpackageName:"+newpackageName);
					
			xml=xml.replaceAll(oldpackageId, newpackageId);
			xml=xml.replaceAll(oldpackageName, newpackageName);
	    }else{  
	    } 
	    
	    String mobileStatus = processElement.getAttributeValue("mobileStatus",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
	    
	    String mobilePhoneStatus = processElement.getAttributeValue("mobilePhoneStatus",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
	    
	    String processType = processElement.getAttributeValue("processType",
				Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
	    
	    // 找所有节点的formkey
		List userTaskElementList = processElement.getChildren("userTask",
				Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
		String taskFormKey = "";
		for (int j = 0; j < userTaskElementList.size(); j++) {
			org.jdom.Element userTaskElement = (org.jdom.Element) userTaskElementList.get(j);
			String formKeyTemp = userTaskElement.getAttributeValue("formKey",
					Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
			if (taskFormKey.indexOf("," + formKeyTemp + ",") < 0 && taskFormKey.indexOf(formKeyTemp + ",") != 0) {
				taskFormKey += (formKeyTemp + ",");
			}
		}
		if (!"".equals(taskFormKey)) {
			taskFormKey = taskFormKey.substring(0, taskFormKey.length() - 1);
		}
		
		//相关联的子流程
	    List<String>  subProcessKeyList=new ArrayList<String>(); 
		//子流程
		List subProcessList = processElement.getChildren("subProcess",
				Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL"));
 
		if (subProcessList != null && subProcessList.size() > 0) {
			org.jdom.Element subProcessElement = null;
			for (int i = 0; i < subProcessList.size(); i++) {
				subProcessElement = (org.jdom.Element) subProcessList.get(i);
				// 子流程引用的流程key
				String subProcessKey = subProcessElement.getAttributeValue("subProcessKey", 
						Namespace.getNamespace("whir", "http://whir.com/ezFlow"));
				subProcessKeyList.add(subProcessKey);
			}
		} 
		
		String repeatStr=getRepeatedKey(subProcessKeyList); 
		if(!repeatStr.equals("")){
			logger.error("设置中有多个节点引用同一个子流程。");
			this.ezflowPrint("<xml><result>-2</result><message>设置中有多个节点引用同一个子流程。</message></xml>");
			return null; 
		} 
		if(subProcessKeyList!=null&&subProcessKeyList.size()>0){
			String deleteStr=getDeleteProcessKey(subProcessKeyList); 
			if(!deleteStr.equals("")){
				deleteStr=deleteStr.substring(0,deleteStr.length()-1);
				logger.error("有删除的子流程，流程ID为："+deleteStr);
				this.ezflowPrint("<xml><result>-3</result><message>有删除的子流程，流程ID为："+deleteStr+"</message></xml>");
				return null; 
			} 
		}
		
		Map m = new HashMap();
		
		m.put("createOrgId", orgId);
		m.put("createUserId", userId);
		m.put("createUserName", username);
		m.put("designerXml", xml);
		m.put("domainId", domainId);
		m.put("isdeployed", 0);
		m.put("packageId", packageId);
		m.put("packageName", packageName);
		m.put("processId", processId);
		m.put("processName", processName);
		m.put("processScopeIds", processScopeIds);
		m.put("processScopeNames", processScopeNames);
		m.put("processCanReadIds", processCanReadIds);
		m.put("processCanModifyIds", processCanModifyIds);
		m.put("processAdministratorIds", processAdministratorIds);
	    m.put("sort", "1");
		
		m.put("mobileStatus", mobileStatus);
		m.put("mobilePhoneStatus", mobilePhoneStatus);
		m.put("subType", subType);
		m.put("processType", processType);		 
		m.put("formType", formType);
		
		// 新加----------------------------
		m.put("subProcessKeyList", subProcessKeyList);
		m.put("formCode", formKey);
		m.put("activityCodes", taskFormKey);
		
		List processPackageList = new ArrayList();
		//com.whir.service.api.ezflowservice.EzFlowPackageService flowService1 = new com.whir.service.api.ezflowservice.EzFlowPackageService();
		//List<Map> packageList = flowService1.getPackageList(new HashMap());
		
		BPMPackageBD  bd=new BPMPackageBD(); 
		
		List allPackageList=bd.getPackageList(""+this.moduleId, domainId);
		boolean exist = true;
		String packageIdArr[]=packageId.split(",");
		if(packageIdArr!=null&&packageIdArr.length>0){
			for(int i=0;i<packageIdArr.length;i++){
				boolean _exist=false;
				for (int j = 0; j < allPackageList.size(); j++) {
					Object obj[]=(Object[])allPackageList.get(j);
					if (packageIdArr[i] != null&& packageIdArr[i].equals((String) obj[2])) {
						logger.debug("流程包id 存在"+ obj[2] + "::::::"+ packageIdArr[i]);
						_exist = true;
						break;
					}
				}
				if(!_exist){
					exist=false;
					break;
				}
			}
		}
		logger.debug("流程包id " + exist);
		if (!exist) {
			/*response.setCharacterEncoding("utf-8");
			response.setContentType("text/xml");
			response.getWriter().print(
					"<xml><result>-1</result><message>流程分类:"+ packageId + "不存在!</message></xml>");
			response.getWriter().flush();
			response.getWriter().close();*/
			packageId =com.whir.component.security.crypto.EncryptUtil.replaceHtmlcode(packageId);
			this.printJsonResult("流程分类:"+ packageId + "不存在!");
			return null;
		}
		
		// 检查存在性
		Map queryMap = new HashMap();
		queryMap.put("processId", processId);
		queryMap.put("processName", processName);
		if (flowService.checkProcessRepeat(queryMap)) {
			/*response.setCharacterEncoding("utf-8");
			response.setContentType("text/xml");
			response.getWriter().print("<xml><result>-1</result><message>流程名或者流程ID重复!</message></xml>");
			response.getWriter().flush();
			response.getWriter().close();*/
			//如果存在 就修改
			Map  cMap=new HashMap();
			cMap.put("processId", processId);
			WhirEzFlowDesignerEntity e=flowService.findDesignerByProcessId(cMap);
			String id=e.getId();
			
			String oldProcessName=" name=\""+processName+"\"";
			String newName=" name=\""+e.getProcessName()+"\"";
			
			if(!processName.equals(e.getProcessName())){
				processName=e.getProcessName();
				xml=xml.replaceFirst(oldProcessName, newName);
				m.put("designerXml", xml);
			}
			
			m.remove("sort"); 
			m.put("id", id);
			m.put("processName", processName);
		    id = flowService.update(m);
			logger.debug("导入修改流程:" + id+" processId:"+processId);
			//this.printJsonResult("流程名或者流程ID重复!");
		    this.printJsonResult(this.SUCCESS);
			return null;
		}
		String id = flowService.insert(m);
		logger.debug("插入一条记录id:" + id);
		this.printJsonResult(this.SUCCESS);
		
		return null;
	}
	
	
	/**
	 * 复制流程
	 * @return
	 */
	public String copyProcess() {
		logger.debug("开始拷贝流程");
		String batchIds = request.getParameter("id");
		logger.debug("copy:" + batchIds);
		// System.out.println("========================================================="+batchIds);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		
		this.initModuleVO();
		ManagerBD managerBD = new ManagerBD();
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}
		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		Map map = new HashMap();
		map.put("ids", batchIds);
		map.put("createUserId", request.getSession().getAttribute("userId"));
		map.put("createUserName", request.getSession().getAttribute("userName"));
		map.put("createOrgId", request.getSession().getAttribute("orgId"));
		map.put("domainId", request.getSession().getAttribute("domainId"));
		String tooLongKeyOfProcessName=flowService.copy(map);
		
		logger.debug("tooLongKeyOfProcessName:"+tooLongKeyOfProcessName);
		
		if(tooLongKeyOfProcessName==null||tooLongKeyOfProcessName.equals("")||tooLongKeyOfProcessName.equals("null")){
			this.printResult(this.SUCCESS);
		}else{
			logger.debug("失败tooLongKeyOfProcessName:"+tooLongKeyOfProcessName);
			this.printResult(" "+tooLongKeyOfProcessName+"等流程的流程ID太长，不能进行复制操作 。");
		} 
		logger.debug("结束拷贝流程");
		return null;
	}
	
	
	/**
	 * 导出流程,多个流程导出成压缩包
	 * 
	 * @return
	 */
	public String export() {
		logger.debug("开始导出流程");
		String batchIds = request.getParameter("batchIds");
		logger.debug("batchIds:"+batchIds);
		String batchIdArr[]=batchIds.split(",");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		
		this.initModuleVO();
		ManagerBD managerBD = new ManagerBD();
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}
		if (batchIdArr.length > 0) {
			com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = 
					new com.whir.service.api.ezflowservice.EzFlowDesignerService();
			response.setCharacterEncoding("utf-8");
			try {
				String fileName = URLEncoder.encode("流程定义包.zip", "UTF-8");
				if (true) {
					String guessCharset = "gb2312"; /*
													 * 根据request的locale
													 * 得出可能的编码，中文操作系统通常是gb2312
													 */
					//fileName = new String("流程定义包.zip".getBytes(guessCharset), "ISO8859-1"); 

					fileName = CommonUtils.encodeName("流程定义包.zip", request);
				}
				response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			response.setContentType("application/zip");
			CheckedOutputStream cos = null;
			try {
				cos = new CheckedOutputStream(response.getOutputStream(), new CRC32());
				ZipOutputStream zos = new ZipOutputStream(cos);
				zos.setEncoding("gbk");
				for (int i = 0; i < batchIdArr.length; i++) {
					Map map = new HashMap();
					map.put("processId", batchIdArr[i]);
					logger.debug("id is" + batchIdArr[i]);
					WhirEzFlowDesignerEntity flow = flowService.findDesignerByProcessId(map);
					ZipEntry entry = new ZipEntry(flow.getProcessName()+ ".xml");
					zos.putNextEntry(entry);
					logger.debug("==========");
					logger.debug(flow.getDesignerXml());
					InputStream bis = new java.io.ByteArrayInputStream(flow.getDesignerXml().getBytes("utf-8"));

					int BUFFER = 1024;
					int count;
					byte data[] = new byte[BUFFER];
					while ((count = bis.read(data, 0, BUFFER)) != -1) {
						zos.write(data, 0, count);
					}
					zos.flush();
					zos.closeEntry();
					bis.close();
				}
				zos.flush();
				zos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("结束导出流程");
		return null;
	}
	
	/**
	 * 删除流程
	 * @return
	 */
	public String deleteProcess(){
		logger.debug("开始删除流程");
		String id = request.getParameter("id");
		String processId = request.getParameter("processId");
		
		if(id==null||id.equals("")||id.equals("")){
			this.printResult(this.ERROR);
			return null;
		}
		
		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		com.whir.ezoffice.bpm.bd.BPMProcessBD  bpmProcessBD=new com.whir.ezoffice.bpm.bd.BPMProcessBD();
		String idArr[]=id.split(",");
		String processIdArr[]=processId.split(",");
		for (int i = 0; i < idArr.length; i++) {
			bpmProcessBD.deleteBPMProcessByEzFlowKey(processIdArr[i]);
			Map map = new HashMap();
			map.put("id", idArr[i]);// * id 主键值
			map.put("processId", processIdArr[i]);// * processId 流程Id
			flowService.delete(map); 			
		}     
		
		oprType = "3";
		oprContent = "删除流程" ;//
		java.util.Date endDate = new java.util.Date();
		logBD.log(request.getSession(true).getAttribute("userId").toString(),
				request.getSession(true).getAttribute("userName").toString(),
				request.getSession(true).getAttribute("orgName").toString(),
				moduleCode, moduleName, startDate, endDate, oprType,
				oprContent, request.getRemoteAddr(), request.getSession(true).getAttribute("domainId").toString());
			
		this.printResult(this.SUCCESS);

		logger.debug("结束删除流程");
		return null;
	}
	
	
	/**
	 * 部署流程
	 * @return
	 */
	public String deploy() {
		logger.debug("开始部署流程");
        this.initModuleVO();
		ManagerBD managerBD = new ManagerBD();
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}

		String id = request.getParameter("id");
		String processId = request.getParameter("processId");
		com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService flowService = new com.whir.service.api.ezflowservice.EzFlowProcessDefinitionService();
		
		//部署子流程， 实际上是部署所有引用此子流程的主流程。
		if(subType.equals("1")){ 
			flowService.deploySubProcess(id);
			ezflowPrint("1");
			logger.debug("结束部署流程1");
			return null;// list(actionMapping, actionForm, request, response);
		}else{ 
			Map map = flowService.deploy(id);
			String result = "-1";
			if (map == null) {
				result = "-1";
			} else {
				result = map.get("deploymentId") == null ? "-2" : (String) map.get("deploymentId");
				logger.debug("result--deploymentId--->"+result);
				if(!result.equals("-2") && Long.valueOf(result).longValue() >0){
					Long userId =CommonUtils.getSessionUserId(request);
					String UserName =CommonUtils.getSessionUserName(request);
					
					java.util.Date DeploymentDate =new java.util.Date();
					
					BPMFlowDeploymentPO po =new BPMFlowDeploymentPO();
					po.setDeploymentId(result);
					po.setDeploymentUserId(userId);
					po.setDeploymentUserName(UserName);
					po.setDeploymentDate(DeploymentDate);
					
					BPMProcessBD bd =new BPMProcessBD();
					
					Long _id =bd.addBPMFlowDeploymentPO(po);
					
					logger.debug("_id----->"+id);
				}
			}
			ezflowPrint(result);
			logger.debug("结束部署流程0");
			return null;// list(actionMapping, actionForm, request, response);
		}
	}
	
	/**
	 * 获取流程设置页面
	 * 
	 * @throws Exception
	 */
	public String setProcess() {
		logger.debug("开始获取流程设置页面");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		
		initModuleVO();
		ManagerBD managerBD = new ManagerBD();
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}
		try {
			//com.whir.ezoffice.ezform.service.FormService formService = new com.whir.ezoffice.ezform.service.FormService();
			//com.whir.service.api.ezflowservice.EzFlowPackageService flowService = new com.whir.service.api.ezflowservice.EzFlowPackageService();
			String userId = (String) request.getSession().getAttribute("userId").toString();
			String orgId = (String) request.getSession().getAttribute("orgId").toString();
			String orgIdString =(String)request.getSession().getAttribute("orgIdString");
			String domainId = (String) request.getSession().getAttribute("domainId").toString();
			logger.debug("action is processSetting");
			/*// 获取所属分类列表
			List processPackageList = new ArrayList();
			List<Map> packageList = flowService.getPackageList(new HashMap());
			for (int i = 0; i < packageList.size(); i++) {
				processPackageList.add(new String[] {
						(String) packageList.get(i).get("PACKAGEID"),
						(String) packageList.get(i).get("PACKAGENAME") });
			}
			request.setAttribute("processPackageList", processPackageList);*/
			
			//取所有的分类
			setPackageList();
			
			OrganizationBD orgBd = new OrganizationBD();
			RoleBD roleBd = new RoleBD();
			
			// 存所有角色
			List roles = roleBd.getAllIdAndName(domainId);
			
			List orgs = orgBd.getValidOrgs();
			
			request.setAttribute("allRoleList", roles);
			request.setAttribute("allOrgList", orgs);
			
			if(subType.equals("1")){
				
			}else{ 
				// 获取可选表单
				BPMFormInfoBD  bd=new BPMFormInfoBD();
				List formList=bd.getAllFormInfo(moduleVO, userId.toString(), orgId, orgIdString, domainId, true);
				request.setAttribute("formList", formList);
			}

			// 获取 所有字段
			List fieldList = new ArrayList();
			request.setAttribute("fieldList", fieldList);

			// 获取提醒项字段
			List processRemindFieldList = new ArrayList();
			request.setAttribute("processRemindFieldList", processRemindFieldList);
			
			//办公地点 分类 
			setAddressTypeList();
			
			//归档类目
			DossierBD bd = new DossierBD();
			request.setAttribute("lmList",bd.getLM(String.valueOf(domainId),null));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		logger.debug("结束获取流程设置页面");
		if(subType.equals("1")){
			return "setprocess_sub"; 
		}else{
		    return "setprocess"; 
		}
	}

	/**
	 * 获取开始事件设置页面
	 * @throws Exception
	 */
	public String setStartEvent() throws Exception {
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		logger.debug("开始打开开始事件设置页面");
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		com.whir.ezoffice.ezform.service.FormService formService = new com.whir.ezoffice.ezform.service.FormService();
		String userId = (String) request.getSession().getAttribute("userId").toString();
		String orgId = (String) request.getSession().getAttribute("orgId").toString();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();
		String formCode = request.getParameter("formCode");
		// 获取可选表单
		List formList = formService.getForms(userId, orgId, domainId, true);

		if (formCode != null && !"".equals(formCode)) {
			formList = formService.getUseSameTableFormsByFormCode(formCode,domainId);
		} else {
			formList = formService.getForms(userId, orgId, domainId, true);
		}

		request.setAttribute("formList", formList);

		// 获取 所有字段
		List fieldList = new ArrayList();

		request.setAttribute("fieldList", fieldList);
       
		logger.debug("结束打开开始事件设置页面");
		// */
		return "setstartevent";
		// return actionMapping.findForward("edit");
	}

	/**
	 * 获取活动设置页面
	 * @throws Exception
	 */
	public String setActivity() throws Exception {
		logger.debug("开始活动设置页面");
		local = request.getSession().getAttribute("org.apache.struts.action.LOCALE").toString();
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		this.initModuleVO();
		String formCode = request.getParameter("formCode");
		
		String userId = (String) request.getSession().getAttribute("userId").toString();
		String orgId = (String) request.getSession().getAttribute("orgId").toString();
		String orgIdString = (String) request.getSession().getAttribute("orgIdString").toString();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();

		OrganizationBD orgBd = new OrganizationBD();
		RoleBD roleBd = new RoleBD();

		java.util.Map include_sysMap = com.whir.org.common.util.SysSetupReader
				.getInstance().getSysSetupMap(request.getSession().getAttribute("domainId").toString());
		int smsInUse = 0;
		if (include_sysMap != null && include_sysMap.get("短信开通") != null
				&& "1".equals(include_sysMap.get("短信开通").toString())) {
			request.setAttribute("noteRemind", "true");
		}
		// 存所有角色
		List roles = roleBd.getAllIdAndName(domainId);

		List orgs = orgBd.getValidOrgs();

		request.setAttribute("allRoleList", roles);
		request.setAttribute("allOrgList", orgs);

		// 存所有组织

		logger.debug("action is processSetting");
		// 获取办理方式
		List taskSequenceTypeList = new ArrayList();
 
		//"抢占"
		taskSequenceTypeList.add(new String[] { "monopolise",Resource.getValue(local, "workflow", "workflow.monopolise")});
		//"顺序"
		taskSequenceTypeList.add(new String[] { "sequential", Resource.getValue(local, "workflow", "workflow.sequential")});
		//"并行"
		taskSequenceTypeList.add(new String[] { "parataxis", Resource.getValue(local, "workflow", "workflow.parataxis")});
		//"单人"
		taskSequenceTypeList.add(new String[] { "monopolise_single", Resource.getValue(local, "workflow", "workflow.single")});
						
		request.setAttribute("taskSequenceTypeList", taskSequenceTypeList);
		// 优先级别
		List priorityList = new ArrayList();
		// priorityList.add(new String[]{"","无"});
		
		//"特提" 
		priorityList.add(new String[] { "50", Resource.getValue(local, "filetransact", "file.sort1") });
		//"特急" 
		priorityList.add(new String[] { "40", Resource.getValue(local, "filetransact", "file.sort2") });
		//"急件"
		priorityList.add(new String[] { "30",Resource.getValue(local, "filetransact", "file.sort4") });
		//"加急"
		priorityList.add(new String[] { "20", Resource.getValue(local, "filetransact", "file.sort3") });
		//"一般"
		priorityList.add(new String[] { "10", Resource.getValue(local, "filetransact", "file.sort5")});
		request.setAttribute("priorityList", priorityList);

		// 获取可选表单
		List formList = new ArrayList();
		com.whir.ezoffice.ezform.service.FormService formService = new com.whir.ezoffice.ezform.service.FormService();
		com.whir.ezoffice.bpm.bd.BPMFormInfoBD formbd=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
		//如果formCode不为空， 取使用统一表的 表单
		if (formCode != null && !"".equals(formCode)) {
			formList = formbd.getUseSameTableFormsByFormCode(formCode, moduleVO, formType, userId, orgId, orgIdString, domainId);
		} else {
			//取所有表单
			//formList = formService.getForms(userId, orgId, domainId, true);
			formList=formbd.getAllFormInfo(moduleVO, userId, orgId, orgIdString, domainId, true);
		}

		request.setAttribute("formList", formList);

		// 获取 所有字段
		List fieldList = new ArrayList();
		request.setAttribute("fieldList", fieldList);

		// 获取批示意见对应字段
		List nodeCommentFieldList = new ArrayList();
		request.setAttribute("nodeCommentFieldList", nodeCommentFieldList);
		
 
		List dutyList=new ArrayList();
		try {
			//dutyList = new ProcessStep().listDutyLevel(domainId);
			com.whir.ezoffice.personnelmanager.bd.NewDutyBD  dbd=new com.whir.ezoffice.personnelmanager.bd.NewDutyBD();
			dutyList=dbd.getListWithScope(userId, orgIdString, domainId);
		} catch (Exception e) {
			
		}
		
		request.setAttribute("dutyList", dutyList);
		 
		setAddressTypeList();
		
		//获取外部数据源配置-----开始
		BPMOutDataSourceBD bd =new BPMOutDataSourceBD();
		String processId =request.getParameter("processKey");
		String taskId =request.getParameter("id");
		logger.debug("processId--setActivity--->"+processId);
		logger.debug("taskId--setActivity--->"+taskId);
		String isProcessSet ="1";
		List outDataSourceList =new ArrayList();
		List outDataSourceListDeal =new ArrayList();
		List outDataSourceListBack =new ArrayList();
		if(!CommonUtils.isEmpty(processId) && !CommonUtils.isEmpty(taskId)){
			outDataSourceList =bd.getBPMOutDataSourceList(processId, taskId, isProcessSet);
			if(outDataSourceList !=null && outDataSourceList.size() >0){
				for(int i=0;i<outDataSourceList.size();i++){
					Object[] obj =(Object[])outDataSourceList.get(i);
					String outDataSourceType =obj[3]==null?"":obj[3].toString();
					logger.debug("outDataSourceType--setActivity--->"+outDataSourceType);
					if(outDataSourceType.equals("DEAL")){//办理中
						outDataSourceListDeal.add(obj);
					}else if(outDataSourceType.equals("BACK")){//退回
						outDataSourceListBack.add(obj);
					}
				}
			}
		}
		request.setAttribute("outDataSourceListDeal", outDataSourceListDeal);
		request.setAttribute("outDataSourceListBack", outDataSourceListBack);
		//获取外部数据源配置-----开始
		 
		if(subType.equals("1")){
			 return "setactivity_sub";
		}else{
			 logger.debug("开始活动设置页面");
		     return "setactivity";
		} 
		 
		// return actionMapping.findForward("edit");
	}
	
	/**
	 * 办公地点分类列表
	 */
	private  void  setAddressTypeList(){
		 //办公地点分类列表
		 EzFlowMainService   service=new EzFlowMainService();
		 String sql=" SELECT  WD.TYPE_ID, WD.TYPENAME FROM   OA_WORKADDRESS_TYPE WD     ORDER BY  WD.ORDERCODE ";
		 Map inMap=new HashMap();
		 inMap.put("sql", sql);
		 List addressTypeList=new ArrayList();
		 Map sortmap=service.searchBySql_out(inMap);
		 List list=(List)sortmap.get("resultList"); 	 
		 if(list!=null&&list.size()>0){
			 for(int i=0;i<list.size();i++){
				 Map emap=(Map)list.get(i);
				 addressTypeList.add(new Object[]{emap.get("TYPE_ID"),emap.get("TYPENAME")});
			 }
		 } 			 
		 request.setAttribute("addressTypeList", addressTypeList);
	}

	/**
	 * 获取调用过程活动设置页面
	 * @throws Exception
	 */
	public String setCallActivity() throws Exception {
		logger.debug("开始调用过程活动设置页面");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0); 
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();
		HashMap m = new HashMap();
		m.put("domainId", domainId);
		m.put("moduleId", "1");
		// 引用流程列表
		List<WhirEzFlowDesignerEntity> list = flowService.listDesigner(m);
		// System.out.println("list size----------------------"+list.size());
		List calledElementList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			WhirEzFlowDesignerEntity flow = list.get(i);
			
			//只能选业务流程  1：业务流程  0：随机流程  2：半自由流程  3：自由流程
			if(flow.getProcessType()==1){
			      calledElementList.add(new String[] { flow.getProcessId(),flow.getProcessName() ,	flow.getFormCode(),flow.getFormType()+""});
			}
		}
		request.setAttribute("calledElementList", calledElementList); 
		
		//主流程的字段   
	    String formcode = request.getParameter("formKey");  
		com.whir.ezoffice.bpm.bd.BPMFormInfoBD  formbd=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
		List fieldList=formbd.getFieldsByFormCode(formcode, formType, (String) request.getSession().getAttribute("domainId"), ""+moduleId);
		request.setAttribute("mainfieldList", fieldList);
		
        
		//数据表名称
		String tableName="";
		if(moduleId==1){
			try{
			   EzFormBD efbd=new EzFormBD();
			   tableName=efbd.getTablePOByFormCode(formcode, (String) request.getSession().getAttribute("domainId")).getTableDesName();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		} 
		request.setAttribute("maintabledisplayName", tableName);
		
		/*
		for (int i = 0; i < fieldList.size(); i++) {
			str += "<field><fieldid>" + ((String[]) fieldList.get(i))[0]
					+ "</fieldid>" + "<fieldtext>"
					+ ((String[]) fieldList.get(i))[1] + "</fieldtext>"
					+ "<fieldtype>" + ((String[]) fieldList.get(i))[2]
					+ "</fieldtype><tabletype>"
					+ ((String[]) fieldList.get(i))[3] + "</tabletype></field>";
		}*/

		return "setcallactivity";
	}
	
	/**
	 * 获取子流程设置页面
	 * @throws Exception
	 */
	public String setSubProcess() throws Exception {
		logger.debug("开始调用过程活动设置页面");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0); 
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
		String domainId = (String) request.getSession().getAttribute("domainId").toString();
		HashMap m = new HashMap();
		m.put("domainId", domainId);
		//表示只取子流程
		m.put("subType", "1");
		// 引用流程列表
		List<WhirEzFlowDesignerEntity> list = flowService.listDesigner(m);
		// System.out.println("list size----------------------"+list.size());
		List calledElementList = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			WhirEzFlowDesignerEntity flow = list.get(i);
			if(flow.getProcessType()==1){
			      calledElementList.add(new String[] { flow.getProcessId(),flow.getProcessName() });
			}
		}
		request.setAttribute("calledElementList", calledElementList);

		return "setSubProcess";
	}
	
	
	
	/**
	 * 获取表达式设置页面所需数据
	 * 
	 * @param actionMapping
	 * @param actionForm
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String setCondition() throws Exception {
		logger.debug("开始表达式设置页面所需数据");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		//com.whir.ezoffice.ezform.service.FormService formService = new com.whir.ezoffice.ezform.service.FormService(); 
		String formCode = request.getParameter("formCode");
		// 获取条件

		// 获取运算符
		if (formCode != null && !"".equals(formCode)) {
			/*List fieldList = formService.getFieldsByFormCode(formCode,
					(String) request.getSession().getAttribute("domainId"));*/
			com.whir.ezoffice.bpm.bd.BPMFormInfoBD  formbd=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
			List fieldList=formbd.getFieldsByFormCode(formCode, formType, (String) request.getSession().getAttribute("domainId"), ""+moduleId);
			request.setAttribute("fieldList", fieldList);
		} else {
			request.setAttribute("fieldList", Collections.EMPTY_LIST);
		}
		if (formCode != null)
			request.setAttribute("formCode", formCode);
		// */
		// return null;
		logger.debug("结束表达式设置页面所需数据");
		return "setcondition";
	}
	
	/**
	 * 根据 字段id 查询 可选值
	 * @return
	 * @throws Exception
	 */
	public String getSelectDataByFieldId() throws Exception {
        logger.debug("开始根据 字段id 查询 可选值");
		String fieldId = request.getParameter("fieldId"); 
		
		com.whir.ezoffice.ezform.service.FormService formService  = new com.whir.ezoffice.ezform.service.FormService();
		
		List fieldList=formService.getSelectDataByFieldId(fieldId); 
		logger.debug("field size is :" + fieldList.size());
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache_Control", "no-cache");
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String str = "<data><fields>";
		for (int i = 0; i < fieldList.size(); i++) {
			str += "<field><hidenValue>" + ((String[]) fieldList.get(i))[0]
					+ "</hidenValue>" + "<showValue>"
					+ ((String[]) fieldList.get(i))[1] + "</showValue></field>";
		}
		str += "</fields>"; 
		// * String[3]-是否主表字段，“1”-主表字段 “0”-子表字段  
		str += "</data>"; 
		ezflowWriter(xml + str);
		logger.debug("结束根据 字段id 查询 可选值");
		return null;
	}
	
	/**
	 * 设置流程排序码
	 * @return
	 * @throws Exception
	 */
	public String setOrder() throws Exception {
		ManagerBD managerBD = new ManagerBD();
		this.initModuleVO();
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		if (!managerBD.hasRight(request.getSession().getAttribute("userId").toString(), rightCode)) {
			return null;
		}
		String id = request.getParameter("id");
		String sort = request.getParameter("sort");
		String oldsort = request.getParameter("oldsort");
 
		if (sort == null || "".equals(sort)) {
 
		} else {
			com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
			Map m = new HashMap();
			m.put("id", id);
			WhirEzFlowDesignerEntity entity = flowService.get(m);
			entity.setSort(Integer.parseInt(sort));
			// entity.update(entity);
			// /*
			m.put("createOrgId", entity.getCreateOrgId());
			m.put("createUserId", entity.getCreateUserId());
			m.put("createUserName", entity.getCreateUserName());
			m.put("designerXml", entity.getDesignerXml());
			m.put("domainId", entity.getDomainId());
			m.put("isdeployed", entity.getIsdeployed());
			m.put("packageId", entity.getPackageId());
			m.put("packageName", entity.getProcessName());
			m.put("processId", entity.getProcessId());
			m.put("processName", entity.getProcessName());
			m.put("processScopeIds", entity.getProcessScopeIds());
			m.put("processScopeNames", entity.getProcessScopeNames());
			m.put("sort", sort);
			flowService.updateDesigner(m);// */
			
			//同步排序码
			BPMProcessBD  bPMProcessBD=new BPMProcessBD();
			bPMProcessBD.updateEzFLOWSort(id, sort);
		}
        this.printResult(this.SUCCESS);
		return null;
	}

	public static boolean toBool(String var) {
		if (var == null || "".equals(var.toString())) {
			return true;
		}
		return Boolean.getBoolean(var);
	}

	public static int toNum(String var) {
		System.out.println("var is :" + var);
		if (var == null || "".equals(var.toString())) {
			return Integer.parseInt(var);
		}
		return 0;
	}

	public static boolean like(String var1, String var2) {
		// 替换
		var2 = var2.replaceAll("\\\\", "\\\\\\\\");
		var2 = var2.replaceAll("\\.", "\\\\.");
		var2 = var2.replaceAll("\\^", "\\\\^");
		var2 = var2.replaceAll("\\[", "\\\\[");
		var2 = var2.replaceAll("\\]", "\\\\]");
		var2 = var2.replaceAll("\\)", "\\\\)");
		var2 = var2.replaceAll("\\(", "\\\\((");
		var2 = var2.replaceAll("\\*", "\\\\*");
		var2 = var2.replaceAll("\\$", "\\\\$");

		var2 = var2.replaceAll("\\?", "\\\\?");
		var2 = var2.replaceAll("\\+", "\\\\+");
		var2 = var2.replaceAll("\\{", "\\\\{");
		var2 = var2.replaceAll("\\}", "\\\\}");
		var2 = var2.replaceAll("\\:", "\\\\:");
		var2 = var2.replaceAll("%", ".*");
		var2 = var2.replaceAll("\\?", ".");
		var2 = "^" + var2 + "$";
		return var1.matches(var2);
	}

	public static boolean notlike(String var1, String var2) {
		return !like(var1, var2);
	}

	/**
	 * 检查表达式是否正确
	 * 
	 * @throws Exception
	 */
	public String judgeJuelExpress() throws Exception {
		logger.debug("开始检查检查表达式是否正确");
		if (request.getSession().getAttribute("userId") == null) {
			return null;
		}
		String expressstr = request.getParameter("expressstr");
		String formCode = request.getParameter("formCode");
		List fieldList = Collections.EMPTY_LIST; 

		if (formCode != null && !"".equals(formCode)) { 
			com.whir.ezoffice.bpm.bd.BPMFormInfoBD  formbd=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
			fieldList=formbd.getFieldsByFormCode(formCode, formType, (String) request.getSession().getAttribute("domainId"), ""+moduleId); 
		}
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		if (expressstr != null && !"".equals(expressstr)) {
			expressstr = expressstr.trim();
			expressstr = "${" + expressstr + "}";
			logger.debug(expressstr);
			// ExpressionFactory factory = new
			// de.odysseus.el.ExpressionFactoryImpl();
			System.setProperty(TreeBuilder.class.getName(),SyntaxExtension.class.getName());
			ExpressionFactory factory = new ExpressionFactoryImpl(System.getProperties());
			// package de.odysseus.el.util provides a ready-to-use subclass of
			// ELContext
			de.odysseus.el.util.SimpleContext context = new de.odysseus.el.util.SimpleContext();

			// 设置函数like notlike isnull notnull
			try {
				context.setFunction("whir", "toint", EzFlowProcessAction.class.getMethod("toNum", new Class[] { String.class }));
				context.setFunction("whir", "tobool", EzFlowProcessAction.class.getMethod("toBool", new Class[] { String.class }));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			// 设置变量
			// 固定变量
			context.setVariable("condi_initiatorDuty", factory.createValueExpression("", String.class));
			context.setVariable("condi_initiatorDutyLevel", factory.createValueExpression("", String.class));
			context.setVariable("condi_preTransactorDutyLevel", factory.createValueExpression("", String.class));
			context.setVariable("condi_initiatorLeaderDutyLevel", factory.createValueExpression("", String.class));
			context.setVariable("condi_preTransactorLeaderDutyLevel", factory.createValueExpression("", String.class));
			context.setVariable("condi_initiatorOrgName", factory.createValueExpression("", String.class));
			
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_LEADER_ACCOUNT, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_DEPART_LEADER_ACCOUNT, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_CHARGE_LEADER_ACCOUNT, factory.createValueExpression("", String.class));
			
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_APPROVALLEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_ONELEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_TWOLEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_THREELEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_FOURLEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_FIVELEVEL, factory.createValueExpression("", String.class));
			context.setVariable(EzFlowFinals.CONDI_INITIATOR_SIXLEVEL, factory.createValueExpression("", String.class));
			
			for (int i = 0; i < fieldList.size(); i++) {
				// log.debug("add a variable " +
				// ((String[])fieldList.get(i))[0]);
				context.setVariable(((String[]) fieldList.get(i))[0], factory.createValueExpression("", String.class));
			}

			// 表单域
			try {
				ValueExpression e2 = factory.createValueExpression(context, expressstr, boolean.class);
				response.getWriter().write("<xml><success>1</success><result>" + e2.getValue(context) + "</result></xml>");
			} catch (Exception e) {
				e.printStackTrace();
				response.getWriter().write("<xml><success>-1</success><message><![CDATA[" + e.getMessage() + "]]></message></xml>");
			}
		} else {
			response.getWriter().write("<xml><success>1</success></xml>");
		}
		logger.debug("结束检查检查表达式是否正确");
		return null;
	}

	
	/**
	 * 根据formKey查询字段列表
	 * @return
	 * @throws Exception
	 */
	public String getFieldByForm() throws Exception {
        logger.debug("开始取表单字段");
		String formcode = request.getParameter("formKey"); 
		
		com.whir.ezoffice.bpm.bd.BPMFormInfoBD  formbd=new com.whir.ezoffice.bpm.bd.BPMFormInfoBD();
		List fieldList=formbd.getFieldsByFormCode(formcode, formType, (String) request.getSession().getAttribute("domainId"), ""+moduleId);
        
		//数据表名称
		String tableName="";
		if(moduleId==1){
			EzFormBD efbd=new EzFormBD();
			tableName=efbd.getTablePOByFormCode(formcode, (String) request.getSession().getAttribute("domainId")).getTableDesName();
			
		}
		logger.debug("field size is :" + fieldList.size());
		response.setContentType("text/xml;charset=UTF-8");
		response.setHeader("Cache_Control", "no-cache");
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
		String str = "<data><fields>";
		for (int i = 0; i < fieldList.size(); i++) {
			str += "<field><fieldid>" + ((String[]) fieldList.get(i))[0]
					+ "</fieldid>" + "<fieldtext>"
					+ ((String[]) fieldList.get(i))[1] + "</fieldtext>"
					+ "<fieldtype>" + ((String[]) fieldList.get(i))[2]
					+ "</fieldtype><tabletype>"
					+ ((String[]) fieldList.get(i))[3] + "</tabletype></field>";
		}
		str += "</fields>";

		// * String[3]-是否主表字段，“1”-主表字段 “0”-子表字段

		String[][] result = formbd.getRelaTrigByFormId(formcode,formType,""+moduleId);
		if (result != null) {
			response.setContentType("text/xml;charset=UTF-8");
			response.setHeader("Cache_Control", "no-cache");
			str += "<RelaTrigs>";
			for (int i = 0; i < result.length; i++) {
				str += "<RelaTrig><RelaTrigId>" + result[i][0]
						+ "</RelaTrigId>" + "<RelaTrigText>" + (result[i])[1]
						+ "</RelaTrigText></RelaTrig>";
			}
			str += "</RelaTrigs>";
		}  
		
		str+="<table><tableName>"+tableName+"</tableName></table>"; 
		str += "</data>"; 
		ezflowWriter(xml + str);
		logger.debug("结束取表单字段");
		return null;
	}
	
	
	public String viewDescription(){
		EzFlowProcessDefinitionService processService=new EzFlowProcessDefinitionService();
		//流程定义id
		String processDefinitionId=request.getParameter("processDefinitionId");
		Map processDefMap=processService.findProcessInfoById(processDefinitionId);	
		String description=processDefMap.get("documentation")==null?"":processDefMap.get("documentation").toString();
		request.setAttribute("description", description);
		logger.debug("");
		return   "description";
	}
	
	public String viewDescriptionByKey(){
		EzFlowProcessDefinitionService processService=new EzFlowProcessDefinitionService();
		//流程定义key
		String processKey=request.getParameter("processKey");
		Map processDefMap=processService.findProcessInfoByKey(processKey);
		String description=processDefMap.get("documentation")==null?"":processDefMap.get("documentation").toString();
		request.setAttribute("description", description);
		logger.debug("");
		return   "description";
	}
	
	private void ezflowOutputPrint(String s){
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);	
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		
		try {
			response.getOutputStream().print(s);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	private void ezflowOutputWriter(String s){
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);	
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
		logger.debug("----ezflowOutputWriter---");
		logger.debug(s);
		try {
			response.getOutputStream().write(s.getBytes("utf-8"));
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void ezflowWriter(String str){
		logger.debug("-----ezflowWriter----- \n"+str);
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		try {
			response.getWriter().write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void ezflowPrint(String str){
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");	
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/xml");
	
		response.setDateHeader("Expires", 0);
		try {
			response.getWriter().print(str);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 异步判断流程ID、流程名称是否重复
     * 2015-12-29
     */
    public String getProcessIdAndProcessName() throws Exception{
    	com.whir.service.api.ezflowservice.EzFlowDesignerService flowService = new com.whir.service.api.ezflowservice.EzFlowDesignerService();
    	String json = null;
    	String processId =request.getParameter("processId");
    	String processName =request.getParameter("processName");
    	
    	logger.debug("processId----->"+processId);
    	logger.debug("processName----->"+processName);
    	
    	Map queryMap = new HashMap();
		queryMap.put("processId", processId);
		queryMap.put("processName", processName);
		boolean isRepeat =false;
		if (flowService.checkProcessRepeat(queryMap)) {
			//ezflowPrint("<xml><result>-1</result><message>流程名或者流程ID重复</message></xml>");
			isRepeat =true;
		}
    	json ="{\"isRepeat\":\""+isRepeat+"\"}";
    	printJsonResult(json);
    	return null;
    }
	
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	/**
	 * 判断是否有重复
	 * @param list
	 * @return
	 */
	private String getRepeatedKey(List<String> list){
		String repeatStr="";
		if(list!=null&&list.size()>0){
			int size=list.size();
			String nowInfo="";
			for(int i=0;i<size;i++){
				int num=0;
				nowInfo=""+list.get(i);
				for(int j=0;j<size;j++){
					if(i!=j){
						if(nowInfo.equals(list.get(j))){
							num++;
						}
					}
				}
				if(num>0){
					repeatStr+=nowInfo+",";
				}
			}
		}
		return repeatStr;	
	}
	
	/**
	 * 
	 * @param list
	 * @return
	 */
	private String getDeleteProcessKey(List<String> list){
		logger.debug("getDeleteProcessKey");
		String deleteStr="";
		String sql ="select processid from ez_flow_de_designer t  where t.sub_type=1  and t.processid in (:keyList)";
		Dbutil dbutil=new Dbutil();
		Map varMap=new HashMap();
		varMap.put("keyList", list);
		try { 
			List nowList=dbutil.getDataListBySQL(sql, varMap);
			logger.debug("nowList.size:"+nowList.size());
			for(String str:list){
				logger.debug("str:"+str);
				int num=0;
				if(nowList!=null&&nowList.size()>0){
					for(int j=0;j<nowList.size();j++){
						Object obj[]=(Object [])nowList.get(j);
						logger.debug("j:"+obj[0]);
						if(str.equals(""+obj[0])){
							num++;
						}
					}
				}
				if(num==0){
					deleteStr+=str+",";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return deleteStr; 
	}

	public int getModuleId() {
		return moduleId;
	}
	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}
	
	
	/**
	 * 根据 moduleId 初始化 moduleVO
	 * 并且初始化权限码
	 */
	private void initModuleVO() {
		if (moduleVO == null) {
			ModuleBD moduleBD = new ModuleBD();
			moduleVO = moduleBD.getModule(moduleId);
		}	
		if (moduleVO!=null&&moduleVO.isProcRight()) { 	
			rightCode = moduleVO.getProcRightType();
		}  
	}

	public ModuleVO getModuleVO() {
		return moduleVO;
	}
	public void setModuleVO(ModuleVO moduleVO) {
		this.moduleVO = moduleVO;
	}
	public String getRightCode() {
		return rightCode;
	}
	public void setRightCode(String rightCode) {
		this.rightCode = rightCode;
	}
	public String getFormType() {
		return formType;
	}
	public void setFormType(String formType) {
		this.formType = formType;
	}
}
