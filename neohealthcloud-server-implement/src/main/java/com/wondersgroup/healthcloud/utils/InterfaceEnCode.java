package com.wondersgroup.healthcloud.utils;

import okio.ByteString;
import org.joda.time.DateTime;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Date;

public class InterfaceEnCode {

	private final static String DES = "DES";

	private final static String KEY = "healthjingan2016";

	public static String getAccessToken() {
		try {
			return InterfaceEnCode.encrypt(new DateTime(new Date()).toString());
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getInterfaceKey() {
		String data = String.valueOf(System.currentTimeMillis());
		try {
			return encrypt(data);
		} catch (Exception e) {
			return null;
		}
	}

	public static Date parseInterfaceKey(String s) {
		try {
			String l = decrypt(s);
			return new Date(Long.parseLong(l));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String data) throws Exception {
		byte[] bytes = encrypt(data.getBytes("UTF-8"), KEY.getBytes());
		return ByteString.of(bytes).base64();
	}

	/**
	 * Description 根据键值进行加密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 *            加密键byte数组
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static String decrypt(String data) throws Exception {
		if (data == null) {
			return null;
		}
		byte[] decodeBytes = ByteString.decodeBase64(data).toByteArray();
		byte[] bytes = decrypt(decodeBytes, KEY.getBytes());
		return new String(bytes, "UTF-8");
	}

	/**
	 * Description 根据键值进行解密
	 * 
	 * @param data
	 * @param key
	 *            加密键byte数组
	 * @return
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		// 生成一个可信任的随机数源
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");

		// 从原始密钥数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);

		// 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(dks);

		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);

		// 用密钥初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

		return cipher.doFinal(data);
	}

	/**
	 * 认证
	 * 
	 * @param ciphertext
	 * @return
	 */
	public static Boolean auth(String ciphertext) {
		String time = null;
		try {
			time = decrypt(ciphertext);
		} catch (Exception e) {
			return false;
		}
		DateTime d1 = new DateTime(new Date()).plusMinutes(-3);
		DateTime d2 = new DateTime(new Date()).plusMinutes(3);
		try {
			DateTime now = new DateTime(time.trim());
			return d1.isBefore(now) && d2.isAfter(now);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return false;
	}

}
