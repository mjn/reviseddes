package com.mjnrand.reviseddes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * RevisedDES simply provides static methods for encrypting a plain text message
 * or decrypting a cipher text message.
 * 
 * @author mark
 */
public class RevisedDES {
  /**
   * Constructor is private in order to prevent instances of this class
   * from being created.
   */
  private RevisedDES() {}
  
  /**
   * Encrypts the plain text file with the given file name using the provided
   * key.
   * 
   * @param plainTextFile   name of the plain text file to encrypt
   * @param cipherTextFile  name of the file in which to output the encrypted cipher text
   * @param key       key to be used for encrypting the file
   * @param byteStream    output cipher text as a byte stream or bit stream
   */
  public static void encrypt(String plainTextFile, String cipherTextFile, Key key, boolean byteStream) {
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    byte[] plainTextBlock = { 0, 0, 0, 0, 0, 0, 0, 0 };
    byte[] cipherTextBlock = null;
    int count = 0;
    boolean stillReading = true;
    
    try {
      bis = new BufferedInputStream(new FileInputStream(plainTextFile));
    } catch (FileNotFoundException fnfe) {
      System.out.println("[ERROR]  File being encrypted does not exist.");
      return;
    }
    
    try {
      if (!byteStream) {
        bos = new BufferedOutputStream(
                  new CipherTextOutputStream(
                  new FileOutputStream(cipherTextFile)));
      } else {
        bos = new BufferedOutputStream(new FileOutputStream(cipherTextFile));
      }
    } catch (FileNotFoundException fnfe) {
      System.out.println("[ERROR]  Output cipher text file could not be opened.");
      return;
    }     
    
    try {
      while (stillReading) {
        count = bis.read(plainTextBlock);
        
        if (count < 8) {
          for (int i=count; i < 8; i++) {
            plainTextBlock[i] = ' ';
          }
          
          stillReading = false;
        }
        
        cipherTextBlock = performInitialPermutation(plainTextBlock);
        
        for (int i=0; i < 16; i++) {
          FeistelRound round = new FeistelRound(cipherTextBlock, key.getSubKey(i));
          cipherTextBlock = round.execute();
        }
        
        cipherTextBlock = swap(cipherTextBlock);
        cipherTextBlock = performInverseInitialPermutation(cipherTextBlock);
        
        bos.write(cipherTextBlock);
      }
    } catch (IOException ioe) {
      try {
        if (bis != null) {
          bis.close();
        }
        
        if (bos != null) {
          bos.close();
        }
      } catch (IOException e) {
        System.out.println("[ERROR]  Unable to close input / output stream.");
        return;
      }
      System.out.println("[ERROR]  Unable to read from plaintext file.");
      return;
    }
    
    try {
      bis.close();
      bos.close();
    } catch (IOException ioe) {
      System.out.println("[ERROR]  Unable to close input / output stream.");
    }
  }
  
  /**
   * Decrypts the cipher text file with the given name using the provided key.
   * 
   * @param cipherTextFile    name of the cipher text file to decrypt
   * @param plainTextFile     name of the file which to output the decrypted plain text
   * @param key         key to be used in decrypting the cipher text
   * @param byteStream      true if the cipher text is a stream of bytes; false if it is a stream of bits
   */
  public static void decrypt(String cipherTextFile, String plainTextFile, Key key, boolean byteStream) {
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    byte[] cipherTextBlock = { 0, 0, 0, 0, 0, 0, 0, 0 };
    byte[] plainTextBlock = null;
    int count = 0;
    
    try {
      if (!byteStream) {
        bis = new BufferedInputStream(
                  new CipherTextInputStream(
                  new FileInputStream(cipherTextFile)));
      } else {
        bis = new BufferedInputStream(new FileInputStream(cipherTextFile));
      }
    } catch (FileNotFoundException fnfe) {
      System.out.println("[ERROR]  File being decrypted does not exist.");
      return;
    }
    
    try {
      bos = new BufferedOutputStream(new FileOutputStream(plainTextFile));
    } catch (FileNotFoundException fnfe) {
      System.out.println("[ERROR]  Output plain text file could not be opened.");
      return;
    }     
    
    try {
      while (true) {
        count = bis.read(cipherTextBlock);
        
        if (count == -1) {
          break;
        }
        
        plainTextBlock = performInitialPermutation(cipherTextBlock);
        
        for (int i=0; i < 16; i++) {
          FeistelRound round = new FeistelRound(plainTextBlock, key.getSubKey((16 - i) - 1));
          plainTextBlock = round.execute();
        }
        
        plainTextBlock = swap(plainTextBlock);
        plainTextBlock = performInverseInitialPermutation(plainTextBlock);
        
        bos.write(plainTextBlock);      
      }
    } catch (IOException ioe) {
      try {
        if (bis != null) {
          bis.close();
        }
        
        if (bos != null) {
          bos.close();
        }
      } catch (IOException e) {
        System.out.println("[ERROR]  Unable to close input / output stream.");        
        return;
      }
      
      System.out.println("[ERROR]  Unable to read from ciphertext file.");      
      return;
    }
    
    try {
      if (bis != null) {
        bis.close();
      }
      
      if (bos != null) {
        bos.close();
      }
    } catch (IOException ioe) {
      System.out.println("[ERROR]  Unable to close input / output stream.");
    }
  }
  
  /**
   * Performs the initial permutation on the given block of bits.
   * 
   * @param bytes   bits to be permuted
   * @return byte[] permuted bits
   */
  private static byte[] performInitialPermutation(byte[] bytes) {
    return Permutation.permute(bytes, Permutation.INITIAL_PERMUTATION);
  }
  
  /**
   * Performs the inverse of the initial permutation on the given block of bits.
   * 
   * @param bytes   bits to be permuted
   * @return byte[] permuted bits
   */
  private static byte[] performInverseInitialPermutation(byte[] bytes) {
    return Permutation.permute(bytes, Permutation.INITIAL_PERMUTATION_INVERSE);
  }
  
  /**
   * Swaps the left hand 32 bits with the right hand 32 bits of the given 64 bit
   * block.
   * 
   * @param bytes   64 bit stream that is to be swapped
   * @return byte[] swapped bits
   */
  private static byte[] swap(byte[] bytes) {
    byte[] result = new byte[8];
    
    for (int i=0; i < 4; i++) {
      result[i] = bytes[i + 4];
      result[i + 4] = bytes[i];
    }
    
    return result;
  }
}
