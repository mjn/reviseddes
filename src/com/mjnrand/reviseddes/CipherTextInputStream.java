package com.mjnrand.reviseddes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * CipherTextInputStream is an input stream class that reads from a stream of
 * 1's and 0's (usually from a file) and converts a sequence of 8 bits into
 * a byte.
 * 
 * @author mark
 */
public class CipherTextInputStream extends InputStream {
	private InputStream is = null;
	
	/**
	 * Construct a CipherTextInputStream using the given input stream.
	 * 
	 * @param is	stream of 1's and 0's to be read
	 */
	public CipherTextInputStream(InputStream is) {
		this.is = is;
	}
	
	/**
	 * Construct a CipherTextInputStream by opening a FileInputStream on the
	 * given cipher text file which is made up of 1's and 0's.
	 * 
	 * @param filename		name of the cipher text file to be opened for streaming
	 * @throws FileNotFoundException	if the cipher text file does not exist or could not be opened
	 */
	public CipherTextInputStream(String filename) throws FileNotFoundException {
		this.is = new FileInputStream(filename);
	}
	
	/**
	 * Reads a single byte from the the cipher text stream by reading the next
	 * 8 "bits" from the stream and constructing a byte.
	 * 
	 * @see java.io.InputStream#read()
	 */
	public int read() throws IOException {
		int result = 0;
		
		for (int i=0; i < 8; i++) {
			int c = this.is.read();
			
			if (c == -1) {
				return c;
			}
			
			if (c != '1' && c != '0') {
				throw new IOException("Not a valid ciphertext file");
			}
			
			result |= ((c == '1') ? (int) Math.pow(2, 7 - i) : 0x00);
		}
		
		return result;
	}
}
