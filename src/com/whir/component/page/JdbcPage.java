package com.whir.component.page;

import com.whir.common.util.DataSourceBase;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class JdbcPage extends PageBase
  implements Page
{
  public List getResultList_impl()
  {
    JdbcPageImpl jdbcPage = new JdbcPageImpl(getConnection(), this.extType);
    try {
      if (this.varMap == null) {
        this.resultMap = jdbcPage.getResult(this.selectPara, 
          this.fromPara, 
          this.wherePara, this.orderByPara, 
          new Integer(this.pageSize), 
          new Integer(this.currentPage));
        buildResult();

        if ((this.resultList.size() == 0) && (this.currentPage > 1)) {
          this.currentPage -= 1;
          getResultList();
        }
      } else {
        this.resultMap = jdbcPage.getResult(this.selectPara, 
          this.fromPara, 
          this.wherePara, this.orderByPara, 
          new Integer(this.pageSize), 
          new Integer(this.currentPage), this.varMap);
        buildResult();

        if ((this.resultList.size() == 0) && (this.currentPage > 1)) {
          this.currentPage -= 1;
          getResultList();
        }
      }
    }
    catch (Exception localException) {
    }
    return this.resultList;
  }

  public List getResultList()
  {
    getResultList_impl();
    return this.resultList;
  }

  public JdbcPage(String selectPara, String fromPara, String wherePara, String orderByPara)
  {
    this.selectPara = selectPara;
    this.fromPara = fromPara;
    this.wherePara = wherePara;
    this.orderByPara = orderByPara;
  }

  private Connection getConnection() {
    Connection conn = null;
    DataSourceBase dataSorce = new DataSourceBase();
    DataSource dataSource = dataSorce.getDataSource();
    try {
      conn = dataSource.getConnection();
    }
    catch (SQLException localSQLException) {
    }
    return conn;
  }
}