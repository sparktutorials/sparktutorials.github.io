package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.io.Serializable;
import java.math.BigInteger;

import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6Exception;
import com.nimbusds.srp6.SRP6Routines;
import com.nimbusds.srp6.SRP6ServerSession;
import com.nimbusds.srp6.SRP6ServerSession.State;

abstract public class SRP6JavascriptServerSession implements Serializable {

	/**
	 * Serializable class version number
	 */
	private static final long serialVersionUID = -5998252135527603869L;

	/**
	 * Returns the one-time server challenge `B` encoded as hex. 
	 * Increments this SRP-6a authentication session to {@link State#STEP_1}.
	 * 
	 * @param username
	 *            The identity 'I' of the authenticating user. Must not be
	 *            {@code null} or empty.
	 * @param salt
	 *            The password salt 's'. Must not be {@code null}.
	 * @param v
	 *            The password verifier 'v'. Must not be {@code null}.
	 * 
	 * @return The server public value 'B' as hex encoded number.
	 * 
	 * @throws IllegalStateException
	 *             If the mehod is invoked in a state other than
	 *             {@link State#INIT}.
	 */
	public String step1(final String username, final String salt, final String v) {
		BigInteger B = session.step1(username, fromHex(salt), fromHex(v));
		return toHex(B);
	}

	/**
	 * Validates a password proof `M1` based on the client one-tiem public key `A`. 
	 * Increments this SRP-6a authentication session to {@link State#STEP_2}.
	 * 
	 * @param A
	 *            The client public value. Must not be {@code null}.
	 * @param M1
	 *            The client evidence message. Must not be {@code null}.
	 * 
	 * @return The server evidence message 'M2' has hex encoded number with
	 *         leading zero padding to match the 256bit hash length.
	 * 
	 * @throws SRP6Exception
	 *             If the client public value 'A' is invalid or the user
	 *             credentials are invalid.
	 * 
	 * @throws IllegalStateException
	 *             If the mehod is invoked in a state other than
	 *             {@link State#STEP_1}.
	 */
	public String step2(final String A, final String M1) throws Exception {
		BigInteger M2 = session.step2(fromHex(A), fromHex(M1));
		String M2str = toHex(M2);
		M2str = HexHashedRoutines.leadingZerosPad(M2str, HASH_HEX_LENGTH);
		return M2str;
	}

	/**
	 * Returns the underlying session state as a String for JavaScript testing.
	 * 
	 * @return The current state.
	 */
	public String getState() {
		return session.getState().name();
	}

	/**
	 * Gets the identity 'I' of the authenticating user.
	 *
	 * @return The user identity 'I', null if undefined.
	 */
	public String getUserID() {
		return session.getUserID();
	}

	/**
	 * The crypto parameters for the SRP-6a protocol. These must be agreed
	 * between client and server before authentication and consist of a large
	 * safe prime 'N', a corresponding generator 'g' and a hash function
	 * algorithm 'H'. You can generate your own with openssl using
	 * {@link OpenSSLCryptoConfigConverter}
	 * 
	 */
	protected final SRP6CryptoParams config;

	/**
	 * The underlying Nimbus session which will be configure for JavaScript
	 * interactions
	 */
	protected final SRP6ServerSession session;
	
	/**
	 * Constructs a JavaScript compatible server session which configures an
	 * underlying Nimbus SRP6ServerSession.
	 * 
	 * @param srp6CryptoParams
	 *            cryptographic constants which must match those being used by
	 *            the client.
	 */
	public SRP6JavascriptServerSession(SRP6CryptoParams srp6CryptoParams) {
		this.config = srp6CryptoParams;
		session = new SRP6ServerSession(config);
		session.setHashedKeysRoutine(new HexHashedURoutine());
		session.setClientEvidenceRoutine(new HexHashedClientEvidenceRoutine());
		session.setServerEvidenceRoutine(new HexHashedServerEvidenceRoutine());
	}

	/**
	 * k is actually fixed and done with hash padding routine which uses
	 * java.net.BigInteger byte array constructor so this is a convenience
	 * method to get at the Java generated value to use in the configuration of
	 * the Javascript
	 * 
	 * @return 'k' calculated as H( N, g )
	 */
	public String k() {
		return toHex(SRP6Routines.computeK(config.getMessageDigestInstance(), config.N, config.g));
	}

	/**
	 * Turn a radix10 string into a java.net.BigInteger
	 * 
	 * @param base10
	 *            the radix10 string
	 * @return the BigInteger representation of the number
	 */
	public static BigInteger fromDecimal(String base10) {
		return new BigInteger(base10, 10);
	}

	/**
	 * This must match the expected character length of the specified algorithm
	 */
	public static int HASH_HEX_LENGTH;

	/**
	 * Outputs the configuration in the way which can be used to configure
	 * JavaScript.
	 * 
	 * Note that 'k' is fixed but uses the byte array constructor of BigInteger
	 * which is not available in JavaScript to you must set it as configuration.
	 * 
	 * @return Parameters required by JavaScript client.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("g: %s\n", config.g.toString(10)));
		builder.append(String.format("N: %s\n", config.N.toString(10)));
		builder.append(String.format("k: %s\n", k()));
		return builder.toString();
	}

	/**
	 * Gets the password salt 's'.
	 * 
	 * @deprecated This value is returned by step1 having a getter means holding onto more memory see issue #4 at https://bitbucket.org/simon_massey/thinbus-srp-js/issues/4
	 * 
	 * @return The salt 's' if available, else {@code null}.
	 */
	@Deprecated 
	public String getSalt() {
		return toHex(session.getSalt());
	}

	/**
	 * Gets the public server value 'B'.
	 * 
	 * @deprecated This value is returned by step1 having a getter means holding onto more memory see issue #4 at https://bitbucket.org/simon_massey/thinbus-srp-js/issues/4
	 * 
	 * @return The public server value 'B' if available, else {@code null}.
	 */
	@Deprecated 
	public String getPublicServerValue() {
		return toHex(session.getPublicServerValue());
	}

	/**
	 * Gets the server evidence message 'M2'.
	 *
	 * @deprecated This value is returned by step2 having a getter means holding onto more memory see issue #4 at https://bitbucket.org/simon_massey/thinbus-srp-js/issues/4
	 * 
	 * @return The server evidence message 'M2' if available, else {@code null}.
	 */
	@Deprecated 
	public String getServerEvidenceMessage() {
		return toHex(session.getServerEvidenceMessage());
	}

	/**
	 * Gets the shared session key 'S' or its hash H(S).
	 *
	 * @param doHash
	 *            If {@code true} the hash H(S) of the session key will be
	 *            returned instead of the raw value.
	 *
	 * @return The shared session key 'S' or its hash H(S). {@code null} will be
	 *         returned if authentication failed or the method is invoked in a
	 *         session state when the session key 'S' has not been computed yet.
	 */
	public String getSessionKey(boolean doHash) {
		String S = toHex(session.getSessionKey(false));
		if (doHash) {
			String K = HexHashedRoutines.toHexString(this.config
					.getMessageDigestInstance().digest(
					S.getBytes(HexHashedRoutines.utf8)));
			return K;
		} else {
			return S;
		}
	}
}
