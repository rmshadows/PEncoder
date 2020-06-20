package algorithmSettings;

import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

/**
 * 类名：Base64编码解码相关的代码
 * @author Jessie
 */
public class Base64coder {
	
	/**
	 * base64加密
	 * 传入byte数组执行base64加密
	 * @param data byte[] 数组待编码数据
	 * @return String 编码后的字符串数据
	 */
	public static String encryptBASE64(byte[] data) {
		// BASE64Encoder encoder = new BASE64Encoder();
		// String encode = encoder.encode(data);
		// 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
		Encoder encoder = Base64.getEncoder();
		String encode = encoder.encodeToString(data);
		return encode;
	}
	
	/**
	 * base64解密
	 * 传入String执行base64解密
	 * @param data String 编码数据 
	 * @return byte[] 解码数据的数组
	 */
	public static byte[] decryptBASE64(String data) {
		// BASE64Decoder decoder = new BASE64Decoder();
		// byte[] buffer = decoder.decodeBuffer(data);
		// 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
		Decoder decoder = Base64.getDecoder();
		byte[] buffer = decoder.decode(data);
		return buffer;
	}
}
