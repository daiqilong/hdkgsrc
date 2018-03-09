package com.whir.ezoffice.formhandler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest; 

import org.apache.log4j.Logger;

import com.whir.ezflow.util.EzFlowUtil;
import com.whir.ezoffice.assetManager.bd.CommentBD;
import com.whir.ezoffice.bpm.actionsupport.BPMProcessScopeAction;
import com.whir.ezoffice.workflow.common.util.FormReflection;
import com.whir.service.api.ezflowservice.EzFlowMainService;


/**
 *
 * @author wanggl
 *
 */
public class EzFlow {
	
	private static Logger logger = Logger.getLogger(EzFlow.class.getName());
	
	public static final String  METHOD_SYN_STATUS="synProcessStatus";

    public static EzFlow getEzFlow(HttpServletRequest request) {
        return new EzFormFlow();
    }

    /**
     * 1：在办  -1：退回发起人  -2：取消     -3：作废100：  办理完毕  -10:草稿
     * @param command
     * @param request
     * @param userId
     * @return
     */
    public Map dealFlow(String command, HttpServletRequest request,
                        String userId) {
    	 
        Map resultMap = new HashMap();
        //发起
        if (command.equals("save")) {
        	/*//重启打开   ropenType： fromDraft 打开草稿     submitAgain 再次发起        reRubmit  重新提交
             String ropenType = request.getParameter("ropenType") == null ? "" :
                               request.getParameter("ropenType").toString();
            if (ropenType.equals("reRubmit")) {
                //做逻辑删除
                EzFlowMainService mainService = new EzFlowMainService();
                String processInstanceId = request.getParameter("processInstanceId").toString();
                mainService.deleteMyHisTask(processInstanceId);
                resultMap = _reSave(request, new Integer("1"));
            } else {
                resultMap = _save(request, new Integer("1"));
            }*/
        	
			String ropenType = request.getParameter("p_wf_openType") == null ? ""
					: request.getParameter("p_wf_openType").toString();
			//重启发起
			if (ropenType.equals("reStart")) {
				
				resultMap = _reSave(request, new Integer("1"));
				
			
			    String businessKey = "";
		        if (resultMap != null) {
		          businessKey =""+ resultMap.get("infoId");
		        }
		        //编号重复 
		        if ((businessKey != null) && (businessKey.equals("-2"))) {
		          //编号重复 不能删除
		        }else{
		        	EzFlowMainService mainService = new EzFlowMainService();
					String processInstanceId = request.getParameter("p_wf_processInstanceId").toString();
					mainService.deleteMyHisTask(processInstanceId);
		        }
			        
			        
			} else {
				resultMap = _save(request, new Integer("1"));
			}
        }
        //批量发起
        if (command.equals("batchSave")) {
            resultMap = _batchSave(request, userId, new Integer("1"));
        }
        //草稿箱发起
        if (command.equals("saveFromDraft")) {
            resultMap = _save(request, new Integer("-10"));
        }
        //草稿箱修改
        if (command.equals("updateFromDraft")) {
            resultMap = _update(request, new Integer("-10"));
        }
        //中间保存退出
        if (command.equals("updateClose")) {
            //resultMap = _update(request, new Integer("1")); 
    		String ezflowBusinessKey = request.getParameter("p_wf_recordId");
            if ((EzFlowUtil.judgeNull(ezflowBusinessKey))
					&& (!ezflowBusinessKey.equals("-1"))) {
				resultMap = _update(request, new Integer("1"));
			} else {
				resultMap = _save(request, new Integer("1"));
				updateWfRecord(request, "" + resultMap.get("infoId"));
			}
        }
        //中间办理
        if (command.equals("update")) {
            /*String ezflowBusinessKey = request.getParameter("ezflowBusinessKey");
            String gate_dealType = request.getParameter("gate_dealType");
            

         	System.out.println("dealFlow  ezflowBusinessKey:"+ezflowBusinessKey);
          	System.out.println("dealFlow  gate_dealType:"+gate_dealType);
            //办理完毕
            if (gate_dealType != null && gate_dealType.equals("COMPLETE")) {
                if (EzFlowUtil.judgeNull(ezflowBusinessKey) &&
                    !ezflowBusinessKey.equals("-1")) {
                    resultMap = _update(request, new Integer("1"));
                } else {
                    resultMap = _save(request, new Integer("1"));
                    updateWfRecord(request, "" + resultMap.get("infoId"));
                }
                resultMap = _complete(request, new Integer("100"));
            } else {
                if (EzFlowUtil.judgeNull(ezflowBusinessKey) &&
                    !ezflowBusinessKey.equals("-1")) {
                    resultMap = _update(request, new Integer("1"));
                	System.out.println("dealFlow  gate_dealType222:"+gate_dealType);
                } else {
                    resultMap = _save(request, new Integer("1"));
                    updateWfRecord(request, "" + resultMap.get("infoId"));
                }
            }*/
        	
			String ezflowBusinessKey = request.getParameter("p_wf_recordId");
			String gate_dealType = request.getParameter("gate_dealType");

			if ((gate_dealType != null) && (gate_dealType.equals("COMPLETE"))) {
				if ((EzFlowUtil.judgeNull(ezflowBusinessKey))
						&& (!ezflowBusinessKey.equals("-1"))) {
					resultMap = _update(request, new Integer("1"));
				} else {
					resultMap = _save(request, new Integer("1"));
					updateWfRecord(request, "" + resultMap.get("infoId"));
				}
				resultMap = _complete(request, new Integer("100"));
			} else if ((EzFlowUtil.judgeNull(ezflowBusinessKey))
					&& (!ezflowBusinessKey.equals("-1"))) {
				resultMap = _update(request, new Integer("1"));
			} else {
				resultMap = _save(request, new Integer("1"));
				updateWfRecord(request, "" + resultMap.get("infoId"));
			}

        }
        //退回
        if (command.equals("back")) {
            resultMap = _back(request, new Integer("-1"));
        }
        //取消
        if (command.equals("cancel")) {
            resultMap = _cancel(request,new Integer("-2"));
        }
        //退回发起人
        if (command.equals("backToSubmit")) {
            resultMap = _update(request, new Integer("1"));
            resultMap = _back(request,new Integer("-1"));
        }
        //办理完毕
        if (command.equals("complete")) {
            resultMap = _update(request, new Integer("100"));
            resultMap = _complete(request, new Integer("100"));
        }
        //作废
        if(command.equals("delete")){
        	 resultMap = _delete(request,new Integer("-2"));
        }

        return resultMap;
    }


    /**
     * 子流程的
     * @param request
     * @param record
     */
    private void updateWfRecord(HttpServletRequest request, String record) {
        String sql = " update  wf_work  set workrecord_id=" + record +
                     "  where   isezflow=1 and ezflowprocessinstanceId=" +
                     request.getParameter("p_wf_processInstanceId");
        String sql2 = " update  EZ_FLOW_HI_PROCINST  set business_key_='" +
                      record + "'  where  proc_inst_id_='" +
                      request.getParameter("p_wf_processInstanceId") + "'";
        CommentBD bd = new CommentBD();
        bd.excuteBySQL(sql);
        bd.excuteBySQL(sql2);
    }

    
    /**
     * @param request
     * @param status
     * @return
     */
    public Map _save(HttpServletRequest request, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	// 保存方法
    	String methodeName="save";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_saveData"))){
    		methodeName=request.getParameter("p_wf_saveData").toString();
    	}
    	className="com.whir.ezoffice.formhandler."+className; 
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
        return result;       
    }

    public Map _batchSave(HttpServletRequest request, String userId, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="batchSave";
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,userId,status);	
        return result;
    }
    
    protected Map batchSave(HttpServletRequest request, String userId, Integer status) {
    	//处理类
    	/*String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="batchSave";
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);*/	
    	Map result=(Map)new EzFormFlow().batchSave(request, userId, status); 	
        return result;
    }
    
    /**
     * 重新发起 
     * @param request
     * @param status
     * @return
     */
    public Map _reSave(HttpServletRequest request, Integer status) {
    	//处理类
    	/*String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="reSave";
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	*/
//    	Map result=(Map)new EzFormFlow().reSave(request, status); 	
//        return result;  
        
        
      //处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="reSave"; 
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
        return result;
    }
    public Map _update(HttpServletRequest request, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="update";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_updateData"))){
    		methodeName=request.getParameter("p_wf_updateData").toString();
    	}
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
 
        return result;
         
    }
    
    /**
     * 
     * @param request
     * @param status
     * @return
     */
    public Map _back(HttpServletRequest request, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="back";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_backData"))){
    		methodeName=request.getParameter("p_wf_backData").toString();
    	}
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
        return result;   
    }
    
    /**
     * 完成时方法
     * @param request
     * @param status
     * @return
     */
    public Map _complete(HttpServletRequest request, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="complete";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_completeData"))){
    		methodeName=request.getParameter("p_wf_completeData").toString();
    	}
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
        return result;
    }
    
    /**
     * 取消
     * @param request
     * @param status
     * @return
     */
    public Map _cancel(HttpServletRequest request, Integer status) {
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="cancel"; 
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	
        return result;
    }
    
    
    /**
     * 作废
     * @param request
     * @param status
     * @return
     */
    public Map _delete(HttpServletRequest request, Integer status) {
    	
    	logger.debug("开始调用delete方法");
    	//处理类
    	String className="EzFormFlow";
    	if(EzFlowUtil.judgeNull(request.getParameter("p_wf_classname"))){
    		className=request.getParameter("p_wf_classname").toString();
    	}
    	String methodeName="delete"; 
    	className="com.whir.ezoffice.formhandler."+className;
    	Map result=(Map)executeMethod(className,methodeName,request,status);	

    	logger.debug("结束调用delete方法");
  
        return result;
    }
    
    public Map _updateStatus(String formCode, String infoId, Integer status) {
        return null;
    }
 
    
    public Object executeMethod(String className, String methodName, HttpServletRequest httpServletRequest,Integer stauts){
        Object result = null;
        try {
            Class cls = Class.forName(className);
            Constructor ct = cls.getConstructor(null);          
            Class[] arg = new Class[2];
            arg[0] = HttpServletRequest.class;
            arg[1] = Integer.class;    
            
            Object arglist[] = new Object[2];
            arglist[0] = httpServletRequest;
            arglist[1] = stauts;
            
            Method meth = cls.getMethod(methodName, arg);
            Object retobj = ct.newInstance(null);
  
            result = meth.invoke(retobj, arglist);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    public Object executeMethod(String className, String methodName, HttpServletRequest httpServletRequest,String userId,Integer stauts){
        Object result = null;
        try {
            Class cls = Class.forName(className);
            Constructor ct = cls.getConstructor(null);          
            Class[] arg = new Class[3];
            arg[0] = HttpServletRequest.class;
            arg[1] = String.class; 
            arg[2] = Integer.class;    
            
            Object arglist[] = new Object[3];
            arglist[0] = httpServletRequest;
            arglist[1] = userId;
            arglist[2] = stauts;
            
            Method meth = cls.getMethod(methodName, arg);
            Object retobj = ct.newInstance(null);
  
            result = meth.invoke(retobj, arglist);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
    
    
    
    public Object executeStatusMethod(String className, String methodName, Map invMap){
        Object result = null;
        try {
            Class cls = Class.forName(className);
            Constructor ct = cls.getConstructor(null);          
            Class[] arg = new Class[1];
            arg[0] = Map.class;   
            
            Object arglist[] = new Object[1];
            arglist[0] = invMap; 
            
            Method meth = cls.getMethod(methodName, arg);
            Object retobj = ct.newInstance(null);
  
            result = meth.invoke(retobj, arglist);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /***
     * 
     * @param inVarMap   
     *         Map中 键值:  p_wf_recordId     :    业务主键id
     *                      p_wf_workStatus   :   当前流程的状态   100 :流程结束     -1:退回发起人     
     * @return
     */
    public  Map  synProcessStatus(Map inVarMap){
    	logger.debug("父类sysProcessStatus ");
    
    	//业务主键id
    	String p_wf_recordId=inVarMap.get("p_wf_recordId")==null?"":inVarMap.get("p_wf_recordId").toString();
    	//状态
    	String p_wf_workStatus=inVarMap.get("p_wf_workStatus")==null?"":inVarMap.get("p_wf_workStatus").toString();
    	
    	//返回值 暂要求返回一个空的Map
    	Map resultMap=new HashMap();
    	return resultMap;
    }

}