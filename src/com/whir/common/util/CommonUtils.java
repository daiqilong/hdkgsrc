package com.whir.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.filters.SecurityWrapperResponse;

import com.whir.component.config.PropertiesUtil;
import com.whir.component.util.BrowseUtils;
import com.whir.ezoffice.customdb.common.util.DbOpt;

/**
 * 公共工具类
 * 
 * @author wang
 * 
 */
public final class CommonUtils {
    
    private static Logger logger = Logger.getLogger(CommonUtils.class.getName());
    
    private CommonUtils() {
    }

    /**
     * 获取session中userPageSize值
     * 
     * @param request
     * @return 页面列表记录数
     */
    public static int getSessionUserPageSize(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        return getUserPageSize(session);
    }

    /**
     * 更新session中userPageSize值
     * 
     * @param pageSize
     * @param request
     * @return true-成功
     */
    public static boolean setSessionUserPageSize(int pageSize,
            HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        session.setAttribute(com.whir.common.util.Constants.USER_PAGE_SIZE,
                pageSize + "");
        return true;
    }

    /**
     * 从session中获取显示记录条数
     * 
     * @param request
     *            HttpServletRequest
     * @return int
     */
    public static int getUserPageSize(HttpServletRequest request) {

        String pageSize = request.getParameter("pageSize");
        if (!isEmpty(pageSize)) {
            try {
                return Integer.parseInt(pageSize);
            } catch (Exception e) {
            }
        }

        HttpSession session = request.getSession(true);
        return Integer
                .parseInt(session
                        .getAttribute(com.whir.common.util.Constants.USER_PAGE_SIZE) != null ? session
                        .getAttribute(com.whir.common.util.Constants.USER_PAGE_SIZE)
                        + ""
                        : com.whir.common.util.Constants.DEFAULT_PAGE_SIZE + "");
    }

    /**
     * 从session中获取显示记录条数
     * 
     * @param session
     *            HttpSession
     * @return int
     */
    public static int getUserPageSize(HttpSession session) {
        return Integer
                .parseInt(session
                        .getAttribute(com.whir.common.util.Constants.USER_PAGE_SIZE) != null ? session
                        .getAttribute(com.whir.common.util.Constants.USER_PAGE_SIZE)
                        + ""
                        : com.whir.common.util.Constants.DEFAULT_PAGE_SIZE + "");
    }

    /**
     * 根据userId获取显示记录条数
     * 
     * @param userId
     *            String
     * @return int
     */
    public static int getUserPageSizeByUserId(String userId) {
        int userPageSize = com.whir.common.util.Constants.DEFAULT_PAGE_SIZE;
        DbOpt dbopt = new DbOpt();
        try {
            String user_pagesize = dbopt
                    .executeQueryToStr("select user_pagesize from org_employee where emp_id="
                            + userId);
            if (!isEmpty(user_pagesize)) {
                userPageSize = Integer.parseInt(user_pagesize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userPageSize;
    }

    /**
     * 获取系统每页记录数组
     * 
     * @param request
     * @return
     */
    public static String[] getSysPageSizeArray(HttpServletRequest request) {
        Long domainId = getSessionDomainId(request);

        return getSysPageSizeArray(domainId + "");
    }

    /**
     * 获取系统每页记录数组
     * 
     * @param domainId
     * @return
     */
    public static String[] getSysPageSizeArray(String domainId) {
        String[] ret = null;
        DbOpt dbopt = new DbOpt();
        try {
            String domain_syspageset = dbopt
                    .executeQueryToStr("select t.domain_syspageset from ORG_DOMAIN t where t.domain_id="
                            + domainId);
            if (!isEmpty(domain_syspageset)) {
                ret = domain_syspageset.trim().split(",");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ret == null) {
            ret = new String[] { "15", "30", "50", "100" };
        }

        return ret;
    }

    /**
     * 根据用户ID获取用户邮箱地址
     * @param userId
     * @return 邮箱地址，如:admin@whir.net
     */
    public static String getUserMail(String userId) {
        String mail = null;
        DbOpt dbopt = new DbOpt();
        try {
            mail = dbopt
                    .executeQueryToStr("select t.empemail from org_employee t where t.emp_id="
                            + userId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return mail;
    }
    
    /**
     * 取得文件下载路径
     * 
     * @param fileName
     *            String
     * @param fileServer
     *            String
     * @param currentDir
     *            String
     * @param uploadType
     *            String 上传方式 1-http other-ftp
     * @param request
     *            HttpServletRequest
     * @return String
     */
    public static String getDownloadFilePath(String fileName,
            String fileServer, String currentDir, String uploadType,
            HttpServletRequest request) {
        String result = "#";
        String uploadDir = "/upload/";

        if ("1".equals(uploadType)) { // http upload
            String filePath = request.getRealPath(uploadDir) + "/" + currentDir
                    + "/";
            String datePath = fileName.toString().substring(0, 6) + "/";
            File file = new File(filePath + datePath + fileName);
            if (file != null && file.isFile()) {
                result = fileServer + uploadDir + currentDir + "/" + datePath
                        + fileName;
            } else {
                file = new File(filePath + fileName);
                if (file != null && file.isFile()) {
                    result = fileServer + uploadDir + currentDir + "/"
                            + fileName;
                }
            }

        } else { // ftp upload
            URL url = null;
            URLConnection urlCon = null;
            InputStream inStream = null;
            String filePath = fileServer + uploadDir + currentDir + "/";
            try {
                url = new URL(filePath + fileName);
                urlCon = url.openConnection();
                inStream = urlCon.getInputStream();
                result = fileServer + uploadDir + currentDir + "/" + fileName;
            } catch (MalformedURLException me1) {
            } catch (IOException e1) {
                try {
                    String datePath = fileName.toString().substring(0, 6) + "/";
                    url = new URL(filePath + datePath + fileName);
                    urlCon = url.openConnection();
                    inStream = urlCon.getInputStream();
                    result = fileServer + uploadDir + currentDir + "/"
                            + datePath + fileName;
                } catch (MalformedURLException me2) {
                } catch (IOException e2) {
                }
            } finally {
                try {
                    if (inStream != null)
                        inStream.close();
                    inStream = null;
                } catch (IOException e3) {
                }
            }
        }

        return result;
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }

        String s = null;
        if (!(o instanceof String)) {
            s = o.toString();
        } else {
            s = (String) o;
        }

        if (s == null || s.trim().length() == 0 || "null".equals(s)) {
            return true;
        }
        return false;
    }
    
    /**
     * 校验是否为空，且是否校验数值
     * @param o
     * @param checkNumber
     * @return
     */
    public static boolean isEmpty(Object o, boolean checkNumber) {
        boolean result = isEmpty(o);
        
        if(result) return true;
        
        if(checkNumber){//校验是否是数值型
            try{
                new BigDecimal(o.toString());
                return false;
            }catch(Exception e){
                return true;
            }
        }
        
        return false;
    }
    
    public static String null2String(String s) {
        return s != null ? (!"null".equals(s) ? s : "") : "";
    }

    public static String appenZero(String s, int len) {
        StringBuffer sb = new StringBuffer();
        if (s.length() < len) {
            for (int i = 0; i < len - s.length(); i++) {
                sb.append("0");
            }
        }
        return sb.append(s).toString();
    }

    // --------------------------------------------------------------------------
    // 共通方法-start
    // --------------------------------------------------------------------------

    /**
     * 获取session实例
     */
    public static HttpSession getHttpSession(HttpServletRequest request) {
        return request.getSession(true);
    }

    /**
     * 获取session中domainId，默认为：0
     * 
     * @param request
     * @return
     */
    public static Long getSessionDomainId(HttpServletRequest request) {
        HttpSession session = getHttpSession(request);
        return session.getAttribute("domainId") == null ? Long.valueOf("0")
                : Long.valueOf(session.getAttribute("domainId").toString());
    }

    /**
     * 获取session中用户ID
     * @param request
     * @return
     */
    public static Long getSessionUserId(HttpServletRequest request) {
        return new Long(getHttpSession(request).getAttribute("userId")
                .toString());
    }
    
    /**
     * 获取session中组织ID
     * @param request
     * @return
     */
    public static Long getSessionOrgId(HttpServletRequest request) {
        return new Long(getHttpSession(request).getAttribute("orgId")
                .toString());
    }

    public static String getSessionUserName(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("userName").toString();
    }

    public static String getSessionOrgName(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("orgName").toString();
    }

    public static String getSessionUserAccount(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("userAccount").toString();
    }
    
    public static String getSessionUserPassword(HttpServletRequest request) {//isEncryptPassword
        String pw = (String)getHttpSession(request).getAttribute("userPassword");
        return pw == null ? "":pw;
    }

    public static String getSessionOrgIdString(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("orgIdString").toString();
    }

    public static String getSessionEmpIdCard(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("empIdCard") + "";
    }

    public static String getSessionEmpNumber(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("empNumber") + "";
    }

    public static String getSessionOrgSelfName(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("orgSelfName") + "";
    }

    public static String getSessionUserSimpleName(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("userSimpleName") + "";
    }

    public static String getSessionOrgSimpleName(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("orgSimpleName").toString();
    }

    public static String getSessionOrgSerial(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("orgSerial").toString();
    }

    public static String getUserIP(HttpServletRequest request) {
        return getHttpSession(request).getAttribute("userIP") + "";
    }

    public static boolean windowOvertime(HttpServletRequest request) {
        HttpSession session = getHttpSession(request);
        if (session == null || session.getAttribute("userName") == null) {
            return true;
        }
        return false;
    }

    public static HttpSession changeSessionIdentifier(HttpServletRequest request)
            throws Exception {

        // get the current session
        HttpSession oldSession = request.getSession();
        
        logger.debug("oldSession id:" + oldSession.getId());

        // make a copy of the session content
        Map<String, Object> temp = new ConcurrentHashMap<String, Object>();
        Enumeration e = oldSession.getAttributeNames();
        while (e != null && e.hasMoreElements()) {
            String name = (String) e.nextElement();
            Object value = oldSession.getAttribute(name);
            temp.put(name, value);
        }

        // kill the old session and create a new one
        oldSession.invalidate();
        
        HttpSession newSession = request.getSession(true);
        
        logger.debug("newSession id:" + newSession.getId());

        // copy back the session content
        for (Map.Entry<String, Object> stringObjectEntry : temp.entrySet()) {
            newSession.setAttribute(stringObjectEntry.getKey(),
                    stringObjectEntry.getValue());
        }

        return newSession;
    }
    
    // --------------------------------------------------------------------------
    // 共通方法-end
    // --------------------------------------------------------------------------

    public static String convertStr(String strIds, String fields) {
        StringBuffer where = new StringBuffer();
        String[] tmp = strIds.split(",");
        int max = 500;
        // 为处理SQL语句中in字段值太多，所做的截断拼接处理
        if (tmp.length > max) {
            int t = tmp.length % max == 0 ? tmp.length / max : tmp.length / max
                    + 1;
            for (int i = 0; i < t; i++) {
                if (i == 0) {
                    where.append(fields + " in (-1");
                    for (int j = 0; j < max; j++) {
                        if ((i * max + j) < tmp.length) {
                            where.append(",").append(tmp[i * max + j]);
                        }
                    }
                    where.append(")");
                } else {
                    where.append(" or " + fields + " in (-1");
                    for (int j = 0; j < max; j++) {
                        if ((i * max + j) < tmp.length) {
                            where.append(",").append(tmp[i * max + j]);
                        }
                    }
                    where.append(")");
                }
            }
        } else {
            where.append(fields + " in (");
            where.append(strIds);
            where.append(")");
        }
        return where.toString();
    }

    /**
     * 获取request参数name-value串，以&分隔
     * 
     * @param request
     * @return
     */
    public static String getQueryString(HttpServletRequest request) {
        String paramUrl = "";
        Enumeration paramsEnum = request.getParameterNames();
        for (int i = 0; paramsEnum.hasMoreElements(); i++) {
            String paramName = (String) paramsEnum.nextElement();

            if (i == 0) {
                paramUrl += "?";
            } else {
                paramUrl += "&";
            }

            paramUrl += paramName + "=" + request.getParameter(paramName);
        }

        return paramUrl;
    }

    /**
     * 获取request参数name-value串，以&分隔
     * 
     * @param request
     * @return
     */
    public static String getEncryptQueryString(HttpServletRequest request) {
        return new BASE64().BASE64EncoderUTF8(getQueryString(request));
    }

    /**
     * 获取request参数name-value串，以&分隔
     * 
     * @param request
     * @return
     */
    public static String getQueryString(HttpServletRequest request,
            boolean encrypt) {
        if (encrypt) {
            return getEncryptQueryString(request);
        } else {
            return getQueryString(request);
        }
    }

    public static String encryptPassword(String password) {
        String result = "";
        
        BASE64 base64 = new BASE64();

        result = base64.BASE64EncoderUTF8(password);
        result = base64.BASE64EncoderUTF8(result + "whir");
        result = base64.BASE64EncoderUTF8(result);

        return result;
    }

    public static String decryptPassword(String password) {
        String result = "";
        
        BASE64 base64 = new BASE64();

        result = base64.BASE64DecoderUTF8(password);
        result = base64.BASE64DecoderUTF8(result);
        result = result.substring(0, result.length() - 4);
        result = base64.BASE64DecoderUTF8(result);

        return result;
    }

    /**
     * 获取登录用户的ip
     * 
     * @param request
     * @return 登录用户的ip
     */
    public static String getIpAddr(HttpServletRequest request) {
        String str = request.getHeader("X-Real-IP");
        if ((str == null) || (str.length() == 0)
                || ("unknown".equalsIgnoreCase(str))) {
            str = request.getHeader("X-Forwarded-For");
        }
        if ((str == null) || (str.length() == 0)
                || ("unknown".equalsIgnoreCase(str))) {
            str = request.getHeader("Proxy-Client-IP");
        }
        if ((str == null) || (str.length() == 0)
                || ("unknown".equalsIgnoreCase(str))) {
            str = request.getHeader("WL-Proxy-Client-IP");
        }        
        if ((str == null) || (str.length() == 0)
                || ("unknown".equalsIgnoreCase(str))) {
            str = request.getRemoteAddr();
        }
        
//        Enumeration e = request.getHeaderNames();
//        while(e.hasMoreElements()){
//            String ah = (String)e.nextElement();
//            logger.debug("getHeader:" + ah + "=" + request.getHeader(ah));
//        }
//        
//        logger.debug("getRemoteAddr:" + request.getRemoteAddr());
        
        //str = request.getRemoteAddr();
        
        return str;
    }
    
    /**
     * 检查是否移动访问
     * 
     * @param req HttpServletRequest
     * @return boolean
     */
    public static boolean checkMobileAccess(HttpServletRequest req) {
        String userAgent = req.getHeader("User-Agent");
        logger.debug("User-Agent:" + userAgent);
        
        if(userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if((userAgent.indexOf("linux") != -1 && userAgent.indexOf("android") != -1) ||
               userAgent.indexOf("iphone") != -1 ||
               userAgent.indexOf("ipad") != -1 || 
               (userAgent.indexOf("msie") != -1 && userAgent.indexOf("touch") != -1) || 
               (userAgent.indexOf("linux") != -1 && (userAgent.indexOf("chrome") != -1 || userAgent.indexOf("safari") != -1))) {
               //userAgent.indexOf("safari")!=-1/*mac pc*/) {
                //eRenEben
                return true;
            }
        }
        
        return false;
    }
    
    
    /**
     * 判断是否是 linux客户端访问  
     * @param request
     * @return
     */
    public static boolean isLinuxClient(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        logger.debug("User-Agent:" + userAgent);
        //User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; ARM; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0)
        //User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko
        //User-Agent:Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; GTB7.5; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)
        
        if(userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if(userAgent.indexOf("linux") != -1 ) {
                return true;
            }
        } 
        return false;
    }
    
    /**
     * 是否平板（ipad、surface）访问
     * 
     * @param req
     * @return true-是 false-否
     */
    public static boolean isPad(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        
        logger.debug("User-Agent:" + userAgent);
        //User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; ARM; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0)
        //User-Agent:Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko
        //User-Agent:Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; GTB7.5; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0)
        
        if(userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if((userAgent.indexOf("linux") != -1 && userAgent.indexOf("android") != -1) ||
               userAgent.indexOf("ipad") != -1 ||
               userAgent.indexOf("android") != -1 ||
               (userAgent.indexOf("msie") != -1 && userAgent.indexOf("touch") != -1 && userAgent.indexOf("tablet pc") != -1)
                 &&userAgent.indexOf("trident/7.0; touch")<0 //某360 浏览器
                ) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 是否禁止平板（ipad、surface）访问
     * 
     * @param req
     * @return true-是 false-否
     */
    public static boolean isForbiddenPad(HttpServletRequest request){
        return !isPad(request);
    }
    
    /**
     * 是否平板（surface）访问
     * 
     * @param req
     * @return true-是 false-否
     */
    public static boolean isSurface(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        
        //logger.debug("User-Agent:" + userAgent);
        //User-Agent:Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; ARM; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0)
        
        if(userAgent != null) {
            userAgent = userAgent.toLowerCase();
            if(userAgent.indexOf("msie") != -1 && userAgent.indexOf("touch") != -1 && userAgent.indexOf("tablet pc") != -1) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 获取浏览器类型
     * 
     * @param request
     * @return 浏览器类型
     */
    public static String getBrowserType(HttpServletRequest request){
        String result = "Unknown";
        
        String userAgent = request.getHeader("User-Agent");
        
        logger.debug("User-Agent:" + userAgent);
        
        result = new BrowseUtils().checkBrowse(userAgent);
        
        return result;
    }
    
    /**
     * 返回统一的url链接包含参数
     * 
     * @param paramsStr
     * @return 返回统一的链接地址
     */
    public static String getUniUrl(String paramsStr){        
        return com.whir.component.config.PropertiesUtil.getInstance().getRootPath() + "/UniAuth!auth.action?" + signatureBaseString(paramsStr);
    }
    
    /**
     * 根据url和参数重新生成新的链接参数
     * 
     * @param paramsStr
     * @return
     */
    public static String signatureBaseString(String paramsStr) {
        
        if(paramsStr == null) return "";
        
        String url = paramsStr;
        String queryParams = paramsStr;
        
        int pos = paramsStr.indexOf("?");
        if(pos != -1){
            url = paramsStr.substring(0, pos);
            queryParams = paramsStr.substring(pos + 1);
        }
        
        if(url.startsWith("/")){
            url = url.substring(1);
        }
        
        logger.debug("url:"+url);
        
        if(queryParams.startsWith("?")){
            queryParams = queryParams.substring(1);
        }
        
        String sortedStr = sortedQueryParams(queryParams);
        String base64Str = new BASE64().BASE64EncoderUTF8(sortedStr);
        String token = new MD5().toMD5(base64Str);
        
        logger.debug("token:"+token);
        
        //return queryParams+"&redirect_url="+(new BASE64().BASE64EncoderUTF8(url))+"&token="+token+"&ts="+(new Date()).getTime();
        return queryParams+"&redirect_url="+url+"&token="+token+"&ts="+(new Date()).getTime();
    }
    
    /**
     * 对参数进行按字典顺序进行排序
     * 
     * @param queryParam
     * @return 已排序的参数串
     */
    public static String sortedQueryParams(String queryParam) {
        logger.debug("queryParam:"+queryParam);
        
        List list = new ArrayList();
        String[] params = queryParam.split("&");
        for (int i = 0, len=params.length; i < len; i++) {
            String[] p = params[i].split("=");
            list.add(p);
        }
        
        compareObject(list, 0, 0);
        
        String sortedStr = "";
        for(int i=0, len=list.size(); i<len; i++){
            String[] p = (String[])list.get(i);
            sortedStr += "&" + p[0] + "=";
            if(p.length > 1){
                sortedStr += p[1];
            }
        }
        
        if(sortedStr.startsWith("&")){
            sortedStr = sortedStr.substring(1);
        }
        
        logger.debug("sortedStr:"+sortedStr);
        
        return sortedStr;
    }
    
    /**
     * 数组排序
     * @param list 待排序list
     * @param index 待比较的下标
     * @param sortType 排序方式 0-升序 1-降序
     */
    public static void compareObject(List list, final int index, final int sortType) {
        Collections.sort(list, new Comparator(){
            public int compare(Object obj1, Object obj2){
                Object[] o1 = (Object[])obj1;
                Object[] o2 = (Object[])obj2;
                
                String s1 = "" + o1[index];
                String s2 = "" + o2[index];
                
                if(s1.compareTo(s2) > 0){
                    if(sortType == 1){
                        return -1;
                    }
                    return 1;
                }
                
                return 0;
            }
        });
    }
    
    public static String getMapValue(String key, Map map){
        if(map == null){
            return "";
        }
        
        Object value = map.get(key);
        if(isEmpty(value)){
            return "";
        }
        
        return (String)value;
    }
    
    /**
     * 获取系统设置中支持的工作流引擎类别
     * 
     * @param domainId
     * @return 0-仅使用ezFLOW引擎 1-仅使用老引擎 2-新老引擎都支持
     */
    public static String getWorkflowType(String domainId) {
        // 0-仅使用ezFLOW引擎 1-仅使用老引擎 2-新老引擎都支持
        String workflowType = com.whir.org.common.util.SysSetupReader
                .getInstance().getSysValueByName("workflowType", domainId);

        return workflowType;
    }
    
    /**
     * 过滤无效的组织、用户（适用于多选组织用户群组）
     * 
     * @param scopeIds 如：$4333$*555*@9800@@37834@*18951*$8527$
     * @return Map 返回有效的组织、用户串
     */
    public static Map getScopeFilter(String scopeIds) {
        return getScopeFilter(scopeIds, false, "0", "id");
    }
    
    /**
     * 过滤无效的组织、用户
     * 
     * @param scopeIds
     * @param single
     * @param whirTagit 1-tagit选择用户
     * @param idOrCode id-数据ID code-账号、编码
     * @return 返回过滤后的串
     */
    public static Map getScopeFilter(String scopeIds, boolean single, String whirTagit, String idOrCode) {
        Map result = new com.whir.org.bd.usermanager.UserBD().filterUserOrgGroup(scopeIds, single, whirTagit, idOrCode, "zh_CN");
        return result;
    }
    
    /**
     * 根据用户账号判断是否AD域登录验证
     * @param userAccount
     * @return true-是 false-否
     */
    public static boolean isADCheckByUserAccount(String userAccount){
        String isAdCheck = new com.whir.org.bd.usermanager.UserBD().getIsAdCheckByAccount(userAccount);
        if("1".equals(isAdCheck)){//AD验证
            return true;
        }        
        return false;
    }
    
    /**
     * 获取系统设置中使用邮件
     * 
     * @param domainId
     * @return 0-外部邮件 1-内部邮件 2-全部使用
     */
    public static String getUseMailType(String domainId) {
        String usemail = com.whir.org.common.util.SysSetupReader
                .getInstance().getSysValueByName("usemail", domainId);

        return usemail;
    }
    
    /**
     * 获取系统设置中WORD编辑的上传附件大小
     * 
     * @param domainId
     * @return (M)
     */
    public static String getWordlimitsize(String domainId) {
        String wordlimitsize = com.whir.org.common.util.SysSetupReader
                .getInstance().getSysValueByName("wordlimitsize", domainId);

        return wordlimitsize;
    }
    
    /**
     * 判断是否ftp上传方式
     * @param domainId
     * @return true-是 false-否
     */
    public static boolean isFtpUpload(String domainId) {
        Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance()
                .getSysSetupMap(domainId);
        if (sysMap != null && sysMap.get("附件上传") != null
                && sysMap.get("附件上传").toString().equals("0")) {
            return true;
        }

        return false;
    }
    
    /**
     * 判断是否加密存储
     * @param domainId
     * @return "1"-是 "0"-否
     */
    public static String isEncrypt(String domainId) {
        Map sysMap = com.whir.org.common.util.SysSetupReader.getInstance()
                .getSysSetupMap(domainId);
        if (sysMap != null && sysMap.get("加密存储") != null
                && sysMap.get("加密存储").toString().equals("1")) {
            return "1";
        }

        return "0";
    }
    
    /**
     * 获取request自定义字段值
     * @param fieldName
     * @param request
     * @return 自定义字段值，如不存在字段，则返回null
     */
    public static String getRequestParamValueByFieldName(String fieldName, HttpServletRequest request) {
        String fieldShow = request.getParameter(fieldName + "_showtype");
        if(fieldShow == null) return null;

        String result = "";//保存值
        if("103".equals(fieldShow) || //单选
           "104".equals(fieldShow) || //多选
           "115".equals(fieldShow) || //附件上传
           "116".equals(fieldShow) || //Word编辑
           "117".equals(fieldShow) || //Excel编辑
           "118".equals(fieldShow) || //WPS编辑
           "210".equals(fieldShow) || //单选人
           "211".equals(fieldShow) || //多选人
           "212".equals(fieldShow) || //单选组织
           "214".equals(fieldShow) || //多选组织
           "704".equals(fieldShow) || //单选人（本组织）
           "705".equals(fieldShow) //多选人（本组织）
           ) {

            //从页面获取相应的值
            String new_component = request.getParameter(Constants.
                PREFIX_NEW_COMPONENT + fieldName);
            if(new_component != null) {
                if("210".equals(fieldShow) || //单选人
                   "211".equals(fieldShow) || //多选人
                   "212".equals(fieldShow) || //单选组织
                   "214".equals(fieldShow) || //多选组织
                   "704".equals(fieldShow) || //单选人（本组织）
                   "705".equals(fieldShow) //多选人（本组织）
                   ) {
                    String reqNameValue = request.getParameter(
                        fieldName + new_component + "_Name");
                    String reqIdValue = request.getParameter(
                        fieldName + new_component + "_Id");
                    result = reqNameValue + ";" + reqIdValue;//保存值

                } else if("115".equals(fieldShow)) {//附件上传
                    String reqNameValue = request.getParameter(fieldName + new_component + "_fileName");
                    String reqIdValue = request.getParameter(fieldName + new_component + "_saveName");
                    String[] fileNameArr = reqNameValue.split(";");
                    String[] saveNameArr = reqIdValue.split(";");
                    String saveFileTemp = "";
                    String fileFileTemp = "";
                    for(int k = 0; k < saveNameArr.length; k++) {
                        if((saveNameArr[k] == null ||
                            saveNameArr[k].equals("aaaaaa") ||
                            saveNameArr[k].trim().equals(""))) {
                            continue;
                        } else {
                            saveFileTemp += saveNameArr[k] + ",";
                            fileFileTemp += fileNameArr[k] + ",";
                        }
                    }
                    result = !CommonUtils.isEmpty(saveFileTemp)?(saveFileTemp + ";" + fileFileTemp):"";//保存值

                } else {
                    String[] newFieldValue = request.getParameterValues(
                        fieldName + new_component);
                    if(newFieldValue != null) {
                        String reqIdValue = "";
                        if(newFieldValue.length > 1) {
                            for(int i = 0, k=newFieldValue.length; i < k; i++) {
                                reqIdValue += newFieldValue[i] + ",";
                            }
                        } else {
                            reqIdValue = newFieldValue[0];
                        }

                        result = reqIdValue;//保存值
                    }
                }
            }

        } else {
            String reqIdValue = request.getParameter(fieldName);
            
            result = reqIdValue;//保存值
        }
        
        if(CommonUtils.isEmpty(result)){
            result = "";
        }
        
        return result;
    }
    
    /**
     * 判断当前用户是否在范围内
     * 
     * @param scopeIds 范围ID串：$123$$567$*323**54*@777@
     * @param userId 当前用户ID
     * @param orgId  当前用户组织ID
     * @param orgIdString 当前用户组织ID串
     * @param groupIdStr 当前用户所在群组ID串
     * @param ignoreNull 是否忽略空（即scopeIds为空时，直接返回ignoreNull），true-是，false-否
     * @return true or false
     */
    public static boolean isContainsScope(String scopeIds, String userId, String orgId, String orgIdString, String ownerGroupIdStr, boolean ignoreNull){
       
        if(!isEmpty(scopeIds)){
            ConversionString conversionString = new ConversionString(scopeIds);
            String scopeUserIds = conversionString.getUserString();
            String scopeOrgIds = conversionString.getOrgString();
            String scopeGroupIds = conversionString.getGroupString();
            
            if(scopeUserIds.indexOf("$"+userId+"$") != -1){
                return true;
            }
            
            if(scopeOrgIds.indexOf("*"+orgId+"*") != -1){
                return true;
            }
            
            if(!isEmpty(scopeOrgIds)){
                scopeOrgIds = scopeOrgIds.substring(1, scopeOrgIds.length()-1);
                String[] scopeOrgIdsArr = scopeOrgIds.split("\\*\\*");
                for(int i=0; i<scopeOrgIdsArr.length; i++){
                    if(orgIdString.indexOf("$"+scopeOrgIdsArr[i]+"$") != -1){
                        return true;
                    }
                }
            }
            
            if(!isEmpty(ownerGroupIdStr)){
                ownerGroupIdStr = ownerGroupIdStr.substring(1, ownerGroupIdStr.length()-1);
                String[] groupIdStrArr = ownerGroupIdStr.split("\\@\\@");
                for(int i=0; i<groupIdStrArr.length; i++){
                    if(scopeGroupIds.indexOf("@"+groupIdStrArr[i]+"@") != -1){
                        return true;
                    }
                }
            }
            
            return false;
        }
        
        return ignoreNull;        
    }
    
    /**
     * 检验参数合法性
     * 
     * @param str
     * @return
     */
    public static boolean checkValidCharacter(String str) {
        if(str == null){
            return true;
        }
        
        if(str.indexOf(" ") != -1 || str.indexOf("'") != -1){
            return false;
        }
        
        return true;
    }
    
    public static boolean checkAccessRequest(HttpServletRequest request,
            HttpServletResponse response) {
        String pathInfo = request.getRequestURI();
        //logger.debug("pathInfo=" + pathInfo);
        String rootPath = PropertiesUtil.getInstance(request).getRootPath();
        try {
            if (pathInfo.indexOf("/evo/ipad/") != -1) {
                response.sendRedirect(rootPath + "/evo/ipad/login.jsp");
                return false;
            }

            if (pathInfo.indexOf("/evo/sp/") != -1) {
                response.sendRedirect(rootPath + "/evo/sp/login.jsp");
                return false;
            }
        } catch (Exception e) {
        }

        return true;
    }
    
    public static void setSessionIdHttpOnly(HttpServletRequest request,
            HttpServletResponse response) {
        try{
            SecurityWrapperResponse securityWrapperResponse = new SecurityWrapperResponse(response, "sanitize");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookie != null) {
                        // ESAPI.securityConfiguration().getHttpSessionIdName() returns JSESSIONID by default configuration
                        if (ESAPI.securityConfiguration().getHttpSessionIdName().equals(cookie.getName())) {
                            securityWrapperResponse.addCookie(cookie);
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * 针对不同浏览器，中文乱码，如：国产化系统安装的firefox4时，附件下载乱码，而高版本正常。
     * 
     * @param name
     * @param request
     * @return
     */
    public static String encodeName(String name, HttpServletRequest request) {        
        return com.whir.component.util.SystemUtils.encodeName(name, request);
    }
}
