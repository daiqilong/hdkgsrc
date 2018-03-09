package com.whir.org.bd.MoveOAmanager;

import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

import org.apache.log4j.Logger;
import org.springframework.jndi.JndiTemplate;

import com.whir.common.hibernate.HibernateBase;
import com.whir.ezoffice.logon.po.SMSCheckcodePO;
import com.whir.org.vo.MoveOAmanager.CorpSetAppPO;
import com.whir.org.vo.MoveOAmanager.CorpSetPO;
import com.whir.org.vo.MoveOAmanager.UserOrgSynErrLogPO;

public class MoveOAmanagerBD extends HibernateBase {
	 private static Logger logger = Logger
     .getLogger(MoveOAmanagerBD.class.getName());
	   
	 public MoveOAmanagerBD() {
	 }
	 private JndiTemplate template;
	 
	
	 
	 public String saveUserOrgSynErrLogPO(List<UserOrgSynErrLogPO> polist) throws HibernateException {
	        String result = "false";
	        try {
	            begin();
	            for(int i=0;i<polist.size();i++){
	            	UserOrgSynErrLogPO po = polist.get(i);
	            	  session.save(po);
	            }
	           
	            result="true";
	            session.flush();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	            	session.close();
	                session = null;
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return result;
	    }
	 
	 public String deleteUserOrgSynErrLogPOByID(int id)throws Exception {
		 String res="false";
		  begin();
	        try {
	            session.delete("from UserOrgSynErrLogPO where id="+id);
	            session.flush();
	            res="true";
	        } catch(Exception e) {
	            throw e;
	        } finally {
	            session.close();
	            session = null;
	            transaction = null;
	        }
		  return res;
		 
	 }
	 
	 public String saveCorpSetPO(CorpSetPO po) throws HibernateException {
	        String result = "false";
	        try {
	            begin();
	            session.save(po);
	            result="true";
	            session.flush();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	            	session.close();
	                session = null;
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return result;
	    }
	 
	 public String deleteCorpSetPO(int id)throws Exception {
		 String res="false";
		  begin();
	        try {
	            session.delete("from CorpSetPO ");
	            session.flush();
	            res="true";
	        } catch(Exception e) {
	            throw e;
	        } finally {
	            session.close();
	            session = null;
	            transaction = null;
	        }
		  return res;
		 
	 }
	 
	 public String saveCorpSetAppPOPO(List<CorpSetAppPO> polist) throws HibernateException {
	        String result = "false";
	        try {
	            begin();
	            for(int i=0;i<polist.size();i++){
	            	CorpSetAppPO po = polist.get(i);
	            	  session.save(po);
	            }
	           
	            result="true";
	            session.flush();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	        } finally {
	            try {
	            	session.close();
	                session = null;
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        return result;
	    }
	 
	 public String deleteCorpSetAppPO()throws Exception {
		 String res="false";
		  begin();
	        try {
	            session.delete("from CorpSetAppPO ");
	            session.flush();
	            res="true";
	        } catch(Exception e) {
	            throw e;
	        } finally {
	            session.close();
	            session = null;
	            transaction = null;
	        }
		  return res;
		 
	 }
	 
	 public CorpSetPO getCorpSetPO() throws HibernateException{
		 CorpSetPO po=new CorpSetPO();
		 begin();
    	 
    	 List<CorpSetPO> polist = session.createQuery(" from CorpSetPO ").list();
    	 if(polist.size()>0){
    		 po =  polist.get(0);
    	 }
    	
         session.flush();
         session.close();
         session = null;
         transaction = null;
		 return po;
	 }
	 
	 public List<CorpSetAppPO> getCorpSetAppPO() throws HibernateException{
		
		 begin();
		 List<CorpSetAppPO> polist = session.createQuery(" from CorpSetAppPO ").list();
		
    	
         session.flush();
         session.close();
         session = null;
         transaction = null;
		 return polist;
	 }
}
