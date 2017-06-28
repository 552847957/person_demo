package com.wondersgroup.healthcloud.utils.wonderCloud;

import okio.ByteString;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;


/**
 * RSA安全编码组件
 * 
 */
public abstract class RSAUtil {

	public static final String KEY_SHA = "SHA";
	public static final String KEY_MD5 = "MD5";
	public static final String KEY_MAC = "HmacMD5";

	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

	private static final String PUBLIC_KEY = "RSAPublicKey";
	private static final String PRIVATE_KEY = "RSAPrivateKey";

    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final int MAX_DECRYPT_BLOCK = 128;

	/**
	 * BASE64解密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptBASE64(String key) throws Exception {
//		return Base64.decodeBase64(key);
		return ByteString.decodeBase64(key).toByteArray();
	}

	/**
	 * BASE64加密
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptBASE64(byte[] key) throws Exception {
//		return Base64.encodeBase64String(key);
		return ByteString.of(key).base64();
	}

	/**
	 * MD5加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptMD5(byte[] data) throws Exception {

		MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
		md5.update(data);
		return md5.digest();

	}

	/**
	 * SHA加密
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptSHA(byte[] data) throws Exception {

		MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
		sha.update(data);

		return sha.digest();

	}

	/**
	 * 初始化HMAC密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initMacKey() throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);

		SecretKey secretKey = keyGenerator.generateKey();
		return encryptBASE64(secretKey.getEncoded());
	}

	/**
	 * HMAC加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptHMAC(byte[] data, String key) throws Exception {

		SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);

		return mac.doFinal(data);

	}

	/**
	 * 用私钥对信息生成数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param privateKey
	 *            私钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String sign(byte[] data, String privateKey) throws Exception {
		// 解密由base64编码的私钥
		byte[] keyBytes = decryptBASE64(privateKey);

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取私钥匙对象
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initSign(priKey);
		signature.update(data);

		return encryptBASE64(signature.sign());
	}

	/**
	 * 校验数字签名
	 * 
	 * @param data
	 *            加密数据
	 * @param publicKey
	 *            公钥
	 * @param sign
	 *            数字签名
	 * 
	 * @return 校验成功返回true 失败返回false
	 * @throws Exception
	 * 
	 */
	public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {

		// 解密由base64编码的公钥
		byte[] keyBytes = decryptBASE64(publicKey);

		// 构造X509EncodedKeySpec对象
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);

		// KEY_ALGORITHM 指定的加密算法
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);

		// 取公钥匙对象
		PublicKey pubKey = keyFactory.generatePublic(keySpec);

		Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
		signature.initVerify(pubKey);
		signature.update(data);

		// 验证签名是否正常
		return signature.verify(decryptBASE64(sign));
	}

	/**
	 * 解密<br>
	 * 用私钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decryptByPrivateKey(String data, String key) throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPrivateKey  privateKey = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] encryptedData = ByteString.decodeBase64(data).toByteArray();
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return URLDecoder.decode(new String(decryptedData), "utf-8");

	}

	/**
	 * 解密<br>
	 * 用公钥解密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key publicKey = keyFactory.generatePublic(x509KeySpec);

		// 对数据解密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.DECRYPT_MODE, publicKey);

		return cipher.doFinal(data);
	}

	/**
	 * 加密<br>
	 * 用公钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encryptByPublicKey(String data, String key) throws Exception {
		// 对公钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得公钥
		X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		RSAPublicKey  publicKey = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1PADDING");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] dataArray = URLEncoder.encode(data, "utf-8").getBytes();
		int inputLen = dataArray.length;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int offSet = 0;
		byte[] cache;
		int i = 0;
		// 对数据分段加密
		while (inputLen - offSet > 0) {
			if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
				cache = cipher.doFinal(dataArray, offSet, MAX_ENCRYPT_BLOCK);
			} else {
				cache = cipher.doFinal(dataArray, offSet, inputLen - offSet);
			}
			out.write(cache, 0, cache.length);
			i++;
			offSet = i * MAX_ENCRYPT_BLOCK;
		}
		byte[] encryptedData = out.toByteArray();

//		byte[] encryptedData = cipher.doFinal(data.getBytes());

		return ByteString.of(encryptedData).base64();
//		return Base64.encodeBase64String(encryptedData);
	//	return new BASE64Encoder().encode(encryptedData);
	}

	/**
	 * 加密<br>
	 * 用私钥加密
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encryptByPrivateKey(byte[] data, String key) throws Exception {
		// 对密钥解密
		byte[] keyBytes = decryptBASE64(key);

		// 取得私钥
		PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

		// 对数据加密
		Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);

		return cipher.doFinal(data);
	}

	/**
	 * 取得私钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 取得公钥
	 * 
	 * @param keyMap
	 * @return
	 * @throws Exception
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return encryptBASE64(key.getEncoded());
	}

	/**
	 * 初始化密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> initKey() throws Exception {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
		keyPairGen.initialize(1024);

		KeyPair keyPair = keyPairGen.generateKeyPair();

		// 公钥
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

		// 私钥
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<String, Object>(2);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);
		return keyMap;
	}
 
	public static void main(String[] args) {

		String decodeType = "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";
        String privateKey = DoctorKeyMap.getPrivateKey("4.1.0");
        try {
            String data = decryptByPrivateKey(decodeType, privateKey);
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}
