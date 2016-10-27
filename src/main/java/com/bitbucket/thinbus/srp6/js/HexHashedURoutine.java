package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.io.Serializable;
import java.math.BigInteger;

import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.URoutine;
import com.nimbusds.srp6.URoutineContext;

/**
 * Custom routine interface for computing the hashed keys 'u'. Compatible with
 * browser implementations by using hashing of string concatenated hex strings
 * 'H( HEX(A) | HEX(B) )'.
 * 
 * <p>
 * Specification RFC 2945
 * 
 * @author Simon Massey
 */
final class HexHashedURoutine implements URoutine, Serializable {

	/**
	 * Serializable class version number
	 */
	private static final long serialVersionUID = -2041787925113762310L;

	/**
	 * Computes the random scrambling parameter u = H(A | B)
	 * 
	 * @param digest
	 *            The hash function 'H'. Must not be {@code null}.
	 * @param A
	 *            The public client value 'A'. Must not be {@code null}.
	 * @param B
	 *            The public server value 'B'. Must not be {@code null}.
	 * 
	 * @return The resulting 'u' value as as 'H( HEX(A) | HEX(B) )'.
	 */
	@Override
	public BigInteger computeU(SRP6CryptoParams cryptoParams, URoutineContext ctx) {
		return HexHashedRoutines.hashValues(cryptoParams.getMessageDigestInstance(), toHex(ctx.A), toHex(ctx.B));
	}
}