package com.bitbucket.thinbus.srp6.js;

import com.nimbusds.srp6.SRP6CryptoParams;

public class SRP6JavaClientSessionSHA256 extends SRP6JavaClientSession {
	/**
	 * Create a SHA256 Java client session compatible with a thinbus server
	 * session.
	 * 
	 * You can generate your own with openssl see {@link OpenSSLCryptoConfigConverter}
	 * 
	 * @param N
	 *            The large safe prime in radix10
	 * @param g
	 *            The safe prime generator in radix10
	 */
	public SRP6JavaClientSessionSHA256(String N, String g) {
		super(new SRP6CryptoParams(SRP6JavascriptServerSession.fromDecimal(N),
				SRP6JavascriptServerSession.fromDecimal(g),
				SRP6JavascriptServerSessionSHA256.SHA_256));
	}
}
