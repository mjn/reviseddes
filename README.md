OVERVIEW
========

The Revised DES cryptosystem is based on the Data Encryption Standard cryptosystem, with several slight modifications.  These modifications include the following:

*	the S-Boxes used in the Revised DES cryptosystem differ from those used in DES in that if the entry in Si, 1 ≤ i ≤ 8, is equal to a, then the corresponding entry in RSi  would be a + i mod 16, where Si is the i-th S-Box in the DES cryptosystem, and RSi is the i-th S-Box in the Revised DES cryptosystem.
*	encryption of the i-th Feistel Round is summarized as follows:
  - Li = Ri-1
  - Ri = ^Li-1 XOR F(Ri-1, Ki)    
	     where ^Li-1 is the bitwise complement of Li-1, and the function F is the 	     same as that used in DES, except the new S-Boxes are used.
*	the permutations used in the key schedule are substituted with the permutations as outlined in the project description.

USAGE
=====

The Revised DES application is provided as a single JAR file, and it is used as a command line tool, similar to UNIX commands such as cp, ls, and tar.  In order to run the application, type the following at a command prompt:

    > java -jar RevisedDES.jar MODE INPUTFILE KEY [OPTIONS]

where

	  MODE        either -e to encrypt or -d to decrypt
    INPUTFILE   plain text file to be encrypted or cipher text file to be decrypted
    KEY         8 character (64 bit) ASCII string
    OPTIONS
	    -o OUTPUTFILE	writes the encrypted / decrypted text to OUTPUTFILE. If not specified, OUTPUTFILE is the same as INPUTFILE with “.enc” or “.dec” appended at the end for encryption or	encryption, respectively.
	    -b	when encrypting, setting this option writes the cipher text as a stream of bytes instead of bits; when decrypting, setting this option reads in a byte stream instead of a bit stream
	    -h	displays a help message

