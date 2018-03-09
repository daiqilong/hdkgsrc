package com.whir.org.vo.MoveOAmanager;

import java.io.Serializable;

public class CorpSetPO
  implements Serializable
{
  private long id;
  private String corpid;
  private String corpsecret;
  private String token;
  private String encodingAESKey;
  private String relactionId;
  private String last_relactionId;

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

  public String getCorpsecret()
  {
    return this.corpsecret;
  }

  public void setCorpsecret(String paramString)
  {
    this.corpsecret = paramString;
  }

  public String getToken()
  {
    return this.token;
  }

  public void setToken(String paramString)
  {
    this.token = paramString;
  }

  public String getEncodingAESKey()
  {
    return this.encodingAESKey;
  }

  public void setEncodingAESKey(String paramString)
  {
    this.encodingAESKey = paramString;
  }

  public String getRelactionId()
  {
    return this.relactionId;
  }

  public void setRelactionId(String paramString)
  {
    this.relactionId = paramString;
  }

  public String getLast_relactionId()
  {
    return this.last_relactionId;
  }

  public void setLast_relactionId(String paramString)
  {
    this.last_relactionId = paramString;
  }
}