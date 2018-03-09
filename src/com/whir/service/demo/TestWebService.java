package com.whir.service.demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import org.codehaus.xfire.client.Client;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class TestWebService
{
  private String wsdl_address = "http://10.12.1.207:7001/defaultroot/xfservices/GeneralWeb?wsdl";

  public String getUserInfoByUserId()
  {
    String input1 = "<input><key>58598301617b958a8b8343b731d0b931</key><cmd>getUserInfoByUserId</cmd><userinfo><id>6687</id></userinfo></input>";
    try {
      Client client = new Client(new URL(this.wsdl_address));

      Object[] results = client.invoke("OAManager", new Object[] { input1 });

      return (String)results[0];
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public String sendSysMail()
  {
    String input2 = "<input><key>xxxxxx</key><cmd>sendSysMail</cmd><domain>0</domain><posterName>测试webservice</posterName><receiverAccounts>sujun,w3</receiverAccounts><mailsubject>标题</mailsubject><mailcontent>正文</mailcontent></input>";
    try {
      Client client = new Client(new URL(this.wsdl_address));

      Object[] results = client.invoke("OAManager", new Object[] { input2 });

      return (String)results[0];
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  public static void main(String[] args) {
    TestWebService test = new TestWebService();

    String result1 = test.getUserInfoByUserId();
    String result2 = test.sendSysMail();
    System.out.println(result1);
    System.out.println(result2);
  }

}