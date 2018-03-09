package com.whir.org.manager.actionsupport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.ConversionString;
import com.whir.common.util.StringSplit;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.StringUtils;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.org.bd.SelectForWorkFlow;
import com.whir.org.bd.groupmanager.GroupBD;
import com.whir.org.bd.organizationmanager.OrganizationBD;
import com.whir.org.common.util.ConvertIdAndName;
import com.whir.org.common.util.EndowVO;
import com.whir.org.manager.bd.ConvertCodeToID;
import com.whir.org.manager.bd.ManagerBD;
import com.whir.org.manager.bd.ManagerService;

/**
 * 选择组织用户群组页面Action
 * 
 * @author wang
 * 
 */
public class SelectOrgAndUserAction extends BaseActionSupport {
    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger
            .getLogger(SelectOrgAndUserAction.class.getName());
    
    public static final String MODULE_CODE = "SelectOrgAndUserAction";

    // 页面上存储选择的id的控件名称(隐藏域)
    private String allowId;
    // 页面上显示选择的名称的控件名称(显示框)
    private String allowName;
    // select=org表示选择组织,select=user表示选择用户,select=group表示选择组
    private String select;
    // single=yes表示单选,single=no表示多选
    private String single;
    // 选择的类型org,user,group的组合
    private String show;
    // 选择的范围
    private String range;
    // 当前组织ID
    private String currentOrgId;
    // 父组织ID
    private String parentOrgId;
    // 是否限制 1-是
    private String limited;
    // 来自模块：父页面-parent,邮件-mail,人事-hrm,短信-message,自定义表单-customform
    private String fromModule;
    // 是否继承父对象：1-是，0-否
    private String fromParent;

    // key key=id表示需要获得user、org或group的主键，key=code表示需要获得user的账号、org的编码或group的编码
    private String key;

    // 单选radio 多选checkbox
    private String type;

    // 组织ID
    private String orgId;
    // 组织编码
    private String orgSerial;
    // 组织名称
    private String orgName;

    private String groupId;
    private String groupName;
    private String groupCode;

    // 群组 1-个人群组 0-公共群组
    private String groupType;
    // 快捷
    private String isShortcut;

    // 外单位 1-外单位 -1-本单位
    private String otherDepart;

    // 指定组织名称串和ID串
    private String nameString;
    private String idString;

    // 选中的ID和名称
    private String selectedId;
    private String selectedName;

    // -------------------------------------------------
    // 查询参数
    private String searchUserCName;
    private String searchDutyOpr;
    private String searchDutyLevel;
    private String searchUserAccounts;
    private String searchUserEName;
    private String searchUserSimpleName;

    private String searchOrgId;
    private String searchOrgName;
    private String searchOrgSerial;

    private String searchGroupName;
    private String searchGroupCode;

    // -------------------------------------------------
    
    //选择确定回调js函数
    private String callbackOK;
    private String callbackCancel;
    
    //0-不显示 1-显示
    private String isShowCheckbox = "0";
    private String isShowAllBtn = "1";
    //是否显示当前用户所在的兼职组织 0-不显示 1-显示
    private String showSidelineorg = "1";
    //选择范围：1-本人 3-本组织 2-本组织及下属组织 0-全部 4-自定义
    private String scopeType = "";
    
    //用于角色用户设置可以选择组织\群组\岗位
    private String ogp = "";//1-是

    /**
     * 加载选人页面
     * 
     * @return
     * @throws Exception
     */
    public String init() throws Exception {
        HttpSession session = request.getSession(true);

        // po.id,po.dutyName,po.dutyLevel
        List dutyList = new ManagerBD().getAllDuty(CommonUtils.getSessionDomainId(request) + "");

        request.setAttribute("dutyList", dutyList);
        
        logger.debug("0-init:range:"+range);        
        if(CommonUtils.isEmpty(range) || "0".equals(range) || "*0*".equals(range)){
            //不是来自父对象及自定义表单
            if(!"1".equals(fromParent) && !"customform".equals(fromModule)){
                if(CommonUtils.isEmpty(limited)){
                    range = session.getAttribute("browseRange").toString();
                    //新工作流程根据code选择用户
                    if ("code".equals(key)) {
                        if(!CommonUtils.isEmpty(range)){
                            ConversionString conversionString = new ConversionString(range);
                            //String scopeUserIds = conversionString.getUserIdString();
                            String scopeOrgIds = conversionString.getOrgIdString();
                            //String scopeGroupIds = conversionString.getGroupIdString();
                            if(!CommonUtils.isEmpty(scopeOrgIds)){
                                if(scopeOrgIds.startsWith(",")){
                                    scopeOrgIds = scopeOrgIds.substring(1);
                                }
                                if(scopeOrgIds.endsWith(",")){
                                    scopeOrgIds = scopeOrgIds.substring(0, scopeOrgIds.length()-1);
                                }
                                if(!CommonUtils.isEmpty(scopeOrgIds)){
                                    DbOpt dbopt = new DbOpt();
                                    try{
                                        String[][] orgSerials = dbopt.executeQueryToStrArr2("select t.orgserial from org_organization t where t.org_id in ("+scopeOrgIds+")", 1);
                                        if(orgSerials != null){
                                            range = "";
                                            for(int i=0; i<orgSerials.length; i++){
                                                range += "*" + orgSerials[i][0] + "*";
                                            }
                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }finally{
                                        try{
                                            if(dbopt!=null)dbopt.close();
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }else {
            if("1".equals(showSidelineorg)){//显示当前用户所在的兼职组织
                String orgId = CommonUtils.getSessionOrgId(request)+"";
                String orgSerial = CommonUtils.getSessionOrgSerial(request);
                String userId = CommonUtils.getSessionUserId(request)+"";
                //新工作流程根据code选择用户
                if ("code".equals(key)) {
                    //本组织范围
                    if(("*"+orgSerial+"*").equals(range)){
                        DbOpt dbopt = new DbOpt();
                        try{
                            String sidelineorg = dbopt.executeQueryToStr("select t.sidelineorg from org_employee t where t.emp_id=?", new Object[]{userId});
                            if(!CommonUtils.isEmpty(sidelineorg)){
                                if(sidelineorg.startsWith("*") && sidelineorg.endsWith("*")){
                                    sidelineorg = sidelineorg.substring(1, sidelineorg.length()-1);
                                    String[] sidelineorgArr = sidelineorg.split("\\*\\*");
                                    for(int i=0; i<sidelineorgArr.length; i++){
                                        String sidelineorgserial = dbopt.executeQueryToStr("select t.orgserial from org_organization t where t.orgstatus=0 and t.org_id=?", new Object[]{sidelineorgArr[i]});
                                        if(!CommonUtils.isEmpty(sidelineorgserial)){
                                            if(range.indexOf("*"+sidelineorgserial+"*") == -1){
                                                range += "*"+sidelineorgserial+"*";
                                            }
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            try{
                                if(dbopt!=null)dbopt.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    //本组织范围
                    if(("*"+orgId+"*").equals(range)){
                        DbOpt dbopt = new DbOpt();
                        try{
                            String sidelineorg = dbopt.executeQueryToStr("select t.sidelineorg from org_employee t where t.emp_id=?", new Object[]{userId});
                            if(!CommonUtils.isEmpty(sidelineorg)){
                                if(sidelineorg.startsWith("*") && sidelineorg.endsWith("*")){
                                    sidelineorg = sidelineorg.substring(1, sidelineorg.length()-1);
                                    String[] sidelineorgArr = sidelineorg.split("\\*\\*");
                                    for(int i=0; i<sidelineorgArr.length; i++){
                                        String sidelineorgid = dbopt.executeQueryToStr("select t.org_id from org_organization t where t.orgstatus=0 and t.org_id=?", new Object[]{sidelineorgArr[i]});
                                        if(!CommonUtils.isEmpty(sidelineorgid)){
                                            if(range.indexOf("*"+sidelineorgid+"*") == -1){
                                                range += "*"+sidelineorgid+"*";
                                            }
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }finally{
                            try{
                                if(dbopt!=null)dbopt.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        
        logger.debug("1-init:range:"+range);
        
        if("0".equals(range)){
            range = "*0*";
        }
        
        request.setAttribute("range", range);
        
        request.setAttribute("selfUnitId", getSelfUnitId());
        
        allowId = StringUtils.ignoreInvialdChar(allowId);
        allowName = StringUtils.ignoreInvialdChar(allowName);
        show = StringUtils.ignoreInvialdChar(show);
        select = StringUtils.ignoreInvialdChar(select);

        return "init";
    }

    private String getSelfUnitId(){        
        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();
        
        String tmpOrgIdString = orgIdString;
        if(orgIdString.startsWith("$") && orgIdString.endsWith("$")){
            tmpOrgIdString = orgIdString.substring(1, orgIdString.length() - 1);
        }
        
        DbOpt dbopt = new DbOpt();
        try{            
            String[] orgIdArr = tmpOrgIdString.split("\\$\\$");            
            for(int i=orgIdArr.length - 1; i>=0; i--){
                if(!CommonUtils.isEmpty(orgIdArr[i])){
                    String selfUnitId = dbopt.executeQueryToStr("select org_id from org_organization where orgtype=0 and org_id=?", new Object[]{orgIdArr[i]});
                    if(!CommonUtils.isEmpty(selfUnitId)){
                        return selfUnitId;
                    }
                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt != null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
        return "-1";
    }
    
    /**
     * 显示组织树
     * 
     * @return
     * @throws Exception
     */
    public String filterOrgTree() throws Exception {
        HttpSession session = request.getSession(true);

        logger.debug("异步加载组织filterOrgList[START]");
        logger.debug("allowId:" + allowId);
        logger.debug("allowName:" + allowName);
        logger.debug("select:" + select);
        logger.debug("show:" + show);
        logger.debug("single:" + single);
        logger.debug("range:" + range);
        logger.debug("currentOrgId:" + currentOrgId);
        logger.debug("parentOrgId:" + parentOrgId);
        logger.debug("limited:" + limited);
        logger.debug("fromModule:" + fromModule);
        logger.debug("key:" + key);
        logger.debug("scopeType:" + scopeType);
        
        //检查参数合法性
        if(CommonUtils.checkValidCharacter(parentOrgId) == false){
            logger.debug("非法参数：parentOrgId");
            return null;
        }
        
        String searchOrgNameTree = request.getParameter("searchOrgNameTree");
        logger.debug("searchOrgNameTree:" + searchOrgNameTree);
        
        String tempRange = range;

        DataSource ds = new com.whir.common.util.DataSourceBase().getDataSource();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        OrganizationBD organizationBD = new OrganizationBD();

        StringBuffer buffer = new StringBuffer("[");
        try {
            conn = ds.getConnection();
            //stmt = conn.prepareStatement("");

            // String type = request.getParameter("type");
            // String range = request.getParameter("range");
            // String fromParent = request.getParameter("fromParent");

            String domainId = CommonUtils.getSessionDomainId(request) + "";

            // key=id表示需要获得user、org或group的主键，key=code表示需要获得user的账号、org的编码或group的编码。
            String org_ = "org_id";
            if ("code".equals(key)) {
                org_ = "orgSerial";
            }

            // 取组织
            if (range == null || "".equals(range) || range.indexOf("*") >= 0) {
                // String parentOrgId = request.getParameter("parentOrgId");

                if (parentOrgId == null) {
                    parentOrgId = "0";// 根组织
                }
                
                ManagerBD managerBD = new ManagerBD();

                StringBuffer rangeIdStringBuffer = new StringBuffer();

                String tmpSql = "";
                String databaseType = com.whir.common.config.SystemCommon
                        .getDatabaseType();

                DatabaseMetaData dbmd = conn.getMetaData();// 用于判断数据库
                String databaseName = dbmd.getDatabaseProductName();

                if(!"*0*".equals(range)){
                    if (databaseType.indexOf("mysql") >= 0) {
                        tmpSql = "select orgidstring from org_organization where ? like concat('%*'," + org_ + ",'*%') and domain_id=" + domainId;
                    } else if (databaseType.indexOf("db2") >= 0) {
                        tmpSql = "select orgidstring from org_organization where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*',char(" + org_ + ")),'*%'),?)>0 and domain_id=" + domainId;
                    } else {
                        if (databaseName.equals("DM DBMS")) {// 如果是达梦数据库
                            // 系统中连接字符串函数有时不支持，有时支持,故写成如下方式
                            tmpSql = "select orgidstring from org_organization where ? like '%*'||" + org_ + "||'*%' and domain_id=" + domainId;
                        } else {
                            tmpSql = "select orgidstring from org_organization where ? like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*'," + org_ + "),'*%') and domain_id=" + domainId;
                        }
                    }
                    
                    logger.debug("tmpSql:"+tmpSql);
                    stmt = conn.prepareStatement(tmpSql);
                    stmt.setString(1, range);
                    rs =stmt.executeQuery();
    
                    while (rs.next()) {
                        String _orgIdStr = StringSplit.splitOrgIdString(rs.getString(1), "$", "_");
                        rangeIdStringBuffer.append(_orgIdStr);
                    }
                    rs.close();
                }

                String rangeIdString = rangeIdStringBuffer.toString();

                logger.debug("处理前range:" + range);
                if ("code".equals(key)) {
                    if(!"".equals(range) && !"0".equals(range) && !"*0*".equals(range)){
                        SelectForWorkFlow selectForWorkFlow = new SelectForWorkFlow();
                        Map map = selectForWorkFlow.groupString(range);
                        range = map.get("orgCodes").toString();
                        List orgList = selectForWorkFlow.getOrgIdListByOrgCodes(range, domainId);
                        range = "";
                        if (orgList != null) {
                            for (int i = 0, j=orgList.size(); i < j; i++) {
                                Object[] arr = (Object[]) orgList.get(i);
                                range += "*" + arr[0] + "*";
                            }
                        }
                        logger.debug("处理中range:" + range);
                    }
                }
                logger.debug("处理后range:" + range);

                Pattern p = null; // 正则表达式
                Matcher m = null; // 操作的字符串

                p = Pattern.compile("\\$[0-9]*\\$");
                m = p.matcher(range);
                range = m.replaceAll("");

                p = Pattern.compile("@[0-9]*@");
                m = p.matcher(range);
                range = m.replaceAll("");

                range = range.replaceAll("\\*\\*", "\\$,\\$");
                range = range.replaceAll("\\*", "\\$");

                logger.debug("处理后range2:" + range);

                //$123$,$456$
                String[] rangeArr = range.split(",");

                String orgId, orgName, orgHasChild, orgIdString, serial;
                int orgLevel = 0;
                int hasHref = 0;
                int show = 0;
                int i = 0;
                //tmpSql = "select org_id,orgName,orgHasJunior,orgLevel,orgIdString,orgSerial from org_organization where orgParentOrgId=" + parentOrgId + " and orgstatus=0 and domain_id=" + domainId;
                
                String selfUnit = request.getParameter("selfUnit");//本单位组织架构
                if("1".equals(selfUnit)){
                    if ("0".equals(parentOrgId)) {
                        String selfUnitId = request.getParameter("selfUnitId");
                        tmpSql = "select org_id,orgName,orgHasJunior,orgLevel,orgIdString,orgSerial from org_organization where org_id=" + (new Long(selfUnitId)) + " and orgstatus=0 and domain_id=" + domainId;
                    }else{
                        tmpSql = "select org_id,orgName,orgHasJunior,orgLevel,orgIdString,orgSerial from org_organization where orgParentOrgId=" + parentOrgId + " and orgstatus=0 and domain_id=" + domainId;
                    }
                    
                }else{
                    tmpSql = "select org_id,orgName,orgHasJunior,orgLevel,orgIdString,orgSerial from org_organization where orgParentOrgId=" + parentOrgId + " and orgstatus=0 and domain_id=" + domainId;
                    if ("0".equals(parentOrgId)) {
                        String rootCorpId = session.getAttribute("rootCorpId")!=null?session.getAttribute("rootCorpId").toString():"";
                        if (!CommonUtils.isEmpty(rootCorpId)) {
                            if ("1".equals(request.getParameter("fromDoc"))) {
                                if (databaseType.indexOf("mysql") >= 0) {
                                    tmpSql += " and orgIdString like '%$" + rootCorpId + "$%'";
                                } else if (databaseType.indexOf("db2") >= 0) {
                                    tmpSql += " and orgIdString like '%$" + rootCorpId + "$%'";
                                } else {
                                    tmpSql += " and orgIdString like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$','" + rootCorpId + "'),'$%')";
                                }
                            }
                        }
                        
                        //根据searchOrgNameTree过滤组织树
                        if(!CommonUtils.isEmpty(searchOrgNameTree)){
                            searchOrgNameTree = searchOrgNameTree.replaceAll("'", "");
                            
                            String orgIds = "-1";
                            String searchOrgNameSql = "select orgIdString from org_organization where orgstatus=0 and domain_id=? "  ;
                            searchOrgNameSql += " and orgName like ? ";
                            stmt = conn.prepareStatement(searchOrgNameSql);
                            stmt.setString(1, domainId);
                            stmt.setString(2, "%" + searchOrgNameTree + "%");
                            rs = stmt.executeQuery();
                            while (rs.next()) {
                                String _orgIdString = rs.getString(1);
                                String _orgIdStr = StringSplit.splitOrgIdString(_orgIdString, "$", "_");
                                _orgIdStr = _orgIdStr.substring(1);
                                //根父节点
                                String _rootParentId = _orgIdStr.substring(0, _orgIdStr.indexOf("$"));
                                
                                orgIds += "," + _rootParentId;
                            }
                            logger.debug("1.searchOrgNameTree orgIds:"+orgIds);
                            
                            tmpSql += " and org_id in (" + orgIds + ")";
                        }
                    }else{
                        
                        //根据searchOrgNameTree过滤组织树
                        if(!CommonUtils.isEmpty(searchOrgNameTree)){
                            searchOrgNameTree = searchOrgNameTree.replaceAll("'", "");
                            
                            String orgIds = "-1";
                            String searchOrgNameSql = "select orgidstring, orgParentOrgId, org_id from org_organization where orgstatus=0 and domain_id=? " ;
                            searchOrgNameSql += " and orgName like ? ";
                            
                            logger.debug("searchOrgNameSql:"+searchOrgNameSql);
                            stmt = conn.prepareStatement(searchOrgNameSql);
                            stmt.setString(1, domainId);
                            stmt.setString(2, "%" + searchOrgNameTree + "%");
                            rs = stmt.executeQuery();
                            while (rs.next()) {
                                String _orgIdString = rs.getString(1);
                                if(_orgIdString.indexOf("$"+parentOrgId+"$") != -1){
                                    orgIds += "," + rs.getString(2) + "," + rs.getString(3);
                                    
                                    //-----------------------------------------
                                    //寻找符合条件的当前父节点下的子节点
                                    String _orgIdStr = StringSplit.splitOrgIdString(_orgIdString, "$", "_");
                                    
                                    logger.debug("_orgIdStr:"+_orgIdStr);
                                    
                                    String tmpStr = "$" + parentOrgId + "$";
                                    _orgIdStr = _orgIdStr.substring(_orgIdStr.indexOf(tmpStr)+ tmpStr.length());//.replaceAll("\\$"+parentOrgId+"\\$","");
                                    logger.debug("_orgIdStr2:"+_orgIdStr);
                                    if(_orgIdStr.indexOf("$") != -1){
                                        _orgIdStr = _orgIdStr.substring(1);
                                        String subOrgId = _orgIdStr.substring(0, _orgIdStr.indexOf("$"));
                                        if(!CommonUtils.isEmpty(subOrgId)){
                                            orgIds += "," + subOrgId;
                                        }
                                    }
                                    //-----------------------------------------
                                }
                            }
                            logger.debug("2.searchOrgNameTree orgIds:"+orgIds);
                            
                            tmpSql += " and org_id in (" + orgIds + ")";
                        }
                    }
                }

                // --------快捷时 只显示自己所在组织级上级组织---start
                if ("1".equals(isShortcut)) {
                    logger.debug("快捷isShortcut:" + isShortcut);

                    String orgIdStringTemp = session
                            .getAttribute("orgIdString").toString();
                    if (!CommonUtils.isEmpty(orgIdStringTemp)) {
                        orgIdStringTemp = orgIdStringTemp
                                .replaceAll("\\$", ",");
                        orgIdStringTemp = orgIdStringTemp.replaceAll(",,", ",");
                        orgIdStringTemp = orgIdStringTemp.replaceAll(",,", ",");
                        orgIdStringTemp = orgIdStringTemp.substring(1,
                                orgIdStringTemp.length() - 1);
                        String[] orgIdAryTemp = orgIdStringTemp.split(",");
                        String orgInStr = " and org_id in (-1";
                        for (int ks = 0; ks < orgIdAryTemp.length; ks++) {
                            orgInStr += "," + orgIdAryTemp[ks];
                        }
                        orgInStr += ") ";
                        tmpSql += orgInStr;
                    }
                }
                // --------快捷时 只显示自己所在组织及上级组织---end                

                tmpSql += " order by orgidstring";
                
                logger.debug("tmpSql:" + tmpSql);
                
                logger.debug("rangeIdString:"+rangeIdString);
                logger.debug("range:"+range);
                stmt = conn.prepareStatement(tmpSql);
                rs = stmt.executeQuery();
                while (rs.next()) {
                    orgId = rs.getString(1);
                    orgName = rs.getString(2).trim();
                    orgHasChild = rs.getString(3);
                    orgLevel = rs.getInt(4);
                    orgIdString = rs.getString(5);
                    serial = rs.getString(6);
                    
                    logger.debug("orgIdString:"+orgIdString);
                    
                    hasHref = 0;
                    show = 0;
                    if ("".equals(range) || "$0$".equals(range)) {
                        hasHref = 1;
                        show = 1;
                    } else {
                        if(!"3".equals(this.scopeType)){//本组织级下属组织
                            for (i = 0; i < rangeArr.length; i++) {
                                logger.debug("rangeArr["+i+"]:"+rangeArr[i]);
                                if (orgIdString.indexOf(rangeArr[i]) >= 0) {
                                    hasHref = 1;
                                    show = 1;
                                    break;
                                }
                            }
                        }
                        
                        if (rangeIdString.indexOf("$" + orgId + "$") >= 0) {
                            show = 1;
                        }
                    }
                    
                    logger.debug("show:"+show);
                    
                    if(show == 0){
                        continue;
                    }

                    buffer.append("{id:" + orgId + ", ");
                    buffer.append("pId:" + parentOrgId + ", ");
                    buffer.append("parentOrgId:" + orgId + ", ");// 用于根据parentOrgId获取下级组织
                    buffer.append("name:'" + orgName + "', ");
                    buffer.append("level:" + orgLevel + ", ");
                    buffer.append("open:"
                            + ("0".equals(orgLevel + "") ? "true" : "false")
                            + ", ");
                    
                    boolean hasSubOrgs = organizationBD.hasSubOrgs(orgId+"", domainId + "");
                    
                    buffer.append("isParent:" + (hasSubOrgs?"true":"false") + ", ");
                    
                    //buffer.append("isParent:" + ("1".equals(orgHasChild + "") ? "true" : "false") + ", ");
                    
                    buffer.append("childCount:'" + orgHasChild + "', ");
                    buffer.append("type:'org', ");
                    buffer.append("serial:'" + serial + "', ");
                    buffer.append("orgIdString:'" + orgIdString + "', ");
                    
                    boolean isCanLink = true;
                    if(!CommonUtils.isEmpty(range) && !"0".equals(range) && !"*0*".equals(range) && !"$0$".equals(range)){
                        range = range.replaceAll(",", "");//去除,逗号
                        String _orgIdStr = StringSplit.splitOrgIdString(orgIdString, "$", "_");
                        //logger.debug("_orgIdStr:"+_orgIdStr);
                        isCanLink = managerBD.isOwnerRangeOrgStr(_orgIdStr, range, "$");
                        logger.debug("isCanLink:"+isCanLink);
                    }
                    
                    buffer.append("isCanLink:" + isCanLink + ", ");//是否可以点击链接

                    // 页签快捷，只有是用户自己所在组织才显示链接---start
                    if ("1".equals(isShortcut)) {
                        if (!orgId.equals(session.getAttribute("orgId")
                                .toString())) {
                            hasHref = 0;// 不显示链接
                        }
                    }
                    // 页签快捷，只有是用户自己所在组织才显示链接---end

                    buffer.append("hasHref:" + hasHref + ", ");
                    buffer.append("show:" + show + "},");

                }
                rs.close();
            }
            
            if (!"1".equals(isShortcut)) {//不显示快捷其他
                //选择范围内含有用户，如:$97853988$$97840206$
                if ((CommonUtils.isEmpty(parentOrgId) || "0".equals(parentOrgId))
                        && tempRange != null && tempRange.indexOf("$") != -1) {
                    buffer.append("{id:99999999999999, ");
                    buffer.append("pId:0, ");
                    buffer.append("parentOrgId:99999999999999, ");
                    buffer.append("name:'选择其它', ");
                    buffer.append("level:0, ");
                    buffer.append("open:false, ");
                    buffer.append("isParent:false, ");
    
                    buffer.append("childCount:'0', ");
                    buffer.append("type:'otheruser', ");
                    buffer.append("serial:'', ");
                    buffer.append("orgIdString:'', ");
                    buffer.append("isCanLink:true, ");// 是否可以点击链接
                    buffer.append("hasHref:1, ");
                    buffer.append("show:1}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        buffer.append("]");

        printJsonResult(buffer.toString());

        logger.debug("返回数据：" + buffer);
        logger.debug("异步加载组织[END]");

        return null;
    }

    /**
     * 显示群组树
     * 
     * @return
     * @throws Exception
     */
    public String filterGroupTree() throws Exception {
        HttpSession session = request.getSession(true);

        logger.debug("异步加载组filterGroupList[START]");
        logger.debug("allowId:" + allowId);
        logger.debug("allowName:" + allowName);
        logger.debug("select:" + select);
        logger.debug("show:" + show);
        logger.debug("single:" + single);
        logger.debug("range:" + range);
        logger.debug("currentOrgId:" + currentOrgId);
        logger.debug("parentOrgId:" + parentOrgId);
        logger.debug("limited:" + limited);
        logger.debug("fromModule:" + fromModule);
        logger.debug("key:" + key);

        DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        StringBuffer buffer = new StringBuffer("[");
        try {
            conn = ds.getConnection();
           

            // String type = request.getParameter("type");
            //String range = request.getParameter("range");
            String fromParent = request.getParameter("fromParent");

            String domainId = CommonUtils.getSessionDomainId(request) + "";

            // key=id表示需要获得user、org或group的主键，key=other表示需要获得user的账号、org的编码或group的编码。
            String group_ = "a.group_id";
            if ("code".equals(key)) {
                group_ = "a.group_code";
            }

            String userId = session.getAttribute("userId").toString();
            String orgIdString = session.getAttribute("orgIdString").toString();
            String where = "";

            /**
             * 以下代码是实现，如果当前用户有兼职组织，并且该兼职组织是群组的适用范围组织，或者是群组的适用范围组织的子组织
             * 则，该用户也可以选择到该群组的信息
             */
            // 获得当前用户的兼职组织id串-----start
            String sideLineOrgIds = "";// 兼职组织串格式*239600**456214*
            String sideLineOrgSql = "select emp.sidelineorg from org_employee emp where emp.emp_id=? ";
            stmt = conn.prepareStatement(sideLineOrgSql);
            stmt.setString(1, userId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                sideLineOrgIds = rs.getString(1) != null ? rs.getString(1) : "";
            }
            rs.close();
            // 获得当前用户的兼职组织id串-----end

            // 获得该用户的所有兼职组织的上级或上级的上级..组织串----start
            String orgIdsStr = "";
            if (!CommonUtils.isEmpty(sideLineOrgIds)) {
                sideLineOrgIds = sideLineOrgIds.replace('*', ',');
                sideLineOrgIds = sideLineOrgIds.replaceAll(",,", ",");
                String[] orgIdAry = sideLineOrgIds.substring(1,
                        sideLineOrgIds.length() - 1).split(",");

                // 循环获得兼职组织的组织id串字段值
                for (int s = 0; s < orgIdAry.length; s++) {
                    String orgStrSql = "select org.org_id,org.orgidstring from org_organization org where org.org_id=? and org.orgstatus=0 ";
                    stmt = conn.prepareStatement(orgStrSql);
                    stmt.setString(1, orgIdAry[s]);
                    rs = stmt.executeQuery();
                    while (rs.next()) {
                        String[] orgIdStr = rs.getString(2).split("\\$");
                        for (int st = 1; st < orgIdStr.length; st = st + 2) {
                            if (orgIdsStr.indexOf("," + orgIdStr[st] + ",") >= 0) {// 已存在，不拼装
                            } else {
                                orgIdsStr += "," + orgIdStr[st] + ",";
                            }
                        }
                    }
                    rs.close();
                }
                orgIdsStr = orgIdsStr.replaceAll(",,", ",");
                if (orgIdsStr.length() > 0) {
                    orgIdsStr = orgIdsStr.substring(1, orgIdsStr.length() - 1);
                }
            }
            String[] orgIdsStrAry = orgIdsStr.split(",");
            // 获得该用户的所有兼职组织的上级或上级的上级..组织串-----end

            // 取群组
            // 取得用户所在的部门上级部门，判断此部门是否在该组的组织使用范围内
            // 取得用户所在的组，判断用户是否在该组的组使用范围内
            if ("".equals(orgIdString)) {
                // 组织Id为0则可以查看所以组
                where = " or 1=1";
                
            } else {
                orgIdString = orgIdString
                        .substring(1, orgIdString.length() - 1);
                String[] orgArr = orgIdString.split("\\$\\$");
                for (int i = 0; i < orgArr.length; i++) {
                    where += " or rangeorg like '%*" + orgArr[i] + "*%' ";
                }

                // 如果当前用户有兼职组织，并且该兼职组织是群组的适用范围组织，或者是群组的适用范围组织的子组织，则也要选择到此群组---start--
                for (int k = 0; k < orgIdsStrAry.length; k++) {
                    if (!orgIdsStrAry[k].equals("")) {
                        where += " or rangeorg like '%*" + orgIdsStrAry[k]
                                + "*%' ";
                    }
                }
                // 如果当前用户有兼职组织，并且该兼职组织是群组的适用范围组织，或者是群组的适用范围组织的子组织，则也要选择到此群组---end--
                stmt = conn.prepareStatement("select group_id from org_group where groupuserstring like ? ");
                stmt.setString(1, "%$" + userId + "$%");
                rs = stmt.executeQuery();
                while (rs.next()) {
                    where += " or rangegroup like '%@" + rs.getString(1)
                            + "@%' ";
                }
                
                rs.close();
            }
            
            String p_groupClassId = request.getParameter("groupClassId");
            logger.debug("p_groupClassId:"+p_groupClassId);

            String sql = "";

            String databaseType = com.whir.common.config.SystemCommon
                    .getDatabaseType();
            // 个人自定义群组
            if ("1".equals(groupType)) {
                sql = "select a.group_id,a.group_name,a.group_code, '', '' from org_group a where a.grouptype=1 and a.createdemp="
                        + userId
                        + " and a.domain_id="
                        + domainId
                        + " order by a.groupOrder,a.group_name, a.group_id desc";
            } else {
                if (databaseType.indexOf("mysql") >= 0) {
                    if ("1".equals(fromParent)) {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where ? like concat('%@',"
                                + group_
                                + ",'@%') and a.domain_id=" + domainId;
                    } else {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where ((a.rangeemp is null and a.rangeorg is null and a.rangegroup is null) or (a.rangeemp='' and a.rangeorg='' and a.rangegroup='') or  a.rangeemp like '%$"
                                + userId
                                + "$%' "
                                + where
                                + " or ? like concat('%@',"
                                + group_
                                + ",'@%')) and a.domain_id=" + domainId;
                    }
                } else if (databaseType.indexOf("db2") >= 0) {
                    if ("1".equals(fromParent)) {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where locate(ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',char("
                                + group_
                                + ")),'@%'),?)>0 and a.domain_id=" + domainId;
                    } else {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where ((a.rangeemp is null and a.rangeorg is null and a.rangegroup is null) or (a.rangeemp='' and a.rangeorg='' and a.rangegroup='') or  a.rangeemp like '%$"
                                + userId
                                + "$%' "
                                + where
                                + " or locate(ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',char("
                                + group_
                                + ")),'@%'),?)>0 ) and a.domain_id=" + domainId;
                    }
                } else {
                    if ("1".equals(fromParent)) {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where ? like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',"
                                + group_ + "),'@%') and a.domain_id=" + domainId;
                    } else {
                        sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where ((a.rangeemp is null and a.rangeorg is null and a.rangegroup is null) or (a.rangeemp='' and a.rangeorg='' and a.rangegroup='') or  a.rangeemp like '%$"
                                + userId
                                + "$%' "
                                + where
                                + " or ? like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',"
                                + group_ + "),'@%')) and a.domain_id=" + domainId;
                    }
                }
                sql += " and a.group_class_id=b.id and a.grouptype=0";
                
                if(!CommonUtils.isEmpty(p_groupClassId)){
                    sql += " and b.id="+p_groupClassId;
                }
                
                //根据群组名称检索过滤群组树
                String searchGroupNameTree = request.getParameter("searchGroupNameTree");
                logger.debug("searchGroupNameTree:" + searchGroupNameTree);
                if(!CommonUtils.isEmpty(searchGroupNameTree)){
                    searchGroupNameTree = searchGroupNameTree.replaceAll("'", "");
                    sql += " and a.group_name like '%"+searchGroupNameTree+"%'";
                }
                
                sql += " order by b.sort_no, b.id, a.groupOrder, a.group_name, a.group_id desc";
            }

            logger.debug("获取群组sql:" + sql);

            boolean isLoadGroup = false;
            if ("1".equals(groupType)) {// 个人自定义群组
                if (CommonUtils.isEmpty(request.getParameter("pId"))) {
                    buffer.append("{id:0, ");
                    buffer.append("pId:-1, ");
                    buffer.append("isParent:true, ");
                    buffer.append("name:'自定义群组', ");
                    buffer.append("serial:'', ");
                    buffer.append("show:1 ");
                    buffer.append("}, ");
                } else {
                    isLoadGroup = true;
                }
            } else {// 公共群组
                isLoadGroup = true;
            }

            if (isLoadGroup) {
            	stmt=conn.prepareStatement(sql);
            	if ("0".equals(groupType)) {
            		stmt.setString(1, range);
            	}
            	
                rs = stmt.executeQuery();
                String groupId = "", group_code = "";
                String compareClassId = "";
                while (rs.next()) {
                    groupId = rs.getString(1);
                    group_code = rs.getString(3);
                    
                    if (range.indexOf("@") >= 0) {
                        String tmpGroupId = groupId;
                        if (!"id".equals(key)) {
                            tmpGroupId = group_code;
                        }

                        if (range.indexOf("@" + tmpGroupId + "@") == -1) {
                            continue;
                        }
                    }
                    
                    String groupClassId = rs.getString(4);
                    if ("0".equals(groupType)) {//群组分类
                        if(CommonUtils.isEmpty(p_groupClassId)){
                            if(!CommonUtils.isEmpty(groupClassId) && !compareClassId.equals(groupClassId)){
                                buffer.append("{id:" + groupClassId + ", ");
                                buffer.append("pId:0, ");
                                buffer.append("isParent:true, ");
                                buffer.append("name:'" + rs.getString(5) + "', ");
                                buffer.append("show:1, ");
                                buffer.append("groupClassId:"+groupClassId);
                                
                                buffer.append("}, ");
                                
                                compareClassId = groupClassId;
                            }
                        }else{
                            buffer.append("{id:" + groupId + ", ");
                            buffer.append("pId:" + groupClassId + ", ");
                            buffer.append("isParent:false, ");
                            buffer.append("name:'" + rs.getString(2) + "', ");
                            buffer.append("serial:'" + group_code + "', ");
                            buffer.append("show:1 ");
                            
                            buffer.append("}, ");
                        }
                        
                    }else{

                        buffer.append("{id:" + groupId + ", ");
                        buffer.append("pId:0, ");
                        buffer.append("isParent:false, ");
                        buffer.append("name:'" + rs.getString(2) + "', ");
                        buffer.append("serial:'" + group_code + "', ");
                        buffer.append("show:1 ");
                        
                        buffer.append("}, ");
                    }
                }
                rs.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        buffer.append("]");

        printJsonResult(buffer.toString());

        logger.debug("返回数据：" + buffer);

        logger.debug("异步加载组filterGroupList[END]");

        return null;
    }

    /**
     * 仅组织
     * 
     * @return
     * @throws Exception
     */
    public String getOrgList() throws Exception {
        HttpSession session = request.getSession(true);

        logger.debug("orgId:" + orgId);
        logger.debug("orgName:" + orgName);
        logger.debug("currentOrgId:" + currentOrgId);
        logger.debug("otherDepart:" + otherDepart);
        logger.debug("range:" + range);
        logger.debug("key:" + key);
        logger.debug("isShortcut:" + isShortcut);
        logger.debug("scopeType:" + scopeType);

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        // 取得用户的根组织、组织、单位
        String rootCorpId = session.getAttribute("rootCorpId").toString();
        String departId = session.getAttribute("departId").toString();
        String corpId = session.getAttribute("corpId").toString();
        
        String _orgId = orgId;
        if("3".equals(scopeType)){
            _orgId = "-1";//仅显示本组织
        }

        ManagerBD managerBD = new ManagerBD();

        Map map = null;
        // 人事管理选人
        if ("hrm".equals(fromModule)) {
            map = managerBD.getSubOrgAndAllUsers(_orgId, currentOrgId, domainId,
                    rootCorpId, corpId, departId, otherDepart);
        } else {
            map = managerBD.getSubOrgAndUsers(_orgId, currentOrgId, domainId,
                    rootCorpId, corpId, departId, otherDepart);
        }
        
        List orgList = (List)map.get("org");
        //if (!"1".equals(isShortcut)) {
            List rangeOrgList = getRangeOrgList(key, request, range, orgList);
            
            if(!"*0*".equals(range)){//从指定范围内获取组织
                boolean isOwner = managerBD.isOwnerRangeOrg(orgId, range, "*");
                if(isOwner){
                    isShowCheckbox = "1";
                }
            }

            request.setAttribute("orgList", rangeOrgList);
//        } else {//快捷
//            request.setAttribute("orgList", orgList);
//        }

        return "orgList";
    }

    /**
     * 检索用户
     * 
     * @return
     * @throws Exception
     */
    public String getSearchUserList() throws Exception {

        return "searchUserList";
    }

    /**
     * 仅群组
     * 
     * @return
     * @throws Exception
     */
    public String getGroupList() throws Exception {
        logger.debug("groupType:" + groupType);
        logger.debug("searchGroupName:" + searchGroupName);
        logger.debug("groupId:" + groupId);

        HttpSession session = request.getSession(true);
        DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        List groupList = new ArrayList();
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();

            // 取得所有的当前用户有权限看到的组
            String userId = session.getAttribute("userId").toString();
            String orgIdString = session.getAttribute("orgIdString").toString();
            String domainId = CommonUtils.getSessionDomainId(request) + "";
            String where = "";            

            // 取群组
            // 取得用户所在的部门上级部门，判断此部门是否在该组的组织使用范围内
            // 取得用户所在的组，判断用户是否在该组的组使用范围内
            if ("".equals(orgIdString)) {
                // 组织Id为0则可以查看所以组
                where = " or 1=1";
            } else {
                orgIdString = orgIdString
                        .substring(1, orgIdString.length() - 1);
                String[] orgArr = orgIdString.split("\\$\\$");
                for (int i = 0; i < orgArr.length; i++) {
                    where += " or rangeorg like '%*" + orgArr[i] + "*%' ";
                }
                rs = stmt
                        .executeQuery("select group_id from org_group where groupuserstring like '%$"
                                + userId + "$%'");
                while (rs.next()) {
                    where += " or rangegroup like '%@" + rs.getString(1)
                            + "@%' ";
                }
                rs.close();
            }
            
            String sql = "select a.group_id,a.group_name,a.group_code from org_group a where a.domain_id=" + domainId;
            String databaseType = com.whir.common.config.SystemCommon
                    .getDatabaseType();

            if ("1".equals(groupType)) {
                sql += " and a.groupType=1 and a.createdemp=" + userId;
                
            } else if ("0".equals(groupType)) {
                sql = "select a.group_id,a.group_name,a.group_code, b.id, b.class_name from org_group a, org_group_class b where a.group_class_id=b.id and a.domain_id=" + domainId;
                sql += " and ((a.rangeemp is null and a.rangeorg is null and a.rangegroup is null) or (a.rangeemp='' and a.rangeorg='' and a.rangegroup='') or a.rangeemp like '%$"
                        + userId
                        + "$%' "
                        + where
                        + ") and a.grouptype=0 ";
                
            }else{//用于查询
                sql += " and ((a.groupType=1 and a.createdemp=" + userId + ") ";
                sql += " or (((a.rangeemp is null and a.rangeorg is null and a.rangegroup is null) or (a.rangeemp='' and a.rangeorg='' and a.rangegroup='') or a.rangeemp like '%$"
                    + userId
                    + "$%' "
                    + where
                    + ") and a.grouptype=0)) ";
            }

            if (!CommonUtils.isEmpty(searchGroupName)) {
                searchGroupName = searchGroupName.replaceAll("'", "");// 过滤单引号
                if (databaseType.indexOf("sqlserver") >= 0) {
                    sql += " and a.group_name like N'%" + searchGroupName + "%' ";
                } else {
                    sql += " and a.group_name like '%" + searchGroupName + "%' ";
                }
            }
            
            if("1".equals(request.getParameter("notshowgrouplist"))){
                sql += " and 1>2 ";
            }

            if ("0".equals(groupType)) {
                sql += " order by b.sort_no, b.id, a.groupOrder, a.group_name, a.group_id desc";
            }else{
                sql += " order by a.grouporder, a.group_name, a.group_id desc";
            }

            logger.debug("sql:" + sql);

            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String groupId = rs.getString(1);
                if (range != null){// && range.indexOf("@") >= 0) {
                    if (range.indexOf("@" + groupId + "@") < 0) {
                        continue;
                    }
                }

                Object[] groupObj = new Object[3];
                groupObj[0] = groupId;
                groupObj[1] = rs.getString(2);// 组名称
                groupObj[2] = rs.getString(3);// 组编码
                groupList.add(groupObj);
            }
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        } finally {
            try {
                stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("groupList", groupList);

        return "groupList";
    }

    /**
     * 组织用户
     * 
     * @return
     * @throws Exception
     */
    public String getOrgUserList() throws Exception {
        long sTime = (new Date()).getTime();
        HttpSession session = request.getSession(true);

        logger.debug("getOrgUserList:->");
        logger.debug("orgId:" + orgId);
        logger.debug("orgName:" + orgName);
        logger.debug("currentOrgId:" + currentOrgId);
        logger.debug("otherDepart:" + otherDepart);
        logger.debug("range:" + range);
        logger.debug("fromParent:" + fromParent);
        logger.debug("fromModule:" + fromModule);
        logger.debug("show:" + show);
        logger.debug("single:" + single);
        logger.debug("key:" + key);
        logger.debug("parentOrgId:" + parentOrgId);

        String domainId = CommonUtils.getSessionDomainId(request) + "";
        // 取得用户的根组织、组织、单位
        String rootCorpId = session.getAttribute("rootCorpId").toString();
        String departId = session.getAttribute("departId").toString();
        String corpId = session.getAttribute("corpId").toString();

        ManagerBD managerBD = new ManagerBD();

        Map map = null;
        // 人事管理选人
        if ("hrm".equals(fromModule)) {
            map = managerBD.getSubOrgAndAllUsers(orgId, currentOrgId, domainId,
                    rootCorpId, corpId, departId, otherDepart);
        } else {
            map = managerBD.getSubOrgAndUsers(orgId, currentOrgId, domainId,
                    rootCorpId, corpId, departId, otherDepart);
        }

        if (CommonUtils.isEmpty(range) || "0".equals(range)){
            range = "*0*";
        }
        
        if(!"*0*".equals(range)){//从指定范围内获取组织            
            boolean isOwner = managerBD.isOwnerRangeOrg(parentOrgId, range, "*");
            
            logger.debug("isOwner:"+isOwner);
            
            if(isOwner){
                isShowCheckbox = "1";
            }else{
                isShowAllBtn = "0";
            }
        }
        
        logger.debug("isShowCheckbox:"+isShowCheckbox);
        logger.debug("isShowAllBtn:"+isShowAllBtn);

        // 来自父选择范围
        if ("1".equals(fromParent) && !"*0*".equals(range)) {
            List userList = map.get("user") != null ? (List) map.get("user")
                    : null;
            userList = getRangeUserList(key, request, range, userList);

            request.setAttribute("userList", userList);
        } else {
            request.setAttribute("userList", map.get("user"));
        }

        // 如果没有指定显示组织,则只显示用户
        if (select.indexOf("org") < 0 || "yes".equals(single)) {
            return "userList";
        }
        
        List orgList = (List)map.get("org");        
        if(!"*0*".equals(range)){//从指定范围内获取组织
            orgList = getRangeOrgList(key, request, range, orgList);
        }

        request.setAttribute("orgList", orgList);
        
        if("1".equals(ogp)){
            //根据组织ID获取人员所在的岗位--start--2014-09-24
            ManagerService ms = new ManagerService();
            String[][] empPositions = ms.getEmpPositions(orgId, domainId);        
            request.setAttribute("empPositions", empPositions);        
            //根据组织ID获取人员所在的岗位--end--2014-09-24
        }
        
        long eTime = (new Date()).getTime();        
        logger.debug("run time: "+(eTime-sTime)+" 's.");

        return "orgUserList";
    }

    /**
     * 群组用户
     * 
     * @return
     * @throws Exception
     */
    public String getGroupUserList() throws Exception {
        long sTime = (new Date()).getTime();
        logger.debug("groupId:" + groupId);
        logger.debug("groupName:" + groupName);
        logger.debug("groupCode:" + groupCode);

        // 取得某个组下的所有用户
        GroupBD groupBD = new GroupBD();
        List userList = null;

        // 人事管理选人(根据组)
        if ("hrm".equals(fromModule)) {
            userList = groupBD.selectGroupAllUser(groupId);
        } else {
            userList = groupBD.selectGroupUser(groupId);
        }
        request.setAttribute("userList", userList);
        
        long eTime = (new Date()).getTime();        
        logger.debug("run time: "+(eTime-sTime)+" 's.");

        return "groupUserList";
    }

    public String searchUser() throws Exception {
        return "searchUser";
    }

    /**
     * 检索用户列表
     * 
     * @return
     * @throws Exception
     */
    public String publicUserSearchAll() throws Exception {
        long sTime = (new Date()).getTime();
        HttpSession session = request.getSession(true);

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

        // 公共选人页面的搜索页面
        String domainId = CommonUtils.getSessionDomainId(request) + "";
        String userId = session.getAttribute("userId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        String viewSQL = "user.empId,user.empName,user.userAccounts,user.empSex,organization.orgNameString, organization.orgName";
        String fromSQL = "com.whir.org.vo.usermanager.EmployeeVO AS user JOIN user.organizations organization";
        String whereSQL = "";
        whereSQL += "where user.userIsActive=1 and user.userIsDeleted=0 and user.userAccounts is not null and organization.orgStatus=0 and user.domainId="
                + domainId + " ";

        logger.debug("参数searchUserCName:" + searchUserCName);
        logger.debug("参数searchDutyOpr:" + searchDutyOpr);
        logger.debug("参数searchDutyLevel:" + searchDutyLevel);
        logger.debug("参数searchUserAccounts:" + searchUserAccounts);
        logger.debug("参数searchUserEName:" + searchUserEName);
        logger.debug("参数searchUserSimpleName:" + searchUserSimpleName);
        logger.debug("参数searchOrgName:" + searchOrgName);
        logger.debug("参数searchOrgSerial:" + searchOrgSerial);
        logger.debug("参数searchGroupName:" + searchGroupName);
        logger.debug("参数searchGroupCode:" + searchGroupCode);
        logger.debug("参数searchOrgId:" + searchOrgId);
        logger.debug("参数range:" + range);
        logger.debug("参数scopeType:" + scopeType);

        Map varMap = new HashMap();
        if (searchOrgId != null && "0".equals(searchOrgId)) { // 全部组织

            if ("code".equals(key)) {
                range = new ConvertCodeToID().convertRangToIds(range, true);
            }

            int i = 0;
            if (!CommonUtils.isEmpty(range) && !"*0*".equals(range) && !"0".equals(range)) {
                //用户
                whereSQL += " and ('"
                        + range
                        + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',user.empId),'$%') ";
                
                //组织
                ConvertIdAndName cIdAndName = new ConvertIdAndName();
                EndowVO endowVO = cIdAndName.splitId(range);
                String orgIds = endowVO.getOrgIdArray();

                if (!"".equals(orgIds)) {
                    String[] rangeArr = orgIds.split(",");
                    
                    DbOpt dbopt = new DbOpt();
                    try{
                        for (i = 0; i < rangeArr.length; i++) {
                            if("3".equals(this.scopeType)){//本组织
                                whereSQL += " or organization.orgId = :orgId"
                                        + i + " or user.sidelineOrg like :sidelineOrg"
                                        + i + " ";
                                varMap.put("orgId" + i, rangeArr[i]);
                                varMap
                                        .put("sidelineOrg" + i, "%*" + rangeArr[i]
                                                + "*%");
                            }else {
                                whereSQL += " or organization.orgIdString like :orgId"
                                    + i + " or user.sidelineOrg like :sidelineOrg"
                                    + i + " ";
                                varMap.put("orgId" + i, "%$" + rangeArr[i] + "$%");
                                varMap
                                        .put("sidelineOrg" + i, "%*" + rangeArr[i]
                                                + "*%");
                            
                                //下级组织兼职组织
                                String subsql = "select org_id from org_organization where orgidstring like '%$"+rangeArr[i]+"$%' and domain_id=" + domainId;
                                String[] subOrg = dbopt.executeQueryToStrArr1(subsql);
                                if(subOrg != null){
                                    for(int i0=0; i0<subOrg.length; i0++){
                                        whereSQL += " or user.sidelineOrg like '%*"+subOrg[i0]+"*%'";
                                    }
                                }
                            }
                            
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally {
                        try {
                            dbopt.close();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                
                if (!CommonUtils.isEmpty(searchGroupName)) {
                    // 群组
                    whereSQL += " or '"
                            + range
                            + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',groupVO.groupId),'@%') ";
                }
                
                whereSQL += ")";
            }

            // 获得所有职务水平所组成的串----start
            List dutylist = new ManagerBD().getAllDuty(domainId);
            String allDutyLevel = "";
            if (dutylist != null) {
                Object[] obj;
                for (int s = 0; s < dutylist.size(); s++) {
                    obj = (Object[]) dutylist.get(s);
                    allDutyLevel = allDutyLevel + obj[2] + ",";
                }
            }
            if (allDutyLevel != null && !allDutyLevel.equals("")) {
                allDutyLevel = allDutyLevel.substring(0,
                        allDutyLevel.length() - 1); // 除掉最后一个,
            }
            // 获得所有职务水平所组成的串----end

            // 查询组织或兼职组织为查询条件组织的用户情况
            // 组织
            if (!CommonUtils.isEmpty(searchOrgName)) {
                whereSQL += " and (organization.orgNameString like :orgName "; // 所属组织
                whereSQL += " or user.sidelineOrgName like :sidelineOrgName)"; // 兼职组织
                varMap.put("orgName", "%" + searchOrgName + "%");
                varMap.put("sidelineOrgName", "%" + searchOrgName + "%");
            }

            // 职务级别
            if (!CommonUtils.isEmpty(searchDutyOpr)) {
                if (!CommonUtils.isEmpty(searchDutyLevel)) {
                    String temp_searchDutyLevel = searchDutyLevel;
                    if(searchDutyLevel.indexOf("_")!=-1){
                        temp_searchDutyLevel = searchDutyLevel.split("_")[1];
                    }
                    logger.debug("参数temp_searchDutyLevel:" + temp_searchDutyLevel);
                    
                    whereSQL += " and user.empDutyLevel " + searchDutyOpr
                            + " :searchDutyLevel "
                            + " and user.empDutyLevel in(" + allDutyLevel + ")";
                    varMap.put("searchDutyLevel", temp_searchDutyLevel);
                }
            }
            // 中文名
            if (!CommonUtils.isEmpty(searchUserCName)) {
                whereSQL += " and user.empName LIKE :empName ";
                varMap.put("empName", "%" + searchUserCName + "%");
            }
            // 账号
            if (!CommonUtils.isEmpty(searchUserAccounts)) {
                whereSQL += " and user.userAccounts LIKE :userAccounts ";
                varMap.put("userAccounts", "%" + searchUserAccounts + "%");
            }
            // 英文名
            if (!CommonUtils.isEmpty(searchUserEName)) {
                whereSQL += " and user.empEnglishName LIKE :empEnglishName ";
                varMap.put("empEnglishName", "%" + searchUserEName + "%");
            }
            // 简码
            if (!CommonUtils.isEmpty(searchUserSimpleName)) {
                whereSQL += " and user.userSimpleName LIKE :userSimpleName ";
                varMap.put("userSimpleName", "%" + searchUserSimpleName + "%");
            }

            // 群组
            if (!CommonUtils.isEmpty(searchGroupName)) {
                DbOpt dbopt = new DbOpt();
                String where = " and (1>2 ";
                if ("".equals(orgIdString)) {
                    // 组织Id为0则可以查看所有组
                    where = " or 1=1";
                } else {
                    orgIdString = orgIdString.substring(1,
                            orgIdString.length() - 1);
                    String[] orgArr = orgIdString.split("\\$\\$");
                    for (int i0 = 0; i0 < orgArr.length; i0++) {
                        where += " or groupVO.rangeOrg like :rangeOrg" + i0
                                + " ";
                        varMap.put("rangeOrg" + i0, "%*" + orgArr[i0] + "*%");
                    }

                    try {
                        String[] groupArr = dbopt
                                .executeQueryToStrArr1("select group_id from org_group where groupuserstring like '%$"
                                        + userId + "$%'");
                        if (groupArr != null && groupArr.length > 0) {
                            for (int i0 = 0; i0 < groupArr.length; i0++) {
                                where += " or groupVO.rangeGroup like :rangeGroup"
                                        + i0 + " ";
                                varMap.put("rangeGroup" + i0, "%@"
                                        + groupArr[i0] + "@%");
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
                }

                where += " or (groupVO.rangeEmp is null and groupVO.rangeOrg is null and groupVO.rangeGroup is null)";
                where += " or (groupVO.rangeEmp='' and groupVO.rangeOrg='' and groupVO.rangeGroup='')";
                where += " or groupVO.rangeEmp like :rangeEmp ";

                varMap.put("rangeEmp", "%$" + userId + "$%");

                where += ") and groupVO.groupType=0 ";

                whereSQL += where;

                whereSQL += " and groupVO.groupName LIKE :groupName ";
                varMap.put("groupName", "%" + searchGroupName + "%");
            }

            // 群组
            if (!CommonUtils.isEmpty(searchGroupName)) {
                fromSQL += " join user.groups groupVO ";
            }

        } else { // 具体组织
            if("3".equals(this.scopeType)){//本组织
                whereSQL += "  and (organization.orgId = :orgId or user.sidelineOrg like :sidelineOrg ";
                varMap.put("orgId", searchOrgId);
                varMap.put("sidelineOrg", "%*" + searchOrgId + "*%"); 
            }else {
                whereSQL += "  and (organization.orgIdString like :orgId or user.sidelineOrg like :sidelineOrg ";
                varMap.put("orgId", "%$" + searchOrgId + "$%");
                varMap.put("sidelineOrg", "%*" + searchOrgId + "*%");            
            
                DbOpt dbopt = new DbOpt();
                try{
                    //下级组织兼职组织
                    String subsql = "select org_id from org_organization where  orgidstring like '%$"+searchOrgId+"$%' and domain_id=" + domainId;
                    String[] subOrg = dbopt.executeQueryToStrArr1(subsql);
                    if(subOrg != null){
                        for(int i0=0; i0<subOrg.length; i0++){
                            whereSQL += " or user.sidelineOrg like '%*"+subOrg[i0]+"*%'";
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        dbopt.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            whereSQL += ") ";
        }

        // 排序
        String orderBy = " order by ";
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
        String[] arr = { "empId", "empName", "userAccounts", "empSex",
                "orgNameString", "orgName"};
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, list);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";

        printResult(G_SUCCESS, json);

        logger.debug("json数据：" + json);
        
        long eTime = (new Date()).getTime();        
        logger.debug("run time: "+(eTime-sTime)+" 's.");

        return null;
    }

    /**
     * 检索组织
     * @return
     * @throws Exception
     */
    public String searchOrg() throws Exception {
        return "searchOrg";
    }
    
    public String searchOrgList() throws Exception {

        logger.debug("查询列表开始");

        HttpSession session = request.getSession(true);
        Long domainId = CommonUtils.getSessionDomainId(request);
        String userId = session.getAttribute("userId").toString();
        String orgId = session.getAttribute("orgId").toString();

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

        String viewSQL = "org_id, orgname, orgserial, orgnamestring, orgidstring ";
        String fromSQL = " org_organization ";
        String whereSQL = "where domain_id=" + domainId + " and orgstatus=0 ";
        
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();

        /*
         * varMap，查询参数Map
         */
        Map varMap = new HashMap();
        if (!CommonUtils.isEmpty(searchOrgName)) {
            whereSQL += " and orgnamestring like :searchOrgName ";

            varMap.put("searchOrgName", "%" + searchOrgName + "%");
        }
        
        if (!CommonUtils.isEmpty(searchOrgSerial)) {
            whereSQL += " and orgserial like :searchOrgSerial ";

            varMap.put("searchOrgSerial", "%" + searchOrgSerial + "%");
        }
        
        String org_ = "org_id";
        if ("code".equals(key)) {
            org_ = "orgserial";
        }

        if (range != null && !"".equals(range) && !"0".equals(range)
                && !"*0*".equals(range)) {

            if (databaseType.indexOf("mysql") >= 0) {
                whereSQL += " and '" + range + "' like concat('%*'," + org_
                        + ",'*%')";
            } else {
                whereSQL += " and '"
                        + range
                        + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*',"
                        + org_ + "),'*%')";
            }
        }

        // 排序
        String orderBy = " order by ";
        orderBy += " orgidstring asc";
        
        logger.debug("sql:" + viewSQL + fromSQL + whereSQL + orderBy);

        /*
         * PageFactory.getHibernatePage，分页查询，如果不排序，传入空字符串
         */
        Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL,
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
        /*
         * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
         */
        String[] arr = { "orgId", "orgName", "orgSerial", "orgNameString" };
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, list, "id", MODULE_CODE);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";

        // System.out.println("----[json]:"+json);
        printResult(G_SUCCESS, json);

        logger.debug("查询列表结束");

        return null;
    }
    
    /**
     * 检索组织
     * 
     * @return
     * @throws Exception
     */
    public String searchOrgList_nopager() throws Exception {
        String domainId = CommonUtils.getSessionDomainId(request) + "";

        logger.debug("searchOrgName:" + searchOrgName);
        logger.debug("searchOrgSerial:" + searchOrgSerial);

        String[][] result = new String[0][3];
        DbOpt dbopt = new DbOpt();
        try {
            String databaseType = com.whir.common.config.SystemCommon
                    .getDatabaseType();

            String sql = "select org_id, orgname, orgserial from org_organization where 1=1 ";

            // 组织名称
            if (!CommonUtils.isEmpty(searchOrgName)) {
                searchOrgName = searchOrgName.replaceAll("'", "");// 过滤单引号
                if (databaseType.indexOf("sqlserver") >= 0) {
                    sql += " and orgname like N'%" + searchOrgName + "%'";

                } else {
                    sql += " and orgname like '%" + searchOrgName + "%'";
                }
            }

            // 组织编码
            if (!CommonUtils.isEmpty(searchOrgSerial)) {
                searchOrgSerial = searchOrgSerial.replaceAll("'", "");// 过滤单引号
                if (databaseType.indexOf("sqlserver") >= 0) {
                    sql += " and orgserial like N'%" + searchOrgSerial + "%'";

                } else {
                    sql += " and orgserial like '%" + searchOrgSerial + "%'";
                }
            }

            String org_ = "org_id";
            if ("code".equals(key)) {
                org_ = "orgserial";
            }

            if (range != null && !"".equals(range) && !"0".equals(range)
                    && !"*0*".equals(range)) {

                if (databaseType.indexOf("mysql") >= 0) {
                    sql += " and '" + range + "' like concat('%*'," + org_
                            + ",'*%')";
                } else {
                    sql += " and '"
                            + range
                            + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*',"
                            + org_ + "),'*%')";
                }
            }

            sql += " and domain_id=" + domainId;
            sql += " and orgstatus=0";
            sql += " order by orgidstring";

            logger.debug("sql:" + sql);

            result = dbopt.executeQueryToStrArr2(sql, 3);

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (dbopt != null) {
                dbopt.close();
            }
        }
        
        if(result == null) result = new String[0][3];

        request.setAttribute("orgList", result);

        return "searchOrg";
    }

    /**
     * 过滤有选择范围时的用户
     * 
     * @param request
     *            HttpServletRequest
     * @param range
     *            String
     * @param userList
     *            List
     * @return List
     */
    private List getRangeUserList(String key, HttpServletRequest request,
            String range, List userList) {
        logger.debug("getRangeUserList--userList.size():"+userList);
        
        HttpSession session = request.getSession(true);
        String orgId = session.getAttribute("orgId").toString();
        String orgSerial = session.getAttribute("orgSerial") != null ? session.getAttribute("orgSerial").toString() : "";

        List list = new ArrayList();
        if (range.indexOf("*" + orgId + "*") >= 0
                || range.indexOf("*" + orgSerial + "*") >= 0) {
            return userList;

        } else if (userList != null && userList.size() > 0) {
            for (int s = 0, j=userList.size(); s < j; s++) {
                Object[] userObj = (Object[]) userList.get(s);
                String idOrcode = userObj[0].toString();
                if ("code".equals(key) && userObj[2] == null) {
                    continue;
                }

                if ("code".equals(key)) {
                    idOrcode = userObj[2].toString();
                }

                if (range.indexOf("$" + idOrcode + "$") >= 0) {
                    list.add(userObj);
                }
            }
            return list;

        } else {
            return userList;
        }
    }

    private List getRangeOrgList(String key, HttpServletRequest request,
            String range, List orgList) {
        HttpSession session = request.getSession(true);

        DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        // key=id表示需要获得user、org或group的主键，key=code表示需要获得user的账号、org的编码或group的编码。
        String org_ = "org_id";
        if ("code".equals(key)) {
            org_ = "orgSerial";
        }

        // 取组织
        if (!CommonUtils.isEmpty(range) && !"0".equals(range)
                && !"*0*".equals(range)) {
            StringBuffer rangeIdStringBuffer = new StringBuffer();
            String tmpSql = "";
            String databaseType = com.whir.common.config.SystemCommon
                    .getDatabaseType();
            try {
                conn = ds.getConnection();
                stmt = conn.createStatement();

                DatabaseMetaData dbmd = conn.getMetaData();// 用于判断数据库
                String databaseName = dbmd.getDatabaseProductName();

                if (databaseType.indexOf("mysql") >= 0) {
                    tmpSql = "select orgidstring from org_organization where '"
                            + range + "' like concat('%*'," + org_
                            + ",'*%') and domain_id=" + domainId;
                } else if (databaseType.indexOf("db2") >= 0) {
                    tmpSql = "select orgidstring from org_organization where locate(EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%*',char("
                            + org_
                            + ")),'*%'),'"
                            + range
                            + "')>0 and domain_id=" + domainId;
                } else {
                    if (databaseName.equals("DM DBMS")) {// 如果是达梦数据库
                        // 系统中连接字符串函数有时不支持，有时支持,故写成如下方式
                        tmpSql = "select orgidstring from org_organization where '"
                                + range
                                + "' like '%*'||"
                                + org_
                                + "||'*%' and domain_id=" + domainId;
                    } else {
                        tmpSql = "select orgidstring from org_organization where '" 
                                + range
                                + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%*',"
                                + org_ + "),'*%') and domain_id=" + domainId;
                    }
                }

                logger.debug("tmpSql:" + tmpSql);

                rs = stmt.executeQuery(tmpSql);

                while (rs.next()) {
                    rangeIdStringBuffer.append(rs.getString(1));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e);
            } finally {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String rangeIdString = rangeIdStringBuffer.toString();
            
            logger.debug("rangeIdString:" + rangeIdString);
            logger.debug("key:" + key);

            logger.debug("处理前range:" + range);
            if ("code".equals(key)) {
                SelectForWorkFlow selectForWorkFlow = new SelectForWorkFlow();
                Map map = selectForWorkFlow.groupString(range);
                range = map.get("orgCodes").toString();
                List orgList_ = selectForWorkFlow.getOrgIdListByOrgCodes(range,
                        domainId);
                range = "";
                if (orgList_ != null) {
                    for (int i = 0, j=orgList_.size(); i < j; i++) {
                        Object[] arr = (Object[]) orgList_.get(i);
                        range += "*" + arr[0] + "*";
                    }
                }
                logger.debug("处理中range:" + range);
            }
            logger.debug("处理后range:" + range);

            Pattern p = null; // 正则表达式
            Matcher m = null; // 操作的字符串

            p = Pattern.compile("\\$[0-9]*\\$");
            m = p.matcher(range);
            range = m.replaceAll("");

            p = Pattern.compile("@[0-9]*@");
            m = p.matcher(range);
            range = m.replaceAll("");

            range = range.replaceAll("\\*\\*", "\\$,\\$");
            range = range.replaceAll("\\*", "\\$");

            logger.debug("处理后range2:" + range);

            String[] rangeArr = range.split(",");

            // org.orgId,org.orgName,org.orgSerial
            List list = new ArrayList();
            if (orgList != null && orgList.size() > 0) {
                for (int s = 0, j=orgList.size(); s < j; s++) {
                    Object[] obj = (Object[]) orgList.get(s);
                    String idOrcode = obj[0].toString();
                    String orgIdString = obj[3].toString();
                    if ("code".equals(key) && obj[2] == null) {
                        continue;
                    }

                    if ("code".equals(key)) {
                        idOrcode = obj[2].toString();
                    }

                    int show = 0;
                    for (int i = 0; i < rangeArr.length; i++) {
                        if (orgIdString.indexOf(rangeArr[i]) > 0) {
                            show = 1;
                            break;
                        }
                    }

                    /*if (rangeIdString.indexOf("$" + orgId + "$") >= 0) {
                        show = 1;
                    }*/
                    
                    if(show == 0){
                        continue;
                    }
                    
                    list.add(obj);
                }
                return list;
            }
        }

        return orgList;
    }
    
    public String otherUser() throws Exception {

        String domainId = CommonUtils.getSessionDomainId(request) + "";

        logger.debug("range:" + range);

        List userList = new ArrayList();
        
        java.sql.Connection conn=null;
        java.sql.Statement stmt=null;
        java.sql.ResultSet rs=null;
        try {
            javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase().getDataSource();
            conn = ds.getConnection();
            stmt = conn.createStatement();
            
            String databaseType = com.whir.common.config.SystemCommon
                    .getDatabaseType();

            String sql = "";
            if (databaseType.indexOf("mysql") >= 0) {
                if ("code".equals(key)) {
                    sql = "select emp_id, empname, useraccounts, CURSTATUS, IMID, EMPSEX from org_employee where '"
                        + range
                        + "' like concat('%$',useraccounts,'$%') and useraccounts is not null and useraccounts <> '' and USERISDELETED=0 and domain_id="
                        + domainId;
                } else {                    
                    sql = "select emp_id, empname, useraccounts, CURSTATUS, IMID, EMPSEX from org_employee where '"
                        + range
                        + "' like concat('%$',emp_id,'$%') and useraccounts is not null and useraccounts <> '' and USERISDELETED=0 and domain_id="
                        + domainId;
                }
            } else {
                if ("code".equals(key)) {
                    sql = "select emp_id, empname, useraccounts, CURSTATUS, IMID, EMPSEX from org_employee where '"
                        + range
                        + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',useraccounts),'$%') and useraccounts is not null and USERISDELETED=0 and domain_id="
                        + domainId;

                } else {
                    sql = "select emp_id, empname, useraccounts, CURSTATUS, IMID, EMPSEX from org_employee where '"
                        + range
                        + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',emp_id),'$%') and useraccounts is not null and USERISDELETED=0 and domain_id="
                        + domainId;                    
                }
                
                if (databaseType.indexOf("oracle") >= 0) {
                    sql += " and useraccounts <> ' ' ";
                }else{
                    sql += " and useraccounts <> '' ";
                }
            }

            logger.debug("sql:" + sql);

            rs = stmt.executeQuery(sql);
            while(rs.next()){
                //emp.empId,emp.empName,emp.userAccounts,emp.curStatus,emp.imId, emp.empSex
                String[] arr = new String[6];
                arr[0] = rs.getString(1);
                arr[1] = rs.getString(2);
                arr[2] = rs.getString(3);
                arr[3] = rs.getString(4);
                arr[4] = rs.getString(5);
                arr[5] = rs.getString(6);
                userList.add(arr);                
            }
            
            rs.close();
            stmt.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        request.setAttribute("userList", userList);

        return "otherUser";
    }

    /**
     * 检索指定关键字的数据
     * 
     * @return
     * @throws Exception
     */
    public String searchUserByTerm() throws Exception {
        HttpSession session = request.getSession(true);
        
        String s = request.getParameter("term");
        String single = request.getParameter("single");
        
        logger.debug("0-init:range:"+range);
        
        if(CommonUtils.isEmpty(range) || "0".equals(range) || "*0*".equals(range)){
            //不是来自父对象及自定义表单
            if(!"1".equals(fromParent) && !"customform".equals(fromModule)){
                if(CommonUtils.isEmpty(limited)){
                    range = session.getAttribute("browseRange").toString();
                    if ("code".equals(key)) {//新工作流程根据code选择用户
                        if(!CommonUtils.isEmpty(range)){
                            ConversionString conversionString = new ConversionString(range);
                            String scopeUserIds = conversionString.getUserIdString();
                            String scopeOrgIds = conversionString.getOrgIdString();
                            String scopeGroupIds = conversionString.getGroupIdString();
                            if(!CommonUtils.isEmpty(scopeOrgIds)){
                                if(scopeOrgIds.startsWith(",")){
                                    scopeOrgIds = scopeOrgIds.substring(1);
                                }
                                if(scopeOrgIds.endsWith(",")){
                                    scopeOrgIds = scopeOrgIds.substring(0, scopeOrgIds.length()-1);
                                }
                                if(!CommonUtils.isEmpty(scopeOrgIds)){
                                    DbOpt dbopt = new DbOpt();
                                    try{
                                        String[][] orgSerials = dbopt.executeQueryToStrArr2("select t.orgserial from org_organization t where t.org_id in ("+scopeOrgIds+")", 1);
                                        if(orgSerials != null){
                                            range = "";
                                            for(int i=0; i<orgSerials.length; i++){
                                                range += "*" + orgSerials[i][0] + "*";
                                            }
                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }finally{
                                        try{
                                            if(dbopt!=null)dbopt.close();
                                        }catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        logger.debug("1-init:range:"+range);
        
        if("0".equals(range)){
            range = "*0*";
        }
        
        /*
         * pageSize每页显示记录数，即list.jsp中分页部分select中的值
         */
        int pageSize = 999;
        /*
         * currentPage，当前页数
         */
        int currentPage = 1;

        // 公共选人页面的搜索页面
        String domainId = CommonUtils.getSessionDomainId(request) + "";
        String userId = session.getAttribute("userId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        String viewSQL = "user.empId,user.empName,user.userAccounts,user.empSex,organization.orgNameString";
        String fromSQL = "com.whir.org.vo.usermanager.EmployeeVO AS user JOIN user.organizations organization";
        String whereSQL = "";
        whereSQL += "where user.userIsActive=1 and user.userIsDeleted=0 and user.userAccounts is not null and organization.orgStatus=0 and user.domainId="
                + domainId + " ";

        logger.debug("参数searchUserCName:" + searchUserCName);
        logger.debug("参数searchDutyOpr:" + searchDutyOpr);
        logger.debug("参数searchDutyLevel:" + searchDutyLevel);
        logger.debug("参数searchUserAccounts:" + searchUserAccounts);
        logger.debug("参数searchUserEName:" + searchUserEName);
        logger.debug("参数searchUserSimpleName:" + searchUserSimpleName);
        logger.debug("参数searchOrgName:" + searchOrgName);
        logger.debug("参数searchOrgSerial:" + searchOrgSerial);
        logger.debug("参数searchGroupName:" + searchGroupName);
        logger.debug("参数searchGroupCode:" + searchGroupCode);
        logger.debug("参数searchOrgId:" + searchOrgId);
        logger.debug("参数range:" + range);

        Map varMap = new HashMap();
        // 全部组织

        if ("code".equals(key)) {
            range = new ConvertCodeToID().convertRangToIds(range, true);
        }

        if (range != null && !"".equals(range) && !"*0*".equals(range)
                && !"0".equals(range)) {
            //用户
            whereSQL += " and ('"
                    + range
                    + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',user.empId),'$%') ";
            
            //组织
            ConvertIdAndName cIdAndName = new ConvertIdAndName();
            EndowVO endowVO = cIdAndName.splitId(range);
            String orgIds = endowVO.getOrgIdArray();

            if (!"".equals(orgIds)) {
                String[] rangeArr = orgIds.split(",");
                
                DbOpt dbopt = new DbOpt();
                try{
                    for (int i = 0; i < rangeArr.length; i++) {
                        whereSQL += " or organization.orgIdString like :orgId"
                                + i + " or user.sidelineOrg like :sidelineOrg"
                                + i + " ";
                        varMap.put("orgId" + i, "%$" + rangeArr[i] + "$%");
                        varMap
                                .put("sidelineOrg" + i, "%*" + rangeArr[i]
                                        + "*%");
                        
                        //下级组织兼职组织
                        /*String subsql = "select org_id from org_organization where orgidstring like '%$"+rangeArr[i]+"$%' and domain_id=" + domainId;
                        String[] subOrg = dbopt.executeQueryToStrArr1(subsql);
                        if(subOrg != null){
                            for(int i0=0; i0<subOrg.length; i0++){
                                whereSQL += " or user.sidelineOrg like '%*"+subOrg[i0]+"*%'";
                            }
                        }*/
                        
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    try {
                        dbopt.close();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            
            if (!CommonUtils.isEmpty(searchGroupName)) {
                // 群组
                whereSQL += " or '"
                        + range
                        + "' like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%@',groupVO.groupId),'@%') ";
            }
            
            whereSQL += ")";
        }

        // 中文名
        if (!CommonUtils.isEmpty(s)) {
            whereSQL += " and (user.empName LIKE :empName ";
            varMap.put("empName", "%" + s + "%");
            
            //账号
            whereSQL += " or user.userAccounts LIKE :userAccounts ";
            varMap.put("userAccounts", "%" + s + "%");
            
            //英文名
            whereSQL += " or user.empEnglishName LIKE :empEnglishName ";
            varMap.put("empEnglishName", "%" + s + "%");
            
            //简码
            whereSQL += " or user.userSimpleName LIKE :userSimpleName ";
            varMap.put("userSimpleName", "%" + s + "%");
            
            //工号
            whereSQL += " or user.empNumber LIKE :empNumber ";
            varMap.put("empNumber", "%" + s + "%");
            
            whereSQL += ") ";
            
        }else{
            whereSQL += " and 1<>1 ";
        }

        // 排序
        String orderBy = " order by ";
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
        
        String separate_u = "$";
        boolean singleFlag = false;
        if("1".equals(single) || "true".equals(single) || "yes".equals(single)){
            singleFlag = true;
            separate_u = "";
        }
        
        StringBuffer json = new StringBuffer("[");
        if(list != null){
            for(int i=0, len=list.size(); i<len; i++){
                Object[] obj = (Object[])list.get(i);
                //user.empId,user.empName,user.userAccounts,user.empSex,organization.orgNameString
                String empId = ""+obj[0];
                String empName = ""+obj[1];
                //String userAccounts = ""+obj[2];
                String orgNameString = ""+obj[4];
                
                if(i > 0) json.append(",");
                
                json.append("{");
                //json.append("\"id\":\""+empId+"\",");
                json.append("\"label\":\"" + empName +"\",");
                json.append("\"value\":\"" + separate_u + empId + separate_u + "\",");
                json.append("\"title\":\"" + orgNameString + "\"");
                json.append("}");
            }
        }
        
        json.append("]");

        printJsonResult(json.toString());
        
        return null;
    }
    
    /**
     * 过滤无效数据
     * 
     * @return
     */
    public String getScopeFilter() {
        String scopeIds = request.getParameter("filter_scopeIds");
        String single = request.getParameter("single");
        String whirTagit = request.getParameter("whirTagit");
        
        String show = request.getParameter("show");
        String select = request.getParameter("select");
        
        //String separate_u = "$";
        boolean singleFlag = false;
        if("1".equals(single) || "true".equals(single) || "yes".equals(single)){
            singleFlag = true;
        }
        
        String key = request.getParameter("key");
        
        Map result = CommonUtils.getScopeFilter(scopeIds, singleFlag, whirTagit, key);

        String scopeIdStr = (String) result.get("scopeIdStr");
        String scopeNameStr = (String) result.get("scopeNameStr");
        String scopeTitleStr = (String) result.get("scopeTitleStr");
        
        StringBuffer json = new StringBuffer("{");
                
        json.append("\"scopeIdStr\":\""+scopeIdStr+"\",");
        json.append("\"scopeNameStr\":\""+scopeNameStr+"\",");
        json.append("\"scopeTitleStr\":\""+scopeTitleStr+"\"");
        
        json.append("}");
        
        printJsonResult(json.toString());

        return null;
    }
    
    /**
     * 已选择的用户页面
     * @return
     */
    public String selectedUser() {
        return "selectedUser";
    }
    
    public String getSelectedUserList() {
        long sTime = (new Date()).getTime();
        HttpSession session = request.getSession(true);
        Map varMap = new HashMap();
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

        // 公共选人页面的搜索页面
        String domainId = CommonUtils.getSessionDomainId(request) + "";
        String userId = session.getAttribute("userId").toString();
        String orgIdString = session.getAttribute("orgIdString").toString();

        String viewSQL = "user.empId,user.empName,user.userAccounts,user.empSex,organization.orgNameString, organization.orgName";
        String fromSQL = "com.whir.org.vo.usermanager.EmployeeVO AS user JOIN user.organizations organization";
        String whereSQL = "";
        whereSQL += "where user.userIsActive=1 and user.userIsDeleted=0 and user.userAccounts is not null and organization.orgStatus=0 and user.domainId="
                + domainId + " ";
        
        String selectedUserId = request.getParameter("selectedUserId");
        
        whereSQL += " and (1<>1 ";

        logger.debug("参数selectedUserId:" + selectedUserId);
        if(!CommonUtils.isEmpty(selectedUserId)){
            whereSQL += " or :selectedUserId "
                + " like ezoffice.FN_LINKCHAR(ezoffice.FN_LINKCHAR('%$',user.empId),'$%') ";
            varMap.put("selectedUserId", selectedUserId);
        }
        whereSQL += ") ";

        

        // 排序
        String orderBy = " order by ";
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
        String[] arr = { "empId", "empName", "userAccounts", "empSex",
                "orgNameString", "orgName"};
        JacksonUtil util = new JacksonUtil();
        String json = util.writeArrayJSON(arr, list);
        json = "{pager:{pageCount:" + pageCount + ",recordCount:" + recordCount
                + "},data:" + json + "}";

        printResult(G_SUCCESS, json);

        logger.debug("json数据：" + json);
        
        long eTime = (new Date()).getTime();        
        logger.debug("run time: "+(eTime-sTime)+" 's.");

        return null;
    }
    
    public String getAllowId() {
        return allowId;
    }

    public void setAllowId(String allowId) {
        this.allowId = allowId;
    }

    public String getAllowName() {
        return allowName;
    }

    public void setAllowName(String allowName) {
        this.allowName = allowName;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getCurrentOrgId() {
        return currentOrgId;
    }

    public void setCurrentOrgId(String currentOrgId) {
        this.currentOrgId = currentOrgId;
    }

    public String getParentOrgId() {
        return parentOrgId;
    }

    public void setParentOrgId(String parentOrgId) {
        this.parentOrgId = parentOrgId;
    }

    public String getLimited() {
        return limited;
    }

    public void setLimited(String limited) {
        this.limited = limited;
    }

    public String getFromModule() {
        return fromModule;
    }

    public void setFromModule(String fromModule) {
        this.fromModule = fromModule;
    }

    public String getKey() {
        // 默认为ID
        if (key == null)
            return "id";

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getOrgSerial() {
        return orgSerial;
    }

    public void setOrgSerial(String orgSerial) {
        this.orgSerial = orgSerial;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public String getIsShortcut() {
        return isShortcut;
    }

    public void setIsShortcut(String isShortcut) {
        this.isShortcut = isShortcut;
    }

    public String getOtherDepart() {
        return otherDepart;
    }

    public void setOtherDepart(String otherDepart) {
        this.otherDepart = otherDepart;
    }

    public String getNameString() {
        return nameString;
    }

    public void setNameString(String nameString) {
        this.nameString = nameString;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String selectedName) {
        this.selectedName = selectedName;
    }

    public String getType() {
        return "yes".equals(single) ? "radio" : "checkbox";
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSearchGroupName() {
        return searchGroupName;
    }

    public void setSearchGroupName(String searchGroupName) {
        this.searchGroupName = searchGroupName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getFromParent() {
        return fromParent;
    }

    public void setFromParent(String fromParent) {
        this.fromParent = fromParent;
    }

    public String getSearchUserCName() {
        return searchUserCName;
    }

    public void setSearchUserCName(String searchUserCName) {
        this.searchUserCName = searchUserCName;
    }

    public String getSearchDutyOpr() {
        return searchDutyOpr;
    }

    public void setSearchDutyOpr(String searchDutyOpr) {
        this.searchDutyOpr = searchDutyOpr;
    }

    public String getSearchDutyLevel() {
        return searchDutyLevel;
    }

    public void setSearchDutyLevel(String searchDutyLevel) {
        this.searchDutyLevel = searchDutyLevel;
    }

    public String getSearchUserAccounts() {
        return searchUserAccounts;
    }

    public void setSearchUserAccounts(String searchUserAccounts) {
        this.searchUserAccounts = searchUserAccounts;
    }

    public String getSearchUserEName() {
        return searchUserEName;
    }

    public void setSearchUserEName(String searchUserEName) {
        this.searchUserEName = searchUserEName;
    }

    public String getSearchUserSimpleName() {
        return searchUserSimpleName;
    }

    public void setSearchUserSimpleName(String searchUserSimpleName) {
        this.searchUserSimpleName = searchUserSimpleName;
    }

    public String getSearchOrgId() {
        return searchOrgId;
    }

    public void setSearchOrgId(String searchOrgId) {
        this.searchOrgId = searchOrgId;
    }

    public String getSearchOrgName() {
        return searchOrgName;
    }

    public void setSearchOrgName(String searchOrgName) {
        this.searchOrgName = searchOrgName;
    }

    public String getSearchOrgSerial() {
        return searchOrgSerial;
    }

    public void setSearchOrgSerial(String searchOrgSerial) {
        this.searchOrgSerial = searchOrgSerial;
    }

    public String getSearchGroupCode() {
        return searchGroupCode;
    }

    public void setSearchGroupCode(String searchGroupCode) {
        this.searchGroupCode = searchGroupCode;
    }

    public String getCallbackOK() {
        return callbackOK;
    }

    public void setCallbackOK(String callbackOK) {
        this.callbackOK = callbackOK;
    }

    public String getCallbackCancel() {
        return callbackCancel;
    }

    public void setCallbackCancel(String callbackCancel) {
        this.callbackCancel = callbackCancel;
    }

    public String getIsShowCheckbox() {
        return isShowCheckbox;
    }

    public void setIsShowCheckbox(String isShowCheckbox) {
        this.isShowCheckbox = isShowCheckbox;
    }

    public String getIsShowAllBtn() {
        return isShowAllBtn;
    }

    public void setIsShowAllBtn(String isShowAllBtn) {
        this.isShowAllBtn = isShowAllBtn;
    }

    public String getShowSidelineorg() {
        return showSidelineorg;
    }

    public void setShowSidelineorg(String showSidelineorg) {
        this.showSidelineorg = showSidelineorg;
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public String getOgp() {
        return ogp;
    }

    public void setOgp(String ogp) {
        this.ogp = ogp;
    }

}
