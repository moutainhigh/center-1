package com.cmall.systemcenter.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AESCipher {

	private static final String IV_STRING = "16-Bytes--String";

	public static final String key = "huijiayouhenhaol";

    public static String encryptAES(String content, String key)

			throws InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, UnsupportedEncodingException, 
			InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
		byte[] byteContent = content.getBytes("UTF-8");

		byte[] enCodeFormat = key.getBytes();
	    SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
			
	    byte[] initParam = IV_STRING.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			
	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	 
	    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		
	    byte[] encryptedBytes = cipher.doFinal(byteContent);
		
	    String enc = org.apache.commons.codec.binary.Base64.encodeBase64String(encryptedBytes);
//	    Encoder encoder = Base64.getEncoder();
//	    String enc = encoder.encodeToString(encryptedBytes);

	    enc = DcbBase64Util.encode(enc.getBytes());
	    return enc;
	}

	public static String decryptAES(String content, String key) 
			throws InvalidKeyException, NoSuchAlgorithmException, 
			NoSuchPaddingException, InvalidAlgorithmParameterException, 
			IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
		content = new String(DcbBase64Util.decode(content));
		
		byte[] encryptedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(content);
		
//	    Decoder decoder = Base64.getDecoder();
//	    byte[] encryptedBytes = decoder.decode(content);
		
	    byte[] enCodeFormat = key.getBytes();
	    SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
		
	    byte[] initParam = IV_STRING.getBytes();
	    IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

	    byte[] result = cipher.doFinal(encryptedBytes);
		
	    return new String(result, "UTF-8");
	}
}