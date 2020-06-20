package appCtrl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import algorithmSettings.AEScoder;
import fileCtrl.CheckingInput;
import fileCtrl.ExportAsXlsFile;
import fileCtrl.ReadPEncoderDB;
import iface.IfPwdCoder;

/**
 * Description:程序主要控制器
 * Program Name:PEncoder-v2.0
 * Date:2020-06
 * @author Jessie justaaaa@163.com
 * @version 2.0
 */
public class MainProgram {
	// 创建及设置窗口
	private JFrame mainWindow = new JFrame("PEncoder2.0 -- by Jessie");// 标题
	// 界面按钮
	private JButton run = new JButton("运行");
	private JButton copy = new JButton("复制");
	private JButton edit = new JButton("编辑");

//	菜单栏
	private JMenuBar menubar = new JMenuBar();
	private JMenu options = new JMenu("选项");
	private JMenu file = new JMenu("文件");
	private JMenu help = new JMenu("帮助");
	private JMenuItem popM = new JMenuItem("What do you want?");
//	右击菜单
	private JPopupMenu pop = new JPopupMenu();
//	菜单项目
	private JMenuItem changeKeys = new JMenuItem("更换密钥");
	private JMenuItem top = new JMenuItem("窗口置顶/取消置顶");
	private JMenuItem manual = new JMenuItem("如何使用");
	private JMenuItem about = new JMenuItem("关于");
	private JMenuItem export = new JMenuItem("导出");
	private JMenuItem exit = new JMenuItem("退出");
	private JMenuItem EN = new JMenuItem("编码DBbak文件");
	private JMenuItem DE = new JMenuItem("解码DB文件");
	private JMenuItem newfile = new JMenuItem("新建DBbak文件");

//	定义单选按钮，初始处于加密
	private JRadioButton encoderButton = new JRadioButton("加密", true);
	private JRadioButton decoderButton = new JRadioButton("解密", false);
	private JRadioButton isPwdInCleartext = new JRadioButton("隐藏", false);
//	按钮组合
	private ButtonGroup modeSelect = new ButtonGroup();

//	定义一个38列的单行文本域
	public static JPasswordField keyA = new JPasswordField();// 密钥
	public static JPasswordField keyB = new JPasswordField();// vi偏移量
	private JTextArea inputArea = new JTextArea(4, 38);
	private JTextArea outputArea = new JTextArea(4, 38);
	private JTextArea console = new JTextArea(10, 38);

	private static final String INFO_OUTPUTAREA = "———————运行结果将显示在这里———————";
	private static char defaultChar = '●';
	public static boolean isItSemicolon = false;
	private static String cKeyInput = null;

	/**
	 * 打开密码文件
	 * 用于编辑按钮的打开文件，Windows下用记事本（1903版本以上）。Linux下用Gedit
	 * @throws InterruptedException 忽略异常
	 */
	private static void openDBFile() throws InterruptedException {
		try {
			String fileSeparator = File.separator;
			File notepad = new File("C:\\WINDOWS\\system32\\notepad.exe");
			if (notepad.exists() || fileSeparator == "\\") {
				Runtime.getRuntime().exec("C:\\WINDOWS\\system32\\notepad.exe .\\PEncoderDatabasebak");
			} else {
				Runtime.getRuntime().exec("gedit ./PEncoderDatabasebak");// .waitFor()
			}
		} catch (IOException e1) {
			System.out.println("打开失败！请检查DB文件或TXT[Windows]、Gedit[Linux]路径。");
			e1.printStackTrace();
		}
	}

	/**
	 * 复制到剪辑版
	 * 复制加密、解密内容到剪辑版上
	 * @param text 要复制的文字
	 */
	private static void copyToClickboard(String text) {
		String ret = "";
		Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
		// 获取剪切板中的内容
		Transferable clipTf = sysClip.getContents(null);
		if (clipTf != null) {
			// 检查内容是否是文本类型
			if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
				try {
					ret = (String) clipTf.getTransferData(DataFlavor.stringFlavor);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (ret.equals(text)) {

		} else {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			// 封装文本内容
			Transferable trans = new StringSelection(text);
			// 把文本内容设置到系统剪贴板
			clipboard.setContents(trans, null);
		}
		System.out.println("已复制到剪辑板。");
	}

	/**
	 * 控制台信息
	 * 显示程序运行日志
	 */
	private void outputUI() {
		// 捕获控制台输出到GUI界面上
		OutputStream textAreaStream = new OutputStream() {
			public void write(int b) throws IOException {
				console.append(String.valueOf((char) b));
			}
			public void write(byte b[]) throws IOException {
				console.append(new String(b));
			}
			public void write(byte b[], int off, int len) throws IOException {
				console.append(new String(b, off, len));
			}
		};
		PrintStream myOut = new PrintStream(textAreaStream);
		System.setOut(myOut);
//		dbc.setBounds(1ut);
		System.setErr(myOut);
	}

	/**
	 * 计时器进度条
	 * 无用功能，不要在意
	 */
	private class TimePro {
		Timer timer;
		
		void init() {
			final SimulatedActivity target = new SimulatedActivity(100);
			// 以启动一条线程的方式来执行一个耗时的任务
			final Thread targetThread = new Thread(target);
			targetThread.start();
			final ProgressMonitor dialog = new ProgressMonitor(null, "等待任务完成", "已完成：", 0, target.getAmount());
			timer = new Timer(300, e -> {
				// 以任务的当前完成量设置进度对话框的完成比例
				dialog.setProgress(target.getCurrent());
				// 如果用户单击了进度对话框的"取消"按钮
				try {
					if (dialog.isCanceled()) {
						// 停止计时器
						timer.stop();
						// 中断任务的执行线程
						targetThread.interrupt();// ①
						for (int i = 50; i > 0; i--) {
							for (int j = 0; j < i; j++) {
								System.out.print("?");
							}
							System.out.println();
						}
						throw new Exception("踢出");
						// 系统退出
//						System.exit(0);
					}
				} catch (Exception e1) {
					System.out.println("提前结束。");
				}
			});
			timer.start();
		}
	}

	/**
	 * 更换密钥功能
	 * 用于更换密钥
	 */
	private static void ChangingKey() {
		String[] key = new String[2];
		List<String> a = new ArrayList<>();
		List<String> b = new ArrayList<>();
		List<String> c = new ArrayList<>();
		List<String> d = new ArrayList<>();
		String readResult = "";
		String lineTxt = null;
		String bakSep = ":";
		String fs = File.separator;
		String bakpath = "." + fs + "PEncoderDatabasebak";
		try {
			String[] keySplit = cKeyInput.split("/");
			if (!CheckingInput.inputFilter(cKeyInput, false)) {
				System.out.println("含有非法字符！");
				throw new Exception("非法字符");
			} else {
				key[0] = keySplit[0];
				key[1] = keySplit[1];
			}
		} catch (Exception e1) {
			System.out.println("请检查输入的新密钥合法性！");
		}
		File bak = new File(bakpath);
		try {
			if (!bak.exists()) {
				System.out.println("文件不存在");
			} else {
				System.out.println("为以防万一，原bak文件不会被覆盖，请手动删除谢谢。");
				InputStreamReader read = new InputStreamReader(new FileInputStream(bakpath), "UTF-8");// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				while ((lineTxt = bufferedReader.readLine()) != null) {
					String[] split = lineTxt.split(bakSep);
					a.add(split[0]);
					b.add(split[1]);
					c.add(split[2]);
					d.add(split[3]);
				}
//				for (String string : a) {
//					System.out.println(string);
//				}
//				for (String string : b) {
//					System.out.println(string);
//				}
//				for (String string : c) {
//					System.out.println(string);
//				}
//				for (String string : d) {
//					System.out.println(string);
//				}
				for (int i = 0; i < c.size(); i++) {
					IfPwdCoder ipc = new AEScoder();
					String x = ipc.decoder(c.get(i));// 解密
					x = AEScoder.ckeyEncode(x, key[0], key[1]);// 再加密
					c.set(i, new String(x));
				}
				for (int i = 0; i < a.size(); i++) {
					String aa = a.get(i);
					String bb = b.get(i);
					String cc = c.get(i);
					String dd = d.get(i);
//					System.out.println(aa);
					readResult = readResult + aa + ":" + bb + ":" + cc + ":" + dd + "\n";
				}
//				System.out.println(readResult);
				ReadPEncoderDB.writeToText(readResult, bakpath + "NEW");
				bufferedReader.close();
			}
		} catch (Exception e1) {
			System.out.println("Quit.");
		}
	}

//	-----------------用于执行界面初始化的init方法---------------------
	/**
	 * 界面初始化
	 * 主程序界面初始化
	 */
	@SuppressWarnings("serial")
	public void mainWindowInit() {
		// 设置字体
		Font f = new Font("宋体", Font.PLAIN, 19);
//		Font p = new Font("宋体", Font.PLAIN, 12);

		pop.add(popM); // 右键菜单

//		设置窗口大小
		mainWindow.setFont(f);
		String fs = File.separator;
		if (fs.equals("\\")) {
			mainWindow.setSize(415, 615);
		} else if (fs.equals("/")) {
			mainWindow.setSize(400, 600);
		}
//		mainWindow.setSize(400,600);
		mainWindow.setLayout(null);
		mainWindow.setResizable(false);

//		创建JPanel
		JPanel workPanel = new JPanel();
		workPanel.setLayout(null);
		JScrollPane consolePanel = new JScrollPane(console);
		workPanel.setFont(f);
		consolePanel.setFont(f);
		workPanel.setBounds(0, 0, 400, 340);
		consolePanel.setBounds(0, 340, 400, 200);
		workPanel.setBorder(BorderFactory.createTitledBorder("工作区"));
		consolePanel.setBorder(BorderFactory.createTitledBorder("日志"));
//		workPanel.setFont(p);
//		consolePanel.setFont(p);
		mainWindow.add(workPanel);
		mainWindow.add(consolePanel);

//		菜单添加到菜单条中
		menubar.add(file);
		file.setFont(f);
		file.add(newfile);
		newfile.setFont(f);
		DE.setFont(f);
		EN.setFont(f);
		file.add(EN);
		file.add(DE);
		file.add(exit);
		menubar.add(options);
		menubar.setFont(f);// 设置JLabel的字体
		options.setFont(f);
		options.add(export);
		options.add(top);
		top.setFont(f);
		export.setFont(f);
		options.add(changeKeys);
		changeKeys.setFont(f);
		exit.setFont(f);
		menubar.add(help);
		help.setFont(f);
		help.add(manual);
		manual.setFont(f);
		help.add(about);
		about.setFont(f);
		mainWindow.setJMenuBar(menubar);// 为f窗口设置菜单条

//		设置工作区--------------------------------------------------------------
		JLabel keyALabel = new JLabel("Key-A:");
		keyALabel.setFont(f);
		JLabel keyBLabel = new JLabel("Key-B:");
		keyBLabel.setFont(f);
		JLabel inputLabel = new JLabel("输 入:");
		inputLabel.setFont(f);
		JLabel outputLabel = new JLabel("输 出:");
		outputLabel.setFont(f);
		keyA.setFont(f);
		keyB.setFont(f);

		keyALabel.setBounds(8, 5, 60, 50);// 标签A
		keyBLabel.setBounds(8, 41, 60, 50);// 标签B
		keyA.setBounds(70, 19, 321, 25);// 密钥A输入框
		keyA.setEchoChar((char) 0);// 设置默认显示明文密钥
		keyB.setEchoChar((char) 0);// 设置默认显示明文密钥
//		keyA.setText(INFO_KeyA);
		keyA.setForeground(Color.LIGHT_GRAY);
//		keyB.setText(INFO_KeyB);
		keyB.setForeground(Color.LIGHT_GRAY);
		keyA.setHorizontalAlignment(JTextField.CENTER);// key居中
		keyB.setHorizontalAlignment(JTextField.CENTER);// key居中
		keyB.setBounds(70, 55, 321, 25);// 密钥B输入框
		inputLabel.setBounds(8, 85, 75, 30);// 输入 位置
//		inputLabel.setBorder(BorderFactory.createLineBorder(Color.red, 3));//调试用
		outputLabel.setBounds(8, 181, 75, 30);// 输出 位置
//		outputLabel.setBorder(BorderFactory.createLineBorder(Color.red, 3));//调试用
		inputArea.setBounds(70, 88, 319, 85);// 结果输入
		outputArea.setBounds(70, 185, 319, 88);// 结果输出
		outputArea.setEditable(false);// 输出面板不准编辑
		outputArea.setText(INFO_OUTPUTAREA);
		outputArea.setForeground(Color.LIGHT_GRAY);
		// 下面保证两个按钮只激活一个
		modeSelect.add(encoderButton);
		modeSelect.add(decoderButton);
		encoderButton.setSelected(true);

		if (fs.equals("\\")) {
			encoderButton.setBounds(8, 274, 55, 20);
			decoderButton.setBounds(8, 296, 55, 15);
			isPwdInCleartext.setBounds(8, 311, 55, 20);
		} else if (fs.equals("/")) {
			encoderButton.setBounds(8, 274, 50, 20);
			decoderButton.setBounds(8, 296, 50, 15);
			isPwdInCleartext.setBounds(8, 311, 50, 20);
		}

		edit.setBounds(70, 288, 75, 35);
//		ebc.setBounds(100,300, 40, 35);
//		dbc.setBounds(100,335, 40, 35);
		edit.setFont(f);
		copy.setBounds(220, 288, 80, 35);
		copy.setFont(f);
		run.setBounds(310, 288, 80, 35);
		run.setFont(f);
		workPanel.add(keyALabel);
		workPanel.add(keyBLabel);
		workPanel.add(keyA);
		workPanel.add(keyB);
		workPanel.add(inputLabel);
		workPanel.add(outputLabel);
		workPanel.add(inputArea);
		workPanel.add(outputArea);
		workPanel.add(encoderButton);
		workPanel.add(decoderButton);
		workPanel.add(isPwdInCleartext);
		workPanel.add(edit);
		workPanel.add(copy);
		workPanel.add(run);
		inputArea.setLineWrap(true); // 自动换行
		inputArea.setWrapStyleWord(true); // 单词完整保留
		outputArea.setLineWrap(true); // 自动换行
		outputArea.setWrapStyleWord(true); // 单词完整保留

//		设置日志区---------------------------------------------------------------
//		console.setFont(f);//设置字体
		console.setLineWrap(true); // 自动换行
		console.setWrapStyleWord(true); // 单词完整保留
		console.setText("Welcome!\n");
		console.setEditable(false);
		outputUI();

//		设置事件-----------------------------------------------------------------
//		按钮事件=================================================================
		edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					openDBFile();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClickboard(outputArea.getText().toString());
			}
		});
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					outputArea.setText(null);
					if (encoderButton.isSelected()) {
						String a = CheckingInput.pwdAppend(inputArea.getText());
						IfPwdCoder encoding = new AEScoder();
						String x = encoding.encode(a);
						if (isItSemicolon) {
							outputArea.setText(null);
						} else {
							outputArea.setText(x);
						}
//						outputArea.setText(encoding.encode(a));
						System.out.println("——加密完成——");
					} else if (decoderButton.isSelected()) {
						String b = inputArea.getText();
						IfPwdCoder decoding = new AEScoder();
						String x = decoding.decoder(b);
						x = x.replaceAll(";", "");
						if(x.contains("；")) {
							x = x.replaceAll("；", ";");
						}
						outputArea.setText(x);
						System.out.println("——解密完成——");
					} else {
						System.out.println("运行出错。");
					}
				} catch (Exception e3) {
					// TODO: handle exception
				}
			}
		});
		isPwdInCleartext.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (isPwdInCleartext.isSelected()) {
					keyA.setEchoChar(defaultChar);
					keyB.setEchoChar(defaultChar);
				} else if (!isPwdInCleartext.isSelected()) {
					keyA.setEchoChar((char) 0);
					keyB.setEchoChar((char) 0);
				} else {
					System.out.println("Radio button error.");
				}
//				System.out.println(isPwdInCleartext.isSelected());
			}
		});

//		菜单事件
		outputArea.setComponentPopupMenu(pop);
		popM.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new TimePro().init();
			}
		});

		newfile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("正在新建bak文件……");
					ReadPEncoderDB.newDBbakFile();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		EN.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("正在编码bak文件……");
					ReadPEncoderDB.encodeDB();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
			}
		});
		DE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("正在解码DB文件……");
					ReadPEncoderDB.decodeDB();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
//					e1.printStackTrace();
				}
			}
		});
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean t = mainWindow.isAlwaysOnTop();
				if(t) {
					mainWindow.setAlwaysOnTop(false);
				}
				int a = JOptionPane.showConfirmDialog(null, "确认退出?", "WARNING", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE);
//				System.out.println(a);
				if (a == 0) {
					System.exit(0);
				} else {
					System.out.println("返回主界面");
				}
				if(t) {
					mainWindow.setAlwaysOnTop(true);
				}
			}
		});
		
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean t = mainWindow.isAlwaysOnTop();
				if(t) {
					mainWindow.setAlwaysOnTop(false);
				}
				Object[] possibleValues = { "密码密文导出", "密码明文导出" };
				try {
					Object selectedValue = JOptionPane.showInputDialog(null, "导出前请检查bak文件。", "以何种方式导出？",
							JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
					if (selectedValue.equals(possibleValues[0])) {
						ExportAsXlsFile.createCSV(false);
//						System.out.println(selectedValue.equals(possibleValues[0]));
					} else if (selectedValue.equals(possibleValues[1])) {
						ExportAsXlsFile.createCSV(true);
					} else {
						System.out.println("ERROR!");
						return;
					}
				} catch (Exception e1) {
//					e1.printStackTrace();
					System.out.println("CANCEL");
				}
				finally{
					if(t) {
						mainWindow.setAlwaysOnTop(true);
					}
				}
			}
		});
		top.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!mainWindow.isAlwaysOnTop()) {
					mainWindow.setAlwaysOnTop(true);
				}
				else {
					mainWindow.setAlwaysOnTop(false);
				}
			}
		});
		changeKeys.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean t = mainWindow.isAlwaysOnTop();
				if(t) {
					mainWindow.setAlwaysOnTop(false);
				}
				cKeyInput = JOptionPane.showInputDialog("请输入KeyA和KeyB，用“/”隔开。e.g.:1234/4321");
				ChangingKey();
				if(t) {
					mainWindow.setAlwaysOnTop(true);
				}
//				System.out.println(cKeyInput);
			}
		});
		
		manual.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean t = mainWindow.isAlwaysOnTop();
				if(t) {
					mainWindow.setAlwaysOnTop(false);
				}
				String a = "具体使用方法请查阅附带的README.md文件\n按键说明：\n新建：新建bak文件。      编码：将bak文件转DB文件\n解码：将DB文件转bak文件  退出：退出程序\n导出：导出Excel表格      改密：修改加密密钥\n帮助：本对话框           关于：部分信息";
				JOptionPane.showMessageDialog(null, a, "帮助", JOptionPane.INFORMATION_MESSAGE);
				if(t) {
					mainWindow.setAlwaysOnTop(true);
				}
			}
		});
		about.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean t = mainWindow.isAlwaysOnTop();
				if(t) {
					mainWindow.setAlwaysOnTop(false);
				}
				String a = "名称：PEncoder密碼加密器v2.0\n作者：Ryan\n日期：2020-06-19";
				JOptionPane.showMessageDialog(null, a, "关于", JOptionPane.INFORMATION_MESSAGE);
				if(t) {
					mainWindow.setAlwaysOnTop(true);
				}
			}
		});

//		设置文本框事件=============================================================
		keyA.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				String text = String.copyValueOf(keyA.getPassword());
				if (text.length() + str.length() > 16) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "最长仅支持16位密钥！", "你在搞什么！", JOptionPane.ERROR_MESSAGE);
					return;
				}
				super.insertString(offs, str, a);
			}
		});
		keyB.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				String text = String.copyValueOf(keyB.getPassword());
				if (text.length() + str.length() > 16) {
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(null, "最长仅支持16位密钥！", "请注意你的行为", JOptionPane.ERROR_MESSAGE);
					return;
				}
				super.insertString(offs, str, a);
			}
		});
		inputArea.setDocument(new PlainDocument() {
			@Override
			public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
				if (encoderButton.isSelected()) {
					String text = String.valueOf(inputArea.getText());
					if (text.length() + str.length() > 30) {
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "最长仅支持30位明文密码！", "提示", JOptionPane.ERROR_MESSAGE);
						return;
					}
					super.insertString(offs, str, a);
				} else {
					String text = String.valueOf(inputArea.getText());
					if (text.length() + str.length() > 300) {
						Toolkit.getDefaultToolkit().beep();
						JOptionPane.showMessageDialog(null, "？？？", "你在搞什么！", JOptionPane.ERROR_MESSAGE);
						return;
					}
					super.insertString(offs, str, a);
				}
			}
		});

//		设置关闭窗口时，退出程序
		mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainWindow.setVisible(true);
		mainWindow.setLocationRelativeTo(null);//居中显示

		/**
		 * Set the Nimbus look and feel
		 */
		if (fs.equals("\\")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			
		}
	}
}
