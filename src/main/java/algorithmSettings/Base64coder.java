package algorithmSettings;

import java.util.Base64;

/**
 * Base64 编码/解码工具（非加密，仅做二进制与字符串的转换）。
 *
 * @author jessie
 */
public final class Base64coder {

	private Base64coder() {
		// 工具类，禁止实例化
	}

	/**
	 * Base64 编码。
	 *
	 * @param data 待编码数据，可为 null（返回 null）
	 * @return 编码后的字符串，data 为 null 时返回 null
	 */
	public static String encodeBase64(byte[] data) {
		if (data == null) {
			return null;
		}
		return Base64.getEncoder().encodeToString(data);
	}

	/**
	 * Base64 解码。
	 *
	 * @param data Base64 字符串，可为 null（返回 null）
	 * @return 解码后的字节数组，data 为 null 时返回 null
	 * @throws IllegalArgumentException 当 data 不是合法 Base64 时
	 */
	public static byte[] decodeBase64(String data) {
		if (data == null || data.isEmpty()) {
			return null;
		}
		return Base64.getDecoder().decode(data);
	}
}
