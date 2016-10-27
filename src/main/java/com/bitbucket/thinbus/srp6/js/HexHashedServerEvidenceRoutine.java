package com.bitbucket.thinbus.srp6.js;

import static com.nimbusds.srp6.BigIntegerUtils.toHex;

import java.io.Serializable;
import java.math.BigInteger;

import com.nimbusds.srp6.SRP6CryptoParams;
import com.nimbusds.srp6.SRP6ServerEvidenceContext;
import com.nimbusds.srp6.ServerEvidenceRoutine;

/**
 * Custom routine interface for computing the server evidence message 'M1'.
 * Compatible with browser implementations by using hashing of string
 * concatenated hex strings 'H( HEX(A) | HEX(M1) | HEX(S)'.
 * 
 * <p>
 * Specification RFC 2945
 * 
 * @author Simon Massey
 */
public class HexHashedServerEvidenceRoutine implements ServerEvidenceRoutine, Serializable {

	/**
	 * Serializable class version number
	 */
	private static final long serialVersionUID = 3243998651178428263L;

	/**
	 * Computes a server evidence message 'M2'.
	 * 
	 * @param cryptoParams
	 *            The crypto parameters for the SRP-6a protocol.
	 * @param ctx
	 *            Snapshot of the SRP-6a server session variables which may be
	 *            used in the computation of the server evidence message.
	 * 
	 * @return Server evidence message 'M2' as 'H( HEX(A) | HEX(M1) | HEX(S)'
	 */
	@Override
	public BigInteger computeServerEvidence(SRP6CryptoParams cryptoParams, SRP6ServerEvidenceContext ctx) {
		return HexHashedRoutines.hashValues(cryptoParams.getMessageDigestInstance(), toHex(ctx.A), toHex(ctx.M1), toHex(ctx.S));
	}

}
