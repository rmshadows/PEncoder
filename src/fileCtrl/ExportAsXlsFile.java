package fileCtrl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import algorithmSettings.AEScoder;
import iface.IfPwdCoder;

/**
 * 类名：导出CSV文件
 * @author Jessie
 */
public class ExportAsXlsFile {

	/**
	 * 创建CSV文件
	 * 创建一个CSV文件
	 * @param isClearText boolean 是否为明文密码
	 */
	public static void createCSV(boolean isClearText) {
		//读取文件
		String fs = File.separator;
		List<String> a = new ArrayList<>();
		List<String> b = new ArrayList<>();
		List<String> c = new ArrayList<>();
		List<String> d = new ArrayList<>();
		String lineTxt = null;
		String bakSep = ":";
		String bakpath = "." + fs + "PEncoderDatabasebak";
		File bak = new File(bakpath);
		try {
			if (!bak.exists()) {
				System.out.println("文件不存在");
			 }
			 else {
				InputStreamReader read = new InputStreamReader(new FileInputStream(bakpath),"UTF-8");//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				while((lineTxt = bufferedReader.readLine()) != null){
					String[] split = lineTxt.split(bakSep);
					a.add(split[0]);
					b.add(split[1]);
					if(isClearText) {
						IfPwdCoder ipc = new AEScoder();
						String x = ipc.decoder(split[2]);
//						System.out.println(x);
						x = x.replaceAll(";", "");
//						System.out.println(x);
						c.add(x);
					}
					else if (!isClearText) {
						c.add(split[2]);
					}
					d.add(split[3]);
//					System.out.println(split[3]);
				}
				bufferedReader.close();
			}
		} catch (Exception e1) {
			System.out.println("ERROR.Check format!");
		}
		// 表格头
		Object[] head = { "平台名称", "账户名称", "用户密码","信息备注" };
		List<Object> headList = Arrays.asList(head);
		//数据
		List<List<Object>> dataList = new ArrayList<List<Object>>();
		List<Object> rowList = null;
		for (int i = 0; i < a.size(); i++) {
			rowList = new ArrayList<Object>();
			rowList.add(a.get(i));
			rowList.add(b.get(i));
			rowList.add(c.get(i));
			rowList.add(d.get(i));
//			rowList.add(new Date());
			dataList.add(rowList);
		}
		String fileName = "ExportFile.csv";//文件名称
		String filePath = "."+fs; //文件路径
		
		File csvFile = null;
		BufferedWriter csvWtriter = null;
		try {
			csvFile = new File(filePath + fileName);
			File parent = csvFile.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			csvFile.createNewFile();
			if (fs.equals("\\")) {//Windows下用GB2312.GBK可以兼容GB2312
				// GB2312使正确读取分隔符","
				try {
					csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
				}catch (Exception egbk) {
					System.out.println("GBK出错，转UTF-8");
					csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);
				}
			}
			else if (fs.equals("/")){
				// UTF-8使正确读取分隔符","
				csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "UTF-8"), 1024);
			}	
			//文件下载，使用如下代码
//			response.setContentType("application/csv;charset=gb18030");
//			response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
//			ServletOutputStream out = response.getOutputStream();
//			csvWtriter = new BufferedWriter(new OutputStreamWriter(out, "GB2312"), 1024);
			int num = headList.size() / 2;
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < num; i++) {
				buffer.append(" ,");
			}
			csvWtriter.write(buffer.toString() + fileName + buffer.toString());
			csvWtriter.newLine();
			// 写入文件头部
			writeRow(headList, csvWtriter);
			// 写入文件内容
			for (List<Object> row : dataList) {
				writeRow(row, csvWtriter);
			}
			csvWtriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				csvWtriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 逐行CSV写入方法
	 * 写入CSV文件
	 * @param row 每一行
	 * @param csvWriter CSV写入
	 * @throws IOException 忽略异常
	 */
	private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
		for (Object data : row) {
			StringBuffer sb = new StringBuffer();
			String rowStr = sb.append("\"").append(data).append("\",").toString();
			csvWriter.write(rowStr);
		}
		csvWriter.newLine();
	}
}
