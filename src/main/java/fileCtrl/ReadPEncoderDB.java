package fileCtrl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import algorithmSettings.AEScoder;
import algorithmSettings.Base64coder;
import iface.IfPwdCoder;

/**
 * 密码数据库文件读写：bak 明文格式与 DB 编码格式的互转。
 *
 * @author jessie
 */
public final class ReadPEncoderDB {

	private static final String BAK_FILE = "PEncoderDatabasebak";
	private static final String DB_FILE = "PEncoderDatabase";
	private static final String DB_HEADER = "闽⫷";
	/** 默认列分隔符（与 v2 密文中的 "v2:" 区分，避免混淆）；新建 bak 时使用，读取时兼容 ":" */
	public static final String BAK_DELIMITER = "⫸";

	private ReadPEncoderDB() {
		// 工具类
	}

	/**
	 * 将文本写入指定文件（UTF-8，覆盖已存在文件）。
	 *
	 * @param content  待写入内容
	 * @param fileName 文件名（不含路径，当前目录下）
	 */
	public static void writeToText(String content, String fileName) throws IOException {
		String path = "." + File.separator + fileName;
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		file.createNewFile();
		try (BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
			if (content != null) {
				bw.write(content);
			}
			bw.flush();
		}
	}

	/**
	 * 新建 bak 文件（若已存在则拒绝）。
	 */
	public static void newDBbakFile() throws IOException {
		String path = "." + File.separator + BAK_FILE;
		File file = new File(path);
		if (file.exists()) {
			System.out.println("bak文件存在!拒绝新建。");
			return;
		}
		String template = "使用前请删除此行。\n默认分隔符（可复制）：" + BAK_DELIMITER + "\n格式示例：\n软件平台" + BAK_DELIMITER + "账号名" + BAK_DELIMITER + "密码" + BAK_DELIMITER + "备注";
		writeToText(template, BAK_FILE);
		System.out.println("已新建bak文件。");
	}

	/**
	 * 解码 DB 文件为 bak 明文文件。
	 *
	 * @throws IOException 文件不存在或读写失败
	 */
	public static void decodeDB() throws IOException {
		String path = "." + File.separator + DB_FILE;
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("文件不存在或出错！请将 PEncoderDatabase 放置于当前路径并检查权限。");
			throw new IOException("文件不存在: " + path);
		}
		System.out.println("DB文件存在，开始读取……");
		StringBuilder readResult = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contentEquals(DB_HEADER)) {
					readResult.setLength(0);
				} else {
					readResult.append(line).append("\n");
				}
			}
		}
		System.out.println("读取DB文件完毕。");
		String raw = readResult.toString();
		if (raw.isEmpty()) {
			System.out.println("DB 文件内容为空或仅有头部。");
			return;
		}
		raw = raw.substring(0, raw.length() - 1).replaceAll("\\s+", "");
		IfPwdCoder decry = new AEScoder();
		String x = decry.decode(raw);
		byte[] b = Base64coder.decodeBase64(x);
		if (b == null) {
			throw new IOException("Base64 解码失败");
		}
		String c = CheckingInput.byteArrayToStr(b);
		if (c == null || c.isEmpty()) {
			throw new IOException("解密后内容异常");
		}
		writeToText(c, BAK_FILE);
	}

	/**
	 * 编码 bak 明文文件为 DB 文件。
	 *
	 * @throws IOException 文件不存在或读写失败
	 */
	public static void encodeDB() throws IOException {
		String path = "." + File.separator + BAK_FILE;
		File file = new File(path);
		if (!file.exists()) {
			System.out.println("文件不存在或出错！请将 PEncoderDatabasebak 放置于当前路径并检查权限。");
			throw new IOException("文件不存在: " + path);
		}
		System.out.println("bak文件存在，开始读取……");
		StringBuilder readResult = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				readResult.append(line).append("\n");
			}
		}
		System.out.println("读取bak文件完毕。");
		String content = readResult.toString();
		byte[] bytes = CheckingInput.stringToByteArray(content);
		if (bytes == null) {
			throw new IOException("内容转字节失败");
		}
		String a = Base64coder.encodeBase64(bytes);
		IfPwdCoder enc = new AEScoder();
		String x = enc.encode(a);
		String w = DB_HEADER + "\n" + x;
		writeToText(w, DB_FILE);
	}
}
