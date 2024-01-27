package iface;

/**
 * 算法接口文件
 * @author jessie
 */
public interface IfPwdCoder {
	String encode(String clearText);
	String decoder(String cipherText);
}