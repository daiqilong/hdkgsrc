package com.whir.component.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class JacksonUtil {
    private static Logger logger = Logger
            .getLogger(JacksonUtil.class.getName());
    // private JsonGenerator jsonGenerator = null;
    // private ObjectMapper objectMapper = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public JacksonUtil() {

//        if (objectMapper == null) {
//            objectMapper = new ObjectMapper();
//            try {
//                jsonGenerator = objectMapper.getJsonFactory()
//                        .createJsonGenerator(System.out, JsonEncoding.UTF8);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private String htmlEncode(String str) {
        str = str.replaceAll("'", "&#39;");
        str = str.replaceAll("\r", "<br/>");
        str = str.replaceAll("\n", "<br/>");
        str = str.replaceAll("\t", "&nbsp;&nbsp;");
        return str;
    }
    
    // java对象构建
    public String writeArrayJSON(String[] fields, List data) {
        long startTime = System.currentTimeMillis();
        
        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
                Object[] arr = (Object[]) data.get(i);
                json.append(i > 0 ? ",{" : "{");
                for (int k = 0, len = fields.length; k < len; k++) {
                    json.append((k > 0 ? ",'" : "'")).append(fields[k]).append("':");

                    Object objVal = arr[k];
                    if (objVal instanceof Date) {
                        objVal = sdf.format(objVal);

                    } else if (objVal instanceof Double
                            || objVal instanceof Float
                            || objVal instanceof Integer) {
                        BigDecimal b = new BigDecimal(objVal.toString());
                        objVal = b.toPlainString();
                    }

                    json.append((objVal != null && (!"".equals(objVal
                            .toString().trim()))) ? "'"
                            + htmlEncode(objVal.toString()) + "'" : "''");
                }
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        // logger.debug("json:"+json);
        
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();
    }
 // java对象构建
    public String writeNewArrayJSON(String[] fields, List<List<Map<String,Object>>> data) {
        long startTime = System.currentTimeMillis();
        
        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
            	int seqNum = i+1;
            	List<Map<String,Object>> list1 = data.get(i);
            	json.append(i > 0 ? ",{'seqNum':'"+seqNum+"'" : "{'seqNum':'"+seqNum+"'");
            	for(Map<String,Object> map:list1){
            		for (int k = 0, len = fields.length; k < len; k++) {
            			if(map.containsKey(fields[k])){
            				json.append( ",'").append(fields[k]).append("':");

                            Object objVal = map.get(fields[k]);
                            if (objVal instanceof Date) {
                                objVal = sdf.format(objVal);

                            } else if (objVal instanceof Double
                                    || objVal instanceof Float
                                    || objVal instanceof Integer) {
                                BigDecimal b = new BigDecimal(objVal.toString());
                                objVal = b.toPlainString();
                            }

                            json.append((objVal != null && (!"".equals(objVal
                                    .toString().trim()))) ? "'"
                                    + htmlEncode(objVal.toString()) + "'" : "''");
            			}
                    }
            	}
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();
    }
    
 // java对象构建
    public String writeListToJSON(String[] fields, List<Map<String,String>> data) {
        long startTime = System.currentTimeMillis();
        
        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
            	int seqNum = i+1;
            	Map<String,String> map = data.get(i);
            	json.append(i > 0 ? ",{'seqNum':'"+seqNum+"'" : "{'seqNum':'"+seqNum+"'");
                for (int k = 0, len = fields.length; k < len; k++) {
                	if(map.containsKey(fields[k])){
        				json.append( ",'").append(fields[k]).append("':");

                        Object objVal = map.get(fields[k]);
                        if (objVal instanceof Date) {
                            objVal = sdf.format(objVal);

                        } else if (objVal instanceof Double
                                || objVal instanceof Float
                                || objVal instanceof Integer) {
                            BigDecimal b = new BigDecimal(objVal.toString());
                            objVal = b.toPlainString();
                        }

                        json.append((objVal != null && (!"".equals(objVal
                                .toString().trim()))) ? "'"
                                + htmlEncode(objVal.toString()) + "'" : "''");
        			}
                }
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();
    }
    
 // java对象构建
    public List<Map<String, Object>> createResultList(String[] fields, List<List<Map<String,Object>>> data) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
            	List<Map<String,Object>> list1 = data.get(i);
            	Map<String, Object> map1 = new HashMap<String, Object>();
            	for(Map<String,Object> map:list1){
            		for (int k = 0, len = fields.length; k < len; k++) {
            			if(map.containsKey(fields[k])){
                            Object objVal = map.get(fields[k]);
                            if (objVal instanceof Date) {
                                objVal = sdf.format(objVal);

                            } else if (objVal instanceof Double
                                    || objVal instanceof Float
                                    || objVal instanceof Integer) {
                                BigDecimal b = new BigDecimal(objVal.toString());
                                objVal = b.toPlainString();
                            }
                            map1.put(fields[k], objVal);
            			}
                    }
            	}
            	resultList.add(map1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return resultList;
    }
    
    // java对象构建
    public String writeArrayJSON(String[] fields, List data, String attr_key,
            String code) {
        long startTime = System.currentTimeMillis();
        
        // 主键
        String[] arr_key = new String[] { attr_key };
        String[] arr_code = new String[] { code };
        if (attr_key.indexOf(",") > 0) {
            arr_key = attr_key.split(",");
            arr_code = code.split(",");
        }

        // 字段
        com.whir.component.security.crypto.EncryptUtil util = new com.whir.component.security.crypto.EncryptUtil();
        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
                Map<String, String> encode_map = new HashMap();
                Object[] arr = (Object[]) data.get(i);
                json.append(i > 0 ? ",{" : "{");
                for (int k = 0, flen = fields.length; k < flen; k++) {
                    Object objVal = arr[k];

                    for (int m = 0, alen = arr_key.length; m < alen; m++) {
                        String f_key = arr_key[m];
                        String t_key = arr_key[m];
                        int kpos = f_key.indexOf(";");
                        if (kpos > 0) {
                            f_key = f_key.substring(0, kpos);
                            t_key = t_key.substring(t_key.indexOf(";") + 1,
                                    t_key.length());
                        }
                        if (fields[k].equals(f_key)) {
                            String encode = util.getSysEncoderKeyVlaue(t_key,
                                    objVal != null ? objVal.toString() : "",
                                    arr_code[m]);
                            encode_map.put(f_key, encode);
                        }
                    }

                    json.append((k > 0 ? ",'" : "'")).append(fields[k]).append("':");

                    if (objVal instanceof Date) {
                        objVal = sdf.format(objVal);

                    } else if (objVal instanceof Double
                            || objVal instanceof Float
                            || objVal instanceof Integer) {
                        BigDecimal b = new BigDecimal(objVal.toString());
                        objVal = b.toPlainString();
                    }

                    json.append((objVal != null && (!objVal.toString().trim()
                            .equals(""))) ? "'" + htmlEncode(objVal.toString())
                            + "'" : "''");
                }

                int emapSize = encode_map.size();
                if (emapSize == 1) {
                    for (Map.Entry<String, String> m : encode_map.entrySet()) {
                        json.append(",'" + util.VERIFY_CODE + "':" + "'"
                                + m.getValue() + "'");
                    }
                } else if (emapSize > 1) {
                    for (Map.Entry<String, String> m : encode_map.entrySet()) {
                        json.append(",'" + m.getKey() + "_" + util.VERIFY_CODE
                                + "':" + "'" + m.getValue() + "'");
                    }
                }
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        // logger.debug("json:"+json);
        
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();

    }

    // java对象构建
    public String writeArrayJSON(String fields_str, List data, String attr_key,
            String code) {
        long startTime = System.currentTimeMillis();
        
        // 主键
        String[] arr_key = new String[] { attr_key };
        String[] arr_code = new String[] { code };
        if (attr_key.indexOf(",") > 0) {
            arr_key = attr_key.split(",");
            arr_code = code.split(",");
        }

        // 字段
        String[] fields = fields_str.split(",");
        if (fields != null) {
            for (int i = 0, flen = fields.length; i < flen; i++) {
                String field = fields[i];
                int pos = field.lastIndexOf(".");
                if (pos >= 0) {
                    field = field.substring(pos + 1, field.length());
                    fields[i] = field.trim();
                }
            }
        }

        // json
        com.whir.component.security.crypto.EncryptUtil util = new com.whir.component.security.crypto.EncryptUtil();
        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
                Map<String, String> encode_map = new HashMap();
                Object[] arr = (Object[]) data.get(i);
                json.append(i > 0 ? ",{" : "{");

                for (int k = 0, flen = fields.length; k < flen; k++) {
                    Object objVal = arr[k];

                    for (int m = 0, alen = arr_key.length; m < alen; m++) {
                        String f_key = arr_key[m];
                        String t_key = arr_key[m];
                        int kpos = f_key.indexOf(";");
                        if (kpos > 0) {
                            f_key = f_key.substring(0, kpos);
                            t_key = t_key.substring(t_key.indexOf(";") + 1,
                                    t_key.length());
                        }
                        if (fields[k].equals(f_key)) {
                            String encode = util.getSysEncoderKeyVlaue(t_key,
                                    objVal != null ? objVal.toString() : "",
                                    arr_code[m]);
                            encode_map.put(f_key, encode);
                        }
                    }
                    json.append((k > 0 ? ",'" : "'")).append(fields[k]).append("':");

                    if (objVal instanceof Date) {
                        objVal = sdf.format(objVal);
                    } else if (objVal instanceof Double
                            || objVal instanceof Float
                            || objVal instanceof Integer) {
                        BigDecimal b = new BigDecimal(objVal.toString());
                        objVal = b.toPlainString();
                    }
                    json.append((objVal != null && (!objVal.toString().trim()
                            .equals(""))) ? "'" + htmlEncode(objVal.toString())
                            + "'" : "''");
                }

                int emapSize = encode_map.size();
                if (emapSize == 1) {
                    for (Map.Entry<String, String> m : encode_map.entrySet()) {
                        json.append(",'" + util.VERIFY_CODE + "':" + "'"
                                + m.getValue() + "'");
                    }
                } else if (emapSize > 1) {
                    for (Map.Entry<String, String> m : encode_map.entrySet()) {
                        json.append(",'" + m.getKey() + "_" + util.VERIFY_CODE
                                + "':" + "'" + m.getValue() + "'");
                    }
                }
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        // logger.debug("json:"+json);
        
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();
    }

    // java对象构建

    public String writeArrayJSON(String fields_str, List data) {
        long startTime = System.currentTimeMillis();
        
        String[] fields = fields_str.split(",");
        if (fields != null) {
            for (int i = 0, flen = fields.length; i < flen; i++) {
                String field = fields[i];
                int pos = field.lastIndexOf(".");
                if (pos >= 0) {
                    field = field.substring(pos + 1, field.length());
                    fields[i] = field.trim();
                }
            }
        }

        StringBuffer json = new StringBuffer();
        json.append("[");
        try {
            for (int i = 0, dlen = data.size(); i < dlen; i++) {
            	int seqNum = i+1;
                Object[] arr = (Object[]) data.get(i);
                json.append(i > 0 ? ",{'seqNum':'"+seqNum+"'" : "{'seqNum':'"+seqNum+"'");
                for (int k = 0, flen = fields.length; k < flen; k++) {
                    Object objVal = arr[k];

                    json.append(",'").append(fields[k]).append("':");

                    if (objVal instanceof Date) {
                        objVal = sdf.format(objVal);

                    } else if (objVal instanceof Double
                            || objVal instanceof Float
                            || objVal instanceof Integer) {
                        BigDecimal b = new BigDecimal(objVal.toString());
                        objVal = b.toPlainString();
                    }

                    json.append((objVal != null && (!"".equals(objVal
                            .toString().trim()))) ? "'"
                            + htmlEncode(objVal.toString()) + "'" : "''");
                }
                json.append("}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.append("]");
        // logger.debug("json:"+json);
        
        logger.debug("process time:"+(System.currentTimeMillis() - startTime)+"'ms.");
        return json.toString();
    }

    // // JavaBean(Entity/Model)转换成JSON
    // public void writeEntityJSON(Object bean) {
    // try {
    // // writeObject可以转换java对象，eg:JavaBean/Map/List/Array等
    // jsonGenerator.writeObject(bean);
    // // writeValue具有和writeObject相同的功能
    // objectMapper.writeValue(System.out, bean);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // // 将Map集合转换成Json字符串
    // public void writeMapJSON(Map map) {
    // try {
    // System.out.println("jsonGenerator");
    // jsonGenerator.writeObject(map);
    // System.out.println("objectMapper");
    // objectMapper.writeValue(System.out, map);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // 将List集合转换成json
    /**
     * public String writeListJSON(List<?> list) { try { String json = new
     * ObjectMapper().writeValueAsString(list); return json; } catch
     * (IOException e) { e.printStackTrace(); } return ""; }
     */

    // java对象构建
    // public void writeOthersJSON() {
    // try {
    //
    // String[] arr = new String[] { "a", "b", "c" };
    // /**
    // * System.out.println("jsonGenerator"); String str =
    // * "hello world jackson!"; //byte
    // * jsonGenerator.writeBinary(str.getBytes()); //boolean
    // * jsonGenerator.writeBoolean(true); //null
    // * jsonGenerator.writeNull(); //float
    // * jsonGenerator.writeNumber(2.2f); //char
    // * jsonGenerator.writeRaw("c"); //String jsonGenerator.writeRaw(str,
    // * 5, 10); //String jsonGenerator.writeRawValue(str, 5, 5); //String
    // * jsonGenerator.writeString(str);
    // * jsonGenerator.writeTree(JsonNodeFactory.instance.POJONode(str));
    // * System.out.println();
    // */
    //
    // // Object
    // jsonGenerator.writeStartObject();// {
    // jsonGenerator.writeStringField("name", "jackson");// name:jackson
    // jsonGenerator.writeEndObject();// }
    //
    // // complex Object
    // jsonGenerator.writeStartObject();// {
    // jsonGenerator.writeObjectField("infos", arr);// infos:[array]
    // jsonGenerator.writeEndObject();// }
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // 将json字符串转换成JavaBean对象
    public void readJson2Entity() {
        String json = "{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}";
        /**
         * try { AccountBean acc = objectMapper.readValue(json,
         * AccountBean.class); System.out.println(acc.getName());
         * System.out.println(acc); } catch (JsonParseException e) {
         * e.printStackTrace(); } catch (JsonMappingException e) {
         * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
         */
    }

    // // 将json字符串转换成List<Map>集合
    // public void readJson2List() {
    // String json =
    // "[{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"
    // +
    // "{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}]";
    // try {
    // List<LinkedHashMap<String, Object>> list = objectMapper.readValue(
    // json, List.class);
    // System.out.println(list.size());
    // for (int i = 0; i < list.size(); i++) {
    // Map<String, Object> map = list.get(i);
    // Set<String> set = map.keySet();
    // for (Iterator<String> it = set.iterator(); it.hasNext();) {
    // String key = it.next();
    // System.out.println(key + ":" + map.get(key));
    // }
    // }
    // } catch (JsonParseException e) {
    // e.printStackTrace();
    // } catch (JsonMappingException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // Json字符串转换成Array数组/list<bean>
    public void readJson2Array() {
        /**
         * String json =
         * "[{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"
         * +
         * "{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}]"
         * ; try { AccountBean[] arr = objectMapper.readValue(json,
         * AccountBean[].class); System.out.println(arr.length); for (int i = 0;
         * i < arr.length; i++) { System.out.println(arr[i]); }
         * 
         * List<AccountBean> al = Arrays.asList(arr);
         * 
         * } catch (JsonParseException e) { e.printStackTrace(); } catch
         * (JsonMappingException e) { e.printStackTrace(); } catch (IOException
         * e) { e.printStackTrace(); }
         */
    }

    // // Json字符串转换成Map集合
    // public void readJson2Map() {
    // String json =
    // "{\"success\":true,\"A\":{\"address\": \"address2\",\"name\":\"haha2\",\"id\":2,\"email\":\"email2\"},"
    // +
    // "\"B\":{\"address\":\"address\",\"name\":\"haha\",\"id\":1,\"email\":\"email\"}}";
    // try {
    // Map<String, Map<String, Object>> maps = objectMapper.readValue(
    // json, Map.class);
    // System.out.println(maps.size());
    // Set<String> key = maps.keySet();
    // Iterator<String> iter = key.iterator();
    // while (iter.hasNext()) {
    // String field = iter.next();
    // System.out.println(field + ":" + maps.get(field));
    // }
    // } catch (JsonParseException e) {
    // e.printStackTrace();
    // } catch (JsonMappingException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    // Jackson对XML的支持
    public void writeObject2Xml() {
        /**
         * //stax2-api-3.0.2.jar System.out.println("XmlMapper"); XmlMapper xml
         * = new XmlMapper();
         * 
         * try { //javaBean转换成xml //xml.writeValue(System.out, bean);
         * StringWriter sw = new StringWriter(); xml.writeValue(sw, bean);
         * System.out.println(sw.toString()); //List转换成xml List<AccountBean>
         * list = new ArrayList<AccountBean>(); list.add(bean); list.add(bean);
         * System.out.println(xml.writeValueAsString(list));
         * 
         * //Map转换xml文档 Map<String, AccountBean> map = new HashMap<String,
         * AccountBean>(); map.put("A", bean); map.put("B", bean);
         * System.out.println(xml.writeValueAsString(map)); } catch
         * (JsonGenerationException e) { e.printStackTrace(); } catch
         * (JsonMappingException e) { e.printStackTrace(); } catch (IOException
         * e) { e.printStackTrace(); }
         */
    }

    public void destory() {
        /*
         * try { if (jsonGenerator != null) { jsonGenerator.flush(); } if
         * (!jsonGenerator.isClosed()) { jsonGenerator.close(); } jsonGenerator
         * = null; objectMapper = null; System.gc(); } catch (IOException e) {
         * e.printStackTrace(); }
         */
    }

}