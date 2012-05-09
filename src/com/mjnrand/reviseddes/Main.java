package com.mjnrand.reviseddes;

/**
 * Main is the main class that is used to start the Revised DES application.
 * 
 * @author mark
 */
public class Main {
  /** Encryption mode option for the Revised DES application. */
  private static final int ENCRYPTION_MODE = 0;
  
  /** Decryption mode option for the Revised DES application. */
  private static final int DECRYPTION_MODE = 1;
  
  /** Revised DES option parameters. */
  private static int mode = ENCRYPTION_MODE;
  private static String inputFileName = null;
  private static String outputFileName = null;
  private static String key = null;
  private static boolean byteStream = false;
  
  /**
   * Main method used to run the Revised DES application.
   * 
   * @param args    command line arguments passed to the application
   */
  public static void main(String args[]) {
    handleArguments(args);
    
    if (mode == ENCRYPTION_MODE) {
      Key k = new Key(key.getBytes());
      k.generateSubKeys();
      
      if (outputFileName == null) {
        outputFileName = new StringBuffer(inputFileName).append(".enc").toString();
      }
      
      RevisedDES.encrypt(inputFileName, outputFileName, k, byteStream);
    } else if (mode == DECRYPTION_MODE) {
      Key k = new Key(key.getBytes());
      k.generateSubKeys();
      
      if (outputFileName == null) {
        outputFileName = new StringBuffer(inputFileName).append("dec").toString();
      }
      
      RevisedDES.decrypt(inputFileName, outputFileName, k, byteStream);
    } else {
      usage();
    }
  }
  
  /**
   * Parse the command line arguments to make sure they are valid, and set up
   * the required parameters.
   * 
   * @param args    array of command line arguments
   */
  private static void handleArguments(String[] args) {
    boolean modeSet = false;
    boolean outputSet = false;
    boolean bytesSet = false;
    
    try {
      for (int i=0; i < args.length; i++) {
        if (args[i].equals("-e")) {
          if (!modeSet) {
            mode = ENCRYPTION_MODE;
            modeSet = true;
            
            inputFileName = args[++i];
            key = args[++i];
            
            if (key.length() != 8) {
              usage();
            }
            
            continue;
          } else {
            usage();
          }
        }
        
        if (args[i].equals("-d")) {
          if (!modeSet) {
            mode = DECRYPTION_MODE;
            modeSet = true;
            
            inputFileName = args[++i];
            key = args[++i];
            
            if (key.length() != 8) {
              usage();
            }
            
            continue;
          } else {
            usage();
          }
        }
        
        if (args[i].equals("-o")) {
          if (!outputSet) {
            outputFileName = args[++i];
            outputSet = true;
              
            continue;
          } else {
            usage();
          }
        }
        
        if (args[i].equals("-b")) {
          if (!bytesSet) {
            byteStream = true;
            bytesSet = true;
            
            continue;
          } else {
            usage();
          }
        }
        
        if (args[i].equals("-h")) {
          usage();
        }
      }
    } catch (ArrayIndexOutOfBoundsException aiobe) {
      usage();
    }
        
    if (!modeSet) {
      usage();
    }
  }
  
  /**
   * Outputs a usage message to the console describing the various options and
   * mode of operation for the Revised DES application.
   */
  private static void usage() {
    String usageMsg = new StringBuffer("Usage: java -jar RevisedDES.jar MODE INPUTFILE KEY [OPTIONS]\n\n").
                append("Encrypts / decrypts a given file using the provided key.\n").
                append("The key must be an 8-character (64-bit) ASCII string.\n\n").
                append("Modes:\n").               
                append("\t-e\t\tencrypt a plain text file\n").
                append("\t-d\t\tdecrypt a cipher text file\n\n").
                append("Options:\n").
                append("\t-o OUTPUTFILE\twrites the encrypted / decrypted text to OUTPUTFILE\n").
                append("\t-b\t\twhile encrypting, setting this option writes the\n").
                append("\t\t\tcipher text as a stream of bytes instead of bits;\n").
                append("\t\t\twhile decrypting, setting this option reads in a byte\n").
                append("\t\t\tstream instead of a bit stream\n").
                append("\t-h\t\tdisplays this help message").
                toString();
                
    System.out.println(usageMsg);
    
    System.exit(-1);
  }
  
  /**
   * Private constructor to prevent creation of an instance of Main.
   */
  private Main() {}
}
