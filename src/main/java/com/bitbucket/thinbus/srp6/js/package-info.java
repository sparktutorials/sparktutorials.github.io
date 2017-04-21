/**
 * Browser aligned tools for Secure Remote Password (SRP-6a) authentication. 
 * Provides Nimbus SRP6ServerSession classes which matches the Javascript implementation.
 * Also provides a SRP6ClientSession so that Java clients can authenticated to the same server session. 
 * Also provides a HexHashedVerifierGenerator class which can be used for user registration of password reset from java to be able to email out a temporary password. 
 * See the Java and Javascript unit tests in the source repo for examples of all the functionality. 
 *
 * @author Simon Massey
 */
package com.bitbucket.thinbus.srp6.js;
