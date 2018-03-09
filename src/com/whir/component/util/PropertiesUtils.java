package com.whir.component.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class PropertiesUtils {
	// action配置文件路径  
	private static final String ACTIONPATH = "wsdl.properties";  
	
	/**
	 * 增加属性文件值
	 * 
	 * @param key
	 * @param value
	 * @throws URISyntaxException 
	 */
	public static void addProperties(String key[], String value[]) throws URISyntaxException {
		Properties iniFile = getProperties();
		FileOutputStream oFile = null;
		String path = PropertiesUtils.class.getClassLoader().getResource("").toURI().getPath().substring(1); 
		File file = new File(path + ACTIONPATH);
		try {
			iniFile.put(key, value);
			oFile = new FileOutputStream(file, true);
			iniFile.store(oFile, "modify properties file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oFile != null) {
					oFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取配置文件
	 * 
	 * @return
	 */
	public static Properties getProperties() {
		Properties props=null;
		if (props == null) {
	        props = new Properties();
	        try {
	        	String path = PropertiesUtils.class.getClassLoader().getResource("").toURI().getPath().substring(1); 
	        	// 把文件读入文件输入流，存入内存中  
	        	FileInputStream fis = new FileInputStream(new File(path + ACTIONPATH));     
	        	//加载文件流的属性     
	        	props.load(fis); 
	        }
	        catch (Exception e) {
	          e.printStackTrace();
	        }
	    }
		return props;
	}
	/**
	 * 读取配置文件
	 * 
	 * @return
	 */
	public static Properties getProperties(String actionPath) {
		Properties props=null;
		if (props == null) {
	        props = new Properties();
	        try {
	        	String path = PropertiesUtils.class.getClassLoader().getResource("").toURI().getPath().substring(1);
	        	// 把文件读入文件输入流，存入内存中  
	        	FileInputStream fis = new FileInputStream(new File(path + actionPath));     
	        	//加载文件流的属性     
	        	props.load(fis); 
	        }
	        catch (Exception e) {
	          e.printStackTrace();
	        }
	    }
		return props;
	}
	/**
	 * 保存属性到文件中
	 * 
	 * @param pro
	 * @param file
	 * @throws URISyntaxException 
	 */
	public static void saveProperties(Properties pro) throws URISyntaxException {
		if (pro == null) {
			return;
		}
		String path = PropertiesUtils.class.getClassLoader().getResource("").toURI().getPath().substring(1); 
		File file = new File(path + ACTIONPATH);
		FileOutputStream oFile = null;
		try {
			oFile = new FileOutputStream(file, false);
			pro.store(oFile, "modify properties file");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oFile != null) {
					oFile.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 修改属性文件
	 * 
	 * @param key
	 * @param value
	 * @throws URISyntaxException 
	 */
	public static void updateProperties(String key, String value) throws URISyntaxException {
		// key为空则返回
		if (key == null || "".equalsIgnoreCase(key)) {
			return;
		}
		Properties pro = getProperties();
		if (pro == null) {
			pro = new Properties();
		}
		pro.put(key, value);

		// 保存属性到文件中
		saveProperties(pro);
	}

	public static void main(String[] args) {
		try {
			updateProperties("key", "value");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
