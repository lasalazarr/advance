package ec.gov.informatica.firmadigital;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Main {

	private static final char[] PASSWORD = "ricardo".toCharArray();

	// 8-byte Salt
	private static final byte[] SALT = { (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32, (byte) 0x56, (byte) 0x35,
			(byte) 0xE3, (byte) 0x03 };

	// Iteration count
	private static final int ITERATION_COUNT = 17;

	public static void main(String[] args) throws Exception {
		String xml = "<hi>Hola</hi>";
		String encriptado = encrypt(xml);
		String original = decrypt(encriptado);

		System.out.println("original=" + xml);
		System.out.println("encriptado=" + encriptado);
		System.out.println("original=" + original);
	}

	public static String encrypt(String message) throws Exception {
		KeySpec keySpec = new PBEKeySpec(PASSWORD, SALT, ITERATION_COUNT);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

		Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
		ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		byte[] enc = ecipher.doFinal(message.getBytes());
		return new BASE64Encoder().encode(enc);
	}

	public static String decrypt(String message) throws Exception {
		KeySpec keySpec = new PBEKeySpec(PASSWORD, SALT, ITERATION_COUNT);
		SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
		AlgorithmParameterSpec paramSpec = new PBEParameterSpec(SALT, ITERATION_COUNT);

		Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
		dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

		byte[] dec = new BASE64Decoder().decodeBuffer(message);
		byte[] utf8 = dcipher.doFinal(dec);
		return new String(utf8);
	}
}