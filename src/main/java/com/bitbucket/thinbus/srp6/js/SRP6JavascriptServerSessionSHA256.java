package com.bitbucket.thinbus.srp6.js;

import java.io.Serializable;

import com.nimbusds.srp6.SRP6CryptoParams;

/**
 * Wrapper of a server session matching the Javascript client session
 * SRP6JavascriptClientSession_N1024_SHA256. BigInteger values are communicated
 * as hex strings. Hashing is done as string concat of hex numbers. Does not
 * include any session timeout logic on the assumption that can be handled by
 * web server session logic.
 * <p>
 * Specification RFC 2945.
 * 
 * @author Simon Massey
 */
public class SRP6JavascriptServerSessionSHA256 extends SRP6JavascriptServerSession implements Serializable {

	/**
	 * Serializable class version number
	 */
	private static final long serialVersionUID = 8311147633496438232L;

	public static final String SHA_256 = "SHA-256";

	/**
	 * This must match the expected character length of the specified algorithm
	 * i.e. SHA-256 is 64
	 */
	public static int HASH_HEX_LENGTH = 64;

	/**
	 * Create a SHA-256 server session compatible with a JavaScript client
	 * session.
	 * 
	 * You can generate your own with openssl see {@link OpenSSLCryptoConfigConverter}
	 * 
	 * @param N
	 *            The large safe prime in radix10
	 * @param g
	 *            The safe prime generator in radix10
	 */
	public SRP6JavascriptServerSessionSHA256(String N, String g) {
		super(new SRP6CryptoParams(fromDecimal(N), fromDecimal(g), SHA_256));
	}
}
