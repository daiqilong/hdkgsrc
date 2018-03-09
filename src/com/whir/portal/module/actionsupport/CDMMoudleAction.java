package com.whir.portal.module.actionsupport;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.whir.common.util.CommonUtils;
import com.whir.common.util.DataSourceBase;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.component.util.JacksonUtil;

public class CDMMoudleAction extends BaseActionSupport{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5957611521728273579L;
	private static Logger logger = Logger.getLogger(CDMMoudleAction.class.getName());

	/**
	 * CDM信息
	 *  @param request
	 *            HttpServletRequest
	 */
	public String flightInforQuery()
	  {
	    return "flightInforQuery";
	  }
	
	/**
	 * 查找CDM信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public String flightInforQueryData() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    Date date= new Date();
	    String dateStr = format.format(date);
	    
		String queryDate = request.getParameter("queryDate");
		String flno = request.getParameter("flno");
		int pageSize = CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        String viewSQL = "q.FLNO,d.ADEP,g.ADES,c.STOD,c.STOA,c.RWY,c.PBAY,c.ACT,c.ATOT,c.ALDT";
        String fromSQL = "cdminfor c,(select a.id,b.whir$jcxxzh_f3637 as adep from CDMINFOR a inner join whir$jcxxzh b on a.adep=b.whir$jcxxzh_f3636) d, "
        	+"(select e.id,f.whir$jcxxzh_f3637 as ades from CDMINFOR e inner join whir$jcxxzh f on e.ades=f.whir$jcxxzh_f3636) g, "
            +"(select o.id,concat(p.whir$hkgszh_f3631,substr(o.flno,4)) as flno from CDMINFOR o inner join whir$hkgszh p on substr(o.flno,1,3)=p.whir$hkgszh_f3630) q ";
        StringBuffer whereSQL = new StringBuffer();
        whereSQL.append(" Where 1=1 and c.id=d.id and g.id=c.id and q.id=c.id");
        if(queryDate!=null && !"".equals(queryDate)){
        	whereSQL.append(" and c.STOD like'%"+queryDate+"%' ");
        } else{
        	whereSQL.append(" and c.STOD like'%"+dateStr+"%' ");
        }
        if(flno!=null && !"".equals(flno)){
        	whereSQL.append(" and c.FLNO like'%"+flno+"%' ");
        }
        whereSQL.append(" order by c.STOD desc");
		Page page = PageFactory.getJdbcPage(viewSQL, fromSQL, whereSQL.toString(), "");
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        List list = page.getResultList();
        
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        JacksonUtil util = new JacksonUtil();
		String json = util.writeArrayJSON(viewSQL, list);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+"}";
		printResult(G_SUCCESS,json);
	    return null;
	}
	
	
	/**
	 * 查找CDM信息总数数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public Integer getFlightLineSearchRecordCount() {
        int recordCount=0;
        DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      String sql = "";
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      sql = "select count(*) from cdminfor c,(select a.id,b.whir$jcxxzh_f3637 as adep from CDMINFOR a inner join whir$jcxxzh b on a.adep=b.whir$jcxxzh_f3636) d, " +
	      		"(select e.id,f.whir$jcxxzh_f3637 as ades from CDMINFOR e inner join whir$jcxxzh f on e.ades=f.whir$jcxxzh_f3636) g, " +
	      		"(select o.id,concat(p.whir$hkgszh_f3631,substr(o.flno,4)) as flno from CDMINFOR o inner join whir$hkgszh p on substr(o.flno,1,3)=p.whir$hkgszh_f3630) q " +
	      		"Where 1=1 and c.id=d.id and g.id=c.id and q.id=c.id order by c.STOD desc";
	      System.out.println("sql值："+sql);
	      rs = stmt.executeQuery(sql);
	      while (rs.next()) {
	    	  recordCount = Integer.parseInt(rs.getString(1));
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	    return recordCount;
	}
	
	
	/**
	 * 查找CDM信息数据
	 * 
	 * @param request
	 *            HttpServletRequest
	 */
	public List<Map<String,String>> getFlightLineSearchData(String flno,String queryDate,String curPage) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		int pageSize=5;
		int max = Integer.parseInt(curPage)*pageSize;
		int min = (Integer.parseInt(curPage)-1)*pageSize+1;
        DataSourceBase dsb = new DataSourceBase();
	    Connection conn = null;
	    Statement stmt = null;
	    try {
	      conn = dsb.getDataSource().getConnection();
	      stmt = conn.createStatement();
	      ResultSet rs = null;
	      StringBuffer sql = new StringBuffer();
	      sql.append("SELECT * FROM (select m.*,rownum rn from(select q.FLNO,d.ADEP,g.ADES,c.STOD,c.STOA,c.RWY,c.PBAY,c.ACT,c.ATOT,c.ALDT from cdminfor c,(select a.id,b.whir$jcxxzh_f3637 as adep")
	      .append(" from CDMINFOR a inner join whir$jcxxzh b on a.adep=b.whir$jcxxzh_f3636) d,")
	      .append("(select e.id,f.whir$jcxxzh_f3637 as ades from CDMINFOR e inner join whir$jcxxzh f on e.ades=f.whir$jcxxzh_f3636) g, ")
	      .append("(select o.id,concat(p.whir$hkgszh_f3631,substr(o.flno,4)) as flno from CDMINFOR o inner join whir$hkgszh p on substr(o.flno,1,3)=p.whir$hkgszh_f3630) q ")
	      .append("Where 1=1 and c.id=d.id and g.id=c.id and q.id=c.id");
	      if(queryDate!=null && !"".equals(queryDate)){
	    	  sql.append(" and c.STOD like'%"+queryDate+"%' ");
	      }
	      if(flno!=null && !"".equals(flno)){
	    	  sql.append(" and c.FLNO like'%"+flno+"%' ");
	      }
	      sql.append(" order by c.STOD asc) m where rownum<="+max+") where rn>="+min);
	      System.out.println("sql值："+sql.toString());
	      rs = stmt.executeQuery(sql.toString());
	      while (rs.next()) {
	    	  Map<String,String> map = new HashMap<String,String>();
	           map.put("flno",rs.getString(1));
	           map.put("adep",rs.getString(2));
	           map.put("ades",rs.getString(3));
	           map.put("stod",rs.getString(4));
	           map.put("stoa",rs.getString(5));
	           map.put("rwy",rs.getString(6));
	           map.put("pbay",rs.getString(7));
	           map.put("act",rs.getString(8));
	           map.put("atot",rs.getString(9));
	           map.put("aldt",rs.getString(10));
	          list.add(map);
	      }
          rs.close();
	    } catch (SQLException ex) {
		      ex.printStackTrace();
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException2)
		      {
		      }
	   } finally {
		      try
		      {
		        if (stmt != null) {
		          stmt.close();
		        }
		        if (conn != null)
		          conn.close();
		      }
		      catch (SQLException localSQLException3) {
		      }
		}
	    return list;
	}
}
