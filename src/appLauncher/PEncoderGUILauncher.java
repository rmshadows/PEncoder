package appLauncher;

import appCtrl.MainProgram;

/**
 * 类名：Swing用户界面加载器
 * 用于加载图形界面
 * @author jessie
 */

public class PEncoderGUILauncher {
	
	/**
	 * 方法名：主程序入口main方法
	 * @param args 略
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> new MainProgram().mainWindowInit());
	}
}