package com.bitbucket.thinbus.srp6.js;

import java.io.Serializable;

import com.nimbusds.srp6.SRP6CryptoParams;

/**
 * Wrapper of a server session matching the Javascript client session
 * SRP6JavascriptClientSession_N256_SHA1. BigInteger values are communicated as
 * hex strings. Hashing is done as string concat of hex numbers. Does not
 * include any session timeout logic on the assumption that can be handled by
 * web server session logic.
 * <p>
 * Specification RFC 2945.
 * 
 * @author Simon Massey
 */
public class SRP6JavascriptServerSessionSHA1 extends SRP6JavascriptServerSession implements Serializable {

	/**
	 * Serializable class version number
	 */
	private static final long serialVersionUID = -8615033464877868308L;

	public static final String SHA_1 = "SHA-1";

	/**
	 * This must match the expected character length of the specified algorithm
	 * i.e. SHA-1 is 40
	 */
	public static int HASH_HEX_LENGTH = 40;

	/**
	 * Create a SHA1 server session compatible with a JavaScript client session.
	 * 
	 * You can generate your own with openssl see {@link OpenSSLCryptoConfigConverter}
	 * 
	 * @param N
	 *            The large safe prime in radix10
	 * @param g
	 *            The safe prime generator in radix10
	 */
	public SRP6JavascriptServerSessionSHA1(String N, String g) {
		super(new SRP6CryptoParams(fromDecimal(N), fromDecimal(g), SHA_1));
	}

}
