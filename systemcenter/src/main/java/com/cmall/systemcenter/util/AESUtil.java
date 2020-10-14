package com.cmall.systemcenter.util;

import java.security.MessageDigest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;

import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

/**
 * AES 加密解密
 * 
 */
public class AESUtil {    
	/**
	 * 16位加密秘钥
	 */
	private static final String SEED_16_CHARACTER = "mgepqfjdrkzdzxwp";

	
	private Cipher encryptCipher;

	private Cipher decryptCipher;	

	/**
	 */
	public void initialize() {
		try {		
		    SecretKeySpec skeySpec = new SecretKeySpec(SEED_16_CHARACTER.getBytes(),"Rijndael");
			encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			skeySpec.getFormat();
			encryptCipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			decryptCipher.init(Cipher.DECRYPT_MODE, skeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 */
	public String encrypt(String data) {
		
		if (data == null){
			return null;
		}

		try {
            byte[] dataBytes = data.getBytes();
			byte[] doFinal = encryptCipher.doFinal(dataBytes);
			String b64dEncode = b64dEncode(doFinal);
			return b64dEncode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 */
	public String decrypt(String data) {

		if (data == null){
			return null;
		}
		
		try {
			byte[] b64dDecode = b64dDecode(data);
			return new String(decryptCipher.doFinal(b64dDecode));			
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] b64dDecode(String str){
		byte[] decode = BASE64DecoderStream.decode(str.getBytes());
		return decode;
	}
	public static String b64dEncode(byte[] strByte){
		byte[] encode = BASE64EncoderStream.encode(strByte);
		return new String(encode);
	}
	
	public static void main(String[] args) {
		AESUtil qu =new AESUtil();
		qu.initialize();
		System.out.println(qu.encrypt("123456"));
	}

}
