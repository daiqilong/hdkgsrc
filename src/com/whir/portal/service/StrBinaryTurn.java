package com.whir.portal.service;

public class StrBinaryTurn {
	     /** 
	      * 字符串转换成十六进制值
	      * @param bin String 我们看到的要转换成十六进制的字符串 
	      * @return 
	      */ 
	     public static String bin2hex(String bin) {
	        char[] digital = "0123456789ABCDEF".toCharArray(); 
	         StringBuffer sb = new StringBuffer("");
	        byte[] bs = bin.getBytes(); 
	         int bit;
	         for (int i = 0; i < bs.length; i++) { 
	             bit = (bs[i] & 0x0f0) >> 4;
	            sb.append(digital[bit]); 
	             bit = bs[i] & 0x0f;
	             sb.append(digital[bit]); 
	       }
	         return sb.toString(); 
	   }
	     /**
	      * 十六进制转换字符串 
	      * @param hex String 十六进制
	     * @return String 转换后的字符串 
	      */
	     public static String hex2bin(String hex) { 
	         String digital = "0123456789ABCDEF";
	         char[] hex2char = hex.toCharArray(); 
	         byte[] bytes = new byte[hex.length()/ 2];
	         int temp; 
	         for (int i = 0; i < bytes.length; i++) {
	             temp = digital.indexOf(hex2char[2 * i]) * 16; 
	             temp += digital.indexOf(hex2char[2 * i + 1]);
	             bytes[i] = (byte) (temp & 0xff); 
	        }
	         return new String(bytes); 
	     }
	    
	     /** 
	     * java字节码转字符串  
	     * @param b 
	      * @return  
	      */
	     public static String byte2hex(byte[] b) { //一个字节的数， 
	   
	        // 转成16进制字符串 
	   
	         String hs = ""; 
	         String tmp = "";
	        for (int n = 0; n < b.length; n++) { 
	           //整数转成十六进制表示
	    
	             tmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
             if (tmp.length() == 1) { 
	                 hs = hs + "0" + tmp;
	             } else { 
	                 hs = hs + tmp;
	           } 
	         }
	        tmp = null; 
	         return hs.toUpperCase(); //转成大写
	    
	     }
	    
	  /**
	    * 字符串转java字节码 
	      * @param b
	    * @return 
	      */
	     public static byte[] hex2byte(byte[] b) { 
	         if ((b.length % 2) != 0) {
	             throw new IllegalArgumentException("长度不是偶数"); 
        }
	         byte[] b2 = new byte[b.length / 2]; 
	         for (int n = 0; n < b.length; n += 2) {
	             String item = new String(b, n, 2); 
	             // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
	    
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
	         } 
	         b = null;
	        return b2; 
	     }
    
	   //将Unicode字符串转换成bool型数组
	     public static boolean[] StrToBool(String input){
	         boolean[] output=Binstr16ToBool(BinstrToBinstr16(StrToBinstr(input)));
	         return output;
	     }
	     //将bool型数组转换成Unicode字符串
	     public static String BoolToStr(boolean[] input){
	         String output=BinstrToStr(Binstr16ToBinstr(BoolToBinstr16(input)));
	         return output;
	     }
	     //将字符串转换成二进制字符串，以空格相隔
	     public static String StrToBinstr(String str) {
	         char[] strChar=str.toCharArray();
	         String result="";
	         for(int i=0;i<strChar.length;i++){
	             result +=Integer.toBinaryString(strChar[i])+ " ";
	         }
	         return result;
	     }
	     //将二进制字符串转换成Unicode字符串
	     public static String BinstrToStr(String binStr) {
	         String[] tempStr=StrToStrArray(binStr);
	         char[] tempChar=new char[tempStr.length];
	         for(int i=0;i<tempStr.length;i++) {
	             tempChar[i]=BinstrToChar(tempStr[i]);
	         }
	         return String.valueOf(tempChar);
	     }
	     //将二进制字符串格式化成全16位带空格的Binstr
	     public static String BinstrToBinstr16(String input){
	         StringBuffer output=new StringBuffer();
	         String[] tempStr=StrToStrArray(input);
	         for(int i=0;i<tempStr.length;i++){
	             for(int j=16-tempStr[i].length();j>0;j--)
	                 output.append('0');
	             output.append(tempStr[i]+" ");
	         }
	         return output.toString();
	     }
	     //将全16位带空格的Binstr转化成去0前缀的带空格Binstr
	     public static String Binstr16ToBinstr(String input){
	         StringBuffer output=new StringBuffer();
	         String[] tempStr=StrToStrArray(input);
	         for(int i=0;i<tempStr.length;i++){
	             for(int j=0;j<16;j++){
	                 if(tempStr[i].charAt(j)=='1'){
	                     output.append(tempStr[i].substring(j)+" ");
	                     break;
	                 }
	                 if(j==15&&tempStr[i].charAt(j)=='0')
	                     output.append("0"+" ");
	             }
	         }
	         return output.toString();
	     }   
	     //二进制字串转化为boolean型数组  输入16位有空格的Binstr
	     public static boolean[] Binstr16ToBool(String input){
	         String[] tempStr=StrToStrArray(input);
	         boolean[] output=new boolean[tempStr.length*16];
	         for(int i=0,j=0;i<input.length();i++,j++)
	             if(input.charAt(i)=='1')
	                 output[j]=true;
	             else if(input.charAt(i)=='0')
	                 output[j]=false;
	             else
	                 j--;
	         return output;
	     }
	     //boolean型数组转化为二进制字串  返回带0前缀16位有空格的Binstr
	     public static String BoolToBinstr16(boolean[] input){ 
	         StringBuffer output=new StringBuffer();
	         for(int i=0;i<input.length;i++){
	             if(input[i])
	                 output.append('1');
	             else
	                 output.append('0');
	             if((i+1)%16==0)
	                 output.append(' ');           
	         }
	         output.append(' ');
	         return output.toString();
	     }
	     //将二进制字符串转换为char
	     public static char BinstrToChar(String binStr){
	         int[] temp=BinstrToIntArray(binStr);
	         int sum=0;   
	         for(int i=0; i<temp.length;i++){
	             sum +=temp[temp.length-1-i]<<i;
	         }   
	         return (char)sum;
	     }
	     //将初始二进制字符串转换成字符串数组，以空格相隔
	     public static String[] StrToStrArray(String str) {
	         return str.split(" ");
	     }
	     //将二进制字符串转换成int数组
	     public static int[] BinstrToIntArray(String binStr) {       
	         char[] temp=binStr.toCharArray();
	         int[] result=new int[temp.length];   
	         for(int i=0;i<temp.length;i++) {
	             result[i]=temp[i]-48;
	         }
	         return result;
	     }
	     
//	     
//	     
//	     	public static void main(String[] args)
//	     	{
//	     		String bString = "1010101111001101";
//	     		System.out.println(binaryString2hexString(bString));
//	     	}
	       //二进制转16进制
     	public static String binaryString2hexString(String bString)
     	{
     		if (bString == null || bString.equals("") || bString.length() % 8 != 0)
     			return null;
     		StringBuffer tmp = new StringBuffer();
     		int iTmp = 0;
     		for (int i = 0; i < bString.length(); i += 4)
     		{
     			iTmp = 0;
     			for (int j = 0; j < 4; j++)
     			{
     				iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
     			}
     			tmp.append(Integer.toHexString(iTmp));
     		}
     		return tmp.toString();
     	}
	     //16进制转二进制
     	
//     	public class Hex2Binary
//     	{
//     		public static void main(String[] args)
//     		{
//     			String hexString = "ABCD";
//     			System.out.println(hexString2binaryString(hexString));
//     		}

 		public static String hexString2binaryString(String hexString)
 		{
 			if (hexString == null || hexString.length() % 2 != 0)
 				return null;
 			String bString = "", tmp;
 			for (int i = 0; i < hexString.length(); i++)
 			{
 				tmp = "0000"
 						+ Integer.toBinaryString(Integer.parseInt(hexString
 								.substring(i, i + 1), 16));
 				bString += tmp.substring(tmp.length() - 4);
 			}
 			return bString;
 		}
     		
	    public static void main(String[] args) {
	        String content = "03F5F30659E436F093D6518728189B03"; 
	        System.out.println(hex2bin(content));
	        System.out.println(hex2bin(bin2hex(content))); 
	     }
	}
