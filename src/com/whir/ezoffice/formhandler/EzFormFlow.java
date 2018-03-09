package com.whir.ezoffice.formhandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import com.whir.common.util.CommonUtils;
import com.whir.ezoffice.bpm.bd.BPMFormInfoBD;
import com.whir.ezoffice.bpm.bd.BPMOutDataSourceBD;
import com.whir.ezoffice.customdb.common.util.DbOpt;
import com.whir.ezoffice.ezform.bd.FormDataBD;
import com.whir.ezoffice.ezform.frontHtml.Utils;
import com.whir.ezoffice.ezform.ui.UIBD;
import com.whir.ezoffice.formhandler.runtime.RuntimeProcessFactory;
import com.whir.ezoffice.information.infomanager.bd.NewInformationBD;

/**
 * 表单流转
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: </p>
 * @author w3
 * @version 10.3.0.0
 */
public class EzFormFlow extends EzFlow {
    
    private static Logger logger = Logger.getLogger(EzFormFlow.class.getName());

    static byte[] LOCK = new byte[0]; 

    private RuntimeProcessFactory factory = RuntimeProcessFactory.getInstance();

    //1：在办
    //-1：退回发起人 
    //-2：取消
    //-3：作废
    //100：办理完毕
    //
    //-10:草稿

    public EzFormFlow() {
    }

    /**
     * 保存
     * @param request HttpServletRequest
     * @param status int
     * @return Map
     */
    public Map save(HttpServletRequest request, Integer status) {
        logger.debug("保存[save]...");
        
        BPMFormInfoBD bpmBD =new BPMFormInfoBD();
        String fieldValueContent ="";
    	String fieldValueFile ="";
    	String p_wf_formKey =request.getParameter("p_wf_formKey");
    	//p_wf_processName 获取流程名称
    	String processName =request.getParameter("p_wf_processName");
        
        //来之evo客户端 不调用下面的PC方法。
    	String p_wf_client_type=request.getParameter("p_wf_client_type");
    	if(p_wf_client_type!=null&&p_wf_client_type.equals("evo")){ 
    		Map newMap=new HashMap();
    		newMap.put("infoId", request.getParameter("p_wf_recordId"));
    		return newMap;
    	}
    	
        long startTime = System.currentTimeMillis();
        
        Map result = new HashMap();

        String infoId = "";//新增为空
        String formId = request.getParameter("formId");
        synchronized(LOCK) {
            infoId = new FormDataBD().save(request, status);
            
            logger.debug("<-----getData-EzFormFlow-start--->");
            if(p_wf_formKey !=null && !"".equals(p_wf_formKey)){
            	try {
                	String[] dataStr =bpmBD.getData("ezflow",p_wf_formKey, Long.valueOf(infoId));
                	if(dataStr !=null){
                		fieldValueContent =dataStr[0];
                		fieldValueFile =dataStr[1];
                	}
                } catch(Exception e) {
                	e.printStackTrace();
                	logger.debug("<-----getData 异常----->");
                }
            }
            logger.debug("<-----getData-EzFormFlow-end--->");
        }
        
        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("save", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        long endTime = System.currentTimeMillis();
        
        logger.debug("消耗时间：" + (endTime - startTime));
        
        //全文检索 2015/12/14
        logger.debug("fieldValueContent--EzFormFlow--->"+fieldValueContent);
        logger.debug("fieldValueFile--EzFormFlow--->"+fieldValueFile);
        if(!CommonUtils.isEmpty(infoId)){
        	if(!CommonUtils.isEmpty(processName)){
        		fieldValueContent =fieldValueContent + processName;
        	}
        	NewInformationBD newInformationbd =new NewInformationBD();
        	newInformationbd.saveInformationLucene(new String[]{String.valueOf(infoId),"1","workflow","1",p_wf_formKey,fieldValueContent,fieldValueFile});
        }

        return result;
    }

    /**
     * 批量发起时调用
     * @param request HttpServletRequest
     * @param userId String
     * @param status int
     * @return Map
     */
    public Map batchSave(HttpServletRequest request, String userId, Integer status) {
        logger.debug("批量发起保存[batchSave]...");
        
        Map result = new HashMap();

        String infoId = "";//新增为空
        String formId = request.getParameter("formId");
        //用于批量发起生成业务数据
        request.setAttribute("temp_batchSendUserId", userId);

        synchronized(LOCK) {
            infoId = new FormDataBD().save(request, status);
            
            logger.debug("infoId--batchSave--->"+infoId);
        }

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("save", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        //全文检索 2015/12/14
        if(!CommonUtils.isEmpty(infoId)){
        	//NewInformationBD newInformationbd =new NewInformationBD();
        	//newInformationbd.saveInformationLucene(new String[]{String.valueOf(infoId),"1","workflow"});
        }

        return result;
    }

    /**
     * 重新发起保存，删除原数据
     * @param request HttpServletRequest
     * @param status int
     * @return Map
     */
    public Map reSave(HttpServletRequest request, Integer status) {
        logger.debug("重新发起保存[reSave]...");
        
        BPMFormInfoBD bpmBD =new BPMFormInfoBD();
        String fieldValueContent ="";
    	String fieldValueFile ="";
        String p_wf_formKey =request.getParameter("p_wf_formKey");
        
        Map result = new HashMap();

        String infoId = request.getParameter("infoId"); //表单记录ID
        String formId = request.getParameter("formId");
        //用于重新发起
        request.setAttribute("reSave", "1");
        String deleteRecordId = infoId;
        synchronized(LOCK) {
            infoId = new FormDataBD().save(request, status);
            
            logger.debug("<-----getData-EzFormFlow-start--->");
            if(p_wf_formKey !=null && !"".equals(p_wf_formKey)){
            	try {
                	String[] dataStr =bpmBD.getData("ezflow",p_wf_formKey, Long.valueOf(infoId));
                	if(dataStr !=null){
                		fieldValueContent =dataStr[0];
                		fieldValueFile =dataStr[1];
                	}
                } catch(Exception e) {
                	e.printStackTrace();
                	logger.debug("<-----getData 异常----->");
                }
            }
            logger.debug("<-----getData-EzFormFlow-end--->");
            if(!infoId.equals("-2")){
            	deleteRecord(formId, deleteRecordId);
            }
        }

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("save", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        //全文检索 2015/12/14
        if(!CommonUtils.isEmpty(infoId)){
        	String processName =request.getParameter("p_wf_processName");
        	if(!CommonUtils.isEmpty(processName)){
        		fieldValueContent =fieldValueContent + processName;
        	}
        	NewInformationBD newInformationbd =new NewInformationBD();
        	newInformationbd.saveInformationLucene(new String[]{String.valueOf(infoId),"1","workflow","1",p_wf_formKey,fieldValueContent,fieldValueFile});
        }

        return result;
    }

    /**
     * 办理环节更新
     * @param request HttpServletRequest
     * @param status int
     * @return Map
     */
    public Map update(HttpServletRequest request, Integer status) {
        logger.debug("办理环节更新[update]...");
        
        BPMFormInfoBD bpmBD =new BPMFormInfoBD();
        String fieldValueContent ="";
    	String fieldValueFile ="";
        String p_wf_formKey =request.getParameter("p_wf_formKey");
        
        //来之evo客户端 不调用下面的PC方法。
    	String p_wf_client_type=request.getParameter("p_wf_client_type");
    	if(p_wf_client_type!=null&&p_wf_client_type.equals("evo")){ 
    		Map newMap=new HashMap();
    		newMap.put("infoId", request.getParameter("p_wf_recordId"));
    		return newMap;
    	}
    	
    	//外部数据源配置功能操作-----开始
    	this.operateOutDataSource(request, "DEAL");
    	//外部数据源配置功能操作-----结束
    	
        long startTime = System.currentTimeMillis();
        
        Map result = new HashMap();

        String infoId = request.getParameter("infoId"); //表单记录ID
        String formId = request.getParameter("formId");
        synchronized(LOCK) {
            new FormDataBD().update(request, status);
            
            logger.debug("<-----getData-EzFormFlow-start--->");
            if(p_wf_formKey !=null && !"".equals(p_wf_formKey)){
            	try {
                	String[] dataStr =bpmBD.getData("ezflow",p_wf_formKey, Long.valueOf(infoId));
                	if(dataStr !=null){
                		fieldValueContent =dataStr[0];
                		fieldValueFile =dataStr[1];
                	}
                } catch(Exception e) {
                	e.printStackTrace();
                	logger.debug("<-----getData 异常----->");
                }
            }
            logger.debug("<-----getData-EzFormFlow-end--->");
        }

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("update", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        long endTime = System.currentTimeMillis();
        
        logger.debug("消耗时间：" + (endTime - startTime));
        
        //全文检索 2015/12/14
        if(!CommonUtils.isEmpty(infoId)){
        	String processName =request.getParameter("p_wf_processName");
        	if(!CommonUtils.isEmpty(processName)){
        		fieldValueContent =fieldValueContent + processName;
        	}
        	NewInformationBD newInformationbd =new NewInformationBD();
        	newInformationbd.saveInformationLucene(new String[]{String.valueOf(infoId),"2","workflow","1",p_wf_formKey,fieldValueContent,fieldValueFile});
        }

        return result;
    }

    /**
     * 退回
     * @param request HttpServletRequest
     * @param status int
     * @return Map
     */
    public Map back(HttpServletRequest request, Integer status) {
        logger.debug("退回[back]...");
        
        //来之evo客户端 不调用下面的PC方法。
    	String p_wf_client_type=request.getParameter("p_wf_client_type");
    	if(p_wf_client_type!=null&&p_wf_client_type.equals("evo")){ 
    		Map newMap=new HashMap();
    		newMap.put("infoId", request.getParameter("p_wf_recordId"));
    		return newMap;
    	}
    	
    	//外部数据源配置功能操作-----开始
    	this.operateOutDataSource(request, "BACK");
    	//外部数据源配置功能操作-----结束
    	
        long startTime = System.currentTimeMillis();
        
        Map result = new HashMap();

        String formId = request.getParameter("formId");
        String infoId = request.getParameter("infoId");
        DbOpt dbopt = null;
        try {
            UIBD uibd = new UIBD();
            dbopt = new DbOpt();

            String[][] tableIdName = uibd.getTableIDAndName(formId);

            /*String[][] fields = dbopt.executeQueryToStrArr2(
                "select field_id, field_name, field_value, field_codevalue from tfield where field_show = 111 and field_table = " + tableIdName[0][0], 4);
            if(fields != null && fields.length > 0) {
                ResultSet rs = dbopt.executeQuery("select * from " + tableIdName[0][1] + " where " + tableIdName[0][1] + "_id = " + infoId);
                ResultSetMetaData rsmd = rs.getMetaData();
                boolean isExist_workStatus = false;
                for(int j = 0; j < rsmd.getColumnCount(); j++) {
                    if(rsmd.getColumnName(j + 1).equalsIgnoreCase(tableIdName[0][1] + "_workstatus")) {
                        isExist_workStatus = true;
                        break;
                    }
                }
                for(int i = 0; i < fields.length; i++) {
                    String val = dbopt.executeQueryToStr("select " +
                        fields[i][1] + " from " + tableIdName[0][1] + " where " + tableIdName[0][1] + "_id = " + infoId);
                    if(val != null && !"".equals(val) && !"null".equals(val.toLowerCase())) {
                        String[] temp = fields[i][2] == null ? null : fields[i][2].split("=");
                        if(temp != null && temp.length > 0) {
                            String codeAdd = temp[4]; //增长步长
                            String codeValue = fields[i][3];
                            //long value = Long.parseLong(codeValue) - Long.parseLong(codeAdd);
                            //dbopt.executeUpdate("update tfield set field_codevalue = " +value + " where FIELD_ID = " + fields[i][0]);
                            //?
                            //dbopt.executeUpdate("update " + tableIdName[0][1] + " set " + fields[i][1] + " = null where " + tableIdName[0][1] + "_id = " + recordId);
                            //退回
                            if(isExist_workStatus)
                                dbopt.executeUpdate("update " + tableIdName[0][1] + " set " + tableIdName[0][1] +
                                    "_workstatus = -1 where " + tableIdName[0][1] + "_id = " + infoId);
                        }
                    }
                }
            }*/

            dbopt.executeUpdate("update " + tableIdName[0][1] + " set " + tableIdName[0][1] + "_workstatus = -1 where " + tableIdName[0][1] + "_id = " + infoId);
            
            String[][] subTableName = dbopt.executeQueryToStrArr2("select distinct t.table_name from ttable t where t.table_id in (select table_id from ez_form_table where is_main_table='0' and form_id="+formId+")", 1);
            if(subTableName != null){
                for(int i=0; i<subTableName.length; i++){
                    logger.debug("subTableName:"+subTableName[i][0]);
                    if(!CommonUtils.isEmpty(subTableName[i][0])){
                        dbopt.executeUpdate("update " + subTableName[i][0] + " set " + subTableName[i][0] + "_workstatus=-1 " + " where " + subTableName[i][0] + "_foreignkey=" + infoId);
                    }
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt != null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("back", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        long endTime = System.currentTimeMillis();
        
        logger.debug("消耗时间：" + (endTime - startTime));

        return result;
    }

    public Map complete(HttpServletRequest request, Integer status) {
        logger.debug("完毕[complete]...");
        //来之evo客户端 不调用下面的PC方法。
    	String p_wf_client_type=request.getParameter("p_wf_client_type");
    	if(p_wf_client_type!=null&&p_wf_client_type.equals("evo")){ 
    		Map newMap=new HashMap();
    		newMap.put("infoId", request.getParameter("p_wf_recordId"));
    		return newMap;
    	}
    	
    	
        Map result = new HashMap();

        String formId = request.getParameter("formId");
        String infoId = request.getParameter("infoId");
        DbOpt dbopt = null;
        try {
            UIBD uibd = new UIBD();
            dbopt = new DbOpt();

            String[][] tableIdName = uibd.getTableIDAndName(formId);

            /*java.sql.ResultSet rs = dbopt.executeQuery("select * from " + tableIdName[0][1] + " where " + tableIdName[0][1] + "_id = " + infoId);
            java.sql.ResultSetMetaData rsmd = rs.getMetaData();
            boolean isExist_workStatus = false;
            for(int j = 0; j < rsmd.getColumnCount(); j++) {
                if(rsmd.getColumnName(j + 1).equalsIgnoreCase(tableIdName[0][1] + "_workstatus")) {
                    isExist_workStatus = true;
                    break;
                }
            }
            //办理完毕
            if(isExist_workStatus) {
                dbopt.executeUpdate("update " + tableIdName[0][1] + " set " + tableIdName[0][1] +
                                    "_workstatus = 100 where " + tableIdName[0][1] + "_id = " + infoId);
            }*/
            dbopt.executeUpdate("update " + tableIdName[0][1] + " set " + tableIdName[0][1] + "_workstatus = 100 where " + tableIdName[0][1] + "_id = " + infoId);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt != null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("complete", infoId, formId, request, status);
        //----------------------------------------------------------------------

        return result;
    }

    public Map updateStatus(String formCode, String infoId, Integer status) {        
        DbOpt dbopt = null;
        try {
            UIBD uibd = new UIBD();
            dbopt = new DbOpt();
            
            String formId = uibd.getFormIdByFormCode(formCode, "0");

            String[][] tableIdName = uibd.getTableIDAndName(formId);

            dbopt.executeUpdate("update " + tableIdName[0][1] + " set " +
                                    tableIdName[0][1] + "_workstatus = "+ status.intValue() +" where " +
                                    tableIdName[0][1] + "_id = " + infoId);

        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dbopt != null)dbopt.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Map delete(HttpServletRequest request, Integer status) {
        logger.debug("删除[delete]...");
        
        Map result = new HashMap();

        String formId = request.getParameter("formId");
        String infoId = request.getParameter("infoId");

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("delete", infoId, formId, request, status);
        //----------------------------------------------------------------------
        
        //全文检索 2015/12/14
        if(!CommonUtils.isEmpty(infoId)){
        	NewInformationBD newInformationbd =new NewInformationBD();
        	newInformationbd.saveInformationLucene(new String[]{String.valueOf(infoId),"3","workflow"});
        }

        return result;
    }
    
    public Map cancel(HttpServletRequest request, Integer status) {
        logger.debug("取消[cancel]...");
        
        Map result = new HashMap();

        String formId = request.getParameter("formId");
        String infoId = request.getParameter("infoId");

        result.put("infoId", infoId);

        //----------------------------------------------------------------------
        factory.invokeRuntimeProcess("cancel", infoId, formId, request, status);
        //----------------------------------------------------------------------

        return result;
    }
    
    /***  
     *   
     * @param inVarMap     
     *         Map中 键值:  p_wf_recordId     :    业务主键id  
     *                      p_wf_workStatus   :   当前流程的状态   100 :流程结束     -1:退回发起人       
     * @return  
     */  
    public Map synProcessStatus(Map inVarMap){
        logger.debug("开始synProcessStatus ");
        
        //业务主键id  
        String p_wf_recordId = inVarMap.get("p_wf_recordId")==null?"-1":inVarMap.get("p_wf_recordId").toString();  
        
        //状态  100 :流程结束     -1:退回发起人   
        String p_wf_workStatus = inVarMap.get("p_wf_workStatus")==null?"1":inVarMap.get("p_wf_workStatus").toString();  
        
//        传入参数inVarMap 的键值：  
//        p_wf_recordId     :    业务主键id    
//        p_wf_workStatus   :    当前流程的状态   100 :流程结束     -1:退回发起人    -2:取消      -3: 作废   1:办理中      0:发起  
//                               目前就只 100  与 -1  这个两个状态会调用这个方法。   
//                                 
//        p_wf_pool_processId  :  流程定义id  
//        p_wf_tableId         :  老流程 tableId (只老流程有值)  
//        p_wf_formKey         :  新流程表单key  (只新流程有值)  
//        p_wf_curUserId       :  当前人userId  
        
        String p_wf_pool_processId = inVarMap.get("p_wf_pool_processId")==null?"":inVarMap.get("p_wf_pool_processId").toString();
        String p_wf_tableId = inVarMap.get("p_wf_tableId")==null?"":inVarMap.get("p_wf_tableId").toString();
        String p_wf_formKey = inVarMap.get("p_wf_formKey")==null?"":inVarMap.get("p_wf_formKey").toString();
        String p_wf_curUserId = inVarMap.get("p_wf_curUserId")==null?"":inVarMap.get("p_wf_curUserId").toString();
                
        //返回值 暂要求返回一个空的Map  
        Map resultMap=new HashMap();
        
        //调用预算接口
        Map inputMap = new HashMap(); 
        inputMap.put("p_wf_recordId", p_wf_recordId);
        inputMap.put("pool_process_id", p_wf_pool_processId);
        inputMap.put("p_wf_workStatus", p_wf_workStatus);
        
        inputMap.put("p_wf_tableId", p_wf_tableId);
        inputMap.put("p_wf_formKey", p_wf_formKey);
        
        inputMap.put("userId", p_wf_curUserId);
        //根据userId获取用户相关信息
        String[][] userInfo = new Utils().getUserInfo(p_wf_curUserId);

        inputMap.put("userName", userInfo[0][1]);
        inputMap.put("orgId", userInfo[0][3]);
        inputMap.put("orgName", userInfo[0][4]);
        inputMap.put("domainId", userInfo[0][14]);
        
        //costApplyList：从自定义表中查询的内容，costApplyList 是String[]数组，
        //数组中的内容如下，顺序就按照这个顺序即可:预算部门id、预算科目id、预算年月、预算金额
        List costApplyList = new UIBD().getBudgetCostApply(inputMap);
        inputMap.put("costApplyList", costApplyList);
        
        //更新业务数据状态
        this.updateStatus(p_wf_formKey, p_wf_recordId, new Integer(p_wf_workStatus));

        return resultMap;
    }
    
    public boolean deleteRecord(String formId, String recordId) {
        logger.debug("删除[deleteRecord]...");
        
        boolean result = false;
        DbOpt dbopt = new DbOpt();
        try {
            
            String mTableName = dbopt.executeQueryToStr("select t.table_name from ttable t where t.table_id in (select table_id from ez_form_table where is_main_table='1' and form_id="+formId+")");
            
            if (!CommonUtils.isEmpty(mTableName)) {
                dbopt.executeUpdate("delete from " + mTableName + " where " + mTableName + "_id=" + recordId);
                
                String[][] subTableName = dbopt.executeQueryToStrArr2("select distinct t.table_name from ttable t where t.table_id in (select table_id from ez_form_table where is_main_table='0' and form_id="+formId+")", 1);
                if(subTableName != null){
                    for(int i=0; i<subTableName.length; i++){
                        logger.debug("subTableName:"+subTableName[i][0]);
                        if(!CommonUtils.isEmpty(subTableName[i][0])){
                            dbopt.executeUpdate("delete from " + subTableName[i][0] + " where " + subTableName[i][0] + "_foreignkey=" + recordId);
                        }
                    }
                }
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
        
        //全文检索 2015/12/14
        if(!CommonUtils.isEmpty(recordId)){
        	NewInformationBD newInformationbd =new NewInformationBD();
        	newInformationbd.saveInformationLucene(new String[]{String.valueOf(recordId),"3","workflow"});
        }
        
        return result;
    }
    
    /**
     * 处理外部数据源配置功能
     * outDataSourceType 处理类型：DEAL：办理中   BACK：退回    COMPLETE：办理完毕 
     */
    public void operateOutDataSource(HttpServletRequest request, String outDataSourceType){
    	String processId =request.getParameter("p_wf_processDefinitionKey");
    	String taskId =request.getParameter("p_wf_cur_activityId");
    	logger.debug("processId----->"+processId);
    	logger.debug("taskId----->"+taskId);
    	
    	if(!CommonUtils.isEmpty(processId) && !CommonUtils.isEmpty(taskId)){
    		BPMOutDataSourceBD bd =new BPMOutDataSourceBD();
    		//根据流程KEY、活动ID、办理类型查询外部数据源配置信息
	    	List outDataSourceList =bd.getBPMOutDataSourceInfo(processId, taskId, outDataSourceType);
	    	logger.debug("outDataSourceList----->"+outDataSourceList);
	    	if(outDataSourceList !=null && outDataSourceList.size() >0){
	    		String outDataSourceCode ="";
	    		String outDataSourceSql ="";
	    		String outDataSourceFields ="";
	    		String outDataSourceFieldsValue ="";
	    		String isProcessSet ="";
	    		logger.debug("outDataSourceList.size()----->"+outDataSourceList.size());
	    		for(int i=0;i<outDataSourceList.size();i++){
	    			Object[] obj =(Object[])outDataSourceList.get(0);
	    			outDataSourceCode =obj[0]==null?"":obj[0].toString();
	    			outDataSourceSql =obj[1]==null?"":obj[1].toString();
	    			outDataSourceFields =obj[2]==null?"":obj[2].toString();
	    			isProcessSet =obj[3]==null?"":obj[3].toString();
	    		}
	    		
	    		logger.debug("isProcessSet----->"+isProcessSet);
	    		
	    		logger.debug("outDataSourceFields----->"+outDataSourceFields);
	    		//1|whir$t3260_f4157,2|whir$t3260_f4156,2|whir$t3260_f4157
	    		if(!CommonUtils.isEmpty(outDataSourceFields)){
	    			String[] outDataSourceFieldsObj =outDataSourceFields.split(",");
	    			for(int j=0;j<outDataSourceFieldsObj.length;j++){
	    				logger.debug("outDataSourceFieldsObj----->"+outDataSourceFieldsObj[j]);
	    				if(!CommonUtils.isEmpty(outDataSourceFieldsObj[j])){
	    					String[] outDataSourceFieldsObj_param =outDataSourceFieldsObj[j].toString().split("\\|");
	    					
	    					logger.debug("outDataSourceFieldsObj-value---->"+request.getParameter(outDataSourceFieldsObj_param[1]));
	    					outDataSourceFieldsValue += outDataSourceFieldsObj_param[0] +"|"+ request.getParameter(outDataSourceFieldsObj_param[1]) +",";
	    				}
	    			}
	    			if(outDataSourceFieldsValue.endsWith(",")){
	    				outDataSourceFieldsValue =outDataSourceFieldsValue.substring(0, outDataSourceFieldsValue.length()-1);
	    			}
	    		}
	    		logger.debug("outDataSourceCode----->"+outDataSourceCode);
	    		logger.debug("outDataSourceSql----->"+outDataSourceSql);
	    		logger.debug("outDataSourceFieldsValue----->"+outDataSourceFieldsValue);
	    		
	    		bd.executeOperateBySql(outDataSourceCode, outDataSourceSql, outDataSourceFields, outDataSourceFieldsValue);
	    	}
    	}
    }
}
