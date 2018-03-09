package com.whir.service.common;

import java.io.*;

import org.apache.log4j.Logger;
import org.jdom.*;
import org.jdom.input.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import com.whir.common.util.MD5;

/**
 * 调用API接口
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 9.4.0.0
 */
public class CallApi {

    private String SERVICE_PARSE_XML = "/ServiceParse.xml";
    private static Logger logger = Logger.getLogger(CallApi.class.getName());
    private static String vkey = null;
    public static final String DEFAULT_AUTH_KEY = "auth.key.whir2012";

    private static Map serviceMap = null;

    public CallApi() {
        initMap();
    }

    /**
     * 注册service parse服务
     */
    private void initMap() {
        if(serviceMap == null) {
            serviceMap = new HashMap(0);

            try {
                SAXBuilder sbuilder = new SAXBuilder();
                InputStream isparse = CallApi.class.getResourceAsStream(
                    SERVICE_PARSE_XML);

                Document parseDoc = sbuilder.build(isparse);
                Element rootService = parseDoc.getRootElement();
                Element vkeyElement = rootService.getChild("vkey");
                vkey = vkeyElement.getValue();

                List serviceList = rootService.getChildren("service");
                if(serviceList != null && serviceList.size() > 0) {
                    for(int i = 0; i < serviceList.size(); i++) {
                        Element serviceElement = (Element)serviceList.get(i);
                        Element classElement = serviceElement.getChild("class");
                        String className = classElement.getValue();

                        Element methodsElement = serviceElement.getChild(
                            "methods");
                        List methodList = methodsElement.getChildren("method");
                        if(methodList != null && methodList.size() > 0) {
                            for(int j = 0; j < methodList.size(); j++) {
                                Element methodElement = (Element)methodList.
                                    get(j);
                                String methodValue = methodElement.getValue();
                                serviceMap.put(methodValue, className);
                            }
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param input String 输入参数xml格式
     * @throws Exception
     * @return String 返回结果xml格式
     */
    public String getResult(String input) throws Exception {
        if(serviceMap == null)
            throw new Exception("Error: serviceMap can not is null");

        //System.out.println("input:\n"+input);

        SAXBuilder builder = new SAXBuilder();
        byte[] b = input.getBytes("utf-8");
        InputStream is = new ByteArrayInputStream(b);
        Document doc = builder.build(is);
        Element root = doc.getRootElement();

        //debug
        boolean isDebug = false;
        Element debugElement = root.getChild("debug");
        if(debugElement != null){
            if("1".equals(debugElement.getValue())){
                isDebug = true;
            }
        }

        if(isDebug)System.out.println("[input]:\n"+input);

        //取得安全认证标识key
        String key = root.getChild("key").getValue();
        //返回结果
        String result = "";
        if(checkValid(key) == false) {
            result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            result += "<output>";
            result += "<message>";
            result += "<result>-1</result>";
            result += "<description>非法安全认证标识</description>";
            result += "</message>";
            result += "</output>";
            return result;
            //throw new Exception("Error: key invalid");
        }

        String cmd = root.getChild("cmd").getValue();
        if(!"saveVideoMeeting".equals(cmd) && !"savEeverydayRunSituation".equals(cmd) 
        		&& !"saveRealTimeFlow".equals(cmd) && !"saveDutyInformation".equals(cmd) && !"setRefreshFlag".equals(cmd)){
        	//TransCenter 交换中心传递参数--不校验ServiceToken--xiehd 20160715
            String TransCenter="";
            Element TransCenterEle = root.getChild("TransCenter");
            if(TransCenterEle!=null){
            	TransCenter = TransCenterEle.getValue();
            }
            
            //ServiceToken启用开关
            String ServiceTokenUserFalg="1";
            com.whir.component.config.ConfigXMLReader reader = new com.whir.component.config.ConfigXMLReader();
            ServiceTokenUserFalg = reader.getAttribute("serviceKeyList","ServiceTokenUserFalg");
            
            //String cmd = "addUser";
            if("getServiceToken".equals(cmd)//获取ServiceToken接口不校验ServiceToken,
            		||"getCorpSetPO".equals(cmd)||"getCorpSetAppPO".equals(cmd)//获取微信企业号管理配置接口不校验ServiceToken,
            		||"TransCenter".equals(TransCenter)//交换中心数据接口不校验ServiceToken
            		||"getDogApplyInfo".equals(cmd)||"setDogWriteCount".equals(cmd)//公司环境加密狗接口跳过servicetoken验证
            		||"getWriteDogNetPassword".equals(cmd)||"getDogApplyInfoByClientName".equals(cmd)
            		||"setDogWriteCountAndMacId".equals(cmd)||"setWriteDogNetPassword".equals(cmd)||"0".equals(ServiceTokenUserFalg)
            		){
            	
            	logger.debug("-------getServiceToken--------");
            }else{
            	//开始校验token
            	Element tokenele = root.getChild("ServiceToken");
            	Element serviceKeyele = root.getChild("serviceKey");
            	Element userKeyele = root.getChild("userKey");
            	Element userKeyTypeele = root.getChild("userKeyType");
            	if(tokenele!=null&&serviceKeyele!=null&&userKeyele!=null&&userKeyTypeele!=null){
            		String token = root.getChild("ServiceToken").getValue();
                	String serviceKey = root.getChild("serviceKey").getValue();
                	String userKey = root.getChild("userKey").getValue();
                	String userKeyType = root.getChild("userKeyType").getValue();
                	Boolean tokenflag = TokenManager.getInstance().judgeToken(token, serviceKey, userKey, userKeyType);
                	 //返回结果
                    if(!tokenflag) {
                    	logger.debug("-------Token令牌验证错误--------");
                        result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                        result += "<output>";
                        result += "<message>";
                        result += "<result>0</result>";
                        result += "<description>TokenError</description>";
                        result += "</message>";
                        result += "</output>";
                        return result;
                        //throw new Exception("Error: key invalid");
                    }
            	}else{
            		logger.debug("-------必填项不能为空!--------");
            		 result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                     result += "<output>";
                     result += "<message>";
                     result += "<result>0</result>";
                     result += "<description>必填项不能为空</description>";
                     result += "</message>";
                     result += "</output>";
                     return result;
            	}
            	
            }
        }
        String className = (String)serviceMap.get(cmd);
        logger.debug("-------className!--------："+className);
        if(className != null && !"".equals(className.trim())) {
            try {
                Class clazz = Class.forName(className);

                Class[] classTypes = new Class[] {Document.class};
                Object[] params = new Object[] {doc};
                //通过构造函数实例化
                Object target = clazz.getConstructor(classTypes).newInstance(
                    params);

                Class[] methodTypes = new Class[] {String.class};
                Object[] methodParams = new Object[] {cmd};
                Method method = clazz.getMethod("parse", methodTypes);
                //调用parse方法
                result = (String)method.invoke(target, methodParams);

            } catch(ClassNotFoundException e) {
                e.printStackTrace();
            } catch(NoSuchMethodException ns) {
                ns.printStackTrace();
                result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                result += "<output>";
                result += "<message>";
                result += "<result>-1</result>";
                result += "<description>未找到对应的方法</description>";
                result += "</message>";
                result += "</output>";
            } catch(SecurityException se) {
                se.printStackTrace();
            } catch(IllegalAccessException ie) {
                ie.printStackTrace();
            } catch(IllegalArgumentException iae) {
                iae.printStackTrace();
                result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                result += "<output>";
                result += "<message>";
                result += "<result>-1</result>";
                result += "<description>参数不合法</description>";
                result += "</message>";
                result += "</output>";
            } catch(InvocationTargetException ite) {
                ite.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(isDebug)System.out.println("[output]:\n"+result);

        return result;
    }

    /**
     * 验证key是否合法
     * @param key String
     * @return boolean
     */
    private boolean checkValid(String key) {
        /*
        if(DEFAULT_AUTH_KEY.equals(key)){
            return true;
        }*/
    	
        if(vkey == null || "".equals(vkey)){
            return false;
        }
        //System.out.println("=============vkey:"+vkey);
        logger.debug("=============key:"+key);
        if(key.indexOf("#!#@!@")>-1){//交换中心 服务端配置多个客户端authKey值时用#!#@!@隔开，并且不加密
    		String[] keyArr = key.split("#!#@!@");
    		for(int i=0;i<keyArr.length;i++){
    			if(vkey.equals(keyArr[i])){
    				logger.debug("=============key:合法"+keyArr[i]);
    				 return true;
    			}
    		}
    	}
        
        //do something...
        String sKey = new MD5().toMD5(vkey);
        if(sKey.equalsIgnoreCase(key)){
            return true;
        }

        return false;
    }
}
