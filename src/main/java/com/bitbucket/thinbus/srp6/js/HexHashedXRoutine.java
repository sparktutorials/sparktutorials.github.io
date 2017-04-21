package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.fromHex;
import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.math.BigInteger;
import java.security.MessageDigest;

import com.nimbusds.srp6.XRoutine;

public class HexHashedXRoutine implements XRoutine {
	/**
	 * Computes the password key 'x'.
	 *
	 * @param digest
	 *            The hash function 'H'.
	 * @param salt
	 *            The salt 's'. This is considered a mandatory argument in
	 *            computation of 'x'. Must not be {@code null} or empty.
	 * @param username
	 *            The user identity 'I'. Must not be {@code null} or empty.
	 * @param password
	 *            The user password 'P'. This is considered a mandatory argument
	 *            in the computation of 'x'. Must not be {@code null} or empty.
	 *
	 * @return The resulting 'x' value.
	 */
	@Override
	public BigInteger computeX(MessageDigest digest, byte[] salt,
			byte[] username, byte[] password) {
		final String i = new String(username, HexHashedRoutines.utf8);
		final String p = new String(password, HexHashedRoutines.utf8);
		final String s = toHex(new BigInteger(1, salt));

		if (i == null || i.trim().isEmpty())
			throw new IllegalArgumentException(
					"The user identity 'I' must not be null or empty");

		if (p == null || p.trim().isEmpty())
			throw new IllegalArgumentException(
					"The user password 'P' must not be null or empty");

		if (s == null || s.trim().isEmpty())
			throw new IllegalArgumentException(
					"The user salt 's' must not be null or empty");

		final String x = HexHashedRoutines.hashCredentials(digest, s, i, p);
		final BigInteger X = fromHex(x);
		return X;
	}

}
