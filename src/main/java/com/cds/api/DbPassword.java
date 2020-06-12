package com.cds.api;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbPassword {
	static private final Logger logger = LoggerFactory.getLogger(DbPassword.class);

	public static void main(String[] args) {
		String str = "postgres";
		String encStr = encAES(str);
		System.out.println(encStr);
		System.out.println(decAES(encStr));
		str = "oracle";
		encStr = encAES(str);
		System.out.println(encStr);
		System.out.println(decAES(encStr));
		str = "mysql";
		encStr = encAES(str);
		System.out.println(encStr);
		System.out.println(decAES(encStr));
		str = "<yourPAssword1!>";
		encStr = encAES(str);
		System.out.println(encStr);
		System.out.println(decAES(encStr));
	}

	public static Key getAESKey() throws UnsupportedEncodingException {
		String iv;
		Key keySpec;
		// String key = "encryption!@1234";
		String key = "cdsservice_passwd";
		iv = key.substring(0, 16);
		byte[] keyBytes = new byte[16];
		byte[] b = key.getBytes("UTF-8");

		int len = b.length;
		if (len > keyBytes.length) {
			len = keyBytes.length;
		}

		System.arraycopy(b, 0, keyBytes, 0, len);
		keySpec = new SecretKeySpec(keyBytes, "AES");

		return keySpec;
	}

	// 암호화
	public static String encAES(String str) {
		String enStr = str;
		Cipher c;
		try {
			Key keySpec = getAESKey();
			c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
			byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
			enStr = new String(Base64.getEncoder().encode(encrypted));
		} catch (Exception e) {
			logger.error("DbPassword.encAES", e);
		}
		return enStr;
	}
	static final String iv = "098765432165432A";
	// 복호화
	public static String decAES(String enStr) {
		String decStr = enStr;
		try {
			Key keySpec = getAESKey();
			Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
			c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
			byte[] byteStr = Base64.getDecoder().decode(enStr.getBytes("UTF-8"));
			decStr = new String(c.doFinal(byteStr), "UTF-8");
		} catch (Exception e) {
			logger.error("DbPassword.decAES", e);
		}

		return decStr;
	}

}
