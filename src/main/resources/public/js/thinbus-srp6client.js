/**
 * Thinbus Javascript Secure Remote Password (SRP)
 * Version  ${project.version}
 * Copyright 2014-2015 Simon Massey
 * http://www.apache.org/licenses/LICENSE-2.0
*/
function SRP6JavascriptClientSession() {
	"use strict";
	
	/**
	 * The session is initialised and ready to begin authentication
	 * by proceeding to {@link #STEP_1}.
	 */
	this.INIT = 0;
		
	/**
	 * The authenticating user has input their identity 'I' 
	 * (username) and password 'P'. The session is ready to proceed
	 * to {@link #STEP_2}.
	 */
	this.STEP_1 = 1;
		
	/**
	 * The user identity 'I' is submitted to the server which has 
	 * replied with the matching salt 's' and its public value 'B' 
	 * based on the user's password verifier 'v'. The session is 
	 * ready to proceed to {@link #STEP_3}.
	 */
	this.STEP_2 = 2;
		
	/**
	 * The client public key 'A' and evidence message 'M1' are
	 * submitted and the server has replied with own evidence
	 * message 'M2'. The session is finished (authentication was 
	 * successful or failed).
	 */
	this.STEP_3 = 3;
  
	this.state = this.INIT;
	
	this.x = null;
	this.v = null;
	this.I = null;
	this.P = null;
	this.B = null;
	this.A = null;
	this.a = null;
	this.k = null;
	this.u = null;
	this.S = null;
	this.K = null;
	this.M1str = null;
	
	// private
	this.check = function(v, name) {
		if( typeof v === 'undefined' || v === null || v === "" || v === "0" ) {
			throw new Error(name+" must not be null, empty or zero");
		}
	};
	
	/** private<p>
	 * 
	 * Computes x = H(s | H(I | ":" | P))
	 * <p> Uses string concatenation before hashing. 
	 * <p> Specification RFC 2945
	 *
	 * @param salt     The salt 's'. Must not be null or empty.
	 * @param identity The user identity/email 'I'. Must not be null or empty.
	 * @param password The user password 'P'. Must not be null or empty
	 * @return The resulting 'x' value as BigInteger.
	 */
	this.generateX = function(salt, identity, password) {
		this.check(salt, "salt");
		this.check(identity, "identity");
		this.check(password, "password");
		//console.log("js salt:"+salt+",i:"+identity+",p:"+password);
		var hash1 = this.H(identity+':'+password);
		
		// server BigInteger math will trim leading zeros so we must do likewise to get a match
		while (hash1.substring(0, 1) === '0') { 
			//console.log("stripping leading zero from M1");
			hash1 = hash1.substring(1);
		}
		
		//console.log("js hash1:"+hash1);
		//console.log("js salt:"+salt);
		var concat = (salt+hash1).toUpperCase();
		//console.log("js concat:"+concat);
		var hash = this.H(concat);
		
		// Java BigInteger math will trim leading zeros so we do likewise
		while (hash.substring(0, 1) === '0') { 
			//console.log("stripping leading zero from M1");
			hash = hash.substring(1);
		}		
		
		//console.log("js hash:"+hash)
		//console.log("js x before modN "+this.fromHex(hash));
		this.x = this.fromHex(hash).mod(this.N());
		return this.x;
	};

	/**
	 * Computes the session key S = (B - k * g^x) ^ (a + u * x) (mod N)
	 * from client-side parameters.
	 * 
	 * <p>Specification: RFC 5054
	 *
	 * @param N The prime parameter 'N'. Must not be {@code null}.
	 * @param g The generator parameter 'g'. Must not be {@code null}.
	 * @param k The SRP-6a multiplier 'k'. Must not be {@code null}.
	 * @param x The 'x' value, see {@link #computeX}. Must not be 
	 *          {@code null}.
	 * @param u The random scrambling parameter 'u'. Must not be 
	 *          {@code null}.
	 * @param a The private client value 'a'. Must not be {@code null}.
	 * @param B The public server value 'B'. Must note be {@code null}.
	 *
	 * @return The resulting session key 'S'.
	 */
	this.computeSessionKey = function(k, x, u, a, B) {
		this.check(k, "k");
		this.check(x, "x");
		this.check(u, "u");
		this.check(a, "a");
		this.check(B, "B");
	
		var exp = u.multiply(x).add(a);
		var tmp = this.g().modPow(x, this.N()).multiply(k);
		return B.subtract(tmp).modPow(exp, this.N());
	};
}

// public helper
SRP6JavascriptClientSession.prototype.toHex = function(n) {
	"use strict";
	return n.toString(16);
};

// public helper
/* jshint ignore:start */
SRP6JavascriptClientSession.prototype.fromHex = function(s) {
	"use strict";
	return new BigInteger(""+s, 16); // jdk1.7 rhino requires string concat
};
/* jshint ignore:end */

// public getter of the current workflow state. 
SRP6JavascriptClientSession.prototype.getState = function() {
	"use strict";
	return this.state;
};

/**
 * Gets the shared sessionkey
 * 
 * @param hash Boolean With to return the large session key 'S' or 'K=H(S)'
 */
SRP6JavascriptClientSession.prototype.getSessionKey = function(hash) {
	"use strict";
	if( this.S === null ) {
		return null;
	}
	this.SS = this.toHex(this.S);
	if(typeof hash !== 'undefined' && hash === false){
		return this.SS;
	} else {
		if( this.K === null ) {
			this.K = this.H(this.SS);
		}
		return this.K;
	}
};

// public getter
SRP6JavascriptClientSession.prototype.getUserID = function() {
	"use strict";
	return this.I;
};

/* 
 * Generates a new salt 's'. This takes the current time, a pure browser random value, and an optional server generated random, and hashes them all together. 
 * This should ensure that the salt is unique to every use registration regardless of the quality of the browser random generation routine. 
 * Note that this method is optional as you can choose to always generate the salt at the server and sent it to the browser as it is a public value.  
 * <p>
 * Always add a unique constraint to where you store this in your database to force that all users on the system have a unique salt. 
 *
 * @param opionalServerSalt An optional server salt which is hashed into a locally generated random number. Can be left undefined when calling this function.
 * @return 's' Salt as a hex string of length driven by the bit size of the hash algorithm 'H'. 
 */
SRP6JavascriptClientSession.prototype.generateRandomSalt = function(opionalServerSalt) {
	"use strict";
	var s = null;
	
	/* jshint ignore:start */
	s = random16byteHex.random();
	/* jshint ignore:end */

	// if you invoke without passing the string parameter the '+' operator uses 'undefined' so no nullpointer risk here
	var ss = this.H((new Date())+':'+opionalServerSalt+':'+s);
	return ss;
};

/* 
 * Generates a new verifier 'v' from the specified parameters.
 * <p>The verifier is computed as v = g^x (mod N). 
 * <p> Specification RFC 2945
 *
 * @param salt     The salt 's'. Must not be null or empty.
 * @param identity The user identity/email 'I'. Must not be null or empty.
 * @param password The user password 'P'. Must not be null or empty
 * @return The resulting verifier 'v' as a hex string
 */
SRP6JavascriptClientSession.prototype.generateVerifier = function(salt, identity, password) {
	"use strict";
	// no need to check the parameters as generateX will do this
	var x = this.generateX(salt, identity, password);
	//console.log("js x: "+x)
	this.v = this.g().modPow(x, this.N());
	return this.toHex(this.v);
};

/**
 * Records the identity 'I' and password 'P' of the authenticating user.
 * The session is incremented to {@link State#STEP_1}.
 * <p>Argument origin:
 * <ul>
 *     <li>From user: user identity 'I' and password 'P'.
 * </ul>
 * @param userID   The identity 'I' of the authenticating user, UTF-8
 *                 encoded. Must not be {@code null} or empty.
 * @param password The user password 'P', UTF-8 encoded. Must not be
 *                 {@code null}.
 * @throws IllegalStateException If the method is invoked in a state 
 *                               other than {@link State#INIT}.
 */
SRP6JavascriptClientSession.prototype.step1 = function(identity, password) {
	"use strict";
	this.check(identity, "identity");
	this.check(password, "password");
	this.I = identity;
	this.P = password;
	if( this.state !== this.INIT ) {
		throw new Error("IllegalStateException not in state INIT");
	}
	this.state = this.STEP_1;
};

/**
 * Computes the random scrambling parameter u = H(A | B)
 * <p> Specification RFC 2945
 * Will throw an error if 
 *
 * @param A      The public client value 'A'. Must not be {@code null}.
 * @param B      The public server value 'B'. Must not be {@code null}.
 *
 * @return The resulting 'u' value.
 */
SRP6JavascriptClientSession.prototype.computeU = function(Astr, Bstr) {
	"use strict";
	this.check(Astr, "Astr");
	this.check(Bstr, "Bstr");
	/* jshint ignore:start */
	var output = this.H(Astr+Bstr);
	var u = new BigInteger(""+output,16);
	if( BigInteger.ZERO.equals(u) ) {
	   throw new Error("SRP6Exception bad shared public value 'u' as u==0");
	}
	return u;
	/* jshint ignore:end */
};

/**
 * Receives the password salt 's' and public value 'B' from the server.
 * The SRP-6a crypto parameters are also set. The session is incremented
 * to {@link State#STEP_2}.
 * <p>Argument origin:
 * <ul>
 *     <li>From server: password salt 's', public value 'B'.
 *     <li>Pre-agreed: crypto parameters prime 'N', 
 *         generator 'g' and hash function 'H'.
 * </ul>
 * @param s      The password salt 's' as a hex string. Must not be {@code null}.
 * @param B      The public server value 'B' as a hex string. Must not be {@code null}.
 * @param k      k is H(N,g) with padding by the server. Must not be {@code null}.
 * @return The client credentials consisting of the client public key 
 *         'A' and the client evidence message 'M1'.
 * @throws IllegalStateException If the method is invoked in a state 
 *                               other than {@link State#STEP_1}.
 * @throws SRP6Exception         If the public server value 'B' is invalid.
 */
SRP6JavascriptClientSession.prototype.step2 = function(s, BB) {
	"use strict";
	this.check(s, "s");
	//console.log("M1 js s:" + s);
	this.check(BB, "BB");
	//console.log("M1 js BB:" + BB);
	
	if( this.state !== this.STEP_1 ) {
		throw new Error("IllegalStateException not in state STEP_1");
	}
	
	// this is checked when passed to computeSessionKey
	this.B = this.fromHex(BB); 

	var ZERO = null;
	
	/* jshint ignore:start */
	ZERO = BigInteger.ZERO;
	/* jshint ignore:end */
	
	if (this.B.mod(this.N()).equals(ZERO)) {
		throw new Error("SRP6Exception bad server public value 'B' as B == 0 (mod N)");
	}
	
	//console.log("M1 js k:" + k);

	// this is checked when passed to computeSessionKey
	var x = this.generateX(s, this.I, this.P);
	//console.log("M1 js x:" + x);
	
	var r1 = null;
	var r2 = null;
	
	/* jshint ignore:start */
	r1 = random16byteHex.random();
	r2 = random16byteHex.random();
	/* jshint ignore:end */
	
	// we use Date.now() to prevent the same 'a' being returned for multiple login attempts if `window.crypto` is buggy
	var aStr = this.H((new Date())+':'+this.I+':'+r1+':'+r2);
	// this is checked when passed to computeSessionKey
	this.a = this.fromHex(aStr);
	//console.log("M1 js a:" + a);
	
	this.A = this.g().modPow(this.a, this.N());
	//console.log("M1 js A:" + A);
	this.check(this.A, "A");
	
	this.u = this.computeU(this.A.toString(16),BB);
	//console.log("M1 js u:" + u);
	
	this.S = this.computeSessionKey(this.k, x, this.u, this.a, this.B);
	this.check(this.S, "S");
	
	//console.log("jsU:" + toHex(u));
	//console.log("jsS:" + toHex(S));
	
	var AA = this.toHex(this.A);
	
	this.M1str = this.H(AA+BB+this.toHex(this.S));
	this.check(this.M1str, "M1str");
	
	// server BigInteger math will trim leading zeros so we must do likewise to get a match
	while (this.M1str.substring(0, 1) === '0') { 
		//console.log("stripping leading zero from M1");
		this.M1str = this.M1str.substring(1);
	}
	
	//console.log("M1str:" + this.M1str);
	
	//console.log("jsABS:" + AA+BB+this.toHex(this.S));
	//console.log("M1 js A:" + AA);
	//console.log("M1 js B:" + BB);
	//console.log("v:" + this.v);
	//console.log("u:" + this.u);	
	//console.log("A:" + this.A);
	//console.log("b:" + this.B);
	//console.log("S:" + this.S);
	//console.log("M1:" + this.M1);
	//console.log("M1 js S:" + this.toHex(this.S));
	//console.log("M1 jsM1:" + this.M1str);
	
	this.state = this.STEP_2;
	return { A: AA, M1: this.M1str };
};

/**
 * Receives the server evidence message 'M1'. The session is incremented
 * to {@link State#STEP_3}.
 *
 * <p>Argument origin:
 * <ul>
 *     <li>From server: evidence message 'M2'.
 * </ul>
 * @param serverM2 The server evidence message 'M2' as string. Must not be {@code null}.
 * @throws IllegalStateException If the method is invoked in a state 
 *                               other than {@link State#STEP_2}.
 * @throws SRP6Exception         If the session has timed out or the 
 *                               server evidence message 'M2' is 
 *                               invalid.
 */
SRP6JavascriptClientSession.prototype.step3 = function(M2) {
	"use strict";
	this.check(M2);
	
	// Check current state
	if (this.state !== this.STEP_2)
		throw new Error("IllegalStateException State violation: Session must be in STEP_2 state");

	//console.log("M2 js A:" + toHex(A));
	//console.log("M2 jsM1:" + M1str);
	//console.log("M2 js S:" + toHex(S));
	
	var computedM2 = this.H(this.toHex(this.A)+this.M1str+this.toHex(this.S));
	
	//console.log("M2 jsServerM2:" + M2);
	//console.log("M2 jsClientM2:" + computedM2);
	
	// server BigInteger math will trim leading zeros so we must do likewise to get a match
	while (computedM2.substring(0, 1) === '0') { 
		//console.log("stripping leading zero from computedM2");
		computedM2 = computedM2.substring(1);
	}
	
	//console.log("server  M2:"+M2+"\ncomputedM2:"+computedM2);
	if ( ""+computedM2 !== ""+M2) {
		throw new Error("SRP6Exception Bad server credentials");
	}

	this.state = this.STEP_3;
	
	return true;
};
