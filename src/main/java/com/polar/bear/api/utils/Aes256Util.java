package com.polar.bear.api.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Component
public class Aes256Util {

	private static String IV = "bearshopbearshop";
	private static String key = "b1e2a3r4s5h6o7p8b9e1a2r3s4h5o6p7";

	public static String getEncrypt(String str) {
		String encStr = null;
		try {
			byte[] keyBytes = key.getBytes();

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			int bsize = cipher.getBlockSize();
			IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("UTF-8"));

			SecretKeySpec secureKey = new SecretKeySpec(keyBytes, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secureKey, ivspec);
			byte[] encrypted = cipher.doFinal(str.getBytes("UTF-8"));

			encStr = new String(Base64.encodeBase64(encrypted));

		} catch (Exception e) {
			encStr = "";
			e.printStackTrace();
		}

		return encStr;
	}

	public static String getDecrypt(String str) {
		String decStr = null;

		try {
			byte[] keyBytes = key.getBytes();

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

			IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes("UTF-8"));

			SecretKeySpec secureKey = new SecretKeySpec(keyBytes, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secureKey, ivspec);
			
			
			byte[] decrypted = Base64.decodeBase64(str.getBytes());

			decStr = new String(cipher.doFinal(decrypted), "UTF-8");

		} catch (Exception e) {
			decStr = "";
			e.printStackTrace();
		}

		return decStr;
	}

//	public static void main(String[] args) {
//		System.out.println(Aes256Util.getEncrypt("test"));
//		System.out.println(Aes256Util.getDecrypt("d1RDt/tkmw9D+eFUvIr4Hg=="));
//	}
}
