package com.whir.ezoffice.ezform.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.ezoffice.ezform.util.FormHelper;

/**
 * UIBD
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 10.3.0.0
 */
public class UIBD {
    
    private static Logger logger = Logger.getLogger(UIBD.class
            .getName());

    public UIBD() {
    }

    /**
     * 获取表单关联的所有字段
     * @param formId String
     * @return String[][]
     * 返回值：
     *      0-字段ID
     *      1-字段Code
     *      2-字段显示名称
     *      3-字段系统名称
     *      4-数据表ID
     *      5-字段类型
     *      6-字段长度
     */
    public String[][] getFormAllFields(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,"; //0
            querySql += " d.field_code,"; //1
            querySql += " d.field_desname,"; //2
            querySql += " d.field_name,"; //3
            querySql += " d.field_table,"; //4
            querySql += " d.field_type,"; //5
            querySql += " d.field_len,"; //6
            querySql += " d.field_index,"; //7
            querySql += " d.field_null,"; //8
            querySql += " d.field_sort,"; //9
            querySql += " d.field_only,"; //10
            querySql += " d.field_default,"; //11
            querySql += " d.field_des,"; //12
            querySql += " d.field_list,"; //13
            querySql += " d.field_width,"; //14
            querySql += " d.field_show,"; //15
            querySql += " d.field_value,"; //16
            querySql += " d.field_codevalue,"; //17
            querySql += " d.field_percent,"; //18
            querySql += " d.field_intype,"; //19
            querySql += " d.field_ds,"; //20
            querySql += " d.field_sql,"; //21
            querySql += " d.field_def_setting,"; //22
            querySql += " e.table_name,"; //23
            querySql += " f.show_id,"; //24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";// + formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getFormAllFields-----------sql--------:\n" +
                               querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 获取表单关联的所有主表字段
     * @param formId String
     * @return String[][]
     * 返回值：
     *      0-字段ID
     *      1-字段Code
     *      2-字段显示名称
     *      3-字段系统名称
     *      4-数据表ID
     *      5-字段类型
     *      6-字段长度
     */
    public String[][] getFormFields(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='1'";//主表
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getFormFields-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 自定义模块portal取新自定义表单提醒字段为日期的数据
     * @param formId
     * @return
     */
    /*public String[][] getNewFormFieldsByPortal(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_name,";//1
            querySql += " d.field_desname";//2

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d ";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_show in (107,109)";
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 2, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getNewFormFieldsByPortal-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }*/
    
    /**
     * 自定义模块portal取自定义表单提醒字段为日期的数据
     * @param formId
     * @return
     */
    /*public String[][] getFormFieldsByPortal(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " a.field_id,";//0
            querySql += " a.field_name,";//1
            querySql += " a.field_desname";//2

            querySql += " from ttable b,tfield a,tpage c,tarea d ";
            querySql += " where c.page_id=?";//+formId;
            querySql += " and a.field_table = b.table_id ";
            querySql += " and b.table_name = d.area_table ";
            querySql += " and d.page_id = c.page_id ";
            querySql += " and a.field_show in (107,109)";
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 2, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getFormFieldsByPortal-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }*/
    
    /**
     * 获取表单关联的所有子表字段
     * @param formId String
     * @return String[][]
     * 返回值：
     *      0-字段ID
     *      1-字段Code
     *      2-字段显示名称
     *      3-字段系统名称
     *      4-数据表ID
     *      5-字段类型
     *      6-字段长度
     */
    public String[][] getForeignFields(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='0'";//子表
            //querySql += " order by b.table_id, c.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getForeignFields-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 根据formId和子表名称获取表单关联的子表字段
     * @param formId String
     * @param tableName String
     * @return String[][]
     */
    public String[][] getForeignFieldsByTableName(String formId, String tableName) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " e.table_totfield";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='0'";//子表
            querySql += " and e.table_name=?";//'"+tableName+"'";
            querySql += " order by d.field_sequence, d.field_id";
            
            Object[] sqlParams = {formId, tableName};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getForeignFieldsByTableName-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 获取表单关联的指定子表所有字段
     * @param formId String
     * @param tableName String
     * @param domainId String
     * @return String[][]
     */
    public String[][] getForeignFields(String formId, String tableName, String domainId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='0'";//子表
            querySql += " and e.table_name=?";//'"+tableName+"'";
            querySql += " and a.domain_id=?";//+domainId;
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId, tableName, domainId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getForeignFields-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    
    /**
     * 获取表单关联的指定子表所有字段
     * @param formId String
     * @param tableName String
     * @param domainId String
     * @return String[][]
     */
    public String[][] getForeignFieldsAll(String formId,  String domainId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='0'";//子表
           // querySql += " and e.table_name=?";//'"+tableName+"'";
            querySql += " and a.domain_id=?";//+domainId;
            //querySql += " order by c.sort_no";
            
            Object[] sqlParams = {formId,  domainId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getForeignFields-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 取得主表字段信息
     * @param formId String
     * @return String[][]
     */
    public String[][] getFieldInfo(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "select distinct ";
        querySql += " d.field_name,"; //0
        querySql += " d.field_only,"; //1
        querySql += " d.field_show,"; //2
        querySql += " d.field_id,"; //3
        querySql += " d.field_value"; //4

        querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
        querySql += " where a.form_id=?";// + formId;
        querySql += " and a.form_id=b.form_id";
        querySql += " and b.id=c.form_table_id";
        querySql += " and b.table_id=e.table_id";
        querySql += " and c.field_id=d.field_id";
        querySql += " and d.field_table=e.table_id";
        querySql += " and d.field_show=f.show_id";
        querySql += " and b.is_main_table='1'"; //主表
        
        Object[] sqlParams = {formId};

        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            result = dbopt.executeQueryToStrArr2(querySql, 5, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getFieldInfo-----------sql--------:\n" +
                               querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {}
            return result;
        }
    }

    /**
     * 获得表单关联的所有子表
     * @param formId String
     * @return String[]
     */
    public String[] getForeignTables(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " e.table_name";//0

            querySql += " from ez_form a, ez_form_table b, ttable e";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and b.is_main_table='0'";//子表
            //querySql += " order by b.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr1(querySql, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getForeignTables-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 获取主表ID和系统名称
     * @param formId String
     * @return String[][]
     */
    public String[][] getTableIDAndName(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " e.table_id,";//0
            querySql += " e.table_name";//1

            querySql += " from ez_form a, ez_form_table b, ttable e";
            querySql += " where a.form_id=?";//+formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and b.is_main_table='1'";//主表
            //querySql += " order by b.sort_no";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getTableIDAndName-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 根据formCode获取主表ID和系统名称
     * @param formCode String
     * @return String[][]
     */
    public String[][] getTableIDAndNameByFormCode(String formCode) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " e.table_id,";//0
            querySql += " e.table_name";//1

            querySql += " from ez_form a, ez_form_table b, ttable e";
            querySql += " where a.form_code=?";//'"+formCode+"'";
            querySql += " and a.form_type='0'";
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and b.is_main_table='1'";//主表
            //querySql += " order by b.sort_no";
            
            Object[] sqlParams = {formCode};

            result = dbopt.executeQueryToStrArr2(querySql, sqlParams);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getTableIDAndNameByFormCode-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 取得所有金额大写的指定字段
     * @param fieldId String
     * @param domainId String
     * @return String[][]
     */
    public String[][] getField302(String fieldId, String domainId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            result = getField302(fieldId, domainId, dbopt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public String[][] getField302(String fieldId, String domainId, DbOpt dbopt) throws Exception {
        String[][] result = null;

        String querySql = " select field_name, field_value, field_table ";
        querySql += "  from tfield ";
        querySql += " where field_show=302 and field_table in (select field_table from tfield where FIELD_ID=? and domain_id=?) ";

        logger.debug("getField302-----------sql--------:\n"+querySql);
        
        Object[] sqlParams = {fieldId, domainId};
        
        result = dbopt.executeQueryToStrArr2(querySql, 3, sqlParams);
        return result;
    }
    
    public String[][] getField302ByTableId(String tableId, String domainId, DbOpt dbopt) throws Exception {
        String[][] result = null;

        String querySql = " select field_name, field_value, field_table ";
        querySql += "  from tfield ";
        querySql += " where field_show=302 and field_table=?";// + tableId;

        logger.debug("getField302-----------sql--------:\n"+querySql);
        
        Object[] sqlParams = {tableId};
        
        result = dbopt.executeQueryToStrArr2(querySql, 3, sqlParams);
        return result;
    }
    
    public String[][] getField203(String fieldName, String fieldTable, String domainId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = getField203(fieldName, fieldTable, domainId, dbopt);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public String[][] getField203(String fieldName, String fieldTable, String domainId, DbOpt dbopt) throws Exception {
        String[][] result = null;

        String querySql = " select field_name, field_value, field_table ";
        querySql += "  from tfield ";
        querySql += " where field_show=203 and field_table =? and field_value like ? ";
        
        logger.debug("getField203-----------sql--------:\n"+querySql);
        
        Object[] sqlParams = {fieldTable, "%"+fieldName+"%"};

        result = dbopt.executeQueryToStrArr2(querySql, 3, sqlParams);
        return result;
    }
    
    /**
     * 取得表单中的所有合计字段
     * @param tableId String
     * @return String
     */
    public String getComputeFieldByTableId(String tableId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result = "";
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += " select distinct a.field_name, a.field_value ";
            querySql += "  from tfield a ";
            querySql += " where field_show=203 and a.field_table=?";//+tableId;
            
            Object[] sqlParams = {tableId};

            String[][] computeField = dbopt.executeQueryToStrArr2(querySql, sqlParams);
            if (computeField != null) {
                for (int i = 0; i < computeField.length; i++) {
                    result += computeField[i][1] + ",";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getComputeFieldByTableId-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 取得自定数据表值
     * @param infoId String
     * @param tableName String
     * @param fieldArr String[][]
     * @return Map
     */
    public Map getMainDataById_bak(String infoId, String tableName, String[][] fieldArr) {
        Map result = new HashMap(0);
        if(fieldArr == null){
            return result;
        }
        String querySql = "";
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += " select ";
            for(int i=0; i<fieldArr.length; i++){
                querySql += fieldArr[i][3] + (i<fieldArr.length-1?",":"");
            }
            querySql += "  from " + tableName + " ";
            querySql += " where " + tableName + "_ID = " + infoId;

            result = dbopt.executeQueryToMap(querySql);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getMainDataById-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public Map getMainDataById(String infoId, String tableName, String[][] fieldArr, String wfModuleId) {
        Map result = new HashMap(0);
        if(fieldArr == null){
            return result;
        }
        
        if(CommonUtils.isEmpty(tableName)){
            return result;
        }

        boolean isBussinessModule = false;//是否业务固定数据表
        if(!CommonUtils.isEmpty(wfModuleId) && !"1".equals(wfModuleId) && !tableName.startsWith("whir$")/*自定义数据表+业务定制，目前政务版会议管理用到*/){
            isBussinessModule = true;
        }
        
        String querySql = "";
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            
            String primaryKey = tableName + "_ID";
            if(isBussinessModule){
                String immoform_primarykey = dbopt.executeQueryToStr("select immoform_primarykey from wf_immobilityform where wf_module_id="+wfModuleId);
                if(!CommonUtils.isEmpty(immoform_primarykey)){
                    primaryKey = immoform_primarykey;
                }
            }

            querySql += " select ";
            
            int flen = fieldArr.length;
            for(int i=0; i<flen; i++){
                querySql += fieldArr[i][3] + (i<flen-1?",":"");
            }
            querySql += "  from " + tableName + " ";
            querySql += " where " + primaryKey + " = " + infoId;

            result = dbopt.executeQueryToMap(querySql);
            
            //处理业务字段值-存储在不同的字段中 转换成符合自定数据表的存储格式
            if(isBussinessModule){
                changeValueMap(result, infoId, tableName, fieldArr, wfModuleId, dbopt);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getMainDataById-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    /**
     * 处理业务字段值-存储在不同的字段中 转换成符合自定数据表的存储格式
     * @param result
     * @param infoId
     * @param tableName
     * @param fieldArr
     * @param wfModuleId
     * @param dbopt
     * @throws Exception
     */
    private void changeValueMap(Map result, String infoId, String tableName, String[][] fieldArr, String wfModuleId, DbOpt dbopt) throws Exception {
        if("15".equals(wfModuleId)){//会议管理-会议申请
            int flen = fieldArr.length;
            for(int i=0; i<flen; i++){
                String field_name = fieldArr[i][3];
                //OA_BOARDROOMAPPLY 申请表
                //主持人：EMCEENAME   EMCEE
                if("emceename".equals(field_name)){
                    String sql = "select emceename, emcee from " + tableName + " where boardroomapplyid="+infoId;
                    String[][] arr = dbopt.executeQueryToStrArr2(sql, 2);
                    if(arr != null && arr.length > 0){
                        if(!CommonUtils.isEmpty(arr[0][0]) && !CommonUtils.isEmpty(arr[0][1])){
                            result.put("emceename", arr[0][0]+";"+arr[0][1]);
                        }
                    }
                    
                //出席领导：ATTENDEELEADER  ATTENDEELEADERID
                }else if("attendeeleader".equals(field_name)){
                    String sql = "select attendeeleader, attendeeleaderid from " + tableName + " where boardroomapplyid="+infoId;
                    String[][] arr = dbopt.executeQueryToStrArr2(sql, 2);
                    if(arr != null && arr.length > 0){
                        if(!CommonUtils.isEmpty(arr[0][0]) && !CommonUtils.isEmpty(arr[0][1])){
                            result.put("attendeeleader", arr[0][0]+";"+arr[0][1]);
                        }
                    }
                    
                //会议记录人：NOTEPERSONNAME     NOTEPERSON
                }else if("notepersonname".equals(field_name)){
                    String sql = "select notepersonname, noteperson from " + tableName + " where boardroomapplyid="+infoId;
                    String[][] arr = dbopt.executeQueryToStrArr2(sql, 2);
                    if(arr != null && arr.length > 0){
                        if(!CommonUtils.isEmpty(arr[0][0]) && !CommonUtils.isEmpty(arr[0][1])){
                            result.put("notepersonname", arr[0][0]+";"+arr[0][1]);
                        }
                    }
                    
                //单位收文员：SWPERSON  SWPERSONID
                }else if("swperson".equals(field_name)){
                    String sql = "select swperson, swpersonid from " + tableName + " where boardroomapplyid="+infoId;
                    String[][] arr = dbopt.executeQueryToStrArr2(sql, 2);
                    if(arr != null && arr.length > 0){
                        if(!CommonUtils.isEmpty(arr[0][0]) && !CommonUtils.isEmpty(arr[0][1])){
                            result.put("swperson", arr[0][0]+";"+arr[0][1]);
                        }
                    }
                    
                //附件：OA_BDROOMAPPACCESSORY 附件表
                }else if("attachments".equals(field_name)){
                    String sql = "select savename, name from OA_BDROOMAPPACCESSORY where boardroomapplyid="+infoId+" order by bdroomappaccessoryid";
                    String[][] arr = dbopt.executeQueryToStrArr2(sql, 2);
                    if(arr != null && arr.length > 0){
                        String sn0 = "";
                        String sn1 = "";
                        for(int i0=0; i0<arr.length; i0++){
                            String savename = arr[i0][0];
                            String name = arr[i0][1];
                            if(!CommonUtils.isEmpty(savename) && !CommonUtils.isEmpty(name)){
                                sn0 += savename + ",";
                                sn1 += name + ",";
                            }
                        }
                        
                        result.put("attachments", !CommonUtils.isEmpty(sn0)?sn0+";"+sn1:"");
                    }
                }
            }
        }
    }
    
    //--------------------------------------------------------------------------
    //DealWithCommentClass-批示人和日期-start
    //--------------------------------------------------------------------------
    public String[][] getDealWithUserAndDate(String formId, String recordId, String fieldName){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String sql = "select b.empname, a.dealwithtime, a.dealwithemployeecomment from wf_dealwithcomment a,org_employee b where a.wf_dealwith_id in (select wf_dealwith_id from wf_dealwith where databasetable_id="+formId+" and databaserecord_id="+recordId+") and a.dealwithemployee_id=b.emp_id and a.commentfield='"+fieldName+"' order by dealwithtime desc";
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStrArr2(sql,3);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    //Word编辑
    public String XXXgetTextField(String formId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result = null;
        String sql = "select field_name from tfield where field_show=116 and field_table in (select table_id from ttable where table_name in (select area_table from tarea t where area_name='form1' and page_id="+formId+"))";
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStr(sql);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public String XXXgetTextFieldValue(String formId, String fieldName, String recordId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result = null;
        String sql = "select area_table from tarea t where area_name='form1' and page_id="+formId;
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            String tableName = dbopt.executeQueryToStr(sql);
            String sql2 = "select "+fieldName+" from "+tableName+" where "+tableName+"_id="+recordId;
            result = dbopt.executeQueryToStr(sql2);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public String[][] getFieldType(String fieldId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result[][] = null;
        String sql = "select field_show, field_value from tfield where field_id=?";//+fieldId;
        
        Object[] sqlParams = {fieldId};
        
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStrArr2(sql,2, sqlParams);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    public String getHtmlFor701(String fieldId, String fieldName, String userName, String commentDate){
        String result="";
        result += "if(document.getElementById('"+fieldId+"-"+fieldName+"')){";
        //result += "document.getElementById('"+fieldId+"-"+fieldName+"').style.borderBottom='1px solid red';";
        //result += "document.getElementById('"+fieldId+"-"+fieldName+"').align='center';";
        //result += "document.getElementById('"+fieldId+"-"+fieldName+"').style.width='280';";
        if(userName==null||commentDate==null){
            result += "document.getElementById('"+fieldId+"-"+fieldName+"').innerHTML='&nbsp;'";
        }else{
            result += "document.getElementById('"+fieldId+"-"+fieldName+"').innerHTML='<font color=black>"+userName+"&nbsp;&nbsp;"+commentDate+"</font>'";
        }
        result += "}";
        return result;
    }

    public String getHtmlFor701Arr(String fieldId, String fieldName, String[][] dealWithUserAndDateArr){
        String result="";
        result += "if(document.getElementById('"+fieldId+"-"+fieldName+"')){";
        //result += "document.getElementById('"+fieldId+"-"+fieldName+"').style.borderBottom='1px solid red';";
        result += "document.getElementById('"+fieldId+"-"+fieldName+"').align='left';";
        //result += "document.getElementById('"+fieldId+"-"+fieldName+"').style.width='280';";
        String innerHTML="";
        for(int i=0; i<dealWithUserAndDateArr.length; i++){
            String userName = dealWithUserAndDateArr[i][0];
            String commentDate = dealWithUserAndDateArr[i][1].substring(0,10);
            if(i==0){
                //innerHTML += "<div style=\"height:38;border-top:1px solid #ED608B;border-bottom:1px solid #ED608B\">&nbsp;&nbsp;<font color=black>"+userName+"&nbsp;&nbsp;&nbsp;&nbsp;"+commentDate+"</font></div>";
                innerHTML += "<div>&nbsp;&nbsp;<font color=black>"+userName+"&nbsp;&nbsp;&nbsp;&nbsp;"+commentDate+"</font></div>";
            }else{
                //innerHTML += "<div style=\"height:38;border:0px solid #ED608B\">&nbsp;&nbsp;<font color=black>"+userName+"&nbsp;&nbsp;"+commentDate+"</font></div>";
                innerHTML += "<div>&nbsp;&nbsp;<font color=black>"+userName+"&nbsp;&nbsp;"+commentDate+"</font></div>";
            }
        }
        result += "document.getElementById('"+fieldId+"-"+fieldName+"').innerHTML='"+innerHTML+"'";
        result += "}";
        return result;
    }
    //--------------------------------------------------------------------------
    //DealWithCommentClass-批示人和日期-end
    //--------------------------------------------------------------------------

    /**
     * 取得自定义字段的值
     * @param fieldId String 自定义字段ID
     * @return List
     */
    public List getDataWithFieldId(String fieldId){
        if(fieldId==null||"".equals(fieldId))
            return null;

        List list = new ArrayList();
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;

        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            
            Object[] sqlParams = {fieldId};

            String[][] t = dbopt.executeQueryToStrArr2("select field_show, field_value from tfield where field_id=?", 2, sqlParams);
            if(t==null || t.length<1)
                return null;

            String showType = t[0][0];
            String temp = t[0][1];
            if("103".equals(showType)) { //单选
                if(temp.startsWith("@")) {
                    //表示从数据库中选择单选项
                    String table = temp.substring(temp.indexOf("][") + 2,
                                                  temp.length() - 1);
                    String[][] data = null;
                    //从数据库中提取数据
                    try {
                        data = dbopt.executeQueryToStrArr2("select " +
                            table.substring(0, table.indexOf(".")) +
                            "_id," +
                            table.substring(table.indexOf(".") + 1,
                                            table.length()) + " from " +
                            table.
                            substring(0, table.indexOf(".")));
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    //根据提取的数据将数据以单选类型显示出来
                    if(data != null) {
                        for(int i = 0; i < data.length; i++) {
                            String[] sArr = new String[2];
                            sArr[0] = data[i][0];
                            sArr[1] = data[i][1];
                            list.add(sArr);
                        }
                    }

                } else {
                    String[] tempArr = temp.split(";");
                    for(int i = 0; i < tempArr.length; i++) {
                        if(tempArr[i] != null && tempArr[i].trim().length() > 0 &&
                           tempArr[i].indexOf("/") > 0 &&
                           tempArr[i].indexOf("/") < tempArr[i].length() - 1) {
                            String[] sArr = new String[2];
                            sArr[0] = tempArr[i].split("/")[0];
                            sArr[1] = tempArr[i].split("/")[1];
                            list.add(sArr);
                        } else if(tempArr[i] != null &&
                                  tempArr[i].trim().length() > 0) {
                            String[] sArr = new String[2];
                            sArr[0] = tempArr[i];
                            sArr[1] = tempArr[i];
                            list.add(sArr);
                        }
                    }
                }

            } else if("105".equals(showType)) { //下拉框
                if(temp.startsWith("@")) {
                    //表示从数据库中选择单选项
                    String table = temp.substring(temp.indexOf("][") + 2,
                                                  temp.length() - 1);
                    String[][] data = null;
                    //从数据库中提取数据
                    try {
                        String col = table.substring(table.indexOf(".") + 1,
                            table.length());
                        data = dbopt.executeQueryToStrArr2("select " +
                            table.substring(0, table.indexOf(".")) + "_id," +
                            col + " from " +
                            table.substring(0, table.indexOf(".")) +
                            " order by " + col);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    //根据提取的数据将数据以单选类型显示出来
                    if(data != null) {
                        for(int i = 0; i < data.length; i++) {
                            String[] sArr = new String[2];
                            sArr[0] = data[i][0];
                            sArr[1] = data[i][1];
                            list.add(sArr);
                        }
                    }

                    //自动关联两个数据库中相同的字段值
                } else if(temp.startsWith("$@")) {
                    //表示从数据库中选择单选项
                    String table = temp.substring(temp.indexOf("][") + 2,
                                                  temp.length() - 1);
                    String[][] data = null;
                    //从数据库中提取数据
                    try {
                        String col = table.substring(table.indexOf(".") + 1,
                            table.length());
                        data = dbopt.executeQueryToStrArr2("select " +
                            table.substring(0, table.indexOf(".")) + "_id," +
                            col + " from " +
                            table.substring(0, table.indexOf(".")) +
                            " order by " + col);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    //根据提取的数据将数据以单选类型显示出来
                    if(data != null) {
                        for(int i = 0; i < data.length; i++) {
                            String[] sArr = new String[2];
                            sArr[0] = data[i][0];
                            sArr[1] = data[i][1];
                            list.add(sArr);
                        }
                    }

                } else { //解析预先设定的值，并生成脚本语言
                    String[] tempArr = temp.split(";");
                    for(int i = 0; i < tempArr.length; i++) {
                        if(tempArr[i] != null && tempArr[i].trim().length() > 0 &&
                           tempArr[i].indexOf("/") >= 0 &&
                           tempArr[i].indexOf("/") < tempArr[i].length() - 1) {
                            String[] sArr = new String[2];
                            sArr[0] = tempArr[i].split("/")[0];
                            sArr[1] = tempArr[i].split("/")[1];
                            list.add(sArr);

                            //形式二：[显示文字];[显示文字];...例：万户;汉斯;华为。
                        } else if(tempArr[i] != null &&
                                  tempArr[i].trim().length() > 0) {
                            String[] sArr = new String[2];
                             sArr[0] = tempArr[i];
                             sArr[1] = tempArr[i];
                             list.add(sArr);
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

        return list;
    }

    /**
     * 取对应字段批示意见
     * @param formId String 表单ID
     * @param recordId String 记录ID
     * @param fieldName String 对应字段
     * @return String[][]
     */
    public String[][] getDealWithComment(String formId, String recordId, String fieldName){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String sql = "select b.empname, a.dealwithtime, a.dealwithemployeecomment from wf_dealwithcomment a,org_employee b where a.wf_dealwith_id in (select wf_dealwith_id from wf_dealwith where databasetable_id=? and databaserecord_id=?) and a.dealwithemployee_id=b.emp_id and a.commentfield=? order by dealwithtime asc";
        
        Object[] sqlParams = {formId, recordId, fieldName};
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStrArr2(sql,3, sqlParams);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 根据workId取得工作流相应信息
     * @param workId String
     * @return List
     */
    public List getDetailParamByWorkId(String workId) {
        Map _workMap = new HashMap(0);
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String sql = "select ";
        sql += " WORKFILETYPE, "; //0
        sql += " WORKCURSTEP, "; //1
        sql += " WORKTITLE, "; //2
        sql += " WORKDEADLINE, "; //3
        sql += " WORKSUBMITPERSON, "; //4
        sql += " WORKSUBMITTIME, "; //5
        sql += " WORKTYPE, "; //6
        sql += " WORKACTIVITY, "; //7
        sql += " WORKTABLE_ID, "; //8
        sql += " WORKRECORD_ID, "; //9
        sql += " WF_WORK_ID, "; //10
        sql += " WORKSUBMITPERSON, "; //11
        sql += " WF_SUBMITEMPLOYEE_ID, "; //12
        sql += " WORKALLOWCANCEL, "; //13
        sql += " WORKPROCESS_ID, "; //14
        sql += " WORKSTEPCOUNT, "; //15
        sql += " WORKMAINLINKFILE, "; //16
        sql += " WORKSUBMITTIME, "; //17
        sql += " WORKCURSTEP, "; //18
        sql += " CREATORCANCELLINK, "; //19
        sql += " ISSTANDFORWORK, "; //20
        sql += " STANDFORUSERID, "; //21
        sql += " STANDFORUSERNAME, "; //22
        sql += " WORKCREATEDATE, "; //23
        sql += " SUBMITORG, "; //24
        sql += " WORKDONEWITHDATE, "; //25
        sql += " EMERGENCE, "; //26
        sql += " INITACTIVITY, "; //27
        sql += " INITACTIVITYNAME, "; //28
        sql += " TRANTYPE, "; //29
        sql += " TRANFROMPERSONID, "; //30
        sql += " PROCESSDEADLINEDATE, "; //31
        sql += " WF_CUREMPLOYEE_ID, "; //32
        sql += " DOMAIN_ID "; //33
        sql += "  from WF_WORK ";
        sql += " where WF_WORK_ID = ?";// + workId;
        
        Object[] sqlParams = {workId};
        
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            _workMap = dbopt.executeQueryToMap(sql, sqlParams);
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex1) {}
        }

        String creatorcancellink = (String)_workMap.get("creatorcancellink");
        String workstepcount = (String)_workMap.get("workstepcount");
        String processdeadlinedate = (String)_workMap.get("processdeadlinedate");
        String workallowcancel = (String)_workMap.get("workallowcancel");
        String workdonewithdate = (String)_workMap.get("workdonewithdate");
        String workcreatedate = (String)_workMap.get("workcreatedate");
        String wf_curemployee_id = (String)_workMap.get("wf_curemployee_id");
        String initactivity = (String)_workMap.get("initactivity");
        String tranfrompersonid = (String)_workMap.get("tranfrompersonid");
        String workcurstep = (String)_workMap.get("workcurstep");
        String workmainlinkfile = (String)_workMap.get("workmainlinkfile");
        String worksubmitperson = (String)_workMap.get("worksubmitperson");
        String initactivityname = (String)_workMap.get("initactivityname");
        String worktable_id = (String)_workMap.get("worktable_id");
        String standforuserid = (String)_workMap.get("standforuserid");
        String wf_work_id = (String)_workMap.get("wf_work_id");
        String worksubmittime = (String)_workMap.get("worksubmittime");
        String wf_submitemployee_id = (String)_workMap.get(
            "wf_submitemployee_id");
        String worktype = (String)_workMap.get("worktype");
        String workrecord_id = (String)_workMap.get("workrecord_id");
        String workprocess_id = (String)_workMap.get("workprocess_id");
        String workactivity = (String)_workMap.get("workactivity");
        String submitorg = (String)_workMap.get("submitorg");
        String isstandforwork = (String)_workMap.get("isstandforwork");
        String standforusername = (String)_workMap.get("standforusername");
        String workdeadline = (String)_workMap.get("workdeadline");
        String trantype = (String)_workMap.get("trantype");
        String emergence = (String)_workMap.get("emergence");
        String workfiletype = (String)_workMap.get("workfiletype");
        String worktitle = (String)_workMap.get("worktitle");
        String domain_id = (String)_workMap.get("domain_id");

        List paramList = new ArrayList();
        paramList.add(creatorcancellink != null ? creatorcancellink : "");
        paramList.add(workstepcount != null ? workstepcount : "");
        paramList.add(processdeadlinedate != null ? processdeadlinedate : "");
        paramList.add(workallowcancel != null ? workallowcancel : "");
        paramList.add(workdonewithdate != null ? workdonewithdate : "");
        paramList.add(workcreatedate != null ? workcreatedate : "");
        paramList.add(wf_curemployee_id != null ? wf_curemployee_id : "");
        paramList.add(initactivity != null ? initactivity : "");
        paramList.add(tranfrompersonid != null ? tranfrompersonid : "");
        paramList.add(workcurstep != null ? workcurstep : "");
        paramList.add(workmainlinkfile != null ? workmainlinkfile : "");
        paramList.add(worksubmitperson != null ? worksubmitperson : "");
        paramList.add(initactivityname != null ? initactivityname : "");
        paramList.add(worktable_id != null ? worktable_id : ""); //13
        paramList.add(standforuserid != null ? standforuserid : "");
        paramList.add(wf_work_id != null ? wf_work_id : "");
        paramList.add(worksubmittime != null ? worksubmittime : "");
        paramList.add(wf_submitemployee_id != null ? wf_submitemployee_id : "");
        paramList.add(worktype != null ? worktype : "");
        paramList.add(workrecord_id != null ? workrecord_id : ""); //19
        paramList.add(workprocess_id != null ? workprocess_id : ""); //20
        paramList.add(workactivity != null ? workactivity : ""); //21
        paramList.add(submitorg != null ? submitorg : ""); //22
        paramList.add(isstandforwork != null ? isstandforwork : "");
        paramList.add(standforusername != null ? standforusername : "");
        paramList.add(workdeadline != null ? workdeadline : "");
        paramList.add(trantype != null ? trantype : "");
        paramList.add(emergence != null ? emergence : "");
        paramList.add(workfiletype != null ? workfiletype : "");
        paramList.add(worktitle != null ? worktitle : "");
        //activityClass  当是3时 是自动返回活动

        List activityInfoList = new com.whir.ezoffice.workflow.newBD.WorkFlowBD().
            getActivityClass_multi(worktable_id, workrecord_id,
                                   workactivity);
        String[] activityInfo = (String[])activityInfoList.get(0);
        paramList.add(activityInfo[0] != null ? activityInfo[0] : "");

        return paramList;//contains 31 elements
    }

    /**
     * 根据fieldId取得字段扩展信息
     * @param fieldId String
     * @return String[][]
     */
    public String[][] getFieldExtInfoByFieldId(String fieldId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String sql = "select field_intype, field_ds, field_sql, field_value, field_def_setting, field_show, field_desname, field_name, field_default from tfield where field_id=? ";
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            Object[] param = {fieldId};
            result = dbopt.executeQueryToStrArr2(sql, 9,param);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }
    
    /**
     * 根据wfModuleId获取批示意见字段
     * 
     * @param wfModuleId
     * @return
     */
    public String[][] getCommentFieldsByWfModuleId(String wfModuleId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            String querySql = "select distinct ";
            querySql += " d.field_id,"; //0
            querySql += " d.field_name,"; //1
            querySql += " d.field_def_setting,"; //2
            querySql += " d.field_value,"; //3
            querySql += " d.field_table,"; //4
            querySql += " e.table_name"; //5

            querySql +=
                " from tfield d, ttable e, tshow f";
            querySql += " where e.wf_module_id=?";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and d.field_show=401"; //批示意见
            
            Object[] sqlParams = {wfModuleId};

            result = dbopt.executeQueryToStrArr2(querySql, 6, sqlParams);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 根据formCode取得对应批示意见类型字段
     * @param formId String
     * @return String[][]
     */
    public String[][] getCommentFieldsByFormCode(String formCode){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            String querySql = "select distinct ";
            querySql += " d.field_id,"; //0
            querySql += " d.field_name,"; //1
            querySql += " d.field_def_setting,"; //2
            querySql += " d.field_value,"; //3
            querySql += " d.field_table,"; //4
            querySql += " e.table_name"; //5

            querySql +=
                " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_code=?";//'" + formCode + "'";
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='1'"; //主表
            querySql += " and d.field_show=401"; //批示意见
            
            Object[] sqlParams = {formCode};

            result = dbopt.executeQueryToStrArr2(querySql, 6, sqlParams);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(dbopt!=null)dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 根据签名格式获取对应批示意见字段的签名日期
     * @param commentFieldName String 批示意见字段系统名
     * @param commentDate String 日期字符串
     * @param formCommentFields String[][]
     * @param format String
     * @return String
     */
    public String getCommentDateFormatStr(String commentFieldName,
                                          String commentDate,
                                          String[][] formCommentFields,
                                          String format) {
        if(commentDate==null || "".equals(commentDate.trim()) || "null".equals(commentDate.trim()))return "";
        if(commentDate.indexOf(".")>0){
            commentDate = commentDate.substring(0,commentDate.indexOf("."));
        }

        String nowTime_format = "(" + commentDate + ")";
        if(formCommentFields != null && formCommentFields.length > 0) {
            for(int i0 = 0; i0 < formCommentFields.length; i0++) {
                if(formCommentFields[i0][1].equals(commentFieldName)) {
                    if("1".equals(formCommentFields[i0][2])) { //“签名+日期”
                        nowTime_format = "(" + (commentDate.length()>=10?commentDate.substring(0, 10) : commentDate) +
                            ")";
                        break;
                    } else if("2".equals(formCommentFields[i0][2])) { //“仅签名”
                        nowTime_format = "";
                        break;
                    }else { //“签名+日期时间”
                        nowTime_format = "(" + commentDate + ")";
                        break;
                    }
                }
            }
        }
        return nowTime_format;
    }

    /**
     * 读取字表中所有的计算字段，并生成脚本语言
     * @param formId String
     * @return String
     */
    public String getForeignComputeFieldHTML(String formId) {
        String computeFieldHTML = "";
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;

        String sql = "select distinct ";
        sql += " d.field_name,";
        sql += " d.field_value";
        sql +=
            " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
        sql += " where a.form_id=?";// + formId;
        sql += " and a.form_id=b.form_id";
        sql += " and b.id=c.form_table_id";
        sql += " and b.table_id=e.table_id";
        sql += " and c.field_id=d.field_id";
        sql += " and d.field_table=e.table_id";
        sql += " and d.field_show=f.show_id";
        sql += " and f.show_id=203";
        sql += " and b.is_main_table='0'"; //子表
        //sql += " order by b.table_id, c.sort_no";
        
        Object[] sqlParams = {formId};

        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            String[][] computeField = dbopt.executeQueryToStrArr2(sql, sqlParams);
            
            List oList = new ArrayList();
            if(computeField != null) {
                for(int i = 0; i < computeField.length; i++) {
                    oList.add(computeField[i]);
                    
                    /*computeFieldHTML +=
                        "<input type='hidden' name='computeForeignField' value='" +
                        computeField[i][0]
                        +
                        "'/><input type='hidden' name='computeForeignFieldValue' value='" +
                        computeField[i][1] + "'/>";*/
                }
                
                Collections.sort(oList, new Comparator(){
                    public int compare(Object obj1, Object obj2){
                        String[] o1 = (String[])obj1;
                        String[] o2 = (String[])obj2;
                        
                        if(o1[1].indexOf(o2[0]) != -1){
                            return 1;
                        }
                        
                        return 0;
                    }
                });
                
                for(int i=0; i<oList.size(); i++){
                    String[] cf = (String[])oList.get(i);
                    
                    String computeForeignField = cf[0];
                    String computeForeignFieldValue = cf[1];
                    
                    computeFieldHTML +=
                        "<input type='hidden' name='computeForeignField' value='" +
                        computeForeignField
                        +
                        "'/><input type='hidden' name='computeForeignFieldValue' value='" +
                        computeForeignFieldValue + "'/>";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getForeignComputeFieldHTML-----------sql--------:\n" + sql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {}

            return computeFieldHTML;
        }
    }

    /**
     * 根据表单ID取得计算字段相应的字段JS
     * @param formId String
     * @param fieldName String
     * @return String
     */
    public String getForeignComputeFieldJS(String formId, String fieldName) {
        String ret = "";
        logger.debug("--------------------------getForeignComputeFieldJS 1-------------------------");
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            ret = getForeignComputeFieldJS(formId, fieldName, dbopt);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {}
        }
        return ret;
    }

    public String getForeignComputeFieldJS(String formId, String fieldName, DbOpt dbopt) throws Exception {
        String ret = "";
        logger.debug("--------------------------getForeignComputeFieldJS 2-------------------------");
        String[] fieldArr = getForeignComputeFields(formId, dbopt);
        if(fieldArr != null && fieldArr.length > 0) {
            for(int j = 0; j < fieldArr.length; j++) {
                if(fieldName.equals(fieldArr[j])) {
                    return "setComputeForeignField();";
                }
            }
        }

        return ret;
    }
    
    public String[] getForeignComputeFields(String formId, DbOpt dbopt) throws Exception {
        String[] ret = null;
        logger.debug("--------------------------getForeignComputeFields-------------------------");
        String querySql = "select distinct ";
        querySql += " d.field_name,";
        querySql += " d.field_value";
        querySql +=
            " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
        querySql += " where a.form_id=?";// + formId;
        querySql += " and a.form_id=b.form_id";
        querySql += " and b.id=c.form_table_id";
        querySql += " and b.table_id=e.table_id";
        querySql += " and c.field_id=d.field_id";
        querySql += " and d.field_table=e.table_id";
        querySql += " and d.field_show=f.show_id";
        querySql += " and f.show_id in (203, 803)";
        querySql += " and b.is_main_table='0'"; //子表
        //querySql += " order by b.table_id, c.sort_no";

        logger.debug("getForeignComputeFieldJS-----------sql--------:\n" + querySql);
        String tmpArr = "";
        Object[] sqlParams = {formId};

        String[][] computeField = dbopt.executeQueryToStrArr2(querySql, sqlParams);
        if(computeField != null && computeField.length>0) {
            for(int i = 0; i < computeField.length; i++) {
                if(computeField[i][1] != null &&
                   computeField[i][1].length() > 0) {
                    String fields = computeField[i][1].replaceAll("\\+",
                        ",");
                    fields = fields.replaceAll("/", ",");
                    fields = fields.replaceAll("\\*", ",");
                    fields = fields.replaceAll("-", ",");
                    fields = fields.replaceAll("\\(", ",");
                    fields = fields.replaceAll("\\)", ",");
                    while(fields.indexOf(",,") >= 0) {
                        fields = fields.replaceAll(",,", ",");
                    }
                    /** update by suj  start 2015/6/2
                    String[] fieldArr = fields.split(",");
                    return fieldArr;
                    ***/
                        
                    if(!fields.endsWith(",")){
                    	fields=fields+",";
                    }
                    tmpArr += fields;
                    
                    /** update by suj end 2015/6/2*/
                }
            }
        }
        /** update by suj start 2015/6/2 */
        if(tmpArr.endsWith(",")){
        	tmpArr = tmpArr.substring(0,tmpArr.length()-1);
        }
        ret=tmpArr.split(",");
        /** update by suj end 2015/6/2*/

        return ret;
    }
    
    /**
     * 根据fieldId获取字段信息
     * @param fieldId String
     * @return String[][]
     */
    public String[][] getFieldInfoByFieldId(String fieldId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            String sql = "select a.FIELD_ID,a.FIELD_CODE,a.FIELD_DESNAME,a.FIELD_NAME,a.FIELD_MODEL,a.FIELD_TABLE,a.FIELD_TYPE,a.FIELD_LEN,a.FIELD_REF,a.FIELD_INDEX,a.FIELD_NULL,a.FIELD_SORT,a.FIELD_ONLY,a.FIELD_DEFAULT,a.FIELD_UPDATA,a.FIELD_DES,a.FIELD_LIST,a.FIELD_WIDTH,a.FIELD_SHOW,a.FIELD_VALUE,a.FIELD_LIMIT,a.FIELD_OWNER,a.FIELD_DATE,a.DOMAIN_ID,a.FIELD_HIDE,a.FIELD_SEQUENCE,a.FIELD_QUERY,a.FIELD_CODEVALUE,a.SYS_ATTR,a.FIELD_PERCENT,a.FIELD_INTYPE,a.FIELD_DS,a.FIELD_SQL,a.FIELD_DEF_SETTING ";
            //                        0           1             2             3             4             5            6             7              8           9           10           11           12            13              14            15           16          17           18           19           20            21            22          23           24            25               26              27            28            29             30           31          32                33
            sql += "   from tfield a ";

            sql += " where a.FIELD_ID=?";// + fieldId;
            
            Object[] sqlParams = {fieldId};

            result = dbopt.executeQueryToStrArr2(sql, 34, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 获取字表的统计字段
     * @param tablename String
     * @param domainId String
     * @return String[][]
     */
    public String[][] getTotalFieldByTableName(String tablename,
                                               String domainId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String sql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();
            sql =
                "select e.field_name,e.field_desname,e.field_id from tfield e, ttable t ";
            if(dbopt.dbtype.indexOf("oracle") != -1) {
                sql +=
                    "where instr(t.table_totfield,e.field_name,1,1)>0 and t.table_name='" +
                    tablename + "' and e.domain_id=" + domainId;
            } else if(dbopt.dbtype.indexOf("mysql") != -1) {
                sql +=
                    "where instr(t.table_totfield,e.field_name)>0 and t.table_name='" +
                    tablename + "' and e.domain_id=" + domainId;
            } else {
                sql +=
                    "where charindex(e.field_name,t.table_totfield,0)>0 and t.table_name='" +
                    tablename + "' and e.domain_id=" + domainId;
            }
            sql += " and e.field_table=t.table_id";
            result = getTotalFieldByTableName(tablename, domainId, dbopt);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getTotalFieldByTableName-----------sql--------:\n" + sql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            return result;
        }
    }

    public String[][] getTotalFieldByTableName(String tablename, String domainId, DbOpt dbopt) throws Exception {
        String[][] result = null;
        String sql = "select e.field_name,e.field_desname,e.field_id from tfield e, ttable t ";
        if (dbopt.dbtype.indexOf("oracle") != -1) {
            sql += "where instr(t.table_totfield,e.field_name,1,1)>0 and t.table_name='"
                    + tablename + "' and e.domain_id=" + domainId;
        } else if (dbopt.dbtype.indexOf("mysql") != -1) {
            sql += "where instr(t.table_totfield,e.field_name)>0 and t.table_name='"
                    + tablename + "' and e.domain_id=" + domainId;
        } else {
            sql += "where charindex(e.field_name,t.table_totfield,0)>0 and t.table_name='"
                    + tablename + "' and e.domain_id=" + domainId;
        }
        sql += " and e.field_table=t.table_id";
        result = dbopt.executeQueryToStrArr2(sql, 3);
        return result;
    }
    
    /**
     * 根据用户传入的SQL将数据加载到一个二维字符串数组
     * @param sql:SQL语句
     * @param col：需要转换为二维字符串数组的列数
     */
    public String[][] loadDataBySQL(String sql, String col) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result[][] = null;

        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStrArr2(sql, Integer.parseInt(col));
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("loadDataBySQL-----------sql--------:\n"+sql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            return result;
        }
    }

    /**
     * 判断主表单与子表单是否使用同一数据表，若不是同一数据表则为新表单
     * @param parentFormId String 主流程表单ID
     * @param formId String 子流程表单ID
     * @return boolean
     */
    public boolean isNewForm(String parentFormId, String formId) {
        boolean res = true;
        com.whir.common.util.DataSourceBase ds = new com.whir.common.util.
            DataSourceBase();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        try {
            conn = ds.getDataSource().getConnection();
            stmt = conn.createStatement();
            long parentTableID = 0L, tableId = 0L;
            java.sql.ResultSet rs = stmt.executeQuery(
                "select b.table_id from ez_form a, ez_form_table b where a.form_id=b.form_id and b.is_main_table='1' and a.form_id=" +
                parentFormId);
            if(rs.next()) {
                parentTableID = rs.getLong(1);
            }
            rs.close();

            rs = stmt.executeQuery(
                "select b.table_id from ez_form a, ez_form_table b where a.form_id=b.form_id and b.is_main_table='1' and a.form_id=" +
                formId);
            if(rs.next()) {
                tableId = rs.getLong(1);
            }
            rs.close();

            if(parentTableID == tableId) {
                res = false;
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            if(stmt != null) {
                try {
                    stmt.close();
                } catch(Exception er) {
                    er.printStackTrace();
                }
            }
            if(conn != null) {
                try {
                    conn.close();
                } catch(Exception err) {
                    err.printStackTrace();
                }
            }
        }
        return res;
    }

    /**
     * 取得某个表单中的数据库中唯一的字段
     * @param formId String
     * @return String
     */
    public String getOnlyFields(String formId) {
        StringBuffer field = new StringBuffer();
        com.whir.common.util.DataSourceBase ds = new com.whir.common.util.
            DataSourceBase();
        java.sql.Connection conn = null;
        java.sql.Statement stmt = null;
        String sql = "select distinct a.field_name from tfield a, ez_form_field b where a.field_id=b.field_id and a.field_only=1 and a.field_table in (select table_id from ttable where table_id in( select table_id from ez_form_table where form_id=" +
                formId + "))";
        try {
            conn = ds.getDataSource().getConnection();
            stmt = conn.createStatement();
            java.sql.ResultSet rs = stmt.executeQuery(sql);
            int i = 0;
            while(rs.next()) {
                if(i == 0) {
                    field.append(rs.getString(1));
                } else {
                    field.append(",").append(rs.getString(1));
                }
                i++;
            }
            rs.close();
            stmt.close();
        } catch(Exception ex) {
            ex.printStackTrace();
            System.out.println("getOnlyFields-----------sql--------:\n"+sql);
        } finally {
            if(conn != null) {
                try {
                    conn.close();
                } catch(Exception err) {
                    err.printStackTrace();
                }
            }
        }
        return field.toString();
    }

    /*
     * 根据用户设置取得查询字段的HTML代码
     * @param formId:自定义表单ID
     * @return String computeFieldHTML:该表单关联数据库表的计算字段HTML脚本
     */
    public String getComputeFieldHTML(String formId) {
        String computeFieldHTML = "";
        DbOpt dbopt = null;

        String querySql = "";
        querySql += "select distinct ";
        querySql += " d.field_name,"; //0
        querySql += " d.field_value"; //1

        querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
        querySql += " where a.form_id=?";// + formId;
        querySql += " and a.form_id=b.form_id";
        querySql += " and b.id=c.form_table_id";
        querySql += " and b.table_id=e.table_id";
        querySql += " and c.field_id=d.field_id";
        querySql += " and d.field_table=e.table_id";
        querySql += " and d.field_show=f.show_id";
        querySql += " and b.is_main_table='1'"; //子表
        querySql += " and f.show_id in (203,803)";//计算字段和预算金额
        //querySql += " order by c.sort_no";
        
        Object[] sqlParams = {formId};

        try {
            dbopt = new DbOpt();
            String[][] computeField = dbopt.executeQueryToStrArr2(querySql, sqlParams);
            if(computeField != null) {
                for(int i = 0; i < computeField.length; i++) {
                    computeFieldHTML +=
                        "<input type='hidden' name='computeField' id='computeField' value='" +
                        computeField[i][0] + "'>";
                    computeFieldHTML +=
                        "<input type='hidden' name='computeFieldValue' id='computeFieldValue' value='" +
                        computeField[i][1] + "'>";
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getComputeFieldHTML-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }

            return computeFieldHTML;
        }
    }

    /**
     * 根据userId获取用户信息
     * @param userId String
     * @return String[][]
     */
    public String[][] getUserInfoByUserId(String userId) {
        DbOpt dbopt = null;

        String querySql = "select ";
        querySql += " a.emp_id,";
        querySql += " a.empname,";
        querySql += " c.org_id,";
        querySql += " c.orgsimplename";
        querySql +=
            " from org_employee a, org_organization_user b, org_organization c";
        querySql += " where a.emp_id=b.emp_id";
        querySql += " and b.org_id=c.org_id";
        querySql += " and a.emp_id=?";//+userId;
        
        Object[] sqlParams = {userId};

        try {
            dbopt = new DbOpt();
            return dbopt.executeQueryToStrArr2(querySql, 4, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println(
                "getUserInfoByUserId-----------sql--------:\n" + querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {}
        }
        return null;
    }
    
    public String[][] getDataValueFromDSWithFieldId(String fieldId) {
        return getDataValueFromDSWithFieldId(fieldId, null);
    }

    /**
     * 根据fieldId取得其它数据源sql数据值
     * @param fieldId String
     * @return String[][]
     */
    public String[][] getDataValueFromDSWithFieldId(String fieldId, Map paramsMap) {
        //System.out.println("--------------------getDataValueFromDSWithFieldId----------------------------------"+fieldId);
        String[][] fieldInfo = getFieldExtInfoByFieldId(fieldId);
        String field_intype = fieldInfo[0][0];
        String field_ds = fieldInfo[0][1];
        String field_sql = fieldInfo[0][2];
        String fieldShow = fieldInfo[0][5];

        if(!"1".equals(field_intype))return null;
        if(FormHelper.isEmpty(field_ds))return null;
        if(FormHelper.isEmpty(field_sql))return null;
        
        //---------------------------------------------------
        //替换系统参数
        field_sql = FormHelper.processSQLWithParam(field_sql, paramsMap);
        //---------------------------------------------------

        com.whir.component.extds.DBTools dbt = new com.whir.component.extds.
            DBTools(field_ds);

        int show = Integer.parseInt(fieldShow);
        //System.out.println("--------------------show----------------------------------"+show);
        //System.out.println("--------------------field_ds----------------------------------"+field_ds);
        //System.out.println("--------------------field_sql----------------------------------"+field_sql);

        String[][] ret = null;
        try {
            switch(show) {
                case 101: {
                    ret = dbt.executeQueryToStrArr2(field_sql, 1);
                    //System.out.println("--------------------ret----------------------------------"+ret);
                }
                break;
                case 103: {
                    ret = dbt.executeQueryToStrArr2(field_sql, 1);
                }
                break;
                case 104: {
                    ret = dbt.executeQueryToStrArr2(field_sql, 1);
                }
                break;
                case 105: {
                    ret = dbt.executeQueryToStrArr2(field_sql, 1);
                }
                break;
                case 110: {
                    ret = dbt.executeQueryToStrArr2(field_sql, 1);
                }
                break;
            }
        } catch(Exception e) {
            System.out.println(
                "---------------------------------------------\n");
            System.out.println(field_sql);
            System.out.println(
                "\n---------------------------------------------");
            e.printStackTrace();
            return null;
        } finally {
            try {
                if(dbt!=null)dbt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    /**
     * 根据formId获取主表字段
     * @param formId String
     * @return String[][]
     */
    public String[][] getFormMainFields(String formId) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;
        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,"; //0
            querySql += " d.field_code,"; //1
            querySql += " d.field_desname,"; //2
            querySql += " d.field_name,"; //3
            querySql += " d.field_table,"; //4
            querySql += " d.field_type,"; //5
            querySql += " d.field_len,"; //6
            querySql += " d.field_index,"; //7
            querySql += " d.field_null,"; //8
            querySql += " d.field_sort,"; //9
            querySql += " d.field_only,"; //10
            querySql += " d.field_default,"; //11
            querySql += " d.field_des,"; //12
            querySql += " d.field_list,"; //13
            querySql += " d.field_width,"; //14
            querySql += " d.field_show,"; //15
            querySql += " d.field_value,"; //16
            querySql += " d.field_codevalue,"; //17
            querySql += " d.field_percent,"; //18
            querySql += " d.field_intype,"; //19
            querySql += " d.field_ds,"; //20
            querySql += " d.field_sql,"; //21
            querySql += " d.field_def_setting,"; //22
            querySql += " e.table_name,"; //23
            querySql += " f.show_id,"; //24
            querySql += " e.table_desname,"; //25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " e.wf_module_id";//29

            querySql +=
                " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=?";// + formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and b.is_main_table='1'"; //主表
            querySql += " order by d.field_sequence, d.field_id";
            
            Object[] sqlParams = {formId};

            result = dbopt.executeQueryToStrArr2(querySql, 30, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getFormMainFields-----------sql--------:\n" +
                               querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public String getFormIdByFormCode(String formCode, String domainId) {
        String form_sql = "select a.form_id from ez_form a ";
        form_sql += " where a.domain_id=?";// + domainId;
        form_sql += " and a.form_type=0 ";
        form_sql += " and a.form_code=?";//'" + formCode + "' ";
        //form_sql += " order by a.form_id";
        
        Object[] sqlParams = {domainId, formCode};

        DbOpt dbopt = new DbOpt();
        try {
            return dbopt.executeQueryToStr(form_sql, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
    
    public String getFormCodeByFormId(String formId, String domainId) {
        String form_sql = "select a.form_code from ez_form a ";
        form_sql += " where a.domain_id=?";// + domainId;
        form_sql += " and a.form_id=?";// + formId;
        //form_sql += " order by a.form_id";
        
        Object[] sqlParams = {domainId, formId};

        DbOpt dbopt = new DbOpt();
        try {
            return dbopt.executeQueryToStr(form_sql, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    public String[][] getShowTypeByFieldName(String formId, String fieldName) {
        String form_sql = "select t.field_show, t.field_value from ez_form a, ez_form_table b, ez_form_field c, tfield t ";
        form_sql += " where a.form_id=b.form_id and b.id=c.form_table_id and c.field_id=t.field_id ";
        form_sql += " and a.form_type='0' ";
        form_sql += " and a.form_id=?";// + formId + " ";
        form_sql += " and t.field_name=?";//'" + fieldName + "' ";
        //form_sql += " order by a.form_id";

        Object[] sqlParams = {formId, fieldName};
        
        DbOpt dbopt = new DbOpt();
        try {
            return dbopt.executeQueryToStrArr2(form_sql, 2, sqlParams);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }
    
    /**
     * 获取业务表单关联的所有主表字段
     * @param wfModuleId String
     * @param domainId String
     * @param sysAttr String 0-系统字段 1-用户字段
     * @return String[][]
     * 返回值：
     *      0-字段ID
     *      1-字段Code
     *      2-字段显示名称
     *      3-字段系统名称
     *      4-数据表ID
     *      5-字段类型
     *      6-字段长度
     */
    public String[][] getFieldsByWfModuleId(String wfModuleId, String domainId, String sysAttr, String fieldName) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,";//0
            querySql += " d.field_code,";//1
            querySql += " d.field_desname,";//2
            querySql += " d.field_name,";//3
            querySql += " d.field_table,";//4
            querySql += " d.field_type,";//5
            querySql += " d.field_len,";//6
            querySql += " d.field_index,";//7
            querySql += " d.field_null,";//8
            querySql += " d.field_sort,";//9
            querySql += " d.field_only,";//10
            querySql += " d.field_default,";//11
            querySql += " d.field_des,";//12
            querySql += " d.field_list,";//13
            querySql += " d.field_width,";//14
            querySql += " d.field_show,";//15
            querySql += " d.field_value,";//16
            querySql += " d.field_codevalue,";//17
            querySql += " d.field_percent,";//18
            querySql += " d.field_intype,";//19
            querySql += " d.field_ds,";//20
            querySql += " d.field_sql,";//21
            querySql += " d.field_def_setting,";//22
            querySql += " e.table_name,";//23
            querySql += " f.show_id,";//24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29
            

            querySql += " from tfield d, ttable e, tshow f";
            querySql += " where ";
            querySql += " d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and e.wf_module_id="+wfModuleId;
            
            if(!CommonUtils.isEmpty(sysAttr)){
                querySql += " and d.sys_attr='"+sysAttr+"'";
            }
            
            if(!CommonUtils.isEmpty(fieldName)){
                querySql += " and d.field_name='"+fieldName+"'";
            }
            
            //querySql += " order by c.sort_no";

            result = dbopt.executeQueryToStrArr2(querySql, 30);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("getFormFields-----------sql--------:\n"+querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    
    public String[][] getFieldsByWfModuleId(String wfModuleId, String domainId) {
        
        return getFieldsByWfModuleId(wfModuleId, domainId, null, null);
    }
    
    /**
     * 根据wfModuleId获取指定fieldName的字段信息
     * @param wfModuleId
     * @param fieldName
     * @param domainId
     * @return
     */
    public String[][] getFieldsByWfModuleIdWithFieldName(String wfModuleId, String fieldName, String domainId) {
        
        return getFieldsByWfModuleId(wfModuleId, domainId, null, fieldName);
    }
    
    /**
     * 根据表单ID和业务模块ID获取表单字段
     * 
     * @param formId
     * @param wfModuleId
     * @param fieldSource 1-主表字段 0-字段字段 空-所有字段
     * @param sysAttr 0-系统字段，1-用户字段
     * @return
     */
    public String[][] getFormFieldsByWfModuleId(String formId, String wfModuleId, String fieldSource, String sysAttr) {
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String[][] result = null;

        String querySql = "";
        try {
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            querySql += "select distinct ";
            querySql += " d.field_id,"; //0
            querySql += " d.field_code,"; //1
            querySql += " d.field_desname,"; //2
            querySql += " d.field_name,"; //3
            querySql += " d.field_table,"; //4
            querySql += " d.field_type,"; //5
            querySql += " d.field_len,"; //6
            querySql += " d.field_index,"; //7
            querySql += " d.field_null,"; //8
            querySql += " d.field_sort,"; //9
            querySql += " d.field_only,"; //10
            querySql += " d.field_default,"; //11
            querySql += " d.field_des,"; //12
            querySql += " d.field_list,"; //13
            querySql += " d.field_width,"; //14
            querySql += " d.field_show,"; //15
            querySql += " d.field_value,"; //16
            querySql += " d.field_codevalue,"; //17
            querySql += " d.field_percent,"; //18
            querySql += " d.field_intype,"; //19
            querySql += " d.field_ds,"; //20
            querySql += " d.field_sql,"; //21
            querySql += " d.field_def_setting,"; //22
            querySql += " e.table_name,"; //23
            querySql += " f.show_id,"; //24
            querySql += " e.table_desname,";//25
            querySql += " d.field_sequence,";//26
            querySql += " d.sys_attr,";//27
            querySql += " d.field_decnum,";//28
            querySql += " d.field_signpic";//29

            querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
            querySql += " where a.form_id=" + formId;
            querySql += " and a.form_id=b.form_id";
            querySql += " and b.id=c.form_table_id";
            querySql += " and b.table_id=e.table_id";
            querySql += " and c.field_id=d.field_id";
            querySql += " and d.field_table=e.table_id";
            querySql += " and d.field_show=f.show_id";
            querySql += " and e.wf_module_id="+wfModuleId;
            
            if(!CommonUtils.isEmpty(fieldSource)){
                querySql += " and b.is_main_table='"+fieldSource+"'";//子表
            }
            
            if(!CommonUtils.isEmpty(sysAttr)){
                querySql += " and d.sys_attr='"+sysAttr+"'";
            }
            
            //querySql += " order by c.sort_no";

            result = dbopt.executeQueryToStrArr2(querySql, 30);
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("getFormFieldsByWfModuleId-----------sql--------:\n" +
                               querySql);
        } finally {
            try {
                if(dbopt!=null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    /**
     * 获取预算自定表数据
     * @param inputMap
     * @return
     */
    public List getBudgetCostApply(Map inputMap){
        List result = new ArrayList();
        
        String p_wf_recordId = (String)inputMap.get("p_wf_recordId");
        String pool_process_id = (String)inputMap.get("pool_process_id");
        String p_wf_workStatus = (String)inputMap.get("p_wf_workStatus");
        String p_wf_tableId = (String)inputMap.get("p_wf_tableId");
        
        //表单编号-ezform
        String p_wf_formKey = (String)inputMap.get("p_wf_formKey");
        String userId = (String)inputMap.get("userId");
        String userName = (String)inputMap.get("userName");
        String orgId = (String)inputMap.get("orgId");
        String orgName = (String)inputMap.get("orgName");
        String domainId = (String)inputMap.get("domainId");
        
        if(!CommonUtils.isEmpty(p_wf_formKey)){
            String querySql = "";
            String[][] fieldArr = null;
            com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
            try {
                dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

                //主表数据
                querySql = "select distinct ";
                querySql += " d.field_name,";
                querySql += " d.field_show,";
                querySql += " e.table_name,";
                querySql += " b.is_main_table,";
                querySql += " e.table_id";

                querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
                querySql += " where a.form_code='" + p_wf_formKey + "'";
                querySql += " and a.form_type='0' ";
                querySql += " and a.form_id=b.form_id";
                querySql += " and b.id=c.form_table_id";
                querySql += " and b.table_id=e.table_id";
                querySql += " and b.is_main_table='1'";//主表
                querySql += " and c.field_id=d.field_id";
                querySql += " and d.field_table=e.table_id";
                querySql += " and d.field_show=f.show_id";
                querySql += " and d.field_show in(801,802,803,805)";
                querySql += " order by e.table_id, d.field_show, e.table_name, b.is_main_table";
                
                logger.debug("主表："+querySql);

                fieldArr = dbopt.executeQueryToStrArr2(querySql, 5);                
                if(fieldArr != null){
                    String f801 = "";
                    String f802 = "";
                    String f803 = "";
                    String f805 = "";
                    String tableName = "";
                    for(int i=0; i<fieldArr.length; i++){
                        tableName = fieldArr[i][2];
                        String showId = fieldArr[i][1];
                        if("801".equals(showId)){
                            f801 = fieldArr[i][0];
                        }else if("802".equals(showId)){
                            f802 = fieldArr[i][0];
                        }else if("803".equals(showId)){
                            f803 = fieldArr[i][0];
                        }else if("805".equals(showId)){
                            f805 = fieldArr[i][0];
                        }
                    }

                    if(!CommonUtils.isEmpty(f801) && !CommonUtils.isEmpty(f802) && !CommonUtils.isEmpty(f803) && !CommonUtils.isEmpty(f805)){
                        String sql = "select " + f801 + "," + f802 + "," + f805 + "," + f803 + " from " + tableName + " where " + tableName + "_id="+p_wf_recordId;
                        String[] dataArr = dbopt.executeQueryToStrArr1(sql);
                        if(dataArr != null){
                            result.add(dataArr);
                        }
                    }
                }
                
                fieldArr = null;
                
                //子表数据
                querySql = "select distinct ";
                querySql += " d.field_name,";
                querySql += " d.field_show,";
                querySql += " e.table_name,";
                querySql += " b.is_main_table,";
                querySql += " e.table_id";

                querySql += " from ez_form a, ez_form_table b, ez_form_field c, tfield d, ttable e, tshow f";
                querySql += " where a.form_code='" + p_wf_formKey + "'";
                querySql += " and a.form_type='0' ";
                querySql += " and a.form_id=b.form_id";
                querySql += " and b.id=c.form_table_id";
                querySql += " and b.table_id=e.table_id";
                querySql += " and b.is_main_table='0'";//子表
                querySql += " and c.field_id=d.field_id";
                querySql += " and d.field_table=e.table_id";
                querySql += " and d.field_show=f.show_id";
                querySql += " and d.field_show in(801,802,803,805)";
                querySql += " order by e.table_id, d.field_show, e.table_name, b.is_main_table";
                
                logger.debug("子表："+querySql);
                
                fieldArr = dbopt.executeQueryToStrArr2(querySql, 5);                
                if(fieldArr != null){
                    String f801 = "";
                    String f802 = "";
                    String f803 = "";
                    String f805 = "";
                    String tableName = "";
                    for(int i=0; i<fieldArr.length; i++){
                        tableName = fieldArr[i][2];
                        String showId = fieldArr[i][1];
                        if("801".equals(showId)){
                            f801 = fieldArr[i][0];
                        }else if("802".equals(showId)){
                            f802 = fieldArr[i][0];
                        }else if("803".equals(showId)){
                            f803 = fieldArr[i][0];
                        }else if("805".equals(showId)){
                            f805 = fieldArr[i][0];
                        }
                    }

                    if(!CommonUtils.isEmpty(f801) && !CommonUtils.isEmpty(f802) && !CommonUtils.isEmpty(f803) && !CommonUtils.isEmpty(f805)){
                        String sql = "select " + f801 + "," + f802 + "," + f805 + "," + f803 + " from " + tableName + " where " + tableName + "_FOREIGNKEY="+p_wf_recordId + " order by " + tableName + "_id";
                        String[] dataArr = dbopt.executeQueryToStrArr1(sql);
                        if(dataArr != null){
                            result.add(dataArr);
                        }
                    }
                }
                
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(dbopt!=null)dbopt.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
    }
    
    public String getCommentUserOrgNameWithCommentField(String userId, String commentFieldName, String defaultValue) {
        if(CommonUtils.isEmpty(defaultValue)){
            return "";
        }
        
        String[][] formCommentFields = new String[1][6];
        
        formCommentFields[0][1] = commentFieldName;
        formCommentFields[0][3] = defaultValue;
        
        return getCommentUserOrgNameWithCommentField(userId, commentFieldName, formCommentFields);
    }
    
    /**
     * 根据批示意见对应字段获取用户组织名称
     * 
     * @param userId 用户ID
     * @param commentFieldName 批示意见字段
     * @param formCommentFields 表单批示意见字段数组
     * @return
     * @see UI207
     */
    public String getCommentUserOrgNameWithCommentField(String userId, String commentFieldName, 
            String[][] formCommentFields) {
        String result = "";
        
        logger.debug("commentFieldName:"+commentFieldName);
        
        if(formCommentFields != null && formCommentFields.length > 0) {
            for(int i0 = 0; i0 < formCommentFields.length; i0++) {
                if(formCommentFields[i0][1].equals(commentFieldName)) {
                    String fieldValue = formCommentFields[i0][3];
                    
                    logger.debug("fieldValue:"+fieldValue);
                    
                    //defaultValue
                    //组织长名称-full（默认）
                    //最末端组织-self
                    //最末端组织向上一级-parent
                    //从本单位开始至末端-unit（如果没有则为全称）
                    String defaultValue = fieldValue;
                    if("self".equals(defaultValue)){
                        String[][] orgArr = UIData.getOrgInfoByUserId(userId);
                        String orgName = orgArr[0][2];
                        
                        result = orgName;
                        
                        int pos = orgName.lastIndexOf(".");
                        if(pos != -1){
                            result = orgName.substring(pos + 1);
                        }
                    }else if("parent".equals(defaultValue)){
                        String[][] orgArr = UIData.getOrgInfoByUserId(userId);
                        String orgName = orgArr[0][2];
                        
                        result = orgName;
                        
                        String[] orgNameArr = orgName.split("\\.");
                        int len = orgNameArr.length;
                        if(len > 2){
                            result = orgNameArr[len - 2] + "." + orgNameArr[len - 1];
                        }
                    }else if("unit".equals(defaultValue)){
                        result = UIData.getUnitName(userId);
                        
                    }else if("full".equals(defaultValue)){
                        String[][] orgArr = UIData.getOrgInfoByUserId(userId);
                        String orgName = orgArr[0][2];
                        
                        result = orgName;
                    }
                    
                    break;
                }
            }
        }
        
        return result;        
    }
    
    /**
     * 获取批示人签名图片
     * @param userId
     * @return
     */
    public String getDealWithUserSignImg(String userId){
        com.whir.ezoffice.customdb.common.util.DbOpt dbopt = null;
        String result = null;
        String sql = "select b.SIGNATUREIMGSAVENAME from org_employee b where b.emp_id=?";
        
        Object[] sqlParams = {userId};
        
        try{
            dbopt = new com.whir.ezoffice.customdb.common.util.DbOpt();

            result = dbopt.executeQueryToStr(sql, sqlParams);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                dbopt.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }

        return result;
    }
}
