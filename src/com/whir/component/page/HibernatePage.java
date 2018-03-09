package com.whir.component.page;

import java.util.List;
import java.util.Map;

public class HibernatePage extends PageBase
  implements Page
{
  public List getResultList()
  {
    HibernatePageImpl pageImpl = new HibernatePageImpl();
    try {
      this.resultMap = pageImpl.getResult(this.selectPara, this.fromPara, 
        this.wherePara + this.orderByPara, 
        new Integer(this.pageSize), 
        new Integer(this.currentPage), 
        this.varMap);
      buildResult();

      if ((this.resultList.size() == 0) && (this.currentPage > 1)) {
        this.currentPage -= 1;
        this.resultMap = pageImpl.getResult(this.selectPara, this.fromPara, 
          this.wherePara + this.orderByPara, 
          new Integer(this.pageSize), 
          new Integer(this.currentPage), 
          this.varMap);
        buildResult();
      }
    }
    catch (Exception localException) {
    }
    return this.resultList;
  }
  public void setSelectPara(String selectPara) {
    this.selectPara = selectPara;
  }

  public void setWherePara(String wherePara) {
    this.wherePara = wherePara;
  }

  public void setResultMap(Map resultMap) {
    this.resultMap = resultMap;
  }

  public void setFromPara(String fromPara) {
    this.fromPara = fromPara;
  }

  public void setOrderByPara(String orderByPara) {
    this.orderByPara = orderByPara;
  }

  public HibernatePage(String selectPara, String fromPara, String wherePara, String orderByPara)
  {
    this.selectPara = selectPara;
    this.fromPara = fromPara;
    this.wherePara = wherePara;
    this.orderByPara = orderByPara;
  }
}