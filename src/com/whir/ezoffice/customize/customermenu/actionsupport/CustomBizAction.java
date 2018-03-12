package com.whir.ezoffice.customize.customermenu.actionsupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.*;

import org.apache.log4j.Logger;
import com.whir.component.util.JacksonUtil;
import com.whir.component.util.StringUtils;
import com.whir.component.actionsupport.BaseActionSupport;
import com.whir.component.page.Page;
import com.whir.component.page.PageFactory;
import com.whir.common.util.CommonUtils;
import com.whir.common.util.ConversionString;
import com.whir.org.manager.bd.ManagerBD;

import com.whir.ezoffice.customdb.customdb.bd.CustomDatabaseBD;
import com.whir.ezoffice.customForm.bd.CustomFormBD;
import com.whir.ezoffice.customize.customermenu.bd.*;
import com.whir.ezoffice.customize.customermenu.common.*;
import com.whir.ezoffice.customize.customermenu.po.*;

import java.io.*;

public class CustomBizAction extends BaseActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomBizAction.class.getName());
	/**
	 * 打开自定义左侧菜单
	 * @return
	 */
    public String goLeftMenu(){
    	
    	return "custMenuLeftMenu";
    }
    /**
     * 自定义链接
     * @return
     */
    public String ssoLink(){
    	
    	return "ssoLink";
    }
    /**
     * 打开菜单右侧(导航)
     * @return
     */
    public String goRightMenu() throws Exception {
    	HttpSession session = request.getSession(true);
    	String local = session.getAttribute("org.apache.struts.action.LOCALE").toString();
    	String menuId=request.getParameter("menuId");
    	CustomerMenuConfigerPO po = new CustMenuWithOriginalBD().loadMenuSetById(menuId,CommonUtils.getSessionDomainId(request)+"");
    	//11.5需求  添加设置字段联动  2016/04/14 start
        Long trigSettingId=po.getTrigSettingId();        
        //11.5需求  添加设置字段联动  2016/04/14 end
    	boolean hasCustmenuAuth = new CustomerMenuDB().checkCustmenuAuth(po.getMenuViewUser(),
                po.getMenuViewOrg(),
                po.getMenuViewGroup(),
                CommonUtils.getSessionUserId(request)+"",
                CommonUtils.getSessionOrgIdString(request)+"",
                CommonUtils.getSessionDomainId(request)+"");
    	boolean isRefFlow = false;
    	boolean isNewRefFlow = false;
    	if (hasCustmenuAuth) {
            if (new ManagerBD().hasRight(CommonUtils.getSessionUserId(request)+"", 
            		                     "99-" + menuId + "-03")) {
                if (po.getMenuRefFlow() != null && 
                	po.getMenuRefFlow().length() > 0 &&
                    !"-1".equals(po.getMenuRefFlow())) {
                	
                	String[] menuRefFlows = po.getMenuRefFlow().toString().split("\\$");
                	if(menuRefFlows.length==2&&
    		        		"newFlow".equals(menuRefFlows[0])){
                		isNewRefFlow = true;
                	}
                    isRefFlow = true;
                }
            }
        }
    	request.setAttribute("isRefFlow", isRefFlow);
    	request.setAttribute("isNewRefFlow", isNewRefFlow);
    	
    	CustomDatabaseBD dbBD = new CustomDatabaseBD();
		String tableId = po.getMenuListTableMap() + "";
		String tableName = dbBD.getSingleTableName(tableId);
		
        String[][] queryFields = null;
        //优先自定义模块查询设置
        if(po.getMenuListQueryConditionElements()!=null&&
           !"null".equals(po.getMenuListQueryConditionElements())&&
           !"".equals(po.getMenuListQueryConditionElements())){
        	queryFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(po.getMenuListQueryConditionElements(),
        			                                                            CommonUtils.getSessionDomainId(request)+"");
        }else{                                                                     //自定义数据库查询设置
        	queryFields = dbBD.getQueryField(po.getMenuListTableMap().toString());
        }
        
        String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
        //生成查询
        String searchPart = "";
        if (queryFields!=null){
        	int pt = 0;
            int colms = 3; //默认三列
            int rows = (queryFields.length/colms) + 1;
            for(int i=0;i<rows;i++){
            	searchPart += "<tr>";
            	for(int j=0;j<colms;j++){
            		if(pt<queryFields.length){
            			searchPart +="<td class=\"whir_td_searchtitle\">"+queryFields[pt][1] + "：</td>";  
            			searchPart +="<td class=\"whir_td_searchinput\">"+new com.whir.ezoffice.customForm.bd.QueryFieldBD().getQueryFieldHTML(queryFields[pt][0],"_search")+"</td>";
            		}else{
            			if(i==rows-1&&j==colms-1){
            			  searchPart +="<td colspan=\"2\" class=\"SearchBar_toolbar\">"; 
            			  searchPart +="<input type=\"button\" class=\"btnButton4font\"  onclick=\"refreshListForm('queryForm');\"  value=\"立即查找\"/>&nbsp;";
            			  searchPart +="<input type=\"button\" class=\"btnButton4font\" value=\"清　除\" onclick=\"resetForm(this);\" />";
            			  searchPart +="</td>";  
            			}else{
            			  searchPart +="<td class=\"whir_td_searchtitle\">&nbsp;</td>";  
            			  searchPart +="<td class=\"whir_td_searchinput\">&nbsp;</td>";
            			}
            		}
            		pt++;
            	}
            	searchPart += "</tr>";
            }
        }
        request.setAttribute("searchPart", searchPart);
        
        //获取列表保存字段
    	List list22 = new CustomerMenuDB().getFieldControls(menuId,"4");
    	//可编辑字段
    	String custListFields ="";
    	if(list22 != null && list22.size()>0){
    		for(int i=0;i<list22.size();i++){
    			List _list = (List)list22.get(i);
    			custListFields += _list.get(1)+",";
    		}
    	}
        
		//生成列表头
        String[][] listHeadFields = null;
        //优先自定义模块查询设置
        if (po.getMenuListDisplayElements() != null &&
           po.getMenuListDisplayElements().length() > 0) {
        	listHeadFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(po.getMenuListDisplayElements(),
                                                                                CommonUtils.getSessionDomainId(request)+"");
        } else {                                                         //自定义数据库查询设置
        	listHeadFields = dbBD.getListField(po.getMenuListTableMap().toString());
        }
		String headerContainer = "";
		List showJSData = new ArrayList();
		if (listHeadFields != null) {
			headerContainer += "<tr class=\"listTableHead\">";
			headerContainer +="<td whir-options=\"field:'id',width:'2%',checkbox:true,renderer:showcheckbox\">"+
			                  "<input type=\"checkbox\" name=\"items\" id=\"items\" onclick=\"" +
					          "setCheckBoxState('id',this.checked);\" ></td>";
			for(int i=0;i<listHeadFields.length;i++){
				
				Object[] obj = new Object[8];
			    obj[0] = "show_"+listHeadFields[i][2];
			    obj[1] = listHeadFields[i][0];
			    obj[2] = listHeadFields[i][1];
			    obj[3] = listHeadFields[i][2];
			    obj[4] = listHeadFields[i][3];
			    obj[5] = listHeadFields[i][4];
			    obj[6] = listHeadFields[i][5];
			    obj[7] = listHeadFields[i][6];
			    
				//附件或可编辑类型前台获取展示
				if(((obj[3]+"").equals(po.getMenuMaintenanceSubTableName())||
						custListFields.indexOf(obj[3]+"") > -1)||
					((com.whir.component.util.Field.FIELD_UP_FILE+"").equals(obj[5]))){
					showJSData.add(obj);
					headerContainer += "<td whir-options=\"field:'"+listHeadFields[i][2]+"',"+
	                   "width:'"+listHeadFields[i][3]+"%',renderer:"+obj[0]+"\">"+
	                   listHeadFields[i][1]+"</td>";
				}else{
					headerContainer += "<td whir-options=\"field:'"+listHeadFields[i][2]+"',"+
	                   "width:'"+listHeadFields[i][3]+"%' \">"+
	                   listHeadFields[i][1]+
	                   "<img src=\""+rootPath+"/images/blanksort.gif\" onclick=\"orderBy(this,'"+listHeadFields[i][2]+"');\"></td>";
				
				}
				
				
			    
			}
			if(isRefFlow){
				headerContainer +="<td whir-options=\"field:'ban', width:'10%',renderer:showApp\">办理状态</td>";
			}
			headerContainer +="<td whir-options=\"field:'opt', width:'8%',renderer:showoperate\">操作</td>";
			headerContainer +="</tr>";
		}
		request.setAttribute("showJSData", showJSData);
		
		logger.debug("*****************headerContainer: "+headerContainer);
		request.setAttribute("headerContainer", headerContainer);
		
		request.setAttribute("customerMenuConfpo", po);
		request.setAttribute("tableName", tableName);
		
		List _list = new ManagerBD().getRightScope(CommonUtils.getSessionUserId(request)+"",
				                                   "99-" + menuId + "-03");
        String rightType = "";
        String defineOrgs = "";
        if(_list != null ){

            if(_list.size() > 0){
                Object[] obj = (Object[]) _list.get(0);
                rightType = obj[0] + "";
                defineOrgs = obj[1] + "";
                if(obj[3]!=null&&!"null".equals(obj[3]+"")){
                	defineOrgs += obj[3];
                }
                if(obj[4]!=null&&!"null".equals(obj[4]+"")){
                	defineOrgs += obj[4];
                }
            }
        }
        request.setAttribute("rightType", rightType);
        request.setAttribute("defineOrgs", defineOrgs);
        
        String addbutName = com.whir.i18n.Resource.getValue(local,"common","comm.add");
        String importbutName = com.whir.i18n.Resource.getValue(local,"common","comm.import");
        String exportbutName = com.whir.i18n.Resource.getValue(local,"common","comm.export");
        String delselectbutName = com.whir.i18n.Resource.getValue(local,"common","comm.delselect");
        ManagerBD mBD = new ManagerBD();
        
        boolean hasNewForm = false;
        if((po.getMenuSearchBound()+"").indexOf("new$")!=-1){
        	hasNewForm = true;
        }
        request.setAttribute("hasNewForm", hasNewForm);
        
        //生成按钮栏
        String middlButton = "&nbsp;";
        //数据新增权限
        if (hasCustmenuAuth) {
            if (mBD.hasRight(CommonUtils.getSessionUserId(request)+"", 
            		          "99-" + menuId + "-03")) {
                if (po.getMenuRefFlow() != null && 
                	po.getMenuRefFlow().length() > 0 &&
                    !"-1".equals(po.getMenuRefFlow())) {

                    String action = tokenFlowAction(po.getMenuRefFlow(), po.getMenuOpenStyle());
                    middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                             " onclick=\"goFlow('"+action+"')\" value=\""+addbutName+"\" />";
                    middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                             " onclick=\"importData()\" value=\""+importbutName+"\" />";
                }else{
                	if(hasNewForm){
                	   middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                             " onclick=\"addnewform()\" value=\""+addbutName+"\" />";
                	}else{
                	   middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                             " onclick=\"add()\" value=\""+addbutName+"\" />";
                	}
                    middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                             " onclick=\"importData()\" value=\""+importbutName+"\" />";
                }
                middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                               " onclick=\"importMod()\" value=\"下载模板\" />";
            }
        }
        CustomerMenuDB bd = new CustomerMenuDB();
        //获取自定义按钮
        List list = bd.getCustButtons(po.getId()+"");
        //获取列表保存字段
        List list2 = bd.getFieldControls(po.getId()+"","4");
        //可编辑字段
        if(list2 != null && list2.size()>0){
            middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
            " onclick=\"saveAllFields()\" value=\"批量保存\" />";
        }
        
        if(list!=null && list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                CustomerMenuButtonPO buttonPO = (CustomerMenuButtonPO)list.get(i);
                if("0".equals(buttonPO.getActtype())){

                      String css = getButtonCss(buttonPO.getActname());
                      //不设置查看权限默认都能看到
                      if(buttonPO.getViewscopes()!=null &&
                         !"null".equals(buttonPO.getViewscopes()) &&
                         !"".equals(buttonPO.getViewscopes())){

                        ConversionString conversionString = new ConversionString(buttonPO.getViewscopes());
                        String userString = conversionString.getUserString();
                        String orgString = conversionString.getOrgString();
                        String groupString = conversionString.getGroupString();

                        if (bd.checkCustmenuButtonAuth(request,userString,orgString,groupString)) {
                          
                        	middlButton += "&nbsp;<input type=\"button\" class=\""+css+"\" "+
                                           " onclick=\"goBatch('" +
                                           buttonPO.getLinkurl() + "','')\" value=\""+
                                           buttonPO.getActname()+"\" />";
                         }
                      }else{
                    	  middlButton += "&nbsp;<input type=\"button\" class=\""+css+"\" "+
                                          " onclick=\"goBatch('" +
                                          buttonPO.getLinkurl() + "','')\" value=\""+
                                          buttonPO.getActname()+"\" />";
                      }
                }
            }
         }
         
         //数据维护权限  批量删除
         if (hasCustmenuAuth) {
             if (mBD.hasRight(CommonUtils.getSessionUserId(request)+"",
            		          "99-" + menuId + "-03")) {
            	 middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                                " onclick=\"delBatch()\" value=\""+delselectbutName+"\" />";
             }
         }
          String isHasExport="false";//判断是否有导出权限  2016/04/06
         //数据导出权限 2.0版本新增
         if (hasCustmenuAuth) {
             if (mBD.hasRight(CommonUtils.getSessionUserId(request)+"",
            		          "99-" + menuId + "-04")) {
            	 middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                                " onclick=\"exportDataById()\" value=\"选中导出\" />";
            	 middlButton += "&nbsp;<input type=\"button\" class=\"btnButton4font\" "+
                                " onclick=\"exportData()\" value=\""+exportbutName+"\" />";
            	 isHasExport="true";//判断是否有导出权限
             }
         }
        request.setAttribute("isHasExport", isHasExport);
        request.setAttribute("middlButton", middlButton);
    	
        //11.5需求  添加设置字段联动  2016/04/14 start        
        request.setAttribute("trigSettingId", trigSettingId);
        //11.5需求  添加设置字段联动  2016/04/14 end
    	return "custMenuRight";
    }
    
    /**
     * 获取列表每个字段显示内容
     * @return
     */
    private String getShowHTMLIN(String dataId,
                              String dataValue,
						      String fieldid,
						      String fielddesname,
						      String fieldname,
						      String fieldwidth,
						      String fieldshow,
						      String fieldvalue,
						      String fieldtype,
						      String isLink,
						      String canModify,
						      String thisrow) {
		String json = dataValue;
		String value = (dataValue != null &&
		          dataValue.length() > 0 &&
		          dataValue.indexOf(";") > 0)?
		        		  dataValue.substring(0,dataValue.indexOf(";")):dataValue;
		value = (value==null||"null".equals(value))?"":value;
		if("1000001".equals(fieldtype)){
			value = new CustomerMenuDB().getFloatString(value);
		}
        
		if ( ((com.whir.component.util.Field.FIELD_RADIO+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_CHECKBOX+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_SELECT+"").equals(fieldshow)||
                (com.whir.component.util.Field.FIELD_RELATION_SAVE+"").equals(fieldshow))
               && dataValue != null
               && dataValue.length() > 0) { //类型RADIO, CHECKBOX SELECT
			
			 String linkurl = new CustomFormBD().getFieldShowValue(fieldname,
                                                                    fieldshow,
                                                                    dataValue,
                                                                    fieldid) + "&nbsp;";
              if ("0".equals(isLink)) {
            	  json = linkurl;
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+linkurl+"</a>";
              }
          } else if (
                  ((com.whir.component.util.Field.FIELD_MORE_SELECTPERSON+"").equals(fieldshow) ||
                   (com.whir.component.util.Field.FIELD_SIMPLE_ORG+"").equals(fieldshow) ||
                   (com.whir.component.util.Field.FIELD_MORE_ORG+"").equals(fieldshow) )
                  && dataValue != null
                  && dataValue.length() > 0) {                                //多选人\单选组织\多选组织
        	  
        	  if ("0".equals(isLink)) {
            	  json = value;
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value+"</a>";
              }
          } else if ( (com.whir.component.util.Field.FIELD_PASSWORD+"").equals(fieldshow)&& 
        		  dataValue!= null && 
        		  dataValue.length() > 0) {                         //密码
              json = "******";
          } else if ( (com.whir.component.util.Field.FIELD_UP_FILE+"").equals(fieldshow) &&
        		        dataValue != null &&
        		        dataValue.length() > 0) {                   //附件
 
              String tempStr = ((dataValue!= null&&
            		             dataValue.length() > 0 && 
            		             dataValue.indexOf(";") > 0) ? dataValue.split(";")[1].replaceAll(",,",""):"");
              String fileName = ((dataValue != null && 
            		              dataValue.length() > 0 && 
            		              dataValue.indexOf(";") > 0) ? dataValue.split(";")[0].replaceAll(",,",""):"");
              String path = "customform";
              while(tempStr.length()>0 && tempStr.endsWith(",")){
                  tempStr = tempStr.substring(0,tempStr.length()-1);
              }
              String enTempStr = "";
              try{
                 enTempStr = java.net.URLEncoder.encode(tempStr,"UTF-8");
              }catch(Exception e){
            	 e.printStackTrace();
              }
              
              com.whir.component.security.crypto.EncryptUtil util = 
			      new com.whir.component.security.crypto.EncryptUtil();
		
		      String dlcode = util.getSysEncoderKeyVlaue("FileName",fileName,"dir");
		
              String fileServer = com.whir.component.config.ConfigReader.getFileServer(request.getRemoteAddr());
              String tempLink = " '"+fileServer+"/public/download/download.jsp?verifyCode="+
				     dlcode+"&FileName="+
                     fileName+"&name="+enTempStr+"&path="+path+"',isFull:'true',winName: 'opencust" + dataId+ "' ";
              if ("0".equals(isLink)) {
            	  String[] tempStr0s =tempStr.split(",");
                  String[] fileName0s = fileName.split(",");
                  json = "";
            	  for(int k=0;k<tempStr0s.length;k++){
            		  try{
                          enTempStr = java.net.URLEncoder.encode(tempStr0s[k],"UTF-8");
                       }catch(Exception e){
                     	 e.printStackTrace();
                       }
                      dlcode = util.getSysEncoderKeyVlaue("FileName",fileName0s[k],"dir");
                       
                      tempLink = fileServer+"/public/download/download.jsp?verifyCode="+dlcode+"&FileName="+
                                 fileName0s[k]+"&name="+enTempStr+"&path="+path;
                      json += "<a href='"+tempLink+"' target=\"downloadIframe\" >" + tempStr0s[k]+"</a>,&nbsp;";
                  }
              } else if ("1".equals(isLink)){
            	  json ="<a href=\"javascript:void(0)\" "+
            	        " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+tempStr+"</a>";
              }

          } else if ( (com.whir.component.util.Field.FIELD_AUTONUM+"").equals(fieldshow) &&
        		       dataValue!= null &&
        		       dataValue.length() > 0) {                                           //多行文本
            		        		  
    		  if ("0".equals(isLink)) {
    			  /***
                  if (value.length() > 10)
                	  json = value.substring(0, 10) + "......";
                  else
                	  json = value + "&nbsp;";
                	  **/
    			  json = value + "&nbsp;";
              } else if ("1".equals(isLink)){
            	  String value0 = "";
            	  /**
                  if (value.length() > 10)
                      value0 = value.substring(0, 10) + "......";
                  else
                      value0 = value + "&nbsp;";
                      **/
            	  value0 = value + "&nbsp;";
                  
                  json ="<a href=\"javascript:void(0)\" "+
      	                " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value0+"</a>";
              }

          } else if ( (com.whir.component.util.Field.FIELD_TEXT + "").equals(fieldshow)) { //单行文本

             if("1".equals(canModify)) {                           //可编辑

                  //获取列表编辑字段属性
                  List _list3 = new CustomerMenuDB().getFieldInfoById(fieldid);
                  String _fieldLen = "0";
                  if (_list3 != null) {
                      _fieldLen = _list3.get(6) + "";
                  }
                  if ("1000002".equals(fieldtype)) {
                	  json = "<input type=\"text\" name=\"" +fieldname+"\" id=\"" +fieldname +"\" "+
                	         " value=\"" + value + "\" size=\"10\" class=\"inputText\" "+
                	         " whir-options=\"vtype:[{'maxLength':"+_fieldLen+"}]\">";
                  } else {
                	  json = "<input type=\"text\" name=\"" +fieldname+"\" id=\"" +fieldname +"\" "+
         	                 " value=\"" + value + "\" size=\"10\" class=\"inputText\" "+
         	                 " whir-options=\"vtype:['notempty',{'maxLength':"+_fieldLen+"},'digit']\">";
                  }
              }else if ("1".equals(isLink)){
                  json ="<a href=\"javascript:void(0)\" "+
      	                " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value+"</a>";
              }else{
            	  json =value;
              }

         } else if ( (com.whir.component.util.Field.FIELD_SIMPLE_SELECTPERSON+"").equals(fieldshow)) { //单选人(全部)
        	 
            String simpleName = "";
            String simpleId = "";
            if(dataValue != null &&
            	dataValue.length() > 0 &&
            	dataValue.indexOf(";") > 0){
                simpleName = dataValue.substring(0,dataValue.indexOf(";"));
                simpleId = dataValue.substring(dataValue.indexOf(";")+1,dataValue.length());
            }
            String rootPath = com.whir.component.config.PropertiesUtil.getInstance().getRootPath();
            if ("1".equals(canModify)) {                           //可编辑
            	
            	json = "<input type=\"text\" name=\""+fieldname+"_Name"+thisrow+"\" id=\"" +fieldname+"_Name"+thisrow+"\" "+
   	                   " value=\"" + simpleName + "\" style=\"width:90%\" class=\"inputText\"  readonly>"+
   	                   "<a href=\"javascript:void(0);\" class=\"selectIco\" onClick=\"openSelect({allowId:'"+fieldname+"_Id"+thisrow+"', allowName:'"+fieldname+"_Name"+thisrow+"', "+
   	                   " select:'user', single:'yes', show:'user', range:'*0*',limited:'1'});\"></a>"+
   	                   "<input type='hidden' id='"+fieldname+"_Id"+thisrow+"' name='"+fieldname+"_Id"+thisrow+"' value='"+simpleId+"'>";
   	                   
            }else if ("1".equals(isLink)){
                json ="<a href=\"javascript:void(0)\" "+
    	                " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+simpleName+"</a>";
            }else{
            	json =simpleName;
            }

        } else if ( (com.whir.component.util.Field.FIELD_DATE + "").equals(fieldshow)) { //日期
        	if ("1".equals(canModify)){                           //可编辑
        		
        		json ="<input type=\"text\" id=\""+fieldname+thisrow+"\" name=\""+fieldname+thisrow+"\" "+
        		      " class=\"Wdate whir_datebox\" onclick=\"WdatePicker({el:'"+fieldname+thisrow+"'})\" value=\""+value+"\"/>";
        	}else if ("1".equals(isLink)){
        		json ="<a href=\"javascript:void(0)\" "+
                      " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value+"</a>";
            }else{
            	json = value;
            }

        } else if ( (com.whir.component.util.Field.FIELD_DATETIME +"").equals(fieldshow)) { //日期时间

        	if ("1".equals(canModify)){                           //可编辑

        		json ="<input type=\"text\" id=\""+fieldname+thisrow+"\" name=\""+fieldname+thisrow+"\" "+
  		              " class=\"Wdate whir_datetimebox\" onclick=\"WdatePicker({el:'"+fieldname+thisrow+"',dateFmt:'yyyy-MM-dd HH:mm'})\" value=\""+value+"\"/>";
        	}else if ("1".equals(isLink)){
        		
        		json ="<a href=\"javascript:void(0)\" "+
                      " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value+"</a>";
        	}else{
            	 json = value;
            }
         } else {
        	 
        	 if("1".equals(isLink)){
        		 json ="<a href=\"javascript:void(0)\" "+
                        " onclick=\"view('"+dataId+"');\" class=\"xinzeng_29\">"+value+"</a>";
             }else{
            	 json = value;
              }
          }

//		printJsonResult(json);
		return json;
	}
    /**
     * 前台获取列表每个字段显示内容
     * @return
     */
    public String getShowHTML() {

		 //String dataId = request.getParameter("dataId")==null?"":com.whir.component.security.crypto.EncryptUtil.htmlcode(request.getParameter("dataId"));//安全性
		 //String dataValue = request.getParameter("dataValue")==null?"":com.whir.component.security.crypto.EncryptUtil.htmlcode(request.getParameter("dataValue"));//安全性
    	 String dataId = request.getParameter("dataId");
    	 String dataValue = request.getParameter("dataValue");
    	 String fieldid = request.getParameter("fieldid");
		 String fielddesname = request.getParameter("fielddesname");
		 String fieldname = request.getParameter("fieldname");
		 String fieldwidth = request.getParameter("fieldwidth");
		 String fieldshow = request.getParameter("fieldshow");
		 String fieldvalue = request.getParameter("fieldvalue");
		 String fieldtype = request.getParameter("fieldtype");
		 String isLink = request.getParameter("isLink");
		 String canModify = request.getParameter("canModify");
		 String thisrow = request.getParameter("thisrow");

		String json = getShowHTMLIN(dataId,dataValue,fieldid,fielddesname,fieldname,fieldwidth,fieldshow,fieldvalue,fieldtype,isLink,canModify,thisrow);
		printJsonResult(json);
		return null;
	}
    /**
     * 删除列表数据
     * @return
     * @throws Exception
     */
    public String delBatchData() throws Exception { 
		boolean result = false;
//		System.out.println("-------------------delBatchData----------------------");
		String menuName = request.getParameter("menuName");
		String tableName = request.getParameter("tableName");
		String infoIds = request.getParameter("infoIds");
//		System.out.println("tableName:"+tableName);
//		System.out.println("infoIds:"+infoIds);
		
        String[] delFiles = new CustomerMenuDB().getFiles(tableName, infoIds);
        if (new CustomerMenuDB().deleteBizDatas(tableName, infoIds)) {
            //删除物理文件
            if(delFiles !=null){
                  com.whir.common.util.DeleteFile deluitl = new com.whir.common.util.DeleteFile();
                  deluitl.setRequest(request);
                  deluitl.deleteFiles("customform", 
                		              delFiles,
                		              CommonUtils.getSessionDomainId(request)+"");
            }
            //记录日志
            com.whir.ezoffice.security.log.bd.LogBD logBD = new com.
                    whir.ezoffice.security.log.bd.LogBD();
            java.util.Date startDate = new java.util.Date();
            String moduleCode = "oa_customize_data"; //模块编码
            logBD.log(CommonUtils.getSessionUserId(request)+"", 
            		  CommonUtils.getSessionUserName(request)+"", 
            		  CommonUtils.getSessionOrgName(request)+"", 
            		  moduleCode, 
            		  "",
                      startDate, 
                      startDate, 
                      "3", 
                      menuName,
                      request.getRemoteAddr(),
                      CommonUtils.getSessionDomainId(request)+"");

        }
		
        printResult("success");  
            
        return null;  
	}
    /**
     * 获取列表数据
     * @return
     * @throws Exception
     */
    public String getCustDataList() throws Exception {
    	
    	logger.debug("------------------------getCustDataList-------------------------");
    	
    	if("1".equals(request.getParameter("saveAllFieldsFlag"))){
    		new CustomerMenuDB().saveListFields(request);
    	}
    	
    	int pageSize = com.whir.common.util.CommonUtils.getUserPageSize(request);
		int currentPage = 0;
        if (request.getParameter("startPage") != null) {
        	currentPage = Integer.parseInt(request.getParameter("startPage"));
        }
        /*
		 * orderByFieldName，用来排序的字段
		 */
		String orderByFieldName = request.getParameter("orderByFieldName")!=null?request.getParameter("orderByFieldName"):"";
		/*
		 * orderByType，排序的类型
		 */
		String orderByType = request.getParameter("orderByType")!=null?request.getParameter("orderByType"):"";
		
        String menuId=request.getParameter("menuId");
        String tableName=request.getParameter("tableName");
        String isRefFlow=request.getParameter("isRefFlow");
        String isNewRefFlow=request.getParameter("isNewRefFlow");
        String formId=request.getParameter("formId");
        String rightType = request.getParameter("rightType");
        String defineOrgs = request.getParameter("defineOrgs");
        
        CustomerMenuConfigerPO po = new CustMenuWithOriginalBD().
                                    loadMenuSetById(menuId,CommonUtils.getSessionDomainId(request)+"");
        CustomerMenuDB cDB = new CustomerMenuDB();
        String selectPara = cDB.getSelectPara(po,request);
        String fromPara = " "+tableName+" "; 
        String wherePara = cDB.getWherePara(po,request);
        String orderByPara = " " + cDB.getOrderByPara(po,request);

        if(orderByFieldName!=null&&!"".equals(orderByFieldName)){
    	   //orderByPara = " order by "+tableName+"."+orderByFieldName+" "+orderByType;
        	orderByPara = " order by "+orderByFieldName+" "+orderByType;
        	
        	//如果页面传的排序参数类似的话，会造成分页的时候数据交叉显示，2016-09-06 11.5补丁 start	
        	orderByPara=orderByPara+","+tableName+"_id desc ";
			//如果页面传的排序参数类似的话，会造成分页的时候数据交叉显示，2016-09-06 11.5补丁 end
        }
        //自定义数据表中设置A字段列表中不显示，自定义模块中设置A字段为排序字段，此自定义模块中不显示数据，后台报错。
        String sortField = "";
        if(!CommonUtils.isEmpty(orderByPara)){
            try{
                String temp_ = orderByPara.trim();
                temp_ = temp_.replaceAll("  ", " ");
                temp_ = temp_.substring(temp_.toLowerCase().indexOf("order by ") + 9);
                temp_ = temp_.trim();
                sortField = temp_.split(" ")[0];
            }catch(Exception e){
                e.printStackTrace();
            }
            if(!CommonUtils.isEmpty(sortField)){
                if(selectPara.toLowerCase().indexOf(sortField.toLowerCase()) == -1){
                    selectPara += "," + sortField;
                }
            }
        }
        
        if("true".equals(isRefFlow)){
        	if("true".equals(isNewRefFlow)){
        		logger.debug("-------------------isNewRefFlow----------------------"+isNewRefFlow); 
        		formId = formId.replaceAll("new\\$","");
        		selectPara += ",wfwork.workcurstep,wfwork.workstatus";
            	fromPara += " left join (select hip.whir_dealing_activity workcurstep,hip.whir_status workstatus,hip.business_key_ keyid "+
            	   " from ez_flow_hi_procinst hip,ez_form  f "+
            	   " where  f.form_code=hip.whir_formkey and f.form_id="+formId+") wfwork on "+tableName+"."+tableName+"_id=wfwork.keyid ";
            	wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
        	}else{
        	  logger.debug("-------------------tableName----------------------"+tableName);
        	  selectPara += ",workcurstep,workstatus";
        	  fromPara += " left join wf_work wfwork on "+tableName+"."+tableName+"_id=wfwork.workrecord_id ";
        	  wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
        	}
        }
          
          logger.debug("-----------------------------------------------------------");           
          logger.debug("-------------------selectPara----------------------"+selectPara);
          logger.debug("-------------------fromPara----------------------"+fromPara);
          logger.debug("-------------------wherePara----------------------"+wherePara);
          logger.debug("-------------------orderByPara----------------------"+orderByPara);
        String tableId = request.getParameter("tableId");
        String totFileds = cDB.getTotleFields(tableId,
                                            request,
                                            fromPara,
                                            wherePara);
        
        logger.debug("-------------------totFileds----------------------"+totFileds);
        Page page = PageFactory.getJdbcPage(selectPara, fromPara,wherePara,orderByPara);
        page.setPageSize(pageSize);
        page.setCurrentPage(currentPage);
        Map varMap = new HashMap();
        page.setVarMap(varMap);
        /*
         * list，数据结果
         */
        List list = page.getResultList();
        
        int pageCount = page.getPageCount();
        int recordCount = page.getRecordCount();
        
        //---------设置每个字段属性
        String[][] listHeadFields = null;
        CustomDatabaseBD dbBD = new CustomDatabaseBD();
        if (po.getMenuListDisplayElements() != null &&
            po.getMenuListDisplayElements().length() > 0) {
         	listHeadFields = new CustMenuWithOriginalBD().getQueryShowFieldsByCase(po.getMenuListDisplayElements(),
                                                                                 CommonUtils.getSessionDomainId(request)+"");
         } else {                                                         //自定义数据库查询设置
         	listHeadFields = dbBD.getListField(po.getMenuListTableMap().toString());
         }
		Map showJSData = null;
		if (listHeadFields != null) {
			showJSData = new HashMap();
			for(int i=0;i<listHeadFields.length;i++){
				
				Object[] obj = new Object[8];
			    obj[0] = "show_"+listHeadFields[i][2];
			    obj[1] = listHeadFields[i][0];
			    obj[2] = listHeadFields[i][1];
			    obj[3] = listHeadFields[i][2];
			    obj[4] = listHeadFields[i][3];
			    obj[5] = listHeadFields[i][4];
			    obj[6] = listHeadFields[i][5];
			    obj[7] = listHeadFields[i][6];
			    showJSData.put(obj[3], obj);
			}
		}
		//-----------设置每个字段属性
		
        
        /*
         * 异步刷新页面，不返回任何页面，将数据结果封装成json格式返回
         */
        JacksonUtil util = new JacksonUtil();
        String[] fields = null;
        String[] fields_new = null;
        if(selectPara!=null){
        	fields = selectPara.split(",");
        	fields[0]="id";
        	//有流程
        	if("true".equals(isRefFlow)){
        		fields[fields.length-2]="workcurstep";
        		fields[fields.length-1]="workstatus";
        	}
        	//判断数据权限
            fields_new = new String[fields.length+1];
            for(int i=0;i<fields.length;i++){
            	fields_new[i]=fields[i];
            }
            fields_new[fields.length]="hasUpdAndDelRight";//给查询的字段取名为hasUpdAndDelRight 在前台判断checkbox是否显示权限
        }
        //获取列表保存字段
    	List list2 = new CustomerMenuDB().getFieldControls(menuId,"4");
    	//可编辑字段
    	String custListFields ="";
    	if(list2 != null && list2.size()>0){
    		for(int i=0;i<list2.size();i++){
    			List _list = (List)list2.get(i);
    			custListFields += _list.get(1)+",";
    		}
    	}
        List list_new = new ArrayList();
        if(list!=null){
			for(int i=0;i<list.size();i++){
				Object[] obj = (Object[])list.get(i);
				Object[] obj1 = new Object[fields.length+1];
				//本条数据id
				obj1[0] = obj[0];
				
				//本条数据其他显示值设定
				for(int j=1;j<fields.length;j++){
					//根据字段名称取字段属性
					Object[] obj_field = (Object[])showJSData.get(fields[j]);
					if(obj_field==null){
						obj1[j] =obj[j];
						continue;
					}
					
					String _fieldid = obj_field[1]+"";
					String _fielddesName = obj_field[2]+"";
					String _fieldname = obj_field[3]+"";
					String _fieldwidth = obj_field[4]+"";
					String _fieldshow = obj_field[5]+"";
					String _fieldvalue = obj_field[6]+"";
					String _fieldtype = obj_field[7]+"";
					String isLink = "0";
					if(_fieldname.equals(po.getMenuMaintenanceSubTableName())){
					    isLink = "1";
					}
					String canModify="0";
					if (custListFields.indexOf(_fieldname) > -1) {
						canModify = "1";
					}
					//可编辑或者链接字段不获取显示
					if(isLink.equals("1")||
					   canModify.equals("1")||
					   (com.whir.component.util.Field.FIELD_UP_FILE+"").equals(_fieldshow)){
					   obj1[j] =obj[j];
					}else{
					   obj1[j] = getShowHTMLIN(obj[0]+"",
							              obj[j]+"",
							              _fieldid,
							              _fielddesName,
							              _fieldname,
							              _fieldwidth,
							              _fieldshow,
							              _fieldvalue,
							              _fieldtype,
							              isLink,
							              canModify,
						                  i+"")+"";
					}
				}
				
				obj1[fields.length]=hasUpdAndDelRight(tableName,obj1[0]+"",rightType,defineOrgs);
				list_new.add(obj1);
			}
		}
         
		String json = util.writeArrayJSON(fields_new,list_new);
		json = "{pager:{pageCount:"+pageCount+",recordCount:"+recordCount+"},data:"+json+",sumInfo:'"+totFileds+"'}";
		printResult(G_SUCCESS,json);
		//logger.debug("查询列表结束");
		return null;
		
	}
    
    /**
     * 获取每行自定义按钮
     * @return
     * @throws Exception
     */
    public String getButtonList() throws Exception {
    	
    	String menuId=request.getParameter("menuId");
    	CustomerMenuDB bd = new CustomerMenuDB();
        //获取自定义按钮
        List list = bd.getCustButtons(menuId);
        String json = "[";
    	//获取自定义按钮
    	if (list != null && list.size() > 0) {
    		for (int k = 0; k < list.size(); k++) {
    			CustomerMenuButtonPO buttonPO = (CustomerMenuButtonPO) list.get(k);
    			if ("1".equals(buttonPO.getActtype())) {
    				//不设置查看权限默认都能看到
    				if(buttonPO.getViewscopes()!=null &&
    				   !"null".equals(buttonPO.getViewscopes()) &&
    				   !"".equals(buttonPO.getViewscopes())){

    					ConversionString conversionString = new ConversionString(buttonPO.getViewscopes());
    					String userString = conversionString.getUserString();
    					String orgString = conversionString.getOrgString();
    					String groupString = conversionString.getGroupString();
    					if (bd.checkCustmenuButtonAuth(request,
    							                       userString,
    							                       orgString,
    							                       groupString)) {
    						json += "{\"css\":\"" + getButtonCss(buttonPO.getActname()) + "\", \"linkurl\":\""
						     + buttonPO.getLinkurl() + "\", \"name\":\""
						     + buttonPO.getActname() + "\"},";
    					}
    				}else{
    					json += "{\"css\":\"" + getButtonCss(buttonPO.getActname()) + "\", \"linkurl\":\""
					     + buttonPO.getLinkurl() + "\", \"name\":\""
					     + buttonPO.getActname() + "\"},";
    				}
    			}
    		}
    	}   
    	json += "]";
//		System.out.println("------json:" + json);
		printJsonResult(json);

		return null;
	}
    /**
     * 导入数据
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
	public String importDataExcel() throws Exception {
    	logger.debug("-------------------importDataExcel---------------------");
    	
    	String tableName = request.getParameter("tableName");
    	String filename = request.getParameter("fileName");  
    	String formId = request.getParameter("formId");  
    	String hasNewForm = request.getParameter("hasNewForm");  
        String year_month = filename.substring(0,6);  
        String filePath = request.getRealPath("/upload/fileimport")+ "/" + year_month+"/"+filename;
        File file = new File(filePath); 
        logger.debug("tableName:"+tableName);
        logger.debug("formId:"+formId);
        logger.debug("hasNewForm:"+hasNewForm);
        int retProcC = new CustomizeDataImport().importDataProcessor(tableName,
        		                                                     filePath,
                                                                     CommonUtils.getSessionUserId(request)+"",
                                                                     CommonUtils.getSessionOrgId(request)+"",
                                                                     formId,
        /**判断是否导入成功，导入不成功提示导入失败   2016-01-27 start **/                                                           hasNewForm);
        if(retProcC!=-1){
        	printJsonResult("success");
        }else{
        	printJsonResult("导入失败！");
        }
        /**判断是否导入成功，导入不成功提示导入失败    2016-01-27 end **/ 
        //printJsonResult("success");
    	return null;
    }
    /**
     * 导出 导入数据模版
     * @return
     * @throws Exception
     */
    public String expDataExcelMod() throws Exception {
    	
		String tableId = request.getParameter("tableId");
		String formId = request.getParameter("formId");
		String hasNewForm = request.getParameter("hasNewForm");
		CustomizeDataImport customizeDataImport = new CustomizeDataImport();
		
		if("true".equals(hasNewForm)){
		   formId = formId.replaceAll("new\\$","");
		}
		String[][] listFields = customizeDataImport.getListField(formId,hasNewForm);
		 String title="导入数据模板";
		 List list=new ArrayList();
		 if (listFields != null) {
			 String[] obj = new String[listFields.length];
		   	 for (int i = 0, len=listFields.length; i < len; i++) {
		   		if("1000000".equals(listFields[i][6])){
		   			obj[i]="整型";
		        }else if("1000001".equals(listFields[i][6])){
		        	obj[i]="浮点型";
			    }else if("1000002".equals(listFields[i][6])){
			    	obj[i]="字符型";
			    }else if("1000003".equals(listFields[i][6])){
			    	obj[i]="文本型";
			    }
		   	 }
		   	list.add(obj);
		  }
		 
		 String[][] arr = null;
		 if(listFields!=null){
			arr = new String[listFields.length][3];
			for(int i=0, len=listFields.length;i<len;i++){
				arr[i][0]=listFields[i][1];
				arr[i][1]="String";
				arr[i][2]="";
			}
		 }

		StringBuffer result = new StringBuffer(64);
		com.whir.component.export.excel.ExcelExport test = new com.whir.component.export.excel.ExcelExport();
		result.append(test.installExcelTitleAndHeader(title, arr));// 返回xml类型字符串
		for (int i = 0, len=list.size(); i < len; i++) {
			// 循环每行数据，每行数据开始要加"<rows>"
		    result.append("<rows>");
			String[] obj = (String[]) list.get(i);
			for (int j = 0, alen=arr.length; j < alen; j++) {
				String val = obj[j];
				String type = arr[j][1];
				String outType = arr[j][2];
				result.append(test.installExcelColumn(val, type, outType));// 返回xml类型字符串
			}
			// 每行数据结束要加"</rows>"
			result.append("</rows>");
		}
		//test.dataToExcel(response, result.toString());
		test.dataToExcel(request,response, result.toString());

		return null;
	}
    
    /**
     * 获取是否有修改删除数据权限
     * @return 1: 有权限
     *         0：没有权限
     * @throws Exception
     */
    public String hasUpdAndDelRight() throws Exception{
    	
    	String tableName = request.getParameter("tableName");
    	String infoId = request.getParameter("infoId");
    	String rightType = request.getParameter("rightType");
    	String defineOrgs = request.getParameter("defineOrgs");
    	
    	boolean hasAuth = new CustomerMenuDB().
    	          hasUpdAndDelRight(request,
    	        		            tableName,
    	        		            infoId,
    	        		            rightType,
    	        		            defineOrgs, 
    	        		            CommonUtils.getSessionDomainId(request)+"");
    	String res = "0";
    	if(hasAuth){
    		res = "1";
    	}
        
    	printJsonResult(res);
		return null;
    }
    /**
     * 获取是否有修改删除数据权限
     * @param tableName
     * @param infoId
     * @param rightType
     * @param defineOrgs
     * @return 
     * 
     * @throws Exception
     */
    public boolean hasUpdAndDelRight(String tableName,
	                                String infoId,
	                                String rightType,
	                                String defineOrgs) throws Exception{
    	
    	boolean hasAuth = new CustomerMenuDB().
    	          hasUpdAndDelRight(request,
    	        		            tableName,
    	        		            infoId,
    	        		            rightType,
    	        		            defineOrgs, 
    	        		            CommonUtils.getSessionDomainId(request)+"");
		return hasAuth;
    }

    /**
     * 导出数据
     * @return
     * @throws Exception
     */
    public String expDataExcel() throws Exception {
    	logger.debug("-----------------------------expDataExcel---------------------------");
		String orderByFieldName = request.getParameter("orderByFieldName") != null ? 
				                request.getParameter("orderByFieldName"): "";

		String orderByType = request.getParameter("orderByType") != null ? 
				                request.getParameter("orderByType"): "";

		String menuId = request.getParameter("menuId");
		String tableName = request.getParameter("tableName");
		String isRefFlow = request.getParameter("isRefFlow");
		String isNewRefFlow=request.getParameter("isNewRefFlow");
		String formId=request.getParameter("formId");
		
		String infoIds = request.getParameter("infoIds");

		CustomerMenuConfigerPO po = new CustMenuWithOriginalBD().loadMenuSetById(menuId, 
				                                   CommonUtils.getSessionDomainId(request)+ "");
		CustomerMenuDB cDB = new CustomerMenuDB();
		List expSelectPara = cDB.getExpSelectPara(po, request);
		String selectPara="";
		if(expSelectPara!=null){
			for(int i=0, len=expSelectPara.size(); i<len; i++){
				selectPara += ((List) expSelectPara.get(i)).get(1)+",";
			}
			selectPara = escapeString(selectPara);
		}
		
		String fromPara = " " + tableName + " ";
		//String wherePara = cDB.getWherePara(po, request);
		String wherePara = cDB.getWhereParaExport(po, request);//(add by tianml 2015/11/16 用于设置导出的数据范围)
		String orderByPara = cDB.getOrderByPara(po, request);
		
		if (orderByFieldName != null && !"".equals(orderByFieldName)) {
			orderByPara = " order by " + tableName + "." + orderByFieldName+ " " + orderByType;
		}
		if ("true".equals(isRefFlow)) {
			if ("true".equals(isNewRefFlow)) {
				formId = formId.replaceAll("new\\$","");
				selectPara += ",wfwork.workcurstep,wfwork.workstatus";
            	fromPara += " left join (select hip.whir_dealing_activity workcurstep,hip.whir_status workstatus,hip.business_key_ keyid "+
            	   " from ez_flow_hi_procinst hip,ez_form  f "+
            	   " where  f.form_code=hip.whir_formkey and f.form_id="+formId+") wfwork on "+tableName+"."+tableName+"_id=wfwork.keyid ";
            	wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
			}else{
			  selectPara += ",workcurstep,workstatus";
			  fromPara += " left join wf_work wfwork on " + tableName + "."+ tableName + "_id=wfwork.workrecord_id ";
			  wherePara += " and (wfwork.workstatus=1 or wfwork.workstatus=100) ";
			}
		}

		if(infoIds!=null&&
			!"null".equals(infoIds)&&
			!"".equals(infoIds)){
			String[] infoIdss = infoIds.split(",");
			String _infoidSql=" and (1<>1 ";
			for(int i=0;i<infoIdss.length;i++){
				_infoidSql += " or "+tableName+"_id ="+infoIdss[i];
			}
			_infoidSql += ") ";
			wherePara += _infoidSql;
		}
		
		logger.debug("selectPara:" + selectPara);
		logger.debug("fromPara:" + fromPara);
		logger.debug("wherePara:" + wherePara);
		logger.debug("orderByPara:" + orderByPara);

		String title = po.getMenuName();
		List list = cDB.getExpDataList(expSelectPara, 
                                       selectPara,
                                       fromPara, 
                                       wherePara, 
                                       orderByPara);

		String[][] arr = null;
		if(expSelectPara!=null){
			arr = new String[expSelectPara.size()][3];
			for(int i=0,len=expSelectPara.size(); i<len; i++){
				List _list = (List) expSelectPara.get(i);
				arr[i][0] = _list.get(0)+"";
				arr[i][1] = "String";
				arr[i][2] = "";
			}
		}

		StringBuffer result = new StringBuffer(64);
		com.whir.component.export.excel.ExcelExport test = new com.whir.component.export.excel.ExcelExport();
		result.append(test.installExcelTitleAndHeader(title, arr));// 返回xml类型字符串
		for (int i = 0, len=list.size(); i < len; i++) {
			// 循环每行数据，每行数据开始要加"<rows>"
		    result.append("<rows>");
			String[] obj = (String[]) list.get(i);
			for (int j = 0, alen=arr.length; j < alen; j++) {
				String val = obj[j];
				String type = arr[j][1];
				String outType = arr[j][2];
				result.append(test.installExcelColumn(val, type, outType));// 返回xml类型字符串
			}
			// 每行数据结束要加"</rows>"
			result.append("</rows>");
        }
		logger.debug("result:"+result);
//		test.dataToExcel(response, result.toString());
		test.dataToExcel(request,response, result.toString());

		return null;
	}
    /**
	 * 获取工作流链接地址
	 * 
	 * @param flowData
	 * @param style
	 * @return
	 */
    private String tokenFlowAction(String flowData, int style) {
        
        String action = "";
        String[] flowDatas = flowData.split("\\$");
        if(flowDatas.length==2&&
        		"newFlow".equals(flowDatas[0])){
        	
        	String path =
                com.whir.component.config.PropertiesUtil.getInstance().getRootPath()
                +"/ezflowopen!startProcess.action?p_wf_processDefinitionKey="+flowDatas[1]
                +"&openType=startOpen";
            action = (style == 0) ? path :"openWin({url:"+path+",isFull:'true',winName: 'openflow' });";
        }else{
	        String[] values = getFlowTokenArray(flowData, "$");
	        if (values != null) {
	            String path =
	                    com.whir.component.config.PropertiesUtil.getInstance().getRootPath()
	                    +"/wfopenflow!startProcess.action?p_wf_processId="+values[0];
	            action = (style == 0) ? path :"openWin({url:"+path+",isFull:'true',winName: 'openflow' });";
	        }
        }
        return action;
    }
    private String[] getFlowTokenArray(String flowStr, String mark) {
        String[] values = new String[5];
        int m = 0;
        StringTokenizer tk = new StringTokenizer(flowStr, mark);
        while (tk.hasMoreTokens() && m<5) {
            values[m] = tk.nextToken();
            while(m==4 && tk.hasMoreTokens()) values[m] += mark + tk.nextToken();
            m++;
        }
        return values;
    }
    /*
     * 去尾部逗号
     */
    private String escapeString(String str) {
        if (!"".equals(str)) {
            while (str.endsWith(" ")) {
                str = str.substring(0, str.length() - 1);
            } while (str.endsWith(",")) {
                str = str.substring(0, str.length() - 1);
            }
        }
        return str;
    }
    /*
     * 设置按钮css
     */
    private String getButtonCss(String str) {
    	String css="btnButton4font";
    	if (str.length()<=2) {
            css = "btnButton4font";
        } else if (str.length()>2 && str.length()<=4) {
            css = "btnButton4font";
        } else if (str.length()>4 && str.length()<=6) {
            css = "btnButton6font";
        } else if (str.length()>6 && str.length()<=8) {
            css = "btnButton8font";
        } else if (str.length()>8) {
            css = "btnButton10font";
        }
        return css;
    }
    
    

}
