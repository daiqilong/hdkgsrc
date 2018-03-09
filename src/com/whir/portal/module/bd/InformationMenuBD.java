package com.whir.portal.module.bd;

import java.util.List;

import javax.ejb.CreateException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import net.sf.hibernate.Query;

import com.whir.common.hibernate.HibernateBase;

public class InformationMenuBD extends HibernateBase implements SessionBean {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SessionContext sessionContext;

  public void ejbCreate()
    throws CreateException
  {
  }

  public void ejbRemove()
  {
  }

  public void ejbActivate()
  {
  }

  public void ejbPassivate()
  {
  }

  public void setSessionContext(SessionContext sessionContext)
  {
    this.sessionContext = sessionContext;
  }

  public List getChannelMenu(String channelId, String domainId, String channelType) throws Exception{
  List list = null;
  begin();

  String tmpSql = "";
  try {
	  tmpSql = "select distinct po1.channelId, po1.channelName, po1.channelLevel, po1.channelParentId, po1.channelShowType,po1.channelIdString from com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po1, com.whir.ezoffice.information.channelmanager.po.InformationChannelPO po2 where po2.channelIdString like EZOFFICE.FN_LINKCHAR(EZOFFICE.FN_LINKCHAR('%$', EZOFFICE.FN_INTTOSTR(po1.channelId)), '$%')  and po1.domainId=" + 
          domainId + 
          " and po1.channelType=" + channelType + " start with po1.channelId = '"+channelId+"' connect by prior po1.channelId = po1.channelParentId";
    Query query = this.session.createQuery(tmpSql);
    list = query.list();
  } catch (Exception e) {
    System.out.println("----------------------------------------------");
    e.printStackTrace();
    System.out.println("error tmpSql:" + tmpSql);
    System.out.println("----------------------------------------------");
    throw e;
  } finally {
    this.session.close();
    this.session = null;
    this.transaction = null;
  }
  return list;
}
}
