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
import java.util.Arrays;
import java.util.regex.Pattern;

import algorithmSettings.AEScoder;
import algorithmSettings.Base64coder;
import algorithmSettings.CryptoException;
import iface.IfPwdCoder;

/**
 * 密码数据库文件读写：bak 明文格式与 DB 编码格式的互转。
 *
 * @author jessie
 */
public final class ReadPEncoderDB {

	private static final String BAK_FILE = "PEncoderDatabasebak";
	private static final String DB_FILE = "PEncoderDatabase";
	/** 新版 DB 文件首行标识 */
	private static final String DB_HEADER = "闽⫷";
	/** 旧版 DB 文件首行标识（解码时与 DB_HEADER 均识别并跳过） */
	private static final String DB_HEADER_OLD = "闽:::";
	/** 默认列分隔符（与 v2 密文前缀 v2⸎ 区分）；新建 bak 时使用，读取时兼容 ":" */
	public static final String BAK_DELIMITER = "⫸";

	private ReadPEncoderDB() {
		// 工具类
	}

	/** 用于比较时统一换行与首尾空白 */
	private static String normalizeForCompare(String s) {
		if (s == null) return "";
		return s.replace("\r\n", "\n").replace('\r', '\n').trim();
	}

	/**
	 * 解析 bak 一行。v2 密文前缀为 v2⸎，不含冒号，按分隔符 split 即可。
	 *
	 * @param line 一行内容
	 * @return [平台, 账号, 密码, 备注]，无法解析时返回 null
	 */
	public static String[] parseBakLine(String line) {
		if (line == null || line.isEmpty()) return null;
		String sep = line.contains(BAK_DELIMITER) ? BAK_DELIMITER : ":";
		String[] split = line.split(Pattern.quote(sep), -1);
		if (split.length < 4) return null;
		String password = split[2];
		String remarks = split.length == 4 ? split[3] : String.join(sep, Arrays.copyOfRange(split, 3, split.length));
		return new String[]{split[0], split[1], password, remarks};
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
	 * @param forceOverwrite 若 true 则目标 bak 已存在且内容不一致时仍覆盖；若 false 则不一致时抛出 TargetMismatchException
	 * @throws IOException 文件不存在或读写失败
	 * @throws TargetMismatchException 目标 bak 已存在且与当前 DB 解密结果不一致且未强制覆盖
	 */
	public static void decodeDB(boolean forceOverwrite) throws IOException, TargetMismatchException, CryptoException {
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
				if (line.contentEquals(DB_HEADER) || line.contentEquals(DB_HEADER_OLD)) {
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
		if (!forceOverwrite) {
			File bakFile = new File("." + File.separator + BAK_FILE);
			if (bakFile.exists()) {
				StringBuilder existing = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(bakFile), StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						existing.append(line).append("\n");
					}
				}
				String existingContent = existing.length() > 0 ? existing.substring(0, existing.length() - 1) : "";
				if (!normalizeForCompare(c).equals(normalizeForCompare(existingContent))) {
					throw new TargetMismatchException("目标 bak 文件已存在，且与当前 DB 解密结果不一致（可能不是同一份数据或密钥不同）。仍要覆盖吗？");
				}
			}
		}
		writeToText(c, BAK_FILE);
	}

	/** 解码 DB 为 bak（未强制覆盖时，若目标 bak 已存在且内容不一致会抛 TargetMismatchException）。 */
	public static void decodeDB() throws IOException, TargetMismatchException, CryptoException {
		decodeDB(false);
	}

	/**
	 * 编码 bak 明文文件为 DB 文件。
	 *
	 * @param forceOverwrite 若 true 则目标 DB 已存在且与当前 bak 不一致时仍覆盖；若 false 则不一致时抛出 TargetMismatchException
	 * @throws IOException 文件不存在或读写失败
	 * @throws TargetMismatchException 目标 DB 已存在且与当前 bak 编码结果不一致且未强制覆盖
	 */
	public static void encodeDB(boolean forceOverwrite) throws IOException, TargetMismatchException, CryptoException {
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
		if (!forceOverwrite) {
			File dbFile = new File("." + File.separator + DB_FILE);
			if (dbFile.exists()) {
				StringBuilder dbContent = new StringBuilder();
				try (BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(dbFile), StandardCharsets.UTF_8))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.contentEquals(DB_HEADER) || line.contentEquals(DB_HEADER_OLD)) {
							dbContent.setLength(0);
						} else {
							dbContent.append(line).append("\n");
						}
					}
				}
				String raw = dbContent.toString();
				if (!raw.isEmpty()) {
					raw = raw.substring(0, raw.length() - 1).replaceAll("\\s+", "");
					try {
						IfPwdCoder decry = new AEScoder();
						String x = decry.decode(raw);
						byte[] b = Base64coder.decodeBase64(x);
						if (b != null) {
							String existingDecoded = CheckingInput.byteArrayToStr(b);
							if (existingDecoded != null && !normalizeForCompare(content).equals(normalizeForCompare(existingDecoded))) {
								throw new TargetMismatchException("目标 DB 文件已存在，且与当前 bak 内容不一致（可能不是同一份数据或密钥不同）。仍要覆盖吗？");
							}
						}
					} catch (CryptoException e) {
						throw new TargetMismatchException("目标 DB 文件已存在，且无法用当前密钥解密（可能不是同一份数据或密钥不同）。仍要覆盖吗？");
					}
				}
			}
		}
		String a = Base64coder.encodeBase64(bytes);
		IfPwdCoder enc = new AEScoder();
		String x = enc.encode(a);
		String w = DB_HEADER + "\n" + x;
		writeToText(w, DB_FILE);
	}

	/** 编码 bak 为 DB（未强制覆盖时，若目标 DB 已存在且内容不一致会抛 TargetMismatchException）。 */
	public static void encodeDB() throws IOException, TargetMismatchException, CryptoException {
		encodeDB(false);
	}
}
