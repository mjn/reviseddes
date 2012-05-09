package com.mjnrand.reviseddes;

/**
 * Feistel Round represents a single Feistel Round that is to be executed
 * during the encryption / decryption process of the Revised DES cryptosystem.
 * 
 * @author mark
 */
public class FeistelRound {
	private byte[] left = new byte[4];
	private byte[] right = new byte[4];
	private byte[] key = null;
	
	/**
	 * Constructs a new Feistel Round with the given block of bits and key
	 * to be used for this round.
	 * 
	 * @param block		block of bits to be encrypted / decrypted
	 * @param key		subkey to be used during this round
	 */
	public FeistelRound(byte[] block, byte[] key) {
		this.split(block);
		this.key = key;
	}
	
	/**
	 * Executes this Feistel Round and returns the resulting bits.
	 * 
	 * @return byte[]	encrypted / decrypted bits after executing this round
	 */
	public byte[] execute() {
		byte[] result = new byte[8];
		byte[] expanded = null;
		byte[] substituted = null;
		
		this.swapRight(result);
		expanded = this.performExpansion();
		expanded = this.performXOR(expanded, key);
		substituted = this.performSubstition(expanded);
		substituted = this.performPermutation(substituted);
		this.left = this.performCompliment(this.left);
		this.left = this.performXOR(substituted, this.left);
		this.swapLeft(result);
		
		return result;
	}
	
	/**
	 * Splits the given 64 bit stream into two 32 bit streams, the left side
	 * and the right side.
	 * 
	 * @param block		64 bit stream to be split
	 */
	private void split(byte[] block) {
		for (int i=0; i < 4; i++) {
			left[i] = block[i];
			right[i] = block[4 + i];
		}
	}
	
	/**
	 * Swaps the bits of the right hand side into the left hand side of the given
	 * bit stream.
	 * 
	 * @param bytes		64 bit stream that is to have the right hand bits swapped with the left side
	 */
	private void swapRight(byte[] bytes) {
		for (int i=0; i < 4; i++) {
			bytes[i] = this.right[i];
		}
	}
	
	/**
	 * Swaps the bits of the left hand side into the right hand side of the given bit
	 * stream.
	 * 
	 * @param bytes		64 bit stream that is to have the left side bits swapped with the right side
	 */
	private void swapLeft(byte[] bytes) {
		for (int i=4; i < 8; i++) {
			bytes[i] = this.left[i - 4];
		}
	}
	
	/**
	 * Performs the expansion permutation on the right hand side of the original block
	 * of bits passed into this Feistel Round.
	 * 
	 * @return byte[]	permuted bits
	 */
	private byte[] performExpansion() {
		return Permutation.permute(this.right, Permutation.EXPANSION_PERMUTATION);
	}
	
	/**
	 * Performs the S-Box substitution on the given 48 bit stream, and returns the
	 * result.
	 * 
	 * @param bytes		48 bit stream to be substituted
	 * @return byte[]	resulting 32 bit stream
	 */
	private byte[] performSubstition(byte[] bytes) {
		byte[] result = new byte[4];
		
		for (int i=0; i < 8; i++) {
			int startByteNum = ((i - 1) * 6) / 8;
			int endByteNum = ((i * 6) - 1) / 8;
			int startBitPos = ((i - 1) * 6) % 8;
			int endBitPos = ((i * 6) - 1) % 8;
			
			int row = ((((int) Math.pow(2, (7 - startBitPos)) & bytes[startByteNum]) == 0) ? 0x00 : 0x02) +
			          ((((int) Math.pow(2, (7 - endBitPos)) & bytes[endByteNum]) == 0) ? 0x00 : 0x01);
			          
			int col = 0;
			
			for (int j=3; j >= 0; j--) {
				if ((startBitPos = (startBitPos + 1) % 8) == 0) {
					startByteNum++;
				}
				
				col += (((int) Math.pow(2, (7 - startBitPos)) & bytes[startByteNum]) == 0) ? 0 : (int) Math.pow(2, j);
			}
			
			result[i / 2] |= SBox.S_BOXES[i][row][col];
			
			if ((i % 2) == 0) {
				result[i / 2] <<= 4;
			}			
		}
		
		return result;
	}
	
	/**
	 * Performs the permutation function on the given 32 bit stream.
	 * 
	 * @param bytes		32 bit stream to be permuted
	 * @return byte[]	permuted bits
	 */
	private byte[] performPermutation(byte[] bytes) {
		return Permutation.permute(bytes, Permutation.PERMUTATION_FUNCTION);
	}
	
	/**
	 * Performs an XOR on the given bit streams.
	 * 
	 * @param param1	bit stream to be XOR'ed
	 * @param param2	bit stream to be XOR'ed
	 * @return byte[]	XOR'ed bits
	 */
	private byte[] performXOR(byte[] param1, byte[] param2) {
		byte[] result = new byte[param1.length];
		
		for (int i=0; i < param1.length; i++) {
			result[i] = (byte) (param1[i] ^ param2[i]);
		}
		
		return result;
	}
	
	/**
	 * Returns the bitwise complement of the given bit stream.
	 * 
	 * @param bytes		bit stream to be complemented
	 * @return byte[]	complemented bits
	 */
	private byte[] performCompliment(byte[] bytes) {
		byte[] result = new byte[bytes.length];
		
		for (int i=0; i < bytes.length; i++) {
			result[i] = (byte) (~bytes[i]);
		}
		
		return result;
	}
}
