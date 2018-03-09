package com.whir.portal.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.whir.portal.service.Base64Helper;

/**
 * md5加密工具类
 * 把原文按照标准转换成32位小写的字符串
 */
public class MD5 {
	
	private static String Algorithm="DES"; //定义 加密算法,可用 DES,DESede,Blowfish
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	//生成密钥, 注意此步骤时间比较长
	public static byte[] getKey() throws Exception{
		KeyGenerator keygen = KeyGenerator.getInstance(Algorithm);
		SecretKey deskey = keygen.generateKey();
		return deskey.getEncoded();
	}
	
	//加密
	public static byte[] encode(byte[] input,byte[] key) throws Exception{
		SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key,Algorithm);
		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.ENCRYPT_MODE,deskey);
		byte[] cipherByte=c1.doFinal(input);
		return cipherByte;
	}
	
	//解密
	public static byte[] decode(byte[] input,byte[] key) throws Exception{
		SecretKey deskey = new javax.crypto.spec.SecretKeySpec(key,Algorithm);
		Cipher c1 = Cipher.getInstance(Algorithm);
		c1.init(Cipher.DECRYPT_MODE,deskey);
		byte[] clearByte=c1.doFinal(input);
		return clearByte;
	}
	
	private static String byteToHexString(byte b) {
		return hexDigits[(b & 0xf0) >> 4] + hexDigits[b & 0x0f];
	}
	/**
	 * 转换字节数组为16进制字串
	 * 
	 * @param b
	 *            字节数组
	 * @return 16进制字串
	 */
	private static String byteArrayToHexString(byte[] b) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			buf.append(byteToHexString(b[i]));
		}
		return buf.toString();
	}
	
	/**
	 * md5加密 生成32位小写字符串
	 * @param origin 原文
	 * @return
	 * @author chenli
	 * @data Jan 18, 2013
	 */
	public static String MD5Encode(String origin) {
		String resultString = null;
		
		resultString = origin;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return resultString;
	}
	//md5()信息摘要, 不可逆
	public static byte[] md5(byte[] input) throws Exception{
		java.security.MessageDigest alg=java.security.MessageDigest.getInstance("MD5"); //or "SHA-1"
		alg.update(input);
		byte[] digest = alg.digest();
		return digest;
	}
	
	
	public static String MD5Encode(String origin, String charSet) {
		String resultString = null;
		resultString = new String(origin);
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			resultString = byteArrayToHexString(md.digest(resultString.getBytes(charSet)));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return resultString;
	}
	
	public static void main(String[] args)  
	{  
	    String testStr = "111111";  
	    System.out.println("加密前：" + testStr);  
	  
	    String decodeStr = new MD5().MD5Encode(testStr);
	    System.out.println("解密数据：" + decodeStr);  
	} 
}
