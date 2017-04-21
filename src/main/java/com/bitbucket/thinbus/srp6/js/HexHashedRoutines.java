package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Secure Remote Password (SRP-6a) hashing routine for Java compatible with
 * browser implementations by using hashing of string concatenated hex strings.
 * 
 * <p>
 * Specification RFC 2945
 * 
 * @author Simon Massey
 */
public class HexHashedRoutines {
	
	public final static Charset utf8 = utf8();

	static Charset utf8() {
		return Charset.forName("UTF8");
	}

	public static BigInteger hashValues(final MessageDigest digest, final String... values) {
		final StringBuilder builder = new StringBuilder();
		for (String v : values) {
			builder.append(v);
		}
		final byte[] bytes = builder.toString().getBytes(utf8);
		digest.update(bytes, 0, bytes.length);
		return new BigInteger(1, digest.digest());
	}

	private HexHashedRoutines() {
		// empty
	}

	public static String leadingZerosPad(String value, int desiredLength) {
		StringBuilder builder = new StringBuilder();
		int difference = desiredLength - value.length();
		for (int i = 0; i < difference; i++) {
			builder.append('0');
		}
		builder.append(value);
		return builder.toString();
	}

	public static String hashCredentials(MessageDigest digest, String salt,
			String identity, String password) {
		digest.reset();

		String concat = identity + ":" + password;

		digest.update(concat.getBytes(utf8));
		byte[] output = digest.digest();
		digest.reset();

		final String hash1 = toHex(new BigInteger(1, output));
		
		concat = (salt + hash1).toUpperCase();
		
		digest.update(concat.getBytes(utf8));
		output = digest.digest();

		return toHexString(output);
	}

	final private static char[] hexArray = "0123456789abcdef".toCharArray();

	/**
	 * http://stackoverflow.com/a/9855338
	 * 
	 * Compute a String in HexDigit from the input. Note that this string may
	 * have leading zeros but hex strings created by toString(16) of BigInteger
	 * would strip leading zeros.
	 * 
	 * @param bytes
	 *            Raw byte array
	 * @return Hex encoding of the input
	 */
	public static String toHexString(final byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
}
