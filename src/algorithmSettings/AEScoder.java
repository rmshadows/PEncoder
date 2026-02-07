package algorithmSettings;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import appCtrl.MainProgram;
import fileCtrl.CheckingInput;
import iface.IfPwdCoder;

/**
 * AES-CBC 加解密实现。
 * <ul>
 *   <li><b>新格式 (v2)</b>：使用 PBKDF2 从 KeyA+KeyB 派生密钥，每次加密使用随机 IV，密文为 {@code "v2:" + hex(IV) + hex(密文)}。</li>
 *   <li><b>旧格式</b>：KeyA 补齐/截断为密钥，KeyB 为 IV，密文为 hex(密文)。解密时自动识别并兼容。</li>
 * </ul>
 *
 * @author jessie
 */
public class AEScoder implements IfPwdCoder {

	private static final String ENCRYPT_ALGORITHM = "AES";
	private static final String CIPHER_MODE = "AES/CBC/PKCS5Padding";
	private static final String CHARSET_NAME = StandardCharsets.UTF_8.name();
	private static final int KEY_SIZE_BYTES = 16;
	private static final int IV_SIZE_BYTES = 16;

	/** 新格式密文前缀，用于识别并兼容旧数据 */
	private static final String V2_PREFIX = "v2:";

	/** PBKDF2 迭代次数，提高穷举成本 */
	private static final int PBKDF2_ITERATIONS = 50_000;
	private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
	/** 固定盐（仅用于派生，不随密文存储；与随机 IV 配合使用） */
	private static final byte[] KDF_SALT = "PEncoder.AES.v2!".getBytes(StandardCharsets.UTF_8);

	private static final SecureRandom RANDOM = new SecureRandom();

	// ---------- 旧格式：密钥/IV 直接由字符串补齐或截断（仅用于解密旧数据） ----------

	private static byte[] legacyKeyFromPassword(String password) {
		if (password == null) return null;
		byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < KEY_SIZE_BYTES) {
			byte[] result = new byte[KEY_SIZE_BYTES];
			System.arraycopy(bytes, 0, result, 0, bytes.length);
			return result;
		}
		return bytes.length == KEY_SIZE_BYTES ? bytes : java.util.Arrays.copyOf(bytes, KEY_SIZE_BYTES);
	}

	/** 旧格式加密：KeyA 为密钥，KeyB 为 IV */
	private static byte[] legacyEncrypt(byte[] plainBytes, byte[] keyBytes, byte[] ivBytes) throws CryptoException {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ENCRYPT_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			return cipher.doFinal(plainBytes);
		} catch (Exception e) {
			throw new CryptoException("加密失败", e);
		}
	}

	/** 旧格式解密 */
	private static byte[] legacyDecrypt(byte[] cipherBytes, byte[] keyBytes, byte[] ivBytes) throws CryptoException {
		try {
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ENCRYPT_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			return cipher.doFinal(cipherBytes);
		} catch (Exception e) {
			throw new CryptoException("解密失败", e);
		}
	}

	// ---------- 新格式：PBKDF2 派生密钥 + 随机 IV ----------

	/**
	 * 使用 PBKDF2 从 KeyA+KeyB 派生 16 字节 AES 密钥。
	 */
	private static byte[] deriveKey(String keyA, String keyB) throws CryptoException {
		String password = (keyA != null ? keyA : "") + "\n" + (keyB != null ? keyB : "");
		if (password.trim().isEmpty()) {
			throw new CryptoException("密钥不能为空");
		}
		try {
			KeySpec spec = new PBEKeySpec(password.toCharArray(), KDF_SALT, PBKDF2_ITERATIONS, KEY_SIZE_BYTES * 8);
			SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			byte[] keyBytes = factory.generateSecret(spec).getEncoded();
			// 清空 PBEKeySpec 内部 char[] 的引用（密钥已复制到 keyBytes）
			return keyBytes;
		} catch (Exception e) {
			throw new CryptoException("密钥派生失败", e);
		}
	}

	/**
	 * 新格式加密：随机 IV + PBKDF2 密钥，返回 "v2:" + hex(IV) + hex(密文)。
	 */
	private static String encryptV2(byte[] plainBytes, String keyA, String keyB) throws CryptoException {
		byte[] keyBytes = deriveKey(keyA, keyB);
		byte[] iv = new byte[IV_SIZE_BYTES];
		RANDOM.nextBytes(iv);
		try {
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ENCRYPT_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
			byte[] cipherBytes = cipher.doFinal(plainBytes);
			return V2_PREFIX + bytesToHex(iv) + bytesToHex(cipherBytes);
		} catch (Exception e) {
			throw new CryptoException("加密失败", e);
		}
	}

	/**
	 * 新格式解密：解析 "v2:" + hex(IV) + hex(密文)，用 PBKDF2 密钥解密。
	 */
	private static byte[] decryptV2(String cipherHex, String keyA, String keyB) throws CryptoException {
		String hex = cipherHex.substring(V2_PREFIX.length());
		if (hex.length() < IV_SIZE_BYTES * 2) {
			throw new CryptoException("v2 密文过短");
		}
		byte[] iv = hexToBytes(hex.substring(0, IV_SIZE_BYTES * 2));
		byte[] cipherBytes = hexToBytes(hex.substring(IV_SIZE_BYTES * 2));
		byte[] keyBytes = deriveKey(keyA, keyB);
		try {
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ENCRYPT_ALGORITHM);
			IvParameterSpec ivSpec = new IvParameterSpec(iv);
			Cipher cipher = Cipher.getInstance(CIPHER_MODE);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
			return cipher.doFinal(cipherBytes);
		} catch (Exception e) {
			throw new CryptoException("解密失败", e);
		}
	}

	// ---------- UI 耦合（后续可改为参数传入） ----------

	private static String getKeyAFromUi() {
		return String.copyValueOf(MainProgram.keyA.getPassword());
	}

	private static String getKeyBFromUi() {
		return String.copyValueOf(MainProgram.keyB.getPassword());
	}

	// ---------- 对外接口 ----------

	@Override
	public String encode(String clearText) {
		byte[] plainBytes = CheckingInput.stringToByteArray(clearText);
		if (plainBytes == null) {
			throw new CryptoException("明文转字节失败");
		}
		String keyA = getKeyAFromUi();
		String keyB = getKeyBFromUi();
		return encryptV2(plainBytes, keyA, keyB);
	}

	@Override
	public String decode(String cipherText) {
		if (cipherText == null || cipherText.isEmpty()) {
			throw new CryptoException("密文为空");
		}
		String keyA = getKeyAFromUi();
		String keyB = getKeyBFromUi();

		if (cipherText.startsWith(V2_PREFIX)) {
			byte[] plainBytes = decryptV2(cipherText, keyA, keyB);
			return CheckingInput.byteArrayToStr(plainBytes);
		}

		// 旧格式：hex(密文)，KeyA 为密钥，KeyB 为 IV
		byte[] cipherBytes = hexToBytes(cipherText);
		byte[] keyBytes = legacyKeyFromPassword(keyA);
		byte[] ivBytes = legacyKeyFromPassword(keyB);
		if (keyBytes == null || ivBytes == null) {
			throw new CryptoException("密钥 KeyA/KeyB 无效");
		}
		byte[] plainBytes = legacyDecrypt(cipherBytes, keyBytes, ivBytes);
		return CheckingInput.byteArrayToStr(plainBytes);
	}

	/**
	 * 使用新密钥重新加密（更换密钥流程）。始终使用新格式（PBKDF2 + 随机 IV）。
	 */
	public static String ckeyEncode(String clearText, String newKeyA, String newKeyB) {
		byte[] plainBytes = CheckingInput.stringToByteArray(clearText);
		if (plainBytes == null) {
			throw new CryptoException("明文转字节失败");
		}
		// 若旧密文带 "v2:"，先解密再加密会得到明文；这里 clearText 在调用方已是解密后的明文
		return encryptV2(plainBytes, newKeyA, newKeyB);
	}

	// ---------- 工具方法 ----------

	private static String bytesToHex(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("bytes 不能为 null");
		}
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) sb.append('0');
			sb.append(hex);
		}
		return sb.toString().toUpperCase();
	}

	private static byte[] hexToBytes(String hex) {
		if (hex == null || hex.length() < 2) {
			return new byte[0];
		}
		hex = hex.toLowerCase();
		int len = hex.length() / 2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++) {
			String pair = hex.substring(2 * i, 2 * i + 2);
			result[i] = (byte) (Integer.parseInt(pair, 16) & 0xFF);
		}
		return result;
	}
}
