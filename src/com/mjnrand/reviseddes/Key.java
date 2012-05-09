package com.mjnrand.reviseddes;

/**
 * Key represents a 64 key that is used for encrypting / decrypting a file using
 * the Revised DES cryptosystem.  The subkeys for each Feistel Round can easily
 * be generated and retrieved when needed.
 * 
 * @author mark
 */
public class Key {
  private byte[] key = null;
  private byte[][] subkeys = new byte[16][6];
  
  /**
   * Create a new instance of the Key class with the given 64 bit key.
   * 
   * @param key 64 bit (8 byte) stream to be used as the key
   */
  public Key(byte[] key) {
    this.key = key;
  }
  
  /**
   * Generates the subkeys to be used in each of the Feistel Rounds during
   * the encryption / decryption process.
   */
  public void generateSubKeys() {
    byte[] pc1Result = this.performPermutedChoice1();
    byte[] rotationBytes = pc1Result;
    
    for (int i=0; i < 15; i++) {
      byte[] shiftResult = this.performRotation(rotationBytes, Permutation.LEFT_SHIFT_SCHEDULE[i]);
      this.subkeys[i] = this.performPermutedChoice2(shiftResult);
      rotationBytes = shiftResult;
    }
  }
  
  /**
   * Returns the subkey for the given number, to be used in an individual round
   * of the encryption / decryption process.
   * 
   * @param num   number of the subkey to be returned; must be between 0-15
   * @return byte[] array of bytes representing the requested subkey
   */
  public byte[] getSubKey(int num) {
    return this.subkeys[num];
  }
  
  /**
   * Performs the Permuted Choice 1 permutation on the original key.
   *  
   * @return byte[] the permuted bits
   */
  private byte[] performPermutedChoice1() {
    return Permutation.permute(key, Permutation.PERMUTED_CHOICE_1);
  }

  /**
   * Performs the Permuted Choice 2 permutation on the given stream of bits.
   * 
   * @param bytes   bits to be permuted
   * @return byte[] the permuted bits
   */
  private byte[] performPermutedChoice2(byte[] bytes) {
    return Permutation.permute(bytes, Permutation.PERMUTED_CHOICE_2);
  }
  
  /**
   * Performs the given number of left circular shifts on the given set of
   * bits, and returns the result.  Each shift is performed on both the left
   * hand 28 bits, as well as the right hand 28 bits of the 56 bit input
   * 
   * @param rotationBytes   56 bit stream to be shifted
   * @param numRotations    number of left circular shifts to be performed
   * @return byte[]     shifted bits
   */
  private byte[] performRotation(byte[] rotationBytes, int numRotations) {
    byte[] result = { 0, 0, 0, 0, 0, 0, 0 };
    byte MASK = (byte) 0x80;
    
    for (int i=0; i < numRotations; i++) {
      // LEFT SIDE ROTATION
      byte leftSideOverFlow = (byte) (((rotationBytes[0] & MASK) == 0) ? 0x00 : 0x10);
      result[0] = (byte) (rotationBytes[0] << 1);
      
      for (int j=1; j <= 3; j++) {
        byte b = (byte) (((rotationBytes[j] & MASK) == 0) ? 0x00 : 0x01);
        result[j-1] |= b;
        result[j] = (byte) (rotationBytes[j] << 1);
      }
      
      result[3] |= leftSideOverFlow;
      
      // RIGHT SIDE ROTATION
      byte rightSideOverFlow = (byte) (((rotationBytes[3] & (byte) 0x08) == 0) ? 0x00 : 0x01);
      result[3] |= ((rotationBytes[3] << 1) & (byte) 0x0f);
      
      for (int k=4; k < 7; k++) {
        byte b = (byte) (((rotationBytes[k] & MASK) == 0) ? 0x00 : 0x01);
        result[k-1] |= b;
        result[k] = (byte) (rotationBytes[k] << 1);
      }
      
      result[6] |= rightSideOverFlow;
    }
    
    return result;
  }
}
