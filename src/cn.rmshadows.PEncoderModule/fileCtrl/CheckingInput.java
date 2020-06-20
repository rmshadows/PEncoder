package fileCtrl;

import appCtrl.MainProgram;

/**
 * 类名：一些辅助检查手段
 * @author Jessie
 */
public class CheckingInput {
	
	/**
	 * String转byte数组
	 * String转byte数组
	 * @param str String 字符串
	 * @return byte[]
	 */
	public static byte[] stringToByteArray(String str) {
		try {
			if (str == null) {
				return null;
			}
			else {
				byte[] byteArray = str.getBytes();
				return byteArray;
			}
		}catch (Exception e) {
			System.out.println("String转byte数组:stringToByteArray error.");
			return null;
		}
	}
	
	/**
	 * byte数组转String
	 * byte数组转String
	 * @param byteArray byte[] 字节数组
	 * @return String
	 */
	public static String byteArrayToStr(byte[] byteArray) {
		if (byteArray == null) {
		return null;
		}
		String str = new String(byteArray);
		return str;
	}
	
	/**
	 * 限制输入长度
	 * 过滤用户的输入，禁止输入中文分号“；”
	 * @param in String 输入字符串
	 * @param isDecodeOn boolean 解码模式是否开启
	 * @return boolean 是否合法
	 */
	public static boolean inputFilter(String in , boolean isDecodeOn) {
		if ((in.length() > 30)&&(isDecodeOn == false)){
			System.out.print("输入过长！暂不支持超长密码。");
			return false;
		}
		else if (in.length() == 0) {
			System.out.println("输入为空！");
			return false;
		}
		else{
			boolean status = in.contains("：")||in.contains("；")||in.contains("【")||in.contains("】");
			if(status){
				System.out.println("暂不支持包含有“：”、“；”、“【”、“】”等中文特殊符号的明文密码。");
				MainProgram.isItSemicolon = true;
				return false;
			}
			MainProgram.isItSemicolon = false;
			return true;
		}
	}
	
	/**
	 * 密码补齐
	 * 明文密码不足30位的补。
	 * @param in String 密码 
	 * @return String 补齐后的密码
	 */
	public static String pwdAppend(String in) {
		int strLength = 30;
		int strLen = in.length();
		if(inputFilter(in, false)==true){
			if (in.length()<=30) {
				if (in.contains(";")) {
					in = in.replaceAll(";", "；");
				}
				else {
					
				}
				while (strLen < strLength) {
//					System.out.println(in);
					StringBuffer sb = new StringBuffer();
//					sb.append("0").append(in);// 左补0
					sb.append(in).append(";");//右补‘;’
					in = sb.toString();
					strLen = in.length();
				}
				return in;
			}
			else {
				System.out.println("通过长度检查但出错。");
				return null; 
			}
		}
		else if (inputFilter(in, false)==false) {
			System.out.println("最长支持16位明文密码。");
			return null;
		}
		else {
			System.out.println("出错！");
			return null;
		}
	}
	
	/**
	 * 密钥补齐
	 * key不到16的补0
	 * @param in String 密钥 
	 * @return String 补齐后的密钥
	 */
	public static String keyAppend(String in) {
		int strLength = 16;
		int strLen = in.length();
		if(inputFilter(in, false)==true){
			if (in.length()<=16) {
				while (strLen < strLength) {
					StringBuffer sb = new StringBuffer();
//					sb.append("0").append(in);// 左补0
					sb.append(in).append(";");//右补0
					in = sb.toString();
					strLen = in.length();
				}
				return in;
			}
			else {
				System.out.println("通过长度检查但出错。");
				return null;
			}
		}
		else if (inputFilter(in, false)==false) {
			System.out.println("最长支持16位密钥。");
			return null;
		}
		else {
			System.out.println("密钥处理出错！");
			return null;
		}
	}
}