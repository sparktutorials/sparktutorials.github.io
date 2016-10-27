package com.bitbucket.thinbus.srp6.js;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.nimbusds.srp6.SRP6Routines;

/**
 * A class to parse the output of 'openssl dhparam -text bits' where bits is the
 * prime number bit length. Will output 'N', 'g', 'k' in bases 10, 10, 16
 * respectively. Note that k is derived from 'N' and 'g' but Nimbus 1.4.x
 * currently uses a the byte array constructor of BigInteger to computes 'k'
 * which is not available in Javascript so the value genenerated by Java needs
 * to be configure in the Javascript.
 */
public class OpenSSLCryptoConfigConverter {

	public List<String> run(String hash, List<String> lines) throws Exception {
		int generator = 0;
		StringBuilder hexparts = new StringBuilder();

		for (String line : lines) {
			if (line.endsWith("prime:")) {
				// skip this one
			} else if (line.endsWith(":")) {
				hexparts.append(line.trim());
			} else if (line.contains("generator")) {
				try {
					generator = generator(line.trim());
				} catch (Exception e) {
					throw new AssertionError(
							"could not parse 'generator: x' number out of line containing 'generator': "
									+ line);
				}
			}
		}

		if (generator <= 0) {
			throw new AssertionError(
					"could not parse 'generator: x' number out of line containing 'generator'");
		}

		String primeHex = hexparts.toString().replace(":", "");

		List<String> output = new ArrayList<String>();

		BigInteger N = new BigInteger(primeHex, 16);
		BigInteger g = new BigInteger(generator + "");

		output.add("hashing to create 'k' using " + hash);

		MessageDigest digest = MessageDigest.getInstance(hash);
		BigInteger k = SRP6Routines.computeK(digest, N, g);

		output.add("computing...");
		output.add("N base10: " + N.toString(10));
		output.add("g base10: " + g.toString(10));
		output.add("k base16: " + k.toString(16));

		return output;
	}

	public static void main(String[] args) throws Exception {

		if (args.length != 2) {
			System.err.println("Arguments: file hash ");
			System.err.println("Example  : /tmp/my_dhparam.txt SHA-256 ");
			System.exit(1);
		}

		final String file = args[0];
		final String hash = args[1];

		System.out
				.println(String
						.format("Attempting to load 'openssl dhparam -text <bitlength>' output text file at: %s",
								file));

		final List<String> lines = Files.readAllLines(Paths.get(args[0]),
				Charset.forName("UTF8"));

		System.out.println(String.format("Loaded %s lines.", lines.size()));

		System.out.println(String.format(
				"Creating configuration parmeters using hash algorithm %s.",
				hash));

		for (String output : (new OpenSSLCryptoConfigConverter()).run(hash,
				lines)) {
			System.out.println(output);
		}
	}

	static Pattern generatorPattern = Pattern
			.compile(".*generator: (\\d*) \\(.*");

	private static int generator(String line) {
		Matcher matcher = generatorPattern.matcher(line);
		matcher.matches();
		String number = matcher.group(1);
		return Integer.valueOf(number);
	}
}
