package com.whir.org.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.whir.component.SysSettings;
import com.whir.ezoffice.customdb.common.util.DbOpt;

public class SysSetupReader {

    private static final long serialVersionUID = 1L;
    private static Logger logger = Logger.getLogger(SysSetupReader.class
            .getName());

    private static SysSetupReader sysSetupReader;
    private static Map sysSetupMap;
    private HttpServletRequest request;

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public static SysSetupReader getInstance() {
    	
        if (sysSetupReader == null) {
            sysSetupReader = new SysSetupReader();
            sysSetupReader.init("0");
        }
        return sysSetupReader;
    }

    public static SysSetupReader getInstance(String domainId) {
        if (sysSetupReader == null) {
            sysSetupReader = new SysSetupReader();
            sysSetupReader.init(domainId);
        }
        return sysSetupReader;
    }

    public SysSetupReader() {
    }

    public void init(String domainId) {
        logger.debug("加载...");
        
        String querySql = "SELECT ";
        querySql += " DOMAIN_SERVEROPTION, ";
        querySql += " DOMAIN_WORKLOG, ";
        querySql += " DOMAIN_MAILBOXSIZE, ";
        querySql += " DOMAIN_NETDISKSIZE, ";
        querySql += " domain_fonttype, ";
        querySql += " domain_wordsize, ";
        querySql += " domain_mailnum, ";
        querySql += " ishistorylog, ";
        querySql += " historylog_days, ";
        querySql += " historylog_runhours, ";
        
        querySql += " captcha, ";
        querySql += " accountscase, ";
        querySql += " wordlimitsize, ";
        querySql += " workflowtype, ";
        querySql += " usemail, ";
        querySql += " attach_preview, ";
        querySql += " page_fontsize, ";
        querySql += " use_forgot_password,";
        querySql += " oa_location, ";
        querySql += " oa_wxlocation, ";
        querySql += " yibo_flag, ";
        querySql += " oa_vkey, ";
        querySql += " oa_infoorder, ";
        querySql += " oa_forumorder, ";
        querySql += " oa_mailremind, ";
        querySql += " oa_PDF, ";
        //20170505 -by jqq evo端word编辑增加选人范围
        querySql += " evoWordRangeIds, ";
        querySql += " evoWordRangeNames ";

        querySql += " FROM ORG_DOMAIN where domain_id=? ";
        
        String serverOption = "1111111111010010";// 增加了后8位分别表示：邮件群发(0表示不控制、1表示控制)、内部联系人查看(0表示不控制、1表示控制)、附件是否加密存储(0表示否、1表示是)、同一用户同时登录(0表示不允许、1允许)、信息未查看用户(0不允许,1允许)、移动办公信息推送(0不使用,1使用)、ftp下载方式(0-ftp,1-http)、移动OA位置服务标志(0-不使用,1-使用)
        String workLog = "";
        String mailBoxSize = "";
        String netDiskSize = "";
        String HtmlFontType = "";// html编辑器字体
        String HtmlWordSize = "";// html编辑器字号
        String mailNum = "10000";// 邮件数量(默认10000)

        String historyLog = "0";
        String logSaveDays = "";
        String logRunHour = "";
        
        String captcha = "0";
        String accountscase = "1";
        String wordlimitsize = "15";
        String workflowType = "2";//2-新老引擎都支持
        String usemail = "1";//0-外部邮件 1-内部邮件 2-全部使用
        String attachPreview = "0";
        String pageFontSize = "14";
        String resetPassword = "0";//找回密码功能 默认不使用
        String location = "0";//客户端定位
        String wxlocation = "0";//微信客户端定位
        String yibo_flag = "0";//易播栏目
        String oa_vkey="";
        String oa_infoorder="0";//默认发帖日期0
        String oa_forumorder="1";//默认回帖时间1
        String oa_mailremind="1";//默认不选中“无”，即邮件提醒打开
        String oa_PDF= "0";//默认不开起pdf批注
        //20170505 -by jqq evo端word编辑范围
        String evoWordRangeIds= "";
        String evoWordRangeNames= "";
        java.sql.Connection conn = null;
        java.sql.PreparedStatement stmt = null;
        try {
            sysSetupMap = new HashMap();
            javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                    .getDataSource();
            conn = ds.getConnection();
            stmt = conn.prepareStatement(querySql);
            stmt.setString(1, domainId);
            java.sql.ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                serverOption = rs.getString(1);
                workLog = rs.getString(2);
                mailBoxSize = rs.getString("domain_mailboxsize");
                netDiskSize = rs.getString("domain_netdisksize");
                HtmlFontType = rs.getString("domain_fonttype") != null ? rs
                        .getString("domain_fonttype") : "";
                HtmlWordSize = rs.getString("domain_wordsize") != null ? rs
                        .getString("domain_wordsize") : "";
                mailNum = rs.getString("domain_mailnum") != null ? rs
                        .getString("domain_mailnum") : "10000";

                historyLog = rs.getString("ishistorylog") != null ? rs
                        .getString("ishistorylog") : "0";
                logSaveDays = rs.getString("historylog_days") != null ? rs
                        .getString("historylog_days") : "60";
                logRunHour = rs.getString("historylog_runhours") != null ? rs
                        .getString("historylog_runhours") : "1";
                        
                captcha = rs.getString("captcha") != null ? rs.getString("captcha") : "0";
                accountscase = rs.getString("accountscase") != null ? rs.getString("accountscase") : "1";
                wordlimitsize = rs.getString("wordlimitsize") != null ? rs.getString("wordlimitsize") : "15";
                workflowType = rs.getString("workflowType") != null ? rs.getString("workflowType") : "2";
                usemail = rs.getString("usemail") != null ? rs.getString("usemail") : "1";
                attachPreview = rs.getString("attach_preview") != null ? rs
                        .getString("attach_preview") : "0";
                pageFontSize = rs.getString("page_fontsize") != null ? rs
                                .getString("page_fontsize") : "14";
                resetPassword = rs.getString("use_forgot_password") != null ? rs
	                    .getString("use_forgot_password") : "0";
                location = rs.getString("oa_location") != null ? rs
	                    .getString("oa_location") : "0";
                wxlocation = rs.getString("oa_wxlocation") != null ? rs
	                    .getString("oa_wxlocation") : "0";
                yibo_flag = rs.getString("yibo_flag") != null ? rs
	                    .getString("yibo_flag") : "0";
                oa_vkey = rs.getString("oa_vkey") != null ? rs
                        .getString("oa_vkey") : "";
                oa_infoorder = rs.getString("oa_infoorder") != null ? rs
                        .getString("oa_infoorder") : "0";
                oa_forumorder = rs.getString("oa_forumorder") != null ? rs
                        .getString("oa_forumorder") : "1";
                oa_mailremind = rs.getString("oa_mailremind") != null ? rs
                        .getString("oa_mailremind") : "1";
                oa_PDF = rs.getString("oa_PDF") != null ? rs
                        .getString("oa_PDF") : "0";
                evoWordRangeIds = (rs.getString("evoWordRangeIds") != null
                        && !"".equals(rs.getString("evoWordRangeIds")) && !"null"
                        .equals(rs.getString("evoWordRangeIds"))) ? rs
                        .getString("evoWordRangeIds") : "";
                evoWordRangeNames = (rs.getString("evoWordRangeNames") != null
                        && !"".equals(rs.getString("evoWordRangeNames")) && !"null"
                        .equals(rs.getString("evoWordRangeNames"))) ? rs
                        .getString("evoWordRangeNames") : "";

            }
            rs.close();
            stmt.close();
            conn.close();

            Map map = new HashMap();
            map.put("附件上传", serverOption.charAt(0) + "");
            map.put("使用手写意见", serverOption.charAt(1) + "");

            map.put("短信开通", serverOption.charAt(2) + "");
            map.put("图形工作流", serverOption.charAt(3) + "");
            map.put("RTX在线感知", serverOption.charAt(4) + "");
            map.put("WORD编辑", serverOption.charAt(5) + "");
            
            // zhuo add
            map.put("与ezSITE结合", serverOption.charAt(6) + "");
            map.put("电子签章", serverOption.charAt(7) + "");

            // ----2008-11-13
            if (serverOption.length() > 8) {
                map.put("邮件群发", serverOption.charAt(8) + "");
            } else {
                map.put("邮件群发", "1");
            }
            if (serverOption.length() > 9) {
                map.put("内部联系人", serverOption.charAt(9) + "");
            } else {
                map.put("内部联系人", "1");
            }
            if (serverOption.length() > 10) {
                map.put("加密存储", serverOption.charAt(10) + "");
            } else {
                map.put("加密存储", "0");
            }
            if (serverOption.length() > 11) {// 同一用户同时登录(0表示不允许、1允许)
                map.put("userLoginType", serverOption.charAt(11) + "");
            } else {
                map.put("userLoginType", "1");
            }
            if (serverOption.length() > 12) {// 信息未查看用户(0表示不允许、1允许)
                map.put("infoNotView", serverOption.charAt(12) + "");
            } else {
                map.put("infoNotView", "0");
            }
            if (serverOption.length() > 13) {// 移动办公信息推送(0表示不使用、1使用)
                map.put("isMobilePush", serverOption.charAt(13) + "");
            } else {
                map.put("isMobilePush", "0");
            }

            if (serverOption.length() > 14) {// ftp下载方式:0-ftp控件下载 1-http下载
                map.put("ftpDownloadType", serverOption.charAt(14) + "");
            } else {
                map.put("ftpDownloadType", "1");
            }

            // 移动OA位置服务
            if (serverOption.length() > 15) {// 0表示不使用、1使用
                map
                        .put("isMobilePositionService", serverOption.charAt(15)
                                + "");
            } else {
                map.put("isMobilePositionService", "0");
            }
            // evoword  --2015-11-26--xiehd--
            if (serverOption.length() > 16) {// 0表示不使用、1使用
                map
                        .put("evoword", serverOption.charAt(16)
                                + "");
            } else {
                map.put("evoword", "0");
            }

            map.put("HtmlFontType", HtmlFontType);// html编辑器字体
            map.put("HtmlWordSize", HtmlWordSize);// html编辑器字号
            map.put("mailNum", mailNum);// 邮件数量

            map.put("mailBoxSize", mailBoxSize);
            map.put("netDiskSize", netDiskSize);

            // 历史日志
            map.put("historyLog", historyLog);
            map.put("logSaveDays", logSaveDays);
            map.put("logRunHour", logRunHour);
            
            map.put("captcha", captcha);
            map.put("accountscase", accountscase);
            map.put("wordlimitsize", wordlimitsize);
            map.put("workflowType", workflowType);
            map.put("usemail", usemail);
            map.put("attachPreview", attachPreview);
            map.put("pageFontSize", pageFontSize);
            map.put("resetPassword", resetPassword);
            map.put("location", location);
            map.put("wxlocation", wxlocation);
            map.put("yibo_flag", yibo_flag);
            map.put("oa_vkey", oa_vkey);
            map.put("oa_infoorder", oa_infoorder);
            map.put("oa_forumorder", oa_forumorder);
            map.put("oa_mailremind", oa_mailremind);
            map.put("oa_PDF", oa_PDF);
            //20170505 -by jqq evo端word编辑增加选人范围
            map.put("evoWordRangeIds", evoWordRangeIds);
            map.put("evoWordRangeNames", evoWordRangeNames);
            sysSetupMap.put(domainId, map);
            sysSetupMap.put("workLog", workLog);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    public Map getSysSetupMap(String domainId) {
        if (sysSetupMap == null || sysSetupMap.get(domainId) == null) {
            init(domainId);
        }
        return (Map) sysSetupMap.get(domainId);
    }

    public void updateSysSetup(HttpServletRequest httpServletRequest)
            throws Exception {
        String configFile = System.getProperty("user.dir")
                + "/config/sysSetup.xml";
        FileInputStream configFileInputStream = new FileInputStream(new File(
                configFile)); // 建立配置文件的输入流
        SAXBuilder sb = new SAXBuilder(); // 新建立构造器
        Document doc = sb.build(configFileInputStream);

        Element root = doc.getRootElement(); // 取得根节点, 就是例子中的<total>节点
        List list = root.getChildren(); // 取得根节点下一层所有节点放入List类中
        Element item = null, sub1 = null, sub2 = null;
        for (int i = 0; i < list.size(); i++) {
            item = (Element) list.get(i); // 取得节点实例
            sub1 = item.getChild("name");
            sub2 = item.getChild("inuse");
            if (sub1.getText().equals("附件上传")) {
                if (httpServletRequest.getParameter("uploadProtocol") != null) {
                    sub2.setText(httpServletRequest
                            .getParameter("uploadProtocol"));
                }
            } else if (sub1.getText().equals("使用手写意见")) {
                if (httpServletRequest.getParameter("handIdea") != null) {
                    sub2.setText(httpServletRequest.getParameter("handIdea"));
                }
            }
            /* START----------------NEW[何炜]20051102[NUM:1]------------------- */
            else if (sub1.getText().equals("短信开通")) {
                if (httpServletRequest.getParameter("message") != null) {
                    sub2.setText(httpServletRequest.getParameter("message"));
                }
            }
            /* END------------------NEW[何炜]20051102[NUM:1]------------------- */
            /* START----------------NEW[何炜]20051102[NUM:1]------------------- */
            else if (sub1.getText().equals("图形工作流")) {
                if (httpServletRequest.getParameter("ezWorkFlow") != null) {
                    sub2.setText(httpServletRequest.getParameter("ezWorkFlow"));
                }
            }
            /* END------------------NEW[何炜]20051102[NUM:1]------------------- */
            else if (sub1.getText().equals("RTX在线感知")) {
                if (httpServletRequest.getParameter("rtxOnline") != null) {
                    sub2.setText(httpServletRequest.getParameter("rtxOnline"));
                }
            }
        }
        Format format = Format.getCompactFormat();
        format.setEncoding("gb2312"); // 设置xml文件的字符为gb2312
        format.setIndent("    "); // 设置xml文件的缩进为4个空格

        XMLOutputter outp = new XMLOutputter(format);
        outp.output(doc, new FileOutputStream(configFile));
        configFileInputStream.close();
    }

    /**
     * 判断是否是多域系统
     * 
     * @return String 如果是多域系统返回null否则将返回单域的域帐户
     */
    public String isMultiDomain() {
        String account = null;
        int i = 0;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT domain_account,domain_id FROM ORG_DOMAIN");
            while (rs.next()) {
                i++;
                if (rs.getString(2).equals("0")) {// 如果多余2条数据，则在登录页面要显示输入单位帐号(只有2条数据domain_id
                    // 为0或-1时不显示)
                    account = rs.getString(1);
                }
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        if (i > 2) {
            return null;
        } else {
            return account;
        }
    }

    /**
     * 取得系统设置
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getSystemOption(String domainId) {
        StringBuffer serverOption = new StringBuffer("111111110101");// 1111111111010010
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT DOMAIN_SERVEROPTION FROM ORG_DOMAIN where domain_id="
                            + domainId);
            if (rs.next()) {
                serverOption = new StringBuffer(rs.getString(1));
            }
            rs.close();

            rs = stmt
                    .executeQuery("select module_log,module_serial from security_log_module where domain_id="
                            + domainId);
            if (rs.next()) {
                serverOption.append(",").append(rs.getString(1)).append(
                        rs.getString(2));
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        return serverOption.toString();
    }

    public String getWorkLog(String domainId) {
        String workLog = "";
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT DOMAIN_WORKLOG FROM ORG_DOMAIN where domain_id="
                            + domainId);
            if (rs.next()) {
                workLog = rs.getString(1);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        return workLog;
    }

    /**
     * 获取 移动oa位置服务相关参数
     * 
     * @param domainId
     *            String
     * @return String[]
     */
    public String[] getMobileOA(String domainId) {
        String[] mobileOAList = new String[2];
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT MOBILEOA_STARTTIME,MOBILEOA_ENDTIME "
                            + " FROM ORG_DOMAIN where domain_id=" + domainId);
            if (rs.next()) {
                mobileOAList[0] = rs.getString(1);
                mobileOAList[1] = rs.getString(2);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        return mobileOAList;
    }

    /**
     * 保存 移动oa位置服务相关参数
     * 
     * @param startTime
     *            String
     * @param endTime
     *            String
     * @return boolean
     */
    public boolean updateMobileOASet(String startTime, String endTime) {
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat(
                "HHmm");
        java.text.ParsePosition pos = new java.text.ParsePosition(0);
        java.util.Date date_startTime = formatter.parse(startTime, pos);
        java.text.ParsePosition pos1 = new java.text.ParsePosition(0);
        java.util.Date date_endTime = formatter.parse(endTime, pos1);

        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            stmt.executeUpdate("update ORG_DOMAIN set MOBILEOA_STARTTIME="
                    + date_startTime.getTime() + ",MOBILEOA_ENDTIME="
                    + date_endTime.getTime());
            stmt.close();
            conn.close();
            result = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * 更新系统设置
     * 
     * @param domainId
     *            String 域ID
     * @param options
     *            String 系统设置标志如： 111111,0oa_index,1oa_information等
     * @param workLog
     * @param mailBoxSize
     *            默认邮箱得大小
     * @param netDiskSize
     *            默认网络硬盘得大小
     * @return boolean
     */
    public boolean updateSystemOption(String domainId, String options,
            String workLog, String mailBoxSize, String netDiskSize) {
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            if (options != null) {
                String[] optionArr = options.split(",");
                conn = ds.getConnection();
                stmt = conn.createStatement();
                stmt
                        .executeUpdate("update ORG_DOMAIN set DOMAIN_SERVEROPTION='"
                                + optionArr[0]
                                + "',DOMAIN_WORKLOG="
                                + workLog
                                + ",domain_mailboxsize="
                                + mailBoxSize
                                + ",domain_netdisksize="
                                + netDiskSize
                                + " where domain_id=" + domainId);
                String flag = "0";
                String serial = "";
                for (int i = 1; i < optionArr.length; i++) {
                    flag = String.valueOf(optionArr[i].charAt(0));
                    serial = optionArr[i].substring(1, optionArr[i].length());
                    System.out
                            .println("update security_log_module set module_log="
                                    + flag
                                    + " where  parent_serial='"
                                    + serial
                                    + "' and domain_id=" + domainId);
                    stmt
                            .executeUpdate("update security_log_module set module_log="
                                    + flag
                                    + " where  parent_serial='"
                                    + serial
                                    + "' and domain_id=" + domainId);
                }
                stmt.close();
                conn.close();
                result = true;
                init(domainId);
            }
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * date:2009-03-24 增加了一个参数，类型为字符数组，字符数组存储一些设置的值
     * 
     * @param domainId
     *            String
     * @param options
     *            String
     * @param workLog
     *            String
     * @param mailBoxSize
     *            String
     * @param netDiskSize
     *            String
     * @param attachLimit
     *            String
     * @param attachLimitSize
     *            String
     * @param setupValues
     *            String[]
     *            setupValues[0]html编辑器字体,setupValues[1]html编辑器字号,setupValues
     *            [2]邮件数量
     * @return boolean
     */
    public boolean updateSystemOption(String domainId, String options,
            String workLog, String mailBoxSize, String netDiskSize,
            String attachLimit, String attachLimitSize, String[] setupValues) {
        logger.debug("options:"+options);
        
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        String databaseType = com.whir.common.config.SystemCommon
                .getDatabaseType();// 判断数据库类型
        try {
            if (options != null) {
                String[] optionArr = options.split(",");
                conn = ds.getConnection();
                stmt = conn.createStatement();
                String updateSql = "update ORG_DOMAIN set DOMAIN_SERVEROPTION='"
                    + optionArr[0]
                    + "',DOMAIN_WORKLOG="
                    + workLog
                    + ",domain_mailboxsize="
                    + mailBoxSize
                    + ",domain_netdisksize="
                    + netDiskSize
                    + ",domain_attachlimit="
                    + attachLimit
                    + ",domain_attachLimitSize="
                    + attachLimitSize
                    + ",domain_fonttype='"
                    + setupValues[0]
                    + "',domain_wordsize='"
                    + setupValues[1]
                    + "',domain_mailnum='"
                    + setupValues[2]
                    + "',captcha='"
                    + setupValues[6]
                    + "',accountscase='"
                    + setupValues[7]
                    + "',wordlimitsize="
                    + setupValues[8]
                    + ",workflowtype='"
                    + setupValues[9]
                    + "', "
                    + "usemail='"
                    + setupValues[10]
                    + "',attach_preview='"
                    + setupValues[11]
                    + "',page_fontsize='"
                    + setupValues[12]
                    + "',use_forgot_password='"
                    + setupValues[13]
	                + "',oa_location='"
	                + setupValues[14]
	                + "',oa_wxlocation='"
	                + setupValues[15]
	                + "',yibo_flag='"
	                + setupValues[16]
                    + "',oa_infoorder='"
  	                + setupValues[17]
	                + "',oa_forumorder='"
	                + setupValues[18]
                    + "',oa_mailremind='"
  	                + setupValues[19]
                    + "',oa_PDF='"
	                + setupValues[20]
                    + "',evoWordRangeIds='"
                    + setupValues[21]
                    + "',evoWordRangeNames='"
                    + setupValues[22]
                    + "' where domain_id=" + domainId;
                if (databaseType.indexOf("sqlserver") >= 0) {// sqlserver数据库
                    updateSql = "update ORG_DOMAIN set DOMAIN_SERVEROPTION='"
                            + optionArr[0] + "',DOMAIN_WORKLOG=" + workLog
                            + ",domain_mailboxsize=" + mailBoxSize
                            + ",domain_netdisksize=" + netDiskSize
                            + ",domain_attachlimit=" + attachLimit
                            + ",domain_attachLimitSize=" + attachLimitSize
                            + ",domain_fonttype=N'" + setupValues[0]
                            + "',domain_wordsize=N'" + setupValues[1]
                            + "',domain_mailnum='" + setupValues[2] + ""
                            + "',captcha='"
                            + setupValues[6]
                            + "',accountscase='"
                            + setupValues[7]
                            + "',wordlimitsize="
                            + setupValues[8]
                            + ",workflowtype='"
                            + setupValues[9]
                            + "', "
                            + "usemail='"
                            + setupValues[10]
                            + "',attach_preview='"
                            + setupValues[11]
                            + "',page_fontsize='"
                            + setupValues[12]
                            + "',use_forgot_password='"
                            + setupValues[13]
                            + "',oa_location='"
                            + setupValues[14]
                            + "',oa_wxlocation='"
                            + setupValues[15]
	                        + "',yibo_flag='"
	                        + setupValues[16]
	                        + "',oa_infoorder='"
	        	            + setupValues[17]
	      	                + "',oa_forumorder='"
	      	                + setupValues[18]
	                        + "',oa_mailremind='"
	    	                + setupValues[19]
                            + "',oa_PDF='"
          	                + setupValues[20]
                            + "',evoWordRangeIds='"
                            + setupValues[21]
                            + "',evoWordRangeNames='"
                            + setupValues[22]
                            + "' where domain_id=" + domainId;

                }
                
                // stmt.executeUpdate("update ORG_DOMAIN set DOMAIN_SERVEROPTION='"+optionArr[0]+"',DOMAIN_WORKLOG="
                // + workLog +
                // ",domain_mailboxsize="+mailBoxSize+",domain_netdisksize="+netDiskSize+",domain_attachlimit="+attachLimit+",domain_attachLimitSize="+attachLimitSize+" where domain_id="
                // +domainId);
                stmt.executeUpdate(updateSql);
                
                String flag = "0";
                String serial = "";
                /*
                 * for(int i=1;i<optionArr.length;i++){
                 * flag=String.valueOf(optionArr[i].charAt(0));
                 * serial=optionArr[i].substring(1,optionArr[i].length());
                 * stmt.executeUpdate
                 * ("update security_log_module set module_log="
                 * +flag+" where  parent_serial='"
                 * +serial+"' and domain_id="+domainId); }
                 */

                // 历史日志
                // saveHistoryLogSetting(request, stmt, domainId);
                saveHistoryLogSetting(setupValues[3], setupValues[4],
                        setupValues[5], stmt, domainId);

                stmt.close();
                result = true;

                logger.debug("reinit...");
                init(domainId);
                SysSettings.getInstance(domainId, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * date:2009-03-24 保存日志设置
     * 
     * @param domainId
     *            String
     * @param options
     *            String
     * @return boolean
     */
    public boolean updateSystemLogOption(String domainId, String options) {
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            if (options != null) {
                String[] optionArr = options.split(",");
                conn = ds.getConnection();
                stmt = conn.createStatement();
                String flag = "0";
                String serial = "";
                for (int i = 0; i < optionArr.length; i++) {
                    flag = String.valueOf(optionArr[i].charAt(0));
                    serial = optionArr[i].substring(1, optionArr[i].length());
                    stmt
                            .executeUpdate("update security_log_module set module_log="
                                    + flag
                                    + " where  parent_serial='"
                                    + serial
                                    + "' and domain_id=" + domainId);
                }
                stmt.close();
                conn.close();
                result = true;
                // init(domainId);
            }
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * date:2009-03-25 更新历史邮件设置
     * 
     * @param historyMailValues
     *            String[]
     *            historyMailValues[0]历史邮件开关，0表示未启用，1表示启用；historyMailValues
     *            [1]保存天数，historyMailValues[2]运行时间
     * @return boolean
     */
    public boolean updateHistoryMailSet(String[] historyMailValues) {
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            if (historyMailValues != null && historyMailValues.length == 3) {
                conn = ds.getConnection();
                stmt = conn.createStatement();
                stmt.executeUpdate("update oa_mail_h_set set MAILSET_USE="
                        + Integer.parseInt(historyMailValues[0])
                        + ",mailset_day="
                        + Integer.parseInt(historyMailValues[1])
                        + ",mailset_time="
                        + Integer.parseInt(historyMailValues[2]));
                stmt.close();
                conn.close();
                result = true;
            }
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }

    /**
     * date:2009-03-25 获得历史邮件设置信息，返回historyMailInfo[3]数组
     * historyMailInfo[0]历史邮件开关
     * ，0表示未启用，1表示启用；historyMailInfo[1]保存天数，historyMailInfo[2]运行时间
     * 
     * @return String[]
     */
    public String[] getHistoryMailSet() {
        String[] historyMailInfo = new String[3];
        historyMailInfo[0] = "0";
        historyMailInfo[1] = "60";
        historyMailInfo[2] = "4";
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT MAILSET_USE,mailset_day,mailset_time FROM oa_mail_h_set ");
            if (rs.next()) {
                historyMailInfo[0] = rs.getString(1);
                historyMailInfo[1] = rs.getString(2);
                historyMailInfo[2] = rs.getString(3);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        } finally {
            return historyMailInfo;
        }
    }

    /**
     * 取默认得邮箱大小
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getMailBoxSize(String domainId) {
        String mailBoxSize = "";
        if (sysSetupMap == null || sysSetupMap.get(domainId) == null
                || ((Map) sysSetupMap.get(domainId)).get("mailBoxSize") == null) {
            init(domainId);
        }
        mailBoxSize = ((Map) sysSetupMap.get(domainId)).get("mailBoxSize")
                .toString();
        return mailBoxSize;
    }

    /**
     * 取默认得网络硬盘大小
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getNetDiskSize(String domainId) {
        String mailBoxSize = "";
        if (sysSetupMap == null || sysSetupMap.get(domainId) == null
                || ((Map) sysSetupMap.get(domainId)).get("netDiskSize") == null) {
            init(domainId);
        }
        mailBoxSize = ((Map) sysSetupMap.get(domainId)).get("netDiskSize")
                .toString();
        return mailBoxSize;
    }

    /* START----------------NEW[何炜]20051102[NUM:2]------------------- */
    public boolean hasMsg(String domainId) {
        boolean hasMsg = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("短信开通") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("短信开通").toString()
                    .equals("1")) {
                hasMsg = true;
            }
        }
        return hasMsg;
    }

    /* END------------------NEW[何炜]20051102[NUM:2]------------------- */

    /* START----------------NEW[何炜]20051206[NUM:2]------------------- */
    public boolean hasEzWorkFlow(String domainId) {
        boolean hasEzWorkFlow = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("图形工作流") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("图形工作流").toString()
                    .equals("1")) {
                hasEzWorkFlow = true;
            }
        }
        return hasEzWorkFlow;
    }

    /* END------------------NEW[何炜]20051206[NUM:2]------------------- */

    public boolean hasHandSign(String domainId) {
        boolean hasHandSign = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("使用手写意见") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("使用手写意见").toString()
                    .equals("1")) {
                hasHandSign = true;
            }
        }
        return hasHandSign;
    }

    public boolean hasWordEdit(String domainId) {
        boolean hasWordEdit = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("WORD编辑") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("WORD编辑").toString()
                    .equals("1")) {
                hasWordEdit = true;
            }
        }

        return hasWordEdit;
    }

    public boolean hasRtxOnline(String domainId) {
        boolean hasRtxOnline = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("RTX在线感知") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("RTX在线感知").toString()
                    .equals("1")) {
                hasRtxOnline = true;
            }
        }
        return hasRtxOnline;
    }

    public boolean rtxIsUsed() {
        String useRTX = "0";
        try {
            String configFile = System.getProperty("user.dir")
                    + "/config/whconfig.xml";
            FileInputStream configFileInputStream = new FileInputStream(
                    new File(configFile)); // 建立配置文件的输入流
            /**
             * 使用JDOM进行解析
             */
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(configFileInputStream);
            Element root = doc.getRootElement();
            Element node = root.getChild("RtxServer");
            useRTX = node.getAttributeValue("use");
        } catch (Exception ex) {
            // System.out.println("取rtx服务器域名出错！");
            // 取得设置的SDK服务的IP地址
        }
        if (useRTX.equals("1")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取附件限制大小
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getAttachLimit(String domainId) {
        String attachLimit = "";
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            stmt = conn.createStatement();
            rs = stmt
                    .executeQuery("SELECT domain_attachLimit,domain_attachLimitSize FROM ORG_DOMAIN where domain_id="
                            + domainId);
            if (rs.next()) {
                attachLimit = rs.getString(1) + rs.getString(2);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        return attachLimit;
    }

    /**
     * 邮件群发是否控制 控制：true；不控制：false Date:2008-11-13
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean hasEmailSend(String domainId) {
        boolean hasEmailSend = true;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("邮件群发") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("邮件群发").toString()
                    .equals("0")) {
                hasEmailSend = false;
            }
        }
        return hasEmailSend;
    }

    /**
     * 内部联系人是否控制 控制：true；不控制：false Date:2008-11-13
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean hasInnerPerson(String domainId) {
        boolean hasInnerPerson = true;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("内部联系人") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("内部联系人").toString()
                    .equals("0")) {
                hasInnerPerson = false;
            }
        }
        return hasInnerPerson;
    }

    /**
     * 附件是否加密存储 加密：true；不加密：false；默认为不加密
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean isAttachEncrypt(String domainId) {
        boolean hasAttachEncrypt = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("加密存储") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("加密存储").toString()
                    .equals("1")) {
                hasAttachEncrypt = true;
            }
        }
        return hasAttachEncrypt;
    }

    /**
     * 是否ftp上传方式
     * 
     * @param sysMap
     *            Map
     * @return boolean
     */
    public boolean isFtp(Map sysMap) {
        boolean hasFtp = false;
        if (sysMap != null && sysMap.get("附件上传") != null
                && sysMap.get("附件上传").toString().equals("0")) {
            hasFtp = true;
        }
        return hasFtp;
    }

    /**
     * 是否使用移动OA位置服务标志
     * 
     * @param sysMap
     *            Map
     * @return boolean true-使用 false-不使用
     */
    public boolean isMobilePositionService(Map sysMap) {
        boolean isMobilePositionService = false;
        if (sysMap != null && sysMap.get("isMobilePositionService") != null
                && sysMap.get("isMobilePositionService").toString().equals("1")) {
            isMobilePositionService = true;
        }
        return isMobilePositionService;
    }

    /**
     * 根据domainId获取是否使用移动OA位置服务标志
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean isMobilePositionService(String domainId) {
        boolean isMobilePositionService = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null) {
            Map sysMap = (Map) sysSetupMap.get(domainId);
            if (sysMap != null && sysMap.get("isMobilePositionService") != null) {
                if (sysMap.get("isMobilePositionService").toString()
                        .equals("1")) {
                    isMobilePositionService = true;
                }
            }
        }
        return isMobilePositionService;
    }

    /**
     * date:2009-03-24 获得是否允许同一用户同时登录，返回true表示允许，返回false表示不允许
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean isAllowUserLogin(String domainId) {
        boolean isAllow = true;// 默认允许同一个人同时登录
        try {
            if (sysSetupMap != null
                    && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("userLoginType") != null) {
                if (((Map) sysSetupMap.get(domainId)).get("userLoginType")
                        .toString().equals("0")) {
                    isAllow = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("isAllowUserLogin error");
        } finally {
            return isAllow;
        }
    }

    /**
     * date:2009-03-24 获得Html编辑器系统设置中的字体，如果返回的是""则没有设置字体样式
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getHtmlFontType(String domainId) {
        String reFontType = "";
        try {
            if (sysSetupMap != null
                    && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("HtmlFontType") != null) {
                reFontType = ((Map) sysSetupMap.get(domainId)).get(
                        "HtmlFontType").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getHtmlFontType error");
        } finally {
            return reFontType;
        }
    }

    /**
     * date:2009-03-24 获得Html编辑器系统设置中的字号，如果返回的是""则没有设置字号
     * 
     * @param domainId
     *            String
     * @return String
     */
    public String getHtmlWordSize(String domainId) {
        String reWordSize = "";
        try {
            if (sysSetupMap != null
                    && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("HtmlWordSize") != null) {
                reWordSize = ((Map) sysSetupMap.get(domainId)).get(
                        "HtmlWordSize").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getHtmlWordSize error");
        } finally {
            return reWordSize;
        }
    }

    /**
     * date:2009-03-24 获得系统设置中设置的邮件数量
     * 
     * @param domainId
     *            String
     * @return int
     */
    public int getInnerMailNum(String domainId) {
        int reMailNum = 10000;// 默认为10000封
        try {
            if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("mailNum") != null) {
                reMailNum = Integer.parseInt(((Map) sysSetupMap.get(domainId))
                        .get("mailNum").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getInnerMailNum error");
        } finally {
            return reMailNum;
        }
    }

    /**
     * date:2009-05-18 获得是否允许信息未查看用户，返回true表示允许，返回false表示不允许
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean isInfoNotView(String domainId) {
        boolean isAllow = false;// 默认不允许
        try {
            if (sysSetupMap != null
                    && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("infoNotView") != null) {
                if (((Map) sysSetupMap.get(domainId)).get("infoNotView")
                        .toString().equals("1")) {
                    isAllow = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("isInfoNotView error");
        } finally {
            return isAllow;
        }
    }

    /**
     * date:2009-12-30 是否使用移动办公信息推送，返回true表示使用，返回false表示不使用
     * 
     * @param domainId
     *            String
     * @return boolean
     */
    public boolean isMobilePush(String domainId) {
        boolean isAllow = false;// 默认不使用
        try {
            if (sysSetupMap != null
                    && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get("isMobilePush") != null) {
                if (((Map) sysSetupMap.get(domainId)).get("isMobilePush")
                        .toString().equals("1")) {
                    isAllow = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("isMobilePush error");
        } finally {
            return isAllow;
        }
    }

    public boolean saveData(String val) {
        if (val == null || "".equals(val))
            return false;
        DbOpt dbopt = new DbOpt();
        Connection conn = null;
        PreparedStatement ps = null;
        String sql = "update oa_wf_overdate set status = ? where id=1";
        try {
            conn = dbopt.getConnection();
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            ps.setString(1, val);
            ps.executeUpdate();
            dbopt.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbopt.rollback();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (dbopt != null)
                    dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public String getStatus() {
        String val = "";
        DbOpt dbopt = new DbOpt();
        try {
            val = dbopt
                    .executeQueryToStr("select status from oa_wf_overdate where id=1");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dbopt != null)
                    dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    public void saveHistoryLogSetting(HttpServletRequest request,
            Statement stmt, String domainId) throws Exception {
        String historyLog = request.getParameter("historyLog");// 0-不启用 1-启用
        String logSaveDays = request.getParameter("logSaveDays");
        String logRunHour = request.getParameter("logRunHour");

        stmt.executeUpdate("update ORG_DOMAIN set ishistorylog='" + historyLog
                + "', historylog_days=" + logSaveDays
                + ", historylog_runhours=" + logRunHour + " where domain_id="
                + domainId);
    }

    /**
     * 历史日志选项设置
     * 
     * @param historyLog
     *            0-不启用 1-启用
     * @param logSaveDays
     * @param logRunHour
     * @param stmt
     * @param domainId
     * @throws Exception
     */
    public void saveHistoryLogSetting(String historyLog, String logSaveDays,
            String logRunHour, Statement stmt, String domainId)
            throws Exception {
        stmt.executeUpdate("update ORG_DOMAIN set ishistorylog='" + historyLog
                + "', historylog_days=" + logSaveDays
                + ", historylog_runhours=" + logRunHour + " where domain_id="
                + domainId);
    }

    public String getSysValueByName(String name, String domainId) {
        String result = "";
        try {
            if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                    && ((Map) sysSetupMap.get(domainId)).get(name) != null) {
                result = ((Map) sysSetupMap.get(domainId)).get(name).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getSysValueByName error");
        } finally {
            return result;
        }
    }
    //2015-11-26--xiehd 获取EVOword
    public boolean hasEVOWordEdit(String domainId) {
        boolean hasWordEdit = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("evoword") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("evoword").toString()
                    .equals("1")) {
                hasWordEdit = true;
            }
        }

        return hasWordEdit;
    }
    
    //2015-11-30--xiehd 获取oa_location
    public boolean hasLocation(String domainId) {
        boolean hasLocation = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("location") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("location").toString()
                    .equals("1")) {
                hasLocation = true;
            }
        }

        return hasLocation;
    }
    
    //2015-11-30--xiehd 获取oa_wxlocation
    public boolean hasWXLocation(String domainId) {
        boolean hasWXLocation = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("wxlocation") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("wxlocation").toString()
                    .equals("1")) {
            	hasWXLocation = true;
            }
        }

        return hasWXLocation;
    }
    
  //2015-12-09--xiehd 获取yibo_flag
    public boolean hasYibo_flag(String domainId) {
        boolean yibo_flag = false;
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("yibo_flag") != null) {
            if (((Map) sysSetupMap.get(domainId)).get("yibo_flag").toString()
                    .equals("1")) {
            	yibo_flag = true;
            }
        }

        return yibo_flag;
    }
  //2016-03-02--xiehd 获取oa_vkey
    public String getOa_vkey(String domainId) {
    	String oa_vkey = "";
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("oa_vkey") != null) {
        	oa_vkey = ((Map) sysSetupMap.get(domainId)).get("oa_vkey").toString();
        }
       
        return oa_vkey;
    }
    
  //2016-05-09--xiehd 获取oa_vkey,如果表中Oa_vkey为空则取配置文件ServiceParse中的值
    public String getVkey(String domainId) {
    	String oa_vkey = "";
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("oa_vkey") != null) {
        	oa_vkey = ((Map) sysSetupMap.get(domainId)).get("oa_vkey").toString();
        }
        //如果表中Oa_vkey为空则取配置文件ServiceParse中的值
        if("".equals(oa_vkey)){
//        	String web_inf = request.getSession().getServletContext()
//    		.getRealPath("/WEB-INF/");
        	try {
        		  String path=Thread.currentThread().getContextClassLoader().getResource("").toString();  
        	        //path=path.replace('/', '\\'); // 将/换成\  
        	        path=path.replace("file:", ""); //去掉file:  
        	        path=path.replace("classes\\", ""); //去掉class\  
        	        path=path.substring(1); //去掉第一个\,如 \D:\JavaWeb...  
        	        path+="ServiceParse.xml"; 
	    		//File file = new java.io.File(web_inf + "/classes/ServiceParse.xml");
    	        if(System.getProperty ("os.name").indexOf("Windows")>-1){
    	        	path=path.substring(1); //去掉第一个\,如 \D:\JavaWeb... 
    	        }
        	    File file = new java.io.File(path);
				BufferedReader br = new BufferedReader(new InputStreamReader(
							new FileInputStream(file)));
	    		List list2 = new ArrayList();
	    		SAXBuilder builder = new SAXBuilder();
	    		Document doc = (Document) builder.build(file);
	    		Element foo = doc.getRootElement();
	    		oa_vkey = foo.getChildText("vkey");
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return oa_vkey;
    }
    /**
     *更改webservice密码 by xiehd 2016-03-02
     * 
     * @param domainId
     *            String
     * @param webservicePassword
     *            String
     
     * @return boolean
     */
    public boolean updateOA_vkey(String domainId, String webservicePassword) {
        
        boolean result = false;
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        java.sql.ResultSet rs = null;
      
        try {
            if (webservicePassword != null) {
                conn = ds.getConnection();
                stmt = conn.createStatement();
                String updateSql = "update ORG_DOMAIN set oa_vkey=? where domain_id=?";
                PreparedStatement  pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1,webservicePassword);
                pstmt.setString(2,domainId);
                pstmt.executeUpdate();
                stmt.close();
                result = true;
                logger.debug("reinit...");
                init(domainId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }
    /**
     *修改ServiceParse.xml中的<vkey>值
     * @param webservicePassword
     * @return 
     * @throws IOException 
     * @throws JDOMException 
     */
    public boolean saveVkey(String domainId ,String webservicePassword) throws JDOMException, IOException{
    	String web_inf = request.getSession().getServletContext()
		.getRealPath("/WEB-INF/");
		File file = new java.io.File(web_inf + "/classes/ServiceParse.xml");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		List list2 = new ArrayList();
		SAXBuilder builder = new SAXBuilder();
		Document doc = (Document) builder.build(file);
		Element foo = doc.getRootElement();
		String vkey = foo.getChildText("vkey");
		if(vkey!=null){
			 while (true) {
					String str = br.readLine();
					// 读取文件当中的一行
					if (str == null)
						break;
					// 如果读取的是空，也就是文件读取结束 跳出循环
					if (str.indexOf(vkey) != -1) {
						// 修改密码
						str = str.replace(vkey, webservicePassword);
					}
					list2.add(str);
					// 把修改之后的str放到集合当中
				}
				br.close();
				PrintWriter pw = new PrintWriter(file);
				// 建立一个输出流，把东西写入文件
				for (int i = 0; i < list2.size(); i++) {
					String str = (String) list2.get(i);
					// 从集合当中取出字符串
					pw.println(str);
					// 把该字符串写入文件当中
				}
				pw.close();
		    	return true;
		 }else{
			 logger.debug("vkey节点不存在！");
			 return false;
		 }
		 
    }
    
  //2016-04-07--xiehd 获取信息列表排序 0-发布日期，1-修改日期
    public String getOa_infoorder(String domainId) {
    	String oa_infoorder = "";
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("oa_infoorder") != null) {
        	oa_infoorder = ((Map) sysSetupMap.get(domainId)).get("oa_infoorder").toString();
        }
        return oa_infoorder;
    }
  //2016-04-07--xiehd 获取信息列表排序 0-发帖时间，1-回帖时间
    public String getOa_forumorder(String domainId) {
    	String oa_forumorder = "";
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("oa_forumorder") != null) {
        	oa_forumorder = ((Map) sysSetupMap.get(domainId)).get("oa_forumorder").toString();
        }
        return oa_forumorder;
    }
    //2016-04-08--xiehd 获取是否开启邮件提醒 0-无提醒，1-有提醒
    public String getOa_mailremind(String domainId) {
    	String oa_mailremind = "";
        if (sysSetupMap != null && sysSetupMap.get(domainId) != null
                && ((Map) sysSetupMap.get(domainId)).get("oa_mailremind") != null) {
        	oa_mailremind = ((Map) sysSetupMap.get(domainId)).get("oa_mailremind").toString();
        }
        return oa_mailremind;
      
    }
    /**
     * 获取evo端word编辑范围
     * @param domainId
     * @return
     */
    public String getEvoWordRangeIds(String domainId) {
        StringBuffer evoWordRangeIds = new StringBuffer("");
        javax.sql.DataSource ds = new com.whir.common.util.DataSourceBase()
                .getDataSource();
        java.sql.Connection conn = null;
        java.sql.PreparedStatement pt = null;
        java.sql.ResultSet rs = null;
        try {
            conn = ds.getConnection();
            pt = conn.prepareStatement("SELECT evoWordRangeIds FROM ORG_DOMAIN where domain_id=?");
            pt.setString(1, domainId);
            rs = pt.executeQuery();
            if (rs.next()) {
                evoWordRangeIds = new StringBuffer(rs.getString(1));
            }
            rs.close();

            pt.close();
            conn.close();
        } catch (Exception ex) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {

                }
            }
        }
        return evoWordRangeIds.toString();
    }
    /**
     * 判断用户是否有evo端word编辑范围的权限
     * @param domainId
     * @param userId
     * @return
     */
    public boolean hasEvoWordRange(String domainId, String userId) {
        boolean f = false;
        SysSetupReader sysRed = SysSetupReader.getInstance();
        String options = sysRed.getSystemOption(domainId);

        if (options.charAt(16) == '1') {//使用
            //判断范围
            String evoWordRangeIds = getEvoWordRangeIds(domainId);
            if (evoWordRangeIds != null && "".equals(evoWordRangeIds)) {//为空时默认所有人都有权限
                f = true;
            } else if (evoWordRangeIds != null
                    && evoWordRangeIds.indexOf("$" + userId + "$") > -1) {
                f = true;
            }
        }

        return f;
    }
}
