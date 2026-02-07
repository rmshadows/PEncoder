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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import algorithmSettings.AEScoder;
import algorithmSettings.CryptoException;
import iface.IfPwdCoder;

/**
 * 将 bak 密码表导出为 CSV 文件（可选明文或密文密码列）。
 *
 * @author jessie
 */
public final class ExportAsXlsFile {

	private static final String BAK_PATH = "." + File.separator + "PEncoderDatabasebak";
	private static final String CSV_NAME = "ExportFile.csv";

	private ExportAsXlsFile() {
		// 工具类
	}

	/**
	 * 导出 CSV：从 bak 文件读取，按行解析为 4 列（平台、账户、密码、备注）。
	 *
	 * @param isClearText true 则密码列解密为明文，false 则保留密文
	 */
	public static void createCSV(boolean isClearText) {
		List<String> platforms = new ArrayList<>();
		List<String> accounts = new ArrayList<>();
		List<String> passwords = new ArrayList<>();
		List<String> remarks = new ArrayList<>();
		File bak = new File(BAK_PATH);
		if (!bak.exists()) {
			System.out.println("文件不存在");
			return;
		}
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(BAK_PATH), StandardCharsets.UTF_8))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parsed = ReadPEncoderDB.parseBakLine(line);
				if (parsed == null) continue;
				platforms.add(parsed[0]);
				accounts.add(parsed[1]);
				if (isClearText) {
					IfPwdCoder ipc = new AEScoder();
					try {
						String plain = ipc.decode(parsed[2]);
						plain = plain != null ? plain.replaceAll(";", "") : parsed[2];
						passwords.add(plain);
					} catch (CryptoException ex) {
						System.out.println("解密行失败，跳过: " + ex.getMessage());
						passwords.add(parsed[2]);
					}
				} else {
					passwords.add(parsed[2]);
				}
				remarks.add(parsed[3]);
			}
		} catch (Exception e1) {
			System.out.println("ERROR. Check format!");
			return;
		}
		List<Object> headList = Arrays.asList("平台名称", "账户名称", "用户密码", "信息备注");
		List<List<Object>> dataList = new ArrayList<>();
		for (int i = 0; i < platforms.size(); i++) {
			dataList.add(Arrays.asList(platforms.get(i), accounts.get(i), passwords.get(i), remarks.get(i)));
		}
		File csvFile = new File("." + File.separator + CSV_NAME);
		File parent = csvFile.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
		String charset = File.separator.equals("\\") ? "GBK" : StandardCharsets.UTF_8.name();
		try {
			csvFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		try (BufferedWriter csvWriter = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(csvFile), charset), 1024)) {
			int num = headList.size() / 2;
			StringBuilder buffer = new StringBuilder();
			buffer.append(" ,".repeat(Math.max(0, num)));
			csvWriter.write(buffer + CSV_NAME + buffer);
			csvWriter.newLine();
			writeRow(headList, csvWriter);
			for (List<Object> row : dataList) {
				writeRow(row, csvWriter);
			}
			csvWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
		for (Object data : row) {
			csvWriter.write("\"" + data + "\",");
		}
		csvWriter.newLine();
	}
}
