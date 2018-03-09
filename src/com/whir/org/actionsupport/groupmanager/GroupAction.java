package com.whir.org.actionsupport.groupmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.ConversionString;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.security.crypto.EncryptUtil;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.bd.groupmanager.GroupClassBD;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.bd.usermanager.UserBD;
import com.whir.org.common.util.ConvertIdAndName;
import com.whir.org.common.util.EndowVO;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.org.vo.groupmanager.GroupClassVO;
import com.whir.org.vo.groupmanager.GroupVO;

/**
 * 群组设置
 * 
 * @author wanghx
 * 
 */
public class GroupAction extends BaseActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger
            .getLogger(GroupAction.class.getName());
    
    public static final String MODULE_CODE = "GroupAction";
 // 系统管理员 权限
	private static final String SYS_MANAGE_RIGHT = "00*01*01";
	// 普通管理员 权限
	private static final String SYS_MANAGE_RIGHT_USER = "00*01*02";
    private GroupVO groupVO;

    // 1-个人群组 0-公共群组
    private String groupType;

    // 查询条件
    private String groupId;
    private String groupName;
    private String groupUserNames;
    private String classId;

    private String rangeId;
    private String createdOrg;

    public String initList() throws Exception {
        
        groupType = EncryptUtil.htmlcode(groupType);
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        GroupClassBD classBD = new GroupClassBD();
        List classList = classBD.getGroupClassList("", domainId + "");
        request.setAttribute("classList", classList);
            
        return "initList";
    }

    /**
     * 群组列表
     * 
     * @return
     * @throws Exception
     */
    public String listGroup() throws Exception {

        logger.debug("查询列表开始");

        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        // String orgIdString = session.getAttribute("orgIdString").toString();

        String rightCode = "00*01*02";
        if ("1".equals(session.getAttribute("sysManager").toString())) {
            rightCode = "00*01*01";
        }

        /*
         * pageSize每页显示记录数，即list.jsp中分页部分select中的值
         */
        int pageSize = com.whir.common.util.CommonUtils
                .getUserPageSize(request);

        /*
         * currentPage，当前页数
         */
        int currentPage = 0;
        if (request.getParameter("startPage") != null) {
            currentPage = Integer.parseInt(request.getParameter("startPage"));
        }

        String viewSQL = "aaa.groupId,aaa.groupName,aaa.groupUserNames,emp.empName,aaa.groupUserString,aaa.rangeName, c.className, aaa.groupOrder, aaa.groupType, aaa.rangeEmp, aaa.rangeOrg, aaa.rangeGroup ";
        String fromSQL = "com.whir.org.vo.groupmanager.GroupVO aaa left join aaa.classVO c , com.whir.org.vo.usermanager.UserPO emp";
        String whereSQL = "where aaa.domainId=" + domainId;

        if ("1".equals(session.getAttribute("sysManager").toString())) {
            whereSQL += " and aaa.createdEmp=emp.empId ";
        } else {
            ManagerBD bd = new ManagerBD();
            whereSQL += " and aaa.createdEmp=emp.empId and (emp.empId="
                    + userId
                    + " or ("
                    + bd.getRightFinalWhere(userId, orgId, rightCode,
                            "aaa.createdOrg", "aaa.createdEmp") + "))";
        }

        if ("1".equals(groupType)) {
            whereSQL += " and aaa.groupType=1 and emp.empId=" + userId;
        } else {
            whereSQL += " and aaa.groupType=0";
        }

        logger.debug("groupName:" + groupName);
        logger.debug("groupUserNames:" + groupUserNames);

        /*
         * varMap，查询参数Map
         */
        Map varMap = new HashMap();
        if (!CommonUtils.isEmpty(groupName)) {
            whereSQL += " and aaa.groupName like :groupName ";

            varMap.put("groupName", "%" + groupName + "%");
        }

        if (!CommonUtils.isEmpty(groupUserNames)) {
            whereSQL += " and aaa.groupUserNames like :groupUserNames ";

            varMap.put("groupUserNames", "%" + groupUserNames + "%");
        }
        
        if (!CommonUtils.isEmpty(classId)) {
            whereSQL += " and c.id = :classId ";

            varMap.put("classId", classId);
        }

        // 排序
        String orderBy = " order by ";
        orderBy += " c.sortNo, c.id, aaa.groupOrder, aaa.groupName, aaa.groupId desc";

        /*
         * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
         */
        Page page = PageFactory.getHibernatePage(viewSQL, fromSQL, whereSQL,
                orderBy);
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

        // ---自定义群组列表显示组用户 过滤上组用户中已经被禁用或删除的---start------
        UserBD userBD = new UserBD();
        DbOpt dbopt = new DbOpt();
        List listTemp = new ArrayList();
        if (list != null && list.size() > 0) {
            try {
                for (int s = 0, j=list.size(); s < j; s++) {
                    Object[] objAry = (Object[]) list.get(s); // objAry[2]组用户名称串，objAry[4]组用户id串
                    // -------判断组用户是否被禁用或删除---然后重新组成组用户id串和组用户名称串,格式
                    // $id1$$id2$$id3$ 用户1,用户2,用户3,-----start-----
                    String groupUserIdString = "";
                    String groupUserNames = "";
                    if (objAry[4] != null && objAry[4].toString().length() > 0) {
                        String groupUserIdsTemp = objAry[4].toString()
                                .replaceAll("\\$", ",");
                        groupUserIdsTemp = groupUserIdsTemp.replaceAll(",,",
                                ",");
                        groupUserIdsTemp = groupUserIdsTemp.replaceAll("null",
                                "");
                        String[] groupUserIdsTempAry = groupUserIdsTemp
                                .substring(1, groupUserIdsTemp.length() - 1)
                                .split(",");
                        
                        logger.debug("groupUserIdsTemp:"+groupUserIdsTemp);
                        
                        String groupUserNamesTemp = objAry[2].toString();
                        logger.debug("groupUserNamesTemp:"+groupUserNamesTemp);
                        
                        /*groupUserNamesTemp = groupUserNamesTemp.replaceAll(
                                "null", "");
                        String[] groupUserNamesTempAry = groupUserNamesTemp
                                .substring(0, groupUserNamesTemp.length() - 1)
                                .split(",");*/
                        for (int sInt = 0, n=groupUserIdsTempAry.length; sInt < n; sInt++) {
                            String[][] empInfo = dbopt.executeQueryToStrArr2(
                                    "select userisactive, userisdeleted, empname from org_employee where emp_id="
                                            + groupUserIdsTempAry[sInt], 3);
                            if(empInfo!=null && empInfo.length>0 && empInfo[0].length>0){
	                            if ("1".equals(empInfo[0][0] + "")
	                                    && "0".equals(empInfo[0][1] + "")) { // 该用户是活动的并且是未被删除的
	                                groupUserIdString = groupUserIdString + "$"
	                                        + groupUserIdsTempAry[sInt] + "$";
	                                groupUserNames = groupUserNames + empInfo[0][2]
	                                        + ",";
	                                // + groupUserNamesTempAry[sInt] + ",";
	                            }
                            }
                        }
                    }

                    objAry[4] = groupUserIdString; // 拼装后的组用户id串
                    objAry[2] = groupUserNames; // 拼装后的组用户名称串
                    // -------判断组用户是否被禁用或删除---然后重新组成组用户id串和组用户名称串,格式
                    // $id1$$id2$$id3$ 用户1,用户2,用户3,-----end-----
                    
                    //--过滤适用范围用户名修改--start
                    String rangeEmp = (String)objAry[9];
                    String rangeOrg = (String)objAry[10];
                    String rangeGroup = (String)objAry[11];
                    String rangeIdStr = "";
                    
                    if(!CommonUtils.isEmpty(rangeOrg)){
                        rangeIdStr += rangeOrg;
                    }
                    if(!CommonUtils.isEmpty(rangeEmp)){
                        rangeIdStr += rangeEmp;
                    }
                    if(!CommonUtils.isEmpty(rangeGroup)){
                        rangeIdStr += rangeGroup;
                    }
                    
                    Map map = userBD.filterUserOrgGroup(rangeIdStr, false, "", "id", "zh_CN");
                    String rangeNameStr = (String)map.get("scopeNameStr");
                    objAry[5] = rangeNameStr;
                    //--过滤适用范围用户名修改--end
                    
                    String _groupOrder = objAry[7] == null ? "1000" : objAry[7] + "";
                    if(_groupOrder.endsWith(".0000")){
                        _groupOrder = _groupOrder.substring(0, _groupOrder.length()-5);
                    }
                    objAry[7] = _groupOrder;
                    
                    listTemp.add(objAry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    dbopt.close();
                } catch (Exception e) {
                }
            }
        }
        // ---自定义群组列表显示组用户 过滤上组用户中已经被禁用或删除的---end--------

        /*
         * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
         */
        String[] arr = { "groupId", "groupName", "groupUserNames", "empName",
                "groupUserString", "rangeName", "className", "groupOrder",
                "groupType" };
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, listTemp, "groupId", MODULE_CODE);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";

        // System.out.println("----[json]:"+json);
        printResult(G_SUCCESS, json);

        logger.debug("查询列表结束");

        return null;
    }

    /**
     * 新增、修改数据初始化
     * 
     * @throws Exception
     */
    public void initData() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        
        request.setAttribute("createdOrg", orgId);

        String rightCode = "1".equals(session.getAttribute("sysManager")
                .toString()) ? "00*01*01" : "00*01*02";

        ManagerBD managerBD = new ManagerBD();
        List list = managerBD.getRightScope(userId, rightCode);

        Object[] obj = (list == null || list.size() < 1) ? null
                : (Object[]) list.get(0);

        String type = "";
        if (obj != null && obj.length > 1) {
            type = obj[0].toString();
        }

        if ("4".equals(type)) {
            String range = (String) obj[1];
            if (range != null && !"".equals(range) && !"null".equals(range)) {
                if (range.indexOf("**") > 0) {
                    list = new OrganizationBD().getNameAndId(range);
                    // 如果管理员的可管理组织多于一个在需要指定一个组织
                    request.setAttribute("multiRange", "1");
                    request.setAttribute("managerRange", list);
                } else {
                    if (range.equals("0")) {
                        request.setAttribute("managerRange", "0");
                    } else {
                        request.setAttribute("managerRange", range.substring(1,
                                range.length() - 1));
                    }
                }
            } else {
                request.setAttribute("managerRange", "0");
            }

        } else if ("0".equals(type)) {
            request.setAttribute("managerRange", "0");

        } else {
            request.setAttribute("managerRange", orgId);//"0");
        }

        GroupClassBD classBD = new GroupClassBD();
        List classList = classBD.getGroupClassList("", domainId + "");
        request.setAttribute("classList", classList);

        // -----------如果是系统管理员 则选人范围要和管理员权限一致，否则按照浏览范围来选人------------------start
        String managerScope = "*" + orgId + "*";
        if ("1".equals(session.getAttribute("sysManager").toString())) {
            managerScope = "*0*";
        } else {
            List list_ = new com.whir.org.manager.bd.ManagerBD().getRightScope(
                    userId, "00*01*02");
            if (list_ != null && list_.size() > 0) {
                Object[] obj_ = (Object[]) list_.get(0);
                String type_ = "";
                if (obj_ != null) {
                    type_ = obj_[0].toString();
                }

                if ("4".equals(type_)) {
                    if (obj_[1] != null) {
                        managerScope = obj_[1].toString();
                    }
                }
            }
        }
        request.setAttribute("managerScope", managerScope);
        // -----------如果是系统管理员 则选人范围要和管理员权限一致，否则按照浏览范围来选人--------------------end

    }

    /**
     * 进入新增页面
     * 
     * @return
     * @throws Exception
     */
    public String addGroup() throws Exception {
        initData();
        
        GroupVO vo = new GroupVO();
        vo.setGroupOrder("1000");
        this.setGroupVO(vo);

        return "addGroup";
    }

    /**
     * 保存群组
     * 
     * @return
     * @throws Exception
     */
    public String saveGroup() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = CommonUtils.getSessionUserId(request) + "";
        String orgId = CommonUtils.getSessionOrgId(request) + "";
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String userIP = session.getAttribute("userIP").toString();

        logger.debug("" + groupVO.getGroupUserNames());
        logger.debug("" + groupVO.getGroupUserString());
        logger.debug("" + groupVO.getGroupCode());
        logger.debug("" + groupVO.getGroupName());
        logger.debug("" + groupVO.getRangeName());
        logger.debug("" + groupVO.getGroupOrder());
        logger.debug("" + groupVO.getGroupType());

        groupVO.setCreatedOrg(createdOrg);
        groupVO.setCreatedEmp(userId);
        groupVO.setDomainId(domainId + "");
        groupVO.setGroupType(groupType);

        GroupBD bd = new GroupBD();

        boolean flag = bd.checkGroupCode(groupVO);
        if (flag) {
            printResult("该群组编码已存在，请重新输入！");

            return null;
        }

        Date startDate = new Date();

        ConvertIdAndName cIdAndName = new ConvertIdAndName();
        EndowVO endowVO = cIdAndName.splitId(groupVO.getGroupUserString());
        String strId = endowVO.getEmpIdArray();

        ConversionString conversionString = new ConversionString(rangeId);
        String scopeUserIds = conversionString.getUserString();
        String scopeOrgIds = conversionString.getOrgString();
        String scopeGroupIds = conversionString.getGroupString();
        groupVO.setRangeEmp(scopeUserIds);
        groupVO.setRangeOrg(scopeOrgIds);
        groupVO.setRangeGroup(scopeGroupIds);

        int result = bd.add(groupVO, strId.split(","));
        if (result > 0) {
            printResult("该群组名称已存在，请重新输入！");
            
            return null;
        } else {
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            Date endDate = new Date();
            if ("0".equals(groupType)) {
                logBD.log(userId, userName, orgName, "system_group", "系统管理",
                        startDate, endDate, "1", groupVO.getGroupName(),
                        userIP, domainId + "");

            } else {
                logBD.log(userId, userName, orgName, "oa_personwork_group",
                        "个人办公-个人设置", startDate, endDate, "1", "自定义群组", userIP,
                        domainId + "");
            }

            printResult("success");
        }

        return null;
    }

    public String loadGroup() throws Exception {
    	if("0".equals(groupType)){
    	   if(!this.judgeCallRight("", SYS_MANAGE_RIGHT)&&!this.judgeCallRight("", SYS_MANAGE_RIGHT_USER)){
               return this.NO_RIGHTS;
           }
    	}
//    	if (!checkOperateRights()) {
//    	      return "noright";
//    	    }
       
        
        if(CommonUtils.isEmpty(groupId, true)){
            return this.NO_RIGHTS;
        }

        initData();

        GroupVO vo = new GroupVO();
        GroupBD bd = new GroupBD();

        List list = bd.selectSingle(groupId);
        Object[] obj = (Object[]) list.get(0);

        // ---自定义群组列表显示组用户 过滤上组用户中已经被禁用或删除的---start------
        // -------判断组用户是否被禁用或删除---然后重新组成组用户id串和组用户名称串,格式 $id1$$id2$$id3$
        // 用户1,用户2,用户3,-----start-----
        
        String groupUserIdString = obj[1] != null? obj[1].toString() : "";
        String groupUserNames = obj[2] != null? obj[2].toString() : "";
        
        /*if (obj[1] != null && obj[1].toString().length() > 0) {
            DbOpt dbopt = new DbOpt();
            try {
                String groupUserIdsTemp = obj[1].toString().replaceAll("\\$",
                        ",");
                groupUserIdsTemp = groupUserIdsTemp.replaceAll(",,", ",");
                groupUserIdsTemp = groupUserIdsTemp.replaceAll("null", "");
                String[] groupUserIdsTempAry = groupUserIdsTemp.substring(1,
                        groupUserIdsTemp.length() - 1).split(",");
                
                logger.debug("groupUserIdsTemp:"+groupUserIdsTemp);
                
                String groupUserNamesTemp = obj[2].toString();
                logger.debug("groupUserNamesTemp:"+groupUserNamesTemp);
                
                for (int sInt = 0; sInt < groupUserIdsTempAry.length; sInt++) {

                    String[][] empInfo = dbopt.executeQueryToStrArr2(
                            "select userisactive, userisdeleted, empname from org_employee where emp_id="
                                    + groupUserIdsTempAry[sInt], 3);

                    if ("1".equals(empInfo[0][0] + "")
                            && "0".equals(empInfo[0][1] + "")) { // 该用户是活动的并且是未被删除的
                        groupUserIdString = groupUserIdString + "$"
                                + groupUserIdsTempAry[sInt] + "$";
                        groupUserNames = groupUserNames + empInfo[0][2] + ",";
                        // + groupUserNamesTempAry[sInt] + ",";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    dbopt.close();
                } catch (Exception e) {
                }
            }
        }*/
        // -------判断组用户是否被禁用或删除---然后重新组成组用户id串和组用户名称串,格式 $id1$$id2$$id3$
        // 用户1,用户2,用户3,-----end-----
        vo.setGroupUserString(groupUserIdString);
        vo.setGroupUserNames(groupUserNames);
        // ---自定义群组列表显示组用户 过滤上组用户中已经被禁用或删除的---end--------

        vo.setGroupId(new Long(groupId));
        vo.setGroupName(obj[0] + "");
        vo.setCreatedOrg(obj[3] + "");
        //vo.setRangeName(obj[4] == null ? "" : obj[4] + "");

        String rangeId_ = "";
        rangeId_ += obj[6] == null ? "" : obj[6] + "";
        rangeId_ += obj[5] == null ? "" : obj[5] + "";
        rangeId_ += obj[7] == null ? "" : obj[7] + "";
        
        //--过滤适用范围用户名修改--start
        UserBD userBD = new UserBD();        
        Map map = userBD.filterUserOrgGroup(rangeId_, false, "", "id", "zh_CN");
        String rangeNameStr = (String)map.get("scopeNameStr");
        vo.setRangeName(rangeNameStr);
        
        rangeId_ = (String)map.get("scopeIdStr");
        //--过滤适用范围用户名修改--end

        this.setRangeId(rangeId_);
        
        String _groupOrder = obj[8] == null ? "1000" : obj[8] + "";
        if(_groupOrder.endsWith(".0000")){
            _groupOrder = _groupOrder.substring(0, _groupOrder.length()-5);
        }

        vo.setGroupOrder(_groupOrder);
        vo.setGroupCode(obj[10] == null ? "" : obj[10] + "");
        GroupClassVO classVO = new GroupClassVO();
        classVO.setId(obj[9] == null ? new Long(0)
                : new Long(obj[9].toString()));
        vo.setClassVO(classVO);
        
        vo.setGroupDescription(obj[11] == null ? "" : obj[11] + "");
        
        createdOrg = vo.getCreatedOrg();
        
        request.setAttribute("createdOrg", createdOrg);
        
        logger.debug("createdOrg:"+createdOrg);

        this.setGroupVO(vo);

        return "loadGroup";
    }

    /**
     * 修改群组
     * @return
     * @throws Exception
     */
    public String modifyGroup() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = CommonUtils.getSessionUserId(request) + "";
        String orgId = CommonUtils.getSessionOrgId(request) + "";
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String userIP = session.getAttribute("userIP").toString();

        logger.debug("" + groupVO.getGroupUserNames());
        logger.debug("" + groupVO.getGroupUserString());
        logger.debug("" + groupVO.getGroupCode());
        logger.debug("" + groupVO.getGroupName());
        logger.debug("" + groupVO.getRangeName());
        logger.debug("" + groupVO.getGroupOrder());
        logger.debug("" + groupVO.getGroupType());

        groupVO.setCreatedOrg(createdOrg);
        groupVO.setCreatedEmp(userId);
        groupVO.setDomainId(domainId + "");
        groupVO.setGroupType(groupType);
        
        logger.debug("createdOrg:"+createdOrg);

        GroupBD bd = new GroupBD();

        boolean flag = bd.checkGroupCode(groupVO);
        if (flag) {
            printResult("该群组编码已存在，请重新输入！");

            return null;
        }

        Date startDate = new Date();

        ConvertIdAndName cIdAndName = new ConvertIdAndName();
        EndowVO endowVO = cIdAndName.splitId(groupVO.getGroupUserString());
        String strId = endowVO.getEmpIdArray();

        ConversionString conversionString = new ConversionString(rangeId);
        String scopeUserIds = conversionString.getUserString();
        String scopeOrgIds = conversionString.getOrgString();
        String scopeGroupIds = conversionString.getGroupString();
        
        String groupClassId = "";
        if("0".equals(groupType)){
            groupClassId = groupVO.getClassVO().getId() + "";
        }

        int result = bd.update(groupVO.getGroupId()+"", groupVO.getGroupName(), groupVO
                .getGroupUserString(), strId.split(","), groupVO
                .getGroupUserNames(), createdOrg, groupVO.getRangeName(),
                scopeUserIds, scopeOrgIds, scopeGroupIds, groupType, groupVO
                        .getGroupOrder(), groupClassId,
                groupVO.getGroupCode(), groupVO.getGroupDescription());

        if (result > 0) {
            printResult("该群组名称已存在，请重新输入！");

            return null;
        } else {
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            Date endDate = new Date();
            if ("0".equals(groupType)) {
                logBD.log(userId, userName, orgName, "system_group", "系统管理",
                        startDate, endDate, "2", groupVO.getGroupName(),
                        userIP, domainId + "");

            } else {
                logBD.log(userId, userName, orgName, "oa_personwork_group",
                        "个人办公-个人设置", startDate, endDate, "2", "自定义群组", userIP,
                        domainId + "");
            }

            printResult("success");
        }

        return null;
    }

    /**
     * 单个删除
     * 
     * @return
     * @throws Exception
     */
    public String deleteGroup() throws Exception {
    	if("0".equals(groupType)){
    	 if(!this.judgeCallRight("", SYS_MANAGE_RIGHT)&&!this.judgeCallRight("", SYS_MANAGE_RIGHT_USER)){
             return this.NO_RIGHTS;
         }
    	}
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = CommonUtils.getSessionUserId(request) + "";
        String orgId = CommonUtils.getSessionOrgId(request) + "";
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String userIP = session.getAttribute("userIP").toString();

        Date startDate = new Date();

        if (!CommonUtils.isEmpty(groupId)) {
            GroupBD bd = new GroupBD();

            String[] ids = groupId.split(",");

            String names = bd.del(ids);

            if (!CommonUtils.isEmpty(names)) {
                com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
                Date endDate = new Date();
                if ("0".equals(groupType)) {
                    logBD.log(userId, userName, orgName, "system_group",
                            "系统管理", startDate, endDate, "3", names, userIP,
                            domainId + "");

                } else {
                    logBD.log(userId, userName, orgName, "oa_personwork_group",
                            "个人办公-个人设置", startDate, endDate, "3", "自定义群组",
                            userIP, domainId + "");

                }
                printResult("success");
            }
        }

        return null;
    }

    /**
     * 批量删除
     * 
     * @return
     * @throws Exception
     */
    public String batchDelGroup() throws Exception {
    	if("0".equals(groupType)){
    	 if(!this.judgeCallRight("", SYS_MANAGE_RIGHT)&&!this.judgeCallRight("", SYS_MANAGE_RIGHT_USER)){
             return this.NO_RIGHTS;
         }
    	}
        return deleteGroup();
    }

    public GroupVO getGroupVO() {
        return groupVO;
    }

    public void setGroupVO(GroupVO groupVO) {
        this.groupVO = groupVO;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupUserNames() {
        return groupUserNames;
    }

    public void setGroupUserNames(String groupUserNames) {
        this.groupUserNames = groupUserNames;
    }

    public String getRangeId() {
        return rangeId;
    }

    public void setRangeId(String rangeId) {
        this.rangeId = rangeId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreatedOrg() {
        return createdOrg;
    }

    public void setCreatedOrg(String createdOrg) {
        this.createdOrg = createdOrg;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    /**
     * 判断权限
     */
    public boolean checkOperateRights() throws Exception {
        HttpSession session = request.getSession(true);

        String sysManager = session != null ? session
                .getAttribute("sysManager")
                + "" : "";
        boolean sysRole = false;
        boolean userRole = false;

        if (sysManager.indexOf("1") >= 0) {
            sysRole = true;
            userRole = true;
            return true;
        }

        if (sysManager.indexOf("2") >= 0) {
            userRole = true;
            return true;
        }

        if (!sysRole && !userRole) {
            return false;
        }

        return false;
    }
}
