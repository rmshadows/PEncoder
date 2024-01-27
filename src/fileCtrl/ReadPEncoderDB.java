package fileCtrl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import algorithmSettings.AEScoder;
import algorithmSettings.Base64coder;
import iface.IfPwdCoder;

/**
 * 类名：读取密码文件
 * 
 * @author jessie
 */
public class ReadPEncoderDB {

	/**
	 * 文件写入
	 * 生成文本文件
	 * @param TextIn String 待写入的文本
	 * @param fileName String  文件名
	 * @throws IOException 忽略抛出异常
	 */
	public static void writeToText(String TextIn, String fileName) throws IOException {
		// 生成的文件路径
		String fileSeparator = File.separator;
		String path = "." + fileSeparator + fileName;
		File file = new File(path);
		// 覆盖
		if (file.exists()) {
			file.delete();
			file.createNewFile();
		} else {
			file.createNewFile();
		}
		// write 解决中文乱码问题
		// FileWriter fw = new FileWriter(file, true);
		OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(TextIn);
		bw.flush();
		bw.close();
		fw.close();
	}

	/**
	 * 新建密码文件
	 * 新建密碼文件，第一次使用
	 * @throws IOException 忽略抛出异常
	 */
	public static void newDBbakFile() throws IOException {
		String fileSeparator = File.separator;
		String dbFilePath = "." + fileSeparator + "PEncoderDatabasebak";
//		System.out.println(dbFilePath);
		File dbFile = new File(dbFilePath);
//		System.out.println(fileSeparator);
		if (dbFile.exists()) {
			System.out.println("bak文件存在!拒绝新建。");
		} else {
			try {
				writeToText("使用前请删除此行，注意英文冒号的使用位置！格式示例：\n软件平台:账号名:密码:备注", "PEncoderDatabasebak");
				System.out.println("已新建bak文件。");
			} catch (Exception e) {
				System.out.println("新建bak文件失败。");
			}
		}
	}
	
	/**
	 * 解码密码文件
	 * 解碼密碼文件
	 * @throws IOException 忽略抛出异常
	 */
	public static void decodeDB() throws IOException {
		String fileSeparator = File.separator;
		String dbFilePath = "." + fileSeparator + "PEncoderDatabase";
//		System.out.println(dbFilePath);
		File dbFile = new File(dbFilePath);
		String readResult = null;
//		System.out.println(fileSeparator);
		if (dbFile.exists()) {
			System.out.println("DB文件存在，开始读取……");
		} else {
			System.out.println("文件不存在或出错！请将PEncoderDatabase文件放置于当前路径并检查权限。");
		}
		String encoding = "UTF-8";
		InputStreamReader read = new InputStreamReader(new FileInputStream(dbFilePath), encoding);// 考虑到编码格式
		try (read) {
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			String zh = "闽:::";
			while ((lineTxt = bufferedReader.readLine()) != null) {
				if (lineTxt.contentEquals(zh)) {
//					System.out.println(lineTxt.contentEquals(zh));
					readResult = "";
				} else {
					readResult = readResult + lineTxt + "\n";
//					System.out.println(zh.contentEquals(String.valueOf(lineTxt)));
//					System.out.println(zh.contentEquals(lineTxt));
//					System.out.println(lineTxt);
				}
			}
		} catch (Exception e) {
			System.out.println("文件读取出错!请注意格式及编码。");
		} finally {
			//			System.out.println(readResult);
			System.out.println("读取DB文件完毕。");
		}
		readResult = readResult.substring(0, readResult.length() - 1);
//		System.out.println(readResult);
		IfPwdCoder decry = new AEScoder();
		String x = decry.decoder(readResult);
		byte[] b = Base64coder.decryptBASE64(x);
		String c = CheckingInput.byteArrayToStr(b);
		c = c.substring(4, c.length());
//		System.out.println(c);
		writeToText(c, "PEncoderDatabasebak");
	}
	
	/**
	 * 编码密码文件
	 * 編碼密碼文件
	 * @throws IOException 忽略抛出异常
	 */
	public static void encodeDB() throws IOException {
		String fileSeparator = File.separator;
		String dbFilePath = "." + fileSeparator + "PEncoderDatabasebak";
//		System.out.println(dbFilePath);
		File dbFile = new File(dbFilePath);
		String readResult = null;
//		System.out.println(fileSeparator);
		if (dbFile.exists()) {
			System.out.println("bak文件存在，开始读取……");
		} else {
			System.out.println("文件不存在或出错！请将PEncoderDatabasebak文件放置于当前路径并检查权限。");
		}
		String encoding = "UTF-8";
		InputStreamReader read = new InputStreamReader(new FileInputStream(dbFilePath), encoding);// 考虑到编码格式
		try (read) {
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				readResult = readResult + lineTxt + "\n";
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("文件读取出错!请注意格式及编码。");
		} finally {
			System.out.println("读取bak文件完毕。");
		}
		String zh = "闽:::\n";
		String a = Base64coder.encryptBASE64(CheckingInput.stringToByteArray(readResult));
		IfPwdCoder cry = new AEScoder();
		String x = cry.encode(a);
		String w = zh + x;
//		System.out.println(w);
		writeToText(w, "PEncoderDatabase");
	}
}
