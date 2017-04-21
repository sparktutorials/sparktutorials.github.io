package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.math.BigInteger;
import java.security.MessageDigest;

import com.nimbusds.srp6.SRP6CryptoParams;

/**
 * Generates a SRP6 verifier. WARNING: You should use the JavaScript client not
 * the Java client for generating the verifier. See the
 * TestSRP6JavascriptClientSessionSHA256.js for an example. This class is only
 * for systems which let users login from Java clients in addition to JavaScript
 * clients who additionally wish to implement user registration of password
 * reset logic in their Java clients which subsequently let users login via a
 * browser. It is probably better to implement user registration or password
 * rest logic only via the browser. Certainly you SHOULD to avoid this code ever
 * being run on the server as that would require the password to be transmitted
 * to the server which is something which SRP is designed to avoid.
 */
public class HexHashedVerifierGenerator {
	protected final SRP6CryptoParams config;

	/**
	 * @param N
	 *            The large safe prime in radix10
	 * @param g
	 *            The safe prime generator in radix10
	 * @param hashName
	 *            The name of the hashing algorithm e.g. SHA256
	 */
	public HexHashedVerifierGenerator(String N, String g, String hashName) {
		config = new SRP6CryptoParams(
				SRP6JavascriptServerSession.fromDecimal(N),
				SRP6JavascriptServerSession.fromDecimal(g), hashName);
	}

	private String hashCredentials(String salt, String identity, String password) {
		MessageDigest digest = config.getMessageDigestInstance();
		return HexHashedRoutines.hashCredentials(digest, salt,
				identity, password);
	}

	// matches javascript client library does which is H(s | H(i | ":" | p))
	private BigInteger generateX(String salt, String identity, String password) {
		String hash = hashCredentials(salt, identity, password);
		return fromHex(hash).mod(config.N);
	}

	/**
	 * Browser does string concat version of x = H(s | H(i | ":" | p)).
	 * Specification is RFC 5054 Which we repeat here to be able to reset the
	 * password in a java client.
	 * 
	 * @param salt
	 *            The random salt stored at user registration
	 * @param identity
	 *            The user username
	 * @param password
	 *            The user password. Note this should only ever be on java
	 *            clients and never sent to the java server.
	 * @return An SRP password verifier
	 */
	public String generateVerifier(String salt, String identity, String password) {
		BigInteger x = generateX(salt, identity, password);
		BigInteger v = config.g.modPow(x, config.N);
		return toHex(v).toLowerCase();
	}

}
