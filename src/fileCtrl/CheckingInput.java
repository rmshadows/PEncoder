package fileCtrl;

import java.nio.charset.StandardCharsets;

/**
 * 输入校验与字符串/字节转换工具（密钥、明文长度与非法字符检查）。
 *
 * @author jessie
 */
public final class CheckingInput {

	private static final int MAX_PLAIN_LENGTH = 30;
	private static final int MAX_KEY_LENGTH = 16;

	/** 上次 inputFilter 因中文符号（冒号、分号等）拒绝时为 true，供调用方读取。 */
	private static volatile boolean lastSemicolonRejection;

	private CheckingInput() {
		// 工具类
	}

	/**
	 * 上次校验是否因「中文特殊符号」被拒（用于 UI 清空输出等）。
	 */
	public static boolean wasLastRejectionSemicolon() {
		return lastSemicolonRejection;
	}

	public static byte[] stringToByteArray(String str) {
		if (str == null) {
			return null;
		}
		try {
			return str.getBytes(StandardCharsets.UTF_8);
		} catch (Exception e) {
			System.out.println("stringToByteArray error.");
			return null;
		}
	}

	public static String byteArrayToStr(byte[] byteArray) {
		if (byteArray == null) {
			return null;
		}
		try {
			return new String(byteArray, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 校验输入：长度（明文≤30/解码可更长）、非空、禁止中文冒号/分号/方括号。
	 *
	 * @param in        输入字符串
	 * @param isDecodeOn 解码模式（解码时不做 30 字限制）
	 * @return 是否通过校验
	 */
	public static boolean inputFilter(String in, boolean isDecodeOn) {
		lastSemicolonRejection = false;
		if (in == null || in.isEmpty()) {
			System.out.println("输入为空！");
			return false;
		}
		if (!isDecodeOn && in.length() > MAX_PLAIN_LENGTH) {
			System.out.print("输入过长！暂不支持超长密码。");
			return false;
		}
		boolean hasInvalidChars = in.contains("：") || in.contains("；") || in.contains("【") || in.contains("】");
		if (hasInvalidChars) {
			System.out.println("暂不支持包含“：”、“；”、“【”、“】”等中文特殊符号的明文密码。");
			lastSemicolonRejection = true;
			return false;
		}
		return true;
	}

	/**
	 * 明文密码补齐到 30 位（右侧补英文分号），并做 inputFilter 校验。
	 *
	 * @param in 明文密码
	 * @return 补齐后的字符串，校验不通过时返回 null
	 */
	public static String pwdAppend(String in) {
		if (!inputFilter(in, false)) {
			System.out.println("明文密码最长 " + MAX_PLAIN_LENGTH + " 位。");
			return null;
		}
		if (in.length() > MAX_PLAIN_LENGTH) {
			System.out.println("通过长度检查但出错。");
			return null;
		}
		if (in.contains(";")) {
			in = in.replaceAll(";", "；");
		}
		StringBuilder sb = new StringBuilder(in);
		while (sb.length() < MAX_PLAIN_LENGTH) {
			sb.append(";");
		}
		return sb.toString();
	}

	/**
	 * 密钥补齐到 16 位（右侧补分号），并做 inputFilter 校验。
	 *
	 * @param in 密钥字符串
	 * @return 补齐后的字符串，校验不通过时返回 null
	 */
	public static String keyAppend(String in) {
		if (!inputFilter(in, false)) {
			System.out.println("密钥最长 " + MAX_KEY_LENGTH + " 位。");
			return null;
		}
		if (in.length() > MAX_KEY_LENGTH) {
			System.out.println("通过长度检查但出错。");
			return null;
		}
		StringBuilder sb = new StringBuilder(in);
		while (sb.length() < MAX_KEY_LENGTH) {
			sb.append(";");
		}
		return sb.toString();
	}
}
