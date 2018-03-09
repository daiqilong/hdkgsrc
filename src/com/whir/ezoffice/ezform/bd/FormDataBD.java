package com.whir.ezoffice.ezform.bd;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.ezoffice.customdb.customdb.bd.AutoCode;
import com.whir.ezoffice.ezform.ui.UIBD;
import com.whir.ezoffice.ezform.util.FormContants;
import com.whir.ezoffice.ezform.util.FormHelper;
import com.whir.ezoffice.hrm.common.CommonUtils;
import com.whir.ezoffice.relation.bd.RelationBD;

/**
 * 表单保存BD
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author w3
 * @version 10.3.0.0
 */
public class FormDataBD {

    private static Logger logger = Logger.getLogger(FormDataBD.class.
        getName());

    public static final String TYPE_INT = "i"; //整型
    public static final String TYPE_LONG = "l"; //长整形
    public static final String TYPE_FLOAT = "f"; //浮点型
    public static final String TYPE_STRING = "s"; //字符

    //private String TYPE_DATE = "d";
    
    private String DM_SQL = "SYS.SYSCOLUMNS a, SYS.SYSOBJECTS b";//SYSDBA.SYSCOLUMNS a,SYSDBA.SYSTABLES b

    private String _userId = "";
    private String _userName = "";
    private String _orgId = "";
    private String _groupId = "";
    private String _orgSimpleName = "";

    public FormDataBD() {
    }

    /**
     * 设置
     * @param request HttpServletRequest
     */
    private void initUserData(HttpServletRequest request){
        //批量发起人
        String batchSendUserId = (String)request.getAttribute("temp_batchSendUserId");
        if(FormHelper.isNotEmpty(batchSendUserId)){
            UIBD uibd = new UIBD();
            String[][] userInfo = uibd.getUserInfoByUserId(batchSendUserId);
            _userId = batchSendUserId;
            _userName = userInfo[0][1];
            _orgId = userInfo[0][2];
            _groupId = "";
            _orgSimpleName = userInfo[0][3]!=null?userInfo[0][3]:"";

        }else{
            HttpSession session = request.getSession(true);
            _userId = session.getAttribute("userId") + "";
            _userName = session.getAttribute("userName") + "";
            _orgId = session.getAttribute("orgId") + "";
            _groupId = session.getAttribute("groupId")!=null?session.getAttribute("groupId").toString():"";
            _orgSimpleName = session.getAttribute("orgSimpleName")!=null?session.getAttribute("orgSimpleName").toString():"";
        }
    }

    /**
     * 保存表单数据
     * @param request HttpServletRequest
     * @param status Integer
     * @return String
     */
    public String save(HttpServletRequest request, Integer status) {
        DbOpt dbopt = null;
        String infoId = "";
        try {
            dbopt = new DbOpt();

            UIBD uibd = new UIBD();
            String formId = request.getParameter("formId");

            //------------------------------------------------------------------
            //批量发起
            initUserData(request);
            //------------------------------------------------------------------

            //保存表单中主表记录
            infoId = this.saveData(formId, request, dbopt, status);
            if(infoId.equals("-2")){
            	return infoId;
            }
            
            //保存子表关联
            if(infoId != null && infoId.length() >= 1) { //是否保存成功

                //读取表单关联字表数组
                String[] tables = uibd.getForeignTables(formId);
                if(tables != null && tables.length > 0) {
                    //循环遍历子表
                    for(int i = 0, tlen=tables.length; i < tlen; i++) {
                        String tableName = tables[i];
                        if(tableName == null || tableName.trim().length() < 1)continue;

                        //取得子表关联到表单中的字段及其属性数组
                        String[][] field = uibd.getForeignFieldsByTableName(
                            formId,
                            tableName);
                        if(field != null && field.length > 0) {
                            String forInfoIds = "";
                            //取得记录的数量
                            String[] types = null;//request.getParameterValues(field[0][3] + "_type");
                            for(int k=0,m=field.length; k<m; k++){
                                types = request.getParameterValues(field[k][3] + "_type");
                                if(types != null && types.length > 0) {
                                    break;
                                }
                            }
                            
                            if(types != null && types.length > 0) {
                                logger.debug("tableName:" + tableName + ",types.length:"+types.length);
                                
                                //start 追加自定义表中缺失的字段，主要用于升级历史数据表
                                reCreateTableFieldForSub("oracle", tableName,
                                    dbopt);
                                //end 追加自定义表中缺失的字段，主要用于升级历史数据表

                                //遍历每一条记录并保存
                                for(int j = 0, len=types.length; j < len; j++) {
                                    String subDataId = 
                                        this.saveForeignData(formId, request,
                                                tableName,
                                                field, j, dbopt, infoId, status);
                                    forInfoIds += subDataId + ",";
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return infoId;
    }

    /**
     * 保存页面关联数据
     * @param request HttpServletRequest
     * @param pageId String：自定义表单的ID
     * @return Boolean
     * 保存自定义表单数据，实现逻辑：
     *     1、调用saveData保存表单主表数据
     *     2、如果主表数据保存失败，返回；成功则继续3
     *     3、读取表单关联的子表，循环遍历子表，
     *     4、读取字表记录数量，循环调用saveForeignData保存每条记录
     */

    public String saveData(String formId, HttpServletRequest request,
                           DbOpt dbopt, Integer status) {
        if(formId == null || formId.length() < 1) {
            logger.debug("表单ID不能为空。");
            return "";
        }

        UIBD uibd = new UIBD();
        //读取表单关联字段及其属性数组
        String[][] fieldStr = uibd.getFieldInfo(formId);

        if(fieldStr == null || fieldStr.length < 1) {
            
            logger.debug("表单未关联字段。");
            //return "";
            fieldStr = new String[0][0];
        }

        //读取表单关联的数据表名称
        String[][] tableIdName = uibd.getTableIDAndName(formId);
        String tableId = tableIdName[0][0]; //自定义数据表ID
        String table = tableIdName[0][1]; //自定义数据表名

        if(table == null || table.trim().length() < 1) {
            logger.debug("表单关联主表不能为空。");
            return "";
        }

        String p_wf_openType = request.getParameter("p_wf_openType");
        logger.debug("p_wf_openType:"+p_wf_openType);

        //HttpSession session = request.getSession(true);
        Boolean success = Boolean.TRUE;

        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String yyyyMMdd = sdf2.format(now);

        String infoId = ""; //新增信息的ID
        String DB_TYPE = dbopt.dbtype;
        Connection conn = null;
        PreparedStatement ps = null;
        String prepareSQL = "";
        try {
            conn = dbopt.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            String databaseName = dbmd.getDatabaseProductName();

            //start 追加自定义表中缺失的字段，主要用于升级历史数据表
            //reCreateTableFieldForMain(databaseName, table, dbopt);
            //end 追加自定义表中缺失的字段，主要用于升级历史数据表

            //数据保存SQL的字段部分
            String sqlHead = "insert into " + table + "(";
            //数据保存SQL的值部分
            String sqlPrepared = " values(";

            infoId = getPrimaryKeyId(databaseName, dbopt);

            List upList = new ArrayList();
            List dataList = new ArrayList();

            //------------------------------------------------------
            sqlHead += table + "_id," + table + "_owner," + table +
                "_org," + table + "_group," + table + "_workstatus,";
            sqlPrepared += "?,?,?,?,?,";

            dataList.add(new String[] {TYPE_LONG, infoId});
            dataList.add(new String[] {TYPE_STRING, _userId});
            dataList.add(new String[] {TYPE_STRING, _orgId});
            dataList.add(new String[] {TYPE_STRING, _groupId});
            dataList.add(new String[] {TYPE_INT, status + ""});
            //------------------------------------------------------

            if(DB_TYPE.equals("oracle")) {
                sqlHead += table + "_date,";
                sqlPrepared += "?,";
                dataList.add(new String[] {TYPE_STRING, yyyyMMdd});
            }

            //遍历表单关联的所有字段，读取字段的值，生成SQL
            for(int i = 0, flen=fieldStr.length; i < flen; i++) {
                String f_field = fieldStr[i][0];
                String f_show  = fieldStr[i][2];
                String fieldType = request.getParameter(f_field +
                    "_type");
                //String fieldSize = request.getParameter(f_field + "_size");

                /*if(i == 0) {
                    //------------------------------------------------------
                    sqlHead += table + "_id," + table + "_owner," + table +
                        "_org," + table + "_group," + table + "_workstatus,";
                    sqlPrepared += "?,?,?,?,?,";

                    dataList.add(new String[] {TYPE_LONG, infoId});
                    dataList.add(new String[] {TYPE_STRING, _userId});
                    dataList.add(new String[] {TYPE_STRING, _orgId});
                    dataList.add(new String[] {TYPE_STRING, _groupId});
                    dataList.add(new String[] {TYPE_INT, status+""});
                    //------------------------------------------------------

                    if(DB_TYPE.equals("oracle")) {
                        sqlHead += table + "_date,";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, yyyyMMdd});
                    }
                }*/

                if(fieldType == null)continue;

                //数值类型字段
                if("number".equals(fieldType)) {
                    String _rnd_name = request.getParameter(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name;
                    if(FormHelper.isEmpty(_rnd_name)) {
                        param_name = f_field;
                    }

                    String f_field_val = request.getParameter(param_name);
                    if(f_field_val != null &&
                       f_field_val.trim().length() >
                       0) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_FLOAT,
                                     f_field_val.trim().replaceAll(",", "")});
                        //------------------------------------------------------
                    }else{
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_FLOAT, null});
                        //------------------------------------------------------
                    }
                } else

                //字符/文本类型字段
                if("varchar".equals(fieldType)) {
                    String _rnd_name = request.getParameter(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name;
                    if(FormHelper.isEmpty(_rnd_name)) {
                        param_name = f_field;
                    }

                    String f_field_val = request.getParameter(param_name);

                    //----------------------------------------------------------
                    //批量发起保存
                    //----------------------------------------------------------
                    if("708".equals(f_show)){
                        String batchSendUserIds = request.getParameter("batchSendUserIds");
                        if(batchSendUserIds!=null){
                            if(batchSendUserIds.equals(f_field+"_Id")){
                                f_field_val = _userName;
                            }
                        }
                    }
                    //----------------------------------------------------------

                    if(f_field_val != null) {
                        sqlHead += f_field + ",";

                        String val = f_field_val.replaceAll("'", "\\\\''");
                        if("111".equals(f_show) && !"".equals(val)) { //自动编号字段唯一性验证
                        	/*String aaa = dbopt.dbtype.indexOf("sqlserver")>=0?"N":"";
                        	com.whir.common.util.DataSourceBase ds = new com.whir.common.util.DataSourceBase();
                        	Connection conn1 = null;
                        	java.sql.Statement stmt = null;
                    		conn1 = ds.getDataSource().getConnection();
                    		stmt = conn.createStatement();
                    		java.sql.ResultSet rs=null;
                    		val = val.trim();
                    		if(val==null || val.equals("") || val.equals("null")){
                    			val="为空时不判断";
                    		}
                    		try{
	                    		String sql="select count(*) from "+table+" where "+f_field+"="+aaa+"'"+val+"' ";//and ("+tableName+"_WORKSTATUS!=-1 or "+tableName+"_WORKSTATUS is null)";
	                    		if(infoId!=null && !"null".equals(infoId) && !"".equals(infoId)){
	                    			sql+=" and "+table+"_id<>"+infoId;
	                    		}
	                    		rs=stmt.executeQuery(sql);
	                    		if(rs.next()){
	                    			String cnt = rs.getString(1);
	                    			if(!"0".equals(cnt)){
	                    				//自动编号不唯一
	                    				infoId="-2";
	                    				return "";
	                    			}
	                    		}
                    		}catch(Exception err){
                    			err.printStackTrace();
                    		}finally{
                    			rs.close();
	                    		stmt.close();
	                    	    if(conn1!=null){
	                    			try{
	                    				conn1.close();
	                    			}catch(Exception err){
	                    				err.printStackTrace();
	                    			}
	                    		}
                    		}*/
                    		
                            if(!"reStart".equals(p_wf_openType)){   
                                val = new AutoCode().getCodeVal(val, fieldStr, table, i, dbopt);
                            }

                            //------------------------------------------------------
                            sqlPrepared += "?,";
                            dataList.add(new String[] {TYPE_STRING, val});
                            //------------------------------------------------------
                        } else {

                            //------------------------------------------------------
                            sqlPrepared += "?,";
                            dataList.add(new String[] {TYPE_STRING, f_field_val});
                            //------------------------------------------------------
                        }
                    }
                } else

                //上传文件类型字段
                if("file".equals(fieldType)) {
                    String _rnd_name = request.getParameter(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name;

                    String param_saveName = request.getParameter(param_name +
                        "_saveName");
                    if(param_saveName != null) {
                        String fileName = request.getParameter(param_name +
                            "_fileName");
                        String saveName = param_saveName; //saveName中“aaaaaa”表示无效文件，在fileName中对应位置的文件名也是无效的

                        String[] fileNameArr = splitStr(fileName, "\\|");
                        String[] saveNameArr = splitStr(saveName, "\\|");
                        boolean flag = true;
                        String saveFileTemp = "";
                        String fileFileTemp = "";
                        for(int k = 0; k < saveNameArr.length; k++) {
                            if((saveNameArr[k] == null ||
                                saveNameArr[k].equals("aaaaaa") ||
                                saveNameArr[k].trim().equals(""))) {
                                continue;
                            } else {
                                flag = false;
                                saveFileTemp += saveNameArr[k] + ",";
                                fileFileTemp += fileNameArr[k] + ",";
                            }
                        }
                        if(!flag) {
                            //------------------------------------------------------
                            sqlHead += f_field + ",";
                            sqlPrepared += "?,";
                            dataList.add(new String[] {TYPE_STRING,
                                         saveFileTemp + ";" + fileFileTemp});
                            //------------------------------------------------------
                        }
                    }
                } else

                //多选类型
                if("combox".equals(fieldType)) {
                    String _rnd_name = request.getParameter(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name;

                    String[] temp = request.getParameterValues(param_name);
                    if(temp != null && temp.length > 0) {
                        String t = "";
                        for(int j = 0; j < temp.length; j++) {
                            if(!CommonUtils.isEmpty(temp[j])){
                                t += temp[j] + ",";
                            }
                        }

                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, t});
                        //------------------------------------------------------
                    }
                } else

                //单选人，多选人，单选组织，多选组织
                if("personorg".equals(fieldType)) {
                    String _rnd_name = request.getParameter(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name;

                    String param_Name = request.getParameter(param_name +
                        "_Name");
                    if(param_Name != null && !param_Name.equals("") &&
                       param_Name.length() > 0) {
                    	//20160920删除单选多选最后逗号
                    	if(param_Name.lastIndexOf(",")==param_Name.length()-1){
                    		param_Name = param_Name.substring(0, param_Name.length()-1);
                    	}
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_Name +
                                     ";" +
                                     request.getParameter(param_name + "_Id")});
                        //------------------------------------------------------
                    }
                } else

                //登录人姓名和日期
                if("loginPersonDate".equals(fieldType)) {
                    String param_loginUser = request.getParameter(f_field +
                        "_loginUser");
                    if(param_loginUser != null &&
                       param_loginUser.length() > 0) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_loginUser +
                                     " " +
                                     request.getParameter(f_field +
                            "_loginDate")});
                        //------------------------------------------------------
                    }
                } else

                //时间 日期类型字段
                if("datetime".equals(fieldType)) {
                    String f_field_val = request.getParameter(f_field);
                    if(f_field_val != null &&
                       f_field_val.trim().length() > 0) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     f_field_val + " " +
                                     request.getParameter(f_field +
                            "hours") + ":" +
                                     request.getParameter(f_field +
                            "minutes")});
                        //------------------------------------------------------
                    }
                }
            }

            int result = 0;
            if(infoId != null) {
                prepareSQL = sqlHead.substring(0, sqlHead.length() - 1) +
                    ") " + sqlPrepared.substring(0, sqlPrepared.length() - 1) +
                    ")";

                //执行SQL 保存数据
                result = executeUpdateSql(prepareSQL, dataList, conn, ps);

                /*
                                 //执行SQL保存计算字段的值
                 updateComputeField(infoId, tableId, table, dbopt, "0", request);

                                 //帐号
                 String[][] updateArr = dbopt.executeQueryToStrArr2(
                    "select field_name,field_type from tfield a,ttable b"
                    + " where a.field_table=b.table_id and b.table_name='" +
                    table + "' and a.field_show=406");
                 if(updateArr != null && updateArr.length > 0) {
                    String userAccount = session.getAttribute(
                        "userAccount") == null ? "" :
                        session.getAttribute("userAccount").
                        toString();

                    String sql = "update " + table + " set ";
                    for(int i = 0; i < updateArr.length; i++) {
                        sql += (i > 0 ? "," : "") + updateArr[i][0] +
                            "='" + userAccount + "'";
                    }
                    sql += " where " + table + "_id=" +
                        infoId;
                    upList.add(sql);
                                 }

                                 //ID
                                 updateArr = dbopt.executeQueryToStrArr2(
                    "select field_name,field_type from tfield a,ttable b"
                    + " where a.field_table=b.table_id and b.table_name='" +
                    table + "' and a.field_show=201");
                 if(updateArr != null && updateArr.length > 0) {
                    String userId = session.getAttribute(
                        "userId") == null ? "" :
                        session.getAttribute("userId").
                        toString();

                    String sql = "update " + table + " set ";
                    for(int i = 0; i < updateArr.length; i++) {
                        sql += (i > 0 ? "," : "") + updateArr[i][0] +
                            "='" + userId + "'";
                    }
                    sql += " where " + table + "_id=" +
                        infoId;
                    upList.add(sql);
                                 }

                                 //姓名
//               updateArr = dbopt.executeQueryToStrArr2("select field_name,field_type from tfield a,ttable b"
//                       +" where a.field_table=b.table_id and b.table_name='"+table+"' and a.field_show=202");
//               for(int i=0;i<updateArr.length;i++){
//                   String userAccount = request.getSession(true).getAttribute("userName")==null?"":request.getSession(true).getAttribute("userName").toString();
//                   upList.add("update "+table+" set "+updateArr[i][0]+"='"+userAccount+"' where " + table + "_id=" + infoId);
//               }
//               //部门
//               updateArr = dbopt.executeQueryToStrArr2("select field_name,field_type from tfield a,ttable b"
//                       +" where a.field_table=b.table_id and b.table_name='"+table+"' and a.field_show=207");
//               for(int i=0;i<updateArr.length;i++){
//                   String userAccount = request.getSession(true).getAttribute("orgName")==null?"":request.getSession(true).getAttribute("orgName").toString();
//                   upList.add("update "+table+" set "+updateArr[i][0]+"='"+userAccount+"' where " + table + "_id=" + infoId);
//               }

                                 //组织ID
                                 updateArr = dbopt.executeQueryToStrArr2(
                    "select field_name,field_type from tfield a,ttable b"
                    + " where a.field_table=b.table_id and b.table_name='" +
                    table + "' and a.field_show=213");
                 if(updateArr != null && updateArr.length > 0) {
                    String orgId = session.getAttribute(
                        "orgId") == null ? "" :
                        session.getAttribute("orgId").
                        toString();

                    String sql = "update " + table + " set ";
                    for(int i = 0; i < updateArr.length; i++) {
                        sql += (i > 0 ? "," : "") + updateArr[i][0] +
                            "='" + orgId + "'";
                    }
                    sql += " where " + table + "_id=" +
                        infoId;
                    upList.add(sql);
                                 }

                                 for(int n = 0; n < upList.size(); n++) {
                    dbopt.executeUpdate(upList.get(n).toString());
                                 }*/
            }

            if(result < 1) {
                success = Boolean.FALSE;
            } else {
                if(DB_TYPE.indexOf("mysql") >= 0) {
                    dbopt.executeUpdate("update " + table + " set " + table +
                                        "_date=sysdate() where " + table +
                                        "_id=" +
                                        infoId);
                }
                /****关联性设计************/
                saveRelationInfo(infoId, tableId, request, "1");
            }
        } catch(Exception e) {
            success = Boolean.FALSE;
            System.out.println("sql:"+prepareSQL);
            e.printStackTrace();
        } finally {
            try {
                if(ps != null) {
                    ps.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            if(success.equals(Boolean.TRUE)) {
                return infoId;
            } else {
                return "";
            }
        }
    }

    /**
     * 保存页面关联数据
     * @param request HttpServletRequest
     * @param pageId String：自定义表单的ID
     * @return Boolean实现过程：
     * 1、读取所有关联字段，遍历字段
     * 2、判断数据库类型
     * 3、读取字段的值，字段分为：字符型、数值型、上传类型、单选、多选、选人选组织、计算字段
     * 4、生成SQL语句
     * 5、执行SQL，返回记录ID
     */
    public String saveForeignData(String formId, HttpServletRequest request,
                                  String table,
                                  String[][] fieldStr, int seq, DbOpt dbopt,
                                  String priId, Integer status) {
        //HttpSession session = request.getSession(true);

        java.util.Date now = new java.util.Date();
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String yyyyMMdd = sdf2.format(now);

        Boolean success = Boolean.TRUE;
        String infoId = ""; //新增信息的ID

        String DB_TYPE = dbopt.dbtype;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dbopt.getConnection();
            DatabaseMetaData dbmd = conn.getMetaData();
            String databaseName = dbmd.getDatabaseProductName();

            //start 追加自定义表中缺失的字段，主要用于升级历史数据表
            //reCreateTableFieldForSub(databaseName, table, dbopt);
            //end 追加自定义表中缺失的字段，主要用于升级历史数据表

            String sqlHead = "insert into " + table + "(";
            String sqlPrepared = " values(";

            infoId = getPrimaryKeyId(databaseName, dbopt);

            List dataList = new ArrayList();
            for(int i = 0, flen=fieldStr.length; i < flen; i++) {
                int index = seq;

                String f_field = fieldStr[i][3];
                
                logger.debug("index:"+index+", fieldName:"+f_field);
                
                String fieldType = request.getParameterValues(f_field + "_type")!=null?request.getParameterValues(f_field + "_type")[index]:null;
                
                if(i == 0) {
                    //------------------------------------------------------
                    sqlHead += table + "_id," + table + "_owner," + table +
                        "_org," + table + "_group," + table +
                        "_FOREIGNKEY," + table + "_workstatus,";

                    sqlPrepared += "?,?,?,?,?,?,";
                    dataList.add(new String[] {TYPE_LONG, infoId});
                    dataList.add(new String[] {TYPE_STRING, _userId});
                    dataList.add(new String[] {TYPE_STRING, _orgId});
                    dataList.add(new String[] {TYPE_STRING, _groupId});
                    dataList.add(new String[] {TYPE_LONG, priId});
                    dataList.add(new String[] {TYPE_INT, status+""});
                    //------------------------------------------------------

                    if(DB_TYPE.equals("oracle")) {
                        sqlHead += table + "_date,";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, yyyyMMdd});
                    }
                }
                
                if(FormHelper.isEmpty(fieldType))continue;
                
                String showType = request.getParameterValues(f_field + "_showtype")!=null?request.getParameterValues(f_field + "_showtype")[index]:"";
                if("401".equals(showType))continue;//子表不保存批示意见字段

                //数字类型字段
                if("number".equals(fieldType)) {
                    //----------------------------------------------------------
                    String[] _rnd_name = request.getParameterValues(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field;
                    if(_rnd_name!=null){
                       if(_rnd_name.length>0) {
                           param_name = f_field + _rnd_name[index];
                           index = 0;
                       }
                    }
                    //----------------------------------------------------------

                    String[] param_field = request.getParameterValues(
                        param_name);
                    if(param_field != null &&
                       param_field[index] != null &&
                       param_field[index].trim().length() > 0) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_FLOAT,
                                     param_field[
                                     index].trim().replaceAll(",", "")});
                        //------------------------------------------------------
                    } else {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_FLOAT, null});
                        //------------------------------------------------------
                    }
                } else

                //字符/文本类型字段
                if("varchar".equals(fieldType)) {
                    //----------------------------------------------------------
                    String[] _rnd_name = request.getParameterValues(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field;
                    if(_rnd_name!=null){
                       if(_rnd_name.length>0) {
                           param_name = f_field + _rnd_name[index];
                           index = 0;
                       }
                    }
                    //----------------------------------------------------------

                    String[] param_field = request.getParameterValues(
                        param_name);
                    if(param_field != null &&
                       param_field[index] != null) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, param_field[index]});
                        //------------------------------------------------------
                    } else {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, null});
                        //------------------------------------------------------
                    }
                } else

                //上传文件类型字段
                if("file".equals(fieldType)) {
                    String[] _rnd_name = request.getParameterValues(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name[index];
                    index = 0;

                    String[] param_saveName = request.getParameterValues(
                        param_name + "_saveName");
                    if(param_saveName != null &&
                       param_saveName[index] != null) {
                        String fileName = request.getParameterValues(
                            param_name + "_fileName")[index];
                        String saveName = param_saveName[index]; //saveName中“aaaaaa”表示无效文件，在fileName中对应位置的文件名也是无效的

                        String[] fileNameArr = splitStr(fileName, "\\|");
                        String[] saveNameArr = splitStr(saveName, "\\|");
                        boolean flag = true;
                        String saveFileTemp = "";
                        String fileFileTemp = "";
                        for(int k = 0; k < saveNameArr.length; k++) {
                            if(saveNameArr[k] == null ||
                               saveNameArr[k].equals("aaaaaa") ||
                               saveNameArr[k].trim().equals("")) {
                                continue;
                            } else {
                                flag = false;
                                saveFileTemp += saveNameArr[k] + ",";
                                fileFileTemp += fileNameArr[k] + ",";
                            }
                        }
                        if(!flag) {
                            //------------------------------------------------------
                            sqlHead += f_field + ",";
                            sqlPrepared += "?,";
                            dataList.add(new String[] {TYPE_STRING,
                                         saveFileTemp + ";" + fileFileTemp});
                            //------------------------------------------------------
                        }
                    }

                } else

                //多选类型
                if("combox".equals(fieldType)) {
                    String[] _rnd_name = request.getParameterValues(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name[index];

                    String[] temp = request.getParameterValues(param_name);
                    if(temp != null && temp.length > 0) {
                        String t = "";
                        for(int j = 0; j < temp.length; j++) {
                            if(!CommonUtils.isEmpty(temp[j])){
                                t += temp[j] + ",";
                            }
                        }

                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING, t});
                        //------------------------------------------------------
                    }

                } else

                //单选人，多选人，单选组织，多选组织
                if("personorg".equals(fieldType)) {
                    String[] _rnd_name = request.getParameterValues(FormContants.
                        PREFIX_NEW_COMPONENT + f_field);
                    String param_name = f_field + _rnd_name[index];
                    index = 0;

                    String[] param_Name = request.getParameterValues(
                        param_name + "_Name");
                    if(param_Name != null &&
                       param_Name[index] != null && !param_Name[index].equals("") /*&&
                       param_Name[index].length() > 0*/) {
                    	//20160920删除单选多选最后逗号
                    	if(param_Name[index].lastIndexOf(",")==param_Name[index].length()-1){
                    		param_Name[index] = param_Name[index].substring(0, param_Name[index].length()-1);
                    	}
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_Name[index] + ";" +
                                     request.
                                     getParameterValues(param_name + "_Id")[
                                     index]});
                        //------------------------------------------------------
                    }
                } else

                //登录人姓名和日期
                if("loginPersonDate".equals(fieldType)) {
                    String[] param_loginUser = request.getParameterValues(
                        f_field + "_loginUser");
                    if(param_loginUser != null &&
                       param_loginUser[index] != null /*&&
                       param_loginUser[index].length() > 0*/) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_loginUser[index] + " " +
                                     request.
                                     getParameterValues(f_field +
                            "_loginDate")[index]});
                        //------------------------------------------------------
                    }
                } else

                //时间 日期类型字段
                if("datetime".equals(fieldType)) {
                    String[] param_field = request.getParameterValues(f_field);
                    if(param_field != null &&
                       param_field[index] != null /*&&
                       param_field[index].trim().length() > 0*/) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_field[
                                     index] + " " +
                                     request.getParameterValues(f_field +
                            "hours")[index]
                                     + ":" +
                                     request.getParameterValues(f_field +
                            "minutes")[index]});
                        //------------------------------------------------------
                    }

                } else

                if("numbercompute".equals(fieldType)) {
                    String[] param_field = request.getParameterValues(f_field);
                    if(param_field != null &&
                       param_field[index] != null /*&&
                       param_field[index].trim().length() > 0*/) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_FLOAT,
                                     param_field[
                                     index].replaceAll(",", "")});
                        //------------------------------------------------------
                    }
                } else

                if("varcharcompute".equals(fieldType)) {
                    String[] param_field = request.getParameterValues(f_field);
                    if(param_field != null &&
                       param_field[index] != null /*&&
                       param_field[index].trim().
                       length() > 0*/) {
                        //------------------------------------------------------
                        sqlHead += f_field + ",";
                        sqlPrepared += "?,";
                        dataList.add(new String[] {TYPE_STRING,
                                     param_field[
                                     index]});
                        //------------------------------------------------------
                    }
                }
            }

            int result = 0;
            if(infoId != null) {
                if(dataList != null && dataList.size() > 6) {
                    String prepareSQL = sqlHead.substring(0,
                        sqlHead.length() - 1) +
                        ") " +
                        sqlPrepared.substring(0, sqlPrepared.length() - 1) +
                        ")";

                    result = executeUpdateSql(prepareSQL, dataList, conn, ps);
                }
            }

            if(result < 1) {
                success = Boolean.FALSE;
            } else {
                if(DB_TYPE.indexOf("mysql") >= 0) {
                    dbopt.executeUpdate("update " + table + " set " + table +
                                        "_date=sysdate() where " + table +
                                        "_id=" +
                                        infoId);
                }
            }
        } catch(Exception e) {
            success = Boolean.FALSE;
            e.printStackTrace();
        } finally {
            try {
                if(ps != null) {
                    ps.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            if(success.equals(Boolean.TRUE)) {
                return infoId;
            } else {
                return "";
            }
        }
    }

    /**
     * 更新表单数据
     * @param request HttpServletRequest
     * @return Boolean
     * 实现逻辑：
              1、调用updateData更新表单主表数据
              2、如果主表数据保存失败，返回；成功则继续3
              3、读取表单中本次修改的子表数组，循环遍历子表，删除字表中已有的关联记录
              4、从表单读取字表记录数量，循环调用saveForeignData保存每条记录
     */
    public Boolean update(HttpServletRequest request, Integer status) {
        Boolean success = Boolean.FALSE;
        DbOpt dbopt = null;
        try {
            dbopt = new DbOpt();

            UIBD uibd = new UIBD();

            String formId = request.getParameter("formId");
            String infoId = request.getParameter("infoId");

            //------------------------------------------------------------------
            //批量发起
            initUserData(request);
            //------------------------------------------------------------------

            //更新主表记录
            success = this.updateData(formId, infoId, request, dbopt, status);
            
            logger.debug("update return:"+success);

            //保存子表关联
            if(success.booleanValue()) {
                //读取表单中本次修改的字表数组
                String[] tables = request.getParameterValues("showTable");
                if(tables != null && tables.length > 0) {
                    for(int i = 0, tlen=tables.length; i < tlen; i++) {
                        String tableName = tables[i];
                        if(tableName == null || tableName.trim().length() < 1)continue;
                        
                        logger.debug("sub table name:"+tableName);

                        //读取字表中所有已关联的字段
                        String[][] field = uibd.getForeignFieldsByTableName(
                            formId, tableName);
                        if(field != null && field.length > 0) {
                            //读取字表中记录的数量
                            String[] types = null;//request.getParameterValues(field[0][3] + "_type");
                            for(int k=0,m=field.length; k<m; k++){
                                types = request.getParameterValues(field[k][3] + "_type");
                                if(types != null && types.length > 0) {
                                    break;
                                }
                            }
                            
                            String forInfoIds = "";
                            if(types != null && types.length > 0) {
                                logger.debug("sub table types:"+types);
                                
                                //start 追加自定义表中缺失的字段，主要用于升级历史数据表
                                reCreateTableFieldForSub("oracle", tableName,
                                    dbopt);
                                //end 追加自定义表中缺失的字段，主要用于升级历史数据表
                                //临时使用字段_RELABYINDE
                                dbopt.executeUpdate("update " + tableName + " set " + tableName + "_RELABYINDE='" + infoId + "' where " + tableName + "_FOREIGNKEY=" + infoId);

                                boolean isDeleteOldData = true;
                                //遍历每一条记录并保存记录
                                for(int j = 0, len=types.length; j < len; j++) {
                                    String subDataId = this.saveForeignData(formId, request,
                                            tableName,
                                            field, j, dbopt,
                                            infoId, status);
                                    if(CommonUtils.isEmpty(subDataId)){//出现异常情况
                                        isDeleteOldData = false;
                                    }
                                    forInfoIds += subDataId  + ",";
                                }
                                
                                if(isDeleteOldData){
                                    //删除子表中所有已有的记录
                                    dbopt.executeUpdate("delete from " + tableName + " where " + tableName + "_RELABYINDE='" + infoId + "'");
                                }else{
                                    dbopt.executeUpdate("update " + tableName + " set " + tableName + "_RELABYINDE='' where " + tableName + "_RELABYINDE='" + infoId + "'");
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                dbopt.close();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }

    /**
     * 修改关联数据
     * @param request HttpServletRequest
     * @param pageId String:自定义表单ID
     * @param infoId String：修改数据的ID
     * @return Boolean
     */
    public Boolean updateData(String formId, String infoId,
                              HttpServletRequest request, DbOpt dbopt, Integer status) {
        //HttpSession session = request.getSession(true);
        if(formId == null || formId.length() < 1) {
            logger.debug("表单ID不能为空。");
            return Boolean.FALSE;
        }

        if(infoId == null || infoId.length() < 1) {
            logger.debug("业务数据infoId不能为空。");
            return Boolean.FALSE;
        }
        
        String p_wf_openType = request.getParameter("p_wf_openType");
        logger.debug("p_wf_openType:"+p_wf_openType);

        UIBD uibd = new UIBD();
        Boolean success = Boolean.TRUE;
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //表单关联的所有字段的属性数组
            String[][] fieldStr = uibd.getFieldInfo(formId);

            if(fieldStr == null || fieldStr.length < 1) {
                logger.debug("表单未关联字段。");
                return Boolean.FALSE;
            }

            //表单关联的数据表名称
            String[][] tableIdName = uibd.getTableIDAndName(formId);
            String tableId = tableIdName[0][0];
            String table = tableIdName[0][1];

            if(table == null || table.trim().length() < 1) {
                logger.debug("表单关联主表不能为空。");
                return Boolean.FALSE;
            }

            conn = dbopt.getConnection();

            String sqlHead = "update " + table + " set "; //修改数据SQL
            sqlHead += table + "_workstatus=" + status + ",";
            String sqlValue = " where " + table + "_id=" + infoId; //修改数据条件语句

            //不可编辑字段
            String hide_Field = request.getParameter("hide_Field");
            if(hide_Field == null || hide_Field.equals("null") || "".equals(hide_Field)){
                hide_Field = request.getParameter("Hide_Field");
            }
            String hideField = (hide_Field == null || hide_Field.equals("null")) ?
                "" : hide_Field;
            
            String p_wf_cur_ModifyField = request.getParameter("p_wf_cur_ModifyField")!=null?request.getParameter("p_wf_cur_ModifyField"):"";
            //自定义模块menuId
            String menuId = request.getParameter("menuId");

            List dataList = new ArrayList();
            for(int i = 0, flen=fieldStr.length; i < flen; i++) {
                String f_field = fieldStr[i][0];
                //System.out.println("hideField="+hideField);
                //System.out.println("f_field="+f_field);
//                if(f_field == null || hideField.indexOf(f_field) >= 0) {
//                    continue;
//                }
                
                //可写字段
                if(p_wf_cur_ModifyField.indexOf("$"+f_field+"$") != -1 || !CommonUtils.isEmpty(menuId)){
                
                    String f_show  = fieldStr[i][2];
    
                    String fieldType = request.getParameter(f_field +
                        "_type");
                    //tring fieldSize = request.getParameter(f_field + "_size");
                    //System.out.println("fieldType="+fieldType);
    
                    if(fieldType == null)continue;
    
                    //数字类型字段
                    if("number".equals(fieldType)) {
                        String _rnd_name = request.getParameter(FormContants.
                            PREFIX_NEW_COMPONENT + f_field);
                        String param_name = f_field + _rnd_name;
                        if(FormHelper.isEmpty(_rnd_name)) {
                            param_name = f_field;
                        }
    
                        String f_field_val = request.getParameter(param_name);
                        if(f_field_val != null) {
                            if(f_field_val.length() == 0){
                                f_field_val = "";//数值型存储默认值0
                            }
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_FLOAT,
                                         f_field_val.trim().replaceAll(",",
                                "")});
                            //------------------------------------------------------
    
                            if("1".equals(fieldStr[i][1]) && !FormHelper.isEmpty(f_field_val)) {
                                String[] fieldOnly = dbopt.
                                    executeQueryToStrArr1("select " +
                                    f_field + " from " + table +
                                    " where " + f_field + "=" +
                                    f_field_val.
                                    replaceAll(",", "") + " and " + table +
                                    "_id<>" + infoId);
                                if(fieldOnly != null && fieldOnly.length > 0) {
                                    success = Boolean.FALSE;
                                    break;
                                }
                            }
                        }
                    } else
    
                    //字符/文本类型字段
                    if("varchar".equals(fieldType)) {
                        String _rnd_name = request.getParameter(FormContants.
                            PREFIX_NEW_COMPONENT + f_field);
                        String param_name = f_field + _rnd_name;
                        if(FormHelper.isEmpty(_rnd_name)) {
                            param_name = f_field;
                        }
    
                        String f_field_val = request.getParameter(param_name);
                        if(f_field_val != null) {
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_STRING, f_field_val});
                            //------------------------------------------------------
    
                            String prefix = "";
                            if(dbopt.dbtype.indexOf("sqlserver") >= 0) {
                                prefix = "N";
                            }
    
                            if("1".equals(fieldStr[i][1]) && !FormHelper.isEmpty(f_field_val)) {
                                String[] fieldOnly = dbopt.
                                    executeQueryToStrArr1("select " +
                                    f_field + " from " + table +
                                    " where " + f_field + "=" + prefix + "'" +
                                    f_field_val +
                                    "' and " + table + "_id<>" + infoId);
                                if(fieldOnly != null && fieldOnly.length > 0) {
                                    success = Boolean.FALSE;
                                    break;
                                }
                            }
    
                            //--------------------------------------------------
                            String val = f_field_val.replaceAll("'", "\\\\''");
    
                            String[] isExist = dbopt.executeQueryToStrArr1(
                                "select " + f_field + " from " + table +
                                " where " + table + "_id=" + infoId);
    
                            //自动编号字段检查字段唯一性
                            if("111".equals(f_show) && !"".equals(val) &&
                               (isExist == null || isExist.length < 1 ||
                                isExist[0] == null || "".equals(isExist[0]))) {
                                if(!"reStart".equals(p_wf_openType)){    
                                    val = new AutoCode().getCodeVal(val, fieldStr, table, i, dbopt);
                                }
                            } else {
                                //this.updateAutoCode(f_field,val,fieldStr[i][3]);
                            }
                            //--------------------------------------------------
                        }
                    } else
    
                    //上传文件类型字段
                    if("file".equals(fieldType)) {
                        String _rnd_name = request.getParameter(FormContants.
                            PREFIX_NEW_COMPONENT + f_field);
                        String param_name = f_field + _rnd_name;
    
                        String param_saveName = request.getParameter(param_name +
                            "_saveName");
                        if(param_saveName != null) {
                            String fileName = request.getParameter(param_name +
                                "_fileName");
                            String saveName = param_saveName; //saveName中“aaaaaa”表示无效文件，在fileName中对应位置的文件名也是无效的
    
                            String[] fileNameArr = splitStr(fileName, "\\|");
                            String[] saveNameArr = splitStr(saveName, "\\|");
                            boolean flag = true;
                            String saveFileTemp = "";
                            String fileFileTemp = "";
                            for(int k = 0; k < saveNameArr.length; k++) {
                                if((saveNameArr[k] == null ||
                                    saveNameArr[k].equals("aaaaaa") ||
                                    saveNameArr[k].trim().equals(""))) {
                                    continue;
                                } else {
                                    flag = false;
                                    saveFileTemp += saveNameArr[k] + ",";
                                    fileFileTemp += fileNameArr[k] + ",";
                                }
                            }
                            String t = saveFileTemp + ";" + fileFileTemp;
                            if("".equals(saveFileTemp)){
                                t = "";
                                flag = false;
                            }
                            if(!flag) {
                                //------------------------------------------------------
                                sqlHead += f_field + "=?,";
                                dataList.add(new String[] {TYPE_STRING, t});
                                //------------------------------------------------------
                            }
                        }
                    } else
    
                    //多选类型
                    if("combox".equals(fieldType)) {
                        String _rnd_name = request.getParameter(FormContants.
                            PREFIX_NEW_COMPONENT + f_field);
                        String param_name = f_field + _rnd_name;
    
                        String[] temp = request.getParameterValues(param_name);
                        if(temp != null && temp.length > 0) {
                            String t = "";
                            for(int j = 0; j < temp.length; j++) {
                                if(!CommonUtils.isEmpty(temp[j])){
                                    t += temp[j] + ",";
                                }
                            }
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_STRING, t});
                            //------------------------------------------------------
                        } else {
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_STRING, ""});
                            //------------------------------------------------------
                        }
                    } else
    
                    //单选人，多选人，单选组织，多选组织
                    if("personorg".equals(fieldType)) {
                        String _rnd_name = request.getParameter(FormContants.
                            PREFIX_NEW_COMPONENT + f_field);
                        String param_name = f_field + _rnd_name;
    
                        String param_Name = request.getParameter(param_name +
                            "_Name");
                        if(param_Name != null) {
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            if(param_Name.length()>0){
                                dataList.add(new String[] {TYPE_STRING,
                                         param_Name +
                                         ";" +
                                         request.getParameter(param_name + "_Id")});
                            }else{
                                dataList.add(new String[] {TYPE_STRING, ""});
                            }
                            //------------------------------------------------------
                        }
                    } else
    
                    //登录人姓名和日期
                    if("loginPersonDate".equals(fieldType)) {
                        String param_loginUser = request.getParameter(f_field +
                            "_loginUser");
                        if(param_loginUser != null /*&&
                           param_loginUser.length() > 0*/) {
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_STRING,
                                         param_loginUser + " " +
                                         request.getParameter(f_field +
                                "_loginDate")});
                            //------------------------------------------------------
                        }
                    } else
    
                    //时间 日期类型字段
                    if("datetime".equals(fieldType)) {
                        String f_field_val = request.getParameter(f_field);
                        if(f_field_val != null /*&&
                           f_field_val.trim().length() > 0*/) {
                            //------------------------------------------------------
                            sqlHead += f_field + "=?,";
                            dataList.add(new String[] {TYPE_STRING,
                                         f_field_val +
                                         " " +
                                         request.getParameter(f_field + "hours")
                                         + ":" +
                                         request.getParameter(f_field + "minutes")});
                            //------------------------------------------------------
                        }
                    }
                }
            }

            //执行sql语句
            int result = 1;
            if(success.equals(Boolean.TRUE)) {
                if(!sqlHead.trim().endsWith(" set")) {
                    String prepareSQL = sqlHead.trim().substring(0,
                        sqlHead.length() - 1) + " " + 
                        sqlValue;

                    result = executeUpdateSql(prepareSQL, dataList, conn, ps);
                }

                //执行SQL保存计算字段的值
                //updateComputeField(infoId, tableId, table, dbopt, "1", request);
            }

            if(result < 1) {
                success = Boolean.FALSE;
            } else {
                /****关联性设计************/
                saveRelationInfo(infoId, tableId, request, "2");
            }
        } catch(Exception e) {
            success = Boolean.FALSE;
            e.printStackTrace();
        } finally {
            try {
                if(ps != null) {
                    ps.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }

            return success;
        }
    }

    /**
     * 取得主表的关联子表
     * @param pageId String
     * @return String[]
     */
    public String[] getForeignTable(String pageId) {
        DbOpt dbopt = null;
        String[] result = null;

        try {
            dbopt = new DbOpt();
            result = dbopt.executeQueryToStrArr1(
                "select distinct b.AREA_NAME from tarea b"
                + " where b.area_name<>'form1' and b.PAGE_ID=" + pageId);
        } catch(Exception e) {
            logger.error(
                "FormDataBD error on getForeignTable information:" +
                e.getMessage());
        } finally {
            try {
                dbopt.close();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            return result;
        }
    }

    //获取所有用户设置关联字段,及字段相关信息
    public String[][] getForeignFieldInfo(String pageId, String table) {
        DbOpt dbopt = null;
        String[][] result = null;

        try {
            dbopt = new DbOpt();
            if(table != null) {
                //                                                          0           1            2           3           4
                result = dbopt.executeQueryToStrArr2("select distinct a.ELT_TABLE,c.field_only,c.field_show,b.area_name,c.field_id from TELT a ,tarea b,tfield c"
                    + ",ttable d where b.area_name=d.table_name and c.field_table=d.table_id and b.area_name='" +
                    table +
                    "' and b.AREA_id=a.AREA_id and a.ELT_TABLE=c.field_name "
                    + " and a.PAGE_ID=" + pageId, 5);
            } else {
                //                                                          0           1            2           3           4
                result = dbopt.executeQueryToStrArr2("select distinct a.ELT_TABLE,c.field_only,c.field_show,b.area_name,c.field_id from TELT a ,tarea b,tfield c,ttable d"
                    + " where b.area_name=d.table_name and c.field_table=d.table_id and b.area_name<>'form1' and b.AREA_id=a.AREA_id and a.ELT_TABLE=c.field_name "
                    + " and a.PAGE_ID=" + pageId, 5);
            }
        } catch(Exception e) {
            logger.error(
                "FormDataBD error on getForeignFieldInfo information:" +
                e.getMessage());
        } finally {
            try {
                dbopt.close();
            } catch(SQLException ex) {
                ex.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 用于主表
     * @param databaseName String
     * @param table String
     * @param dbopt DbOpt
     * @throws SQLException
     * @throws Exception
     * @return String
     */
    public String reCreateTableFieldForMain(String databaseName, String table,
                                            DbOpt dbopt) throws
        SQLException, Exception {
        String tableNameUpper = table.toUpperCase();
        //start 追加自定义表中缺失的字段，主要用于升级历史数据表
        if(dbopt.dbtype.equals("oracle")) {
            Statement stat = dbopt.getStatement();
            //达梦数据库
            if(databaseName.toUpperCase().indexOf("DM") != -1) {
                String ddlStr =
                    " select count(*) from "+DM_SQL+" where a.ID=b.ID and b.NAME='" +
                    tableNameUpper + "' and (a.NAME = '" +
                    tableNameUpper + "_ORG' OR a.NAME = '" +
                    tableNameUpper + "_GROUP')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_ORG VARCHAR2(100) NULL";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_GROUP VARCHAR2(100) NULL";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from "+DM_SQL+" where a.ID=b.ID and b.NAME='" +
                    tableNameUpper + "' and (a.NAME = '" +
                    tableNameUpper + "_OWNER')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_OWNER NUMERIC(20) default 0";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from "+DM_SQL+" where a.ID=b.ID and b.NAME='" +
                    tableNameUpper + "' and (a.NAME = '" +
                    tableNameUpper + "_DATE')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_DATE varchar2(24) default 0";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from "+DM_SQL+" where a.ID=b.ID and b.NAME='" +
                    tableNameUpper + "' and (a.NAME = '" +
                    tableNameUpper + "_WORKSTATUS')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_WORKSTATUS NUMERIC(5)";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

            } else {
                String ddlStr =
                    " select count(*) from user_tab_columns where table_name='" +
                    tableNameUpper + "' and (column_name = '" +
                    tableNameUpper + "_ORG' OR column_name = '" +
                    tableNameUpper + "_GROUP')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_ORG VARCHAR2(100) NULL";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_GROUP VARCHAR2(100) NULL";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from user_tab_columns where table_name='" +
                    tableNameUpper + "' and (column_name = '" +
                    tableNameUpper + "_OWNER')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_OWNER NUMBER(20) default 0";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from user_tab_columns where table_name='" +
                    tableNameUpper + "' and (column_name = '" +
                    tableNameUpper + "_DATE')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_DATE varchar2(24) default 0";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }

                ddlStr =
                    " select count(*) from user_tab_columns where table_name='" +
                    tableNameUpper + "' and (column_name = '" +
                    tableNameUpper + "_WORKSTATUS')";
                ddlStr = dbopt.executeQueryToStr(ddlStr);
                if("0".equals(ddlStr)) {
                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
                        "_WORKSTATUS NUMBER(5)";
                    stat.addBatch(ddlStr.toUpperCase());
                    stat.executeBatch();
                }
            }

        } else if(dbopt.dbtype.indexOf("db2") >= 0) {
            Statement stat = dbopt.getStatement();
            String ddlStr = "select count(*) from syscat.columns " +
                "where TABNAME='" + tableNameUpper +
                "' and (COLNAME = '"
                + tableNameUpper + "_ORG' OR COLNAME = '" +
                tableNameUpper + "_GROUP')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_ORG VARCHAR(100)";
                stat.addBatch(ddlStr.toUpperCase());
                stat.executeBatch();
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_GROUP VARCHAR(100)";
                stat.addBatch(ddlStr.toUpperCase());
                stat.executeBatch();
            }

            ddlStr = "select count(*) from syscat.columns " +
                "where TABNAME='" + tableNameUpper +
                "' and (COLNAME = '"
                + tableNameUpper + "_OWNER')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_OWNER DECIMAL(20)";
                stat.addBatch(ddlStr.toUpperCase());
                stat.executeBatch();
            }

            ddlStr = "select count(*) from syscat.columns " +
                "where TABNAME='" + tableNameUpper +
                "' and (COLNAME = '"
                + tableNameUpper + "_WORKSTATUS')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_WORKSTATUS DECIMAL(5)";
                stat.addBatch(ddlStr.toUpperCase());
                stat.executeBatch();
            }

        } else if(dbopt.dbtype.indexOf("mssqlserver") >= 0) {
            Statement stat = dbopt.getStatement();
            String ddlStr =
                " select count(*) from syscolumns where id=object_id('" +
                table + "') and (name = '" + table +
                "_ORG' OR name = '" + table + "_GROUP')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_ORG NVARCHAR(100) NULL, " + table +
                    "_GROUP NVARCHAR(100) NULL";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from syscolumns where id=object_id('" +
                table + "') and (name = '" + table + "_OWNER')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_OWNER NUMERIC(20) default 0";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from syscolumns where id=object_id('" +
                table + "') and (name = '" + table + "_DATE')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_DATE nvarchar(24)";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from syscolumns where id=object_id('" +
                table + "') and (name = '" + table + "_WORKSTATUS')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_WORKSTATUS NUMERIC(5)";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

        } else if(dbopt.dbtype.indexOf("mysql") >= 0) {
            table = table.toUpperCase();
            Statement stat = dbopt.getStatement();
            String ddlStr =
                " select count(*) from information_schema.columns where upper(table_name)= '" +
                table + "' and (upper(column_name) = '" + table +
                "_ORG' OR upper(column_name) = '" + table + "_GROUP')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_ORG VARCHAR(100) NULL ";
                stat.addBatch(ddlStr);
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_GROUP VARCHAR(100) NULL";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from information_schema.columns where upper(table_name)= '" +
                table + "' and (upper(column_name) = '" + table +
                "_OWNER')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_OWNER bigint default 0";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from information_schema.columns where upper(table_name)= '" +
                table + "' and (upper(column_name) = '" + table + "_DATE')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_DATE varchar(24)";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

            ddlStr =
                " select count(*) from information_schema.columns where upper(table_name)= '" +
                table + "' and (upper(column_name) = '" + table +
                "_WORKSTATUS')";
            ddlStr = dbopt.executeQueryToStr(ddlStr);
            if("0".equals(ddlStr)) {
                ddlStr = " ALTER TABLE " + table + " ADD " + table +
                    "_WORKSTATUS int";
                stat.addBatch(ddlStr);
                stat.executeBatch();
            }

        }
        //end 追加自定义表中缺失的字段，主要用于升级历史数据表

        return "1";
    }

    /**
     * 用于子表
     * @param databaseName String
     * @param table String
     * @param dbopt DbOpt
     * @throws SQLException
     * @throws Exception
     * @return String
     */
    public String reCreateTableFieldForSub(String databaseName, String table,
                                           DbOpt dbopt) throws
        SQLException, Exception {
        String tableNameUpper = table.toUpperCase();
        if(dbopt.dbtype.indexOf("oracle") != -1) {
            Statement stat = dbopt.getStatement();
            //达梦数据库
            if(databaseName.toUpperCase().indexOf("DM") != -1) {
//                String ddlStr =
//                    " select count(*) from SYSDBA.SYSCOLUMNS a,SYSDBA.SYSTABLES b where a.ID=b.ID and b.NAME='" +
//                    tableNameUpper + "' and (a.NAME = '"
//                    + tableNameUpper + "_ORG' OR a.NAME = '" +
//                    tableNameUpper + "_GROUP')";
                String ddlStr2 =
                    " select count(*) from "+DM_SQL+" where a.ID=b.ID and b.NAME='" +
                    tableNameUpper + "' and (a.NAME = '"
                    + tableNameUpper + "_FOREIGNKEY')";
                //ddlStr = dbopt.executeQueryToStr(ddlStr);
                ddlStr2 = dbopt.executeQueryToStr(ddlStr2);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_ORG VARCHAR2(100) NULL";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_GROUP VARCHAR2(100) NULL";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }
                if("0".equals(ddlStr2)) {
                    ddlStr2 = " ALTER TABLE " + table + " ADD " + table +
                        "_FOREIGNKEY NUMERIC(20) NULL";
                    stat.addBatch(ddlStr2.toUpperCase());
                    stat.executeBatch();
                }
//                ddlStr =
//                    " select count(*) from SYSDBA.SYSCOLUMNS a,SYSDBA.SYSTABLES b where a.ID=b.ID and b.NAME='" +
//                    tableNameUpper + "' and (a.NAME = '" +
//                    tableNameUpper + "_OWNER')";
//                ddlStr = dbopt.executeQueryToStr(ddlStr);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_OWNER NUMBER(20) default 0";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }
//
//                ddlStr =
//                    " select count(*) from SYSDBA.SYSCOLUMNS a,SYSDBA.SYSTABLES b where a.ID=b.ID and b.NAME='" +
//                    tableNameUpper + "' and (a.NAME = '" +
//                    tableNameUpper + "_DATE')";
//                ddlStr = dbopt.executeQueryToStr(ddlStr);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_DATE varchar2(24)";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    ddlStr = " ALTER TABLE " + table + " modify " + table +
//                        "_DATE varchar2(24) to_char(sysdate,'YYYY-mm-dd')";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }

            } else {
//                String ddlStr =
//                    " select count(*) from user_tab_columns where table_name='" +
//                    tableNameUpper + "' and (column_name = '"
//                    + tableNameUpper + "_ORG' OR column_name = '" +
//                    tableNameUpper + "_GROUP')";
                String ddlStr2 =
                    " select count(*) from user_tab_columns where table_name='" +
                    tableNameUpper + "' and (column_name = '"
                    + tableNameUpper + "_FOREIGNKEY')";
                //ddlStr = dbopt.executeQueryToStr(ddlStr);
                ddlStr2 = dbopt.executeQueryToStr(ddlStr2);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_ORG VARCHAR2(100) NULL";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_GROUP VARCHAR2(100) NULL";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }
                if("0".equals(ddlStr2)) {
                    ddlStr2 = " ALTER TABLE " + table + " ADD " + table +
                        "_FOREIGNKEY NUMBER(20) NULL";
                    stat.addBatch(ddlStr2.toUpperCase());
                    stat.executeBatch();
                }
//                ddlStr =
//                    " select count(*) from user_tab_columns where table_name='" +
//                    tableNameUpper + "' and (column_name = '" +
//                    tableNameUpper + "_OWNER')";
//                ddlStr = dbopt.executeQueryToStr(ddlStr);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_OWNER NUMBER(20) default 0";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }
//
//                ddlStr =
//                    " select count(*) from user_tab_columns where table_name='" +
//                    tableNameUpper + "' and (column_name = '" +
//                    tableNameUpper + "_DATE')";
//                ddlStr = dbopt.executeQueryToStr(ddlStr);
//                if("0".equals(ddlStr)) {
//                    ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                        "_DATE varchar2(24) to_char(sysdate,'YYYY-mm-dd')";
//                    stat.addBatch(ddlStr.toUpperCase());
//                    stat.executeBatch();
//                }
            }
        } else if(dbopt.dbtype.indexOf("db2") >= 0) {
            Statement stat = dbopt.getStatement();
//            String ddlStr =
//                " select count(*) from syscat.columns where tabname='" +
//                tableNameUpper + "' and (colname = '"
//                + tableNameUpper + "_ORG' OR colname = '" +
//                tableNameUpper + "_GROUP')";
            String ddlStr2 =
                " select count(*) from syscat.columns where tabname='" +
                tableNameUpper + "' and (colname = '"
                + tableNameUpper + "_FOREIGNKEY')";
            //ddlStr = dbopt.executeQueryToStr(ddlStr);
            ddlStr2 = dbopt.executeQueryToStr(ddlStr2);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_ORG VARCHAR(100)";
//                stat.addBatch(ddlStr.toUpperCase());
//                stat.executeBatch();
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_GROUP VARCHAR(100)";
//                stat.addBatch(ddlStr.toUpperCase());
//                stat.executeBatch();
//            }
            if("0".equals(ddlStr2)) {
                ddlStr2 = " ALTER TABLE " + table + " ADD " + table +
                    "_FOREIGNKEY decimal(20)";
                stat.addBatch(ddlStr2.toUpperCase());
                stat.executeBatch();
            }
//            ddlStr =
//                " select count(*) from syscat.columns where tabname='" +
//                tableNameUpper + "' and (colname = '" +
//                tableNameUpper + "_OWNER')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_OWNER DECIMAL(20)";
//                stat.addBatch(ddlStr.toUpperCase());
//                stat.executeBatch();
//            }
//
//            ddlStr =
//                " select count(*) from syscat.columns where tabname='" +
//                tableNameUpper + "' and (colname = '" +
//                tableNameUpper + "_DATE')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_DATE varchar(24)";
//                stat.addBatch(ddlStr.toUpperCase());
//                stat.executeBatch();
//            }
        } else if(dbopt.dbtype.indexOf("sqlserver") >= 0) {
            Statement stat = dbopt.getStatement();
//            String ddlStr =
//                " select count(*) from syscolumns where id=object_id('" +
//                table + "') and (name = '" + table +
//                "_ORG' OR name = '" + table + "_GROUP')";
            String ddlStr2 =
                " select count(*) from syscolumns where id=object_id('" +
                table + "') and (name = '" + table + "_FOREIGNKEY')";
            //ddlStr = dbopt.executeQueryToStr(ddlStr);
            ddlStr2 = dbopt.executeQueryToStr(ddlStr2);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_ORG NVARCHAR(100) NULL, " + table +
//                    "_GROUP NVARCHAR(100) NULL";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }
            if("0".equals(ddlStr2)) {
                ddlStr2 = " ALTER TABLE " + table + " ADD " + table +
                    "_FOREIGNKEY NUMERIC(20) NULL";
                stat.addBatch(ddlStr2);
                stat.executeBatch();
            }

//            ddlStr =
//                " select count(*) from syscolumns where id=object_id('" +
//                table + "') and (name = '" + table +
//                "_OWNER')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_OWNER NUMERIC(20) default 0";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }
//
//            ddlStr =
//                " select count(*) from syscolumns where id=object_id('" +
//                table + "') and (name = '" + table +
//                "_DATE')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_DATE nvarchar(24)";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }

        } else if(dbopt.dbtype.indexOf("mysql") >= 0) {
            table = table.toUpperCase();
            Statement stat = dbopt.getStatement();
//            String ddlStr =
//                " select count(*) from information_schema.columns where upper(table_name)='" +
//                table + "' and (upper(column_name)= '" + table +
//                "_ORG' OR upper(column_name) = '" + table + "_GROUP')";
            String ddlStr2 =
                " select count(*) from information_schema.columns where upper(table_name)='" +
                table + "' and (upper(column_name) = '" + table +
                "_FOREIGNKEY')";
            //ddlStr = dbopt.executeQueryToStr(ddlStr);
            ddlStr2 = dbopt.executeQueryToStr(ddlStr2);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_ORG VARCHAR(100) NULL";
//                stat.addBatch(ddlStr);
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_GROUP VARCHAR(100) NULL";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }
            if("0".equals(ddlStr2)) {
                ddlStr2 = " ALTER TABLE " + table + " ADD " + table +
                    "_FOREIGNKEY bigint NULL";
                stat.addBatch(ddlStr2);
                stat.executeBatch();
            }

//            ddlStr =
//                " select count(*) from information_schema.columns where upper(table_name)='" +
//                table + "' and (upper(column_name) = '" + table +
//                "_OWNER')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_OWNER bigint default 0";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }
//
//            ddlStr =
//                " select count(*) from information_schema.columns where upper(table_name)='" +
//                table + "' and (upper(column_name) = '" + table +
//                "_DATE')";
//            ddlStr = dbopt.executeQueryToStr(ddlStr);
//            if("0".equals(ddlStr)) {
//                ddlStr = " ALTER TABLE " + table + " ADD " + table +
//                    "_DATE varchar(24)";
//                stat.addBatch(ddlStr);
//                stat.executeBatch();
//            }
        }

        return "1";
    }

    /**
     * 替换计算字段中非必填项
     * @param fields String[]
     * @param computeField String
     * @param dbType int 数据库类型
     * @return String
     */
    private static String replaceComputeFields(String[] fields,
                                               String computeField, int dbType) {
        String result = computeField.toLowerCase();
        result = result.replaceAll("\\$", "~");
        if(fields != null) {
            for(int i = 0; i < fields.length; i++) {
                String f = fields[i].toLowerCase();
                int ind = computeField.toLowerCase().indexOf(f);
                if(ind != -1) {
                    String s = computeField.toLowerCase().substring(ind +
                        f.length());

                    Pattern pattern = Pattern.compile("[0-9]");
                    Matcher matcher = pattern.matcher(s.length() > 1 ?
                        s.substring(0, 1) : "");
                    boolean flag = matcher.matches();

                    if(!flag) {
                        String ff = f.replaceAll("\\$", "~");
                        if(dbType == 0) { //oracle
                            if(result.indexOf(ff) > 0 &&
                               result.substring(0,
                                                result.indexOf(ff)).endsWith(
                                "/")) {
                                result = result.replaceAll(ff,
                                    "nvl(" + ff + ",1)");
                            } else {
                                result = result.replaceAll(ff,
                                    "nvl(" + ff + ",0)");
                            }
                        } else if(dbType == 1) { //sqlserver
                            if(result.indexOf(ff) > 0 &&
                               result.substring(0,
                                                result.indexOf(ff)).endsWith(
                                "/")) {
                                result = result.replaceAll(ff,
                                    "isnull(" + ff + ",1)");
                            } else {
                                result = result.replaceAll(ff,
                                    "isnull(" + ff + ",0)");
                            }
                        } else if(dbType == 2) { //mysql
                            if(result.indexOf(ff) > 0 &&
                               result.substring(0,
                                                result.indexOf(ff)).endsWith(
                                "/")) {
                                result = result.replaceAll(ff,
                                    "ifnull(" + ff + ",1)");
                            } else {
                                result = result.replaceAll(ff,
                                    "ifnull(" + ff + ",0)");
                            }
                        }
                    }
                }
            }
            result = result.replaceAll("~", "\\$");
        }
        return result;
    }

    /**
     * 保存关联性
     * @param infoId String
     * @param tableId String
     * @param request HttpServletRequest
     * @param flag String 1-新增 2-修改
     */
    private void saveRelationInfo(String infoId, String tableId,
                                  HttpServletRequest request, String flag) {
        /****关联性设计************/
        RelationBD relationBD = new RelationBD();
        if("1".equals(flag)) { //新增保存
            relationBD.saveRelationList(request, infoId);

        } else if("2".equals(flag)) { //修改保存
            relationBD.saveRelationList(request);
        }
    }

    /**
     * 获取记录主键
     * @param databaseName String
     * @param dbopt DbOpt
     * @throws Exception
     * @return String
     */
    public String getPrimaryKeyId(String databaseName, DbOpt dbopt) throws
        Exception {
        String infoId = "";
        //获得新增信息的ID
        if(dbopt.dbtype.indexOf("oracle") >= 0) {
            if(databaseName.toUpperCase().indexOf("DM") != -1) {
                infoId = dbopt.executeQueryToStr(
                    "Select HIBERNATE_SEQUENCE.Nextval");
            } else {
                infoId = dbopt.executeQueryToStr(
                    "Select HIBERNATE_SEQUENCE.Nextval From dual");
            }
        } else if(dbopt.dbtype.indexOf("sqlserver") >= 0) {
//                dbopt.executeUpdate("update ezoffice.OA_SEQ set SEQ_SEQ = SEQ_SEQ+1");
//                infoId = dbopt.executeQueryToStr( "select SEQ_SEQ from ezoffice.OA_SEQ");

            ResultSet rs = dbopt.executeQuery("{call P_GET_WF_SEQUENCE}");
            if(rs.next()) {
                infoId = rs.getString("WF_SEQUENCE_VAL");
            }
            rs.close();

        } else {
            dbopt.executeUpdate(
                "update ezoffice.OA_SEQ set SEQ_SEQ = SEQ_SEQ+1");
            infoId = dbopt.executeQueryToStr(
                "select SEQ_SEQ from ezoffice.OA_SEQ");
        }

        return infoId;
    }

    /**
     * 取得计算字段
     * @param tableId String
     * @param dbopt DbOpt
     * @throws Exception
     * @return String[]
     */
    private String[] getComputeFields(String tableId, DbOpt dbopt) throws
        Exception {
        //取得计算字段的各字段
        String sql = "select FIELD_NAME from TFIELD where (FIELD_TYPE=1000000 or FIELD_TYPE=1000001) and FIELD_TABLE = " +
            tableId;
        String[] cmp_fields = dbopt.executeQueryToStrArr1(sql);
        return cmp_fields;
    }

    /**
     * 执行sql
     * @param sql String
     * @param dataList List
     * @param conn Connection
     * @param ps PreparedStatement
     * @throws Exception
     * @return int
     */
    public int executeUpdateSql(String prepareSQL, List dataList,
                                Connection conn,
                                PreparedStatement ps) throws Exception {
        logger.debug("prepareSQL:"+prepareSQL);

        int result = 0;
        ps = conn.prepareStatement(prepareSQL);
        if(dataList != null && dataList.size() > 0) {            
            for(int j0 = 0, dlen=dataList.size(); j0 < dlen; j0++) {
                String[] dataArr = (String[])dataList.get(j0);
                String type = dataArr[0];
                String val = dataArr[1];

                if(FormHelper.isNotEmpty(val)) {
                    if(TYPE_INT.equals(type)) {
                        try{
                            ps.setInt(j0 + 1, Integer.parseInt(val));
                        }catch(Exception e){
                            ps.setObject(j0 + 1, null);
                        }
                    } else if(TYPE_LONG.equals(type)) {
                        try{
                            ps.setLong(j0 + 1, Long.parseLong(val));
                        }catch(Exception e){
                            ps.setObject(j0 + 1, null);
                        }
                    } else if(TYPE_FLOAT.equals(type)) {
                        try{
                            ps.setBigDecimal(j0 + 1,
                                             new java.math.BigDecimal(val));
                        }catch(Exception e){
                            ps.setObject(j0 + 1, null);
                        }
                    } else if(TYPE_STRING.equals(type)) {
                        ps.setString(j0 + 1, val);
                    }
                }else{
                    if(TYPE_STRING.equals(type)){
                        ps.setString(j0 + 1, "");
                    }else{
                        ps.setObject(j0 + 1, null);
                    }
                }
            }
        }
        result = ps.executeUpdate();

        //System.out.println("-------------------result----end-----------="+result);

        return result;
    }

    /**
     * 更新计算字段
     * @param infoId String
     * @param tableId String
     * @param table String
     * @param dbopt DbOpt
     * @param saveOrUpdate String 0-新增 1-修改
     * @param request HttpServletRequest
     * @throws Exception
     * @return int
     */
    private void updateComputeField(String infoId, String tableId,
                                    String table, DbOpt dbopt,
                                    String saveOrUpdate,
                                    HttpServletRequest request) throws
        Exception {
        int result = 0;
        String DB_TYPE = dbopt.dbtype;

        String computeSqlHead = " update " + table + " set "; //计算字段保存SQL

        //取得计算字段的各字段
        String[] cmp_fields = getComputeFields(tableId, dbopt);

        //计算字段SQL生成（保存计算字段的值）
        if(DB_TYPE.indexOf("oracle") >= 0) {
            //获取计算字段的值并保存到数据库
            String[] computeField = request.getParameterValues(
                "computeField"); //计算字段的字段名称
            String[] computeFieldValues = request.getParameterValues(
                "computeFieldValue"); //计算字段的值
            if(computeField != null && computeField.length > 0) { //包含多个计算字段
                for(int k = 0; k < computeField.length; k++) {
                    if(computeFieldValues[k] == null ||
                       computeFieldValues[k].trim().length() < 1) {
                        continue;
                    }
                    //读取计算字段的属性
                    String type = dbopt.executeQueryToStr(
                        "select field_type from tfield,ttable where field_name='" +
                        computeField[k] +
                        "' and field_table=table_id and table_name='" +
                        table + "'");
                    //判断计算字段是字符型还是数字类型
                    if(type.trim().equals("1000000") ||
                       type.trim().equals("1000001")) { //数字类型
                        computeSqlHead += computeField[k] + "=";
                        //保留两位小数（四舍五入）
                        computeSqlHead += "round(" +
                            replaceComputeFields(cmp_fields,
                                                 computeFieldValues[k], 0) +
                            ",2),";
                    } else {
                        if("''".equals(computeFieldValues[k])) {
                            computeFieldValues[k] = "";
                        }
                        computeSqlHead += computeField[k] + "=";
                        computeSqlHead += "'" +
                            computeFieldValues[k].replaceAll("'", "＇").
                            replaceAll("&", "'||'&'||'") + "',";
                    }
                }
            } else { //只有一个计算字段
                String cmpField = request.getParameter("computeField");
                String cmpFieldValues = request.getParameter(
                    "computeFieldValue");
                if(cmpField != null && cmpField.trim().length() > 0 &&
                   !cmpField.toUpperCase().equals("NULL") &&
                   (cmpFieldValues != null &&
                    cmpFieldValues.trim().length() > 0)) {
                    //读取计算字段的类型
                    String type = dbopt.executeQueryToStr(
                        "select field_type from tfield,ttable where field_name='" +
                        cmpField +
                        "' and field_table=table_id and table_name='" +
                        table + "'");
                    if(type.trim().equals("1000000") ||
                       type.trim().equals("1000001")) { //数值类型
                        computeSqlHead += cmpField + "=";
                        computeSqlHead += "round(" +
                            replaceComputeFields(cmp_fields, cmpFieldValues,
                                                 0) + ",2),";
                    } else { //字符型
                        if("''".equals(cmpFieldValues)) {
                            cmpFieldValues = "";
                        }
                        computeSqlHead += cmpField + "=";
                        computeSqlHead += "'" +
                            cmpFieldValues.replaceAll("'",
                            "＇").replaceAll("&", "'||'&'||'") + "',";
                    }
                }
            }

        } else if(DB_TYPE.indexOf("sqlserver") >= 0 ||
                  DB_TYPE.indexOf("mysql") >= 0) {
            //获取计算字段的值并保存到数据库
            String[] computeField = request.getParameterValues(
                "computeField");
            String[] computeFieldValues = request.getParameterValues(
                "computeFieldValue");
            if(computeField != null && computeField.length > 0) {
                for(int k = 0; k < computeField.length; k++) {
                    if(computeFieldValues[k] == null ||
                       computeFieldValues[k].trim().length() < 1) {
                        continue;
                    }
                    String type = dbopt.executeQueryToStr(
                        "select field_type from tfield,ttable where field_name='" +
                        computeField[k] +
                        "' and field_table=table_id and table_name='" +
                        table + "'");
                    if(type.trim().equals("1000000") ||
                       type.trim().equals("1000001")) {
                        computeSqlHead += computeField[k] + "=";
                        if(DB_TYPE.indexOf("sqlserver") >= 0) {
                            computeSqlHead += "round(" +
                                replaceComputeFields(cmp_fields,
                                computeFieldValues[k], 1) + ",2),";
                        } else {
                            computeSqlHead += "round(" +
                                replaceComputeFields(cmp_fields,
                                computeFieldValues[k], 2) + ",2),";
                        }

                    } else {
                        if("''".equals(computeFieldValues[k])) {
                            computeFieldValues[k] = "";
                        }
                        computeSqlHead += computeField[k] + "=";
                        if(DB_TYPE.indexOf("sqlserver") >= 0) {
                            computeSqlHead += "N'" +
                                computeFieldValues[k].
                                replaceAll("'", "＇") + "',";
                        } else {
                            computeSqlHead += "'" +
                                computeFieldValues[k].
                                replaceAll("'", "＇") + "',";

                        }
                    }
                }

            } else {
                String cmpField = request.getParameter("computeField");
                String cmpFieldValues = request.getParameter(
                    "computeFieldValue");
                if(cmpField != null && cmpField.trim().length() > 0 &&
                   !cmpField.toUpperCase().equals("NULL") &&
                   (cmpFieldValues != null &&
                    cmpFieldValues.trim().length() > 0)) {
                    String type = dbopt.executeQueryToStr(
                        "select field_type from tfield,ttable where field_name='" +
                        cmpField +
                        "' and field_table=table_id and table_name='" +
                        table + "'");
                    if(type.trim().equals("1000000") ||
                       type.trim().equals("1000001")) {
                        computeSqlHead += cmpField + "=";
                        if(DB_TYPE.indexOf("sqlserver") >= 0) {
                            computeSqlHead += "round(" +
                                replaceComputeFields(cmp_fields,
                                cmpFieldValues, 1) + ",2),";
                        } else {
                            computeSqlHead += "round(" +
                                replaceComputeFields(cmp_fields,
                                cmpFieldValues, 2) + ",2),";
                        }
                    } else {
                        if("''".equals(cmpFieldValues)) {
                            cmpFieldValues = "";
                        }
                        computeSqlHead += cmpField + "=";
                        if(DB_TYPE.indexOf("sqlserver") >= 0) {
                            computeSqlHead += "N'" +
                                cmpFieldValues.replaceAll("'", "＇") +
                                "',";
                        } else {
                            computeSqlHead += "'" +
                                cmpFieldValues.replaceAll("'", "＇") +
                                "',";
                        }
                    }
                }
            }
        }

        //执行SQL保存计算字段的值
        if(!computeSqlHead.trim().endsWith("set")) {
            String sql = computeSqlHead.substring(0,
                                                  computeSqlHead.length() - 1) +
                " where " + table + "_id=" + infoId;

            logger.debug("compute SQL:"+sql);

            result = dbopt.executeUpdate(sql);
            result = dbopt.executeUpdate(sql);
        }
    }

    public String[] splitStr(String str, String regex) {
        return str.split(regex);
    }
    
    /**
     * 删除自定义数据表数据
     * 
     * @param formId
     * @param infoIds
     * @return
     */
    public String deleteFormData(String formId, String infoIds){
        logger.debug("删除[deleteFormData]...");
        
        String result = "0";
        DbOpt dbopt = new DbOpt();
        try {
            
            String mTableName = dbopt.executeQueryToStr("select t.table_name from ttable t where t.table_id in (select table_id from ez_form_table where is_main_table='1' and form_id="+formId+")");
            
            if (!CommonUtils.isEmpty(mTableName) && !CommonUtils.isEmpty(infoIds)) {
                if(infoIds.endsWith(",")){
                    infoIds = infoIds.substring(0, infoIds.length()-1);
                }
                dbopt.executeUpdate("delete from " + mTableName + " where " + mTableName + "_id in (" + infoIds + ")");
                
                String[][] subTableName = dbopt.executeQueryToStrArr2("select t.table_name from ttable t where t.table_id in (select table_id from ez_form_table where is_main_table='0' and form_id="+formId+")", 1);
                if(subTableName != null){
                    for(int i=0; i<subTableName.length; i++){
                        dbopt.executeUpdate("delete from " + subTableName[i] + " where " + subTableName[i] + "_foreignkey in (" + infoIds + ")");
                    }
                }
                result = "1";
            }

        } catch (Exception ex) {                
            ex.printStackTrace();
        }finally{
            try {
                if (dbopt != null)dbopt.close();
            } catch (Exception err) {
                err.printStackTrace();
            }
        }
        
        return result;
    }
}
