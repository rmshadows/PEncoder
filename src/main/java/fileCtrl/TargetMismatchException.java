package fileCtrl;

import java.io.IOException;

/**
 * 编码/解码时目标文件已存在，且与当前要写入的内容不一致，需用户确认是否覆盖。
 *
 * @author jessie
 */
public class TargetMismatchException extends IOException {

	private static final long serialVersionUID = 1L;

	public TargetMismatchException(String message) {
		super(message);
	}
}
