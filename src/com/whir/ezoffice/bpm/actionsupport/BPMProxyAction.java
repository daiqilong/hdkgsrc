package com.whir.ezoffice.bpm.actionsupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.bpm.bd.BPMProxyBD;
import com.whir.ezoffice.bpm.po.BPMProxyPO; 
import com.whir.ezoffice.workflow.newBD.ProcessBD;
import com.whir.org.manager.bd.ManagerBD;

public class BPMProxyAction extends BaseActionSupport {
	
	private static Logger logger = Logger.getLogger(BPMProxyAction.class.getName());
	
	// 模块类code
	public static final String MODULE_CODE = "BPMProxyAction";

	private String proxyEmpName;
	private String proxystate;

	private String id;

	private BPMProxyPO po;

	// system 表示来之系统管理设置
	private String from = "";

	// 开始时间
	private String beginTimeStr;
	private String endTimeStr;

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	
	public String add(){
		logger.debug("开始进入新增代理页面");
		HttpSession session=request.getSession();
		po=new BPMProxyPO();
		po.setDomainId(session.getAttribute("domainId")+"");
		po.setCreateEmpId(new Long(""+session.getAttribute("userId")));
		po.setCreateEmpName(""+session.getAttribute("userName"));
		po.setCreateOrgId(new Long(""+session.getAttribute("orgId")));
		//默认有效
		po.setProxyState(1);
		//默认全部流程
		po.setProxyAllProcess(1);
		if(from.equals("system")){
			
		}else{
			po.setEmpId(new Long(""+session.getAttribute("userId")));
			po.setEmpName(""+session.getAttribute("userName"));
		}
		
		po.setBeginTime(new java.util.Date());
		po.setEndTime(new java.util.Date());
		
		//取出全部流程
		loadProcess();
		logger.debug("结束进入新增代理页面");
		return "add";
	}
	
	
	public String modify(){
		logger.debug("开始进入修改代理页面");
		//根据id 取出代理信息
		load();
		//取出全部流程
	    loadProcess();
		logger.debug("结束进入修改代理页面");
		return "modify";
	}
	
	
	
	private void load(){
		BPMProxyBD bd=new BPMProxyBD();
		po = bd.load(new Long(id));
	    beginTimeStr=simpleDateFormat.format(po.getBeginTime());
	    endTimeStr=simpleDateFormat.format(po.getEndTime());	
	}
	
	
	private void loadProcess() {
		// 显示流程
		ProcessBD procBD = new ProcessBD();
		HttpSession session=request.getSession();
		String domainId=session.getAttribute("domainId")+"";
		List processList = procBD.getAllProcess(domainId);

		java.util.ArrayList packageList = new java.util.ArrayList();
		if (processList != null) {
			String upPackageId = "";
			for (int i = 0; i < processList.size(); i++) {
				Object[] processObj = (Object[]) processList.get(i);
				if (!upPackageId.equals(processObj[0].toString())) {
					upPackageId = processObj[0].toString();
					Object[] packageObj = { processObj[0], processObj[1] };
					packageList.add(packageObj);
				}
			}
		}
		request.setAttribute("packageList", packageList);
		request.setAttribute("processList", processList);
	}

	
	/**
	 * 修改保存
	 * @return
	 */
	public String save(){
		logger.debug("开始保存代理"); 
		logger.debug("po.beginTime:"+request.getParameter("po.beginTime"));
		//logger.debug("po.getBeginTime().toLocaleString():"+po.getBeginTime().toLocaleString());
		
		String beginTime=request.getParameter("po.beginTime");
		String endTime=request.getParameter("po.endTime");
		if(beginTime!=null&&!beginTime.equals("")&&!beginTime.equals("null")){ 
			try {
				po.setBeginTime(simpleDateFormat2.parse(beginTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	    if(endTime!=null&&!endTime.equals("")&&!endTime.equals("null")){
	    	try {
				po.setEndTime(simpleDateFormat2.parse(endTime));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
		HttpSession session=request.getSession();
		String userId=""+session.getAttribute("userId")+"";
		String userName=""+session.getAttribute("userName");
		String domainId=""+session.getAttribute("domainId");
		
        BPMProxyBD bd=new BPMProxyBD();
        
        
        String processIdArr[] = request.getParameterValues("processId");
        String bpmProcessIds="";
        if(processIdArr!=null&&processIdArr.length>0){
        	for(String str:processIdArr){
        		bpmProcessIds+="$"+str+"$";
        	}
        }
        po.setProxyBPMProcessID(bpmProcessIds);
        
        //保存或者 修改 的 结果
        String saveorupdateResult="";
		
		//id 不为空则为修改
		if(id!=null&&!id.equals("")&&!id.equals("null")){
			saveorupdateResult=bd.update(po,new Long(id+""), new Long(userId));
			// -------增加日志信息----2009-06-30-----start
			if ("system".equals(request.getParameter("from"))) {
				String logContent = "代理人：" + po.getProxyEmpName() + "，被代理人：" + po.getEmpName();
				com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
				java.util.Date date = new java.util.Date();
				logBD.log(userId.toString(), userName,
						session.getAttribute("orgName").toString(),
						"system_work_agent", "系统管理", date, date, "2",
						logContent, session.getAttribute("userIP").toString(),
						domainId);
			}
		}else{
			//新增
			po.setCreateTime(new java.util.Date());
			saveorupdateResult=bd.add(po, new Long(userId));
			if ("system".equals(request.getParameter("from"))) {
				String logContent = "代理人：" + po.getProxyEmpName()+ "，被代理人：" + po.getEmpName();
				com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
				java.util.Date date = new java.util.Date();
				logBD.log(userId, userName, session .getAttribute("orgName").toString(),
						"system_work_agent", "系统管理", date, date, "1",
						logContent, session.getAttribute("userIP").toString(), domainId);
			}
		} 
        bd.setUnavailableProxy(); 
        if(saveorupdateResult.equals("repeat")){
        	this.printResult("代理流程已设置，不能重复设置。");
        }else{
        	this.printResult(this.SUCCESS);
		}
        logger.debug("结束保存代理");
		return null;
	}
	
	
	
	public String delete() {
		logger.debug("开始删除代理");
		logger.debug("id:"+id);
		BPMProxyBD bd=new BPMProxyBD();
		HttpSession session=request.getSession();
		String curUserId=""+session.getAttribute("userId");
		String curUserName=""+session.getAttribute("userName");
		String domainId=""+session.getAttribute("domainId");
		
		
		String sysManager  = session!=null?session.getAttribute("sysManager")+"":"";
		String userAccount = session!=null?session.getAttribute("userAccount")+"":"";
		boolean sysRole=false, userRole=false, defineRole=false;
		if(sysManager.indexOf("1")>=0){sysRole=true;userRole=true;defineRole=true;}
		if(sysManager.indexOf("2")>=0){userRole=true;}
		
		
		
		String[] idAry = null;
		String logContent = "";
		if (null != id) {
			String ids = ""+id; 
			logger.debug("ids:"+ids);
			logger.debug("curUserId:"+curUserId);
			if (ids.endsWith(",")) {
				ids=ids.substring(0, ids.length()-1);
			}  
			idAry = ids.split(",");
			
			//来自系统管理
			if ("system".equals(from)) {
				//youguanli quanxian  de caiengn  
				if(sysRole||userRole){ 
				}else{
					this.printResult(NO_ILLEGAL);
					return null;
				}     
				for (int s = 0; s < idAry.length; s++) {
					BPMProxyPO po = bd.load(new Long(idAry[s]));
					if (po == null)
						continue;
					String personName1 = po.getProxyEmpName() != null ? po.getProxyEmpName() : "";
					String personName2 = po.getEmpName() != null ? po.getEmpName() : "";
					logContent += "代理人：" + personName1 + "，被代理人：" + personName2+ "；";
				}
			}else if("my".equals(from)){
				//ruogu
				boolean result=bd.judgeHaveAllMyId(idAry, new Long(curUserId));
				if(!result){
					this.printResult(NO_ILLEGAL);
					return null; 
				}
			}else{
				this.printResult(NO_ILLEGAL);
				return null; 
			} 
			bd.delBatch(ids, new Long(curUserId));  
		}else{
			this.printResult(NO_ILLEGAL);
			return null;
		}

		// -------增加日志信息----2009-06-30-----start
		if ("system".equals(request.getParameter("from"))) {
			if (logContent.length() > 1) {
				logContent = logContent.substring(0, logContent.length() - 1);
			}
			com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
			java.util.Date date = new java.util.Date();
			logBD.log(curUserId.toString(), curUserName,
					session.getAttribute("orgName").toString(),
					"system_work_agent", "系统管理", date, date, "3", logContent,
					session.getAttribute("userIP").toString(), domainId);
		}
		logger.debug("结束删除");
		this.printResult(this.SUCCESS);
		return null;
	}
	
	 
	
	public String  fileList(){
		logger.debug("开始进入工作代理列表页面");
	    //设置所有过期的代理为无效代理
		BPMProxyBD bd=new BPMProxyBD();
        bd.setUnavailableProxy();
		logger.debug("结束进入工作代理列表页面");
		return "fileList";
	}
	
	
	public String list(){
		logger.debug("开始异步取代理列表数据");
		HttpSession session = request.getSession();
		/*
		 * pageSize每页显示记录数，即list.jsp中分页部分select中的值
		 */
		int pageSize = com.whir.common.util.CommonUtils.getUserPageSize(request);
		/*
		 * orderByFieldName，用来排序的字段
		 */
		String orderByFieldName = request.getParameter("orderByFieldName") != null ? request.getParameter("orderByFieldName") : "";
		/*
		 * orderByType，排序的类型
		 */
		String orderByType = request.getParameter("orderByType") != null ? request.getParameter("orderByType") : "";
		/*
		 * currentPage，当前页数
		 */
		int currentPage = 0;
		if (request.getParameter("startPage") != null) {
			currentPage = Integer.parseInt(request.getParameter("startPage"));
		}
		String viewSQL = " po.id,po.proxyEmpName,po.beginTime,po.endTime,po.proxyState,po.empId,po.createEmpName,po.createTime,po.empName,po.proxyEmpId ";

		String fromSQL = " com.whir.ezoffice.bpm.po.BPMProxyPO po";
		String whereSQL = " where po.domainId=:domainId ";
		String order = " order by po.createTime desc ";
		
		/*
		 * varMap，查询参数Map
		 */
		Map varMap = new HashMap();
		varMap.put("domainId",session.getAttribute("domainId")+"");

		//if(proxyEmpName!=null && !proxyEmpName.equals("null") && !proxyEmpName.equals("")){
		if(proxyEmpName!=null && !proxyEmpName.equals("")){
			whereSQL+=" and  po.proxyEmpName  like :proxyEmpName ";
			varMap.put("proxyEmpName", "%"+proxyEmpName+"%");
        }
		if(proxystate!=null && !proxystate.equals("null") && !proxystate.equals("")){
			whereSQL+="  and po.proxyState=:proxystate ";
			varMap.put("proxystate", proxystate);
        }
 
		
		if ("system".equals(from)) {
			// --------20090204 start---new add ---------
			// 判断权限范围,范围同当前用户列表显示权限一致
			ManagerBD managerBD = new ManagerBD();
			String rightName = "00*01*02";
			if ("1".equals(session.getAttribute("sysManager").toString()))
				rightName = "00*01*01";

			String whereTmp = managerBD.getRightWhere(
					session.getAttribute("userId").toString(), 
					session.getAttribute("orgId").toString(), rightName, "organization.orgId", "user.empId");
			if (whereTmp.equals("")) {
				whereTmp = "1<1";
			}
			if (whereTmp != null && !whereTmp.equals("")) {
				whereSQL += " and " + whereTmp;
			}
 
			fromSQL = " com.whir.ezoffice.bpm.po.BPMProxyPO  po ,"
					+ "com.whir.org.vo.usermanager.EmployeeVO AS user join user.organizations organization ";

			whereSQL += "  and po.empId=user.empId ";
		} else {
			// 自己的
			whereSQL += "  and po.empId=:userId ";
			varMap.put("userId", "" + session.getAttribute("userId"));
		}
		
        //排序
		if (orderByFieldName != null && !"".equals(orderByFieldName)) {
			order = " order by po." + orderByFieldName + " " + orderByType;
		}
		
		/*
		 * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
		 */
		Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL, order);
		page.setPageSize(pageSize);
		page.setCurrentPage(currentPage);
		/*
		 * page.setVarMap(varMap);若没有查询条件，可略去此行
		 */
		page.setVarMap(varMap);
		/*
		 * list，数据结果
		 */
		List list = page.getResultList();
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
		JacksonUtil util = new JacksonUtil();
 
		//"buttonId" 为生成验证码的列名，    this.MODULE_CODE  为模块code  默认为本类的名
		String json = util.writeArrayJSON(viewSQL, list,"id",this.MODULE_CODE);
		json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount + "},data:" + json + "}";
		printResult(G_SUCCESS,json);
		logger.debug("结束异步取代理列表数据");
		return null;
	}
	
	public String getProxyEmpName() {
		return proxyEmpName;
	}
	public void setProxyEmpName(String proxyEmpName) {
		this.proxyEmpName = proxyEmpName;
	}
	public String getProxystate() {
		return proxystate;
	}
	public void setProxystate(String proxystate) {
		this.proxystate = proxystate;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public BPMProxyPO getPo() {
		return po;
	}
	public void setPo(BPMProxyPO po) {
		this.po = po;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getBeginTimeStr() {
		return beginTimeStr;
	}
	public void setBeginTimeStr(String beginTimeStr) {
		this.beginTimeStr = beginTimeStr;
	}
	public String getEndTimeStr() {
		return endTimeStr;
	}
	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
	}
}
