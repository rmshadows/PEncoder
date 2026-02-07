package appLauncher;

import appCtrl.MainProgram;

import javax.swing.SwingUtilities;

/**
 * PEncoder 图形界面启动器。
 * 在 EDT 中创建并显示主窗口，符合 Swing 线程安全要求。
 *
 * @author jessie
 */
public final class PEncoderGUILauncher {

	private PEncoderGUILauncher() {
		// 工具类，禁止实例化
	}

	/**
	 * 程序入口。在事件分发线程中初始化并显示主窗口。
	 *
	 * @param args 命令行参数（当前未使用）
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(PEncoderGUILauncher::launchMainWindow);
	}

	private static void launchMainWindow() {
		new MainProgram().mainWindowInit();
	}
}