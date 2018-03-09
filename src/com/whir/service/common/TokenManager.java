package com.whir.service.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;

public class TokenManager
{
  private static Logger logger = Logger.getLogger(TokenManager.class.getName());

  private static TokenManager tokenManager = null;

  private static long lastClearTime = 0L;
  private static final long timeout = 7200000L;
  private static final long clearTimeSet = 7200000L;
  private static ConcurrentHashMap<String, String[]> tokenMap = null;

  private TokenManager()
  {
    tokenMap = new ConcurrentHashMap();
  }

  public static synchronized TokenManager getInstance()
  {
    if (tokenManager == null) {
      tokenManager = new TokenManager();
    }
    return tokenManager;
  }

  public String createToken(String systemKey, String userKey, String userKeyType)
  {
    long time;
    try
    {
      clearLostToken();
    } catch (Exception e) {
      logger.debug("=========clearLostToken()报错");
      e.printStackTrace();
    } finally {
      time = System.currentTimeMillis();
    }String token = UUID.randomUUID().toString();
    tokenMap.put(systemKey + "_" + userKey + "_" + userKeyType, new String[] { token, systemKey, userKey, userKeyType, Long.toString(time) });
    return token;
  }

  private void clearLostToken()
  {
    long nowTime = System.currentTimeMillis();
    try
    {
      if (nowTime - lastClearTime > 7200000L) {
        logger.debug("==========开始执行清除过期token");
        Iterator it = tokenMap.entrySet().iterator();
        Map newMap = new HashMap();
        while (it.hasNext()) {
          Map.Entry entry = (Map.Entry)it.next();
          String key = entry.getKey().toString();
          String[] value = (String[])entry.getValue();

          String time = value[4];
          if (nowTime - Long.parseLong(time) >= 7200000L) {
            tokenMap.remove(key);
            logger.debug("==========token过期移除:" + value[0]);
          }
        }
        logger.debug("==========完成执行清除过期token");
        lastClearTime = nowTime;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean judgeToken(String token, String systemKey, String userKey, String userKeyType)
  {
    clearLostToken();
    boolean result = false;

    if (tokenMap.get(systemKey + "_" + userKey + "_" + userKeyType) != null) {
      String tokenTemp = ((String[])tokenMap.get(systemKey + "_" + userKey + "_" + userKeyType))[0];
      logger.debug("==========tokenTemp:" + tokenTemp);
      if ((token != null) && 
        (tokenTemp != null) && (token.equals(tokenTemp)))
      {
        long time = Long.parseLong(((String[])tokenMap.get(systemKey + "_" + userKey + "_" + userKeyType))[4]);
        long nowTime = System.currentTimeMillis();
        if (nowTime - time < 7200000L)
          result = true;
        else {
          logger.debug("==========time过期:" + (nowTime - time - 7200000L));
        }
      }
    }

    return result;
  }
}