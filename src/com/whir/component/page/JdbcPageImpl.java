package com.whir.component.page;

import com.whir.common.hibernate.HibernateBase;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSetMetaData;
import org.apache.log4j.Logger;
import com.whir.common.db.NamedParameterStatement;
import java.util.Iterator;

/**
 *
 * <p>Title: </p>
 * <p>Description: 使用jdbc来分页</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: WanHu Internet Resource(Hefei) C0. Ltd</p>
 * @author 王国良
 * @version 1.0
 */
public class JdbcPageImpl {
    private static Logger logger = Logger.getLogger(JdbcPageImpl.class.getName());
    private java.sql.Connection conn=null; 
    private String extType="0";
    
    public  JdbcPageImpl(java.sql.Connection conn) {
        this.conn=conn;
    }
    
    public  JdbcPageImpl(java.sql.Connection conn,String extType) {
        this.conn=conn;
        this.extType=extType; 
    }

    /**
     *
     *  不需要自己加 关键字  select
     *  不需要自己加 关键字 from
     *  但需要自己加 关键字 where 因为 where条件可能为空
     * @param viewSql String   显示字段
     * @param fromsql String
     * @param wheresql String
     * @param orderBySql String
     * @param currentPage Integer  当前的页数
     * @param volume Integer       每页的条数
     * @throws Exception
     * @return Map
     * 王国良 2009.10.10
     */
    public Map getResult(String viewSql, String fromsql, String wheresql,
                             String orderBySql, Integer volume,Integer currentPage) throws Exception {
        Map map = new HashMap();
        //起始条数
        String startNum = "" + ((currentPage.intValue() - 1) * volume.intValue());
        //结尾条数
        String endNum = "" + ((currentPage.intValue() - 1) * volume.intValue() +
                         volume.intValue());
        //总条数
        int totalSize = 0;
        //此次返回的记录
        List recordlist = new ArrayList();

        String totalWhereSql = wheresql + orderBySql;

        String errorCountSql = "";
        String errorRealSql = "";

        //虚拟order by (sql server 分页时要用)
        String orderBysql_virtual = orderBySql.toUpperCase().replaceAll(" DESC",
                " asc1 ").replaceAll(" ASC", " DESC ").replaceAll(" asc1 ",
                " ASC ");
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();

        //取总条数的语句
        StringBuffer queryBuffer = new StringBuffer();
        //实际的拼接的语句
        StringBuffer queryCount = new StringBuffer();

        queryCount.append("SELECT COUNT(*) FROM ");
        queryCount.append(fromsql);
        queryCount.append(" ");
        int p = totalWhereSql.toUpperCase().indexOf("ORDER BY");
        if (p >= 0) {
            queryCount.append(totalWhereSql.substring(0, p));
        } else {
            queryCount.append(totalWhereSql);
        }

        //拼接的语句
        queryBuffer.append("SELECT ");
        queryBuffer.append(viewSql);
        queryBuffer.append(" FROM ");
        queryBuffer.append(fromsql);
        queryBuffer.append(" ");
        queryBuffer.append(totalWhereSql);

        java.sql.Statement stmt = conn.createStatement();
        java.sql.Statement countstmt = conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
        java.sql.ResultSet rs = null;
        try {
            //查 总条数
            if (queryCount.toString().toUpperCase().indexOf(" GROUP BY ") > 0) {
                /*System.out.println("===========================================");
                 System.out.println("queryCount.toString():"+queryCount.toString());
                 System.out.println("===========================================");*/
                errorCountSql = queryCount.toString();
                rs = countstmt.executeQuery(errorCountSql);
                rs.last();
                totalSize = rs.getRow();
                rs = null;
            } else {
                if (viewSql.toUpperCase().indexOf("DISTINCT") >= 0) {
                    /*System.out.println("===========================================1");
                     System.out.println("queryBuffer.toString():"+queryBuffer.toString());
                     System.out.println("===========================================1");*/
                    errorCountSql = queryBuffer.toString();
                    rs = countstmt.executeQuery(errorCountSql);
                    rs.last();
                    totalSize = rs.getRow();
                    rs = null;
                } else {
                    /*System.out.println("===========================================");
                     System.out.println("queryCount.toString():" +queryCount.toString());
                     System.out.println("===========================================");*/
                    errorCountSql = queryCount.toString();
                    rs = stmt.executeQuery(errorCountSql);
                    while (rs.next()) {
                        totalSize = rs.getInt(1);
                    }
                }
            }
            map.put("recordCount", new Integer(totalSize));
            //分页的语句
            StringBuffer totalqueryBuffer = new StringBuffer();
            //oracle数据库
            if (databaseType.equals("oracle")) {
                totalqueryBuffer.append(
                        " select * from ( select tableAlias.*, rownum tablern from (").
                        append(queryBuffer).append(") tableaLias");
                totalqueryBuffer.append(" where rownum<= ").append(endNum).
                        append(") where tablern>").append(startNum);

            } else if (databaseType.equals("mssqlserver")) {
                // select * from (   select TOP 15 * FROM ( SELECT TOP 30   * from aaattt   ORDER BY aaa ASC ) as aSysTable
                //ORDER BY aaa DESC ) as bSysTable   ORDER BY aaa ASC
                int topVolume = volume.intValue();
                if (totalSize < currentPage.intValue() * volume.intValue()) {
                    if (currentPage.intValue() > 1)
                        topVolume = totalSize -
                                    (currentPage.intValue() - 1) *
                                    volume.intValue();
                }
                //mssqlserver 中   select     top   15   distinct XX 报错  应该改为：
                // select  distinct   top   15    XX
                
                String distinctStr="";
                String viewSqlL_toUpperCase=viewSql.trim().toUpperCase();
                logger.debug("viewSqlL_toUpperCase:"+viewSqlL_toUpperCase);
                //
                if(viewSqlL_toUpperCase.startsWith("DISTINCT ")){
                	distinctStr=" DISTINCT "; 
                	viewSqlL_toUpperCase=viewSqlL_toUpperCase.replaceFirst("DISTINCT ", " ");
                } 
                logger.debug("viewSqlL_toUpperCase2:"+viewSqlL_toUpperCase);
                viewSqlL_toUpperCase=" "+viewSqlL_toUpperCase+" ";
                logger.debug("viewSqlL_toUpperCase3:"+viewSqlL_toUpperCase);
                logger.debug("distinctStr:"+distinctStr);
                
                totalqueryBuffer.append(" select * from ( select top ").append(
                        topVolume);
                totalqueryBuffer.append(" *  from ( select ").append(distinctStr).append(" top ").append(
                        currentPage.intValue() * volume.intValue()).append(" ");
                //String viewSql,String fromsql,String wheresql,String orderBySql,
                totalqueryBuffer.append(viewSqlL_toUpperCase).append(" from ").append(
                        fromsql).append(" ").append(wheresql).append(orderBySql).
                        append(") as aSysTable ");
                totalqueryBuffer.append(orderBysql_virtual).append(
                        " ) as bSysTable").append(orderBySql);

            } else if (databaseType.equals("mysql")) {
                totalqueryBuffer.append(queryBuffer).append(" limit ").append(startNum).append(",").append(volume);
            } else {

            }

            /*System.out.println("====================totalqueryBuffer====================currentPage:"+currentPage);
                System.out.println("totalqueryBuffer.toString():" + totalqueryBuffer.toString());
             System.out.println("=====================totalqueryBuffer==================");*/
              
            
            logger.debug("totalqueryBuffer.toString():"+totalqueryBuffer.toString());
            errorRealSql = totalqueryBuffer.toString();
            //分页查询
            rs = stmt.executeQuery(errorRealSql);

            //拼接结果
            ResultSetMetaData rsmd = rs.getMetaData(); //取得数据表中的字段数目，类型等返回结果
            //是以ResultSetMetaData对象保存
            //
            int columnCount = rsmd.getColumnCount(); //列的总数
            
            logger.debug("columnCount:"+columnCount);
            //按新的逻辑来处理，  oracle 不要加 tablern字段信息 
            if(extType.equals("1")){
            	if (databaseType.equals("oracle")) {
            		columnCount=columnCount-1;
            	}
            }
            logger.debug("columnCount1:"+columnCount);
            logger.debug("extType:"+extType);

            while (rs.next()) {
                Object[] obj = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    if (rs.getString(rsmd.getColumnName(i)) != null) {
                        obj[i - 1] = rs.getString(rsmd.getColumnName(i));
                    } else {
                        obj[i - 1] = "";
                    }
                }
                recordlist.add(obj);
            }
            map.put("list", recordlist);
        } catch (Exception e) {
            logger.error("---------没有varMap 的getResult出错----------------");
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("errorCountSql:" + errorCountSql);
            logger.error("errorRealSql:" + errorRealSql);
            logger.error("----------------------------------------");
        } finally {
            if(this.conn!=null){
                conn.close();
                conn = null;
            }
        }
        return map;
    }

    /**
     *
     *  不需要自己加 关键字  select
     *  不需要自己加 关键字 from
     *  但需要自己加 关键字 where 因为 where条件可能为空
     * @param viewSql String   显示字段
     * @param fromsql String
     * @param wheresql String
     * @param orderBySql String
     * @param currentPage Integer  当前的页数
     * @param volume Integer       每页的条数
     * @throws Exception
     * @return Map
     * 王国良 2009.10.10
     */
    public Map getResult(String viewSql, String fromsql, String wheresql,
                             String orderBySql,
                             Integer volume, Integer currentPage,Map varMap) throws Exception {

        logger.debug("viewSql:" + viewSql);
        logger.debug("fromsql:" + fromsql);
        logger.debug("wheresql:" + wheresql);
        logger.debug("orderBySql:"+orderBySql);
        logger.debug("volume:" + volume);
        logger.debug("currentPage:" + currentPage);
        
        if (varMap != null) {
            Iterator it = varMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = "" + entry.getKey();
                Object value = entry.getValue();
                logger.debug("key:" + key + "  value:" + value);
            }
        }

        Map map = new HashMap();
        //起始条数
        String startNum = "" + ((currentPage.intValue() - 1) * volume.intValue());
        //结尾条数
        String endNum = "" + ((currentPage.intValue() - 1) * volume.intValue() +
                         volume.intValue());
        //总条数
        int totalSize = 0;
        //此次返回的记录
        List recordlist = new ArrayList();

        String totalWhereSql = wheresql + orderBySql;

        String errorCountSql = "";
        String errorRealSql = "";

        //虚拟order by (sql server 分页时要用)
        String orderBysql_virtual = orderBySql.toUpperCase().replaceAll(" DESC",
                " asc1 ").replaceAll(" ASC", " DESC ").replaceAll(" asc1 ",
                " ASC ");
        String databaseType = com.whir.common.config.SystemCommon.getDatabaseType();

        //取总条数的语句
        StringBuffer queryBuffer = new StringBuffer();
        //实际的拼接的语句
        StringBuffer queryCount = new StringBuffer();

        queryCount.append("SELECT COUNT(*) FROM ");
        queryCount.append(fromsql);
        queryCount.append(" ");
        int p = totalWhereSql.toUpperCase().indexOf("ORDER BY");
        if (p >= 0) {
            queryCount.append(totalWhereSql.substring(0, p));
        } else {
            queryCount.append(totalWhereSql);
        }

        //拼接的语句
        queryBuffer.append("SELECT ");
        queryBuffer.append(viewSql);
        queryBuffer.append(" FROM ");
        queryBuffer.append(fromsql);
        queryBuffer.append(" ");
        queryBuffer.append(totalWhereSql);

        java.sql.Statement stmt = conn.createStatement();
        NamedParameterStatement  namedParameter=null;
        java.sql.Statement countstmt = conn.createStatement(java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
        java.sql.ResultSet rs = null;
        try {

            //查 总条数
            if (queryCount.toString().toUpperCase().indexOf(" GROUP BY ") > 0) {
                /*System.out.println("===========================================");
                 System.out.println("queryCount.toString():"+queryCount.toString());
                 System.out.println("===========================================");*/
                errorCountSql = queryCount.toString();
                namedParameter=new NamedParameterStatement(conn,errorCountSql,java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
                namedParameter.setVarMap(varMap);
                //rs = countstmt.executeQuery(errorCountSql);
                rs=namedParameter.executeQuery();
                rs.last();
                totalSize = rs.getRow();
                namedParameter.close();
                rs = null;
            } else {
                if (viewSql.toUpperCase().indexOf("DISTINCT") >= 0) {
                    /*System.out.println("===========================================1");
                     System.out.println("queryBuffer.toString():"+queryBuffer.toString());
                     System.out.println("===========================================1");*/
                    errorCountSql = queryBuffer.toString();
                    namedParameter = new NamedParameterStatement(conn,errorCountSql,java.sql.ResultSet.TYPE_SCROLL_SENSITIVE, java.sql.ResultSet.CONCUR_UPDATABLE);
                    namedParameter.setVarMap(varMap);
                    //rs = countstmt.executeQuery(errorCountSql);
                    rs=namedParameter.executeQuery();
                    rs.last();
                    totalSize = rs.getRow();
                    namedParameter.close();
                    rs = null;
                } else {
                    /*System.out.println("===========================================");
                     System.out.println("queryCount.toString():" +queryCount.toString());
                     System.out.println("===========================================");*/
                    errorCountSql = queryCount.toString();
                    namedParameter = new NamedParameterStatement(conn,errorCountSql);
                    namedParameter.setVarMap(varMap);
                    //rs = stmt.executeQuery(errorCountSql);
                    rs = namedParameter.executeQuery();
                    while (rs.next()) {
                        totalSize = rs.getInt(1);
                    }
                    rs.close();
                    rs=null;
                    namedParameter.close();
                }
            }

            map.put("recordCount", new Integer(totalSize));
            //分页的语句
            StringBuffer totalqueryBuffer = new StringBuffer();
            //oracle数据库
            if (databaseType.equals("oracle")) {
                totalqueryBuffer.append(
                        " select * from ( select tableAlias.*, rownum tablern from (").
                        append(queryBuffer).append(") tableaLias");
                totalqueryBuffer.append(" where rownum<= ").append(endNum).
                        append(") where tablern>").append(startNum);

            } else if (databaseType.equals("mssqlserver")) {
                // select * from (   select TOP 15 * FROM ( SELECT TOP 30   * from aaattt   ORDER BY aaa ASC ) as aSysTable
                //ORDER BY aaa DESC ) as bSysTable   ORDER BY aaa ASC
                int topVolume = volume.intValue();
                if (totalSize < currentPage.intValue() * volume.intValue()) {
                    if (currentPage.intValue() > 1)
                        topVolume = totalSize -
                                    (currentPage.intValue() - 1) *
                                    volume.intValue();
                }
                
                String distinctStr="";
                String viewSqlL_toUpperCase=viewSql.trim().toUpperCase();
                logger.debug("viewSqlL_toUpperCase:"+viewSqlL_toUpperCase);
                //
                if(viewSqlL_toUpperCase.startsWith("DISTINCT ")){
                	distinctStr=" DISTINCT "; 
                	viewSqlL_toUpperCase=viewSqlL_toUpperCase.replaceFirst("DISTINCT ", " ");
                } 
                logger.debug("viewSqlL_toUpperCase2:"+viewSqlL_toUpperCase);
                viewSqlL_toUpperCase=" "+viewSqlL_toUpperCase+" ";
                logger.debug("viewSqlL_toUpperCase3:"+viewSqlL_toUpperCase);
                logger.debug("distinctStr:"+distinctStr);
                
                totalqueryBuffer.append(" select * from ( select top ").append(
                        topVolume);
                totalqueryBuffer.append(" *  from ( select "+distinctStr+" top ").append(
                        currentPage.intValue() * volume.intValue()).append(" ");
                //String viewSql,String fromsql,String wheresql,String orderBySql,
                totalqueryBuffer.append(viewSqlL_toUpperCase).append(" from ").append(
                        fromsql).append(" ").append(wheresql).append(orderBySql).
                        append(") as aSysTable ");
                totalqueryBuffer.append(orderBysql_virtual).append(
                        " ) as bSysTable").append(orderBySql);

            } else if (databaseType.equals("mysql")) {
                totalqueryBuffer.append(queryBuffer).append(" limit ").append(startNum).append(",").append(volume);
            } else {

            }

            /*System.out.println("====================totalqueryBuffer====================currentPage:"+currentPage);
                System.out.println("totalqueryBuffer.toString():" + totalqueryBuffer.toString());
             System.out.println("=====================totalqueryBuffer==================");*/
            logger.debug("totalqueryBuffer.toString():"+totalqueryBuffer.toString());
            errorRealSql = totalqueryBuffer.toString();
            namedParameter = new NamedParameterStatement(conn,errorRealSql);
            namedParameter.setVarMap(varMap);
            //分页查询
            //rs = stmt.executeQuery(errorRealSql);
            rs = namedParameter.executeQuery();
            //拼接结果
            ResultSetMetaData rsmd = rs.getMetaData(); //取得数据表中的字段数目，类型等返回结果
            //是以ResultSetMetaData对象保存
            //
            int columnCount = rsmd.getColumnCount(); //列的总数
            
            //按新的逻辑来处理，  oracle 不要加 tablern字段信息 
            if(extType.equals("1")){
            	if (databaseType.equals("oracle")) {
            		columnCount=columnCount-1;
            	}
            }
            
            while (rs.next()) {
                Object[] obj = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    if (rs.getString(rsmd.getColumnName(i)) != null) {
                        obj[i - 1] = rs.getString(rsmd.getColumnName(i));
                    } else {
                        obj[i - 1] = "";
                    }
                }
                recordlist.add(obj);
            }
            logger.debug("recordlist.size:"+recordlist.size());
            map.put("list", recordlist);
        } catch (Exception e) {
            logger.error("---------getResult出错----------------");
            logger.error("出错信息：",e);
            logger.error(e.getMessage());
            e.printStackTrace();
            logger.error("viewSql:" + viewSql);
            logger.error("fromsql:" + fromsql);
            logger.error("wheresql:" + wheresql);
            logger.error("orderBySql:" + orderBySql);
            logger.error("volume:" + volume);
            logger.error("currentPage:" + currentPage);
            logger.error("errorCountSql:" + errorCountSql);
            logger.error("errorRealSql:" + errorRealSql);
            logger.error("varMap中的值:" );
            if (varMap != null) {
                Iterator it = varMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = "" + entry.getKey();
                    Object value = entry.getValue();
                    logger.error("key:"+key+"  value:" + value);
                }
            }
             e.printStackTrace();
            logger.error("----------------------------------------");
        } finally {
            if(this.conn!=null){
                conn.close();
                conn = null;
            }
        }
        return map;
    }


}
