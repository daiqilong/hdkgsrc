package com.whir.org.actionsupport.usermanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.Constants;
import com.whir.common.util.MD5;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.config.ConfigXMLReader;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.ezoffice.personnelmanager.bd.NewDutyBD;
import com.whir.ezoffice.personnelmanager.bd.WorkAddressBD;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.bd.rightmanager.RightBD;
import com.whir.org.bd.rolemanager.RoleBD;
import com.whir.org.bd.usermanager.UserBD;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.org.vo.rightmanager.RightVO;
import com.whir.org.vo.usermanager.UserPO;


/**
 * 系统管理-当前用户
 * 
 * @author wanghx
 * 
 */
public class UserAction extends BaseActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(UserAction.class.getName());
    
    public static final String MODULE_CODE = "UserAction";

 // 系统管理员 权限
	private static final String SYS_MANAGE_RIGHT = "00*01*01";
	// 用户管理员 权限
	private static final String SYS_MANAGE_RIGHT_USER = "00*01*02";
	
    //add-新增 modify-修改
    private String action;
    //base-基本信息 rights-角色权限
    private String saveOperateType = "base";
    // 状态 active-当前用户 disabled-禁用用户 sleep-休眠用户 apply-申请账号用户 export-导出用户
    private String status;
    private String confirmPassword;

    private String searchSimpleName;
    private String searchLeaderName;
    private String searchDuty;
    private String mobileUserFlag;

    private String cnName;
    private String enName;
    private String orgName;
    private String isSuper;
    private String userAccount;
    private String empId;

    private UserPO userPO;

    // 获得系统最大授权用户数
    private int dogMaxUserNum = 1500;
    private int currentUserNum = 0;

    private String userRoleId;
    private String orgIds;
    private String rightIds;
    private String rightScopeTypes;
    private String rightScopeScopes;
    private String rightScopeDsps;

    private String tansUse;

    private String isPasswordRule;
    private boolean showMobilePush = false;
    private boolean showAdCheck = false;
    //'0':'全部','1':'自定义'
    private String rd = "0";
    
    //"0"表示普通管理员，"1"表示是系统管理员
    private String sysManagerFlag = "0";
    
    //用户权限
    private String roleIds;
//    private String rightIdSend;
//    private String oldRightId;
//    private String oldRightType;
//    private String oldRightScope;
//    private String oldRightScopeDsp;
    
    //批量增加用户
    private String[] batch_userAccounts;//账号
    private String[] batch_userPassword;//密码
    //private String[] batch_conUserPassword;
    private String[] batch_empName;//中文名
    private String[] batch_userSimpleName;//用户简码
    //private String[] batch_orgNames;
    private String[] batch_orgIds;//所属组织
    
    //批量修改用户
    private String userNameStr;
    private String userIdStr;
    
    private String empMobilePhone;
    //企业号
    private String enterprisenumber;
    
    private static int DOG_MAX_USER_NUM =10;//= getDogMaxUserCount();
    
    //
    private static int DOG_MAX_APP_NUM=10;
    private static int DOG_MAX_WEIXIN_NUM=10;
    
    static { 
    	  int dogMaxUserNum = 1500;
    	  int dogMaxAPPUserNum = 10;
    	  int dogMaxWeixinUserNum = 10;
          // ----------获得系统最大授权用户数-----start
          try {
              com.whir.common.init.DogManager dm = com.whir.common.init.DogManager
                      .getInstance();
              String[] dogInfo = dm.getDogkey();
              dogMaxUserNum = Integer.parseInt(dogInfo[1]);
              dogMaxAPPUserNum = Integer.parseInt(dogInfo[3]);
              dogMaxWeixinUserNum = Integer.parseInt(dogInfo[4]);
          } catch (Exception ex) {
              System.out.println("--读系统授权用户数--出现异常");
          }
          // ----------获得系统最大授权用户数------end
          DOG_MAX_USER_NUM=dogMaxUserNum;
          DOG_MAX_APP_NUM=dogMaxAPPUserNum;
          DOG_MAX_WEIXIN_NUM=dogMaxWeixinUserNum;  
    }

    public String initList() throws Exception {

        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        dogMaxUserNum = DOG_MAX_USER_NUM;//getDogMaxUserCount();

        currentUserNum = getSysActivityUserCount();
        
       

        ManagerBD managerBD = new ManagerBD();
        String range = "*0*";
        String rightCode = "00*01*02";
        
        if ("1".equals(session.getAttribute("sysManager").toString()))
            rightCode = "00*01*01";

        if (rightCode.equals("00*01*01")) {
            range = "*0*";
        } else {
            List list = managerBD.getRightScope(userId, rightCode);
            Object[] obj = (Object[]) list.get(0);
            String scopeType = obj[0].toString();
            if ("0".equals(scopeType)) {
                // 可以维护全部数据
                range = "*0*";

            } else if ("1".equals(scopeType)) {
                // 可以维护本人的数据(即只能维护本组织)
                range = "*" + orgId + "*";
            } else if ("2".equals(scopeType)) {
                // 可以维护本组织及下级组织的数据
                range = "*" + orgId + "*";

            } else if ("3".equals(scopeType)) {
                // 可以维护本组织的数据
                range = "*" + orgId + "*";

            } else if ("4".equals(scopeType)) {
                // 维护定义的范围
                if (CommonUtils.isEmpty(obj[1])) {
                    range = "*-1*";
                } else {
                    range = obj[1].toString();
                }
            }
        }

        request.setAttribute("range", range);
        // aaa.id, aaa.dutyName, aaa.dutyLevel, aaa.scopeName
        request.setAttribute("listDuty", new NewDutyBD().getListWithScope(
                userId, orgIdString, domainId + ""));

        return "initList";
    }

    private static int getDogMaxUserCount() {
        int dogMaxUserNum = 1500;
        // ----------获得系统最大授权用户数-----start
        try {
            com.whir.common.init.DogManager dm = com.whir.common.init.DogManager
                    .getInstance();
            String[] dogInfo = (String[]) dm.getDogkey();
            dogMaxUserNum = Integer.parseInt(dogInfo[1]);
        } catch (Exception ex) {
            System.out.println("--读系统授权用户数--出现异常");
        }
        // ----------获得系统最大授权用户数------end

        return dogMaxUserNum;
    }

    private int getSysActivityUserCount() {
        // 获得系统中活动的用户数(没有被删除或禁用并且是正式的用户总数)
        com.whir.org.bd.usermanager.UserBD userBD = new com.whir.org.bd.usermanager.UserBD();
        int currentUserNum = userBD.getSysActivityUserCount();

        return currentUserNum;
    }

    public String getSysActivityUserNum() throws Exception {
        int currentUserNum = getSysActivityUserCount();

        printJsonResult("{\"currentUserNum\"," + currentUserNum + "}");

        return null;
    }

    /**
     * 当前用户
     * 
     * @return
     * @throws Exception
     */
    public String userList() throws Exception {
        logger.debug("查询列表开始");

        Long domainId = CommonUtils.getSessionDomainId(request);
        
        HttpSession session = request.getSession(true);
        String orgId2 = session.getAttribute("orgId").toString();//获取登陆用户的组织ID
        orgId2 = (String) (request.getParameter("orgId")==null?orgId2:request.getParameter("orgId"));//获取点击组织树时的组织ID
        String init =  (String) (request.getParameter("init")==null?"":request.getParameter("init"));//获取init用于判断是否为点击树
        
        /*
         * pageSize每页显示记录数，即list.jsp中分页部分select中的值
         */
        int pageSize = com.whir.common.util.CommonUtils
                .getUserPageSize(request);
        /*
         * orderByFieldName，用来排序的字段
         */
        String orderByFieldName = request.getParameter("orderByFieldName") != null ? request
                .getParameter("orderByFieldName")
                : "";
        /*
         * orderByType，排序的类型
         */
        String orderByType = request.getParameter("orderByType") != null ? request
                .getParameter("orderByType")
                : "";
        /*
         * currentPage，当前页数
         */
        int currentPage = 0;
        if (request.getParameter("startPage") != null) {
            currentPage = Integer.parseInt(request.getParameter("startPage"));
        }

        String viewSQL = "user.empId,user.empName,user.empEnglishName,user.empSex,user.userAccounts,user.userIsSuper,organization.orgNameString,user.empDuty,user.userSimpleName,user.empLeaderName,user.canSendMail,user.empLeaderId,user.latestLogonTime,user.userSleepReasons,user.empNumber,user.securitypolicy,user.mobileUserFlag,user.enterprisenumber";
        String fromSQL = "com.whir.org.vo.usermanager.UserPO as user join user.organizations organization";
        String whereSQL = "where user.domainId=" + domainId;

        logger.debug("status:" + status);

        if ("disabled".equals(status)) {// 禁用用户
            whereSQL += " and user.userIsActive=0 and user.userIsDeleted=0 and user.userAccounts is not null and user.userIsFormalUser=1 ";

        } else if ("sleep".equals(status)) {// 休眠用户
            whereSQL += " and user.userIsActive=1 and user.userIsDeleted=0 and user.userIsSleep=1 and user.userAccounts is not null and user.userIsFormalUser=1 ";

        } else if ("apply".equals(status)) {// 申请账号用户
            whereSQL += " and user.userIsDeleted=0 and user.isApplyAccount='1' ";

        } else if ("active".equals(status)) {// 当前用户
            whereSQL += " and user.userIsActive=1 and user.userIsDeleted=0 and user.userIsSleep=0 and user.userAccounts is not null and user.userIsFormalUser=1 ";
        } else {
            return null;
        }
        
        if("notfirst".equals(init)){
    		
        	whereSQL+=" and ( organization.orgId = "+orgId2+" or user.sidelineOrg like  '"+"%*" + orgId2 + "*%' ) ";
	   	}else{
	   		whereSQL+="";
	   	}
        
        /*
         * varMap，查询参数Map
         */
        Map varMap = new HashMap();
        if (!CommonUtils.isEmpty(cnName)){
            whereSQL += " and user.empName like :empName ";
            varMap.put("empName", "%" + cnName + "%");
        }
        
        if (!CommonUtils.isEmpty(enName)) {
            whereSQL += " and user.empEnglishName like :empEnglishName ";
            varMap.put("empEnglishName", "%" + enName + "%");
        }
        
        if (!CommonUtils.isEmpty(userAccount)) {
            whereSQL += " and user.userAccounts like :userAccounts ";
            varMap.put("userAccounts", "%" + userAccount + "%");
        }
        
        if (!CommonUtils.isEmpty(searchSimpleName)) {
            whereSQL += " and user.userSimpleName like :userSimpleName ";
            varMap.put("userSimpleName", "%" + searchSimpleName + "%");
        }
     
        /*if (!CommonUtils.isEmpty(cnName) && !CommonUtils.isEmpty(enName)) {
            if (isAllSpace(cnName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.empName like :empName or user.empEnglishName like :empEnglishName)";
                varMap.put("empName", "%" + cnName + "%");
                varMap.put("empEnglishName", "%" + enName + "%");
            } else {
                whereSQL += " and ((";
                String[] cnNameAry = getStrBySpace(cnName).split(" ");
                whereSQL += " user.empName like :empName ";
                varMap.put("empName", "%" + cnNameAry[0] + "%");
                for (int s = 1; s < cnNameAry.length; s++) {
                    whereSQL += " or user.empName like :empName" + s + " ";
                    varMap.put("empName" + s, "%" + cnNameAry[s] + "%");
                }
                whereSQL += ") or user.empEnglishName like :empEnglishName) ";
                varMap.put("empEnglishName", "%" + enName + "%");
            }
        } else if (!CommonUtils.isEmpty(cnName)) {
            if (isAllSpace(cnName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.empName like :empName) ";
                varMap.put("empName", "%" + cnName + "%");
            } else {
                String[] cnNameAry = getStrBySpace(cnName).split(" ");
                whereSQL += " and (user.empName like :empName ";
                varMap.put("empName", "%" + cnNameAry[0] + "%");
                for (int s = 1; s < cnNameAry.length; s++) {
                    whereSQL += " or user.empName like :empName" + s + " ";
                    varMap.put("empName" + s, "%" + cnNameAry[s] + "%");
                }
                whereSQL += ") ";
            }
        } else if (!CommonUtils.isEmpty(enName)) {
            whereSQL += " and user.empEnglishName like :empEnglishName ";
            varMap.put("empEnglishName", "%" + enName + "%");
        }

        if (!CommonUtils.isEmpty(userAccount)) {
            if (isAllSpace(userAccount)) {// 全是以空格组成的字符串
                whereSQL += " and (user.userAccounts like :userAccounts) ";
                varMap.put("userAccounts", "%" + userAccount + "%");
            } else {
                String[] userAccountAry = getStrBySpace(userAccount).split(" ");
                whereSQL += " and (user.userAccounts like :userAccounts ";
                varMap.put("userAccounts", "%" + userAccountAry[0] + "%");
                for (int s = 1; s < userAccountAry.length; s++) {
                    whereSQL += " or user.userAccounts like :userAccounts" + s
                            + " ";
                    varMap.put("userAccounts" + s, "%" + userAccountAry[s]
                            + "%");
                }
                whereSQL += ") ";
            }
        }
        
        if (!CommonUtils.isEmpty(searchSimpleName)) {
            if (isAllSpace(searchSimpleName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.userSimpleName like :userSimpleName) ";
                varMap.put("userSimpleName", "%" + searchSimpleName + "%");
            } else {
                String[] simpleNameAry = getStrBySpace(searchSimpleName).split(
                        " ");
                whereSQL += " and (user.userSimpleName like :userSimpleName ";
                varMap.put("userSimpleName", "%" + simpleNameAry[0] + "%");
                for (int s = 1; s < simpleNameAry.length; s++) {
                    whereSQL += " or user.userSimpleName like :userSimpleName"
                            + s + " ";
                    varMap.put("userSimpleName" + s, "%" + simpleNameAry[s]
                            + "%");
                }
                whereSQL += ") ";
            }
        }*/

        if (!CommonUtils.isEmpty(orgName)) {
            whereSQL += " and organization.orgNameString like :orgName ";
            varMap.put("orgName", "%" + orgName + "%");
        }

        if (!CommonUtils.isEmpty(isSuper)) {
            whereSQL += " and user.userIsSuper = :isSuper ";
            varMap.put("isSuper", isSuper);
        }

        if (!CommonUtils.isEmpty(searchLeaderName)) {
            whereSQL += " and user.empLeaderName like :empLeaderName ";
            varMap.put("empLeaderName", "%" + searchLeaderName + "%");
        }
        
        //增加“职务”作为查询字段
        if(!CommonUtils.isEmpty(searchDuty)){
            whereSQL += " and user.empDuty like :empDuty ";
            varMap.put("empDuty", "%" + searchDuty + "%");
        }
        
        //增加“移动办公”作为查询字段
        if(!CommonUtils.isEmpty(mobileUserFlag)){
            whereSQL += " and user.mobileUserFlag = :mobileUserFlag ";
            varMap.put("mobileUserFlag", mobileUserFlag);
        }
        //企业号
        if (!CommonUtils.isEmpty(enterprisenumber)) {
            whereSQL += " and user.enterprisenumber = :enterprisenumber ";
            varMap.put("enterprisenumber", enterprisenumber);
        }
        // 判断权限范围
        ManagerBD managerBD = new ManagerBD();
        String rightName = "00*01*02";

       // HttpSession session = request.getSession(true);
        if ("1".equals(session.getAttribute("sysManager").toString()))
            rightName = "00*01*01";

        String whereTmp = managerBD.getRightWhere(session
                .getAttribute("userId").toString(), session.getAttribute(
                "orgId").toString(), rightName, "organization.orgId",
                "user.empId");
        if (whereTmp.equals("")) {
            whereTmp = "1<1";
        }

        if (whereTmp != null && !whereTmp.equals("")) {
            whereSQL += " and " + whereTmp;
        }

        logger.debug("WHERE语句：" + whereSQL);

        // 排序
        String orderBy = " order by ";
        if (!CommonUtils.isEmpty(orderByFieldName)) {
            orderBy += "user." + orderByFieldName + " " + orderByType + ",";
        }
        orderBy += "organization.orgIdString,user.empDutyLevel,user.userOrderCode,user.empName,user.empId";

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

        /* ......如果需要对查询结果进行扩充 start....... */
        // -------判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式 $id1$$id2$$id3$
        // 领导1,领导2,领导3,-----start
        List listTemp = new ArrayList();
        if (list != null && list.size() > 0) {
            UserBD userBD = new UserBD();
            for (int s = 0, len=list.size(); s < len; s++) {
                Object[] objAry = (Object[]) list.get(s);// objAry[11]上级领导id串
                String leaderIdsStr = "";
                String leaderNamesStr = "";
                if (!CommonUtils.isEmpty(objAry[11])) {
                    String userIdString = objAry[11].toString().replaceAll(
                            "\\$", ",");
                    userIdString = userIdString.replaceAll(",,", ",");
                    if (userIdString.startsWith(",")) {
                        userIdString = userIdString.substring(1, userIdString
                                .length());
                    }
                    if (userIdString.endsWith(",")) {
                        userIdString = userIdString.substring(0, userIdString
                                .length() - 1);
                    }
                    String[] userInfo = userBD
                            .getUserIdStrAndUserNameStr(userIdString);
                    leaderIdsStr = userInfo[0];
                    leaderNamesStr = userInfo[1];
                }
                objAry[9] = leaderNamesStr;// 上级领导id串
                objAry[11] = leaderIdsStr;// 上级领导名称串

                listTemp.add(objAry);
            }
        }
        // -------判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式 $id1$$id2$$id3$
        // 领导1,领导2,领导3,-----end
        /* ......如果需要对查询结果进行扩充 end....... */

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
        String[] arr = { "empId", "empName", "empEnglishName", "empSex",
                "userAccounts", "userIsSuper", "orgNameString", "empDuty",
                "userSimpleName", "empLeaderName", "canSendMail",
                "empLeaderId", "latestLogonTime", "userSleepReasons",
                "empNumber", "securitypolicy", "mobileUserFlag","enterprisenumber"};
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, listTemp, "empId", MODULE_CODE);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";

        // System.out.println("----[json]:"+json);
        printResult(G_SUCCESS, json);

        logger.debug("查询列表结束");

        return null;
    }

    /**
     * 新增用户
     * 
     * @return
     * @throws Exception
     */
    public String addUser() throws Exception {
        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        String managerId = session.getAttribute("userId").toString();
        String browseRange = (String) session.getAttribute("browseRange");
        String sysManager = session.getAttribute("sysManager").toString();
        String managerOrgId = session.getAttribute("orgId").toString();
        //String orgId2 = (String) (request.getParameter("orgId")==null?"":request.getParameter("orgId"));//获取点击组织树时的组织ID
        dogMaxUserNum = DOG_MAX_USER_NUM;//getDogMaxUserCount();

        currentUserNum = getSysActivityUserCount();

        if (currentUserNum >= dogMaxUserNum) {
            // 系统用户大于狗中的用户数

            return null;
        }

        // 取得管理员有权限管理的角色
//        RoleBD roleBD = new RoleBD();
//        
//        List roles = roleBD.getOwnerRoles(managerId, managerOrgId, browseRange,
//                sysManager, domainId + "");
//
//        request.setAttribute("roles", roles);
        
        request.setAttribute("userRightScope", new ArrayList());
        
        // 取得管理员的管理范围
        getManagerScope();

        // aaa.id, aaa.dutyName, aaa.dutyLevel, aaa.scopeName
        request.setAttribute("listDuty", new NewDutyBD().getListWithScope(
                userId, orgIdString, domainId + ""));

        // 是否使用数据交换中心
        ConfigXMLReader reader = new ConfigXMLReader();
        tansUse = reader.getAttribute("TransCenter", "use");
        logger.debug("tansUse:" + tansUse);

        com.whir.org.common.util.SysSetupReader sysReader = com.whir.org.common.util.SysSetupReader
                .getInstance();
        showMobilePush = sysReader.isMobilePush(domainId + "");

        logger.debug("showMobilePush:" + showMobilePush);

        // 是否使用AD域开关
        int useLDAP = new com.whir.ezoffice.ldap.LDAP().getUseLDAP();
        // isCheckFlag为0表示不验证AD域账号密码，1表示验证AD域账号密码
        int isCheckFlag = new com.whir.ezoffice.ldap.LDAP().getIsCheckFlag();
        if (useLDAP == 1 && isCheckFlag == 1) {
            showAdCheck = true;
        }
        logger.debug("showAdCheck:" + showAdCheck);

        UserPO po = new UserPO();

        // 初始字段值
        po.setUserAccounts("");
        po.setUserPassword("");
        po.setEmpSex((byte) 0);
        po.setCanSendMail("0");
        po.setKeyValidate("0");
        po.setIsAdCheck("0");
        po.setUserIsSuper((byte) 0);
        po.setMobileUserFlag("0");
        po.setSecuritypolicy("0");
        po.setUserOrderCode("1000");
        po.setIsMobilePush("0");
        po.setIsMobileReceive("0");
        po.setIsAdCheck("0");
        po.setIsPasswordRule("0");

        po.setMailboxSize(sysReader.getMailBoxSize(domainId + ""));
        po.setNetDiskSize(sysReader.getNetDiskSize(domainId + ""));
       
        this.setUserPO(po);

        isPasswordRule = "0";
        rd = "0";
        action = "add";
        
        if(sysManager.indexOf("1")>=0){
            sysManagerFlag = "1";
        }

        return "addUser";
    }

    /**
     * 保存用户
     * 
     * @return
     * @throws Exception
     */
    public String saveUser() throws Exception {
        logger.debug("保存用户信息。。。");
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = CommonUtils.getSessionUserId(request) + "";
        String orgId = CommonUtils.getSessionOrgId(request) + "";
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String userIP = session.getAttribute("userIP").toString();
       
        java.util.Date startDate = new java.util.Date();

        userPO.setCreatedOrg(Long.parseLong(orgId));
        byte userIsActive = 1;
        byte userIsFormalUser = 1;
        // Date userSuperBegin = new Date(httpServletRequest.getParameter(
        // "userSuperBegin"));
        // Date userSuperEnd = new Date(httpServletRequest.getParameter(
        // "userSuperEnd"));

        userPO.setSkin(Constants.DEFAULT_THEME_SKIN);//默认设置
        userPO.setUserIsActive(userIsActive);
        userPO.setUserIsFormalUser(Integer.valueOf("1"));
       
        if (userPO.getUserIsSuper() == 1) {
            // userPO.setUserSuperBegin(userSuperBegin);
            // userPO.setUserSuperEnd(userSuperEnd);
            
            String userSuperBegin = request.getParameter("userPO.userSuperBegin");
            String userSuperEnd = request.getParameter("userPO.userSuperEnd");
         
            userPO.setUserSuperBegin(new Date(userSuperBegin.replaceAll("-", "/")));
            userPO.setUserSuperEnd(new Date(userSuperEnd.replaceAll("-", "/")));
            
        } else {
            userPO.setUserSuperBegin(new java.util.Date());
            userPO.setUserSuperEnd(new java.util.Date());
        }

        userPO.setDomainId(domainId + "");

        // String mailboxSize = httpServletRequest.getParameter("mailboxSize");
        // String netDiskSize = httpServletRequest.getParameter("netDiskSize");
        // if(mailboxSize==null||"".equals(mailboxSize.trim())){
        // mailboxSize = "0";
        // }
        // if(netDiskSize==null||"".equals(netDiskSize.trim())){
        // netDiskSize = "0";
        // }
        // userPO.setMailboxSize(mailboxSize);
        // userPO.setNetDiskSize(netDiskSize);
        
        String mailboxSize = userPO.getMailboxSize();
        String netDiskSize = userPO.getNetDiskSize();
        if(CommonUtils.isEmpty(mailboxSize)){
            userPO.setMailboxSize("0");
        }
        if(CommonUtils.isEmpty(netDiskSize)){
            userPO.setNetDiskSize("0");
        }

        // employeeVO.setCanSendMail(canSendMail);
        userPO.setUserIsSleep("0");

        if (!"1".equals(userPO.getMobileUserFlag())) {
            userPO.setSecuritypolicy(null);
        }
        
        if(CommonUtils.isEmpty(userPO.getKeyValidate())){
            userPO.setKeyValidate("0");
        }
        
        if(CommonUtils.isEmpty(userPO.getIsPasswordRule())){
            userPO.setIsPasswordRule("0");
        }
        
        String workAddress = userPO.getWorkAddress();
        String workAddressIds = "";
        if(!CommonUtils.isEmpty(workAddress)){
            String[] workAddressArr = workAddress.split(",");
            if(workAddressArr != null){
                for(int i=0; i<workAddressArr.length; i++){
                    String temp = workAddressArr[i];
                    if(!CommonUtils.isEmpty(temp)){
                        workAddressIds += temp + ",";
                    }
                }
            }
        }
        userPO.setWorkAddress(workAddressIds);

        String[] orgIdArr = new String[0];
        String[] rightIdArr = new String[0];
        String[] rightScopeTypeArr = new String[0];
        String[] rightScopeScopeArr = new String[0];
        String[] roleIdArr = userRoleId.split(",");

        orgIdArr = new String[1];
        orgIdArr[0] = orgIds;
        if (rightIds.indexOf(",") != -1) {
            rightIdArr = rightIds.split(",");
        }
        if (rightScopeTypes.indexOf(",") != -1) {
            rightScopeTypeArr = rightScopeTypes.split(",");
        }
        if (rightScopeScopes.indexOf(",") != -1) {
            rightScopeScopeArr = rightScopeScopes.split(",");
        }

        UserBD userBD = new UserBD();
        Map map = userBD.add(userPO, orgIdArr, rightIdArr, rightScopeTypeArr,
                rightScopeScopeArr, rightScopeDsps, roleIdArr);

        int result = 2;
        if (map.get("result") != null) {
            result = ((Integer) map.get("result")).intValue();
        }

        logger.debug("result:" + result);

        if (result == 0) {
            long empId = -1;
            if (map.get("empId") != null) {
                empId = ((Long) map.get("empId")).longValue();
            }

            logger.debug("empId:" + empId);

            // 记录新增用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            java.util.Date endDate = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理",
                    startDate, endDate, "1", userPO.getEmpName() + "/"
                            + userPO.getUserAccounts(), userIP, domainId + "");

            // --------------------------------------------------------------
            // 调用系统管理-用户接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.UserImpl", "addUser", "1",
                        domainId + "");
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String.class,
                            HttpServletRequest.class };
                    Object[] paramsValue = new Object[] { empId + "", request };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "1");
                }
            } catch (Exception e) {
                logger
                        .error("--调用用户接口出错--[com.whir.plugins.sys.impl.UserImpl][addUser]");
                e.printStackTrace();
            }
            // 调用系统管理-用户接口--end--
            // --------------------------------------------------------------

            printResult(G_SUCCESS, "{empId:"+empId+"}");
        }else{
            logger.debug("保存用户信息失败"+result);
            if (result == 1) {
                printJsonResult("{\"result\":\"账号重复！\"}");
            }else if(result == 4){
                printJsonResult("{\"result\":\"用户简码重复！\"}");
            }else if(result == 9){
                printJsonResult("{\"result\":\"用户英文名重复！\"}");
            }else {
                printResult("failure", "null");
            }
        }

        return null;
    }

    /**
     * 进入修改页面
     * 
     * @return
     * @throws Exception
     */
    public String loadUser() throws Exception {
        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;
        
       
        if(CommonUtils.isEmpty(empId, true)){
            return this.NO_RIGHTS;
        }
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        String managerId = session.getAttribute("userId").toString();
        String browseRange = (String) session.getAttribute("browseRange");
        String sysManager = session.getAttribute("sysManager").toString();
        String managerOrgId = session.getAttribute("orgId").toString();

        // 取得管理员有权限管理的角色
        RoleBD roleBD = new RoleBD();
        
//        List roles = roleBD.getOwnerUserRoles(managerId, managerOrgId,
//                browseRange, sysManager, empId, domainId + "");
//        // --原为在修改用户信息时，操作人有权限的角色但被操作人以前没有选中的角色 此时在修改页面上不显示该角色
//        // 在新增页面显示正确-------------start---------
//        String roleIdStr = ",";
//        if (roles != null && roles.size() > 0) {
//            for (int i = 0; i < roles.size(); i++) {
//                Object[] obj = (Object[]) roles.get(i);
//                roleIdStr = roleIdStr + obj[0] + ",";
//            }
//        }
//        
//        // 操作人自己拥有权限操作的所有角色
//        List hasRoles = roleBD.getOwnerRoles(managerId, managerOrgId,
//                browseRange, sysManager, domainId+"");
//        if (hasRoles != null && hasRoles.size() > 0) {
//            for (int s = 0; s < hasRoles.size(); s++) {
//                Object[] obj2 = (Object[]) hasRoles.get(s);
//                if (roleIdStr.indexOf("," + obj2[0] + ",") == -1) {// 如果没有，则把这个角色添加进去
//                    roles.add(obj2);
//                }
//            }
//        }
//
//        request.setAttribute("roles", roles);

        // 取得管理员的管理范围
        getManagerScope();

        request.setAttribute("listDuty", new NewDutyBD().getListWithScope(
                userId, orgIdString, domainId + ""));

        // 取用户原有的角色
        List oldRoles = roleBD.getUserRole(empId);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < oldRoles.size(); i++) {
            sb.append("$");
            sb.append((Object) oldRoles.get(i));
            sb.append("$,");
        }

        userRoleId = sb.toString();
        if(!CommonUtils.isEmpty(userRoleId)){
            roleIds = userRoleId.replaceAll("\\$", "");
        }
        request.setAttribute("oldRoles", userRoleId);        

        // 取用户原有的权限范围
        RightBD rightBD = new RightBD();
        List userRightScope = rightBD.getUserRightScope(empId);
        request.setAttribute("userRightScope", userRightScope);

        UserBD userBD = new UserBD();
        //emp.empName,emp.empEnglishName,emp.userAccounts,emp.empSex,emp.userPassword,emp.userIsSuper,emp.userSuperBegin,emp.userSuperEnd,
        //org.orgId,org.orgName,emp.empLeaderId,emp.empLeaderName,emp.browseRange,emp.browseRangeName,emp.userOrderCode,emp.empDuty,
        //emp.keyValidate,emp.keySerial,emp.userSimpleName,emp.mailboxSize,emp.netDiskSize,emp.signatureImgName,emp.signatureImgSaveName,
        //emp.sidelineOrg,emp.sidelineOrgName,emp.canSendMail,emp.isPasswordRule,emp.isAdCheck,emp.isMobilePush,emp.isMobileReceive,
        //emp.mobileUserFlag,emp.chargeLeaderIds,emp.chargeLeaderNames,emp.deptLeaderIds,emp.deptLeaderNames, emp.securitypolicy, 
        //org.orgNameString, emp.empIdCard
        List userList = userBD.getUserInfo(new Long(empId));
        Object[] obj = (Object[]) userList.get(0);
        
        String _userAccounts = obj[2]!=null?obj[2]+"":"";
        
        UserPO po = new UserPO();
        po.setEmpId(Long.parseLong(empId));
        po.setEmpName(obj[0]+"");
        po.setEmpEnglishName(obj[1]!=null?obj[1]+"":"");
        po.setUserAccounts(_userAccounts);
        po.setEmpSex((byte)Integer.parseInt(obj[3]+""));
        po.setUserIsSuper((byte)Integer.parseInt(obj[5]+""));
        
        logger.debug("userSuperBegin:"+obj[6]);
        logger.debug("userSuperEnd:"+obj[7]);
        
        if(!CommonUtils.isEmpty(obj[6])){
            po.setUserSuperBegin(new Date((obj[6]+"").substring(0,10).replaceAll("-", "/")));
        }
        
        if(!CommonUtils.isEmpty(obj[7])){
            po.setUserSuperEnd(new Date((obj[7]+"").substring(0,10).replaceAll("-", "/")));
        }
        
        orgIds = obj[8] + "";
        orgName = obj[9] + "";

        // ---过滤已经禁用或删除的上级领导--判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式
        // $id1$$id2$$id3$ 领导1,领导2,领导3,-----start
        String leaderIdsStr = "";
        String leaderNamesStr = "";
        if (!CommonUtils.isEmpty(obj[10])) {
            String userIdString = obj[10].toString().replaceAll("\\$", ",");
            userIdString = userIdString.replaceAll(",,", ",");
            if (userIdString.startsWith(",")) {
                userIdString = userIdString.substring(1, userIdString.length());
            }
            if (userIdString.endsWith(",")) {
                userIdString = userIdString.substring(0,
                        userIdString.length() - 1);
            }
            String[] userInfo = userBD.getUserIdStrAndUserNameStr(userIdString);
            leaderIdsStr = userInfo[0];
            leaderNamesStr = userInfo[1];
        }
        // ---过滤已经禁用或删除的上级领导--判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式
        // $id1$$id2$$id3$ 领导1,领导2,领导3,-----end
        
        po.setEmpLeaderId(leaderIdsStr);
        po.setEmpLeaderName(leaderNamesStr);
        
        po.setBrowseRange(obj[12]!=null?obj[12]+"":"");
        po.setBrowseRangeName(obj[13]!=null?obj[13]+"":"");
        String userOrderCode_ = obj[14]!=null?obj[14]+"":"";
        if(CommonUtils.isEmpty(userOrderCode_)){
            userOrderCode_ = "1000";
        }
        po.setUserOrderCode(userOrderCode_);
        po.setEmpDuty(obj[15]!=null?obj[15]+"":"");
        po.setKeyValidate(obj[16]!=null?obj[16]+"":"");
        po.setKeySerial(obj[17]!=null?obj[17]+"":"");
        po.setUserSimpleName(obj[18]!=null?obj[18]+"":"");
        po.setMailboxSize(obj[19]!=null?obj[19]+"":"0");
        po.setNetDiskSize(obj[20]!=null?obj[20]+"":"0");
        po.setSignatureImgName(obj[21]!=null?obj[21]+"":"");
        po.setSignatureImgSaveName(obj[22]!=null?obj[22]+"":"");
        po.setSidelineOrg(obj[23]!=null?obj[23]+"":"");
        po.setSidelineOrgName(obj[24]!=null?obj[24]+"":"");
        po.setCanSendMail(obj[25]!=null?obj[25]+"":"");
        // 是否对该用户启用密码规则
        po.setIsPasswordRule(obj[26]!=null?obj[26]+"":"");
        // 是否对该用户ad域验证，0：否，1：是
        po.setIsAdCheck(obj[27]!=null?obj[27]+"":"");
        // 是否移动办公信息推送，0：否，1：是
        po.setIsMobilePush(obj[28]!=null?obj[28]+"":"0");
        // 是否接收移动办公信息推送，0：否，1：是
        po.setIsMobileReceive(obj[29]!=null?obj[29]+"":"0");
        // 对手机版帐号登录的限制，如果没有对该账号开通，则不可以登录
        po.setMobileUserFlag(obj[30]!=null?obj[30]+"":"0");
        
        String chargeLeaderIds = "";
        String chargeLeaderNames = "";
        if (!CommonUtils.isEmpty(obj[31])) {
            String userIdString = obj[31].toString().replaceAll("\\$", ",");
            userIdString = userIdString.replaceAll(",,", ",");
            if (userIdString.startsWith(",")) {
                userIdString = userIdString.substring(1, userIdString.length());
            }
            if (userIdString.endsWith(",")) {
                userIdString = userIdString.substring(0,
                        userIdString.length() - 1);
            }
            String[] userInfo = userBD.getUserIdStrAndUserNameStr(userIdString);
            chargeLeaderIds = userInfo[0];
            chargeLeaderNames = userInfo[1];
        }
        po.setChargeLeaderIds(chargeLeaderIds);
        po.setChargeLeaderNames(chargeLeaderNames);
        
        String deptLeaderIds = "";
        String deptLeaderNames = "";
        if (!CommonUtils.isEmpty(obj[33])) {
            String userIdString = obj[33].toString().replaceAll("\\$", ",");
            userIdString = userIdString.replaceAll(",,", ",");
            if (userIdString.startsWith(",")) {
                userIdString = userIdString.substring(1, userIdString.length());
            }
            if (userIdString.endsWith(",")) {
                userIdString = userIdString.substring(0,
                        userIdString.length() - 1);
            }
            String[] userInfo = userBD.getUserIdStrAndUserNameStr(userIdString);
            deptLeaderIds = userInfo[0];
            deptLeaderNames = userInfo[1];
        }
        po.setDeptLeaderIds(deptLeaderIds);
        po.setDeptLeaderNames(deptLeaderNames);
        
        po.setSecuritypolicy(obj[35]!=null?obj[35]+"":"0");// 安全策略
        po.setEmpIdCard(obj[37]!=null?obj[37]+"":"");// 身份证号
        
        com.whir.org.common.util.SysSetupReader sysReader = com.whir.org.common.util.SysSetupReader.getInstance();
        
        if(CommonUtils.isEmpty(_userAccounts)){
            po.setMailboxSize(sysReader.getMailBoxSize(domainId + ""));
            po.setNetDiskSize(sysReader.getNetDiskSize(domainId + ""));
        }
        
        //员工编号
        po.setEmpNumber(obj[38]!=null?obj[38]+"":"");
        //办公地点
        po.setWorkAddress(obj[39]!=null?obj[39]+"":"");
        List addressList = new WorkAddressBD().list(new Long(domainId));
        String addressName = "";
        String addressId = "";
        if(addressList!=null && addressList.size() > 0){
            for(int i=0, len=addressList.size(); i<len; i++){
                Object[] workAddressObj = (Object[]) addressList.get(i);
                String wa = workAddressObj[0] + "";
                if((","+po.getWorkAddress()+",").indexOf(","+workAddressObj[0]+",")!=-1){
                    addressName += workAddressObj[1].toString()+"," ;
                    addressId += workAddressObj[0]+",";
                }
            }
        }
        po.setWorkAddress(addressId);
        request.setAttribute("addressName",addressName);
        //手机号
        po.setEmpMobilePhone(obj[40]!=null?obj[40]+"":"");
        //企业号
        po.setEnterprisenumber(obj[41]!=null?obj[41]+"":"");
        this.setUserPO(po);
        
        // 是否使用数据交换中心
        ConfigXMLReader reader = new ConfigXMLReader();
        tansUse = reader.getAttribute("TransCenter", "use");
        logger.debug("tansUse:" + tansUse);
        
        if(!CommonUtils.isEmpty(po.getBrowseRange())){
            rd = "1";
        }
        
        //是否使用AD域开关
        int useLDAP = new com.whir.ezoffice.ldap.LDAP().getUseLDAP();
        //isCheckFlag为0表示不验证AD域账号密码，1表示验证AD域账号密码
        int isCheckFlag = new com.whir.ezoffice.ldap.LDAP().getIsCheckFlag();
        if(useLDAP==1 && isCheckFlag==1){
            showAdCheck = true;
        }
        
        action = "modify";
        if(sysManager.indexOf("1")>=0){
            sysManagerFlag = "1";
        }

        return "loadUser";
    }
    
    /**
     * 修改用户
     * @return
     * @throws Exception
     */
    public String modifyUser() throws Exception {
        logger.debug("--修改用户--start--");
        logger.debug("empId:"+empId);
        
        if(!"1".equals(userPO.getEnterprisenumber())){
        	userPO.setEnterprisenumber("0");
		}
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = CommonUtils.getSessionUserId(request) + "";
        String orgId = CommonUtils.getSessionOrgId(request) + "";
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();
        String userIP = session.getAttribute("userIP").toString();

        java.util.Date startDate = new java.util.Date();
        userPO.setCreatedOrg(Long.parseLong(orgId+""));
        userPO.setEmpId(Long.parseLong(empId));
        
        String mailboxSize = userPO.getMailboxSize();
        String netDiskSize = userPO.getNetDiskSize();
        if(CommonUtils.isEmpty(mailboxSize)){
            userPO.setMailboxSize("0");
        }
        if(CommonUtils.isEmpty(netDiskSize)){
            userPO.setNetDiskSize("0");
        }

        byte userIsActive = 1;
        byte userIsFormalUser = 1;

        userPO.setUserIsActive(userIsActive);
        userPO.setUserIsFormalUser(Integer.valueOf("1"));
        
        if(CommonUtils.isEmpty(userPO.getKeyValidate())){
            userPO.setKeyValidate("0");
            userPO.setKeySerial("");
        }
        
        if(CommonUtils.isEmpty(userPO.getIsPasswordRule())){
            userPO.setIsPasswordRule("0");
        }

        if (userPO.getUserIsSuper() == 1) {
            String userSuperBegin = request.getParameter("userPO.userSuperBegin");
            String userSuperEnd = request.getParameter("userPO.userSuperEnd");
            
            userPO.setUserSuperBegin(new Date(userSuperBegin.replaceAll("-", "/")));
            userPO.setUserSuperEnd(new Date(userSuperEnd.replaceAll("-", "/")));
        } else {
            userPO.setUserSuperBegin(new java.util.Date());
            userPO.setUserSuperEnd(new java.util.Date());
        }
        
        if("0".equals(rd)){
            userPO.setBrowseRange("");
            userPO.setBrowseRangeName("");
        }
        
        String workAddress = userPO.getWorkAddress();
        String workAddressIds = "";
        if(!CommonUtils.isEmpty(workAddress)){
            String[] workAddressArr = workAddress.split(",");
            if(workAddressArr != null){
                for(int i=0; i<workAddressArr.length; i++){
                    String temp = workAddressArr[i];
                    if(!CommonUtils.isEmpty(temp)){
                        workAddressIds += temp + ",";
                    }
                }
            }
        }
        userPO.setWorkAddress(workAddressIds);
        
        logger.debug("empDuty:"+userPO.getEmpDuty());
        
        String[] orgIdArr = new String[0];
        String[] rightIdArr = new String[0];
        String[] rightScopeTypeArr = new String[0];
        String[] rightScopeScopeArr = new String[0];
        String[] roleIdArr = userRoleId.split(",");        
        
        logger.debug("userRoleId:"+userRoleId);
        logger.debug("rightIds:"+rightIds);
        logger.debug("rightScopeTypes:"+rightScopeTypes);
        logger.debug("rightScopeScopes:"+rightScopeScopes);
        logger.debug("rightScopeDsps:"+rightScopeDsps);
        orgIdArr = new String[1]; 
        orgIdArr[0] = orgIds;
        
        if (rightIds.indexOf(",") != -1) {
            if(rightIds.endsWith(",")){
                rightIds = rightIds.substring(0, rightIds.length()-1);                
            }
            rightIdArr = rightIds.split(",");
        }
        if (rightScopeTypes.indexOf(",") != -1) {
            if(rightScopeTypes.endsWith(",")){
                rightScopeTypes = rightScopeTypes.substring(0, rightScopeTypes.length()-1);                
            }
            rightScopeTypeArr = rightScopeTypes.split(",");
        }
        if (rightScopeScopes.indexOf(",") != -1) {
            if(rightScopeScopes.endsWith(",")){
                rightScopeScopes = rightScopeScopes.substring(0, rightScopeScopes.length()-1);                
            }
            rightScopeScopeArr = rightScopeScopes.split(",");
        }

        if(!"1".equals(userPO.getMobileUserFlag())) {
            userPO.setSecuritypolicy(null);
        }

        logger.debug("saveOperateType:"+saveOperateType);
        
        int result = 0;
        UserBD userBD = new UserBD();
        if("base".equals(saveOperateType)){//仅保存基本信息
            rightIdArr = null;
            result = userBD.update(userPO,
                                   orgIdArr,
                                   rightIdArr,
                                   rightScopeTypeArr,
                                   rightScopeScopeArr,
                                   rightScopeDsps,
                                   roleIdArr);

        }else{//保存基本信息及权限
            result = userBD.update(userPO,
                                   orgIdArr,
                                   rightIdArr,
                                   rightScopeTypeArr,
                                   rightScopeScopeArr,
                                   rightScopeDsps,
                                   roleIdArr);
        }
       
        if(result == 0){
          //记录更新用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.
                ezoffice.security.log.bd.LogBD();
            java.util.Date endDate = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理",
                      startDate, endDate, "2",
                      userPO.getEmpName() + "/" +
                      userPO.getUserAccounts(),
                      session.getAttribute("userIP").toString(), domainId+"");
            //--------------------------------------------------------------
            //调用系统管理-用户接口--start--
            try{
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo("com.whir.plugins.sys.impl.UserImpl", "updateUser", "1", session.getAttribute("domainId").toString());
                if(interfaceInfos != null){
                    Class[]  paramsType  = new Class[] {String.class, HttpServletRequest.class};
                    Object[] paramsValue = new Object[] {empId, request};
                    new com.whir.plugins.sys.InterfaceUtils().execute(interfaceInfos, paramsType, paramsValue, "1");
                }
            }catch(Exception e){
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            //调用系统管理-用户接口--end--
            //--------------------------------------------------------------
            
            printResult(G_SUCCESS, "{empId:"+empId+"}");
        }else{
            if (result == 1) {
                printJsonResult("{\"result\":\"账号重复！\"}");
            }else if(result == 3){
                printJsonResult("{\"result\":\"此令牌已经分配给其他人使用,请更换一个令牌！\"}");
            }else if(result == 4){
                printJsonResult("{\"result\":\"用户简码重复！\"}");
            }else if(result == 5){
                printJsonResult("{\"result\":\"外部邮箱添加不成功,请稍候手工添加！\"}");
            }else if(result == 9){
                printJsonResult("{\"result\":\"用户英文名重复！\"}");
            }else if(result == -100){
                printJsonResult("{\"result\":\"超过购买用户数！\"}");
            }else {
                printResult("failure", "null");
            }
           
        }
        
        logger.debug("--修改用户--end--");
        
        return null;
    }

    /**
     * 批量增加用户
     * 
     * @return
     * @throws Exception
     */
    public String addBatchUser() throws Exception {
        
        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;

        dogMaxUserNum = DOG_MAX_USER_NUM;//getDogMaxUserCount();

        currentUserNum = getSysActivityUserCount();

        // 取得管理员的管理范围
        getManagerScope();

        return "addBatchUser";
    }

    /**
     * 批量修改
     * 
     * @return
     * @throws Exception
     */
    public String modiBatchUser() throws Exception {
        // 检查业务操作权限
        if (checkOperateRights() == false)
            return NO_RIGHTS;
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        // 取得管理员的管理范围
        getManagerScope();

        String ids = empId;
        if (ids.endsWith(",")) {
            ids = ids.substring(0, ids.length() - 1);
        }

        UserBD userBD = new UserBD();
        String[] userInfo = userBD.getUserIdStrAndUserNameStr2(ids);
        userIdStr = ids;
        userNameStr = userInfo[1];
        
        request.setAttribute("listDuty", new NewDutyBD().getListWithScope(
                userId, orgIdString, domainId + ""));

        return "modiBatchUser";
    }
    
    /**
     * 删除单个用户
     * @return
     * @throws Exception
     */
    public String deleteUser() throws Exception {
        if(!"no".equals(request.getParameter("single"))){
        	 if (checkOperateRights() == false)
                 return NO_RIGHTS;
        }
        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.delete(ids);
            
            //[11.0.0.12补丁]用户账号已删除，群组中仍然显示该用户，要求同步删除。
            new GroupBD().delUserSyncGroup(ids);            

            // 记录删除用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            java.util.Date date = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理", date,
                    date, "3", result, session.getAttribute("userIP")
                            .toString(), domainId + "");

            // --------------------------------------------------------------
            // 调用系统管理-用户接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.UserImpl", "deleteUser",
                        "1", session.getAttribute("domainId").toString());
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String[].class,
                            HttpServletRequest.class };
                    Object[] paramsValue = new Object[] { ids, request };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "1");
                }
            } catch (Exception e) {
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            // 调用系统管理-用户接口--end--
            // --------------------------------------------------------------

            printResult("success");
        }

        return null;
    }
    
    /**
     * 禁用用户
     * @return
     * @throws Exception
     */
    public String disableUser() throws Exception {
    	 if (checkOperateRights() == false)
             return NO_RIGHTS;
    	
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.disable(ids);

            // 记录删除用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            java.util.Date date = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理", date,
                    date, "5", result, session.getAttribute("userIP")
                            .toString(), domainId+"");

            // --------------------------------------------------------------
            // 调用系统管理-用户接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.UserImpl", "disableUser",
                        "1", session.getAttribute("domainId").toString());
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String[].class };
                    Object[] paramsValue = new Object[] { ids };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "1");
                }
            } catch (Exception e) {
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            // 调用系统管理-用户接口--end--
            // --------------------------------------------------------------

            printResult("success");
        }

        return null;
    }
    
    /**
     * 恢复被禁用用户
     * @return
     * @throws Exception
     */
    public String recoverUser() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.recover(ids);

            // 记录删除用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            java.util.Date date = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理",
                    date, date, "6", result,
                    session.getAttribute("userIP").toString(), domainId+"");

          //--------------------------------------------------------------
          //调用系统管理-用户接口--start--
          try{
              com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
              String[] interfaceInfos = sibd.getInterfaceInfo("com.whir.plugins.sys.impl.UserImpl", "enableUser", "1", session.getAttribute("domainId").toString());
              if(interfaceInfos != null){
                  Class[]  paramsType  = new Class[] {String[].class};
                  Object[] paramsValue = new Object[] {ids};
                  new com.whir.plugins.sys.InterfaceUtils().execute(interfaceInfos, paramsType, paramsValue, "1");
              }
          }catch(Exception e){
              System.out.println("--调用用户接口出错--");
              e.printStackTrace();
          }
          //调用系统管理-用户接口--end--
          //--------------------------------------------------------------

            printResult("success");
        }

        return null;
    }
    
    /**
     * 休眠用户
     * @return
     * @throws Exception
     */
    public String sleepUser() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.sleepUser(ids, "管理员操作");

            // --------------------------------------------------------------
            // 调用系统管理-用户接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.UserImpl", "sleepUser", "1",
                        session.getAttribute("domainId").toString());
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String[].class };
                    Object[] paramsValue = new Object[] { ids };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "1");
                }
            } catch (Exception e) {
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            // 调用系统管理-用户接口--end--
            // --------------------------------------------------------------

            printResult("success");
        }

        return null;
    }
    
    /**
     * 恢复休眠用户
     * @return
     * @throws Exception
     */
    public String recoverSleepUser() throws Exception {
    	 if (checkOperateRights() == false)
             return NO_RIGHTS;
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.recoverSleep(ids);

            // --------------------------------------------------------------
            // 调用系统管理-用户接口--start--
            try {
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo(
                        "com.whir.plugins.sys.impl.UserImpl",
                        "enableSleepUser", "1", session
                                .getAttribute("domainId").toString());
                if (interfaceInfos != null) {
                    Class[] paramsType = new Class[] { String[].class };
                    Object[] paramsValue = new Object[] { ids };
                    new com.whir.plugins.sys.InterfaceUtils().execute(
                            interfaceInfos, paramsType, paramsValue, "1");
                }
            } catch (Exception e) {
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            // 调用系统管理-用户接口--end--
            // --------------------------------------------------------------

            printResult("success");
        }

        return null;
    }
    
    /**
     * 批量增加用户
     * @return
     * @throws Exception
     */
    public String saveBatchUser() throws Exception {

        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        com.whir.org.common.util.SysSetupReader sysReader = com.whir.org.common.util.SysSetupReader
                .getInstance();

        String mailBoxSize = sysReader.getMailBoxSize(domainId + "");
        String netDiskSize = sysReader.getNetDiskSize(domainId + "");

        if(batch_userAccounts != null && batch_userAccounts.length > 0){
            UserBD userBD = new UserBD();
            for (int s = 0; s < batch_userAccounts.length; s++) {// 循环增加用户
                UserPO employeeVO = new UserPO();
                employeeVO.setUserAccounts(batch_userAccounts[s]);
                employeeVO.setUserPassword(batch_userPassword[s]);// 密码
                employeeVO.setEmpName(batch_empName[s]);
                employeeVO.setUserSimpleName(batch_userSimpleName[s]);
                
                employeeVO.setCanSendMail("0");
                byte empSex = 0;
                employeeVO.setEmpSex(empSex);
                byte isMarriage = 0;
                employeeVO.setEmpIsMarriage(isMarriage);
                byte empStatus = 0;
                employeeVO.setEmpStatus(empStatus);
    
                byte userIsActive = 1;
                byte userIsDeleted = 0;
                employeeVO.setUserIsActive(userIsActive);
                employeeVO.setUserIsDeleted(userIsDeleted);
                employeeVO.setUserIsFormalUser(new Integer(1));
                employeeVO.setUserSuperBegin(new Date());
                employeeVO.setUserSuperEnd(new Date());
                employeeVO.setUserOrderCode("10000.0000");
                employeeVO.setCreatedOrg(Long.parseLong(orgId));
                employeeVO.setKeyValidate("0");
                employeeVO.setDomainId(domainId+"");
                employeeVO.setSkin(Constants.DEFAULT_THEME_SKIN);
                employeeVO.setMailboxSize(mailBoxSize);
                employeeVO.setNetDiskSize(netDiskSize);
                employeeVO.setEmpDutyLevel("1000"); 
                employeeVO.setCanSendMail("0");
                employeeVO.setIsPasswordRule("0");
                employeeVO.setUserIsSleep("0");
                employeeVO.setIsAdCheck("0");
                employeeVO.setIsMobilePush("0");// 是否移动办公信息推送，0：否，1：是
                employeeVO.setIsMobileReceive("0");// 是否接收移动办公信息推送，0：否，1：是
    
                String[] orgIdArr = new String[1];
                String[] rightIdArr = new String[0];
                String[] rightScopeTypeArr = new String[0];
                String[] rightScopeScopeArr = new String[0];
                String rightScopeDsp = "";
                String[] roleIdArr = new String[1];
                roleIdArr[0] = "";
                
                orgIdArr[0] = batch_orgIds[s];//所属组织ID
                
                com.whir.org.vo.organizationmanager.OrganizationVO vo = new com.whir.org.bd.organizationmanager.OrganizationBD().getOrgByOrgId(batch_orgIds[s]);
                if(vo != null){
                    //分管领导
                    employeeVO.setChargeLeaderIds(vo.getChargeLeaderIds()!=null?vo.getChargeLeaderIds():"");
                    employeeVO.setChargeLeaderNames(vo.getChargeLeaderNames()!=null?vo.getChargeLeaderNames():"");
                    
                    //部门领导
                    employeeVO.setDeptLeaderIds(vo.getOrgManagerEmpId()!=null?vo.getOrgManagerEmpId():"");
                    employeeVO.setDeptLeaderNames(vo.getOrgManagerEmpName()!=null?vo.getOrgManagerEmpName():"");
                    
                    //上级领导
                    employeeVO.setEmpLeaderId(vo.getOrgManagerEmpId()!=null?vo.getOrgManagerEmpId():"");
                    employeeVO.setEmpLeaderName(vo.getOrgManagerEmpName()!=null?vo.getOrgManagerEmpName():"");
                }
                
                Map result = userBD.add(employeeVO, orgIdArr, rightIdArr, rightScopeTypeArr,
                        rightScopeScopeArr, rightScopeDsp, roleIdArr);
                
                long empId = -1;
                if (result.get("empId") != null) {
                    empId = ((Long) result.get("empId")).longValue();
    
                    // --------------------------------------------------------------
                    // 调用系统管理-用户接口--start--
                    try {
                        com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                        String[] interfaceInfos = sibd.getInterfaceInfo(
                                "com.whir.plugins.sys.impl.UserImpl", "addUser",
                                "1", session.getAttribute("domainId").toString());
                        if (interfaceInfos != null) {
                            Class[] paramsType = new Class[] { String.class,
                                    HttpServletRequest.class };
                            Object[] paramsValue = new Object[] { empId + "",
                                    request };
                            new com.whir.plugins.sys.InterfaceUtils().execute(
                                    interfaceInfos, paramsType, paramsValue, "1");
                        }
                    } catch (Exception e) {
                        System.out.println("--调用用户接口出错--");
                        e.printStackTrace();
                    }
                    // 调用系统管理-用户接口--end--
                    // --------------------------------------------------------------
    
                    // 记录日志信息
                    com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
                    java.util.Date date = new java.util.Date();
                    logBD.log(userId, userName, orgName, "system_user", "系统管理",
                            date, date, "1", employeeVO.getEmpName() + "/"
                                    + employeeVO.getUserAccounts(), session
                                    .getAttribute("userIP").toString(), domainId
                                    + "");
                }
            }
            printResult(G_SUCCESS);
        }

        return null;
    }
    
    /**
     * 批量修改用户
     * @return
     * @throws Exception
     */
    public String updateBatchUser() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(userIdStr)) {
            // 0.被修改用户id串，1.职务，2.所属组织id，3.上级领导id串，4.上级领导名称串
            String[] modiUserInfo = new String[10];
            modiUserInfo[0] = userIdStr;
            modiUserInfo[1] = userPO.getEmpDuty();
            modiUserInfo[2] = orgIds;
            modiUserInfo[3] = userPO.getEmpLeaderId();
            modiUserInfo[4] = userPO.getEmpLeaderName();
            modiUserInfo[5] = userPO.getMailboxSize();
            modiUserInfo[6] = userPO.getNetDiskSize();
            
            //移动办公
            String _mobileUserFlag = userPO.getMobileUserFlag();
            String _securitypolicy = userPO.getSecuritypolicy();
            if(!"1".equals(_mobileUserFlag)){
                _mobileUserFlag = "0";
                _securitypolicy = "";
            }else{
                if(CommonUtils.isEmpty(_securitypolicy)){
                    _securitypolicy = "0";
                }
            }
            modiUserInfo[7] = _mobileUserFlag;
            modiUserInfo[8] = _securitypolicy;
            //企业号
            modiUserInfo[9] = userPO.getEnterprisenumber();
            
            UserBD userBD = new UserBD();
            String result = userBD.updateBatch(modiUserInfo);

          //调用系统管理-用户接口--start--
            String[] ids = modiUserInfo[0].split(",");//被修改的用户id数组
            try{
                com.whir.org.sys.bd.SysInterfaceBD sibd = new com.whir.org.sys.bd.SysInterfaceBD();
                String[] interfaceInfos = sibd.getInterfaceInfo("com.whir.plugins.sys.impl.UserImpl", "updateUser", "1", session.getAttribute("domainId").toString());
                if(interfaceInfos != null){
                    Class[]  paramsType  = new Class[] {String.class, HttpServletRequest.class};
                    for(int i=0;i<ids.length;i++){
                    	  String id = ids[i];
                    	  Object[] paramsValue = new Object[] {id, request};
                          new com.whir.plugins.sys.InterfaceUtils().execute(interfaceInfos, paramsType, paramsValue, "1");
                    }
                  
                }
            }catch(Exception e){
                System.out.println("--调用用户接口出错--");
                e.printStackTrace();
            }
            //调用系统管理-用户接口--end--
            
            // 记录修改用户
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
            java.util.Date date = new java.util.Date();
            logBD.log(userId, userName, orgName, "system_user", "系统管理", date,
                    date, "2", result, session.getAttribute("userIP")
                            .toString(), domainId + "");

            printResult(G_SUCCESS);
        }
        return null;
    }
    
    /**
     * 复制用户
     * @return
     * @throws Exception
     */
    public String copyUser() throws Exception {

        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        String userName = session.getAttribute("userName").toString();
        String orgName = session.getAttribute("orgName").toString();

        if (!CommonUtils.isEmpty(empId)) {
            String[] ids = empId.split(",");
            UserBD userBD = new UserBD();
            String result = userBD.copyUser(ids);

            if (!CommonUtils.isEmpty(result)) {

                // 记录复制用户
                com.whir.ezoffice.security.log.bd.LogBD logBD = new com.whir.ezoffice.security.log.bd.LogBD();
                java.util.Date date = new java.util.Date();
                logBD.log(userId, userName, orgName, "system_user", "系统管理",
                        date, date, "1", result, session.getAttribute("userIP")
                                .toString(), domainId + "");
                printResult(G_SUCCESS);
            }
        }

        return null;
    }
    
    /**
     * 导出用户
     * @return
     * @throws Exception
     */
    public String exportUser() throws Exception {
        logger.debug("导出列表开始");

        Long domainId = CommonUtils.getSessionDomainId(request);

        /*
         * pageSize每页显示记录数，即list.jsp中分页部分select中的值
         */
        int pageSize = com.whir.common.util.Constants.EXPORT_PAGE_SIZE;//com.whir.common.util.CommonUtils.getUserPageSize(request);
        /*
         * orderByFieldName，用来排序的字段
         */
        String orderByFieldName = request.getParameter("orderByFieldName") != null ? request
                .getParameter("orderByFieldName")
                : "";
        /*
         * orderByType，排序的类型
         */
        String orderByType = request.getParameter("orderByType") != null ? request
                .getParameter("orderByType")
                : "";
        /*
         * currentPage，当前页数
         */
        int currentPage = 0;
        if (request.getParameter("startPage") != null) {
            currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        //导出从第一页开始
        currentPage = 1;
        
        String viewSQL = "user.empId,user.empName,user.empEnglishName,user.empSex,user.userAccounts,user.userIsSuper,organization.orgNameString,user.empDuty,user.userSimpleName,user.empLeaderName,user.canSendMail,user.empLeaderId,user.latestLogonTime,user.userSleepReasons,user.empNumber,user.securitypolicy,user.mobileUserFlag";
        String fromSQL = "com.whir.org.vo.usermanager.UserPO AS user join user.organizations organization";
        String whereSQL = "where user.domainId=" + domainId;
        
        //选中导出empId
        String selectExportId = request.getParameter("selectExportId");
        if(!CommonUtils.isEmpty(selectExportId)){
            whereSQL += " and user.empId in (" + selectExportId + ") ";
        }

        logger.debug("status:" + status);

        if ("disabled".equals(status)) {// 禁用用户
            whereSQL += " and user.userIsActive=0 and user.userIsDeleted=0 and user.userAccounts is not null and user.userIsFormalUser=1 ";

        } else if ("sleep".equals(status)) {// 休眠用户
            whereSQL += " and user.userIsActive=1 and user.userIsDeleted=0 and user.userIsSleep=1 and user.userAccounts is not null and user.userIsFormalUser=1 ";

        } else if ("apply".equals(status)) {// 申请账号用户
            whereSQL += " and user.userIsDeleted=0 and user.isApplyAccount='1' ";

        } else if ("active".equals(status)) {// 当前用户
            whereSQL += " and user.userIsActive=1 and user.userIsDeleted=0 and user.userIsSleep=0 and user.userAccounts is not null and user.userIsFormalUser=1 ";
        } else {
            return null;
        }

        /*
         * varMap，查询参数Map
         */
        Map varMap = new HashMap();
        if (!CommonUtils.isEmpty(cnName) && !CommonUtils.isEmpty(enName)) {
            if (isAllSpace(cnName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.empName like :empName or user.empEnglishName like :empEnglishName)";
                varMap.put("empName", "%" + cnName + "%");
                varMap.put("empEnglishName", "%" + enName + "%");
            } else {
                whereSQL += " and ((";
                String[] cnNameAry = getStrBySpace(cnName).split(" ");
                whereSQL += " user.empName like :empName ";
                varMap.put("empName", "%" + cnNameAry[0] + "%");
                for (int s = 1; s < cnNameAry.length; s++) {
                    whereSQL += " or user.empName like :empName" + s + " ";
                    varMap.put("empName" + s, "%" + cnNameAry[s] + "%");
                }
                whereSQL += ") or user.empEnglishName like :empEnglishName) ";
                varMap.put("empEnglishName", "%" + enName + "%");
            }
        } else if (!CommonUtils.isEmpty(cnName)) {
            if (isAllSpace(cnName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.empName like :empName) ";
                varMap.put("empName", "%" + cnName + "%");
            } else {
                String[] cnNameAry = getStrBySpace(cnName).split(" ");
                whereSQL += " and (user.empName like :empName ";
                varMap.put("empName", "%" + cnNameAry[0] + "%");
                for (int s = 1; s < cnNameAry.length; s++) {
                    whereSQL += " or user.empName like :empName" + s + " ";
                    varMap.put("empName" + s, "%" + cnNameAry[s] + "%");
                }
                whereSQL += ") ";
            }
        } else if (!CommonUtils.isEmpty(enName)) {
            whereSQL += " and user.empEnglishName like :empEnglishName ";
            varMap.put("empEnglishName", "%" + enName + "%");
        }

        if (!CommonUtils.isEmpty(userAccount)) {
            if (isAllSpace(userAccount)) {// 全是以空格组成的字符串
                whereSQL += " and (user.userAccounts like :userAccounts) ";
                varMap.put("userAccounts", "%" + userAccount + "%");
            } else {
                String[] userAccountAry = getStrBySpace(userAccount).split(" ");
                whereSQL += " and (user.userAccounts like :userAccounts ";
                varMap.put("userAccounts", "%" + userAccountAry[0] + "%");
                for (int s = 1; s < userAccountAry.length; s++) {
                    whereSQL += " or user.userAccounts like :userAccounts" + s
                            + " ";
                    varMap.put("userAccounts" + s, "%" + userAccountAry[s]
                            + "%");
                }
                whereSQL += ") ";
            }
        }

        if (!CommonUtils.isEmpty(orgName)) {
            whereSQL += " and organization.orgNameString like :orgName ";
            varMap.put("orgName", "%" + orgName + "%");
        }

        if (!CommonUtils.isEmpty(isSuper)) {
            whereSQL += " and user.userIsSuper = :isSuper ";
            varMap.put("isSuper", isSuper);
        }

        if (!CommonUtils.isEmpty(searchSimpleName)) {
            if (isAllSpace(searchSimpleName)) {// 全是以空格组成的字符串
                whereSQL += " and (user.userSimpleName like :userSimpleName) ";
                varMap.put("userSimpleName", "%" + searchSimpleName + "%");
            } else {
                String[] simpleNameAry = getStrBySpace(searchSimpleName).split(
                        " ");
                whereSQL += " and (user.userSimpleName like :userSimpleName ";
                varMap.put("userSimpleName", "%" + simpleNameAry[0] + "%");
                for (int s = 1; s < simpleNameAry.length; s++) {
                    whereSQL += " or user.userSimpleName like :userSimpleName"
                            + s + " ";
                    varMap.put("userSimpleName" + s, "%" + simpleNameAry[s]
                            + "%");
                }
                whereSQL += ") ";
            }
        }

        if (!CommonUtils.isEmpty(searchLeaderName)) {
            whereSQL += " and user.empLeaderName like :empLeaderName ";
            varMap.put("empLeaderName", "%" + searchLeaderName + "%");
        }
        
        //增加“职务”作为查询字段
        if(!CommonUtils.isEmpty(searchDuty)){
            whereSQL += " and user.empDuty like :empDuty ";
            varMap.put("empDuty", "%" + searchDuty + "%");
        }
        
        //增加“移动办公”作为查询字段
        if(!CommonUtils.isEmpty(mobileUserFlag)){
            whereSQL += " and user.mobileUserFlag = :mobileUserFlag ";
            varMap.put("mobileUserFlag", mobileUserFlag);
        }

        // 判断权限范围
        ManagerBD managerBD = new ManagerBD();
        String rightName = "00*01*02";

        HttpSession session = request.getSession(true);
        if ("1".equals(session.getAttribute("sysManager").toString()))
            rightName = "00*01*01";

        String whereTmp = managerBD.getRightWhere(session
                .getAttribute("userId").toString(), session.getAttribute(
                "orgId").toString(), rightName, "organization.orgId",
                "user.empId");
        if (whereTmp.equals("")) {
            whereTmp = "1<1";
        }

        if (whereTmp != null && !whereTmp.equals("")) {
            whereSQL += " and " + whereTmp;
        }

        logger.debug("WHERE语句：" + whereSQL);

        // 排序
        String orderBy = " order by ";
        if (!CommonUtils.isEmpty(orderByFieldName)) {
            orderBy += "user." + orderByFieldName + " " + orderByType + ",";
        }
        orderBy += "organization.orgIdString,user.empDutyLevel,user.userOrderCode,user.empName";

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
        
        List roleNameList = getUserRolesNameList(list);

        /* ......如果需要对查询结果进行扩充 start....... */
        // -------判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式 $id1$$id2$$id3$
        // 领导1,领导2,领导3,-----start
        List listTemp = new ArrayList();
        
        //user.empId,user.empName,user.empEnglishName,user.empSex,user.userAccounts,user.userIsSuper,organization.orgNameString,
        //user.empDuty,user.userSimpleName,user.empLeaderName,user.canSendMail,user.empLeaderId,user.latestLogonTime,user.userSleepReasons,
        //user.empNumber,user.securitypolicy
      
        
        if (list != null && list.size() > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserBD userBD = new UserBD();
            for (int s = 0, len=list.size(); s < len; s++) {
                Object[] objAry = (Object[]) list.get(s);// objAry[11]上级领导id串
                
                String[] newObj = new String[11];
                
                newObj[0] = (String)objAry[4];
                newObj[1] = (String)objAry[1];
                newObj[2] = (String)objAry[8];
                
                //性别
                if("0".equals(objAry[3]+"")){
                    newObj[3] = "男";
                }else if("1".equals(objAry[3]+"")){
                    newObj[3] = "女";
                }else{
                    newObj[3] = "";
                }
                
                newObj[4] = (String)objAry[6];
                newObj[5] = objAry[7]!=null?(String)objAry[7]:"";
                
                String leaderIdsStr = "";
                String leaderNamesStr = objAry[9] != null?(String)objAry[9]:"";//"";
                //--20160112 过滤被删除的上级领导     by  xiehd
                if (!CommonUtils.isEmpty(objAry[11])) {
                    String userIdString = objAry[11].toString().replaceAll(
                            "\\$", ",");
                    userIdString = userIdString.replaceAll(",,", ",");
                    if (userIdString.startsWith(",")) {
                        userIdString = userIdString.substring(1, userIdString
                                .length());
                    }
                    if (userIdString.endsWith(",")) {
                        userIdString = userIdString.substring(0, userIdString
                                .length() - 1);
                    }
                    String[] userInfo = userBD
                            .getUserIdStrAndUserNameStr(userIdString);
                    leaderIdsStr = userInfo[0];
                    leaderNamesStr = userInfo[1];
                }
                
                newObj[6] = leaderNamesStr;
                
                //特权
                if("1".equals(objAry[5]+"")){
                    newObj[7] = "是";
                }else{
                    newObj[7] = "否";
                }
                
                //最后登录时间
                if(objAry[12] != null){
                    Date date = (Date)objAry[12];
                    newObj[8] = sdf.format(date);
                }else{
                    newObj[8] = "";
                }
                newObj[9] = (String)roleNameList.get(s);
                
                if("1".equals(objAry[16]+"")){
                    newObj[10] = "是";
                }else{
                    newObj[10] = "否";
                }

                listTemp.add(newObj);
            }
        }
        // -------判断上级领导是否被禁用或删除---然后重新组成上级领导id串和上级领导名称串,格式 $id1$$id2$$id3$
        // 领导1,领导2,领导3,-----end
        /* ......如果需要对查询结果进行扩充 end....... */

        /*
         * pageCount，总页数
         */
        int pageCount = page.getPageCount();
        /*
         * recordCount，总记录数
         */
        int recordCount = page.getRecordCount();        
        
        String title = "";
        if ("disabled".equals(status)) {// 禁用用户
            title = "禁用用户";

        } else if ("sleep".equals(status)) {// 休眠用户
            title = "休眠用户";

        } else if ("apply".equals(status)) {// 申请账号用户
            title = "申请账号用户";

        } else if ("active".equals(status)) {// 当前用户
            title = "当前用户";
        } else {
            return null;
        }

        // 二维数组表示列表头部，一位数组的长度为头部长度，二维数组内容为表头名称、数据类型和输出类型，如{"姓名","String",""}，列表头部为“姓名”，数据类型为“String”，输出类型为空，字符串类型没有输出方式
        String[][] arr = { { "帐号", "String", "" }, { "中文名", "String", "" },
                { "简码", "String", "" }, { "性别", "String", "" },
                { "组织", "String", "" }, { "职务", "String", "" },
                { "上级领导", "String", "" }, { "特权", "String", "" },
                { "最后登录时间", "String", "" }, { "角色", "String", "" }, { "移动办公", "String", "" } };

        com.whir.component.export.excel.ExcelExport eep = new com.whir.component.export.excel.ExcelExport();
        StringBuffer result = new StringBuffer(64);
        result.append(eep.installExcelTitleAndHeader(title, arr));// 返回xml类型字符串

        for (int i = 0, len=listTemp.size(); i < len; i++) {
            // 循环每行数据，每行数据开始要加"<rows>"
            result.append("<rows>");
            String[] obj = (String[]) listTemp.get(i);
            for (int j = 0, k=arr.length; j < k; j++) {
                String val = obj[j];
                String type = arr[j][1];
                String outType = arr[j][2];
                result.append(eep.installExcelColumn(val, type, outType));// 返回xml类型字符串
            }
            // 每行数据结束要加"</rows>"
            result.append("</rows>");
        }

        eep.dataToExcel(request, response, result.toString());
        
        logger.debug("导出列表结束");

        return null;
    }
    
    /**
     * 获取权限信息
     * @return
     * @throws Exception
     */
    public String getRights() throws Exception {
        
        logger.debug("roleIds:"+roleIds);
        
        String[] rightIdSend = request.getParameterValues("rightIdSend");
        logger.debug("rightIdSend:"+arrToStr(rightIdSend));
        
        if (rightIdSend != null) {//重新选择的权限
            logger.debug("rightIdSend length:"+rightIdSend.length);
            request.setAttribute("oldRightId", rightIdSend);
            
            String[] rightTypeSend = request.getParameterValues("rightTypeSend");
            String[] rightScopeSend = request.getParameterValues("rightScopeSend");
            String[] rightScopeDspSend = request.getParameterValues("rightScopeDspSend");
            
            logger.debug("rightTypeSend:"+arrToStr(rightTypeSend));
            logger.debug("rightScopeSend:"+arrToStr(rightScopeSend));
            logger.debug("rightScopeDspSend:"+arrToStr(rightScopeDspSend));            
            
            request.setAttribute("oldRightType", rightTypeSend);
            request.setAttribute("oldRightScope", rightScopeSend);
            request.setAttribute("oldRightScopeDsp", rightScopeDspSend);
            
        } else {//原有存储的权限
            String[] rightId = request.getParameterValues("rightId");
            String[] rightType = request.getParameterValues("rightType");
            String[] rightScope = request.getParameterValues("rightScope");
            String[] rightScopeDsp = request.getParameterValues("rightScopeDsp");
            
            logger.debug("rightId:"+arrToStr(rightId));
            logger.debug("rightType:"+arrToStr(rightType));
            logger.debug("rightScope:"+arrToStr(rightScope));   
            logger.debug("rightScopeDsp:"+arrToStr(rightScopeDsp));

            request.setAttribute("oldRightId", rightId);
            request.setAttribute("oldRightType", rightType);
            request.setAttribute("oldRightScope", rightScope);
            request.setAttribute("oldRightScopeDsp", rightScopeDsp);
        }
        
        if(!CommonUtils.isEmpty(roleIds)){
            if(roleIds.endsWith(",")){
                roleIds = roleIds.substring(0, roleIds.length()-1);
            }
            
            RoleBD roleBD = new RoleBD();
            List rights = roleBD.getDistinctRights(roleIds);
            
            if(rights != null){
                List sysRightsList = new ArrayList();
                List customRightsList = new ArrayList();
                List customizeRightsList = new ArrayList();
                for(int i=0; i<rights.size(); i++){
                    RightVO vo = (RightVO)rights.get(i);
                    String rightCode = vo.getRightCode();
                    String codeStartStr = rightCode.indexOf("*")>0?rightCode.substring(0, rightCode.indexOf("*")):rightCode.substring(0, rightCode.indexOf("-"));
                    if(codeStartStr.equalsIgnoreCase("99")){//权限代码以"99"开头的是自定义模块
                        customRightsList.add(vo);
                    }else if(codeStartStr.equalsIgnoreCase("CRM")){//权限代码以"CRM"开头的是自定义关系
                        customizeRightsList.add(vo);
                    }else{//系统
                        sysRightsList.add(vo);
                    }
                }
                
                request.setAttribute("sysRightsList", sysRightsList);
                request.setAttribute("customRightsList", customRightsList);
                request.setAttribute("customizeRightsList", customizeRightsList);
            }
        }
        
        return "getRights";
    }

    /**
     * 检查账号是否重复
     * 
     * @return
     * @throws Exception
     */
    public String checkUserAccounts() throws Exception {
        // HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        if (!CommonUtils.isEmpty(userAccount)) {
            logger.debug("before userAccount:" + userAccount);
            userAccount = userAccount.replaceAll("'", "");
            logger.debug("after userAccount:" + userAccount);

            com.whir.common.util.DataSourceBase dsb = new com.whir.common.util.DataSourceBase();
            java.sql.Connection conn = null;
            java.sql.PreparedStatement stmt = null;
            try {
                conn = dsb.getDataSource().getConnection();
               
                String sql = "select count(emp_id) from org_employee where useraccounts='"
                        + userAccount
                        + "' and userisdeleted=0 and domain_id=? "
                        ;

                String databaseType = com.whir.common.config.SystemCommon
                        .getDatabaseType();
                if (databaseType.indexOf("sqlserver") >= 0) {
                    sql = "select count(emp_id) from org_employee where useraccounts=N'"
                            + userAccount
                            + "' and userisdeleted=0 and domain_id=? ";
                }
                if (!CommonUtils.isEmpty(empId)) {
                    sql += " and emp_id<>? ";
                }
               
                logger.debug("sql:" + sql);
                stmt = conn.prepareStatement(sql);
                stmt.setLong(1, domainId);
                if (!CommonUtils.isEmpty(empId)) {
                	stmt.setString(2, empId);
                }
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        printJsonResult("{\"result\":\"1\", \"message\":\"该账号已经被注册，请重新输入!\"}");
                    } else {
                        printJsonResult("{\"result\":\"0\", \"message\":\"该账号可以使用。\"}");
                    }
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                logger.debug(ex);
                ex.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

        return null;
    }
    
    /**
     * 判断密码在两年内是否重复，返回"0"不重复，返回"1"重复
     * @return
     * @throws Exception
     */
    public String checkPasswordIsRepeat() throws Exception {

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        
        logger.debug("userId:"+userId);
        logger.debug("password:"+password);
        
        password = password.replaceAll("￥", "&");// 还原
        
        MD5 md5 = new MD5();
        password = md5.getMD5ofStr(password);
        
        UserBD userBD = new UserBD();
        String isRepeat = userBD.getPasswordIsRepeat(userId, password);
        
        printJsonResult("{\"result\":"+isRepeat+"}");

        return null;
    }
    
    /**
     * 验证账号、简码是否重复
     * @return 1-重复
     * @throws Exception
     */
    public String checkUserInfoIsRepeat() throws Exception {
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);

        String checkType = request.getParameter("checkType");
        String checkValue = request.getParameter("checkValue");
        
        logger.debug("checkType:"+checkType);
        logger.debug("checkValue:"+checkValue);

        UserBD userBD = new UserBD();
        String returnValue = userBD.checkUserInfoIsRepeat(checkType,
                checkValue, domainId + "");

        printJsonResult("{\"result\":\"" + returnValue + "\"}");

        return null;
    }
    
    /**
     * 验证用户是否有直接下属
     * @return
     * @throws Exception
     */
    public String checkUserHasLeader() throws Exception {
    	 if (checkOperateRights() == false)
             return NO_RIGHTS;
    	  
        Long domainId = CommonUtils.getSessionDomainId(request);
        
        String checkType = request.getParameter("checkType");
        logger.debug("checkType:"+checkType);
        logger.debug("empId:"+empId);
        
        UserBD userBD = new UserBD();
        String returnValue = userBD.checkUserHasLeader(empId, checkType, domainId+"");
        
        printJsonResult("{\"result\":\"" + returnValue + "\"}");
        
        return null;
    }
    
    /**
     * 获取相关领导(分管领导：部门领导：上级领导：)
     * @return
     * @throws Exception
     */
    public String getLeaders() throws Exception {
        logger.debug("orgIds:" + orgIds);

        com.whir.org.vo.organizationmanager.OrganizationVO vo = new com.whir.org.bd.organizationmanager.OrganizationBD()
                .getOrgByOrgId(orgIds);
        
        UserBD userBD = new UserBD();
        String orgManagerEmpId = "";//部门领导
        String orgManagerEmpName = "";
        if (!CommonUtils.isEmpty(vo.getOrgManagerEmpId())) {
            String userIdString = vo.getOrgManagerEmpId().toString().replaceAll("\\$", ",");
            userIdString = userIdString.replaceAll(",,", ",");
            if (userIdString.startsWith(",")) {
                userIdString = userIdString.substring(1, userIdString.length());
            }
            if (userIdString.endsWith(",")) {
                userIdString = userIdString.substring(0,
                        userIdString.length() - 1);
            }
            String[] userInfo = userBD.getUserIdStrAndUserNameStr(userIdString);
            orgManagerEmpId = CommonUtils.isEmpty(userInfo[0])?"":userInfo[0];
            orgManagerEmpName = CommonUtils.isEmpty(userInfo[1])?"":userInfo[1];
        }
        //vo.setOrgManagerEmpId(orgManagerEmpId);
        //vo.setOrgManagerEmpName(orgManagerEmpName);
        
        String chargeLeaderIds = "";//分管领导
        String chargeLeaderNames = "";
        if (!CommonUtils.isEmpty(vo.getChargeLeaderIds())) {
            String userIdString = vo.getChargeLeaderIds().toString().replaceAll("\\$", ",");
            userIdString = userIdString.replaceAll(",,", ",");
            if (userIdString.startsWith(",")) {
                userIdString = userIdString.substring(1, userIdString.length());
            }
            if (userIdString.endsWith(",")) {
                userIdString = userIdString.substring(0,
                        userIdString.length() - 1);
            }
            String[] userInfo = userBD.getUserIdStrAndUserNameStr(userIdString);
            chargeLeaderIds = CommonUtils.isEmpty(userInfo[0])?"":userInfo[0];
            chargeLeaderNames = CommonUtils.isEmpty(userInfo[1])?"":userInfo[1];
        }
        //vo.setChargeLeaderIds(chargeLeaderIds);
        //vo.setChargeLeaderNames(chargeLeaderNames);

        String ret = "{\"chargeLeaderIds\":\""
                + chargeLeaderIds
                + "\", \"chargeLeaderNames\":\""
                + chargeLeaderNames
                + "\", \"deptLeaderIds\":\""
                + orgManagerEmpId
                + "\", \"deptLeaderNames\":\""
                + orgManagerEmpName
                + "\", \"empLeaderId\":\""
                + orgManagerEmpId
                + "\", \"empLeaderName\":\""
                + orgManagerEmpName + "\"}";

        //logger.debug("ret:" + ret);

        printJsonResult(ret);

        return null;
    }

    private void getManagerScope() {
        HttpSession session = request.getSession(true);
        // 取得管理员的管理范围
        String managerScope = "*" + session.getAttribute("orgId") + "*";
        if ("1".equals(session.getAttribute("sysManager").toString())) {
            managerScope = "*0*";
        } else {
            List list = new ManagerBD().getRightScope(session.getAttribute("userId").toString(), "00*01*02");
            if(list != null && list.size() > 0){
                Object[] obj = (Object[])list.get(0);
                String type = obj[0].toString();
                if ("4".equals(type)) {
                    if (obj[1] != null) {
                        managerScope = obj[1].toString();
                    }
                }
            }
        }
        request.setAttribute("managerScope", managerScope);
    }

    private void quickmail(int type, String userAccount, String pwd) {
        // if (com.whir.quarkmail.EditUser.getEnableMailSystem()) {
        // com.whir.quarkmail.EditUser eu = com.whir.quarkmail.EditUser
        // .getInstance();
        // switch (type) {
        // case 1:
        //
        // // 添加
        // eu.addUser(userAccount, pwd);
        // break;
        // case 2:
        //
        // // 修改
        // eu.modiUser(userAccount, pwd);
        // break;
        // case 3:
        //
        // // 删除
        // eu.delUser(userAccount);
        // break;
        //
        // case 4:
        //
        // // 禁用
        // eu.stopUser(userAccount);
        // break;
        // case 5:
        //
        // // 恢复禁用
        // eu.startUser(userAccount);
        // break;
        // case 6:
        // eu.stopSendOut(userAccount);
        // break;
        // case 7:
        // eu.allowSendOut(userAccount);
        // break;
        //
        // default:
        // break;
        // }
        // }
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

    /**
     * ToDo:根据用户信息list，找出每个用户所对应的角色名称roleNamelist
     * 
     * @param list
     *            List用户信息list
     * @return List 返回用户信息对应的角色名称roleNamelist
     */
    public List getUserRolesNameList(List list) {
        List roleNameList = new ArrayList();
        RoleBD roleBD = new RoleBD();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Object[] obj = (Object[]) list.get(i);
                List rolesList = roleBD.getUserRole(obj[0].toString());// 根据用户id获得该用户对应所拥有的角色rolesList
                String roleNameStr = "";
                if (rolesList != null && rolesList.size() > 0) {
                    for (int j = 0; j < rolesList.size(); j++) {// 循环拼装角色串格式：角色a,角色b,角色c,角色d,
                        String roleId = rolesList.get(j).toString();
                        List roleInfoList = roleBD.getSingleRoleInfo(roleId);// 根据角色id获得该角色的信息
                        if (roleInfoList != null && roleInfoList.size() > 0) {
                            Object[] objRole = (Object[]) roleInfoList.get(0);
                            roleNameStr += objRole[0].toString() + ",";
                        }
                    }
                }
                roleNameList.add(roleNameStr);
            }
        }
        return roleNameList;
    }

    /**
     * TODO:判断字符串是否全是空格
     * 
     * @param str
     *            String
     * @return boolean
     */
    private boolean isAllSpace(String str) {
        boolean returnBool = false;
        if (str != null && !str.equals("")) {
            String strTemp = str.replaceAll(" ", "");
            if (strTemp.length() == 0) {
                returnBool = true;
            }
        }
        return returnBool;
    }

    /**
     * TODO:将字符串过滤成格式111 222 333 444的字符串 如传入串 111 222 333 444，返回111 222 333
     * 444的串，中间仅以一个空格作为分割符
     * 
     * @param str
     *            String
     * @return String
     */
    private String getStrBySpace(String str) {
        String returnStr = str;
        if (returnStr.startsWith(" ")) {
            returnStr = returnStr.substring(1, returnStr.length());
            returnStr = getStrBySpace(returnStr);
        } else if (returnStr.indexOf("  ") >= 0) {
            returnStr = returnStr.replaceAll("  ", " ");
            returnStr = getStrBySpace(returnStr);
        }
        return returnStr;
    }

    private String arrToStr(String[] arr) {
        if (arr == null)
            return "";
        
        String ret = "";
        for (int i = 0; i < arr.length; i++) {
            ret += arr[i] + ",";
        }
        return ret;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearchSimpleName() {
        return searchSimpleName;
    }

    public void setSearchSimpleName(String searchSimpleName) {
        this.searchSimpleName = searchSimpleName;
    }

    public String getSearchLeaderName() {
        return searchLeaderName;
    }

    public void setSearchLeaderName(String searchLeaderName) {
        this.searchLeaderName = searchLeaderName;
    }

    public String getCnName() {
        return cnName;
    }

    public void setCnName(String cnName) {
        this.cnName = cnName;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(String isSuper) {
        this.isSuper = isSuper;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public int getDogMaxUserNum() {
        return dogMaxUserNum;
    }

    public void setDogMaxUserNum(int dogMaxUserNum) {
        this.dogMaxUserNum = dogMaxUserNum;
    }

    public int getCurrentUserNum() {
        return currentUserNum;
    }

    public void setCurrentUserNum(int currentUserNum) {
        this.currentUserNum = currentUserNum;
    }

    public UserPO getUserPO() {
        return userPO;
    }

    public void setUserPO(UserPO userPO) {
        this.userPO = userPO;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }

    public String getRightIds() {
        return rightIds;
    }

    public void setRightIds(String rightIds) {
        this.rightIds = rightIds;
    }

    public String getRightScopeTypes() {
        return rightScopeTypes;
    }

    public void setRightScopeTypes(String rightScopeTypes) {
        this.rightScopeTypes = rightScopeTypes;
    }

    public String getRightScopeScopes() {
        return rightScopeScopes;
    }

    public void setRightScopeScopes(String rightScopeScopes) {
        this.rightScopeScopes = rightScopeScopes;
    }

    public String getRightScopeDsps() {
        return rightScopeDsps;
    }

    public void setRightScopeDsps(String rightScopeDsps) {
        this.rightScopeDsps = rightScopeDsps;
    }

    public String getTansUse() {
        return tansUse;
    }

    public void setTansUse(String tansUse) {
        this.tansUse = tansUse;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getIsPasswordRule() {
        return isPasswordRule;
    }

    public void setIsPasswordRule(String isPasswordRule) {
        this.isPasswordRule = isPasswordRule;
    }

    public boolean isShowMobilePush() {
        return showMobilePush;
    }

    public void setShowMobilePush(boolean showMobilePush) {
        this.showMobilePush = showMobilePush;
    }

    public boolean isShowAdCheck() {
        return showAdCheck;
    }

    public void setShowAdCheck(boolean showAdCheck) {
        this.showAdCheck = showAdCheck;
    }

    public String getRd() {
        return rd;
    }

    public void setRd(String rd) {
        this.rd = rd;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSysManagerFlag() {
        return sysManagerFlag;
    }

    public void setSysManagerFlag(String sysManagerFlag) {
        this.sysManagerFlag = sysManagerFlag;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getSaveOperateType() {
        return saveOperateType;
    }

    public void setSaveOperateType(String saveOperateType) {
        this.saveOperateType = saveOperateType;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String[] getBatch_userAccounts() {
        return batch_userAccounts;
    }

    public void setBatch_userAccounts(String[] batchUserAccounts) {
        batch_userAccounts = batchUserAccounts;
    }

    public String[] getBatch_userPassword() {
        return batch_userPassword;
    }

    public void setBatch_userPassword(String[] batchUserPassword) {
        batch_userPassword = batchUserPassword;
    }

    public String[] getBatch_empName() {
        return batch_empName;
    }

    public void setBatch_empName(String[] batchEmpName) {
        batch_empName = batchEmpName;
    }

    public String[] getBatch_userSimpleName() {
        return batch_userSimpleName;
    }

    public void setBatch_userSimpleName(String[] batchUserSimpleName) {
        batch_userSimpleName = batchUserSimpleName;
    }

    public String[] getBatch_orgIds() {
        return batch_orgIds;
    }

    public void setBatch_orgIds(String[] batchOrgIds) {
        batch_orgIds = batchOrgIds;
    }

    public String getUserNameStr() {
        return userNameStr;
    }

    public void setUserNameStr(String userNameStr) {
        this.userNameStr = userNameStr;
    }

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }

    public String getSearchDuty() {
        return searchDuty;
    }

    public void setSearchDuty(String searchDuty) {
        this.searchDuty = searchDuty;
    }

    public String getMobileUserFlag() {
        return mobileUserFlag;
    }

    public void setMobileUserFlag(String mobileUserFlag) {
        this.mobileUserFlag = mobileUserFlag;
    }

	public String getEmpMobilePhone() {
		return empMobilePhone;
	}

	public void setEmpMobilePhone(String empMobilePhone) {
		this.empMobilePhone = empMobilePhone;
	}

	public String getEnterprisenumber() {
		return enterprisenumber;
	}

	public void setEnterprisenumber(String enterprisenumber) {
		this.enterprisenumber = enterprisenumber;
	}
	
	public static int getDOG_MAX_USER_NUM() {
		return DOG_MAX_USER_NUM;
	}

	public static void setDOG_MAX_USER_NUM(int dOGMAXUSERNUM) {
		DOG_MAX_USER_NUM = dOGMAXUSERNUM;
	}

	public static int getDOG_MAX_APP_NUM() {
		return DOG_MAX_APP_NUM;
	}

	public static void setDOG_MAX_APP_NUM(int dOGMAXAPPNUM) {
		DOG_MAX_APP_NUM = dOGMAXAPPNUM;
	}

	public static int getDOG_MAX_WEIXIN_NUM() {
		return DOG_MAX_WEIXIN_NUM;
	}

	public static void setDOG_MAX_WEIXIN_NUM(int dOGMAXWEIXINNUM) {
		DOG_MAX_WEIXIN_NUM = dOGMAXWEIXINNUM;
	}

	//判断APP用户数量
	public String judgmentAPPNum(){
		 try {
			if (checkOperateRights() == false)
			     return NO_RIGHTS;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String userAccount = request.getParameter("userAccount");
		String empId = request.getParameter("empId")==null?"":request.getParameter("empId");
		UserBD userBD = new UserBD();
		String app ="";
		if("".equals(empId)||empId==null){
			app= userBD.getAPPnum(userAccount);
		}else{
			app= userBD.getAPPnumByEmpId(empId);
		}
		
		//String weixinNum = userBD.getEnterprisenumbernum(userId);
		int appnum = 0;
		 try{
			 appnum = Integer.parseInt(app);
         }catch(Exception e){}
		
		String res = "";
		if(appnum>=DOG_MAX_APP_NUM){
			res="移动APP用户数量已超出最大值！";
			printResult("false","{'res':'"+res+"'}");
		}else{
			printResult("true","{'res':'"+res+"'}");
		}
		return null;
	}
	//判断微信企业号用户数量
	public String judgmentWeixinNum(){
		String userAccount = request.getParameter("userAccount");
		String empId = request.getParameter("empId")==null?"":request.getParameter("empId");
		UserBD userBD = new UserBD();
		//String app = userBD.getAPPnum(userId);
		String weixin="";
		if("".equals(empId)||empId==null){
			weixin = userBD.getEnterprisenumbernumByEmpId(empId);
		}else{
			weixin = userBD.getEnterprisenumbernum(userAccount);
		}
		 
		int weixinnum = 0;
		 try{
			 weixinnum = Integer.parseInt(weixin);
		 }catch(Exception e){}
		String res = "";
		if(weixinnum>=DOG_MAX_WEIXIN_NUM){
			res="企业号用户数量已超出最大值！";
			printResult("false","{'res':'"+res+"'}");
		}else{
			printResult("true","{'res':'"+res+"'}");
		}
		return null;
	}
	
	//批量修改用户时判断App用户数量
	public String batchjudgmentAPPNum(){
		 try {
			if (checkOperateRights() == false)
			     return NO_RIGHTS;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String userIdString = request.getParameter("userIdStr");
		int idNum = userIdString.split(",").length;
		UserBD userBD = new UserBD();
//		userIdString = "$"+userIdString.replace(",", "$$")+"$";
//		String userAccountString = userBD.getUserAccountByIds(userIdString);
		String res = "";
//		String app = userBD.getAPPnum(userAccount);
		String app = userBD.getAPPnumByEmpId(userIdString);
		//String weixinNum = userBD.getEnterprisenumbernum(userId);
		int appnum = 0;
		 try{
			 appnum = Integer.parseInt(app);
         }catch(Exception e){}
         if(appnum+idNum>DOG_MAX_APP_NUM){
 			res="移动APP用户数量已超出最大值！";
 			printResult("false","{'res':'"+res+"'}");
 		}else{
 			printResult("true","{'res':'"+res+"'}");
 		}
		
		return null;
	}

	//批量修改用户时判断App用户数量
	public String batchjudgmentWeixinNum(){
		
		String userIdString = request.getParameter("userIdStr");
		int idNum = userIdString.split(",").length;
		UserBD userBD = new UserBD();
		//userIdString = "$"+userIdString.replace(",", "$$")+"$";
		//String userAccountString = userBD.getUserAccountByIds(userIdString);
		String res = "";
		//String weixin = userBD.getEnterprisenumbernum(userAccount);
		String weixin = userBD.getEnterprisenumbernumByEmpId(userIdString);
		int weixinnum = 0;
		 try{
			 weixinnum = Integer.parseInt(weixin);
		 }catch(Exception e){}
		if(weixinnum+idNum>DOG_MAX_WEIXIN_NUM){
			res="企业号用户数量已超出最大值！";
			printResult("false","{'res':'"+res+"'}");
		}else{
			printResult("true","{'res':'"+res+"'}");
		}
		return null;
	}
	
	//批量获取用户时用户mobileUserFlag
	public String batchgetAPP(){
		 try {
			if (checkOperateRights() == false)
			     return NO_RIGHTS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String ids = request.getParameter("ids");
		UserBD userBD = new UserBD();
		String mobileUserFlags = userBD.batchgetAPP(ids);
		String res="";
		if(mobileUserFlags.indexOf("1")!=-1){
			res="选中的用户中包含APP用户！";
			printResult("true","{'res':'"+mobileUserFlags+"'}");
		}else{
			res="选中的用户中不包含APP用户！";
			printResult("false","{'res':'"+mobileUserFlags+"'}");
		}
		return null;
	}
	
	//批量获取用户时用户Enterprisenumber
	public String batchgetEnterprisenumber(){
		String ids = request.getParameter("ids");
		UserBD userBD = new UserBD();
		String enterprisenumbers = userBD.batchgetEnterprisenumber(ids);
		String res="";
		if(enterprisenumbers.indexOf("1")!=-1){
			res="选中的用户中包含企业号用户！";
			printResult("true","{'res':'"+enterprisenumbers+"'}");
		}else{
			res="选中的用户中不包含企业号用户！";
			printResult("false","{'res':'"+enterprisenumbers+"'}");
		}
		return null;
	}
	
	/**
     * 检查身份证号是否重复
     * 
     * @return
     * @throws Exception
     */
    public String empIdCardCheck() throws Exception {
        // HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String empIdCard = request.getParameter("empIdCard");
        if (!CommonUtils.isEmpty(empIdCard)) {
            logger.debug(" empIdCard:" + empIdCard);
           

            com.whir.common.util.DataSourceBase dsb = new com.whir.common.util.DataSourceBase();
            java.sql.Connection conn = null;
            java.sql.PreparedStatement stmt = null;
            try {
                conn = dsb.getDataSource().getConnection();
               // stmt = conn.createStatement();
                String sql="";
                String databaseType = com.whir.common.config.SystemCommon
                        .getDatabaseType();
               
                sql = "select count(emp_id) from org_employee where empidcard=? and userisdeleted=0 and domain_id=? " ;
                
                if (!CommonUtils.isEmpty(empId)) {
                    sql += " and emp_id<>? ";
                }

                logger.debug("sql:" + sql);
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, empIdCard);
                stmt.setLong(2, domainId);
                if (!CommonUtils.isEmpty(empId)) {
                	 stmt.setString(3, empId);
                }
                
                java.sql.ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {
                        printJsonResult("{\"result\":\"1\", \"message\":\"该身份证号已经被注册，请重新输入!\"}");
                    } else {
                        printJsonResult("{\"result\":\"0\", \"message\":\"该身份证号可以使用。\"}");
                    }
                }
                rs.close();
                stmt.close();
            } catch (Exception ex) {
                logger.debug(ex);
                ex.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.close();
                }
            }
        }

        return null;
    }
}
