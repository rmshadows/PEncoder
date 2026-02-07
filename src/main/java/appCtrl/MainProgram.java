package appCtrl;

import algorithmSettings.AEScoder;
import algorithmSettings.CryptoException;
import fileCtrl.CheckingInput;
import fileCtrl.ExportAsXlsFile;
import fileCtrl.ReadPEncoderDB;
import iface.IfPwdCoder;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.KeyStroke;

import static java.lang.String.format;

/**
 * Description:程序主要控制器
 * Program Name:PEncoder
 * Date:2026-02-07
 * @author Jessie justaaaa@163.com
 * @version 2.3
 */
public class MainProgram {
	public final static String VERSION = "2.3";
	public final static String UPDATE = "2026-02-07";

	private static Locale currentLocale = Locale.getDefault().getLanguage().startsWith("zh") ? Locale.SIMPLIFIED_CHINESE : Locale.ENGLISH;
	private static ResourceBundle bundle;

	/** 在命名模块中不能用 Control，改为用 UTF-8 手动加载同包下的 properties。 */
	private static ResourceBundle loadBundleForLocale(Locale locale) throws IOException {
		String name = Locale.SIMPLIFIED_CHINESE.equals(locale) ? "Messages_zh_CN.properties" : "Messages_en.properties";
		try (InputStream is = MainProgram.class.getResourceAsStream(name)) {
			if (is == null) {
				throw new IOException("Resource not found: " + name);
			}
			return new java.util.PropertyResourceBundle(new InputStreamReader(is, StandardCharsets.UTF_8));
		}
	}

	private static ResourceBundle getBundle() {
		if (bundle == null) {
			try {
				bundle = loadBundleForLocale(currentLocale);
			} catch (Exception e) {
				throw new RuntimeException("Failed to load resource bundle", e);
			}
		}
		return bundle;
	}

	private static void reloadBundle() {
		try {
			bundle = loadBundleForLocale(currentLocale);
		} catch (Exception e) {
			throw new RuntimeException("Failed to load resource bundle", e);
		}
	}

	private static String msg(String key) {
		try {
			return getBundle().getString(key);
		} catch (Exception e) {
			return key;
		}
	}

	private final JFrame mainWindow = new JFrame();
	private final JButton run = new JButton();
	private final JButton copy = new JButton();
	private final JButton edit = new JButton();

	private final JMenuBar menubar = new JMenuBar();
	private final JMenu options = new JMenu();
	private final JMenu file = new JMenu();
	private final JMenu help = new JMenu();
	private final JMenuItem popM = new JMenuItem();
	private final JPopupMenu pop = new JPopupMenu();
	private final JMenuItem changeKeys = new JMenuItem();
	private final JMenuItem top = new JMenuItem();
	private final JMenuItem manual = new JMenuItem();
	private final JMenuItem about = new JMenuItem();
	private final JMenuItem export = new JMenuItem();
	private final JMenuItem exit = new JMenuItem();
	private final JMenuItem EN = new JMenuItem();
	private final JMenuItem DE = new JMenuItem();
	private final JMenuItem newfile = new JMenuItem();
	private final JMenu langMenu = new JMenu();
	private final JMenuItem langZh = new JMenuItem();
	private final JMenuItem langEn = new JMenuItem();
	private final JCheckBoxMenuItem autoEncodeOnExitItem = new JCheckBoxMenuItem();
	private final JMenuItem chooseBakEditorItem = new JMenuItem();

	private static final String PREF_AUTO_ENCODE_ON_EXIT = "autoEncodeBakToDbOnExit";
	private static final String PREF_BAK_EDITOR = "bakEditorPath";
	private static final String BAK_FILENAME = "PEncoderDatabasebak";
	private static final String DB_FILENAME = "PEncoderDatabase";
	private static final String BACKUP_SUFFIX = ".backup";

	private final JRadioButton encoderButton = new JRadioButton();
	private final JRadioButton decoderButton = new JRadioButton();
	private final JRadioButton isPwdInCleartext = new JRadioButton();
//	按钮组合
	private final ButtonGroup modeSelect = new ButtonGroup();

//	定义一个38列的单行文本域
	public static JPasswordField keyA = new JPasswordField();// 密钥
	public static JPasswordField keyB = new JPasswordField();// vi偏移量
	private final JTextArea inputArea = new JTextArea(4, 38);
	private final JTextArea outputArea = new JTextArea(4, 38);
	private final JTextArea console = new JTextArea(10, 38);

	private static final char defaultChar = '●';
	public static boolean isItSemicolon = false;
	private static String cKeyInput = null;

	/** 根据当前语言更新所有界面文案。 */
	private void updateUITexts() {
		mainWindow.setTitle(format(msg("window.title"), VERSION));
		run.setText(msg("btn.run"));
		copy.setText(msg("btn.copy"));
		edit.setText(msg("btn.edit"));
		file.setText(msg("menu.file"));
		options.setText(msg("menu.options"));
		help.setText(msg("menu.help"));
		newfile.setText(msg("menu.newfile"));
		newfile.setToolTipText(msg("tip.menu.newfile"));
		EN.setText(msg("menu.encode"));
		EN.setToolTipText(msg("tip.menu.encode"));
		DE.setText(msg("menu.decode"));
		DE.setToolTipText(msg("tip.menu.decode"));
		exit.setText(msg("menu.exit"));
		exit.setToolTipText(msg("tip.menu.exit"));
		export.setText(msg("menu.export"));
		export.setToolTipText(msg("tip.menu.export"));
		top.setText(msg("menu.top"));
		top.setToolTipText(msg("tip.menu.top"));
		changeKeys.setText(msg("menu.changeKeys"));
		changeKeys.setToolTipText(msg("tip.menu.changeKeys"));
		manual.setText(msg("menu.manual"));
		manual.setToolTipText(msg("tip.menu.manual"));
		about.setText(msg("menu.about"));
		about.setToolTipText(msg("tip.menu.about"));
		langMenu.setText(msg("menu.lang"));
		langZh.setText(msg("menu.lang_zh"));
		langZh.setToolTipText(msg("tip.menu.lang_zh"));
		langEn.setText(msg("menu.lang_en"));
		langEn.setToolTipText(msg("tip.menu.lang_en"));
		autoEncodeOnExitItem.setText(msg("menu.auto_encode_on_exit"));
		autoEncodeOnExitItem.setToolTipText(msg("tip.menu.auto_encode_on_exit"));
		chooseBakEditorItem.setText(msg("menu.choose_bak_editor"));
		chooseBakEditorItem.setToolTipText(msg("tip.menu.choose_bak_editor"));
		popM.setText(msg("pop.what"));
		encoderButton.setText(msg("radio.encrypt"));
		decoderButton.setText(msg("radio.decrypt"));
		isPwdInCleartext.setText(msg("radio.hide"));
		outputArea.setText(msg("area.output_placeholder"));
		if (workPanel != null) {
			workPanel.setBorder(BorderFactory.createTitledBorder(msg("border.work")));
		}
		if (consolePanel != null) {
			consolePanel.setBorder(BorderFactory.createTitledBorder(msg("border.log")));
		}
		if (keyALabel != null) keyALabel.setText(msg("label.keyA"));
		if (keyBLabel != null) keyBLabel.setText(msg("label.keyB"));
		if (inputLabel != null) inputLabel.setText(msg("label.input"));
		if (outputLabel != null) outputLabel.setText(msg("label.output"));
	}

	private JPanel workPanel;
	private JScrollPane consolePanel;
	private JLabel keyALabel;
	private JLabel keyBLabel;
	private JLabel inputLabel;
	private JLabel outputLabel;

	/** 备份指定文件到同目录下的 文件名.backup，若原文件存在则覆盖备份。 */
	private static void backupFile(String fileName) throws IOException {
		String dir = "." + File.separator;
		File src = new File(dir + fileName);
		if (!src.exists()) {
			return;
		}
		File dest = new File(dir + fileName + BACKUP_SUFFIX);
		Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
		System.out.println(format(msg("dialog.backup_done"), fileName + BACKUP_SUFFIX));
	}

	/** 执行退出流程：若勾选“退出时自动编码”，则先备份再编码，失败时询问是否仍退出。 */
	private void doExit() {
		boolean wasOnTop = mainWindow.isAlwaysOnTop();
		if (wasOnTop) {
			mainWindow.setAlwaysOnTop(false);
		}
		int confirm = JOptionPane.showConfirmDialog(mainWindow, msg("dialog.confirm_exit"), msg("dialog.warning"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.YES_OPTION) {
			if (wasOnTop) mainWindow.setAlwaysOnTop(true);
			System.out.println("返回主界面");
			return;
		}
		if (autoEncodeOnExitItem.isSelected()) {
			try {
				backupFile(BAK_FILENAME);
				backupFile(DB_FILENAME);
				ReadPEncoderDB.encodeDB();
				System.out.println(msg("dialog.exit_encode_ok"));
			} catch (Exception e) {
				String errMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
				int exitAnyway = JOptionPane.showConfirmDialog(mainWindow,
						format(msg("dialog.exit_encode_fail"), errMsg), msg("dialog.error"),
						JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
				if (exitAnyway != JOptionPane.YES_OPTION) {
					if (wasOnTop) mainWindow.setAlwaysOnTop(true);
					return;
				}
			}
		}
		if (wasOnTop) {
			mainWindow.setAlwaysOnTop(false);
		}
		mainWindow.dispose();
		System.exit(0);
	}

	private static boolean getAutoEncodeOnExitPreference() {
		try {
			return Preferences.userNodeForPackage(MainProgram.class).getBoolean(PREF_AUTO_ENCODE_ON_EXIT, false);
		} catch (Exception e) {
			return false;
		}
	}

	private static void setAutoEncodeOnExitPreference(boolean value) {
		try {
			Preferences.userNodeForPackage(MainProgram.class).putBoolean(PREF_AUTO_ENCODE_ON_EXIT, value);
		} catch (Exception ignored) {
		}
	}

	/** 跨平台：检测当前系统可用的编辑器，返回 [显示名, 可执行路径] 列表；首项为“系统默认”（路径为空）。 */
	private static List<String[]> detectAvailableEditors() {
		List<String[]> out = new ArrayList<>();
		String os = System.getProperty("os.name", "").toLowerCase();
		boolean win = os.contains("win");
		boolean mac = os.contains("mac");

		out.add(new String[]{msg("editor.default"), ""});

		if (win) {
			out.add(new String[]{"Notepad", "notepad.exe"});
			String pf = System.getenv("ProgramFiles");
			String pf86 = System.getenv("ProgramFiles(x86)");
			if (pf != null) {
				tryAddPath(out, pf + "\\Notepad++\\notepad++.exe", "Notepad++");
				tryAddPath(out, pf + "\\Microsoft VS Code\\Code.exe", "VS Code");
			}
			if (pf86 != null) {
				tryAddPath(out, pf86 + "\\Notepad++\\notepad++.exe", "Notepad++");
			}
			String local = System.getenv("LOCALAPPDATA");
			if (local != null) {
				tryAddPath(out, local + "\\Programs\\Microsoft VS Code\\Code.exe", "VS Code");
			}
		} else if (mac) {
			tryAddPath(out, "/Applications/TextEdit.app/Contents/MacOS/TextEdit", "TextEdit");
			resolveViaWhich(out, "nano", "nano");
			resolveViaWhich(out, "vim", "Vim");
			resolveViaWhich(out, "code", "VS Code");
		} else {
			// Linux 等
			resolveViaWhich(out, "gedit", "gedit");
			resolveViaWhich(out, "kate", "Kate");
			resolveViaWhich(out, "xed", "xed");
			resolveViaWhich(out, "nano", "nano");
			resolveViaWhich(out, "vim", "Vim");
			resolveViaWhich(out, "code", "VS Code");
		}
		return out;
	}

	private static void tryAddPath(List<String[]> out, String path, String displayName) {
		if (path == null || path.isBlank()) return;
		File f = new File(path);
		if (f.exists() && !containsPath(out, path)) {
			out.add(new String[]{displayName, f.getAbsolutePath()});
		}
	}

	private static boolean containsPath(List<String[]> list, String path) {
		for (String[] pair : list) {
			if (pair.length >= 2 && path.equals(pair[1])) return true;
		}
		return false;
	}

	/** Unix/Linux/macOS：用 which 解析命令路径，若存在则加入列表。 */
	private static void resolveViaWhich(List<String[]> out, String command, String displayName) {
		String path = which(command);
		if (path != null && !path.isBlank() && !containsPath(out, path)) {
			out.add(new String[]{displayName, path});
		}
	}

	private static String which(String command) {
		try {
			ProcessBuilder pb = new ProcessBuilder("which", command);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			String line;
			try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				line = r.readLine();
			}
			int exit = p.waitFor();
			if (exit == 0 && line != null && !line.isBlank()) {
				return line.trim();
			}
		} catch (Exception ignored) {
		}
		return null;
	}

	/** 弹出列表选择编辑器（系统检测到的 + 浏览），保存到 Preferences。 */
	private void chooseBakEditor() {
		List<String[]> editors = detectAvailableEditors();
		String[] labels = new String[editors.size() + 1];
		int i = 0;
		for (String[] pair : editors) {
			labels[i++] = pair[0];
		}
		labels[i] = msg("editor.browse");
		String current = getBakEditorPath();
		int currentIndex = 0;
		for (int j = 0; j < editors.size(); j++) {
			if (editors.get(j).length >= 2 && editors.get(j)[1].equals(current)) {
				currentIndex = j;
				break;
			}
		}
		Object choice = JOptionPane.showInputDialog(mainWindow, msg("dialog.choose_editor_prompt"),
				msg("dialog.choose_editor_title"), JOptionPane.QUESTION_MESSAGE, null, labels, labels[currentIndex]);
		if (choice == null) return;
		String selected = (String) choice;
		if (msg("editor.browse").equals(selected)) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle(msg("dialog.choose_editor_title"));
			if (File.separator.equals("\\")) {
				fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Executable (*.exe)", "exe"));
			}
			if (fc.showOpenDialog(mainWindow) == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null) {
				String path = fc.getSelectedFile().getAbsolutePath();
				setBakEditorPath(path);
				JOptionPane.showMessageDialog(mainWindow, format(msg("dialog.editor_saved"), path), msg("dialog.warning"), JOptionPane.INFORMATION_MESSAGE);
			}
			return;
		}
		for (String[] pair : editors) {
			if (pair[0].equals(selected)) {
				setBakEditorPath(pair.length >= 2 ? pair[1] : "");
				String show = pair.length >= 2 && !pair[1].isBlank() ? pair[1] : msg("editor.default");
				JOptionPane.showMessageDialog(mainWindow, format(msg("dialog.editor_saved"), show), msg("dialog.warning"), JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
	}

	/** 解码 DB 为 bak（菜单或 Ctrl+D 触发）。 */
	private void runDecodeDB() {
		try {
			System.out.println("正在解码DB文件……");
			ReadPEncoderDB.decodeDB();
		} catch (CryptoException e1) {
			System.out.println("解码失败: " + e1.getMessage());
			JOptionPane.showMessageDialog(mainWindow, "解码失败: " + e1.getMessage(), msg("dialog.error"), JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			System.out.println("文件操作失败: " + e1.getMessage());
			JOptionPane.showMessageDialog(mainWindow, "文件操作失败: " + e1.getMessage(), msg("dialog.error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/** 读取用户设置的 bak 编辑器路径，空表示使用系统默认。 */
	private static String getBakEditorPath() {
		try {
			String p = Preferences.userNodeForPackage(MainProgram.class).get(PREF_BAK_EDITOR, "");
			return (p == null || p.isBlank()) ? "" : p.trim();
		} catch (Exception e) {
			return "";
		}
	}

	/** 保存用户设置的 bak 编辑器路径。 */
	private static void setBakEditorPath(String path) {
		try {
			Preferences.userNodeForPackage(MainProgram.class).put(PREF_BAK_EDITOR, path == null ? "" : path.trim());
		} catch (Exception ignored) {
		}
	}

	/**
	 * 打开 bak 文件：若用户已设置编辑器则用该程序打开，否则按平台使用默认（Windows 记事本 / Linux gedit / macOS TextEdit）。
	 */
	private void openDBFile() {
		String path = "." + File.separator + BAK_FILENAME;
		String editor = getBakEditorPath();
		try {
			if (editor != null && !editor.isBlank()) {
				Runtime.getRuntime().exec(new String[]{editor, path});
			} else {
				String os = System.getProperty("os.name", "").toLowerCase();
				if (os.contains("win")) {
					Runtime.getRuntime().exec(new String[]{"notepad.exe", path});
				} else if (os.contains("mac")) {
					Runtime.getRuntime().exec(new String[]{"open", "-e", path});
				} else {
					Runtime.getRuntime().exec(new String[]{"gedit", path});
				}
			}
		} catch (IOException e1) {
			System.out.println("打开失败！请检查 bak 文件是否存在，或在选项中指定可用编辑器。");
			e1.printStackTrace();
		}
	}

	/**
	 * 将文本复制到系统剪贴板。
	 *
	 * @param text 要复制的文字
	 */
	private static void copyToClipboard(String text) {
		if (text == null) {
			return;
		}
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), null);
		System.out.println("已复制到剪贴板。");
	}

	/**
	 * 控制台信息
	 * 显示程序运行日志
	 */
	/** 将 System.out/err 重定向到日志区，便于在界面查看。 */
	private void outputUI() {
		OutputStream textAreaStream = new OutputStream() {
			@Override
			public void write(int b) {
				console.append(String.valueOf((char) b));
			}
			@Override
			public void write(byte[] b, int off, int len) {
				console.append(new String(b, off, len, StandardCharsets.UTF_8));
			}
		};
		System.setOut(new PrintStream(textAreaStream));
		System.setErr(new PrintStream(textAreaStream));
	}

	/** 右键彩蛋：进度条（无实际业务）。 */
	private static class TimePro {
		private Timer timer;

		void init() {
			SimulatedActivity target = new SimulatedActivity(100);
			Thread worker = new Thread(target);
			worker.start();
			ProgressMonitor dialog = new ProgressMonitor(null, msg("progress.wait"), msg("progress.done"), 0, target.getAmount());
			timer = new Timer(300, e -> {
				dialog.setProgress(target.getCurrent());
				if (dialog.isCanceled()) {
					timer.stop();
					worker.interrupt();
					System.out.println("提前结束。");
				}
			});
			timer.start();
		}
	}

	/**
	 * 更换密钥：读取 bak 文件，用当前密钥解密每条密码，再用新密钥加密并写回 bakNEW。
	 */
	private static void changeKeys() {
		if (cKeyInput == null || cKeyInput.isBlank()) {
			System.out.println("未输入新密钥或已取消。");
			return;
		}
		String[] keySplit = cKeyInput.split("/");
		if (keySplit.length != 2) {
			JOptionPane.showMessageDialog(null, msg("dialog.changekey_format"), msg("dialog.warning"), JOptionPane.WARNING_MESSAGE);
			return;
		}
		String newKeyA = keySplit[0].trim();
		String newKeyB = keySplit[1].trim();
		if (!CheckingInput.inputFilter(newKeyA, false) || !CheckingInput.inputFilter(newKeyB, false)) {
			JOptionPane.showMessageDialog(null, msg("dialog.changekey_invalid"), msg("dialog.error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		String fs = File.separator;
		String bakpath = "." + fs + "PEncoderDatabasebak";
		File bak = new File(bakpath);
		if (!bak.exists()) {
			JOptionPane.showMessageDialog(null, msg("dialog.file_bak_not_found"), msg("dialog.error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		List<String> platforms = new ArrayList<>();
		List<String> accounts = new ArrayList<>();
		List<String> passwords = new ArrayList<>();
		List<String> remarks = new ArrayList<>();
		String bakSep = ":";
		try {
			System.out.println("为以防万一，原 bak 文件不会被覆盖，请手动删除谢谢。");
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(bakpath), StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String[] split = line.split(bakSep);
					if (split.length >= 4) {
						platforms.add(split[0]);
						accounts.add(split[1]);
						passwords.add(split[2]);
						remarks.add(split[3]);
					}
				}
			}
			IfPwdCoder coder = new AEScoder();
			for (int i = 0; i < passwords.size(); i++) {
				String plain = coder.decode(passwords.get(i));
				passwords.set(i, AEScoder.ckeyEncode(plain, newKeyA, newKeyB));
			}
			StringBuilder out = new StringBuilder();
			for (int i = 0; i < platforms.size(); i++) {
				out.append(platforms.get(i)).append(":").append(accounts.get(i))
						.append(":").append(passwords.get(i)).append(":").append(remarks.get(i)).append("\n");
			}
			ReadPEncoderDB.writeToText(out.toString(), bakpath + "NEW");
		} catch (CryptoException e1) {
			System.out.println("更换密钥失败: " + e1.getMessage());
			JOptionPane.showMessageDialog(null, "更换密钥失败: " + e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e1) {
			System.out.println("操作失败: " + e1.getMessage());
		}
	}

//	-----------------用于执行界面初始化的init方法---------------------
	/**
	 * 界面初始化
	 * 主程序界面初始化
	 */
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

		workPanel = new JPanel();
		workPanel.setLayout(null);
		consolePanel = new JScrollPane(console);
		workPanel.setFont(f);
		consolePanel.setFont(f);
		workPanel.setBounds(0, 0, 400, 340);
		consolePanel.setBounds(0, 340, 400, 200);
		workPanel.setBorder(BorderFactory.createTitledBorder(msg("border.work")));
		consolePanel.setBorder(BorderFactory.createTitledBorder(msg("border.log")));
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
		menubar.setFont(f);
		options.setFont(f);
		options.add(export);
		options.add(top);
		top.setFont(f);
		export.setFont(f);
		options.add(changeKeys);
		changeKeys.setFont(f);
		autoEncodeOnExitItem.setFont(f);
		autoEncodeOnExitItem.setSelected(getAutoEncodeOnExitPreference());
		options.add(autoEncodeOnExitItem);
		chooseBakEditorItem.setFont(f);
		options.add(chooseBakEditorItem);
		langMenu.setFont(f);
		langMenu.add(langZh);
		langZh.setFont(f);
		langMenu.add(langEn);
		langEn.setFont(f);
		options.add(langMenu);
		exit.setFont(f);
		menubar.add(help);
		help.setFont(f);
		help.add(manual);
		manual.setFont(f);
		help.add(about);
		about.setFont(f);
		mainWindow.setJMenuBar(menubar);// 为f窗口设置菜单条

		keyALabel = new JLabel();
		keyALabel.setFont(f);
		keyBLabel = new JLabel();
		keyBLabel.setFont(f);
		inputLabel = new JLabel();
		inputLabel.setFont(f);
		outputLabel = new JLabel();
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
		outputArea.setEditable(false);
		outputArea.setForeground(Color.LIGHT_GRAY);
		// 下面保证两个按钮只激活一个
		modeSelect.add(encoderButton);
		modeSelect.add(decoderButton);
		encoderButton.setSelected(true);

		// 单选按钮：统一高度与间距，避免重叠或间距不一
		int radioWidth = 95;
		int radioHeight = 20;
		int radioGap = 4;
		int radioY = 274;
		encoderButton.setBounds(8, radioY, radioWidth, radioHeight);
		decoderButton.setBounds(8, radioY + radioHeight + radioGap, radioWidth, radioHeight);
		isPwdInCleartext.setBounds(8, radioY + 2 * (radioHeight + radioGap), radioWidth, radioHeight);

		edit.setBounds(108, 288, 75, 35);
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
		console.setLineWrap(true);
		console.setWrapStyleWord(true);
		console.setText("Welcome!\n");
		console.setEditable(false);
		outputUI();

		updateUITexts();

//		设置事件-----------------------------------------------------------------
//		按钮事件=================================================================
		edit.addActionListener(e -> openDBFile());
		copy.addActionListener(e -> copyToClipboard(outputArea.getText()));
		run.addActionListener(e -> {
			try {
				outputArea.setText(null);
				if (encoderButton.isSelected()) {
					String a = CheckingInput.pwdAppend(inputArea.getText());
					isItSemicolon = CheckingInput.wasLastRejectionSemicolon();
					if (a == null) {
						return;
					}
					IfPwdCoder encoding = new AEScoder();
					String x = encoding.encode(a);
					if (isItSemicolon) {
						outputArea.setText(null);
					} else {
						outputArea.setText(x);
					}
					System.out.println("——加密完成——");
				} else if (decoderButton.isSelected()) {
					String b = inputArea.getText();
					IfPwdCoder decoding = new AEScoder();
					String x = decoding.decode(b);
					x = x.replaceAll(";", "");
					if (x.contains("；")) {
						x = x.replaceAll("；", ";");
					}
					outputArea.setText(x);
					System.out.println("——解密完成——");
				} else {
					System.out.println("运行出错。");
				}
			} catch (CryptoException e3) {
				System.out.println("加解密失败: " + e3.getMessage());
				JOptionPane.showMessageDialog(mainWindow, "加解密失败: " + e3.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e3) {
				System.out.println("运行出错: " + e3.getMessage());
			}
		});
		isPwdInCleartext.addActionListener(e -> {
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
		});

//		菜单事件
		outputArea.setComponentPopupMenu(pop);
		popM.addActionListener(e -> new TimePro().init());

		newfile.addActionListener(e -> {
			try {
				System.out.println("正在新建bak文件……");
				ReadPEncoderDB.newDBbakFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
		EN.addActionListener(e -> {
			try {
				System.out.println("正在编码bak文件……");
				ReadPEncoderDB.encodeDB();
			} catch (CryptoException e1) {
				System.out.println("编码失败: " + e1.getMessage());
				JOptionPane.showMessageDialog(mainWindow, "编码失败: " + e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			} catch (IOException e1) {
				System.out.println("文件操作失败: " + e1.getMessage());
				JOptionPane.showMessageDialog(mainWindow, "文件操作失败: " + e1.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
			}
		});
		DE.addActionListener(e -> runDecodeDB());
		DE.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));
		exit.addActionListener(e -> doExit());
		
		export.addActionListener(e -> {
			boolean t = mainWindow.isAlwaysOnTop();
			if(t) {
				mainWindow.setAlwaysOnTop(false);
			}
			Object[] possibleValues = { msg("dialog.export_cipher"), msg("dialog.export_plain") };
			try {
				Object selectedValue = JOptionPane.showInputDialog(null, msg("dialog.export_prompt"), msg("dialog.export_title"),
						JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[0]);
				if (selectedValue != null && selectedValue.equals(possibleValues[0])) {
					ExportAsXlsFile.createCSV(false);
				} else if (selectedValue != null && selectedValue.equals(possibleValues[1])) {
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
		});
		top.addActionListener(e -> mainWindow.setAlwaysOnTop(!mainWindow.isAlwaysOnTop()));
		autoEncodeOnExitItem.addActionListener(e -> setAutoEncodeOnExitPreference(autoEncodeOnExitItem.isSelected()));
		chooseBakEditorItem.addActionListener(e -> chooseBakEditor());
		langZh.addActionListener(e -> {
			currentLocale = Locale.SIMPLIFIED_CHINESE;
			reloadBundle();
			updateUITexts();
		});
		langEn.addActionListener(e -> {
			currentLocale = Locale.ENGLISH;
			reloadBundle();
			updateUITexts();
		});
		changeKeys.addActionListener(e -> {
			boolean wasOnTop = mainWindow.isAlwaysOnTop();
			if (wasOnTop) {
				mainWindow.setAlwaysOnTop(false);
			}
			cKeyInput = JOptionPane.showInputDialog(msg("dialog.changekey_prompt"));
			changeKeys();
			if (wasOnTop) {
				mainWindow.setAlwaysOnTop(true);
			}
		});
		
		manual.addActionListener(e -> {
			boolean t = mainWindow.isAlwaysOnTop();
			if(t) {
				mainWindow.setAlwaysOnTop(false);
			}
			JOptionPane.showMessageDialog(null, msg("help.content"), msg("dialog.help_title"), JOptionPane.INFORMATION_MESSAGE);
			if(t) {
				mainWindow.setAlwaysOnTop(true);
			}
		});
		about.addActionListener(e -> {
			boolean t = mainWindow.isAlwaysOnTop();
			if(t) {
				mainWindow.setAlwaysOnTop(false);
			}
			JOptionPane.showMessageDialog(null, format(msg("about.content"), VERSION, UPDATE), msg("dialog.about_title"), JOptionPane.INFORMATION_MESSAGE);
			if(t) {
				mainWindow.setAlwaysOnTop(true);
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

		// 快捷键：Ctrl+M 切换加密/解密模式
		mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK), "toggleEncryptDecrypt");
		mainWindow.getRootPane().getActionMap().put("toggleEncryptDecrypt", new AbstractAction() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (encoderButton.isSelected()) {
					decoderButton.setSelected(true);
				} else {
					encoderButton.setSelected(true);
				}
			}
		});

		mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainWindow.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				doExit();
			}
		});
		mainWindow.setVisible(true);
		mainWindow.setLocationRelativeTo(null);//居中显示

		/**
		 * Set the Nimbus look and feel
		 */
		if (fs.equals("\\")) {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (InstantiationException e1) {
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnsupportedLookAndFeelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
