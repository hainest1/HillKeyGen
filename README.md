# HillKeyGen
Cryptology project to generate a Hill cipher key in the form of an N x N matrix (typically 2 x 2) of values 1-26, 
which has determinant mod 26 that is relatively prime to 26. Once a valid matrix has been found, the result is 
displayed in the window and output to a text file. Uses JAMA package to handle matrix arithmetic.
