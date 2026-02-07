package iface;

/**
 * 密码加解密接口。
 * 实现可能抛出 {@link algorithmSettings.CryptoException}（运行时异常），调用方可按需捕获。
 *
 * @author jessie
 */
public interface IfPwdCoder {
	String encode(String clearText);
	String decode(String cipherText);
}