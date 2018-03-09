package com.whir.portal.basedata.bd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.whir.component.util.PropertiesUtils;


public class WeatherForecastBD {
	// action配置文件路径  
	private static final String ACTIONPATH = "ftppath.properties";  
//	private static Properties props;
	/**
	 * 
	 */
	private static final long serialVersionUID = -719868959247581033L;
	private static Logger logger = Logger.getLogger(WeatherForecastBD.class.getName());
	/**
	 * 获取天气、pm、预警信息
	 */
	public List<Map<String,Object>> getWeatherForecastInfo(){
		Properties properties = PropertiesUtils.getProperties(ACTIONPATH);
		String qxzxftp_tq = properties.getProperty("qxzxftp_tq");
		String qxzxftp_cac = properties.getProperty("qxzxftp_cac");
		String qxzxftp_pm = properties.getProperty("qxzxftp_pm");
		
	    String tqFileName = getTqFiles(qxzxftp_tq);
	    String cacFileName1 = getCacFiles(qxzxftp_cac);
	    String pmFileName = getPmFiles(qxzxftp_pm);
	    
	    String tqFilePath = qxzxftp_tq+"\\"+tqFileName;
	    String pmFilePath = qxzxftp_pm+"\\"+pmFileName;
	    List<Map<String,Object>> list = readTxtFile(tqFilePath,qxzxftp_cac,cacFileName1,pmFilePath,"pc");
	    return list;
	}
	
	public List<Map<String,Object>> getWeiXingInfo(){
		Properties properties = PropertiesUtils.getProperties(ACTIONPATH);
		String qxzxftp_tq = properties.getProperty("qxzxftp_tq");
		String qxzxftp_cac = properties.getProperty("qxzxftp_cac");
		String qxzxftp_pm = properties.getProperty("qxzxftp_pm");
		
	    String tqFileName = getTqFiles(qxzxftp_tq);
	    String cacFileName1 = getCacFiles(qxzxftp_cac);
	    String pmFileName = getPmFiles(qxzxftp_pm);
	    
	    String tqFilePath = qxzxftp_tq+"\\"+tqFileName;
	    String pmFilePath = qxzxftp_pm+"\\"+pmFileName;
	    List<Map<String,Object>> list = readTxtFile(tqFilePath,qxzxftp_cac,cacFileName1,pmFilePath,"wx");
	    return list;
	}
	/**
	 * 获取气象云图
	 */
	public List<String> getWeatherCloudPicture(){
		Properties properties = PropertiesUtils.getProperties(ACTIONPATH);
		String qxzxftp_gms = properties.getProperty("qxzxftp_gms");
		List<String> list = getFiles(qxzxftp_gms);
		Collections.reverse(list); // 倒序排列 
	    return list;
	}
	
	public List<String> getFiles(String filePath){
		List<String> list = new ArrayList<String>();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd");
		Date date1= new Date();
		String fileName = format1.format(date1);
		File root = new File(filePath);
	    File[] files = root.listFiles();
	    for(File file:files) {
	    	 boolean result = false;
		     if(!file.isDirectory()){
		    	 result = compare_date(fileName,file.getName().substring(5, 15));
		     }
		     if(result){
		    	 list.add(file.getName());
		     }
	    }
	    return list;
	}
	
	public boolean compare_date(String DATE1, String DATE2) {
		boolean result = false;
		long day=0;
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy_MM_dd");
        try {
        	Date dt1 = format1.parse(DATE1);
  		  	Date dt2 = format1.parse(DATE2);
  		    day=(dt1.getTime() - dt2.getTime())/(24*60*60*1000);
            if (day<2) {
            	result = true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
	
	
	public String getTqFiles(String filePath){
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer str = new StringBuffer();
		String fileName = "19000101000000";
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 if("YB".equals(file.getName().substring(0, 2))){
			    		 fileName  = compare_date(fileName,file.getName().substring(2, 16),format1);
			    	 }
			     }     
		    }
		    str.append("YB"+fileName+".txt");
		    System.out.println("getTqFiles:"+str.toString());
		    return str.toString();
	}
	
	public String getCacFiles(String filePath){
		  SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		  StringBuffer str = new StringBuffer();
		  String fileName = "19000101000000";
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 if("RVR".equals(file.getName().substring(0, 3))){
			    		 fileName  = compare_date(fileName,file.getName().substring(3, 17),format1);
			    	 }
			     }     
		    }
		    str.append("RVR"+fileName);
		    System.out.println("getCacFiles:"+str.toString());
		    return str.toString();
//		SimpleDateFormat format1 = new SimpleDateFormat("MMddHH");
//		StringBuffer str = new StringBuffer();
//		String fileName = "010100";
//		  File root = new File(filePath);
//		    File[] files = root.listFiles();
//		    for(File file:files){     
//			     if(!file.isDirectory()){
//			    	 fileName  = compare_date(fileName,file.getName().substring(3, 9),format1);
//			     }     
//		    }
//		    str.append("CAC"+fileName);
//		    System.out.println("getCacFiles:"+str.toString());
//		    return str.toString();
	}
	
	public String getPmFiles(String filePath){
		SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
		StringBuffer str = new StringBuffer();
		String fileName = "19000101000000";
		  File root = new File(filePath);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(!file.isDirectory()){
			    	 fileName  = compare_date(fileName,file.getName().substring(0, 14),format1);
			     }     
		    }
		    str.append(fileName+".dat");
		    System.out.println("getPmFiles:"+str.toString());
		    return str.toString();
	}
	/**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     * @param filePath

     */
    public List<Map<String, Object>> readTxtFile(String tqFilePath,String qxzxftp_cac,String cacFileName1,String pmFilePath,String source){
    	List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    	List<String> list1 = new ArrayList<String>();
    	Map<String, Object> map = new HashMap<String, Object>();
        try {
                File file=new File(tqFilePath);
                if(file.isFile() && file.exists()){ //判断文件是否存在
                    InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file));//考虑到编码格式
                    BufferedReader bufferedReader = new BufferedReader(read);
                    String lineTxt = null;
                    while((lineTxt = bufferedReader.readLine()) != null){
                    	list1.add(lineTxt);
                    }
                    read.close();
	        }else{
	            System.out.println("找不到指定的文件1");
	        }
        } catch (Exception e) {
            System.out.println("读取文件内容出错1");
            e.printStackTrace();
        }
        
        try {
        	File root = new File(qxzxftp_cac);
		    File[] files = root.listFiles();
		    for(File file:files){     
			     if(file.getName().contains(cacFileName1)){
			            if(file.isFile() && file.exists()){ //判断文件是否存在
			                InputStreamReader read = new InputStreamReader(
			                new FileInputStream(file));//考虑到编码格式
			                BufferedReader bufferedReader = new BufferedReader(read);
			                String lineTxt = null;
			                while((lineTxt = bufferedReader.readLine()) != null){
			                	String[] strArray = lineTxt.split(" ");
			                	map.put(strArray[0], strArray[1]);
//			                	if(lineTxt.contains("METAR ZSSS")){
//			                		String[] strArray = lineTxt.split(" ");
//			                		if(strArray.length>=5 && strArray[4].contains("v")){
//			                			map.put("METAR ZSSS", strArray[5]);
//			                		} else {
//			                			map.put("METAR ZSSS", strArray[4]);
//			                		}
//			                	}
//			                	if(lineTxt.contains("METAR ZSPD")){
//			                		String[] strArray = lineTxt.split(" ");
//			                		if(strArray.length>=5 && strArray[4].contains("v")){
//			                			map.put("METAR ZSPD", strArray[5]);
//			                		} else {
//			                			map.put("METAR ZSPD", strArray[4]);
//			                		}
//			                	}
			                }
			                read.close();
					    }else{
					        System.out.println("找不到指定的文件2");
					    }
			     }     
		    }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错2");
	        e.printStackTrace();
	    }
	    
	    try {
            File file=new File(pmFilePath);
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file));//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                	if(lineTxt.contains("a34050")){
                		String[] lineTxtArray = lineTxt.split(";");
                		if(lineTxtArray.length>2){
                			DecimalFormat df=new DecimalFormat("0.0");
                			Float pm = Float.parseFloat(lineTxtArray[2]);
                			int pmdouble = (int)(pm*1000.0f);
                			String state = "无污染";
                			String flag = "0";
        					if(0<pmdouble && pmdouble<50){
        						state = "优";
        						flag = "0";
        					} else if(50<pmdouble && pmdouble<100){
        						state = "良";
        						flag = "1";
        					} else if(100<pmdouble && pmdouble<150){
        						state = "轻度污染";
        						flag = "2";
        					} else if(150<pmdouble && pmdouble<200){
        						state = "中度污染";
        						flag = "3";
        					} else if(200<pmdouble && pmdouble<300){
        						state = "重度污染";
        						flag = "4";
        					} else if(300<pmdouble && pmdouble<500){
        						state = "严重污染";
        						flag = "5";
        					}
                			map.put("PM2.5", df.format(pm*1000.0f));
                			map.put("state", state);
                			map.put("flag", flag);
                		} else {
                			map.put("PM2.5", "无");
                			map.put("state", "无污染");
                			map.put("flag", "0");
                		}
                	}
                }
                read.close();
	        }else{
	            System.out.println("找不到指定的文件3");
	        }
	    } catch (Exception e) {
	        System.out.println("读取文件内容出错3");
	        e.printStackTrace();
	    }
    
        if(list1.size()==13){
        	Map<String, Object> map1 = new HashMap<String, Object>();
        	map1.put("0", "虹桥天气");
        	map1.put("1", list1.get(3).toString());
        	map1.put("2", list1.get(4).toString());
        	map1.put("3", list1.get(5).toString());
//        	map1.put("4", "能见度:"+map.get("METAR ZSSS"));
        	map1.put("4", "能见度:"+map.get("ZSSS"));
        	list.add(map1);
        	
        	Map<String, Object> map2 = new HashMap<String, Object>();
        	map2.put("0", "浦东天气");
        	map2.put("1", list1.get(7).toString());
        	map2.put("2", list1.get(8).toString());
        	map2.put("3", list1.get(9).toString());
        	map2.put("4", "能见度:"+map.get("METAR ZSPD"));
        	map2.put("4", "能见度:"+map.get("ZSPD"));
        	list.add(map2);
        	if("pc".equals(source)){
        		Map<String, Object> map3 = new HashMap<String, Object>();
            	map3.put("0", "上海飞行情报区未来24小时危险天气预报");
            	map3.put("1", list1.get(11).toString());
            	map3.put("2", list1.get(12).toString());
            	list.add(map3);
            	
            	Map<String, Object> map4 = new HashMap<String, Object>();
            	map4.put("1", map.get("PM2.5"));
            	map4.put("2", map.get("state"));
            	map4.put("3", map.get("flag"));
            	list.add(map4);
        	}
        } else {
        	System.out.println("读取文件内容数据不全");
        }
        return list;
    }
    
	public String compare_date(String DATE1, String DATE2,SimpleDateFormat format1) {
        String date = "";
        Date date1= new Date();
        try {
        	Date dt1 = format1.parse(DATE1);
  		  	Date dt2 = format1.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
            	date1 = dt1;
            } else if (dt1.getTime() < dt2.getTime()) {
            	date1 = dt2;
            } else {
            	date1 = dt2;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
		date = format1.format(date1); 
        return date;
    }
	
	/*public static void main(String[] args){
		getWeatherForecastInfo();
	}*/
}
