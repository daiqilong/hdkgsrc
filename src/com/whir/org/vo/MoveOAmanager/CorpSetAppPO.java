package com.whir.org.vo.MoveOAmanager;

import java.io.Serializable;

public class CorpSetAppPO
  implements Serializable
{
  private long id;
  private String corpid;
  private String appname;
  private String appid;

  public long getId()
  {
    return this.id;
  }

  public void setId(long paramLong)
  {
    this.id = paramLong;
  }

  public String getCorpid()
  {
    return this.corpid;
  }

  public void setCorpid(String paramString)
  {
    this.corpid = paramString;
  }

  public String getAppname()
  {
    return this.appname;
  }

  public void setAppname(String paramString)
  {
    this.appname = paramString;
  }

  public String getAppid()
  {
    return this.appid;
  }

  public void setAppid(String paramString)
  {
    this.appid = paramString;
  }
}