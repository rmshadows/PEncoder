package algorithmSettings;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import appCtrl.MainProgram;
import fileCtrl.CheckingInput;
import iface.IfPwdCoder;

/**
 * AES算法包
 * 实现IfPwdCoder接口，包含AES相关的方法，包括加密解密等
 * @author jessie
 */
public class AEScoder implements IfPwdCoder {
	
	/**
	 * 参数：
	 * keyA = KeyWords
	 * keyB = ivSpec
	 * 位  数 = 16byte
	 */
	private static final String ENCRY_ALGORITHM = "AES";//加密方法名称
	private static final String CIPHER_MODE = "AES/CBC/PKCS5Padding";//填充方式
	private static final String CHARACTER = "UTF-8";//编码
	private static final int PWD_SIZE = 16;//采用128位的Key 16byte
	
	/**
	 * 密钥长度补全
	 * 把所给的String密钥转为16 Byte数组并填充
	 * @param password String KeyA or KeyB 
	 * @return Byte[] 密钥的byte数组
	 * @throws UnsupportedEncodingException 忽略编码错误
	 */
	private static byte[] pwdHandler(String password, boolean isIv) throws UnsupportedEncodingException {
		byte[] data = null;
		if (isIv) {
			// iv只能16位
			if (password != null) {
				byte[] bytes = password.getBytes(CHARACTER);//一个中文3位长度，一数字1位
				if (password.length() < 16) {
					System.arraycopy(bytes, 0, data = new byte[16], 0, bytes.length);
				} 
				else {
					data = bytes;
				}
			}
		}else {
			if (password != null) {
				byte[] bytes = password.getBytes(CHARACTER);//一个中文3位长度，一数字1位
				if (password.length() < PWD_SIZE) {
					System.arraycopy(bytes, 0, data = new byte[PWD_SIZE], 0, bytes.length);
				} 
				else {
					data = bytes;
				}
			}
		}
		return data;
	}
	
	/**
	 * 最原始的AES加密器
	 * 将提供的byte数组的‘明文’和‘密钥’转化为 输出字节数字
	 * @param clearTextBytes byte[] 明文密码
	 * @param pwdBytes byte[] KeyA
	 * @return byte[] 
	 */
	private static byte[] encrypt(byte[] clearTextBytes, byte[] pwdBytes) {
		try {
			//参数要求：keySpec、ivSpec
			// 1 获取加密密钥
			SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(pwdHandler(String.copyValueOf(MainProgram.keyB.getPassword()), true));
			// 2 获取Cipher实例
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			// 查看数据块位数 默认为16（byte） * 8 =128 bit
			//System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
			// 3 初始化Cipher实例。设置执行模式以及加密密钥
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			// 4 执行
			byte[] cipherTextBytes = cipher.doFinal(clearTextBytes);
			System.out.println("加密完成。");
			// 5 返回密文字符集
			return cipherTextBytes;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		//加密失败
		System.out.println("加密失败!");
		return null;
	}
	
	/**
	 * 最原始的AES解码器
	 * 将提供的byte数组的‘密文’和‘密钥’转化为 输出字节数字
	 * @param cipherTextBytes byte[] 密文密码KeyA 
	 * @param pwdBytes byte[] KeyA 
	 * @return byte[] 
	 */
	private static byte[] decrypt(byte[] cipherTextBytes, byte[] pwdBytes) {
		//参数设置：keySpec、ivSpec
		try {
			// 1 获取解密密钥
			SecretKeySpec keySpec = new SecretKeySpec(pwdBytes, ENCRY_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(pwdHandler(String.copyValueOf(MainProgram.keyB.getPassword()), true));
			// 2 获取Cipher实例
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			// 查看数据块位数 默认为16（byte） * 8 =128 bit
			//System.out.println("数据块位数(byte)：" + cipher.getBlockSize());
			// 3 初始化Cipher实例。设置执行模式以及加密密钥
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			// 4 执行
			byte[] clearTextBytes = cipher.doFinal(cipherTextBytes);
			// 5 返回明文字符集
			return clearTextBytes;
		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
		} catch (InvalidKeyException e) {
//			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
//			e.printStackTrace();
		} catch (BadPaddingException e) {
//			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
//			e.printStackTrace();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		// 解密错误 返回null
		System.out.println("解密失败!");
		return null;
	}
	
	/**
	 * 重写接口加密方法
	 * 将明文密码加密成密文密码
	 * @param clearText String 明文密码 
	 * @return String 密文密码
	 */
	@Override
	public String encode(String clearText) {
		String encoded = null;
		byte[] a = CheckingInput.stringToByteArray(clearText);//明文密码转字节数组
		byte[] b = null;
		try {
			b = pwdHandler(String.copyValueOf(MainProgram.keyA.getPassword()), false);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//keyA
//		System.out.println(CheckingInput.byteArrayToStr(b));
		byte[] c = encrypt(a, b);
		encoded = byte2hex(c);
		return encoded;
	}
	
	/**
	 * 重写接口解密方法
	 * 将密文密码解密成明文密码
	 * @param cipherText String 密文密码 
	 * @return String 明文密码 
	 */
	@Override
	public String decoder(String cipherText) {
		String decoded = null;
//		System.out.println(cipherText);
		byte[] a = hex2byte(cipherText);
		byte[] b = null;
		try {
			b = pwdHandler(String.copyValueOf(MainProgram.keyA.getPassword()), false);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}//keyA
		byte[] c = decrypt(a, b);
		decoded = CheckingInput.byteArrayToStr(c);
		return decoded;
	}
	
	/**
	 * 更换密钥方法
	 * 替换KeyA、B时调用的方法
	 * @param clearText String 旧密码内容
	 * @param nKeyA String 新KeyA
	 * @param nKeyB String 新KeyB
	 * @return String 新密码内容
	 */
	public static String ckeyEncode(String clearText,String nKeyA, String nKeyB) {
		String encoded = null;
		byte[] a = CheckingInput.stringToByteArray(clearText);//明文密码转字节数组
		byte[] b = null;//新的KeyA
		try {
			b = pwdHandler(nKeyA, false);//先填充
//			System.out.println(String.copyValueOf(MainProgram.keyA.getPassword()));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}//keyA
		try {
			SecretKeySpec keySpec = new SecretKeySpec(b, ENCRY_ALGORITHM);
			IvParameterSpec iv = new IvParameterSpec(pwdHandler(nKeyB, true));//新的KeyB
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
			byte[] cipherTextBytes = cipher.doFinal(a);
			encoded = byte2hex(cipherTextBytes);//加密
		} catch (NoSuchPaddingException e) {
			System.out.println("加密失败!");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("加密失败!");
		} catch (BadPaddingException e) {
			System.out.println("加密失败!");
		} catch (IllegalBlockSizeException e) {
			System.out.println("加密失败!");
		} catch (InvalidKeyException e) {
			System.out.println("加密失败!");
		} catch (Exception e) {
			System.out.println("加密失败!");
		}
		return encoded;
	}
	
	/**
	 * Byte数组转十六进制字符串
	 * 字节数组转成16进制字符串，用来保存AES加密后的内容，防止数据丢失
	 * @param bytes byte[] 字节数组
	 * @return String 十六进制字符串
	 */
	private static String byte2hex(byte[] bytes) { // 一个字节的数，
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		String tmp = "";
		for (byte aByte : bytes) {
			// 整数转成十六进制表示
			tmp = (Integer.toHexString(aByte & 0XFF));
			if (tmp.length() == 1) {
				sb.append("0");
			}
			sb.append(tmp);
		}
		return sb.toString().toUpperCase(); // 转成大写
	}
	
	/**
	 * Byte数组转十六进制字符串
	 * 读取hex字符串转换成字节数组 ，用来解码AES加密后的内容
	 * @param str String 十六进制字符串
	 * @return byte[] 字节数组
	 */
	private static byte[] hex2byte(String str) {
		if (str == null || str.length() < 2) {
			return new byte[0];
		}
		str = str.toLowerCase();
		int l = str.length() / 2;
		byte[] result = new byte[l];
		for (int i = 0; i < l; ++i) {
			String tmp = str.substring(2 * i, 2 * i + 2);
			result[i] = (byte) (Integer.parseInt(tmp, 16) & 0xFF);
		}
		return result;
	}
}
