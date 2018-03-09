package com.whir.component.page;

import com.whir.common.hibernate.HibernateBase;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import net.sf.hibernate.Query;
import java.util.Iterator;
import java.util.Collection;
import org.apache.log4j.*;

/**
 *
 * <p>Title: </p>
 * <p>Description: 使用HQL 取分页数据的类</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: WanHu Internet Resource(Hefei) C0. Ltd</p>
 * @author 王国良
 * @version 1.0
 */
public class HibernatePageImpl extends HibernateBase{
    private static Logger logger = Logger.getLogger(HibernatePageImpl.class.getName());
    public HibernatePageImpl() {
    }

    /**
     * 使用HQL来分页的
     * paramap可以为空，如果为空就是不使用变量sql的方式
     * @param para String         查询出来的字段
     * @param PO String           from  po
     * @param where String        where 条件
     * @param PageSize Integer    每页条数
     * @param CurrentPage Integer 当前页数
     * @param paramap Map -----------------增加参数列  key: HQL中变量名  value:变量对应的值
     * @throws Exception
     * @return Map
     */
    public Map getResult(String selectPara, String fromPO, String where, Integer PageSize, Integer CurrentPage,Map paramap) throws Exception{

        logger.debug("selectPara:" + selectPara);
        logger.debug("fromPO:" + fromPO);
        logger.debug("where:" + where);
        logger.debug("PageSize:" + PageSize);
        logger.debug("CurrentPage:" + CurrentPage);
        if (paramap != null) {
            Iterator it = paramap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = "" + entry.getKey();
                Object value = entry.getValue();
                logger.debug("key:" + key + "  value:" + value);
            }
        }
        //处理% 与_ like的特殊字符
        if(where!=null&&!where.equals("")){
        	where=dealSpecialStr(where,paramap);     	
        }
        logger.debug("处理后的变量参数");
        if (paramap != null) {
            Iterator it = paramap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = "" + entry.getKey();
                Object value = entry.getValue();
                logger.debug("key:" + key + "  value:" + value);
            }
        }
        
        List list = null;
        int recordCount;
        int pageSize=PageSize.intValue();
        int currentPage=CurrentPage.intValue();
        Map map=new HashMap();
        int beginRecord = pageSize * (currentPage - 1);
        StringBuffer queryBuffer = new StringBuffer();
        StringBuffer queryCount = new StringBuffer();
        queryCount.append("SELECT COUNT(*) FROM ");
        queryCount.append(fromPO);
        queryCount.append(" ");
        int p = where.toUpperCase().indexOf("ORDER BY");
        if(p >= 0){
            queryCount.append(where.substring(0, p));
        }else{
            queryCount.append(where);
        }

        queryBuffer.append("SELECT ");
        queryBuffer.append(selectPara);
        queryBuffer.append(" FROM ");
        queryBuffer.append(fromPO);
        queryBuffer.append(" ");
        queryBuffer.append(where);
        begin();
        //查询总条数的query
        Query countQuery=null;
        //查询字段的query
        Query searchQuery=null;
        try {
            //取得总计录数
            countQuery = session.createQuery(queryCount.toString());
            searchQuery = session.createQuery(queryBuffer.toString());
            //-------------------------加变量参数start------------------------------
            if (paramap != null) {
                Iterator it = paramap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = "" + entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof Collection) {
                        countQuery.setParameterList(key, (Collection) value);
                        searchQuery.setParameterList(key, (Collection) value);
                    } else if (value instanceof Object[]) {
                        countQuery.setParameterList(key, (Object[]) value);
                        searchQuery.setParameterList(key, (Object[]) value);
                    } else if(value instanceof  java.util.Date){
                    	java.util.Date  date=(java.util.Date)value;
                        System.out.println("时间:"+date.toLocaleString());
                    	countQuery.setTimestamp(key,date);
                        searchQuery.setTimestamp(key,date);                  	
                    } else {
                        countQuery.setParameter(key, value);
                        searchQuery.setParameter(key, value);
                    }
                }
            }
            //-------------------------加变量参数end ------------------------------

            if (queryCount.toString().toUpperCase().indexOf(" GROUP BY ") > 0) {
                recordCount = countQuery.list().size();
            } else {
                if (selectPara.toUpperCase().indexOf("DISTINCT") >= 0) {
                    recordCount = searchQuery.list().size();
                } else {
                    List rlist = countQuery.list();
                    if (rlist != null && rlist.size() > 0) {
                        recordCount = ((Integer) rlist.get(0)).intValue();
                    } else {
                        recordCount = 0;
                    }
                }
            }

            //总条数
            map.put("recordCount", new Integer(recordCount));
            //设置分页开始结束
            searchQuery.setFirstResult(beginRecord);
            searchQuery.setMaxResults(pageSize);
            //查询结果
            list = searchQuery.list();
            map.put("list", list);
        } catch (Exception e) {
            logger.error("-------------getResult出错信息：------------------");
            logger.error("出错信息：",e);
            logger.error(e.getMessage());
            logger.error("selectPara："+selectPara);
            logger.error("fromPO："+fromPO);
            logger.error("where："+where);
            logger.error("PageSize："+PageSize);
            logger.error("CurrentPage："+CurrentPage);
            logger.error("queryBuffer:"+queryBuffer.toString());
            if (paramap != null) {
                Iterator it = paramap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    String key = "" + entry.getKey();
                    Object value = entry.getValue();
                    logger.error("key:" + key + "  value:" + value);
                }
            }
            e.printStackTrace();
            logger.error("-------------getResult出错------------------");
            session.close();
        } finally {
            session.close();

        }
        return map;
    }
    
    /**
     * 处理  % 与 _ 查询的转移字符
     * @param where
     * @param paramap
     * @return
     */
    private String dealSpecialStr(String where, Map paramap){
    	String resultWhere = where;
    	try{
			
			logger.debug("dealSpecialStr  ："+resultWhere);
			int likeIndex = 0;
			//使用like 的变量名
			List likeParaList = new ArrayList();
			//寻找使用like 的变量名
			if (where != null && where.indexOf(" like ") >= 0&&where.indexOf(":", likeIndex)>=0) {
				while (where.indexOf(" like ", likeIndex) > 0&&where.indexOf(":", likeIndex)>0) {
					likeIndex = where.indexOf(" like ", likeIndex);
					int likeParaIndex = where.indexOf(":", likeIndex);
					int index0 = where.indexOf(" ", likeParaIndex);
					int index1 = where.indexOf(")", likeParaIndex);
					int index2 = where.indexOf(">", likeParaIndex);
					int index3 = where.indexOf("<", likeParaIndex);
					int index4 = where.indexOf("=", likeParaIndex);
					int index5 = where.indexOf("?", likeParaIndex);
					int index6 = where.indexOf("!", likeParaIndex);
	
					int length = where.length();
					int intArr[] = { index0, index1, index2, index3, index4, index5, index6, length };
					int likeSpaceIndex = getMinValue(intArr);
					logger.debug("likeParaIndex:"+likeParaIndex);
					logger.debug("likeSpaceIndex:"+likeSpaceIndex); 
					String likePara =null;
					if(likeSpaceIndex>likeParaIndex&&likeParaIndex>0){
					    likePara = where.substring(likeParaIndex, likeSpaceIndex);
					}
					//没有变量  或者取出来的所谓变量 中含有空格等字符（其实不是变量名）
					if(likePara==null||likePara.indexOf(" ")>=0){					
					}else{
					  likeParaList.add(likePara);
					}
					logger.debug("likePara:" + likePara);
					
					likeIndex++;
					logger.debug("likePara:"+ where.indexOf(" like ", likeIndex));
				}
			}
			
	    	boolean neddDeal=false;
	    	Map changeParaMap=new HashMap();
	    	if (paramap != null) {
	            Iterator it = paramap.entrySet().iterator();
	            while (it.hasNext()) {
	                Map.Entry entry = (Map.Entry) it.next();
	                String key = "" + entry.getKey();
	                Object value = entry.getValue();
	                if(value instanceof String &&likeParaList.contains(":"+key)){ 
	                	String value_="";
	                	if(value!=null){
	                		value_=value.toString();
	                		if(value_.startsWith("%")&&value_.endsWith("%")){  
	                			value_=value_.substring(1,value_.length()-1);
	                			if(value_.indexOf("%")>=0||value_.indexOf("_")>=0){
		                			value_=value_.replaceAll("%", "/%");
		                			value_=value_.replaceAll("_", "/_");
		                			value_="%"+value_+"%";
		                			logger.debug("resultWhere change1:"+resultWhere);
		                			resultWhere=resultWhere.replaceAll(":"+key, ":"+key+" escape '/' ");
		                			logger.debug("resultWhere change2:"+resultWhere);
		                			logger.debug("value:"+value+"  value_:"+value_);
		                			changeParaMap.put(key, value_);
	                			}
	                		}
	                	}
	                } 
	            }
	            
	            //替换处理的变量值
	            if (changeParaMap != null) {
	                Iterator changeit = changeParaMap.entrySet().iterator();
	                while (changeit.hasNext()) {
	                    Map.Entry entry = (Map.Entry) changeit.next();
	                    String key = "" + entry.getKey();
	                    String value = ""+entry.getValue();
	                    paramap.put(key, value);
	                   
	                }
	            }
	        }
    	}catch(Exception e){
    		logger.error(" dealSpecialStr出错");
    		logger.error("",e);
    	}
    	logger.debug("dealSpecialStr  结束  resultWhere："+resultWhere);
    	return resultWhere;
    }
    
    /**
     * 寻找 一组数中最小的值  <=-1不算
     * @param intArr
     * @return
     */
	public int getMinValue(int intArr[]) {
		int result = intArr[0];
		for (int i = 1; i < intArr.length; i++) {
			if (result <= -1) {
				result = intArr[i];
			} else if (intArr[i] <= -1) {

			} else {
				result = Math.min(result, intArr[i]);
			}
		}
		return result;
	}

}
