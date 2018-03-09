package com.whir.portal.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;

public class PropertyUtil {
		  public static String getPropertyByKey(String key)
		  {
		    Properties props = new Properties();
		    String value = "";
		    try {
		      props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("kgjmhconfig.properties"));
		      value = props.getProperty(key);
		      if (StringUtils.isBlank(value))
		        throw new RuntimeException("Please configure the " + key + " in the file config.properties ");
		    }
		    catch (IOException e)
		    {
		      e.printStackTrace();
		    }
		    return value;
		  }

		  public static String getAllProperties() {
		    Properties props = new Properties();
		    String value = "";
		    try {
		      props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("kgjmhconfig.properties"));
		      Enumeration keys = props.keys();
		      while (keys.hasMoreElements()) {
		        String key = (String)keys.nextElement();
		        String val = props.getProperty(key);
		        value = value + " key==" + key + "  value== " + val + "  \n";
		      }
		      if (StringUtils.isBlank(value))
		        throw new RuntimeException("Cann't find the config.properties! ");
		    }
		    catch (IOException e)
		    {
		      e.printStackTrace();
		    }
		    return value;
		  }
		}
