package com.whir.portal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class XmlHelper 
{
	  private static final Log log = LogFactory.getLog(XmlHelper.class);

	  public static String getElement(String docXml, String xpath)
	  {
	    try
	    {
	      InputStream is = IOUtils.toInputStream(docXml, "utf-8");
	      Document doc = new SAXReader().read(is);
	      List childNodes = doc.selectNodes(xpath);
	      if ((childNodes != null) && (childNodes.size() > 0))
	        return ((Element)childNodes.get(0)).getTextTrim();
	    }
	    catch (IOException e1)
	    {
	      e1.printStackTrace();
	    }
	    catch (DocumentException e) {
	      e.printStackTrace();
	    }

	    return null;
	  }

	  public static List<Element> getElements(String docXml, String xpath)
	  {
	    try
	    {
	      InputStream is = IOUtils.toInputStream(docXml, "utf-8");
	      Document doc = new SAXReader().read(is);
	      List childNodes = doc.selectNodes(xpath);
	      if ((childNodes != null) && (childNodes.size() > 0))
	        return childNodes;
	    }
	    catch (IOException e1)
	    {
	      e1.printStackTrace();
	    }
	    catch (DocumentException e) {
	      e.printStackTrace();
	    }

	    return null;
	  }

	  public static List<Element> getElements(Element element, String xpath)
	  {
	    List childNodes = element.selectNodes(xpath);
	    if ((childNodes != null) && (childNodes.size() > 0)) {
	      return childNodes;
	    }
	    return null;
	  }

	  public static String getElement(Element element, String xpath)
	  {
	    List childNodes = element.selectNodes(xpath);

	    if ((childNodes != null) && (childNodes.size() > 0)) {
	      return ((Element)childNodes.get(0)).getTextTrim();
	    }
	    return null;
	  }

	  public static List<Element> getElements(String docXml)
	  {
	    try
	    {
	      InputStream is = IOUtils.toInputStream(docXml, "utf-8");
	      Document doc = new SAXReader().read(is);
	      List childNodes = doc.selectNodes("//input/*");
	      if ((childNodes != null) && (childNodes.size() > 0))
	        return childNodes;
	    }
	    catch (IOException e1)
	    {
	      e1.printStackTrace();
	    }
	    catch (DocumentException e) {
	      e.printStackTrace();
	    }

	    return null;
	  }
	}
