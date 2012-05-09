package com.mjnrand.reviseddes;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * CipherTextOutputStream is an output stream that writes a single byte in it's
 * bit form (as a sequence of 1's and 0's).
 * 
 * @author mark
 */
public class CipherTextOutputStream extends OutputStream {
	private OutputStream os = null;
	
	/**
	 * Construct an instance of the cipher text output stream using the given output
	 * stream.
	 * 
	 * @param os	output stream that the bit stream is to be written to
	 */
	public CipherTextOutputStream(OutputStream os) {
		this.os = os;
	}
	
	/**
	 * Constructs an instance of the cipher text output stream that will write the
	 * bytes to a file output stream using the given file name.
	 * 
	 * @param filename		name of the file to write the bits to
	 * @throws FileNotFoundException	if the file to be written to does not exist or could not be opened
	 */
	public CipherTextOutputStream(String filename) throws FileNotFoundException {
		this.os = new FileOutputStream(filename);
	}
	
	/**
	 * Writes the given byte to the stream as a bit stream (sequence of 1's and 0's).
	 * 
	 * @see java.io.OutputStream#write(int)
	 */
	public void write(int b) throws IOException {
		for (int i=0; i < 8; i++) {
			this.os.write(((b & (int) Math.pow(2, 7 - i)) == 0) ? '0' : '1');
		}
	}
}
