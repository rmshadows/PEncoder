package algorithmSettings;

/**
 * 加解密过程出错时抛出（运行时异常，无需在方法签名中声明）。
 *
 * @author jessie
 */
public class CryptoException extends RuntimeException {

	public CryptoException(String message) {
		super(message);
	}

	public CryptoException(String message, Throwable cause) {
		super(message, cause);
	}
}
