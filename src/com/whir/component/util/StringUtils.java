package com.whir.component.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.Collator;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils
{
  private static final String[] hexDigits = { "0", "1", "2", "3", "4", "5", 
    "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

  public static String killNull(String str)
  {
    String returnStr = null;
    if ((str == null) || ("null".equals(str.toLowerCase())))
      returnStr = "";
    else {
      returnStr = str;
    }
    return returnStr;
  }

  public static String stringToASCII(String transParam)
  {
    if ((transParam == null) || (transParam.length() == 0)) {
      return null;
    }
    char[] transChars = transParam.toCharArray();
    String ascii = "";

    int charASCII = -1;
    for (int i = 0; i < transChars.length; i++) {
      charASCII = transChars[i];

      if ((charASCII == 73) || (charASCII == 79)) {
        charASCII++;
      }
      ascii = ascii + charASCII;
    }
    return ascii;
  }

  public static boolean isEmpty(String str)
  {
    if ((str == null) || (str.equalsIgnoreCase("null")) || (str.length() <= 0)) {
      return true;
    }
    return false;
  }

  public static boolean isNotEmpty(String str)
  {
    return !isEmpty(str);
  }

  public static boolean isBlank(String str)
  {
    int strLen;
    if ((str == null) || ((strLen = str.length()) == 0))
      return true;
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static boolean isNotBlank(String str)
  {
    return !isBlank(str);
  }

  public static String trim(String str)
  {
    return str == null ? null : str.trim();
  }

  public static String replace(String source, String oldStr, String newStr)
  {
    return replace(source, oldStr, newStr, true);
  }

  public static String replace(String source, String oldStr, String newStr, boolean matchCase)
  {
    if (isNotEmpty(source)) {
      return null;
    }

    if (source.toLowerCase().indexOf(oldStr.toLowerCase()) == -1) {
      return source;
    }
    int findStartPos = 0;
    int a = 0;
    while (a > -1) {
      int b = 0;

      String str1 = source;
      String str2 = str1.toLowerCase();
      String str3 = oldStr;
      String str4 = str3.toLowerCase();
      String strB;
      String strA;
      if (matchCase) {
         strA = str1;
        strB = str3;
      } else {
        strA = str2;
        strB = str4;
      }
      a = strA.indexOf(strB, findStartPos);
      if (a > -1) {
        b = oldStr.length();
        findStartPos = a + b;
        StringBuffer bbuf = new StringBuffer(source);

        findStartPos = findStartPos + newStr.length() - b;
      }
    }
    return source;
  }

  public static String splitWith(String src, String regex, String other)
  {
    String retString = "";
    for (int i = 0; i < src.length() - 1; i++) {
      String traStr = src.substring(i, i + 1);
      int countRegex = 0;
      if ((traStr.equals(regex)) || (countRegex % 2 != 0)) {
        if (traStr.equals(regex))
          countRegex++;
        int k = src.indexOf(regex, i + 1);
        if (k != -1) {
          String dis = src.substring(i, k + 1);
          if (checkOther(dis, other)) {
            retString = retString + dis;
          } else {
            src = src.substring(k, src.length());
            retString = retString + splitWith(src, regex, other);
            return retString;
          }
          i = k;
        }
      }
    }
    return retString;
  }

  public static String arrayToString(String[] arrays, String splitWord)
  {
    String resultString = "";
    if ((arrays != null) && (arrays.length != 0)) {
      StringBuffer tmpstring = new StringBuffer();
      boolean flag = false;
      for (int i = 0; i < arrays.length; i++) {
        if (flag) {
          if (isBlank(splitWord))
            tmpstring.append(",");
          else {
            tmpstring.append(splitWord);
          }
        }
        tmpstring.append(arrays[i].trim());
        flag = true;
      }
      resultString = tmpstring.toString();
    }
    return resultString;
  }

  public static String listToString(List stringlist, String splitWord)
  {
    String resultString = "";
    if ((stringlist != null) && (stringlist.size() != 0)) {
      StringBuffer tmpstring = new StringBuffer();
      boolean flag = false;
      for (int i = 0; i < stringlist.size(); i++) {
        if (flag) {
          if (isBlank(splitWord))
            tmpstring.append(",");
          else {
            tmpstring.append(splitWord);
          }
        }
        tmpstring.append(stringlist.get(i).toString().trim());
        flag = true;
      }
      resultString = tmpstring.toString();
    }
    return resultString;
  }

  public static String[] stringToArray(String string, String splitWord)
  {
    String[] tmpArray = (String[])null;
    if (isNotBlank(string)) {
      if (isNotBlank(splitWord)) {
        tmpArray = string.split(splitWord);
      } else {
        tmpArray = new String[1];
        tmpArray[0] = new String(string);
      }
    }
    return tmpArray;
  }

  public static List stringToList(String string, String splitWord)
  {
    List tmpList = null;
    if (isNotBlank(string)) {
      if (isNotBlank(splitWord)) {
        tmpList = Arrays.asList(string.split(splitWord));
      } else {
        String[] tmpArray = { string };
        tmpList = Arrays.asList(tmpArray);
      }
    }
    return tmpList;
  }

  private static boolean checkOther(String dist, String other)
  {
    for (int i = 0; i < other.length(); i++) {
      if (dist.indexOf(other.substring(i, i + 1)) != -1)
        return false;
    }
    return true;
  }


  public static void sortch(String[] arr)
  {
    Comparator cmp = Collator.getInstance(Locale.CHINA);
    Arrays.sort(arr, cmp);
  }

  public static String replaceHtmlCode(String content)
  {
    if (isEmpty(content)) {
      return "";
    }
    String[] eventKeywords = { "onmouseover", "onmouseout", "onmousedown", 
      "onmouseup", "onmousemove", "onclick", "ondblclick", 
      "onkeypress", "onkeydown", "onkeyup", "ondragstart", 
      "onerrorupdate", "onhelp", "onreadystatechange", "onrowenter", 
      "onrowexit", "onselectstart", "onload", "onunload", 
      "onbeforeunload", "onblur", "onerror", "onfocus", "onresize", 
      "onscroll", "oncontextmenu" };
    content = replace(content, "<script", "&lt;script", false);
    content = replace(content, "</script", "&lt;/script", false);
    content = replace(content, "<marquee", "&lt;marquee", false);
    content = replace(content, "</marquee", "&lt;/marquee", false);
    content = replace(content, "\r\n", "<BR/>");

    for (int i = 0; i < eventKeywords.length; i++) {
      content = replace(content, eventKeywords[i], 
        "_" + eventKeywords[i], false);
    }
    return content;
  }

  public static String escapeHTMLTags(String str)
  {
    StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
      case '<':
        stringBuffer.append("&lt;");
        break;
      case '>':
        stringBuffer.append("&gt;");
        break;
      case '\r':
        stringBuffer.append("<br/>");
        break;
      case '\n':
        stringBuffer.append("<br/>");
        break;
      case '\'':
        stringBuffer.append("&acute;");
        break;
      case '"':
        stringBuffer.append("&quot;");
        break;
      case ' ':
        stringBuffer.append("&nbsp;");
        break;
      default:
        stringBuffer.append(c);
      }
    }

    return stringBuffer.toString();
  }

  public static String escapeHTML(String str) {
    StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
      case '<':
        stringBuffer.append("&lt;");
        break;
      case '>':
        stringBuffer.append("&gt;");
        break;
      case '=':
      default:
        stringBuffer.append(c);
      }
    }

    return stringBuffer.toString();
  }

  public static String escapeHTMLTagsGW(String str)
  {
    StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
      case '<':
        stringBuffer.append("&lt;");
        break;
      case '>':
        stringBuffer.append("&gt;");
        break;
      case '\'':
        stringBuffer.append("&acute");
        break;
      case '"':
        stringBuffer.append("&quot;");
        break;
      case ' ':
        stringBuffer.append("&nbsp;");
        break;
      default:
        stringBuffer.append(c);
      }
    }

    return stringBuffer.toString();
  }

  public static String escapeHTMLTagsSimple(String str)
  {
    StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      switch (c) {
      case '\n':
        stringBuffer.append("<br/>");
        break;
      case ' ':
        stringBuffer.append("&nbsp;");
        break;
      default:
        stringBuffer.append(c);
      }
    }

    return stringBuffer.toString();
  }

  public static String escapeHTMLQuot(String str)
  {
    StringBuffer stringBuffer = new StringBuffer(str.length() + 6);
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '"')
        stringBuffer.append("\\");
      stringBuffer.append(c);
    }
    return stringBuffer.toString();
  }

  public static String ISO2GB(String text)
  {
    String result = "";
    try {
      result = new String(text.getBytes("ISO-8859-1"), "GB2312");
    } catch (UnsupportedEncodingException ex) {
      result = ex.toString();
    }
    return result;
  }

  public static String GB2ISO(String text)
  {
    String result = "";
    try {
      result = new String(text.getBytes("GB2312"), "ISO-8859-1");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public static String Transcoding(String text, String coding0, String coding1)
  {
    String result = "";
    try {
      result = new String(text.getBytes(coding0), coding1);
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    }
    return result;
  }

  public static String Utf8URLencode(String text)
  {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if ((c >= 0) && (c <= 'ÿ')) {
        result.append(c);
      } else {
        byte[] b = new byte[0];
        try {
          b = Character.toString(c).getBytes("UTF-8");
        } catch (Exception localException) {
        }
        for (int j = 0; j < b.length; j++) {
          int k = b[j];
          if (k < 0)
            k += 256;
          result.append("%" + Integer.toHexString(k).toUpperCase());
        }
      }
    }
    return result.toString();
  }



  public static boolean Utf8codeCheck(String text)
  {
    String sign = "";
    if (text.startsWith("%e")) {
      int i = 0; for (int p = 0; p != -1; i++) {
        p = text.indexOf("%", p);
        if (p != -1)
          p++;
        sign = sign + p;
      }
    }
    return sign.equals("147-1");
  }

  public static boolean isUtf8Url(String text)
  {
    text = text.toLowerCase();
    int p = text.indexOf("%");
    if ((p != -1) && (text.length() - p > 9)) {
      text = text.substring(p, p + 9);
    }
    return Utf8codeCheck(text);
  }

  public static String URLEncoder(String str, String coding)
    throws UnsupportedEncodingException
  {
    String ENStr = "";
    if (str != null) {
      ENStr = URLEncoder.encode(str, coding);
      ENStr = ENStr.replace('%', '$');
    }
    return ENStr;
  }

  public static String URLDecoder(String ENStr, String coding)
    throws UnsupportedEncodingException
  {
    String Str = "";
    if (ENStr != null) {
      ENStr = ENStr.replace('$', '%');
      URLDecoder urlDecoder = new URLDecoder();
      Str = URLDecoder.decode(ENStr, coding);
    }
    return Str;
  }

  public static String byteArrayToHexString(byte[] b)
  {
    StringBuffer resultSb = new StringBuffer();
    for (int i = 0; i < b.length; i++) {
      resultSb.append(byteToHexString(b[i]));
    }
    return resultSb.toString();
  }

  private static String byteToHexString(byte b)
  {
    int n = b;
    if (n < 0)
      n += 256;
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigits[d1] + hexDigits[d2];
  }

  public static String toLowerCaseInitial(String srcString, boolean flag)
  {
    StringBuffer sb = new StringBuffer();
    if (flag)
      sb.append(Character.toLowerCase(srcString.charAt(0)));
    else {
      sb.append(Character.toUpperCase(srcString.charAt(0)));
    }
    sb.append(srcString.substring(1));
    return sb.toString();
  }

  public static String reverse(String str)
  {
    if (str == null) {
      return null;
    }
    return new StringBuffer(str).reverse().toString();
  }

  public static boolean isNumeric(String str)
  {
    Pattern pattern = Pattern.compile("^[\\d]*$");
    return pattern.matcher(str).matches();
  }

  public static boolean isInteger(String str)
  {
    Pattern pattern = Pattern.compile("^[-\\+]?[1-9][\\d]*$");
    return pattern.matcher(str).matches();
  }

  public static boolean isDouble(String str)
  {
    Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
    return pattern.matcher(str).matches();
  }

  public static boolean isNumber(String str)
  {
    return (isDouble(str)) || (isNumeric(str));
  }

  public static boolean isEmail(String str)
  {
    Pattern pattern = 
      Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    return pattern.matcher(str).matches();
  }

  public static boolean isChinese(String str)
  {
    Pattern pattern = Pattern.compile("[Α-￥]+$");
    return pattern.matcher(str).matches();
  }

  public static boolean isHandPhone(String handphone)
  {
    try
    {
      if (!handphone.substring(0, 1).equals("1")) {
        return false;
      }
      if ((handphone == null) || (handphone.length() != 11)) {
        return false;
      }
      String check = "^[1]\\d{10}$";
      Pattern regex = Pattern.compile(check);
      Matcher matcher = regex.matcher(handphone);
      boolean isMatched = matcher.matches();
      if (isMatched) {
        return true;
      }
      return false;
    } catch (RuntimeException e) {
    }
    return false;
  }

  public static String AddBr(String Content)
  {
    if (Content == null) {
      return "";
    }
    String makeContent = new String();

    makeContent = Content.replaceAll("\n", "<br>");
    return makeContent;
  }

  public static String convertStr(String strIds, String fields) {
    StringBuffer where = new StringBuffer();
    String[] tmp = strIds.split(",");
    int max = 500;

    if (tmp.length > max) {
      int t = tmp.length % max == 0 ? tmp.length / max : tmp.length / max + 
        1;
      for (int i = 0; i < t; i++)
        if (i == 0) {
          where.append(fields + " in (-1");
          for (int j = 0; j < max; j++) {
            if (i * max + j < tmp.length) {
              where.append(",").append(tmp[(i * max + j)]);
            }
          }
          where.append(")");
        } else {
          where.append(" or " + fields + " in (-1");
          for (int j = 0; j < max; j++) {
            if (i * max + j < tmp.length) {
              where.append(",").append(tmp[(i * max + j)]);
            }
          }
          where.append(")");
        }
    }
    else {
      where.append(fields + " in (");
      where.append(strIds);
      where.append(")");
    }
    return where.toString();
  }

  public static boolean isEmpty(Object o)
  {
    if (o == null) {
      return true;
    }

    String s = null;
    if (!(o instanceof String))
      s = o.toString();
    else {
      s = (String)o;
    }

    if ((s == null) || (s.trim().length() == 0) || ("null".equals(s))) {
      return true;
    }
    return false;
  }


  public static String HtmlToText(String inputString)
  {
    String htmlStr = inputString.replaceAll("&nbsp;", " ");
    String textStr = "";
    try
    {
      String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

      String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

      String regEx_html = "<[^>]+>";

      Pattern p_script = Pattern.compile(regEx_script, 2);
      Matcher m_script = p_script.matcher(htmlStr);
      htmlStr = m_script.replaceAll("");

      Pattern p_style = Pattern.compile(regEx_style, 2);
      Matcher m_style = p_style.matcher(htmlStr);
      htmlStr = m_style.replaceAll("");

      Pattern p_html = Pattern.compile(regEx_html, 2);
      Matcher m_html = p_html.matcher(htmlStr);
      htmlStr = m_html.replaceAll("");

      textStr = htmlStr;
    } catch (Exception e) {
      System.err.println("Html2Text: " + e.getMessage());
    }

    return textStr;
  }

  public static String formatString(String str, Object[] params)
  {
    MessageFormat mf = new MessageFormat(str);
    return mf.format(params);
  }

  public static String getFilePathOrNameSmall(String pathOrName)
  {
    return getFilePathOrName(pathOrName, "_small");
  }

  public static String getFilePathOrNameMiddle(String pathOrName)
  {
    return getFilePathOrName(pathOrName, "_middle");
  }

  public static String getFilePathOrNameBig(String pathOrName)
  {
    return getFilePathOrName(pathOrName, "_big");
  }

  public static String getFilePathOrName(String pathOrName, String sizeType)
  {
    String filename_1 = pathOrName
      .substring(0, pathOrName.lastIndexOf("."));
    String filename_2 = pathOrName.substring(pathOrName.lastIndexOf("."), 
      pathOrName.length());
    pathOrName = filename_1 + sizeType + filename_2;
    return pathOrName;
  }

  public static String[] getSplitArray(String sourceStr, String tag)
  {
    return sourceStr.split(tag);
  }

  public static String[] getSplitArrayForUpload(String sourceStr)
  {
    return getSplitArray(sourceStr, "\\|");
  }

  public static String ignoreInvialdChar(String str)
  {
    if (isNotEmpty(str)) {
      String temp = str.replaceAll("['|\n|\r|\t| |(|;|,|)|/|\\\\]", "");
      return temp;
    }

    return str;
  }

  public static String ignoreInvialdChar(String str, String regex) {
    if (isNotEmpty(str)) {
      String temp = str.replaceAll("[" + regex + "]", "");
      return temp;
    }

    return str;
  }

  public static boolean checkInvalidChar(String srcStr, String invalidChar)
  {
    if ((srcStr == null) || (invalidChar == null)) return false;

    char[] strArr = srcStr.toCharArray();

    char[] array = invalidChar.toCharArray();

    for (char c : strArr) {
      for (char ch : array) {
        if (c == ch) {
          return true;
        }
      }
    }
    return false;
  }
}
